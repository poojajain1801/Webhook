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

#ifdef __ANDROID__

// Project Libraries
#include <wrappers/android_wrapper.h>
#include <wrappers/android_wrapper_utils.h>
#include <utils/pin_validator.h>
#include <utils/mcbp_core_exception.h>
#include <utils/transaction_keys.h>
#include <utils/random_transaction_keys.h>
#include <utils/byte_array.h>
#include <core/mobile_kernel/dsrp_input_data.h>
#include <core/mobile_kernel/dsrp_output_data.h>
#include <core/card_utilities.h>
#include <utils/utilities.h>

#include <log/log.h>

// C++ Libraries
#include <mutex>
#include <utility>
#include <map>
#include <string>

/*******************************************************************************
 * Exported functions
 ******************************************************************************/

extern "C" {
EXPORT
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM * vm, void * reserved) {
  return mcbp_android::AndroidWrapper::instance()->on_load(vm, reserved);
}

}  // end of extern "C" { }

/*******************************************************************************
 * JNI Methods' mapping
 ******************************************************************************/

static JNINativeMethod sMethods[] = {
  {"activateContactless",
    "(Lcom/mastercard/mcbp/card/profile/MppLiteModule;)V",
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->activate_contactless)},  // NOLINT(whitespace/line_length)

  {"startContactless",
    "(Lcom/mastercard/mcbp/card/mpplite/TransactionCredentials;Lcom/mastercard/mcbp/card/mpplite/ContactlessTransactionListener;JIZZZZ)V",  // NOLINT(whitespace/line_length)
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->start_contactless)},   // NOLINT(whitespace/line_length)

  {"activateRemote",
    "(Lcom/mastercard/mcbp/card/profile/MppLiteModule;Lcom/mastercard/mcbp/card/mpplite/TransactionCredentials;Z)V",    // NOLINT(whitespace/line_length)
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->activate_remote)},  // NOLINT(whitespace/line_length)

  {"transactionRecord",
    "(Lcom/mastercard/mcbp/card/mobilekernel/DsrpInputData;)Lcom/mastercard/mcbp/card/mobilekernel/DsrpOutputData;",  // NOLINT(whitespace/line_length)
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->transaction_record)},  // NOLINT(whitespace/line_length)

  {"stopContactlessNative",
    "()V",
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->stop_contactless)},  // NOLINT(whitespace/line_length)

  {"deactivateContactless",
    "()V",
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->deactivate_contactless)},  // NOLINT(whitespace/line_length)

  {"deactivateRemote",
    "()V",
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->deactivate_remote)},  // NOLINT(whitespace/line_length)

  {"processApduNative",
    "(Lcom/mastercard/mobile_api/bytes/ByteArray;)[B",
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->transceive)},  // NOLINT(whitespace/line_length)

  {"credentialsReceived",
    "()Z",
    reinterpret_cast<void*>(&mcbp_android::AndroidWrapper::instance()->credentials_received)},  // NOLINT(whitespace/line_length)
};

int jniRegisterNativeMethods(JNIEnv *env, const char *className,
                             const JNINativeMethod *gMethods, int numMethods) {
  mcbp_core::Log* log = mcbp_core::Log::instance();
  log->d("Registering %s natives\n", className);

  jclass cls = env->FindClass(className);
  if (cls == nullptr) {
    log->d("Native registration unable to find class %s\n", className);
    return -1;
  }
  if (env->RegisterNatives(cls, gMethods, numMethods) < 0) {
    log->d("Registration of native method failed for %s\n", className);
    return -1;
  }
  return 0;
}

