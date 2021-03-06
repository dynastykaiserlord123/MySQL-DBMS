package db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DBConnect {
	public static Connection getConnect(String url, String username, String password)
			throws ClassNotFoundException, SQLException {
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static void executeUpdate(Connection conn, String query) {
		if (query.length() > 0) {
			try {
				PreparedStatement ps = conn.prepareStatement(query);
				ps.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static ResultSet executeQuery(Connection conn, String query) {
		if (query.length() > 0) {
			try {
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet set = ps.executeQuery();
				return set;
			} catch (SQLException e) {
				return null;
			}
		}
		return null;
	}

	public static TableModel resultSetToTableModel(ResultSet set) {
		try {
			ResultSetMetaData metaData = set.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			Vector<String> columnNames = new Vector<String>();

			for (int column = 0; column < numberOfColumns; column++) {
				columnNames.addElement(metaData.getColumnLabel(column + 1));
			}

			Vector<Vector<Object>> rows = new Vector<Vector<Object>>();

			while (set.next()) {
				Vector<Object> newRow = new Vector<Object>();

				for (int i = 1; i <= numberOfColumns; i++) {
					newRow.addElement(set.getObject(i));
				}

				rows.addElement(newRow);
			}

			return new DefaultTableModel(rows, columnNames);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}
}
