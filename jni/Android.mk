##########################################################################################

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libffmpegjni

LOCAL_SRC_FILES := ffmpeg_jni.c ffmpeg.c ffmpeg_opt.c ffmpeg_filter.c cmdutils.c

LOCAL_C_INCLUDES += $(JNI_H_INCLUDE) \
                    $(LOCAL_PATH)/include/compat \
                    $(LOCAL_PATH)/include/libavcodec \
                    $(LOCAL_PATH)/include/libavdevice \
                    $(LOCAL_PATH)/include/libavfilter \
                    $(LOCAL_PATH)/include/libavformat \
                    $(LOCAL_PATH)/include/libavresample \
                    $(LOCAL_PATH)/include/libavutil \
                    $(LOCAL_PATH)/include/libpostproc \
                    $(LOCAL_PATH)/include/libswresample \
                    $(LOCAL_PATH)/include/libswscale \
                    $(LOCAL_PATH)/include/ \
                    $(LOCAL_PATH)/

LOCAL_CFLAGS += -Wno-error=implicit-function-declaration -L$(LOCAL_PATH)/lib/libffmpeg.so

LOCAL_SHARED_LIBRARIES := libffmpeg

include $(BUILD_SHARED_LIBRARY)

##########################################################################################

include $(CLEAR_VARS)

LOCAL_PREBUILT_LIBS := libffmpeg:lib/libffmpeg.so

LOCAL_MODULE_TAGS := optional

##########################################################################################

include $(BUILD_MULTI_PREBUILT)

##########################################################################################