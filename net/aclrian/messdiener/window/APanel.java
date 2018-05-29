package net.aclrian.messdiener.window;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

public class APanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public APanel() {
		setLayout(null);
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
	
	

}
