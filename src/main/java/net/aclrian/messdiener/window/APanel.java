package net.aclrian.messdiener.window;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

public class APanel extends JPanel {

	private static final long serialVersionUID = -175407300858987490L;
	private int dfbtnwidth;
	private int dfbtnheight;
	private JButton btnSpeichern = new JButton("Speichern");
	private JButton btnAbbrechen = new JButton("Abbrechen");
	/**
	 * Create the panel.
	 * @param showCancelAndSave 
	 */
	public APanel(int defaultButtonwidth, int defaultButtonheight, boolean showCancelAndSave) {
		setLayout(null);
		setBorder(WAlleMessen.b);
		this.setDfbtnwidth(defaultButtonwidth);
		this.setDfbtnheight(defaultButtonheight);
		if (showCancelAndSave) {
			add(btnSpeichern);
			add(btnAbbrechen);
		}
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = (Component) e.getSource();
				int Lwidth = c.getBounds().width;
				int Lheigth = c.getBounds().height;
				graphics(Lwidth, Lheigth);
				setVisible(true);
			}
		});
	}

	protected void graphics(int width, int heigth) {
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

}
