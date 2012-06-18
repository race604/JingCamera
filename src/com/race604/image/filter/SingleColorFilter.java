package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;
import com.race604.camera.SurfaceViewBase.OnTouchSurfaceListener;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class SingleColorFilter implements IFilter, OnTouchSurfaceListener {

	private Point mPoint = new Point(0, 0);
	
	@Override
	public void onPreview(int[] rgba, byte[] yuv, int width, int height) {
		SingleColor(width, height, yuv, rgba, mPoint.x, mPoint.y);
	}
	
	public native void SingleColor(int width, int height, byte yuv[], int[] rgba, int x, int y);

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
        
        mPoint = surface.getPointAt(x, y);
        
        return false;
    }

}
