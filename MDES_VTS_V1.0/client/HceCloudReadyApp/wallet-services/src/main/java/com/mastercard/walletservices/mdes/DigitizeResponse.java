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

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
  * Represents the digitize response for CMS-D.
  */
public class DigitizeResponse {

    @JSON(name = "isError")
    private boolean isError;

    @JSON(name = "errorDescription")
    private String errorDescription;

    @JSON(name = "tokenUniqueReference")
    private String tokenUniqueReference;

    /**
     * Returns json string.
     *
     * @return Json string.
     */
    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // ByteArray serialization
        return serializer.serialize(this);
    }

    /**
     * Returns equivalent
     * {@link DigitizeResponse}
     * object from given json string.
     *
     * @param jsonContent The Content of the Response as JSON Object
     * @return The Digitized Response object
     */
    public static DigitizeResponse valueOf(String jsonContent) {
        return new JSONDeserializer<DigitizeResponse>()
                .deserialize(jsonContent, DigitizeResponse.class);
    }

    //Method added for flexJson library, used while serializing
    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(final String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }


}
