package server;

import java.net.InetAddress;
import java.security.KeyPair;

import credentials.SafeCredentials;

public class UserData {
	private SafeCredentials credentials;
	private InetAddress remoteAddress;
	private int portNo;
	private KeyPair key;

	public SafeCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(SafeCredentials credentials) {
		this.credentials = credentials;
	}

	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public KeyPair getKey() {
		return key;
	}

	public void setKey(KeyPair key) {
		this.key = key;
	}
}
