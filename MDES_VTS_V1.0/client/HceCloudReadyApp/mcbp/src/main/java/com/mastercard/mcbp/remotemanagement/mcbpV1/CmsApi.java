/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package com.mastercard.mcbp.remotemanagement.mcbpV1;

/**
 * Class containing constants related to CMS.
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class CmsApi {

    /**
     * Version to append in activation proof.
     */
    public static final byte VERSION_CONTROL = (byte) 0x68;
    /**
     * Relative url to append with base url for activation request
     */
    static final String ACTIVATE_URI = "/api/1/0/activate";

    /**
     * Relative url to append with base url for authentication request
     */
    static final String AUTHENTICATE_URI = "/api/1/0/notification/authenticate";

    /**
     * Relative url to append with base url for activation proof request
     */
    static final String ACTIVATION_PROOF_URI = "/api/1/0/activation/proof";

    /**
     * Relative url to append with base url for mobile check request
     */
    static final String REQUEST_MOBILE_CHECK = "/api/1/0/requestMobileCheck";

    static final String ISSUER_IDENTIFIER_PARAM = "issuerIdentifier";
    static final String MOBILE_ID_PARAM = "mobileId";
    static final String CMS_MPA_ID_PARAM = "cmsMPAId";

    static final String ACTIVATION_PROOF_PARAM = "proofRequest";

    static final String DEVICE_INFO_OS_NAME_PARAM = "osName";
    static final String DEVICE_INFO_OS_VERSION_PARAM = "osVersion";
    static final String DEVICE_INFO_OS_FIRMWARE_BUILD_PARAM = "osFirmwarebuild";
    static final String DEVICE_INFO_DEVICE_MANUFACTURER_PARAM = "manufacturer";
    static final String DEVICE_INFO_MODEL_PARAM = "model";
    static final String DEVICE_INFO_PRODUCT_PARAM = "product";
    static final String DEVICE_INFO_OS_UNIQUE_IDENTIFIER_PARAM = "osUniqueIdentifier";
    static final String DEVICE_INFO_IMEI_PARAM = "imei";
    static final String DEVICE_INFO_MAC_ADDRESS_PARAM = "macAddress";
    static final String DEVICE_INFO_NFC_SUPPORT_PARAM = "nfcSupport";
    static final String DEVICE_INFO_SCREEN_SIZE_PARAM = "screenSize";
}