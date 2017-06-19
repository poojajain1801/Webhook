/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
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
 */

package com.mastercard.mcbp.utils.crypto;

import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Random;

public class UnitTestPinBlockFormatter {
    CryptoService defaultCryptoService = null;

    @Before
    public void setUp() throws Exception {
        defaultCryptoService = CryptoServiceFactory.getDefaultCryptoService();
    }

    @Test
    public void testPanNumber() throws Exception {
        String EXPECTED_VALUE = "1586494682035777";
        String paymentAppInstanceId = "paymentAppInstanceId123";
        ByteArray result = CryptoServiceImpl.generatePanSubstituteValue(paymentAppInstanceId);
        Assert.assertEquals(EXPECTED_VALUE, new String(result.getBytes()));
    }

    //--------------------------------------------------------------------------------------------
    // Plain PIN block related test cases
    //--------------------------------------------------------------------------------------------

    @Test(expected = McbpCryptoException.class)
    public void testVerifyPlainPinBlockWithNull() throws Exception {
        CryptoServiceImpl.generatePlainTextPinField(null);
    }

    @Test(expected = McbpCryptoException.class)
    public void testVerifyPlainPinBlockWith3Length() throws Exception {
        String pin = "123";
        ByteArray byteArray = ByteArray.of(pin.getBytes());
        CryptoServiceImpl.generatePlainTextPinField(byteArray);
    }

    @Test(expected = McbpCryptoException.class)
    public void testVerifyPlainPinBlockWith13Length() throws Exception {
        String pin = "1234567890123";
        ByteArray byteArray = ByteArray.of(pin.getBytes());
        CryptoServiceImpl.generatePlainTextPinField(byteArray);
    }

