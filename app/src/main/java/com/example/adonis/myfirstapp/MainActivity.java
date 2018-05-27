package com.example.adonis.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    static final int BAR_CODE_VALUE = 1; //value identified returned data

    TextView result_text;
    Button scan_click;
    TextView name_textView;
    TextView adm_textView;
    TextView academic_textView;
    TextView date_textView;
    CheckBox allow_checkbox;
    TextView program_textView;
    ImageView student_image;
    TableLayout units_table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //following loads all ui components
        setContentView(R.layout.activity_main);


        result_text = findViewById(R.id.result_textview);
        scan_click = findViewById(R.id.btn_scan);
        student_image = findViewById(R.id.imageView);
        name_textView = findViewById(R.id.textViewNameValue);
        adm_textView = findViewById(R.id.textViewRegValue);
        academic_textView = findViewById(R.id.textViewAcademicValue);
        date_textView = findViewById(R.id.textViewDateValue);
        allow_checkbox = findViewById(R.id.allowed_exam_checkBox);
        program_textView = findViewById(R.id.textViewProgramValue);
        units_table = findViewById(R.id.units_table);
        scan_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callScanning();
            }
        });
    }


    /*called when user clicks the button*/
    public void callScanning() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, BAR_CODE_VALUE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BAR_CODE_VALUE) {
            Log.w("MainActivity", String.valueOf(requestCode));
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String returned_string = data.getStringExtra("exam_key");
                if (isStringInt(returned_string)) {
                    getExamDetails(Integer.parseInt(returned_string));
                } else {
                    result_text.setText(returned_string);
                }
            }
        }
    }

    public boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void getExamDetails(final int exam_id) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://examverify.pythonanywhere.com/api/exam/" + exam_id + "/";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String student_name = (String) response.get("student_name");
                            String student_admission = (String) response.get("admission");
                            String student_date = (String) response.get("date_allowed");
                            boolean student_allowed = (Boolean) response.get("allowed_exam");
                            String student_program = (String) response.get("program");
                            int student_period = (int) response.get("period_exams");
                            int program_id = (int) response.get("program_id");
                            String student_image_url = (String) response.get("image");
                            name_textView.setText(student_name);
                            adm_textView.setText(student_admission);
                            date_textView.setText(student_date);
//                            academic_textView.setText(student_academic);
                            program_textView.setText(student_program);
                            allow_checkbox.setChecked(student_allowed);
                            //setup image
                            new DownloadImageTask(student_image).execute(student_image_url);
                            addAndGetUnits(student_period, program_id);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result_text.setText(String.format("Not found. For id%d", exam_id));
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private void addAndGetUnits(int period, int program) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://examverify.pythonanywhere.com/api/units/?period=" + period + "&program=" + program;

        // Request a string response from the provided URL.
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            apply_table(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result_text.setText("Not found. For id");
            }
        });
        queue.add(jsonRequest);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void apply_table(JSONArray response) throws JSONException {
        for (int i = 0; i < response.length(); i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            String code = response.getJSONObject(i).getString("code");
            String name = response.getJSONObject(i).getString("name");
            TextView code_view = new TextView(this);
            TextView name_view = new TextView(this);
            code_view.setText(code);
            name_view.setText(name);
            row.addView(code_view);
            row.addView(name_view);
            units_table.addView(row, i);
        }
    }
}