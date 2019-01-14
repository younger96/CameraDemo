package com.example.a47420.camerademo.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    //保存照片
    public static void saveBitmap(Bitmap b) {
        String jpegName = rootPath + getTime() + ".png";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //保存照片
    public static String  saveBitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0){
            return null;
        }
        long start = System.currentTimeMillis();
        String jpegName = rootPath + "/" + getTime() + ".png";
        try {
            Bitmap bitmap = BitmapUtils.createBitmapRotate(BitmapUtils.Bytes2Bimap(bytes),90);
            File file = new File(jpegName);
            if(!file.exists())
                //创建文件
                file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(jpegName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.i(TAG, "saveBitmap: "+(System.currentTimeMillis()-start));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpegName;
    }

    //获取视频存储路径
    public static String getMediaOutputPath() {
        return rootPath + "/" + getTime() + ".mp4";
    }

    private static String getTime() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(currentTimeMillis()));
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}