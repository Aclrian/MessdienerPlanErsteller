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
import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.pfarrei.Manuell.EnumWorking;
import net.aclrian.mpe.pfarrei.Manuell.Handling;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.Utilities;

public class AProgress {

	private AData ada;
	public static final String VersionID = "b660";
	private ArrayList<Messdiener> memit;
	WEinFrame wam;

	public AProgress(boolean startWeF) {
		ada = new AData(this);
		if (startWeF) {
			wam = new WEinFrame(this);
		}
	}

	public AData getAda() {
		return ada;
	}

	public ArrayList<Messdiener> getMediarraymitMessdaten() {
		if (memit != null) {
			update();
			return memit;
		}
		memit = ada.getUtil().getAlleMedisVomOrdnerAlsList(ada.getSavepath(), ada);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : memit) {
			messdiener.setnewMessdatenDaten(ada.getUtil().getSavepath(), year, this);
		}
		update();
		return memit;
	}

	private void update() {
		for (Messdiener medi : ada.getMediarray()) {
			for (Messdiener mmedi : memit) {
				if (medi.toString().equals(mmedi.toString())) {
					Messdaten md = mmedi.getMessdatenDaten();
					md.update(medi, this);
				}
			}
		}
	}

	public void start() {
		wam.setVisible(true);
	}

	public Object[][] getAbmeldenTableVector(String[] s, SimpleDateFormat df) {
		getMediarraymitMessdaten().sort(Messdiener.compForMedis);
		Object[][] rtn = new Object[getMediarraymitMessdaten().size()][s.length];
		for (int i = 0; i < getMediarraymitMessdaten().size(); i++) {
			Object[] o = rtn[i];
			Messdiener m = getMediarraymitMessdaten().get(i);
			o[0] = m.toString();
			for (int j = 1; j < s.length; j++) {
				o[j] = m.getMessdatenDaten().ausgeteilt(s[j]);
			}
		}
		return rtn;
	}

	public WEinFrame getWAlleMessen() {
		return wam;
	}

	public ArrayList<Messe> optimieren(Calendar cal, StandartMesse sm, Date end, ArrayList<Messe> mes)
			throws Exception {
		if (cal.getTime().before(end) && !AData.sonstiges.isSonstiges(sm)) {
			SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
			String tag = wochendagformat.format(cal.getTime());
			if (tag.startsWith(sm.getWochentag())) {
				Date d = cal.getTime();
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				SimpleDateFormat dfuhr = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
				try {
					Date frting = dfuhr
							.parse(df.format(d) + "-" + sm.getBeginn_stundealsString() + ":" + sm.getBeginn_minute());
					Messe m = new Messe(frting, sm, ada);
					mes.add(m);
				} catch (Exception e) {
					new Erroropener(e);
					e.printStackTrace();
				}
				cal.add(Calendar.DATE, 7);
				mes = optimieren(cal, sm, end, mes);
			} else {
				cal.add(Calendar.DATE, 1);
				mes = optimieren(cal, sm, end, mes);
			}
		}
		return mes;
	}

	public ArrayList<Messe> generireDefaultMessen(Date anfang, Date ende) throws Exception {
		ArrayList<Messe> rtn = new ArrayList<Messe>();
		Calendar start = Calendar.getInstance();
		for (StandartMesse sm : ada.getPfarrei().getStandardMessen()) {
			if (!AData.sonstiges.isSonstiges(sm)) {
				start.setTime(anfang);
				ArrayList<Messe> m = optimieren(start, sm, ende, new ArrayList<Messe>());
				rtn.addAll(m);
			}
		}
		rtn.sort(Messe.compForMessen);
		Utilities.logging(getClass(), "generireDefaultMessen", "DefaultMessen generiert");
		return rtn;
	}

	public ArrayList<Messdiener> getLeiter(ArrayList<Messdiener> param) {
		ArrayList<Messdiener> rtn = new ArrayList<Messdiener>();
		for (Messdiener messdiener : param) {
			if (messdiener.isIstLeiter()) {
				rtn.add(messdiener);
			}
		}
		return rtn;
	}

	public Messdiener getMessdienerFromString(String valueAt, ArrayList<Messdiener> arrayList) {
		for (Messdiener messdiener : arrayList) {
			if (messdiener.toString().equals(valueAt)) {
				return messdiener;
			}
		}
		return null;
	}

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
				JOptionPane op = new JOptionPane("Das Programm wird nun beendet und mit den neuen Daten gestartet!",
						JOptionPane.INFORMATION_MESSAGE);
				JFrame f = new JFrame();
				f.setAlwaysOnTop(true);
				JDialog dialog = op.createDialog(f, "Warnung!");
				WEinFrame.farbe(dialog);
				dialog.setVisible(true);
				System.exit(0);
			} else {
				String name = pf.getName();
				// WARUM?: name = name.replaceAll(" ", "_");
				File f = new File(savepath, name + AData.pfarredateiendung);
				f.delete();
				name = name.replaceAll(" ", "_");
				File f2 = new File(savepath, name + AData.pfarredateiendung);
				f2.delete();
				ada.setPfarrei(pf);
				WriteFile_Pfarrei.writeFile(pf, savepath);
			}
		} catch (NullPointerException e) {
		}
		WriteFile_Pfarrei.writeFile(pf, savepath);
		ada.erneuern(ap, savepath);
		wfp.dispose();
		wam = new WEinFrame(this);
		start();
	}

	private void remove(ArrayList<StandartMesse> smarray, StandartMesse sm) {
		for (int i = 0; i < smarray.size(); i++) {
			StandartMesse messe = smarray.get(i);
			if (messe.toString().equals(sm.toString())) {
				smarray.remove(i);
			}
		}
	}

	public void setNeu(boolean startWeF) {
		wam.dispose();
		AProgress ap = new AProgress(startWeF);
		ap.start();
	}
}
