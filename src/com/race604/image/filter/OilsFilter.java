package com.race604.image.filter;

import android.view.MotionEvent;

import com.race604.camera.SurfaceViewBase;

public class OilsFilter implements IFilter{

	public native void preview(int width, int height, byte yuv[], int[] rgba, int strength);
    public native void taken(int width, int height, int[] rgba, int strength);
    
    private int mStrength = 10;
    
    static {
        System.loadLibrary("jing_native");
    }
    
	
	@Override
	public boolean onTouchSurface(SurfaceViewBase surfaceView,
			MotionEvent envent) {
		return false;
	}

	@Override
	public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
		preview(width, height, yuv, rgba, mStrength);
	}

	@Override
	public void onTakePicture(int[] data, int width, int height) {
		taken(width, height, data, (int) (mStrength * 2));
		
	}

	@Override
	public void onInit(int width, int height) {
	}

}
