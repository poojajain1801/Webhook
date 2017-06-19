package com.comviva.hceservice.util.crypto;


import com.comviva.hceservice.util.ArrayUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.GeneralSecurityException;
import java.util.Arrays;

public class AESUtil {
    public static byte[] mobSessionKeyConf = ArrayUtil.getByteArray("F767033AB5F7CEBDD38FCED2D74BCD0C");
    public static byte[] mobSessionKeyMac = ArrayUtil.getByteArray("978DFDB168E119601FB259945BD0FF7F");
    public static String tokenUniqueReference;

    public static final int BLOCK_SIZE = 16;

    public enum Padding {
        NoPadding,      // JCA standard
        PKCS5Padding,   // JCA standard
        ISO7816_4
    }

    public static void setMobSessionKeys(String mobSessionKeyConf, String mobSessionKeyMac) {
        AESUtil.mobSessionKeyConf = ArrayUtil.getByteArray(mobSessionKeyConf);
        AESUtil.mobSessionKeyMac = ArrayUtil.getByteArray(mobSessionKeyMac);
    }

    public static void setTokenUniqueReference(String tokenUniqueReference) {
        AESUtil.tokenUniqueReference = tokenUniqueReference;
    }

    public static void resetSession() {
        mobSessionKeyConf = null;
        mobSessionKeyMac = null;
        tokenUniqueReference = null;
    }

    public static byte[] prepareSv(int counter, boolean m2c) {
        return ArrayUtil.getByteArray((m2c ? "00" : "01") + String.format("%06X", counter) + "000000000000000000000000");
    }

    /**
     * ISO/IEC 7816-4 padding.
     *
     * @param data Data to be padded
     * @return padded data
     */
    private static byte[] padISO7816(byte[] data) {
        int paddedDataLen = data.length + (BLOCK_SIZE - (data.length % BLOCK_SIZE));
        byte[] paddedData = Arrays.copyOf(data, paddedDataLen);
        paddedData[data.length] = (byte) 0x80;
        return paddedData;
    }

    /**
     * Removes ISO/IES 7816-4 padding
     *
     * @param data Padded data
     * @return Data after removing padding
     */
    private static byte[] removeISO7816Padding(byte[] data) {
        int i = data.length - 1;
        while (i >= 0) {
            if (data[i] == (byte) 0x80) {
                break;
            }
            i--;
        }
        if (i < 0) {
            return null;
        }
        return Arrays.copyOf(data, i);
    }

