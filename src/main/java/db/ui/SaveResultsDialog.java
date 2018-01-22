package db.ui;

import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class SaveResultsDialog extends JFrame{
	public SaveResultsDialog(ResultSet set) {		 
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");   		 
		int userSelection = fileChooser.showSaveDialog(this);		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
		    try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave.getAbsolutePath() + ".csv"));
				int columns = set.getMetaData().getColumnCount();
				while(set.next()) {
					String line = new String();
					for(int i = 1; i <= columns; i++) {
						line+=set.getObject(i).toString() + ",";
					}
					bw.write(line + "\n");
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public SaveResultsDialog(TextArea txtCommand) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");   		 
		int userSelection = fileChooser.showSaveDialog(this);	 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
		    BufferedWriter bw = null;
		    try {
				bw = new BufferedWriter(new FileWriter(fileToSave.getAbsolutePath() + ".sql"));
				bw.write(txtCommand.getText());
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public SaveResultsDialog(DBManager manager) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a script to load");   		 
		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File scriptToLoad = fileChooser.getSelectedFile();
		    System.out.println("Loaded file: " + scriptToLoad.getAbsolutePath());
		    try {
		    	String script = new String();
				BufferedReader br = new BufferedReader(new FileReader(scriptToLoad.getAbsolutePath()));
				String line;
				while((line = br.readLine())!=null) {
					script += line + "\n";
				}
				manager.txtCommand.setText(script);
				br.close();	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
