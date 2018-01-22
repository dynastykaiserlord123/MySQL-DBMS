package db.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.util.Vector;

public class TableSchema extends Panel {
	private static FontMetrics fMetrics;
	private static Font fFont;
	private static int iRowHeight;
	private static int iIndentWidth;
	static {
		fFont = new Font("Centaur", Font.BOLD, 14);
		fMetrics = Toolkit.getDefaultToolkit().getFontMetrics(fFont);
		iRowHeight = getMaxHeight(fMetrics);
		iIndentWidth = 10;
	}
	private Dimension dMinimum;
	private int iMaxTextLength;
	private int iWidth, iHeight;
	private int iSbWidth, iSbHeight;
	private Vector<String[]> vData;
	private Graphics gImage;
	private Image iImage;
	private int iX, iY;
	private int iRowCount;

	public TableSchema() {
		super();
		vData = new Vector<String[]>();
		setLayout(new BorderLayout());
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		iSbHeight = 17;
		iSbWidth = 17;
		iHeight = h - iSbHeight;
		iWidth = w - iSbWidth;
		iImage = null;
		repaint();
	}
	
	public void setMinimumSize(Dimension d) {
		dMinimum = d;
	}

	public Dimension preferredSize() {
		return dMinimum;
	}

	public Dimension minimumSize() {
		return dMinimum;
	}

	public void update(Graphics g) {
		paint(g);
	}
	
	public void update() {
		repaint();
	}

	public void removeAll() {
		vData = new Vector<String[]>();
		iRowCount = 0;
		iMaxTextLength = 10;
		repaint();
	}

	public boolean mouseDown(Event e, int x, int y) {
		if (iRowHeight == 0 || x > iWidth || y > iHeight) {
			return true;
		}
		y += iRowHeight / 2;
		String[] root = new String[100];
		root[0] = "";
		int currentindent = 0;
		int cy = iRowHeight;
		boolean closed = false;
		int i = 0;
		y += iY;
		for (; i < iRowCount; i++) {
			String[] s = (String[]) vData.elementAt(i);
			String key = s[0];
			String folder = s[2];
			int ci = currentindent;

			for (; ci > 0; ci--) {
				if (key.startsWith(root[ci])) {
					break;
				}
			}

			if (root[ci].length() < key.length()) {
				ci++;
			}

			if (closed && ci > currentindent) {
				continue;
			}

			if (cy <= y && cy + iRowHeight > y) {
				break;
			}

			root[ci] = key;
			closed = folder != null && folder.equals("+");
			currentindent = ci;
			cy += iRowHeight;
		}

		if (i >= 0 && i < iRowCount) {
			String[] s = (String[]) vData.elementAt(i);
			String folder = s[2];

			if (folder != null && folder.equals("+")) {
				folder = "-";
			} else if (folder != null && folder.equals("-")) {
				folder = "+";
			}

			s[2] = folder;

			vData.setElementAt(s, i);
			repaint();
		}

		return true;
	}

	public void addLayer(String key, String value, String state, int colour) {
		String[] row = new String[4];

		if (value == null) {
			value = "";
		}

		row[0] = key;
		row[1] = value;
		row[2] = state;
		row[3] = String.valueOf(colour);

		vData.addElement(row);

		int len = fMetrics.stringWidth(value);

		if (len > iMaxTextLength) {
			iMaxTextLength = len;
		}
		iRowCount++;
	}

	public void addLayer(String key, String value) {
		addLayer(key, value, null, 0);
	}

	public void paint(Graphics g) {

		if (g == null || iWidth <= 0 || iHeight <= 0) {
			return;
		}

		g.setColor(SystemColor.control);
		g.fillRect(iWidth, iHeight, iSbWidth, iSbHeight);

		if (iImage == null) {
			iImage = createImage(iWidth, iHeight);
			gImage = iImage.getGraphics();

			gImage.setFont(fFont);
		}

		gImage.setColor(Color.white);
		gImage.fillRect(0, 0, iWidth, iHeight);

		int[] lasty = new int[100];
		String[] root = new String[100];

		root[0] = "";

		int currentindent = 0;
		int y = iRowHeight;

		y -= iY;

		boolean closed = false;

		for (int i = 0; i < iRowCount; i++) {
			String[] s = (String[]) vData.elementAt(i);
			String key = s[0];
			String data = s[1];
			String folder = s[2];
			int ci = currentindent;

			for (; ci > 0; ci--) {
				if (key.startsWith(root[ci])) {
					break;
				}
			}

			if (root[ci].length() < key.length()) {
				ci++;
			}

			if (closed && ci > currentindent) {
				continue;
			}

			closed = folder != null && folder.equals("+");
			root[ci] = key;

			int x = iIndentWidth * ci - iX;

			gImage.setColor(Color.lightGray);
			gImage.drawLine(x, y, x + iIndentWidth, y);
			gImage.drawLine(x, y, x, lasty[ci]);

			lasty[ci + 1] = y;

			int py = y + iRowHeight / 3;
			int px = x + iIndentWidth * 2;

			if (folder != null) {
				lasty[ci + 1] += 4;

				int rgb = Integer.parseInt(s[3]);

				gImage.setColor(rgb == 0 ? Color.white : new Color(rgb));
				gImage.fillRect(x + iIndentWidth - 3, y - 3, 7, 7);
				gImage.setColor(Color.black);
				gImage.drawRect(x + iIndentWidth - 4, y - 4, 8, 8);
				gImage.drawLine(x + iIndentWidth - 2, y, x + iIndentWidth + 2, y);

				if (folder.equals("+")) {
					gImage.drawLine(x + iIndentWidth, y - 2, x + iIndentWidth, y + 2);
				}
			} else {
				px -= iIndentWidth;
			}

			gImage.setColor(Color.black);
			gImage.drawString(data, px, py);

			currentindent = ci;
			y += iRowHeight;
		}

		g.drawImage(iImage, 0, 0, this);
	}

	private static int getMaxHeight(FontMetrics f) {
		return f.getHeight() + 2;
	}
}
