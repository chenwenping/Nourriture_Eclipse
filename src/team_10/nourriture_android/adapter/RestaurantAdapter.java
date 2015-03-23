package team_10.nourriture_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.RestaurantDetailActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.bean.RestaurantBean;
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2015/3/21.
 */
public class RestaurantAdapter extends BaseAdapter {

    public List<RestaurantBean> mRestaurantList = new ArrayList<RestaurantBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private RestaurantViewHolder rvh = null;
    private String pictureBaseUrl = "http://176.31.191.185:1337/";

    public RestaurantAdapter(Context context, List<RestaurantBean> restaurantList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mRestaurantList = restaurantList;
    }

    public RestaurantAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_restaurant, null);
            rvh = new RestaurantViewHolder();
            rvh.Restaurant_item_rl = (LinearLayout) convertView.findViewById(R.id.restaurant_item_ll);
            rvh.name = (TextView) convertView.findViewById(R.id.restaurant_name_tv);
            rvh.description = (TextView) convertView.findViewById(R.id.restaurant_description_tv);
            rvh.time = (TextView) convertView.findViewById(R.id.restaurant_time_tv);
            rvh.picture = (ImageView) convertView.findViewById(R.id.restaurant_picture_img);
            convertView.setTag(rvh);
        } else {
            rvh = (RestaurantViewHolder) convertView.getTag();
        }

        final RestaurantBean restaurantBean = (RestaurantBean) mRestaurantList.get(position);
        rvh.name.setText(restaurantBean.getName());
        rvh.description.setText(restaurantBean.getDescription());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(restaurantBean.getDate());
        rvh.time.setText(strDate);

        AsynImageLoader asynImageLoader = new AsynImageLoader();
        if (restaurantBean.getPicture() == null || "".equals(restaurantBean.getPicture().trim()) || "null".equals(restaurantBean.getPicture().trim())) {
            rvh.picture.setImageResource(R.drawable.default_dish_picture);
        } else {
            asynImageLoader.showImageAsyn(rvh.picture, pictureBaseUrl + restaurantBean.getPicture(), R.drawable.default_dish_picture);
        }

        rvh.Restaurant_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestaurantDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("RestaurantBean", restaurantBean);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mRestaurantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mRestaurantList.size();
    }

    static class RestaurantViewHolder {
        TextView name;
        TextView description;
        TextView time;
        ImageView picture;
        LinearLayout Restaurant_item_rl;
    }
}