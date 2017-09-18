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

#ifndef mcbpcore_jni_unit_test_utilities_h  // NOLINT
#define mcbpcore_jni_unit_test_utilities_h  // NOLINT

// Project includes
#include <utils/byte_array.h>

// Library includes
#include <string>

using std::string;

namespace mcbp_unit_test {

// NOTE: This is the same function as the utilities.h file in utils
// It is replicated here to properly compile when WB is used

// Convert a string into a Byte Array of HEX values
// (e.g. 'A0FC21' -> 0xA0, 0xFC, 0x21)
// If the lenght of the string is odd (an exeception is raised)
void StringToHex(const std::string& str, ByteArray* const array);

/**
  * Convert a ByteArray into a string
  */
std::string byte_array_to_string(const ByteArray& array);

}  // End of namespace mcbp_unit_test

#endif  // NOLINT
