package com.example.a47420.camerademo.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.a47420.camerademo.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 2018/12/19
 * from 陈秋阳
 * 功能描述：
 */
public class CameraController implements ICamera{
    private static final String TAG = "CameraController";
    private Camera camera;
    private ToneGenerator mTone;//声音控制
    private SurfaceHolder mSurfaceHolder;

    private SurfaceView mSurfaceView;


    private float displayScale;
    private int mToneValue = 0;//声音大小

    public CameraController(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mSurfaceHolder = mSurfaceView.getHolder();
    }

    @Override
    public void open(int type) {
        int rotation=((WindowManager)mSurfaceView.getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        if(!openCamera(type))return;
        setParameters(camera,rotation);
        setDisplayOrientation(camera,rotation);
        setPreviewDisplay(camera,mSurfaceHolder);
        camera.startPreview();
    }


    @Override
    public void close() {
        camera.release();
    }

    @Override
    public void doCapture(final CameraFrameLayout.OnGetPathListener onGetPathListener) {
        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {  //快门按下的时候onShutter()被回调
//                if(mTone == null){
//                    //发出提示用户的声音
//                    mTone = new ToneGenerator(AudioManager.STREAM_MUSIC,
//                            ToneGenerator.MIN_VOLUME);
//                }
//                mTone.startTone(ToneGenerator.TONE_PROP_NACK);
            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {//返回照片的JPEG格式的数据
                Camera.Parameters ps = camera.getParameters();
                if(ps.getPictureFormat() == PixelFormat.JPEG){
                    //存储拍照获得的图片
                    String path = FileUtil.saveBitmap(data);
                    onGetPathListener.getPath(path);
                }
            }
        });
    }

    @Override
    public void doOpenOrCloseLight() {
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)){//关闭闪光灯
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }else {//打开闪光灯
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
        camera.setParameters(parameters);
    }

    @Override
    public void doFocusArea(Rect focusRect) {//对焦
        camera.cancelAutoFocus();
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            parameters.setFocusAreas(focusAreas);
        } else {
            Log.i(TAG, "focus areas not supported");
        }
        final String currentFocusMode = parameters.getFocusMode();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(parameters);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
            }
        });
    }


    //相机使用第一步，打开相机，获得相机实例
    private boolean openCamera(int cameraId){
        if(!checkCameraId(cameraId))return false;
        camera=Camera.open(cameraId);
        return true;
    }

    //相机使用第二步，设置相机实例参数
    //TODO :里面还存在问题，需要修改
    private void setParameters(Camera camera,int rotation){
        Camera.Parameters parameters=camera.getParameters();

        //PreviewSize设置为设备支持的最高分辨率
        final Camera.Size size=Collections.max(camera.getParameters().getSupportedPreviewSizes(),new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return lhs.width*lhs.height-rhs.width*rhs.height;
            }
        });
        parameters.setPreviewSize(size.width,size.height);

        //PictureSize设置为和预览大小最近的
        Camera.Size picSize=Collections.max(parameters.getSupportedPictureSizes(), new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return (int) (Math.sqrt(Math.pow(size.width-rhs.width,2)+Math.pow(size.height-rhs.height,2))-
                        Math.sqrt(Math.pow(size.width-lhs.width,2)+Math.pow(size.height-lhs.height,2)));
            }
        });
        parameters.setPictureSize(picSize.width,picSize.height);
        //如果相机支持自动聚焦，则设置相机自动聚焦，否则不设置
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //设置滤镜效果
//        parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);

        //设置特效白平衡
//        parameters.setZoom();

        camera.setParameters(parameters);
        resizeDisplayView();
    }

    //相机使用第三步，设置相机预览方向
    private void setDisplayOrientation(Camera camera,int rotation){
        if(rotation== Surface.ROTATION_0||rotation==Surface.ROTATION_180){
            camera.setDisplayOrientation(90);
        }else{
            camera.setDisplayOrientation(0);
        }
    }

    //相机使用第四步，设置相机预览载体SurfaceHolder
    private void setPreviewDisplay(Camera camera,SurfaceHolder holder){
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //调整SurfaceView的大小
    private void resizeDisplayView(){
        Camera.Parameters parameters=camera.getParameters();
        Camera.Size size=parameters.getPreviewSize();
        FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        float scale=size.width/(float)size.height;
        displayScale=mSurfaceView.getHeight()/(float)mSurfaceView.getWidth();
        if(scale>displayScale){
            layoutParams.height= (int) (scale*mSurfaceView.getWidth());
            layoutParams.width=mSurfaceView.getWidth();
        }else{
            layoutParams.width= (int) (mSurfaceView.getHeight()/scale);
            layoutParams.height=mSurfaceView.getHeight();
        }
        Log.e(TAG,"-->"+size.width+"/"+size.height);
        Log.e(TAG,"--<"+layoutParams.height+"/"+layoutParams.width);
        mSurfaceView.setLayoutParams(layoutParams);
        mSurfaceView.invalidate();
    }


    //检查是否有摄像头
    private boolean checkCameraId(int cameraId){
        return cameraId>=0&&cameraId<Camera.getNumberOfCameras();
    }


}
