package team_10.nourriture_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.DishAdapter;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

public class UserInfoActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String USER_DISHES_DATA_PATH = "_user_dishes_data.bean";
    private List<DishBean> userDishList = new ArrayList<>();
    private SwipeRefreshLayout swipeLayout;
    private ListView dishListView;
    private DishAdapter dishAdapter;
    private boolean isRefresh = false;
    private LinearLayout user_no_dish_ll;
    private Button back_btn, add_dish_btn;
    private TextView tv_name, tv_birth, tv_sex, tv_email, tv_introduction, tv_user_title;
    private ImageView img_photo;
    private String pictureBaseUrl = "http://176.31.191.185:1337/";
    private UserBean userBean;
    private int request = 5;
    private SharedPreferences sp;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        userBean = (UserBean) intent.getSerializableExtra("userBean");

        dishAdapter = new DishAdapter(this, false, true);

        sp = getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        initView();
        initData();
    }
    
    private void initView(){
        tv_user_title = (TextView) findViewById(R.id.user_title_tv);
        img_photo = (ImageView) findViewById(R.id.photo_img);
        tv_name = (TextView) findViewById(R.id.name_tv);
        tv_birth = (TextView) findViewById(R.id.birth_tv);
        tv_sex = (TextView) findViewById(R.id.sex_tv);
        tv_email = (TextView) findViewById(R.id.email_tv);
        tv_introduction = (TextView) findViewById(R.id.introduction_tv);

        user_no_dish_ll = (LinearLayout) findViewById(R.id.ll_user_no_dish);
        add_dish_btn = (Button) findViewById(R.id.btn_add_dish);
        add_dish_btn.setOnClickListener(this);
        back_btn = (Button) findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        dishListView = (ListView) findViewById(R.id.dishListView);
    }

    private void initData(){
        tv_user_title.setText(userBean.getUsername().trim());
        tv_name.setText("Name: " + userBean.getUsername().trim());
        tv_birth.setText("Birth: 1992-11-24");
        if(userBean.getGender()==null || "".equals(userBean.getGender().trim())){
            tv_sex.setText("Sex: Keep Secret.");
        }else{
            tv_sex.setText("Sex: " + userBean.getGender().trim());
        }
        if(userBean.getEmail()==null || "".equals(userBean.getEmail().trim())){
            tv_sex.setText("Email: Keep Secret.");
        }else{
            tv_email.setText("Email: " + userBean.getEmail().trim());
        }
        if(userBean.getIntroduction()==null || "".equals(userBean.getIntroduction().trim())){
            tv_sex.setText("Description: No Introduction.");
        }else{
            tv_introduction.setText("Description: " + userBean.getIntroduction().trim());
        }
        Log.e("UserInfoActivity picture", userBean.getPicture());
        /*AsynImageLoader asynImageLoader = new AsynImageLoader();
        if (userBean.getPicture() == null || "".equals(userBean.getPicture().trim()) || "null".equals(userBean.getPicture().trim())) {
            img_photo.setImageResource(R.drawable.default_avatar);
        } else {
            asynImageLoader.showImageAsyn(img_photo, pictureBaseUrl + userBean.getPicture(), R.drawable.default_avatar);
        }*/
        getUserDishList();
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getUserDishList();
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
            case R.id.btn_add_dish:
                Intent intent = new Intent(UserInfoActivity.this, DishAddActivity.class);
                startActivityForResult(intent, request);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DishAddActivity.KEY_ADD_DISH) {
            getUserDishList();
        }
    }

    public void getUserDishList() {

        NourritureRestClient.getWithLogin("getMyDishes", null, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("user dish", response.toString());
                if (statusCode == 200) {
                    try {
                        userDishList = JsonTobean.getList(DishBean[].class, response.toString());
                        Collections.reverse(userDishList);
                        ObjectPersistence.writeObjectToFile(UserInfoActivity.this, userDishList, USER_DISHES_DATA_PATH);
                        if (userDishList != null && userDishList.size() > 0) {
                            swipeLayout.setVisibility(View.VISIBLE);
                            user_no_dish_ll.setVisibility(View.GONE);
                            if (isRefresh) {
                                if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                                    dishAdapter.mDishList.clear();
                                }
                                dishAdapter.mDishList.addAll(userDishList);
                                isRefresh = false;
                            } else {
                                dishAdapter = new DishAdapter(UserInfoActivity.this, false, true);
                                dishAdapter.mDishList.addAll(userDishList);
                            }
                            dishListView.setAdapter(dishAdapter);
                            dishAdapter.notifyDataSetChanged();
                        } else {
                            user_no_dish_ll.setVisibility(View.VISIBLE);
                            swipeLayout.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalUserDishesData();
                    if (userDishList != null && userDishList.size() > 0) {
                        swipeLayout.setVisibility(View.VISIBLE);
                        user_no_dish_ll.setVisibility(View.GONE);
                        if (isRefresh) {
                            if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                                dishAdapter.mDishList.clear();
                            }
                            dishAdapter.mDishList.addAll(userDishList);
                            isRefresh = false;
                        } else {
                            dishAdapter = new DishAdapter(UserInfoActivity.this, false, true);
                            dishAdapter.mDishList.addAll(userDishList);
                        }
                        dishListView.setAdapter(dishAdapter);
                        dishAdapter.notifyDataSetChanged();
                    } else {
                        //Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                        user_no_dish_ll.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                getLocalUserDishesData();
                if (userDishList != null && userDishList.size() > 0) {
                    if (isRefresh) {
                        if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                            dishAdapter.mDishList.clear();
                        }
                        dishAdapter.mDishList.addAll(userDishList);
                        isRefresh = false;
                    } else {
                        dishAdapter = new DishAdapter(UserInfoActivity.this, false, true);
                        dishAdapter.mDishList.addAll(userDishList);
                    }
                    dishListView.setAdapter(dishAdapter);
                    dishAdapter.notifyDataSetChanged();
                } else {
                    //Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                    user_no_dish_ll.setVisibility(View.VISIBLE);
                    swipeLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getLocalUserDishesData() {
        List<DishBean> localDishList = (List<DishBean>) ObjectPersistence.readObjectFromFile(this, USER_DISHES_DATA_PATH);
        if (localDishList != null) {
            userDishList = localDishList;
        }
    }
}
