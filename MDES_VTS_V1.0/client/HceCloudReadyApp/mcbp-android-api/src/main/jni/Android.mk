LOCAL_PATH      := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE  	:= utils
LOCAL_CFLAGS    := -Werror
LOCAL_SRC_FILES := $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./src/log/*.cpp)) $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./src/utils/*.cpp)) $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./src/utils/emvco/*.cpp))
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/./src/
LOCAL_C_INCLUDES := $(LOCAL_PATH)/./src/
LOCAL_EXPORT_CFLAGS := -D__ANDROID__
LOCAL_EXPORT_LDLIBS := -llog
LOCAL_STATIC_LIBRARIES := cryptopp562
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := core
LOCAL_CFLAGS    := -Wno-overflow
LOCAL_SRC_FILES := $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./src/core/*.cpp)) $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./src/core/mpp/*.cpp)) $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./src/core/mcm/*.cpp)) $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./src/core/mobile_kernel/*.cpp))
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/./src
LOCAL_EXPORT_CFLAGS := -D__ANDROID__
LOCAL_STATIC_LIBRARIES := utils
include $(BUILD_STATIC_LIBRARY)

#Build Cryptopp library
include $(LOCAL_PATH)/cryptopp562/Android.mk

include $(CLEAR_VARS)
LOCAL_MODULE    := mcbpcore-jni
LOCAL_CFLAGS    := -Werror
LOCAL_SRC_FILES := $(LOCAL_PATH)/./src/wrappers/unit_test_wrapper.cpp $(LOCAL_PATH)/./src/wrappers/android_wrapper.cpp $(LOCAL_PATH)/./src/wrappers/android_wrapper_utils.cpp
LOCAL_EXPORT_CFLAGS := -D__ANDROID__
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/./src
LOCAL_STATIC_LIBRARIES := core
LOCAL_LDLIBS += -latomic
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := mcbpcryptoservice-jni
LOCAL_CFLAGS    := -Werror
LOCAL_SRC_FILES := $(LOCAL_PATH)/./src/wrappers/crypto_service_wrapper.cpp $(LOCAL_PATH)/./src/wrappers/android_wrapper_utils.cpp $(LOCAL_PATH)/./src/cryptoservice/crypto_functions.cpp $(LOCAL_PATH)/./src/cryptoservice/mobile_keys.cpp
LOCAL_EXPORT_CFLAGS := -D__ANDROID__
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/./src
LOCAL_STATIC_LIBRARIES := utils
LOCAL_LDLIBS += -latomic
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := unittest
LOCAL_CFLAGS := -Werror
LOCAL_EXPORT_CFLAGS := -D__ANDROID__
LOCAL_SRC_FILES := $(subst $(LOCAL_PATH)/./,,$(wildcard $(LOCAL_PATH)/./unit_test/*.cpp))
LOCAL_EXPORT_C_INCLUDES :=  $(LOCAL_PATH)/./src/
LOCAL_C_INCLUDES := $(LOCAL_PATH)/./src/
LOCAL_C_INCLUDES += $(LOCAL_PATH)/./unit_test/
LOCAL_STATIC_LIBRARIES := googletest_main
LOCAL_STATIC_LIBRARIES += cryptopp562
LOCAL_SHARED_LIBRARIES := mcbpcore-jni 
LOCAL_SHARED_LIBRARIES += mcbpcryptoservice-jni
include $(BUILD_EXECUTABLE)

$(call import-module,third_party/googletest)