    /**
     * Encrypts/decrypts the given data with given key using AES algorithm in CBC mode.
     *
     * @param data      Input data to encrypt/decrypt
     * @param key       AES Key
     * @param iv        Initial Vector. If iv is null then default value 00...00 (16 bytes) will be used.
     * @param padding   Padding method.
     * @param isEncrypt <code>true </code>Encrypt the data <br>
     *                  <code>false </code>Decrypt the data
     * @return Encrypted/Decrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] cipherCBC(byte[] data, byte[] key, byte[] iv, Padding padding, boolean isEncrypt) throws GeneralSecurityException {
        byte[] outBuff = null;

        // Create Key object from the given key value
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");

        // Create Cipher instance and initialize it with key and encryption mode
        Cipher cipherObj;
        switch (padding) {
            case ISO7816_4:
                cipherObj = Cipher.getInstance("AES/CBC/" + Padding.NoPadding.name());
                if (isEncrypt) {
                    data = padISO7816(data);
                }
                break;

            // JCA Standard padding
            default:
                cipherObj = Cipher.getInstance("AES/CBC/" + padding.name());
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv != null ? iv : ArrayUtil.getByteArray("00000000000000000000000000000000"));
        cipherObj.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keyObj, ivParameterSpec);
        outBuff = cipherObj.doFinal(data);

        // Remove Padding for non-JCA standard Padding Algorithm
        if (!isEncrypt && Padding.ISO7816_4 == padding) {
            outBuff = removeISO7816Padding(outBuff);
        }
        return outBuff;
    }

    /**
     * Encrypts/decrypts the given data with given key using AES algorithm in ECB.
     *
     * @param data      Input data to encrypt/decrypt
     * @param key       AES Key
     * @param padding   Padding method.
     * @param isEncrypt <code>true </code>Encrypt the data <br>
     *                  <code>false </code>Decrypt the data
     * @return Encrypted/Decrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] cipherECB(byte[] data, byte[] key, Padding padding, boolean isEncrypt) throws GeneralSecurityException {
        byte[] outBuff = null;

        // Create Key object from the given key value
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");

        // Create Cipher instance and initialize it with key and encryption mode
        Cipher cipherObj;
        switch (padding) {
            case ISO7816_4:
                cipherObj = Cipher.getInstance("AES/ECB/" + Padding.NoPadding.name());
                if (isEncrypt) {
                    data = padISO7816(data);
                }
                break;

            // JCA Standard padding
            default:
                cipherObj = Cipher.getInstance("AES/ECB/" + padding.name());
        }
        cipherObj.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keyObj);
        outBuff = cipherObj.doFinal(data);

        // Remove Padding for non-JCA standard Padding Algorithm
        if (!isEncrypt && Padding.ISO7816_4 == padding) {
            outBuff = removeISO7816Padding(outBuff);
        }
        return outBuff;
    }

    /**
     * CCM as defined in ISO/IEC 19772 as mechanism 3.
     *
     * @param inBuff  Input data
     * @param key     AES Key
     * @param iv      Starting Value
     * @param encrypt <code>true </code>If encryption is required <br></><code>false </code>Decryption is required
     * @return Encrypted/Decrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] cipherCcm(byte[] inBuff, byte[] key, byte[] iv, boolean encrypt) throws GeneralSecurityException {
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keyObj, ivSpec);

        byte[] bEncData = cipher.doFinal(inBuff);
        return bEncData;
    }

    /**
     * Calculates AES MAC.
     *
     * @param data Input Data
     * @param key  AES Key
     * @return MAC of input data
     * @throws GeneralSecurityException
     */
    public static byte[] aesMac(byte[] data, byte[] key) throws GeneralSecurityException {
        // Pad data with according to ISO/IEC 7816-4
        byte[] paddedData = padISO7816(data);
        final int noOfBlocks = paddedData.length / BLOCK_SIZE;

        byte[] out = new byte[BLOCK_SIZE];

        // Create Key object from the given key value
        SecretKeySpec keyObj = new SecretKeySpec(key, "AES");

        Cipher cipherObj = Cipher.getInstance("AES/CBC/NoPadding");
        cipherObj.init(Cipher.ENCRYPT_MODE, keyObj, new IvParameterSpec(new byte[BLOCK_SIZE]));
        for (int i = 0; i < noOfBlocks; i++) {
            out = ArrayUtil.xor(paddedData, i * BLOCK_SIZE, out, 0, BLOCK_SIZE);
            out = cipherObj.doFinal(out);
        }
        return Arrays.copyOfRange(out, 0, 8);
    }

