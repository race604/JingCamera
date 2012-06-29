package com.race604.image.filter;

import com.race604.camera.SurfaceViewBase.OnTouchSurfaceListener;

public interface IFilter extends OnTouchSurfaceListener {
	/**
	 * @param rgba
	 * 			Out put RGBA
	 * @param yuv
	 * 			Input raw data in TUV format
	 * @param width
	 * 			Image width
	 * @param height
	 * 			Image height
	 */
	public void onPreview(int[] rgba, byte[] yuv, int width, int height);
	public void onTakePicture(int[] data, int width, int height);
	public void onInit();
}
