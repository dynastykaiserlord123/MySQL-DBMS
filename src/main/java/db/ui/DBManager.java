package db.ui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.MemoryImageSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;

import db.connection.DBConnect;

public class DBManager extends Applet implements ActionListener, WindowListener, KeyListener {
	static DatabaseMetaData dMeta;
	private ResultSet set;
	Connection conn;
	JFrame fMain;
	Image imgEmpty;
	Panel pResult;
	TextArea txtCommand;
	JButton butExecute;
	JButton butClearText;
	JButton butClearTable;
	JTable table;
	ResultsDisplayWindow window;
	TableSchema tableschema;
	ConnectionDialog connectionDialog;
	Boolean local = false;
	Boolean backingUpLocal = false;

	public DBManager() {
		fMain = new JFrame("MySQL Database Manager");
		imgEmpty = createImage(new MemoryImageSource(2, 2, new int[4 * 4], 2, 2));

		fMain.setIconImage(imgEmpty);
		fMain.addWindowListener(this);

		MenuBar bar = new MenuBar();

		String[] fitems = { "-Connect...", "--", "-Open Script...", "-Save Script...", "-Save Result...", "--",
				"-Import Data...", "-Backup Database...", "--", "-Exit" };

		addMenu(bar, "File", fitems);

		String[] vitems = { "RRefresh Tables", "--", "GResults in Grid", "TResults in Text" };

		addMenu(bar, "View", vitems);
		fMain.setMenuBar(bar);
		fMain.setSize(640, 480);
		fMain.add("Center", this);
		Panel pQuery = new Panel();
		Panel pCommand = new Panel();
		pResult = new Panel();

		pQuery.setLayout(new BorderLayout());
		pCommand.setLayout(new BorderLayout());
		pResult.setLayout(new BorderLayout());
		txtCommand = new TextArea(5, 40);
		txtCommand.setFont(new Font("Century", Font.ITALIC, 16));
		txtCommand.setBackground(Color.LIGHT_GRAY);
		txtCommand.addKeyListener(this);

		butExecute = new JButton("EXECUTE");
		butClearText = new JButton("CLEAR QUERIES");
		butClearTable = new JButton("CLEAR TABLE");
		butExecute.addActionListener(this);
		butClearText.addActionListener(this);
		butClearTable.addActionListener(this);
		butExecute.setFont(new Font("Centaur", Font.ITALIC, 14));
		butClearText.setFont(new Font("Centaur", Font.ITALIC, 14));
		butClearTable.setFont(new Font("Centaur", Font.ITALIC, 14));
		butExecute.setBackground(Color.cyan);
		butClearText.setBackground(Color.orange);
		butClearTable.setBackground(Color.lightGray);
		Panel pane = new Panel();
		pane.setLayout(new GridLayout());
		pane.add("East", butExecute);
		pane.add("Center", butClearTable);
		pane.add("West", butClearText);
		pane.setVisible(true);
		pCommand.add("Center", txtCommand);
		pCommand.add("South", pane);

		window = new ResultsDisplayWindow();

		setLayout(new BorderLayout());
		pResult.add("Center", window);
		pQuery.add("North", pResult);
		pQuery.add("Center", pCommand);
		fMain.add(pQuery);
		tableschema = new TableSchema();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		if (d.width >= 640) {
			tableschema.setMinimumSize(new Dimension(300, 150));
		} else {
			tableschema.setMinimumSize(new Dimension(275, 125));
		}
		window.setMinimumSize(new Dimension(550, 400));
		tableschema.setVisible(true);
		fMain.add("East", tableschema);
		doLayout();
		fMain.pack();

		Dimension size = fMain.getSize();

		if (d.width >= 640) {
			fMain.setLocation((d.width - size.width) / 2, (d.height - size.height) / 2);
		} else {
			fMain.setLocation(0, 0);
			fMain.setSize(d);
		}
		fMain.setVisible(true);
		txtCommand.requestFocus();
		connect();
		// jdbc:mysql://localhost:3306/company
	}

