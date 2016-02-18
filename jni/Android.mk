LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := tempConversion
LOCAL_SRC_FILES := tempConversion.cpp

include $(BUILD_SHARED_LIBRARY)