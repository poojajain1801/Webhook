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

// Project includes
#include <unit_test/unit_test_utilities.h>

// Libraries includes
#include <iostream>
#include <string>

using std::string;
using std::cerr;
using std::endl;

namespace mcbp_unit_test {

Byte string_to_hex_byte(const string& two_digits) {
  if (two_digits.size() != 2) {
    cerr << "string_to_hex_byte - Invalid input" << endl;
    throw std::exception();
  }

  Byte result = 0x00;
  Byte digit  = 0x00;
  for (unsigned int i = 0; i < 2; i++) {
    digit = (Byte)two_digits[i];
    // Conversion from ASCII encoding to a single Byte
    if (digit >= 48 && digit <= 57) {
      // Digit in the interval 0-9
      digit -= 48;
    } else if (digit >=65 && digit <= 70) {
      // Digit in the interval A-F
      digit -= 55;
    } else if (digit >=97 && digit <= 102) {
      // Digit in the interval a-f
      digit -= 87;
    } else {
      cerr << "string_to_hex_byte - Invalid digits: " << two_digits << endl;
      throw std::exception();
    }
    if (i == 0) {
      // 4 most significant bits
      // Shift and assign to result
      result = (digit << 4);
    } else {
      // 4 least significant bits
      // Add to result
      result += digit;
    }
  }
  return result;
}

void StringToHex(const std::string& str, ByteArray* const result) {
  if (str.size() % 2 != 0) {
    cerr << "string_to_hex_byte_array - invalid string length" << endl;
    throw std::exception();
  }

  result->resize(str.size() / 2);

  if (result->empty()) return;

  for (unsigned int i = 0; i < str.size() - 1; i += 2) {
    string digits(str.begin() + i, str.begin() + i + 2);
    result->at(i / 2) = string_to_hex_byte(digits);
  }
}

char hex_to_digit(const Byte value) {
  if (value >= 0 && value <= 9) {
    // It is a number - 48 is the ASCII value for 0
    return static_cast<char>(value + 48);
  }
  if (value >= 10 && value <= 15) {
    // 65 is the ASCII value for A (thus we have to add 55)
    return static_cast<char>(value + 55);
  }
  cerr << "Invalid Character: " << string(value, sizeof(Byte)) << endl;
  throw std::exception();

  return 0;
}

std::string byte_array_to_string(const ByteArray& array) {
  string result;
  result.reserve(array.size() * 2);
  typedef ByteArray::const_iterator Iterator;
  for (Iterator it = array.begin(); it != array.end(); ++it) {
    Byte part_1 = ((*it & 0xF0) >> 4);
    result.push_back(hex_to_digit(part_1));
    Byte part_2 = (*it & 0x0F);
    result.push_back(hex_to_digit(part_2));
  }
  return result;
}

}  // namespace mcbp_unit_test