namespace mcbp_android {
/*******************************************************************************
 * AndroidWrapper Static members' initialization
 ******************************************************************************/

AndroidWrapper* AndroidWrapper::instance_     = nullptr;
mcbp_core::McbpCard *AndroidWrapper::cl_card_ = nullptr;
mcbp_core::McbpCard *AndroidWrapper::rp_card_ = nullptr;
JavaVM *AndroidWrapper::g_java_vm_            = nullptr;
jobject AndroidWrapper::cl_listener_          = nullptr;
bool AndroidWrapper::credentials_received_    = false;

// Create the only instance of the singletone object
std::once_flag flag_mcbp_core_android_wrapper;

void CreateAndroidWrapper() {
  if (AndroidWrapper::instance_ != nullptr) return;
  AndroidWrapper::instance_ = new AndroidWrapper();
}

AndroidWrapper* AndroidWrapper::instance() {
  if (instance_ == nullptr)
    std::call_once(flag_mcbp_core_android_wrapper, CreateAndroidWrapper);
  return instance_;
}

/*******************************************************************************
 * AndroidWrapper Members' definition
 ******************************************************************************/

jint JNICALL AndroidWrapper::on_load(JavaVM * vm, void * reserved) {
  JNIEnv *env;

  // Get the JVM
  g_java_vm_ = vm;

  // Now get the env variable
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK)
    return -1;

  // Register methods with env->RegisterNatives.
  size_t no_elems = static_cast<size_t>(sizeof(sMethods) / sizeof(sMethods[0]));
  jniRegisterNativeMethods(
        env, "com/mastercard/mcbp/card/mpplite/mcbpv1/MppLiteNativeImpl",
        sMethods, no_elems);
  CheckForJavaExceptions(env);

  return JNI_VERSION_1_6;
}

void AndroidWrapper::activate_contactless(JNIEnv* env,
                                          jobject thiz,
                                          jobject card_profile_obj) {
  using mcbp_android::ThrowJavaException;
  if (env == nullptr)
    ThrowJavaException(env, "Env is null");

  if (cl_card_ != nullptr)  /* If the card exists we deactivate it */
    deactivate_contactless(env, thiz);

  if (card_profile_obj == nullptr)
    ThrowJavaException(env, "The CardProfile Java object is null");

  // Convert the JAVA input into a format that is suitable for the profile
  mcbp_core::CardProfileData profile;

  // We do not read the remote payment data as we care only for contactless

  // Get the Risk Management Data
  try {
    ReadCardRiskManagementData(env, card_profile_obj, &profile);
    ReadContactlessData(env, card_profile_obj, &profile);

    if (!profile.cl_supported)
      ThrowJavaException(env, "The Profile does not support Contactless");

    auto* cl_mcm_card_profile = mcbp_core::create_mcm_card_profile(profile);
    cl_card_ = new mcbp_core::McbpCard();
    cl_card_->initialize(cl_mcm_card_profile);
  } catch (const mcbp_core::Exception& e) {
    mcbp_core::Log::instance()->d("Native Exception: %s\n", e.what());
    ThrowJavaException(env, e.what());
    return;
  } catch (...) {
    mcbp_core::Log::instance()->d("Generic C++ Exception");
    ThrowJavaException(env, "Generic C++ Exception");
    return;
  }
}
void AndroidWrapper::start_contactless(JNIEnv* env,
                                       jobject  thiz,
                                       jobject  credentials,
                                       jobject  listener,
                                       jlong    j_amount,
                                       jint     j_currency,
                                       jboolean j_exact_amount,
                                       jboolean j_cvm_entered,
                                       jboolean j_cvm_required,
                                       jboolean j_online_allowed) {
  if (env == nullptr)
    ThrowJavaException(env, "ENV is nullptr");

  if (cl_card_ == nullptr)
    ThrowJavaException(env, "MCBP Card: Not initialized");

  // Read keys
  using mcbp_core::TransactionKeys;
  mcbp_core::KeysData keys;
  TransactionKeys* suks = nullptr;

  if (credentials != nullptr) {
    credentials_received_ = true;
    ReadKeys(env, credentials, &keys);
    suks = new TransactionKeys(keys);
  }

  const int64_t amount      = static_cast<int64_t>(j_amount);
  const int32_t currency    = static_cast<int64_t>(j_currency);
  const bool exact          = static_cast<bool>(j_exact_amount);
  const bool cvm_entered    = static_cast<bool>(j_cvm_entered);
  const bool cvm_required   = static_cast<bool>(j_cvm_required);
  const bool online_allowed = static_cast<bool>(j_online_allowed);

  try {
    // The keys are always validated/unlocked in Java
    cl_card_->start_contactless(suks, nullptr, instance_, amount,
                                currency, exact, cvm_entered, cvm_required,
                                online_allowed);
  } catch (const mcbp_core::Exception& e) {
    mcbp_core::Log::instance()->d("Native Exception: %s\n", e.what());
    ThrowJavaException(env, e.what());
    return;
  }
  catch (...) {
    mcbp_core::Log::instance()->d("Generic C++ Exception");
    ThrowJavaException(env, "Generic C++ Exception");
    return;
  }

  // Save the pointer back to Java now (after we are sure the initialization has
  // completed without exceptions
  CheckForJavaExceptions(env);
  cl_listener_ = reinterpret_cast<jobject>(env->NewGlobalRef(listener));

  // Call Back to Java
  jclass cls = env->GetObjectClass(cl_listener_);
  CheckForJavaExceptions(env);

  if (cls == nullptr)
    ThrowJavaException(env, "CL Listener Class not found");

  jmethodID method = env->GetMethodID(cls, "onContactlessReady", "()V");
  CheckForJavaExceptions(env);

  if (method == nullptr)
    ThrowJavaException(env, "Method onContactlessReady not found");

  env->CallVoidMethod(cl_listener_, method);
  CheckForJavaExceptions(env);
}

