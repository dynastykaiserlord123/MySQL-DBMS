package db.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class ImportDataDialog extends JFrame {
	public ImportDataDialog(String tablename, Connection conn) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select file to import");
		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			if (!fileToSave.getAbsolutePath().contains(".csv")) {
				throw new IllegalArgumentException("Only .csv files are currently supported");
			}
			try {
				PreparedStatement ps1 = conn.prepareStatement("select * from " + tablename);
				ResultSet set = ps1.executeQuery();
				ResultSetMetaData rsmd = set.getMetaData();
				String schema = "(";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (i == rsmd.getColumnCount()) {
						schema += rsmd.getColumnName(i) + ")";
					} else {
						schema += rsmd.getColumnName(i) + ",";
					}
				}
				String fields = "(?";
				for (int j = 1; j < rsmd.getColumnCount(); j++) {
					fields += ",?";
				}
				fields += ")";
				PreparedStatement ps2 = conn.prepareStatement("insert into " + tablename + schema + " values" + fields);
				BufferedReader br = new BufferedReader(new FileReader(fileToSave.getAbsolutePath()));
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
					if (line != null) {
						String[] values = line.split(",");
						for (int k = 0; k < values.length; k++) {
							try {
								ps2.setInt(k + 1, Integer.parseInt(values[k]));
							} catch (Exception ex) {
								ps2.setString(k + 1, values[k]);
							}
						}
						try {
							ps2.execute();
						} catch (MySQLIntegrityConstraintViolationException ex) {
							System.out.println("Duplicate entry encountered: " + line);
						}
					}
				}
				br.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
