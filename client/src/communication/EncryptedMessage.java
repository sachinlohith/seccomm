package communication;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import client.SmockClient;

public class EncryptedMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uname;
	private SealedObject cipherText = null;

	public EncryptedMessage(Message message, RSAPublicKey publicKey) {
		this.uname = message.getUname();
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			try {
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			} catch (InvalidKeyException e) {
				if (SmockClient.debug) {
					SmockClient.log.log(Level.INFO, "Invalid key specified");
				}
			}
		    try {
		    	SealedObject obj = new SealedObject(message.getMsg(), cipher);
		    	setCipherText(obj);
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		} catch (NoSuchAlgorithmException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO,
						"No such encryption algorithm exists");
			}
		} catch (NoSuchPaddingException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "No such padding exists");
			}
		}
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public SealedObject getCipherText() {
		return cipherText;
	}

	public void setCipherText(SealedObject obj) {
		this.cipherText = obj;
	}

}
