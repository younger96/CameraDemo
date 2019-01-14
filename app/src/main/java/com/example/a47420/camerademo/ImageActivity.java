package com.example.a47420.camerademo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.a47420.camerademo.base.BaseActivity;
import com.example.a47420.camerademo.util.BitmapUtils;
import com.example.a47420.camerademo.util.FileUtil;

import java.io.File;

/**
 * 2019/1/14
 * from 陈秋阳
 * 功能描述：
 */
public class ImageActivity extends BaseActivity {
    ImageView img ;
    String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("path"))){
            path = getIntent().getStringExtra("path");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void init() {
        if (!TextUtils.isEmpty(path)){
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            img.setImageURI(uri);
        }
    }

    @Override
    protected void initView() {
        img = findViewById(R.id.img);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_img;
    }
}
