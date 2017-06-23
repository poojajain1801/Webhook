/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mastercard.mcbp.packages;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

/**
 * A helper to request a particular Android permission
 */
public enum PermissionHelper {
    INSTANCE;

    /**
     * Logging tag
     */
    private static final String TAG = PermissionHelper.class.getName();

    /**
     * Check that the application has access to a list of permissions. Any missing permissions
     * will be requested
     *
     * @param permissions     Collections of String Permissions as defined in Android Manifest class
     * @param callingActivity The activity that will handle onRequestPermissionsResult
     * @return True if the permission is already granted,
     * false if the permission is not already granted and a request has for the permission has been made
     */
    public boolean requestPermissions(String[] permissions, Activity callingActivity, int requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Store the permissions not currently granted
            ArrayList<String> permissionsNeedRequesting = new ArrayList<String>();
            for (String permission : permissions) {
                // Check each permission
                if (!checkPermission(permission, callingActivity)) {
                    permissionsNeedRequesting.add(permission);
                }
            }

            // Check if any permissions are currently not granted
            if (permissionsNeedRequesting.size() > 0) {
                // Request permissions not currently granted
                callingActivity.requestPermissions(permissionsNeedRequesting.toArray(new String[permissionsNeedRequesting.size()]), requestCode);

                return false;
            } else {
                // All permissions already granted.
                return true;
            }
        } else {
            // Permission management not supported on pre Android M devices
            return true;
        }
    }

    /**
     * Check that the application has access to a permission. If the permissions is not granted, it
     * will be requested
     *
     * @param permission      the permission to request
     * @param callingActivity the activity that will handle onRequestPermissionsResult
     * @return True if the permission is already granted,
     * false if the permission is not already granted and a request has for the permission has been made
     */
    public boolean requestPermissions(String permission, Activity callingActivity, int requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!checkPermission(permission, callingActivity)) {

                // Permission not already granted - request permission
                Log.d(TAG, "Permission " + permission + " not granted. Requesting.");

                String[] permissionArray = {permission};
                callingActivity
                        .requestPermissions(permissionArray, requestCode);
                return false;
            } else {
                Log.d(TAG, "Permission " + permission + " already granted.");
                // Permission already granted, call listener
                return true;
            }
        } else {
            // Permission management not supported on pre Android M devices
            return true;
        }
    }

    /**
     * Checks to see if the requested permission is already granted. Does not request the permission
     * if it is missing. Use requestPermission or requestPermissions to automatically acquire
     * missing permissions
     *
     * @param permission      the permission to check for
     * @param callingActivity the calling activity
     * @return true if the permission is already granted, false if it is not
     */
    public boolean checkPermission(String permission, Activity callingActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check to see if permission already granted.
            int permissionStatus = callingActivity.checkSelfPermission(permission);

            return permissionStatus == PackageManager.PERMISSION_GRANTED;
        } else {
            // Permission management not supported on pre Android M devices
            return true;
        }
    }
}
