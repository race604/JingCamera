package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase;
import com.race604.camera.SurfaceViewBase.OnTouchSurfaceListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

public class SingleColorFilter implements IFilter, OnTouchSurfaceListener {

	private byte[] mColor = new byte[3];
	
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
    public void onTakePicture(byte[] data, int width, int height) {
    	
        BitmapFactory.Options resample = new BitmapFactory.Options();
        resample.inSampleSize = 2;
        
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, resample);
        data = null;
        
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        int[] rgba = new int[bmpW*bmpH];
        bmp.getPixels(rgba, 0, bmpW, 0, 0, bmpW, bmpH);
        bmp.recycle();
        
        taken(bmpW, bmpH, rgba, mColor[0], (int) (5*1.5));
        
        bmp = Bitmap.createBitmap(rgba, bmpW, bmpH, Bitmap.Config.ARGB_8888);
        
        Utils.saveBitmapToFile(bmp);
        bmp.recycle();
        System.gc();
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
        Utils.yuv2rgb(mColor, mColor);
        Utils.rgb2hsv(mColor, mColor);
        
        return false;
    }

}
