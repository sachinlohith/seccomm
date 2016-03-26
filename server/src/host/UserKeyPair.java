package host;

import java.io.Serializable;

public class UserKeyPair implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] pubKey;
	private byte[] privKey;

	public byte[] getPubKey() {
		return pubKey;
	}

	public void setPubKey(byte[] pubKey) {
		this.pubKey = pubKey;
	}

	public byte[] getPrivKey() {
		return privKey;
	}

	public void setPrivKey(byte[] privKey) {
		this.privKey = privKey;
	}

}
