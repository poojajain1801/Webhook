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
#include <core/mcm/mcm_card_profile.h>
#include <core/mcm/contactless_context.h>
#include <utils/transaction_keys.h>
#include <core/mcm/mcm_lite_listener.h>
#include <utils/emvco/select_apdu.h>
#include <utils/emvco/gpo_apdu.h>
#include <utils/emvco/read_record_apdu.h>
#include <utils/emvco/compute_cc_apdu.h>
#include <utils/emvco/ccc_response_apdu.h>
#include <utils/emvco/generate_ac_apdu.h>
#include <utils/emvco/generate_ac_response_apdu.h>
#include <core/mcm/cryptogram_input.h>
#include <core/mcm/transaction_output.h>

#ifndef SRC_CORE_MCM_MCM_LITE_SERVICES_H_  // NOLINT
#define SRC_CORE_MCM_MCM_LITE_SERVICES_H_  // NOLINT

namespace mcbp_core {

/**
 * MPP Lite State Machine
 */
enum class McmLiteState {
  /** 
   * The MPP Lite does not contain any Card Profile nor transaction credentials
   */
  STOPPED,
  /**
   * The MPP Lite contains a valid Card Profile, but no transaction credentials
   */
  INITIALIZED,
  /**
   * The MPP Lite contains a card profile that is compatible with remote 
   * payment and has been successfully armed with a set of transaction
   * credentials
   */
  RP_READY,
  /**
   * The MPP Lite is ready to execute a Contactless Payment transaction and the
   * reader has not yet selected the payment application
   */
  CL_NOT_SELECTED,
  /**
   * The MPP Lite is ready to execute a Contactless Payment transaction and the
   * reader has selected the payment application
   */
  CL_SELECTED,
  /**
   * The MPP Lite has succesfully completed the processing GPO C-APDU and it is
   * ready to generate cryptogram upon request of the payment terminal
   */
  CL_INITIATED
};

/**
 *  \brief     Initialize the McmLite with a Card Profile object
 *  \details   It loads the parameters (i.e. Card Profile) of the digitized
 *             card that will be used for the subsequent contactless or remote
 *             payment transaction
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class McmLite {
 public:
  // Default constructor
  McmLite();

    /**
   * \brief Starts a contactless payment transaction.
   * @param credentials This object contains the one-time credentials necessary
   *                    to perform the transaction.
   * @param listener    A reference to an object implementing the interface 
   *                    for EventListener
   * @param amount      The amount or maximum amount allowed for the forthcoming
   *                    contactless transaction.
   * @param currency    The currency associated with the amount
   * @param exact_amount It indicates whether the parameter amount corresponds
   *                     to the exact value (true) or the maximum (false) amount
   *                     allowed for the next contactless transaction
   * @param cvm_entered It specifies whether the Mobile PIN or an alternate on
   *                    device Cardholder Verification Method (CVM) has been
   *                    entered
   * @param cvm_required Indicating whether the Mobile PIN (or any other on
   *                     device CVM is required to authorize the transaction
   *                     online or authenticate the application offline
   * @param online_allowed It indicates whether the McmLite is allowed to return
   *                       an online cryptogram. If false, the McmLite will
   *                       systemativally decline the transaction.
   */
  void initialize(const McmCardProfile* card_profile);

  /**
   * \brief Starts a contactless payment transaction.
   * @param credentials This object contains the one-time credentials necessary
   *                    to perform the transaction.
   * @param listener    A reference to an object implementing the interface 
   *                    for EventListener
   * @param amount      The amount or maximum amount allowed for the forthcoming
   *                    contactless transaction.
   * @param currency    The currency associated with the amount
   * @param exact_amount It indicates whether the parameter amount corresponds
   *                     to the exact value (true) or the maximum (false) amount
   *                     allowed for the next contactless transaction
   * @param cvm_entered It specifies whether the Mobile PIN or an alternate on
   *                    device Cardholder Verification Method (CVM) has been
   *                    entered
   * @param cvm_required Indicating whether the Mobile PIN (or any other on
   *                     device CVM is required to authorize the transaction
   *                     online or authenticate the application offline
   * @param online_allowed It indicates whether the McmLite is allowed to return
   *                       an online cryptogram. If false, the McmLite will
   *                       systemativally decline the transaction.
   */
  void start_contactless_payment(TransactionKeys* credentials,
                                 McmLiteListener *const listener,
                                 const int64_t& amount,
                                 const int32_t& currency,
                                 const bool exact_amount,
                                 const bool cvm_entered,
                                 const bool cvm_required,
                                 const bool online_allowed);

