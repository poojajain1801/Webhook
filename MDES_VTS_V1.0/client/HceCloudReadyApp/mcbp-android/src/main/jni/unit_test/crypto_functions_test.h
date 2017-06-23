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

#ifndef UNIT_TEST_CRYPTO_FACTORY_TEST_H_  // NOLINT
#define UNIT_TEST_CRYPTO_FACTORY_TEST_H_  // NOLINT

// Project Libraries
#include <log/log.h>
#include <unit_test/rapidxml.hpp>
#include <unit_test/rapidxml_utils.hpp>
#include <gtest/gtest.h>

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

class CryptoFuntionsTest : public ::testing::Test {
 protected:
  typedef multimap <string, map<string, string> >::const_iterator TestsIt;
  typedef map <string, string>::const_iterator parameterIterator;

  CryptoFuntionsTest();

  // Per-test-case set-up.
  // Called before the first test in this test case.
  // Can be omitted if not needed.
  static void SetUpTestCase();

  // Per-test-case tear-down.
  // Called after the last test in this test case.
  // Can be omitted if not needed.
  static void TearDownTestCase();

  // If the constructor and destructor are not enough for setting up
  // and cleaning up each test, you can define the following methods:

  virtual void SetUp() {
    // Code here will be called immediately after the constructor (right
    // before each test).
    const ::testing::TestInfo* const test_info =
    ::testing::UnitTest::GetInstance()->current_test_info();

    // Read data for the next coming test
    data_available_ = read_data(test_info->name());
  }

  virtual void TearDown() {
    // Code here will be called immediately after each test (right
    // before the destructor).

    data_available_ = false;
  }

  virtual ~CryptoFuntionsTest() {
    // You can do clean-up work that doesn't throw exceptions here.
  }

  // Read input data from the XML file
  bool read_data(const char *test_name);

  static bool read_input_file();

  static ByteArray string_to_byte_array(const std::string& str);

  // Useful data structure for test cases. Each test read its own variables
  // A test may use any of the variables below. Not all the tests may use all
  // the variables
  struct TestData {
   public:
    ByteArray input_data_1;
    ByteArray input_data_2;
    ByteArray input_data_3;
    ByteArray input_data_4;
    ByteArray input_data_5;
    ByteArray input_data_6;
    ByteArray expected_result_1;
    ByteArray expected_result_2;
    ByteArray expected_result_3;
  };

  // Data structure to keep the entire content of the XML input data in
  // memory
  // multimap < function_name, map < parameter, value > >
  // For each function name there is a set of tuples. Each tuple is
  // parameter, value. The multimap allows several functions with the same
  // name
  static multimap< string, map<string, string> >* tests_;

  // Data structure to store the list of tests for the current function
  // under evaluation
  std::list<TestData> current_tests_;

  // Set and re-set by Set-Up and Tear-Down functions
  bool data_available_;

 private:
  // Utility function to extract parameters. If the parameter is found, it is
  // assigned to destination
  static void readParameter(const string param_name,
                            const pair<TestsIt, TestsIt> functions,
                            ByteArray* destination);
};

}  // end of namespace mcbp_unit_test

#endif  // defined(UNIT_TEST_CRYPTO_FACTORY_TEST_H_)  // NOLINT
