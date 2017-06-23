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
#include <log/log.h>
#include <utils/byte_array.h>
#include <utils/transaction_keys.h>
#include <utils/cardholder_validator.h>
#include <core/mcm/mcm_card_profile.h>
#include <core/mcm/mcm_lite_listener.h>
#include <core/mcm/mcm_lite_services.h>
#include <core/mobile_kernel/mobile_kernel.h>
#include <core/mobile_kernel/dsrp_input_data.h>
#include <core/mobile_kernel/dsrp_output_data.h>

#ifndef SRC_CORE_MCBP_CARD_H_  // NOLINT
#define SRC_CORE_MCBP_CARD_H_  // NOLINT

namespace mcbp_core {

/**
 * \brief Class that emulates a MCBP Card. 
 * \details It responds directly to APDU commands from the NFC Controller or 
 * to requests for remote cryptogram generation.
 *
 * This module contains only the native part of the MCBP Card. The actual
 * MCBP Card as specified in the MPA Functional Description is implemented
 * in the host language (e.g. Java)
 *
 * APIs used by this module should be mapped into private members of the host
 * class (e.g. Private Java native methods of the Java Class MCBPCard)
 *
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class McbpCard {
 public:
  /**
   * Create an empty McbpCard object. The Mpp_Lite is initialized when either
   * start_contacless or start_remote_payment are invoked with the relevant
   * CardProfile
   */
  McbpCard();

  /**
   * Activate the McbpCard for a Contactless transaction
   * 
   * This method differs from the Mobile Payment Application Functional
   * Description 1.0 because this method is private to the actual 
   * McbpCard object in the host language
   * 
   * Note that there is no start message here. The method transceive is used
   * instead
   *
   * @param card_profile The Mpp_Lite Card Profile
   */
  void initialize(const McmCardProfile* card_profile);

  /**
   * Initiate the contactless transaction by providing keys and Cardholder
   * Validator.
   * @param keys  The set of keys to be used for the next transaction
   * @param validator The Card Holder validation method
   *
   */
  void start_contactless(TransactionKeys* keys,
                         CardholderValidator* validator,
                         McmLiteListener *const listener,
                         const int64_t& amount,
                         const int32_t& currency,
                         const bool exact_amount,
                         const bool cvm_entered = true,
                         const bool cvm_required = true,
                         const bool online_allowed = true);

  /**
   * Remove the credentials from the MCM Lite object and put it back to
   * initialized state
   */
  void stop_contactless();

  /**
   * Prepare the Mpp_Lite module to be ready for a subsequent call to calculate
   * the transaction record (call to transaction_record)
   * @param card_profile  The Card Profile
   * @param keys          The set of keys to be used for the next transaction
   * @param validator     The Card Holder validation method
   *
   */
  void activate_remote(TransactionKeys* keys, CardholderValidator* validator);

  /**
   * Calculate the remote transaction cryptogram
   *
   * It is responsability of the calling class to generate and store the log
   * into the LDE
   *
   * This function is used to only perform the cryptographic operations
   * of the remote payment transaction (steps 2 and 3 of Figure 53, Page 115)
   * of the MPA Functional Description 1.0
   *
   * @param transaction_data The DSRP input data that will be used to generate
   *                         the remote cryptogram
   * @return DsrpOutputData The DSRP output data that can be used to build a
   *                        UCAF or DE55 authorization message.
   */
  DsrpOutputData transaction_record(const DsrpInputData& transaction_data);

  /**
   * Stop any active mode (i.e. contactless or remote). Destroy any related set
   * of keys and card profile. Finally, delete the Mpp_Lite object and the
   * mobile kernel (if it exists).
   */
  void deactivate();

  /**
   * Handle C-APDU and respond with R-APDU
   * @param c_apdu The Command APDU
   * @return r_apdu The Response APDU
   */
  const ByteArray transceive(const ByteArray& c_apdu);

  /**
   * Denstructor
   */
  ~McbpCard();

 protected:
  // None

 private:
  /**
   * The Mpp_Lite object
   */
  McmLite *mcm_lite_;

  /**
   * Pointer to the MobileKernel object
   */
  MobileKernel *mobile_kernel_;

  // Flags tracking the activation of remote payment and contactless. Only once
  // at the time can be active
  bool rp_activated_;
  bool cl_activated_;
};

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCBP_CARD_H_)  // NOLINT