jbyteArray AndroidWrapper::transceive(JNIEnv* env, jobject thiz,
                                      jobject c_apdu) {
  if (env == nullptr)
    ThrowJavaException(env, "ENV is null");

  if (cl_card_ == nullptr)
    ThrowJavaException(env, "The Contactless Card has not been initialized");

  ByteArray command_apdu = ConvertAndroidByteArray(env, c_apdu);
  ByteArray resp_apdu = cl_card_->transceive(command_apdu);

  mcbp_core::Wipe(&command_apdu);
  return CreateJavaByteArray(env, resp_apdu);
}

void AndroidWrapper::activate_remote(JNIEnv* env,
                                     jobject thiz,
                                     jobject card_profile_obj,
                                     jobject credentials_obj,
                                     jboolean cvm_entered) {
  if (env == nullptr)
    ThrowJavaException(env, "ENV is null");

  if (card_profile_obj == nullptr)
    ThrowJavaException(env, "The CardProfile Java object is null");

  if (credentials_obj == nullptr)
    ThrowJavaException(env, "The Credentials Java object is null");

  if (rp_card_ != nullptr)
    ThrowJavaException(env, "A Remote Payment Card has been already activated");

  // Convert the JAVA input into a format that is suitable for the profile
  mcbp_core::CardProfileData profile;

  try {
    // We are not interested in the Contactless portion here
    ReadRemotePaymentData(env, card_profile_obj, &profile);
    CheckForJavaExceptions(env);

    ReadCardRiskManagementData(env, card_profile_obj, &profile);
    CheckForJavaExceptions(env);

    if (!profile.rp_supported) {
      ThrowJavaException(env, "The Profile does not support Remote Payment");
    }

    auto* rp_mcm_card_profile = mcbp_core::create_mcm_card_profile(profile);

    rp_card_ = new mcbp_core::McbpCard();
    rp_card_->initialize(rp_mcm_card_profile);

    // Read credentials
    mcbp_core::KeysData keys;
    ReadKeys(env, credentials_obj, &keys);

    mcbp_core::TransactionKeys* suks = new mcbp_core::TransactionKeys(keys);

    // We assume the keys have been unlocked in Java
    rp_card_->activate_remote(suks, nullptr);
  } catch (const mcbp_core::Exception& e) {
    mcbp_core::Log::instance()->d("Native Exception: %s\n", e.what());
    ThrowJavaException(env, e.what());
    return;
  }
  catch (...) {
    mcbp_core::Log::instance()->d("Generic C++ Exception");
    ThrowJavaException(env, "Generic C++ Exception");
    return;
  }
}

