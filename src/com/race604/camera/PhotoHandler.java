package com.race604.camera;

import com.race604.image.filter.IFilter;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Environment;

import java.io.File;

public class PhotoHandler implements PictureCallback {

	private static final String TAG = PhotoHandler.class.getName();
	
	private IFilter mFilter;
	
	public PhotoHandler(IFilter filter) {
		this.mFilter = filter;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
	    
	    Size picSize = camera.getParameters().getPictureSize();
	    
	    mFilter.onTakePicture(data, picSize.width, picSize.height);

//		File pictureFileDir = getDir();
//
//		if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
//
//			Log.d(TAG, "Can't create directory to save image.");
//			return;
//
//		}
//
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
//		String date = dateFormat.format(new Date());
//		String photoFile = "JCAM_" + date + ".jpg";
//
//		String filename = pictureFileDir.getPath() + File.separator + photoFile;
//
//		File pictureFile = new File(filename);
//
//		try {
//			FileOutputStream fos = new FileOutputStream(pictureFile);
//			fos.write(data);
//			fos.close();
//		} catch (Exception error) {
//			Log.d(TAG, "File" + filename + "not saved: "
//					+ error.getMessage());
//		}
	}

	private File getDir() {
		File sdDir = Environment
		  .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir, "JingCamera");
	}
}
