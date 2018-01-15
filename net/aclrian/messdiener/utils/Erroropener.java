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
	/**
	 * oeffnet einen JOptionFrame mit vorgegebenen Werten
	 * @param error
	 */
	public Erroropener(String error) {
		boolean isrunning = true;
		if (error == "unvollstaendigeeingabe") {
			JOptionPane.showMessageDialog(new JFrame(), "Bitte alle Felder eintragen!", "Fehler!", 0);
			isrunning = false;
		} else if (error == "Wochentag") {
			JOptionPane.showMessageDialog(new JFrame(), "Bitte den Wochentag groß und richtig schreiben!\nbspw. Montag",
					"Fehler!", 0);
			isrunning = false;
		} else if (error == "Stunde der Messe groesser als 24") {
			JOptionPane.showMessageDialog(new JFrame(), error, "Fehler!", 0);
			isrunning = false;
		} else if (error == "Minute der Messe groesser als 60") {
			JOptionPane.showMessageDialog(new JFrame(), error, "Fehler!", 0);
			isrunning = false;
		} else if (error == "E-mail ist nicht gueltig.") {
			JOptionPane.showMessageDialog(new JFrame(), error, "Fehler!", 0);
			isrunning = false;
		}
		if (isrunning)
			JOptionPane.showMessageDialog(new JFrame(), "Unbekannter Fehlercode: \"" + error + '"', "Fehler!", 0);
	}
/**
 * 
 * @param error gibt einen direkten Fehler aus
 * @param direktefehlercodeausgabe darf null sein
 */
	public Erroropener(String error, Object direktefehlercodeausgabe) {
		JOptionPane.showMessageDialog(new JFrame(), error, "Fehler!", 0);
	}
}