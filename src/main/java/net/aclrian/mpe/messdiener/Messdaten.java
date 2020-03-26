package net.aclrian.mpe.messdiener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javafx.stage.Window;
import net.aclrian.mpe.controller.MediController;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.utils.*;

public class Messdaten {

	private ArrayList<Messdiener> geschwister;
	private ArrayList<Messdiener> freunde;
	private int anz_messen;
	private int insgesamtEingeteilt;
	private int max_messen;
	private ArrayList<Date> eingeteilt = new ArrayList<>();
	private ArrayList<Date> ausgeteilt = new ArrayList<>();
	private ArrayList<Date> pause = new ArrayList<>();
	RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();

	public Messdaten(Messdiener m) {
		geschwister = new ArrayList<>();
		freunde = new ArrayList<>();
		update(m);
		max_messen = berecheMax(m.getEintritt(), getMaxYear(), m.isIstLeiter(),
				DateienVerwalter.dv.getPfarrei().getSettings());
		anz_messen = 0;
		insgesamtEingeteilt = 0;
	}

	public static int getMinYear() {
		return getMaxYear() - 18;
	}

	public static int getMaxYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public void austeilen(Date d) {
		ausgeteilt.add(d);
	}

	public void ausausteilen(Date d){
		ausgeteilt.remove(d);
	}

	public int berecheMax(int eintritt, int aktdatum, boolean leiter, Einstellungen einst) {
		int id = 0;
		if (leiter) {
			id++;
		}
		int eins;
		int zwei = einst.getDaten(id).getAnz_dienen();

		int abstand = aktdatum - eintritt;
		if (abstand < 0) {
			abstand = 0;
		}
		if (abstand >= Einstellungen.lenght) {
			abstand = Einstellungen.lenght - 3;
		}
		eins = einst.getDaten(abstand + 2).getAnz_dienen();
		if (zwei < eins) {
			eins = zwei;
		}

		return eins;
	}

	public void einteilen(Date date, boolean hochamt) {
			if (kann(date, false)) {
				eingeteilt.add(date);
				pause.add(gettheNextDay(date));
				pause.add(getthepreviuosDay(date));
				anz_messen++;
				insgesamtEingeteilt++;
				if (hochamt) {
					anz_messen--;
				}
			}
	}


	public void einteilenVorzeitig(Date date, boolean hochamt) {
			if (kannvorzeitg(date)) {
				eingeteilt.add(date);
				insgesamtEingeteilt++;
				if (!hochamt) {
					pause.add(gettheNextDay(date));
					anz_messen++;
				}
			}
	}

	public void nullen() {
		anz_messen = 0;
		insgesamtEingeteilt = 0;
		eingeteilt = new ArrayList<>();
		pause = new ArrayList<>();
	}

	public ArrayList<Messdiener> getAnvertraute(ArrayList<Messdiener> medis) {
		ArrayList<Messdiener> rtn = new ArrayList<>();
		rd.removeDuplicatedEntries(geschwister);
		rd.removeDuplicatedEntries(freunde);

		for (int i = 0; i < geschwister.size(); i++) {
			for (int j = 0; j < medis.size(); j++) {
				if (geschwister.get(i).makeId().equals(medis.get(j).makeId())) {
					rtn.add(medis.get(j));
				}
			}
		}
		for (int i = 0; i < freunde.size(); i++) {
			for (int j = 0; j < medis.size(); j++) {
				if (freunde.get(i).makeId().equals(medis.get(j).makeId())) {
					rtn.add(medis.get(j));
				}
			}
		}

		rd.removeDuplicatedEntries(rtn);
		return rtn;
	}

	public double getMax_messen() {
		return (max_messen == 0) ? 0.001 : max_messen;
	}

	public int getMax_messenInt() {
		return max_messen;
	}

	public boolean kannvorzeitg(Date date) {
		return !contains(date, ausgeteilt);
	}

