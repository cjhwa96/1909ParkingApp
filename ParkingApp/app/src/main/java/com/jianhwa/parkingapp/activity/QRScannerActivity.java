package com.jianhwa.parkingapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.jianhwa.parkingapp.R;
import com.jianhwa.parkingapp.entity.ParkingTicket;
import com.jianhwa.parkingapp.entity.ServerService;

import org.w3c.dom.Text;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;

public class QRScannerActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;

    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView_QRpreview;
    BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        if (checkPermission()) {

            surfaceView = findViewById(R.id.QRcamerapreview);
            textView_QRpreview = findViewById(R.id.QR_textView);

            barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.QR_CODE).build();

            cameraSource = new CameraSource.Builder(this, barcodeDetector)
                    .setRequestedPreviewSize(640, 480) .setAutoFocusEnabled(true) .build();

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    try {
                        cameraSource.start(holder);
                    } catch (IOException e) {
                        System.out.println("[QRScannerActivity] Failed to start camera source." );
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

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                    if (qrCodes.size() != 0){
                        textView_QRpreview.post(new Runnable() {
                            @Override
                            public void run() {
                                Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(1000);
                                if (qrCodes.valueAt(0).displayValue.equals("DriveIn")){
                                    Intent intent = new Intent(QRScannerActivity.this, EnteringActivity.class);
                                    intent.putExtra("QR_TYPE", "DriveIn");
                                    startActivity(intent);
                                    finish();
                                } else if (qrCodes.valueAt(0).displayValue.equals("ExpressIn")){
                                    Intent intent = new Intent(QRScannerActivity.this, EnteringActivity.class);
                                    intent.putExtra("QR_TYPE", "ExpressIn");
                                    startActivity(intent);
                                    finish();
                                } else if (qrCodes.valueAt(0).displayValue.equals("Exit")){
                                    Intent intent = new Intent(QRScannerActivity.this, EnteringActivity.class);
                                    intent.putExtra("QR_TYPE", "Exit");
                                    startActivity(intent);
                                    finish();
                                }
                                textView_QRpreview.setText(qrCodes.valueAt(0).displayValue);
                            }
                        });
                    }
                }
            });

        } else {
            requestPermission();
            finish();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted\nRetry QR Scanner", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QRScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
