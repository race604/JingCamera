#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

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

JNIEXPORT void JNICALL Java_com_race604_image_filter_SingleColorFilter_SingleColor(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray yuv, jintArray bgra, jint yuvcolor)
{
    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	int frameSize = width * height;

	int U = (yuvcolor > 8) & 0xff - 128;
	int V = yuvcolor & 0xff - 128;

	for (int j = 0; j < height; j++) {
		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
		for (int i = 0; i < width; i+=2) {
			v = (0xff & _yuv[uvp++]) - 128;
			u = (0xff & _yuv[uvp++]) - 128;

			int dU = u - U;
			int dV = v - V;

			if (dU*dV + dV*dV > 50) {
				_yuv[uvp-1] = 128;
				_yuv[uvp-2] = 128;

			} 
		}
	}
    Mat myuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
    Mat mbgra(height, width, CV_8UC4, (unsigned char *)_bgra);

    //Please make attention about BGRA byte order
    //ARGB stored in java as int array becomes BGRA at native level
    cvtColor(myuv, mbgra, CV_YUV420sp2BGR, 4);

    env->ReleaseIntArrayElements(bgra, _bgra, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
}

}
