package team_10.nourriture_android.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by ping on 2014/12/24.
 */
public class GlobalParams {
    /**
     * 程序数据在sd卡上保存的根目录
     */
    public static final String GXY_ROOT_DIR = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "nourriture"
            + File.separator;

    public static final String GXY_USER_DIR = GXY_ROOT_DIR + "user" + File.separator;

    public static final String GXY_data_DIR = GXY_ROOT_DIR + "data" + File.separator;

//	public static final String MAIN_URL = "http://5.196.19.84:1337/json";

    /**
     * 程序图片数据在sd卡上保存的目录
     */
    public final static String STR_CAPTURE_FILE_PATH = GXY_ROOT_DIR + "Photo"
            + File.separator;

    public final static String STR_SERIALIZEBLE_FILE_PATH = GXY_ROOT_DIR
            + "Serialize" + File.separator;

    public static final String CACHE_DIR = GXY_ROOT_DIR + "Cache"
            + File.separator;

    public static final String POSTFIX_CACHE = ".cache";

    public static final String TAG_GOBAL_SHARE_PREFERENCES = "nourriture_preferences";

    public static final String TAG_LOGIN_PREFERENCES = "nourriture_user_login";

    public static final String TAG_NOTIFICATION_PREFERENCES = "nourriture_notification_num";

    /*
     * 从连接池中取连接的超时时间
     */
    public static final int CONNMANAGERTIMEOUT = 10 * 1000;
    /* 连接超时 */
    public static final int CONNECTIONTIMEOUT = 10 * 1000;
    /* 请求超时 */
    public static final int SOTIMEOUT = 10 * 1000;

}