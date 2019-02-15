
package com.example.a47420.camerademo.util.file;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.a47420.camerademo.MyApp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by walljiang on 2018/5/8.
 */

public class OutFileGenerator {
    private static final String TAG = "OutFileGenerator";
    public static File IMAGE_FOLDER;

    static {
        IMAGE_FOLDER = new File(MyApp.getApplicationInstance().getFilesDir(), "youngimage");
        if (!IMAGE_FOLDER.exists()) {
            IMAGE_FOLDER.mkdir();
        }
    }

    public static String generateFile(String sourceFile, byte[] bytes) {
        File file = new File(IMAGE_FOLDER, sourceFile);
        long start = System.currentTimeMillis();
        try {
            FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath());
            outputStream.write(bytes);
            outputStream.close();
            Log.i(TAG, "saveBitmap: "+(System.currentTimeMillis()-start));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }




//    public static void deleteThumbFile(String filePath) {
//        if (!TextUtils.isEmpty(filePath) && filePath.startsWith(IMAGE_FOLDER.getAbsolutePath())
//            && filePath.contains("_thumb")) {
//            deleteFile(new File(filePath));
//        }
//    }


//    public static void deleteFile(final File file) {
//        if (file == null || !file.exists()) {
//            return;
//        }
//        ThreadPool.shorter().execute(new Runnable() {
//            @Override
//            public void run() {
//                FileUtils.deleteFile(file);
//            }
//        });
//    }
}
