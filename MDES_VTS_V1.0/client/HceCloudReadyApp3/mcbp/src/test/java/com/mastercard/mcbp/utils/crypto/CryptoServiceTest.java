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

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static com.mastercard.mcbp.utils.crypto.CryptoService.Mode.DECRYPT;
import static com.mastercard.mcbp.utils.crypto.CryptoService.Mode.ENCRYPT;
import static org.junit.Assert.assertEquals;

/**
 * Test class for Crypto Library Default implementation (not the interfaces)
 */
public class CryptoServiceTest {
    private CryptoServiceImpl cryptoService = null;

    @Before
    public void setUp() throws Exception {
        cryptoService = CryptoServiceImpl.INSTANCE;
    }

    @Test
    public void testGetRandomByteArray() throws Exception {
        // Note: this test may in principle fail if two equal vectors are extracted
        for (int i = 4; i < 128; i++) {
            byte[] array1 = cryptoService.getRandomByteArray(i).getBytes();
            byte[] array2 = cryptoService.getRandomByteArray(i).getBytes();
            assertEquals(false, Arrays.equals(array1, array2));
        }
    }

    @Test
    public void testGetRandom() throws Exception {
        // Note: this test may in principle fail if two equal vectors are extracted
        for (int i = 4; i < 128; i++) {
            byte[] array1 = cryptoService.getRandom(i);
            byte[] array2 = cryptoService.getRandom(i);
            assertEquals(false, Arrays.equals(array1, array2));
        }
    }

