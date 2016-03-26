package host;

import java.io.Serializable;
import java.net.InetAddress;

public class RemoteHost implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InetAddress address;
	private byte[] key;

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}
}
