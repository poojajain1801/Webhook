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

package com.mastercard.mcbp.remotemanagement.file.credentials;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.remotemanagement.file.TestKeyStore;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * basic test for reading CMS-C Card Profile and SUKs from file
 */
public class CredentialsDataMdesCmsCTest {
        // Test value for the digitized card id
        public static String DIGITIZED_CARD_ID = "5455012000100135FFFF01150318220416";

        @Before
        public void setUp() throws Exception {
                // Load the test keys
                TestKeyStore.addKey("1f27882afffc3ddc15233e46c7414196303527f2",
                        "404142434445464748494A4B4C4D4E4F");
                TestKeyStore.addKey("5455012000100135-pin", "1234");
                TestKeyStore.addKey("14a76feb000aa1e3690872f08854afdb4238afbd",
                        "53B66892AAD71B386DC645A9102D8628");
        }

    final static String mInputData = "{\n" +
            "  \"rawTransactionCredentials\": [\n" +
            "    {\n" +
            "      \"atc\": 8088,\n" +
            "      \"idn\": \"73D2E0111DDB3E570F0F2C8901AEA485\",\n" +
            "      \"contactlessMdSessionKey\": \"870ECB5223E4F09D8FE2544C5A59AFD6\",\n" +
            "      \"contactlessUmdSessionKey\": \"A0E53A5C4A52714E2BE5D46D884FFF5C\",\n" +
            "      \"dsrpMdSessionKey\": \"0E73032B652BFD5A2CD5DEDA95F1E82F\",\n" +
            "      \"dsrpUmdSessionKey\": \"7B147B892C9E330EEAFFBB97149BA038\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8090,\n" +
            "      \"idn\": \"3BFEFA0CB2846ABE5B530FC8AE3ECC15\",\n" +
            "      \"contactlessMdSessionKey\": \"9E1934D8058D88B63AED4C120AF92CBC\",\n" +
            "      \"contactlessUmdSessionKey\": \"0A656F1FACFF2B3E14AFEEF33024590A\",\n" +
            "      \"dsrpMdSessionKey\": \"F2D71FAC13CBB5F793F071592AFD2CAA\",\n" +
            "      \"dsrpUmdSessionKey\": \"2A285DC6783D137BF12A4377E44A0D99\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8092,\n" +
            "      \"idn\": \"B4E5ED5A3817950459F34BCFCC1E32BB\",\n" +
            "      \"contactlessMdSessionKey\": \"B7EFC9C00C0C0AAF6C8A5707DA8B2654\",\n" +
            "      \"contactlessUmdSessionKey\": \"07F8EF3013E225D3809874869B70F765\",\n" +
            "      \"dsrpMdSessionKey\": \"48CE4ED4C6A1D28EFBBD1C60A6BE5B86\",\n" +
            "      \"dsrpUmdSessionKey\": \"C2B8CED24DCFF4EF98099BCC33C6CA31\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8094,\n" +
            "      \"idn\": \"F8D66A4198F15CA343A9D2FEEE8F2CEF\",\n" +
            "      \"contactlessMdSessionKey\": \"F3B06DFDCD5E40AEBB56381A00409EA7\",\n" +
            "      \"contactlessUmdSessionKey\": \"D91F1A885D543B49667E3A0E2EC98601\",\n" +
            "      \"dsrpMdSessionKey\": \"3436E911B2CE4A419323D801A5583AF3\",\n" +
            "      \"dsrpUmdSessionKey\": \"2619567D33866AF2EEB5AD96A7F2731F\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8096,\n" +
            "      \"idn\": \"FADC20C6351F330AE1BEE3898F873D83\",\n" +
            "      \"contactlessMdSessionKey\": \"E761E58415D91C0F6317460BFE580416\",\n" +
            "      \"contactlessUmdSessionKey\": \"47CB737476A44053813AF0F558A68469\",\n" +
            "      \"dsrpMdSessionKey\": \"02DAC803397328D6CA66291194038963\",\n" +
            "      \"dsrpUmdSessionKey\": \"EE03D6897274F90F92FF19AF37B56C41\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8098,\n" +
            "      \"idn\": \"60CB0274614FFB353BBFB97CDE3AD7A0\",\n" +
            "      \"contactlessMdSessionKey\": \"4E7A9407CE99FCFA27080DF6215A61FE\",\n" +
            "      \"contactlessUmdSessionKey\": \"25D1F7B85777D9639EAFDE8D1857FF3F\",\n" +
            "      \"dsrpMdSessionKey\": \"0304118CEF443410B5B7494336CAD14C\",\n" +
            "      \"dsrpUmdSessionKey\": \"9E7AB1882E30493A1F7E83C080F153B5\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8100,\n" +
            "      \"idn\": \"CF8F0A6A4A01D6C50016971413C00DEB\",\n" +
            "      \"contactlessMdSessionKey\": \"6B437C63B7BF7B6A3A5CC39E1ED4588C\",\n" +
            "      \"contactlessUmdSessionKey\": \"FDE71E8BC3DC5F5726649F2D3A1A247D\",\n" +
            "      \"dsrpMdSessionKey\": \"0533DE05EF7A5FB54B98BD886892B9F8\",\n" +
            "      \"dsrpUmdSessionKey\": \"2E8D7B051DB85F429B7FD69B911B2110\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8102,\n" +
            "      \"idn\": \"D3EB5D5A6C7B170CE20FA874E7C27EEC\",\n" +
            "      \"contactlessMdSessionKey\": \"6FC1F5605D23C284B807223B235D6700\",\n" +
            "      \"contactlessUmdSessionKey\": \"49C7A0C3BF720D5D9736FC31558E4FF3\",\n" +
            "      \"dsrpMdSessionKey\": \"ABCC60D132C76EAFAD2C1A3982BA7C97\",\n" +
            "      \"dsrpUmdSessionKey\": \"82BA444CF527B9D089E48AD976143F68\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"atc\": 8104,\n" +
            "      \"idn\": \"EAD18793AEFB8B475B6F13B2E9438465\",\n" +
            "      \"contactlessMdSessionKey\": \"F6C8F639A07182E6DA7779930F5737B7\",\n" +
            "      \"contactlessUmdSessionKey\": \"FEA02847440580991500D858251BF381\",\n" +
            "      \"dsrpMdSessionKey\": \"0E09E1FE91281BD4B61A0114E57F3E68\",\n" +
            "      \"dsrpUmdSessionKey\": \"90A11CC2EBF2A8BA4AE0CB16821F59C8\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"kekId\": \"14a76feb000aa1e3690872f08854afdb4238afbd\"\n" +
            "}";
}