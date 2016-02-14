package iPodia;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// http://www.asjava.com/core-java/java-md5-example/

public class MD5Encryption {
	public static String encrypt(String initialString) {
		if (initialString == null)
			return "";

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			BigInteger number = new BigInteger(1, md.digest(initialString.getBytes()));
			return number.toString();
		} catch (NoSuchAlgorithmException e) {
			return initialString;
		}
	}
}
