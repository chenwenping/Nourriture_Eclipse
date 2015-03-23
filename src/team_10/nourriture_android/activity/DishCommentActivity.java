package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.CommentAdapter;
import team_10.nourriture_android.bean.CommentBean;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/21.
 */
public class DishCommentActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String DISH_COMMENTS_DATA_PATH = "_dish_comments_data.bean";
    private DishBean dishBean;
    private List<CommentBean> commentList;
    private CommentBean commentAddBean;
    private Context mContext;
    private CommentAdapter commentAdapter;
    private SwipeRefreshLayout swipeLayout;
    private ListView commentListView;
    private TextView no_comment_tv;
    private boolean isRefresh = false;
    private EditText dish_comment_et;
    private Button dish_comment_btn;
    private String dish_comment; // send comment content
    private Button back_btn;
    private ProgressDialog progress;
    private boolean isLogin = false;
    private SharedPreferences sp;
    private int request = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_comment);

        mContext = this;
        Intent intent = getIntent();
        dishBean = (DishBean) intent.getSerializableExtra("dishBean");

        sp = getApplicationContext().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);

        initView();

        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.show();

        getAllComments();
    }

    public void initView() {
        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        commentListView = (ListView) this.findViewById(R.id.commentListView);
        no_comment_tv = (TextView) this.findViewById(R.id.tv_no_dish_comment);
        dish_comment_et = (EditText) this.findViewById(R.id.et_dish_comment);
        dish_comment_btn = (Button) this.findViewById(R.id.btn_dish_comment);
        back_btn = (Button) this.findViewById(R.id.btn_back);
        dish_comment_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dish_comment:
                if (isLogin) {
                    dish_comment = dish_comment_et.getText().toString().trim();
                    if (dish_comment == null || "".equals(dish_comment)) {
                        Toast.makeText(DishCommentActivity.this, "Please enter the comment content.", Toast.LENGTH_SHORT).show();
                    } else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(dish_comment_et.getWindowToken(), 0); // 隐藏软键盘
                        progress.setMessage("Comment...");
                        progress.show();
                        addComment(dish_comment);
                    }
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, request);
                }
                break;
            case R.id.btn_back:
                finish();
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
        }
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getAllComments();
                }
            }, 3000);
        }
    }

    public void getAllComments() {
        String url = "getCommentsFromDish/" + dishBean.get_id();
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("commentList", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 200) {
                    try {
                        commentList = JsonTobean.getList(CommentBean[].class, response.toString());
                        Collections.reverse(commentList);
                        ObjectPersistence.writeObjectToFile(mContext, commentList, dishBean.get_id() + DISH_COMMENTS_DATA_PATH);
                        if (commentList == null || commentList.size() == 0) {
                            no_comment_tv.setVisibility(View.VISIBLE);
                            swipeLayout.setVisibility(View.GONE);
                        } else {
                            no_comment_tv.setVisibility(View.GONE);
                            swipeLayout.setVisibility(View.VISIBLE);
                            if (isRefresh) {
                                if (commentAdapter.mCommentList != null && commentAdapter.mCommentList.size() > 0) {
                                    commentAdapter.mCommentList.clear();
                                }
                                commentAdapter.mCommentList.addAll(commentList);
                                isRefresh = false;
                            } else {
                                commentAdapter = new CommentAdapter(mContext, false);
                                commentAdapter.mCommentList.addAll(commentList);
                            }
                            commentListView.setAdapter(commentAdapter);
                            commentAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    getLocalCommentsData();
                    if (commentList == null || commentList.size() == 0) {
                        no_comment_tv.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.GONE);
                    } else {
                        no_comment_tv.setVisibility(View.GONE);
                        swipeLayout.setVisibility(View.VISIBLE);
                        if (isRefresh) {
                            if (commentAdapter.mCommentList != null && commentAdapter.mCommentList.size() > 0) {
                                commentAdapter.mCommentList.clear();
                            }
                            commentAdapter.mCommentList.addAll(commentList);
                            isRefresh = false;
                        } else {
                            commentAdapter = new CommentAdapter(mContext, false);
                            commentAdapter.mCommentList.addAll(commentList);
                        }
                        commentListView.setAdapter(commentAdapter);
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                getLocalCommentsData();
                if (commentList == null || commentList.size() == 0) {
                    no_comment_tv.setVisibility(View.VISIBLE);
                    swipeLayout.setVisibility(View.GONE);
                } else {
                    no_comment_tv.setVisibility(View.GONE);
                    swipeLayout.setVisibility(View.VISIBLE);
                    if (isRefresh) {
                        if (commentAdapter.mCommentList != null && commentAdapter.mCommentList.size() > 0) {
                            commentAdapter.mCommentList.clear();
                        }
                        commentAdapter.mCommentList.addAll(commentList);
                        isRefresh = false;
                    } else {
                        commentAdapter = new CommentAdapter(mContext, false);
                        commentAdapter.mCommentList.addAll(commentList);
                    }
                    commentListView.setAdapter(commentAdapter);
                    commentAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void addComment(String dish_comment) {
        RequestParams params = new RequestParams();
        params.add("dish", dishBean.get_id());
        params.add("content", dish_comment);

        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        NourritureRestClient.postWithLogin("comments", params, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("addComment", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 201) {
                    try {
                        commentAddBean = JsonTobean.getBean(CommentBean.class, response.toString());
                        dish_comment_et.setText("");
                        getAllComments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Adding comment is wrong. Please try it.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Adding comment is wrong. Please try it.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocalCommentsData() {
        List<CommentBean> localCommentList = (List<CommentBean>) ObjectPersistence.readObjectFromFile(mContext, dishBean.get_id() + DISH_COMMENTS_DATA_PATH);
        if (localCommentList != null) {
            commentList = localCommentList;
        }
    }
}
