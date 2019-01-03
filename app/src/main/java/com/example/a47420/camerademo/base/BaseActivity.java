package com.example.a47420.camerademo.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

/**
 * 2018/12/20
 * from 陈秋阳
 * 功能描述：
 */
public abstract class BaseActivity extends Activity {
    protected Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        mContext = this;
        initView();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyView();
    }

    protected abstract void destroyView();

    protected abstract void init();

    protected abstract void initView();

    protected abstract int getLayoutId();
}

