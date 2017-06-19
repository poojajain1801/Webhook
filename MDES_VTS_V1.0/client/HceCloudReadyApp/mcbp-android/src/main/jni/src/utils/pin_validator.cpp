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

#include <utils/pin_validator.h>
#include <utils/utilities.h>

#include <log/log.h>

namespace mcbp_core {

void PinValidator::unlock_umd_key(ByteArray* umd_key) {
  if (pin_ == nullptr || pin_->empty()) return;

  ByteArray& pin = *pin_;
  ByteArray& key = *umd_key;

  const Byte pin_size = static_cast<Byte>(pin.size());

  ByteArray shifted_pin(pin_size, 0x00);
  for (std::size_t i = 0; i < pin_size; i++)
    shifted_pin[i] = (pin[i] << 1);

  // We can only handle up to 8 digit PIN, if more then just loop 8 times
  const std::size_t len = pin_size < 8 ? pin_size: 8;
  for (std::size_t i = 0; i < len; i++) {
    // Unlock both Contactless and Remote Payment keys
    key[i]     ^= shifted_pin[i];
    key[i + 8] ^= shifted_pin[i];
  }
}

void PinValidator::wipe() {
  Wipe(pin_);
}

}  // namespace mcbp_core
