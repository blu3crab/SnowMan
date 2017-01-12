package com.adaptivehandyapps.snowman;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by matuc on 11/7/2016.
 */

public class CustomViewSnow extends View {

    private static final String TAG = CustomViewSnow.class.getSimpleName();

    private static final int SNOWMAN_RED = 192;
    private static final int SNOWMAN_GREEN = 192;
    private static final int SNOWMAN_BLUE = 200;

    private static final int DRAW_EXTRA_MAX = 4;

    private static final float DEFAULT_GREET_TEXT_SIZE_DP = 96.0F;
    private static final float DECREMENT_GREET_TEXT_SIZE_DP = 24.0F;

    private int mCanvasWidth;   // from canvas - padding
    private int mCanvasHeight;

    private Paint mExtraPaint;
    private Paint mSnowmanPaint;
    private Paint mGreetingPaint;

    private SnowFlakeList mSnowFlakeList;

    private Double mShakeX = 0.0;
    private Double mShakeY = 0.0;
    private Double mShakeZ = 0.0;

    private Integer mAnimationsInProgress = 0;
    private Integer startFlakeInx;
    private Integer endFlakeInx;

    private Integer mDrawExtras = 0;

    private Integer mGreet1Inx = 0;
    private Integer mGreet2Inx = 0;
    private Integer mGreetPass = 0;

    private String[] mGreet1 = {"Shake ", "Merry ", "Happy ", "Hoorah ", "Feliz ", "Buon ", "Stille ", "Silly ", "Jolly "};
    private String[] mGreet2 = {"Me!", "Christmas!", "Holidays!", "Hanukkah!", "Navidad!", "Natale!", "Nacht!", "Solstice!", "Jingles!"};
    ///////////////////////////////////////////////////////////////////////////
    public CustomViewSnow(Context context) {
        super(context);
        init();
    }

