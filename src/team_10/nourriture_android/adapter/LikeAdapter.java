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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

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
import team_10.nourriture_android.bean.LikeBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2015/1/6.
 */
public class LikeAdapter extends BaseAdapter {

    public List<LikeBean> mLikeList = new ArrayList<LikeBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private LikeViewHolder lvh = null;
    private DishBean dishBean;
    private SharedPreferences sp;

    public LikeAdapter(Context context, List<LikeBean> dishList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mLikeList = dishList;
    }

    public LikeAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_favor_dish, null);
            lvh = new LikeViewHolder();
            lvh.favor_dish_item_ll = (LinearLayout) convertView.findViewById(R.id.favor_dish_item_ll);
            lvh.dish = (TextView) convertView.findViewById(R.id.tv_favor_dish);
            lvh.user = (TextView) convertView.findViewById(R.id.tv_favor_user);
            lvh.time = (TextView) convertView.findViewById(R.id.tv_favor_time);
            convertView.setTag(lvh);
        } else {
            lvh = (LikeViewHolder) convertView.getTag();
        }

        final LikeBean likeBean = (LikeBean) mLikeList.get(position);
        lvh.dish.setText(likeBean.getDish());
        lvh.user.setText(likeBean.getUser());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(likeBean.getDate());
        lvh.time.setText(strDate);

        lvh.favor_dish_item_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDishFromFavor(likeBean);
            }
        });

        lvh.favor_dish_item_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Confirm delete");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Do you really want to delete the favor dish ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFavorDish(likeBean);
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

        return convertView;
    }

    public void getDishFromFavor(LikeBean likeBean) {
        String url = "dishes/" + likeBean.getDish();
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("get dish by favor", response.toString());
                if (statusCode == 200) {
                    try {
                        dishBean = (DishBean) JsonTobean.getList(DishBean[].class, response.toString()).get(0);
                        Intent intent = new Intent(mContext, DishDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dishBean", dishBean);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteFavorDish(final LikeBean likeBean) {
        sp = mContext.getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        String url = "likes/" + likeBean.get_id();
        NourritureRestClient.deleteWithLogin(url, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("delete favor like", response.toString());
                if (statusCode == 204) {
                    mLikeList.remove(likeBean);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Deleting favor dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(mContext, "Deleting favor dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(mContext, "Deleting favor dish is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Object getItem(int position) {
        return mLikeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mLikeList.size();
    }

    static class LikeViewHolder {
        TextView dish;
        TextView user;
        TextView time;
        LinearLayout favor_dish_item_ll;
    }
}
