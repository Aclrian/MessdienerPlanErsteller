package net.aclrian.mpe.components;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class ATextField extends JTextField {

    private static final long serialVersionUID = 3035453534855750143L;
    private String title;

    public ATextField(String title) {
	this.setTitle(title);
	setText(title);
	this.addFocusListener(new FocusListener() {

	    @Override
	    public void focusGained(FocusEvent fe) {
		if (getText().equals(title)) {
		    setText("");
		}
	    }

	    @Override
	    public void focusLost(FocusEvent fe) {
		if (getText().equals("")) {
		    setText(title);
		}
	    }
	});
    }

    public String getTitle() {
	return title;
    }

    @Override
    public String getText() {
	if (super.getText().equals(title)) {
	    return "";
	}
	return super.getText();
    }

    public void setTitle(String title) {
	this.title = title;
    }

}