    public CustomViewSnow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void clearGreeting() {
        mGreetPass = 0;
        mGreet1Inx = 0;
        mGreet2Inx = 0;
    }
    public void setGreeting() {
        ++mGreetPass;
        if (mGreetPass < mGreet1.length) {
            ++mGreet1Inx;
            ++mGreet2Inx;
        }
        else {
            int min = 0;
            int max = mGreet1.length - 1;
            Random r = new Random();
            mGreet1Inx = r.nextInt((max - min) + 1) + min;
            mGreet2Inx = r.nextInt((max - min) + 1) + min;
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    public void setColor(int r, int g, int b) {
        mSnowmanPaint.setColor(Color.rgb(r, g, b));
        mGreetingPaint.setColor(Color.rgb(r, g, b));
        Log.d(TAG, "setColor r,g,b " + r + ", " + g + ", " + b);
        invalidate();
    }
    ///////////////////////////////////////////////////////////////////////////
    public Integer getDrawExtras() {
        return mDrawExtras;
    }
    ///////////////////////////////////////////////////////////////////////////
    public void setDrawExtras(Integer drawCount) {
        if (drawCount > DRAW_EXTRA_MAX) mDrawExtras = DRAW_EXTRA_MAX;
        else mDrawExtras = drawCount;
    }
    ///////////////////////////////////////////////////////////////////////////
    public void setShake(Double x, Double y, Double z) {
        mShakeX = x;
        mShakeY = y;
        mShakeZ = z;
    }
    ///////////////////////////////////////////////////////////////////////////
    public int getFlakeCount() {
        final int minFlakeCount = 1;
        final int maxFlakeCount = SnowFlakeList.FLAKE_COUNT_MAX;
        Random r = new Random();
        return r.nextInt((maxFlakeCount - minFlakeCount) + 1) + minFlakeCount;
    }
    ///////////////////////////////////////////////////////////////////////////
    public RectF getSky() {
        return new RectF(getPaddingLeft(), - getPaddingTop(), mCanvasWidth, (mCanvasHeight / 4));
    }

    ///////////////////////////////////////////////////////////////////////////
    public SnowFlakeList startFlakeList() {
        int flakeCount = getFlakeCount();
        RectF sky = getSky();
        return new SnowFlakeList(flakeCount, sky);
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean addFlakeList() {
        int flakeCount = getFlakeCount();
        RectF sky = getSky();
        mSnowFlakeList.addSnowFlakes(flakeCount, sky);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public void animateFlake() {

        if (mAnimationsInProgress <= 0 ) {
            // create a new snowflake collection
            mSnowFlakeList = startFlakeList();
            startFlakeInx = 0;
            endFlakeInx = mSnowFlakeList.getSnowFlakeList().size();
        }
        else {
            // add to collection
            addFlakeList();
            startFlakeInx = endFlakeInx;
            endFlakeInx = mSnowFlakeList.getSnowFlakeList().size();
            // set extras to draw
            setDrawExtras(1);
        }

        setGreeting();

        // prepare for random duration generation
        final int min = 1000;
        final int max = 8000;
        Random r = new Random();
        SnowFlake flake = new SnowFlake();
//        for (final SnowFlake snowFlake : mSnowFlakeList.getSnowFlakeList()) {
        for (int i = startFlakeInx; i < endFlakeInx; i++) {
            final SnowFlake snowFlake = mSnowFlakeList.getSnowFlakeList().get(i);
            ++mAnimationsInProgress;
            // ValueAnimator to generate incremental values via evaluate method
            Double startPropertyValue = 0.0;
            Double endPropertyValue = new Double(mCanvasHeight - (mCanvasHeight / 8));
            ValueAnimator animation = ValueAnimator.ofObject(new TypeEvaluator<Double>() {
                @Override
                public Double evaluate(float fraction, Double startValue, Double endValue) {
                    return startValue + (fraction * (endValue - startValue));
                }
            }, startPropertyValue, endPropertyValue);
            long duration = r.nextInt(max - min) + min;
            animation.setDuration(duration);
//            animation.setDuration(2000);
            // update listener triggers on ValueAnimator animation values then invalidates view to trigger redraw
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Double value = (Double) animation.getAnimatedValue();
//                    float fraction = animation.getAnimatedFraction();
//                    if (fraction < 0.10) snowFlake.setFlakeX(snowFlake.getBaseX() + value);
//                    else if (fraction < 0.20) snowFlake.setFlakeX(snowFlake.getBaseX() - value);
                    snowFlake.setFlakeY(snowFlake.getBaseY() + value);
                    snowFlake.getRect().offsetTo(snowFlake.getFlakeX().floatValue(), snowFlake.getFlakeY().floatValue());
//                Log.d(TAG, "onAnimationUpdate value, X, Y = " + value + ", " + mSnowFlake.getFlakeX() + ", " + mSnowFlake.getFlakeY());
                    invalidate();
                }
            });

            animation.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    // done
                    --mAnimationsInProgress;
                    Log.d(TAG, "onAnimationEnd...");
                    if (mAnimationsInProgress <= 0) {
                        // set extras to draw
                        setDrawExtras(0);
                        mGreetingPaint.setColor(Color.rgb(255,255,255));
                    }
                }
            });
            animation.start();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    private void init() {
        // snowman paint
        mSnowmanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSnowmanPaint.setStyle(Paint.Style.FILL);
//        mSnowmanPaint.setColor(Color.rgb(127, 127, 127));
        mSnowmanPaint.setColor(Color.rgb(SNOWMAN_RED, SNOWMAN_GREEN, SNOWMAN_BLUE));
        // extras paint
        mExtraPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mExtraPaint.setStyle(Paint.Style.FILL);
        mExtraPaint.setColor(Color.rgb(255,255,255));
        // ground paint
        mGreetingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGreetingPaint.setStyle(Paint.Style.FILL);
//        mGreetingPaint.setColor(Color.rgb(SNOWMAN_RED, SNOWMAN_GREEN, SNOWMAN_BLUE));
        mGreetingPaint.setColor(Color.rgb(255,255,255));
        mGreetingPaint.setTextAlign(Paint.Align.LEFT);
        mGreetingPaint.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD_ITALIC));
        // set default text size
        mGreetingPaint.setTextSize(DEFAULT_GREET_TEXT_SIZE_DP);

//        // create 1st snowflake
//        mSnowFlake = new SnowFlake();
        mSnowFlakeList = startFlakeList();
        // set extras to draw
        setDrawExtras(0);

    }

    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