void AndroidWrapper::stop_contactless(JNIEnv* env, jobject thiz) {
  credentials_received_ = false;
  cl_card_->stop_contactless();
  if (cl_listener_ != nullptr) {
    env->DeleteGlobalRef(cl_listener_);
    cl_listener_ = nullptr;
  }
}

void AndroidWrapper::ReleaseGlobalReferences(JNIEnv* env) {
  if (env != nullptr) {
    if (cl_listener_ != nullptr) {
      env->DeleteGlobalRef(cl_listener_);
      cl_listener_ = nullptr;
    }
  }
}

void AndroidWrapper::deactivate_card(mcbp_core::McbpCard*& card) {  // NOLINT
  if (card != nullptr) {
    card->deactivate();
    delete card;
    card = nullptr;
  }
}

void AndroidWrapper::deactivate_contactless(JNIEnv* env, jobject thiz) {
  deactivate_card(cl_card_);
  credentials_received_ = false;
  ReleaseGlobalReferences(env);
}

void AndroidWrapper::deactivate_remote(JNIEnv* env, jobject thiz) {
  deactivate_card(rp_card_);
}


jobject AndroidWrapper::transaction_record(JNIEnv* env, jobject thiz,
                                           jobject dsrp_in) {
  using mcbp_core::DsrpInputData;
  using mcbp_core::DsrpOutputData;

  if (env == nullptr)
    ThrowJavaException(env, "ENV is null");

  if (rp_card_ == nullptr)
    ThrowJavaException(env, "The Remote Payment Card has not been initialized");

  if (dsrp_in == nullptr)
    ThrowJavaException(env, "Invalid DSRP Input data");

  DsrpOutputData dsrp_output;

  try {
    DsrpInputData dsrp_input;
    ReadDsrpInput(env, dsrp_in, &dsrp_input);

    dsrp_output = rp_card_->transaction_record(dsrp_input);
  } catch (const mcbp_core::Exception& e) {
    ThrowJavaException(env, e.what());
  }

  return FormatDsrpOutput(env, dsrp_output);
}

AndroidWrapper::~AndroidWrapper() {
  deactivate_contactless(nullptr, nullptr);
  deactivate_remote(nullptr, nullptr);
  // Delete Global References
  if (g_java_vm_ != nullptr) {
    JNIEnv *env = nullptr;
    if (g_java_vm_->GetEnv(reinterpret_cast<void**>(&env),
                                        JNI_VERSION_1_6) == JNI_OK) {
      ReleaseGlobalReferences(env);
    }
    g_java_vm_ = nullptr;
  }
}

