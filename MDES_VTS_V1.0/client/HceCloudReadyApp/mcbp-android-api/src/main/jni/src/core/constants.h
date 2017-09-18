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

#include <utils/byte_array.h>

#ifndef SRC_CORE_CONSTANTS_H_  // NOLINT
#define SRC_CORE_CONSTANTS_H_  // NOLINT

namespace mcbp_core {

using std::size_t;

// MasterCard PPSE AID
static const ByteArray kPpseAid = {0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59,
                                   0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31};

// TLV Tags
static const ByteArray kTagAc                         = {0x9F, 0x26};
static const ByteArray kTagAip                        = {0x82};
static const ByteArray kTagAed                        = {0x5F, 0x24};
static const ByteArray kTagAmountAuthor               = {0x9F, 0x02};
static const ByteArray kTagAmountOther                = {0x9F, 0x03};
static const ByteArray kTagAtc                        = {0x9F, 0x36};
static const ByteArray kTagCid                        = {0x9F, 0x27};
static const ByteArray kTagCvmResults                 = {0x9F, 0x34};
static const ByteArray kTagDedicatedFileName          = {0x84};
static const ByteArray kTagFciTemplate                = {0x6F};
static const ByteArray kTagFciProprietaryTemplate     = {0xA5};
static const Byte      kTagFormat2                    = 0x77;
static const ByteArray kTagIad                        = {0x9F, 0x10};
static const ByteArray kTagPan                        = {0x5A};
static const ByteArray kTagPanSeqNum                  = {0x5F, 0x34};
static const ByteArray kTagPoscii                     = {0xDF, 0x4B};
static const ByteArray kTagSdad                       = {0x9F, 0x4B};
static const ByteArray kTagTerminalCountryCode        = {0x9F, 0x1A};
static const ByteArray kTagTransactionCurrencyCode    = {0x5F, 0x2A};
static const ByteArray kTagTrxDate                    = {0x9A};
static const ByteArray kTagTrxType                    = {0x9C};
static const ByteArray kTagTvr                        = {0x95};
static const ByteArray kTagUn                         = {0x9F, 0x37};

// Response Codes ISO 7816
namespace Iso7816 {
  static const ByteArray kSwClaNotSupported             = {0x6E, 0x00};
  static const ByteArray kSwConditionsNotSatisfied      = {0x69, 0x85};
  static const ByteArray kSwFileNotFound                = {0x6A, 0x82};
  static const ByteArray kSWIncorrectP1P2               = {0x6A, 0x86};
  static const ByteArray kSwInsNotSupported             = {0x6D, 0x00};
  static const ByteArray kSwRecordNotFound              = {0x6A, 0x83};
  static const ByteArray kSuccessWord                   = {0x90, 0x00};
  static const ByteArray kSwUnknown                     = {0x6F, 0x00};
  static const ByteArray kSwWrongLength                 = {0x67, 0x00};
}  // end of namespace Iso7816

// Business logic values
static const uint32_t  kMaxCurrencyValue              = 999;
static const uint64_t  kMaxTransactionValue           = 999999999999;

// Command APDU - Ins values
static const Byte kInsComputeCC                       = 0x2A;
static const Byte kInsGenerateAC                      = 0xAE;
static const Byte kInsGetGpo                          = 0xA8;
static const Byte kInsReadRecord                      = 0xB2;
static const Byte kInsSelect                          = 0xA4;

// Command APDU - Cla values
static const Byte kClaComputeCC                       = 0x80;
static const Byte kClaGenerateAC                      = 0x80;
static const Byte kClaGetGpo                          = 0x80;
static const Byte kClaReadRecord                      = 0x00;
static const Byte kClaSelect                          = 0x00;

// Command Apdu - P1 Values
static const Byte kP1ComputeCC                        = 0x8E;
static const Byte kP1GetGpo                           = 0x00;
static const Byte kP1Select                           = 0x04;

// Command Apdu - P2 Values
static const Byte kP2ComputeCC                        = 0x80;
static const Byte kP2GetGpo                           = 0x00;
static const Byte kP2Select                           = 0x00;

// Command Apdu - Lc Values
static const Byte kLcComputeCC                        = 0x16;
static const Byte kLcGetGpo1                          = 0x0D;
static const Byte kLcGetGpo2                          = 0x03;

// Other APDUs values
static const Byte kGetGpoDataTag                      = 0x83;
static const Byte kGetGpoDataLength1                  = 0x0B;
static const Byte kGetGpoDataLength2                  = 0x01;

static const int kCommandApduClaOffset                = 0;
static const int kCommandApduInsOffset                = 1;
static const int kCommandApduP1Offset                 = 2;
static const int kCommandApduP2Offset                 = 3;
static const int kCommandApduLcOffset                 = 4;
static const int kCommandApduDataOffset               = 5;

// Configuration values
static const size_t kMinimumCdol1Lenght               = 45;
static const size_t kUdol1Lenght                      = 22;
static const size_t kMaxRsaKeyParameterLength         = 100;

// CID constants
static const Byte kOnlineDecision                     = 0x80;
static const Byte kDeclineDecision                    = 0x00;

// Transit Related values
static const ByteArray kMerchantCodeTransit1          = {0x41, 0x11};
static const ByteArray kMerchantCodeTransit2          = {0x41, 0x31};
static const ByteArray kMerchantCodeTransit3          = {0x47, 0x84};
static const ByteArray kMerchantCodeTransit4          = {0x75, 0x23};

// Generate AC related headers
static const Byte      kDdaHeader                     = 0x6A;
static const Byte      kDdaTrailer                    = 0xBC;

// Some other utility values
static const ByteArray kPosciiConflictingContext      = {0x00, 0x08, 0x00};
static const ByteArray kPosciiZeroes                  = {0x00, 0x00, 0x00};
static const ByteArray kPosciiPinRequired             = {0x00, 0x01, 0x00};
static const ByteArray kPosciiCvmEntered              = {0x00, 0x10, 0x00};
static const ByteArray kTerminalCodeUS                = {0x08, 0x40};
static const ByteArray kTerminalCode0000              = {0x00, 0x00};

// Reference Control Parameters coding for Generate AC command
static const Byte kRcpAac                             = {0x00};
static const Byte kRcpTc                              = {0x40};
static const Byte kRcpArqc                            = {0x80};
static const Byte kRcpRfu                             = {0xC0};
static const Byte kRcpDdaAcRequested                  = {0x01};
static const Byte kRcpDdaAcNotRequested               = {0x00};

static const ByteArray kAtcRandomKeys                 = {0x00, 0x01};

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_CONSTANTS_H_)  // NOLINT
