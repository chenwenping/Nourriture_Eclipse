package team_10.nourriture_android.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.NotificationAdapter;
import team_10.nourriture_android.bean.NotificationBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2015/1/4.
 */
public class NotificationActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String NOTIFICATION_DATA_PATH = "_notification_data.bean";
    public static int NOTIFICATION_IS_READ = 1;
    private Context mContext;
    private ProgressDialog progress;
    private SharedPreferences sp;
    private Button back_btn;
    private NotificationAdapter notificationAdapter;
    private SwipeRefreshLayout swipeLayout;
    private ListView notificationListView;
    private boolean isRefresh = false;
    private List<NotificationBean> unReadNotificationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mContext = this;
        progress = new ProgressDialog(this);
        sp = getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        initView();
        initData();
    }

    public void initView() {
        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        notificationListView = (ListView) this.findViewById(R.id.notificationListView);

        back_btn = (Button) this.findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
    }

    public void initData() {
        unReadNotificationList = (List<NotificationBean>) getIntent().getSerializableExtra("unReadNotificationList");
        notificationAdapter = new NotificationAdapter(mContext, false);
        if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
            notificationAdapter.mNotificationList.addAll(unReadNotificationList);
            notificationListView.setAdapter(notificationAdapter);
            notificationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getUnReadNotificationList();
                }
            }, 3000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent();
                if (notificationAdapter.mNotificationList != null) {
                    intent.putExtra("notification_num", String.valueOf(notificationAdapter.mNotificationList.size()));
                }
                setResult(NOTIFICATION_IS_READ, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        if (notificationAdapter.mNotificationList != null) {
            intent.putExtra("notification_num", String.valueOf(notificationAdapter.mNotificationList.size()));
        }
        setResult(NOTIFICATION_IS_READ, intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }

    public void getUnReadNotificationList() {
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
        NourritureRestClient.getWithLogin("getMyUnreadNotifications", null, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("getMyUnreadNotifications", response.toString());
                if (statusCode == 200) {
                    try {
                        unReadNotificationList = JsonTobean.getList(NotificationBean[].class, response.toString());
                        Collections.reverse(unReadNotificationList);
                        ObjectPersistence.writeObjectToFile(mContext, unReadNotificationList, NOTIFICATION_DATA_PATH);
                        if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                            if (notificationAdapter.mNotificationList != null && notificationAdapter.mNotificationList.size() > 0) {
                                notificationAdapter.mNotificationList.clear();
                            }
                            notificationAdapter.mNotificationList.addAll(unReadNotificationList);
                            isRefresh = false;
                            notificationListView.setAdapter(notificationAdapter);
                            notificationAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, "There is no unread notification", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalNotificationData();
                    if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                        if (notificationAdapter.mNotificationList != null && notificationAdapter.mNotificationList.size() > 0) {
                            notificationAdapter.mNotificationList.clear();
                        }
                        notificationAdapter.mNotificationList.addAll(unReadNotificationList);
                        isRefresh = false;
                        notificationListView.setAdapter(notificationAdapter);
                        notificationAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "There is no unread notification", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                getLocalNotificationData();
                if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                    if (notificationAdapter.mNotificationList != null && notificationAdapter.mNotificationList.size() > 0) {
                        notificationAdapter.mNotificationList.clear();
                    }
                    notificationAdapter.mNotificationList.addAll(unReadNotificationList);
                    isRefresh = false;
                    notificationListView.setAdapter(notificationAdapter);
                    notificationAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "There is no unread notification", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocalNotificationData() {
        List<NotificationBean> localNotificationList = (List<NotificationBean>) ObjectPersistence.readObjectFromFile(mContext, NOTIFICATION_DATA_PATH);
        if (localNotificationList != null) {
            unReadNotificationList = localNotificationList;
        }
    }
}
