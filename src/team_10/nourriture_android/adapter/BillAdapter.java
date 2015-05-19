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
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/21.
 */
public class BillAdapter extends BaseAdapter {

    public List<BillBean> mBillList = new ArrayList<BillBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private billViewHolder bvh = null;
    private DishBean dishBean;
    
    public BillAdapter(Context context, List<BillBean> billList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mBillList = billList;
    }

    public BillAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bill, null);
            bvh = new billViewHolder();
            bvh.bill_item_rl = (LinearLayout) convertView.findViewById(R.id.bill_item_ll);
            bvh.dish = (TextView) convertView.findViewById(R.id.bill_dish_tv);
            bvh.count = (TextView) convertView.findViewById(R.id.bill_dish_count_tv);
            bvh.price = (TextView) convertView.findViewById(R.id.bill_dish_price_tv);
            bvh.restaurant = (TextView) convertView.findViewById(R.id.bill_restaurant_tv);
            convertView.setTag(bvh);
        } else {
            bvh = (billViewHolder) convertView.getTag();
        }

        final BillBean billBean = (BillBean) mBillList.get(position);
        getMyBillDetail(billBean);
        /*bvh.dish.setText(billBean.getDish_name());
        bvh.count.setText(billBean.getDish_count());
        bvh.price.setText(billBean.getDish_price());
        bvh.restaurant.setText(billBean.getRestaurant_name());*/
        
        bvh.bill_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DishDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dishBean", dishBean);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public void getMyBillDetail(final BillBean bean){
		String url = "dishes/" + bean.getDish();
		NourritureRestClient.get(url, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Log.e("getMyBillDetail", response.toString());
	            if (statusCode == 200) {
	            	try {
						dishBean = (DishBean) JsonTobean.getList(DishBean[].class, response.toString()).get(0);

						bean.setDish_name(dishBean.getName());
						bean.setDish_count(dishBean.getDish_count());
						bean.setDish_price(dishBean.getPrice());
						
						bvh.dish.setText(dishBean.getName());
				        bvh.count.setText("1");
				        bvh.price.setText(String.valueOf(dishBean.getPrice()));
				        bvh.restaurant.setText(dishBean.getRestaurant());
				        
						Log.e("dishBean.getName()", dishBean.getName());
						Log.e("bean.getDish_name()", bean.getDish_name());
						
	            	} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
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

    static class billViewHolder {
        TextView dish;
        TextView count;
        TextView price;
        TextView restaurant;
        LinearLayout bill_item_rl;
    }
}
