package net.aclrian.messdiener.utils;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.window.WAlleMessen;

/**
 * Eine klassische Aclrian Klasse: DER ERROROPENER <br>
 * Er zeigt schnelle Fehlermeldungen an!</br>
 *
 * @author Aclrian
 *
 */
public class Erroropener {


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
		JOptionPane op = new JOptionPane(error, JOptionPane.ERROR_MESSAGE);
		JFrame f = new JFrame();
		JDialog dialog = op.createDialog(f, "Fehler!");
		//farbe(op, false);
		WAlleMessen.farbe(dialog, false);
		dialog.setVisible(true);
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