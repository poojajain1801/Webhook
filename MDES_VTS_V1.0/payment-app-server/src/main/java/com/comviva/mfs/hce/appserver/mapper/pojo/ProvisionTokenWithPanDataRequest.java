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
 * ProvisionTokenGivenPanEnrollmentId Request
 * Created by amgoth.madan on 4/28/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvisionTokenWithPanDataRequest {

    private String userId;
    private String activationCode;
    private String encryptionMetaData;
    private String clientAppID;
    private String clientWalletAccountID;
    private String ip4address;
    private String location;
    private String local;
    private String issuerAuthCode;
    private String emailAddressHash;
    private String emailAddress;
    private String protectionType;
    private String clientDeviceID;
    private String panSource;
    private String consumerEntryMode;
    private EncPaymentInstrument encPaymentInstrument;
    private String presentationType;
    private String accountType;
    private String encRiskDataInfo;
    private SsdData ssdData;
    private String channelSecurityContext;
    private String platformType;

    /*
    public ProvisionTokenWithPanDataRequest(String userId, String activationCode, String encryptionMetaData,String clientAppID,String clientWalletAccountID,
                                            String ip4address,String location,String local,String issuerAuthCode,String emailAddressHash,String emailAddress,
                                            String protectionType,String clientDeviceID,String panSource,String consumerEntryMode,EncPaymentInstrument encPaymentInstrument,
                                            String presentationType,String accountType,String encRiskDataInfo,SsdData ssdData,String channelSecurityContext,String platformType) {

        this.userId=userId;
        this.activationCode=activationCode;
        this.encryptionMetaData=encryptionMetaData;
        this.clientAppID=clientAppID;
        this.clientDeviceID=clientDeviceID;
        this.clientWalletAccountID=clientWalletAccountID;
        this.ip4address=ip4address;
        this.location=location;
        this.issuerAuthCode=issuerAuthCode;
        this.emailAddressHash=emailAddressHash;
        this.emailAddress=emailAddress;
        this.protectionType=protectionType;
        this.presentationType=presentationType;
        this.accountType=accountType;
        this.encRiskDataInfo=encRiskDataInfo;
        this.ssdData=ssdData;
        this.channelSecurityContext=channelSecurityContext;
        this.platformType=platformType;
        this.local=local;
        this.panSource=panSource;
        this.consumerEntryMode=consumerEntryMode;
        this.encPaymentInstrument=encPaymentInstrument;
    }

    public ProvisionTokenWithPanDataRequest() {
    } */
}