  /**
   * \brief Starts a remote payment transaction.
   * @param credentials This object contains the one-time credentials necessary
   *                    to perform the transaction.
   * @param cvm_entered It specifies whether the Mobile PIN or an alternate on
   *                    device Cardholder Verification Method (CVM) has been
   *                    entered
   */
  void start_remote_payment(TransactionKeys *const credentials,
                            const bool cvm_entered);

  /**
   * Cancel a pending transaction.
   * It cancel a pending transaction that has been previously initiated and
   * wipe the transaction credentials.
   * This method is also called after a successful completion of the
   * transaction to polish the McmLite internal state and wipe credentials.
   */
  void cancel_payment();

  /**
   * \brief Performs payment transaction.
   * @param input The data elements that are needed to compute a remote payment
   *              transaction
   * @param output A pointer to the object where the output of the transaction
   *               will be stored.
   */
  void create_remote_cryptogram(const CryptogramInput& input,
                                TransactionOutput *const output);

  /**
   * \brief Responds to a Command APDU.
   * @param command_apdu An array of bytes containing the C-APDU sent by the 
   *                     contactless reader.
   */
  const ByteArray process_apdu(const ByteArray& command_apdu);

  /**
   * Reinitializes the McmLite as it had just been created. The Card Profile and
   * transaction credentials are erased.
   */
  void stop();

  /**
   * Denstructor
   */
  ~McmLite();

 protected:
  // None

 private:
  // Internal current state of the MPP Lite
  McmLiteState state_;

  // CVM entered
  bool cvm_entered_;

  // CVR
  ByteArray cvr_;

  // Transaction Credentials
  TransactionKeys* credentials_;

  // Card Profile
  const McmCardProfile* card_profile_;

  // Contactless Context
  ContactlessContext cl_context_;

  // Private member functions

  /**
   * Handles the SELECT C-APDU (SELECT PPSE and SELECT AID) and returns the 
   * R-APDU.
   * @param selectApdu the SELECT C-APDU
   * @return the R-APDU
   */
  ResponseApdu select(const SelectApdu& c_apdu);

  /**
   * Handles the GET PROCESSING OPTIONS C-APDU and returns the R-APDU.
   * @param gpoApdu the GPO C-APDU
   * @return the R-APDU
   */
  ResponseApdu processing_options(const GpoApdu& c_apdu);

  /**
   * Handles the READ RECORD C-APDU and returns the R-APDU.
   *
   * @param readRecordApdu the Read Record C-APDU
   *
   * @return the R-APDU
   */
  ResponseApdu read_record(const ReadRecordApdu& c_apdu) const;

  /**
   * Handles the COMPUTE CRYPTOGRAPHIC CHECKSUMC C-APDU and returns the R-APDU.
   *
   * @param computeCCApdu the COMPUTE CC C-APDU
   *
   * @return the R-APDU
   */
  ResponseApdu compute_cc(const ComputeCCApdu& c_apdu);

  /**
   * Handles the GENERATE AC C-APDU and returns the R-APDU.
   *
   * @param genACApdu the GenAC C-APDU
   *
   * @return the R-APDU
   */
  ResponseApdu generate_ac(const GenerateACApdu& c_apdu);

  // Utility functions

  //----------------------------------------------------------------------------
  // Generic utility functions
  //----------------------------------------------------------------------------

  // Check whether the terminal is an offline only. Returns True if offline only
  static bool offline_terminal(const Byte terminal_type);
  // Check whether there is a conflict in the context. True if conflict
  bool context_conflict() const;
  // Process PIN info and set cvr accordingly - Valid for GAC and REM
  void process_pin_info();
  // Check whether it is a transit transaction
  static bool is_transit(const ByteArray& amount,
                         const ByteArray& merch_cat_code);

