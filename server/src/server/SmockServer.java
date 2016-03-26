package server;

import host.RemoteHost;
import host.UserKeyPair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import credentials.Credentials;
import credentials.SafeCredentials;

public class SmockServer {
	public final static int port = 16000; // port no to bind to
	public final static int backlog = 100000; // no of clients to keep in queue
	public final static int timeout = 0; // timeout of the server in
											// milliseconds
	public static InetAddress hostAddress; // host address to bind to
	private ServerSocket socket; // ServerSocket instance
	public static MongoClient mongoClient;
	private static Socket smockServer;

	public SmockServer() {
		try {
			hostAddress = server.Driver.ha;
		} catch (Exception e) {
			if (server.Driver.debug) {
				server.Driver.log.log(Level.INFO,
						"Could not load the host address");
			}
			System.exit(1);
		}
		try {
			socket = new ServerSocket(port, backlog, hostAddress);
			socket.setSoTimeout(timeout);
			if (server.Driver.debug) {
				server.Driver.log.log(Level.INFO, "Created the server on port "
						+ port + " with backlog " + backlog + " with timeout "
						+ timeout);
			}
		} catch (IOException e) {
			if (server.Driver.debug) {
				server.Driver.log.log(Level.INFO,
						"Could not create the server on port " + port);
			}
			System.exit(1);
		}
	}

	public void register() {
		while (true) {
			try {
				smockServer = this.socket.accept();
				if (server.Driver.debug) {
					server.Driver.log.log(Level.INFO, "Connected to "
							+ smockServer.getRemoteSocketAddress()
							+ " at port number " + smockServer.getPort());
				}
				ObjectInputStream in = new ObjectInputStream(
						smockServer.getInputStream());
				Credentials credentials = (Credentials) in.readObject();
				SafeCredentials safeCredentials = new SafeCredentials(
						credentials);
				UserData data = new UserData();
				data.setCredentials(safeCredentials);
				data.setPortNo(smockServer.getPort());
				data.setRemoteAddress(smockServer.getInetAddress());
				UserKeyPair key = registerUser(data);
				ObjectOutputStream out = new ObjectOutputStream(
						smockServer.getOutputStream());
				out.writeObject(key);
				Driver.keyRing = getHashMap();
				out.writeObject(Driver.keyRing);
			} catch (IOException e) {
				if (server.Driver.debug) {
					server.Driver.log.log(Level.INFO,
							"Could not accept client request");
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				if (server.Driver.debug) {
					server.Driver.log.log(Level.INFO,
							"Could not accept credentials");
				}
			} catch (NoSuchAlgorithmException e) {
				if (server.Driver.debug) {
					server.Driver.log.log(Level.INFO,
							"No such encryption algorithm exists");
				}
			} catch (InvalidKeySpecException e) {
				if (server.Driver.debug) {
					server.Driver.log.log(Level.INFO, "Invalid key spec given");
				}
			} finally {
				try {
					smockServer.close();
				} catch (IOException e) {
					if (server.Driver.debug) {
						server.Driver.log.log(Level.INFO,
								"Couldn't close server");
					}
				}
			}
		}
	}

	private HashMap<String, RemoteHost> getHashMap() throws UnknownHostException {
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

	private static UserKeyPair registerUser(UserData data)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
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
		BasicDBObject doc = new BasicDBObject("uname", data.getCredentials()
				.getUname()).append("pwd", data.getCredentials().getPasswd())
				.append("address", data.getRemoteAddress().toString())
				.append("port", data.getPortNo());
		BasicDBObject user = new BasicDBObject("uname", data.getCredentials()
				.getUname());
		DBCursor cursor = coll.find(user);
		if (!cursor.hasNext()) {
			try {
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
				keyGen.initialize(2048);
				KeyPair key = keyGen.genKeyPair();
				RSAPublicKey pub = (RSAPublicKey) key.getPublic();
				RSAPrivateKey priv = (RSAPrivateKey) key.getPrivate();
				doc.append("publickey",
						DatatypeConverter.printHexBinary(pub.getEncoded()));
				doc.append("privatekey",
						DatatypeConverter.printHexBinary(priv.getEncoded()));
				coll.insert(doc);
				UserKeyPair keyPair = new UserKeyPair();
				keyPair.setPrivKey(priv.getEncoded());
				keyPair.setPubKey(pub.getEncoded());
				return keyPair;
			} catch (NoSuchAlgorithmException e) {
				if (server.Driver.debug) {
					server.Driver.log.log(Level.INFO,
							"Invalid algorithm specified");
				}
			}
		} else {
			if (server.Driver.debug) {
				server.Driver.log.log(Level.INFO,
						"Invalid credentials, user already exists");
			}
			return null;
		}
		return null;
	}
}