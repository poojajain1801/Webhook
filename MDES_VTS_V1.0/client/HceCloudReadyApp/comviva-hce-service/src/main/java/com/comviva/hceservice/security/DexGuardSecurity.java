package com.comviva.hceservice.security;

import android.content.Context;

import com.guardsquare.dexguard.runtime.detection.DebugDetector;
import com.guardsquare.dexguard.runtime.detection.EmulatorDetector;
import com.guardsquare.dexguard.runtime.detection.RootDetector;
import com.guardsquare.dexguard.runtime.detection.TamperDetector;

public class DexGuardSecurity implements SecurityInf {
    public final int OK = 1;
    public final int NOTOK = -1;

    private Context context;
    public static DexGuardSecurity instance;

    private DexGuardSecurity(Context context) {
        this.context = context;
    }

    public static DexGuardSecurity getInstance(Context context) {
        if(instance == null) {
            instance = new DexGuardSecurity(context);
        }
        return instance;
    }

    @Override
    public boolean isDebuggable() {
        // Let the DexGuard utility library detect whether the application
        // is debuggable. The return code equals OK if it is not.
        int isDebuggable = DebugDetector.isDebuggable(context, OK);

        // Let the DexGuard utility library detect whether the a debugger
        // is attached to the application. The return code is OK if not connected.
        int isDebuggerConnected = DebugDetector.isDebuggerConnected(OK);

        // Let the DexGuard utility library detect whether the app is
        // signed with a debug key. The return code equals OK if not so.
        int isSignedWithDebugKey = DebugDetector.isSignedWithDebugKey(context, OK);

        // Let the DexGuard utility library detect whether the app is
        // running in an emulator. The return code is OK if that is not the
        // case.
        int isRunningInEmulator = EmulatorDetector.isRunningInEmulator(context, OK);

        // Let the DexGuard utility library detect whether the app is
        // running on a rooted device. The return code is OK if not so.
        if ((isDebuggable != OK) || (isDebuggerConnected != OK)
                || (isSignedWithDebugKey != OK) || (isRunningInEmulator != OK)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDeviceRooted() {
        return RootDetector.isDeviceRooted(OK) == OK ? true : false;
    }

    @Override
    public boolean isApkTampered() {
        return TamperDetector.checkApk(context, OK) == NOTOK ? false : true;

    }

    @Override
    public DeviceStatus getDeviceStatus() {
        if(isDebuggable() || isDeviceRooted() || isApkTampered()) {
            return DeviceStatus.NOT_SAFE;
        }
        return DeviceStatus.SAFE;
    }
}
