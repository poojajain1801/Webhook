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

// C++ libraries

// Project libraries
#include <core/mcm/contactless_context.h>

namespace mcbp_core {

ContactlessContext::ContactlessContext() :
      bl_amount_(),
      bl_currency_(),
      bl_exact_amount_(),
      cvm_required_(true),
      online_allowed_(true),
      alternate_aid_(false),
      aip_(),
      poscii_(),
      pdol_values_(),
      response_apdu_(),
      contactless_transaction_context_(),
      cryptogram_output_(),
      listener_(nullptr) {
  // All the work is done in the initialization list
}

void ContactlessContext::wipe() {
  listener_ = nullptr;
}

ContactlessContext::~ContactlessContext() {
  wipe();
}

}  // namespace mcbp_core
