package net.aclrian.mpe.panels;

import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.start.WEinFrame;

public class APanel extends JPanel {

	private static final long serialVersionUID = -175407300858987490L;
	private int dfbtnwidth;
	private int dfbtnheight;
	private JButton btnSpeichern = new JButton("Speichern");
	private JButton btnAbbrechen = new JButton("Abbrechen");
	protected Art art;
	private TitledBorder border;
	public final static Font f = new Font("SansSerif", 0, 30);

	public APanel(int defaultButtonwidth, int defaultButtonheight, boolean showCancelAndSave, String title,
			AProgress ap) {
		setLayout(null);
		art = Art.neu;
		//Border b2 = BorderFactory.createLineBorder(WEinFrame.neuhell1);
		border = BorderFactory.createTitledBorder(WEinFrame.b, "<html><body><h1>" + title + "</h1></body></html>",
				TitledBorder.CENTER, TitledBorder.TOP, f, WEinFrame.neuhell1);
		// border.setTitleJustification(TitledBorder.CENTER);
		// border.setTitleColor(WEinFrame.neuhell2);
		// border.setTitleFont(f);
		setBorder(border);
		this.setDfbtnwidth(defaultButtonwidth);
		this.setDfbtnheight(defaultButtonheight);
		if (showCancelAndSave) {
			add(btnSpeichern);
			add(btnAbbrechen);
			addComponentListener(new ComponentListener() {

				@Override
				public void componentShown(ComponentEvent e) {
					graphics();
					setVisible(true);
				}

				@Override
				public void componentResized(ComponentEvent e) {
					graphics();
					setVisible(true);
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					graphics();
					setVisible(true);
				}

				@Override
				public void componentHidden(ComponentEvent e) {
					graphics();
					setVisible(true);
				}
			});
		}
	}

	public Art getArt() {
		return art;
	}

	public void graphics() {
	}

	public int getDfbtnwidth() {
		return dfbtnwidth;
	}

	private void setDfbtnwidth(int dfbtnwidth) {
		this.dfbtnwidth = dfbtnwidth;
	}

	public int getDfbtnheight() {
		return dfbtnheight;
	}

	private void setDfbtnheight(int dfbtnheight) {
		this.dfbtnheight = dfbtnheight;
	}

	public JButton getBtnSpeichern() {
		return btnSpeichern;
	}

	public JButton getBtnAbbrechen() {
		return btnAbbrechen;
	}

	public void setDfbtnBounds(int breite, int width) {
		setDfbtnheight(width);
		setDfbtnwidth(breite);
	}

	public void setArt(Art art) {
		this.art = art;
	}

	public enum Art {
		neu, bearbeiten;
	}

	@Override
	public String toString() {
		return "APANEL:" + super.toString();
	}
}
