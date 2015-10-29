package com.loqti.afw.zxing;

/**
 * for ZXing utils config
 * Created by "sinlov" on 2015/10/28.
 */
public class ZXingConf {
    public static final String KEY_SCAN_RESULT = "activity:capture:result";

    public static boolean DEBUG = false;

    public static void setDEBUG(boolean DEBUG) {
        ZXingConf.DEBUG = DEBUG;
    }

    private ZXingConf() {
    }
}