void AndroidWrapper::on_event(
    const mcbp_core::ContactlessTransactionContext& context) {
  // The transaction has been completed (in a way or another) and credentials
  // are no longer valid
  credentials_received_ = false;
  JNIEnv *env = nullptr;
  if (g_java_vm_ == nullptr) throw mcbp_core::Exception("jvm is nullptr");

  // Now get the env variable
  if (g_java_vm_->GetEnv(reinterpret_cast<void**>(&env),
                         JNI_VERSION_1_6) != JNI_OK)
    throw mcbp_core::Exception("Failed to get the environment using GetEnv()");

  jbyteArray atc = CreateJavaByteArray(env, context.atc);
  jbyteArray amount = CreateJavaByteArray(env, context.amount);
  jbyteArray currency_code = CreateJavaByteArray(env, context.currency_code);
  jbyteArray transaction_date =
        CreateJavaByteArray(env, context.transaction_date);
  jbyteArray transaction_type =
        CreateJavaByteArray(env, ByteArray(1, context.transaction_type));
  jbyteArray unpredictable_number =
        CreateJavaByteArray(env, context.unpredictable_number);
  jbyte cid = reinterpret_cast<jint>(static_cast<int>(context.cid));
  jbyteArray application_cryptogram =
                      CreateJavaByteArray(env, context.application_cryptogram);

  // TODO(andrea): Context.result is converted to int instead that using Java
  // enumerator. It would be nice to use Enum, but so far it has been
  // challenging to get it to work
  jint result = static_cast<jint>(CreateJavaContextType(env, context.result));

  // Create the Java Object
  std::string cls_name =
      "com/mastercard/mcbp/card/mpplite/mcbpv1/ContactlessLogImpl";
  jclass cls = env->FindClass(cls_name.c_str());
  CheckForJavaExceptions(env);

  if (cls == nullptr)
    ThrowJavaException(env, (cls_name + " class not found").c_str());

  jmethodID constructor = env->GetMethodID(cls, "<init>",
                                           "([B[B[B[B[B[BI)V");
  if (constructor == nullptr)
    ThrowJavaException(env, (cls_name + " constructor not found").c_str());

  CheckForJavaExceptions(env);

  jobject trx_log = env->NewObject(cls, constructor,
                                   unpredictable_number,
                                   atc,
                                   application_cryptogram,
                                   transaction_date,
                                   amount,
                                   currency_code,
                                   result);
  CheckForJavaExceptions(env);

  // Call Back to Java
  jclass cls_callback = env->GetObjectClass(cl_listener_);
  CheckForJavaExceptions(env);

  if (cls_callback == nullptr)
    ThrowJavaException(env, "CL Listener Class not found");

  jmethodID method_callback =
    env->GetMethodID(cls_callback, "onContactlessTransactionCompleted",
      "(Lcom/mastercard/mcbp/card/mpplite/ContactlessLog;)V");
  CheckForJavaExceptions(env);

  if (method_callback == nullptr)
    ThrowJavaException(env,
      "Method onContactlessTransactionCompleted not found");
  env->CallVoidMethod(cl_listener_, method_callback, trx_log);
  CheckForJavaExceptions(env);
}

jboolean AndroidWrapper::credentials_received(JNIEnv* env, jobject thiz) {
  return static_cast<jboolean>(credentials_received_);
}

/*******************************************************************************
 * UTILITY FUNCTIONS 
 ******************************************************************************/

void AndroidWrapper::ReadCardRiskManagementData(JNIEnv* env,
          jobject card_profile_obj, mcbp_core::CardProfileData* profile) {
  if (card_profile_obj == nullptr)
    mcbp_android::
          ThrowJavaException(env, "card_profile_obj is nullptr");

  jobject card_risk_obj = mcbp_android::GetJavaObjectFromMethod(
      env,
      card_profile_obj,
      "getCardRiskManagementData",
      "()Lcom/mastercard/mcbp/card/profile/CardRiskManagementData;");
  CheckForJavaExceptions(env);

  profile->additional_check_table =
        GetAndroidByteArrayFromVoidMethod(env, card_risk_obj,
                                          "getAdditionalCheckTable");

  profile->crm_country_code =
    GetAndroidByteArrayFromVoidMethod(env, card_risk_obj, "getCrmCountryCode");
}

