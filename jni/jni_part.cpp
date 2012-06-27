#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include "imgproc/color.hpp"

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

//JNIEXPORT void JNICALL Java_com_race604_image_filter_SingleColorFilter_SingleColor(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray yuv, jintArray bgra, jbyte Y, jbyte U, jbyte V)
//{
    //jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    //jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	//int frameSize = width * height;

	//int uu = (0xff & U);
	//int vv = (0xff & V);

	//for (int j = 0; j < height; j+=2) {
		//int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
		//for (int i = 0; i < width; i+=2) {
			//u = (0xff & _yuv[uvp++]);
			//v = (0xff & _yuv[uvp++]);

			//int dU = u - uu;
			//int dV = v - vv;

			//if (dU*dU + dV*dV > 255) {
				//_yuv[uvp-1] = 128;
				//_yuv[uvp-2] = 128;

			//} 
		//}
	//}
	//Mat myuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
	//Mat mbgra(height, width, CV_8UC4, (unsigned char *)_bgra);
	
	

    ////Please make attention about BGRA byte order
    ////ARGB stored in java as int array becomes BGRA at native level
    //cvtColor(myuv, mbgra, CV_YUV420sp2BGR, 4);
    ////cvtColor(mbgra, mbgra, CV_BGR2HSV);
    ////cvtColor(mbgra, mbgra, CV_HSV2BGR);

    //env->ReleaseIntArrayElements(bgra, _bgra, 0);
    //env->ReleaseByteArrayElements(yuv, _yuv, 0);
//}

JNIEXPORT void JNICALL Java_com_race604_image_filter_SingleColorFilter_SingleColor(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray yuv, jintArray bgra, jbyte Y, jbyte U, jbyte V)
{
    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	Mat myuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
	Mat mbgra(height, width, CV_8UC4, (unsigned char *)_bgra);

    //Please make attention about BGRA byte order
    //ARGB stored in java as int array becomes BGRA at native level
    cvtColor(myuv, mbgra, CV_YUV420sp2BGR, 4);
	cvtColor(mbgra, mbgra, CV_BGR2HSV);
	struct RGB2HSV_b cvt(4, 0, 180);

	int channels = mbgra.channels();
	int nRows = mbgra.rows * channels;
	int nCols = mbgra.cols;
	if (I.isContinuous())
	{
		nCols *= nRows;
		nRows = 1;
	}
	int i,j;
	uchar* p;
	uchar hsv[3];
	for( i = 0; i < nRows; ++i)
	{
		p = I.ptr<uchar>(i);
		for ( j = 0; j < nCols; ++j)
		{
			p[j] = table[p[j]];
		}
	}


    cvtColor(mbgra, mbgra, CV_HSV2BGR);



    env->ReleaseIntArrayElements(bgra, _bgra, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
}

}
