package com.comviva.hceservice.security;

import android.content.Context;
import com.guardsquare.dexguard.runtime.detection.DebugDetector;
import com.guardsquare.dexguard.runtime.detection.EmulatorDetector;
import com.guardsquare.dexguard.runtime.detection.RootDetector;
import com.guardsquare.dexguard.runtime.detection.TamperDetector;

public class DexGuardSecurity implements SecurityInf {
    private static final int  OK =1;


    private Context context;
    private static  DexGuardSecurity instance;

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
        int isDebuggable = DebugDetector.isDebuggable(context,OK);
        if(isDebuggable != OK)
        {
            return true;
        }

        int isDebuggableConnected = DebugDetector.isDebuggerConnected(OK);
        if(isDebuggableConnected != OK)
        {
            return true;
        }

        int isSignedWithDebugKey = DebugDetector.isSignedWithDebugKey(context,OK);
        if(isSignedWithDebugKey != OK)
        {
            return true;
        }

        int isRunningInEmulator = EmulatorDetector.isRunningInEmulator(context,OK);
        if(isRunningInEmulator != OK)
        {
            return true;
        }


        return false;
    }

    @Override
    public boolean isDeviceRooted() {
        int isDeviceRooted =  RootDetector.isDeviceRooted(context,OK);
        if(isDeviceRooted != OK)
        {
            return true;
        }else
        {
            return false;
        }
    }

    @Override
    public boolean isApkTampered() {
        int isTempered =  TamperDetector.checkApk(context, OK);
        if(isTempered != OK)
        {
            return true;
        }else
        {
            return false;
        }
    }

    @Override
    public DeviceStatus getDeviceStatus() {
        if((isDebuggable()) || isDeviceRooted() || isApkTampered()) {
            return DeviceStatus.NOT_SAFE;
        }
        return DeviceStatus.SAFE;
    }
}