void AndroidWrapper::ReadContactlessData(JNIEnv* env,
                                         jobject card_profile_obj,
                                         mcbp_core::CardProfileData* profile) {
  using mcbp_core::ByteArrayToString;
  if (card_profile_obj == nullptr)
    mcbp_android::
          ThrowJavaException(env, "card_profile_obj is nullptr");

  jobject cl_data_obj = mcbp_android::GetJavaObjectFromMethod(
      env,
      card_profile_obj,
      "getContactlessPaymentData",
      "()Lcom/mastercard/mcbp/card/profile/ContactlessPaymentData;");
  CheckForJavaExceptions(env);

  if (cl_data_obj == nullptr) {
    profile->cl_supported = false;
    return;
  }
  profile->cl_supported = true;

  profile->cl_aid =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj, "getAid");

  profile->cl_ppse_fci =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj, "getPpseFci");

  profile->cl_payment_fci =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj, "getPaymentFci");

  profile->cl_gpo_response =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj, "getGpoResponse");

  int cdol1_related_data_length =
      GetIntFromVoidMethod(env, cl_data_obj, "getCdol1RelatedDataLength");
  if (cdol1_related_data_length < 0) cdol1_related_data_length = 0;
  profile->cl_cdol1_related_data_length =
      ByteArray(1, static_cast<Byte>(cdol1_related_data_length));

  profile->cl_ciac_decline =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj, "getCiacDecline");

  profile->cl_cvr_mask_and =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj, "getCvrMaskAnd");

  profile->cl_issuer_application_data =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj,
                                        "getIssuerApplicationData");

  jobject icc_data_obj = mcbp_android::GetJavaObjectFromMethod(
      env,
      cl_data_obj,
      "getIccPrivateKeyCrtComponents",
      "()Lcom/mastercard/mcbp/card/profile/IccPrivateKeyCrtComponents;");
  CheckForJavaExceptions(env);

  profile->cl_icc_private_key_a =
      ByteArrayToString(
          GetAndroidByteArrayFromVoidMethod(env, icc_data_obj,
                                            "getU"));

  profile->cl_icc_private_key_p =
      ByteArrayToString(
          GetAndroidByteArrayFromVoidMethod(env, icc_data_obj,
                                            "getP"));

  profile->cl_icc_private_key_q =
      ByteArrayToString(
          GetAndroidByteArrayFromVoidMethod(env, icc_data_obj,
                                            "getQ"));

  profile->cl_icc_private_key_dp =
      ByteArrayToString(
          GetAndroidByteArrayFromVoidMethod(env, icc_data_obj,
                                            "getDp"));

  profile->cl_icc_private_key_dq =
      ByteArrayToString(
          GetAndroidByteArrayFromVoidMethod(env, icc_data_obj,
                                            "getDq"));

  profile->cl_pin_iv_cvc3_track2 =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj,
                                        "getPinIvCvc3Track2");

  profile->cl_ciac_decline_on_ppms =
      GetAndroidByteArrayFromVoidMethod(env, cl_data_obj,
                                        "getCiacDeclineOnPpms");

  ReadRecords(env, cl_data_obj, profile);

  jobject alt_cl_obj = mcbp_android::GetJavaObjectFromMethod(
     env,
     cl_data_obj,
     "getAlternateContactlessPaymentData",
     "()Lcom/mastercard/mcbp/card/profile/AlternateContactlessPaymentData;");  // NOLINT(whitespace/line_length)
  CheckForJavaExceptions(env);

  if (alt_cl_obj != nullptr) {
    profile->alt_aid =
        GetAndroidByteArrayFromVoidMethod(env, alt_cl_obj, "getAid");

    profile->alt_payment_fci =
        GetAndroidByteArrayFromVoidMethod(env, alt_cl_obj, "getPaymentFci");

    profile->alt_gpo_response =
        GetAndroidByteArrayFromVoidMethod(env, alt_cl_obj, "getGpoResponse");

    profile->alt_ciac_decline =
        GetAndroidByteArrayFromVoidMethod(env, alt_cl_obj, "getCiacDecline");

    profile->alt_cvr_mask_and =
        GetAndroidByteArrayFromVoidMethod(env, alt_cl_obj, "getCvrMaskAnd");
  } else {
    mcbp_core::Log::instance()->d("Alternate CL Data is nullptr");
  }
}

