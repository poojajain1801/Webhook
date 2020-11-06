/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.mapper.pojo;

import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Object to contain all device registration inputs.
 * Created by tarkeshwar.v on 1/10/2017.
 */
@Getter
@Setter
public class RegDeviceParam {
    /* Information entered by the user */
    private String userId;
    private String activationCode;
    private String mobilePin;

    // Information extracted by MPA sdk
    private String paymentAppId;
    private String paymentAppInstanceId;
    private String publicKeyFingerprint;
    private String rgk;
    private String gcmRegistrationId;
    private DeviceInfo deviceInfo;
    private DeviceInitParams deviceInitParams;

    public RegDeviceParam(String userId,
                          String activationCode,
                          String mobilePin,
                          String paymentAppId,
                          String paymentAppInstanceId,
                          String publicKeyFingerprint,
                          String rgk,
                          String gcmRegistrationId,
                          DeviceInfo deviceInfo,
                          DeviceInitParams deviceInitParams) {
        this.userId = userId;
        this.activationCode = activationCode;
        this.mobilePin = mobilePin;
        this.paymentAppId = paymentAppId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.publicKeyFingerprint = publicKeyFingerprint;
        this.rgk = rgk;
        this.gcmRegistrationId = gcmRegistrationId;
        this.deviceInfo = deviceInfo;
        this.deviceInitParams = deviceInitParams;
    }

    public RegDeviceParam() {
    }
}
