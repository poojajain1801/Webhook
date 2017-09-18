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

#ifndef SRC_MCBP_CORE_EXCEPTION_H_  // NOLINT
#define SRC_MCBP_CORE_EXCEPTION_H_  // NOLINT

// Project includes
#include <utils/byte_array.h>
#include <core/constants.h>

// Libraries includes
#include <exception>
#include <string>

#ifdef _WIN32
#define NOEXCEPT throw()
#else
#define NOEXCEPT noexcept
#endif

namespace mcbp_core {

class Log;

/**
  * Generic exception handler
  */
class Exception : public std::exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit Exception(const char* const msg,
                     const ByteArray& error_code = Iso7816::kSwUnknown);

  /**
   * Create a new exception with a debug message
   */
  explicit Exception(const std::string& msg,
                     const ByteArray& error_code = Iso7816::kSwUnknown);

  /**
    * Output to a log file the debug message related to this exception
    */
  virtual const char* what() const NOEXCEPT;

  /**
   * Get the reason code for the exception as Byte HEX (e.g. 0x00 format)
   */
  virtual const ByteArray& error_code() const NOEXCEPT;

  /**
    * Denstructor
    */
  ~Exception() NOEXCEPT;

 protected:
  // None

 private:
  /**
   * error string
   */
  const char* const msg_;

  /**
    * Exception error code (As ByteArray);
    */
  const ByteArray error_code_;

  // Default constructor is not allowed.
  Exception();
};


/*----------------------------------------------------------------------------*/
/* InvalidInput                                                               */
/*----------------------------------------------------------------------------*/

class InvalidInput : public Exception {
 public:
   /**
    * Create a new exception with a debug message
    */
  explicit InvalidInput(const char* const msg,
                        const ByteArray& error_code = Iso7816::kSwUnknown);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidInput(const std::string& msg,
                        const ByteArray& error_code = Iso7816::kSwUnknown);

  /**
    * Denstructor
    */
  ~InvalidInput() NOEXCEPT;

 private:
  /**
   * The Error code associated with the message (if any)
   */
  ByteArray error_code_;
};

/*----------------------------------------------------------------------------*/
/* InvalidCla                                                                 */
/*----------------------------------------------------------------------------*/

class InvalidCla : public InvalidInput {
 public:
   /**
    * Create a new exception with a debug message
    */
  explicit InvalidCla(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidCla(const std::string& msg);

  /**
    * Denstructor
    */
  ~InvalidCla() NOEXCEPT;
};

/*----------------------------------------------------------------------------*/
/* InvalidIns                                                                 */
/*----------------------------------------------------------------------------*/

class InvalidIns : public InvalidInput {
 public:
   /**
    * Create a new exception with a debug message
    */
  explicit InvalidIns(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidIns(const std::string& msg);

   /**
    * Denstructor
    */
  ~InvalidIns() NOEXCEPT;
};

/*----------------------------------------------------------------------------*/
/* InvalidP1                                                                  */
/*----------------------------------------------------------------------------*/

class InvalidP1 : public InvalidInput {
 public:
   /**
    * Create a new exception with a debug message
    */
  explicit InvalidP1(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidP1(const std::string& msg);

  /**
    * Denstructor
    */
  ~InvalidP1() NOEXCEPT;
};

/*----------------------------------------------------------------------------*/
/* InvalidP2                                                                  */
/*----------------------------------------------------------------------------*/

class InvalidP2 : public InvalidInput {
 public:
   /**
    * Create a new exception with a debug message
    */
  explicit InvalidP2(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidP2(const std::string& msg);

  /**
    * Denstructor
    */
  ~InvalidP2() NOEXCEPT;
};

/*----------------------------------------------------------------------------*/
/* InvalidLc                                                                  */
/*----------------------------------------------------------------------------*/

class InvalidLc : public InvalidInput {
 public:
   /**
    * Create a new exception with a debug message
    */
  explicit InvalidLc(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidLc(const std::string& msg);

  /**
    * Denstructor
    */
  ~InvalidLc() NOEXCEPT;
};

/*----------------------------------------------------------------------------*/
/* InvalidDigitizedCardId                                                     */
/*----------------------------------------------------------------------------*/


class InvalidDigitizedCardId : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit InvalidDigitizedCardId(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidDigitizedCardId(const std::string& msg);

  /**
    * Denstructor
    */
  ~InvalidDigitizedCardId() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* InvalidDigitizedCardCp                                                     */
/*----------------------------------------------------------------------------*/


class InvalidDigitizedCardCp : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit InvalidDigitizedCardCp(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidDigitizedCardCp(const std::string& msg);

  /**
    * Denstructor
    */
  ~InvalidDigitizedCardCp() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* InvalidState                                                               */
/*----------------------------------------------------------------------------*/


class InvalidState : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit InvalidState(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InvalidState(const std::string& msg);

  /**
    * Denstructor
    */
  ~InvalidState() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* DsrpIncompatibleProfileError                                               */
/*----------------------------------------------------------------------------*/


class DsrpIncompatibleProfileError : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit DsrpIncompatibleProfileError(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit DsrpIncompatibleProfileError(const std::string& msg);

  /**
    * Denstructor
    */
  ~DsrpIncompatibleProfileError() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* ClCredentialsError                                                         */
/*----------------------------------------------------------------------------*/


class ClCredentialsError : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit ClCredentialsError(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit ClCredentialsError(const std::string& msg);

  /**
    * Denstructor
    */
  ~ClCredentialsError() NOEXCEPT;
};

/*----------------------------------------------------------------------------*/
/* DsrpCredentialsError                                                       */
/*----------------------------------------------------------------------------*/


class DsrpCredentialsError : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit DsrpCredentialsError(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit DsrpCredentialsError(const std::string& msg);

  /**
    * Denstructor
    */
  ~DsrpCredentialsError() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* UnitializedError                                                           */
/*----------------------------------------------------------------------------*/


class UnitializedError : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit UnitializedError(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit UnitializedError(const std::string& msg);

  /**
    * Denstructor
    */
  ~UnitializedError() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* InternalError                                                              */
/*----------------------------------------------------------------------------*/


class InternalError : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit InternalError(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit InternalError(const std::string& msg);

  /**
    * Denstructor
    */
  ~InternalError() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* UnexpectedDataError                                                        */
/*----------------------------------------------------------------------------*/


class UnexpectedDataError : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit UnexpectedDataError(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit UnexpectedDataError(const std::string& msg);

  /**
    * Denstructor
    */
  ~UnexpectedDataError() NOEXCEPT;
};


/*----------------------------------------------------------------------------*/
/* TransactionLoggingError                                                    */
/*----------------------------------------------------------------------------*/


class TransactionLoggingError : public Exception {
 public:
  /**
    * Create a new exception with a debug message
    */
  explicit TransactionLoggingError(const char* const msg);

  /**
   * Create a new exception with a debug message
   */
  explicit TransactionLoggingError(const std::string& msg);

  /**
    * Denstructor
    */
  ~TransactionLoggingError() NOEXCEPT;
};

}  // namespace mcbp_core

#endif  // defined(SRC_MCBP_CORE_EXCEPTION_H_)  // NOLINT
