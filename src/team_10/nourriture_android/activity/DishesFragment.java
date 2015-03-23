package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class DishesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String DISHES_DATA_PATH = "_dishes_data.bean";
    private List<DishBean> dishesList;
    private SwipeRefreshLayout swipeLayout;
    private ListView dishListView;
    private DishAdapter dishAdapter;
    private ProgressDialog progress;
    private boolean isRefresh = false;
    private Button addDish_btn;
    private EditText search_et;
    private Button search_btn;
    private String search_content;
    private List<DishBean> searchDishList;
    private boolean isSearch = false;
    private boolean beforeChangeHaveText = true;
    private boolean afterChangeHaveText = true;
    private int searchIconDefault; // default search icon
    private int searchIconClear; // clear search text icon
    private Context mContext;
    private boolean isLogin = false;
    private int request = 1;
    private DishBean dishAddBean; // the dish added by user
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dishes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        addDish_btn = (Button) getActivity().findViewById(R.id.btn_add_dish);
        searchIconDefault = R.drawable.search_icon;
        searchIconClear = R.drawable.search_delete;
        search_btn = (Button) getActivity().findViewById(R.id.btn_search);
        search_et = (EditText) getActivity().findViewById(R.id.et_search_text);
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
                    getSearchDishList();
                    afterChangeHaveText = false;
                }
                if (beforeChangeHaveText && afterChangeHaveText) {
                    isSearch = true;
                    search_btn.setBackgroundResource(searchIconDefault);
                }
            }
        });
        addDish_btn.setOnClickListener(this);
        search_btn.setOnClickListener(this);

        swipeLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        dishListView = (ListView) getActivity().findViewById(R.id.dishListView);

        sp = getActivity().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading...");
        progress.show();

        getAllDishes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_dish:
                //isLogin = MyApplication.getInstance().isLogin();
                isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
                Log.e("isLogin", String.valueOf(isLogin));
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), DishAddActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, request);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, request);
                }
                break;
            case R.id.btn_search:
                if (isSearch) {
                    search_content = search_et.getText().toString().trim();
                    if (search_content == null || "".equals(search_content)) {
                        Toast.makeText(getActivity(), "Please enter the search content.", Toast.LENGTH_SHORT).show();
                    } else {
                        isSearch = false;
                        search_content = search_content.replaceAll(" ", "");
                        getSearchDishList();
                        search_btn.setBackgroundResource(searchIconClear);
                    }
                } else {
                    search_et.setText("");
                    isSearch = true;
                    search_btn.setBackgroundResource(searchIconDefault);
                    search_content = "";
                    getSearchDishList();
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }

    public void getSearchDishList() {
        searchDishList = new ArrayList<>();
        if (dishesList != null && dishesList.size() > 0) {
            for (int i = 0; i < dishesList.size(); i++) {
                DishBean dishBean = dishesList.get(i);
                if (dishBean.getName().contains(search_content) || search_content.contains(dishBean.getName())) {
                    searchDishList.add(dishBean);
                }
            }
            if (searchDishList != null && searchDishList.size() > 0) {
                if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                    dishAdapter.mDishList.clear();
                }
                dishAdapter.mDishList.addAll(searchDishList);
                dishListView.setAdapter(dishAdapter);
                dishAdapter.notifyDataSetChanged();
            } else {
                // search result no dish.
                search_et.setText("");
                search_et.requestFocus();
                search_btn.setBackgroundResource(searchIconDefault);
                search_content = "";
                Toast.makeText(getActivity(), "No match result. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.KEY_IS_LOGIN) {
            isLogin = true;
        } else if (resultCode == DishAddActivity.KEY_ADD_DISH) {
            isRefresh = true;
            getAllDishes();
        }
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getAllDishes();
                }
            }, 3000);
        }
    }

    public void getAllDishes() {
        NourritureRestClient.get("dishes", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("dishes", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 200) {
                    try {
                        dishesList = JsonTobean.getList(DishBean[].class, response.toString());
                        Collections.reverse(dishesList);
                        ObjectPersistence.writeObjectToFile(mContext, dishesList, DISHES_DATA_PATH);
                        if (isRefresh) {
                            //dishAdapter = new DishAdapter(getActivity(), dishesList);
                            if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                                dishAdapter.mDishList.clear();
                            }
                            dishAdapter.mDishList.addAll(dishesList);
                            isRefresh = false;
                        } else {
                            dishAdapter = new DishAdapter(mContext, false);
                            dishAdapter.mDishList.addAll(dishesList);
                        }
                        dishListView.setAdapter(dishAdapter);
                        dishAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalDishesData();
                    if (dishesList != null && dishesList.size() > 0) {
                        if (isRefresh) {
                            if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                                dishAdapter.mDishList.clear();
                            }
                            dishAdapter.mDishList.addAll(dishesList);
                            isRefresh = false;
                        } else {
                            dishAdapter = new DishAdapter(mContext, false);
                            dishAdapter.mDishList.addAll(dishesList);
                        }
                        dishListView.setAdapter(dishAdapter);
                        dishAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                getLocalDishesData();
                if (dishesList != null && dishesList.size() > 0) {
                    if (isRefresh) {
                        if (dishAdapter.mDishList != null && dishAdapter.mDishList.size() > 0) {
                            dishAdapter.mDishList.clear();
                        }
                        dishAdapter.mDishList.addAll(dishesList);
                        isRefresh = false;
                    } else {
                        dishAdapter = new DishAdapter(mContext, false);
                        dishAdapter.mDishList.addAll(dishesList);
                    }
                    dishListView.setAdapter(dishAdapter);
                    dishAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocalDishesData() {
        List<DishBean> localDishList = (List<DishBean>) ObjectPersistence.readObjectFromFile(mContext, DISHES_DATA_PATH);
        if (localDishList != null) {
            dishesList = localDishList;
        }
    }
}