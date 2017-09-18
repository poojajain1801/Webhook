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

#include <utils/mcbp_core_exception.h>
#include <log/log.h>

#include <string>

namespace mcbp_core {

Exception::Exception(const char* const msg, const ByteArray& error_code) :
  msg_(msg), error_code_(error_code) { }

Exception::Exception(const std::string& msg, const ByteArray& error_code) :
  msg_(msg.c_str()), error_code_(error_code) {
}

const char* Exception::what() const NOEXCEPT {
  Log::instance()->d("\t %s", msg_);
  return msg_;
}

const ByteArray& Exception::error_code() const NOEXCEPT {
  return error_code_;
}

Exception::~Exception() NOEXCEPT {
  // Do Nothing.
}


/*----------------------------------------------------------------------------*/
/* InvalidInput                                                               */
/*----------------------------------------------------------------------------*/


InvalidInput::InvalidInput(const char* const msg, const ByteArray& error_code)
  : Exception(msg, error_code) { }

InvalidInput::InvalidInput(const std::string& msg, const ByteArray& error_code)
  : Exception(msg, error_code) { }

InvalidInput::~InvalidInput() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* InvalidCla                                                                 */
/*----------------------------------------------------------------------------*/


InvalidCla::InvalidCla(const char* const msg) :
  InvalidInput(msg, Iso7816::kSwClaNotSupported) { }

InvalidCla::InvalidCla(const std::string& msg) :
  InvalidInput(msg, Iso7816::kSwClaNotSupported) { }

InvalidCla::~InvalidCla() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* InvalidIns                                                                 */
/*----------------------------------------------------------------------------*/


InvalidIns::InvalidIns(const char* const msg)
  : InvalidInput(msg, Iso7816::kSwInsNotSupported) { }

InvalidIns::InvalidIns(const std::string& msg)
  : InvalidInput(msg, Iso7816::kSwInsNotSupported) { }

InvalidIns::~InvalidIns() NOEXCEPT { }



/*----------------------------------------------------------------------------*/
/* InvalidP1                                                                  */
/*----------------------------------------------------------------------------*/


InvalidP1::InvalidP1(const char* const msg)
  : InvalidInput(msg, Iso7816::kSWIncorrectP1P2) { }

InvalidP1::InvalidP1(const std::string& msg)
  : InvalidInput(msg, Iso7816::kSWIncorrectP1P2) { }

InvalidP1::~InvalidP1() NOEXCEPT { }



/*----------------------------------------------------------------------------*/
/* InvalidP2                                                                  */
/*----------------------------------------------------------------------------*/


InvalidP2::InvalidP2(const char* const msg)
  : InvalidInput(msg, Iso7816::kSWIncorrectP1P2) { }

InvalidP2::InvalidP2(const std::string& msg)
: InvalidInput(msg, Iso7816::kSWIncorrectP1P2) { }

InvalidP2::~InvalidP2() NOEXCEPT { }



/*----------------------------------------------------------------------------*/
/* InvalidLc                                                                  */
/*----------------------------------------------------------------------------*/


InvalidLc::InvalidLc(const char* const msg)
  : InvalidInput(msg, Iso7816::kSwWrongLength) { }

InvalidLc::InvalidLc(const std::string& msg)
  : InvalidInput(msg, Iso7816::kSwWrongLength) { }

InvalidLc::~InvalidLc() NOEXCEPT { }



/*----------------------------------------------------------------------------*/
/* InvalitDigitezedCardId                                                     */
/*----------------------------------------------------------------------------*/


InvalidDigitizedCardId::InvalidDigitizedCardId(const char* const msg)
  : Exception(msg) { }

InvalidDigitizedCardId::InvalidDigitizedCardId(const std::string& msg)
  : Exception(msg) { }

InvalidDigitizedCardId::~InvalidDigitizedCardId() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* InvalidDigitizedCardCp                                                     */
/*----------------------------------------------------------------------------*/


InvalidDigitizedCardCp::InvalidDigitizedCardCp(const char* const msg)
  : Exception(msg) { }

InvalidDigitizedCardCp::InvalidDigitizedCardCp(const std::string& msg)
  : Exception(msg) { }

InvalidDigitizedCardCp::~InvalidDigitizedCardCp() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* InvalidState                                                               */
/*----------------------------------------------------------------------------*/


InvalidState::InvalidState(const char* const msg)
  : Exception(msg, Iso7816::kSwConditionsNotSatisfied) { }

InvalidState::InvalidState(const std::string& msg) : Exception(msg) { }

InvalidState::~InvalidState() NOEXCEPT  { }


/*----------------------------------------------------------------------------*/
/* DsrpIncompatibleProfileError                                               */
/*----------------------------------------------------------------------------*/


DsrpIncompatibleProfileError::DsrpIncompatibleProfileError(
  const char* const msg) : Exception(msg) { }

DsrpIncompatibleProfileError::DsrpIncompatibleProfileError(
  const std::string& msg) : Exception(msg) { }

DsrpIncompatibleProfileError::~DsrpIncompatibleProfileError() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* ClCredentialsError                                                         */
/*----------------------------------------------------------------------------*/


ClCredentialsError::ClCredentialsError(const char* const msg)
  : Exception(msg) { }

ClCredentialsError::ClCredentialsError(const std::string& msg)
  : Exception(msg) { }

ClCredentialsError::~ClCredentialsError() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* DsrpCredentialsError                                                       */
/*----------------------------------------------------------------------------*/


DsrpCredentialsError::DsrpCredentialsError(const char* const msg)
  : Exception(msg) { }

DsrpCredentialsError::DsrpCredentialsError(const std::string& msg)
  : Exception(msg) { }

DsrpCredentialsError::~DsrpCredentialsError() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* UnitializedError                                                           */
/*----------------------------------------------------------------------------*/


UnitializedError::UnitializedError(const char* const msg) : Exception(msg)  { }

UnitializedError::UnitializedError(const std::string& msg) : Exception(msg) { }

UnitializedError::~UnitializedError() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* InternalError                                                              */
/*----------------------------------------------------------------------------*/


InternalError::InternalError(const char* const msg) : Exception(msg) { }

InternalError::InternalError(const std::string& msg) : Exception(msg) { }

InternalError::~InternalError() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* UnexpectedDataError                                                        */
/*----------------------------------------------------------------------------*/


UnexpectedDataError::UnexpectedDataError(const char* const msg)
  : Exception(msg, Iso7816::kSwConditionsNotSatisfied) { }

UnexpectedDataError::UnexpectedDataError(const std::string& msg)
  : Exception(msg) { }

UnexpectedDataError::~UnexpectedDataError() NOEXCEPT { }


/*----------------------------------------------------------------------------*/
/* TransactionLoggingError                                                    */
/*----------------------------------------------------------------------------*/


TransactionLoggingError::TransactionLoggingError(const char* const msg)
  : Exception(msg) { }

TransactionLoggingError::TransactionLoggingError(const std::string& msg)
  : Exception(msg) { }

TransactionLoggingError::~TransactionLoggingError() NOEXCEPT { }

}  // namespace mcbp_core
