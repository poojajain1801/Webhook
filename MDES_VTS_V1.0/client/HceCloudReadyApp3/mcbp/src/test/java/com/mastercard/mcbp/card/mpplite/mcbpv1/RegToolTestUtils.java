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

package com.mastercard.mcbp.card.mpplite.mcbpv1;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.provisioning.simulator.core.credentials.SingleUseKeysBuilder;
import com.mastercard.mcbp.provisioning.simulator.core.crypto.CertificateStore;
import com.mastercard.mcbp.provisioning.simulator.core.profile.ProfileBuilder;
import com.mastercard.mcbp.remotemanagement.mdes.credentials.TransactionCredential;
import com.mastercard.mcbp.remotemanagement.mdes.credentials.TransactionCredentialContainer;
import com.mastercard.mcbp.remotemanagement.mdes.profile.DigitizedCardProfileMdes;
import com.mastercard.mcbp.remotemanagement.mdes.profile.DigitizedCardProfileMdesContainer;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import org.apache.commons.codec.binary.Hex;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to generate profiles based on Regtool xls files
 */
public enum RegToolTestUtils {
    INSTANCE;

    /**
     * Card Layout Descriptor Header
     */
    public final static String CLD_HEADER = "1101011201010137130A04";

    /**
     * Card Layout Descriptor Trailer
     */
    public final static String CLD_TRAILER = "160F1A10020003FFFFFF4578706972657316180807010003" +
                                             "FFFFFF4D5220412E2043415244484F4C4445520216131004626"
                                             + "1636B5F6261636B67726F756E64150" +
                                             "20300012C161B0817030004FFFFFF2A2A2"
                                             + "A2A202A2A2A2A202A2A2A2A202A2A2A2A160D3110010003" +
                                             "FFFFFF2A2A2F2A2A020D160B3E1E0540030000002A2A2A";

    /**
     * The Data Encryption Key used to build the profile and keys according to the CMS-D format
     */
    // public final static String DATA_ENCRYPTION_KEY = "00112233445566778899AABBCCDDEEFF";
    public final static String DATA_ENCRYPTION_KEY = "2AE441D0F6862DB34F04D58AEE79E8A4";
    // 402605EB4B3E3E72DB6D7AF498B5325BFF7D04FF89AF366213E475023F9E2E37

    /**
     * The data structure containing all the test profiles that this class is able to generate
     */
    private final static Map<String, DigitizedCardProfile> sProfiles = new HashMap<>();

    /**
     * Flag specifying whether or not the object has been initialized. We used a lazy initialization
     * for the list of profiles. We create all of them when the first profile is requested.
     */
    private static boolean sIsInitialized = false;

    /**
     * Generate a single use key of a given ATC for a given Digitized Card Id and a given mobile pin
     *
     * @param digitizedCardId The Digitized Card Id
     * @param mobilePin       The Mpbile Pin to be used to lock/unlock the UMD Keys
     * @param atc             The ATC for which those keys should be built
     * @return The Single Use Key built using the default Issuer Master Key
     */
    public static SingleUseKey getSingleUseKey(final String digitizedCardId,
                                               final String mobilePin,
                                               final int atc) {
        final SingleUseKeysBuilder singleUseKeysBuilder = new SingleUseKeysBuilder();
        singleUseKeysBuilder.forDigitizedCardId(digitizedCardId)
                            .forMobilePin(mobilePin)
                            .usingMdesCmsDedicatedOutput(DATA_ENCRYPTION_KEY);
        final String singleUseKeyAsJson = singleUseKeysBuilder.buildAsJson(atc);

        final String[] deserializeSukArrayJson =
                JsonUtils.deserializeStringArray(singleUseKeyAsJson);

        final TransactionCredential transactionCredential =
                TransactionCredential.valueOf(deserializeSukArrayJson[0].getBytes());

        TransactionCredentialContainer transactionCredentialContainer =
                new TransactionCredentialContainer(transactionCredential,
                                                   digitizedCardId,
                                                   ByteArray.of(DATA_ENCRYPTION_KEY));

        return transactionCredentialContainer.toSingleUseKey();
    }

    /**
     * Build a given profile using its name
     *
     * @param profileName The profile name according to the MPA Perso Profile v1.0.3 (xls doc)
     * @return The Person Profile as JSON String in CMS-D format using the DATA_ENCRYPTION_KEY
     */
    public static DigitizedCardProfile getProfileByName(final String profileName) {
        if (!sIsInitialized) {
            initialize();
        }
        return sProfiles.get(profileName);
    }

