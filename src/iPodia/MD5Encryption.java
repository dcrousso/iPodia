package iPodia;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// got this example from http://www.asjava.com/core-java/java-md5-example/

public class MD5Encryption {
	public static String encrypt(String initialString) {
		try {
			if (initialString == null) {
				return "";
			}
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(initialString.getBytes());
			BigInteger number = new BigInteger(1, digest);
			String encryptedString = number.toString();
			
			//this gives you the 32 character bit encryption
			
			return encryptedString;
			
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	
	}
}
