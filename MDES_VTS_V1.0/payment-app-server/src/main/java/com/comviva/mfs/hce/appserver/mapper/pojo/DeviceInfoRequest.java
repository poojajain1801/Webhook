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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  DeviceInfo Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoRequest {

    private String deviceName;
    private String formFactor;
    private String imei;
    private String id;
    private String msisdn;
    private String nfcCapable;
    private String osName;
    private String osVersion;
    private String serialNumber;
    private String storageTechnology;

   /* public DeviceInfoRequest(String deviceName, String formFactor, String imei, String id, String msisdn, String nfcCapable, String osName, String osVersion, String serialNumber, String storageTechnology) {
        this.deviceName = deviceName;
        this.formFactor = formFactor;
        this.imei = imei;
        this.id = id;
        this.msisdn = msisdn;
        this.nfcCapable = nfcCapable;
        this.osName = osName;
        this.osVersion = osVersion;
        this.serialNumber = serialNumber;
        this.storageTechnology = storageTechnology;
    }

    public DeviceInfoRequest() {
    }*/
}
