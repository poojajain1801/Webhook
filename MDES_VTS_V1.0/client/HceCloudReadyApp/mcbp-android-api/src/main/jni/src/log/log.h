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

// Project libraries
#include <utils/byte_array.h>
#include <string>

#ifndef MCBPCORE_JNI_LOG_LOG_H_  // NOLINT
#define MCBPCORE_JNI_LOG_LOG_H_  // NOLINT

namespace mcbp_core {

#define MCBP_CORE_DEBUG
#define LOG_TAG          "mcbpcorejni"

class Log {
 public:
  /** This function is called to create an instance of the class.
   * Calling the constructor publicly is not allowed. The constructor
   * is private and is only called by this Instance function.
   */
  static Log* instance();

  /**
   * Generic DEBUG Log
   */
  static void d(const char *fmt, ...);

  /**
   * Log a ByteArray object
   */
  static void d(const ByteArray& array, const char* const name);

  /**
   * Log a Byte object
   */
  static void d(const Byte& byte, const char* const name);

  /**
   * Log a std::string object
   */
  static void d(const std::string& str);

 protected:
  // None

 private:
  // Constructor and other operators are NOT available.
  Log() { }  // Private so that it can  not be called
  Log(const Log&) { }
  Log& operator=(const Log&);

  // Pointer to the only instance of the singleton object
  static Log* instance_;

  // Friend function used for thread-safe initialization
  friend void CreateLog();
};

}  // namespace mcbp_core

#endif  // defined(MCBPCORE_JNI_LOG_LOG_H_)  // NOLINT
