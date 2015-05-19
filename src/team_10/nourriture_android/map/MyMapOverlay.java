/*package team_10.nourriture_android.map;

import android.view.MotionEvent;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;

//覆盖整个地图捕捉触控事件的OverLay
public abstract class MyMapOverlay extends Overlay{
	
	private int point_X;
	private int point_Y;
	private GeoPoint newPoint;
	
	public MyMapOverlay(int x,int y){
		point_X = x;
		point_Y = y;
		
	}
	
	boolean flagMove=false;
	//这里实现根据地图移动时重新获取屏幕中心点的经纬度坐标
    @Override 
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
    	System.out.println("X->"+event.getX()+":"+point_X);
    	System.out.println("Y->"+event.getY()+":"+point_Y);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
        	changePoint(newPoint,1);
        }else if(event.getAction() == MotionEvent.ACTION_UP){
        	newPoint = mapView.getProjection().fromPixels(point_X,point_Y);
        	changePoint(newPoint,2);
        }       
        return false;
    }
    
    public abstract void changePoint(GeoPoint newPoint,int type);
}
*/