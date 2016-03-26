package gui;

import host.RemoteHost;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import client.SmockClient;

import communication.EncryptedMessage;
import communication.Message;
import communication.MessageSender;

public class MessageGUI {
	private RemoteHost remoteHost;

	public MessageGUI() {
		final JFrame mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setLayout(new GridLayout(3, 1));
		JPanel userDetails = new JPanel(new GridLayout(1, 2));
		JLabel uname = new JLabel("Username: ", JLabel.RIGHT);
		final JTextField userName = new JTextField();
		userName.setSize(1, 10);
		userDetails.add(uname);
		userDetails.add(userName);
		mainFrame.add(userDetails);
		JPanel messageDetails = new JPanel(new GridLayout(1, 2));
		JLabel msg = new JLabel("Message: ", JLabel.RIGHT);
		final JTextArea message = new JTextArea(3, 20);
		message.setEditable(true);
		JScrollPane scroll = new JScrollPane(message);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		messageDetails.add(msg);
		messageDetails.add(scroll);
		mainFrame.add(messageDetails);
		JPanel submit = new JPanel(new GridLayout(1, 2));
		JButton sbmt = new JButton("Submit");
		sbmt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String uname = userName.getText();
				String msg = message.getText();
				Message messg = new Message();
				messg.setMsg(msg);
				RemoteHost host = SmockClient.keyRing.get(uname);
				System.out.println(host);
				if (!host.equals(null)) {
					mainFrame.dispose();
					messg.setUname(SmockClient.uname);
					remoteHost = host;
					send(messg);
				} else {
					new MessageSender(uname, msg);
				}
				mainFrame.dispose();
			}
		});
		submit.add(sbmt);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
			}
		});
		submit.add(cancel);
		mainFrame.add(submit);
		mainFrame.pack();
		mainFrame.setVisible(true);
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