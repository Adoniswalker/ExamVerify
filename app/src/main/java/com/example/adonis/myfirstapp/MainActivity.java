package com.example.adonis.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    static final int BAR_CODE_VALUE = 1; //value identified returned data

    TextView result_text;
    Button scan_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //following loads all ui components
        setContentView(R.layout.activity_main);


        result_text = findViewById(R.id.result_textview);
        scan_click = findViewById(R.id.btn_scan);
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
//                Log.w("MainActivity", "Good result");
                String returned_string = data.getStringExtra("exam_key");
                ;
                if(isStringInt(returned_string)){
                    getExamDetails(Integer.parseInt(returned_string));
                }else {
                    result_text.setText(returned_string);
                }
            }
        }
    }
    public boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public void getExamDetails(final int exam_id) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://examverify.pythonanywhere.com/api/exam/"+exam_id+"/";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        result_text.setText("Response is: " + response.toString());
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
}
