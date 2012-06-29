package com.race604.camera;

import com.race604.image.filter.IFilter;
import com.race604.image.filter.LomoFilter;
import com.race604.image.filter.SingleColorFilter;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

public class CameraActivity extends Activity implements OnClickListener {
	private static final String TAG = CameraActivity.class.getName();

	private FilterSurfaceView mSvCameraView;
	private SurfaceHolder mSurfaceHolder;
	private Button mCaptureBtn;
	private CheckBox mAutofocusCk;
	
	private static final int MENU_FILER_SINGLE_COLOR = 11;
	private static final int MENU_FILER_LOMO = 12;
	

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	    menu.clear();
        MenuItem mi = null;

        mi = menu.add(Menu.NONE, MENU_FILER_SINGLE_COLOR, Menu.NONE, "单色");
        mi = menu.add(Menu.NONE, MENU_FILER_LOMO, Menu.NONE, "Lomo");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case MENU_FILER_SINGLE_COLOR: {
                IFilter filter = new SingleColorFilter();
                mJpegCallback.setFilter(filter);
                mSvCameraView.setFilter(filter);
                break;
            }
            case MENU_FILER_LOMO: {
                IFilter filter = new LomoFilter();
                mJpegCallback.setFilter(filter);
                mSvCameraView.setFilter(filter);
            }
            default:
                break;
        }
        
        return true;
    }

    private ShutterCallback mShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			// TODO
		}
	};
	
	private PhotoHandler mJpegCallback = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.camera_layout);

		mCaptureBtn = (Button) findViewById(R.id.btn_capture);
		mAutofocusCk = (CheckBox) findViewById(R.id.ck_auto_focus);
		mSvCameraView = (FilterSurfaceView) findViewById(R.id.sv_camera_preview);
		
		mJpegCallback = new PhotoHandler(this);

		IFilter filter = new SingleColorFilter();
		mJpegCallback.setFilter(filter);
        mSvCameraView.setFilter(filter);
		
		mCaptureBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_capture: {
			mSvCameraView.takePicture(mShutterCallback, null, mJpegCallback);
			break;
		}
		default:
			break;
		}

	}
}