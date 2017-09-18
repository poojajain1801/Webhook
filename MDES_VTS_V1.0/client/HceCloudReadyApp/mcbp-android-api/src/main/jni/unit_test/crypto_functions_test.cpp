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
#include <unit_test/crypto_functions_test.h>
#include <unit_test/unit_test_utilities.h>
#include <utils/rsa_certificate.h>
#include <cryptoservice/crypto_functions.h>
#include <utils/crypto_factory.h>

// Libraries includes
#include <exception>
#include <utility>
#include <map>
#include <list>
#include <string>

namespace mcbp_unit_test {

using std::size_t;

TEST_F(CryptoFuntionsTest, decryptMobileKeys) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    const mcbp_crypto_service::MobileKeys mobile_keys =
        mcbp_crypto_service::decrypt_mobile_keys(i->input_data_1,
                                                 i->input_data_2,
                                                 i->input_data_3,
                                                 i->input_data_4);

    EXPECT_EQ(i->expected_result_1, mobile_keys.get_transport_key());
    EXPECT_EQ(i->expected_result_2, mobile_keys.get_mac_key());
    EXPECT_EQ(i->expected_result_3, mobile_keys.get_data_encryption_key());
  }
}

TEST_F(CryptoFuntionsTest, sha256) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  const auto *const crypto = mcbp_core::CryptoFactory::instance();
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    EXPECT_EQ(i->expected_result_1,
              crypto->sha_256(i->input_data_1));
  }
}

TEST_F(CryptoFuntionsTest, ldeEncryption) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray encrypted =
        mcbp_crypto_service::lde_encryption(i->input_data_1, i->input_data_2);
    EXPECT_EQ(i->expected_result_1, encrypted);
  }
}

TEST_F(CryptoFuntionsTest, ldeDecryption) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray decrypted =
        mcbp_crypto_service::lde_decryption(i->input_data_1, i->input_data_2);
    EXPECT_EQ(i->expected_result_1, decrypted);
  }
}

TEST_F(CryptoFuntionsTest, decryptNotificationData) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray decrypted =
        mcbp_crypto_service::decrypt_notification_data(i->input_data_1,
                                                       i->input_data_2,
                                                       i->input_data_3);
    EXPECT_EQ(i->expected_result_1, decrypted);
  }
}

TEST_F(CryptoFuntionsTest, encrypt_retry_request_data) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray encrypted =
        mcbp_crypto_service::encrypt_retry_request_data(i->input_data_2,
                                                        i->input_data_1);
    EXPECT_EQ(i->expected_result_1, encrypted);
  }
}

TEST_F(CryptoFuntionsTest, build_service_request) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray encrypted =
        mcbp_crypto_service::build_service_request(i->input_data_1,
                                                   i->input_data_2,
                                                   i->input_data_3,
                                                   i->input_data_4,
                                                   i->input_data_5[0]);
    EXPECT_EQ(i->expected_result_1, encrypted);
  }
}

TEST_F(CryptoFuntionsTest, decrypt_service_response) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray decrypted =
        mcbp_crypto_service::decrypt_service_response(i->input_data_1,
                                                      i->input_data_2,
                                                      i->input_data_3,
                                                      i->input_data_4);
    EXPECT_EQ(i->expected_result_1, decrypted);
  }
}

TEST_F(CryptoFuntionsTest, decrypt_icc_component) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray decrypted =
        mcbp_crypto_service::decrypt_icc_component(i->input_data_1,
                                                   i->input_data_2);
    EXPECT_EQ(i->expected_result_1, decrypted);
  }
}

TEST_F(CryptoFuntionsTest, decrypt_icc_kek) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray decrypted =
        mcbp_crypto_service::decrypt_icc_kek(i->input_data_1,
                                                   i->input_data_2);
    EXPECT_EQ(i->expected_result_1, decrypted);
  }
}

TEST_F(CryptoFuntionsTest, calculate_authentication_code) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray authentication_code =
        mcbp_crypto_service::calculate_authentication_code(
            i->input_data_1,
            i->input_data_2,
            i->input_data_3);
    EXPECT_EQ(i->expected_result_1, authentication_code);
  }
}

TEST_F(CryptoFuntionsTest, encrypt_pin_block) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    const ByteArray pin = i->input_data_1;
    const ByteArray app_instance_id = i->input_data_2;
    const ByteArray key = i->input_data_3;

    const ByteArray encrypted_pin =
        mcbp_crypto_service::encrypt_pin_block(pin, app_instance_id, key);

    const ByteArray decrypted_pin =
        mcbp_crypto_service::decrypt_pin_block(encrypted_pin,
                                               app_instance_id, key);

    EXPECT_EQ(pin, decrypted_pin);
  }
}

