package com.example.a47420.camerademo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zhouztashin on 2015/7/24.
 */
public class BitmapUtils {

    public static final String TAG = "BitmapUtils";
    private static final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 保存bitmap
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static boolean saveBitmaps(Context context, Bitmap bitmap, File newFile) {
        try {

            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                    newFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            return true;
        } catch (Exception e) {
            Log.d("hyh", "BitmapUtils: saveBitmaps: error");
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap view2Bitmap(View view) {
        int w = view.getWidth();
        int h = view.getHeight();

        //Matrix matrix = new Matrix();
        //float scale = view.getScaleX();
        //matrix.preScale(scale, scale);
        //
        //Bitmap bmp = Bitmap.createBitmap((int)(w * scale), (int)(h * scale), Bitmap.Config.ARGB_8888);
        //Canvas c = new Canvas(bmp);
        //c.setMatrix(matrix);


        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        view.draw(c);

        return bmp;
    }

    public static String saveBitmap(Bitmap bmp) {
        Log.i(TAG, "saving Bitmap : ");
        return saveBitmap(bmp, rootPath);
    }

    public static String saveBitmap(Bitmap bmp, String path) {
        Log.i(TAG, "saving Bitmap : " + path);
        String fileName = getCurPNGFileName();
        return saveBitmap(bmp, path, fileName);
    }

    public static String saveBitmap(Bitmap bmp, String path, String fileName) {
        String name = path + "/" + fileName;
        Log.i(TAG, "saving Bitmap : " + name);
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fileout = new FileOutputStream(name);
            BufferedOutputStream bufferOutStream = new BufferedOutputStream(fileout);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileout);
            bufferOutStream.flush();
            bufferOutStream.close();
            Log.i(TAG, "Bitmap " + name + " saved!");
        } catch (IOException e) {
            Log.e(TAG, "Err when saving bitmap...");
            e.printStackTrace();
            return null;
        }
        return name;
    }

    private static String getCurPNGFileName() {
        return   getTime() + ".png";
    }

