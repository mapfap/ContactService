package contact.entity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Common interface of object that can be digested to MD5.
 * @author mapfap - Sarun Wongtanakarn
 */
public abstract class MD5Digestable {

	/**
	 * Get MD5 of this object.
	 * @return MD5 of this object.
	 */
	public abstract String getMD5();

	/**
	 * Digest the UTF-8 String to MD5.
	 * @param data data to be digested.
	 * @return String of MD5.
	 */
	protected String digest( String data ) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update( data.getBytes() );
			byte[] digest = messageDigest.digest();
			BigInteger bigInt = new BigInteger( 1,digest );
			String hashtext = bigInt.toString( 16 );
			while( hashtext.length() < 32 ){
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch ( NoSuchAlgorithmException e ) {
			e.printStackTrace();
		}
		return null;
	}

}
