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
#include <wrappers/crypto_service_wrapper.h>
#include <wrappers/android_wrapper_utils.h>
#include <cryptoservice/crypto_functions.h>
#include <utils/utilities.h>
#include <utils/mcbp_core_exception.h>

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
  return mcbp_android::AndroidCryptoService::instance()->on_load(vm, reserved);
}

}  // end of extern "C" { }

/*******************************************************************************
 * JNI Methods' mapping
 ******************************************************************************/
using mcbp_android::AndroidCryptoService;

static JNINativeMethod sMethods[] = {
  {"generate_ac",
    "([B[B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->generate_ac)},
  {"compute_cc",
    "([B[B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->compute_cc)},
  {"unlock_session_key",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->unlock_session_key)},
  {"decrypt_mobile_keys",
    "([B[B[B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->decrypt_mobile_keys)},
  {"decrypt_notification_data",
    "([B[B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->decrypt_notification_data)},
  {"lde_encryption",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->lde_encryption)},
  {"lde_decryption",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->lde_decryption)},
  {"encrypt_retry_request_data",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->encrypt_retry_request_data)},
  {"decrypt_retry_request_data",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->decrypt_retry_request_data)},
  {"build_service_request",
    "([B[B[B[BI)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->build_service_request)},
  {"decrypt_service_response",
    "([B[B[B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->decrypt_service_response)},
  {"decrypt_icc_component",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->decrypt_icc_component)},
{"decrypt_icc_kek",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->decrypt_icc_kek)},
  {"calculate_authentication_code",
    "([B[B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->calculate_authentication_code)},
  {"encrypt_pin_block",
    "([B[B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->encrypt_pin_block)},
  {"decrypt_data_encrypted_field",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->decrypt_data_encrypted_field)},
  {"encrypt_random_generated_key",
    "([B[B)[B",
    reinterpret_cast<void*>(
      &AndroidCryptoService::instance()->encrypt_random_generated_key)},
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
 * AndroidCryptoService Static members' initialization
 ******************************************************************************/

AndroidCryptoService* AndroidCryptoService::instance_ = nullptr;
JavaVM *AndroidCryptoService::g_java_vm_              = nullptr;

// Create the only instance of the singletone object
std::once_flag flag_mcbp_core_android_wrapper;

void CreateAndroidCryptoService() {
  if (AndroidCryptoService::instance_ != nullptr) return;
  AndroidCryptoService::instance_ = new AndroidCryptoService();
}

AndroidCryptoService* AndroidCryptoService::instance() {
  if (instance_ == nullptr)
    std::call_once(flag_mcbp_core_android_wrapper, CreateAndroidCryptoService);
  return instance_;
}

using mcbp_core::Wipe;
using mcbp_android::ThrowJavaException;
using mcbp_android::ThrowMcbpCryptoException;

/*******************************************************************************
 * AndroidCryptoService Members' definition
 ******************************************************************************/

jint JNICALL AndroidCryptoService::on_load(JavaVM * vm, void * reserved) {
  JNIEnv *env;

  // Get the JVM
  g_java_vm_ = vm;

  // Now get the env variable
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK)
    return -1;

  // Register methods with env->RegisterNatives.
  size_t no_elems = static_cast<size_t>(sizeof(sMethods) / sizeof(sMethods[0]));
  jniRegisterNativeMethods(
        env, "com/mastercard/mcbp/utils/crypto/CryptoServiceNativeImpl",
        sMethods, no_elems);
  CheckForJavaExceptions(env);

  return JNI_VERSION_1_6;
}

void AndroidCryptoService::check_environment(JNIEnv* env) {
  if (env == nullptr) ThrowJavaException(env, "Env is null");
}

