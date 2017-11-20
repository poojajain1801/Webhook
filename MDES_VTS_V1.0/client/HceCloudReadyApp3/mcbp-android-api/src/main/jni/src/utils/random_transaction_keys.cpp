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

#include <utils/random_transaction_keys.h>
#include <utils/crypto_factory.h>

namespace mcbp_core {

RandomTransactionKeys::RandomTransactionKeys() {
  const std::size_t key_size = TransactionKeys::kSizeOfTransactionKey;
  const std::size_t idn_size = TransactionKeys::kSizeOfIdn;

  const auto *const crypto = CryptoFactory::instance();
  sk_cl_umd_ = crypto->generate_random(key_size);
  sk_cl_md_  = crypto->generate_random(key_size);
  sk_rp_umd_ = crypto->generate_random(key_size);
  sk_rp_md_  = crypto->generate_random(key_size);
  idn_       = crypto->generate_random(idn_size);
  atc_       = kAtcRandomKeys;
  pvs_       = 0x00;
}

}  // namespace mcbp_core
