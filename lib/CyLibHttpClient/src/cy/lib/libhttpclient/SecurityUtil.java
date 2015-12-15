package cy.lib.libhttpclient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
	public static final String MD5Encryption(String source) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();

			messageDigest.update(source.getBytes("UTF-8"));
			byte[] byteArray = messageDigest.digest();

			StringBuffer md5StrBuff = new StringBuffer();

			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
					md5StrBuff.append("0").append(
							Integer.toHexString(0xFF & byteArray[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}

			return md5StrBuff.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String md5sum(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try{
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while((numRead=fis.read(buffer)) > 0) {
				md5.update(buffer,0,numRead);
			}
			fis.close();
			return toHexString(md5.digest());	
		} catch (Exception e) {
			System.out.println("error");
			return null;
		}
	}
	public static String sha1sum(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try{
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("SHA-1");
			while((numRead=fis.read(buffer)) > 0) {
				md5.update(buffer,0,numRead);
			}
			fis.close();
			return toHexString(md5.digest());	
		} catch (Exception e) {
			System.out.println("error");
			return null;
		}
	}
	 public static String toHexString(byte[] keyData) {
	        if (keyData == null) {
	            return null;
	        }
	        int expectedStringLen = keyData.length * 2;
	        StringBuilder sb = new StringBuilder(expectedStringLen);
	        for (int i = 0; i < keyData.length; i++) {
	            String hexStr = Integer.toString(keyData[i] & 0x00FF,16);
	            if (hexStr.length() == 1) {
	                hexStr = "0" + hexStr;
	            }
	            sb.append(hexStr);
	        }
	        return sb.toString();
	    }

}
