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

package com.mastercard.mcbp.lde;

import android.content.Context;

import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.utils.tlv.ParsingException;

/**
 * Instantiate the Android Local Database Encrypted (LDE)
 */
public enum LdeAndroidFactory {
    INSTANCE;

    /**
     * We enforce only one instance of the LDE via the Factory method
     * */
    private static Lde mLde = null;
    /**
     * Get the Default Android Database
     *
     * @return The Default Android MCBP Database implementation
     * */
    static public Lde getDefaultMcbpDatabase(final Context context) {
        // Return the current DB if it exists already
        if (mLde != null) return mLde;

        // Setup the standard database required for MCBP
        McbpDataBase db = AndroidMcbpDataBaseFactory.getDefaultMcbpDatabase(context);

        // Create the encryption wrapper for the database
        try {
            mLde = new Lde(db);
            return mLde;
        } catch (ParsingException | McbpCryptoException | InvalidInput e) {
            // Something went wrong and we could not instantiate the object
            // This has to be raised as runtime exception as there is nothing we can do to recover
            // from it
            throw new RuntimeException(e);
        }
    }
}
