package net.aclrian.mpe.components;

import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ARadioButton extends JRadioButton {
/**
	 * 
	 */
	private static final long serialVersionUID = 8176402875258508649L;

	public ARadioButton(String arg0) {
		super(arg0);
	//	setIcon(new ImageIcon(References.class.getResource("radio.png")));
		addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(isSelected()){
	//				setIcon(new ImageIcon(References.class.getResource("radiosel.png")));
				} else{
	//				setIcon(new ImageIcon(References.class.getResource("radio.png")));
				}
				
			}
		});
		setSelected(false);
	}


}
