package team_10.nourriture_android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;


public class UITableView extends ListView implements OnScrollListener {
    private static final int MAX_ALPHA = 255;
    int titledy;
    private TreeMap<String, Bitmap> titlemap;
    private TreeMap<String, String> groupNameMap;
    private List<String> keylist;
    private int Section;
    private View mHeadView;
    private Paint mPaint;

    public UITableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Section = 0;
        titlemap = new TreeMap<String, Bitmap>();
        groupNameMap = new TreeMap<String, String>();
        keylist = new ArrayList<String>();
        mPaint = new Paint();
        this.setVerticalFadingEdgeEnabled(false);
        this.setOnScrollListener(this);
    }

    public UITableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UITableView(Context context) {
        this(context, null);
    }

    public void clear() {
        Section = 0;
        if (titlemap != null) {
            titlemap.clear();
        }

        if (keylist != null) {
            keylist.clear();
        }

        if (groupNameMap != null) {
            groupNameMap.clear();
        }

        mHeadView = null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//			LoadImage.getInstance().doTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        for (int i = 0; i < visibleItemCount; i++) {
            Object obj = this.getAdapter().getItem(firstVisibleItem + i);
            if (obj.getClass() == String.class) {
                String groupName = obj.toString();
//				if (titlemap.get("" + (firstVisibleItem + i)) == null) {
//					this.getChildAt(i).setDrawingCacheEnabled(true);
//					Bitmap bmp = Bitmap.createBitmap(this.getChildAt(i)
//							.getDrawingCache());
//					if (!bmp.isRecycled()) {
//						titlemap.put("" + (firstVisibleItem + i), bmp);
//						groupNameMap.put("" + (firstVisibleItem + i), groupName);
//						if (!keylist.contains("" + (firstVisibleItem + i))) {
//							keylist.add("" + (firstVisibleItem + i));
//						}
//					}
//					this.getChildAt(i).setDrawingCacheEnabled(false);
//				}else {
                String exit_key = null;
                for (Entry<String, String> entry : groupNameMap.entrySet()) {
                    if (entry.getValue().equals(groupName)) {
                        exit_key = entry.getKey();
                        break;
                    }
                }

                if (exit_key != null) {
                    if (!exit_key.equals("" + (firstVisibleItem + i))) {
                        groupNameMap
                                .put("" + (firstVisibleItem + i), groupName);
                        groupNameMap.remove(exit_key);

                        Bitmap bitmap = titlemap.get(exit_key);
                        titlemap.put("" + (firstVisibleItem + i), bitmap);
                        titlemap.remove(exit_key);

                        keylist.remove(exit_key);
                        if (!keylist.contains("" + (firstVisibleItem + i))) {
                            keylist.add("" + (firstVisibleItem + i));
                        }
                    }
                } else {
                    createBitmap(i, firstVisibleItem, groupName);
                }
                Section = firstVisibleItem + i;
                mHeadView = this.getChildAt(i);
                break;
            }
        }
    }

    private void createBitmap(int i, int firstVisibleItem, String groupName) {
        this.getChildAt(i).setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(this.getChildAt(i)
                .getDrawingCache());
        if (!bmp.isRecycled()) {
            titlemap.put("" + (firstVisibleItem + i), bmp);
            groupNameMap.put("" + (firstVisibleItem + i), groupName);
            if (!keylist.contains("" + (firstVisibleItem + i))) {
                keylist.add("" + (firstVisibleItem + i));
            }
        }
        this.getChildAt(i).setDrawingCacheEnabled(false);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeadView == null)
            return;
        canvas.save();
        View child = this.getChildAt(0);
        if (child == null)
            return;
        int childid = this.getPositionForView(child);
        String titlekey = "";
        String key = "" + Section;
        titledy = 0;
        mPaint.setAlpha(MAX_ALPHA);
        Collections.sort(keylist, new MyComparator());
        Log.d("TAG", "childid:" + childid + "----Section:" + Section + "-----index:" + keylist.indexOf(key));
        if (childid < Section) {
            if (keylist.contains(key) && keylist.indexOf(key) > 0) {
                titlekey = keylist.get(keylist.indexOf(key) - 1);
            } else {
                titlekey = keylist.get(0);
            }
//			标签移动计算
            if (mHeadView.getTop() <= mHeadView.getHeight()) {
                titledy -= (mHeadView.getHeight() - mHeadView.getTop());
                int alpha = (int) (MAX_ALPHA * (mHeadView.getTop() * 1.0f / mHeadView.getHeight()));
                mPaint.setAlpha(alpha);
            }
        } else if (childid == Section) {
            titlekey = keylist.get(keylist.indexOf(key));
            titledy = 0;
        } else if (childid > Section && keylist.indexOf(key) == keylist.size() - 1) {
            titlekey = keylist.get(keylist.size() - 1);
            titledy = 0;
        }
        if (titlemap.get(titlekey) != null)
            canvas.drawBitmap(titlemap.get(titlekey), 0, titledy, mPaint);
        canvas.restore();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        clear();
        super.setAdapter(adapter);
    }

    class MyComparator implements Comparator<String> {

        @Override
        public int compare(String obj1, String obj2) {
            int i = Integer.valueOf(obj1);
            int j = Integer.valueOf(obj2);
            if (i < j) {
                return -1;
            } else if (i > j) {
                return 1;
            }

            return 0;
        }

    }
}
