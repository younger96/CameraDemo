package com.example.a47420.camerademo;

import android.app.Application;
import android.content.Context;

/**
 * 2018/12/17
 * from 陈秋阳
 * 功能描述：
 */
public class MyApp extends Application {
    private static Context mContext;


    public static Context getApplicationInstance() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }


}
