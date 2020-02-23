package net.aclrian.mpe.utils;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.aclrian.mpe.messdiener.Messdaten.CouldnotFindMedi;
//import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.WEinFrame;

/**
 * Eine klassische Aclrian Klasse: DER ERROROPENER <br>
 * Er zeigt schnelle Fehlermeldungen an!</br>
 *
 * @author Aclrian
 *
 */
public class Erroropener {

	private static final String[] actions = { "Jetzt beheben", "Sp�ter" };

	/**
	 * oeffnet einen JOptionFrame mit vorgegebenen Werten
	 * 
	 * @param error
	 */
	@Deprecated
	public Erroropener(Exception error) {
		Dialogs.error(error, "Fehler");
		/*
		 * if (error == "unvollstaendigeeingabe") { error =
		 * "Bitte alle Felder eintragen!"; } else if (error == "Wochentag") { error =
		 * "Bitte den Wochentag gro"+References.
		 * GROssenSZ+" und richtig schreiben!\nbspw. Montag"; }
		 */
		/*TODO 
		 * 
		 * if (error instanceof CouldnotFindMedi) {
			JOptionPane op = new JOptionPane(error.getMessage(), JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION,
					null, actions, 0);
			JFrame f = new JFrame();
			f.setAlwaysOnTop(true);
			JDialog dialog = op.createDialog(f, "Warnung!");
			WEinFrame.farbe(dialog);
			dialog.setVisible(true);
			if (op.getValue() != null && op.getValue() == actions[0]) {
				// if (JOptionPane.showOptionDialog(f, error.getMessage(), "Warnung!",
				// JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, actions,
				// actions[0])== 0) {
				// Freunde
				ArrayList<String> al = new ArrayList<String>(
						Arrays.asList(((CouldnotFindMedi) error).getMessdiener().getFreunde()));
				al.remove(((CouldnotFindMedi) error).getFalscherEintrag());
				((CouldnotFindMedi) error).getMessdiener()
						.setFreunde(al.toArray(((CouldnotFindMedi) error).getMessdiener().getFreunde()));
				// Geschwi
				al = new ArrayList<String>(Arrays.asList(((CouldnotFindMedi) error).getMessdiener().getGeschwister()));
				al.remove(((CouldnotFindMedi) error).getFalscherEintrag());
				((CouldnotFindMedi) error).getMessdiener()
						.setGeschwister(al.toArray(((CouldnotFindMedi) error).getMessdiener().getGeschwister()));
				((CouldnotFindMedi) error).getMessdiener().makeXML(((CouldnotFindMedi) error).getAlleMessdiener(),
						((CouldnotFindMedi) error).getStandartMesse());
				Log.getLogger().info(((CouldnotFindMedi) error).getMessdiener().toString() + " wurde behoben.");
			}*/
	/*	} else {
			JOptionPane op = new JOptionPane(error.getMessage(), JOptionPane.ERROR_MESSAGE);
			JFrame f = new JFrame();
			JDialog dialog = op.createDialog(f, "Fehler!");
			WEinFrame.farbe(dialog);
			dialog.setVisible(true);
		}*/
	}
	/*
	 * private JButton[] getButton() { JButton[] rtn = new JButton[2]; rtn[0] = new
	 * JButton(actions[0]); rtn[1] = new JButton(actions[1]); }
	 */

}