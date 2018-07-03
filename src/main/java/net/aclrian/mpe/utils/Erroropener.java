package net.aclrian.mpe.utils;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.WEinFrame;

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
		if (error == "unvollstaendigeeingabe") {
			error = "Bitte alle Felder eintragen!";
		} else if (error == "Wochentag") {
			error = "Bitte den Wochentag gro"+References.GROssenSZ+" und richtig schreiben!\nbspw. Montag";
		}
		JOptionPane op = new JOptionPane(error, JOptionPane.ERROR_MESSAGE);
		JFrame f = new JFrame();
		JDialog dialog = op.createDialog(f, "Fehler!");
		WEinFrame.farbe(dialog, false);
		dialog.setVisible(true);
	}
}