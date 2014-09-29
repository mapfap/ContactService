package contact.entity;

import java.io.UnsupportedEncodingException;
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
		try {
			byte[] bytesOfMessage = data.toString().getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest( bytesOfMessage );
			String digested = new String( thedigest, "UTF-8" );
			System.out.println( digested );
			return digested;
		} catch ( UnsupportedEncodingException | NoSuchAlgorithmException ex ) {
			ex.printStackTrace();
		}
		return null;
	}
	
}
