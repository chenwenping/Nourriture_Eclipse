package team_10.nourriture_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Date;
import java.util.UUID;

public class SharedPreferencesUtil {

    public static final String TAG_AUTO_LOGIN_BOOL = "auto_login";
    public static final String TAG_USER_NAME = "USER_NAME";
    public static final String TAG_PASSWORD = "SAVE_PASSWORD";
    public static final String TAG_IS_LOGIN = "IS_LOGIN";
    public static final String TAG_LOGIN_TIME = "USER_TIME";
    public static final String TAG_USER_TOKEN = "TAG_USER_TOKEN";
    private static final String SHAREDPREFERE_NAME = "nourriture_";
    private static final String TAG_NOTIFICATION_NUM = "NOTIFICATION_NUM";

    /**
     * 保存登录
     *
     * @param context
     * @param uid
     * @param pwd
     * @param isLogin
     */
    public static synchronized void saveLogin(Context context, String uid, String pwd, boolean isLogin) {
        SharedPreferences sp = context.getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        Date d = new Date();
        Editor editor = sp.edit();
        editor.putString(TAG_USER_NAME, uid);
        editor.putString(TAG_PASSWORD, pwd);
        editor.putBoolean(TAG_IS_LOGIN, isLogin);
        editor.putLong(TAG_LOGIN_TIME, d.getTime());
        editor.commit();
    }

    public static synchronized void saveNotificationNum(Context context, String notification_num) {
        SharedPreferences sp = context.getSharedPreferences(GlobalParams.TAG_NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(TAG_NOTIFICATION_NUM, notification_num);
        editor.commit();
    }

    public static synchronized String getNotificationNum(Context context) {
        SharedPreferences sp = context.getSharedPreferences(GlobalParams.TAG_NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getString(TAG_NOTIFICATION_NUM, "");
    }

    /*public static synchronized String getUserName(Context context){
        SharedPreferences sp=context.getSharedPreferences(GlobalParams.TAG_GOBAL_SHARE_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getString(TAG_USER_NAME, "");
    }

    public static synchronized String getPassword(Context context){
        SharedPreferences sp=context.getSharedPreferences(GlobalParams.TAG_GOBAL_SHARE_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getString(TAG_PASSWORD, "");
    }

    public static synchronized boolean getIsLogin(Context context){
        SharedPreferences sp=context.getSharedPreferences(GlobalParams.TAG_GOBAL_SHARE_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getBoolean(TAG_IS_LOGIN, false);
    }*/

    public static synchronized String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(GlobalParams.TAG_GOBAL_SHARE_PREFERENCES, Context.MODE_PRIVATE);
        return sp.getString(TAG_USER_TOKEN, "");
    }

    /**
     * 清理登录
     *
     * @param context
     */
    public static synchronized void clearLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(GlobalParams.TAG_GOBAL_SHARE_PREFERENCES, 0);
        Editor editor = sp.edit();
        editor.putString(SharedPreferencesUtil.TAG_PASSWORD, "");
        editor.putBoolean(SharedPreferencesUtil.TAG_AUTO_LOGIN_BOOL, false);
        editor.commit();
    }

    /**
     * 获取唯一标示ID，该id在每一台设备上对应的应用都是唯一的
     *
     * @param context
     * @return
     */
    public synchronized static String getInstallationId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERE_NAME, Context.MODE_PRIVATE);
        String installationId = sp.getString("installationId", "");
        if ("".equals(installationId.trim())) {
            String uuid = UUID.randomUUID().toString();
            Date d = new Date();
            String sd = String.valueOf(d.getTime());
            if (sd.length() > 13) {
                installationId = uuid.substring(uuid.length() - 10, uuid.length()) + sd.substring(sd.length() - 13, sd.length());
            } else {
                installationId = uuid.substring(uuid.length() - 10, uuid.length()) + sd;
            }
            Editor editor = sp.edit();
            editor.putString("installationId", installationId);
            editor.commit();
        }
        return installationId;
    }

    public static synchronized void saveMSN(Context context, long u_id, String MSN[]) {
        StringBuffer sb = new StringBuffer();
        for (String m : MSN) {
            sb.append(m).append(";");
        }
        SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERE_NAME + u_id, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("MSN", sb.toString());
        editor.commit();
    }

    public static synchronized String[] getMSN(Context context, long u_id) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERE_NAME + u_id, Context.MODE_PRIVATE);
        String s = sp.getString("MSN", "");
        String MSN[] = null;
        if (s != null && s.equals(""))
            MSN = s.split(";");
        return MSN;
    }
}
