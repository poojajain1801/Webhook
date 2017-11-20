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

#include <unit_test/test_data.h>
#include <unit_test/unit_test_utilities.h>
#include <wrappers/keys_data.h>

// C++ libraries
#include <mutex>
#include <algorithm>
#include <map>
#include <utility>
#include <exception>
#include <iostream>
#include <string>

namespace mcbp_unit_test {

using std::cerr;
using std::endl;
using std::string;
using std::map;
using std::pair;
using std::transform;
using rapidxml::file;
using rapidxml::xml_node;

// Global static pointer used to ensure a single instance of the class.
TestKeysSet* TestKeysSet::instance_ = nullptr;
std::map<KeysId, mcbp_core::KeysData> TestKeysSet::keys_;

std::once_flag flag_mcbp_core_test_data;

void CreateTestKeysSet() {
  if (TestKeysSet::instance_ != nullptr) return;
  TestKeysSet::instance_ = new TestKeysSet();
}

TestKeysSet* TestKeysSet::instance() {
  if (instance_ == nullptr)
    std::call_once(flag_mcbp_core_test_data, CreateTestKeysSet);
  return instance_;
}

const mcbp_core::KeysData& TestKeysSet::test_keys(const string& card_name,
                                                  const uint16_t atc) {
  KeysId keys_id;
  keys_id.card_name = card_name;
  keys_id.atc = atc;
  typedef std::map<KeysId, mcbp_core::KeysData>::const_iterator C_It;
  C_It p = keys_.find(keys_id);
  if (p != keys_.end()) return p->second;

  std::cerr << "Keys not found" << std::endl;
  throw std::exception();
}

const mcbp_core::KeysData& TestKeysSet::test_keys(const string& card_name,
                                                  const string& s_atc) {
  // convert the string (assumed to be HEX string to uint16_t)
  if (s_atc.size() != 4) {
    std::cerr << "Malformed input (ATC)" << std::endl;
    throw std::exception();
  }

  ByteArray b_atc;
  mcbp_unit_test::StringToHex(s_atc, &b_atc);

  uint16_t atc = (b_atc[0] << 8) + b_atc[1];

  return test_keys(card_name, atc);
}

bool TestKeysSet::read_parameters(const xml_node<> *credential_node,
                                  mcbp_core::KeysData* data) {
      const xml_node<> * param_node = nullptr;
      param_node = credential_node->first_node("atc");
      if (param_node == nullptr) return false;
      StringToHex(param_node->value(), &data->atc);

      param_node = credential_node->first_node("idn");
      if (param_node == nullptr) return false;
      StringToHex(param_node->value(), &data->idn);

      param_node = credential_node->first_node("sk_cl_umd");
      if (param_node == nullptr) return false;
      StringToHex(param_node->value(), &data->sk_cl_umd);

      param_node = credential_node->first_node("sk_cl_md");
      if (param_node == nullptr) return false;
      StringToHex(param_node->value(), &data->sk_cl_md);

      param_node = credential_node->first_node("sk_rp_umd");
      if (param_node == nullptr) return false;
      StringToHex(param_node->value(), &data->sk_rp_umd);

      param_node = credential_node->first_node("sk_rp_md");
      if (param_node == nullptr) return false;
      StringToHex(param_node->value(), &data->sk_rp_md);

      param_node = credential_node->first_node("pin");
      if (param_node == nullptr) return false;
      StringToHex(param_node->value(), &data->pin);

      return true;
}

TestKeysSet::TestKeysSet() {
  initialize();
}

void TestKeysSet::initialize() {
  // Read keys from file
  try {
    const char file_name[] = "./test_keys.xml";

    file<> xml_file(file_name);
    if (xml_file.data() == nullptr) return;

    rapidxml::xml_document<> input;
    input.parse<0>(xml_file.data());  // Default template is char

    const xml_node<> *root_node = input.first_node("KeysSets");
    if (root_node == nullptr) return;

    // For each Set of Keys
    for (const xml_node<> *keys_node = root_node->first_node("keys");
         keys_node != nullptr;
         keys_node = keys_node->next_sibling()) {
      if (keys_node->first_attribute("card_name") == nullptr) continue;
      string card_name = keys_node->first_attribute("card_name")->value();

      for (const auto *credential_node = keys_node->first_node("credentials");
           credential_node != nullptr;
           credential_node = credential_node->next_sibling()) {
        mcbp_core::KeysData data;

        if (!read_parameters(credential_node, &data)) continue;

        KeysId keys_id;
        keys_id.card_name = card_name;
        uint16_t atc = (data.atc[0] << 8) + data.atc[1];
        keys_id.atc = atc;

        keys_.insert(std::pair<KeysId, mcbp_core::KeysData>(keys_id, data));
      }
    }
  } catch (const std::exception& e) {
    cerr << "Generic exception capture: " << e.what() << endl;
    throw;
  }
}

/*******************************************************************************
 * Test Card Profiles
 ******************************************************************************/

// Global static pointer used to ensure a single instance of the class.
TestCardProfileSet* TestCardProfileSet::instance_ = nullptr;
std::map<string, mcbp_core::CardProfileData> TestCardProfileSet::cards_;

std::once_flag flag_mcbp_core_test_card_profile_data;

void CreateTestCardProfileSet() {
  if (TestCardProfileSet::instance_ != nullptr) return;
  TestCardProfileSet::instance_ = new TestCardProfileSet();
}

TestCardProfileSet* TestCardProfileSet::instance() {
  using std::call_once;
  if (instance_ == nullptr)
    call_once(flag_mcbp_core_test_card_profile_data, CreateTestCardProfileSet);
  return instance_;
}

const mcbp_core::CardProfileData& TestCardProfileSet::test_card(
    const string& card_name) {
  typedef std::map<string, mcbp_core::CardProfileData>::const_iterator C_It;
  C_It p = cards_.find(card_name);
  if (p != cards_.end()) return p->second;

  std::cout << "Demo card profile not found" << std::endl;
  throw std::exception();
}

TestCardProfileSet::TestCardProfileSet() {
  initialize();
}

bool TestCardProfileSet::add_param(const map<string, string>& parameters,
                                   const string& parameter, ByteArray* dest) {
  string value;
  if (add_param(parameters, parameter, &value)) {
    dest->resize(value.size() / 2);
    StringToHex(value, dest);
    return true;
  }
  dest->clear();
  return false;
}

bool TestCardProfileSet::add_param(const map<string, string>& parameters,
                                   const string& parameter, string* dest) {
  map<string, string>::const_iterator param_it = parameters.find(parameter);
  if ( param_it != parameters.end() ) {
    *dest = param_it->second;
    return true;
  }
  dest->clear();
  return false;
}

bool TestCardProfileSet::add_param(const map<string, string>& parameters,
                                   const string& parameter, bool* dest) {
  map<string, string>::const_iterator param_it = parameters.find(parameter);
  if ( param_it != parameters.end() ) {
    string value = param_it->second;
    transform(value.begin(), value.end(), value.begin(), ::tolower);
    value == "true" ? *dest = true: *dest = false;
    return true;
  }
  return false;
}

bool TestCardProfileSet::read_parameters(const rapidxml::xml_node<> *card_node,
                                         mcbp_core::CardProfileData* data) {
  map<string, string> values;
  // Read card profile values
  for (xml_node<> *param_node = card_node->first_node("string");
       param_node != nullptr;
       param_node = param_node->next_sibling()) {
    string param_name  = param_node->first_attribute("name")->value();
    string param_value = param_node->value();

    values.insert(pair<string, string>(param_name, param_value));
  }
  bool valid = true;

  // Read mandatory values first
  valid &= add_param(values, "cl_supported", &data->cl_supported);
  valid &= add_param(values, "rp_supported", &data->cl_supported);

  if (data->cl_supported == true) {
    valid =
      add_param(values, "additional_check_table",
                &data->additional_check_table)                                 &
      add_param(values, "crm_country_code", &data->crm_country_code)           &
      add_param(values, "cl_aid", &data->cl_aid)                               &
      add_param(values, "cl_ppse_fci", &data->cl_ppse_fci)                     &
      add_param(values, "cl_payment_fci", &data->cl_payment_fci)               &
      add_param(values, "cl_gpo_response", &data->cl_gpo_response)             &
      add_param(values, "cl_cdol1_related_data_length",
                        &data->cl_cdol1_related_data_length)                   &
      add_param(values, "cl_ciac_decline", &data->cl_ciac_decline)             &
      add_param(values, "cl_cvr_mask_and", &data->cl_cvr_mask_and)             &
      add_param(values, "cl_issuer_application_data",
                &data->cl_issuer_application_data)                             &
      add_param(values, "cl_icc_private_key_a",  &data->cl_icc_private_key_a)  &
      add_param(values, "cl_icc_private_key_p",  &data->cl_icc_private_key_p)  &
      add_param(values, "cl_icc_private_key_q",  &data->cl_icc_private_key_q)  &
      add_param(values, "cl_icc_private_key_dp", &data->cl_icc_private_key_dp) &
      add_param(values, "cl_icc_private_key_dq", &data->cl_icc_private_key_dq) &
      add_param(values, "cl_pin_iv_cvc3_track2",
                &data->cl_pin_iv_cvc3_track2);
      add_param(values, "cl_ciac_decline_on_ppms",
                &data->cl_ciac_decline_on_ppms);

    ByteArray rec;
    if (add_param(values, "record_1_1", &rec)) data->records[0x0101] = rec;
    if (add_param(values, "record_2_1", &rec)) data->records[0x0201] = rec;
    if (add_param(values, "record_2_2", &rec)) data->records[0x0202] = rec;
    if (add_param(values, "record_2_3", &rec)) data->records[0x0203] = rec;
    if (add_param(values, "record_3_1", &rec)) data->records[0x0301] = rec;
    if (add_param(values, "record_3_2", &rec)) data->records[0x0302] = rec;
    if (add_param(values, "record_3_3", &rec)) data->records[0x0303] = rec;
    if (add_param(values, "record_4_1", &rec)) data->records[0x0401] = rec;
    if (add_param(values, "record_4_2", &rec)) data->records[0x0402] = rec;
    if (add_param(values, "record_4_3", &rec)) data->records[0x0403] = rec;
    if (add_param(values, "record_4_4", &rec)) data->records[0x0404] = rec;

    // Alternate AID is optional
    add_param(values, "alt_aid", &data->alt_aid);
    add_param(values, "alt_payment_fci", &data->alt_payment_fci);
    add_param(values, "alt_gpo_response", &data->alt_gpo_response);
    add_param(values, "alt_ciac_decline", &data->alt_ciac_decline);
    add_param(values, "alt_cvr_mask_and", &data->alt_cvr_mask_and);
  }
  if (data->rp_supported) {
    valid =
      add_param(values, "rp_track2_equivalent_data",
                &data->rp_track2_equivalent_data)                  &
      add_param(values, "rp_pan", &data->rp_pan)                   &
      add_param(values, "rp_pan_sequence_number",
                &data->rp_pan_sequence_number)                     &
      add_param(values, "rp_application_expiry_date",
                &data->rp_application_expiry_date)                 &
      add_param(values, "rp_aip", &data->rp_aip)                   &
      add_param(values, "rp_ciac_decline", &data->rp_ciac_decline) &
      add_param(values, "rp_cvr_mask_and", &data->rp_cvr_mask_and) &
      add_param(values, "rp_issuer_application_data",
                &data->rp_issuer_application_data);
  }
  return valid;
}

void TestCardProfileSet::initialize() {
  // Read keys from file
  try {
    const char file_name[] = "./test_cards.xml";

    file<> xml_file(file_name);
    if (xml_file.data() == nullptr) return;

    rapidxml::xml_document<> input;
    input.parse<0>(xml_file.data());  // Default template is char

    const xml_node<> *root_node = input.first_node("cards");
    if (root_node == nullptr) return;

    // For each Set of Keys
    for (const xml_node<> *card_node = root_node->first_node("card");
         card_node != nullptr;
         card_node = card_node->next_sibling()) {
      if (card_node->first_attribute("name") == nullptr) continue;
      string card_name = card_node->first_attribute("name")->value();

      mcbp_core::CardProfileData data;

      if (!read_parameters(card_node, &data)) {
        cerr << "Invalid Card Profile input for card: " << card_name << endl;
        continue;
      }
      cards_[card_name] = data;
    }
  } catch (const std::exception& e) {
    cerr << "Generic exception capture: " << e.what() << endl;
    throw;
  }
}

}  // namespace mcbp_unit_test
