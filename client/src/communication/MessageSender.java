package communication;

import gui.ErrorPage;
import host.RemoteHost;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import client.SmockClient;
import credentials.Credentials;

public class MessageSender {

	public final JFrame mainFrame = new JFrame();
	public RemoteHost remoteHost;
	public Message message = new Message();

	public MessageSender(String uname, String msg) {
		message.setMsg(msg);
		message.setUname(uname);
		if (SmockClient.debug) {
			SmockClient.log.log(Level.INFO, "Message " + msg
					+ " is being sent to " + uname);
		}
		mainFrame.setVisible(true);
		mainFrame.setSize(300, 300);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new FlowLayout());
		JPanel details = new JPanel(new GridLayout(5, 2));
		JLabel userName = new JLabel("Username:", JLabel.CENTER);
		final JTextField userNameData = new JTextField();
		details.add(userName);
		details.add(userNameData);
		JLabel passwordField = new JLabel("Password:", JLabel.CENTER);
		final JPasswordField passwordData = new JPasswordField();
		passwordData.setEchoChar('*');
		details.add(passwordField);
		details.add(passwordData);
		mainFrame.add(details);
		JPanel submit = new JPanel(new GridLayout(1, 2));
		JButton login = new JButton("Login");
		JButton cancel = new JButton("Cancel");
		submit.add(login);
		submit.add(cancel);
		mainFrame.add(submit);
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
			}
		});
		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String userName = userNameData.getText();
				char[] password = passwordData.getPassword();
				boolean error = login(userName, password);
				if (!error) {
					mainFrame.dispose();
					message.setUname(userName);
					send(message);
				} else {
					if (SmockClient.debug) {
						SmockClient.log.log(Level.INFO, "Invalid user login");
					}
					mainFrame.dispose();
					new ErrorPage("Invalid username and password combination");
				}
			}
		});
		mainFrame.pack();
	}

	protected boolean login(String userName, char[] password) {
		Socket socket = null;
		try {
			socket = new Socket(InetAddress.getByName("localhost"),
					SmockClient.loginPort);
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Connected to "
						+ SmockClient.serverAddress + " at port no "
						+ SmockClient.loginPort);
			}
			ObjectOutputStream out = new ObjectOutputStream(
					socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());
			out.writeObject(0);
			Credentials cred = new Credentials();
			cred.setUname(userName);
			cred.setPasswd(password);
			out.writeObject(cred);
			boolean error = (boolean) in.readObject();
			if (!error) {
				out.writeObject(message.getUname());
				remoteHost = (RemoteHost) in.readObject();
				if (SmockClient.debug) {
					SmockClient.log.log(Level.INFO, this.remoteHost.getKey()
							.toString());
				}
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO,
						"Could not connect to the server");
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO,
						"Class of object to be read not found");
				e.printStackTrace();
			}
		}
		try {
			socket.close();
		} catch (Exception e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Could not close socket");
			}
		}
		return false;
	}

	public void send(Message message) {
		Socket socket = null;
		try {
			X509EncodedKeySpec ks = new X509EncodedKeySpec(remoteHost.getKey());
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(ks);
			EncryptedMessage cipherText = new EncryptedMessage(message, pubKey);
			socket = new Socket(remoteHost.getAddress(), 16002);
			ObjectOutputStream out = new ObjectOutputStream(
					socket.getOutputStream());
			out.writeObject(cipherText);
		} catch (IOException e) {
			if (SmockClient.debug) {
				SmockClient.log
						.log(Level.INFO, "Could not connect to the peer");
			}
		} catch (NoSuchAlgorithmException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "No such algorithm");
			}
		} catch (InvalidKeySpecException e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Invalid key");
			}
		}
		try {
			socket.close();
		} catch (Exception e) {
			if (SmockClient.debug) {
				SmockClient.log.log(Level.INFO, "Could not close the socket");
			}
		}
	}
}