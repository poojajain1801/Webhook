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
package com.comviva.mfs.hce.appserver.mapper.vts;

public enum RequestId {
    ENROLL_DEVICE("vts/clients",
            "Comviva-Vts-001"),

    ENROLL_PAN("vts/panEnrollments",
            "Comviva-Vts-002"),

    PROVISION_TOKEN_ENROLLMENT_ID("vts/panEnrollments/{vPanEnrollmentID}/provisionedTokens",
            "Comviva-Vts-003"),

    PROVISION_TOKEN_DATA("vts/provisionedTokens",
            "Comviva-Vts-005"),

    CONFIRM_PROVISIONING("vts/provisionedTokens/{vProvisionedTokenID}/confirmProvisioning",
            "Comviva-Vts-006"),

    AAM_REPLENISH("vts/provisionedTokens/{vProvisionedTokenID}/replenish",
            "Comviva-Vts-007"),

    AAM_CONFIRM_REPLENISHMENT("vts/provisionedTokens/${vProvisionedTokenID}/confirmReplenishment",
            "Comviva-Vts-008"),

    REPLENISH_ODA_DATA("vts/provisionedTokens/{vProvisionedTokenID}/replenishODA",
            "Comviva-Vts-009"),

    SUBMIT_IDV_STEP_UP_METHOD("vts/provisionedTokens/{vProvisionedTokenID}/stepUpOptions/method",
            "Comviva-Vts-010"),

    VALIDATE_OTP("vts/provisionedTokens/{vProvisionedTokenID}/stepUpOptions/validateOTP",
            "Comviva-Vts-011"),

    VALIDATE_AUTHENTICATION_CODE("vts/provisionedTokens/{vProvisionedTokenID}/stepUpOptions/validateAuthCode",
            "Comviva-Vts-012"),

    GET_STEP_UP_OPTIONS("vts/provisionedTokens/{vProvisionedTokenID}/stepUpOptions",
            "Comviva-Vts-013"),

    GET_CARD_METADATA("vts/panEnrollments/{vPanEnrollmentID}",
            "Comviva-Vts-014"),

    GET_PAYMENT_DATA_TOKEN_ID("vts/provisionedTokens/{vProvisionedTokenID}/paymentData{vProvisionedTokenID}/paymentData",
            "Comviva-Vts-015"),

    GET_TRANSACTION_HISTORY("/vts/paymentTxns",
            "Comviva-Vts-016"),

    GET_CONTENT("vts/cps/getContent/{guid}",
            "Comviva-Vts-017"),

    GET_TOKEN_STATUS("/vts/provisionedTokens/{vProvisionedTokenID}",
            "Comviva-Vts-018"),

    TLCM_SUSPEND_TOKEN("vts/provisionedTokens/{vProvisionedTokenID}/suspend",
            "Comviva-Vts-019"),

    TLCM_RESUME_TOKEN("vts/provisionedTokens/{vProvisionedTokenID}/resume",
            "Comviva-Vts-020"),

    TLCM_DELETE_TOKEN("vts/provisionedTokens/{vProvisionedTokenID}/delete",
            "Comviva-Vts-021"),

    GET_PAN_DATA("/vts/panData?apiKey=key",
            "Comviva-Vts-022"),

    CONFIRM_TOKEN_ASSOCIATION("/vts/provisionedTokens/{vProvisionedTokenID}/confirmAssociation",
            "Comviva-Vts-023"),

    STATUS_NOTIFICATIONS("",
            "Comviva-Vts-023");

    /**
     * This is suffix to be appended to VTS url for corresponding API.
     */
    private String resourcePath;

    /**
     * (Required) Unique ID for the API request <br/>
     * Format: String. Size: 1-36, [A-Z][a-z][0-9,-]
     */
    private String xRequestId;

    RequestId(String resourcePath, String xRequestId) {
        this.resourcePath = resourcePath;
        this.xRequestId = xRequestId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getXRequestId() {
        return xRequestId;
    }

}
