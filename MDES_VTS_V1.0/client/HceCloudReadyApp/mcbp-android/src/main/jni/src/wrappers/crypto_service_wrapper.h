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

#ifndef SRC_WRAPPERS_ANDROID_WRAPPER_H_  // NOLINT
#define SRC_WRAPPERS_ANDROID_WRAPPER_H_  // NOLINT

#ifdef __ANDROID__

#include <jni.h>

namespace mcbp_android {

/**
 *  \brief     The Android Wrapper class.
 *  \details   Wrapper class to connect Android JNI to the library *  
 *  \copyright MasterCard International Incorporated and/or its affiliates.
 *             All rights reserved.
 */
class AndroidCryptoService {
 public:
  /**
   * Get the instance of the wrapper
   * @return a pointer to the only existing instance of the wrapper
   */
  static AndroidCryptoService* instance();

  /**
   *  \brief    Compute the UMD and MD Cryptograms to be used in the Generate AC
   */
  static jbyteArray generate_ac(JNIEnv* env, jobject thiz,
                                const jbyteArray cryptogram_input,
                                const jbyteArray umd_session_key,
                                const jbyteArray md_session_key);

  /**
   *  \brief    Compute the UMD and MD Cryptograms to be used in the 
   *            Compute Cryptographic Checksum operation
   */
  static jbyteArray compute_cc(JNIEnv* env, jobject thiz,
                               const jbyteArray cryptogram_input,
                               const jbyteArray umd_session_key,
                               const jbyteArray md_session_key);

  /**
   *  \brief    Compute the UMD and MD Cryptograms to be used in the 
   *            Compute Cryptographic Checksum operation
   */
  static jbyteArray unlock_session_key(JNIEnv* env, jobject thiz,
                                       const jbyteArray single_use_key,
                                       const jbyteArray mobile_pin);

  /**
   * Decrypt the mobile keys using a given key. Returns the MobileKeys object
   */
  static jbyteArray decrypt_mobile_keys(
      JNIEnv* env,
      jobject thiz,
      const jbyteArray encrypted_transport_key,
      const jbyteArray encrypted_mac_key,
      const jbyteArray encrypted_data_encryption_key,
      const jbyteArray key);

  /**
   * Decrypt a notification data message.
   * It first verify that the MAC matches and then decrypt the data.
   * It returns an empty ByteArray if the MAC does not match the received ones
   */
  static jbyteArray decrypt_notification_data(JNIEnv* env,
                                              jobject thiz,
                                              const jbyteArray response_data,
                                              const jbyteArray mac_key,
                                              const jbyteArray transport_key);

  /**
   * Perform an LDE encryption
   */
  static jbyteArray lde_encryption(JNIEnv* env,
                                   jobject thiz,
                                   const jbyteArray data,
                                   const jbyteArray key);

  /**
   * Perform an LDE decryption
   */
  static jbyteArray lde_decryption(JNIEnv* env,
                                   jobject thiz,
                                   const jbyteArray data,
                                   const jbyteArray key);

  /**
   * Encrypt a Retry Request message
   */
  static jbyteArray encrypt_retry_request_data(JNIEnv* env,
                                               jobject thiz,
                                               const jbyteArray data,
                                               const jbyteArray key);
  /**
   * Decrypt a Retry Request message
   */
  static jbyteArray decrypt_retry_request_data(JNIEnv* env,
                                               jobject thiz,
                                               const jbyteArray data,
                                               const jbyteArray key);
  
  /**
   * Build a service request message
   * @param data The service request data
   * @param mac_key The Mobile Key (MAC)
   * @param transport_key The Mobile Key (Transport)
   * @param session_code The Session Code
   * @param counter the counter for this message to build the IV
   * @return the fully built service request message
   */
  static jbyteArray build_service_request(JNIEnv* env,
                                          jobject thiz,
                                          const jbyteArray data,
                                          const jbyteArray mac_key,
                                          const jbyteArray transport_key,
                                          const jbyteArray session_code,
                                          const int counter);

