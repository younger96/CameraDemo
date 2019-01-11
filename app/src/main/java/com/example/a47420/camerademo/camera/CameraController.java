package com.example.a47420.camerademo.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ToneGenerator;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.a47420.camerademo.util.FileUtil;

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
    private static int displayWidth;
    private static int displayHeight;//设置展示的控件大小

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
    public void doFocusMetringArea(int x, int y) {//对焦
        if (displayWidth == 0 || displayHeight == 0){
            return;
        }
        camera.cancelAutoFocus();

        Rect focusRect = calculateTapArea(x,y,1f);
        Rect metringRect = calculateTapArea(x,y,1f);

        Camera.Parameters parameters;
        parameters = camera.getParameters();
        //对焦
        if (parameters.getMaxNumFocusAreas() > 0) {//支持对焦区域的个数,暂时只需要一个
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            parameters.setFocusAreas(focusAreas);
        } else {
            Log.i(TAG, "focus areas not supported");
        }

        //测光
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(metringRect, 800));
            parameters.setMeteringAreas(meteringAreas);
        }else {
            Log.i(TAG, "metering areas not supported");
        }


        final String currentFocusMode = parameters.getFocusMode();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(parameters);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Log.i(TAG, "onAutoFocus: Auto"+success);
                if (success){
                    Camera.Parameters params = camera.getParameters();
                    params.setFocusMode(currentFocusMode);
                    camera.setParameters(params);
                }
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
        Log.i(TAG, "setDisplayOrientation: "+rotation);
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

    public void setDisplayWidth(int displayWidth) {
        CameraController.displayWidth = displayWidth;
    }

    public void setDisplayHeight(int displayHeight) {
        CameraController.displayHeight = displayHeight;
    }

    //将点击的对焦点绘制成300*300的矩阵区域，区域在1000*1000内
    private static Rect calculateTapArea(float x, float y, float scaleSize) {
        int focusAreaSize = (int) (300 * scaleSize);
        int centerY = (int) (1000 - x / displayWidth * 2000);
        int centerX = (int) (y / displayHeight * 2000 - 1000);

        int halfAreaSize = focusAreaSize / 2;
        Rect rect = new Rect();
        if (centerX > 1000-halfAreaSize || centerX < -1000+halfAreaSize){//排除边界情况
            boolean isX = centerX>0;
            rect.left = isX?1000-focusAreaSize:-1000;
            rect.right = isX?1000:-1000+focusAreaSize;
        }else {
            rect.left = centerX-halfAreaSize;
            rect.right = centerX+halfAreaSize;
        }

        if (centerY > 1000-halfAreaSize || centerY < -1000+halfAreaSize){
            boolean isY = centerY>0;
            rect.top = isY?1000-focusAreaSize:-1000;
            rect.bottom = isY?1000:-1000+focusAreaSize;
        }else {
            rect.top = centerY-halfAreaSize;
            rect.bottom = centerY+halfAreaSize;
        }

        Log.i(TAG, "calculateTapAreaXY: "+"  x:"+x+"  y:"+y+" cX:"+centerX+"  cY:"+centerY);
        Log.i(TAG, "calculateTapAreaRect: "+"  left:"+rect.left+"  right:"+rect.right+"  top:"+rect.top+"  bottom:"+rect.bottom);
        return rect;
    }

    //将点击的对焦点绘制成50*50的矩阵区域，区域在1000*1000内
//    private static Rect calculateTapArea(float x, float y, float scaleSize) {
//        int focusAreaSize = (int) (300 * scaleSize);
//        int centerX = (int) (x / displayWidth * 2000 - 1000);
//        int centerY = (int) (y / displayHeight * 2000 - 1000);
//        int mPadding = 800;//不能到边界,因为对焦会模糊
//
//        int halfAreaSize = focusAreaSize / 2;
//        Rect rect = new Rect();
//        if (centerX > 1000-halfAreaSize || centerX < -1000+halfAreaSize){//排除边界情况
//            boolean isX = centerX>0;
//            rect.left = isX?1000-focusAreaSize:-1000;
//            rect.right = isX?1000:-1000+focusAreaSize;
//        }else {
//            rect.left = centerX-halfAreaSize;
//            rect.right = centerX+halfAreaSize;
//        }
//
//        if (centerY > 1000-halfAreaSize || centerY < -1000+halfAreaSize){
//            boolean isY = centerY>0;
//            rect.top = isY?1000-focusAreaSize:-1000;
//            rect.bottom = isY?1000:-1000+focusAreaSize;
//        }else {
//            rect.top = centerY-halfAreaSize;
//            rect.bottom = centerY+halfAreaSize;
//        }
//
//        Log.i(TAG, "calculateTapAreaXY: "+"  x:"+x+"  y:"+y+" cX:"+centerX+"  cY:"+centerY);
//        Log.i(TAG, "calculateTapAreaRect: "+"  left:"+rect.left+"  right:"+rect.right+"  top:"+rect.top+"  bottom:"+rect.bottom);
//        return rect;
//    }


    //min <= x <= max
    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
}
