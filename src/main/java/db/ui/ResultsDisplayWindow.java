package db.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.sql.ResultSet;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneLayout;

import db.connection.DBConnect;

public class ResultsDisplayWindow extends Panel {
	private Dimension dMinimum;
	private int iRowHeight;
	private int iGridWidth;
	private int iX, iY;
	private int iSbWidth, iSbHeight;
	private int iWidth, iHeight;
	protected int iRowCount;
	protected String[] sColHead = new String[0];
	protected Font fFont;
	private JTable table;
	private int[] iColWidth;
	private int iColCount;
	private JScrollPane pane;
	public ResultsDisplayWindow() {
		super();
		setLayout(new BorderLayout());
		table = new JTable();
		pane = new JScrollPane(table);
		add(pane, BorderLayout.CENTER);
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		iSbHeight = 0;
		iSbWidth = 0;
		iHeight = h - iSbHeight;
		iWidth = w - iSbWidth;
		repaint();
	}

	public void setData(ResultSet set) {
		remove(pane);
		table.setModel(DBConnect.resultSetToTableModel(set));
		pane = new JScrollPane(table);
		pane.add(table.getTableHeader());		
		add(pane, BorderLayout.CENTER);
		update();
	}

	public void clearTable() {
		remove(pane);
		update();
	}

	public Dimension getPreferredSize() {
		return dMinimum;
	}

	public void setMinimumSize(Dimension d) {
		dMinimum = d;
	}

	public boolean handleEvent(Event e) {
		switch (e.id) {
		case Event.SCROLL_LINE_UP:
		case Event.SCROLL_LINE_DOWN:
		case Event.SCROLL_PAGE_UP:
		case Event.SCROLL_PAGE_DOWN:
		case Event.SCROLL_ABSOLUTE:
			repaint();
			return true;
		}
		return super.handleEvent(e);
	}

	public void update() {
		repaint();
	}
}
