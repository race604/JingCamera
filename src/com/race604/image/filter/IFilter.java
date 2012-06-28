package com.race604.image.filter;

public interface IFilter {
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
	public void onTakePicture(byte[] data, int width, int height);
	public void onInit();
}
