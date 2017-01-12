package com.adaptivehandyapps.snowman;

import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by matuc on 12/8/2016.
 */

///////////////////////////////////////////////////////////////////////////
public class SnowFlakeList {
    private static final String TAG = SnowFlakeList.class.getSimpleName();

    public final static Integer FLAKE_COUNT_MAX = 256;
//    public final static Integer FLAKE_COUNT_MAX = 2;

    private List<SnowFlake> mSnowFlakeList;

    private Integer mFlakeCount = FLAKE_COUNT_MAX;

    ///////////////////////////////////////////////////////////////////////////
    public SnowFlakeList(Integer flakeCount, RectF bounds) {
        // create collection
        mSnowFlakeList = new ArrayList<>();

        addSnowFlakes(flakeCount, bounds);
//        // setup loop
//        final float minX = bounds.left;
//        final float maxX = bounds.right;
//        final float minY = bounds.top;
//        final float maxY = bounds.bottom;
//        Random r = new Random();
//
//        // for each flake
//        for (Integer i = 0; i < getFlakeCount(); i++) {
//            // determine position
//            Double x = (r.nextDouble() * (maxX-minX)) + minX;
//            Double y = (r.nextDouble() * (maxY-minY)) + minY;
//            // create snowflake
//            SnowFlake sf = new SnowFlake(x, y);
//            // add to list
//            mSnowFlakeList.add(sf);
//        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //
    public Boolean addSnowFlakes(Integer flakeCount, RectF bounds) {
        // validate count
        setFlakeCount(flakeCount);
        // setup loop
        final float minX = bounds.left;
        final float maxX = bounds.right;
        final float minY = bounds.top;
        final float maxY = bounds.bottom;
        Random r = new Random();

        // for each flake
        for (Integer i = 0; i < getFlakeCount(); i++) {
            // determine position
            Double x = (r.nextDouble() * (maxX-minX)) + minX;
            Double y = (r.nextDouble() * (maxY-minY)) + minY;
            // create snowflake
            SnowFlake sf = new SnowFlake(x, y);
            // add to list
            mSnowFlakeList.add(sf);
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    public Integer getFlakeCount() {
        return mFlakeCount;
    }

    public void setFlakeCount(Integer flakeCount) {
        // validate count
        if (flakeCount < FLAKE_COUNT_MAX) mFlakeCount = flakeCount;
        else Log.e(TAG, " SnowFlakeList would choke on " + flakeCount + " snowflakes.");
    }

    public List<SnowFlake> getSnowFlakeList() {
        return mSnowFlakeList;
    }

    public void setSnowFlakeList(List<SnowFlake> mSnowFlakeList) {
        this.mSnowFlakeList = mSnowFlakeList;
    }
    ///////////////////////////////////////////////////////////////////////////
}