    public static void main(String[] args) {
        /*int m2c = 2;
        int c2m = 1;
        encProvisionMPAReq(m2c);
        encNotifyProvisionResultCmsReq(m2c);
        encDeleteCmsReq(m2c);*/

        //encTokenCredentials();
        //encMobPin();

        //decCmsDResponse(c2m);

        /*byte[] data = ArrayUtil.getByteArray("4245BB09534AAE4DA6C5D52E5A074100");
        byte[] key = ArrayUtil.getByteArray("4245BB09534AAE4DA6C5D52E5A074100");
        System.out.println(ArrayUtil.getHexString(data));
        try {
            byte[] encData = AESUtil.cipherECB(data, key, Padding.ISO7816_4, true);

            encData = ArrayUtil.getByteArray("DF9B066F3C5415F1DCA4B90417AF20026161797EA94C62AF51A680E42EE3BDF2A4280624E210E1A761AA7B35133389A6195F1FF5F8380C4433FA8A61C8523E5B2CA160F91702E361E3C0FECA4FC377CC499E4B23E67B3FDDA36AD6929B91DD0C731AF9A815910F3D64AC77D71151A08B4349BE5549C38C76B5DA3CEC94924371FA703A7249AE0DE0C45A9435A8F4F92418B784ED1F98B42B5C1282DCFC0DEAB608103F03064F90E0D949FCDF091EF6F7D122B8A34D9705BDA039991CFF2020285813AFEB8712FFB75AB1A189B516595DCA864880077A4AAF5E854EAB9DD21F1911668B49C4CA8C100C5B201EA290883F23A9C69B967C409C4AC5015738F184B6A4638C1F8252E6CDB89738D3314149ADD724CE6E8110113D987AC911B72C35BD755AF58A562098111DCD2F60F6AB770BCC017F4A3C82195AABC9921B943ACBB4C2C98E6AF9C1B25F6E8C6C5CC9637EE81174E53BD9D43C4B29E2C272F8B9A6DB7F726DF5786DB6B2CDF1F874738A5E585021410A4B31F88EEF4414DF62407EC4C3D6EEDFEB07404D318A5B4CA211CE3AF2D17546A10E6D2545EA7BFC1FD939B5E6D9A6908E0925D7F6DD292B258D2F49BCDF8AA538C933B6940B0C4DA074718A34BCC7F401E9626A63D187DD9FE5B843F610F20CCAEA30680DB177B1821452889D8AFB7EE6E8D51ACF8C8E7B79A1EFDE617F51234E8318FA83280CF4EFE4E2CE17D911DD67C071FAA4E8DAC4355A9AFC6CF73EB93D6FF78B8DBB0DFFCD20F721021462FC4FD0EDED67207A96712B757A827A06B7554FF1AC7667200BD49A7EE029A3CBD810399152E1BA89FD00705955B6453A275BE32D09167F4C96B26C893A22D89B8AC4072AB43B85D0DBBE569445E7188968B4BEDC137BBA4A24AB920A89C50A28BFF6922E5FD80742F932E784EC65A61233F4FCCB3DA1D383F0379764099B796A0679C47092CB2EB43E42E9115CD79EE5A9632E508CF3233EE151A226FCCF30CB3654E63862F3FD24F56311ACA489ADDCEBC390C405CF2E03CBB7563AC5D81BC25C02F98ADC5857D46F1F470307F6398040DA6B68E3016D9850693FB4C55A2E4B65EF921080D87168E3F95C564BC1D5467B927A0D3CF40AB7E89339DBCA3D1E7532709A35754548596DEDAB57B7970996DBD77DEB72353312B281FA2DCB925C678B0C5A7D001EBFDCCA1370F3B7117AB8BF454568E17A6D41291DB28A954877952563AFE3E6238DBBE3AF2AC6AF6E686D0E09D58A012F3A5F7EE938618778FAEB510CBAAAEF542C1EE7635C9E4EA4FC4E92DC5FD2824DCA8CA7EB6F3139131D52614E10D09ADE6213F4C3C5259C442EB93DA4B2195C528C165798661258994E060A70B1923B191A903B4A1F2A457C4AE6F0D29CB60C37647A7CF95FBEEE3110AD79FB7D08E58BBB09BBB6E6542CAA3B542C02C8BDC01D491CB477702CC78ABBD6B41F067DAC256296867D1E4506ED547CF766B095BF3CB29C98A3C8E5BC5845CF5552B6218E51D53303A610A6EF341C5E25DEE667B75F2EF64146E9232628BC574D6876314BF5C47F293CEEE1BAC679B59E8651059EF8C00B1F22FAEF63AA526CC51B29802897C0240E62D206A2FEEB8C76B2B779F9AECA6E38BD7740814190582E31683B700BC2248DD8FB994455E3DEA07FE0D53D77113639238D6AA77A5625E4DF1BBE9C0C759503A72AF0AF595C8D3E9D3E63CF3C54A5A71F0A47FC57A050AAD4374AFE796DD92168E6388F070FFAD6BA85FF79F28176D48F5DDCDE2DBEB13A01D7989BC0A4722D0BA47C4EE4C7A70F4029B47FC524B1679B7474FB861204ABD2DECB1D45706F46C7F58F250EF85EB8AFD71441E6DAB0BC9AAC016C37F6D102CA40AB09475D0BD30FF957B3D20BB9441AA6098BC59EA592C9B83F2B22B5245BE8BED73491566E7BC5575C02AB9D010A453F3DF3D75E87E43F2D7931BB62290E8C9065FEC6FA1141B933861D1754576D83FED706428D8322530E5B7F5EA0A3ABF716DA9B17B3187944081630DA28C4B999C546DA17CCDD2247FC8FD96FD44A429682155FAF70B0EFAF75FBE02137C80D93B2C04AD99250B1507BC99045482465ACE96A2F09F7BA3CB3CEB8C2B934DC6CFBF7B4AA4900AA1F06AC66057418DB7E32C24559B7CDC044DE1E86D83D2468E92EB5A88878CBAC3DD6AA366B57A6C2D0288FABCA165977984068EEDDE265F92158DCC4CE1E50A0F54C4ACC85B20BD3AD2EB65B8F28A09AF68157AC18C2920F9DA46AE472D560CBAA02AF2DF58C40F0802E5014BF9EF6AD86AC4F93FE411B4EBC7BB95FB23B53E9C4DBA30733E237B45DCD1A0F18E717EDD37E50F7E95323C944E07A9D570F860F5B86F1CA0D13014219E5F5357F34D7E6F982FCDC17FEC60BD568086BE823A7C530704E167744B97047E279A909A7EC469B59BF0549A76FB0CB58768640BF77D8CC59685F117D54FE421531F0F99D523CAD47A38A7203EA832117B14129A51A2EA1072D58B9A1CE0AB3DAE59F0EA6469E2D882CA8E2764ECA93E13B929A00B0108BAD8F5372BE3053781B525063484C226F0F13751D3E9AA674A83E277CAF53E95FBC01A5BA5E7513E0FB3643A79DE40265A6E634CC452DCCC5B8BE1F214E9CF1619C9D9F7F2AC732707CB3EAEBB3389C92DD985E139616CC1AD4BC56B95E96A4DAF3AAC909782FA0B16F752E599A2C9D0511BC857344BB9206217D1B543A1A8C95EAE25AC2E637DE212C91A3CFEA8F27EF6D12BEDC2C55D2F6324CA9E48AFE50B16027EAF466EA527CB2C46EC6E3B7357EF8938BA915E8B141F3E2AE75EABF55DAC8CAB9353A06EFBD7763C519ECF2CE1C77DBD86D28EBBEFEF612BEE2AC1C421982570C6621E492ACC40F8894D62A4EA9DB43D33A272B712FDDFCF5403A79EB1564A3294D0D32D6C26E93F8FDE42FF1A45AC45A1BFD696735F2BAB0CB9432A2F5839AA9148B7C911777D1067DACDFFBBE1686BC22E7B4580B020229B722B9CF384EECDB0B4F680827118EBFC54213C498632B8B8CD8EDE897CE7EBFE4D214FD819E6D0B32DBEF179AF10293746A70B3F3AD9EA7FC5111F9F05F13825A8C6AB0145C24E8C3F150C228F8507752D2D3108BAB9122A8070EB97CE384CE62F1889694C79E92C45B27FB5E9BC230EEFC6B3DD4319C7D14F01D1677F8121B8DD52BE8E9A83FCEDEB00B0A1E6E709ADC2C3F939C388EFBFB48F78F0D9CD18BE96DE6DC56BB4DEC3D4C3E7239B4E6AE0EBAAA111F3F7B3214C83E67F4D074145D62C46E24D8DEA9637EA3EF34952BC661D1C4D0C1A271E58CE31499038548F1A2F48E29F58B18AAE1904A673D2449DF8035827361494B1130456AAF2E90B6A2DBC48965C55BC44860D10748604D190A2DF930C9B5508BFC36151B35918514928B41C1312A5B611C2375783F9B619B5485592EAE60248500F81A3F1AD190676C04A45F754ECFB7DE34DDB39C479BC94B30578FB8A510DC640C1C1581AECB01766A9EA8BA16BE0E981B36F7B8FBAE818AF1937104B9E7D9A73A84E95CCFF2B185C485C83B8460A06B889701E9B813FF206AF5826719E24D71D413634B057AE05D13E0B6BF4F27EBDD1475AB97A8BF022113808FB30A4764E32F639BB5D2AA755C9865841D8BEE9E1A8119B685703B25A40380DD31CA77BDAE327C379A11A714714E92AFAC4177FFC97A73D7D05D27783E897E0CC7ED1E11B1EFF62E155DA4C1601527692F793DD2B1A3EFA3253ADB83B93A35E2069A6066B5ACBD4A341BA94BFB0413072571B36D32C4EDF0BDC7C8FBAFB458D9B104648EF8D98BB611E47F6FB198E7807CFDD46CBA315B2E5D98F69E7458008EEAFBD63FF894EB0FDDD1EBCB50C23F0581225441C8EC4AAC3BA616A9094C7283E793B0DA81D7C781B9059CC411F85B522F47068F80EBE76BBC709764ED86D67D47C4811AC80A483F5B7D8F4E94969468573E00B717F808840A1F88ABA8D54E5C64D4FC86869B58D133CB1D8E9A749065063E488AB4ABFD5EE67FCD977B5945936BED2C0091E2E1274B05C28EA0641CE0C39C96D62BAC6F2F9AA3D4B94D202192C3ACB8A960488E79B6C010AE3C1715CA7129DB55690C774341E909BA68DEC4020ECFA91C278D461D7A11F43088D903AF7223FABC423450519C3CE50ADAD21A17D35A276ACC6F9A5AA13EE10EA10C2296E1FE237E93E15B7060DAB87912995182EA6A261A8EA28E6159BBB46787447993756334BF92EBA409B6AFF830A7FDFA41EE97F40CA02A07DD117FAAED9F8A51098218E98DCFF69DE4F1ED2E1E1E05D3FA5B6F897C59EE24DCF13A1A2A52A6EF15D402E9BD954C03776319C6AA925E76BF352F56ECBD30E2E6C0B1B73AEFD8388DFB2EC2BBA60C2B1FF7467B8228EC849019267635A1F41214A48A55BC1B91C6FA1755E73173503C7B8ADFED0D8369DC86DBEE8EB267C2DC75B767EF4AB52C3438A141620F08AEE25BD647C809BD381C07B99B69C089B3AD1E706C1159A19B76FDA0605843EEECB7885D921D6155618ECB78FED05CE2C578D60A514B388AFE01E779644E186A6C396704641B59893BFDEB84C64E207B3FD2A6FE12A8EDFD247A710DEBEF9C92749CD6B4C06F6AD42843521C76341FC26B74F788EF48755402786BC56A6120621523628F1EE5367A86A304913799AD743D7956BD20A668718C3E9B4F418298BA0D3C99C77EB32247E68842FDB00C29E1633C38E6B38324FE98CE2F491AB2C3F744EB51C81BE805CAFAE286CFBAC5787B418C50507BF48CD86A4268E981DD9FF4C1D90E7D5332D9C4AE292E1105B93E58D519B84D5AC65826B4BC2F0AE5B6DE4538EC88D47AB5651806826C98140056F34057CD0383DD64EBD6BABC38BE45A74FBE718CD3E23C0FD2DCDD149DC4A7DB8546C7E2C2020D7269F9D379FAD924A49EF5F456573EB7B521B51B03F5CA93D25E1A306AB13C639E1D71F6C933B00DC91D99C3D1B4091FE434D50C344E508AE5D6B9622C538FF98A68D5A4187DE194095FEFBA807FF0F035C83DEEB27B30F7DAFD96314C583BD3C5D29DC2CD5CCEFE711B8801CA06BB4B07D35A2550B335ACD4DB398D30E1008E968435B2EC67D717BBEBEE8466B07168EA4B0F1128C2B0529F1BD7BE1644491C996286BDC3D10A131348D38BA209976FF74B3672F02B0781DCB4F5B50FD58C61346ACB200FE0F1244CCF285209C92E5AE5789591DA549492F8887AFBFCAFE0755983F020B4CBE563AF18C0481329BC4FB37786E621931AFF18658344DAE5269C4F3A7A44");
            System.out.println(ArrayUtil.getHexString(encData));
            byte[] decData = AESUtil.cipherCcm(encData, key, Constants.CCM_NONCE, false);
            System.out.println(new JSONObject(new String(decData)));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
    }

     /*public static String encNotifyProvisionResultCmsReq(int m2c) {
        JSONObject jsReqData = new JSONObject();
        jsReqData.put("requestId", "700000000001");
        jsReqData.put("tokenUniqueReference", tokenUniqueReference);
        jsReqData.put("result", "SUCCESS");
        try {
            // Encrypt data
            byte[] bData = cipherCcm(jsReqData.toString().getBytes(), mobSessionKeyConf, prepareSv(m2c, true), true);
            // Calculate MAC
            byte[] bMac = aesMac(bData, mobSessionKeyMac);

            byte[] encData = Arrays.copyOf(bData, bData.length+bMac.length);
            System.arraycopy(bMac, 0, encData, bData.length, bMac.length);

            //System.out.print(ArrayUtil.getHexString(encData));
            return ArrayUtil.getHexString(encData);
        } catch (GeneralSecurityException e) {

        }
        return null;
    }

    public static String encProvisionMPAReq(int m2c) {
        JSONObject provisionReqData = new JSONObject();
        provisionReqData.put("requestId", "3000000001");
        provisionReqData.put("tokenUniqueReference", tokenUniqueReference);

        try {
            // Encrypt data
            byte[] bData = cipherCcm(provisionReqData.toString().getBytes(),
                    mobSessionKeyConf,
                    prepareSv(m2c, true),
                    true);
            // Calculate MAC
            byte[] bMac = aesMac(bData, mobSessionKeyMac);

            byte[] encData = Arrays.copyOf(bData, bData.length+bMac.length);
            System.arraycopy(bMac, 0, encData, bData.length, bMac.length);

            String encryptedData = ArrayUtil.getHexString(encData);
            //System.out.println(encryptedData);
            return encryptedData;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encDeleteCmsReq(int m2c) {
        JSONObject jsReqData = new JSONObject();
        jsReqData.put("requestId", "700000000001");
        jsReqData.put("tokenUniqueReference", tokenUniqueReference);
        jsReqData.put("transactionCredentialsStatus", new JSONArray());
        try {
            byte[] bData = cipherCcm(jsReqData.toString().getBytes(),
                    mobSessionKeyConf,
                    prepareSv(m2c, true),
                    true);
            // Calculate MAC
            byte[] bMac = aesMac(bData, mobSessionKeyMac);

            byte[] encData = Arrays.copyOf(bData, bData.length+bMac.length);
            System.arraycopy(bMac, 0, encData, bData.length, bMac.length);

            //System.out.println(ArrayUtil.getHexString(encData));
            return ArrayUtil.getHexString(encData);
        } catch (GeneralSecurityException e) {
        }
        return null;
    }

   public static void encMobPin() {
        byte[] data = ArrayUtil.getByteArray("4412342198765432109876827382028F");
        byte[] key = Constants.AES_KEY;
        System.out.println(ArrayUtil.getHexString(data));
        try {
            byte[] encData = AESUtil.cipherECB(data, key, Padding.NoPadding, true);
            System.out.println(ArrayUtil.getHexString(encData));
            byte[] decData = AESUtil.cipherECB(encData, key, Padding.NoPadding, false);
            System.out.println(ArrayUtil.getHexString(decData));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static void encTokenCredentials() throws JSONException {
        CardProfile cardProfile = new CardProfile();
        JSONObject jsonCardProfile = cardProfile.getCardProfile();
        JSONObject tokenCredentialData = new JSONObject();
        tokenCredentialData.put("iccKek", "5F70884B25739773B6E84EC6A43E7CD1E5F598B034449642A61884F593CFB9D3");
        tokenCredentialData.put("kekId", "222222");
        tokenCredentialData.put("cardProfile", jsonCardProfile);
        String data = tokenCredentialData.toString();
        byte[] bData = data.getBytes();
        byte[] key = ArrayUtil.getByteArray("4245BB09534AAE4DA6C5D52E5A074100");
        byte[] nonce = ArrayUtil.getByteArray("0102030405060708090A0B");

        System.out.println(data);
        try {
            byte[] encData = AESUtil.cipherCcm(bData, key, nonce, true);
            System.out.println("\n\n Encryted data : \n" + ArrayUtil.getHexString(encData));
            byte[] decData = AESUtil.cipherCcm(encData, key, nonce, false);
            String temp = new String(decData);
            System.out.println(temp);
            JSONObject tempJson = new JSONObject(temp);
            System.out.println("\n\n JSON Format : \n");
            System.out.println(tempJson);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject decCmsDResponse(int c2m, String cmsResp) {
        byte[] bCmsResp = ArrayUtil.getByteArray(cmsResp);

        try {
            byte[] encData = Arrays.copyOfRange(bCmsResp, 0, bCmsResp.length-8);
            byte[] expMac = Arrays.copyOfRange(bCmsResp, bCmsResp.length-8, bCmsResp.length);

            byte[] calcMac = aesMac(encData, mobSessionKeyMac);
            if(!ArrayUtil.compare(expMac, 0, calcMac, 0, calcMac.length)) {
                System.out.println("Incorrect MAC");
            }

            byte[] bData = cipherCcm(bCmsResp,
                    mobSessionKeyConf,
                    prepareSv(c2m, false),
                    false);

            String resp = new String(bData);
            JSONObject response = new JSONObject(resp);
            return response;
            //System.out.println(response.toString());
        } catch (GeneralSecurityException e) {

        }
        return null;
    }*/
}
