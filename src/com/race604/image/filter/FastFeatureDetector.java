package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;

import android.view.MotionEvent;

public class FastFeatureDetector implements IFilter {

    private int mColor;
	
    @Override
	public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
		FindFeatures(width, height, yuv, rgba);
	}
	
	public native void FindFeatures(int width, int height, byte yuv[], int[] rgba);

    static {
        System.loadLibrary("jing_native");
    }

    @Override
    public void onInit(int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onTouchSurface(SurfaceViewBase surfaceView, MotionEvent envent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onTakePicture(int[] data, int width, int height) {
        // TODO Auto-generated method stub
        
    }

}
