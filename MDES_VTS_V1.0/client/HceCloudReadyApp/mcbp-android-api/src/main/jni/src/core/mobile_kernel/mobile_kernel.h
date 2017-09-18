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
#include <utils/byte_array.h>
#include <core/mcm/mcm_lite_services.h>
#include <core/mobile_kernel/dsrp_input_data.h>
#include <core/mobile_kernel/dsrp_output_data.h>

// C++ Libraries
#include <string>

#ifndef SRC_CORE_MCM_CARD_RECORD_H_  // NOLINT
#define SRC_CORE_MCM_CARD_RECORD_H_  // NOLINT

namespace mcbp_core {

/**
 *  \brief     The Mobile Kernel
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 *  \details  The Mobile Kernel provides APIs to create Digital Secure Remote 
 *            Payments (DSRP) transaction data, used indirectly by services such
 *            as the MPA Remote Payment API to perform online commerce 
 *            transactions.
 *            The Mobile Kernel currently supports two types of online commerce 
 *            responses:
 *            - UCAF The Mobile Kernel prepares data that will be populated 
 *              into DE48 SE43 (Universal Cardholder Authentication Field) of an
 *              authorization message
 *            - DE55 The Mobile Kernel prepares data that will be populated 
 *              into DE55 Integrated Circuit Data(ICC) System-Related Data of an
 *              authorization message
 */
class MobileKernel {
 public:
  /**
   * Create an instance of the Mobile Kernel
   */
  explicit MobileKernel(McmLite* const mcm_lite);

  /**
   * Generate DSRP data
   * @input  input_data The Input data used to calculate the DSRP cryptogram
   * @output output     The output data formatted as structured data
   * @output application_cryptogram The output data formatted as application
   *                                cryptogram
   */
  void generate_dsrp_data(const DsrpInputData& input_data,
                          DsrpOutputData* output,
                          ByteArray* application_cryptogram);
 protected:
  // None

 private:
  // Pointer to the MCM Lite object
  McmLite* const mcm_lite_;
};

}  // namespace mcbp_core

#endif  // MCBPCORE_JNI_SRC_CORE_MCM_CARD_RECORD_H_  // NOLINT
