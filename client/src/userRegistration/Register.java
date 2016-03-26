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

public class Register {
	private Socket socket;

	public boolean doRegister(Credentials credentials) throws ClassNotFoundException {
		boolean status = false;
		try {
			socket = new Socket(SmockClient.serverAddress, SmockClient.port);
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Connected to "
						+ SmockClient.serverAddress + " at port no "
						+ SmockClient.port);
			}
			ObjectOutputStream out = new ObjectOutputStream(
					socket.getOutputStream());
			out.writeObject(credentials);
			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());
			SmockClient.key = (UserKeyPair) in.readObject();
			SmockClient.keyRing = (HashMap<String, RemoteHost>) in.readObject();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec ks = new X509EncodedKeySpec(
					SmockClient.key.getPubKey());
			SmockClient.pubKey = (RSAPublicKey) keyFactory.generatePublic(ks);
			PKCS8EncodedKeySpec ks1 = new PKCS8EncodedKeySpec(
					SmockClient.key.getPrivKey());
			SmockClient.privKey = (RSAPrivateKey) keyFactory
					.generatePrivate(ks1);
			if (SmockClient.key == null) {
				new ErrorPage("User already exists. Please login");
			} else {
				if (SmockClient.debug) {
					SmockClient.log.log(Level.INFO, SmockClient.key.toString());
				}
				status = true;
				SmockClient.uname = credentials.getUname();
			}
		} catch (IOException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO,
						"Could not connect to the server..... Try again");
			}
		} catch (NoSuchAlgorithmException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "No such class found");
			}
		} catch (InvalidKeySpecException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Invalid key spec");
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
		return status;
	}
}