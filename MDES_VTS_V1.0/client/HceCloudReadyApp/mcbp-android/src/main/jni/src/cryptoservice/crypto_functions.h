/*******************************************************************************
 * Copyright (c) 2015, MasterCard International Incorporated and/or its
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
*******************************************************************************/

/**
 * The following coding guidelines have been followed
 * http://google-styleguide.googlecode.com/svn/trunk/cppguide.xml
 *
 * MCBP = MasterCard Cloud-Based Payments
 */

#include <utils/byte_array.h>
#include <cryptoservice/mobile_keys.h>

#ifndef CRYPTOSERVICE_CRYPTO_FUNCTIONS_H  //NOLINT
#define CRYPTOSERVICE_CRYPTO_FUNCTIONS_H  //NOLINT

#ifdef _WIN32
#define EXPORT
#else
// Export Macro - Need to ensure functions are visible although
// the use of -fvisibility=hidden flag
#define EXPORT __attribute__((__visibility__("default")))
#endif

namespace mcbp_crypto_service {

/**
 * Build both UMD and MD cryptograms for the Generate AC. The two cryptograms
 * are returned as concatenated vectors
 */
EXPORT ByteArray generate_ac(const ByteArray& cryptogram_input,
                             const ByteArray& umd_session_key,
                             const ByteArray& md_session_key);

/**
 * Build both UMD and MD cryptograms for the Compute Cryptographic Checksum. 
 * The two cryptograms are returned as concatenated vectors
 */
EXPORT ByteArray compute_cc(const ByteArray& cryptogram_input,
                            const ByteArray& umd_session_key,
                            const ByteArray& md_session_key);

/**
 * Generate the Session Key by X-ORing the Single Use Key with Mobile Pin
 */
EXPORT ByteArray unlock_session_key(const ByteArray& single_use_key,
                                    const ByteArray& mobile_pin);

/**
 * Decrypt the mobile keys using a given key. Returns the MobileKeys object
 */
EXPORT MobileKeys decrypt_mobile_keys(
    const ByteArray& encrypted_transport_key,
    const ByteArray& encrypted_mac_key,
    const ByteArray& encrypted_data_encryption_key,
    const ByteArray& key);

/**
 * Decrypt a notification data message.
 * It first verify that the MAC matches and then decrypt the data.
 * It returns an empty ByteArray if the MAC does not match the received ones
 */
EXPORT ByteArray decrypt_notification_data(const ByteArray& response_data,
                                           const ByteArray& mac_key,
                                           const ByteArray& transport_key);

/**
 * Perform an LDE encryption
 */
EXPORT ByteArray lde_encryption(const ByteArray& data, const ByteArray& key);

/**
 * Perform an LDE decryption
 */
EXPORT ByteArray lde_decryption(const ByteArray& data, const ByteArray& key);

/**
 * Encrypt a Retry Request message
 */
EXPORT ByteArray encrypt_retry_request_data(const ByteArray& data,
                                            const ByteArray& key);

/**
 * Decrypt a Retry Request message
 */
EXPORT ByteArray decrypt_retry_request_data(const ByteArray& data,
                                            const ByteArray& key);

/**
 * Build a service request message
 * @param data The service request data
 * @param mac_key The Mobile Key (MAC)
 * @param transport_key The Mobile Key (Transport)
 * @param session_code The Session Code
 * @param counter the counter for this message to build the IV
 * @return the fully built service request message
 */
EXPORT ByteArray build_service_request(const ByteArray& data,
                                       const ByteArray& mac_key,
                                       const ByteArray& transport_key,
                                       const ByteArray& session_code,
                                       const uint16_t counter);

/**
 * Decrypt a CMS Service Response
 * @param service_data   The data as received from the CMS, which is expected to
 *                       be in the format of COUNTERS | ENC_DATA | MAC
 * @param mac_key        The mobile MAC key
 * @param transport_key  The mobile transport key
 * @param session_code   The session code used to diversify Transport and Mac 
 *                       keys.
 * @return The uncrypted response data
 */
EXPORT ByteArray decrypt_service_response(const ByteArray& service_data,
                                          const ByteArray& mac_key,
                                          const ByteArray& transport_key,
                                          const ByteArray& session_code);

/**
 * Decrypt the ICC component which is received encrypted with the data
 * encryption key from the CMS-D
 * @param data The encrypted ICC component
 * @param key The decryption key to be used
 * @return The unencrypted ICC component
 */
EXPORT ByteArray decrypt_icc_component(const ByteArray& data,
                                       const ByteArray& key);

/**
 * Decrypt the ICC KEK which is then used to decrypt ICC components
 * @param data The encrypted ICC KEK
 * @param key The decryption key to be used
 * @return The unencrypted ICC component
 */
EXPORT ByteArray decrypt_icc_kek(const ByteArray& data, const ByteArray& key);

/**
 * Calculate the Authentication Code as specified in the MCBP MDES CMS-D APIs
 *
 * @param mobile_keyset_id    The Mobile Key Set Id (as byte array)
 * @param device_finger_print The Device Finger Print
 * @param session_code       The Session Code
 * @return A byte array containing the authentication code
 */
EXPORT ByteArray calculate_authentication_code(
    const ByteArray& mobile_keyset_id,
    const ByteArray& session_code,
    const ByteArray& device_finger_print);

/**
 * Encrypt the PIN Block according to the ISO/FDIS - 9564 Format 4 PIN block 
 * encipher
 *
 * @param pinData              The PIN Block
 * @param payment_instance_id  The Payment Instance ID, which will be used to
 *                             generate the PAN surrogate
 * @param key                  The Encryption Key
 * @return The encrypted PIN Block
 */
EXPORT ByteArray encrypt_pin_block(const ByteArray& pin_data,
                                   const ByteArray& payment_instance_id,
                                   const ByteArray& key);
/**
 * Decrypt the PIN Block according to the ISO/FDIS - 9564 Format 4 PIN block
 * encipher
 *
 * Please note that this function is reported here for unit test purposes as
 * such operation is never expected to be performed by the mobile application
 *
 * @param pinData              The PIN Block
 * @param payment_instance_id  The Payment Instance ID, which will be used to
 *                             generate the PAN surrogate
 * @param key                  The Decryption Key
 * @return The encrypted PIN Block
 */
EXPORT ByteArray decrypt_pin_block(const ByteArray& pin_data,
                                   const ByteArray& payment_instance_id,
                                   const ByteArray& key);

/**
 * Decrypt a field that was encrypted using the Data Encryption Key according 
 * to the MDES APIs
 *
 * @param data  The data to be decrypted
 * @param key   The key to be used
 * @return The decrypted data field
 */
EXPORT ByteArray decrypt_data_encrypted_field(const ByteArray& data,
                                              const ByteArray& key);

/**
 * Decrypt the data contained in the notification message. 
 * The data received from the CMS is expected to be in the format of
 * SESSION_ID | ENC_DATA | MAC
 *
 * @param encrypted_data  The ENC_DATA of the received message
 * @param mac_data        The MAC of the received message
 * @param mac_key         The mobile mac key
 * @param transport_key   The mobile transport key
 *
 * @return The unencrypted notification data
 */
EXPORT ByteArray decrypt_mcbpv1_notification_data(
    const ByteArray& service_data,
    const ByteArray& mac_data,
    const ByteArray& mac_key,
    const ByteArray& transport_key);

/**
 * Encrypt a random generated key using RSA as per CMS-D APIs document
 * @param random_generated_key  The random generated key to be encrypted
 * @param public_key            The encryption key (RSA Public Key)
 * @return The encrypted key using RSA and OAEP SHA-256 padding
 */
EXPORT ByteArray encrypt_random_generated_key(
    const ByteArray& random_generated_key,
    const ByteArray& public_key);

/**
 * Utility function to generate the Plain Text PAN Field according to the
 * ISO/FDIS 9564-1:2014(E) Pin Block Format 4
 * @param pan_data The PAN encoded as ASCII decimal values
 *                 (e.g. 5213 ... 2345 -> 0x35, 0x32, 0x31, 0x33 ... etc)
 * @return The encoded PAN field as per Pin Block Format 4
 */
EXPORT ByteArray generate_plain_text_pan_field(const ByteArray& pan_data);

/**
 * Utility function to generate the Plain Text PIN Field according to the
 * ISO/FDIS 9564-1:2014(E) Pin Block Format 4
 *
 * @param pin_data The PIN encoded as ASCII decimal value
 *                 (e.g. 1234 -> 0x31, 0x32, 0x33, 0x34)
 * @return The encoded PIN field as per Pin Block Format 4
 */
EXPORT ByteArray generate_plain_text_pin_field(const ByteArray& pin_data);

/**
 * Retrieve the PIN from the plain text PIN block format
 * @param The plain text PIN block as per Pin Block Format 4
 * @return the PIN as ASCII encoded
 */
EXPORT ByteArray pin_from_plain_text_pin_block(
    const ByteArray& plain_text_pin_block);


}  // namespace mcbp_crypto_service

#endif  // defined(CRYPTOSERVICE_CRYPTO_FUNCTIONS_H)  //NOLINT
