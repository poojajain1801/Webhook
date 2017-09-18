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

#include <wrappers/android_wrapper_utils.h>
#include <utils/byte_array.h>
#include <log/log.h>

#include <jni.h>

#include <string>
#include <map>
#include <vector>

namespace mcbp_android {

std::string ConvertJString(JNIEnv* const env, const jstring str) {
  if (!str) return std::string("");

  const jsize len = env->GetStringUTFLength(str);
  const char* strChars =
                  env->GetStringUTFChars(str, reinterpret_cast<jboolean*>(0));

  std::string result(strChars, len);
  env->ReleaseStringUTFChars(str, strChars);

  return result;
}

ByteArray ConvertJByteArray(JNIEnv* const env, const jbyteArray array) {
  if (array == nullptr) return ByteArray();

  jsize array_size = env->GetArrayLength(array);
  CheckForJavaExceptions(env);
  ByteArray result(array_size, 0x00);

  jbyte *bytes = env->GetByteArrayElements(array, 0);
  CheckForJavaExceptions(env);
  for (std::size_t i = 0; i < array_size; i++)
    result[i] = bytes[i];

  env->ReleaseByteArrayElements(array, bytes, 0);
  return result;
}

void ThrowJavaException(JNIEnv* const env, const char* const msg) {
  jclass c = env->FindClass("com/mastercard/mcbp/utils/exceptions/mpplite/MppLiteException");  // NOLINT
  CheckForJavaExceptions(env);
  // if (c == nullptr) c = env->FindClass("java/lang/RuntimeException");
  // CheckForJavaExceptions(env);
  env->ThrowNew(c, msg);
}

void ThrowMcbpCryptoException(JNIEnv* const env, const char* const msg) {
  jclass c = env->FindClass("com/mastercard/mcbp/utils/exceptions/crypto/McbpCryptoException");  // NOLINT
  CheckForJavaExceptions(env);
  env->ThrowNew(c, msg);
}

/**
 * Get a Java Object from a method of a given container object
 **/
jobject GetJavaObjectFromMethod(JNIEnv* const env,
                                const jobject container_obj,
                                const std::string& method_name,
                                const std::string& method_signature) {
  if (container_obj == nullptr)
    ThrowJavaException(env, "Container object is nullptr!");

  jclass container_cls = env->GetObjectClass(container_obj);
  CheckForJavaExceptions(env);

  if (container_cls == nullptr)
    ThrowJavaException(env, "Class not found - is this a Java Object?");

  jmethodID method_id = env->GetMethodID(container_cls, method_name.c_str(),
                                         method_signature.c_str());
  CheckForJavaExceptions(env);

  if (method_id == nullptr)
    ThrowJavaException(env, (method_name + " not found").c_str());

  jobject object = static_cast<jobject>(
                      env->CallObjectMethod(container_obj, method_id));
  CheckForJavaExceptions(env);

  if (object == nullptr)
    ThrowJavaException(
        env, ("Object not found using method: " + method_name).c_str());

  return object;
}

std::string GetStringFromVoidMethod(JNIEnv* const env,
                                    const jobject container_obj,
                                    const std::string& method_name) {
  if (env == nullptr) ThrowJavaException(env, "ENV is nullptr");
  if (container_obj == nullptr) ThrowJavaException(env, "Object is nullptr");

  jstring j_str = static_cast<jstring>(
                      GetJavaObjectFromMethod(env, container_obj, method_name,
                                              "()Ljava/lang/String;"));
  std::string str = mcbp_android::ConvertJString(env, j_str);
  return str;
}

ByteArray ConvertAndroidByteArray(JNIEnv* const env,
                                  const jobject android_byte_array) {
  if (env == nullptr)
    ThrowJavaException(env, "ENV is nullptr");
  if (android_byte_array == nullptr)
    ThrowJavaException(env, "Object is nullptr");

  jbyteArray array =
      static_cast<jbyteArray>(GetJavaObjectFromMethod(env, android_byte_array,
                                                      "getBytes", "()[B"));
  return ConvertJByteArray(env, array);
}

ByteArray GetAndroidByteArrayFromVoidMethod(JNIEnv* const env,
                                            const jobject container_obj,
                                            const std::string& method_name) {
  if (env == nullptr) ThrowJavaException(env, "ENV is nullptr");
  if (container_obj == nullptr) ThrowJavaException(env, "Object is nullptr");

  jobject my_obj = static_cast<jobject>(
      GetJavaObjectFromMethod(env, container_obj, method_name,
                              "()Lcom/mastercard/mobile_api/bytes/ByteArray;"));

  if (my_obj == nullptr)
    ThrowJavaException(env, "Invalid Object - is it a ByteArrayInterface?");

  return ConvertAndroidByteArray(env, my_obj);
}

int CreateJavaContextType(JNIEnv* const env,
                              const mcbp_core::ContextType& context) {
  if (env == nullptr) ThrowJavaException(env, "ENV is nullptr");
  using mcbp_core::ContextType;

  mcbp_core::Log* log = mcbp_core::Log::instance();

  jfieldID contextField = static_cast<jfieldID>(0);
  switch (context) {
    case ContextType::MCHIP_FIRST_TAP:
      log->d("ContextType: MCHIP_FIRST_TAP");
      return 1;
    case ContextType::MCHIP_COMPLETED:
      log->d("ContextType: MCHIP_COMPLETED");
      return 2;
    case ContextType::MAGSTRIPE_FIRST_TAP:
      log->d("ContextType: MAGSTRIPE_FIRST_TAP");
      return 3;
    case ContextType::MAGSTRIPE_COMPLETED:
      log->d("ContextType: MAGSTRIPE_COMPLETED");
      return 4;
    case ContextType::CONTEXT_CONFLICT:
      log->d("ContextType: CONTEXT_CONFLICT");
      return 5;
    case ContextType::UNSUPPORTED_TRANSIT:
      log->d("ContextType: UNSUPPORTED_TRANSIT");
      return 6;
    case ContextType::MAGSTRIPE_DECLINED:
      log->d("ContextType: MAGSTRIPE_DECLINED");
      return 7;
    case ContextType::MCHIP_DECLINED:
      log->d("ContextType: MCHIP_DECLINED");
      return 8;
    default:
      break;
  }
  return 0;
}

jbyteArray CreateJavaByteArray(JNIEnv* const env, const ByteArray& source) {
  const unsigned int size = source.size();
  std::vector<jbyte> data(size);

  for (std::size_t i = 0; i < size; i++)
    data[i] = source[i];

  jbyteArray destination = env->NewByteArray(size);
  CheckForJavaExceptions(env);

  if (destination == nullptr) return nullptr; /* out of memory error thrown */

  env->SetByteArrayRegion(destination, 0, size, &data[0]);
  CheckForJavaExceptions(env);

  return destination;
}

int64_t GetLongFromVoidMethod(JNIEnv* const env,
                              const jobject container_obj,
                              const std::string& method_name) {
  if (env == nullptr) ThrowJavaException(env, "ENV is nullptr");
  if (container_obj == nullptr) ThrowJavaException(env, "Object is nullptr");

  jclass container_cls = env->GetObjectClass(container_obj);
  CheckForJavaExceptions(env);

  if (container_cls == nullptr)
          ThrowJavaException(env, "Class not found - is this a Java Object?");

  jmethodID method_id = env->GetMethodID(container_cls,
                                         method_name.c_str(), "()J");
  CheckForJavaExceptions(env);

  if (method_id == nullptr)
    ThrowJavaException(env, (method_name + " not found").c_str());

  jlong j_long = static_cast<jlong>(
                      env->CallLongMethod(container_obj, method_id));
  CheckForJavaExceptions(env);

  return static_cast<int64_t>(j_long);
}

int GetIntFromVoidMethod(JNIEnv* const env,
                         const jobject container_obj,
                         const std::string& method_name) {
  if (env == nullptr) ThrowJavaException(env, "ENV is nullptr");
  if (container_obj == nullptr) ThrowJavaException(env, "Object is nullptr");

  jclass container_cls = env->GetObjectClass(container_obj);
  CheckForJavaExceptions(env);

  if (container_cls == nullptr)
          ThrowJavaException(env, "Class not found - is this a Java Object?");

  jmethodID method_id = env->GetMethodID(container_cls,
                                         method_name.c_str(), "()I");
  CheckForJavaExceptions(env);

  if (method_id == nullptr)
    ThrowJavaException(env, (method_name + " not found").c_str());

  jint j_int = static_cast<jint>(
                      env->CallIntMethod(container_obj, method_id));
  CheckForJavaExceptions(env);

  return static_cast<int>(j_int);
}

uint16_t GetCharFromVoidMethod(JNIEnv* const env, const jobject container_obj,
                               const std::string& method_name) {
  if (env == nullptr) ThrowJavaException(env, "ENV is nullptr");
  if (container_obj == nullptr) ThrowJavaException(env, "Object is nullptr");

  jclass container_cls = env->GetObjectClass(container_obj);
  CheckForJavaExceptions(env);

  if (container_cls == nullptr)
          ThrowJavaException(env, "Class not found - is this a Java Object?");

  jmethodID method_id = env->GetMethodID(container_cls,
                                         method_name.c_str(), "()C");
  CheckForJavaExceptions(env);

  if (method_id == nullptr)
    ThrowJavaException(env, (method_name + " not found").c_str());

  jchar j_char = static_cast<jchar>(
                      env->CallCharMethod(container_obj, method_id));
  CheckForJavaExceptions(env);

  return static_cast<uint16_t>(j_char);
}

Byte GetByteFromVoidMethod(JNIEnv* const env, const jobject container_obj,
                               const std::string& method_name) {
  if (env == nullptr) ThrowJavaException(env, "ENV is nullptr");
  if (container_obj == nullptr) ThrowJavaException(env, "Object is nullptr");

  jclass container_cls = env->GetObjectClass(container_obj);
  CheckForJavaExceptions(env);

  if (container_cls == nullptr)
          ThrowJavaException(env, "Class not found - is this a Java Object?");

  jmethodID method_id = env->GetMethodID(container_cls,
                                         method_name.c_str(), "()B");
  CheckForJavaExceptions(env);

  if (method_id == nullptr)
    ThrowJavaException(env, (method_name + " not found").c_str());

  jbyte j_byte = static_cast<jbyte>(
                      env->CallByteMethod(container_obj, method_id));
  CheckForJavaExceptions(env);

  return static_cast<Byte>(j_byte);
}

uint32_t GetUnsignedFromVoidMethod(JNIEnv* const env,
                                   const jobject container_obj,
                                   const std::string& method_name) {
  if (env == nullptr)
    ThrowJavaException(env, "ENV is nullptr");
  if (container_obj == nullptr)
    ThrowJavaException(env, "Object is nullptr");

  jclass container_cls = env->GetObjectClass(container_obj);

  if (container_cls == nullptr)
    ThrowJavaException(env, "Class not found - is this a Java Object?");

  jmethodID method_id =
      env->GetMethodID(container_cls, method_name.c_str(), "()J");
  CheckForJavaExceptions(env);

  if (method_id == nullptr)
    ThrowJavaException(env, (method_name + " not found").c_str());

  jlong j_long =
      static_cast<jlong>(env->CallLongMethod(container_obj, method_id));

  CheckForJavaExceptions(env);
  return static_cast<uint32_t>(j_long);
}

}  // namespace mcbp_android

#endif
