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
import team_10.nourriture_android.bean.BillBean;
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
            bvh.name = (TextView) convertView.findViewById(R.id.bill_dish_name_tv);
            bvh.count = (TextView) convertView.findViewById(R.id.bill_dish_count_tv);
            bvh.price = (TextView) convertView.findViewById(R.id.bill_dish_price_tv);
            bvh.time = (TextView) convertView.findViewById(R.id.bill_time_tv);
            convertView.setTag(bvh);
        } else {
            bvh = (billViewHolder) convertView.getTag();
        }

      /*  final BillBean BillBean = (BillBean) mBillList.get(position);
        bvh.name.setText(BillBean.getName());
        bvh.description.setText(BillBean.getDescription());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(BillBean.getDate());
        bvh.time.setText(strDate);

        AsynImageLoader asynImageLoader = new AsynImageLoader();
        if (BillBean.getPicture() == null || "".equals(BillBean.getPicture().trim()) || "null".equals(BillBean.getPicture().trim())) {
            bvh.picture.setImageResource(R.drawable.default_bill_picture);
        } else {
            asynImageLoader.showImageAsyn(bvh.picture, pictureBaseUrl + BillBean.getPicture(), R.drawable.default_bill_picture);
        }*/

        bvh.bill_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DishDetailActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putSerializable("BillBean", BillBean);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public void deleteUserbill(final BillBean BillBean) {
        String url = "billes/" + BillBean.get_id();

        SharedPreferences sp = mContext.getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        NourritureRestClient.deleteWithLogin(url, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("delete bill", response.toString());
                if (statusCode == 204) {
                    mBillList.remove(BillBean);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Deleting bill is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(mContext, "Deleting bill is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
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

    static class billViewHolder {
        TextView name;
        TextView count;
        TextView price;
        TextView time;
        LinearLayout bill_item_rl;
    }
}
