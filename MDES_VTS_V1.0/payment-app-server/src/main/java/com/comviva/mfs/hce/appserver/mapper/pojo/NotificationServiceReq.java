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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;


/**
 * NotificationServiceReq
 * */
@Component
public class NotificationServiceReq {

    @JsonProperty("date")
    private Long date;
    @JsonProperty("api")
    private String api;
    @JsonProperty("vProvisionedTokenID")
    private String vProvisionedTokenID;
    @JsonProperty("vPanEnrollmentID")
    private String vPanEnrollmentID;

    /**
     * NotificationServiceReq
     * @param api api
     * @param date date
     * @param vPanEnrollmentID vPanEnrollmentID
     * @param vProvisionedTokenID vProvisionedTokenId
     * */
    public NotificationServiceReq(Long date, String api, String vProvisionedTokenID,String vPanEnrollmentID) {
        this.date = date;
        this.api = api;
        this.vProvisionedTokenID = vProvisionedTokenID;
        this.vPanEnrollmentID = vPanEnrollmentID;
    }

    public NotificationServiceReq() {}

    public Long getDate() {
        return date;
    }

    public String getApi() {
        return api;
    }

    /**
     * getvProvisionedTokenID
     * @return string
     * */
    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setApi(String api) {
        this.api = api;
    }

    /**
     * servProvisionedTokenID
     * @param vProvisionedTokenID vProvisionedTokenId
     * */
    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    /**
     * getvPanEnrollmentID
     * */
    public String getvPanEnrollmentID() {
        return vPanEnrollmentID;
    }

    /**
     * setvPanEnrollmentID*/
    public void setvPanEnrollmentID(String vPanEnrollmentID) {
        this.vPanEnrollmentID = vPanEnrollmentID;
    }
}