//        Log.d(TAG, "onMeasure width/height mode " + modeToString(widthMode) + "/" + modeToString(heightMode));
//        Log.d(TAG, "onMeasure width/height size = " + widthSize + "/ " + heightSize);

        mCanvasWidth = widthSize;
        mCanvasHeight = heightSize;

        Log.d(TAG, "onMeasure width/height = " + mCanvasWidth + "/" + mCanvasHeight);

        //MUST CALL THIS
        setMeasuredDimension(mCanvasWidth, mCanvasHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    private String modeToString(int mode) {
        if (mode == MeasureSpec.EXACTLY) return "MeasureSpec.EXACTLY";
        if (mode == MeasureSpec.AT_MOST) return "MeasureSpec.AT_MOST";
        return "MeasureSpec.UNSPECIFIED";
    }

    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Clear canvas
//        canvas.drawColor(Color.TRANSPARENT);
//        if (mAnimationsInProgress <= 0 ) canvas.drawColor(Color.BLUE);
        canvas.drawColor(Color.BLUE);

        // draw greeting
        drawGreeting(canvas);

        // draw snowman
        drawSnowman(canvas);

        // draw snow flakes
        drawFlake(canvas);
    }

    ///////////////////////////////////////////////////////////////////////////
    private void setDims(Canvas canvas) {
        // canvas dims less padding
        mCanvasWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        mCanvasHeight = canvas.getHeight() - getPaddingTop() - getPaddingBottom();
//        Log.d(TAG, "setDims canvas width/height " + mCanvasWidth + "/ " + mCanvasHeight);
    }

    ///////////////////////////////////////////////////////////////////////////
    private void drawGreeting(Canvas canvas) {
        // draw greeting if animation not in progress
//        if (mAnimationsInProgress > 0) return;
//        canvas.drawText("Happy Holidays", getPaddingLeft()*4, getPaddingTop()*8, mGreetingPaint);

//        float textPaintSize = DEFAULT_GREET_TEXT_SIZE_DP;
        float textPaintSize = mGreetingPaint.getTextSize();

        String greeting = mGreet1[mGreet1Inx] + mGreet2[mGreet2Inx];
        float textWidth = mGreetingPaint.measureText(greeting);

        // if text overflows canvas, decrement until text size underfills screen width
        while (textWidth > canvas.getWidth() && textPaintSize > DECREMENT_GREET_TEXT_SIZE_DP) {
            textPaintSize -= DECREMENT_GREET_TEXT_SIZE_DP;
            mGreetingPaint.setTextSize(textPaintSize);
            textWidth = mGreetingPaint.measureText(greeting);
        }

        // center text
        float x = (canvas.getWidth()/2) - (textWidth/2);
        canvas.drawText(greeting, x + getPaddingLeft(), getPaddingTop()*16, mGreetingPaint);

    }
    ///////////////////////////////////////////////////////////////////////////
    private void drawSnowman(Canvas canvas) {
        setDims(canvas);
        // offset to bottom w/ half arc
        float ovalBaseOffset = mCanvasHeight - (mCanvasWidth/2);
        RectF ovalBase = getOval(canvas, 1, ovalBaseOffset);
//        int red = Color.red(mSnowmanPaint.getColor());
//        int green = Color.green(mSnowmanPaint.getColor());
//        int blue = Color.blue(mSnowmanPaint.getColor());
//        mSnowmanPaint.setColor(Color.rgb(red, green, blue));
        canvas.drawArc(ovalBase, 180, 180, true, mSnowmanPaint);

        float ovalMidOffset = ovalBaseOffset - (ovalBase.height()/2);
        RectF ovalMid = getOval(canvas, 0.66f, ovalMidOffset);
//        mSnowmanPaint.setColor(Color.rgb(191, 191, 191));
//        mSnowmanPaint.setColor(Color.rgb(red + 8, green + 8, blue + 8));
//        mSnowmanPaint.setAlpha(mSnowmanPaint.getAlpha()+8);
        canvas.drawArc(ovalMid, 0, 360, true, mSnowmanPaint);

        float ovalHeadOffset = ovalMidOffset - (ovalMid.height()/2);
        RectF ovalHead = getOval(canvas, 0.33f, ovalHeadOffset);
//        mSnowmanPaint.setColor(Color.rgb(223, 223, 223));
//        mSnowmanPaint.setColor(Color.rgb(red + 16, green + 16, blue + 16));
//        mSnowmanPaint.setAlpha(mSnowmanPaint.getAlpha()+8);
        canvas.drawArc(ovalHead, 0, 360, true, mSnowmanPaint);

        // draw extras
        drawExtras(canvas, ovalHead );

//        RectF innerOval = getOval(canvas, 0.9f);
//        canvas.drawArc(innerOval, 180, 180, true, backgroundInnerPaint);
//
//        Bitmap mask = Bitmap.createScaledBitmap(mMask, (int) (oval.width() * 1.1), (int) (oval.height() * 1.1) / 2, true);
//        canvas.drawBitmap(mask, oval.centerX() - oval.width() * 1.1f / 2, oval.centerY() - oval.width() * 1.1f / 2, maskPaint);
//
//        canvas.drawText(unitsText, oval.centerX(), oval.centerY() / 1.5f, unitsPaint);
    }
    ///////////////////////////////////////////////////////////////////////////
    private RectF getOval(Canvas canvas, float factor, float offsetY) {
        RectF oval;

        oval = new RectF(0, 0, mCanvasWidth * factor, mCanvasWidth * factor);
//        oval.offset(getPaddingLeft(), getPaddingTop() + offsetY);
//        oval.offset(getPaddingLeft(), offsetY);
        oval.offset(getPaddingLeft() + ((mCanvasWidth-(mCanvasWidth * factor))/2), offsetY);

        return oval;
    }

    ///////////////////////////////////////////////////////////////////////////
    private void drawExtras(Canvas canvas, RectF ovalHead) {
        if (getDrawExtras() > 0) {
            // draw 1st extra - eyes
            float factor = 0.08F;
            RectF eye1 = new RectF(0, 0, mCanvasWidth * factor, mCanvasWidth * factor);
            eye1.offset(ovalHead.centerX() - (ovalHead.width()/3), ovalHead.centerY() - (ovalHead.height()/3));
            canvas.drawArc(eye1, 0, 360, true, mExtraPaint);
            RectF eye2 = new RectF(0, 0, mCanvasWidth * factor, mCanvasWidth * factor);
            eye2.offset(ovalHead.centerX() + (ovalHead.width()/3) - eye2.width(), ovalHead.centerY() - (ovalHead.height()/3));
            canvas.drawArc(eye2, 0, 360, true, mExtraPaint);
            // draw smile
            factor = 0.15F;
            RectF smile = new RectF(0, 0, (mCanvasWidth * factor), mCanvasWidth * factor);
            smile.offset(ovalHead.centerX()-(smile.width()/2), ovalHead.centerY() - (smile.height()/4));
            canvas.drawArc(smile, 0, 180, true, mExtraPaint);
        }
        else {
            // draw lines for eyes, smile
            // draw 1st extra - eyes
            float factor = 0.08F;
            RectF eye1 = new RectF(0, 0, mCanvasWidth * factor, 4);
            eye1.offset(ovalHead.centerX() - (ovalHead.width()/3), ovalHead.centerY() - (ovalHead.height()/3) + (mCanvasWidth * factor/2));
            canvas.drawArc(eye1, 0, 360, true, mExtraPaint);
            RectF eye2 = new RectF(0, 0, mCanvasWidth * factor, 4);
            eye2.offset(ovalHead.centerX() + (ovalHead.width()/3) - eye2.width(), ovalHead.centerY() - (ovalHead.height()/3) + (mCanvasWidth * factor/2));
            canvas.drawArc(eye2, 0, 360, true, mExtraPaint);
            // draw smile
            factor = 0.15F;
            RectF smile = new RectF(0, 0, (mCanvasWidth * factor), 4);
            smile.offset(ovalHead.centerX()-(smile.width()/2), ovalHead.centerY() - (smile.height()/4)  + (mCanvasWidth * factor/2));
            canvas.drawArc(smile, 0, 180, true, mExtraPaint);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    private void drawFlake(Canvas canvas) {
        if (mSnowFlakeList != null) {
            for (SnowFlake snowFlake : mSnowFlakeList.getSnowFlakeList()) {
                canvas.drawArc(snowFlake.getRect(), 0, 360, true, snowFlake.getPaint());
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
}
