package com.race604.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.race604.image.filter.IFilter;

public class FilterSurfaceView extends SurfaceViewBase {

	private static final String TAG = FilterSurfaceView.class.getName();
	private int mFrameSize;
	private Bitmap mBitmap;
	private int[] mRGBA;
	private IFilter mFilter = null;

	public FilterSurfaceView(Context context) {
		super(context);
	}

	public FilterSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FilterSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setFilter(IFilter filter) {
		mFilter = filter;
	}

	@Override
	protected Bitmap processFrame(byte[] data) {

		int[] rgba = mRGBA;

//		CameraUtil.decodeYUV420SPGrayscale(rgba, data, getFrameWidth(),
//				getFrameHeight());
		
		if (mFilter != null) {
			mFilter.onPreview(rgba, data, getFrameWidth(), getFrameHeight());
			
		} else {
			CameraUtil.decodeYUV420SP(rgba, data, getFrameWidth(),
					getFrameHeight());
		}
		
		mBitmap.setPixels(rgba, 0/* offset */, getFrameWidth() /* stride */, 0, 0,
				getFrameWidth(), getFrameHeight());
		return mBitmap;
	}

	@Override
	protected void onPreviewStared(int previewWidtd, int previewHeight) {
		mFrameSize = previewWidtd * previewHeight;
		mRGBA = new int[mFrameSize];
		mBitmap = Bitmap.createBitmap(previewWidtd, previewHeight,
				Bitmap.Config.ARGB_8888);
	}

	@Override
	protected void onPreviewStopped() {
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		mRGBA = null;

	}
	
}
