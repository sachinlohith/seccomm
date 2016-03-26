package gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import userRegistration.Login;
import credentials.Credentials;

public class LoginPage {
	final JFrame mainFrame = new JFrame();

	public LoginPage() {
		mainFrame.setVisible(true);
		mainFrame.setSize(300, 300);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
			public void actionPerformed(ActionEvent arg0) {
				String uname = userNameData.getText();
				char[] password = passwordData.getPassword();
				Credentials credentials = new Credentials();
				credentials.setUname(uname);
				credentials.setPasswd(password);
				if (Login.doLogin(credentials)) {
					mainFrame.dispose();
				} 
			}
		});
	}
}
