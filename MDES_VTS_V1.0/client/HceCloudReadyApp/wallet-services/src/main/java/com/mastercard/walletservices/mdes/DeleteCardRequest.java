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
  * Encapsulate the data which will be needed to process request to delete a Token Credential.
  */
public class DeleteCardRequest {

    @JSON(name = "digitizedCardId")
    private String digitizedCardId;

    public String getDigitizedCardId() {
        return digitizedCardId;
    }

    public void setDigitizedCardId(final String digitizedCardId) {
        this.digitizedCardId = digitizedCardId;
    }

    /**
     * DigitizeResponse
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
     * {@link com.mastercard.walletservices.mdes.DeleteCardRequest}
     * object from given json string.
     *
     * @param jsonContent The Content of the Request as JSON Object
     * @return A DeleteCardRequest object
     */
    public static DeleteCardRequest valueOf(String jsonContent) {
        return new JSONDeserializer<DeleteCardRequest>()
                .deserialize(jsonContent, DeleteCardRequest.class);
    }
}
