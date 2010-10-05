/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adw.launcher;


import android.widget.TextView;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;

/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
public class BubbleTextView extends TextView {
    //private static final float CORNER_RADIUS = 8.0f;
    private static final float PADDING_H = 5.0f;
    private static final float PADDING_V = 1.0f;

    private final RectF mRect = new RectF();
    private Paint mPaint;

    private boolean mBackgroundSizeChanged;
    private Drawable mBackground;
    private float mCornerRadius;
    private float mPaddingH;
    private float mPaddingV;
    //adw custom corner radius themable
    private float mCustomCornerRadius=8.0f;
    //ADW custom notifier counters
    private String mCounter=null;
    private int mCounterSize=0;
    private final Rect mRect2 = new Rect();
    private Paint mStrokePaint;
    private Paint mTextPaint;
    public BubbleTextView(Context context) {
        super(context);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setFocusable(true);
        //mBackground = getBackground();
        mBackground=IconHighlights.getDrawable(getContext(),IconHighlights.TYPE_DESKTOP);
        setBackgroundDrawable(null);
        mBackground.setCallback(this);
        //ADW: Load textcolor and bubble color from theme
        String themePackage=AlmostNexusSettingsHelper.getThemePackageName(getContext(), Launcher.THEME_DEFAULT);
        int color=getContext().getResources().getColor(R.color.bubble_dark_background);
        if(!themePackage.equals(Launcher.THEME_DEFAULT)){
        	Resources themeResources=null;
        	try {
    			themeResources=getContext().getPackageManager().getResourcesForApplication(themePackage);
    		} catch (NameNotFoundException e) {
    			//e.printStackTrace();
    		}
    		if(themeResources!=null){
    			int resourceId=themeResources.getIdentifier("bubble_color", "color", themePackage);
    			if(resourceId!=0){
    				color=themeResources.getColor(resourceId);
    			}
    			int textColorId=themeResources.getIdentifier("bubble_text_color", "color", themePackage);
    			if(textColorId!=0){
    				setTextColor(themeResources.getColor(textColorId));
    			}
    			int cornerId=themeResources.getIdentifier("bubble_radius", "integer", themePackage);
    			if(cornerId!=0){
    				mCustomCornerRadius=(float)themeResources.getInteger(cornerId);
    			}
    		}
        }
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPaint.setColor(getContext().getResources().getColor(R.color.bubble_dark_background));
        mPaint.setColor(color);
        final float scale = getContext().getResources().getDisplayMetrics().density;
        //mCornerRadius = CORNER_RADIUS * scale;
        mCornerRadius = mCustomCornerRadius * scale;
        mPaddingH = PADDING_H * scale;
        //noinspection PointlessArithmeticExpression
        mPaddingV = PADDING_V * scale;
        
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setARGB(255, 255, 0, 0);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setARGB(255, 255, 255, 255);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(12);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setShadowLayer(2f, 1, 1, 0xFF000000);
        
    }

    @Override
    protected boolean setFrame(int left, int top, int right, int bottom) {
        if (getLeft() != left || getRight() != right || getTop() != top || getBottom() != bottom) {
            mBackgroundSizeChanged = true;
        }
        return super.setFrame(left, top, right, bottom);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground || super.verifyDrawable(who);
    }

    @Override
    protected void drawableStateChanged() {
        Drawable d = mBackground;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
        super.drawableStateChanged();
    }

    @Override
    public void draw(Canvas canvas) {
        final Drawable background = mBackground;
        if (background != null) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if (mBackgroundSizeChanged) {
                background.setBounds(0, 0,  getRight() - getLeft(), getBottom() - getTop());
                mBackgroundSizeChanged = false;
            }

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }
        if(getText().length()>0){
	        final Layout layout = getLayout();
	        final RectF rect = mRect;
	        final int left = getCompoundPaddingLeft();
	        final int top = getExtendedPaddingTop();
	
	        rect.set(left + layout.getLineLeft(0) - mPaddingH,
	                top + layout.getLineTop(0) -  mPaddingV,
	                Math.min(left + layout.getLineRight(0) + mPaddingH, getScrollX() + getRight() - getLeft()),
	                top + layout.getLineBottom(0) + mPaddingV);
	        canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, mPaint);
        }
        super.draw(canvas);
        //OVERLAY COUNTERS
        if(mCounter!=null){
            canvas.save();
            canvas.translate(getScrollX(), getScrollY());
            canvas.drawCircle(mCounterSize+15, mCounterSize+15,mCounterSize, mStrokePaint);
            canvas.drawText(mCounter, mCounterSize+15, mCounterSize+15+mRect2.height()/2, mTextPaint);
            canvas.restore();
        }
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
}
