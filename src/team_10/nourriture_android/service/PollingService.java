package team_10.nourriture_android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.NotificationActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.NotificationBean;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

public class PollingService extends Service {

    public static final String ACTION = "team_10.nourriture_android.service.PollingService";
    private static final String NOTIFICATION_DATA_PATH = "_notification_data.bean";
    private Notification mNotification;
    private NotificationManager mManager;
    private List<NotificationBean> unReadNotificationList;
    private int notification_num = 0;
    private SharedPreferences sp;
    private boolean isLogin = false;
    private UserBean userBean;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initNotificationManager();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();
    }

    private void initNotificationManager() {
        mManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        mNotification = new Notification();
        mNotification.icon = icon;
        mNotification.tickerText = "New Notification";
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    private void showNotification() {
        if (isLogin) {
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            intent.putExtra("unReadNotificationList", (Serializable) unReadNotificationList);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            mNotification.when = System.currentTimeMillis();
            mNotification.setLatestEventInfo(getApplicationContext(),
                    getResources().getString(R.string.app_name), "You have unread notification.", pendingIntent);
            mManager.notify(0, mNotification);
        }
    }

    private void updateNotificationByBroadCast() {
        Intent intentBroadcast = new Intent();
        intentBroadcast.putExtra("notificationNum", String.valueOf(notification_num));
        intentBroadcast.setAction("android.action.Notification");
        sendBroadcast(intentBroadcast);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

    private void getUnReadNotification() {
        userBean = MyApplication.getInstance().getUserBeanFromFile();
        sp = getApplicationContext().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        NourritureRestClient.getWithLogin("getMyUnreadNotifications", null, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("polling unread notifications", response.toString());
                if (statusCode == 200) {
                    try {
                        unReadNotificationList = JsonTobean.getList(NotificationBean[].class, response.toString());
                        Collections.reverse(unReadNotificationList);
                        if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                            notification_num = unReadNotificationList.size();
                            int num = getLocalNotificationNum();
                            if (num == 0) {
                                showNotification();
                            } else if (notification_num > num) {
                                showNotification();
                            }
                            updateNotificationByBroadCast();
                            ObjectPersistence.writeObjectToFile(getApplicationContext(), unReadNotificationList, userBean.get_id() + NOTIFICATION_DATA_PATH);
                        } else {
                            notification_num = 0;
                            updateNotificationByBroadCast();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private int getLocalNotificationNum() {
        List<NotificationBean> localNotificationList = (List<NotificationBean>) ObjectPersistence.readObjectFromFile(getApplicationContext(), userBean.get_id() + NOTIFICATION_DATA_PATH);
        if (localNotificationList != null) {
            return localNotificationList.size();
        }
        return 0;
    }

    class PollingThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            while (true) {
                System.out.println("Polling...");
                try {
                    Thread.sleep(10000000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getUnReadNotification();
                Looper.loop();
            }
        }
    }
}
