package net.aclrian.messdiener.panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AScrollBar extends JScrollBar {

	public AScrollBar(Color background) {
		this.setBackground(background);
		setBackground(background);
		this.setUI(new BasicScrollBarUI()
	    {   
	        @Override
	        protected JButton createDecreaseButton(int orientation) {
	            return createZeroButton();
	        }

	        @Override    
	        protected JButton createIncreaseButton(int orientation) {
	              return createZeroButton();
	        }
	       
	        @Override 
		     protected void configureScrollBarColors(){
		         this.thumbColor = Color.GREEN;
		     }

	    });
	}

	private JButton createZeroButton() {
	    JButton jbutton = new JButton();
	    jbutton.setBackground(this.getBackground());
	    jbutton.setPreferredSize(new Dimension(0, 0));
	    jbutton.setMinimumSize(new Dimension(0, 0));
	    jbutton.setMaximumSize(new Dimension(0, 0));
	    return jbutton;
	}
	
	

}
