
package com.race604.camera;

import com.race604.image.filter.IFilter;
import com.race604.image.filter.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Environment;

import java.io.File;

public class PhotoHandler implements PictureCallback {

    private static final String TAG = PhotoHandler.class.getName();

    private IFilter mFilter;

    private Context mContext;
    
    private float mOritention;

    public PhotoHandler(Context context) {
        this.mContext = context;
    }

    public void setFilter(IFilter filter) {
        this.mFilter = filter;
    }
    
    public void setOritention(float o) {
    	this.mOritention = o;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        BitmapFactory.Options resample = new BitmapFactory.Options();
        resample.inSampleSize = 2;

        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, resample);
        data = null;

        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        int[] rgba = new int[bmpW * bmpH];
        bmp.getPixels(rgba, 0, bmpW, 0, 0, bmpW, bmpH);
        bmp.recycle();

        if (mFilter != null) {

            mFilter.onTakePicture(rgba, bmpW, bmpH);
        }
        
        bmp = Bitmap.createBitmap(rgba, bmpW, bmpH, Bitmap.Config.ARGB_8888);

        Utils.saveBitmapToFile(bmp, mOritention);
        bmp.recycle();
        System.gc();

        // File pictureFileDir = getDir();
        //
        // if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
        //
        // Log.d(TAG, "Can't create directory to save image.");
        // return;
        //
        // }
        //
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        // String date = dateFormat.format(new Date());
        // String photoFile = "JCAM_" + date + ".jpg";
        //
        // String filename = pictureFileDir.getPath() + File.separator +
        // photoFile;
        //
        // File pictureFile = new File(filename);
        //
        // try {
        // FileOutputStream fos = new FileOutputStream(pictureFile);
        // fos.write(data);
        // fos.close();
        // } catch (Exception error) {
        // Log.d(TAG, "File" + filename + "not saved: "
        // + error.getMessage());
        // }
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "JingCamera");
    }
}
