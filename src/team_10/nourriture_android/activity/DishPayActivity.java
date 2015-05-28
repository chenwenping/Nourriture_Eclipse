package team_10.nourriture_android.activity;

import org.apache.http.Header;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DishPayActivity extends ActionBarActivity implements OnClickListener{

	private ProgressDialog progress;
	private TextView dish_name_tv, dish_price_tv, dish_count_tv, total_price_tv;
	private LinearLayout dish_pay_success_ll;
	private Button back_btn, pay_btn, bill_tip_btn;
	private DishBean dishBean;
	private UserBean userBean;
	private Boolean isSuccess = false;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dish_pay);
		
		Intent intent = getIntent();
        dishBean = (DishBean) intent.getSerializableExtra("dishBean");
        
        userBean = MyApplication.getInstance().getUserBeanFromFile();
        progress = new ProgressDialog(this);
        sp = getApplicationContext().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        initView();
        initData();
	}
	
	public void initView(){
		dish_pay_success_ll = (LinearLayout)findViewById(R.id.ll_dish_pay_success);
		dish_name_tv = (TextView)findViewById(R.id.tv_dish_pay_name);
		dish_price_tv = (TextView)findViewById(R.id.tv_dish_price);
		dish_count_tv = (TextView)findViewById(R.id.tv_dish_count);
		total_price_tv = (TextView)findViewById(R.id.tv_total_price);
		back_btn = (Button)findViewById(R.id.btn_back);
		pay_btn = (Button)findViewById(R.id.btn_dish_pay);
		bill_tip_btn = (Button)findViewById(R.id.bill_tip_btn);
		back_btn.setOnClickListener(this);
		pay_btn.setOnClickListener(this);
		bill_tip_btn.setOnClickListener(this);
	}

	public void initData(){
		dish_name_tv.setText(String.valueOf(dishBean.getName()));
		dish_price_tv.setText(String.valueOf(dishBean.getPrice()));
		dish_count_tv.setText(String.valueOf(dishBean.getDish_count()));
		total_price_tv.setText(String.valueOf(dishBean.getTotal_price()));
		dish_pay_success_ll.setVisibility(View.GONE);
		pay_btn.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_dish_pay:
	        progress.setMessage("Dealing...");
	        progress.show();
	        payDish();
			break;
		case R.id.bill_tip_btn:
			 Intent intent = new Intent(DishPayActivity.this, BillActivity.class);
             startActivity(intent);
			break;
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}
	
	public void payDish(){
		RequestParams params = new RequestParams();
        params.add("restaurant_id", dishBean.getRestaurant());
        params.add("dish_id", dishBean.get_id());
        params.add("dish_name", dishBean.getName());
        params.add("dish_price", String.valueOf(dishBean.getPrice()));
        params.add("restaurant_name", dishBean.getRestaurant());
        
		String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
	    String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

	    NourritureRestClient.postWithLogin("payDish", params, username, password, new JsonHttpResponseHandler(){
	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
	    		Log.e("payDish", response.toString());
	    		isSuccess = true;
	    		if (progress.isShowing()) {
                    progress.dismiss();
                }
	    		pay_btn.setVisibility(View.GONE);
		        dish_pay_success_ll.setVisibility(View.VISIBLE);
		        
	    	}
	    	
	    	@Override
	    	public void onFailure(int statusCode, Header[] headers,
	    			Throwable throwable, JSONObject errorResponse) {
	    		// TODO Auto-generated method stub
	    		//super.onFailure(statusCode, headers, throwable, errorResponse);
	    		
	    		Log.e("errorResponse", errorResponse.toString());
	    	}
	    });
	    
		if(isSuccess){
			pay_btn.setVisibility(View.GONE);
	        dish_pay_success_ll.setVisibility(View.VISIBLE);
		}else{
			pay_btn.setVisibility(View.VISIBLE);
	        dish_pay_success_ll.setVisibility(View.GONE);
		}
	}
		
}
