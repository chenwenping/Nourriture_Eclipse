/*package team_10.nourriture_android.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import team_10.nourriture_android.R;
import team_10.nourriture_android.map.MyMapOverlay;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;

public class LocationActivity extends MapActivity implements LocationListener {

	private MapView mapView;
	private MapController mMapCtrl;
	private List<Overlay> mapOverlays;
	public GeoPoint locPoint;
	private MyMapOverlay mOverlay;
	private TextView desText;
	private String lost_tips;
	private int point_X;
	private int point_Y;

	public final int MSG_VIEW_LONGPRESS = 10001;
	public final int MSG_VIEW_ADDRESSNAME = 10002;
	public final int MSG_GONE_ADDRESSNAME = 10003;
	private Intent mIntent;
	private int mLatitude;
	private int mLongitude;
	private String name;
	private BMapManager mapManager;
	private MKLocationManager mLocationManager = null;
	private boolean isLoadAdrr = true;
	private MKSearch mMKSearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_location);
		initMap();
		mIntent = getIntent();
		mLatitude = mIntent.getIntExtra("latitude", 0);
		mLongitude = mIntent.getIntExtra("longitude", 0);
		name = mIntent.getStringExtra("name");
		mapView = (MapView) findViewById(R.id.map_view);
		desText = (TextView) this.findViewById(R.id.map_bubbleText);
		lost_tips = getResources().getString(R.string.load_tips);
		if (mLatitude != 0 && mLongitude != 0) {
			locPoint = new GeoPoint((int) (mLatitude * 1E6),
					(int) (mLongitude * 1E6));
			desText.setText(name);
		}
		mapView.setBuiltInZoomControls(true);
		mapView.setClickable(true);
		mMapCtrl = mapView.getController();
		point_X = this.getWindowManager().getDefaultDisplay().getWidth() / 2;
		point_Y = this.getWindowManager().getDefaultDisplay().getHeight() / 2;
		mOverlay = new MyMapOverlay(point_X, point_Y) {
			@Override
			public void changePoint(GeoPoint newPoint, int type) {
				if (type == 1) {
					mHandler.sendEmptyMessage(MSG_GONE_ADDRESSNAME);
				} else {
					locPoint = newPoint;
					mHandler.sendEmptyMessage(MSG_VIEW_LONGPRESS);
				}

			}
		};
		mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 0) {
			mapOverlays.clear();
		}
		mapOverlays.add(mOverlay);
		mMapCtrl.setZoom(20);

	}

	private void initMap() {

		// 初始化MapActivity
		mapManager = new BMapManager(getApplication());
		// init方法的第一个参数需填入申请的API Key
		mapManager.init("D00kpN0V5FFGUnEHLNk9ErgF", null);
		super.initMapActivity(mapManager);
		// 实例化搜索地址类
		mMKSearch = new MKSearch();
		// 初始化搜索地址实例
		mMKSearch.init(mapManager, new MySearchListener());
		mLocationManager = mapManager.getLocationManager();
		// 注册位置更新事件
		mLocationManager.requestLocationUpdates(this);
		// 使用GPS定位
		mLocationManager
				.enableProvider((int) MKLocationManager.MK_GPS_PROVIDER);
	}

	@Override
	protected void onResume() {
		if (mapManager != null) {
			mapManager.start();
		}
		super.onResume();

	}

	@Override
	protected void onPause() {
		isLoadAdrr = false;
		if (mapManager != null) {
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


	*//**
	 * 通过经纬度获取地址
	 * 
	 * @param point
	 * @return
	 *//*
	private String getLocationAddress(GeoPoint point) {
		String add = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6,
					1);
			Address address = addresses.get(0);
			int maxLine = address.getMaxAddressLineIndex();
			if (maxLine >= 2) {
				add = address.getAddressLine(1) + address.getAddressLine(2);
			} else {
				add = address.getAddressLine(1);
			}
		} catch (IOException e) {
			add = "";
			e.printStackTrace();
		}
		return add;
	}


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_VIEW_LONGPRESS:// 处理长按时间返回位置信息
			{
				if (null == locPoint)
					return;
				mMKSearch.reverseGeocode(locPoint);
				desText.setVisibility(View.VISIBLE);
				desText.setText(lost_tips);
				mMapCtrl.animateTo(locPoint);
				mapView.invalidate();
			}
				break;
			case MSG_VIEW_ADDRESSNAME:
				desText.setText((String) msg.obj);
				desText.setVisibility(View.VISIBLE);
				break;
			case MSG_GONE_ADDRESSNAME:
				desText.setVisibility(View.GONE);
				break;
			}
		}
	};

	// 关闭程序也关闭定位
	@Override
	protected void onDestroy() {
		if (mapManager != null) {
			mapManager.destroy();
			mapManager = null;
		}
		super.onDestroy();
	}

	*//**
	 * 根据MyLocationOverlay配置的属性确定是否在地图上显示当前位置
	 *//*
	@Override
	protected boolean isLocationDisplayed() {
		return false;
	}

	*//**
	 * 当位置发生变化时触发此方法
	 * 
	 * @param location
	 *            当前位置
	 *//*
	public void onLocationChanged(Location location) {
		if (location != null) {
			locPoint = new GeoPoint((int) (location.getLatitude()* 1E6),
					(int) (location.getLongitude()* 1E6));
			mHandler.sendEmptyMessage(MSG_VIEW_LONGPRESS);
		}
	}

	*//**
	 * 内部类实现MKSearchListener接口,用于实现异步搜索服务
	 * 
	 * @author liufeng
	 *//*
	public class MySearchListener implements MKSearchListener {
		*//**
		 * 根据经纬度搜索地址信息结果
		 * 
		 * @param result
		 *            搜索结果
		 * @param iError
		 *            错误号（0表示正确返回）
		 *//*
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			if (result == null) {
				return;
			}
			Message msg = new Message();
			msg.what = MSG_VIEW_ADDRESSNAME;
			msg.obj = result.strAddr;
			mHandler.sendMessage(msg);

		}

		*//**
		 * 驾车路线搜索结果
		 * 
		 * @param result
		 *            搜索结果
		 * @param iError
		 *            错误号（0表示正确返回）
		 *//*
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int iError) {
		}

		*//**
		 * POI搜索结果（范围检索、城市POI检索、周边检索）
		 * 
		 * @param result
		 *            搜索结果
		 * @param type
		 *            返回结果类型（11,12,21:poi列表 7:城市列表）
		 * @param iError
		 *            错误号（0表示正确返回）
		 *//*
		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
		}

		*//**
		 * 公交换乘路线搜索结果
		 * 
		 * @param result
		 *            搜索结果
		 * @param iError
		 *            错误号（0表示正确返回）
		 *//*
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int iError) {
		}

		*//**
		 * 步行路线搜索结果
		 * 
		 * @param result
		 *            搜索结果
		 * @param iError
		 *            错误号（0表示正确返回）
		 *//*
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int iError) {
		}

		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}
	}

}
*/


