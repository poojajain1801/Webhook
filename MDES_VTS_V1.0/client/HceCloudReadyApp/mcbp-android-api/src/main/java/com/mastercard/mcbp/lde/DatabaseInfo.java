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

/**
 * Database Info helper object
 */
final class DatabaseInfo {
    static final String DATABASE_NAME = "MCBP.db";
    static final String PREFS_NAME = "MCBP";
    static final String KEY_CREATED = "key_created";
    static final String KEY_STORAGE = "storage";
    static final String KEY_DEVICE_ID = "deviceID";

    /**
     * The version number of the DB. It should be calculated as follows:
     * First digit << 16 +
     * Second digit << 8 +
     * Third digit
     * <p/>
     * For example:
     * 1.0.5 -> 1 << 16 + 0 << 8 + 5 -> 0x00010005
     * <p/>
     * Assumption -> The last character in version (like a in 1.0.6a) will not be greater than p.
     * otherwise, it will result in version number clash with higher versions.
     * 1.0.6a -> 1 << 16 + 0 << 8 + 6 << 4 + asciiOf(a) -> 0x000100C1
     * 1.0.6b -> 1 << 16 + 0 << 8 + 6 << 4 + asciiOf(b) -> 0x000100C2
     * 1.0.6c -> 1 << 16 + 0 << 8 + 6 << 4 + asciiOf(c) -> 0x000100C3
     * 1.0.6r -> 1 << 16 + 0 << 8 + 6 << 4 + asciiOf(r) -> 0x000100D2
     * 1.0.7a -> 1 << 16 + 0 << 8 + 7 << 4 + asciiOf(a) -> 0x000100D1
     */
    static final int DATABASE_VERSION = 0x000100C3;

    // Environment container table
    static final String TABLE_ENVIRONMENT_CONT = "environment_container";
    static final String COL_INIT_STATE = "lde_initialized";
    static final String COL_WALLET_STATE = "wallet_state";
    static final String CMS_MPA_ID = "cms_mpa_id";
    static final String COL_URL = "remote_url";
    static final String COL_MPA_FGP = "mpa_fgp";
    static final String COL_LIFE_CYCLE = "alcd";
    static final String COL_MNO = "mno";
    static final String COL_LATITUDE = "latitude";
    static final String COL_LONGITUDE = "longitude";
    static final String COL_WSP_NAME = "wsp_name";
    static final String COL_WALLET_PIN_STATE = "wallet_pin_state";

    static final String COL_CARD_ID = "card_id";

    static final String COL_TOKEN_UNIQUE_REFERENCE = "token_unique_reference";

    // Card profiles list
    static final String TABLE_CARD_PROFILES_LIST = "card_profiles_list";
    static final String COL_PROFILE_DATA = "card_data";
    static final String COL_PROFILE_STATE = "card_state";
    static final String COL_CARD_PIN_STATE = "card_pin_state";

    static final String TABLE_TOKEN_UNIQUE_REFERENCE_LIST = "token_unique_reference_list";

    // Suk list
    static final String TABLE_SUK_LIST = "suk_list";
    static final String COL_SUK_INFO = "suk_info";
    static final String COL_SUK_ID = "suk_id";
    static final String COL_SUK_CL_UMD = "suk_cl_umd";
    static final String COL_SUK_CL_MD = "suk_cl_md";
    static final String COL_SUK_RP_UMD = "suk_rp_umd";
    static final String COL_SUK_RP_MD = "suk_rp_md";
    static final String COL_IDN = "idn";
    static final String COL_ATC = "atc";
    static final String COL_HASH = "hash";

    // Transaction lists
    static final String TABLE_CARD_TRANSACTIONS_LIST = "card_transaction_list";
    static final String COL_TRANSACTION_LOG = "trans_log";
    static final String COL_TRANSACTION_TIMESTAMP = "time_stamp";
    static final String COL_TRANSACTION_ATC = "tx_atc";
    static final String COL_TRANSACTION_DATE = "tx_date";
    static final String COL_TRANSACTION_ID = "transaction_id";

    //Mobile keys table
    static final String TABLE_MOBILE_KEYS = "mobile_keys";
    static final String COL_MOBILE_KEY_SET_ID = "mobile_keyset_id";
    static final String COL_MOBILE_KEY_VALUE = "mobile_key_value";
    static final String COL_MOBILE_KEY_TYPE = "mobile_key_type";

    //Suk status table
    static final String TABLE_TRANSACTION_CREDENTIAL_STATUS =
            "table_transaction_credential_status";
    static final String TRANSACTION_CREDENTIAL_STATUS = "transaction_credential_status";
}
