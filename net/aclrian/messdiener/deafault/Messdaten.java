package net.aclrian.messdiener.deafault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import net.aclrian.messdiener.window.WMainFrame;

public class Messdaten {
	private ArrayList<Messdiener> geschwister;
	private ArrayList<Messdiener> freunde;
	private int anz_messen;
	private int max_messen;
	private ArrayList<Date> eingeteilt = new ArrayList<Date>();

	public Messdaten(Messdiener m, WMainFrame wmf, int aktdatum) {
		String[] geschwister = m.getGeschwister();
		for (int i = 0; i < geschwister.length; i++) {
			Messdiener medi = null;
			boolean errror = false;
			if (!geschwister[i].equals("")) {
				try {
					medi = wmf.getUtil().sucheMessdiener(geschwister[i], wmf.getAlleMessdiener());
					this.geschwister.add(medi);
				} catch (Exception e) {
					errror = true;
				}
			}

		}
		geschwister = m.getFreunde();
		for (int i = 0; i < geschwister.length; i++) {
			Messdiener medi = null;
			boolean errror = false;
			if (!geschwister[i].equals("")) {
				try {
					medi = wmf.getUtil().sucheMessdiener(geschwister[i], wmf.getAlleMessdiener());
					this.freunde.add(medi);
				} catch (Exception e) {
					errror = true;
				}
			}
		}
		max_messen = berecheMax(m.getEintritt(), aktdatum, m.isIstLeiter());
		anz_messen = 0;
	}

	public int berecheMax(int eintritt, int aktdatum, boolean leiter) {
		if (eintritt < aktdatum) {
			if (leiter) {
				return 1;
			}
			if (eintritt == aktdatum || eintritt + 1 == aktdatum || eintritt + 2 == aktdatum) {
				return 3;
			} else if (eintritt + 3 == aktdatum || eintritt + 4 == aktdatum) {
				return 2;
			} else if (eintritt + 5 == aktdatum || eintritt + 6 == aktdatum) {
				return 1;
			}
			return 1;
		} else {
			return -1;
		}
	}

	public boolean kannnoch() {
		return max_messen > anz_messen;
	}

	public int getMax_messen() {
		return max_messen;
	}

	public int getAnz_messen() {
		return anz_messen;
	}

	public ArrayList<Messdiener> getPrioAnvertraute() {
		// Messdiener[] anv = Array//new Messdiener[geschwister.length +
		// freunde.length];
		ArrayList<Messdiener> rtnmedis = new ArrayList<Messdiener>();
		ArrayList<Messdiener> geschwi = new ArrayList<Messdiener>();
		ArrayList<Messdiener> freunde = new ArrayList<Messdiener>();
		boolean errorgeschwi = false;
		try {
			if (this.geschwister.size() != 0) {
				for (Messdiener messdiener : geschwister) {
					geschwi.add(messdiener);
				}
				Collections.shuffle(geschwi);
			}
		} catch (Exception e) {
			errorgeschwi = true;
		}
		boolean errorfreud = false;
		try {
			if (this.freunde.size() != 0) {
				for (Messdiener messdiener : freunde) {
					freunde.add(messdiener);
				}
				Collections.shuffle(freunde);
			}
		} catch (Exception e) {
			errorfreud = true;
		}
		if (!errorgeschwi) {
			rtnmedis.addAll(geschwi);
		}
		if (!errorfreud) {
			rtnmedis.addAll(freunde);
		}
		return rtnmedis;
	}

	public boolean einteilen(Date date) {
		try {
			if (!this.eingeteilt.contains(date)) {
				eingeteilt.add(date);
				anz_messen++;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public void naechsterMonat() {
		anz_messen = 0;
	}

	
	public ArrayList<Date> getDateVonMessen(){
		return eingeteilt;
	}
}
