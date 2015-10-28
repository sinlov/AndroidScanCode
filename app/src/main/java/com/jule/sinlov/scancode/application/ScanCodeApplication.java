package com.jule.sinlov.scancode.application;

import android.content.Context;

import com.loqti.afw.lifecycle.BaseApplication;

/**
 * for Scan Code App Application
 * Created by "sinlov" on 2015/10/28.
 */
public class ScanCodeApplication extends BaseApplication{

    private static ScanCodeApplication myApplication;
    @Override
    protected void initAppInfo() {

    }

    public static final Context getContext() {
        return BaseApplication.context;
    }

    public static ScanCodeApplication getInstance() {
        if (null == myApplication){
            myApplication = (ScanCodeApplication) BaseApplication.getInstance();
        }
        return myApplication;
    }
}
