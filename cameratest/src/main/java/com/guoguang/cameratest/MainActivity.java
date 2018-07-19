package com.guoguang.cameratest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.guoguang.camera2.Camera2Helper;
import com.guoguang.camera2.Camera2TextureView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Camera2Helper camera2Helper;
    private File file;
    private Camera2TextureView textureView;

    private static final String PHOTO_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String PHOTO_NAME = "camera2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera2Helper.startCamerapreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera2Helper.onDestoryHelper();
    }

    private void init() {
        textureView = (Camera2TextureView) findViewById(R.id.textureView);
        file = new File(PHOTO_PATH, PHOTO_NAME + ".jpg");
        camera2Helper = Camera2Helper.getInstace(MainActivity.this, textureView, file, this);
    }
}
