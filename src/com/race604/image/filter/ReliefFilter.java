package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;

import android.view.MotionEvent;

public class ReliefFilter implements IFilter{

    public native void preview(int width, int height, byte yuv[], int[] rgba);
    public native void taken(int width, int height, int[] rgba);
    
    
    private int mX, mY, mRangeInt;

    static {
        System.loadLibrary("jing_native");
    }
    
    @Override
    public boolean onTouchSurface(SurfaceViewBase surfaceView, MotionEvent envent) {
        return false;
    }

    @Override
    public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
        preview(width, height, yuv, rgba);
    }

    @Override
    public void onTakePicture(int[] data, int width, int height) {
        taken(width, height, data);
    }

    @Override
    public void onInit(int width, int height) {
        
    }

}
