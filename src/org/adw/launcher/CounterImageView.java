package org.adw.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CounterImageView extends ImageView {
    //ADW custom notifier counters
    private String mCounter=null;
    private int mCounterSize=0;
    private final Rect mRect2 = new Rect();
    private Paint mStrokePaint;
    private Paint mTextPaint;

    public CounterImageView(Context context) {
        super(context);
        init();
    }

    public CounterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CounterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init(){
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setARGB(255, 255, 0, 0);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setARGB(255, 255, 255, 255);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(12);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setShadowLayer(2f, 1, 1, 0xFF000000);
    }
    public void setCounter(int counter){
        if(counter>0){
            mCounter=String.valueOf(counter);
            mTextPaint.getTextBounds(mCounter, 0, mCounter.length(), mRect2);
            mCounterSize=(Math.max(mRect2.width(), mRect2.height())/2)+2;
        }else{
            mCounter=null;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.draw(canvas);
        //OVERLAY COUNTERS
        if(mCounter!=null){
            canvas.save();
            canvas.translate(getScrollX(), getScrollY());
            canvas.drawCircle(mCounterSize+5, mCounterSize+5,mCounterSize, mStrokePaint);
            canvas.drawText(mCounter, mCounterSize+5, mCounterSize+5+mRect2.height()/2, mTextPaint);
            canvas.restore();
        }
    }
    
}
