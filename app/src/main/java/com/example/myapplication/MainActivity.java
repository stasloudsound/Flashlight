package com.example.myapplication;


import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.hardware.*;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

class MyClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        Log.i("MyClickListener", "onClick:call method ");

    }
}

public class MainActivity extends AppCompatActivity {

    Bitmap originalImage, newImage;
    ImageView imageView;
    boolean isSwitcherOn, isFlashOn, hasFlash;
    int newWidth, newHeight;
    CameraManager cameraManager;
    String cameraId;
    Camera camera;
    Button button;
    Camera.Parameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[1];
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }


        setStartPosition();
        getSupportActionBar().setTitle("Setting");


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isSwitcherOn) {
                    isSwitcherOn = false;
                    newImage = Bitmap.createBitmap(originalImage, 0, 0, newWidth, newHeight);
                    imageView.setImageBitmap(newImage);

                    try {
                        cameraManager.setTorchMode(cameraId,isSwitcherOn);
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    isSwitcherOn = true;
                    newImage = Bitmap.createBitmap(originalImage, newWidth, 0, newWidth, newHeight);
                    imageView.setImageBitmap(newImage);

                    try {
                        cameraManager.setTorchMode(cameraId,isSwitcherOn);
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });


        Dexter
                .withActivity(this)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

        hasFlash = getApplicationContext()
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            getSupportActionBar().setTitle("No Flash");
        } else {
            getCamera();
        }
        Button btn = findViewById(R.id.btn_one);

        btn.setOnClickListener(view -> {
            Log.i("9999999999999", "onCreate: ");

            if (!isFlashOn) {
                //Toast.makeText(this, "Flash off", Toast.LENGTH_LONG).show();
                getSupportActionBar().setTitle("Flash Onn");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF00DDED));
                getOnn();
            } else {
                //Toast.makeText(this, "Flash onn", Toast.LENGTH_LONG).show();
                getSupportActionBar().setTitle("Flash Off");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFC34DDED));
                getOff();
            }
        });

    }

    private void setStartPosition() {
        imageView = findViewById(R.id.imageView);

        originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        newWidth = originalImage.getWidth() / 2;
        newHeight = originalImage.getHeight();

        newImage = Bitmap.createBitmap(originalImage, 0, 0, newWidth, newHeight);
        imageView.setImageBitmap(newImage);
        isSwitcherOn = false;

    }

    public void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                Toast.makeText(MainActivity.this, "Камеру удалось заполучить!", Toast.LENGTH_LONG).show();
            } catch (Exception exception) {
                getSupportActionBar().setTitle("getCamera ERROR");
            }
        }
    }

    public void getOnn() {

        if (camera == null || !hasFlash) {
            return;
        }
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        camera.setParameters(params);
        camera.startPreview();
        isFlashOn = true;

    }

    public void getOff() {

        if (camera == null || !hasFlash) {
            return;
        }
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
        isFlashOn = false;

    }
}