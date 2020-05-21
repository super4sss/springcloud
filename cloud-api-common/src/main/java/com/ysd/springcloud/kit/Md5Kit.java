
package com.ysd.springcloud.kit;
import java.security.MessageDigest;

public class Md5Kit {

	private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	public static String encrypt(String inStr) {
        try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(inStr.getBytes("UTF-8"));
			byte[] digest = messageDigest.digest();
			return toHex(digest);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
    }
	
	private static String toHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int j = 0; j < bytes.length; j++) {
            sb.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            sb.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return sb.toString();
    }
	
}
