package communication;

import gui.ErrorPage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import javax.crypto.Cipher;

import client.SmockClient;

public class MessageReciever {
	public static int port = 16002;
	public static ServerSocket reciever;

	public static void recieve() {
		Socket socket = null;
		try {
			reciever = new ServerSocket(port, 100000, SmockClient.clientAddress);
			reciever.setSoTimeout(0);
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO,
						"Created the reciever at the port " + port
								+ " with backlog " + 100000);
			}
		} catch (IOException e1) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Couldn't set up the reciever");
			}
		}

		while (true) {
			try {
				socket = reciever.accept();
				ObjectInputStream in = new ObjectInputStream(
						socket.getInputStream());
				EncryptedMessage cipherText = (EncryptedMessage) in
						.readObject();
				if (SmockClient.debug) {
					SmockClient.log.log(Level.INFO,
							"Message " + cipherText.getCipherText() + " from "
									+ cipherText.getUname());
				}
				Cipher dec = Cipher.getInstance("RSA");
				dec.init(Cipher.DECRYPT_MODE, SmockClient.privKey);
				String plainText = (String) cipherText.getCipherText()
						.getObject(dec);
				new ErrorPage(plainText, cipherText.getUname());
				if (SmockClient.debug) {
					SmockClient.log.log(Level.INFO, plainText);
				}
			} catch (Exception e) {
				if (SmockClient.debug) {
					SmockClient.log.log(Level.INFO,
							"Couldn't connect to the client");
					e.printStackTrace();
				}
			}
		}
	}
}
