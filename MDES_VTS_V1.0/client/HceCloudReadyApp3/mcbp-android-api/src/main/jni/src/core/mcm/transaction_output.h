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


// C++ Libraries

// Project libraries
#include <utils/byte_array.h>
#include <core/mcm/cryptogram_output.h>

#ifndef SRC_CORE_MCM_TRANSACTION_OUTPUT_H_  // NOLINT
#define SRC_CORE_MCM_TRANSACTION_OUTPUT_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The DSRP Transaction Output.
 *  \details   The object is created by the caller and passed to the McmLite
 *             which will update it during the execution of the
 *             createRemoteCryptogram method with output parameters. It contains
 *             information that will enable the caller to build a DE55 or UCAF
 *             authorization request message, including static card information
 *             (e.g. the PAN), and dynamic transaction details (e.g. Application
 *             Cryptogram).
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class TransactionOutput {
 public:
  /**
   * Default constructor
   */
  TransactionOutput();

  /**
   * Get the Track2 Equivalent Data
   * @return A constant reference to the Track2 Equivalent data
   */
  const ByteArray& track2_equivalent_data() const;

  /**
   * Set the Track2 Equivalent Data
   * @param track2_eq_data The Track2 Equivalent data
   */
  void set_track2_equivalent_data(const ByteArray& track2_eq_data);

  /**
   * Get the PAN
   * @return A constant reference to the PAN
   */
  const ByteArray& pan() const;

  /**
   * Set the PAN
   * @param The value of the PAN
   */
  void set_pan(const ByteArray& pan);

  /**
   * Get the PAN Sequence Number
   * @return A constant reference to the PAN Sequence Number
   */
  const Byte& pan_sequence_number() const;

  /**
   * Set the PAN Sequence Number
   * @param The value of the PAN Sequence Number
   */
  void set_pan_sequence_number(const Byte& pan_sequence_number);

  /**
   * Get the Application Interchange Profile (AIP)
   * @return A constant reference to the AIP
   */
  const ByteArray& aip() const;

  /**
   * Set the Application Interchange Profile
   * @param The value of the AIP
   */
  void set_aip(const ByteArray& aip);

   /**
   * Get the Application Expiry Date
   * @return A constant reference to the Application Expiry Date
   */
  const ByteArray& application_expiry_date() const;

  /**
   * Set the Application Expiry Date
   * @param The value of the Application Expiry Date
   */
  void set_application_expiry_date(const ByteArray& application_expiry_date);

  /**
   * Get the Cryptogram Output
   * @return A constant reference to the Cryptogram Output
   */
  const CryptogramOutput& cryptogram_output() const;

  /**
   * Get the Cryptogram Output
   * @return A reference to the Cryptogram Output (Note: may modify the value)
   */
  CryptogramOutput& cryptogram_output();

  /**
   * Set the flag for the Cardholder verification method
   * @param cvm_entered True if the Cardholder verification method has been 
   * entered
   */
  void set_cvm_entered(const bool cvm_entered);

  /**
   * Check whether the Cardholder verification method has been entered
   * return True if the Cardholder verification method has been entered, false
   *        otherwise
   */
  bool cvm_entered() const;

 protected:
  // None

 private:
  // Track2 equivalent data
  ByteArray track2_equivalent_data_;      /* max 19 bytes */
  ByteArray pan_;                         /* max 10 bytes */
  Byte      pan_sequence_number_;         /*      1 byte  */
  ByteArray aip_;                         /*      2 bytes */
  ByteArray application_expiry_date_;     /*      3 bytes */

  bool cvm_entered_;
  CryptogramOutput cryptogram_output_;
};

// Inline functions definition

inline const ByteArray& TransactionOutput::track2_equivalent_data() const {
  return track2_equivalent_data_;
}
inline void TransactionOutput::set_track2_equivalent_data(
                                              const ByteArray& track2_eq_data) {
  track2_equivalent_data_ = track2_eq_data;
}
inline const ByteArray& TransactionOutput::pan() const {
  return pan_;
}
inline void TransactionOutput::set_pan(const ByteArray& pan) {
  pan_ = pan;
}
inline const Byte& TransactionOutput::pan_sequence_number() const {
  return pan_sequence_number_;
}
inline void TransactionOutput::set_pan_sequence_number(
                                         const Byte& pan_sequence_number) {
  pan_sequence_number_ = pan_sequence_number;
}
inline const ByteArray& TransactionOutput::aip() const {
  return aip_;
}
inline void TransactionOutput::set_aip(const ByteArray& aip) {
  aip_ = aip;
}
inline const ByteArray& TransactionOutput::application_expiry_date() const {
  return application_expiry_date_;
}
inline void TransactionOutput::set_application_expiry_date(
                                     const ByteArray& application_expiry_date) {
  application_expiry_date_ = application_expiry_date;
}
inline const CryptogramOutput& TransactionOutput::cryptogram_output() const {
  return cryptogram_output_;
}
inline CryptogramOutput& TransactionOutput::cryptogram_output() {
  return cryptogram_output_;
}
inline void TransactionOutput::set_cvm_entered(const bool cvm_entered) {
  cvm_entered_ = cvm_entered;
}
inline bool TransactionOutput::cvm_entered() const {
  return cvm_entered_;
}

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MCM_TRANSACTION_OUTPUT_H_)  // NOLINT
