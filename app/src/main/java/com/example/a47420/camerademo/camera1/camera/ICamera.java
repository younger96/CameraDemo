package com.example.a47420.camerademo.camera1.camera;

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
    void doFocusMetringArea(int x, int y);//点击对焦
}
