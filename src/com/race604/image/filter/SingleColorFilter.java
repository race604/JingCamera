package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;
import com.race604.camera.SurfaceViewBase.OnTouchSurfaceListener;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class SingleColorFilter implements IFilter, OnTouchSurfaceListener {

	private byte[] mColor = new byte[3]; // color int YUV
	
	@Override
	public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
		SingleColor(width, height, yuv, rgba, mColor[0], mColor[1], mColor[2]);
	}
	
	public native void SingleColor(int width, int height, byte yuv[], int[] rgba, byte Y, byte U, byte V);

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
        
        surface.getYUVAt(mColor, x, y);
        
        return false;
    }

}
