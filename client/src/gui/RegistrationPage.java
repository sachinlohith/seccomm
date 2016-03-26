package gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import userRegistration.Register;
import credentials.Credentials;

public class RegistrationPage {
	final JFrame mainframe = new JFrame();

	public RegistrationPage() {
		mainframe.setVisible(true);
		mainframe.setSize(300, 300);
		mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainframe.setLayout(new FlowLayout());
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
		JLabel passwordRepeat = new JLabel("Re Enter Password:", JLabel.CENTER);
		final JPasswordField passwordRepeatData = new JPasswordField();
		passwordRepeatData.setEchoChar('*');
		details.add(passwordRepeat);
		details.add(passwordRepeatData);
		mainframe.add(details);
		JPanel submit = new JPanel(new GridLayout(1, 2));
		JButton register = new JButton("Register");
		JButton cancel = new JButton("Cancel");
		submit.add(register);
		submit.add(cancel);
		mainframe.add(submit);
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainframe.dispose();
			}
		});
		register.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String uname = userNameData.getText();
					char[] password1 = passwordData.getPassword();
					char[] password2 = passwordRepeatData.getPassword();
					if (!Arrays.equals(password1, password2)) {
						throw new PasswordException();
					}
					Credentials credentials = new Credentials();
					credentials.setUname(uname);
					credentials.setPasswd(password1);
					Register reg = new Register();
					if (reg.doRegister(credentials)) {
						removeMainFrame();
					}
				} catch (PasswordException e2) {
					new ErrorPage("Passwords do not match");
				} catch (Exception e2) {
					new ErrorPage("Invalid data provided");
				}
			}
		});
		mainframe.pack();
	}

	public void removeMainFrame() {
		this.mainframe.dispose();
	}
}
