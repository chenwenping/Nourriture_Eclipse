package team_10.nourriture_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.CommentBean;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.bean.LikeBean;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/21.
 */
public class DishDetailActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String DISH_LIKES_DATA_PATH = "_dish_likes_data.bean";
    private static final String DISH_COMMENTS_DATA_PATH = "_dish_comments_data.bean";
    private DishBean dishBean;
    private AsynImageLoader asynImageLoader;
    private Button back_btn;
    private ImageView dish_picture_img, dish_favor_img;
    private LinearLayout dish_favor_ll, dish_comment_ll;
    private TextView dish_name_tv, dish_description_tv, dish_ingredient_tv, dish_step_tv;
    private TextView favor_num_tv, comment_num_tv;
    private List<LikeBean> likeList;
    private List<CommentBean> commentList;
//    private String pictureBaseUrl = "http://5.196.19.84:1337/";
    private String pictureBaseUrl = "http://176.31.191.185:1337/";
    private SharedPreferences sp;
    private boolean isLogin = false;
    private boolean isLike = false;
    private int favor_num = 0;
    private UserBean userBean;
    private LikeBean likeBean;
    private int request = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        Intent intent = getIntent();
        dishBean = (DishBean) intent.getSerializableExtra("dishBean");

        sp = getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
        userBean = MyApplication.getInstance().getUserBeanFromFile();

        initView();
        initData();
    }

    public void initView() {
        dish_name_tv = (TextView) this.findViewById(R.id.tv_dish_name);
        dish_description_tv = (TextView) this.findViewById(R.id.tv_dish_description);
        dish_ingredient_tv = (TextView) this.findViewById(R.id.tv_dish_ingredient);
        dish_step_tv = (TextView) this.findViewById(R.id.tv_dish_step);
        dish_picture_img = (ImageView) this.findViewById(R.id.img_dish_picture);
        dish_favor_img = (ImageView) this.findViewById(R.id.img_dish_favor);
        dish_favor_ll = (LinearLayout) this.findViewById(R.id.ll_dish_favor);
        dish_comment_ll = (LinearLayout) this.findViewById(R.id.ll_dish_comment);
        favor_num_tv = (TextView) this.findViewById(R.id.tv_favor_num);
        comment_num_tv = (TextView) this.findViewById(R.id.tv_comment_num);
        back_btn = (Button) this.findViewById(R.id.btn_back);

        back_btn.setOnClickListener(this);
        dish_favor_ll.setOnClickListener(this);
        dish_comment_ll.setOnClickListener(this);
    }

    public void initData() {
        dish_name_tv.setText(dishBean.getName());
        dish_description_tv.setText(dishBean.getDescription());

        asynImageLoader = new AsynImageLoader();
        if (dishBean.getPicture() == null || "".equals(dishBean.getPicture().trim()) || "null".equals(dishBean.getPicture().trim())) {
            dish_picture_img.setImageResource(R.drawable.default_dish_picture);
        } else {
            asynImageLoader.showImageAsyn(dish_picture_img, pictureBaseUrl + dishBean.getPicture(), R.drawable.default_dish_picture);
        }

        getLikesFromDish();
    }

    public void getLikesFromDish() {
        String url = "getLikesFromDish/" + dishBean.get_id();
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("getLikesFromDish", response.toString());
                if (statusCode == 200) {
                    try {
                        likeList = JsonTobean.getList(LikeBean[].class, response.toString());
                        ObjectPersistence.writeObjectToFile(DishDetailActivity.this, likeList, dishBean.get_id() + DISH_LIKES_DATA_PATH);
                        if (likeList != null && likeList.size() > 0) {
                            favor_num = likeList.size();
                            favor_num_tv.setText(favor_num + " Favor");
                            for (int i = 0; i < likeList.size(); i++) {
                                if ((likeList.get(i).getUser()).equals(userBean.get_id())) {
                                    isLike = true;
                                    dish_favor_img.setImageResource(R.drawable.favor_btn_highlight);
                                    likeBean = likeList.get(i);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalLikesData();
                    if (likeList != null && likeList.size() > 0) {
                        favor_num = likeList.size();
                        favor_num_tv.setText(favor_num + " Favor");
                        for (int i = 0; i < likeList.size(); i++) {
                            if ((likeList.get(i).getUser()).equals(userBean.get_id())) {
                                isLike = true;
                                dish_favor_img.setImageResource(R.drawable.favor_btn_highlight);
                                likeBean = likeList.get(i);
                            } else {
                                isLike = false;
                                dish_favor_img.setImageResource(R.drawable.favor_btn_default);
                            }
                        }
                    } else {
                        Toast.makeText(DishDetailActivity.this, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                getLocalLikesData();
                if (likeList != null && likeList.size() > 0) {
                    favor_num = likeList.size();
                    favor_num_tv.setText(favor_num + " Favor");
                    for (int i = 0; i < likeList.size(); i++) {
                        if ((likeList.get(i).getUser()).equals(userBean.get_id())) {
                            isLike = true;
                            dish_favor_img.setImageResource(R.drawable.favor_btn_highlight);
                            likeBean = likeList.get(i);
                        } else {
                            isLike = false;
                            dish_favor_img.setImageResource(R.drawable.favor_btn_default);
                        }
                    }
                } else {
                    Toast.makeText(DishDetailActivity.this, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocalLikesData() {
        List<LikeBean> localLikeList = (List<LikeBean>) ObjectPersistence.readObjectFromFile(DishDetailActivity.this, dishBean.get_id() + DISH_LIKES_DATA_PATH);
        if (localLikeList != null) {
            likeList = localLikeList;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dish_favor:
                if (isLogin) {
                    if (isLike) {
                        deleteLike();
                        isLike = false;
                        favor_num = favor_num - 1;
                        if (favor_num > 0) {
                            favor_num_tv.setText(favor_num + " Favor");
                        } else {
                            favor_num_tv.setText(" Favor");
                        }
                        dish_favor_img.setImageResource(R.drawable.favor_btn_default);
                        Toast.makeText(this, "don't like it.", Toast.LENGTH_SHORT).show();
                    } else {
                        postLike();
                        isLike = true;
                        favor_num = favor_num + 1;
                        favor_num_tv.setText(favor_num + " Favor");
                        dish_favor_img.setImageResource(R.drawable.favor_btn_highlight);
                        Toast.makeText(this, "like it.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(DishDetailActivity.this, LoginActivity.class);
                    startActivityForResult(intent, request);
                }
                break;
            case R.id.ll_dish_comment:
                Intent intent = new Intent(DishDetailActivity.this, DishCommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dishBean", dishBean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    public void postLike() {
        RequestParams params = new RequestParams();
        params.add("dish", dishBean.get_id());
        params.add("like", String.valueOf(isLike));

        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        NourritureRestClient.postWithLogin("likes", params, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("post like", response.toString());
                if (statusCode == 201) {
                    try {
                        likeBean = JsonTobean.getBean(LikeBean.class, response.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //Toast.makeText(getApplicationContext(), "like it is wrong. Please try it.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteLike() {
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        String url = "likes/" + likeBean.get_id();
        NourritureRestClient.deleteWithLogin(url, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("delete like", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.KEY_IS_LOGIN) {
            isLogin = true;
        }
    }
}
