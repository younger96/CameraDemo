package com.example.a47420.camerademo;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.example.a47420.camerademo.base.BaseActivity;
import com.example.a47420.camerademo.camera.CameraController;
import com.example.a47420.camerademo.camera.CameraFrameLayout;

/**
 * 2018/12/19
 * from 陈秋阳
 * 功能描述：
 */
public class CameraActivity extends BaseActivity {
    CameraFrameLayout cameraFrameLayout;





    @Override
    protected void initView() {
        cameraFrameLayout = findViewById(R.id.cameraSurfaceView);
    }

    @Override
    protected void init() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraFrameLayout.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraFrameLayout.closeCamera();
    }


    @Override
    protected void destroyView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }
}
