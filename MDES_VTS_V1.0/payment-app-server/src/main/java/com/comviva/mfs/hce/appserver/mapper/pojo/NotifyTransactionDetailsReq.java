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
 * Created by Tanmay.Patel on 5/9/2017.
 */
@Getter
@Setter
public class NotifyTransactionDetailsReq {
    private String responseHost;
    private String requestId;
    private String tokenUniqueReference;
    private String registrationCode2;
    private String tdsUrl;
    private String paymentAppInstanceId;

    public NotifyTransactionDetailsReq(String requestId ,String tokenUniqueReference, String registrationCode2, String tdsUrl, String paymentAppInstanceId) {
        this.tokenUniqueReference = tokenUniqueReference;
        this.registrationCode2 = registrationCode2;
        this.tdsUrl = tdsUrl;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.requestId = requestId;
    }
    public NotifyTransactionDetailsReq()
    {
        //Thsi is a Default Constructor.

    }
}
