package eu.dime.mobile.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Taken from: http://apachejava.blogspot.com.es/2011/02/hexconversions-convert-string-byte-byte.html
 *        and: http://stackoverflow.com/questions/5564643/android-calculating-sha-1-hash-from-file-fastest-algorithm
 */
public class Utils {

		 
	    /**
	     *  Convenience method to convert a byte array to a hex string.
	     *
	     * @param  data  the byte[] to convert
	     * @return String the converted byte[]
	     */
	    public static String bytesToHex(byte[] data) {
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) {
	            buf.append(byteToHex(data[i]).toUpperCase());
	        }
	        return (buf.toString());
	    }
	 
	 
	    /**
	     *  method to convert a byte to a hex string.
	     *
	     * @param  data  the byte to convert
	     * @return String the converted byte
	     */
	    public static String byteToHex(byte data) {
	        StringBuffer buf = new StringBuffer();
	        buf.append(toHexChar((data >>> 4) & 0x0F));
	        buf.append(toHexChar(data & 0x0F));
	        return buf.toString();
	    }
	 
	 
	    /**
	     *  Convenience method to convert an int to a hex char.
	     *
	     * @param  i  the int to convert
	     * @return char the converted char
	     */
	    public static char toHexChar(int i) {
	        if ((0 <= i) && (i <= 9)) {
	            return (char) ('0' + i);
	        } else {
	            return (char) ('a' + (i - 10));
	        }
	    }
	    
	    public static String getSHA1FromFileContent(String filename) {
			InputStream fis = null;
			byte[] buffer = new byte[65536];

			String result = null;
			
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-1");			
				fis = new DigestInputStream(new FileInputStream(filename), digest);
				// DigestInputStream needs the file to be read until reaching EOF
				while (fis.read(buffer) != -1) {								
				}			
				// After reaching EOF, we can get the digest
				result = Utils.bytesToHex(digest.digest());
			} catch (Exception e) {
				
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {

					} finally {
						fis = null;
					}
				}
				buffer = null;
			}
			
			return result;
		}
}
