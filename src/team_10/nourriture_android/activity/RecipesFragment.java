package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

public class RecipesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String USER_DISHES_DATA_PATH = "_user_dishes_data.bean";
    private List<DishBean> userDishList = new ArrayList<>();
    private SwipeRefreshLayout swipeLayout;
    private ListView dishListView;
    private DishAdapter dishAdapter;
    private ProgressDialog progress;
    private boolean isRefresh = false;
    private LinearLayout user_dish_login_ll, user_no_dish_ll;
    private Button login_btn, add_dish_btn;
    private Context mContext;
    private boolean isLogin = false;
    private String username;
    private String password;
    private int request = 4;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        dishAdapter = new DishAdapter(mContext, false, true);

        user_no_dish_ll = (LinearLayout) getActivity().findViewById(R.id.ll_user_no_dish);
        add_dish_btn = (Button) getActivity().findViewById(R.id.btn_add_dish);
        add_dish_btn.setOnClickListener(this);
        user_dish_login_ll = (LinearLayout) getActivity().findViewById(R.id.ll_user_dish_login);
        login_btn = (Button) getActivity().findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        swipeLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        dishListView = (ListView) getActivity().findViewById(R.id.dishListView);

        if (isLogin) {
            username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
            password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
            swipeLayout.setVisibility(View.VISIBLE);
            user_dish_login_ll.setVisibility(View.GONE);
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Loading...");
            progress.show();
            getUserDishList();
        } else {
            user_dish_login_ll.setVisibility(View.VISIBLE);
            swipeLayout.setVisibility(View.GONE);
        }
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
            case R.id.login_btn:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, request);
                break;
            case R.id.btn_add_dish:
                Intent intent1 = new Intent(getActivity(), DishAddActivity.class);
                startActivityForResult(intent1, request);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.KEY_IS_LOGIN) {
            isLogin = true;
            username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
            password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
            swipeLayout.setVisibility(View.VISIBLE);
            user_dish_login_ll.setVisibility(View.GONE);
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Loading...");
            progress.show();
            getUserDishList();
        } else if (resultCode == DishAddActivity.KEY_ADD_DISH) {
            getUserDishList();
        }
    }

    public void getUserDishList() {

        NourritureRestClient.getWithLogin("getMyDishes", null, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("user dish", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 200) {
                    try {
                        userDishList = JsonTobean.getList(DishBean[].class, response.toString());
                        Collections.reverse(userDishList);
                        ObjectPersistence.writeObjectToFile(mContext, userDishList, USER_DISHES_DATA_PATH);
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
                                dishAdapter = new DishAdapter(mContext, false, true);
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
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
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
                            dishAdapter = new DishAdapter(mContext, false, true);
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
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                getLocalUserDishesData();
                if (userDishList != null && userDishList.size() > 0) {
                    if (isRefresh) {
                        if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                            dishAdapter.mDishList.clear();
                        }
                        dishAdapter.mDishList.addAll(userDishList);
                        isRefresh = false;
                    } else {
                        dishAdapter = new DishAdapter(mContext, false, true);
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
        List<DishBean> localDishList = (List<DishBean>) ObjectPersistence.readObjectFromFile(mContext, USER_DISHES_DATA_PATH);
        if (localDishList != null) {
            userDishList = localDishList;
        }
    }
}