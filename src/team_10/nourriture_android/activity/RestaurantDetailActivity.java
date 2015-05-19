package team_10.nourriture_android.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.DishAdapter;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.bean.RestaurantBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

public class RestaurantDetailActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

	private Button back_btn, location_btn;
	private TextView name_tv, date_tv, introduction_tv;
	private static final String RESTAURANT_DISHES_DATA_PATH = "_restaurant_dishes_data.bean";
	private List<DishBean> restaurantDishList = new ArrayList<>();
	private SwipeRefreshLayout swipeLayout;
	private ListView dishListView;
	private DishAdapter dishAdapter;
	private boolean isRefresh = false;
	private LinearLayout restaurant_no_dish_ll;
	private ImageView restaurant_img;
	private String pictureBaseUrl = "http://176.31.191.185:1337/";
	private RestaurantBean restaurantBean;
	private int request = 5;
	private SharedPreferences sp;
	private String username;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_detail);

		Intent intent = getIntent();
		restaurantBean = (RestaurantBean) intent
				.getSerializableExtra("RestaurantBean");

		dishAdapter = new DishAdapter(this, false, true);

		sp = getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES,
				Context.MODE_PRIVATE);
		username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
		password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

		initView();
		initData();
	}

	public void initView() {
		restaurant_img = (ImageView) findViewById(R.id.restaurant_img);
		name_tv = (TextView) findViewById(R.id.name_tv);
		date_tv = (TextView) findViewById(R.id.date_tv);
		introduction_tv = (TextView) findViewById(R.id.introduction_tv);
		restaurant_no_dish_ll = (LinearLayout) findViewById(R.id.ll_restaurant_no_dish);
		back_btn = (Button) findViewById(R.id.btn_back);
		back_btn.setOnClickListener(this);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeLayout.setOnRefreshListener(this);
		// 加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
		swipeLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		dishListView = (ListView) findViewById(R.id.dishListView);
		
		location_btn = (Button) findViewById(R.id.btn_location);
		location_btn.setOnClickListener(this);
	}

	public void initData() {
		name_tv.setText(restaurantBean.getRestaurantName().trim());
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(restaurantBean.getRegistrationDate());
        date_tv.setText(strDate);
        
        if(restaurantBean.getIntroduction()==null || "".equals(restaurantBean.getIntroduction().trim())){
        	introduction_tv.setText("No Introduction.");
        }else{
            introduction_tv.setText(restaurantBean.getIntroduction().trim());
        }
        Log.e("RestaurantDetailActivity picture", restaurantBean.getPicture());
        AsynImageLoader asynImageLoader = new AsynImageLoader();
        if (restaurantBean.getPicture() == null || "".equals(restaurantBean.getPicture().trim()) || "null".equals(restaurantBean.getPicture().trim())) {
        	restaurant_img.setImageResource(R.drawable.default_restaurant_picture);
        } else {
            asynImageLoader.showImageAsyn(restaurant_img, pictureBaseUrl + restaurantBean.getPicture(), R.drawable.default_restaurant_picture);
        }
        
        getRestaurantDishList();
    }
	

	@Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getRestaurantDishList();
                }
            }, 3000);
        }
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_location:
			Intent intent = new Intent(RestaurantDetailActivity.this, LocationActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	public void getRestaurantDishList(){
		String url = "getRestaurantDishes/" + restaurantBean.get_id();
		NourritureRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("restaurant dish", response.toString());
                if (statusCode == 200) {
                    try {
                        restaurantDishList = JsonTobean.getList(DishBean[].class, response.toString());
                        Collections.reverse(restaurantDishList);
                        ObjectPersistence.writeObjectToFile(RestaurantDetailActivity.this, restaurantDishList, RESTAURANT_DISHES_DATA_PATH);
                        if (restaurantDishList != null && restaurantDishList.size() > 0) {
                            swipeLayout.setVisibility(View.VISIBLE);
                            restaurant_no_dish_ll.setVisibility(View.GONE);
                            if (isRefresh) {
                                if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                                    dishAdapter.mDishList.clear();
                                }
                                dishAdapter.mDishList.addAll(restaurantDishList);
                                isRefresh = false;
                            } else {
                                dishAdapter = new DishAdapter(RestaurantDetailActivity.this, false, true);
                                dishAdapter.mDishList.addAll(restaurantDishList);
                            }
                            dishListView.setAdapter(dishAdapter);
                            dishAdapter.notifyDataSetChanged();
                        } else {
                            restaurant_no_dish_ll.setVisibility(View.VISIBLE);
                            swipeLayout.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalRestaurantDishesData();
                    if (restaurantDishList != null && restaurantDishList.size() > 0) {
                        swipeLayout.setVisibility(View.VISIBLE);
                        restaurant_no_dish_ll.setVisibility(View.GONE);
                        if (isRefresh) {
                            if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                                dishAdapter.mDishList.clear();
                            }
                            dishAdapter.mDishList.addAll(restaurantDishList);
                            isRefresh = false;
                        } else {
                            dishAdapter = new DishAdapter(RestaurantDetailActivity.this, false, true);
                            dishAdapter.mDishList.addAll(restaurantDishList);
                        }
                        dishListView.setAdapter(dishAdapter);
                        dishAdapter.notifyDataSetChanged();
                    } else {
                        //Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                        restaurant_no_dish_ll.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                getLocalRestaurantDishesData();
                if (restaurantDishList != null && restaurantDishList.size() > 0) {
                    if (isRefresh) {
                        if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                            dishAdapter.mDishList.clear();
                        }
                        dishAdapter.mDishList.addAll(restaurantDishList);
                        isRefresh = false;
                    } else {
                        dishAdapter = new DishAdapter(RestaurantDetailActivity.this, false, true);
                        dishAdapter.mDishList.addAll(restaurantDishList);
                    }
                    dishListView.setAdapter(dishAdapter);
                    dishAdapter.notifyDataSetChanged();
                } else {
                    //Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                    restaurant_no_dish_ll.setVisibility(View.VISIBLE);
                    swipeLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getLocalRestaurantDishesData() {
        List<DishBean> localDishList = (List<DishBean>) ObjectPersistence.readObjectFromFile(this, RESTAURANT_DISHES_DATA_PATH);
        if (localDishList != null) {
            restaurantDishList = localDishList;
        }
    }
}
