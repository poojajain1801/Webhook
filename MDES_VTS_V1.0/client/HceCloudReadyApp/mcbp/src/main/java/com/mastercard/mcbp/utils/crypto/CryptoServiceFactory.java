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

package com.mastercard.mcbp.utils.crypto;

/**
 * Factory to create Mcbp Crypto Service implementations and abstract the internal implementation
 * of the crypto logic
 *
 * The CryptoServiceFactory is a single tone object (cfr. Effective Java 2nd edition - item 3)
 *
 */
public enum CryptoServiceFactory {
    INSTANCE;

    static boolean sNativeCrypto = false;

    /**
     * Get the Default Crypto Service
     *
     * @return The Default Mcbp Crypto Service
     * */
    static public CryptoService getDefaultCryptoService() {
        if (sNativeCrypto) return CryptoServiceNativeImpl.INSTANCE;
        return CryptoServiceImpl.INSTANCE;
    }

    /**
     * Set the default Crypto Service to use the native implementation.
     * */
    static public void enableNativeCryptoService() {
        sNativeCrypto = true;
    }
}
