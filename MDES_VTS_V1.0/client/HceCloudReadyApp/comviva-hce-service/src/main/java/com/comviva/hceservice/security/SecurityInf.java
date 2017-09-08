package com.comviva.hceservice.security;

/**
 * Provides methods to detect debugger, emulator, rooted or tampering state of the device and apk.
 */
public interface SecurityInf {
    /**
     * Detects whether device is in debug mode.
     * @return  <code>true </code>If device is in dubug mode/apk in debug mode/running on emulator <br>
     *     <code>false </code>otherwise
     */
    boolean isDebuggable();

    /**
     * Detects whether device is rooted or not.
     * @return <code>true </code>If device is rooted <br>
     *     <code>false </code>Device is not rooted
     */
    boolean isDeviceRooted();

    /**
     * Detects whether apk is tampered or not.
     * @return <code>true </code>If apk is tampered <br>
     *     <code>false </code>apk is not tampered
     */
    boolean isApkTampered();

    /**
     * Returns device that if there is any security voilation at present.
     * @return Currently device status
     */
    DeviceStatus getDeviceStatus();
}
