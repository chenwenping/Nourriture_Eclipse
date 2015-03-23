package team_10.nourriture_android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.NotificationBean;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;


/**
 * Created by ping on 2014/12/20.
 */
public class NotificationFragment extends Fragment implements View.OnClickListener {

    private static final String NOTIFICATION_DATA_PATH = "_notification_data.bean";
    private LinearLayout ll_user;
    private RelativeLayout rl_user_login, rl_user_info;
    private Button btn_login;
    private TextView tv_name, tv_birth, tv_introduction, tv_notification_num;
    private ImageView img_photo;
    private LinearLayout ll_dishes_comment, ll_favor_dishes, ll_my_friends, ll_my_notification;
    private UserBean userBean;
    private boolean isLogin = false;
    private SharedPreferences sp;
    private int request = 2;
    private NotificationBroadCast receiver;
    private int notification_num = 0;
    private List<NotificationBean> unReadNotificationList;
    private String pictureBaseUrl = "http://176.31.191.185:1337/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (receiver == null) {
            receiver = new NotificationBroadCast();
        }
        IntentFilter intentFilter = new IntentFilter("android.action.Notification");
        getActivity().registerReceiver(receiver, intentFilter);

        ll_user = (LinearLayout) getActivity().findViewById(R.id.user_ll);
        rl_user_login = (RelativeLayout) getActivity().findViewById(R.id.user_login_rl);
        rl_user_info = (RelativeLayout) getActivity().findViewById(R.id.user_info_rl);
        btn_login = (Button) getActivity().findViewById(R.id.login_btn);
        img_photo = (ImageView) getActivity().findViewById(R.id.photo_img);
        tv_name = (TextView) getActivity().findViewById(R.id.name_tv);
        tv_birth = (TextView) getActivity().findViewById(R.id.birth_tv);
        tv_introduction = (TextView) getActivity().findViewById(R.id.introduction_tv);
        tv_notification_num = (TextView) getActivity().findViewById(R.id.notification_num_tv);

        if (isLogin) {
            rl_user_login.setVisibility(View.GONE);
            rl_user_info.setVisibility(View.VISIBLE);

            userBean = MyApplication.getInstance().getUserBeanFromFile();
            tv_name.setText(userBean.getUsername());
            tv_introduction.setText(userBean.getIntroduction());
            /*AsynImageLoader asynImageLoader = new AsynImageLoader();
            if (userBean.getPicture() == null || "".equals(userBean.getPicture().trim()) || "null".equals(userBean.getPicture().trim())) {
                img_photo.setImageResource(R.drawable.default_avatar);
            } else {
                asynImageLoader.showImageAsyn(img_photo, pictureBaseUrl + userBean.getPicture(), R.drawable.default_avatar);
            }*/

            ll_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userBean", userBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } else {
            rl_user_info.setVisibility(View.GONE);
            rl_user_login.setVisibility(View.VISIBLE);
        }

        ll_dishes_comment = (LinearLayout) getActivity().findViewById(R.id.ll_dishes_comment);
        ll_favor_dishes = (LinearLayout) getActivity().findViewById(R.id.ll_favor_dishes);
        ll_my_friends = (LinearLayout) getActivity().findViewById(R.id.ll_my_friends);
        ll_my_notification = (LinearLayout) getActivity().findViewById(R.id.ll_my_notification);

        btn_login.setOnClickListener(this);
        ll_dishes_comment.setOnClickListener(this);
        ll_favor_dishes.setOnClickListener(this);
        ll_my_friends.setOnClickListener(this);
        ll_my_notification.setOnClickListener(this);

        getMyUnreadNotifications();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, request);
                break;
            case R.id.ll_dishes_comment:
                if (isLogin) {
                    Intent intent1 = new Intent(getActivity().getApplicationContext(), UserCommentActivity.class);
                    startActivity(intent1);
                } else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent1, request);
                }
                break;
            case R.id.ll_favor_dishes:
                if (isLogin) {
                    Intent intent2 = new Intent(getActivity().getApplicationContext(), FavorDishesActivity.class);
                    startActivity(intent2);
                } else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent2, request);
                }
                break;
            case R.id.ll_my_friends:
                if (isLogin) {
                    Intent intent3 = new Intent(getActivity().getApplicationContext(), FriendsActivity.class);
                    startActivity(intent3);
                } else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent3, request);
                }
                break;
            case R.id.ll_my_notification:
                if (isLogin) {
                    if (notification_num > 0) {
                        Intent intent3 = new Intent(getActivity().getApplicationContext(), NotificationActivity.class);
                        intent3.putExtra("unReadNotificationList", (Serializable) unReadNotificationList);
                        startActivityForResult(intent3, request);
                    } else {
                        Toast.makeText(getActivity(), "There is no unread notification.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent3, request);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.KEY_IS_LOGIN) {
            isLogin = true;

            rl_user_login.setVisibility(View.GONE);
            rl_user_info.setVisibility(View.VISIBLE);
            userBean = MyApplication.getInstance().getUserBeanFromFile();
            tv_name.setText(userBean.getUsername());
            tv_introduction.setText(userBean.getIntroduction());
            AsynImageLoader asynImageLoader = new AsynImageLoader();
            if (userBean.getPicture() == null || "".equals(userBean.getPicture().trim()) || "null".equals(userBean.getPicture().trim())) {
                img_photo.setImageResource(R.drawable.default_avatar);
            } else {
                asynImageLoader.showImageAsyn(img_photo, userBean.getPicture(), R.drawable.default_avatar);
            }

            getLocalNotificationData();
            if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                tv_notification_num.setText(String.valueOf(unReadNotificationList.size()));
            } else {
                tv_notification_num.setVisibility(View.GONE);
            }
        } else if (resultCode == NotificationActivity.NOTIFICATION_IS_READ) {
            String num = data.getStringExtra("notification_num");
            if (num != null && !"".equals(num.trim()) && !"0".equals(num.trim())) {
                tv_notification_num.setVisibility(View.VISIBLE);
                tv_notification_num.setText(num);
                getMyUnreadNotifications();
            } else {
                tv_notification_num.setVisibility(View.GONE);
            }
        }
    }

    private void getMyUnreadNotifications() {
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
                        ObjectPersistence.writeObjectToFile(getActivity(), unReadNotificationList, userBean.get_id() + NOTIFICATION_DATA_PATH);
                        if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                            notification_num = unReadNotificationList.size();
                            tv_notification_num.setVisibility(View.VISIBLE);
                            tv_notification_num.setText(String.valueOf(notification_num));
                            //SharedPreferencesUtil.saveNotificationNum(getActivity(), String.valueOf(notification_num));
                        } else {
                            tv_notification_num.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalNotificationData();
                    if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                        tv_notification_num.setText(String.valueOf(unReadNotificationList.size()));
                    } else {
                        tv_notification_num.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getLocalNotificationData() {
        List<NotificationBean> localNotificationList = (List<NotificationBean>) ObjectPersistence.readObjectFromFile(getActivity(), userBean.get_id() + NOTIFICATION_DATA_PATH);
        if (localNotificationList != null) {
            unReadNotificationList = localNotificationList;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    class NotificationBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra("notificationNum");
            if (intent.getAction().equals("android.action.Notification")) {
                if (Integer.parseInt(number.toString().trim()) > 0) {
                    tv_notification_num.setVisibility(View.VISIBLE);
                    tv_notification_num.setText(number);
                    getMyUnreadNotifications();
                } else {
                    tv_notification_num.setVisibility(View.GONE);
                }
            }
        }
    }
}