    private static String getTime() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
    }

    public static Bitmap resizeBitmap(String bmpFilePath, int requestedWidth,
                                      int requestedHeight) {
        if (requestedWidth <= 0 || requestedHeight <= 0) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeFile(bmpFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();

        // 获得图片的宽高
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(bmpFilePath, options);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        final int srcWidth = options.outWidth;
        final int srcHeight = options.outHeight;

        options.inSampleSize = (int) getScale(srcWidth, srcHeight,
                requestedWidth, requestedHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(bmpFilePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap == null) {
            return null;
        }

        // 个别机型，用以上方法的压缩率不符合要求，所以再次精确压缩
        if (bitmap.getWidth() > requestedWidth
                || bitmap.getHeight() > requestedHeight) {
            bitmap = resizeBitmapInOldWay(bitmap, requestedWidth,
                    requestedHeight);
        }
        return bitmap;

    }

    /**
     * 统一计算缩放比例
     *
     * @param srcWidth
     * @param requestedWidth
     * @return
     */
    public static float getScale(int srcWidth, int srcHeight,
                                 int requestedWidth, int requestedHeight) {
        float scale = 1;
        if (requestedWidth <= 0 && requestedHeight <= 0) {
            // 不做任何缩放
            scale = 1;
        } else if (requestedWidth > 0 && requestedHeight > 0) {

            float scaleWidth = srcWidth * 1.0f / requestedWidth;
            float scaleHeight = srcHeight * 1.0f / requestedHeight;
            if (scaleWidth < scaleHeight) {
                scale = scaleHeight;
            } else {
                scale = scaleWidth;
            }
        } else if (requestedWidth > 0 && requestedHeight <= 0) {
            float scaleWidth = srcWidth * 1.0f / requestedWidth;
            scale = scaleWidth;
        } else if (requestedWidth <= 0 && requestedHeight > 0) {
            float scaleHeight = srcHeight * 1.0f / requestedHeight;
            scale = scaleHeight;
        }

        return scale;
    }
    public static Bitmap resizeBitmapInOldWay(Bitmap bitmap, int maxWidth,
                                              int maxHeight) {
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // matrix的缩小参数应该为getScale的倒数(小数)
        float scale = 1 / getScale(width, height, maxWidth, maxHeight);
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = null;
        try {
            newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newbm;
    }



    /**
     * 将bitmap调整到指定大小 （中间截取）
     * 超长图 假设10:28  按照控件的比例来截取中间的长度   9:16 = 10:y  算出y，在28中间截取出y
     * @param origin
     * @param newWidth  10
     * @param newHeight  28
     * @return
     */
    public static Bitmap sizeBitmapMiddle(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scale = ((float) newWidth) / (float) newHeight;
        int y = (int) ((float)width/scale) + 1; //精度丢失
        int y0 = height/2 - y/2  ;
//        int y1 = newHeight/2 + y/2;
        Log.i(TAG, "sizeBitmapMiddle: "+"0 "+y0+" "+width+" "+y);
        return Bitmap.createBitmap(origin, 0, y0, width, y);
    }

    //按比例缩放
    public static Bitmap scaleBitmap(Bitmap origin, float scale) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(scale, scale);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    //在Canvas上画图，并得到Canvas上的Bitmap
    public static Bitmap createBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(screenshot);
        canvas.translate(-v.getScrollX(), -v.getScrollY());//我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
        v.draw(canvas);// 将 view 画到画布上
        return screenshot;
    }

    //获得旋转后的图片，内部旋转，长宽不变
    public static Bitmap createBitmapRotate(Bitmap bitmap, int mRotation) {
        Matrix matrix = new Matrix();
        matrix.setRotate(mRotation, bitmap.getWidth(),
                bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }



    //裁剪出displayWidth:displayHeight的图片 适配宽屏,传入参数
    public static Bitmap recreateBitmap16_9(Bitmap origin, int displayWidth, int displayHeight){
        if (origin == null) {
            return null;
        }
        float originScale = (float)origin.getHeight()/origin.getWidth();
        float displayScale = (float) displayHeight/displayWidth;

        Log.i(TAG,"originScale"+originScale+"  displayScale"+displayScale);

        //刚好展示16:9
        if (originScale == displayScale){
            return origin;
        }else if (originScale < displayScale){//图片小于16:9   有可能是15:9  宽度适配
            int x = (int) (((float)origin.getWidth()-displayWidth)/2 + 0.5);
            int y = (int) (((float)origin.getHeight()-displayHeight)/2 + 0.5);
            return Bitmap.createBitmap(origin, x, y, displayWidth, displayHeight);
        }else {//图片大于16:9  有可能就是18:9
            int x = (int) (((float)origin.getWidth()-displayWidth)/2 + 0.5);
            int y = (int) (((float)origin.getHeight()-displayHeight)/2 + 0.5);
            return Bitmap.createBitmap(origin, x, y, displayWidth, displayHeight);
        }
    }

    /**
     * 裁剪出中间的图
     * @param origin
     * @param height
     * @return
     */
    public static Bitmap recreateBitmapInSize(Bitmap origin,int height){
        if (origin == null) {
            return null;
        }
        if (origin.getHeight()<=height){
            return origin;
        }


        int y0 = (int) (((float)origin.getHeight() - height )/2+ 0.5);

        return Bitmap.createBitmap(origin, 0, y0, origin.getWidth(), height);
    }

    //获得旋转后的图片，旋转后长宽互换
    public static Bitmap createBitmapRotateChange(Bitmap bitmap, int mRotation) {
        Matrix matrix = new Matrix();
        matrix.setRotate(mRotation, bitmap.getWidth()/2,
                bitmap.getHeight()/2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    //新建bitmap
    public static Bitmap createBitmapFromSize(int width,int heigth) {
        return Bitmap.createBitmap(width,heigth,Bitmap.Config.ARGB_4444);
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static Bitmap uri2Bimap(String path,Context context) throws IOException {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }



}
