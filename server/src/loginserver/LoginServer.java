package loginserver;

import host.RemoteHost;
import host.UserKeyPair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import server.Driver;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import credentials.Credentials;
import credentials.SafeCredentials;

public class LoginServer {
	public final static int port = 16001;
	public final static int backLog = 100000;
	public final static int timeout = 0;
	private static InetAddress hostAddress;
	private ServerSocket socket;
	public static MongoClient mongoClient;
	private static RemoteHost remoteHost;

	public LoginServer() {
		hostAddress = server.Driver.ha;
		try {
			socket = new ServerSocket(port, backLog, hostAddress);
			socket.setSoTimeout(timeout);
			if (Driver.debug) {
				Driver.log.log(Level.INFO, "Created the login server on port "
						+ port + " with backlog " + backLog + " with timeout "
						+ timeout);
			}
		} catch (Exception e) {
			if (Driver.debug) {
				Driver.log.log(Level.INFO, "Could not create the login server");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void listen() {
		Socket loginServer = null;
		while (true) {
			try {
				loginServer = this.socket.accept();
				if (Driver.debug) {
					Driver.log.log(
							Level.INFO,
							"Connected to "
									+ loginServer.getRemoteSocketAddress()
									+ " at port number "
									+ loginServer.getPort());
				}
				ObjectInputStream in = new ObjectInputStream(
						loginServer.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(
						loginServer.getOutputStream());
				int choice = (int) in.readObject();
				switch (choice) {
				case 0:
					Credentials cred = (Credentials) in.readObject();
					SafeCredentials safeCred = new SafeCredentials(cred);
					if (checkLogin(safeCred)) {
						out.writeObject(false);
						if (loginServer.isConnected()) {
							String uname = (String) in.readObject();
							remoteHost = getHost(uname);
							out.writeObject(remoteHost);
						}
					} else {
						out.writeObject(true);
					}
					break;
				case 1:
					Credentials cred1 = (Credentials) in.readObject();
					SafeCredentials safeCred1 = new SafeCredentials(cred1);
					if (checkLogin(safeCred1)) {
						out.writeObject(false);
						UserKeyPair key = getKeyPair(safeCred1);
						out.writeObject(key);
						Driver.keyRing = getHashMap();
						out.writeObject(Driver.keyRing);
					} else {
						out.writeObject(true);
					}
					break;
				default:
					if (Driver.debug) {
						Driver.log.log(Level.INFO, "Invalid login choice");
					}
					break;
				}
			} catch (IOException e) {
				if (Driver.debug) {
					Driver.log.log(Level.INFO,
							"Connection to the client closed");
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				if (Driver.debug) {
					Driver.log.log(Level.INFO, "Invalid data sent");
				}
			} catch (NoSuchAlgorithmException e) {
				if (Driver.debug) {
					Driver.log.log(Level.INFO,
							"Invalid encryption algorithm specified");
				}
			} catch (InvalidKeySpecException e) {
				if (Driver.debug) {
					Driver.log.log(Level.INFO, "Invalid key specification");
				}
			}
		}
	}

	private HashMap<String, RemoteHost> getHashMap()
			throws UnknownHostException {
		MongoCredential credential = MongoCredential.createMongoCRCredential(
				"server", "secure", "apnss*S".toCharArray());
		try {
			mongoClient = new MongoClient(new ServerAddress(),
					Arrays.asList(credential));
		} catch (UnknownHostException e) {
			if (server.Driver.debug) {
				server.Driver.log.log(Level.INFO,
						"Could not instantiate mongodb client");
			}
			return null;
		}
		DB db = mongoClient.getDB("secure");
		DBCollection coll = db.getCollection("data");
		DBCursor cursor = coll.find();
		RemoteHost host;
		HashMap<String, RemoteHost> hashMap = new HashMap<String, RemoteHost>();
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			String uname = obj.getString("uname");
			System.out.println(uname);
			byte[] pubKey = DatatypeConverter.parseHexBinary(obj
					.getString("publickey"));
			InetAddress address = InetAddress.getByName(obj
					.getString("address").substring(1));
			host = new RemoteHost();
			host.setAddress(address);
			host.setKey(pubKey);
			hashMap.put(uname, host);
		}
		return hashMap;
	}

	private UserKeyPair getKeyPair(SafeCredentials safeCred1)
			throws UnknownHostException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		MongoCredential credential = MongoCredential.createMongoCRCredential(
				"server", "secure", "apnss*S".toCharArray());
		try {
			mongoClient = new MongoClient(new ServerAddress(),
					Arrays.asList(credential));
		} catch (Exception e) {
			if (Driver.debug) {
				Driver.log.log(Level.INFO, "Couldn't connect to mongodb");
			}
			return null;
		}
		DB db = mongoClient.getDB("secure");
		DBCollection coll = db.getCollection("data");
		BasicDBObject doc = new BasicDBObject("uname", safeCred1.getUname());
		DBCursor cursor = coll.find(doc);
		if (cursor.hasNext()) {
			BasicDBObject document = (BasicDBObject) cursor.next();
			byte[] pubKey = DatatypeConverter.parseHexBinary(document
					.getString("publickey"));
			byte[] privKey = DatatypeConverter.parseHexBinary(document
					.getString("privatekey"));
			UserKeyPair keyPair = new UserKeyPair();
			keyPair.setPubKey(pubKey);
			keyPair.setPrivKey(privKey);
			return keyPair;
		} else {
			if (Driver.debug) {
				Driver.log.log(Level.INFO, "Invalid username specified");
			}
			return null;
		}
	}

	private RemoteHost getHost(String uname) throws UnknownHostException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		RemoteHost host = new RemoteHost();
		MongoCredential credential = MongoCredential.createMongoCRCredential(
				"server", "secure", "apnss*S".toCharArray());
		try {
			mongoClient = new MongoClient(new ServerAddress(),
					Arrays.asList(credential));
		} catch (Exception e) {
			if (Driver.debug) {
				Driver.log.log(Level.INFO, "Couldn't connect to mongodb");
			}
			return null;
		}
		DB db = mongoClient.getDB("secure");
		DBCollection coll = db.getCollection("data");
		BasicDBObject doc = new BasicDBObject("uname", uname);
		DBCursor cursor = coll.find(doc);
		if (cursor.hasNext()) {
			BasicDBObject document = (BasicDBObject) cursor.next();
			InetAddress hostName = InetAddress.getByName(document
					.getString("host"));
			byte[] pbkey = DatatypeConverter.parseHexBinary(document
					.getString("publickey"));
			host.setAddress(hostName);
			host.setKey(pbkey);
			return host;
		} else {
			if (Driver.debug) {
				Driver.log.log(Level.INFO, "Invalid username specified");
			}
			return null;
		}
	}

	private boolean checkLogin(SafeCredentials safeCred) {
		MongoCredential credential = MongoCredential.createMongoCRCredential(
				"server", "secure", "apnss*S".toCharArray());
		try {
			mongoClient = new MongoClient(new ServerAddress(),
					Arrays.asList(credential));
		} catch (UnknownHostException e) {
			if (Driver.debug) {
				Driver.log.log(Level.INFO,
						"Could not instantiate mongodb client");
			}
			return false;
		}
		DB db = mongoClient.getDB("secure");
		DBCollection coll = db.getCollection("data");
		BasicDBObject doc = new BasicDBObject("uname", safeCred.getUname())
				.append("pwd", safeCred.getPasswd());
		DBCursor cursor = coll.find(doc);
		if (cursor.hasNext()) {
			doc = (BasicDBObject) cursor.next();
			String uname = doc.getString("uname");
			String pwd = doc.getString("pwd");
			if (uname.equals(safeCred.getUname())
					&& pwd.equals(safeCred.getPasswd())) {
				return true;
			}
		} else {
			return false;
		}
		return false;
	}
}