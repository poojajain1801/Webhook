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

/*******************************************************************************
 * Data Structures in this file are used for testing purposes only and 
 * should not be included and/or used outside the unit test
 ******************************************************************************/

#ifndef SRC_TEST_DATA_TEST_DATA_H_  // NOLINT
#define SRC_TEST_DATA_TEST_DATA_H_  // NOLINT

// project Libraries
#include <utils/byte_array.h>
#include <wrappers/keys_data.h>
#include <wrappers/card_profile_data.h>

#include <unit_test/rapidxml.hpp>
#include <unit_test/rapidxml_utils.hpp>

// C++ libraries
#include <string>
#include <map>

namespace mcbp_unit_test {

using std::string;
using std::map;

struct KeysId {
  string card_name;
  uint16_t atc;
};

inline bool operator<(const KeysId& id_1, const KeysId& id_2) {
  if (id_1.card_name == id_2.card_name) return id_1.atc < id_2.atc;
  return id_1.card_name < id_2.card_name;
}

/**
 *  \brief     Give access to built-in set of keys for testing / verification
 *             purposes.
 *  \details   It currently contains 200 test keys
 *             library may be compiled with hidden visibility flag on
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class TestKeysSet {
 public:
  // Return a specific set of TestKeys (identified by ATC value) for a specific
  // card
  static const mcbp_core::KeysData& test_keys(const string& card_name,
                                              const uint16_t atc);

  // Return a TestKeys for a given PAN and PAN Sequence Number
  // with the specified ATC value (as string)
  static const mcbp_core::KeysData& test_keys(const string& card_name,
                                              const std::string& atc);

  // Get a pointer to the TestKeySet object (READ-ONLY)
  static TestKeysSet* instance();

 private:
  TestKeysSet();

  // Data structure to store the entire set of keys
  static std::map<KeysId, mcbp_core::KeysData> keys_;

  // Pointer to the only instance of this object
  static TestKeysSet* instance_;

  // Load static keys
  void initialize();

  // Friend function used for thread-safe initialization
  friend void CreateTestKeysSet();

  // Utility function to read parameters
  static bool read_parameters(const rapidxml::xml_node<> *credential_node,
                              mcbp_core::KeysData* data);
};

/**
 *  \brief     Give access to built-in card profile for testing / verification
 *             purposes.
 *  \details   Only one test card profile is currently supported
 *             library may be compiled with hidden visibility flag on
 *  \copyright MasterCard International Incorporated and/or its affiliates. 
 *             All rights reserved.
 */
class TestCardProfileSet {
 public:
  // Return a TestCardProfileSet with the specified card ID
  static const mcbp_core::CardProfileData& test_card(const string& card_name);

  // Get a pointer to the TestCardProfileSet object (READ-ONLY)
  static TestCardProfileSet* instance();

 private:
  TestCardProfileSet();

  // Data structure to store the entire set of keys
  static std::map<string, mcbp_core::CardProfileData> cards_;

  // Pointer to the only instance of this object
  static TestCardProfileSet* instance_;

  // Load static keys
  void initialize();

  // Friend function used for thread-safe initialization
  friend void CreateTestCardProfileSet();

  // Utility function to read parameters
  static bool read_parameters(const rapidxml::xml_node<> *card_node,
                              mcbp_core::CardProfileData* data);
  // Add a parameter as string
  static bool add_param(const map<string, string>& parameters,
                        const string& parameter, string* dest);
  // Add a parameter as ByteArray
  static bool add_param(const map<string, string>& parameters,
                        const string& parameter, ByteArray* dest);
  // Add a parameter as bool
  static bool add_param(const map<string, string>& parameters,
                        const string& parameter, bool* dest);
};

}  // namespace mcbp_unit_test

#endif  // defined(SRC_TEST_DATA_TEST_DATA_H_)  // NOLINT
