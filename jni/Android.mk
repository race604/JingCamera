LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on

include ../../OpenCV/OpenCV-2.4.0-android-bin/OpenCV-2.4.0/share/OpenCV/OpenCV.mk

LOCAL_MODULE    := jing_native
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