    /**
     * Build all the known profiles. If the logic for a new profile is added, please make sure it
     * is mapped to a string in the function below.
     */
    private static void initialize() {
        sIsInitialized = true;

        sProfiles.put("MasterCard_MCBP_PersoProfile_1",
                      getDigitizedCardProfile(getMasterCardMcbpPersoProfile1().buildAsJson()));

        sProfiles.put("MasterCard_MCBP_PersoProfile_3",
                      getDigitizedCardProfile(getMasterCardMcbpPersoProfile3().buildAsJson()));

        sProfiles.put("ref_176",
                      getDigitizedCardProfile(getRef176().buildAsJson()));

        sProfiles.put("ref_176_with_alternate_aid",
                      getDigitizedCardProfile(getRef176WithAlternate().buildAsJson()));

        sProfiles.put("ref_176_without_alternate_aid_and_without_dsrp",
                      getDigitizedCardProfile(
                              getRef176WithoutAlternateAndWithoutDsrp().buildAsJson()));

        sProfiles.put("ref_176_without_alternate_aid",
                      getDigitizedCardProfile(getRef176WithoutAlternate().buildAsJson()));

        sProfiles.put("ref_128",
                      getDigitizedCardProfile(getRef128().buildAsJson()));

        sProfiles.put("ref_96",
                      getDigitizedCardProfile(getRef96().buildAsJson()));

        sProfiles.put("profile_ccc_null",
                      getDigitizedCardProfile(getProfileCccNull().buildAsJson()));

        sProfiles.put("profile_ccc_no_domestic",
                      getDigitizedCardProfile(getProfileCccNoDomestic().buildAsJson()));

        sProfiles.put("profile_ccc_no_international",
                      getDigitizedCardProfile(getProfileCccNoInternational().buildAsJson()));

        sProfiles.put("profile_cl_01",
                      getDigitizedCardProfile(getProfileCl01().buildAsJson()));

        sProfiles.put("profile_cl_02",
                      getDigitizedCardProfile(getProfileCl02().buildAsJson()));

        sProfiles.put("profile_cl_03_sub_1",
                      getDigitizedCardProfile(getProfileCl03Sub1().buildAsJson()));

        sProfiles.put("profile_cl_03_sub_3",
                      getDigitizedCardProfile(getProfileCl03Sub3().buildAsJson()));

        sProfiles.put("profile_cl_03_sub_4",
                      getDigitizedCardProfile(getProfileCl03Sub4().buildAsJson()));

        sProfiles.put("profile_cl_03_sub_5",
                      getDigitizedCardProfile(getProfileCl03Sub5().buildAsJson()));

        sProfiles.put("profile_cl_03_sub_6",
                      getDigitizedCardProfile(getProfileCl03Sub6().buildAsJson()));

        sProfiles.put("profile_cl_04_sub_1_3",
                      getDigitizedCardProfile(getProfileCl04Sub1_3().buildAsJson()));

        sProfiles.put("profile_cl_05_sub_1",
                      getDigitizedCardProfile(getProfileCl05Sub1().buildAsJson()));

        sProfiles.put("profile_cl_05_sub_2",
                      getDigitizedCardProfile(getProfileCl05Sub2().buildAsJson()));

        sProfiles.put("profile_cl_05_sub_3",
                      getDigitizedCardProfile(getProfileCl05Sub3().buildAsJson()));

        sProfiles.put("profile_cl_05_sub_4",
                      getDigitizedCardProfile(getProfileCl05Sub4().buildAsJson()));

        sProfiles.put("profile_cl_06_sub_1",
                      getDigitizedCardProfile(getProfileCl06Sub1().buildAsJson()));

        sProfiles.put("profile_cl_06_sub_2",
                      getDigitizedCardProfile(getProfileCl06Sub2().buildAsJson()));

        sProfiles.put("profile_cl_06_sub_3",
                      getDigitizedCardProfile(getProfileCl06Sub3().buildAsJson()));

        sProfiles.put("profile_cl_06_sub_4",
                      getDigitizedCardProfile(getProfileCl06Sub4().buildAsJson()));

        sProfiles.put("profile_cl_09_p_1",
                      getDigitizedCardProfile(getProfileCl09P1().buildAsJson()));

        sProfiles.put("profile_cl_xy",
                      getDigitizedCardProfile(getProfileClxy().buildAsJson()));

        sProfiles.put("profile_cl_105",
                      getDigitizedCardProfile(getProfileCl105().buildAsJson()));

        sProfiles.put("profile_cl_110",
                      getDigitizedCardProfile(getProfileCl110().buildAsJson()));

        sProfiles.put("profile_cl_72",
                      getDigitizedCardProfile(getProfileCl72().buildAsJson()));

        sProfiles.put("mcbp_card_test_ref_176",
                      getDigitizedCardProfile(getMcbpCardTestRef176().buildAsJson()));

        sProfiles.put("mcbp_card_dsrp_test_1",
                      getDigitizedCardProfile(getProfileDsrpTest1().buildAsJson()));

        sProfiles.put("mcbp_card_dsrp_test_wrong_psn",
                      getDigitizedCardProfile(getProfileDsrpTestWithWrongPsn().buildAsJson()));
    }

