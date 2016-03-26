package credentials;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SafeCredentials {
	private String uname;
	private String passwd;
	private static final String SALT = "@google.com";

	public SafeCredentials(Credentials credentials) {
		this.setUname(credentials.getUname());
		this.passwd = String.valueOf(credentials.getPasswd());
		this.passwd = this.passwd + SALT;
		this.passwd = generateHash(this.passwd);
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPasswd() {
		return passwd;
	}

	private static String generateHash(String input) {
		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; idx++) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			// handle error here.
		}

		return hash.toString();
	}

}
