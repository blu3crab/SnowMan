package com.adaptivehandyapps.snowman;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by matuc on 12/7/2016.
 */

///////////////////////////////////////////////////////////////////////////
public class SnowFlake {

    private Paint mSnowflakePaint;

    private RectF mFlakeRect;

    private Double mBaseX;
    private Double mBaseY;
    private Double mFlakeX;
    private Double mFlakeY;


    ///////////////////////////////////////////////////////////////////////////
    public SnowFlake() {
        // init to default base
        init(256.0, 256.0);
    }
    ///////////////////////////////////////////////////////////////////////////
    public SnowFlake(Double baseX, Double baseY) {
        init(baseX, baseY);
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean init (Double baseX, Double baseY) {
        // init paint white
        mSnowflakePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSnowflakePaint.setStyle(Paint.Style.FILL);
        mSnowflakePaint.setColor(Color.rgb(255, 255, 255));
        // set base position & current position
        setBaseX(baseX);
        setBaseY(baseY);
        setFlakeX(getBaseX());
        setFlakeY(getBaseY());
        // create rect plus offset from base
        mFlakeRect = new RectF(0, 0, 16, 16);
        mFlakeRect.offset(getFlakeX().floatValue(), getFlakeY().floatValue());
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // getters & setters
    public Paint getPaint() {
        return mSnowflakePaint;
    }

    public void setPaint(Paint snowflakePaint) {
        this.mSnowflakePaint = snowflakePaint;
    }

    public RectF getRect() {
        return mFlakeRect;
    }

    public void setRect(RectF flakeRect) {
        this.mFlakeRect = flakeRect;
    }

    public Double getBaseX() { return mBaseX; }

    public void setBaseX(Double baseX) { this.mBaseX = baseX; }

    public Double getBaseY() { return mBaseY; }

    public void setBaseY(Double baseY) { this.mBaseY = baseY; }

    public Double getFlakeX() { return mFlakeX; }

    public void setFlakeX(Double flakeX) { this.mFlakeX = flakeX; }

    public Double getFlakeY() { return mFlakeY; }

    public void setFlakeY(Double flakeY) { this.mFlakeY = flakeY; }

}
