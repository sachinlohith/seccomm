package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class ErrorPage {
	public ErrorPage(String error) {
		final JFrame errorFrame = new JFrame();
		errorFrame.setLayout(new GridLayout(2, 1));
		errorFrame.setSize(200, 100);
		errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel errorInfo = new JLabel(error,
				JLabel.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				errorFrame.dispose();
			}
		});
		errorFrame.add(errorInfo);
		errorFrame.add(ok);
		errorFrame.setVisible(true);
		Timer timer = new Timer(3000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				errorFrame.dispose();
			}
		});
		timer.start();
	}
	
	public ErrorPage(String error, String uname) {
		final JFrame errorFrame = new JFrame();
		errorFrame.setLayout(new GridLayout(2, 1));
		errorFrame.setSize(200, 100);
		errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel errorInfo = new JLabel("Message from " + uname + " : " + error,
				JLabel.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				errorFrame.dispose();
			}
		});
		errorFrame.add(errorInfo);
		errorFrame.add(ok);
		errorFrame.setVisible(true);
		Timer timer = new Timer(3000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				errorFrame.dispose();
			}
		});
		timer.start();
	}
}