  /**
   * Decrypt a CMS Service Response
   * @param service_data   The data as received from the CMS, which is expected
   *                       to be in the format of COUNTERS | ENC_DATA | MAC
   * @param mac_key        The mobile MAC key
   * @param transport_key  The mobile transport key
   * @param session_code   The session code used to diversify Transport and Mac 
   *                       keys.
   * @return The uncrypted response data
   */
  static jbyteArray decrypt_service_response(JNIEnv* env,
                                             jobject thiz,
                                             const jbyteArray service_data,
                                             const jbyteArray mac_key,
                                             const jbyteArray transport_key,
                                             const jbyteArray session_code);

  /**
   * Decrypt the ICC component which is received encrypted with the data
   * encryption key from the CMS-D
   * @param data The encrypted ICC component
   * @param key The decryption key to be used
   * @return The unencrypted ICC component
   */
  static jbyteArray decrypt_icc_component(JNIEnv* env,
                                          jobject thiz,
                                          const jbyteArray data,
                                          const jbyteArray key);

  /**
   * Decrypt the ICC KEK which is used to encrypt the ICC key
   * @param data The encrypted ICC KEK
   * @param key The decryption key to be used
   * @return The unencrypted ICC component
   */
  static jbyteArray decrypt_icc_kek(JNIEnv* env,
                                    jobject thiz,
                                    const jbyteArray data,
                                    const jbyteArray key);

  /**
   * Calculate the Authentication Code as specified in the MCBP MDES CMS-D APIs
   *
   * @param mobile_keyset_id    The Mobile Key Set Id (as byte array)
   * @param device_finger_print The Device Finger Print
   * @param session_code       The Session Code
   * @return A byte array containing the authentication code
   */
  static jbyteArray calculate_authentication_code(
      JNIEnv* env,
      jobject thiz,
      const jbyteArray mobile_keyset_id,
      const jbyteArray device_finger_print,
      const jbyteArray session_code);

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
  static jbyteArray encrypt_pin_block(JNIEnv* env,
                                      jobject thiz,
                                      const jbyteArray pin_data,
                                      const jbyteArray payment_instance_id,
                                      const jbyteArray key);

  /**
   * Decrypt a field that was encrypted using the Data Encryption Key according 
   * to the MDES APIs
   *
   * @param data  The data to be decrypted
   * @param key   The key to be used
   * @return The decrypted data field
   */
  static jbyteArray decrypt_data_encrypted_field(JNIEnv* env,
                                                 jobject thiz,
                                                 const jbyteArray data,
                                                 const jbyteArray key);

  /**
   * Encrypt a random generated key using RSA as per CMS-D APIs document
   * @param random_generated_key  The random generated key to be encrypted
   * @param encryption_key        The encryption key
   * @return The encrypted key using RSA and OMAP 1 padding
   */
  static jbyteArray encrypt_random_generated_key(
      JNIEnv* env,
      jobject thiz,
      const jbyteArray random_generated_key,
      const jbyteArray encryption_key);

  /**
   * JNI OnLoad function. It is called by Android Java when the library is first
   * loaded.
   * In this implementation it is mainly used to register native methods.
   * @param vm Pointer to the Android Java Virtual Machine
   * @param reserved Reserved value
   * @return A status value (e.g. JNI_VERSION_1_6 if successful).
   */
  static jint JNICALL on_load(JavaVM * vm, void * reserved);

  /**
   * Denscructor
   */
  ~AndroidCryptoService();

 protected:
  // None

 private:
  AndroidCryptoService() { }

  // Used to create the instance of this singletone object
  friend void CreateAndroidCryptoService();

  /**
   * Pointer to the only instance of the MCBPcore Wrapper
   */
  static AndroidCryptoService* instance_;

  /**
   * Pointer to the hosting Android Java Virtual Machine
   */
  static JavaVM *g_java_vm_;

  static void ReleaseGlobalReferences(JNIEnv* env);

  /**
   * Utility function to check the status of the environment. 
   * It is used before each function call
   */
  static void check_environment(JNIEnv* env);
};

}  // namespace mcbp_android

#endif  // defined(__ANDROID__)  // NOLINT

#endif  // defined(SRC_WRAPPERS_ANDROID_WRAPPER_H_)  // NOLINT
