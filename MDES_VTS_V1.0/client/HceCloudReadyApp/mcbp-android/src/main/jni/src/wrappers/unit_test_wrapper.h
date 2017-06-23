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

#include <core/mcbp_card.h>
#include <core/mobile_kernel/dsrp_input_data.h>
#include <core/mcm/mcm_lite_listener.h>
#include <core/mcm/contactless_transaction_context.h>
#include <wrappers/keys_data.h>
#include <map>

#ifndef SRC_WRAPPERS_UNIT_TEST_WRAPPER_H_  // NOLINT
#define SRC_WRAPPERS_UNIT_TEST_WRAPPER_H_  // NOLINT

#ifdef _WIN32
#define EXPORT
#else
#define EXPORT __attribute__((__visibility__("default")))
#endif

namespace mcbp_unit_test {

/**
 *  \brief     The Unit Test Wrapper class.
 *  \details   Wrapper class to connect unit test module to the library
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class UnitTestWrapper : public mcbp_core::McmLiteListener {
 public:
  /**
   * Get the instance of the wrapper
   * @return a pointer to the only existing instance of the wrapper
   */
  EXPORT
  static UnitTestWrapper* instance();

  /**
   * Create a new Card.
   * @param card_id The ID of the new MCBP Card
   * @return
   */
  EXPORT
  static void add_card(const uint32_t card_id);

  /**
   * \brief Initialize the MCBP Card.
   * This version supports only PIN which is passed as argument
   * @param profile The Card Profile
   * @param keys The Transaction Keys to be used for the next transaction
   * @param pin The PIN for Cardholder Verification
   */
  EXPORT
  static void initialize(const mcbp_core::CardProfileData& profile);

  /**
   * Start contactless
   */
  EXPORT
  static void start_contactless(const mcbp_core::KeysData& keys,
                                ByteArray pin, const int64_t& amount,
                                const int32_t& currency,
                                const bool exact_amount);

  // Respond to an APDU command
  EXPORT
  static ByteArray transceive(const ByteArray& c_apdu);

  // Perform a DSRP transaction and return the DE55 or UCAF formatted message
  // to be sent to the Issuer Simulator for verification
  EXPORT
  static ByteArray remote_payment(const mcbp_core::DsrpInputData& input);

  EXPORT
  static void activate_remote(const mcbp_core::KeysData& keys, ByteArray pin);

  // Stop the MCBP Card for Remote Payment and remove sensitive data
  EXPORT
  static void deactivate();

  /**
   * Stop the MCBP Card
   */
  EXPORT
  static void stop();

  /**
   * Handle events generated by the McmLite
   */
  virtual void on_event(
      const mcbp_core::ContactlessTransactionContext& context);

  /**
   * Denscructor
   */
  ~UnitTestWrapper();

 protected:
  // None

 private:
  UnitTestWrapper() { }

  /**
   * Pointer to the only instance of the MCBPcore Wrapper
   */
  static UnitTestWrapper* instance_;

  /**
   * Pointer to McbpCard
   */
  static mcbp_core::McbpCard *card_;

  // Used to create the first instance
  friend void CreateUnitTestWrapper();
};

}  // namespace mcbp_unit_test

#endif  // defined(SRC_WRAPPERS_UNIT_TEST_WRAPPER_H_)  // NOLINT
