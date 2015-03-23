package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.LikeAdapter;
import team_10.nourriture_android.bean.LikeBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.ObjectPersistence;

/**
 * Created by ping on 2014/12/28.
 */
public class FavorDishesActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String FAVOR_DISHES_DATA_PATH = "_favor_dishes_data.bean";
    private List<LikeBean> favorList;
    private SwipeRefreshLayout swipeLayout;
    private ListView favorListView;
    private LikeAdapter likeAdapter;
    private boolean isRefresh = false;
    private LinearLayout no_favor_dish_ll;
    private Button back_btn;
    private ProgressDialog progress;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_dishes);

        initView();

        mContext = this;
        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.show();
        getFavorDishes();
    }

    public void initView() {
        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        favorListView = (ListView) this.findViewById(R.id.favorListView);
        no_favor_dish_ll = (LinearLayout) this.findViewById(R.id.ll_no_favor_dish);
        back_btn = (Button) this.findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
    }

    public void getFavorDishes() {
        NourritureRestClient.get("likes", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("favor dishes", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 200) {
                    try {
                        favorList = JsonTobean.getList(LikeBean[].class, response.toString());
                        Collections.reverse(favorList);
                        ObjectPersistence.writeObjectToFile(mContext, favorList, FAVOR_DISHES_DATA_PATH);
                        if (favorList != null && favorList.size() > 0) {
                            swipeLayout.setVisibility(View.VISIBLE);
                            no_favor_dish_ll.setVisibility(View.GONE);
                            if (isRefresh) {
                                if (likeAdapter.mLikeList != null && likeAdapter.mLikeList.size() > 0) {
                                    likeAdapter.mLikeList.clear();
                                }
                                likeAdapter.mLikeList.addAll(favorList);
                                isRefresh = false;
                            } else {
                                likeAdapter = new LikeAdapter(mContext, false);
                                likeAdapter.mLikeList.addAll(favorList);
                            }
                            favorListView.setAdapter(likeAdapter);
                            likeAdapter.notifyDataSetChanged();
                        } else {
                            swipeLayout.setVisibility(View.GONE);
                            no_favor_dish_ll.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    getLocalFavorDishData();
                    if (favorList != null && favorList.size() > 0) {
                        swipeLayout.setVisibility(View.VISIBLE);
                        no_favor_dish_ll.setVisibility(View.GONE);
                        if (isRefresh) {
                            if (likeAdapter.mLikeList != null && likeAdapter.mLikeList.size() > 0) {
                                likeAdapter.mLikeList.clear();
                            }
                            likeAdapter.mLikeList.addAll(favorList);
                            isRefresh = false;
                        } else {
                            likeAdapter = new LikeAdapter(mContext, false);
                            likeAdapter.mLikeList.addAll(favorList);
                        }
                        favorListView.setAdapter(likeAdapter);
                        likeAdapter.notifyDataSetChanged();
                    } else {
                        swipeLayout.setVisibility(View.GONE);
                        no_favor_dish_ll.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                getLocalFavorDishData();
                if (favorList != null && favorList.size() > 0) {
                    swipeLayout.setVisibility(View.VISIBLE);
                    no_favor_dish_ll.setVisibility(View.GONE);
                    if (isRefresh) {
                        if (likeAdapter.mLikeList != null && likeAdapter.mLikeList.size() > 0) {
                            likeAdapter.mLikeList.clear();
                        }
                        likeAdapter.mLikeList.addAll(favorList);
                        isRefresh = false;
                    } else {
                        likeAdapter = new LikeAdapter(mContext, false);
                        likeAdapter.mLikeList.addAll(favorList);
                    }
                    favorListView.setAdapter(likeAdapter);
                    likeAdapter.notifyDataSetChanged();
                } else {
                    swipeLayout.setVisibility(View.GONE);
                    no_favor_dish_ll.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getFavorDishes();
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
            default:
                break;
        }
    }

    public void getLocalFavorDishData() {
        List<LikeBean> localFavorList = (List<LikeBean>) ObjectPersistence.readObjectFromFile(mContext, FAVOR_DISHES_DATA_PATH);
        if (localFavorList != null) {
            favorList = localFavorList;
        }
    }
}
