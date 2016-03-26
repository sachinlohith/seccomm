package server;

import host.RemoteHost;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Logger;

import loginserver.LoginServer;

public class Driver {
	public static InetAddress ha;
	public static Logger log = Logger.getLogger("debug");
	public static boolean debug = true;
	public static HashMap<String, RemoteHost> keyRing = null;

	public static void main(String[] args) {
		try {
			ha = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.exit(1);
		}

		Runnable smockserver = new Runnable() {

			@Override
			public void run() {
				SmockServer server = new SmockServer();
				server.register();
			}
		};
		new Thread(smockserver).start();

		Runnable loginserver = new Runnable() {

			@Override
			public void run() {
				LoginServer server = new LoginServer();
				server.listen();
			}
		};
		new Thread(loginserver).start();
	}
}
