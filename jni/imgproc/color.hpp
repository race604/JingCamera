/*
 * =====================================================================================
 *
 *       Filename:  color.hpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  06/27/2012 05:35:19 PM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  YOUR NAME (), 
 *   Organization:  
 *
 * =====================================================================================
 */

#include <limits>

//#define SATURATE_CAST_INT(v) ((v) >  INT_MAX ? INT_MAX : ((v) < INT_MIN ? INT_MIN : (int)(v)))
//#define SATURATE_CAST_UCHAR(v) ((v) >  UCHAR_MAX ? UCHAR_MAX : ((v) < 0 ? 0 : (uchar)(v)))

#define CALC_MIN(a,b) ((a) < (b) ? (a) : (b))
#define CALC_MAX(a,b) ((a) > (b) ? (a) : (b))


inline uchar SATURATE_CAST_UCHAR(int v) {
	if(v > 255) 
		return 255;
	else if(v < 0)
		return 0;
	return (uchar)v;
}
 
inline int SATURATE_CAST_INT(float v) {
	if(v > INT_MAX) 
		return INT_MAX;
	else if(v < INT_MIN)
		return INT_MIN;
	else
		return (int)v;
}
 
////////////////////////////////////// RGB <-> HSV ///////////////////////////////////////


struct RGB2HSV_b
{
    typedef uchar channel_type;
    
    RGB2HSV_b(int _srccn, int _blueIdx, int _hrange)
    : srccn(_srccn), blueIdx(_blueIdx), hrange(_hrange)
    {
        CV_Assert( hrange == 180 || hrange == 256 );
    }
    
    void operator()(const uchar* src, uchar* dst, int n) const
    {
        int i, bidx = blueIdx, scn = srccn;
        const int hsv_shift = 12;
        
        static int sdiv_table[256];
        static int hdiv_table180[256];
        static int hdiv_table256[256];
        static volatile bool initialized = false;
        
        int hr = hrange;
        const int* hdiv_table = hr == 180 ? hdiv_table180 : hdiv_table256;
        n *= 3;
        
        if( !initialized )
        {
            sdiv_table[0] = hdiv_table180[0] = hdiv_table256[0] = 0;
            for( i = 1; i < 256; i++ )
            {
                sdiv_table[i] = SATURATE_CAST_INT((255 << hsv_shift)/(1.*i));
                hdiv_table180[i] = SATURATE_CAST_INT((180 << hsv_shift)/(6.*i));
                hdiv_table256[i] = SATURATE_CAST_INT((256 << hsv_shift)/(6.*i));
            }
            initialized = true;
        }
        
        for( i = 0; i < n; i += 3, src += scn )
        {
            int b = src[bidx], g = src[1], r = src[bidx^2];
            int h, s, v = b;
            int vmin = b, diff;
            int vr, vg;
            
            v = CALC_MAX( v, g );
            v = CALC_MAX( v, r );
            vmin = CALC_MIN( vmin, g );
            vmin = CALC_MIN( vmin, r );
            
            diff = v - vmin;
            vr = v == r ? -1 : 0;
            vg = v == g ? -1 : 0;
            
            s = (diff * sdiv_table[v] + (1 << (hsv_shift-1))) >> hsv_shift;
            h = (vr & (g - b)) +
                (~vr & ((vg & (b - r + 2 * diff)) + ((~vg) & (r - g + 4 * diff))));
            h = (h * hdiv_table[diff] + (1 << (hsv_shift-1))) >> hsv_shift;
            h += h < 0 ? hr : 0;
            
            dst[i] = SATURATE_CAST_UCHAR(h);
            dst[i+1] = (uchar)s;
            dst[i+2] = (uchar)v;
        }
    }
                 
    int srccn, blueIdx, hrange;
};    

void rgb2hsv(uchar* bgr, uchar* hsv){
	struct RGB2HSV_b rgb2hsv(3, 0, 180);

	rgb2hsv(bgr, hsv, 1);
}

////////////////////////////////////// YUV420sp <-> RGBA ///////////////////////////////////////
struct YUV420sp2RGB
{
	int bIdx;
	int dstcn;

    YUV420sp2RGB(int _dcn, int _bIdx)
        : bIdx(_bIdx), dstcn(_dcn) {}

    void operator()(const uchar* srcY, const uchar* srcUV, uchar* dst, int n) const
    {
        //R = 1.164(Y - 16) + 1.596(V - 128)
        //G = 1.164(Y - 16) - 0.813(V - 128) - 0.391(U - 128)
        //B = 1.164(Y - 16)                  + 2.018(U - 128)

        //R = (1220542(Y - 16) + 1673527(V - 128)                  + (1 << 19)) >> 20
        //G = (1220542(Y - 16) - 852492(V - 128) - 409993(U - 128) + (1 << 19)) >> 20
        //B = (1220542(Y - 16)                  + 2116026(U - 128) + (1 << 19)) >> 20

        int i, bidx = bIdx, dcn = dstcn, u, v;
        
        for( i = 0; i < n; i++, dst += dcn, srcY++ )
        {
            int y = std::max(0, int(srcY[0]) - 16);
			if ((i&1) == 0) {
				u = int(srcUV[0]) - 128;
				v = int(srcUV[1]) - 128;
				srcUV += 2;
			}
			int y20 = 1220542 * y;
			int r = (y20 + 1673527 * v + (1 << 19));
			int g = (y20 - 852492 * v - 409993 * u + (1 << 19));
			int b = (y20 + 2116026 * u + (1 << 19));

			dst[bIdx] = SATURATE_CAST_UCHAR(r >> 20);
			dst[1] = SATURATE_CAST_UCHAR(g >> 20);
			dst[bIdx^2] = SATURATE_CAST_UCHAR(b >> 20);
			dst[3] = 0xff;
			
			//int val = y;
			//if (val < 0) val = 0; else if (val > 255) val = 255;
			
			//dst[0] = val;
			//dst[1] = val;
			//dst[2] = val;
			//dst[3] = 0xff;
        }
    }
};

void yuv2rgb(uchar* yuv, uchar* bgr){
	struct YUV420sp2RGB yuv2rgb(3, 0);

	yuv2rgb(yuv, yuv+1, bgr, 1);
}
