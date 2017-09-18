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

#include <cryptoservice/crypto_functions.h>
#include <utils/crypto_factory.h>
#include <utils/utilities.h>

#include <log/log.h>

namespace mcbp_crypto_service {

typedef mcbp_core::CryptoFactory CryptoLibrary;

/**
 * Derive a mobile session key (e.g. transport or mac)
 */
ByteArray derive_mobile_session_key(const ByteArray& key,
                                    const ByteArray& session_code) {
  const auto& crypto = CryptoLibrary::instance();
  ByteArray hashed_key = crypto->mac_sha_256(session_code, key);
  const ByteArray derived_key = ByteArray(hashed_key.begin(),
                                          hashed_key.begin() + 16);
  mcbp_core::Wipe(&hashed_key);
  return derived_key;
}

/**
 * Build both UMD and MD cryptograms for the Generate AC. The two cryptograms
 * are returned as concatenated vectors
 */
ByteArray generate_ac(const ByteArray& cryptogram_input,
                      const ByteArray& umd_session_key,
                      const ByteArray& md_session_key) {
  using mcbp_core::CryptoFactory;

  const ByteArray umd =
    CryptoFactory::instance()->mac(cryptogram_input, umd_session_key);

  const ByteArray md =
    CryptoFactory::instance()->mac(cryptogram_input, md_session_key);

  ByteArray result;
  result.reserve(umd.size() + md.size());

  result.insert(result.end(), umd.begin(), umd.end());
  result.insert(result.end(), md.begin(), md.end());

  return result;
}

/**
 * Build both UMD and MD cryptograms for the Compute Cryptographic Checksum. 
 * The two cryptograms are returned as concatenated vectors
 */
ByteArray compute_cc(const ByteArray& cryptogram_input,
                     const ByteArray& umd_session_key,
                     const ByteArray& md_session_key) {
  using mcbp_core::CryptoFactory;

  const ByteArray umd =
    CryptoFactory::instance()->des_3(cryptogram_input, umd_session_key, true);

  const ByteArray md =
    CryptoFactory::instance()->des_3(cryptogram_input, md_session_key, true);

  ByteArray result = umd;
  result.insert(result.end(), md.begin(), md.end());

  return result;
}

/**
 * Unlock session key from single use key using the PIN
 */
ByteArray unlock_session_key(const ByteArray& single_use_key,
                             const ByteArray& mobile_pin) {
  using mcbp_core::CryptoFactory;

  // There is little we can do if the PIN is empty!
  if (mobile_pin.empty()) return single_use_key;

  ByteArray session_key = single_use_key;

  const Byte pin_size = static_cast<Byte>(mobile_pin.size());

  ByteArray shifted_pin(pin_size, 0x00);
  for (std::size_t i = 0; i < pin_size; i++)
    shifted_pin[i] = (mobile_pin[i] << 1);

  // We can only handle up to 8 digit PIN, if more then just consider 8
  const std::size_t len = pin_size < 8 ? pin_size: 8;
  for (std::size_t i = 0; i < len; i++) {
    // Unlock both Contactless and Remote Payment keys
    session_key[i]     ^= shifted_pin[i];
    session_key[i + 8] ^= shifted_pin[i];
  }

  return session_key;
}

/**
 * Perform an LDE encryption
 */
ByteArray lde_encryption(const ByteArray& data, const ByteArray& key) {
  const ByteArray input = mcbp_core::add_iso_7816_padding(data);
  return CryptoLibrary::instance()->aes_ecb_nopadding(input, key, true);
}

/**
 * Perform an LDE decryption
 */
ByteArray lde_decryption(const ByteArray& data, const ByteArray& key) {
  const ByteArray plain =
      CryptoLibrary::instance()->aes_ecb_nopadding(data, key, false);
  return mcbp_core::remove_iso_7816_padding(plain);
}

MobileKeys decrypt_mobile_keys(const ByteArray& encrypted_transport_key,
                               const ByteArray& encrypted_mac_key,
                               const ByteArray& encrypted_data_encryption_key,
                               const ByteArray& key) {
    const auto* crypto = CryptoLibrary::instance();
    ByteArray transport_key =
        crypto->aes_ecb_nopadding(encrypted_transport_key, key, false);
    ByteArray mac_key =
        crypto->aes_ecb_nopadding(encrypted_mac_key, key, false);
    ByteArray data_encryption_key =
        crypto->aes_ecb_nopadding(encrypted_data_encryption_key, key, false);

    MobileKeys mobile_keys(transport_key, mac_key, data_encryption_key);

    // Clean up temporary data variables
    mcbp_core::Wipe(&transport_key);
    mcbp_core::Wipe(&mac_key);
    mcbp_core::Wipe(&data_encryption_key);

    return mobile_keys;
}

ByteArray decrypt_notification_data(const ByteArray& response_data,
                                    const ByteArray& mac_key,
                                    const ByteArray& transport_key) {
  const size_t encrypted_data_length = response_data.size() - 8;
  if (encrypted_data_length <= 16) {
    throw mcbp_core::InvalidInput("Invalid message length");
  }

  ByteArray encrypted_data;
  encrypted_data.insert(encrypted_data.end(),
                        response_data.begin(),
                        response_data.begin() + encrypted_data_length);
  ByteArray received_mac;
  received_mac.insert(received_mac.end(),
                      response_data.begin() + encrypted_data_length,
                      response_data.end());

  const auto* crypto = CryptoLibrary::instance();
  ByteArray calculated_mac = crypto->aes_cbc_mac(encrypted_data, mac_key);

  if (calculated_mac != received_mac) {
    throw mcbp_core::InvalidInput("Calculated MAC does not match");
  }

  // Securely zeroes the MAC as we do not need it any longer
  mcbp_core::Wipe(&calculated_mac);
  ByteArray decrypted_data = crypto->aes_cbc_nopadding(encrypted_data,
                                                       transport_key,
                                                       false);
  ByteArray notification_data =
      mcbp_core::remove_iso_7816_padding(decrypted_data);

  // Securely zeroes temporary data
  mcbp_core::Wipe(&decrypted_data);

  return notification_data;
}

ByteArray encrypt_retry_request_data(const ByteArray& data,
                                     const ByteArray& key) {
  ByteArray input = mcbp_core::add_iso_7816_padding(data);
  const auto* crypto = CryptoLibrary::instance();
  const ByteArray result = crypto->aes_ecb_nopadding(input, key, true);
  mcbp_core::Wipe(&input);
  return result;
}

ByteArray decrypt_retry_request_data(const ByteArray& data,
                                     const ByteArray& key) {
  const auto* crypto = CryptoLibrary::instance();
  return mcbp_core::remove_iso_7816_padding(
      crypto->aes_ecb_nopadding(data, key, false));
}

ByteArray derive_session_key(const ByteArray& key, const ByteArray& code) {
  const auto* crypto = CryptoLibrary::instance();
  ByteArray hashed_key = crypto->mac_sha_256(code, key);

  ByteArray derived_key(16, 0x00);
  for (int i = 0; i < 16; i++) {
    derived_key[i] = hashed_key[i];
  }
  mcbp_core::Wipe(&hashed_key);
  return derived_key;
}

/**
 * Build the IV for AES CTR mode from the counters value. 
 * The direction MPA to CMS (true) or CMS to MPA (false) must be specified
 */
ByteArray build_iv_from_counter(const uint16_t counter, bool mpa_to_cms) {
  ByteArray iv(16, 0x00);
  iv[0] = mpa_to_cms ? 0x00: 0x01;

  if (counter <= 0xFF) {
    iv[3] = (counter & 0xFF);
  } else if (counter < 0xFFFF) {
    iv[2] = ((counter & 0xFF00) >> 8);
    iv[3] = (counter & 0x00FF);
  } else {
    // Never Expected.
    throw mcbp_core::InvalidInput("Invalid M2C");
  }
  return iv;
}

ByteArray build_service_request(const ByteArray& data,
                                const ByteArray& mac_key,
                                const ByteArray& transport_key,
                                const ByteArray& session_code,
                                const uint16_t counter) {
  const auto* crypto = CryptoLibrary::instance();
  ByteArray derived_transport_key =
      derive_session_key(transport_key, session_code);
  ByteArray derived_mac_key = derive_session_key(mac_key, session_code);

  ByteArray iv = build_iv_from_counter(counter, true);
  ByteArray encrypted_request_data =
      crypto->aes_ctr_nopadding(data, iv, derived_transport_key, true);
  ByteArray mac = crypto->aes_cbc_mac(encrypted_request_data, derived_mac_key);

  ByteArray service_request;
  service_request.reserve(3 + encrypted_request_data.size() + mac.size());
  service_request.insert(service_request.end(), iv.begin() + 1, iv.begin() + 4);
  service_request.insert(service_request.end(),
                         encrypted_request_data.begin(),
                         encrypted_request_data.end());
  service_request.insert(service_request.end(), mac.begin(), mac.end());

  mcbp_core::Wipe(&derived_mac_key);
  mcbp_core::Wipe(&derived_transport_key);
  mcbp_core::Wipe(&iv);
  mcbp_core::Wipe(&encrypted_request_data);

  return service_request;
}

ByteArray decrypt_service_response(const ByteArray& service_data,
                                   const ByteArray& mac_key,
                                   const ByteArray& transport_key,
                                   const ByteArray& session_code) {
  const ByteArray derived_mac_key =
      derive_mobile_session_key(mac_key, session_code);
  const ByteArray derived_transport_key =
      derive_mobile_session_key(transport_key, session_code);

  // Now parse the service data
  const size_t mac_offset = service_data.size() - 8;
  const ByteArray counters =
      ByteArray(service_data.begin(), service_data.begin() + 3);

  const ByteArray encrypted_data = ByteArray(service_data.begin() + 3,
      service_data.begin() + mac_offset);

  const ByteArray received_mac =
      ByteArray(service_data.begin() + mac_offset, service_data.end());

  // Verify the received mac matches the one we calculated
  const auto& crypto = CryptoLibrary::instance();
  const ByteArray computed_mac =
      crypto->aes_cbc_mac(encrypted_data, derived_mac_key);

  if (received_mac != computed_mac) {
    throw mcbp_core::InvalidInput("Calculated and received MACs mismatch");
  }

  // MAC matches: let's decrypt the message
  const int counter = ((counters[1] & 0x00FF) << 8) + (counters[2] & 0x00FF);
  const ByteArray iv = build_iv_from_counter(counter, false);

  return crypto->aes_ctr_nopadding(encrypted_data, iv,
                                   derived_transport_key, false);
}

ByteArray decrypt_icc_component(const ByteArray& data,
                                const ByteArray& key) {
  const ByteArray plain =
      CryptoLibrary::instance()->aes_ecb_nopadding(data, key, false);
  return mcbp_core::remove_iso_7816_padding(plain);
}

ByteArray decrypt_icc_kek(const ByteArray& data, const ByteArray& key) {
    const ByteArray plain =
      CryptoLibrary::instance()->aes_ecb_nopadding(data, key, false);
  return mcbp_core::remove_iso_7816_padding(plain);
}

ByteArray calculate_authentication_code(const ByteArray& mobile_keyset_id,
                                        const ByteArray& session_code,
                                        const ByteArray& device_finger_print) {
  ByteArray data_to_hash = ByteArray();
  data_to_hash.reserve(mobile_keyset_id.size()
                       + device_finger_print.size()
                       + session_code.size());

  data_to_hash.insert(data_to_hash.end(),
                      mobile_keyset_id.begin(),
                      mobile_keyset_id.end());
  data_to_hash.insert(data_to_hash.end(),
                      session_code.begin(),
                      session_code.end());
  data_to_hash.insert(data_to_hash.end(),
                      device_finger_print.begin(),
                      device_finger_print.end());

  const ByteArray authentication_code =
      CryptoLibrary::instance()->sha_256(data_to_hash);

  mcbp_core::Wipe(&data_to_hash);

  return authentication_code;
}

ByteArray decrypt_data_encrypted_field(const ByteArray& data,
                                       const ByteArray& key) {
  return CryptoLibrary::instance()->aes_ecb_nopadding(data, key, false);
}

ByteArray encrypt_random_generated_key(const ByteArray& random_generated_key,
                                       const ByteArray& public_key) {
  const auto& crypto = CryptoLibrary::instance();
  return crypto->rsa_oaep_sha256_mgf1(random_generated_key, public_key, true);
}

/**
 * Utility function to extract the PAN data used to decrypt the PIN Block
 */
ByteArray generate_plain_text_pan_field(const ByteArray& pan_data) {
  ByteArray pan(pan_data.size());
  // Check the input data
  if (pan_data.size() < 12 || pan_data.size() > 19) {
    throw mcbp_core::InvalidInput("Invalid PAN data length");
  }
  for (int i = 0; i < pan_data.size(); i++) {
    pan[i] = pan_data[i] - 0x30;  // Remove ASCII coding
    if (pan[i] < 0 || pan[i] > 9) {
      throw mcbp_core::InvalidInput("Invalid PAN data");
    }
  }
  // Prepare the output
  ByteArray plain_text_pan_field(16, 0x00);

  // Manage the first byte separately
  plain_text_pan_field[0] = ((pan.size() - 12) << 4);
  plain_text_pan_field[0] |= (pan[0] & 0x0F);

  const size_t pan_length = pan.size();

  // Fill in the data
  for (int i = 1; i < plain_text_pan_field.size(); i++) {
    const size_t index = (i << 1) - 1;  // Get the next index to be used

    // Add one digit
    if (index >= pan_length) break;
    plain_text_pan_field[i] = (pan[index] << 4);

    // Check if we should add the second digit as well
    if (index + 1 >= pan_length) break;
    plain_text_pan_field[i] += pan[index + 1];
  }
  return plain_text_pan_field;
}

ByteArray generate_plain_text_pin_field(const ByteArray& pin_data) {
  // First check the data
  if (pin_data.size() < 4 || pin_data.size() > 12) {
    throw mcbp_core::InvalidInput("Invalid PIN Lenght");
  }
  ByteArray pin(pin_data.size());
  for (int i = 0; i < pin_data.size(); i++) {
    pin[i] = pin_data[i] - 0x30;  // Remove the ASCII coding
    if (pin[i] < 0 || pin[i] > 9) {
      throw mcbp_core::InvalidInput("Invalid PIN digit");
    }
  }
  // Now let's process the PIN
  const auto& crypto = CryptoLibrary::instance();
  // We initialize the field with random data, so that we do not have to fill it
  // up later
  ByteArray plain_text_pin_field = crypto->generate_random(16);

  // 0x40 is the value of the Control Field C (i.e. 0100)
  plain_text_pin_field[0] = 0x40 + (0x0F & pin.size());

  const byte fill_nibble = 0x0A;
  const byte fill_byte = 0xAA;

  size_t to_be_filled = 1;
  for (int i = 1; i < plain_text_pin_field.size() / 2; i++) {
    const size_t index = ((i - 1) << 1);
    to_be_filled = i;

    if (index >= pin.size()) break;

    plain_text_pin_field[i] = (pin[index] << 4);

    if (index + 1 < pin.size()) {
      plain_text_pin_field[i] += pin[index + 1];
    } else {
      plain_text_pin_field[i] += fill_nibble;  // Fill Digit
      to_be_filled = i + 1;
      break;
    }
  }
  // Check whether we need to fill any remaining byte
  for (size_t i = to_be_filled; i < plain_text_pin_field.size() / 2; i++) {
    plain_text_pin_field[i] = fill_byte;
  }

  return plain_text_pin_field;
}

/**
 * Retrieve the PIN from the plain text PIN block format
 */
ByteArray pin_from_plain_text_pin_block(const ByteArray& plain_text_pin_block) {
  if (plain_text_pin_block.size() != 16) {
    throw mcbp_core::InvalidInput("Invalid Plain Text PIN block");
  }
  const size_t pin_length = plain_text_pin_block[0] & 0x0F;
  if (pin_length < 4 | pin_length > 12) {
    throw mcbp_core::InvalidInput("Invalid PIN length");
  }
  ByteArray pin(pin_length);
  for (int i = 0, j = 1; i < pin_length; i++) {
    if (i % 2 == 0) {  // We are reading the most significant nibble
      pin[i] = ((plain_text_pin_block[j] & 0xF0) >> 4);
    } else {
      pin[i] = (plain_text_pin_block[j] & 0x0F);
      j++;  // We have read both nibbles, let's move to the next one
    }
    // Check we have got the right value
    if (pin[i] < 0 || pin[i] > 9) {
      throw mcbp_core::InvalidInput("Invalid Pin Digit");
    }
    // We add 0x30 to bring the pin back to ASCII encoding
    pin[i] += 0x30;
  }
  return pin;
}

ByteArray encrypt_pin_block(const ByteArray& pin_data,
                            const ByteArray& application_payment_instance_id,
                            const ByteArray& key) {
  const auto& crypto = CryptoLibrary::instance();
  ByteArray pan_surrogate =
      crypto->generate_pan_substitute_value(application_payment_instance_id);
  ByteArray plain_text_pin_field =
      generate_plain_text_pin_field(pin_data);
  ByteArray plain_text_pan_field =
      generate_plain_text_pan_field(pan_surrogate);
  ByteArray intermediate_block_a =
      crypto->aes_ecb_nopadding(plain_text_pin_field, key, true);

  // XOR the intermediate data a with the pan field
  ByteArray intermediate_block_b(intermediate_block_a.size());
  for (int i = 0; i < intermediate_block_b.size(); i++) {
    intermediate_block_b[i] = intermediate_block_a[i] ^ plain_text_pan_field[i];
  }
  const ByteArray pin_block =
      crypto->aes_ecb_nopadding(intermediate_block_b, key, true);

  // Wipe all the working variables before returning
  mcbp_core::Wipe(&pan_surrogate);
  mcbp_core::Wipe(&plain_text_pan_field);
  mcbp_core::Wipe(&plain_text_pin_field);
  mcbp_core::Wipe(&intermediate_block_a);
  mcbp_core::Wipe(&intermediate_block_b);
  return pin_block;
}

ByteArray decrypt_pin_block(const ByteArray& pin_data,
                            const ByteArray& payment_instance_id,
                            const ByteArray& key) {
  const auto& crypto = CryptoLibrary::instance();

  const ByteArray pan_surrogate =
      crypto->generate_pan_substitute_value(payment_instance_id);

  const ByteArray decrypted_data =
      crypto->aes_ecb_nopadding(pin_data, key, false);

  const ByteArray plain_text_pan_field =
      generate_plain_text_pan_field(pan_surrogate);

  ByteArray intermediate_block_a(plain_text_pan_field.size());

  for (int i = 0; i < plain_text_pan_field.size(); i++) {
    intermediate_block_a[i] = plain_text_pan_field[i] ^ decrypted_data[i];
  }

  const ByteArray intermediate_block_b =
      crypto->aes_ecb_nopadding(intermediate_block_a, key, false);

  return pin_from_plain_text_pin_block(intermediate_block_b);
}

}  // End of namespace mcbp_crypto_service
