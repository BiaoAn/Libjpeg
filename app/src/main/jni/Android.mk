LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libjpeg_static_ndk
LOCAL_SRC_FILES := libjpeg_static_ndk/$(TARGET_ARCH_ABI)/libjpeg_static_ndk.a

include $(PREBUILT_STATIC_LIBRARY)

#include $(CLEAR_VARS)

#LOCAL_MODULE    := libjpeg
#LOCAL_SRC_FILES := libjpeg.so
#include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := libimagerar
LOCAL_SRC_FILES := libimagerar.c
#LOCAL_SHARED_LIBRARIES := libjpeg
LOCAL_STATIC_LIBRARIES := libjpeg_static_ndk
LOCAL_LDLIBS := -ljnigraphics
LOCAL_C_INCLUDES := $(LOCAL_PATH) \
                    $(LOCAL_PATH)/jpeg \
                    $(LOCAL_PATH)/jpeg/android   
LOCAL_CFLAGS := -std=c99

LOCAL_LDLIBS += -llog
include $(BUILD_SHARED_LIBRARY)