    @Test
    public void testMac() throws Exception {
        String expectedResult = "FA95DA9AAB9AA3A5";
        String data = "00000000050000000000000000560020000000097814" +
                "071900564560971B800001A54000040000";
        String key = "705BBE83D11F421C0669A865163A4203";
        ByteArray result = cryptoService.mac(ByteArray.of(data), ByteArray.of(key));
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testMacSha256() throws Exception {
        String expectedResult = "5826A6E0457E615DE4B45E2AF39744B58C1A58778236F2469741291F70A047CA";
        String data = "6000952ACD68CF31C00D73D234404506C2C4A19CD1BA2FCC0064150331";
        String key = "F87AD315400CEDD311A7B7245D0A16F6";
        ByteArray result = cryptoService.macSha256(ByteArray.of(data), ByteArray.of(key));
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testDesEncrypt() throws Exception {
        String expectedResult = "FA95DA9AAB9AA3A5";
        String data = "931155403ACCFDC1";
        String key = "705BBE83D11F421C";
        ByteArray result = cryptoService.des(ByteArray.of(data), ByteArray.of(key), ENCRYPT);
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testDesDecrypt() throws Exception {
        String expectedResult = "931155403ACCFDC1";
        String data = "3ACB5C4CCC0538DD";
        String key = "0669A865163A4203";
        ByteArray result = cryptoService.des(ByteArray.of(data), ByteArray.of(key), DECRYPT);
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testDes3Encrypt() throws Exception {
        String expectedResult = "2187BD38351A17C680CE5E16C7252912836A3463B000415B751C498AE38501" +
                "7C71C840E306844DFE553F0E5ED791A7E652D4432A395F06E0A9BA36993490687308FD87643B01" +
                "1DD00B967341130EBEF6";
        String data = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF01234567" +
                "89ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF012345" +
                "6789ABCDEF";
        String key1 = "11223344556677889900AABBCCDDEEFF";
        String key2 = "11223344556677889900AABBCCDDEEFF1122334455667788";

        ByteArray result = cryptoService.des3(ByteArray.of(data), ByteArray.of(key1), ENCRYPT);
        assertEquals(expectedResult, result.toHexString());

        result = cryptoService.des3(ByteArray.of(data), ByteArray.of(key2), ENCRYPT);
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testDes3Decrypt() throws Exception {
        String expectedResult = "6569BD68F7AEA512644AF80F7E0568FD644AF80F7E0568FD644AF80F7E0568" +
                "FD644AF80F7E0568FD644AF80F7E0568FD644AF80F7E0568FD644AF80F7E0568FD644AF80F7E05" +
                "68FD644AF80F7E0568FD";
        String data = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF01234567" +
                "89ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF012345" +
                "6789ABCDEF";
        String key1 = "11223344556677889900AABBCCDDEEFF";
        String key2 = "11223344556677889900AABBCCDDEEFF1122334455667788";

        ByteArray result = cryptoService.des3(ByteArray.of(data), ByteArray.of(key1), DECRYPT);
        assertEquals(expectedResult, result.toHexString());

        result = cryptoService.des3(ByteArray.of(data), ByteArray.of(key2), DECRYPT);
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testAesEncrypt() throws Exception {
        //this uses 256 bit key and requires
        //Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
        //installed in the JRE
        String expectedResult = "EEB8CD3644E917506F99C84CF6D9D6F7077B393525D40A7DA25328A408922299";
        String data = "6D26A5B13998861A6FA297A1A43A5FA2B17B456BEE9916C21207DFCAFFD918E0";
        String key = "0A8B42F60DD285DB35B2212292642DAA72E3C1FDDF34EF64E426B97F9C92B681";

        ByteArray result = cryptoService.aesEcb(ByteArray.of(data), ByteArray.of(key), ENCRYPT);
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testAesDecrypt() throws Exception {
        //this uses 256 bit key and requires
        //Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
        //installed in the JRE
        String expectedResult = "687474703A2F2F3139322E3136382E302E31393A383038302F72656D6F746573" +
                "650F0F0F0F0F0F0F0F0F0F0F0F0F0F0F";
        String data = "D50B81213D0D28C5F10325A91C788A0E342670DAF69A4BBFEA8330C5FCCA48AC7C118F2025" +
                "D7091316D28F6CB1A78B59";
        String key = "0A8B42F60DD285DB35B2212292642DAA72E3C1FDDF34EF64E426B97F9C92B681";

        ByteArray result = cryptoService.aesEcb(ByteArray.of(data), ByteArray.of(key), DECRYPT);
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testAesCbcMac() throws Exception {
        String expectedResult = "690E7DC9DCB63080";
        String data = "58343B675C6DADD39E7764C168B359D200A08B9F4E4CFA8CE866257547D9BEA2";
        String key = "5B40833A98A79BC3E02A8E2C8BD51E74";

        ByteArray result = cryptoService.aesCbcMac(ByteArray.of(data), ByteArray.of(key));
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testSha1() throws Exception {
        String expectedResult = "8CF7E498460139BDDC8B006524754DEA982F43CD";
        String data = "22000000000500000000000000005600200000000978140719005645609722000000000000" +
                "000000003F00029F2701809F360200019F10120014A54000040000000000000000000000FF";
        assertEquals(expectedResult, cryptoService.sha1(ByteArray.of(data)).toHexString());
    }

    @Test
    public void testSha256() throws Exception {
        String expectedResult = "0145EA389861388D27BB83107FC326633E22D0A6E6A759DE733B482F179ED0B3";
        String data = "20E6B5D14A471947A51006C89B2E9DFAADFD0AD63B115DC9E68892E2AD77B3BA60ACB4E8A" +
                "43A50282A64D5000000";
        assertEquals(expectedResult, cryptoService.sha256(ByteArray.of(data)).toHexString());
    }

    @Test
    public void testAesWithPadding() throws Exception {
        // Encryption
        ByteArray input = ByteArray.of("AABBCCDDEEFF00112233445566");
        ByteArray expectedOutput = ByteArray.of("86C2C4BBC7BC004145B2FB0E76023D59");
        ByteArray key = ByteArray.of("11223344556677889900AABBCCDDEEFF");
        ByteArray output = cryptoService.aesEcbWithPadding(input, key, ENCRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());

        // Decryption
        input = ByteArray.of("86C2C4BBC7BC004145B2FB0E76023D59");
        expectedOutput = ByteArray.of("AABBCCDDEEFF00112233445566");
        output = cryptoService.aesEcbWithPadding(input, key, DECRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());
    }

    @Test
    public void testAesWithPadding2() throws Exception {
        // Encryption
        ByteArray input = ByteArray.of("AABBCCDDEEFF001122334455667788");
        ByteArray expectedOutput = ByteArray.of("7A31E184611ADBC612B85762C71AE890");
        ByteArray key = ByteArray.of("11223344556677889900AABBCCDDEEFF");
        ByteArray output = cryptoService.aesEcbWithPadding(input, key, ENCRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());

        // Decryption
        input = ByteArray.of("7A31E184611ADBC612B85762C71AE890");
        expectedOutput = ByteArray.of("AABBCCDDEEFF001122334455667788");
        output = cryptoService.aesEcbWithPadding(input, key, DECRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());
    }

    @Test
    public void testAesWithPadding3() throws Exception {
        // Encryption
        ByteArray input = ByteArray.of("80808080808080808080808080808080");
        ByteArray expectedOutput =
                ByteArray.of("5E146F4BFDD61C802FA4FAC2A4D55EC5B5E6D9B693089894A96623E164CCBE9B");
        ByteArray key = ByteArray.of("11223344556677889900AABBCCDDEEFF");
        ByteArray output = cryptoService.aesEcbWithPadding(input, key, ENCRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());

        // Decryption
        input = ByteArray.of("5E146F4BFDD61C802FA4FAC2A4D55EC5B5E6D9B693089894A96623E164CCBE9B");
        expectedOutput = ByteArray.of("80808080808080808080808080808080");
        output = cryptoService.aesEcbWithPadding(input, key, DECRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());
    }

    @Test
    public void testAesWithPadding4() throws Exception {
        // Encryption
        ByteArray input = ByteArray.of("800000000000000000000000000000");
        ByteArray expectedOutput = ByteArray.of("EAB0E93176E062886851FA45EA447601");
        ByteArray key = ByteArray.of("11223344556677889900AABBCCDDEEFF");
        ByteArray output = cryptoService.aesEcbWithPadding(input, key, ENCRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());

        // Decryption
        input = ByteArray.of("EAB0E93176E062886851FA45EA447601");
        expectedOutput = ByteArray.of("800000000000000000000000000000");
        output = cryptoService.aesEcbWithPadding(input, key, DECRYPT);
        assertEquals(expectedOutput.toHexString(), output.toHexString());
    }

    @Test
    public void testEncryptServiceRequest() throws Exception {
        String expectedResult =
                "5AA8447EF7CB553EA6E6ACD149FD12023EA8690860D636AC9D8B13255E635C1410BA81259658EB" +
                        "9FBD08D5473AEDDD4A60626A934C218210150083DBE5B687EBB6CDC2BE4595D3D6C6C3" +
                        "AEEC11C5FDBF8F679288EDF8EF6AB2BD70737327E716CFC33C4562670DD0FA27CF822A" +
                        "9DC02AF6518E0EC57A01128F80185D24954DE4A1A3F31DD377356484B777076E84A48A" +
                        "FFB2656F5AEEF041393BA22BAD2D02A8FE678C030D9428A72686B60055F5A29C52C91B" +
                        "4A00AB21913A080874B9F1FEDA7C983D89752957DD6C78C2CAC65C1652BBA09FF54614" +
                        "DA51A04986214A2517AA5C5A765D279856BDA0855C6092CD534BC9053A22F3AC63B393" +
                        "AF8C31E42C85F0308613AEEE2D423A45925CD8E00FC4ABBEF8E4455300685DE2D3E5EE" +
                        "85B9EB594FAE11576DD51D4AADE78800E885F33C2D64471BC15D7735D886B4E5104D90" +
                        "B0146392F00CD79B26153B2C5CCFC92C70A569EFEC0FCFF1FA11B359E75582D52A2462" +
                        "F404D8EBFCD8756AD5C9C449B151EA38D669F68B08723A6119D68B7FDA482F5D81DB68" +
                        "A5266B2D448989E5CC052790942710B85F4D69FFD398C60C4F384C800F290190446396" +
                        "1B48C75B3968871D4D7C3052836CEC48677278D0717038C620A55336ECCE9E6507DF37" +
                        "B04497CDBB09CD774A62F5A1C82F420EA03C568D51240CC83EA1A263831E949E4644AA" +
                        "53173E62C48D64B2B05F069590CED021B7E8A0AF8D96A19F32F2B0C487C03DADAB";

        String data = "7B227365727669636544617461223A227B5C2244435F53554B5F434F4E54454E" +
                "545C223A7B5C224154435C223A5C22303030315C222C5C2249444E5C223A5C224441393834333836" +
                "36374338464443325C222C5C225246555C223A5C2230305C222C5C22534B5F434C5F4D445C223A5C" +
                "2232323542433845383645443831413030463943463943373441363635334244355C222C5C22534B" +
                "5F52505F4D445C223A5C224538453438364633383443384631463844354544303230453033353339" +
                "3144385C222C5C2253554B496E666F5C223A5C2233385C222C5C2253554B5F434C5F554D445C223A" +
                "5C2246433234414634304441304432453846354138334437393333434635323142365C222C5C2253" +
                "554B5F52505F554D445C223A5C223446393832464246323138364234413746383243343543323543" +
                "3045323136445C222C5C22686173685C223A5C223031423035353232423132443042423141363132" +
                "31354637363244343030353638364343353736394132354545313631313945443239314432454539" +
                "363531425C227D2C5C2244435F53554B5F49445C223A5C2235343133333339303030303031353133" +
                "464646463030313530333834303930333238303030313135303332365C227D222C22736572766963" +
                "654944223A2250524F564953494F4E53554B222C2273657276696365526571756573744944223A22" +
                "31343237323935353133313735227D";

        String key = "21AF8FC26DAAF8A23D1F2438ED996F1F";

        int counters = 8498;

        ByteArray result = cryptoService.encryptServiceRequest(
                ByteArray.of(data),
                ByteArray.of(key),
                counters);
        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testDecryptServiceRequest() throws Exception {
        String expectedResult = "7B227365727669636544617461223A227B5C2244435F53554B5F434F4E54454E" +
                "545C223A7B5C224154435C223A5C22303030315C222C5C2249444E5C223A5C224441393834333836" +
                "36374338464443325C222C5C225246555C223A5C2230305C222C5C22534B5F434C5F4D445C223A5C" +
                "2232323542433845383645443831413030463943463943373441363635334244355C222C5C22534B" +
                "5F52505F4D445C223A5C224538453438364633383443384631463844354544303230453033353339" +
                "3144385C222C5C2253554B496E666F5C223A5C2233385C222C5C2253554B5F434C5F554D445C223A" +
                "5C2246433234414634304441304432453846354138334437393333434635323142365C222C5C2253" +
                "554B5F52505F554D445C223A5C223446393832464246323138364234413746383243343543323543" +
                "3045323136445C222C5C22686173685C223A5C223031423035353232423132443042423141363132" +
                "31354637363244343030353638364343353736394132354545313631313945443239314432454539" +
                "363531425C227D2C5C2244435F53554B5F49445C223A5C2235343133333339303030303031353133" +
                "464646463030313530333834303930333238303030313135303332365C227D222C22736572766963" +
                "654944223A2250524F564953494F4E53554B222C2273657276696365526571756573744944223A22" +
                "31343237323935353133313735227D";
        String data = "61F453C70239AA6C098A920F9639A82D5D17B0FDCDADCB3031DA83D1EC67194A932947B4FB" +
                "F7E625674B0D7EB067E386E245D32A3F34D61DEB0FF7B0C4A6059EACE4D21E04501C30D2B9AEAF09" +
                "7ED77A3DD3A2976985AB0DB185437F0C1E56F6005A98E40149F8CA88A2D76FC8B7B2563399B4BA2B" +
                "630E9842C15B549B15EF3BB86FB1C6119D4D3165A2E5A41A1184837F0CAE83085649E860D553A214" +
                "016B841500EC4A99C85F6EBF58166577E6E66580893C11921FC3E51FBAA9077A79454D91CB6F3872" +
                "06DCCD307390EDF28E03A6EA9FC4787864259DBEA5298430CE7BC26AA855EA4321C5ABEC954E6DB4" +
                "B8508B928141C4C88F8AF9F90FD0518895CF7F253C94DB2EE2ABCB14990338135F3D811C2D046663" +
                "3159365636C7D93CAB72A91FE2A30E0EE0BF54CA8498AEAC7F16DFEE9D398E4A30EF6DFF7CC421BA" +
                "142B67009655549388A1F3115247BB4C4F9004494624E2EB7027C27C8B176F55CB89A257063C193E" +
                "03226C01ABDE7787476072D4E6894520116E198C62FE79456635F1B3D17128C2523B6306E1CBDFF4" +
                "CF129930D1B3FE8E6B2205F32253448D7183A6EA4BEBB2D5F1E4AAE900FAE44B54BECDE59B08DA4D" +
                "FF5CE80E25A0C8EE161023840A0AD3B0BFCBBA734D1B13CC6157A03E4F70DA77F4E87A1DC5EC1F66" +
                "727E21157F07C0AA94288A36C1B5F3FAABA4B5C4D3570E4E01BE29AE02213AD81C91460D480933F2" +
                "1D5FF759415DA65B0CF5";
        String key = "21AF8FC26DAAF8A23D1F2438ED996F1F";
        int counters = 2;

        ByteArray result = cryptoService.decryptServiceResponse(
                ByteArray.of(data),
                ByteArray.of(key),
                counters);

        assertEquals(expectedResult, result.toHexString());
    }

    @Test
    public void testDeriveSessionKey() throws Exception {
        final ByteArray pin = ByteArray.of("31323334");
        final ByteArray singleUseKey1 = ByteArray.of("3E6B740FB26E0C3B6A0BA89EFA60D147");
        final ByteArray expectedSessionKey1 = ByteArray.of("5C0F1267B26E0C3B086FCEF6FA60D147");

        final ByteArray singleUseKey2 = ByteArray.of("3E6B740FB26E0C3B6A0BA89EFA60D147");
        final ByteArray expectedSessionKey2 = ByteArray.of("5C0F1267B26E0C3B086FCEF6FA60D147");

        final ByteArray singleUseKey3 = ByteArray.of("3E6B740FB26E0C3B6A0BA89EFA60D147");
        final ByteArray expectedSessionKey3 = ByteArray.of("5C0F1267B26E0C3B086FCEF6FA60D147");

        final ByteArray sessionKey1 = cryptoService.deriveSessionKey(singleUseKey1, pin);
        assertEquals(expectedSessionKey1.toHexString(), sessionKey1.toHexString());

        final ByteArray sessionKey2 = cryptoService.deriveSessionKey(singleUseKey2, pin);
        assertEquals(expectedSessionKey2.toHexString(), sessionKey2.toHexString());

        final ByteArray sessionKey3 = cryptoService.deriveSessionKey(singleUseKey3, pin);
        assertEquals(expectedSessionKey3.toHexString(), sessionKey3.toHexString());
    }

    @Test
    public void test_decryptServiceResponse() throws Exception {
        final byte[] serviceResponse = ByteArray.of
                ("0000026370BC58DB654595E01447B407D46CCDCC30B0DFAA5E110AF37B79431A49C54F25D37631"
                 + "0F2BDF13B9291A9CA1121B180619AF733BC13DADE68ACAD1A301B46B2D0A59095681AA4BAF78"
                 + "A90905069922272ACB18A5803ADC357FD0B88EEBEE087B0B0E6C44B4BDCF070D64B4C9B81B7E"
                 + "384C36DDBC61E5E1C54F6E9ADA3163A49230AD3842C1DA8D").getBytes();
        final byte[] macKey = ByteArray.of("5D5A9FD4B28E3995DA3C28424A5CDC03").getBytes();
        final byte[] transportKey = ByteArray.of("72899A9B37904E4CF1C628F8A9540DBE").getBytes();
        final byte[] sessionCode = ByteArray.of
                ("6000CDDF7D6A3F746AFCF82E45563EB282825B752EEB78D301EC151001").getBytes();
        final ByteArray expectedOutput =
                ByteArray.of("7B226572726F72436F6465223A6E756C6C2C226572726F72446573637269707469"
                             + "6F6E223A6E756C6C2C22726573706F6E7365486F7374223A22687474703A2F2F"
                             + "3137322E32302E31302E333A383038302F636D732F6D646573222C2272657370"
                             + "6F6E73654964223A223230313531303031313933373236383931227D800000");

        final byte[] decryptedResponse =
                cryptoService.decryptServiceResponse(serviceResponse, macKey,
                                                     transportKey, sessionCode);
        assertEquals(expectedOutput.toHexString(), ByteArray.of(decryptedResponse).toHexString());
    }
}

