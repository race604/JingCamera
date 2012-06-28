package com.race604.image.filter;

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
    public void onTakePicture(byte[] data, int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onInit() {
        // TODO Auto-generated method stub
        
    }

}
