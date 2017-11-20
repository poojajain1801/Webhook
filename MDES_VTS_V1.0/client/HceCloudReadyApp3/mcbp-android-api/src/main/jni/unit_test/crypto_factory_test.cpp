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
#include <unit_test/crypto_factory_test.h>
#include <unit_test/unit_test_utilities.h>
#include <utils/rsa_certificate.h>

// Libraries includes
#include <exception>
#include <utility>
#include <map>
#include <list>
#include <string>

namespace mcbp_unit_test {

using std::size_t;

TEST_F(CryptoFactoryTest, mac) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  const auto *const crypto = mcbp_core::CryptoFactory::instance();
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i)
    EXPECT_EQ(i->expected_result, crypto->mac(i->input_data, i->key));
}

TEST_F(CryptoFactoryTest, random) {
  // Note: from time to time this test may fail as two equal values may be
  // generated. This has happened for values of i = 1 (1 Byte)
  const auto *const crypto = mcbp_core::CryptoFactory::instance();
  for (int i = 1; i <= 16384; i *= 2)
    EXPECT_NE(crypto->generate_random(i) , crypto->generate_random(i));
}

// Test DES CBC Encryption set to True
TEST_F(CryptoFactoryTest, des_cbc) {
  try {
    ASSERT_EQ(data_available_, true) << "Input data missing";
    const auto *const crypto = mcbp_core::CryptoFactory::instance();

    for (list<TestData>::const_iterator i = current_tests_.begin();
         i != current_tests_.end(); ++i)
      EXPECT_EQ(i->expected_result,
                crypto->des_cbc(i->input_data, i->key, i->encryption));

    const unsigned int key_size = 8;
    ByteArray key(crypto->generate_random(key_size));

    for (int size = 128; size <= 1024; size *= 2) {
      ByteArray input(crypto->generate_random(size));
      ByteArray enc(crypto->des_cbc(input, key, true));
      ByteArray dec(crypto->des_cbc(enc, key, false));
      EXPECT_EQ(input, dec) << "Decrypt/Encryp do not match, "
      << "key size: " << key.size() << ", input data size: "
      << input.size();
    }
  }
  catch (std::exception& e) {
    cerr << "Exception: " << e.what();
    throw e;
  }
}

// Test DES function with enc set to false
TEST_F(CryptoFactoryTest, des) {
  try {
    const auto* const crypto = mcbp_core::CryptoFactory::instance();

    ASSERT_EQ(data_available_, true) << "Input data missing";
    for (list<TestData>::const_iterator i = current_tests_.begin();
         i != current_tests_.end(); ++i)
      EXPECT_EQ(i->expected_result,
                crypto->des(i->input_data, i->key, i->encryption));

    const unsigned int key_size = 8;
    ByteArray key(crypto->generate_random(key_size));

    for (int size = 128; size <= 1024; size *= 2) {
      ByteArray input(crypto->generate_random(size));
      ByteArray enc(crypto->des(input, key, true));
      ByteArray dec(crypto->des(enc, key, false));
      EXPECT_EQ(input, dec) << "Decrypt/Encryp do not match, "
        << "key size: " << key.size() << ", input data size: "
        << input.size();
    }
  }
  catch (std::exception& e) {
    cerr << "Exception: " << e.what() << endl;
    throw e;
  }
}

// Test DES3 function with enc set to false
TEST_F(CryptoFactoryTest, des_3) {
  try {
    const auto* const crypto = mcbp_core::CryptoFactory::instance();
    ASSERT_EQ(data_available_, true) << "Input data missing";
    for (list<TestData>::const_iterator i = current_tests_.begin();
         i != current_tests_.end(); ++i)
      EXPECT_EQ(i->expected_result,
                crypto->des_3(i->input_data, i->key, i->encryption));

    const unsigned int key_size = 16;
    ByteArray key(crypto->generate_random(key_size));

    for (int size = 128; size <= 1024; size *= 2) {
      ByteArray input(crypto->generate_random(size));
      ByteArray enc(crypto->des_3(input, key, true));
      ByteArray dec(crypto->des_3(enc, key, false));
      EXPECT_EQ(input, dec) << "Decrypt/Encryp do not match, "
        << "key size: " << key.size() << ", input data size: "
        << input.size();
    }
  }
  catch (std::exception e) {
    cerr << "Exception: " << e.what() << endl;
    throw e;
  }
}

// Test the AES CBC MAC function
TEST_F(CryptoFactoryTest, aes_cbc_mac) {
  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i)
    EXPECT_EQ(i->expected_result, crypto->aes_cbc_mac(i->input_data, i->key));
}

// Test the SHA1 function
TEST_F(CryptoFactoryTest, sha_1) {
  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i)
    EXPECT_EQ(i->expected_result, crypto->sha_1(i->input_data));
}

