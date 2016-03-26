package client;

import gui.FrontPage;
import host.RemoteHost;
import host.UserKeyPair;

import java.net.InetAddress;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import communication.MessageReciever;

public class SmockClient {
	public static final int port = 16000;
	public static final int loginPort = 16001;
	public static InetAddress serverAddress;
	public static InetAddress clientAddress;
	public static Logger log = Logger.getLogger("debug");
	public final static boolean debug = true;
	public static UserKeyPair key;
	public static RSAPublicKey pubKey;
	public static RSAPrivateKey privKey;
	public static RSAPublicKey remotePubkey;
	public static Thread thread1, thread2;
	public static HashMap<String, RemoteHost> keyRing;
	public static String uname;

	public static void main(String[] args) {
		try {
			serverAddress = InetAddress.getByName(args[0]);
			clientAddress = InetAddress.getByName(args[1]);
		} catch (Exception e) {
			if (debug) {
				log.log(Level.INFO,
						"Invalid port number and remote address combination");
			}
			System.exit(1);
		}
		Runnable r1 = new Runnable() {

			@Override
			public void run() {
				new FrontPage();
			}
		};
		thread1 = new Thread(r1);
		thread1.start();

		Runnable r2 = new Runnable() {

			@Override
			public void run() {
				MessageReciever.recieve();
			}
		};
		thread2 = new Thread(r2);
		thread2.start();
	}
}