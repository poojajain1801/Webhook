package com.comviva.hceservice.security;

import android.content.Context;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author
 *
 */
public class RSAUtil {

	private PublicKey pub = null;
	private Context context;
	private static String algorithm = "AES";
	private static InputStream fixer=null;
	private static byte[] key;
	private static SecretKey yourKey = null;
	public static RSAUtil getInstance() {
		return instance;
	}

	public static void setInstance(RSAUtil newinstance) {
		instance = newinstance;
	}

	private static RSAUtil instance=null;
	public RSAUtil(Context context) {

		this.context = context;
		//Security.addProvider(new BouncyCastleProvider());
		pub = getPublicKeyFromCert();
	}

	public RSAUtil(InputStream name) {
		//Security.addProvider(new BouncyCastleProvider());
		pub = getPublicKeyFromCertFile(name);
		fixer=name;
	}


	public PublicKey getPublicKeyFromCert(){
		InputStream caInput = null;
		try {
			caInput = new BufferedInputStream(context.getApplicationContext().getAssets().open("mycert.pem"));
		} catch (IOException e) {
			Log.d("Error", "Error in Reading Cert");
		}

		 /*= RSAUtil.class.getResourceAsStream("/mycert.pem");*/
		CertificateFactory f;
		PublicKey pk=null;
		try {
			f = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate)f.generateCertificate(caInput);
			pk = certificate.getPublicKey();
		} catch (CertificateException e) {
			Log.d("Error", "Error in Reading Cert");
		}finally {
			try {
				caInput.close();
			} catch (IOException e) {
				Log.d("Error", "Error in Reading Cert");
			}
		}
		return pk;
	}

	public PublicKey getPublicKeyFromCertFile(InputStream file){
		System.out.println("FILE&******* "+file);


		CertificateFactory f;
		PublicKey pk=null;
		try {
			f = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate)f.generateCertificate(file);
			pk = certificate.getPublicKey();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			Log.d("Error", "Error in Reading Cert");
		}
		return pk;
	}

	/**
	 *
	 * @param
	 * @return
	 */
	public void encryptWithPublic(byte[] symKey, HashMap reqMap) {
		byte[] cipherText = null;
		Map requestJson = new HashMap<>();
		try {
			final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, pub);
			cipherText = cipher.doFinal(symKey);
			reqMap.put("requestKey",convertToString(cipherText));
		} catch (Exception e) {

		}
	}


	private byte[] aesEncryption(String text, HashMap requestJson){
		try{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			SecretKey secretKey = keyGen.generateKey();

			final int AES_KEYLENGTH = 128;
			byte[] iv = new byte[AES_KEYLENGTH / 8];
			SecureRandom prng = new SecureRandom();
			prng.nextBytes(iv);

			Cipher aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey,
					new IvParameterSpec(iv));

			byte[] byteDataToEncrypt = text.getBytes();
			byte[] byteCipherText = aesCipherForEncryption
					.doFinal(byteDataToEncrypt);

			String strCipherText = convertToString(byteCipherText);
			requestJson.put("requestIV",convertToString(iv) );
			requestJson.put("requestEncData",strCipherText);

			return secretKey.getEncoded();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("Error", "Error in Reading Cert");
		}
		return null;
	}

	public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Number of PBKDF2 hardening rounds to use. Larger values increase
		// computation time. You should select a value that causes computation
		// to take >100ms.
		final int iterations = 1000;

		// Generate a 256-bit key
		final int outputKeyLength = 256;

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
		SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
		return secretKey;
	}

	public static SecretKey generateKey() throws NoSuchAlgorithmException {
		// Generate a 256-bit key
		final int outputKeyLength = 256;
		SecureRandom secureRandom = new SecureRandom();
		// Do *not* seed secureRandom! Automatically seeded from system entropy.
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(outputKeyLength, secureRandom);
		yourKey = keyGenerator.generateKey();
		return yourKey;
	}

	public static byte[] encodeFile(byte[] fileData) throws Exception {
		yourKey = generateKey();
		byte[] data = yourKey.getEncoded();
		SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, algorithm);
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted = cipher.doFinal(fileData);

		return encrypted;
	}

	private String convertToString(byte[] buffer){
		return new String(new Base64().encode(buffer));
	}

	public static String doMeth(Context context,String input) {
		/*Request Encryption Starts*/
		RSAUtil rsaUtils = new RSAUtil(context);
		setInstance(rsaUtils);
		HashMap reqMap = new HashMap<>();

		key = rsaUtils.aesEncryption(input, reqMap); // store this key until response is received. This key is used to decrypt response.
		rsaUtils.encryptWithPublic(key, reqMap);
		/*Response Decryption ends*/
		String res=getJsonStringFromMap(reqMap);
		return res;

	}

	public static String  encryptString(String data) {
		RSAUtil rsaUtils = getInstance();
		HashMap reqMap = new HashMap<>();
		byte[] key = rsaUtils.aesEncryption(data, reqMap); // store this key until response is received. This key is used to decrypt response.
		rsaUtils.encryptWithPublic(key, reqMap);
		String dataStr=getJsonStringFromMap(reqMap);
		return dataStr;

	}

	public static String getJsonStringFromMap(Map<String, Object> responseMap) {
		String response;
		JSONObject job = null;
		job=new JSONObject(responseMap);
		response = job.toString();
		return response;
	}


	public static String aesDecrypt(String encryptedText, String iv) {
		try {

			SecretKey key2 = new SecretKeySpec(key, 0, key.length, "AES");
			Cipher cipher;
			cipher= Cipher.getInstance("AES/CBC/PKCS5PADDING");
			byte[] ivBytes = (byte[])new Base64().decode(iv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, key2, new IvParameterSpec(ivBytes));
			byte[] encryptedTextBytes = (byte[])new Base64().decode(encryptedText.getBytes());
			byte[] decryptedTextBytes ;
			decryptedTextBytes= cipher.doFinal(encryptedTextBytes);
			System.out.println("RESP DECRYPTED::::"+new String (decryptedTextBytes));
			return new String (decryptedTextBytes);
		} catch (Exception ex) {
			Log.d("Exception","Exception Occured");
			return null;
		}

	}
}