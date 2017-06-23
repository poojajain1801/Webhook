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

import android.test.AndroidTestCase;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

/**
 * Android unit test for the Native crypto library
 */
public class NativeCryptoLibraryTest extends AndroidTestCase {
    private CryptoService mCryptoServiceNative = CryptoServiceNativeImpl.INSTANCE;


    /**
     * Test the generation of the AC cryptogram
     */
    public void test_BuildGenerateAcCryptograms() throws Exception {
        ByteArray acInput = ByteArray.of("00000000050000000000000000560020000000097814120300"
                                         + "935306871B800001A54100040000");
        ByteArray umdSessionKey = ByteArray.of("9E40C928DA0D2E8F38E7B1FB3CF521B6");
        ByteArray mdSessionKey = ByteArray.of("225BC8E86ED81A00F9CF9C74A6653BD5");

        ByteArray expectedUmd = ByteArray.of("279F5D1D37EDF611");
        ByteArray expectedMd = ByteArray.of("D5C912973960126C");

        CryptoService.TransactionCryptograms cryptograms =
                mCryptoServiceNative.buildGenerateAcCryptograms(acInput.getBytes(),
                                                                umdSessionKey.getBytes(),
                                                                mdSessionKey.getBytes());
        assertEquals(expectedUmd.toHexString(),
                     ByteArray.of(cryptograms.getUmdCryptogram()).toHexString());

        assertEquals(expectedMd.toHexString(),
                     ByteArray.of(cryptograms.getMdCryptogram()).toHexString());
    }

    /**
     * Test the generation of the compute cryptographic checksum cryptogram
     */
    public void test_buildComputeCcCryptograms() throws Exception {
        ByteArray acInput = ByteArray.of("AD56000004900008");
        ByteArray umdSessionKey = ByteArray.of("E97FD90ED81900F668CA331D6CB478AA");
        ByteArray mdSessionKey = ByteArray.of("A5D401AC8EFEB6FD61B9C7225B019874");

        ByteArray expectedUmd = ByteArray.of("C178B137031B6AF5");
        ByteArray expectedMd = ByteArray.of("FAD98F95E4D66432");

        CryptoService.TransactionCryptograms cryptograms =
                mCryptoServiceNative.buildComputeCcCryptograms(acInput.getBytes(),
                                                               umdSessionKey.getBytes(),
                                                               mdSessionKey.getBytes());
        assertEquals(expectedUmd.toHexString(),
                     ByteArray.of(cryptograms.getUmdCryptogram()).toHexString());

        assertEquals(expectedMd.toHexString(),
                     ByteArray.of(cryptograms.getMdCryptogram()).toHexString());
    }

    /**
     * Test the unlock of the Session Key by combining (XOR) the Single Use Key with the Mobile Pin
     */
    public void test_deriveSessionKey() throws Exception {
        ByteArray suk = ByteArray.of("3E6B740FB26E0C3B6A0BA89EFA60D147");
        ByteArray mobilePin = ByteArray.of("31323334");
        ByteArray expectedUmdSessionKey = ByteArray.of("5C0F1267B26E0C3B086FCEF6FA60D147");

        ByteArray umdSessionKey = mCryptoServiceNative.deriveSessionKey(suk, mobilePin);
        assertEquals(expectedUmdSessionKey.toHexString(), umdSessionKey.toHexString());
    }

