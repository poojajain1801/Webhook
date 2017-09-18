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
#include <utils/utilities.h>
#include <utils/mcbp_core_exception.h>
#include <core/mcm/mcm_card_profile.h>

// C++ libraries
#include <vector>
#include <string>
#include <ctime>

namespace mcbp_core {
using std::string;
// All the utility functions are implemented here

Byte GetBcd(const uint32_t digit_1, const uint32_t digit_2) {
  return Byte(digit_1 << 4) + Byte(digit_2);
}

ByteArray Int64ToBcd(const int64_t& input, const std::size_t& length) {
  int64_t number = input;
  ByteArray result(length, 0x00);

  std::size_t i = 0;
  while (number != 0) {
    unsigned digit_1 = 0;
    unsigned digit_2 = 0;
    number -= (digit_1 = number % 10);
    number /= 10;
    number -= (digit_2 = number % 10);
    number /= 10;
    result[length - i - 1] = GetBcd(digit_2, digit_1);
    i++;
    if (i == length) break;
  }
  return result;
}

// Convert a string of two digits into a Byte (e.g. 'FF' -> 0xFF)
Byte StringToHexByte(const string& two_digits) {
  if (two_digits.size() != 2) {
    mcbp_core::InvalidInput e("string_to_hex_byte - Invalid input");
    throw e;
  }
  Byte result = 0x00;
  for (std::size_t i = 0; i < 2; i++) {
    Byte digit = (Byte)two_digits[i];
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
      mcbp_core::InvalidInput e(("string_to_hex_byte - Invalid digits: " +
                                  two_digits).c_str());
      throw e;
    }

    if (i == 0) {
      result = (digit << 4);
    } else {
       result += digit;
    }
  }
  return result;
}

void StringToHex(const std::string& str, ByteArray* const array) {
  if (str.size() % 2 != 0) {
    string error = "StringToHex - invalid string length";
    mcbp_core::InvalidInput e(error);
    throw e;
  }

  array->resize(str.size() / 2);
  if (array->empty()) return;

  for (std::size_t i = 0; i < str.size() - 1; i += 2) {
    string digits(str.begin() + i, str.begin() + i + 2);
    (*array)[i / 2] = StringToHexByte(digits);
  }
}

ByteArray TlvLength(const ByteArray& value) {
  /*
    From EMVco specs 4.3 - Appendix B2

    When bit b8 of the most significant byte of the length field is set to 0,
    the length field consists of only one byte. Bits b7 to b1 code the number
    of bytes of the value field. The length field is within the range 1 to 127.

    When bit b8 of the most significant byte of the length field is set to 1,
    the subsequent bits b7 to b1 of the most significant byte code the number
    of subsequent bytes in the length field. The subsequent bytes code an
    integer representing the number of bytes in the value field. Two bytes are
    necessary to express up to 255 bytes in the value field.

    EMV 4.2 Book 3 - Appendix B (Rules for BER-TLV Data Objects)

    The length field (L) consists of one or more consecutive bytes.
    It indicates the length of the following field. The length field of the
    data objects described in this specification which are transmitted over the
    card-terminal interface is coded on one or two bytes.

    Note: Three length bytes may be used if needed for templates '71' and '72'
    and tag '86' (to express length greater than 255 bytes), as they are not
    transmitted over the card-terminal interface.
    */
  const std::size_t length = value.size();

  if (length <= 127) {
    return ByteArray(1, (Byte)(length));
  } else if (length <= 255) {
    // The first byte has b8 set to 1 and specify the number of additional bytes
    ByteArray tmp(2, 0x81);
    tmp[1] = (Byte)length;
    return tmp;
  } else if (length > 255) {
    const char* const error = "TLV Length greater than 255 are not supported";
    mcbp_core::InvalidInput e(error);
    throw e;
  }
  return ByteArray();
}

ByteArray Tlv(const ByteArray& tag, const ByteArray& value) {
  ByteArray tlv(tag);
  Append(TlvLength(value), &tlv);
  Append(value, &tlv);
  return tlv;
}

bool Zeroes(const ByteArray& input) {
  for (std::size_t i = 0; i < input.size(); i++) {
    if (input[i] != 0) {
      return false;
    }
  }
  return true;
}

