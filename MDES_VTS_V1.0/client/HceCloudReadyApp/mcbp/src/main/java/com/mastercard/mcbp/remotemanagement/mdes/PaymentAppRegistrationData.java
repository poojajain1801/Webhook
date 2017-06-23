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

package com.mastercard.mcbp.remotemanagement.mdes;

import com.mastercard.mcbp.utils.BuildInfo;

import flexjson.JSON;

public class PaymentAppRegistrationData {

    @JSON(name = "registrationCode")
    private String registrationCode;

    /**
     * Deprecated Since MDES API specificationV1.0.3 release July, 2015.
     */
    @Deprecated
    @JSON(name = "publicKey")
    private String publicKey;

    @JSON(name = "pkCertificateUrl")
    private String pkCertificateUrl;

    /**
     * Default constructor (called by FlexJson when a new object is created)
     */
    @SuppressWarnings("unused")  // Used by FlexJson
    public PaymentAppRegistrationData() {
        // Intentionally no-op
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * A one-time registration code that authorizes the Mobile
     * Payment App to registerToCmsD itself with the Credentials Management
     * (Dedicated).Max Length: 64
     *
     * @param registrationCode The one time registration code
     *
     */
    @SuppressWarnings("unused")  // Used by FlexJson
    public void setRegistrationCode(final String registrationCode) {
        this.registrationCode = registrationCode;
    }

    /**
     * Deprecated Since MDES API specificationV1.0.3 release July, 2015.
     * @return Public key.
     */
    @Deprecated
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * @param publicKey
     * The MDES public key used to encrypt the
     * randomly-generated key to be provided by the Mobile Payment App
     * during registration.Max Length: 256
     * Deprecated Since MDES API specificationV1.0.3 release July, 2015.
     */
    @Deprecated
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Get URL to fetch the public key certificate from.
     * @return URL to the public key certificate.
     */
    public String getPkCertificateUrl() {
        return pkCertificateUrl;
    }

    /**
     * @param pkCertificateUrl
     * URL to the public key certificate to be used to encrypt the randomly-generated key
     * provided by the Mobile Payment App during registration
     */
    public void setPkCertificateUrl(String pkCertificateUrl) {
        this.pkCertificateUrl = pkCertificateUrl;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return Returns debug information for the class in debug mode.
     * In release mode it returns only the class name, so that sensitive information is never
     * returned by this method.
     */
    @Override
    public String toString() {

        if (BuildInfo.isDebugEnabled()) {
            return "PaymentAppRegistrationData{" +
                   "registrationCode='" + registrationCode + '\'' +
                   ", publicKey='" + publicKey + '\'' +
                   ", pkCertificateUrl='" + pkCertificateUrl + '\'' +
                   '}';
        } else {
            return "PaymentAppRegistrationData";
        }
    }

}