// Test the AES function
TEST_F(CryptoFactoryTest, aes) {
  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i)
    EXPECT_EQ(i->expected_result,
              crypto->aes(i->input_data, i->key, i->encryption));

  // Test random data vector of 128, 256, 512. and 1024 bytes using random
  // generated keys (128, 192, and 256 bits)
  try {
    for (unsigned int key_size = 16; key_size <= 32; key_size += 8) {
      ByteArray key(crypto->generate_random(key_size));

      for (int size = 128; size <= 1024; size *= 2) {
        ByteArray input(crypto->generate_random(size));
        ByteArray enc(crypto->aes(input, key, true));
        ByteArray dec(crypto->aes(enc, key, false));
        EXPECT_EQ(input, dec) << "Decrypt/Encryp do not match, "
            << "key size: " << key.size() << ", input data size: "
            << input.size();
      }
    }
  }
  catch (std::exception e) {
    cerr << "Exception: " << e.what() << endl;
    throw e;
  }
}

// Test the AES with no padding function
TEST_F(CryptoFactoryTest, aes_ecb_nopadding) {
  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    const ByteArray actual =
        crypto->aes_ecb_nopadding(i->input_data, i->key, i->encryption);
      EXPECT_EQ(i->expected_result, actual);
  }

  // Test random data vector of 128, 256, 512. and 1024 bytes using random
  // generated keys (128, 192, and 256 bits)
  try {
    for (unsigned int key_size = 16; key_size <= 32; key_size += 8) {
      ByteArray key(crypto->generate_random(key_size));

      for (int size = 128; size <= 1024; size *= 2) {
        ByteArray input(crypto->generate_random(size));
        ByteArray enc(crypto->aes_ecb_nopadding(input, key, true));
        ByteArray dec(crypto->aes_ecb_nopadding(enc, key, false));
        EXPECT_EQ(input, dec) << "Decrypt/Encryp do not match, "
            << "key size: " << key.size() << ", input data size: "
            << input.size();
      }
    }
  }
  catch (std::exception e) {
    cerr << "Exception: " << e.what() << endl;
    throw e;
  }
}

// Test the sha256 function
TEST_F(CryptoFactoryTest, sha_256) {
  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i)
    EXPECT_EQ(i->expected_result, crypto->sha_256(i->input_data));
}

// Test the mac_sha256 function
TEST_F(CryptoFactoryTest, mac_sha_256) {
  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i)
    EXPECT_EQ(i->expected_result, crypto->mac_sha_256(i->input_data, i->key));
}

// Test the rsa function
TEST_F(CryptoFactoryTest, rsa) {
  // First initialize the private key
  string primeP         = "CDCF9FDA4FC8BDBE4F641A39CD858BF0C64C80CC2055C041FF32B53E6BD8DC51B3AFB13BF0D5E5DAB7537C63A84D3C19";  // NOLINT
  string primeQ         = "C89EB6CFA22566083268CE3F975850E0F3695FF199791A27394EB8E9137619C6DA65056F4D9BA4D733ACED9108F48443";  // NOLINT
  string exponentP      = "8935153C35307E7EDF98117BDE5907F5D98855DD6AE3D58154CC78D447E5E83677CA7627F5E3EE91CF8CFD97C588D2BB";  // NOLINT
  string exponentQ      = "85BF248A6C18EEB0219B342A64E58B40A2463FF66650BC1A26347B460CF966849198AE4A33BD188F77C89E60B0A302D7";  // NOLINT
  string crtCoefficient = "BDFF1436301672F1B29C3EC7A4C6C4A5F54058A5925393BEAFB1EAA83050BBF27EC745ACBF2BA0B10FBE89E99B057725";  // NOLINT

  string input_str      = "736563726574";

  const auto* const crypto = mcbp_core::CryptoFactory::instance();

  try {
    mcbp_core::RsaCertificate cert;
    cert.init_private_key(primeP, primeQ, exponentP, exponentQ, crtCoefficient);
    ASSERT_EQ(true, cert.validate_private_key(3));
    ASSERT_EQ(data_available_, true) << "Input data missing";
    for (list<TestData>::const_iterator i = current_tests_.begin();
         i != current_tests_.end(); ++i)
      EXPECT_EQ(i->expected_result, crypto->rsa(i->input_data, cert));
  }
  catch (const std::exception& e) {
    cerr << "Exception: " << e.what() << endl;
    throw;
  }
}

TEST_F(CryptoFactoryTest, generate_pan_substitute_value) {
  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  const ByteArray input =
      string_to_byte_array("7061796D656E74417070496E7374616E63654964313233");
  const std::string expected_value = "1586494682035777";

  const ByteArray output = crypto->generate_pan_substitute_value(input);

  EXPECT_EQ(expected_value, std::string(output.begin(), output.end()));
}

TEST_F(CryptoFactoryTest, rsa_oaep_sha256_mgf1) {
  ASSERT_EQ(data_available_, true) << "Input data missing";

  const auto* const crypto = mcbp_core::CryptoFactory::instance();
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    const ByteArray encrypted =
        crypto->rsa_oaep_sha256_mgf1(i->input_data, i->key, i->encryption);
    EXPECT_EQ(i->expected_result, encrypted);
  }
}

/*******************************************************************************
 * CryptoFactory Class definitions
 ******************************************************************************/

