package com.example.a47420.camerademo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.a47420.camerademo.util.PermissionManager;

/**
 * 2018/12/17
 * from 陈秋阳
 * 功能描述：
 */
public class PermissionActivity extends Activity {
    private String[] needPermissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SETTINGS
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        PermissionManager permissionManager = PermissionManager.getInstance(getApplicationContext());
        permissionManager.execute(this,needPermissions);
//        if (permissionManager.getGrantedInfo(needPermissions)){
//            startActivity(new Intent(PermissionActivity.this,MainActivity.class));
//        }


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PermissionActivity.this,CameraActivity.class));
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PermissionActivity.this,MainActivity.class));
            }
        });
    }
}
