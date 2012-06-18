package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;
import com.race604.camera.SurfaceViewBase.OnTouchSurfaceListener;

import android.view.MotionEvent;
import android.view.View;

public class SingleColorFilter implements IFilter, OnTouchSurfaceListener {

	private int mYUV;
	
	@Override
	public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
		SingleColor(width, height, yuv, rgba, mYUV);
		
	}
	
	public native void SingleColor(int width, int height, byte yuv[], int[] rgba, int color);

    static {
        System.loadLibrary("jing_native");
    }

    @Override
    public void onTakePicture(int[] rgba, int[] jpeg, int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onInit() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onTouchSurface(SurfaceViewBase surface, MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        
        byte[] yuv = new byte[3];
        surface.getYUVAt(yuv, x, y);
        
        mYUV = 0xff000000 | ((int)yuv[0] << 16) & 0xff0000 | ((int)yuv[1] << 8) & 0xff00 | (int)yuv[2]  & 0xff;
        
        return false;
    }

}