	void addMenu(MenuBar b, String name, String[] items) {
		Menu menu = new Menu(name);
		for (int i = 0; i < items.length; i++) {
			MenuItem item = new MenuItem(items[i].substring(1));
			char c = items[i].charAt(0);

			if (c != '-') {
				item.setShortcut(new MenuShortcut(c));
			}
			item.addActionListener(this);
			menu.add(item);
		}
		b.add(menu);
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		Object source = e.getSource();
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if (((Frame)e.getSource()).getTitle().equals("MySQL Database Manager") && local) {
			BackupDatabaseDialog bdd = new BackupDatabaseDialog(conn);
			bdd.addWindowListener(this);
		}
		((Frame) source).dispose();
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void executeQuery() {
		if (set != null) {
			try {
				set.close();
			} catch (SQLException e) {
			}
		}
		String query = txtCommand.getText();
		set = DBConnect.executeQuery(conn, query);
		ResultSet copy = DBConnect.executeQuery(conn, query);
		if (copy == null) {
			DBConnect.executeUpdate(conn, query);
		} else {
			window.setData(copy);
		}
		fMain.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if (s.equals("EXECUTE")) {
			if (conn != null) {
				executeQuery();
			}
		} else if (s.equals("Connect...")) {
			connect();
		} else if (s.equals("CLEAR QUERIES")) {
			if (txtCommand.getText() != "") {
				txtCommand.setText("");
			}
		} else if (s.equals("CLEAR TABLE")) {
			window.clearTable();
		} else if (s.equals("Refresh Tables")) {
			if (conn != null) {
				refreshTables();
			}
		} else if (s.equals("Exit")) {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {

				}
			}
			if (set != null) {
				try {
					set.close();
				} catch (SQLException ex) {
				}
			}
			fMain.dispose();
		} else if (s.equals("Open Script...")) {
			SaveResultsDialog getScript = new SaveResultsDialog(this);
		} else if (s.equals("Save Result...")) {
			SaveResultsDialog results = new SaveResultsDialog(set);
		} else if (s.equals("Save Script...")) {
			SaveResultsDialog saveScript = new SaveResultsDialog(txtCommand);
		} else if (s.equals("Import Data...")) {
			final Frame frame = new Frame("Enter table name to import data into");
			frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
			frame.setMinimumSize(fMain.getSize());
			Panel textpane = new Panel();
			Panel buttonpane = new Panel();
			final TextField box = new TextField(20);
			Label description = new Label("Name of table to import data to");
			JButton button = new JButton("Enter table name");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (box.getText().length() == 0) {
						final Frame parent = new Frame();
						parent.setLayout(new FlowLayout());
						JLabel label = new JLabel("Table name cannot be empty!");
						JButton button = new JButton();
						button.setText("OK");
						parent.add(label);
						parent.add(button);
						parent.pack();
						parent.setVisible(true);
						button.addActionListener(new ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent evt) {
								parent.dispose();
							}
						});
					} else {
						ImportDataDialog in = new ImportDataDialog(box.getText(), conn);
						frame.dispose();
					}
				}
			});
			textpane.setLayout(new FlowLayout());
			buttonpane.setLayout(new FlowLayout());
			textpane.add(description);
			textpane.add(box);
			buttonpane.add(button);
			frame.addWindowListener(this);
			frame.add(textpane);
			frame.add(buttonpane);
			frame.pack();
			frame.setVisible(true);
		} else if (s.equals("Backup Database...")) {
			BackupDatabaseDialog bdd = new BackupDatabaseDialog(conn);
			bdd.addWindowListener(this);
		}
	}

	private void connect() {
		connectionDialog = new ConnectionDialog("Connect to Database");
		connectionDialog.setMinimumSize(fMain.getSize());
		connectionDialog.connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!connectionDialog.driver.getSelectedItem().toString().equals("Local operation")) {
						Class.forName(connectionDialog.driver.getSelectedItem().toString());
						conn = DBConnect.getConnect(connectionDialog.dbURL.getText(),
								connectionDialog.username.getText(), connectionDialog.password.getText());
					} else {
						System.out.println("Local operation mode selected");
						local = true;
						if (conn != null) {
							conn.close();
						}
						String driver = "org.apache.derby.jdbc.EmbeddedDriver";
						Class.forName(driver);
						String connectionURL = null;
						if (connectionDialog.dbURL.getText().length() == 0) {
							connectionURL = "jdbc:derby:memory:testDB;create=true";
						} else {
							// jdbc:derby:memory:testDB;restoreFrom=C:\backupdatabase\testDB
							connectionURL = connectionDialog.dbURL.getText();
						}
						conn = DriverManager.getConnection(connectionURL);
						tableschema.removeAll();
						tableschema.update();
					}
					if (conn != null) {
						dMeta = conn.getMetaData();
						refreshTables();
						System.out.println("Successfully connected to database");
					}
					connectionDialog.dispose();
				} catch (ClassNotFoundException ex) {
					System.out.println("Driver does not exist");
				} catch (SQLException e1) {
					System.out.println("SQL error. Could not reach database");
				}
			}
		});
		connectionDialog.addWindowListener(this);
	}

	protected void refreshTables() {
		tableschema.removeAll();
		try {
			int color_table = Color.cyan.getRGB();
			int color_column = Color.magenta.getRGB();
			int color_index = Color.lightGray.getRGB();
			tableschema.addLayer("", dMeta.getURL(), "-", 0);
			String[] usertables = { "TABLE", "GLOBAL TEMPORARY", "VIEW" };
			Vector<String> schemas = new Vector<String>();
			Vector<String> tables = new Vector<String>();
			Vector<String> remarks = new Vector<String>();
			ResultSet result = dMeta.getTables(null, null, null, usertables);
			try {
				while (result.next()) {
					schemas.addElement(result.getString(2));
					tables.addElement(result.getString(3));
					remarks.addElement(result.getString(5));
				}
			} finally {
				result.close();
			}
			for (int i = 0; i < tables.size(); i++) {
				String name = (String) tables.elementAt(i);
				String schema = (String) schemas.elementAt(i);
				String key = "tab-" + name + "-";
				tableschema.addLayer(key, name, "+", color_table);
				String remark = (String) remarks.elementAt(i);
				if ((schema != null) && !schema.trim().equals("")) {
					tableschema.addLayer(key + "s", "schema: " + schema);
				}
				if ((remark != null) && !remark.trim().equals("")) {
					tableschema.addLayer(key + "r", " " + remark);
				}
				ResultSet col = dMeta.getColumns(null, schema, name, null);

				try {
					while (col.next()) {
						String c = col.getString(4);
						String k1 = key + "col-" + c + "-";
						tableschema.addLayer(k1, c, "+", color_column);
						String type = col.getString(6);
						tableschema.addLayer(k1 + "t", "Type: " + type);
						boolean nullable = col.getInt(11) != DatabaseMetaData.columnNoNulls;
						tableschema.addLayer(k1 + "n", "Nullable: " + nullable);
					}
				} finally {
					col.close();
				}
				tableschema.addLayer(key + "ind", "Indices", "+", 0);
				ResultSet ind = dMeta.getIndexInfo(null, schema, name, false, false);
				String oldiname = null;

				try {
					while (ind.next()) {
						boolean nonunique = ind.getBoolean(4);
						String iname = ind.getString(6);
						String k2 = key + "ind-" + iname + "-";
						if ((oldiname == null || !oldiname.equals(iname))) {
							tableschema.addLayer(k2, iname, "+", color_index);
							tableschema.addLayer(k2 + "u", "Unique: " + !nonunique);
							oldiname = iname;
						}
						String c = ind.getString(9);
						tableschema.addLayer(k2 + "c-" + c + "-", c);
					}
				} finally {
					ind.close();
				}
			}
			tableschema.addLayer("p", "Properties", "+", 0);
			tableschema.addLayer("pp", "Product: " + dMeta.getDatabaseProductName());
			tableschema.addLayer("pv", "Version: " + dMeta.getDatabaseProductVersion());
			tableschema.addLayer("pu", "User: " + dMeta.getUserName());
			tableschema.addLayer("pr", "ReadOnly: " + conn.isReadOnly());
			tableschema.addLayer("pa", "AutoCommit: " + conn.getAutoCommit());
			tableschema.addLayer("pd", "Driver: " + dMeta.getDriverName());
		} catch (SQLException e) {
			tableschema.addLayer("", "Error getting metadata:", "-", 0);
			tableschema.addLayer("-", e.getMessage());
			tableschema.addLayer("-", e.getSQLState());
		}
		tableschema.update();
	}

	public static void main(String args[]) {
		new DBManager();
	}
}
