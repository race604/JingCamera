package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;

import android.graphics.Point;
import android.view.MotionEvent;

public class SpherizeFilter implements IFilter {

    public native void preview(int width, int height, byte yuv[], int[] rgba, int x, int y, int range, float scale);
    public native void taken(int width, int height, int[] rgba, int x, int y, int range, float scale);
    
    private int mWidth, mHeight;
    private float mScale = 1.0f;
    private float mRange = 0.2f;
    
    private int mX, mY, mRangeInt;

    static {
        System.loadLibrary("jing_native");
    }
    
    @Override
    public boolean onTouchSurface(SurfaceViewBase surfaceView, MotionEvent envent) {
        Point point = surfaceView.getPointAt((int)envent.getX(), (int)envent.getY());
        mX = point.x;
        mY = point.y;
        
        return true;
    }

    @Override
    public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
        
        preview(width, height, yuv, rgba, mX, mY, mRangeInt, mScale);
        
    }

    @Override
    public void onTakePicture(int[] data, int width, int height) {
        int x = mX * width / mWidth;
        int y = mY * height / mHeight;
        
        int range  = (int)((float)height * mRange);
        
        taken(width, height, data, x, y, range, mScale);
        
    }

    @Override
    public void onInit(int width, int height) {
        mWidth = width;
        mHeight = height;
        mRangeInt = (int)(mHeight * mRange);
        
        mX = mWidth >> 1;
        mY = mHeight >> 1;
    }

}
