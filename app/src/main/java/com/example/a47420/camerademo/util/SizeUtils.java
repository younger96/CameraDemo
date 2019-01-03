package com.example.a47420.camerademo.util;

import android.graphics.Rect;
import android.graphics.RectF;

import com.example.a47420.camerademo.MyApp;

/**
 * 2018/12/20
 * from 陈秋阳
 * 功能描述：
 */
public final class SizeUtils {


    private static final float sDensity = MyApp.getApplicationInstance().getResources().getDisplayMetrics().density;
    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(final float dpValue) {
        return (int) (dpValue * sDensity + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(final float pxValue) {
        return (int) (pxValue / sDensity + 0.5f);
    }


}