  //----------------------------------------------------------------------------
  // Compute CC utility functions
  //----------------------------------------------------------------------------

  // Initialize the context for a Compute Crypto Checksum transaction (CCC.1.10)
  void initialize_context(const ComputeCCApdu& c_apdu);
  // Verify whether a ccc transaction can be approved
  bool approve_ccc(const ComputeCCApdu& c_apdu);
  // Check if a Magstripe transaction is domestic or international and
  // if it is allowed
  bool ms_domestic_international(const ByteArray& terminal_country_code) const;
  // Verify the CVM for Compute Crypto Checksum (CCC.3.3, CCC.3.4, and CCC.3.5)
  bool ccc_verify_cmv(const Byte mobile_support_indicator) const;
  // Compute Crypto Checksum - Response APDU for online approval
  ResponseApdu ccc_online(const ComputeCCApdu& c_apdu);
  // Compute Crypto Checksum - Response APDU for decline
  ResponseApdu ccc_decline(const ComputeCCApdu& c_apdu);
  // Check whether the ComputeCC C-APDU is well formed
  void validate_computecc_apdu(const ComputeCCApdu& c_apdu);

  //----------------------------------------------------------------------------
  // Generate AC utility functions
  //----------------------------------------------------------------------------

  // Initialize the context for a Generate AC transaction (GAC.1.10)
  void initialize_context(const GenerateACApdu& c_apdu);
  // Part of (GAC.2)
  void gac_process_additional_check_table(const ByteArray& cdol1_related_data);
  // (GAC.3)
  bool gac_context_check(const GenerateACApdu& c_apdu);
  // (GAC.4)
  bool gac_crm(const GenerateACApdu& c_apdu);
  // (GAC.5)
  void gac_arqc(const GenerateACApdu& c_apdu);
  // (GAC.6)
  void gac_aac(const GenerateACApdu& c_apdu);
  // (GAC.7)
  void gac_ac(const GenerateACApdu& c_apdu, ResponseApdu* const apdu);
  // (GAC.8)
  void gac_cda(const GenerateACApdu& c_apdu, ResponseApdu* const apdu);
  // Verify the CVM for Generate AC
  bool gac_verify_cvm(const GenerateACApdu& c_apdu);
  // Decide whether the transaction can be approved online
  bool gac_approve_online(const GenerateACApdu& c_apdu);
  // Check whether there is a match between CVR[4:6] and Ciac Decline
  // True if the match is found
  bool gac_match_cvr_ciac_decline() const;
  // Check if an M-CHIP transaction or a Remote Payment transaction is domestic
  // or international and set cvr accordingly.
  void mc_domestic_international(const ByteArray& terminal_country_code);
  // Initialize the crypto output data structure for M-CHIP transactions
  void gac_initialize_crypto_output();
  // Check whether the GenerateAC C-APDU is well formed
  void validate_generateac_apdu(const GenerateACApdu& c_apdu);

  //----------------------------------------------------------------------------
  // Remote Payment utility functions
  //----------------------------------------------------------------------------

  // Check the initial state and the input parameter before starting a remote
  // payment transaction
  void rp_check_state_input(TransactionOutput *const output);
  // Initialize the output data structures for a Remote Payment
  void rp_initialize_output(TransactionOutput *const output);
  // Build a cdol1 related vector for Remote Payment
  ByteArray rp_build_cdol1(const CryptogramInput& input);
  // Modify the status of cvr_ by applying the CVR mask to it
  void rp_apply_cvr_mask(const ByteArray& mask);
  // Decide whether to return an AAC or ARQ for the Remote Payment
  void rp_decide_aac_or_arq(const CryptogramInput& input,
                            TransactionOutput* const output);

  //----------------------------------------------------------------------------
  // Get GPO functions
  //----------------------------------------------------------------------------

  // Set the AIP based on GPO C-APDU
  void gpo_set_aip(const GpoApdu& c_apdu);

  // Check whether the GPO C-APDU is well formed
  void validate_gpo_apdu(const GpoApdu& c_apdu) const;
};

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_MCM_LITE_SERVICES_H_)  // NOLINT
