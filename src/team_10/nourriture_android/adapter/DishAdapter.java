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
import team_10.nourriture_android.activity.DishDetailActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/21.
 */
public class DishAdapter extends BaseAdapter {

    public List<DishBean> mDishList = new ArrayList<DishBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private boolean isUserDish = false;
    private DishViewHolder dvh = null;
//    private String pictureBaseUrl = "http://5.196.19.84:1337/";
    private String pictureBaseUrl = "http://176.31.191.185:1337/";

    public DishAdapter(Context context, List<DishBean> dishList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDishList = dishList;
    }

    public DishAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    public DishAdapter(Context context, boolean update, boolean userDish) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
        isUserDish = userDish;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_dish, null);
            dvh = new DishViewHolder();
            dvh.dish_item_rl = (LinearLayout) convertView.findViewById(R.id.dish_item_ll);
            dvh.name = (TextView) convertView.findViewById(R.id.dish_name_tv);
            dvh.description = (TextView) convertView.findViewById(R.id.dish_description_tv);
            dvh.time = (TextView) convertView.findViewById(R.id.dish_time_tv);
            dvh.picture = (ImageView) convertView.findViewById(R.id.dish_picture_img);
            convertView.setTag(dvh);
        } else {
            dvh = (DishViewHolder) convertView.getTag();
        }

        final DishBean dishBean = (DishBean) mDishList.get(position);
        dvh.name.setText(dishBean.getName());
        dvh.description.setText(dishBean.getDescription());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(dishBean.getDate());
        dvh.time.setText(strDate);

        AsynImageLoader asynImageLoader = new AsynImageLoader();
        if (dishBean.getPicture() == null || "".equals(dishBean.getPicture().trim()) || "null".equals(dishBean.getPicture().trim())) {
            dvh.picture.setImageResource(R.drawable.default_dish_picture);
        } else {
            asynImageLoader.showImageAsyn(dvh.picture, pictureBaseUrl + dishBean.getPicture(), R.drawable.default_dish_picture);
        }

        dvh.dish_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DishDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dishBean", dishBean);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        if (isUserDish) {
            dvh.dish_item_rl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Confirm delete");
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setMessage("Do you really want to delete the dish ?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUserDish(dishBean);
                        }
                    });
                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                    return true;
                }
            });
        }

        return convertView;
    }

    public void deleteUserDish(final DishBean dishBean) {
        String url = "dishes/" + dishBean.get_id();

        SharedPreferences sp = mContext.getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        NourritureRestClient.deleteWithLogin(url, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("delete dish", response.toString());
                if (statusCode == 204) {
                    mDishList.remove(dishBean);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Deleting dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(mContext, "Deleting dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(mContext, "Deleting dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Object getItem(int position) {
        return mDishList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mDishList.size();
    }

    static class DishViewHolder {
        TextView name;
        TextView description;
        TextView time;
        ImageView picture;
        LinearLayout dish_item_rl;
    }
}
