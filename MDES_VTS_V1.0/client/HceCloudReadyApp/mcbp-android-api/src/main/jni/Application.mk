APP_STL		:= c++_static
APP_CPPFLAGS += -fexceptions -fvisibility=hidden -frtti -pipe -fPIC -DCRYPTOPP_DISABLE_ASM=1 -std=c++11
#APP_CPPFLAGS += -fexceptions -Os -frtti -pipe -fPIC -DCRYPTOPP_DISABLE_ASM=1 -std=c++11
NDK_TOOLCHAIN_VERSION=4.9
APP_ABI := armeabi
APP_OPTIM := release
APP_PLATFORM := android-16
