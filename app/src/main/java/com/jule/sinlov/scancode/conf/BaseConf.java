package com.jule.sinlov.scancode.conf;

/**
 * Base Conf
 * @author: sinlov
 * @version: 1.0
 * @createDate: 15/10/8 00:31
 */
public class BaseConf {

    public static final boolean DEBUG_MODE = true;

    public static final String TEST_USER_NAME = "test";
    public static final String TEST_PASS_WORD = "123456";

    /**
     * double click back button to exit APP time set.
     */
    public static final Long CLOSE_APP_TIME = 2000l;
    /**
     * Auto skip time
     */
    public static final Long AUTO_SKIP_TIME = 1000l;

    public static final int DELAY_BEFORE_TIME = 100;

    public static final int ANIMATION_TIME_ORDINARY = 2000;
    public static final int ANIMATION_TIME_LONG = 4000;
    public static final int ANIMATION_TIME_FAST = 500;
    public static final int ANIMATION_TIME_MID = 1000;

    public static final int IMAGE_LOADER_MEMORY_CACHE_EXTRA_OPTIONS_MAX_WIDTH = 400;
    public static final int IMAGE_LOADER_MEMORY_CACHE_EXTRA_OPTIONS_MAX_HEIGHT = 400;
    public static final int IMAGE_LOADER_MEMORY_CACHE_SIZE_MB = 2;
    public static final int IMAGE_LOADER_CACHE_SIZE_PERCENTAGE = 14;
    public static final int IMAGE_LOADER_DISC_CACHE_SIZE_MB = 50;
    public static final int IMAGE_LOADER_DISC_CACHE_FILE_COUNT = 10000;
    public static final int IMAGE_LOADER_THREAD_POOL_SIZE = 3;
    public static final int IMAGE_LOADER_THREAD_POOL_PRIORITY = 1;


}
