package credentials;

import java.io.Serializable;

public class Credentials implements Serializable {
	private static final long serialVersionUID = 1L;
	private String uname;
	private char[] passwd;

	public char[] getPasswd() {
		return passwd;
	}

	public void setPasswd(char[] passwd) {
		this.passwd = passwd;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
}