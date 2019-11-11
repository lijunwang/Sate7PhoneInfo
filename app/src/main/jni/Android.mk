LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_MODULE    := libbase64tool
LOCAL_SRC_FILES := base64tool.cpp

include $(BUILD_SHARED_LIBRARY)