void AndroidWrapper::ReadRemotePaymentData(
        JNIEnv* env,
        jobject card_profile_obj,
        mcbp_core::CardProfileData* profile) {
  jobject rp_data_obj = mcbp_android::GetJavaObjectFromMethod(
      env,
      card_profile_obj,
      "getRemotePaymentData",
      "()Lcom/mastercard/mcbp/card/profile/RemotePaymentData;");
  CheckForJavaExceptions(env);

  if (rp_data_obj == nullptr) {
    profile->rp_supported = false;
    return;
  }
  profile->rp_supported = true;
  profile->rp_track2_equivalent_data =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj,
                                        "getTrack2EquivalentData");
  profile->rp_pan =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj, "getPan");

  profile->rp_pan_sequence_number =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj,
                                        "getPanSequenceNumber");

  profile->rp_application_expiry_date =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj,
                                        "getApplicationExpiryDate");

  profile->rp_aip =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj, "getAip");

  profile->rp_ciac_decline =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj, "getCiacDecline");

  profile->rp_cvr_mask_and =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj, "getCvrMaskAnd");

  profile->rp_issuer_application_data =
      GetAndroidByteArrayFromVoidMethod(env, rp_data_obj,
                                        "getIssuerApplicationData");
}

void AndroidWrapper::ReadRecords(JNIEnv* env, jobject cl_data_obj,
                                 mcbp_core::CardProfileData* profile) {
    jobjectArray records = static_cast<jobjectArray>(
      mcbp_android::GetJavaObjectFromMethod(
          env, cl_data_obj, "getRecords",
          "()[Lcom/mastercard/mcbp/card/profile/Record;"));
  CheckForJavaExceptions(env);

  if (records == nullptr)
    mcbp_android::ThrowJavaException(env, "Empty Records?");

  jsize array_size = env->GetArrayLength(records);
  CheckForJavaExceptions(env);

  for (std::size_t i = 0; i < array_size; i++) {
    jobject record =
        static_cast<jobject>(env->GetObjectArrayElement(records, i));
    CheckForJavaExceptions(env);

    Byte record_number = GetByteFromVoidMethod(env, record, "getRecordNumber");

    Byte sfi = GetByteFromVoidMethod(env, record, "getSfi");

    ByteArray record_value =
        GetAndroidByteArrayFromVoidMethod(env, record, "getRecordValue");

    profile->records.insert(std::pair<uint16_t, ByteArray>(
                                  mcbp_core::RecordId(sfi, record_number),
                                  record_value));
  }
}

void AndroidWrapper::ReadKeys(JNIEnv* env, jobject suk_obj,
                              mcbp_core::KeysData* keys) {
  if (env == nullptr || suk_obj == nullptr || keys == nullptr) return;

  keys->atc = GetAndroidByteArrayFromVoidMethod(env, suk_obj, "getAtc");
  keys->idn = GetAndroidByteArrayFromVoidMethod(env, suk_obj, "getIdn");
  keys->sk_cl_umd =
      GetAndroidByteArrayFromVoidMethod(env, suk_obj, "getUmdSessionKey");
  keys->sk_cl_md =
      GetAndroidByteArrayFromVoidMethod(env, suk_obj, "getMdSessionKey");
  keys->sk_rp_umd =
      GetAndroidByteArrayFromVoidMethod(env, suk_obj, "getUmdSessionKey");
  keys->sk_rp_md =
      GetAndroidByteArrayFromVoidMethod(env, suk_obj, "getMdSessionKey");
}

