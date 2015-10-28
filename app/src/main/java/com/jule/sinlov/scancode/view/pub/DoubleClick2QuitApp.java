package com.jule.sinlov.scancode.view.pub;

import android.app.Application;
import android.content.Context;
import android.view.KeyEvent;

import com.jule.sinlov.scancode.R;
import com.jule.sinlov.scancode.application.ScanCodeApplication;
import com.jule.sinlov.scancode.conf.BaseConf;
import com.loqti.afw.base.codewidget.ToastBuilder;
import com.loqti.afw.lifecycle.BaseApplication;

/**
 * User: sinlov
 * Version: 1.0
 * Date: 2015-09-23
 * Time: 17:29
 */
public class DoubleClick2QuitApp {

    private static final String ERR_CONTEXT_SET = "You have set error context, it must be application context";
    private long closeAppTime;
    private Context context;

    public void doubleClick2QuitApp(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            long nowTime = System.currentTimeMillis();
            if (nowTime - closeAppTime < BaseConf.CLOSE_APP_TIME){
                BaseApplication.getInstance().quit();
            }else{
                closeAppTime = nowTime;
                ToastBuilder.make(context, R.string.toast_msg_double_click_exit_app, ToastBuilder.DEFUAULT_TOAST);
            }
        }
    }

    public DoubleClick2QuitApp(Context context) {
        if (context instanceof Application){
            this.context = context;
        }else {
            new Throwable(ERR_CONTEXT_SET).printStackTrace();
            ScanCodeApplication.getInstance().quit();
        }
    }
}