TEST_F(CryptoFuntionsTest, decrypt_data_encrypted_field) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray decrypted =
        mcbp_crypto_service::decrypt_data_encrypted_field(i->input_data_1,
                                                          i->input_data_2);
    EXPECT_EQ(i->expected_result_1, decrypted);
  }
}

ByteArray queue_to_byte_array(const CryptoPP::ByteQueue queue) {
  ByteArray buffer(queue.MaxRetrievable());
  for (int i = 0; i < buffer.size(); i++) {
    buffer[i] = queue[i];
  }
  return buffer;
}

TEST_F(CryptoFuntionsTest, encrypt_random_generated_key) {
  // For this test we do not get data from a file.
  // We generate a pair of public/private RSA key and we encrypt a few random
  // generated keys (RGKs) and we check that the encryption matches the
  // decryption.

  // The RSA OAEP SHA256 generates encrypted text that differs at each run,
  // thus we cannot have reproducible encryption

  // The decryption method has been verified with other libraries to ensure
  // this test case makes sense (i.e. we are not encrypting/decrypting with
  // potentially a bug in both, thus nullifying the test validity).

  // Let's first create the key pair
  CryptoPP::RandomPool random_;
  CryptoPP::RSA::PrivateKey rsa_private;
  rsa_private.GenerateRandomWithKeySize(random_, 2048);

  CryptoPP::RSA::PublicKey rsa_public(rsa_private);

  CryptoPP::ByteQueue queue_private;
  rsa_private.Save(queue_private);
  CryptoPP::ByteQueue queue_public;
  rsa_public.Save(queue_public);

  // Let's assign the two keys to a ByteArray
  const ByteArray public_key = queue_to_byte_array(queue_public);
  const ByteArray private_key = queue_to_byte_array(queue_private);

  const size_t no_tests = 4;
  const size_t key_size_1 = 16;
  const size_t key_size_2 = 32;

  for (int i = 0; i < no_tests; i++) {
    ByteArray generated_key1(key_size_1);
    ByteArray generated_key2(key_size_2);

    random_.GenerateBlock(&generated_key1[0], key_size_1);
    random_.GenerateBlock(&generated_key2[0], key_size_2);

    const ByteArray ciphered_text_1 =
        mcbp_crypto_service::encrypt_random_generated_key(generated_key1,
                                                          public_key);

    const ByteArray ciphered_text_2 =
        mcbp_crypto_service::encrypt_random_generated_key(generated_key2,
                                                          public_key);

    const auto& crypto = mcbp_core::CryptoFactory::instance();

    const ByteArray recovered_text_1 =
        crypto->rsa_oaep_sha256_mgf1(ciphered_text_1, private_key, false);

    const ByteArray recovered_text_2 =
        crypto->rsa_oaep_sha256_mgf1(ciphered_text_2, private_key, false);

    EXPECT_EQ(generated_key1, recovered_text_1);
    EXPECT_EQ(generated_key2, recovered_text_2);
  }
}

TEST_F(CryptoFuntionsTest, generate_plain_text_pan_field) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray pan_plain_text_field =
        mcbp_crypto_service::generate_plain_text_pan_field(i->input_data_1);
    EXPECT_EQ(i->expected_result_1, pan_plain_text_field);
  }
}

TEST_F(CryptoFuntionsTest, generate_plain_text_pin_field) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    ByteArray pin_plain_text_field =
        mcbp_crypto_service::generate_plain_text_pin_field(i->input_data_1);
    EXPECT_EQ(i->expected_result_1,
              ByteArray(pin_plain_text_field.begin(),
                        pin_plain_text_field.begin() + 8));
  }
}

TEST_F(CryptoFuntionsTest, pin_from_plain_text_pin_block) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    const ByteArray pin =
        mcbp_crypto_service::pin_from_plain_text_pin_block(i->input_data_1);
    EXPECT_EQ(i->expected_result_1, pin);
  }
}

TEST_F(CryptoFuntionsTest, decrypt_pin_block) {
  ASSERT_EQ(data_available_, true) << "Input data missing";
  for (list<TestData>::const_iterator i = current_tests_.begin();
       i != current_tests_.end(); ++i) {
    const ByteArray pin =
        mcbp_crypto_service::decrypt_pin_block(i->input_data_1,
                                               i->input_data_2,
                                               i->input_data_3);
    EXPECT_EQ(i->expected_result_1, pin);
  }
}

/*******************************************************************************
 * CryptoFactory Class definitions
 ******************************************************************************/

multimap< string, map<string, string> >* CryptoFuntionsTest::tests_ = nullptr;

CryptoFuntionsTest::CryptoFuntionsTest() {
  // You can do set-up work for each test here.
  data_available_ = false;
}

