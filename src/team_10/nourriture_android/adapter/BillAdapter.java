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
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.DishDetailActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.bean.BillBean;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/27.
 */
public class BillAdapter extends BaseAdapter {

    public List<BillBean> mBillList = new ArrayList<BillBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private DishBean dishBean;

    public BillAdapter(Context context, List<BillBean> commentList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mBillList = commentList;
    }

    public BillAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BillViewHolder bvh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bill, null);
            bvh = new BillViewHolder();
            bvh.bill_item_rl = (LinearLayout) convertView.findViewById(R.id.bill_item_ll);
            bvh.dish = (TextView) convertView.findViewById(R.id.bill_dish_tv);
            bvh.count = (TextView) convertView.findViewById(R.id.bill_dish_count_tv);
            bvh.price = (TextView) convertView.findViewById(R.id.bill_dish_price_tv);
            bvh.date = (TextView) convertView.findViewById(R.id.bill_date_tv);
            bvh.restaurant = (TextView) convertView.findViewById(R.id.bill_restaurant_tv);
            convertView.setTag(bvh);
        } else {
            bvh = (BillViewHolder) convertView.getTag();
        }

        final BillBean billBean = (BillBean) mBillList.get(position);
        bvh.dish.setText(billBean.getName());
        bvh.count.setText("1");
        bvh.price.setText(String.valueOf(billBean.getPrice()));
        bvh.restaurant.setText(billBean.getRestaurantName());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(billBean.getDate());
        bvh.date.setText(strDate);
        
        bvh.bill_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDishFromBill(billBean);
            }
        });
        
        bvh.bill_item_rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final BillBean billBean = (BillBean) mBillList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Confirm delete");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Are you sure to delete the bill?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMyBill(billBean);
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

    public void getDishFromBill(BillBean billBean) {
        String url = "dishes/" + billBean.getDish();
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("get dish by comment", response.toString());
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
    
   public void deleteMyBill(final BillBean billBean) {
	   String url = "deleteMyBill/" + billBean.get_id();
	   SharedPreferences sp = mContext.getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
       String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
       String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
       NourritureRestClient.deleteWithLogin(url, username, password, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               Log.e("delete my bill", response.toString());
               if (statusCode == 204) {
                   mBillList.remove(billBean);
                   notifyDataSetChanged();
               } else {
                   Toast.makeText(mContext, "Deleting bill is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
               }
               mBillList.remove(billBean);
               notifyDataSetChanged();
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               Toast.makeText(mContext, "Deleting bill is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
           }
       });
   }

    @Override
    public Object getItem(int position) {
        return mBillList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mBillList.size();
    }

    static class BillViewHolder {
        TextView dish;
		TextView count;
        TextView price;
        TextView restaurant;
        TextView date;
        LinearLayout bill_item_rl;
    }
}
