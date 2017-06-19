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

package com.mastercard.walletservices.mdes;

import com.mastercard.mobile_api.bytes.ByteArray;

import flexjson.JSON;
/**
  * Encapsulate the data which will be needed to process request to delete a Token Credential.
  */
public class MobileKeys {
    @JSON(name = "transportKey")
    private ByteArray transportKey;

    @JSON(name = "macKey")
    private ByteArray macKey;

    @JSON(name = "dataEncryptionKey")
    private ByteArray dataEncryptionKey;

    public MobileKeys() {
        super();
    }

    /**
     * Parameterize constructor
     *
     * @param transportKey      Transport key.
     * @param dataEncryptionKey Data encryption key.
     * @param macKey            Mac key.
     */
    public MobileKeys(ByteArray transportKey, ByteArray dataEncryptionKey, ByteArray macKey) {
        this.transportKey = transportKey;
        this.dataEncryptionKey = dataEncryptionKey;
        this.macKey = macKey;
    }

    public ByteArray getTransportKey() {
        return transportKey;
    }

    /**
     * @param transportKey The Mobile Transport Key used to provide
     *                     confidentiality of data at the transport level between the Mobile
     *                     Payment App and the Credentials Management (Dedicated). Encrypted
     *                     using the randomly-generated key (RGK) provided by the Mobile
     *                     Payment App.Max Length: 64
     */
    public void setTransportKey(ByteArray transportKey) {
        this.transportKey = transportKey;
    }

    public ByteArray getMacKey() {
        return macKey;
    }

    /**
     * @param macKey The Mobile MAC Key used to provide integrity of data at
     *               the transport level between the Mobile Payment App and the
     *               Credentials Management (Dedicated). Encrypted using the
     *               randomly-generated key (RGK) provided by the Mobile Payment App. Max
     *               Length: 64
     */
    public void setMacKey(ByteArray macKey) {
        this.macKey = macKey;
    }

    public ByteArray getDataEncryptionKey() {
        return dataEncryptionKey;
    }

    /**
     * @param dataEncryptionKey The Mobile Data Encryption Key used to encrypt any
     *                          sensitive data at the data field level between the Mobile Payment
     *                          App and the Credentials Management (Dedicated). Encrypted using the
     *                          randomly-generated key (RGK) provided by the Mobile Payment App. Max
     *                          Length: 64
     */
    public void setDataEncryptionKey(ByteArray dataEncryptionKey) {
        this.dataEncryptionKey = dataEncryptionKey;
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
        return "MobileKeys";
    }
}
