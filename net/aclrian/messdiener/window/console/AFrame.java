package net.aclrian.messdiener.window.console;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import net.aclrian.messdiener.pictures.References;

import java.awt.BorderLayout;

public class AFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5155520960572161077L;
	private JTextArea textArea;

	public AFrame() {
		setTitle("Konsolen-Ausgabe");
		setIconImage(References.getIcon());
		textArea = new JTextArea();
		textArea.setEditable(false);
		getContentPane().add(textArea, BorderLayout.CENTER);
	}
	
	public void appendText(char arg0){
		String text = textArea.getText();
		text = text + "\n" + arg0;
		textArea.setText(text);
	}
	
	public void changeVisibility() {
		setVisible(!isVisible());
	}
	
}
