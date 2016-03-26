package gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import client.SmockClient;

public class FrontPage {
	public static final JFrame mainFrame = new JFrame();
	public FrontPage() {
		mainFrame.setSize(100, 120);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new FlowLayout());
		JPanel submit = new JPanel(new GridLayout(3, 1));
		JButton sendMessage = new JButton("Send Message");
		JButton cancel = new JButton("Quit");
		JButton register = new JButton("Register/Login");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
				SmockClient.thread2.interrupt();
				SmockClient.thread1.interrupt();
				System.exit(0);
			}
		});
		register.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFrame lrFrame = new JFrame();
				lrFrame.setVisible(true);
				lrFrame.setLayout(new GridLayout(2, 1));
				JButton register = new JButton("Register");
				JButton login = new JButton("Login");
				lrFrame.add(register);
				lrFrame.add(login);
				lrFrame.pack();
				register.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						new RegistrationPage();
						lrFrame.dispose();
					}
				});
				login.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						new LoginPage();
						lrFrame.dispose();
					}
				});
			}
		});
		sendMessage.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new MessageGUI();
			}
		});
		submit.add(sendMessage);
		submit.add(register);
		submit.add(cancel);
		mainFrame.add(submit);
		mainFrame.pack();
		mainFrame.setVisible(true);

	}
}