void CryptoFuntionsTest::SetUpTestCase() {
  tests_ = new multimap< string, map<string, string> >;
  if (!read_input_file()) {
    std::cerr << "Unable to read the input file" << std::endl;
    throw std::exception();
  }
}

void CryptoFuntionsTest::TearDownTestCase() {
  delete tests_;
  tests_ = NULL;
}

bool CryptoFuntionsTest::read_input_file() {
  // Pointer to the XML Input File
  rapidxml::xml_document<> input_document;  // character type defaults to char
  // Pointer to the root node
  rapidxml::xml_node<> *root_node_ = nullptr;

  using rapidxml::file;
  using rapidxml::xml_node;

  try {
    char input_file[] = "./crypto_functions_test_data.xml";

    file<> xml_file(input_file);

    if (xml_file.data() == NULL) {
      cerr << "Unable to open the XML file: " << input_file << endl;
      return false;
    }

    input_document.parse<0>(xml_file.data());  // Default template is char

    char test_case_name[] = "CryptoFunctions";

    root_node_ = input_document.first_node("TestCase");
    if (root_node_ == nullptr ||
        strcmp(test_case_name,
                root_node_->first_attribute("name")->value()) != 0 ) {
      cerr << "Invalid TestCase name: " << test_case_name << endl;
      return false;
    }

    map <string, string> parameters;

    const string input_1  = "input_data_1";
    const string input_2  = "input_data_2";
    const string input_3  = "input_data_3";
    const string input_4  = "input_data_4";
    const string input_5  = "input_data_5";
    const string input_6  = "input_data_6";
    const string expected_1 = "expected_result_1";
    const string expected_2 = "expected_result_2";
    const string expected_3 = "expected_result_3";

    // I have got the right file. Let's process it
    xml_node<> *node = nullptr;
    for (node = root_node_->first_node("function");
         node != nullptr;
         node = node->next_sibling()) {
      // Clean the test data structure for the next function
      parameters.clear();

      string function_name = node->first_attribute("name")->value();
      // string id   = node->first_attribute("id")->value();

      const string input_value_1 = node->first_node(input_1.c_str())->value();
      const string input_value_2 = node->first_node(input_2.c_str())->value();
      const string input_value_3 = node->first_node(input_3.c_str())->value();
      const string input_value_4 = node->first_node(input_4.c_str())->value();
      const string input_value_5 = node->first_node(input_5.c_str())->value();
      const string input_value_6 = node->first_node(input_6.c_str())->value();
      const string expected_value_1 =
          node->first_node(expected_1.c_str())->value();
      const string expected_value_2 =
          node->first_node(expected_2.c_str())->value();
      const string expected_value_3 =
          node->first_node(expected_3.c_str())->value();

      parameters.insert(pair<string, string>(input_1, input_value_1));
      parameters.insert(pair<string, string>(input_2, input_value_2));
      parameters.insert(pair<string, string>(input_3, input_value_3));
      parameters.insert(pair<string, string>(input_4, input_value_4));
      parameters.insert(pair<string, string>(input_5, input_value_5));
      parameters.insert(pair<string, string>(input_6, input_value_6));
      parameters.insert(pair<string, string>(expected_1, expected_value_1));
      parameters.insert(pair<string, string>(expected_2, expected_value_2));
      parameters.insert(pair<string, string>(expected_3, expected_value_3));

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

bool CryptoFuntionsTest::read_data(const char *test_name) {
  try {
    // Read data for this test case
    pair<TestsIt, TestsIt> functions;
    functions = tests_->equal_range(test_name);

    if (functions.first != functions.second) {
      // There is at least one test for our test_name function. Extract data

      while (functions.first != functions.second) {
        TestData data;

        readParameter("input_data_1", functions, &data.input_data_1);
        readParameter("input_data_2", functions, &data.input_data_2);
        readParameter("input_data_3", functions, &data.input_data_3);
        readParameter("input_data_4", functions, &data.input_data_4);
        readParameter("input_data_5", functions, &data.input_data_5);
        readParameter("input_data_6", functions, &data.input_data_6);
        readParameter("expected_result_1", functions, &data.expected_result_1);
        readParameter("expected_result_2", functions, &data.expected_result_2);
        readParameter("expected_result_3", functions, &data.expected_result_3);

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

ByteArray CryptoFuntionsTest::string_to_byte_array(const string& str) {
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

void CryptoFuntionsTest::readParameter(const string param_name,
                                       const pair<TestsIt, TestsIt> functions,
                                       ByteArray* destination) {
  parameterIterator param_i;
  param_i = functions.first->second.find(param_name);
  if (param_i != functions.first->second.end())
    *destination = string_to_byte_array(param_i->second);
}

}  // End of namespace mcbp_unit_test
