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
#include <utils/transaction_keys.h>
#include <utils/cardholder_validator.h>
#include <utils/utilities.h>
#include <utils/mcbp_core_exception.h>

namespace mcbp_core {

const std::size_t TransactionKeys::kSizeOfTransactionKey = 16;
const std::size_t TransactionKeys::kSizeOfIdn            =  8;
const std::size_t TransactionKeys::kSizeOfAtc            =  2;

TransactionKeys::TransactionKeys():
    sk_cl_umd_(kSizeOfTransactionKey, 0x00),
     sk_cl_md_(kSizeOfTransactionKey, 0x00),
    sk_rp_umd_(kSizeOfTransactionKey, 0x00),
     sk_rp_md_(kSizeOfTransactionKey, 0x00),
          atc_(kSizeOfAtc,            0x00),
          idn_(kSizeOfIdn,            0x00),
          pvs_(0x00) { }

TransactionKeys::TransactionKeys(const KeysData& keys, const Byte pvs) :
    sk_cl_umd_(keys.sk_cl_umd),
    sk_cl_md_(keys.sk_cl_md),
    sk_rp_umd_(keys.sk_rp_umd),
    sk_rp_md_(keys.sk_rp_md),
    atc_(keys.atc),
    idn_(keys.idn),
    pvs_(pvs) {
  if (atc_.size() != kSizeOfAtc)
    throw InvalidInput("Invalid ATC");
  if (idn_.size() != kSizeOfIdn)
    throw InvalidInput("Invalid IDN");
  if (!sk_cl_umd().empty() && sk_cl_umd().size() != kSizeOfTransactionKey)
    throw InvalidInput("Invalid Credentials (Contacless UMD)");
  if (!sk_cl_md().empty() && sk_cl_md().size() != kSizeOfTransactionKey)
    throw InvalidInput("Invalid Credentials (Contacless MD)");
  if (!sk_rp_umd().empty() && sk_rp_umd().size() != kSizeOfTransactionKey)
    throw InvalidInput("Invalid Credentials (Remote Payment UMD)");
  if (!sk_rp_md().empty() && sk_rp_md().size() != kSizeOfTransactionKey)
    throw InvalidInput("Invalid Credentials (Remote Payment MD)");
}

TransactionKeys::~TransactionKeys() {
  wipe();
}

void TransactionKeys::unlock(CardholderValidator* validator) NOEXCEPT {
  if (validator == nullptr) return;

  validator->unlock_umd_key(&sk_cl_umd_);
  validator->unlock_umd_key(&sk_rp_umd_);
  validator->wipe();
}

void TransactionKeys::wipe() NOEXCEPT {
  wipe_keys();
  pvs_ = 0x00;
  Wipe(&idn_);
  Wipe(&atc_);
}

void TransactionKeys::wipe_keys() NOEXCEPT {
  Wipe(&sk_cl_umd_);
  Wipe(&sk_cl_md_);
  Wipe(&sk_rp_umd_);
  Wipe(&sk_rp_md_);
}

}  // namespace mcbp_core
