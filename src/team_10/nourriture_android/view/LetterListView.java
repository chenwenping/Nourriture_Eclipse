package team_10.nourriture_android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import team_10.nourriture_android.R;


public class LetterListView extends View {
    private static final int SEARCH = 0;
    private static final float FONT_SIZE_CHANGE_RATE = 0.03f;
    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    String[] b = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"
            , "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;
    float bitmapyPos;
    private float textSize;

    public LetterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetterListView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//		if(showBkg){
//		    canvas.drawColor(Color.parseColor("#40000000"));
//		}

        float height = (float) getHeight() - 20;
        int width = getWidth();
//	    Bitmap bitmap = getBitmap(getResources().getDrawable(R.drawable.xiaoyuanhao_seach_icon));
        float singleHeight = height / b.length;
        int blue = getContext().getResources().getColor(R.color.head_text_color);
        int chooseColor = Color.parseColor("#3399ff");
        for (int i = 0; i < b.length; i++) {
            paint.setColor(blue);
            paint.setTypeface(Typeface.SANS_SERIF);
            paint.setAntiAlias(true);
            if (i == choose) {
                paint.setColor(chooseColor);
//	    	   paint.setFakeBoldText(true);
            }
            paint.setTextSize(textSize);
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
           /*if (i == SEARCH) {
               float magnifier_xPos = width/2 - bitmap.getWidth()/2;
	    	   canvas.drawBitmap(bitmap, magnifier_xPos, singleHeight / 6, paint);
	       }else {*/
            canvas.drawText(b[i], xPos, yPos, paint);
           /*}*/
            paint.reset();
        }
    }

    private Bitmap getBitmap(Drawable d) {
        BitmapDrawable bd = (BitmapDrawable) d;
        return bd.getBitmap();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY() - bitmapyPos;
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        float h = getHeight();
        float f = h - bitmapyPos;
        final int c = (int) (y / f * b.length);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onTouchingLetterChanged(b[c]);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < b.length) {
                        listener.onTouchingLetterChanged(b[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        float height = getHeight();
        textSize = height * FONT_SIZE_CHANGE_RATE;
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}
