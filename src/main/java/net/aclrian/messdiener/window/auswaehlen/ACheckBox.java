package net.aclrian.messdiener.window.auswaehlen;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.aclrian.messdiener.pictures.References;

public class ACheckBox extends JCheckBox{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7515003839963796738L;

	public ACheckBox(String title) {
		this();
		setText(title);	
	}

	public ACheckBox() {
		super();
		setIcon(new ImageIcon(References.class.getResource("unselect.png")));
		addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(isSelected()){
					setIcon(new ImageIcon(References.class.getResource("select.png")));
				} else{
					setIcon(new ImageIcon(References.class.getResource("unselect.png")));
				}
				
			}
		});
	}
	
	@Override
	public String toString() {
		return getText();
	}
}
