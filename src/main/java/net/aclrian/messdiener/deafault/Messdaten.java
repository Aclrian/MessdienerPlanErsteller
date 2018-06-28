package net.aclrian.messdiener.deafault;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import net.aclrian.messdiener.differenzierung.Einstellungen;
import net.aclrian.messdiener.differenzierung.Setting;
import net.aclrian.messdiener.start.AData;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;

public class Messdaten {
	private ArrayList<Messdiener> geschwister;
	private ArrayList<Messdiener> freunde;
	private int anz_messen;
	private int insgesamtEingeteilt;

	public int getInsgesamtEingeteilt() {
		return insgesamtEingeteilt;
	}

	private int max_messen;
	private ArrayList<Date> eingeteilt = new ArrayList<Date>();
	private ArrayList<Date> ausgeteilt = new ArrayList<Date>();
	private ArrayList<Date> pause = new ArrayList<Date>();

	public Messdaten(Messdiener m, AData ada, int aktdatum) {
		geschwister = new ArrayList<Messdiener>();
		freunde = new ArrayList<Messdiener>();
		String[] geschwister = m.getGeschwister();
		for (int i = 0; i < geschwister.length; i++) {
			Messdiener medi = null;
			if (!geschwister[i].equals("") && !geschwister[i].equals("LEER")
					&& !geschwister[i].equals("Vorname, Nachname")) {
				try {
					medi = ada.getUtil().sucheMessdiener(geschwister[i], m, ada);
					if (medi != null) {
						this.geschwister.add(medi);
						Messdaten.removeDuplicatedEntries(this.geschwister);
					} else {
						new Erroropener(
								"Konnte " + geschwister[i] + " nicht finden fuer Anvertraute von: " + m.makeId() + "!");
					}
				} catch (Exception e) {
					new Erroropener(e.getMessage());
					Utilities.logging(this.getClass(), this.getClass().getEnclosingConstructor(), e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		geschwister = m.getFreunde();
		for (int i = 0; i < geschwister.length; i++) {
			Messdiener medi = null;
			if (!geschwister[i].equals("")) {
				try {
					medi = ada.getUtil().sucheMessdiener(geschwister[i], m, ada);
				} catch (Exception e) {
					new Erroropener(e.getMessage());
					e.printStackTrace();
				}
				if (medi != null) {
					freunde.add(medi);
					Messdaten.removeDuplicatedEntries(freunde);
				}
			}
		}
		max_messen = berecheMax(m.getEintritt(), aktdatum, m.isIstLeiter(), ada.getPfarrei().getSettings());
		anz_messen = 0;
		insgesamtEingeteilt = 0;
	}

	public void austeilen(Date d) {
		ausgeteilt.add(d);
	}

	public int berecheMax(int eintritt, int aktdatum, boolean leiter, Einstellungen einst) {
		int id = 0;
		if (leiter) {
			id++;
		}
		int eins;
		int zwei = einst.getDaten()[id].getAnz_dienen();
		if (eintritt < aktdatum) {
			int abstand = aktdatum - eintritt;
			if (abstand >= Einstellungen.lenght) {
				abstand = Einstellungen.lenght - 1;
			}
			eins = einst.getDaten()[abstand + 2].getAnz_dienen();
			if (zwei < eins) {
				eins = zwei;
			}
		} else {
			eins = zwei;
		}
		return eins;
	}

	public void einteilen(Date date, boolean hochamt) {
		try {
			if (kann(date, false)) {
				eingeteilt.add(date);
				pause.add(gettheNextDay(date));
				anz_messen++;
				insgesamtEingeteilt++;
				if (hochamt) {
					anz_messen--;
				}
			}
		} catch (Exception e) {
			new Erroropener(e.getMessage());
			e.printStackTrace();
		}
	}

	public void einteilenVorzeitig(Date date, boolean hochamt, Messdiener medi, AData ada) {
		try {
			if (kannvorzeitg(date, medi.isIstLeiter(), ada)) {
				eingeteilt.add(date);
				pause.add(gettheNextDay(date));
				anz_messen++;
				insgesamtEingeteilt++;
				if (hochamt) {
					anz_messen--;
				}
			}
		} catch (Exception e) {
			new Erroropener(e.getMessage());
			e.printStackTrace();
		}
	}

	public int getAnz_messen() {
		return anz_messen;
	}

	/*
	 * @Deprecated public ArrayList<Messdiener> getPrioAnvertraute() { //
	 * Messdiener[] anv = Array//new Messdiener[geschwister.length + //
	 * freunde.length]; ArrayList<Messdiener> rtnmedis = new
	 * ArrayList<Messdiener>(); ArrayList<Messdiener> geschwi = new
	 * ArrayList<Messdiener>(); ArrayList<Messdiener> freunde = new
	 * ArrayList<Messdiener>(); boolean errorgeschwi = false; try { if
	 * (geschwister.size() != 0) { for (Messdiener messdiener : geschwister) {
	 * geschwi.add(messdiener); } Collections.shuffle(geschwi); } } catch (Exception
	 * e) { errorgeschwi = true; } boolean errorfreud = false; try { if
	 * (this.freunde.size() != 0) { for (Messdiener messdiener : freunde) {
	 * freunde.add(messdiener); } Collections.shuffle(freunde); } } catch (Exception
	 * e) { errorfreud = true; } if (!errorgeschwi) { rtnmedis.addAll(geschwi); } if
	 * (!errorfreud) { rtnmedis.addAll(freunde); } return rtnmedis; }
	 */

	public ArrayList<Date> getDateVonMessen() {
		return eingeteilt;
	}

	public ArrayList<Messdiener> getFreunde() {
		return freunde;
	}

	public ArrayList<Messdiener> getGeschwister() {
		return geschwister;
	}

	public ArrayList<Messdiener> getAnvertraute() {
		ArrayList<Messdiener> rtn = geschwister;
		rtn.addAll(freunde);
		rtn.sort(Messdiener.einteilen);
		removeDuplicatedEntries(rtn);
		return rtn;
	}

	public ArrayList<Messdiener> getAnvertraute(ArrayList<Messdiener> medis) {
		ArrayList<Messdiener> rtn = new ArrayList<Messdiener>();
		Messdaten.removeDuplicatedEntries(geschwister);
		Messdaten.removeDuplicatedEntries(freunde);

		for (int i = 0;i<geschwister.size();i++) {
			for (int j = 0;j<medis.size();j++) {
				if (geschwister.get(i).makeId().equals(medis.get(j).makeId())) {
					rtn.add(medis.get(j));
				}
			}
		}
		for (int i = 0;i<freunde.size();i++) {
			for (int j = 0;j<medis.size();j++) {
				if (freunde.get(i).makeId().equals(medis.get(j).makeId())) {
					rtn.add(medis.get(j));
				}
			}
		}
		removeDuplicatedEntries(rtn);
		return rtn;
	}

	public static void removeDuplicatedEntries(ArrayList<Messdiener> arrayList) {
		HashSet<Messdiener> hashSet = new HashSet<Messdiener>(arrayList);
		arrayList.clear();
		arrayList.addAll(hashSet);
	}

	public double getMax_messen() {
		double rtn = (max_messen == 0) ? 0.001 : max_messen;
		return rtn;
	}

	public int getMax_messenInt() {
		return max_messen;
	}

	public boolean kannvorzeitg(Date date, boolean leiter, AData ada) {
		boolean eins = kanndann(date, false);
		boolean zwei = kannnoch();
		if (leiter && ada.getPfarrei().getSettings().getDaten()[1].getAnz_dienen() == 0) {
			zwei = true;
		}
		if (eins == zwei == true) {
			return eins;
		} else {
			return false;
		}
	}

	public boolean kann(Date date, boolean zwang) {
		boolean eins = kanndann(date, zwang);
		boolean zwei = kannnoch();
		if (eins == zwei == true) {
			return eins;
		} else {
			return false;
		}
	}

	public boolean kanndann(Date date, boolean zwang) {
		if (eingeteilt.isEmpty() && ausgeteilt.isEmpty()) {
			if (zwang || pause.isEmpty()) {
				return true;
			}
		}
		System.out.println(eingeteilt.toString() + ausgeteilt.toString() + pause.toString() + ":" + date.toString());
		if (contains(date, eingeteilt) || contains(gettheNextDay(date), eingeteilt)) {
			return false;
		}
		if (contains(date, ausgeteilt) || contains(gettheNextDay(date), ausgeteilt)) {
			// System.out.print(date + " kann dann nicht! " + getGeschwister().get(0));
			return false;
		}
		if (!zwang) {
			if (contains(date, pause) || contains(gettheNextDay(date), pause)) {
				return false;
			}
		}
		return true;
	}

	private boolean contains(Date date, ArrayList<Date> array) {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String sdate = df.format(date);
		for (Date d : array) {
			if (df.format(d).equals(sdate)) {
				return true;
			}
		}
		return false;
	}

	private boolean kannnoch() {
		return max_messen > anz_messen;
	}

	public void naechsterMonat() {
		anz_messen = 0;
	}

	public void einteilenZwang(Date date, boolean hochamt) {
		try {
			if (!contains(date, ausgeteilt)) {
				eingeteilt.add(date);
				// Date day = gettheNextDay(date);
				// pause.add(day);
				anz_messen++;
				insgesamtEingeteilt++;
				if (hochamt) {
					anz_messen--;
				}
			}
		} catch (Exception e) {
			new Erroropener(e.getMessage());
			e.printStackTrace();
		}

	}

	private Date gettheNextDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	public void loescheAbwesendeDaten() {
		ausgeteilt = new ArrayList<Date>();
	}

	public void addtomaxanz(int medishinzufuegen, AData ada, boolean isLeiter) {
		max_messen += medishinzufuegen;
		Setting[] set = ada.getPfarrei().getSettings().getDaten();
		int id = 0;
		if (isLeiter) {
			id++;
		}
		if (max_messen > set[id].getAnz_dienen()) {
			max_messen = set[id].getAnz_dienen();
		}
	}

	public int getkannnochAnz() {
		return max_messen - anz_messen;
	}

	public double getSortierenDouble() {
		double rtn = getAnz_messen() / getMax_messen();
		return rtn;
	}
}
