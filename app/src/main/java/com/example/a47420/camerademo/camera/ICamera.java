package com.example.a47420.camerademo.camera;

import android.graphics.Rect;

/**
 * 2018/12/19
 * from 陈秋阳
 * 功能描述：
 */
interface ICamera {

    void open(int type);
    void close();

    void doCapture(CameraFrameLayout.OnGetPathListener onGetPathListener);//拍照
    void doOpenOrCloseLight();//打开闪光灯
    void doFocusArea(int x, int y);//点击对焦
}
