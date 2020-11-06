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

import lombok.ToString;

/**
 * Created by tanmay.patel on 1/31/2017.
 */
@ToString
public class AddCardParm extends PayAppServerReq{

    private String tokenType;
    private String paymentAppInstanceId;
    private String paymentAppId;
    private CardInfo cardInfo;
    private DeviceInfoRequest deviceInfo;
    private String cardletId;
    private String consumerLanguage;
    private String tokenAuthenticationValue;
    private String decisioningData;


    public AddCardParm(String serviceId, String tokenType, String paymentAppInstanceId, String paymentAppId, CardInfo cardInfo,DeviceInfoRequest deviceInfo, String cardletId, String consumerLanguage, String tokenAuthenticationValue, String decisioningData) {
        super(serviceId);
        this.tokenType = tokenType;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.paymentAppId = paymentAppId;
        this.cardInfo = cardInfo;
        this.deviceInfo = deviceInfo;
        this.cardletId = cardletId;
        this.consumerLanguage = consumerLanguage;
        this.tokenAuthenticationValue = tokenAuthenticationValue;
        this.decisioningData = decisioningData;
    }


    public AddCardParm() {
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public String getPaymentAppId() {
        return paymentAppId;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public String getCardletId() {
        return cardletId;
    }

    public String getConsumerLanguage() {
        return consumerLanguage;
    }

    public String getTokenAuthenticationValue() {
        return tokenAuthenticationValue;
    }

    public String getDecisioningData() {
        return decisioningData;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public void setCardletId(String cardletId) {
        this.cardletId = cardletId;
    }

    public void setConsumerLanguage(String consumerLanguage) {
        this.consumerLanguage = consumerLanguage;
    }

    public void setTokenAuthenticationValue(String tokenAuthenticationValue) {
        this.tokenAuthenticationValue = tokenAuthenticationValue;
    }

    public void setDecisioningData(String decisioningData) {
        this.decisioningData = decisioningData;
    }

    public DeviceInfoRequest getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfoRequest deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