multimap< string, map<string, string> >* CryptoFactoryTest::tests_ = nullptr;

CryptoFactoryTest::CryptoFactoryTest() {
  // You can do set-up work for each test here.
  data_available_ = false;
}

void CryptoFactoryTest::SetUpTestCase() {
  tests_ = new multimap< string, map<string, string> >;
  if (!read_input_file()) {
    std::cerr << "Unable to read the input file" << std::endl;
    throw std::exception();
  }
}

void CryptoFactoryTest::TearDownTestCase() {
  delete tests_;
  tests_ = NULL;
}

bool CryptoFactoryTest::read_input_file() {
  // Pointer to the XML Input File
  rapidxml::xml_document<> input_document;  // character type defaults to char
  // Pointer to the root node
  rapidxml::xml_node<> *root_node_ = nullptr;

  using rapidxml::file;
  using rapidxml::xml_node;

  try {
    char input_file[] = "./crypto_factory_test_data.xml";

    file<> xml_file(input_file);

    if (xml_file.data() == NULL) {
      cerr << "Unable to open the XML file: " << input_file << endl;
      return false;
    }

    input_document.parse<0>(xml_file.data());  // Default template is char

    char test_case_name[] = "CryptoFactory";

    root_node_ = input_document.first_node("TestCase");
    if (root_node_ == nullptr ||
        strcmp(test_case_name,
                root_node_->first_attribute("name")->value()) != 0 ) {
      cerr << "Invalid TestCase name: " << test_case_name << endl;
      return false;
    }

    map <string, string> parameters;

    const string input      = "input_data";
    const string key        = "key";
    const string expected   = "expected_result";
    const string encryption = "encryption";

  // I have got the right file. Let's process it
  xml_node<> *node = nullptr;
    for (node = root_node_->first_node("function");
         node != nullptr;
         node = node->next_sibling()) {
      // Clean the test data structure for the next function
      parameters.clear();
      string function_name = node->first_attribute("name")->value();
      // string id   = node->first_attribute("id")->value();

      const string input_value = node->first_node(input.c_str())->value();
      const string key_value = node->first_node(key.c_str())->value();
      const string expected_value = node->first_node(expected.c_str())->value();
      const string enc_value = node->first_node(encryption.c_str())->value();

      parameters.insert(pair<string, string>(input, input_value));
      parameters.insert(pair<string, string>(key, key_value));
      parameters.insert(pair<string, string>(expected, expected_value));
      parameters.insert(pair<string, string>(encryption, enc_value));

      // Add the set of parameters to the list
      tests_->insert(pair<string, map <string, string> >
          (function_name, parameters));
    }
    return true;
  }
  catch (const std::exception& e) {
    cerr << "Exception: " << e.what() << endl;
    throw;
  }
}

bool CryptoFactoryTest::read_data(const char *test_name) {
  try {
    // Read data for this test case
    typedef multimap <string, map<string, string> >::const_iterator TestsIt;
    typedef map <string, string>::const_iterator parameterIterator;
    pair<TestsIt, TestsIt> functions;

    parameterIterator param_i;

    functions = tests_->equal_range(test_name);

    if (functions.first != functions.second) {
      // There is at least one test for our test_name function. Extract data

      ByteArray input_data;
      ByteArray key;
      ByteArray expected_result;
      bool   encryption = true;

      while (functions.first != functions.second) {
        param_i = functions.first->second.find("input_data");
        if (param_i != functions.first->second.end())
          input_data = string_to_byte_array(param_i->second);

        param_i = functions.first->second.find("key");
        if (param_i != functions.first->second.end())
          key = string_to_byte_array(param_i->second);

        param_i = functions.first->second.find("expected_result");
        if (param_i != functions.first->second.end())
          expected_result = string_to_byte_array(param_i->second);

        param_i = functions.first->second.find("encryption");
        if (param_i != functions.first->second.end())
          if (param_i->second == "0") encryption = false;

        TestData data;
        data.input_data = input_data;
        data.key = key;
        data.expected_result = expected_result;
        data.encryption = encryption;
        current_tests_.push_back(data);

        // Move to the next match
        functions.first++;
      }
    } else {
      // No data available
      return false;
    }
  }
  catch(const std::exception& e) {
    cerr << "Exception: " << e.what() << endl;
    throw;
  }
  return true;
}

ByteArray CryptoFactoryTest::string_to_byte_array(const string& str) {
  const size_t string_size = str.size();
  if (string_size % 2 == 1) {
    // Uneven size - We could assume there is a missing zero at the beginning
    // However, in our implementation we raise and exception as the strings
    // must be Byte aligned
    cerr << "String length: " << str.size() << endl;
    throw std::exception();
  }
  const size_t hex_length = str.size() / 2;

  ByteArray digits(hex_length);

  unsigned int tmp;
  for (unsigned int i = 0; i < hex_length; i++) {
    // Derive the value from the next two digits

    std::sscanf(&str[0] + 2*i, "%02x", &tmp);
    digits[i] = (Byte)tmp;
  }
  return digits;
}

}  // End of namespace mcbp_unit_test
