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

#ifndef UNIT_TEST_MCBP_CARD_TEST_H_  // NOLINT
#define UNIT_TEST_MCBP_CARD_TEST_H_  // NOLINT

// MCBP Core Libraries
#include <utils/byte_array.h>
#include <utils/utilities.h>
#include <wrappers/unit_test_wrapper.h>
#include <wrappers/keys_data.h>
#include <wrappers/card_profile_data.h>

// Project Libraries
#include <gtest/gtest.h>
#include <unit_test/rapidxml.hpp>
#include <unit_test/rapidxml_utils.hpp>

// C++ Libraries
#include <list>
#include <map>
#include <string>
#include <utility>

namespace mcbp_unit_test {

using std::cerr;
using std::endl;
using std::string;
using std::list;
using std::map;
using std::multimap;
using std::pair;

enum TestDataType {
  CONTACTLESS,
  REMOTE_PAYMENT
};

typedef ByteArray C_APDU;
typedef ByteArray R_APDU;
typedef string TestName;
typedef string CardName;
typedef string ParamName;
typedef string ParamValue;

// XML parameters are stored as pairs <name, value>
typedef map < ParamName, ParamValue > Parameter;

/**
 * Data structure to store a command in terms of command/response
 */
typedef pair< C_APDU, R_APDU > Command;

/**
 * A Transaction is defined as a list of APDU commands
 * (a list of pairs <C-APDU, R-APDU>)
 */
typedef list< Command > Commands;

/*
 * Utility structure to keep the relevant parameters for a Test
 */
struct TestData {
 public:
  mcbp_core::KeysData keys;
  ByteArray pin;
  Commands  cmds;
};

// Each test is identified by a unique name
typedef map< TestName, TestData > Tests;

/**
 * Keep each card paired with its own set of tests
 */
typedef pair< mcbp_core::CardProfileData, Tests > TestCard;

/**
 * For each card to be tested we keep track of its name and list of CardTests
 */
typedef map< CardName, TestCard > TestCaseInput;



class McbpCardTest : public ::testing::TestWithParam< std::pair<std::string,
                                                                std::string> > {
 public:
  /*
   * Per-test-case set-up.
   * Read the XML file and allocate shared resources 
   */
  static void SetUpTestCase();

  /* 
   * Per-test-case tear-down.
   * Delete shared objects
   */
  static void TearDownTestCase();

  /**
   * Get the full list of tests as they have been read from the XML file
   */
  static const std::list< std::pair<std::string, std::string>  > tests_list();

  /**
   * Utility function to return the list of tests to be executed
   */
  static const list< pair<string, string> >
      read_tests_list(string filter_string = "");

 protected:
  // You can remove any or all of the following functions if its body
  // is empty.

  McbpCardTest() {
    // You can do set-up work for each test here.
  }

  virtual ~McbpCardTest() {
    // You can do clean-up work that doesn't throw exceptions here.
  }

  // If the constructor and destructor are not enough for setting up
  // and cleaning up each test, you can define the following methods:
  virtual void SetUp();

  // Code here will be called immediately after each test (right
  // before the destructor).
  virtual void TearDown();

  //
  // Objects declared here can be used by all tests in the test case for Foo.
  //

  // Read input data from the XML file
  static bool read_data(const char *test_name);

  // Pointer to the XML Input File
  static rapidxml::xml_document<> input_;    // character type defaults to char

  // Pointer to the root node
  static rapidxml::xml_node<> *root_node_;

  // Internal data structure used to store Card Profile and Keys as read from
  // the XML file
  static map <string, string> card_and_keys_;

  // Load the a specific card (true for contactless, false for remote payment)
  static bool load_card(string card_name, string test_name,
                        const TestDataType type);
  /**
   *    XML File Structure
   *
   *    TestCard 1
   *     |
   *     |------> TestCardProfile
   *     |
   *     |------> Tests
   *               |
   *               |------>TestName
   *               |
   *               |------>TestData
   *                        |
   *                        |------>TestKeys keys
   *                        |
   *                        |------>ByteArray pin
   *                        |
   *                        |------>TestCommands cmds
   *                                 |
   *                                 |------> C-APDU 1 (ByteArray)
   *                                 |------> R-APDU 1 (ByteArray)
   *
   *                                 |         |     |          |
   *
   *                                 |------> C-APDU N (ByteArray)
   *                                 |------> R-APDU N (ByteArray)
   *
   *    TestCard N
   *     |
   *     |-----------> TestCardProfile
   *     |
   *     |-----------> Tests
   *                    |
   *                    |------>TestName
   *                    |
   *                    |------>TestData
   */


  // Reads a given parameter and saves it in the ByteArray
  // True if the ByteArray is modified (e.g. parameter found). False otherwise
  static bool read_card_value(const string& parameter, ByteArray* dest);

  // Reads a given parameter as string
  static bool read_card_value(const string& parameter, string* dest);

  // Read a given parameter as boolean
  static bool read_card_value(const string& param, bool* dest);

  // Read data from the internal data structure and create a card profile object
  static bool populate_card_profile();

  // Read data from the internal data structure and create a transaction keys
  // object
  static const mcbp_core::KeysData& populate_keys(const string& card_name,
                                                  const string& atc);

  // Read the XML input file
  static bool read_xml(const char* const file_name);

  // Data structure to keep the entire XML file in memory
  static CardName current_test_card_;

  // Current test
  static Commands current_test_commands_;

  // Data structure that maps the content of the XML input file. It is read only
  // once when the Test Case is initialitzed. Every test reuses that data.
  static TestCaseInput* shared_input_;

  // It contain the full set of keys as it is read from the input keys file
  static std::map<uint16_t, mcbp_core::KeysData> shared_keys_;

  // Temporary data structure used to read pairs (name, value) from XML file
  static Parameter tmp_values_;

  // Temporary data structure to store the PIN associated with certain keys
  static ByteArray tmp_pin_;

  static UnitTestWrapper *card_wrapper_;

  // Utility function to run a sequence of APDUs test
  static void apdu_sequence_test();

  static void add_test_dependencies(rapidxml::xml_node<> *node,
                                    const Tests& card_tests,
                                    Commands* test_commands);
};

}  // namespace mcbp_unit_test

#endif  // defined(UNIT_TEST_MCBP_CARD_TEST_H_)  // NOLINT