	public boolean kann(Date date, boolean zwang) {
		boolean eins = kanndann(date, zwang);
		boolean zwei = kannnoch();
		return eins  && zwei;
	}

	public boolean kanndann(Date date, boolean zwang) {
		if (eingeteilt.isEmpty() && ausgeteilt.isEmpty()) {
			if (zwang || pause.isEmpty()) {
				return true;
			}
		}
		if (contains(date, eingeteilt) || contains(gettheNextDay(date), eingeteilt)
				|| contains(getthepreviuosDay(date), eingeteilt)) {
			return false;
		}
		if (contains(date, ausgeteilt) || contains(gettheNextDay(date), ausgeteilt)
				|| contains(getthepreviuosDay(date), ausgeteilt)) {
			return false;
		}
		if (!zwang) {
			return !contains(date, pause);
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

	public boolean ausgeteilt(String sdate) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		for (Date d : ausgeteilt) {
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
		if (!contains(date, ausgeteilt)) {
			eingeteilt.add(date);
			anz_messen++;
			insgesamtEingeteilt++;
			if (hochamt) {
				anz_messen--;
			}
		}
	}

	private Date gettheNextDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	private Date getthepreviuosDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	public void loescheAbwesendeDaten() {
		ausgeteilt = new ArrayList<>();
	}

	public void addtomaxanz(int medishinzufuegen, boolean isLeiter) {
		max_messen += medishinzufuegen;
		int id = 0;
		if (isLeiter) {
			id++;
		}
		if (max_messen > DateienVerwalter.dv.getPfarrei().getSettings().getDaten(id).getAnz_dienen()) {
			max_messen = DateienVerwalter.dv.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
		}
	}

	public double getSortierenDouble() {
		return anz_messen / getMax_messen();
	}

	public int getInsgesamtEingeteilt() {
		return insgesamtEingeteilt;
	}

	public void update(Messdiener m) {
		update(false, m.getGeschwister(), m);
		update(true, m.getFreunde(), m);
	}

	private void update(boolean isfreund, String[] s, Messdiener m) {
		for (String value : s) {
			Messdiener medi;
			if (!value.equals("") && !value.equals("LEER") && !value.equals("Vorname, Nachname")) {
				try {
					medi = sucheMessdiener(value, m, DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList());
					if (medi != null) {
						this.geschwister.add(medi);
						rd.removeDuplicatedEntries(this.geschwister);
					}
				} catch (CouldnotFindMedi e) {
					boolean beheben = Dialogs.frage(e.getMessage(),
							"ignorieren", "beheben");
					if (beheben) {
						ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
						list.remove(value);
						String[] gew = MediController.getArrayString(list, isfreund?Messdiener.freundelenght:Messdiener.geschwilenght);
						if(isfreund) m.setFreunde(gew);
						else m.setGeschwister(gew);
						WriteFile wf = new WriteFile(m);
						try {
							wf.toXML();
						} catch (IOException ex) {
							Dialogs.error(ex, "Konnte es nicht beheben.");
						}
						DateienVerwalter.dv.reloadMessdiener();
						update(isfreund, gew, m);
						return;
					}
				}
			}
		}
	}

	public Messdiener sucheMessdiener(String geschwi, Messdiener akt, ArrayList<Messdiener> medis) throws CouldnotFindMedi {
		for (Messdiener messdiener : medis) {
			if (messdiener.makeId().equals(geschwi)) {
				return messdiener;
			}
		}
		throw new CouldnotFindMedi("Konnte für " + akt.makeId() + " : " + geschwi + " nicht finden",
				akt, geschwi);
	}

	public static class CouldnotFindMedi extends Exception {
		private Messdiener me;
		private String falscherEintrag;

		public CouldnotFindMedi(String message, Messdiener me, String falscherEintrag) {
			super(message);
			this.me = me;
			this.falscherEintrag = falscherEintrag;
		}

		public Messdiener getMessdiener() {
			return me;
		}

		public String getFalscherEintrag() {
			return falscherEintrag;
		}
	}
}