std::vector<char> ByteToHexChar(const ByteArray& source) {
  char const hex_chars[16] = { '0', '1', '2', '3', '4', '5', '6', '7',
                               '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  const std::size_t no_bytes = source.size();
  const std::size_t no_hex = no_bytes * 2;

  std::vector<char> hex_string(no_hex + 1, 0xFF);
  // Explicitly terminate the string
  hex_string[no_hex] = '\0';

  for (std::size_t i = 0, j = 0; i < no_bytes; i++) {
    const char byte = source[i];
    hex_string[j] = hex_chars[(byte & 0xF0) >> 4];
    j++;
    hex_string[j] = hex_chars[(byte & 0x0F) >> 0];
    j++;
  }
  return hex_string;
}

ByteArray StringToByteArray(const string& str) {
  ByteArray result(str.size(), 0x00);
  for (std::size_t i = 0; i < str.size(); i++)
    result[i] = static_cast<Byte>(0xFF & static_cast<Byte>(str[i]));
  return result;
}

uint16_t WordToUnsigned16(const ByteArray& word) {
  if (word.size() != 2) throw InvalidInput("Input must be a Word (2 bytes)");

  return ( (((word[0] & 0xF0) >> 4) << 12) +
           (((word[0] & 0x0F)     ) <<  8) +  // NOLINT
           (((word[1] & 0xF0) >> 4) <<  4) +
           (((word[1] & 0x0F))));
}

ByteArray Unsigned16ToWord(const uint16_t input) {
  ByteArray result(2, 0x00);
  result[1] = static_cast<Byte>(input & 0x00FF);
  result[0] = static_cast<Byte>((input & 0xFF00) >> 8);
  return result;
}

ByteArray EncodeToBase64(const ByteArray& input) {
  static const char *base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    "abcdefghijklmnopqrstuvwxyz"
                                    "0123456789+/";

  const std::size_t no_bytes = input.size();
  const std::size_t no_blocks = no_bytes / 3 + (no_bytes % 3 != 0 ? 1: 0);

  // Prepare the result
  ByteArray result;
  result.reserve(4 * no_blocks);

  // Parse the input
  for (std::size_t i = 0; i < no_blocks; i++) {
    // Prepare the next block
    uint32_t block = 0;

    // Copy the input block into a uint32_t
    for (std::size_t j = 0; j < 3 && (i * 3 + j) < no_bytes; j++)
      block += (input[i * 3 + j] << ((2 - j) * 8));

    for (int j = 3, k = 0; j >= 0; j--, k++) {
      char value = base64_chars[(block >> (6 * j)) & 0x0000003F];
      result.push_back(static_cast<Byte>(value));
    }
  }

  // Add padding, if the last block is incomplete
  const std::size_t padding_bytes = no_bytes % 3 != 0 ? 3 - (no_bytes % 3): 0;

  for (std::size_t i = 1; i <= padding_bytes; ++i)
    result[4 * no_blocks - i] = static_cast<Byte>('=');

  return result;
}

char HexToDigit(const Byte value) {
  if (value >= 0 && value <= 9) {
    // It is a number - 48 is the ASCII value for 0
    return static_cast<char>(value + 48);
  }
  if (value >= 10 && value <= 15) {
    // 65 is the ASCII value for A (thus we have to add 55)
    return static_cast<char>(value + 55);
  }
  Exception e("Invalid Character: " + string(value, sizeof(Byte)));
  throw e;

  return 0;
}

std::string ByteArrayToString(const ByteArray& array) {
  string result;
  result.reserve(array.size() * 2);
  typedef ByteArray::const_iterator Iterator;
  for (Iterator it = array.begin(); it != array.end(); ++it) {
    Byte part_1 = ((*it & 0xF0) >> 4);
    result.push_back(HexToDigit(part_1));
    Byte part_2 = (*it & 0x0F);
    result.push_back(HexToDigit(part_2));
  }
  return result;
}

ByteArray DateToByteArray(const unsigned int year, const unsigned int month,
                          const unsigned int day) {
  ByteArray date;
  date.reserve(3);
  Append(Int64ToBcd(year, 1), &date);
  Append(Int64ToBcd(month, 1), &date);
  Append(Int64ToBcd(day, 1), &date);
  return date;
}

std::string PanToString(const ByteArray& pan) {
  std::string result;
  result.reserve(2 * pan.size());

  for (std::size_t i = 0; i < pan.size(); i++) {
    const Byte digit_1 = (pan[i] >> 4) & 0x0F;
    if (digit_1 < 0 || digit_1 > 9)
      throw InvalidInput("Invalid digit");
    result.push_back(static_cast<char>(digit_1 + 0x30));

    const Byte digit_2 = pan[i] & 0x0F;
    if (digit_2 < 0 || digit_2 > 9) {
      // Accept 'F' only if it is in the last nibble
      if (i == (pan.size() - 1) && (digit_2 & 0x0F) == 'F')
        return result;
      throw InvalidInput("Invalid digit");
    }
    result.push_back(static_cast<char>(digit_2 + 0x30));
  }
  return result;
}

bool CheckDate(const ByteArray& date) {
  if (date.size() != 3) return false;

  time_t now = time(0);
  tm *ltm = new tm;
#ifdef _WIN32
  localtime_s(ltm, &now);
#else
  localtime_r(&now, ltm);
#endif

  const int today_year  = (1900 + ltm->tm_year) % 100;
  const int today_month = 1 + ltm->tm_mon;
  const int today_day   = ltm->tm_mday;

  const int year  = (10 * ((date[0] >> 4) & 0x0F)) + (date[0] & 0x0F);
  const int month = (10 * ((date[1] >> 4) & 0x0F)) + (date[1] & 0x0F);
  const int day   = (10 * ((date[2] >> 4) & 0x0F)) + (date[2] & 0x0F);

  delete ltm;

  if (year < today_year) return false;
  if (year > today_year) return true;

  // Else expiry year is this year
  if (month < today_month) return false;
  if (month > today_month) return true;

  // Else expiry month is this month
  if (day < today_day) return false;

  // The expiry day has not been reached yet or
  // it is today. In both cases return true
  return true;
}

ByteArray UcafResponse(const DsrpOutputData& data) {
  // The UCAF Response is formatted as ISO8583 message
  const ByteArray message_length   = {0x00, 0x00, 0x00, 0x76};
  const ByteArray message_type     = {0x11, 0x00};
  const ByteArray primary_bitmap   = {0x70, 0x04, 0x07, 0x00,
                                      0x29, 0x81, 0x80, 0x00};
  const ByteArray processing_code  = {0x00, 0x00, 0x00};

  const ByteArray pos_entry_mode   = {0x45, 0x30, 0x30, 0x31, 0x30, 0x31, 0x45,
                                      0x30, 0x33, 0x33, 0x34, 0x36};
  const ByteArray function_code    = {0x01, 0x00};
  const ByteArray service_code     = {0x00, 0x00};
  const ByteArray retrieval_ref_no = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                                      0x30, 0x30, 0x30, 0x34, 0x38};
  const ByteArray terminal_id      = {0x30, 0x32, 0x30, 0x30,
                                      0x31, 0x31, 0x30, 0x31};
  const ByteArray de48_header      = {0x54, 0x34, 0x33, 0x1C};  // 1C length

  // Convert PAN and Track 2 Equivalent Data from String
  ByteArray pan;
  StringToHex(data.pan, &pan);
  ByteArray track_2;
  StringToHex(data.track_2_data, &track_2);

  ByteArray ucaf;
  ucaf.reserve(256);  // We reserve more than needed (efficiency).
  Append(message_length, &ucaf);
  Append(message_type,   &ucaf);
  Append(primary_bitmap, &ucaf);
  Append(static_cast<Byte>(data.pan.size()), &ucaf);
  Append(pan, &ucaf);
  Append(processing_code, &ucaf);
  Append(Int64ToBcd(data.transaction_amount, 6), &ucaf);
  Append({data.expiry_date.begin(), data.expiry_date.begin() + 2}, &ucaf);
  Append(pos_entry_mode, &ucaf);
  Append(Int64ToBcd(data.pan_sequence_number, 2), &ucaf);
  Append(function_code, &ucaf);
  Append(static_cast<Byte>(data.track_2_data.size()), &ucaf);
  Append(track_2, &ucaf);
  Append(retrieval_ref_no, &ucaf);
  Append(service_code, &ucaf);
  Append(terminal_id, &ucaf);
  Append(static_cast<Byte>(data.transaction_cryptogram_data.size() +
                           de48_header.size()), &ucaf);
  Append(de48_header, &ucaf);
  Append(data.transaction_cryptogram_data, &ucaf);
  Append(Int64ToBcd(data.currency_code, 2), &ucaf);

  return ucaf;
}

