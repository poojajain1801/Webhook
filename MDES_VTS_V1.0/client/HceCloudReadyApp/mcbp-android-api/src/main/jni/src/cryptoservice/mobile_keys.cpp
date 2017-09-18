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

#include <cryptoservice/mobile_keys.h>
#include <utils/utilities.h>

#include <log/log.h>

namespace mcbp_crypto_service {

MobileKeys::MobileKeys(const ByteArray& transport_key,
                       const ByteArray& mac_key,
                       const ByteArray& data_encryption_key) :
    transport_key_(transport_key),
    mac_key_(mac_key),
    data_encryption_key_(data_encryption_key) {
  // No operation
}

ByteArray MobileKeys::get_transport_key() const {
  return transport_key_;
}

ByteArray MobileKeys::get_mac_key() const {
  return mac_key_;
}

ByteArray MobileKeys::get_data_encryption_key() const {
  return data_encryption_key_;
}

MobileKeys::~MobileKeys() {
  mcbp_core::Wipe(&transport_key_);
  mcbp_core::Wipe(&mac_key_);
  mcbp_core::Wipe(&data_encryption_key_);
}

}  // namespace mcbp_crypto_service
