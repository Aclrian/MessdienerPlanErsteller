package net.aclrian.mpe.start;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Manuell;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.WriteFile_Pfarrei;
import net.aclrian.mpe.start.References;
import net.aclrian.mpe.pfarrei.Manuell.EnumWorking;
import net.aclrian.mpe.pfarrei.Manuell.Handling;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.Utilities;

public class AProgress {

	public void pfarreiaendern(Pfarrei pf) {
		WriteFile_Pfarrei wf = new WriteFile_Pfarrei(pf, this);
		if (wam != null) {
			wam.setVisible(false);
			wam.dispose();// Geht das?
		}
		WEinFrame.farbe(wf);
		wf.setVisible(true);
	}

	public void getPfarreiNeu() {
		WriteFile_Pfarrei wf = new WriteFile_Pfarrei(this);
		if (wam != null) {
			wam.setVisible(false);
		}
		WEinFrame.farbe(wf);
		wf.setVisible(true);
	}

	public void fertig(WriteFile_Pfarrei wfp, Pfarrei pf, String savepath, AProgress ap) {
		try {
			ada.setPfarrei(ada.getUtil().getPfarrei());
			if (!ada.getPfarrei().getStandardMessen().toString().equals(pf.getStandardMessen().toString())) {
				ArrayList<StandartMesse> beibehalten = new ArrayList<StandartMesse>();
				ArrayList<StandartMesse> verbleibende = new ArrayList<StandartMesse>();
				for (StandartMesse sm : pf.getStandardMessen()) {
					boolean b = true;
					for (StandartMesse smold : ada.getPfarrei().getStandardMessen()) {
						if (sm.toString().equals(smold.toString())) {
							beibehalten.add(smold);
							b = false;
							break;
						}
					}
					if (b) {
						verbleibende.add(sm);
					}

				}
				ArrayList<StandartMesse> alte = new ArrayList<StandartMesse>();
				ArrayList<String> alteS = new ArrayList<String>();
				for (StandartMesse alt : ada.getPfarrei().getStandardMessen()) {
					boolean b = false;
					for (StandartMesse sm : beibehalten) {

						if (sm.toString().equals(alt.toString())) {
							b = true;
							break;
						}
					}
					if (!b) {
						alte.add(alt);
						alteS.add(References.Ue + "berarbeitete " + alt.toBenutzerfreundlichenString());
					}

				}
				ArrayList<Handling> hand = new ArrayList<Handling>();
				alteS.add("Neu");
				for (StandartMesse sm : verbleibende) {
					boolean fertig = false;
					do {

						Object[] possibilities = new Object[0];
						possibilities = alteS.toArray(possibilities);
						String s = (String) JOptionPane.showInputDialog(null,
								sm.toString() + " ist neu.\nIst sie wirkilch neu oder wurde sie nur " + References.ue
										+ "berarbeitet?",
								"Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, possibilities, "Neu");

						if ((s != null) && (s.length() > 0)) {
							fertig = true;
							EnumWorking ew;
							StandartMesse alt = null;
							if (s.equals("Neu")) {
								alt = null;
								ew = EnumWorking.neu;
							} else {
								for (StandartMesse sm1 : alte) {
									if (s.endsWith(sm1.toString())) {
										remove(alte, sm1);
										ArrayList<String> aS = new ArrayList<String>();
										for (String string : alteS) {
											if (!string.equals(s)) {
												aS.add(string);
											}
										}
										possibilities = aS.toArray(possibilities);

										alt = sm1;
										break;
									}
								}
								if (alt == null) {
									fertig = false;
								}
								ew = EnumWorking.ueberarbeitet;
							}
							Handling h = new Handling(sm, ew, alt);
							hand.add(h);
						}
					} while (fertig != true);
				}
				ArrayList<StandartMesse> bleibende = new ArrayList<StandartMesse>();
				bleibende.addAll(beibehalten);
				Manuell m = new Manuell(hand, ap, pf);
				m.act();
				WriteFile_Pfarrei.writeFile(pf, savepath);
			//	JOptionPane op = new JOptionPane("Das Programm wird nun beendet und mit den neuen Daten gestartet!",
				//		JOptionPane.INFORMATION_MESSAGE);
			//	JFrame f = new JFrame();
				//f.setAlwaysOnTop(true);
				//JDialog dialog = op.createDialog(f, "Warnung!");
				//WEinFrame.farbe(dialog);
				//dialog.setVisible(true);
				System.exit(0);
			} else {
				String name = pf.getName();
				// WARUM?: name = name.replaceAll(" ", "_");
				File f = new File(savepath, name + DateienVerwalter.pfarredateiendung);
				f.delete();
				name = name.replaceAll(" ", "_");
				File f2 = new File(savepath, name + DateienVerwalter.pfarredateiendung);
				f2.delete();
				//ada.setPfarrei(pf);
				WriteFile_Pfarrei.writeFile(pf, savepath);
			}
		} catch (NullPointerException e) {
		}
		WriteFile_Pfarrei.writeFile(pf, savepath);
		//ada.erneuern(ap, savepath);
//		wfp.dispose();
	//	wam = new WEinFrame(this);
		//start();
	}

	private void remove(ArrayList<StandartMesse> smarray, StandartMesse sm) {
		for (int i = 0; i < smarray.size(); i++) {
			StandartMesse messe = smarray.get(i);
			if (messe.toString().equals(sm.toString())) {
				smarray.remove(i);
			}
		}
	}

	/*
	 * public void setNeu(boolean startWeF) { wam.dispose(); AProgress ap = new
	 * AProgress(startWeF); ap.start(); }
	 */
}
