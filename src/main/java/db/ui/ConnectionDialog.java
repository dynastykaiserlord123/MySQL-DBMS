package db.ui;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

public class ConnectionDialog extends Frame {
	private Panel urlPanel;
	private Panel unPanel;
	private Panel pwPanel;
	private Panel driverPanel;
	private Panel buttonPanel;
	TextField dbURL;
	TextField username;
	TextField password;
	JComboBox<String> driver;
	Button connect;

	public ConnectionDialog(String title) {
		super(title);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		urlPanel = new Panel();
		unPanel = new Panel();
		pwPanel = new Panel();
		driverPanel = new Panel();
		buttonPanel = new Panel();
		urlPanel.setLayout(new FlowLayout());
		unPanel.setLayout(new FlowLayout());
		pwPanel.setLayout(new FlowLayout());
		setSize(300, 250);
		dbURL = new TextField(20);
		username = new TextField(20);
		password = new TextField(20);
		String[] driverList = { "com.mysql.jdbc.Driver", "Local operation" };
		driver = new JComboBox<String>(driverList);
		password.setEchoChar('#');
		dbURL.setSize(100, 50);
		username.setSize(100, 50);
		password.setSize(100, 50);
		Label url = new Label("Database url");
		Label user = new Label("Username");
		Label pw = new Label("Password");
		Label driverLabel = new Label("Database Driver");
		urlPanel.add(url);
		urlPanel.add(dbURL);
		unPanel.add(user);
		unPanel.add(username);
		pwPanel.add(pw);
		pwPanel.add(password);
		driverPanel.add(driverLabel);
		driverPanel.add(driver);
		add(urlPanel);
		add(unPanel);
		add(pwPanel);
		add(driverPanel);
		connect = new Button("Connect");
		buttonPanel.add(connect);
		add(buttonPanel);
		setVisible(true);
	}
}
