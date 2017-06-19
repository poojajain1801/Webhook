# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Softwares\AndriodSDK\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# This is a configuration file for ProGuard.

# http://proguard.sourceforge.net/index.html#manual/usage.html

# More complex applications, applets, servlets, libraries, etc., may contain
# classes that are serialized. Depending on the way in which they are used, they
# may require special attention

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

####################################################################################################
## BEGIN -- MP-SDK specific rules ##

-keep class com.mastercard.mcbp.card.profile.** { *; }
-keep class com.mastercard.mcbp.remotemanagement.mdes.** { *; }
-keep class com.mastercard.mcbp.remotemanagement.mcbpv1.** { *; }

## END -- MP-SDK specific rules ##

####################################################################################################