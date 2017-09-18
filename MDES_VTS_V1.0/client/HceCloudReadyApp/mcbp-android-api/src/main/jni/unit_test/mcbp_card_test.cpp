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

// Project Libraries
#include <core/mobile_kernel/dsrp_input_data.h>
#include <unit_test/mcbp_card_test.h>
#include <unit_test/unit_test_utilities.h>
#include <unit_test/test_data.h>

// C++ Libraries
#include <exception>
#include <iostream>  // NOLINT
#include <list>
#include <string>
#include <utility>

namespace mcbp_unit_test {

// Initialize static resources
Parameter McbpCardTest::tmp_values_;
TestCaseInput* McbpCardTest::shared_input_ = nullptr;
ByteArray McbpCardTest::tmp_pin_;
rapidxml::xml_document<> McbpCardTest::input_;
rapidxml::xml_node<> *McbpCardTest::root_node_ = nullptr;
UnitTestWrapper *McbpCardTest::card_wrapper_ = nullptr;
Commands McbpCardTest::current_test_commands_;

using std::string;
using std::cerr;
using std::endl;
using std::list;
using std::string;
using std::map;
using std::pair;
using std::vector;
using std::exception;
using rapidxml::file;
using rapidxml::xml_node;
using ::testing::ValuesIn;

/*******************************************************************************
 * TEST CASE Logic definition
 ******************************************************************************/

TEST_P(McbpCardTest, ApduSequenceTest) {
  const string& card_name = GetParam().first;
  const string& test_name = GetParam().second;

  std::cerr << "[          ]  -->" << card_name;
  std::cerr << ":" << test_name << std::endl;

  ASSERT_EQ(true, load_card(card_name, test_name, CONTACTLESS))
      << "Input data missing for card " << card_name << "@" << test_name;

  for (Commands::const_iterator i = current_test_commands_.begin();
       i != current_test_commands_.end(); i++) {
    ASSERT_EQ((i->second), card_wrapper_->transceive(i->first));
  }
}

INSTANTIATE_TEST_CASE_P(XMLInput, McbpCardTest,
                        ValuesIn(McbpCardTest::read_tests_list("apdu")));

/*
TEST_F(McbpCardTest, dsrp_ucaf_01) {
  ASSERT_EQ(true, load_card("MasterCard_MCBP_PersoProfile_1", false)) << "Input data missing";
  ByteArray expected_ucaf;
  StringToHex("0000007611007004070029818000105413339000001513000000000000049900151245303031303145303333343600010100245413339000001513D151222600000000000030303030303030303030343800003032303031313031205434331C4161356972356F6D5967763541414637652B6371476F414246413D3D0840", &expected_ucaf);

  mcbp_core::DsrpInputData input;
  input.transaction_amount   = 49900;
  input.other_amount         = 0;
  input.currency_code        = 840;
  input.transaction_type     = 0x00;
  input.unpredictable_number = 2071717674;
  input.cryptogram_type      = mcbp_core::DsrpTransactionType::UCAF;
  input.day                  = 24;
  input.month                = 10;
  input.year                 = 14;
  input.country_code         = 380;
  ASSERT_EQ(expected_ucaf, card_wrapper_->remote_payment(input));
}

TEST_F(McbpCardTest, dsrp_de55_01) {
  ASSERT_EQ(true, load_card("MasterCard_MCBP_PersoProfile_1", false)) << "Input data missing";
  ByteArray expected_de55;
  StringToHex("000000D311007004070029808200105413339000001513000000000000049900151245303031303145303333343600010100245413339000001513D1512226000000000000303030303030303030303438000030323030313130310840799F260835B2581ADAFF9A659F10120114A50000000000000000848899B5D800FF9F36020001950500000000009F2701809F34030100029F37047B7BE72A9F02060000000499009F03060000000000005F2A0208409A031410249C01005A0854133390000015135F3401015F24031512319F1A02038082021A80", &expected_de55);

  mcbp_core::DsrpInputData input;
  input.transaction_amount   = 49900;
  input.other_amount         = 0;
  input.currency_code        = 840;
  input.transaction_type     = 0x00;
  input.unpredictable_number = 2071717674;
  input.cryptogram_type      = mcbp_core::DsrpTransactionType::DE55;
  input.day                  = 24;
  input.month                = 10;
  input.year                 = 14;
  input.country_code         = 380;
  ASSERT_EQ(expected_de55, card_wrapper_->remote_payment(input));
}
*/

/*
TEST_F(McbpCardTest, dsrp_01) {
ByteArray result;
string_to_hex_array("000000D4110070040700298082001054809826001000010000000000000156991811453030313031453033333436000101000A00D0000000303030303030303030303531000030323030313130310978879F34033435539F260878F42E232B377DDF9F2701809F10120114A50000000000000000000000000000009F3704343536379F3602002995050000000000820202809F330320F8C89F1A0200569F3501229F1E0830323030313130319A031311189C01009F02060000000156995F2A0209785F3601029F5301508407A00000000410109F09020105", result );
  ASSERT_EQ( "sialve", dsrp_server(result));
}

TEST_F(McbpCardTest, terminal_01) {
ByteArray result;
string_to_hex_array("000000D4110070040700298082001054809826001000010000000000000156991811453030313031453033333436000101000A00D0000000303030303030303030303531000030323030313130310978879F34033435539F260878F42E232B377DDF9F2701809F10120114A50000000000000000000000000000009F3704343536379F3602002995050000000000820202809F330320F8C89F1A0200569F3501229F1E0830323030313130319A031311189C01009F02060000000156995F2A0209785F3601029F5301508407A00000000410109F09020105", result );
  ASSERT_EQ( "sialve", terminal_simulator(result));
}
*/

/*******************************************************************************
 * Class Definition
 ******************************************************************************/

void McbpCardTest::SetUpTestCase() {
  shared_input_ = new TestCaseInput();
  char input_file[] = "./mcbp_card_test_data.xml";

  if (!read_xml(input_file)) {
    string error = "Unable to read the card profile input file";
    cerr << error.c_str() << endl;
    throw std::exception();
  }
  card_wrapper_ = UnitTestWrapper::instance();
}

void McbpCardTest::TearDownTestCase() {
  delete shared_input_;
  shared_input_ = nullptr;
}

void McbpCardTest::SetUp() {
    // Do Nothing
}

void McbpCardTest::TearDown() {
  card_wrapper_->deactivate();
}

const list< pair<string, string> >
    McbpCardTest::read_tests_list(string filter_string) {
  list< pair<string, string> > result;
  const bool filter = filter_string == "" ? false: true;

  // Pre-screen the XML file for test cases to be executed
  char input_file[] = "./mcbp_card_test_data.xml";

  try {
    file<> xml_file(input_file);

    if (xml_file.data() == nullptr) {
      cerr << "Unable to open the XML file: " << input_file << endl;
      throw exception();
    }
    rapidxml::xml_document<> input_document;
    input_document.parse<0>(xml_file.data());  // Default template is char

    xml_node<> *root_node = input_document.first_node("TestCase");

    if (root_node == nullptr) return result;

    for (xml_node<> *card_node = root_node->first_node("card");
         card_node != nullptr;
         card_node = card_node->next_sibling()) {
      string card_name = card_node->first_attribute("name")->value();

      xml_node<> *tests_node = card_node->first_node("tests");

      if (tests_node == nullptr) continue;

      // For each test
      for (xml_node<> *test_node = tests_node->first_node("test");
           test_node != nullptr;
           test_node = test_node->next_sibling()) {
        // Get the name of the test
        if (test_node->first_attribute("name") != nullptr) {
          const string test_name = test_node->first_attribute("name")->value();

          if (filter) {
            if (test_node->first_attribute("type") == nullptr) continue;
            const string type = test_node->first_attribute("type")->value();
            if (type != filter_string) continue;
          }
          result.push_back(pair<string, string>(card_name, test_name));
        }
      }   // End of test
    }  // End of Card
  } catch (const exception& e) {
    cerr << "Generic exception capture: " << e.what() << endl;
    throw;
  }
  return result;
}

bool McbpCardTest::load_card(string card_name, string test_name,
                             const TestDataType type) {
  // Check if this card exists
  TestCaseInput::const_iterator c_it = shared_input_->find(card_name);
  if (c_it == shared_input_->end()) {
    // Card not found
    cerr << "Card by name " << card_name << " not found" << endl;
    return false;
  }

  const mcbp_core::CardProfileData& card_profile = c_it->second.first;

  // Now check that we have this test case
  Tests::const_iterator c_it_2 = c_it->second.second.find(test_name);
  if ( c_it_2 == c_it->second.second.end() ) {
    cerr << "Test Case by name " << test_name << " not found";
    return false;
  }

  current_test_commands_ = c_it_2->second.cmds;

  // Needed as start function needs to erase the PIN
  ByteArray pin = c_it_2->second.pin;

  // Initialize the new card for both contactless and remote payment
  card_wrapper_->initialize(card_profile);

  if (type == CONTACTLESS) {
    card_wrapper_->start_contactless(c_it_2->second.keys, pin, 0, 0, false);
  } else {  // Remote payment
    card_wrapper_->activate_remote(c_it_2->second.keys, pin);
  }
  return true;
}

const std::list< std::pair<std::string, std::string>  >
    McbpCardTest::tests_list() {
  std::list< std::pair<std::string, std::string> > tests_list;
  if (shared_input_ == nullptr) return tests_list;

  typedef TestCaseInput::const_iterator C_It;
  for (C_It it = shared_input_->begin(); it != shared_input_->end(); ++it) {
    // Go through the list of cards
    std::string card_name = it->first;
    // Get the list of tests
    typedef Tests::const_iterator C_TestIterator;
    for (C_TestIterator c_test_iterator = it->second.second.begin();
          c_test_iterator != it->second.second.end(); ++c_test_iterator) {
      std::string test_name = c_test_iterator->first;
      tests_list.push_back(pair<std::string, std::string>(
          card_name, test_name));
    }
  }
  return tests_list;
}

const mcbp_core::KeysData& McbpCardTest::populate_keys(const string& card_name,
                                                       const string& atc) {
  return TestKeysSet::instance()->test_keys(card_name, atc);
}

// Reads a given parameter and saves it in the ByteArray
// True if the ByteArray is modified (e.g. parameter found). False otherwise
bool McbpCardTest::read_card_value(const string& parameter, ByteArray* dest) {
  string value;
  if ( read_card_value(parameter, &value) ) {
    dest->resize(value.size()/2);
    StringToHex(value, dest);
    return true;
  }
  return false;
}

// Reads a given parameter as string
bool McbpCardTest::read_card_value(const string& parameter, string* dest) {
  typedef Parameter::iterator ProfileIterator;

  ProfileIterator profile_it = tmp_values_.find(parameter);

  if ( profile_it != tmp_values_.end() ) {
    *dest = profile_it->second;
    return true;
  }
  return false;
}

bool McbpCardTest::read_card_value(const string& parameter, bool* dest) {
  typedef Parameter::iterator ProfileIterator;

  ProfileIterator profile_it = tmp_values_.find(parameter);

  if ( profile_it != tmp_values_.end() ) {
    profile_it->second == "true" ? *dest = true: *dest = false;
    return true;
  }
  *dest = false;
  return false;
}

void McbpCardTest::add_test_dependencies(rapidxml::xml_node<> *node,
                                         const Tests& card_tests,
                                         Commands* test_commands) {
  using rapidxml::xml_node;
  xml_node<> * dependencies_node = node->first_node("dependencies");

  if (dependencies_node == nullptr) return;

  // Check if there is pre-test to be performed
  for (rapidxml::xml_node<> *dep_node = dependencies_node->first_node("dep");
    dep_node != nullptr;
    dep_node = dep_node->next_sibling() ) {
    std::string pre_test_name = dep_node->value();
    typedef Tests::const_iterator C_TestIterator;
    C_TestIterator test_it = card_tests.find(pre_test_name);

    if (test_it != card_tests.end()) {
      // I have got the test case I am looking for
      const auto& test_cmds = test_it->second.cmds;
      typedef Commands::const_iterator C_CommandsIt;

      for (C_CommandsIt it = test_cmds.begin(); it != test_cmds.end(); ++it) {
        test_commands->push_back(*it);
      }
    }
  }
}

bool McbpCardTest::read_xml(const char* const file_name) {
  using rapidxml::file;
  using rapidxml::xml_node;
  using std::string;

  if (shared_input_ != nullptr) {
    delete shared_input_;
    // throw mcbp_core::Exception("shared_input_ has been already created");
  }
  shared_input_ = new TestCaseInput;

  try {
    file<> xml_file(file_name);

    if (xml_file.data() == nullptr) {
      cerr << "Unable to open the XML file: " << file_name << endl;
      return false;
    }

    CardName card_name;
    Commands test_commands;

    input_.parse<0>(xml_file.data());  // Default template is char
    root_node_ = input_.first_node("TestCase");

    Tests card_tests;

    // For each card
    for (xml_node<> *card_node = root_node_->first_node("card");
         card_node != nullptr;
         card_node = card_node->next_sibling()) {
      card_name = card_node->first_attribute("name")->value();
      // Clear relevant data structure before reading fields
      tmp_values_.clear();
      card_tests.clear();

      // -----------------------------------------------------------------------
      // Get the card profile
      const auto& card_data =
          TestCardProfileSet::instance()->test_card(card_name);

      // -----------------------------------------------------------------------
      // Read Tests for this Card

      // For each test
      for (xml_node<> *test_node =
              card_node->first_node("tests")->first_node("test");
           test_node != nullptr;
           test_node = test_node->next_sibling()) {
        // Get the name of the test
        const string test_name = test_node->first_attribute("name")->value();

        // ---------------------------------------------------------------------
        // Get the keys and mobile pin

        tmp_values_.clear();

        xml_node<> *keys_node = test_node->first_node("keys");

        mcbp_core::KeysData keys;

        if (keys_node != nullptr) {
          std::string atc = keys_node->value();
          keys = populate_keys(card_name, atc);
        } else {
          ByteArray zero_key = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
          keys.sk_cl_umd = zero_key;
          keys.sk_cl_md  = zero_key;
          keys.sk_rp_umd = zero_key;
          keys.sk_rp_md  = zero_key;
          keys.atc = {0x00, 0x01};
          keys.idn = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        }

        // Clear the temporary test commands data structure before starting.
        test_commands.clear();

        add_test_dependencies(test_node, card_tests, &test_commands);

        // ---------------------------------------------------------------------
        // Get the sequence of C-APDU and R-APDU

        for (xml_node<> *transaction_node = test_node->first_node("c_apdu");
             transaction_node != nullptr;
             transaction_node = transaction_node->next_sibling() ) {
          ByteArray c_apdu, r_apdu;
          StringToHex(transaction_node->value(), &c_apdu);
          transaction_node = transaction_node->next_sibling("r_apdu");
          StringToHex(transaction_node->value(), &r_apdu);

          test_commands.push_back(Command(c_apdu, r_apdu));
        }

        // ---------------------------------------------------------------------
        // Save this test

        TestData test_data;
        test_data.keys = keys;
        test_data.pin  = keys.pin;
        test_data.cmds = test_commands;

        card_tests.insert(pair< TestName, TestData >(test_name, test_data));
      }  // End of Each Test

      using mcbp_core::CardProfileData;
      // Now attach the list of tests to a Card Profile and save everything
      shared_input_->
          insert(pair< CardName, TestCard >(
              card_name, pair< CardProfileData, Tests>(card_data, card_tests)));
    }  // End of Card
  } catch (const std::exception& e) {
    cerr << "Generic exception capture: " << e.what() << endl;
    throw;
  }
  return true;
}

}  // namespace mcbp_unit_test