void AndroidWrapper::ReadDsrpInput(JNIEnv* env, jobject input_obj,
                              mcbp_core::DsrpInputData* dsrp_in) {
  if (env == nullptr)
    ThrowJavaException(env, "env is null");

  if (input_obj == nullptr)
    ThrowJavaException(env, "Invalid DSRP Input Data");

  if (dsrp_in == nullptr)
    return;

  dsrp_in->transaction_amount =
        GetLongFromVoidMethod(env, input_obj, "getTransactionAmount");

  dsrp_in->other_amount =
        GetLongFromVoidMethod(env, input_obj, "getOtherAmount");

  dsrp_in->currency_code =
        GetCharFromVoidMethod(env, input_obj, "getCurrencyCode");

  dsrp_in->transaction_type =
        GetByteFromVoidMethod(env, input_obj, "getTransactionType");

  dsrp_in->unpredictable_number =
        GetUnsignedFromVoidMethod(env, input_obj, "getUnpredictableNumber");

  Byte cryptogram_type =
        GetByteFromVoidMethod(env, input_obj, "getCryptogramTypeAsByte");

  if (cryptogram_type == 0x01) {
    dsrp_in->cryptogram_type = mcbp_core::DsrpTransactionType::UCAF;
  } else if (cryptogram_type == 0x02) {
    dsrp_in->cryptogram_type = mcbp_core::DsrpTransactionType::DE55;
  } else {
    dsrp_in->cryptogram_type = mcbp_core::DsrpTransactionType::UNDEFINED;
  }

  dsrp_in->day =
    static_cast<uint8_t>(GetByteFromVoidMethod(env, input_obj, "getDateDay"));

  dsrp_in->month =
    static_cast<uint8_t>(GetByteFromVoidMethod(env, input_obj, "getDateMonth"));

  dsrp_in->year =
    static_cast<uint16_t>(GetCharFromVoidMethod(env, input_obj, "getDateYear"));

  dsrp_in->country_code =
        GetCharFromVoidMethod(env, input_obj, "getCountryCode");
}

jobject AndroidWrapper::FormatDsrpOutput(
      JNIEnv* env,
      const mcbp_core::DsrpOutputData& dsrp_out) {
  if (env == nullptr) return nullptr;

  std::string cls_name = "com/mastercard/mcbp/card/mobilekernel/DsrpOutputData";
  jclass cls = env->FindClass(cls_name.c_str());

  if (cls == nullptr)
    ThrowJavaException(env, (cls_name + " not found").c_str());
  jmethodID constructor = env->GetMethodID(cls, "<init>",
                          "(Ljava/lang/String;I[B[B[BIJIIJB)V");
  CheckForJavaExceptions(env);

  if (constructor == nullptr)
    ThrowJavaException(env, (cls_name + " constructor not found").c_str());

  jstring pan = env->NewStringUTF(dsrp_out.pan.c_str());
  jint pan_sequence_number = static_cast<jint>(dsrp_out.pan_sequence_number);
  jbyteArray expiry_date = CreateJavaByteArray(env, dsrp_out.expiry_date);
  jbyteArray cryptogram_data =
                CreateJavaByteArray(env, dsrp_out.transaction_cryptogram_data);
  jbyteArray cryptogram =
                CreateJavaByteArray(env, dsrp_out.cryptogram);
  jint ucaf_version = static_cast<jint>(dsrp_out.ucaf_version);
  jlong transaction_amount = static_cast<jlong>(dsrp_out.transaction_amount);
  jchar currency_code = static_cast<jchar>(dsrp_out.currency_code);
  jchar atc = static_cast<jchar>(dsrp_out.atc);
  jlong unpredictable_number =
          static_cast<jlong>(dsrp_out.unpredictable_number);
  jbyte cryptogram_type = static_cast<jbyte>(dsrp_out.cryptogram_type);

  jobject result =
      env->NewObject(cls, constructor, pan,
                     pan_sequence_number, expiry_date, cryptogram,
                     cryptogram_data, ucaf_version, transaction_amount,
                     currency_code, atc, unpredictable_number, cryptogram_type);
  CheckForJavaExceptions(env);
  return result;
}

}  // namespace mcbp_android

#endif  // End of __ANDROID__