ByteArray De55Response(const DsrpOutputData& data) {
  // The DE55 Response is formatted as ISO8583 message
  const ByteArray message_length   = {0x00, 0x00, 0x00, 0xD3};
  const ByteArray message_type     = {0x11, 0x00};
  const ByteArray primary_bitmap   = {0x70, 0x04, 0x07, 0x00,
                                      0x29, 0x80, 0x82, 0x00};
  const ByteArray processing_code  = {0x00, 0x00, 0x00};

  const ByteArray pos_entry_mode   = {0x45, 0x30, 0x30, 0x31, 0x30, 0x31, 0x45,
                                      0x30, 0x33, 0x33, 0x34, 0x36};
  const ByteArray function_code    = {0x01, 0x00};
  const ByteArray service_code     = {0x00, 0x00};
  const ByteArray retrieval_ref_no = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
                                      0x30, 0x30, 0x30, 0x34, 0x38};
  const ByteArray terminal_id      = {0x30, 0x32, 0x30, 0x30, 0x31,
                                      0x31, 0x30, 0x31};

  // Convert PAN and Track 2 Equivalent Data from String
  ByteArray pan;
  StringToHex(data.pan, &pan);
  ByteArray track_2;
  StringToHex(data.track_2_data, &track_2);

  ByteArray de55;
  de55.reserve(256);  // We reserve more than needed (efficiency).
  Append(message_length, &de55);
  Append(message_type,   &de55);
  Append(primary_bitmap, &de55);
  Append(static_cast<Byte>(data.pan.size()), &de55);
  Append(pan, &de55);
  Append(processing_code, &de55);
  Append(Int64ToBcd(data.transaction_amount, 6), &de55);
  Append({data.expiry_date.begin(), data.expiry_date.begin() + 2}, &de55);
  Append(pos_entry_mode, &de55);
  Append(Int64ToBcd(data.pan_sequence_number, 2), &de55);
  Append(function_code, &de55);
  Append(static_cast<Byte>(data.track_2_data.size()), &de55);
  Append(track_2, &de55);
  Append(retrieval_ref_no, &de55);
  Append(service_code, &de55);
  Append(terminal_id, &de55);
  Append(Int64ToBcd(data.currency_code, 2), &de55);
  Append(static_cast<Byte>(data.transaction_cryptogram_data.size()), &de55);
  Append(data.transaction_cryptogram_data, &de55);

  return de55;
}