package team_10.nourriture_android.activity;

import team_10.nourriture_android.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class LocationActivity extends Activity {
	private Toast mToast;
	private BMapManager mBMapManager;
	private MapView mMapView = null;
	private MapController mMapController = null;
	
	
	private LocationClient mLocClient;
	private LocationData mLocData;
	//定位图层
	private	LocationOverlay myLocationOverlay = null;
	
	private boolean isRequest = false;//是否手动触发请求定位
	private boolean isFirstLoc = true;//是否首次定位
	
	private PopupOverlay mPopupOverlay  = null;//弹出泡泡图层，浏览节点时使用
	private View viewCache;
	private BDLocation location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//使用地图sdk前需先初始化BMapManager，这个必须在setContentView()先初始化
		mBMapManager = new BMapManager(this);
		
		//第一个参数是API key,
		//第二个参数是常用事件监听，用来处理通常的网络错误，授权验证错误等，你也可以不添加这个回调接口
		mBMapManager.init("D00kpN0V5FFGUnEHLNk9ErgF", new MKGeneralListenerImpl());
		setContentView(R.layout.activity_location);
		
		//点击按钮手动请求定位
		((Button) findViewById(R.id.request)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestLocation();
			}
		});
		
		mMapView = (MapView) findViewById(R.id.bmapView); //获取百度地图控件实例
        mMapController = mMapView.getController(); //获取地图控制器
        mMapController.enableClick(true);   //设置地图是否响应点击事件
        mMapController.setZoom(14);   //设置地图缩放级别
        mMapView.setBuiltInZoomControls(true);   //显示内置缩放控件
        
        viewCache = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
        mPopupOverlay = new PopupOverlay(mMapView ,new PopupClickListener() {
			
			@Override
			public void onClickedPopup(int arg0) {
				mPopupOverlay.hidePop();
			}
		});
        
        
        
        mLocData = new LocationData();
        
        
        //实例化定位服务，LocationClient类必须在主线程中声明
        mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(new BDLocationListenerImpl());//注册定位监听接口
		
		/**
		 * 设置定位参数
		 */
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); //打开GPRS
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); //设置发起定位请求的间隔时间为5000ms
		option.disableCache(false);//禁止启用缓存定位
