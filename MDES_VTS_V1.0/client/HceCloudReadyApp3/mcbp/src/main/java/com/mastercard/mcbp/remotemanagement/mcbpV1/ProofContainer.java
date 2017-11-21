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

import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.utils.json.JsonUtils;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class ProofContainer {
    private String deviceInformation;
    private String activationProof;

    public String getDeviceInformation() {
        return deviceInformation;
    }

    public void setDeviceInformation(String deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public String getActivationProof() {
        return activationProof;
    }

    public void setActivationProof(String activationProof) {
        this.activationProof = activationProof;
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
            return "ProofContainer [deviceInformation=" + getDeviceInformation() + ", activationProof="
                   + getActivationProof() + "]";
        } else {
            return "ProofContainer";
        }
    }

    public String toJsonString() {
        return new JsonUtils<ProofContainer>(ProofContainer.class).toJsonString(this);
    }

}
