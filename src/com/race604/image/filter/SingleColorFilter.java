package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;

import android.view.MotionEvent;

public class SingleColorFilter implements IFilter{

	private byte[] mColor = new byte[3];
	
	public SingleColorFilter() {
	}
	
	@Override
	public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
	    preview(width, height, yuv, rgba, mColor[0], 5);
	}
	
	public native void preview(int width, int height, byte yuv[], int[] rgba, int h, int th);
	public native void taken(int width, int height, int[] rgba, int h, int th);

    static {
        System.loadLibrary("jing_native");
    }

    @Override
    public void onTakePicture(int[] rgba, int width, int height) {
    	
        taken(width, height, rgba, mColor[0], (int) (5*1.5));
        
    }

    @Override
    public void onInit(int width, int height) {
        
    }

    @Override
    public boolean onTouchSurface(SurfaceViewBase surface, MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        
        surface.getYUVAt(mColor, x, y);
        Utils.yuv2rgb(mColor, mColor);
        Utils.rgb2hsv(mColor, mColor);
        
        return false;
    }

}