    public void test_decryptDataEncryptedField() throws Exception {
        ByteArray data = ByteArray.of("AAF701AF8AEC94573D6BD55ED2958C49");
        ByteArray key = ByteArray.of("C129682A37061084F10D4B2C608C1544");
        ByteArray expected = ByteArray.of("437B73AB0489D2E4120E4EE17ABC6FA2");
        ByteArray actual = mCryptoServiceNative.decryptDataEncryptedField(data, key);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_decryptIccComponent() throws Exception {
        ByteArray data =
                ByteArray.of("CF4D28041A0AE0E4C25211BFB535E5269B6A29CA205932ACB5536ECB9C17DD1B2A443"
                             + "9C387D2F0E51103A6A710A5A2CE0ABE01D759EDB5FA9D752877CDE37BEA26AE243E"
                             + "5B1467B6BF2792CFBA6599A4");
        ByteArray key = ByteArray.of("B693AE272A8BECACC0B5C4FFF9E16356");
        ByteArray expected = ByteArray.of("282B347A9295B88783D105961B28DA43493A5A70DBF78D23D3A228D9"
                                          + "63F9C3895B871B07FBD08946741BEBBB2049DCF17A0944DC626B3A"
                                          + "AE5CA268A4C2889376");
        ByteArray actual = mCryptoServiceNative.decryptIccComponent(data, key);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_decryptIccKek() throws Exception {
        ByteArray data =
                ByteArray.of("4C1EAE18D6EEFE00065B8E183057674AA68383FA2528A0C7A99F9842066837F95E120"
                             + "9DABA0B9302B10E7D72E8D93DAE22650C340F16532534A725D99DB8984A26AE243E"
                             + "5B1467B6BF2792CFBA6599A4");
        ByteArray key = ByteArray.of("B693AE272A8BECACC0B5C4FFF9E16356");
        ByteArray expected =
                ByteArray.of("C79E8001669C4235FE757891D2D8FA68128B19842D8EA586E47D266A4F712EC62D8E0"
                             + "A00614E724F42566F41FA5F6E53BC134BBA762B16893C3450B76FFA5863");
        ByteArray actual = mCryptoServiceNative.decryptIccKey(data, key);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_ldeEncryption() throws Exception {
        ByteArray data = ByteArray.of("72899A9B37904E4CF1C628F8A9540DBE");
        ByteArray key = ByteArray.of("1D0784F13D12840BCC63295B3E8C46A85ACAE5A0FDB393B826E0FBA1AB"
                                     + "CEEC52");
        ByteArray expected = ByteArray.of("49768E66A2123251368757CC39625084A7FFC97A12550102EE704"
                                          + "508E2FDD72A");
        ByteArray actual = ByteArray.of(
                mCryptoServiceNative.ldeEncryption(data.getBytes(), key.getBytes()));
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_ldeDecryption() throws Exception {
        ByteArray data = ByteArray.of("49768E66A2123251368757CC39625084A7FFC97A12550102EE704508E2"
                                      + "FDD72A");
        ByteArray key = ByteArray.of("1D0784F13D12840BCC63295B3E8C46A85ACAE5A0FDB393B826E0FBA1ABC"
                                     + "EEC52");
        ByteArray expected = ByteArray.of("72899A9B37904E4CF1C628F8A9540DBE");
        ByteArray actual = ByteArray.of(
                mCryptoServiceNative.ldeDecryption(data.getBytes(), key.getBytes()));
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_encryptPinBlockSimple() throws Exception {
        ByteArray pin = ByteArray.of("31323334353637");
        String paymentInstanceId = "7cef8825-01fc-4829-bfbd-77e8ef1aad78cc97448b-6bf";
        ByteArray key = ByteArray.of("C129682A37061084F10D4B2C608C1544");

        ByteArray encryptedPinBlock =
                mCryptoServiceNative.encryptPinBlock(pin, paymentInstanceId, key);
        ByteArray decryptedPin =
                retrievePinFromPlainPinFormat(
                        CryptoServiceImpl.INSTANCE.decryptPinBlock(encryptedPinBlock,
                                                                   paymentInstanceId,
                                                                   key));
        assertEquals(pin.toHexString(), decryptedPin.toHexString());
    }

    public void test_encryptPinBlock() throws Exception {
        for (int i = 4; i <= 8; i++) {
            final ByteArray pin = generateRandomPin(i);
            String paymentAppInstanceId = generateRandomString(30);
            ByteArray encryptionKey = generateRandomKey(16);

            ByteArray encryptedPinBlock =
                    mCryptoServiceNative.encryptPinBlock(pin, paymentAppInstanceId, encryptionKey);

            ByteArray decryptedPin =
                    retrievePinFromPlainPinFormat(
                            CryptoServiceImpl.INSTANCE.decryptPinBlock(encryptedPinBlock,
                                                                       paymentAppInstanceId,
                                                                       encryptionKey));
            assertEquals(pin.toHexString(), decryptedPin.toHexString());
        }
    }

    public void test_encryptPinBlock_stress() throws Exception {
        for (int k = 0; k < 100; k++) {
            for (int i = 4; i <= 8; i++) {
                final ByteArray pin = generateRandomPin(i);
                String paymentAppInstanceId = generateRandomString(48);
                ByteArray encryptionKey = generateRandomKey(16);

                // Encrypt in Native
                ByteArray encryptedPinBlock = mCryptoServiceNative
                        .encryptPinBlock(pin, paymentAppInstanceId, encryptionKey);

                // Encrypt in Java
                ByteArray encryptedPinBlock2 = CryptoServiceImpl.INSTANCE
                        .encryptPinBlock(pin, paymentAppInstanceId, encryptionKey);

                // Decrypt in Java (Native encoding)
                ByteArray decrypted_block = CryptoServiceImpl.INSTANCE
                        .decryptPinBlock(encryptedPinBlock, paymentAppInstanceId, encryptionKey);
                ByteArray decryptedPin = retrievePinFromPlainPinFormat(decrypted_block);

                // Decrypt in Java (Java encoding)
                ByteArray decrypted_block2 = CryptoServiceImpl.INSTANCE
                        .decryptPinBlock(encryptedPinBlock2, paymentAppInstanceId, encryptionKey);
                ByteArray decryptedPin2 = retrievePinFromPlainPinFormat(decrypted_block2);

                // Check that both encoding/decoding have been successful
                assertEquals(pin.toHexString(), decryptedPin.toHexString());
                assertEquals(pin.toHexString(), decryptedPin2.toHexString());
            }
        }
    }

    public void test_sha1() throws Exception {
        ByteArray data =
                ByteArray.of("22000000000500000000000000005600200000000978140719005645609722000000"
                             + "000000000000003F00029F2701809F360200019F10120014A54000040000000000"
                             + "000000000000FF");
        ByteArray expected = ByteArray.of("8CF7E498460139BDDC8B006524754DEA982F43CD");
        ByteArray actual = mCryptoServiceNative.sha1(data);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_sha256() throws Exception {
        ByteArray data = ByteArray.of("333535343730303631303434353231");
        ByteArray expected = ByteArray.of("23F8975FCC161F4E1E0EB4CB447C2C1A4112AE6C93851CCA785A8"
                                          + "C017EB91B35");
        ByteArray actual = mCryptoServiceNative.sha256(data);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_buildServiceRequest() throws Exception {
        ByteArray requestData =
                ByteArray.of("7B22726571756573744964223A223230313531303031313933373236353038222C227"
                             + "46F6B656E556E697175655265666572656E6365223A2231646363646134362D3761"
                             + "38622D346366382D613239382D313137306637653665316162227D");
        ByteArray macKey = ByteArray.of("5D5A9FD4B28E3995DA3C28424A5CDC03");
        ByteArray transportKey = ByteArray.of("72899A9B37904E4CF1C628F8A9540DBE");
        ByteArray sessionCode =
                ByteArray.of("6000CDDF7D6A3F746AFCF82E45563EB282825B752EEB78D301EC151001");
        final int counter = 1;
        ByteArray expected = ByteArray.of("000001662C9FD5086B11ABF1D7F06F0AF5CA04039B13C1A319D4FEA5"
                                          + "53477FBFF9186A0D62439FF6436F39096AA0132CDC19DE5760EA43"
                                          + "E301F96290A95BA972C12A36BC8F0F0B99A1746372954BF42F1FD8"
                                          + "B320A8157ED83DEC0A8310C4A60E913B4C3F7F1886009E9E");
        ByteArray actual = mCryptoServiceNative.buildServiceRequest(requestData,
                                                                    macKey,
                                                                    transportKey,
                                                                    sessionCode,
                                                                    counter);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_decryptServiceResponse() throws Exception {
        ByteArray responseData =
                ByteArray.of("0000026370BC58DB654595E01447B407D46CCDCC30B0DFAA5E110AF37B79431A49C54"
                             + "F25D376310F2BDF13B9291A9CA1121B180619AF733BC13DADE68ACAD1A301B46B2D"
                             + "0A59095681AA4BAF78A90905069922272ACB18A5803ADC357FD0B88EEBEE087B0B0"
                             + "E6C44B4BDCF070D64B4C9B81B7E384C36DDBC61E5E1C54F6E9ADA3163A49230AD38"
                             + "42C1DA8D");
        ByteArray macKey = ByteArray.of("5D5A9FD4B28E3995DA3C28424A5CDC03");
        ByteArray transportKey = ByteArray.of("72899A9B37904E4CF1C628F8A9540DBE");
        ByteArray sessionCode =
                ByteArray.of("6000CDDF7D6A3F746AFCF82E45563EB282825B752EEB78D301EC151001");
        ByteArray expected =
                ByteArray.of("7B226572726F72436F6465223A6E756C6C2C226572726F724465736372697074696F6"
                             + "E223A6E756C6C2C22726573706F6E7365486F7374223A22687474703A2F2F313732"
                             + "2E32302E31302E333A383038302F636D732F6D646573222C22726573706F6E73654"
                             + "964223A223230313531303031313933373236383931227D800000");
        ByteArray actual = mCryptoServiceNative.decryptServiceResponse(responseData,
                                                                       macKey,
                                                                       transportKey,
                                                                       sessionCode);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_calculateAuthenticationCode() throws Exception {
        ByteArray mobileKeySetId =
                ByteArray.of("31376661643036622D613631342D346333612D393661612D373062316635636437346"
                             + "638");
        ByteArray deviceFingerPrint =
                ByteArray.of("6000CDDF7D6A3F746AFCF82E45563EB282825B752EEB78D301EC151001");
        ByteArray sessionCode =
                ByteArray.of("FC01C2ABF68FEB468C8B0914CF64FA9CE8C4F0C14BD73AAB453588A35506113E");
        ByteArray expected =
                ByteArray.of("D0680967BD71EEE3C5C4F01B9CB12737198043F8AC242CB3FDDFA4493DAB982A");
        ByteArray actual = mCryptoServiceNative.calculateAuthenticationCode(mobileKeySetId,
                                                                            deviceFingerPrint,
                                                                            sessionCode);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_decryptNotificationData() throws Exception {
        ByteArray responseData = ByteArray.of("28B5062CB55C442E9A4DAB440D18ADF33DDD186379218B5BC"
                                              + "A59B5163FC8BAF5AAA5E14860F422968058ECF8556FCAB1"
                                              + "021AA11AF24A412F079A2E70D63DA64FAE65A38C25B2686"
                                              + "3D3838351C63EA078CA644217E9F9083D3FA4A723C768EE"
                                              + "0AAC295FCEFC392CDC8E4097651D1784D00E317BCE764CA"
                                              + "D71EED5B7A01981B5ABA44817EAF2FAEFED91566AE5C96A"
                                              + "0ED6CA5F207B7C032AFC551F6C74F6B5E5619EFC86BDC3B"
                                              + "07F319C2795944BE05773DAD2774515DAC744D987FCDEAD"
                                              + "546A2C10D682A5C712114F83CF87F84B5A43897135359D3"
                                              + "ACF0D20BD9B980F6E4A15E3B4F1377BD60952D313B7728F"
                                              + "EAC863BC6176100B725A2081C9977595360B3A447F15905"
                                              + "41B90AEB0B23179DF30101F107BD35DCA61C90107");
        ByteArray macKey = ByteArray.of("5D5A9FD4B28E3995DA3C28424A5CDC03");
        ByteArray transportKey = ByteArray.of("72899A9B37904E4CF1C628F8A9540DBE");
        ByteArray expected = ByteArray.of("71CECF906D88E6947D0C426890F2728C7B2265787069727954696D65"
                                          + "7374616D70223A22323031352D31302D30315432303A30373A3236"
                                          + "2E3532382B30313030222C2270656E64696E67416374696F6E223A"
                                          + "2250524F564953494F4E222C2273657373696F6E436F6465223A22"
                                          + "363030304344444637443641334637343641464346383245343535"
                                          + "363345423238323832354237353245454237384433303145433135"
                                          + "31303031222C22746F6B656E556E697175655265666572656E6365"
                                          + "223A2231646363646134362D376138622D346366382D613239382D"
                                          + "313137306637653665316162222C2276616C6964466F725365636F"
                                          + "6E6473223A36302C2276657273696F6E223A223031227D");
        ByteArray actual = mCryptoServiceNative.decryptNotificationData(responseData,
                                                                        macKey,
                                                                        transportKey);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_encryptRandomGeneratedKey() throws Exception {
        // Generate a random key to be encrypted
        ByteArray randomGeneratedKey = generateRandomKey(16);

        // Generate a random RSA key pair
        KeyPair kp = generateRandomRsaKeyPair(2048);
        PrivateKey privateKey = kp.getPrivate();
        PublicKey publicKey = kp.getPublic();

        // Encrypt in Native/C++
        ByteArray encryptedData = mCryptoServiceNative
                .encryptRandomGeneratedKey(randomGeneratedKey,
                                           ByteArray.of(publicKey.getEncoded()));
        // Decrypt in Java
        ByteArray decryptedKey = decryptRandomGeneratedKey(encryptedData, privateKey);

        // Check that the decrypted PIN matches the original one
        assertEquals(ByteArray.of(randomGeneratedKey).toHexString(), decryptedKey.toHexString());
    }

    public void test_encryptRetryRequestData() throws Exception {
        ByteArray data =
                ByteArray.of("7B22636172644964223A2231646363646134362D376138622D346366382D613239382"
                             + "D313137306637653665316162222C226D65746144617461223A227B5C7530303232"
                             + "636172644964656E7469666965725C75303032323A5C75303032323164636364613"
                             + "4362D376138622D346366382D613239382D3131373066376536653161625C753030"
                             + "32322C5C75303032326D4452657175657374456E756D5C75303032323A5C7530303"
                             + "23250524F564953494F4E5C75303032322C5C75303032326D6F62696C654B657953"
                             + "657449645C75303032323A5C753030323231376661643036622D613631342D34633"
                             + "3612D393661612D3730623166356364373466385C75303032322C5C753030323270"
                             + "61796D656E74417070496E7374616E636549645C75303032323A5C7530303232376"
                             + "36566383832352D303166632D343832392D626662642D3737653865663161616437"
                             + "3863633937343438622D3662665C75303032322C5C75303032327061796D656E744"
                             + "1707050726F766964657249645C75303032323A5C75303032326237303662663365"
                             + "2D613236642D343138312D383736652D37653762316565363934323235363538646"
                             + "237622D653930662D346637332D396438662D653433385C75303032322C5C753030"
                             + "323274696D655374616D705C75303032323A313434333732343634363438337D222"
                             + "C227265717565737454797065223A2250524F564953494F4E222C22726574727943"
                             + "6F756E74223A317D");
        ByteArray key = ByteArray.of("C129682A37061084F10D4B2C608C1544");
        ByteArray expected = ByteArray.of("F4A7B0546078450DE243A0A5F26355C941CCD3B24829F0CA8A38AF6F"
                                          + "6873B65DA8474A5E84E49AFCE6390DD6CEA057D440848C680625DD"
                                          + "BFA2EB6643BB84362C206F4EFC24B545D7B9D0F6FAEDCE6E0AA9F4"
                                          + "4D9DF7EB445BAF6202AB010D714DBA7651D15844F642844D9FF8CE"
                                          + "B37BEB9F97F863DF1B4757F840BBEFC5DB96CF103E0B9D571DE52C"
                                          + "9957CE8780A0654D0BBE6562FF390DC7C5D0643A1319AFF0EA7258"
                                          + "64F1D4AA5C47FBB8AF401BAB39DA11A6774BC2BC46684C8C20A75D"
                                          + "44446191CC3C9EE6A4534C22A4071A1FF0948F6696C672C9013BED"
                                          + "F87FB94C84FB696E18582771E37B399EBAADCE1C04C44DCF14EC4E"
                                          + "4FA88C09734328A2BF34350428E64767EF7B056FAFC11734E1BC45"
                                          + "72BBC64E76BA109FE22140B78F0D65BF188DA50AE61A688060A085"
                                          + "5170606DA786E588DF840215D519B2BB01C8DB9C7B747ACDCFDCA3"
                                          + "B0B007810FF500282CCD95384D3584B4A1BE3B00DCC265BC611109"
                                          + "4198EDC38D9B6B0CFE9EA530CE919A0DE1656473B9E9D668A9B5E2"
                                          + "655CFF9572A7A5EB74843F005DD15A0E75B5DC66D5E179BEA9C545"
                                          + "8C9CB1394ED32C7CE442865C55726FC15F1566513A7D4A1B1A837A"
                                          + "19EFA1B185CD704224A7D4C597907E1404CF534966E1997CCF2D63"
                                          + "FB27A7F527884AB3C59E0C14AEC84889CD9AFD5249B8A6B93999C2"
                                          + "75A6F1972BE336EA74DCF4488195045F02A5BABC9867206CF5E62D"
                                          + "F189AF9E68AF71BEDA39784BDCCFCDEA0D6D41F9458130F5D8E992"
                                          + "75F1ED437E929F1DDBAE0A7E77E1E8178E43F1");
        ByteArray actual = mCryptoServiceNative.encryptRetryRequestData(data, key);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_decryptRetryRequestData() throws Exception {
        ByteArray data = ByteArray.of("F4A7B0546078450DE243A0A5F26355C941CCD3B24829F0CA8A38AF6F"
                                      + "6873B65DA8474A5E84E49AFCE6390DD6CEA057D440848C680625DD"
                                      + "BFA2EB6643BB84362C206F4EFC24B545D7B9D0F6FAEDCE6E0AA9F4"
                                      + "4D9DF7EB445BAF6202AB010D714DBA7651D15844F642844D9FF8CE"
                                      + "B37BEB9F97F863DF1B4757F840BBEFC5DB96CF103E0B9D571DE52C"
                                      + "9957CE8780A0654D0BBE6562FF390DC7C5D0643A1319AFF0EA7258"
                                      + "64F1D4AA5C47FBB8AF401BAB39DA11A6774BC2BC46684C8C20A75D"
                                      + "44446191CC3C9EE6A4534C22A4071A1FF0948F6696C672C9013BED"
                                      + "F87FB94C84FB696E18582771E37B399EBAADCE1C04C44DCF14EC4E"
                                      + "4FA88C09734328A2BF34350428E64767EF7B056FAFC11734E1BC45"
                                      + "72BBC64E76BA109FE22140B78F0D65BF188DA50AE61A688060A085"
                                      + "5170606DA786E588DF840215D519B2BB01C8DB9C7B747ACDCFDCA3"
                                      + "B0B007810FF500282CCD95384D3584B4A1BE3B00DCC265BC611109"
                                      + "4198EDC38D9B6B0CFE9EA530CE919A0DE1656473B9E9D668A9B5E2"
                                      + "655CFF9572A7A5EB74843F005DD15A0E75B5DC66D5E179BEA9C545"
                                      + "8C9CB1394ED32C7CE442865C55726FC15F1566513A7D4A1B1A837A"
                                      + "19EFA1B185CD704224A7D4C597907E1404CF534966E1997CCF2D63"
                                      + "FB27A7F527884AB3C59E0C14AEC84889CD9AFD5249B8A6B93999C2"
                                      + "75A6F1972BE336EA74DCF4488195045F02A5BABC9867206CF5E62D"
                                      + "F189AF9E68AF71BEDA39784BDCCFCDEA0D6D41F9458130F5D8E992"
                                      + "75F1ED437E929F1DDBAE0A7E77E1E8178E43F1");
        ByteArray key = ByteArray.of("C129682A37061084F10D4B2C608C1544");
        ByteArray expected =
                ByteArray.of("7B22636172644964223A2231646363646134362D376138622D346366382D613239382"
                             + "D313137306637653665316162222C226D65746144617461223A227B5C7530303232"
                             + "636172644964656E7469666965725C75303032323A5C75303032323164636364613"
                             + "4362D376138622D346366382D613239382D3131373066376536653161625C753030"
                             + "32322C5C75303032326D4452657175657374456E756D5C75303032323A5C7530303"
                             + "23250524F564953494F4E5C75303032322C5C75303032326D6F62696C654B657953"
                             + "657449645C75303032323A5C753030323231376661643036622D613631342D34633"
                             + "3612D393661612D3730623166356364373466385C75303032322C5C753030323270"
                             + "61796D656E74417070496E7374616E636549645C75303032323A5C7530303232376"
                             + "36566383832352D303166632D343832392D626662642D3737653865663161616437"
                             + "3863633937343438622D3662665C75303032322C5C75303032327061796D656E744"
                             + "1707050726F766964657249645C75303032323A5C75303032326237303662663365"
                             + "2D613236642D343138312D383736652D37653762316565363934323235363538646"
                             + "237622D653930662D346637332D396438662D653433385C75303032322C5C753030"
                             + "323274696D655374616D705C75303032323A313434333732343634363438337D222"
                             + "C227265717565737454797065223A2250524F564953494F4E222C22726574727943"
                             + "6F756E74223A317D");

        ByteArray actual = mCryptoServiceNative.decryptRetryRequestData(data, key);
        assertEquals(expected.toHexString(), actual.toHexString());
    }

    public void test_DecryptMobileKeys() throws Exception {
        ByteArray encryptedMacKey = ByteArray.of("E34FABC624529C2FFFA123A0BC72D47A");
        ByteArray encryptedTransportKey = ByteArray.of("30C23072209A533D12D23814E4D2FC1E");
        ByteArray encryptedDataEncryptionKey = ByteArray.of("1CCC5318EBCDEC5FFED5DC05460B040A");
        ByteArray decryptionKey = ByteArray.of("CA101D05F4EE6AB65D2764ED5D31310E");
        ByteArray expected1 = ByteArray.of("72899A9B37904E4CF1C628F8A9540DBE");
        ByteArray expected2 = ByteArray.of("5D5A9FD4B28E3995DA3C28424A5CDC03");
        ByteArray expected3 = ByteArray.of("C129682A37061084F10D4B2C608C1544");

        CryptoService.MobileKeys mobileKeys =
                mCryptoServiceNative.decryptMobileKeys(encryptedMacKey.getBytes(),
                                                       encryptedTransportKey.getBytes(),
                                                       encryptedDataEncryptionKey.getBytes(),
                                                       decryptionKey.getBytes());
        assertEquals(expected1.toHexString(),
                     ByteArray.of(mobileKeys.getTransportKey()).toHexString());
        assertEquals(expected2.toHexString(),
                     ByteArray.of(mobileKeys.getMacKey()).toHexString());
        assertEquals(expected3.toHexString(),
                     ByteArray.of(mobileKeys.getDataEncryptionKey()).toHexString());
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

    private static KeyPair generateRandomRsaKeyPair(final int keySizeInBits) {

        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to find RSA algorithms: " + e.getMessage());
        }

        // Initialize the key, but pass the size in bits
        kpg.initialize(keySizeInBits);
        return kpg.genKeyPair();
    }

    private static ByteArray decryptRandomGeneratedKey(final ByteArray data, PrivateKey privateKey)
            throws McbpCryptoException {
        final byte[] result;
        try {
            // Please refer to http://stackoverflow.com/questions/32161720/breaking-down-rsa-ecb-oaepwithsha-256andmgf1padding
            // information on the need to provide parameters during decryption
            Cipher oaepFromInit = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1",
                                                                 new MGF1ParameterSpec("SHA-256"),
                                                                 PSource.PSpecified.DEFAULT);
            oaepFromInit.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
            result = oaepFromInit.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                NoSuchPaddingException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new McbpCryptoException(e.getMessage());
        }
        final ByteArray decryptedData = ByteArray.of(result);
        Utils.clearByteArray(result);  // We need to clean up temporary variables
        return decryptedData;
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