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

#ifndef SRC_CORE_UTILITIES_H_  //NOLINT
#define SRC_CORE_UTILITIES_H_  //NOLINT

// Project Libraries
#include <utils/byte_array.h>
#include <cryptopp562/integer.h>
#include <core/mobile_kernel/dsrp_output_data.h>
#include <core/mcm/mcm_card_profile.h>
#include <core/mcm/contactless_transaction_context.h>

#ifdef __ANDROID__
#include <jni.h>
#endif

// C++ Libraries
#include <string>
#include <vector>
#include <map>

namespace mcbp_core {

// All the utility functions are defined here and implemented in the cpp file

/** Convert an int64_t to BCD encoding.
 *  @param input The int64_t to be converted to BCD
 *  @param length The maximum number of bytes to be used for the conversion
 *  @return The ByteArray containing the converted input into BCD format
 */
ByteArray Int64ToBcd(const int64_t& input, const std::size_t& length);

/** Convert a string into a Byte Array of HEX values.
 *
 * If the lenght of the string is odd an exception is raised.
 * Note that if the size of the ByteArray is half the size of the string.
 * Examples:
 * - A0E28F -> 0xA0, 0xE2, 0x8F
 * - 23FD1  -> Exception raised
 *
 * @param str The input string.
 * @param array A pointer to the ByteArray that will contain the converted
 *              data.
 */
void StringToHex(const std::string& str, ByteArray* const array);

/**
 * Return the TLV encoding of a given pair of tag, value.
 * @param The Tag
 * @param The Value
 * @return The TLV (Tag-Length-Value) encoding
 */
ByteArray Tlv(const ByteArray& tag, const ByteArray& value);

/**
 * Return the TLV encoding of a given pair of tag, value.
 * @param The Tag
 * @param The Value
 * @return The TLV (Tag-Length-Value) encoding
 */
inline ByteArray Tlv(const ByteArray& tag, const Byte& value) {
  return Tlv(tag, ByteArray(1, value));
}

/**
  * Convert a ByteArray into a vector of char for easy printing
  */
std::vector<char> ByteToHexChar(const ByteArray& source);

/**
 * Securely delete a ByteArray (overwrite memory first and then
 * clean the data structure).
 */
inline void Wipe(ByteArray* array) NOEXCEPT {
  std::fill(array->begin(), array->end(), 0x00);
  array->clear();
}

/**
 * Securely delete a std::string
 */
inline void Wipe(std::string* str) NOEXCEPT {
  std::fill(str->begin(), str->end(), 0);
  str->clear();
}
/**
 * Securely delete a Crypto++ Integer. The Integer is overwritten with zeroes
 * @param input The crypto++ Integer.
 */
inline void Wipe(CryptoPP::Integer input) {
  for (std::size_t i = 0; i < input.ByteCount(); i++) input.SetByte(i, 0x00);
}

/**
 * Check whether a given vector has all the element equal to zero.
 * @param input The input ByteArray
 * @return true if all the elements of the input vector are equal to 0x00,
 *         false otherwise
 */
bool Zeroes(const ByteArray& input);

/**
 * Append the source ByteArray to the Destination.
 * @param source A constant reference to the ByteArray to be appended
 * @param destination The ByteArray that will be modified
 */
inline void Append(const ByteArray& source, ByteArray* destination) {
  destination->insert(destination->end(), source.begin(), source.end());
}

/**
 * Append the source Byte to the Destination.
 * @param source A constant reference to the Byte to be appended
 * @param destination The ByteArray that will be modified
 */
inline void Append(const Byte& source, ByteArray* destination) {
  destination->push_back(source);
}

/** Convert a Word (represented as ByteArray of 2 elements into a uint16_t.
 *  @param word The ByteArray of 2 elements to be converted
 *  @return the Word encoded as uint16_t
 */
uint16_t WordToUnsigned16(const ByteArray& word);

/** Convert a uint16_t into a Word of 2 elements ByteArray.
 *  @param input The Word represented as uint16_t
 *  @return The Word as ByteArray
 */
ByteArray Unsigned16ToWord(const uint16_t input);

/** Convert a ByteArray into its Base64 representation.
 *  The resulting ByteArray would be 33% longer than the original one due to 
 *  Base64 overhead.
 *  @param input The ByteArray to be converted
 *  @return The ByteArray encoded as Base64
 */
ByteArray EncodeToBase64(const ByteArray& input);

/**
 * Convert a string into a ByteArray.
 * @param str The input string
 * @return The ByteArray which contains the converted string
 */
ByteArray StringToByteArray(const std::string& str);

/**
 * ByteArray to String.
 * It is used to return strings to Java (instead of ByteArray)
 */
std::string ByteArrayToString(const ByteArray& array);

/**
 * Calculate a date in ByteArray format YYMMDD given the integer values
 */
ByteArray DateToByteArray(const unsigned int year, const unsigned int month,
                          const unsigned int day);

/**
 * Validate the PAN number
 */
std::string PanToString(const ByteArray& pan);

/**
 * Check whether a date in ByteArray format YYMMDD is correct or not
 */
bool CheckDate(const ByteArray& date);

/**
 * Generate a UCAF formatted message than can be sent to the Issuer Simulator
 * for validation
 * @return The UCAF formatted message
 */
ByteArray UcafResponse(const DsrpOutputData& data);

/**
 * Generate a DE55 formatted message than can be sent to the Issuer Simulator
 * for validation
 * @return The DE55 formatted message
 */
ByteArray De55Response(const DsrpOutputData& data);

/**
 * Determine the Record Id for a given sfi and record number.
 * This is an implementation choice and not based on MCBP specs
 * @param sfi The SFI value
 * @param record_number The Record Number
 * @return The Record ID to be used in the map<RecordId, RecordValue>
 */
inline uint16_t RecordId(const uint8_t sfi, const uint8_t record_number) {
  return  (sfi << 8) + record_number;
}

const int DEFAULT_BLOCK_SIZE = 16;

/**
 * Add Padding according to the ISO 7816 standard (e.g. 0x80 followed by 0x00s)
 */
ByteArray add_iso_7816_padding(const ByteArray& input,
                               const int block_size = DEFAULT_BLOCK_SIZE);

/**
 * Remove padding according to the ISO 7816 standard, if present
 */
ByteArray remove_iso_7816_padding(const ByteArray& input,
                                  const int block_size = DEFAULT_BLOCK_SIZE);

/**
 * Perform the XOR function between two vectors
 */
ByteArray do_xor(const ByteArray& first_array, const std::size_t first_offset,
                 const ByteArray& second_array, const std::size_t second_offset,
                 const std::size_t length);

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_UTILITIES_H_)  //NOLINT
