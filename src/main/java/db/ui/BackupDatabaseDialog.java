package db.ui;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class BackupDatabaseDialog extends JFrame{
	public BackupDatabaseDialog(Connection conn) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");
		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			CallableStatement cs;
			try {
				cs = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
				String path = fileToSave.getAbsolutePath();
				cs.setString(1, path);
				cs.execute();
				cs.close();
				this.dispose();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
