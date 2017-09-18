package com.comviva.hceservice.util;

import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Checks device locking mechanism.
 */
public class DeviceLockUtil {
    /**
     * @param context
     * @return <code>true</code> If pattern set<br>
     *     <code>false</code> if not (or if an issue when checking)
     */
    private static boolean isPatternSet(Context context) {
        ContentResolver cr = context.getContentResolver();
        try {
            int lockPatternEnable = Settings.Secure.getInt(cr, Settings.Secure.LOCK_PATTERN_ENABLED);
            return lockPatternEnable == 1;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    /**
     * <p>Checks to see if the lock screen is set up with either a PIN / PASS / PATTERN</p>
     * @return <code>true </code>if PIN, PASS or PATTERN set<br>
     *     <code>false </code>otherwise.
     */
    public static boolean checkLockingMech(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return isPatternSet(context);
        } else {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure();
        }
    }

}
