#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include "imgproc/color.hpp"
#include "imgproc/filter.h"

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_com_race604_image_filter_FastFeatureDetector_FindFeatures(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray yuv, jintArray bgra)
{
    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

    Mat myuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
    Mat mbgra(height, width, CV_8UC4, (unsigned char *)_bgra);
    Mat mgray(height, width, CV_8UC1, (unsigned char *)_yuv);

    //Please make attention about BGRA byte order
    //ARGB stored in java as int array becomes BGRA at native level
    cvtColor(myuv, mbgra, CV_YUV420sp2BGR, 4);

    vector<KeyPoint> v;

    FastFeatureDetector detector(50);
    detector.detect(mgray, v);
    for( size_t i = 0; i < v.size(); i++ )
        circle(mbgra, Point(v[i].pt.x, v[i].pt.y), 10, Scalar(0,0,255,255));

    env->ReleaseIntArrayElements(bgra, _bgra, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
}

JNIEXPORT void JNICALL Java_com_race604_image_filter_SingleColorFilter_preview(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray yuv, jintArray bgra, jint H, jint T)
{
    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	//Mat myuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
	//Mat mbgra(height, width, CV_8UC4, (unsigned char *)_bgra);

    ////Please make attention about BGRA byte order
    ////ARGB stored in java as int array becomes BGRA at native level
    //cvtColor(myuv, mbgra, CV_YUV420sp2BGR, 4);
	//cvtColor(mbgra, mbgra, CV_BGR2HSV);
	struct YUV420sp2RGB yuv2rgb(4, 0);
	struct RGB2HSV_b rgb2hsv(4, 0, 180);

	int frameSize = width*height;

	unsigned char * srcY = (unsigned char *)_yuv;
	unsigned char * srcUV = srcY + frameSize;
	unsigned char * dst = (unsigned char *)_bgra;

	unsigned char tmp[4];

	for (int j=0; j<height; ++j){
		if ((j&1 == 1)) {
			srcUV -= width;
		}
		for (int i=0; i<width; ++i){
			yuv2rgb(srcY, srcUV, dst, 1);

			rgb2hsv(dst, tmp, 1);
			int h = tmp[0];
			if ((h > H - T && h < H + T) ||
					(h > H - T + 180) || (h < H + T - 180)) {
			} else {
				//unsigned char val = SATURATE_CAST_UCHAR(std::max(0, int(srcY[0]) - 16));
				unsigned char val = dst[0]*0.299f + dst[1]*0.587f + dst[2]*0.114f;

				dst[0] = val;
				dst[1] = val;
				dst[2] = val;
			}

			if ((i&1) == 1){
				srcUV += 2;
			}
			srcY++;

			dst += 4;
		}
	}

    env->ReleaseIntArrayElements(bgra, _bgra, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
}

JNIEXPORT void JNICALL Java_com_race604_image_filter_SingleColorFilter_taken(JNIEnv* env, jobject thiz, jint width, jint height, jintArray bgra, jint H, jint T)
{
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	struct RGB2HSV_b rgb2hsv(4, 0, 180);

	unsigned char * dst = (unsigned char *)_bgra;

	unsigned char tmp[4];

	for (int j=0; j<height; ++j){
		for (int i=0; i<width; ++i){
			rgb2hsv(dst, tmp, 1);
			int h = tmp[0];
			if ((h > H - T && h < H + T) ||
					(h > H - T + 180) || (h < H + T - 180)) {
			} else {
				//unsigned char val = SATURATE_CAST_UCHAR(std::max(0, int(srcY[0]) - 16));
				unsigned char val = dst[0]*0.299f + dst[1]*0.587f + dst[2]*0.114f;

				dst[0] = val;
				dst[1] = val;
				dst[2] = val;
			}
			dst += 4;
		}
	}

    env->ReleaseIntArrayElements(bgra, _bgra, 0);
}
JNIEXPORT void JNICALL Java_com_race604_image_filter_Utils_yuv2rgb(JNIEnv* env, jobject thiz, jbyteArray yuv, jbyteArray bgr)
{
    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jbyte* _bgr = env->GetByteArrayElements(bgr, 0);

	yuv2rgb((uchar*)_yuv, (uchar*)_bgr);

    env->ReleaseByteArrayElements(bgr, _bgr, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
}

JNIEXPORT void JNICALL Java_com_race604_image_filter_Utils_rgb2hsv(JNIEnv* env, jobject thiz, jbyteArray bgr, jbyteArray hsv)
{
    jbyte* _hsv  = env->GetByteArrayElements(hsv, 0);
    jbyte* _bgr = env->GetByteArrayElements(bgr, 0);

	rgb2hsv((uchar*)_hsv, (uchar*)_bgr);

    env->ReleaseByteArrayElements(bgr, _bgr, 0);
    env->ReleaseByteArrayElements(hsv, _hsv, 0);
}


JNIEXPORT void JNICALL Java_com_race604_image_filter_LomoFilter_preview(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray yuv, jintArray bgra)
{
    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	struct YUV420sp2RGB yuv2rgb(4, 0);

	int frameSize = width*height;

	unsigned char * srcY = (unsigned char *)_yuv;
	unsigned char * srcUV = srcY + frameSize;
	unsigned char * dst = (unsigned char *)_bgra;

	for(int j=0; j<height; ++j){
		yuv2rgb(srcY, srcUV, dst, width);
		srcY += width;
		if((j&1) == 1){
			srcUV += width;
		}
		dst += (width<<2);
	}
	dst = (unsigned char *)_bgra;
	llomo(dst, width, height, width>>1, height>>1);

    env->ReleaseIntArrayElements(bgra, _bgra, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
}

JNIEXPORT void JNICALL Java_com_race604_image_filter_LomoFilter_taken(JNIEnv* env, jobject thiz, jint width, jint height, jintArray bgra)
{
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	unsigned char * dst = (unsigned char *)_bgra;
	llomo(dst, width, height, width>>1, height>>1);

    env->ReleaseIntArrayElements(bgra, _bgra, 0);
}

}