ByteArray add_iso_7816_padding(const ByteArray& input, const int block_size) {
  const size_t input_size = input.size();
  const size_t padded_length =
      input_size + block_size - (input_size % block_size);

  ByteArray data_with_padding(padded_length, 0x00);
  for (int i = 0; i < input_size; i++) {
    data_with_padding[i] = input[i];
  }
  data_with_padding[input_size] = 0x80;
  return data_with_padding;
}

ByteArray remove_iso_7816_padding(const ByteArray& input,
                                  const int block_size) {
  // find if there is padding
  size_t padding_bytes = 0;
  bool found = false;

  // Check the last block for padding
  for (size_t i = input.size() - 1; i >= input.size() - block_size; i--) {
    padding_bytes++;
    if (input[i] == (byte) 0x00) {
      continue;
    }
    if (input[i] == (byte) 0x80) {
      found = true;
      break;
    }
  }
  if (found) {
    const size_t result_length = input.size() - padding_bytes;
    ByteArray result(result_length);
    for (int i = 0; i < result_length; i++) {
      result[i] = input[i];
    }
    return result;
  }
  // If padding has not been found, just return the input data
  return input;
}

/**
 * Perform the XOR function between two vectors
 */
ByteArray do_xor(const ByteArray& first_array,
                 const std::size_t first_offset,
                 const ByteArray& second_array,
                 const std::size_t second_offset,
                 const std::size_t length) {
  ByteArray result(length, 0x00);
  for (int i = 0; i < length; i++) {
    result[i] = first_array[first_offset + i] ^ second_array[second_offset + i];
  }
  return result;
}

}  // namespace mcbp_core
