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

package com.mastercard.mcbp.remotemanagement.file.profile;

import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.remotemanagement.file.TestKeyStore;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Simple test class for file profile wrapper
 */
public class CardProfileMdesCmsCTest {
        @Before
        public void setUp() throws Exception {
                // Load the test keys
                TestKeyStore.addKey("1f27882afffc3ddc15233e46c7414196303527f2",
                        "404142434445464748494A4B4C4D4E4F");
                TestKeyStore.addKey("14a76feb000aa1e3690872f08854afdb4238afbd",
                        "53B66892AAD71B386DC645A9102D8628");
        }

        final static String mExpectedCardProfileDecoded =  "{\n" +
                "  \"cardProfile\": {\n" +
                "    \"digitizedCardId\": \"5455012000100135FFFF01150318220416\",\n" +
                "    \"maximumPinTry\": 3,\n" +
                "    \"mppLiteModule\": {\n" +
                "      \"cardRiskManagementData\": {\n" +
                "        \"additionalCheckTable\": \"000000000000000000000000000000000000\",\n" +
                "        \"crmCountryCode\": \"0840\"\n" +
                "      },\n" +
                "      \"contactlessPaymentData\": {\n" +
                "        \"aid\": \"A0000000041010\",\n" +
                "        \"ppseFci\": \"6F39840E325041592E5359532E4444463031A527BF0C2461224F07A0" +
                "000000041010500A4D4153544552434152448701015F550255534203545501\",\n" +
                "        \"paymentFci\": \"8407A0000000041010A52D500A4D4153544552434152448701015" +
                "F2D02656E9F38099F1D089F1A029F3501BF0C0A9F6E0708400000313400\",\n" +
                "        \"gpoResponse\": \"770E82021B8094080801010010010301\",\n" +
                "        \"cdol1RelatedDataLength\": \"2D\",\n" +
                "        \"ciacDecline\": \"010008\",\n" +
                "        \"cvrMaskAnd\": \"FFFFFFFFFFFF\",\n" +
                "        \"issuerApplicationData\": \"0314000100000000000000000000000000FF\",\n" +
                "        \"iccPrivateKeyCrtComponents\": {\n" +
                "          \"p\": \"5949C737CA1C15D5E3C058915172C2107CE7B647A97B8951CD69DB747B550" +
                "0FA1ACCA257C1F2BAB830E4A3CCE4BE8877C6508098DCD86380FB23F8EE5C9C91EFC63AAA9B36438" +
                "F018CB1D67BE6B4DA66\",\n" +
                "          \"q\": \"88DCF2C4BC480F705B0FF721EF84E454B6DCD9F67DECF71517E11E24F5D51" +
                "65CE872DE405BF3240C21F7664A88EF74D777BADD7266E3867CAC9AD0ED6F8C4C78C63AAA9B36438" +
                "F018CB1D67BE6B4DA66\",\n" +
                "          \"dp\": \"AB1D7F51F2BFBBF034C4D92E4A8BD22BB8FCFA7A24FC043CA3726118E532" +
                "EEB924567FB55856E60A246FAD068AB9577A28948D608A13C7AC39048C9B40C227D1C63AAA9B3643" +
                "8F018CB1D67BE6B4DA66\",\n" +
                "          \"dq\": \"2BD5754F936F4F0F9F338505ECBDE894C5A46801B221585F5B85C851EF15" +
                "F0EBD1DD101E8DE96B56A2CA9DF5B3F3A4707452871F931A2793CE550E4753C9C20DC63AAA9B3643" +
                "8F018CB1D67BE6B4DA66\",\n" +
                "          \"u\": \"26F602A3C1C700A367FD4D2EA23D36319AFD656ECE1EBB74D36A11A0BDF6B" +
                "DF9C89EFB04DF8FD6B2C16725D1E968430222433262300C3672F4837B5D125885F3C63AAA9B36438" +
                "F018CB1D67BE6B4DA66\"\n" +
                "        },\n" +
                "        \"pinIvCvc3Track2\": \"1C88\",\n" +
                "        \"ciacDeclineOnPpms\": \"4100\",\n" +
                "        \"alternateContactlessPaymentData\": null,\n" +
                "        \"records\": [\n" +
                "          {\n" +
                "            \"recordNumber\": 1,\n" +
                "            \"sfi\": \"0C\",\n" +
                "            \"recordValue\": \"7081919F6C0200019F62060000000700009F630600000078F" +
                "0009F640104563442353435353031323030303130303133355E202F5E31383131323031313030303" +
                "0303030303030303030303030303030303030309F650200E09F66020F1E9F6B13545501200010013" +
                "5D18112011000000000000F9F6701049F69199F6A049F7E019F02065F2A029F1A029C019A039F150" +
                "29F3501\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"recordNumber\": 1,\n" +
                "            \"sfi\": \"14\",\n" +
                "            \"recordValue\": \"70819357135455012000100135D18112011000000000000F5" +
                "A0854550120001001355F24031811305F25034912315F280208405F3401018C249F02069F03069F1" +
                "A0295055F2A029A039C019F37049F35019F45029F4C089F34039F15028E0C00000000000000001F0" +
                "342039F070200009F080200029F0D0500600000009F0E0500000000009F0F0500600000009F42020" +
                "8409F4A0182\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"recordNumber\": 2,\n" +
                "            \"sfi\": \"14\",\n" +
                "            \"recordValue\": \"7081E08F01F19F32010392246F0744406ADD38E3D6E80BC30" +
                "20EC7D0352F07C5E8B97EF73DCBCC7F4897FFA0FE8B8DE39081B03FCF320DD61A2BA3BDC74DF4FA5" +
                "5F94B72BB8C5BF3174A2EAAB859E05BF02E852E88417616881316BA1030A05EB1F1A6405D05214C9" +
                "9B1FB71B0470BC553AF3F43282E86923305A82F61C5DEACAA5183925FE9F1481290213C930B1A9E6" +
                "B06DEDAD68A282FEAF6ABAF50C60BEC2C816FB3950BF24F4B9DC04A5A9BB3F9F7027FD044DA31B19" +
                "74B07A11A34638C59D05470EC07F83305A4917C677DDA06722ABF21E7BBDE0AD76D2C2CA53E64A2B" +
                "1E013\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"recordNumber\": 3,\n" +
                "            \"sfi\": \"14\",\n" +
                "            \"recordValue\": \"7081BB9F4701039F48009F4681B016DD0C76D66AFE69AEB6A" +
                "6BFE959DFBC5F5DB9B79C58D796F38FFF5BD9EF4E516D6137345203190B6B5D56F939E4508D21C4D" +
                "6CD0DB79D80E0A7C38C9D99018A660790AE28663F1A8C49E4583550E6CD38716533047AA44060198" +
                "EBF47A7AFCCB863408941DFA7D698F5583B50882F6339D221DAFF6DB53F720C2DE0A64F5EBBE30A6" +
                "0EDA11BF2C27D12BD69695B156755026301651805F903A7722A8DBDB754D3F559036D9CB17A4A380" +
                "640F1C44ECF\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"recordNumber\": 1,\n" +
                "            \"sfi\": \"1C\",\n" +
                "            \"recordValue\": \"70295A0854550120001001355F24031811305F34010157135" +
                "455012000100135D18112011000000000000F\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"remotePaymentData\": {\n" +
                "        \"track2Equivalent\": \"5455012000100135D18112011000000000000F\",\n" +
                "        \"pan\": \"5455012000100135\",\n" +
                "        \"panSequenceNumber\": \"01\",\n" +
                "        \"applicationExpiryDate\": \"181130\",\n" +
                "        \"aip\": \"1A00\",\n" +
                "        \"ciacDecline\": \"010008\",\n" +
                "        \"cvrMaskAnd\": \"FF0000000000\",\n" +
                "        \"issuerApplicationData\": \"0314000000000000000000000000000000FF\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"businessLogicModule\": {\n" +
                "      \"cvmResetTimeout\": 30,\n" +
                "      \"dualTapResetTimeout\": 30,\n" +
                "      \"applicationLifeCycleData\": null,\n" +
                "      \"cardLayoutDescription\": \"11018000\",\n" +
                "      \"securityWord\": \"00000000000000000000000000000000\",\n" +
                "      \"cardholderValidators\": [\n" +
                "        \"DEVICE_MOBILE_PIN\"\n" +
                "      ],\n" +
                "      \"mChipCvmIssuerOptions\": {\n" +
                "        \"ackAlwaysRequiredIfCurrencyNotProvided\": false,\n" +
                "        \"ackAlwaysRequiredIfCurrencyProvided\": false,\n" +
                "        \"ackAutomaticallyResetByApplication\": false,\n" +
                "        \"ackPreEntryAllowed\": false,\n" +
                "        \"pinAlwaysRequiredIfCurrencyNotProvided\": true,\n" +
                "        \"pinAlwaysRequiredIfCurrencyProvided\": true,\n" +
                "        \"pinAutomaticallyResetByApplication\": false,\n" +
                "        \"pinPreEntryAllowed\": true\n" +
                "      },\n" +
                "      \"magstripeCvmIssuerOptions\": {\n" +
                "        \"ackAlwaysRequiredIfCurrencyNotProvided\": false,\n" +
                "        \"ackAlwaysRequiredIfCurrencyProvided\": false,\n" +
                "        \"ackAutomaticallyResetByApplication\": false,\n" +
                "        \"ackPreEntryAllowed\": false,\n" +
                "        \"pinAlwaysRequiredIfCurrencyNotProvided\": true,\n" +
                "        \"pinAlwaysRequiredIfCurrencyProvided\": true,\n" +
                "        \"pinAutomaticallyResetByApplication\": false,\n" +
                "        \"pinPreEntryAllowed\": true\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"iccKek\": \"B663F8C5BADEFAD1B73E3FE43FC5806338731BF266190107AAF009E91B114FA3" +
                "AA3C13BE69C6FBFC968412C70F00F4F9\",\n" +
                "  \"kekId\": \"14a76feb000aa1e3690872f08854afdb4238afbd\"\n" +
                "}";
}