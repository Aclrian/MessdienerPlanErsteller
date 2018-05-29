package net.aclrian.messdiener.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Eine klassische Aclrian Klasse: DER ERROROPENER <br>
 * Er zeigt schnelle Fehlermeldungen an!</br>
 *
 * @author Aclrian
 *
 */
public class Erroropener {
	private class MyThread implements Runnable {
		String s;

		public MyThread(String s) {
			this.s = s;
		}

		@Override
		public void run() {
			JOptionPane.showMessageDialog(new JFrame(), s, "Fehler!", 0);
		}
	}


	/**
	 * oeffnet einen JOptionFrame mit vorgegebenen Werten
	 * 
	 * @param error
	 */
	public Erroropener(String error) {
		//this.error = error;
		if (error == "unvollstaendigeeingabe") {
			error = "Bitte alle Felder eintragen!";
		} else if (error == "Wochentag") {
			error = "Bitte den Wochentag gross und richtig schreiben!\nbspw. Montag";
		}
		MyThread mt = new MyThread(error);
		Thread t = new Thread(mt);
		t.start();
	}

	/*
	 * 
	 * @param error gibt einen direkten Fehler aus
	 * 
	 * @param direktefehlercodeausgabe darf null sein
	 *
	 * public Erroropener(String error, Object direktefehlercodeausgabe) { new
	 * MyThread(error); }
	 */

	/**
	 *
	 * error gibt einen direkten Fehler aus
	 * 
	 * @param direktefehlercodeausgabe
	 *            darf null sein
	 * 
	 * @param fehlerausgabe
	 *            in Konsole als throwable
	 * 
	 * @throws Exception
	 */
	public Erroropener(String error, Object object, Object object2) throws Exception {
		throw new Exception(error);
	}

}