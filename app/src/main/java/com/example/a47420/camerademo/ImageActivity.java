package com.example.a47420.camerademo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.a47420.camerademo.base.BaseActivity;
import com.example.a47420.camerademo.util.BitmapUtils;

import java.io.IOException;

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
            try {
                final Bitmap bitmap = BitmapUtils.uri2Bimap(path,this);
                if (bitmap!=null){
                    img.post(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap finalB = resizeBitmap(bitmap,img.getWidth(),img.getHeight());
                            img.setImageBitmap(finalB);
                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Bitmap resizeBitmap(Bitmap origin, int width, int height) {
        Bitmap bF;
        if (origin.getWidth() > origin.getHeight()){
            bF = BitmapUtils.createBitmapRotate(origin,90);
        }else {
            bF = origin;
        }
        float bitScale = (float) bF.getWidth()/bF.getHeight();//图片比例
        float mScale = (float)width/height;//控件比例
        if (mScale > bitScale){//图片的宽小于长，宽度适配
            Matrix matrix = new Matrix();
            matrix.setScale(mScale/bitScale,1);
            bF =  Bitmap.createBitmap(bF, 0, 0, bF.getWidth(),
                    bF.getHeight(), matrix, true);
//            bF =  BitmapUtils.scaleBitmap(bF,mScale);
        }else if (mScale < bitScale){//图片的长小于宽，长度适配
            Matrix matrix = new Matrix();
            matrix.setScale(1,bitScale/mScale);
            bF =  Bitmap.createBitmap(bF, 0, 0, bF.getWidth(),
                    bF.getHeight(), matrix, true);
        }
        return bF;
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