jbyteArray AndroidCryptoService::generate_ac(JNIEnv* env,
                                             jobject thiz,
                                             const jbyteArray cryptogram_input,
                                             const jbyteArray umd_session_key,
                                             const jbyteArray md_session_key) {
  check_environment(env);
  try {
    ByteArray input = ConvertJByteArray(env, cryptogram_input);
    ByteArray umd_key = ConvertJByteArray(env, umd_session_key);
    ByteArray md_key  = ConvertJByteArray(env, md_session_key);

    ByteArray cryptograms =
        mcbp_crypto_service::generate_ac(input, umd_key, md_key);
    jbyteArray result = CreateJavaByteArray(env, cryptograms);

    Wipe(&input);
    Wipe(&umd_key);
    Wipe(&md_key);
    Wipe(&cryptograms);

    return result;
   } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::compute_cc(JNIEnv* env, jobject thiz,
                                            const jbyteArray cryptogram_input,
                                            const jbyteArray umd_session_key,
                                            const jbyteArray md_session_key) {
  check_environment(env);
  try {
    ByteArray input = ConvertJByteArray(env, cryptogram_input);
    ByteArray umd_key = ConvertJByteArray(env, umd_session_key);
    ByteArray md_key  = ConvertJByteArray(env, md_session_key);

    ByteArray cryptograms =
        mcbp_crypto_service::compute_cc(input, umd_key, md_key);
    jbyteArray result = CreateJavaByteArray(env, cryptograms);

    Wipe(&input);
    Wipe(&umd_key);
    Wipe(&md_key);
    Wipe(&cryptograms);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::unlock_session_key(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray single_use_key,
    const jbyteArray mobile_pin) {
  check_environment(env);
  try {
    ByteArray key = ConvertJByteArray(env, single_use_key);
    ByteArray pin = ConvertJByteArray(env, mobile_pin);

    ByteArray session = mcbp_crypto_service::unlock_session_key(key, pin);
    jbyteArray result = CreateJavaByteArray(env, session);

    Wipe(&key);
    Wipe(&pin);
    Wipe(&session);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::decrypt_mobile_keys(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray encrypted_transport_key,
    const jbyteArray encrypted_mac_key,
    const jbyteArray encrypted_data_encr_key,
    const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray enc_tran_key = ConvertJByteArray(env, encrypted_transport_key);
    ByteArray enc_mac_key = ConvertJByteArray(env, encrypted_mac_key);
    ByteArray enc_dek_key = ConvertJByteArray(env, encrypted_data_encr_key);
    ByteArray dec_key = ConvertJByteArray(env, key);

    const mcbp_crypto_service::MobileKeys mobile_keys =
        mcbp_crypto_service::decrypt_mobile_keys(enc_tran_key,
                                                 enc_mac_key,
                                                 enc_dek_key,
                                                 dec_key);
    ByteArray tran_key = mobile_keys.get_transport_key();
    ByteArray mac_key = mobile_keys.get_mac_key();
    ByteArray dek_key = mobile_keys.get_data_encryption_key();
    const size_t all_keys_size =
        tran_key.size() + mac_key.size() + dek_key.size();
    if (all_keys_size != 48) {
      ThrowMcbpCryptoException(env, "Invalid mobile keys");
    }
    ByteArray keys;
    keys.reserve(all_keys_size);
    keys.insert(keys.end(), tran_key.begin(), tran_key.end());
    keys.insert(keys.end(), mac_key.begin(),  mac_key.end());
    keys.insert(keys.end(), dek_key.begin(),  dek_key.end());
    jbyteArray result = CreateJavaByteArray(env, keys);

    Wipe(&enc_tran_key);
    Wipe(&enc_mac_key);
    Wipe(&enc_dek_key);
    Wipe(&tran_key);
    Wipe(&mac_key);
    Wipe(&dek_key);
    Wipe(&keys);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::decrypt_notification_data(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray response_data,
    const jbyteArray mac_key,
    const jbyteArray transport_key) {
  check_environment(env);
  try {
    ByteArray my_response_data = ConvertJByteArray(env, response_data);
    ByteArray my_mac_key = ConvertJByteArray(env, mac_key);
    ByteArray my_transport_key = ConvertJByteArray(env, transport_key);

    using mcbp_crypto_service::decrypt_notification_data;
    ByteArray notification_data = decrypt_notification_data(my_response_data,
                                                            my_mac_key,
                                                            my_transport_key);
    jbyteArray result = CreateJavaByteArray(env, notification_data);

    Wipe(&my_response_data);
    Wipe(&my_mac_key);
    Wipe(&my_transport_key);
    Wipe(&notification_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::lde_encryption(JNIEnv* env,
                                   jobject thiz,
                                   const jbyteArray data,
                                   const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::lde_encryption;
    ByteArray encrypted_data = lde_encryption(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, encrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&encrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::lde_decryption(JNIEnv* env,
                                   jobject thiz,
                                   const jbyteArray data,
                                   const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::lde_decryption;
    ByteArray decrypted_data = lde_decryption(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, decrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&decrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::encrypt_retry_request_data(JNIEnv* env,
                                               jobject thiz,
                                               const jbyteArray data,
                                               const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::encrypt_retry_request_data;
    ByteArray encrypted_data = encrypt_retry_request_data(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, encrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&encrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::decrypt_retry_request_data(JNIEnv* env,
                                               jobject thiz,
                                               const jbyteArray data,
                                               const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::decrypt_retry_request_data;
    ByteArray decrypted_data = decrypt_retry_request_data(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, decrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&decrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::build_service_request(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray data,
    const jbyteArray mac_key,
    const jbyteArray transport_key,
    const jbyteArray session_code,
    const int counter) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_mac_key = ConvertJByteArray(env, mac_key);
    ByteArray my_transport_key = ConvertJByteArray(env, transport_key);
    ByteArray my_session_code = ConvertJByteArray(env, session_code);

    using mcbp_crypto_service::build_service_request;
    ByteArray service_request = build_service_request(my_data,
                                                      my_mac_key,
                                                      my_transport_key,
                                                      my_session_code,
                                                      counter);
    jbyteArray result = CreateJavaByteArray(env, service_request);

    Wipe(&my_data);
    Wipe(&my_mac_key);
    Wipe(&my_transport_key);
    Wipe(&my_session_code);
    Wipe(&service_request);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::decrypt_service_response(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray service_data,
    const jbyteArray mac_key,
    const jbyteArray transport_key,
    const jbyteArray session_code) {
  check_environment(env);
  try {
    ByteArray my_service_data = ConvertJByteArray(env, service_data);
    ByteArray my_mac_key = ConvertJByteArray(env, mac_key);
    ByteArray my_transport_key = ConvertJByteArray(env, transport_key);
    ByteArray my_session_code = ConvertJByteArray(env, session_code);

    using mcbp_crypto_service::decrypt_service_response;
    ByteArray service_response = decrypt_service_response(my_service_data,
                                                          my_mac_key,
                                                          my_transport_key,
                                                          my_session_code);
    jbyteArray result = CreateJavaByteArray(env, service_response);

    Wipe(&my_service_data);
    Wipe(&my_mac_key);
    Wipe(&my_transport_key);
    Wipe(&my_session_code);
    Wipe(&service_response);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::decrypt_icc_component(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray data,
    const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::decrypt_icc_component;
    ByteArray decrypted_data = decrypt_icc_component(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, decrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&decrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::decrypt_icc_kek(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray data,
    const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::decrypt_icc_kek;
    ByteArray decrypted_data = decrypt_icc_kek(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, decrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&decrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::calculate_authentication_code(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray mobile_keyset_id,
    const jbyteArray device_finger_print,
    const jbyteArray session_code) {
  check_environment(env);
  try {
    ByteArray my_mobile_key_id = ConvertJByteArray(env, mobile_keyset_id);
    ByteArray my_dev_finger_print = ConvertJByteArray(env, device_finger_print);
    ByteArray my_session_code = ConvertJByteArray(env, session_code);

    using mcbp_crypto_service::calculate_authentication_code;
    ByteArray decrypted_data =
        calculate_authentication_code(my_mobile_key_id,
                                      my_dev_finger_print,
                                      my_session_code);
    jbyteArray result = CreateJavaByteArray(env, decrypted_data);

    Wipe(&my_mobile_key_id);
    Wipe(&my_dev_finger_print);
    Wipe(&my_session_code);
    Wipe(&decrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::encrypt_pin_block(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray pin_data,
    const jbyteArray payment_instance_id,
    const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_pin_data = ConvertJByteArray(env, pin_data);
    ByteArray my_payment_instance_id =
        ConvertJByteArray(env, payment_instance_id);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::encrypt_pin_block;
    ByteArray encrypted_pin_block =
        encrypt_pin_block(my_pin_data,
                          my_payment_instance_id,
                          my_key);
    jbyteArray result = CreateJavaByteArray(env, encrypted_pin_block);

    Wipe(&my_pin_data);
    Wipe(&my_payment_instance_id);
    Wipe(&my_key);
    Wipe(&encrypted_pin_block);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::decrypt_data_encrypted_field(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray data,
    const jbyteArray key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, data);
    ByteArray my_key = ConvertJByteArray(env, key);

    using mcbp_crypto_service::decrypt_data_encrypted_field;
    ByteArray decrypted_data = decrypt_data_encrypted_field(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, decrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&decrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

jbyteArray AndroidCryptoService::encrypt_random_generated_key(
    JNIEnv* env,
    jobject thiz,
    const jbyteArray random_generated_key,
    const jbyteArray encryption_key) {
  check_environment(env);
  try {
    ByteArray my_data = ConvertJByteArray(env, random_generated_key);
    ByteArray my_key = ConvertJByteArray(env, encryption_key);

    using mcbp_crypto_service::encrypt_random_generated_key;
    ByteArray encrypted_data = encrypt_random_generated_key(my_data, my_key);
    jbyteArray result = CreateJavaByteArray(env, encrypted_data);

    Wipe(&my_data);
    Wipe(&my_key);
    Wipe(&encrypted_data);

    return result;
  } catch (mcbp_core::Exception e) {
    ThrowMcbpCryptoException(env, e.what());
  }
}

}  // namespace mcbp_android

#endif  // End of __ANDROID__
