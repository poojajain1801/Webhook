/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p/>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p/>
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
 *  EnrollDevice Request
 * Created by amgoth madan on 5/16/2017.
 */
@Getter
@Setter
public class EnrollDeviceRequest {

       private String userId;
       private String schemeType;//ALL,MDES,VTS
       private String gcmRegistrationId;
       private String clientDeviceID;
       private MdesDeviceRequest mdes;
       private VtsDeviceRequest vts;

    public EnrollDeviceRequest(String userId, String schemeType, String gcmRegistrationId, String clientDeviceID, MdesDeviceRequest mdes, VtsDeviceRequest vts) {
        this.userId = userId;
        this.schemeType = schemeType;
        this.gcmRegistrationId = gcmRegistrationId;
        this.clientDeviceID = clientDeviceID;
        this.mdes = mdes;
        this.vts = vts;
    }

    public EnrollDeviceRequest() {
    }

}
