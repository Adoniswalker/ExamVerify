package com.example.adonis.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    static final int BAR_CODE_VALUE = 1; //value identified returned data
    TextView result_text = (TextView) findViewById(R.id.result_textview);
    final Button scan_click = (Button)findViewById(R.id.btn_scan);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_click.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
        // Check which request we're responding to
        if (requestCode == BAR_CODE_VALUE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String returned_string = data.getStringExtra("exam_key");
                result_text.setText(returned_string);
            }
        }
    }

    //    final TextView mTextView = (TextView) findViewById(R.id.text);
    // ...

    // Instantiate the RequestQueue.
//    RequestQueue queue = Volley.newRequestQueue(this);
//    String url = "http://examverify.pythonanywhere.com/api/exam/1";
//
//    // Request a string response from the provided URL.
//    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//            new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    // Display the first 500 characters of the response string.
//                    result_textview.setText("Response is: " + response.substring(0, 500));
//                }
//            }, new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            result_textview.setText("That didn't work!");
//        }
//    });
//
//    // Add the request to the RequestQueue.
//    queue.add(stringRequest);
}
