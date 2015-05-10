package team_10.nourriture_android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.FriendAdapter;
import team_10.nourriture_android.bean.FriendBean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/28.
 */
public class FriendsActivity extends ActionBarActivity  implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

	private static final String FRIEND_DATA_PATH = "_friends_data.bean";
    private List<FriendBean> friendList;
    private SwipeRefreshLayout swipeLayout;
    private ListView friendListView;
    private FriendAdapter friendAdapter;
    private ProgressDialog progress;
    private boolean isRefresh = false;
    private EditText search_et;
    private Button search_btn;
    private String search_content;
    private List<FriendBean> searchFriendList;
    private boolean isSearch = false;
    private boolean beforeChangeHaveText = true;
    private boolean afterChangeHaveText = true;
    private int searchIconDefault; // default search icon
    private int searchIconClear; // clear search text icon
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        
       /* searchIconDefault = R.drawable.search_icon;
        searchIconClear = R.drawable.search_delete;
        search_btn = (Button) findViewById(R.id.btn_search);
        search_et = (EditText) findViewById(R.id.et_search_text);
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (search_et.getText() != null) {
                    beforeChangeHaveText = true;
                } else {
                    beforeChangeHaveText = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_et.getText().toString().trim() != null && !"".equals(search_et.getText().toString().trim())) {
                    afterChangeHaveText = true;
                } else {
                    isSearch = true;
                    search_content = "";
                    getsearchFriendList();
                    afterChangeHaveText = false;
                }
                if (beforeChangeHaveText && afterChangeHaveText) {
                    isSearch = true;
                    search_btn.setBackgroundResource(searchIconDefault);
                }
            }
        });
        search_btn.setOnClickListener(this);*/

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        friendListView = (ListView) findViewById(R.id.friendListView);
        
        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.show();
        sp = getApplicationContext().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        
        getFriends();
    }

    public void getsearchFriendList() {
        /*searchFriendList = new ArrayList<>();
        if (friendList != null && friendList.size() > 0) {
            for (int i = 0; i < friendList.size(); i++) {
                FriendBean friendBean = friendList.get(i);
                if (friendBean.getName().contains(search_content) || search_content.contains(friendBean.getName())) {
                    searchFriendList.add(friendBean);
                }
            }
            if (searchFriendList != null && searchFriendList.size() > 0) {
                if (friendAdapter.mDishList != null && friendAdapter.mDishList.size() > 0) {
                    friendAdapter.mDishList.clear();
                }
                friendAdapter.mDishList.addAll(searchFriendList);
                friendListView.setAdapter(friendAdapter);
                friendAdapter.notifyDataSetChanged();
            } else {
                // search result no dish.
                search_et.setText("");
                search_et.requestFocus();
                search_btn.setBackgroundResource(searchIconDefault);
                search_content = "";
                Toast.makeText(this, "No match result. Please try again.", Toast.LENGTH_LONG).show();
            }
        }*/
    }
    
    public void getFriends() {
        String userName = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");

        NourritureRestClient.getWithLogin("getMyFriend", null, userName, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("getMyFriend", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("getMyFriend", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("statusCode", String.valueOf(statusCode));
                Log.e("responseString", responseString);
                Log.e("throwable", throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
        case R.id.btn_back:
            finish();
            break;
        case R.id.btn_search:
            if (isSearch) {
                search_content = search_et.getText().toString().trim();
                if (search_content == null || "".equals(search_content)) {
                    Toast.makeText(this, "Please enter the search content.", Toast.LENGTH_SHORT).show();
                } else {
                    isSearch = false;
                    search_content = search_content.replaceAll(" ", "");
                    getsearchFriendList();
                    search_btn.setBackgroundResource(searchIconClear);
                }
            } else {
                search_et.setText("");
                isSearch = true;
                search_btn.setBackgroundResource(searchIconDefault);
                search_content = "";
                getsearchFriendList();
            }
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
            break;
        default:
            break;
    }
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getFriends();
                }
            }, 3000);
        }
	}
}
