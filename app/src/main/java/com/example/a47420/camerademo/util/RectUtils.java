package com.example.a47420.camerademo.util;

import android.graphics.Rect;
import android.graphics.RectF;

import com.example.a47420.camerademo.MyApp;

/**
 * 2018/12/21
 * from 陈秋阳
 * 功能描述：
 */
public class RectUtils {

    /**
     * 绘制出矩形区域 , 让绘制的点相对于屏幕的中心点
     * @param x 触摸点的x坐标
     * @param y 触摸点的y坐标
     * @param width  总区域
     * @param height 总区域
     * @param focusAreaSize 需要绘制的区域大小
     * @return
     */
    private static Rect calculateTapArea(float x, float y, int width, int height,float focusAreaSize) {
        if (width == 0){
            width = DimenseUtils.getScreenWidth();
        }

        if (height == 0){
            height = DimenseUtils.getScreenHeight();
        }

        int areaSize = Float.valueOf(focusAreaSize).intValue();
        int centerX = (int) (x / width * 2000 - 1000);
        int centerY = (int) (y / height * 2000 - 1000);

        int halfAreaSize = areaSize / 2;
        RectF rectF = new RectF(clamp(centerX - halfAreaSize, -1000, 1000)
                , clamp(centerY - halfAreaSize, -1000, 1000)
                , clamp(centerX + halfAreaSize, -1000, 1000)
                , clamp(centerY + halfAreaSize, -1000, 1000));
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

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
