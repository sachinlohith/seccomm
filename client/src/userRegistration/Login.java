package userRegistration;

import gui.ErrorPage;
import host.RemoteHost;
import host.UserKeyPair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.logging.Level;

import client.SmockClient;
import credentials.Credentials;

public class Login {
	private static Socket socket;
	private static boolean success;

	public static boolean doLogin(Credentials credentials) {
		success = false;
		try {
			socket = new Socket(SmockClient.serverAddress,
					SmockClient.loginPort);
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Connected to "
						+ SmockClient.serverAddress + " at port no "
						+ SmockClient.port);
			}
			ObjectOutputStream out = new ObjectOutputStream(
					socket.getOutputStream());
			out.writeObject(1);
			out.writeObject(credentials);
			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());
			boolean error = (boolean) in.readObject();
			if (!error) {
				success = true;
				SmockClient.uname = credentials.getUname();
				SmockClient.key = (UserKeyPair) in.readObject();
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				X509EncodedKeySpec ks = new X509EncodedKeySpec(
						SmockClient.key.getPubKey());
				SmockClient.pubKey = (RSAPublicKey) keyFactory
						.generatePublic(ks);
				PKCS8EncodedKeySpec ks1 = new PKCS8EncodedKeySpec(
						SmockClient.key.getPrivKey());
				SmockClient.privKey = (RSAPrivateKey) keyFactory
						.generatePrivate(ks1);
				SmockClient.keyRing = (HashMap<String, RemoteHost>) in
						.readObject();
				if (SmockClient.key == null) {
					new ErrorPage("Invalid login");
				} else {
					if (SmockClient.debug) {
						SmockClient.log.log(Level.INFO, "KeyPair is "
								+ SmockClient.key.toString());
					}
				}
			} else {
				if (SmockClient.debug) {
					SmockClient.log.log(Level.INFO, "Invalid user login");
				}
			}
		} catch (IOException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO,
						"Could not connect to the server..... Try again");
			}
		} catch (ClassNotFoundException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "No such class found");
			}
		} catch (NoSuchAlgorithmException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "No such algorithm");
			}
		} catch (InvalidKeySpecException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Invalid key spec");
				e.printStackTrace();
			}
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				if (SmockClient.debug) {
					SmockClient.log
							.log(Level.INFO, "Socker couldn't be closed");
				}
			}
		}
		return success;
	}
}
