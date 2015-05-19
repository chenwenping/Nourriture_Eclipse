package team_10.nourriture_android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.DishDetailActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.bean.BillBean;
import team_10.nourriture_android.bean.CommentBean;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;

/**
 * Created by ping on 2014/12/27.
 */
public class BillAdapter2 extends BaseAdapter {

    public List<BillBean> mBillList = new ArrayList<BillBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;
    private DishBean dishBean;

    public BillAdapter2(Context context, List<BillBean> billList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mBillList = billList;
    }

    public BillAdapter2(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BillViewHolder bvh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bill2, null);
            bvh = new BillViewHolder();
            bvh.dish = (TextView) convertView.findViewById(R.id.tv_dish);
            bvh.from = (TextView) convertView.findViewById(R.id.tv_dish_from);
            bvh.to = (TextView) convertView.findViewById(R.id.tv_dish_to);
            bvh.bill_item_ll = (LinearLayout) convertView.findViewById(R.id.bill_item_ll);
            convertView.setTag(bvh);
        } else {
            bvh = (BillViewHolder) convertView.getTag();
        }

        final BillBean billBean = (BillBean) mBillList.get(position);
        bvh.dish.setText("Dish: " + billBean.getDish());
        bvh.from.setText("From: " + billBean.getFrom());
        bvh.to.setText("To: " + billBean.getTo());
        
        bvh.bill_item_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDishFromBill(billBean);
            }
        });

        bvh.bill_item_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final BillBean bill = (BillBean) mBillList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Confirm delete");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Are you sure to delete the bill?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUserBill(bill);
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
    

    public void deleteUserBill(final BillBean billBean) {
        /*String url = "bill/" + billBean.get_id();
        NourritureRestClient.delete(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("delete user comment", response.toString());
                if (statusCode == 204) {
                    mBillList.remove(billBean);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Deleting bill is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(mContext, "Deleting bill is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
            }
        });*/
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
        TextView from;
        TextView to;
        LinearLayout bill_item_ll;
    }
}
