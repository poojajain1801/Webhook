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

import lombok.Getter;
import lombok.Setter;

/**
 *  MdesDevice Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
public class MdesDeviceRequest {

    private DeviceInfoRequest deviceInfo;
    private String mobilePin;
    private String paymentAppId;
    private String paymentAppInstanceId;
    private String publicKeyFingerprint;
    private String deviceFingerprint;
    private String rgk;

    public MdesDeviceRequest(DeviceInfoRequest deviceInfo, String mobilePin, String paymentAppId,
                             String paymentAppInstanceId, String publicKeyFingerprint, String deviceFingerprint, String rgk) {

        this.deviceInfo=deviceInfo;
        this.mobilePin=mobilePin;
        this.paymentAppId=paymentAppId;
        this.paymentAppInstanceId=paymentAppInstanceId;
        this.publicKeyFingerprint=publicKeyFingerprint;
        this.deviceFingerprint = deviceFingerprint;
        this.rgk=rgk;
    }
    public MdesDeviceRequest() {
    }
}
