package com.race604.image.filter;

public class Utils {
    
    
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
}
