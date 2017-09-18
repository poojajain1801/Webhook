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

package com.mastercard.mcbp.core;

import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import com.mastercard.mcbp.businesslogic.MobileDeviceInfo;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Android Mobile Device information
 */
public class AndroidMobileDeviceInfo extends MobileDeviceInfo {

    private static final String DEVICE_TYPE = "ANDROID";

    /**
     * Default constructor
     */
    public AndroidMobileDeviceInfo(Context context) {
        String deviceId = Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();

        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();

        setOsName(DEVICE_TYPE);
        Point outSize = getDeviceScreenSize(context);
        setScreenSize(String.valueOf(outSize.x) + "X" + String.valueOf(outSize.y));
        setOsVersion(Build.VERSION.RELEASE);
        setOsFirmwareBuild(Build.VERSION.INCREMENTAL);
        setManufacturer(Build.MANUFACTURER);
        setModel(Build.MODEL);
        setProduct(Build.PRODUCT);
        setImei(deviceId);
        setNfcSupport((adapter == null ? "false" : "true"));
        setMacAddress(wInfo.getMacAddress());
        setOsUniqueIdentifier(Build.FINGERPRINT);
    }

    /**
     * Get Device Screen Size
     *
     * @param context Context
     * @return Point
     */
    private Point getDeviceScreenSize(Context context) {
        Point outSize = new Point();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        // API level greater than 17
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(outSize);
        } else {
            try {
                Method getWidthMethod = Display.class.getMethod("getRawWidth");
                Method getHeightMethod = Display.class.getMethod("getRawHeight");
                int width = (Integer) getWidthMethod.invoke(display);
                int height = (Integer) getHeightMethod.invoke(display);
                outSize.set(width, height);
            } catch (Exception e) {
                display.getSize(outSize);
            }
        }
        return outSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray calculateDeviceFingerPrint() throws McbpCryptoException {
        int macAddressLength = (getMacAddress() == null ? 0 : getMacAddress().length());
        int totalLength = getOsName().length() + getOsVersion().length()
                          + getOsFirmwareBuild().length()
                          + getManufacturer().length() + getModel().length()
                          + getProduct().length() + getOsUniqueIdentifier().length()
                          + getImei().length() + macAddressLength
                          + getNfcSupport().length();

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[totalLength]);
        byteBuffer.put(getOsName().getBytes());
        byteBuffer.put(getOsVersion().getBytes());
        byteBuffer.put(getOsFirmwareBuild().getBytes());
        byteBuffer.put(getManufacturer().getBytes());
        byteBuffer.put(getModel().getBytes());
        byteBuffer.put(getProduct().getBytes());
        byteBuffer.put(getOsUniqueIdentifier().getBytes());
        byteBuffer.put(getImei().getBytes());
        if (getMacAddress() != null) {
            byteBuffer.put(getMacAddress().getBytes());
        }
        byteBuffer.put(getNfcSupport().getBytes());
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new McbpCryptoException(e.getMessage());
        }
        byte[] deviceFingerprint = byteBuffer.array();
        byte[] digest = messageDigest.digest(deviceFingerprint);
        return ByteArray.of(digest);
    }
}
