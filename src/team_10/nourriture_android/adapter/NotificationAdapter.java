package team_10.nourriture_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.DishDetailActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.bean.NotificationBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2015/1/5.
 */
public class NotificationAdapter extends BaseAdapter {

    public List<NotificationBean> mNotificationList = new ArrayList<>();
    private DishBean dishBean;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private NotificationViewHolder nvh = null;
    private SharedPreferences sp;

    public NotificationAdapter(Context context, List<NotificationBean> notificationList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mNotificationList = notificationList;
    }

    public NotificationAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_notification, null);
            nvh = new NotificationViewHolder();
            nvh.notification_item_ll = (LinearLayout) convertView.findViewById(R.id.notification_item_ll);
            nvh.user = (TextView) convertView.findViewById(R.id.tv_notification_user);
            nvh.target = (TextView) convertView.findViewById(R.id.tv_notification_target);
            nvh.content = (TextView) convertView.findViewById(R.id.tv_notification_content);
            nvh.time = (TextView) convertView.findViewById(R.id.tv_notification_time);
            nvh.picture = (ImageView) convertView.findViewById(R.id.img_user_photo);
            convertView.setTag(nvh);
        } else {
            nvh = (NotificationViewHolder) convertView.getTag();
        }

        final NotificationBean notificationBean = (NotificationBean) mNotificationList.get(position);
        nvh.user.setText(notificationBean.getUser());
        nvh.target.setText(notificationBean.getTarget());
        nvh.content.setText(notificationBean.getContent());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(notificationBean.getDate());
        nvh.time.setText(strDate);

        nvh.notification_item_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readNotification(notificationBean);
                if ("dish".equals(notificationBean.getTargetType().trim())) {
                    getDishByNotification(notificationBean);
                }
            }
        });

        return convertView;
    }

    public void readNotification(final NotificationBean notificationBean) {
        sp = mContext.getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        RequestParams params = new RequestParams();
        params.add("notification_id", notificationBean.get_id());

        NourritureRestClient.putWithLogin("readNotification", params, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("readNotification", response.toString());
                mNotificationList.remove(notificationBean);
                notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("notificationNum", String.valueOf(mNotificationList.size() - 1));
                intent.setAction("android.action.Notification");
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void getDishByNotification(NotificationBean notificationBean) {
        String url = "dishes/" + notificationBean.getTarget();
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("get dish by notification", response.toString());
                if (statusCode == 200) {
                    try {
                        dishBean = JsonTobean.getList(DishBean[].class, response.toString()).get(0);
                        Intent intent = new Intent(mContext, DishDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dishBean", dishBean);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Network is error, please try it later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("get dish by notification", response.toString());
                if (statusCode == 200) {
                    try {
                        dishBean = JsonTobean.getBean(DishBean.class, response.toString());
                        Intent intent = new Intent(mContext, DishDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dishBean", dishBean);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Network is error, please try it later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(mContext, "Network is error, please try it later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Object getItem(int position) {
        return mNotificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mNotificationList.size();
    }

    static class NotificationViewHolder {
        TextView user;
        TextView content;
        TextView target;
        TextView time;
        ImageView picture;
        LinearLayout notification_item_ll;
    }
}