    /**
     * Convert the JSON generated profile into the internal MPP Lite format for profiles
     *
     * @param profile The Profile to be converted as JSON string
     * @return The Digitized Card Profile ready to be used by the MPP Lite
     */
    private static DigitizedCardProfile getDigitizedCardProfile(final String profile) {
        final DigitizedCardProfileMdes cardProfileMdes = DigitizedCardProfileMdes.valueOf(profile);
        final ByteArray dataEncryptionKey =
                ByteArray.of(RegToolTestUtils.DATA_ENCRYPTION_KEY);

        final DigitizedCardProfileMdesContainer digitizedCardProfileMdesContainer =
                new DigitizedCardProfileMdesContainer(cardProfileMdes, dataEncryptionKey);

        return digitizedCardProfileMdesContainer.toDigitizedCardProfile();
    }

    /**
     * @return Profile CCC Null
     */
    private static ProfileBuilder getProfileCccNull() {
        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withUsMaestro()
                      .withRemotePaymentSupport()
                      .withContactlessCiacDeclineOnPpms("")
                      .withCrmCountryCode("0056")
                      .withIccCertificate(CertificateStore.getIccCertificate("A176"))
                      .withIssuerUrl("ref_176")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER);

        return profileBuilder;
    }

    /**
     * @return Profile CCC_noDomestic
     */
    private static ProfileBuilder getProfileCccNoDomestic() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withContactlessCiacDeclineOnPpms("0200")
                      .withIssuerUrl("ccc_no_domestic");

        return profileBuilder;
    }

    /**
     * @return Profile CCC_noInterntnal
     */
    private static ProfileBuilder getProfileCccNoInternational() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withContactlessCiacDeclineOnPpms("0400")
                      .withIssuerUrl("ccc_no_international");

        return profileBuilder;
    }

    /**
     * @return Profile CL.01
     */
    private static ProfileBuilder getProfileCl01() {
        final ProfileBuilder profileBuilder = getRef176WithoutAlternateAndWithoutDsrp();

        profileBuilder.withContactlessCiacDecline("040000")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl01");

        return profileBuilder;
    }

    /**
     * @return Profile CL.02
     */
    private static ProfileBuilder getProfileCl02() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withContactlessCiacDecline("FFFFFF")
                      .withContactlessCvrMaskAnd("000000000000")
                      .withAlternateContactlessCiacDecline("FBFFFC")
                      .withAlternateContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withDsrpCiacDecline("FFFFFF")
                      .withIssuerUrl("Cl02");

        return profileBuilder;
    }

    /**
     * @return Profile CL.03_Sub1
     */
    private static ProfileBuilder getProfileCl03Sub1() {
        final ProfileBuilder profileBuilder = getRef176WithoutAlternate();

        profileBuilder.withContactlessCiacDecline("040003")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withDsrpCiacDecline("FFFFFF")
                      .withIssuerUrl("Cl03Sub1");

        return profileBuilder;
    }

    /**
     * @return Profile CL.03_Sub3
     */
    private static ProfileBuilder getProfileCl03Sub3() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withAdditionalCheckTable("2A0502FFFFFFFF00000000FFFFFFFFFFFFFF")
                      .withContactlessCiacDecline("040003")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl03Sub3");

        return profileBuilder;
    }

    /**
     * @return Profile CL.03_Sub4
     */
    private static ProfileBuilder getProfileCl03Sub4() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withAdditionalCheckTable("1A0802FFFFFFFF00000000FFFFFFFFFFFFFF")
                      .withContactlessCiacDecline("040003")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl03Sub4");

        return profileBuilder;
    }

    /**
     * @return Profile CL.03_Sub5
     */
    private static ProfileBuilder getProfileCl03Sub5() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withAdditionalCheckTable("1A0002FFFFFFFF00000000FFFFFFFFFFFFFF")
                      .withContactlessCiacDecline("040003")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl03Sub5");

        return profileBuilder;
    }

    /**
     * @return Profile CL.03_Sub6
     */
    private static ProfileBuilder getProfileCl03Sub6() {
        final ProfileBuilder profileBuilder = getRef176WithoutAlternate();

        profileBuilder.withAdditionalCheckTable("1A0401FFFFFFFF00000000FFFFFFFFFFFFFF")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withContactlessCiacDecline("040003")
                      .withIssuerUrl("Cl03Sub6");

        return profileBuilder;
    }

    /**
     * @return Profile CL.04_Sub[1-3]
     */
    private static ProfileBuilder getProfileCl04Sub1_3() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCiacDecline("000000")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withDsrpCiacDecline("FFFFFF")
                      .withIssuerUrl("Cl04Sub1_3");

        return profileBuilder;
    }

    /**
     * @return Profile CL.05_Sub1
     */
    private static ProfileBuilder getProfileCl05Sub1() {
        final ProfileBuilder profileBuilder = getRef176WithoutAlternate();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCiacDecline("040000")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl05Sub1");

        return profileBuilder;
    }

    /**
     * @return Profile CL.05_Sub2
     */
    private static ProfileBuilder getProfileCl05Sub2() {
        final ProfileBuilder profileBuilder = getRef176WithoutAlternate();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCiacDecline("020000")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl05Sub2");

        return profileBuilder;
    }

    /**
     * @return Profile CL.05_Sub3
     */
    private static ProfileBuilder getProfileCl05Sub3() {
        final ProfileBuilder profileBuilder = getRef176WithoutAlternate();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCiacDecline("000001")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl05Sub3");

        return profileBuilder;
    }

    /**
     * @return Profile CL.05_Sub4
     */
    private static ProfileBuilder getProfileCl05Sub4() {

        final ProfileBuilder profileBuilder = getRef176WithoutAlternate();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCiacDecline("000002")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl05Sub4");

        return profileBuilder;
    }

    /**
     * @return Profile CL.06_Sub1
     */
    private static ProfileBuilder getProfileCl06Sub1() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCvrMaskAnd("000000000000")
                      .withAlternateContactlessCiacDecline("040000")
                      .withAlternateContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl06Sub1");

        return profileBuilder;
    }

    /**
     * @return Profile CL.06_Sub2
     */
    private static ProfileBuilder getProfileCl06Sub2() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCvrMaskAnd("000000000000")
                      .withAlternateContactlessCiacDecline("020000")
                      .withAlternateContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl06Sub2");

        return profileBuilder;
    }

    /**
     * @return Profile CL.06_Sub3
     */
    private static ProfileBuilder getProfileCl06Sub3() {
        final ProfileBuilder profileBuilder = getRef176WithAlternate();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCvrMaskAnd("000000000000")
                      .withAlternateContactlessCiacDecline("000001")
                      .withAlternateContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl06Sub3");

        return profileBuilder;
    }

    /**
     * @return Profile CL.06_Sub4
     */
    private static ProfileBuilder getProfileCl06Sub4() {
        final ProfileBuilder profileBuilder = getRef176WithAlternate();

        profileBuilder.withAdditionalCheckTable("01010FFF0102040810204080112233445566")
                      .withContactlessCvrMaskAnd("000000000000")
                      .withAlternateContactlessCiacDecline("000002")
                      .withAlternateContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withIssuerUrl("Cl06Sub4");

        return profileBuilder;
    }

    /**
     * @return Profile CL.09_P1
     */
    private static ProfileBuilder getProfileCl09P1() {
        final ProfileBuilder profileBuilder = getRef176();

        profileBuilder.withContactlessCiacDecline("FBFFFC")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withAlternateContactlessCiacDecline("FFFFFF")
                      .withAlternateContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withDsrpCiacDecline("FFFFFF")
                      .withIssuerUrl("Cl09P1");

        return profileBuilder;
    }

    /**
     * @return Profile CL.xy
     */
    private static ProfileBuilder getProfileClxy() {
        final ProfileBuilder profileBuilder = getRef176WithoutAlternate();

        profileBuilder.withContactlessCiacDecline("040003")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withDsrpCiacDecline("FFFFFF")
                      .withIssuerUrl("Clxy");

        return profileBuilder;
    }

    /**
     * @return Profile CL.105
     */
    private static ProfileBuilder getProfileCl105() {

        final ProfileBuilder profileBuilder = getRef176WithAlternate();

        profileBuilder.withContactlessCiacDecline("FFFFFF")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withAlternateContactlessCiacDecline("FBFFFC")
                      .withDsrpCiacDecline("FFFFFF")
                      .withIssuerUrl("Cl105");

        return profileBuilder;
    }

    /**
     * @return Profile CL.110
     */
    private static ProfileBuilder getProfileCl110() {
        final ProfileBuilder profileBuilder = getRef176WithAlternate();

        profileBuilder.withContactlessCiacDecline("FFFFFF")
                      .withContactlessCvrMaskAnd("FFFE00FFFFFF")
                      .withAlternateContactlessCiacDecline("FDFFFC")
                      .withDsrpCiacDecline("FFFFFF")
                      .withIssuerUrl("Cl110");

        return profileBuilder;
    }

    /**
     *  *
     *  * @return Profile CL.72
     *  
     */
    private static ProfileBuilder getProfileCl72() {

        final ProfileBuilder profileBuilder = getRef176();
        profileBuilder.withContactlessCiacDecline("000000").
                withContactlessCvrMaskAnd("FFFE00FFFFFF").
                              withAlternateContactlessCiacDecline("000000").
                              withDsrpCiacDecline("000000").
                              withIssuerUrl("cl72");

        return profileBuilder;
    }

    /**
     *  *
     *  * @return Profile DSRP Test 1 Profile
     *  
     */
    private static ProfileBuilder getProfileDsrpTest1() {
        final ProfileBuilder profileBuilder = getRef176();
        profileBuilder
                .withDsrpCiacDecline("040000")
                .withIssuerUrl("DsrpTest1");
        return profileBuilder;
    }

    /**
     *  *
     *  * @return Profile DSRP Test Profile with wrong Pan Sequence Number
     *  
     */
    private static ProfileBuilder getProfileDsrpTestWithWrongPsn() {
        final ProfileBuilder profileBuilder = getRef176();
        profileBuilder
                .withPanSequenceNumber("12")
                .withIssuerUrl("DsrpTest1");
        return profileBuilder;
    }

    //--------------------------------------------------------------------------------------------//
    // Reference Profiles
    //--------------------------------------------------------------------------------------------//

    private static ProfileBuilder getRef176WithoutAlternate() {
        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withMagstripe()
                      .withRemotePaymentSupport()
                      .withCrmCountryCode("0056")
                      .withIccCertificate(CertificateStore.getIccCertificate("A176"))
                      .withIssuerUrl("ref_176")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER);

        return profileBuilder;
    }

    private static ProfileBuilder getRef176WithoutAlternateAndWithoutDsrp() {
        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withMagstripe()
                      .withCrmCountryCode("0056")
                      .withIccCertificate(CertificateStore.getIccCertificate("A176"))
                      .withIssuerUrl("ref_176")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER);

        return profileBuilder;
    }

    private static ProfileBuilder getRef176WithAlternate() {
        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withMagstripe()
                      .withCrmCountryCode("0056")
                      .withIccCertificate(CertificateStore.getIccCertificate("A176"))
                      .withIssuerUrl("ref_176")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER)
                      .withUsMaestro()
                      .withUsAipMaskingFunctionality();

        return profileBuilder;
    }

    private static ProfileBuilder getRef176() {
        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withMagstripe()
                      .withUsMaestro()
                      .withRemotePaymentSupport()
                      .withCrmCountryCode("0056")
                      .withIccCertificate(CertificateStore.getIccCertificate("A176"))
                      .withIssuerUrl("ref_176")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER);


        return profileBuilder;
    }

    private static ProfileBuilder getRef128() {
        final String cardPicture = new String(Hex.encodeHex("TVK_5.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withMagstripe()
                      .withUsMaestro()
                      .withRemotePaymentSupport()
                      .withCrmCountryCode("0250")
                      .withIccCertificate(CertificateStore.getIccCertificate("A128"))
                      .withIssuerUrl("ref_128")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER)
                      .withUsAipMaskingFunctionality();

        return profileBuilder;
    }

    private static ProfileBuilder getRef96() {
        final String cardPicture = new String(Hex.encodeHex("TVK_4.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withMagstripe()
                      .withUsMaestro()
                      .withRemotePaymentSupport()
                      .withCrmCountryCode("0250")
                      .withIccCertificate(CertificateStore.getIccCertificate("A96"))
                      .withIssuerUrl("ref_96")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER)
                      .withUsAipMaskingFunctionality();

        return profileBuilder;
    }

    private static ProfileBuilder getMasterCardMcbpPersoProfile1() {
        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = new ProfileBuilder();

        profileBuilder.usingMdesCmsDedicatedJsonFormatWithKek(DATA_ENCRYPTION_KEY)
                      .withPan("5413339000001513")
                      .withoutTokenization()
                      .withPanSequenceNumber("00")
                      .withApplicationExpiryDate("151231")
                      .withMChip()
                      .withMagstripe()
                      .withRemotePaymentSupport()
                      .withServiceCode("226")
                      .withAdditionalCheckTable("000000000000000000000000000000000000")
                      .withCrmCountryCode("0380")
                      .withContactlessCiacDecline("010008")
                      .withContactlessCvrMaskAnd("FFFFFFFFFFFF")
                      .withContactlessIssuerApplicationData("0114000100000000000000000000000000FF")
                      .withContactlessCiacDeclineOnPpms("4100")
                      .withDsrpCiacDecline("010008")
                      .withDsrpCvrMaskAnd("FF0000000000")
                      .withDsrpIssuerApplicationData("0114000000000000000000000000000000FF")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER);

        return profileBuilder;
    }

    private static ProfileBuilder getMasterCardMcbpPersoProfile3() {
        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = new ProfileBuilder();

        profileBuilder.usingMdesCmsDedicatedJsonFormatWithKek(DATA_ENCRYPTION_KEY)
                      .withPan("5413339000001513")
                      .withoutTokenization()
                      .withPanSequenceNumber("00")
                      .withApplicationExpiryDate("201231")
                      .withMagstripe()
                      .withRemotePaymentSupport()
                      .withServiceCode("226")
                      .withAdditionalCheckTable("000000000000000000000000000000000000")
                      .withCrmCountryCode("0380")
                      .withContactlessIssuerApplicationData("0114000100000000000000000000000000FF")
                      .withContactlessCiacDeclineOnPpms("4100")
                      .withDsrpCiacDecline("010008")
                      .withDsrpCvrMaskAnd("FF0000000000")
                      .withDsrpIssuerApplicationData("0114000000000000000000000000000000FF")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER);

        return profileBuilder;
    }

    private static ProfileBuilder initializeProfileBuilder() {
        final ProfileBuilder profileBuilder = new ProfileBuilder();

        profileBuilder.usingMdesCmsDedicatedJsonFormatWithKek(DATA_ENCRYPTION_KEY)
                      .withPan("5413339000001513")
                      .withoutTokenization()
                      .withPanSequenceNumber("00")
                      .withApplicationExpiryDate("201231")
                      .withAdditionalCheckTable("000402FFFFFFFF00000000FFFFFFFFFFFFFF")
                      .withCrmCountryCode("0056")
                      .withContactlessCiacDecline("000000")
                      .withContactlessCvrMaskAnd("FFFFFFFFFFFF")
                      .withContactlessIssuerApplicationData("0114000100000000000000000000000000FF")
                      .withContactlessCiacDeclineOnPpms("0000")
                      //.withUsMaestro()
                      //.withUsAipMaskingFunctionality()
                      .withAlternateContactlessCiacDecline("000000")
                      .withAlternateContactlessCvrMaskAnd("FFFFFFFFFFFF")
                      .withDsrpCiacDecline("000000")
                      .withDsrpCvrMaskAnd("A50000000000")
                      .withDsrpIssuerApplicationData("0114000000000000000000000000000000FF")
                      .withServiceCode("201");

        return profileBuilder;
    }

    private static ProfileBuilder getMcbpCardTestRef176() {

        final String cardPicture = new String(Hex.encodeHex("TVK_6.png".getBytes()));
        final ProfileBuilder profileBuilder = initializeProfileBuilder();

        profileBuilder.withMChip()
                      .withMagstripe()
                      .withUsMaestro()
                      .withRemotePaymentSupport()
                      .withCrmCountryCode("0056")
                      .withIccCertificate(CertificateStore.getIccCertificate("A176"))
                      .withIssuerUrl("ref_176")
                      .withCardLayoutDescription(CLD_HEADER + cardPicture + CLD_TRAILER)
                      .withUsAipMaskingFunctionality();

        return profileBuilder;
    }
}
