package com.race604.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public abstract class SurfaceViewBase extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {
	private static final String TAG = "Sample::SurfaceView";

	private Camera mCamera;
	private int mCurrentCameraId;
	private SurfaceHolder mHolder;
	private int mFrameWidth;
	private int mFrameHeight;
	private byte[] mFrame;
	private boolean mThreadRun;
	private byte[] mBuffer;
	private Matrix mMatrix;
	
	private HashSet<OnTouchSurfaceListener> mTouchListeners = new HashSet<OnTouchSurfaceListener>();

	public SurfaceViewBase(Context context) {
		super(context);
		init();
	}

	public SurfaceViewBase(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SurfaceViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mMatrix = new Matrix();
	}
	
	
	public int getFrameWidth() {
		return mFrameWidth;
	}

	public int getFrameHeight() {
		return mFrameHeight;
	}

	public void setPreview() throws IOException {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			mCamera.setPreviewTexture(new SurfaceTexture(10));
		else
			mCamera.setPreviewDisplay(null);
	}

	public void surfaceChanged(SurfaceHolder _holder, int format, int width,
			int height) {
		Log.i(TAG, "surfaceCreated");
		if (mCamera != null) {
			Camera.Parameters params = mCamera.getParameters();
			mFrameWidth = width;
			mFrameHeight = height;

			List<Size> supportedPreviewSizes = params
					.getSupportedPreviewSizes();
			Size optimalPreviewSize = CameraUtil.getOptimalPreviewSize(
					supportedPreviewSizes, width, height);

			mFrameWidth = optimalPreviewSize.width;
			mFrameHeight = optimalPreviewSize.height;
			
			params.setPreviewSize(getFrameWidth(), getFrameHeight());

			List<String> FocusModes = params.getSupportedFocusModes();
			if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            {
            	params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			
			mCamera.setParameters(params);
			
			mMatrix = new Matrix();
            float scale = (float)getWidth() / (float)getFrameWidth();
            mMatrix.setScale(scale, scale);

			/* Now allocate the buffer */
			params = mCamera.getParameters();
			int size = params.getPreviewSize().width
					* params.getPreviewSize().height;
			size = size
					* ImageFormat.getBitsPerPixel(params.getPreviewFormat())
					/ 8;
			mBuffer = new byte[size];
			/* The buffer where the current frame will be coppied */
			mFrame = new byte[size];
			mCamera.addCallbackBuffer(mBuffer);

			try {
				setPreview();
			} catch (IOException e) {
				Log.e(TAG,
						"mCamera.setPreviewDisplay/setPreviewTexture fails: "
								+ e);
			}

			/*
			 * Notify that the preview is about to be started and deliver
			 * preview size
			 */
			onPreviewStared(params.getPreviewSize().width,
					params.getPreviewSize().height);

			/* Now we can start a preview */
			mCamera.startPreview();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");
		mCurrentCameraId = CameraUtil
				.getCameraId(CameraInfo.CAMERA_FACING_BACK);
		mCamera = Camera.open(mCurrentCameraId);

		mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
			public void onPreviewFrame(byte[] data, Camera camera) {
				synchronized (SurfaceViewBase.this) {
					System.arraycopy(data, 0, mFrame, 0, data.length);
					SurfaceViewBase.this.notify();
				}
				camera.addCallbackBuffer(mBuffer);
			}
		});

		(new Thread(this)).start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		mThreadRun = false;
		if (mCamera != null) {
			synchronized (this) {
				mCamera.stopPreview();
				mCamera.setPreviewCallback(null);
				mCamera.release();
				mCamera = null;
			}
		}
		onPreviewStopped();
	}

	public void takePicture(final ShutterCallback shutter,
			final PictureCallback raw, final PictureCallback jpeg) {
		mCamera.autoFocus(new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				mCamera.takePicture(shutter, raw, jpeg);
			}
		});
	}
	
	public void addOnTouchListener(OnTouchSurfaceListener l) {
	    if(mTouchListeners.contains(l)) {
	        return;
	    }
	    mTouchListeners.add(l);
	}
	
	public void removeOnTouchListener(OnTouchListener l) {
	    mTouchListeners.remove(l);
	}
	
	public void getYUVAt(byte[] yuv, int x, int y) {
	    
	    float[] f = new float[9];
	    mMatrix.getValues(f);

	    float scale = f[Matrix.MSCALE_X];
	    
	    x /= scale;
        y /= scale;
	    
	    if(x < 0 || x > getFrameWidth()
	            || y < 0 || y > getFrameHeight()) {
	        yuv[0] = yuv[1] = yuv[2] = -1;
	    }
	    
	    final int frameSize = getFrameWidth() * getFrameHeight();
	    int uvp = frameSize + (y >> 1) * getFrameWidth() + (x & ~1);
	    yuv[0] = mFrame[y*getWidth() + x];
	    yuv[1] = mFrame[uvp];
	    yuv[2] = mFrame[uvp+1];
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        
	    for(OnTouchSurfaceListener l : mTouchListeners) {
	        if (l.onTouchSurface(this, event) ) {
	            return true;
	        }
	    }
	    
        return super.onTouchEvent(event);
    }

    /*
	 * The bitmap returned by this method shall be owned by the child and
	 * released in onPreviewStopped()
	 */
	protected abstract Bitmap processFrame(byte[] data);

	/**
	 * This method is called when the preview process is beeing started. It is
	 * called before the first frame delivered and processFrame is called It is
	 * called with the width and height parameters of the preview process. It
	 * can be used to prepare the data needed during the frame processing.
	 * 
	 * @param previewWidth
	 *            - the width of the preview frames that will be delivered via
	 *            processFrame
	 * @param previewHeight
	 *            - the height of the preview frames that will be delivered via
	 *            processFrame
	 */
	protected abstract void onPreviewStared(int previewWidtd, int previewHeight);

	/**
	 * This method is called when preview is stopped. When this method is called
	 * the preview stopped and all the processing of frames already completed.
	 * If the Bitmap object returned via processFrame is cached - it is a good
	 * time to recycle it. Any other resourcses used during the preview can be
	 * released.
	 */
	protected abstract void onPreviewStopped();

	public void run() {
		mThreadRun = true;
		Log.i(TAG, "Starting processing thread");
		while (mThreadRun) {
			Bitmap bmp = null;

			synchronized (this) {
				try {
					this.wait();
					bmp = processFrame(mFrame);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (bmp != null) {
				Canvas canvas = mHolder.lockCanvas();
				if (canvas != null) {
//					Matrix matrix = new Matrix();
//					float scale = (float)canvas.getWidth() / (float)getFrameWidth();
//					matrix.setScale(scale, scale);
					canvas.drawBitmap(bmp,mMatrix, null);
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	public static interface OnTouchSurfaceListener{
	    public boolean onTouchSurface(SurfaceViewBase surfaceView, MotionEvent envent);
	}
}