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

#include <log/log.h>
#include <utils/utilities.h>

#include <string>
#include <mutex>

namespace mcbp_core {

#ifdef MCBP_CORE_DEBUG

#include <utils/transaction_keys.h>
#include <utils/byte_array.h>

#ifdef __ANDROID__  // Android Log version
#include <android/log.h>

#else  // Desktop includes

#include <stdio.h>
#include <stdarg.h>

#endif  // End of platform specific includes

#endif  // End of MCBP_CORE_DEBUG includes

// Global static pointer used to ensure a single instance of the class.
Log* Log::instance_ = nullptr;

std::once_flag flag_mcbp_core_log;

void CreateLog() {
  if (Log::instance_ != nullptr) return;
  Log::instance_ = new Log();
}

Log* Log::instance() {
  if (instance_ == nullptr) std::call_once(flag_mcbp_core_log, CreateLog);
  return instance_;
}

#ifdef MCBP_CORE_DEBUG

#ifdef __ANDROID__  // Android Log version

void Log::d(const char *fmt, ...) {
  va_list ap;
  va_start(ap, fmt);
  __android_log_vprint(ANDROID_LOG_INFO, LOG_TAG, fmt, ap);
  va_end(ap);
}

#else  // Desktop Log version

void Log::d(const char *format, ...) {
  va_list argptr;
  va_start(argptr, format);
  vfprintf(stderr, format, argptr);
  va_end(argptr);
  fprintf(stderr, "\n");
}

#endif  // Desktop Log version

void Log::d(const ByteArray& array, const char* const name) {
  d("%s: %s", name, &(ByteToHexChar(array)[0]));
}

void Log::d(const Byte& byte, const char* const name) {
  d("%s: %s", name, &(ByteToHexChar(ByteArray(1, byte))[0]));
}

void Log::d(const std::string& str) {
  d("%s", str.c_str());
}

#else

void Log::d(const char *fmt, ...) { return; }

void Log::d(const ByteArray array, const char* const name) { return; }

void Log::d(const Byte byte, const char* const name) { return; }

void Log::d(const std::string& string) { return; }

#endif  // MCBP_CORE_DEBUG

}  // namespace mcbp_core
