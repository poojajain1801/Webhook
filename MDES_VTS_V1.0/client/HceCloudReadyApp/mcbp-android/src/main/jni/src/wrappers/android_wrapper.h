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

#include <core/mcbp_card.h>
#include <core/mobile_kernel/dsrp_input_data.h>
#include <core/mcm/mcm_lite_listener.h>
#include <core/mcm/contactless_transaction_context.h>
#include <wrappers/keys_data.h>

namespace mcbp_android {

/**
 *  \brief     The Android Wrapper class.
 *  \details   Wrapper class to connect Android JNI to the library *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class AndroidWrapper : public mcbp_core::McmLiteListener {
 public:
  /**
   * Get the instance of the wrapper
   * @return a pointer to the only existing instance of the wrapper
   */
  static AndroidWrapper* instance();

  /**
   * \brief Activate Contactless the MCBP Card.
   * This version supports only PIN which is passed as argument
   * @param env The JNI Environment
   * @param thiz The calling Java class
   * @param card_profile_obj The Androdi Java Card Profile object
   */
  static void activate_contactless(JNIEnv* env, jobject thiz,
                                   jobject card_profile_obj);

  /**
   * Prepare the MCBP Chat dard to Start a contactless transaction by providing
   * a new set of keys, the mobile PIN and related business logic values.
   * @param env The JNI environment
   * @param thiz The calling object.
   * @param credentials The Java object that contains the next set of keys. 
   *                    The caller of the function is responsible for deleting
   *                    the content of the credentials as soon as this function
   *                    returns. A copy of the credentials is stored by the
   *                    method and deleted when no longer needed.
   * @param listener The listener object
   * @param j_amount The amount of the transaction
   * @oaram j_currency Currency Code
   * @param j_exatc_amount Specify whether the value of the amount represents 
   * the exact value or its maximum.
   * @param j_cvm_entered Specify whether the CVM has been entered
   * @param j_cvm_required Specify whether the CVM is required
   * @param j_online_allowed Specify whether an online transaction is allowed
   */
  static void start_contactless(JNIEnv* env, jobject thiz, jobject credentials,
                                jobject listener, jlong j_amount,
                                jint j_currency, jboolean j_exact_amount,
                                jboolean j_cvm_entered, jboolean j_cvm_required,
                                jboolean j_online_allowed);

  /**
   * Respond to a C-APDU.
   * @param env The JNI environment
   * @param this The calling object
   * @param apdu The Command APDU. The caller should clear the content of the
   *              C-APDU if this contains any sensitive information
   * @return The Response APDU
   */
  static jbyteArray transceive(JNIEnv* env, jobject thiz, jobject apdu);

  /**
   * Calculate a DSRP Transaction Record
   * @param env The JNI environment
   * @param thiz The calling object
   * @param dsrp_in The DSRP Input Data
   * @return A Java object containing the DSRP output data
   */
  static jobject transaction_record(JNIEnv* env, jobject thiz, jobject dsrp_in);

  /**
   * Prepare the MCBP Chat dard to Start a Remote Payment with a new set of keys
   * and the mobile PIN.
   * @param env The JNI environment
   * @param thiz The calling object.
   * @param suk_obj The Java object that contains the next set of keys
   * @param j_pin The mobile PIN as entered by the user in the Android OS. 
   *              If null the keys are assumed to have been already unlocked.
   */
  static void activate_remote(JNIEnv* env, jobject thiz, jobject card_profile,
                              jobject credentials, jboolean cvm_entered);

  /**
   * Stop contactless mode and delete credentials, but keep the card profile.
   * @param env The JNI environment
   * @param thiz The calling object.
   */
  static void stop_contactless(JNIEnv* env, jobject thiz);

  /**
   * Deactivate the contactless card
   * @param env The JNI environment
   * @param thiz The calling object.
   */
  static void deactivate_contactless(JNIEnv* env, jobject thiz);

  /**
   * Deactivate the remote payment card
   * @param env The JNI environment
   * @param thiz The calling object.
   */
  static void deactivate_remote(JNIEnv* env, jobject thiz);

  /**
   * Handle events generated by the McmLite
   */
  virtual void on_event(
      const mcbp_core::ContactlessTransactionContext& context);

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
   * Check if credentials have been received and sent to the MCM Lite object
   * (i.e. the start_contactless has been performed with valid keys and the card
   * is waiting for C-APDU to process
   */
  static jboolean credentials_received(JNIEnv* env, jobject thiz);

  /**
   * Denscructor
   */
  ~AndroidWrapper();

 protected:
  // None

 private:
  AndroidWrapper() { }

  /**
   * Pointer to the only instance of the MCBPcore Wrapper
   */
  static AndroidWrapper* instance_;

  /**
   * Internal variable to keep track whether the credentials have been sent to
   * the MCM Lite. It is used to comply with the Android interface that
   * may check this to see if the card has been activated before the HCE Service
   * is actually started.
   */
  static bool credentials_received_;

  /**
   * Pointer to McbpCard for Contactless
   */
  static mcbp_core::McbpCard *cl_card_;

  /**
   * Pointer to the MCBP Card for Remote Payment
   */
  static mcbp_core::McbpCard *rp_card_;

  /**
   * Pointer to the hosting Android Java Virtual Machine
   */
  static JavaVM *g_java_vm_;

  /**
   * Pointer to the Java Listener object
   * Once the contactless transaction is completed an event is notified to this
   * object
   */
  static jobject cl_listener_;

  // Used to create the instance of this singletone object
  friend void CreateAndroidWrapper();

  // Utility Functions
  static void ReadCardRiskManagementData(JNIEnv* env,
                                         jobject card_profile_obj,
                                         mcbp_core::CardProfileData* profile);

  static void ReadContactlessData(JNIEnv* env,
                                  jobject card_profile_obj,
                                  mcbp_core::CardProfileData* profile);

  static void ReadRemotePaymentData(JNIEnv* env,
                                    jobject card_profile_obj,
                                    mcbp_core::CardProfileData* profile);

  static void ReadRecords(JNIEnv* env, jobject cl_data_obj,
                          mcbp_core::CardProfileData* profile);

  static void ReadKeys(JNIEnv* env, jobject suk_obj,
                       mcbp_core::KeysData* keys);

  static void ReadDsrpInput(JNIEnv* env, jobject input_obj,
                            mcbp_core::DsrpInputData* dsrp_in);
  static jobject FormatDsrpOutput(JNIEnv* env,
                                  const mcbp_core::DsrpOutputData& dsrp_out);

  static void deactivate_card(mcbp_core::McbpCard*& card);  // NOLINT

  static void ReleaseGlobalReferences(JNIEnv* env);
};

}  // namespace mcbp_android

#endif  // defined(__ANDROID__)  // NOLINT

#endif  // defined(SRC_WRAPPERS_ANDROID_WRAPPER_H_)  // NOLINT
