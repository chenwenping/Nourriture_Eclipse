package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.content.Context;
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
import team_10.nourriture_android.adapter.RestaurantAdapter;
import team_10.nourriture_android.bean.RestaurantBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.ObjectPersistence;

public class RestaurantFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String RESTAURANT_DATA_PATH = "_restaurant_data.bean";
    private List<RestaurantBean> restaurantList;
    private SwipeRefreshLayout swipeLayout;
    private ListView restaurantListView;
    private RestaurantAdapter restaurantAdapter;
    private ProgressDialog progress;
    private boolean isRefresh = false;
    private EditText search_et;
    private Button search_btn;
    private String search_content;
    private List<RestaurantBean> searchRestaurantList;
    private boolean isSearch = false;
    private boolean beforeChangeHaveText = true;
    private boolean afterChangeHaveText = true;
    private int searchIconDefault; // default search icon
    private int searchIconClear; // clear search text icon
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

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
                    getSearchRestaurantList();
                    afterChangeHaveText = false;
                }
                if (beforeChangeHaveText && afterChangeHaveText) {
                    isSearch = true;
                    search_btn.setBackgroundResource(searchIconDefault);
                }
            }
        });
        search_btn.setOnClickListener(this);

        swipeLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        restaurantListView = (ListView) getActivity().findViewById(R.id.restaurantListView);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading...");
        progress.show();

        getAllRestaurant();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                if (isSearch) {
                    search_content = search_et.getText().toString().trim();
                    if (search_content == null || "".equals(search_content)) {
                        Toast.makeText(getActivity(), "Please enter the search content.", Toast.LENGTH_SHORT).show();
                    } else {
                        isSearch = false;
                        search_content = search_content.replaceAll(" ", "");
                        getSearchRestaurantList();
                        search_btn.setBackgroundResource(searchIconClear);
                    }
                } else {
                    search_et.setText("");
                    isSearch = true;
                    search_btn.setBackgroundResource(searchIconDefault);
                    search_content = "";
                    getSearchRestaurantList();
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }

    public void getSearchRestaurantList() {
        searchRestaurantList = new ArrayList<>();
        if (restaurantList != null && restaurantList.size() > 0) {
            for (int i = 0; i < restaurantList.size(); i++) {
                RestaurantBean restaurantBean = restaurantList.get(i);
                if (restaurantBean.getName().contains(search_content) || search_content.contains(restaurantBean.getName())) {
                    searchRestaurantList.add(restaurantBean);
                }
            }
            if (searchRestaurantList != null && searchRestaurantList.size() > 0) {
                if (restaurantAdapter.mRestaurantList != null && restaurantAdapter.mRestaurantList.size() > 0) {
                    restaurantAdapter.mRestaurantList.clear();
                }
                restaurantAdapter.mRestaurantList.addAll(searchRestaurantList);
                restaurantListView.setAdapter(restaurantAdapter);
                restaurantAdapter.notifyDataSetChanged();
            } else {
                // search result no Restaurant.
                search_et.setText("");
                search_et.requestFocus();
                search_btn.setBackgroundResource(searchIconDefault);
                search_content = "";
                Toast.makeText(getActivity(), "No match result. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getAllRestaurant();
                }
            }, 3000);
        }
    }

    public void getAllRestaurant() {
        NourritureRestClient.get("dishes", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("Restaurant", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 200) {
                    try {
                        restaurantList = JsonTobean.getList(RestaurantBean[].class, response.toString());
                        Collections.reverse(restaurantList);
                        ObjectPersistence.writeObjectToFile(mContext, restaurantList, RESTAURANT_DATA_PATH);
                        if (isRefresh) {
                            if (restaurantAdapter.mRestaurantList != null && restaurantAdapter.mRestaurantList.size() > 0) {
                                restaurantAdapter.mRestaurantList.clear();
                            }
                            restaurantAdapter.mRestaurantList.addAll(restaurantList);
                            isRefresh = false;
                        } else {
                            restaurantAdapter = new RestaurantAdapter(mContext, false);
                            restaurantAdapter.mRestaurantList.addAll(restaurantList);
                        }
                        restaurantListView.setAdapter(restaurantAdapter);
                        restaurantAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalRestaurantData();
                    if (restaurantList != null && restaurantList.size() > 0) {
                        if (isRefresh) {
                            if (restaurantAdapter.mRestaurantList != null && restaurantAdapter.mRestaurantList.size() > 0) {
                                restaurantAdapter.mRestaurantList.clear();
                            }
                            restaurantAdapter.mRestaurantList.addAll(restaurantList);
                            isRefresh = false;
                        } else {
                            restaurantAdapter = new RestaurantAdapter(mContext, false);
                            restaurantAdapter.mRestaurantList.addAll(restaurantList);
                        }
                        restaurantListView.setAdapter(restaurantAdapter);
                        restaurantAdapter.notifyDataSetChanged();
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
                getLocalRestaurantData();
                if (restaurantList != null && restaurantList.size() > 0) {
                    if (isRefresh) {
                        if (restaurantAdapter.mRestaurantList != null && restaurantAdapter.mRestaurantList.size() > 0) {
                            restaurantAdapter.mRestaurantList.clear();
                        }
                        restaurantAdapter.mRestaurantList.addAll(restaurantList);
                        isRefresh = false;
                    } else {
                        restaurantAdapter = new RestaurantAdapter(mContext, false);
                        restaurantAdapter.mRestaurantList.addAll(restaurantList);
                    }
                    restaurantListView.setAdapter(restaurantAdapter);
                    restaurantAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocalRestaurantData() {
        List<RestaurantBean> localRestaurantList = (List<RestaurantBean>) ObjectPersistence.readObjectFromFile(mContext, RESTAURANT_DATA_PATH);
        if (localRestaurantList != null) {
            restaurantList = localRestaurantList;
        }
    }
}