package team_10.nourriture_android.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;

import com.loopj.android.http.JsonHttpResponseHandler;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.BillAdapter;
import team_10.nourriture_android.bean.BillBean;
import team_10.nourriture_android.bean.RestaurantBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class BillActivity extends ActionBarActivity  implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

	private static final String BILL_DATA_PATH = "_bills_data.bean";
	private List<BillBean> billList;
	private SwipeRefreshLayout swipeLayout;
	private ListView billListView;
	private BillAdapter billAdapter;
	private boolean isRefresh = false;
	private LinearLayout no_bill_ll;
	private Button back_btn;
	private ProgressDialog progress;
	private Context mContext;
	private SharedPreferences sp;
	String username, password;
		
	private EditText search_et;
    private Button search_btn;
    private String search_content;
    private List<BillBean> searchBillList;
    private boolean isSearch = false;
    private boolean beforeChangeHaveText = true;
    private boolean afterChangeHaveText = true;
    private int searchIconDefault; // default search icon
    private int searchIconClear; // clear search text icon
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill);

		initView();

		mContext = this;
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		progress.show();
		
		sp = getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
		username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
        
		getMyBill();
	}

	public void initView() {
		searchIconDefault = R.drawable.search_icon;
        searchIconClear = R.drawable.search_delete;
        search_btn = (Button) this.findViewById(R.id.btn_search);
        search_et = (EditText) this.findViewById(R.id.et_search_text);
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
                    getSearchBillList();
                    afterChangeHaveText = false;
                }
                if (beforeChangeHaveText && afterChangeHaveText) {
                    isSearch = true;
                    search_btn.setBackgroundResource(searchIconDefault);
                }
            }
        });
        search_btn.setOnClickListener(this);
		
		swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        billListView = (ListView) this.findViewById(R.id.billListView);
        no_bill_ll = (LinearLayout) this.findViewById(R.id.ll_no_bill);
        back_btn = (Button) this.findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
	}

	public void getSearchBillList(){
		searchBillList = new ArrayList<>();
        if (billList != null && billList.size() > 0) {
            for (int i = 0; i < billList.size(); i++) {
                BillBean billBean = billList.get(i);
                if(billBean.getName()!=null && !"".equals(billBean.getName())){
                	if (billBean.getName().contains(search_content) || search_content.contains(billBean.getName())) {
                        searchBillList.add(billBean);
                    }
                }     
            }
            if (searchBillList != null && searchBillList.size() > 0) {
                if (billAdapter.mBillList != null && billAdapter.mBillList.size() > 0) {
                	billAdapter.mBillList.clear();
                }
                billAdapter.mBillList.addAll(searchBillList);
                billListView.setAdapter(billAdapter);
                billAdapter.notifyDataSetChanged();
            } else {
                // search result no bill.
                search_et.setText("");
                search_et.requestFocus();
                search_btn.setBackgroundResource(searchIconDefault);
                search_content = "";
                Toast.makeText(mContext, "No match result. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
	}
	
	public void getMyBill() {
		 NourritureRestClient.getWithLogin("getMyBill", null, username, password, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Log.e("getMyBill", response.toString());
				if (progress.isShowing()) {
					progress.dismiss();
	            }
	            if (statusCode == 200) {
	              	try {
						billList = JsonTobean.getList(BillBean[].class, response.toString());
						if(billList!=null && billList.size()>0){
							no_bill_ll.setVisibility(View.GONE);
							swipeLayout.setVisibility(View.VISIBLE);
							Collections.reverse(billList);
	                        ObjectPersistence.writeObjectToFile(mContext, billList, BILL_DATA_PATH);
							if (isRefresh) {
                                if (billAdapter.mBillList != null && billAdapter.mBillList.size() > 0) {
                                	billAdapter.mBillList.clear();
                                }
                                billAdapter.mBillList.addAll(billList);
                                isRefresh = false;
                            } else {
                            	billAdapter = new BillAdapter(mContext, false);
                            	billAdapter.mBillList.addAll(billList);
                            }
                            billListView.setAdapter(billAdapter);
                            billAdapter.notifyDataSetChanged();
						}else{
							no_bill_ll.setVisibility(View.VISIBLE);
							swipeLayout.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }else{
	            	if (progress.isShowing()) {
                        progress.dismiss();
                    }
	            	getLocalBillData();
	            	if(billList!=null && billList.size()>0){
						no_bill_ll.setVisibility(View.GONE);
						swipeLayout.setVisibility(View.VISIBLE);
						if (isRefresh) {
                            if (billAdapter.mBillList != null && billAdapter.mBillList.size() > 0) {
                            	billAdapter.mBillList.clear();
                            }
                            billAdapter.mBillList.addAll(billList);
                            isRefresh = false;
                        } else {
                        	billAdapter = new BillAdapter(mContext, false);
                        	billAdapter.mBillList.addAll(billList);
                        }
                        billListView.setAdapter(billAdapter);
                        billAdapter.notifyDataSetChanged();
					}else{
						no_bill_ll.setVisibility(View.VISIBLE);
						swipeLayout.setVisibility(View.GONE);
					}
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
                    getMyBill();
                }
            }, 3000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        	case R.id.btn_search:
        		if (isSearch) {
                    search_content = search_et.getText().toString().trim();
                    if (search_content == null || "".equals(search_content)) {
                        Toast.makeText(mContext, "Please enter the search content.", Toast.LENGTH_SHORT).show();
                    } else {
                        isSearch = false;
                        search_content = search_content.replaceAll(" ", "");
                        getSearchBillList();
                        search_btn.setBackgroundResource(searchIconClear);
                    }
                } else {
                    search_et.setText("");
                    isSearch = true;
                    search_btn.setBackgroundResource(searchIconDefault);
                    search_content = "";
                    getSearchBillList();
                }
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
    
    public void getLocalBillData() {
        List<BillBean> localBillList = (List<BillBean>) ObjectPersistence.readObjectFromFile(mContext, BILL_DATA_PATH);
        if (localBillList != null) {
            billList = localBillList;
        }
    }
}
