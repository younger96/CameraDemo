package com.example.a47420.camerademo.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.example.a47420.camerademo.MyApp;

/**
 * 2018/12/21
 * from 陈秋阳
 * 功能描述：存储量度单位
 */
public class DimenseUtils {

    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;


    public static int getScreenWidth(){
        if (SCREEN_WIDTH == 0){
            Resources resources = MyApp.getApplicationInstance().getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
        }
        return SCREEN_WIDTH;
    }


    public static int getScreenHeight(){
        if (SCREEN_HEIGHT == 0){
            Resources resources = MyApp.getApplicationInstance().getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
        }
        return SCREEN_HEIGHT;
    }
}