//		option.setPoiNumber(5);    //最多返回POI个数   
//		option.setPoiDistance(1000); //poi查询距离        
//		option.setPoiExtraInfo(true);  //是否需要POI的电话和地址等详细信息        
		
		mLocClient.setLocOption(option);
		mLocClient.start();  //	调用此方法开始定位
		
		//定位图层初始化
		myLocationOverlay = new LocationOverlay(mMapView);
		//设置定位数据
	    myLocationOverlay.setData(mLocData);
	    
	    myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.location_arrows));
	    
	    //添加定位图层
	    mMapView.getOverlays().add(myLocationOverlay);
	    myLocationOverlay.enableCompass();
	    
	    //修改定位数据后刷新图层生效
	    mMapView.refresh();
        
	}
	
	
	/**
	 * 定位接口，需要实现两个方法
	 * @author xiaanming
	 *
	 */
	public class BDLocationListenerImpl implements BDLocationListener {

		/**
		 * 接收异步返回的定位结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			
			LocationActivity.this.location = location;
			
			mLocData.latitude = location.getLatitude();
			mLocData.longitude = location.getLongitude();
			//如果不显示定位精度圈，将accuracy赋值为0即可
			mLocData.accuracy = location.getRadius();
			mLocData.direction = location.getDerect();
			
			//将定位数据设置到定位图层里
            myLocationOverlay.setData(mLocData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();
            
            
			
            if(isFirstLoc || isRequest){
				mMapController.animateTo(new GeoPoint(
						(int) (location.getLatitude() * 1e6), (int) (location
								.getLongitude() * 1e6)));
				
				showPopupOverlay(location);
				
				isRequest = false;
            }
            
            isFirstLoc = false;
		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			
		}

	}
	
	
	/**
	 * 常用事件监听，用来处理通常的网络错误，授权验证错误等
	 * @author xiaanming
	 *
	 */
	public class MKGeneralListenerImpl implements MKGeneralListener{

		/**
		 * 一些网络状态的错误处理回调函数
		 */
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				showToast("您的网络出错啦！");
            }
		}

		/**
		 * 授权错误的时候调用的回调函数
		 */
		@Override
		public void onGetPermissionState(int iError) {
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				showToast("API KEY错误, 请检查！");
            }
		}
		
	}

	//
	private class LocationOverlay extends MyLocationOverlay{

		public LocationOverlay(MapView arg0) {
			super(arg0);
		}

		@Override
		protected boolean dispatchTap() {
			showPopupOverlay(location);
			return super.dispatchTap();
		}

		@Override
		public void setMarker(Drawable arg0) {
			super.setMarker(arg0);
		}
		
		
		
	}
	
	
	
	private void showPopupOverlay(BDLocation location){
		 TextView popText = ((TextView)viewCache.findViewById(R.id.location_tips));
		 popText.setText("[Address] " + location.getAddrStr());
		 mPopupOverlay.showPopup(getBitmapFromView(popText),
					new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()*1e6)),
					10);
	}
	
	
	
	/**
	 * 手动请求定位的方法
	 */
	public void requestLocation() {
		isRequest = true;
		
		if(mLocClient != null && mLocClient.isStarted()){
			showToast("正在定位......");
			mLocClient.requestLocation();
		}else{
			Log.d("LocSDK3", "locClient is null or not started");
		}
	}
	
	@Override
	protected void onResume() {
    	//MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onResume();
		super.onResume();
	}



	@Override
	protected void onPause() {
		//MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		//MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.destroy();
		
		//退出应用调用BMapManager的destroy()方法
		if(mBMapManager != null){
			mBMapManager.destroy();
			mBMapManager = null;
		}
		
		//退出时销毁定位
        if (mLocClient != null){
            mLocClient.stop();
        }
		
		super.onDestroy();
	}

	
	
	 /** 
     * 显示Toast消息 
     * @param msg 
     */  
    private void showToast(String msg){  
        if(mToast == null){  
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);  
        }else{  
            mToast.setText(msg);  
            mToast.setDuration(Toast.LENGTH_SHORT);
        }  
        mToast.show();  
    } 
	
	/**
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
	}
	
}