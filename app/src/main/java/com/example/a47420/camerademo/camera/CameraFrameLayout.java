package com.example.a47420.camerademo.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.a47420.camerademo.BuildConfig;
import com.example.a47420.camerademo.R;
import com.example.a47420.camerademo.util.SizeUtils;

import java.io.File;

/**
 * 2018/12/20
 * from 陈秋阳
 * 功能描述：
 */
public class CameraFrameLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "CameraFrameLayout";
    private Button btnCapture;
    private Button btnLight;
    private SurfaceView surfaceView;
    private CameraController mCameraController;

    private Context mContext;


    public CameraFrameLayout(Context context) {
        this(context, null);
    }

    public CameraFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        View root = inflate(getContext(), R.layout.view_layout_camera, this);
        btnCapture = root.findViewById(R.id.btn_capture_camera_view_layout);
        surfaceView = root.findViewById(R.id.surface_view);
        btnLight = root.findViewById(R.id.btn_light_view_layout);
        mCameraController = new CameraController(surfaceView);
        btnCapture.setOnClickListener(this);
        btnLight.setOnClickListener(this);
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                mCameraController.setDisplayWidth(surfaceView.getWidth());
                mCameraController.setDisplayHeight(surfaceView.getHeight());
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: "+event.getX()+"  "+event.getY());
                handleFocus((int)event.getX(),(int)event.getY());
                break;
        }
        return super.onTouchEvent(event);
    }

    private void handleFocus(int x, int y) {
        mCameraController.doFocusArea(x,y);
    }

    public void openCamera() {
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                mCameraController.open(0);
            }
        });
    }


    public void closeCamera() {
        if (mCameraController != null) {
            mCameraController.close();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture_camera_view_layout:
                mCameraController.doCapture(new OnGetPathListener() {
                    @Override
                    public void getPath(final String path) {
                        //将图片交给Image程序处理
                        File file = new File(path);
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        //判断是否是AndroidN以及更高的版本
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                            intent.setDataAndType(contentUri, "image/jpeg");
                        } else {
                            Uri uri = Uri.fromFile(file);
                            intent.setDataAndType(uri, "image/jpeg");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        mContext.startActivity(intent);
                    }
                });
                break;
            case  R.id.btn_light_view_layout:
                mCameraController.doOpenOrCloseLight();
                break;
        }
    }

    interface OnGetPathListener {
        void getPath(String path);
    }
}
