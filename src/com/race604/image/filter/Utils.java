package com.race604.image.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    
    static {
        System.loadLibrary("jing_native");
    }

    public static native void yuv2rgb(byte yuv[], byte[] bgr);
    
    public static native void rgb2hsv(byte bgr[], byte[] hsv);
    
    
    /**
     * @param yuv
     *      yuv: Y, U, V 按照bit链接起来，在int的低三位
     * @return 
     *      RGBA
     */
    public static int yuv2rgba(int yuv) {
        
        int y = (yuv >> 16) & 0xff;
        int u = (yuv >> 8) & 0xff;
        int v = yuv& 0xff;
        
        int y1192 = 1192 * y;
        int r = (y1192 + 1634 * v);
        int g = (y1192 - 833 * v - 400 * u);
        int b = (y1192 + 2066 * u);
        
        if (r < 0) r = 0; else if (r > 262143) r = 262143;
        if (g < 0) g = 0; else if (g > 262143) g = 262143;
        if (b < 0) b = 0; else if (b > 262143) b = 262143;
        
        int rgba = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        
        return rgba;
        
    }
    
    public static int yuv2rgba(int y, int u, int v) {
        int y1192 = 1192 * y;
        int r = (y1192 + 1634 * v);
        int g = (y1192 - 833 * v - 400 * u);
        int b = (y1192 + 2066 * u);
        
        if (r < 0) r = 0; else if (r > 262143) r = 262143;
        if (g < 0) g = 0; else if (g > 262143) g = 262143;
        if (b < 0) b = 0; else if (b > 262143) b = 262143;
        
        int rgba = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        
        return rgba;
    }
    
    
    public static void rgb2yuv(int rgba, int yuv[]) {
        int r = (rgba >> 16) & 0xff;
        int g = (rgba >> 8) & 0xff;
        int b = rgba & 0xff;
        
        int y = (int)(0.299 * r + 0.587 * g + 0.114 * b);
        int u = (int)((b - y) * 0.492f); 
        int v = (int)((r - y) * 0.877f);
        
        yuv[0]= y;
        yuv[1]= u;
        yuv[2]= v;
    }
    
    public static File getImgDir() {
        File sdDir = Environment
          .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "JingCamera");
    }
    
    
    public static int getOrientationForExif(Context context) {
        
        float orientation = 0;
        float[] rotationMatrix = new float[9];
        if(SensorManager.getRotationMatrix(rotationMatrix, null, null, null)){
            float[] orientMatrix = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientMatrix);

            orientation = orientMatrix[0]*180/(float)Math.PI;
        }
        
        int degrees = ExifInterface.ORIENTATION_UNDEFINED;
        if (orientation > -45 && orientation <= 45) {
            degrees = ExifInterface.ORIENTATION_NORMAL;
        } else if (orientation > 45 && orientation <= 135) {
            degrees = ExifInterface.ORIENTATION_ROTATE_90;
        } else if (orientation > 135 || orientation < -135) {
            degrees = ExifInterface.ORIENTATION_ROTATE_180;
        } else {
            degrees = ExifInterface.ORIENTATION_ROTATE_270;
        }
        
//        int rotation = ((WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
//                .getRotation();
//        
//        switch (rotation) {
//        case Surface.ROTATION_0:
//            degrees = ExifInterface.ORIENTATION_NORMAL;
//            break;
//        case Surface.ROTATION_90:
//            degrees = ExifInterface.ORIENTATION_ROTATE_90;
//            break;
//        case Surface.ROTATION_180:
//            degrees = ExifInterface.ORIENTATION_ROTATE_180;
//            break;
//        case Surface.ROTATION_270:
//            degrees = ExifInterface.ORIENTATION_ROTATE_270;
//            break;
//        }

        return degrees;
    }
    
    public static void saveBitmapToFile(Context context, Bitmap bmp) {
        File pictureFileDir = getImgDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "JCAM_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            
            if (pictureFile.exists()) {
                ExifInterface exif = new ExifInterface(filename);
                int oritention = getOrientationForExif(context);
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(oritention));
                exif.saveAttributes();
            }
            
        } catch (Exception error) {
        }
    }
}
