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

#ifndef SRC_WRAPPERS_ANDROID_WRAPPER_UTILS_H_  // NOLINT
#define SRC_WRAPPERS_ANDROID_WRAPPER_UTILS_H_  // NOLINT

#ifdef __ANDROID__

#include <jni.h>
#include <utils/utilities.h>

#include <string>

namespace mcbp_android {

/**
 * Convert a JString into a C++ STL string
 * @param env The JNI environment
 * @param st  The JString to be converted
 * @return The C++ string
 */
std::string ConvertJString(JNIEnv* env, jstring str);

/**
 * Throw a Java Exception
 * @param env The JNI environment
 * @param msg The Message to be associated with the exception
 */
void ThrowJavaException(JNIEnv* const env, const char* const msg);

/**
 * Throw a Java McbpCrypto Exception
 * @param env The JNI environment
 * @param msg The Message to be associated with the exception
 */
void ThrowMcbpCryptoException(JNIEnv* const env, const char* const msg);

/**
 * Get a generic Java object from a Java Method of a Java Class (container_obj)
 * (i.e. from a method with signature "()Ljava/lang/String;"
 * @param env The JNI environment
 * @param container_obj The Object from which the method should be invoked
 * @param method_name The name of the method to be invoked
 * @param method_signature The JNI method signature
 */
jobject GetJavaObjectFromMethod(JNIEnv* const env,
                                const jobject container_obj,
                                const std::string& method_name,
                                const std::string& method_signature);

/**
 * Get a String (C++) from a Void Java Method that returns a Java String
 * (i.e. from a method with signature "()Ljava/lang/String;"
 * @param env The JNI environment.
 * @param container_obj The Object from which the method should be invoked.
 * @param method_name The name of the method to be invoked.
 */
std::string GetStringFromVoidMethod(JNIEnv* const env,
                                    const jobject container_obj,
                                    const std::string& method_name);

/**
 * Get an int64_t from a Void Java Method that returns a Java long.
 * @param env The JNI environment.
 * @param container_obj The Object from which the method should be invoked.
 * @param method_name The name of the method to be invoked.
 */
int64_t GetLongFromVoidMethod(JNIEnv* const env,
                              const jobject container_obj,
                              const std::string& method_name);

/**
 * Get a uint32_t from a Void Java Method that returns a Java long.
 * @param env The JNI environment.
 * @param container_obj The Object from which the method should be invoked.
 * @param method_name The name of the method to be invoked.
 */
uint32_t GetUnsignedFromVoidMethod(JNIEnv* const env,
                                   const jobject container_obj,
                                   const std::string& method_name);

/**
 * Get a uint16_t from a Void Java Method that returns a Java char.
 * @param env The JNI environment.
 * @param container_obj The Object from which the method should be invoked.
 * @param method_name The name of the method to be invoked.
 */
uint16_t GetCharFromVoidMethod(JNIEnv* const env, const jobject container_obj,
                               const std::string& method_name);

/**
 * Get a Byte from a Void Java Method that returns a Java byte
 * @param env The JNI environment.
 * @param container_obj The Object from which the method should be invoked.
 * @param method_name The name of the method to be invoked.
 */
Byte GetByteFromVoidMethod(JNIEnv* const env, const jobject container_obj,
                           const std::string& method_name);

/**
 * Get an Integer from a Void Java Method that returns a Java Integer (int)
 * @param env The JNI environment.
 * @param container_obj The Object from which the method should be invoked.
 * @param method_name The name of the method to be invoked.
 */
int GetIntFromVoidMethod(JNIEnv* const env, const jobject container_obj,
                         const std::string& method_name);

/**
 * Convert a jbyteArray object into a ByteArray.
 * @param env The JNI environment.
 * @param array The Java byteArray.
 * @return The equivalent ByteArray.
 */
ByteArray ConvertJByteArray(JNIEnv* const env, const jbyteArray array);

/**
 * Convert and AndroidByteArray object into a ByteArray.
 * @param env The JNI environment.
 * @param android_byte_array The Java AndroidByteArray.
 * @return The equivalent ByteArray.
 */
ByteArray ConvertAndroidByteArray(JNIEnv* const env,
                                  const jobject android_byte_array);

/**
 * Converts an AndroidByteArrayInterface into a ByteArray.
 * Please note that the container object is assumed to implement the getBytes
 * method.
 * @param env The JNI environment.
 * @param container_obj The AndroidByteArrayInterface object.
 * @param method_name The of the method to be invoked.
 * @return The equivalent ByteArray
 */
ByteArray GetAndroidByteArrayFromVoidMethod(JNIEnv* const env,
                                            const jobject container_obj,
                                            const std::string& method_name);

/**
 * Create a Java ByteArray (i.e. Byte[]) using ByteArray as source
 * @param env The JNI environment
 * @param source The source ByteArray
 * @return The Java Byte Array
 */
jbyteArray CreateJavaByteArray(JNIEnv* const env, const ByteArray& source);

/**
 * Create a Java ContextType object to be returned at the end of the transaction
 * @param env The JNI environment
 * @param context The current MCM Lite context
 * @return The Jave ContextType object
 */
int CreateJavaContextType(JNIEnv* const env,
                              const mcbp_core::ContextType& context);

/**
 * Check whether a Java Exception is pending. If yes, print some description
 * and raise a C++ Exception
 */
inline void CheckForJavaExceptions(JNIEnv* const env) {
  if (env == nullptr)
    ThrowJavaException(env, "ENV is nullptr");
  if (env->ExceptionOccurred()) {
    env->ExceptionDescribe();
    ThrowJavaException(env, "A Java Exception is pending...");
  }
}

}  // namespace mcbp_android

#endif  // defined(__ANDROID__)  // NOLINT

#endif  // defined(SRC_WRAPPERS_ANDROID_WRAPPER_H_)  // NOLINT