    @Test
    public void testVerifyPlainPinBlockWith4Length() throws Exception {
        String EXPECTED_DATA = "441234AAAAAAAAAA";
        String pin = "1234";
        ByteArray byteArray = ByteArray.of(pin.getBytes());
        ByteArray result = CryptoServiceImpl
                .generatePlainTextPinField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().substring(0, 16));
    }

    @Test
    public void testVerifyPlainPinBlockWith9Length() throws Exception {
        String EXPECTED_DATA = "49123411234AAAAA";
        String pin = "123411234";
        ByteArray byteArray = ByteArray.of(pin.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPinField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().substring(0, 16));
    }

    @Test
    public void testVerifyPlainPinBlockWith12Length() throws Exception {
        String EXPECTED_DATA = "4C123411234141AA";
        String pin = "123411234141";
        ByteArray byteArray = ByteArray.of(pin.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPinField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString().substring(0, 16));
    }

    //--------------------------------------------------------------------------------------------
    // Plain PAN block related test cases
    //--------------------------------------------------------------------------------------------

    @Test(expected = McbpCryptoException.class)
    public void testVerifyPlainPanBlockWithNull() throws Exception {
        CryptoServiceImpl.generatePlainTextPinField(null);
    }

    @Test(expected = McbpCryptoException.class)
    public void testVerifyPlainPanBlockWith24Length() throws Exception {
        ByteArray byteArray = ByteArray.get(24);
        byteArray.setByte(0, (byte) 1);
        byteArray.setByte(1, (byte) 2);
        byteArray.setByte(2, (byte) 3);
        byteArray.setByte(3, (byte) 4);
        byteArray.setByte(4, (byte) 5);
        byteArray.setByte(5, (byte) 6);
        byteArray.setByte(6, (byte) 7);
        byteArray.setByte(7, (byte) 8);
        byteArray.setByte(8, (byte) 9);
        byteArray.setByte(9, (byte) 0);
        byteArray.setByte(10, (byte) 1);
        byteArray.setByte(11, (byte) 2);
        byteArray.setByte(12, (byte) 3);
        byteArray.setByte(13, (byte) 4);
        byteArray.setByte(14, (byte) 5);
        byteArray.setByte(15, (byte) 6);
        byteArray.setByte(16, (byte) 7);
        byteArray.setByte(17, (byte) 8);
        byteArray.setByte(18, (byte) 9);
        byteArray.setByte(19, (byte) 9);
        byteArray.setByte(20, (byte) 9);
        byteArray.setByte(21, (byte) 9);
        byteArray.setByte(22, (byte) 9);
        byteArray.setByte(23, (byte) 9);
        CryptoServiceImpl.generatePlainTextPinField(byteArray);
    }

    @Test
    public void testVerifyPlainPanBlockWith3Length() throws Exception {
        String EXPECTED_DATA = "00000000001230000000000000000000";
        String pan = "123";
        ByteArray byteArray = ByteArray.of(pan.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPanField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString());
    }

    @Test
    public void testVerifyPlainPanBlockWith7Length() throws Exception {
        String EXPECTED_DATA = "00000012345670000000000000000000";
        String pan = "1234567";
        ByteArray byteArray = ByteArray.of(pan.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPanField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString());
    }

    @Test
    public void testVerifyPlainPanBlockWith9Length() throws Exception {
        String EXPECTED_DATA = "00001234567890000000000000000000";
        String pan = "123456789";
        ByteArray byteArray = ByteArray.of(pan.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPanField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString());
    }

    @Test
    public void testVerifyPlainPanBlockWith12Length() throws Exception {
        String EXPECTED_DATA = "01234567890120000000000000000000";
        String pan = "123456789012";
        ByteArray byteArray = ByteArray.of(pan.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPanField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString());
    }

    @Test
    public void testVerifyPlainPanBlockWith14Length() throws Exception {
        String EXPECTED_DATA = "21234567890123400000000000000000";
        String pan = "12345678901234";
        ByteArray byteArray = ByteArray.of(pan.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPanField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString());
    }

    @Test
    public void testVerifyPlainPanBlockWith17Length() throws Exception {
        String EXPECTED_DATA = "51234567890123456700000000000000";
        String pan = "12345678901234567";
        ByteArray byteArray = ByteArray.of(pan.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPanField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString());
    }

    @Test
    public void testVerifyPlainPanBlockWith19Length() throws Exception {
        String EXPECTED_DATA = "71234567890123456789000000000000";
        String pan = "1234567890123456789";
        ByteArray byteArray = ByteArray.of(pan.getBytes());
        ByteArray result = CryptoServiceImpl.generatePlainTextPanField(byteArray);
        Assert.assertEquals(EXPECTED_DATA, result.toHexString());
    }

    //--------------------------------------------------------------------------------------------
    // Retrieve PIN from plain PIN block
    //--------------------------------------------------------------------------------------------

    @Test(expected = InvalidInput.class)
    public void testGeneratePinFromPlainPinBlockWithNullValue() throws Exception {
        retrievePinFromPlainPinFormat(null);
    }

    @Test
    public void testGeneratePinFromPlainPinBlockWith4Length() throws Exception {
        String EXPECTED_PIN = "1234";
        ByteArray result = CryptoServiceImpl.generatePlainTextPinField(
                ByteArray.of(EXPECTED_PIN.getBytes(Charset.defaultCharset())));
        Assert.assertEquals(EXPECTED_PIN,
                            new String(retrievePinFromPlainPinFormat(result).getBytes()));
    }

    @Test
    public void testGeneratePinFromPlainPinBlockWith6Length() throws Exception {
        String EXPECTED_PIN = "123456";
        ByteArray result = CryptoServiceImpl.generatePlainTextPinField(
                ByteArray.of(EXPECTED_PIN.getBytes(Charset.defaultCharset())));
        Assert.assertEquals(EXPECTED_PIN,
                            new String(retrievePinFromPlainPinFormat(result).getBytes()));
    }

    @Test
    public void testGeneratePinFromPlainPinBlockWith9Length() throws Exception {
        String EXPECTED_PIN = "123456789";
        ByteArray result = CryptoServiceImpl.generatePlainTextPinField(
                ByteArray.of(EXPECTED_PIN.getBytes(Charset.defaultCharset())));
        String actual = new String(retrievePinFromPlainPinFormat(result).getBytes());
        Assert.assertEquals(EXPECTED_PIN, actual);

    }

    @Test
    public void testGeneratePinFromPlainPinBlockWith12Length() throws Exception {
        String EXPECTED_PIN = "123456789012";
        ByteArray result = CryptoServiceImpl.generatePlainTextPinField(
                ByteArray.of(EXPECTED_PIN.getBytes(Charset.defaultCharset())));
        String actual = new String(retrievePinFromPlainPinFormat(result).getBytes());
        Assert.assertEquals(EXPECTED_PIN, actual);
    }

    @Test
    public void testDecryptEncryptedPINData() throws Exception {
        for (int i = 4; i <= 8; i++) {
            final ByteArray pin = generateRandomPin(i);
            String paymentAppInstanceId = generateRandomString(30);
            ByteArray encryptionKey = generateRandomKey(16);

            ByteArray encryptedPinBlock = CryptoServiceImpl.INSTANCE
                    .encryptPinBlock(pin, paymentAppInstanceId, encryptionKey);

            ByteArray decryptedPin =
                    retrievePinFromPlainPinFormat(
                            CryptoServiceImpl.INSTANCE.decryptPinBlock(encryptedPinBlock,
                                                                       paymentAppInstanceId,
                                                                       encryptionKey));
            Assert.assertEquals(pin.toHexString(), decryptedPin.toHexString());
        }
    }

    /**
     * Retrieve pin from plain PIN block.
     *
     * @param pinFormatData plain PIN block
     * @return PIN
     * @throws com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput
     */
    private static ByteArray retrievePinFromPlainPinFormat(ByteArray pinFormatData) throws
            InvalidInput {
        if (pinFormatData == null || pinFormatData.getLength() != 16) {
            throw new InvalidInput("Invalid data");
        }

        byte firstByte = pinFormatData.getByte(0);
        int pinLength = (firstByte & 0x0F);
        byte pinArray[] = new byte[pinLength];
        boolean isPinOddLength = pinLength % 2 != 0;
        int index = 1;
        int count = 0;
        int computedPinLength = (isPinOddLength ? pinLength - 1 : pinLength) / 2;
        for (; index <= computedPinLength; index++) {
            byte currentByte = (byte) ((pinFormatData.getByte(index) & 0xF0) >> 4);
            pinArray[count++] = (byte) (currentByte | 0x30);
            byte nextByte = (byte) (pinFormatData.getByte(index) & 0x0F);
            pinArray[count++] = (byte) (nextByte | 0x30);
        }
        if (isPinOddLength) {
            int currentByte = (pinFormatData.getByte(index) & 0xF0) >> 4;
            pinArray[count] = (byte) (currentByte | 0x30);
        }
        return ByteArray.of(pinArray);
    }

    @Test
    public void test_AesEcbNoPadding() throws Exception {
        final ByteArray data = ByteArray.of("70310DC89493BC3B4761E6B9257B9155");
        final ByteArray key = ByteArray.of("EC3EC79CFAB9A1DE08845EECED507980");
        final ByteArray expected = ByteArray.of("4836270909AAAAAAD9F3F6F9E658F359");

        final ByteArray decrypted = CryptoServiceImpl.INSTANCE.aesEcb(data, key,
                                                                      CryptoService.Mode.DECRYPT);
        Assert.assertEquals(expected.toHexString(), decrypted.toHexString());
    }

    @Test
    public void test_AesEcbNoPadding2() throws Exception {
        final ByteArray data = ByteArray.of("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        final ByteArray key =  ByteArray.of("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        final ByteArray expected = ByteArray.of("737DEABF6E29AF64FED9439C96837BE6");

        final ByteArray decrypted = CryptoServiceImpl.INSTANCE.aesEcb(data, key,
                                                                      CryptoService.Mode.DECRYPT);
        Assert.assertEquals(expected.toHexString(), decrypted.toHexString());
    }

    @Test
    public void test_AesEcbNoPadding3() throws Exception {
        final ByteArray data = ByteArray.of("4836270909AAAAAAD9F3F6F9E658F359");
        final ByteArray key =  ByteArray.of("EC3EC79CFAB9A1DE08845EECED507980");
        final ByteArray expected = ByteArray.of("70310DC89493BC3B4761E6B9257B9155");

        final ByteArray decrypted = CryptoServiceImpl.INSTANCE.aesEcb(data, key,
                                                                      CryptoService.Mode.ENCRYPT);
        Assert.assertEquals(expected.toHexString(), decrypted.toHexString());
    }

    @Test
    public void test_AesEcbNoPadding4() throws Exception {
        final ByteArray data = ByteArray.of("57251ACDE8271AAEE62518AA3232AABB");
        final ByteArray key =  ByteArray.of("126AABBCD46241715AFFFFA2618ADFEE");
        final ByteArray expected = ByteArray.of("C77B724FADD2C560E00C83F0B98BA342");

        final ByteArray decrypted = CryptoServiceImpl.INSTANCE.aesEcb(data, key,
                                                                      CryptoService.Mode.ENCRYPT);
        Assert.assertEquals(expected.toHexString(), decrypted.toHexString());
    }

    private static ByteArray generateRandomPin(final int length) throws Exception {
        Random random = new Random();
        byte[] pin = new byte[length];
        // Add digits to the PIN
        for (int i = 0; i < length; i++) {
            // Generate a Random digit and convert to ASCII
            int nextDigit = Math.abs(random.nextInt()) % 10;
            pin[i] = (byte) (0x30 + nextDigit);
            if (pin[i] < 0x30 || pin[i] > 0x39) {
                throw new InvalidInput("Invalid PIN Digit");
            }
        }
        ByteArray result = ByteArray.of(pin);
        Utils.clearByteArray(pin);
        return result;
    }

    private static ByteArray generateRandomKey(final int length) {
        Random random = new Random();
        byte[] key = new byte[length];
        random.nextBytes(key);
        ByteArray result = ByteArray.of(key);
        Utils.clearByteArray(key);
        return result;
    }

    private static String generateRandomString(final int length) {
        Random random = new Random();
        byte[] random_string = new byte[length];
        for (int i = 0; i < length; i++) {
            // We get only printable ASCII characters (27 - 132)
            random_string[i] = (byte) (27 + (Math.abs(random.nextInt()) % (132 - 27)));
        }
        String result = new String(random_string);
        Utils.clearByteArray(random_string);
        return result;
    }
}
