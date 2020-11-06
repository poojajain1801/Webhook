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
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * RegisterUserRequest.
 * Created by Amgoth.madan on 4/25/2017.
 */
@Getter
@Setter
public class RegisterUserRequest {

    @NotEmpty
    @Length(max = 60, min = 2)
    private String userId;
    private String clientDeviceID;
    private String imei;
    private String os_name;
    private String device_model;
    private String languageCode;


    public RegisterUserRequest(String userId, String clientDeviceID, String imei, String os_name, String device_model, String languageCode) {
        this.userId = userId;
        this.clientDeviceID = clientDeviceID;
        this.imei = imei;
        this.os_name = os_name;
        this.device_model = device_model;
        this.languageCode = languageCode;
    }

    public RegisterUserRequest() {
    }
}
