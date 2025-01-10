// ********************************************************************
// *
// * Copyright IBM Corporation 2001, 2004
// * 
// * Web Lecture Services
// * 
// ********************************************************************
package ene.eneform.utils.wls;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;
// import javax.xml.bind.*;


/**
 * Insert the type's description here.
 * Creation date: (16/03/2001 12:11:19)
 * @author: Administrator
 */
public class WLSEncrypt 
{
	
/**
 * Encrypt constructor comment.
 */
public WLSEncrypt() {
	super();
}
public static String translate(String strSource)
{
	// This is the specific routine used to encrypt WLS psw
	// The salt is always the same and the psw is converted to lower case before encryption
	// and the salt is removed afterwards
    String strEncrypted = translate(strSource.toLowerCase(), "BL", true, -1);

    return strEncrypted;
}

public static String translate(String strSource, int nMax)
{
	// Historically used for Password and Password Answer calls from WLSContext and ExtraLoginHandler
	// However due to a bug, the max length was never used
	String strEncrypted = translate(strSource);

	return strEncrypted;
}

public static String translate(String strSource, int nMax, String strSalt)
{
	// Historically used for Password  from ComponentHandler
	// However due to a bug, the max length was never used
	String strEncrypted = translate(strSource, strSalt, false, -1);

	return strEncrypted;
}

public static String translate(String strSource, String strSalt, boolean bRemove, int nMax)
{
    String strNewSource = strSource;
    if ((nMax > 0) && (strSource.length() > nMax))
        strNewSource = strSource.substring(0, nMax);

    // convert string to lower case and encrypt using Perl algorithm
    String strEncrypted = JCrypt.crypt(strSalt, strNewSource);

	if (bRemove)
	{
		return strEncrypted.substring(strSalt.length());
	}
	else
    	return strEncrypted;
}
public static String sha256(String base) {
    try{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(base.getBytes("UTF-8"));
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    } catch(Exception ex){
       throw new RuntimeException(ex);
    }
}

// 02/02/2015 A.T.
// LI-2494
/**
 * This program provides the following cryptographic functionalities
 * 1. Encryption using AES
 * 2. Decryption using AES
 * 
 * High Level Algorithm :
 * 1. Generate a AES key (specify the Key size during this phase) 
 * 2. Create the Cipher 
 * 3. To Encrypt : Initialize the Cipher for Encryption
 * 4. To Decrypt : Initialize the Cipher for Decryption
 * 
 * 
 */

// Parameter name in CONFIG_PARAM where we may store (at customer level) a specific key
public static final String PARAM_WLS_YEK = "WLS_YEK";
// Default key
private static String WLS_YEK = "20121127.1350.59923_IMAP:R51-020992";

public static void setWLS_YEK(String wLS_YEK) {
	WLS_YEK = wLS_YEK;
}

private static final int AES_KEYLENGTH = 128;	// change this as desired for the security level you want

// Testing new methods
public static void main(String[] args) {
	
	String strDataToEncrypt = new String();
	String strCipherText = new String();
	String strDecryptedText = new String();

	// Use the same key like in Prod - PartnerWorld
	// setWLS_YEK( "BEX1110@BE.IBM.COM.services.weblectures.AHE.V3.0.WAS.8.5" );

	// Use the same key like in Prod - IBMINTRA W3SSO
	setWLS_YEK( "BEX1110@BE.IBM.COM.services.weblectures.W3SSO.LI-2343.2016" );

	try {
		/**
		 * Step 1. Generate an AES key using KeyGenerator Initialize the
		 * keysize to 128 bits (16 bytes)
		 * 
		 */
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey secretKey = keyGen.generateKey();

	    byte[] keyBytes = WLS_YEK.getBytes(); 
	    SecretKeySpec secretKey2 = new SecretKeySpec( keyBytes, 0, AES_KEYLENGTH / 8, "AES" );
 
		/**
		 * Step 2. Generate an Initialization Vector (IV) 
		 * 		a. Use SecureRandom to generate random bits
		 * 		   The size of the IV matches the blocksize of the cipher (128 bits for AES)
		 * 		b. Construct the appropriate IvParameterSpec object for the data to pass to Cipher's init() method
		 */
		byte[] iv = new byte[AES_KEYLENGTH / 8];	// Save the IV bytes or send it in plaintext with the encrypted data so you can decrypt the data later
		SecureRandom prng = new SecureRandom();
		prng.nextBytes(iv);
		
		/**
		 * Step 3. Create a Cipher by specifying the following parameters
		 * 		a. Algorithm name - here it is AES 
		 * 		b. Mode - here it is CBC mode 
		 * 		c. Padding - e.g. PKCS7 or PKCS5
		 */
		Cipher aesCipherForEncryption = Cipher.getInstance("AES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!

		/**
		 * Step 4. Initialize the Cipher for Encryption
		 */
		aesCipherForEncryption.init( Cipher.ENCRYPT_MODE, 
										secretKey2,		
										new IvParameterSpec(iv));

		/**
		 * Step 5. Encrypt the Data 
		 * 		a. Declare / Initialize the Data. Here the data is of type String 
		 * 		b. Convert the Input Text to Bytes 
		 * 		c. Encrypt the bytes using doFinal method
		 */
		strDataToEncrypt = "Hello World of Encryption using AES ";
		byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
		byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
		// b64 is done differently on Android
                Base64.Encoder encoder = Base64.getEncoder();
		strCipherText = encoder.encodeToString(byteCipherText);
		// System.out.println("Cipher Text generated using AES is "			+ strCipherText + "|||" );

		/**
		 * Step 6. Decrypt the Data 
		 * 		a. Initialize a new instance of Cipher for Decryption (normally don't reuse the same object)
		 * 		   Be sure to obtain the same IV bytes for CBC mode.
		 * 		b. Decrypt the cipher bytes using doFinal method
		 */
		Cipher aesCipherForDecryption = Cipher.getInstance("AES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				

		aesCipherForDecryption.init( Cipher.DECRYPT_MODE, 
										secretKey2,
										new IvParameterSpec(iv));
		byte[] byteDecryptedText = aesCipherForDecryption.doFinal(byteCipherText);
		strDecryptedText = new String(byteDecryptedText);
		// System.out.println(" Decrypted Text message is " + strDecryptedText);
	}
	catch (NoSuchAlgorithmException noSuchAlgo) {
		System.err.println(" No Such Algorithm exists " + noSuchAlgo);
	}
	catch (NoSuchPaddingException noSuchPad) {
		System.err.println(" No Such Padding exists " + noSuchPad);
	}
	catch (InvalidKeyException invalidKey) {
		System.err.println(" Invalid Key " + invalidKey);
	}
	catch (BadPaddingException badPadding) {
		System.err.println(" Bad Padding " + badPadding);
	}
	catch (IllegalBlockSizeException illegalBlockSize) {
		System.err.println(" Illegal Block Size " + illegalBlockSize);
	}
	catch (InvalidAlgorithmParameterException invalidParam) {
		System.err.println(" Invalid Parameter " + invalidParam);
	}
	
//	try
//	{
//		String strTest2 = "IBM Web Lecture Services - Delivery and Information V3.0";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		String strEncrypted2 = AES_WLS_encrypt( strTest2, "ISO-8859-1", "partnerworld", null );
//		String strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "ISO-8859-1", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		strTest2 = "SE CONNECTER au syst\u00e8me de g\u00e9n\u00e9ration de rapports PartnerWorld \u30af\u30e9\u30a6\u30c9\u7ba1\u7406\u30b5\u30fc\u30d3\u30b9";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		// Set here an individual value you want to encrypt
//		strTest2 = "FRZ";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		strTest2 = "1024";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		strTest2 = "1025";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		strTest2 = "1026";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		strTest2 = "1026000";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		strTest2 = "1026001";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		strTest2 = "4294967296";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		strTest2 = "18446744073709551616";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		
//		// ... or to decrypt
//		strEncrypted2 = "p8xVD64YzvFNyiyzgwfMLA==";
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		// 2OK5cDwlM7+a/vGAj971iQ==   --> ACT
//		strEncrypted2 = "2OK5cDwlM7+a/vGAj971iQ==";
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		// lnseZtLLocjPtch+zXSBpA==		--> PEN
//		strEncrypted2 = "lnseZtLLocjPtch+zXSBpA==";
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		// pu75Wh1A0r6q26O2I3cssg==		--> INA
//		strEncrypted2 = "pu75Wh1A0r6q26O2I3cssg==";
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		// w3 sso
//		strTest2 = "10";
//		System.out.println("String to encrypt=" + strTest2 + "|" );
//		strEncrypted2 = AES_WLS_encrypt( strTest2, "ISO-8859-1", "ibmintra_w3sso", null );
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "ISO-8859-1", "ibmintra_w3sso", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		strEncrypted2 = "YrgBAoy7yi/W96aL2tOW8A==";
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "ISO-8859-1", "ibmintra_w3sso", null );
//		System.out.println("After encryption=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		
//		// 22/04/2016
//		// yERPF1hlBGT0BePeGuzM6A%3D%3D	
//		strEncrypted2 = "yERPF1hlBGT0BePeGuzM6A==";
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "ISO-8859-1", "ibmintra_w3sso", null );
//		System.out.println("After encryption 22/04/2016=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//		// 8Xr1mssBkCO7Wa%2FKpBPwNg%3D%3D
//		strEncrypted2 = "UaaqYtZFkjOdhFSH58BfKA==";
//		strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "ISO-8859-1", "ibmintra_w3sso", null );
//		System.out.println("After encryption 18/05/2016 18:20:36 WAS 2=" + strEncrypted2 + "| after decryption=" + strDecrypted2 + "|" );
//
//		strEncrypted2 = "handler=LoginSSL&action=requestlogin&offering=suc1&customer=red&from=short_url_w3_sso&w3sso_id=ejkVEfSJHKwxipdw5Av7vg==";
//		// strEncrypted2.replaceAll("w3sso_id=.*&", "&");
//		strDecrypted2 = removeParameterFromQuery( strEncrypted2, "w3sso_id" );
//		System.out.println("Initial:" + strEncrypted2 + "| after remove w3sso_id=" + strDecrypted2 + "|" );
//
//		strEncrypted2 = "handler=LoginSSL&action=requestlogin&offering=suc1&w3sso_id=ejkVEfSJHKwxipdw5Av7vg==&customer=red&from=short_url_w3_sso&w3sso_id=KK9mlZ7xK4rB3GJS79a70g==";
//		strDecrypted2 = removeParameterFromQuery( strEncrypted2, "w3sso_id" );
//		System.out.println("Initial:" + strEncrypted2 + "| after remove w3sso_id=" + strDecrypted2 + "|" );
//
//		strEncrypted2 = "w3sso_id=ejkVDDDDDDDDEfSJHKwxipdw5Av7vg==&handler=LoginSSL&action=requestlogin&offering=suc1&w3sso_id=ejkVEfSJHKwxipdw5Av7vg==&customer=red&from=short_url_w3_sso&w3sso_id=KK9mlZ7xK4rB3GJS79a70g==";
//		strDecrypted2 = removeParameterFromQuery( strEncrypted2, "w3sso_id" );
//		System.out.println("Initial:" + strEncrypted2 + "| after remove w3sso_id=" + strDecrypted2 + "|" );
//
//		strEncrypted2 = "w3sso_id=ejkVDDDDDDDDEfSJHKwxipdw5Av7vg==&handler=LoginSSL&action=requestlogin&offering=suc1&w3sso_id=ejkVEfSJHKwxipdw5Av7vg==&customer=red&from=short_url_w3_sso&w3sso_id=KK9mlZ7xK4rB3GJS79a70g==&alpha=beta&w3sso=aaa&w3sso_id_aux=gamma";
//		strDecrypted2 = removeParameterFromQuery( strEncrypted2, "w3sso_id" );
//		System.out.println("Initial:" + strEncrypted2 + "| after remove w3sso_id=" + strDecrypted2 + "|" );
//
//	}
//	catch( WLSException w )
//	{
//		System.err.println("Error:" + w.getMessage());
//	}
	
	// Performance tests - uncomment when needed
//	try
//	{
//		int i = -1;
//		int intSuccess = 0;
//		
//		for (  i = 1; i <= 10000 ; i++ )
//		{
//			String strTest2 = "IBM Web Lecture Services - Delivery and Information V3.0";
//			String strEncrypted2 = AES_WLS_encrypt( strTest2, "ISO-8859-1", "partnerworld", null );
//			String strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "ISO-8859-1", "partnerworld", null );
//			intSuccess +=  ( strDecrypted2.equalsIgnoreCase( strTest2) )? 1 : 0 ;
//			
//			strTest2 = "SE CONNECTER au syst\u00e8me de g\u00e9n\u00e9ration de rapports PartnerWorld \u30af\u30e9\u30a6\u30c9\u7ba1\u7406\u30b5\u30fc\u30d3\u30b9";
//			strEncrypted2 = AES_WLS_encrypt( strTest2, "UTF-8", "partnerworld", null );
//			strDecrypted2 = AES_WLS_decrypt( strEncrypted2, "UTF-8", "partnerworld", null );
//			intSuccess +=  ( strDecrypted2.equalsIgnoreCase( strTest2) )? 1 : 0 ;
//		}
//		System.out.println( "End cycle i=" + i  + " intSuccess=" + intSuccess );
//		// Results: 10.000 iterations in less than 5 seconds on a ThinkPad with 2 processors / 4GB memory			
//
//	}
//	catch( WLSException w )
//	{
//		System.out.println("Error:" + w.getMessage());
//	}
//	

}

// Return an encrypted string with AES algorithm
// Input parameters:
// 		strDataToEncrypt	= Original (unencrypted) string
//		strEncoding			= Encoding of the input string (we need it to obtain the corresponding bytes)
//		strCustomer			= Customer (normally from the context); will be used to add some randomness in the algorithm
//		strPublicK			= Public key used for encryption (stored somewhere and linked to the customer); 
//								IF null, will use as default WLS_YEK above
//
//	WARNING: returned string is larger than input string - reserve something like twice the original length
//
public static String AES_WLS_encrypt( String strDataToEncrypt,
										String strEncoding,
										String strPublicK ) 
throws WLSException
{
	String strCipherText = new String();
	strCipherText = "";
	String strIV = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	byte[] baIV = strIV.getBytes();
	
	try {
		// Step 1. Encode public key; We use AES 128 so keysize should be  128 bits (16 bytes)
	    byte[] keyBytes = ( strPublicK != null ) ? strPublicK.getBytes() : WLS_YEK.getBytes();
	    SecretKeySpec secretKey2 = new SecretKeySpec( keyBytes, 0, AES_KEYLENGTH / 8, "AES" );
 
		// Step 2. Use baIV as  Initialization Vector (IV) 

		// Step 3. Create a Cipher by specifying the following parameters
		// 		a. Algorithm name - here it is AES 
		Cipher aesCipherForEncryption = Cipher.getInstance( "AES" ); // Must specify the mode explicitly as most JCE providers default to ECB mode!!

		// Step 4. Initialize the Cipher for Encryption
		aesCipherForEncryption.init( Cipher.ENCRYPT_MODE, 
										secretKey2,		
										new IvParameterSpec( baIV, 0, AES_KEYLENGTH / 8 ));

		// Step 5. Encrypt the Data 
		// 			a. Convert the Input Text to Bytes 
		byte[] byteDataToEncrypt = strDataToEncrypt.getBytes( strEncoding );
		//  		b. Encrypt the bytes using doFinal method
		byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
		//			c. Assemble back a String that may stored somewhere (database, file, session)
		// strCipherText = DatatypeConverter.printBase64Binary( byteCipherText );
                Base64.Encoder encoder = Base64.getEncoder();
		strCipherText = encoder.encodeToString(byteCipherText);
		
		// System.out.println("Cipher Text generated using AES is "			+ strCipherText + "|||" );
	}
	catch (NoSuchAlgorithmException noSuchAlgo) {
		throw new WLSException( " No Such Algorithm exists " + noSuchAlgo.getMessage() );
	}
	catch (NoSuchPaddingException noSuchPad) {
		throw new WLSException( " No Such Padding exists " + noSuchPad.getMessage() );
	}
	catch (InvalidKeyException invalidKey) {
		throw new WLSException( " Invalid Key " + invalidKey.getMessage() );
	}
	catch (BadPaddingException badPadding) {
		throw new WLSException( " Bad Padding " + badPadding.getMessage() );
	}
	catch (IllegalBlockSizeException illegalBlockSize) {
		throw new WLSException( " Illegal Block Size " + illegalBlockSize.getMessage() );
	}
	catch (InvalidAlgorithmParameterException invalidParam) {
		throw new WLSException( " Invalid Parameter " + invalidParam.getMessage() );
	}
	catch (UnsupportedEncodingException badEncoding) {
		throw new WLSException( " Unsupported Encoding Exception " + badEncoding.getMessage() );
	}
	
	return strCipherText;
}


// Decrypt a string with AES algorithm
// Input parameters:
//		strCipherText		= Encrypted string
//		strEncoding			= Encoding of the input string (we need it to obtain the corresponding bytes)
//		strCustomer			= Customer (normally from the context); will be used to add some randomness in the algorithm
//		strPublicK			= Public key used for encryption (stored somewhere and linked to the customer); 
//								IF null, will use as default WLS_YEK above
//
// WARNING:  It is ESSENTIALS that parameters strEncoding, strCustomer, strPublicK are IDENTICAL for a pair encrypt / decrypt!
//
public static String AES_WLS_decrypt( String strCipherText,
										String strEncoding,
										String strPublicK ) 
throws WLSException
{
	String strDecryptedText = new String();
	strDecryptedText = "";
	
	String strIV = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	byte[] baIV = strIV.getBytes();
	
	try {
		// byte[] byteCipherText = DatatypeConverter.parseBase64Binary( strCipherText );
		Base64.Decoder decoder= Base64.getDecoder();
		byte[] byteCipherText = decoder.decode( strCipherText );
		
		// Step 1. Encode public key; We use AES 128 so keysize should be  128 bits (16 bytes)
		byte[] keyBytes = ( strPublicK != null ) ? strPublicK.getBytes() : WLS_YEK.getBytes();
		SecretKeySpec secretKey2 = new SecretKeySpec( keyBytes, 0, AES_KEYLENGTH / 8, "AES" );
		
		// Step 2. Use baIV as  Initialization Vector (IV) 
	
		// Step 6. Decrypt the Data 
		// 		a. Initialize a new instance of Cipher for Decryption
		// 		   Be sure to obtain the same IV bytes for CBC mode.
		Cipher aesCipherForDecryption = Cipher.getInstance("AES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				
		aesCipherForDecryption.init( Cipher.DECRYPT_MODE, 
										secretKey2,
										new IvParameterSpec( baIV, 0, AES_KEYLENGTH / 8 ));
		// 		b. Decrypt the cipher bytes using doFinal method
		byte[] byteDecryptedText = aesCipherForDecryption.doFinal(byteCipherText);

		//			c. Assemble back the String using specified encoding 
		strDecryptedText = new String( byteDecryptedText, strEncoding );

		// System.out.println(" Decrypted Text message is " + strDecryptedText);
	}
	catch (NoSuchAlgorithmException noSuchAlgo) {
		throw new WLSException( " No Such Algorithm exists " + noSuchAlgo.getMessage() );
	}
	catch (NoSuchPaddingException noSuchPad) {
		throw new WLSException( " No Such Padding exists " + noSuchPad.getMessage() );
	}
	catch (InvalidKeyException invalidKey) {
		throw new WLSException( " Invalid Key " + invalidKey.getMessage() );
	}
	catch (BadPaddingException badPadding) {
		throw new WLSException( " Bad Padding " + badPadding.getMessage() );
	}
	catch (IllegalBlockSizeException illegalBlockSize) {
		throw new WLSException( " Illegal Block Size " + illegalBlockSize.getMessage() );
	}
	catch (InvalidAlgorithmParameterException invalidParam) {
		throw new WLSException( " Invalid Parameter " + invalidParam.getMessage() );
	}
	catch (UnsupportedEncodingException badEncoding) {
		throw new WLSException( " Unsupported Encoding Exception " + badEncoding.getMessage() );
	}
	catch (IOException badEncoding) {
		throw new WLSException( " IOException " + badEncoding.getMessage() );
	}
	
	return strDecryptedText;
}

// removes all occurrences of a parameter from a query URL
public static String removeParameterFromQuery( String pstrQuery, String pstrParameter )
{
	String strReturn = pstrQuery;
	String strParameterEqual = pstrParameter + "=";
	int intPosStart = -1;
	int intPosEnd = -1;
	
	try
	{
		while( strReturn.indexOf( strParameterEqual) >= 0 )
		{
			intPosStart = strReturn.indexOf( strParameterEqual);
			// find next position for "&" string (if parameter is in the middle of the query URL...)
			intPosEnd = strReturn.indexOf( "&", intPosStart );
			if( intPosEnd == -1 )
			{
				intPosEnd = strReturn.length();
			}
	
			if ( intPosStart >= 1 && "&".equalsIgnoreCase( strReturn.substring( intPosStart - 1, intPosStart)) )
			{
				intPosStart--;
			}
			strReturn = ( (intPosStart > 0 ) ? strReturn.substring( 0, intPosStart ) : "" ) +
						( (intPosEnd < strReturn.length()) ? strReturn.substring( intPosEnd ) : "" );
		}
	}
	catch( IndexOutOfBoundsException excTemp)
	{
		return pstrQuery;
	}
	
	return strReturn;
}

}

