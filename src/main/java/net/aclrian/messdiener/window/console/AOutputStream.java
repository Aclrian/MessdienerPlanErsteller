package net.aclrian.messdiener.window.console;

import java.io.IOException;
import java.io.OutputStream;

public class AOutputStream extends OutputStream {

	private AFrame af;


	public AOutputStream() {
		this.af = new AFrame();
	}

	@Override
	public void write(int arg0) throws IOException {
	    if (arg0 == '\r')
	         return;
/*
	      if (arg0 == '\n') {
	         final String text = af.getContentPane().getComponent(0).toString() + "\n";
	         SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	               af.appendText(text);
	            }
	         });
	         af.getContentPane().getComponent(0).setLength(0);
	         //sb.append(title + "> ");
	         return;
	      }}
*/System.out.println((char) arg0);
	      af.appendText((char) arg0);

	}
	
	
	public void changeVisibility() {
		af.changeVisibility();
	}

}
