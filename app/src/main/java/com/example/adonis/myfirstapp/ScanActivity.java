package com.example.adonis.myfirstapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //    Button btnAction;
    String intentData = "";
//    boolean isEmail = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
//        btnAction = findViewById(R.id.btnAction);


//        btnAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (intentData.length() > 0) {
//                    if (isEmail)
//                        Toast.makeText(getApplicationContext(), intentData, Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(ScanActivity.this, EmailActivity.class).putExtra("email_address", intentData));
//                    else {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
//                    }
//                }


//            }
//        });
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
//        setting barcode formats
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        // creating camera source and pasing barcode detector
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanActivity.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //releases barcode when back is called
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode " +
                        "scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable(){
                        @Override
                        public void run() {
//                            Log.w("ScanActivity","Let see if its called when data is found");
                            Intent resultIntent = new Intent();
                            intentData = barcodes.valueAt(0).displayValue;
//                            txtBarcodeValue.setText(intentData);

                            resultIntent.putExtra("exam_key", intentData);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                            cameraSource.stop();
                        }
                    });

                }
            }


        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        //when user pause the activity this is called to release resources
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        //when the user starts interacting with the activity this is called
        super.onResume();
        initialiseDetectorsAndSources();
    }


}