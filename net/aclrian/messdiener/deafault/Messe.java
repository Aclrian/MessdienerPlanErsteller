package net.aclrian.messdiener.deafault;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.WMainFrame;

/**
 * Klasse von Messen
 *
 * @author Aclrian
 *
 */
public class Messe {
	@Override
	public String toString() {
		return getID();
	}

	public static final Comparator<Messe> compForMessen = new Comparator<Messe>() {

		@Override
		public int compare(Messe o1, Messe o2) {
			Date do1 = o1.getDate();
			Date do2 = o2.getDate();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

			return df.format(do1).compareToIgnoreCase(df.format(do2));
		}
	};
	private String Wochentag;
	private boolean hochamt;
	private int anz_messdiener;
	private Date date;
	private String kirche;
	private SimpleDateFormat df = new SimpleDateFormat("EEE", Locale.GERMAN);
	private SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.");
	private SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
	@SuppressWarnings("unused")
	private SimpleDateFormat dfall = new SimpleDateFormat("EEE dd.MM.yyyy:HH:ss", Locale.GERMAN);
	private StandartMesse em = new Sonstiges();
	private String typ;
	private ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
	private ArrayList<Messdiener> leiter = new ArrayList<Messdiener>();
	private String titel = "";

	public Messe(boolean hochamt, int anz_medis, Date datummituhrzeit, String ort, String typ, String titel,
			WMainFrame wmf) {
		if (wmf.getPfarrei().getOrte().contains(ort) && wmf.getPfarrei().getTypen().contains(typ)) {
			bearbeiten(hochamt, anz_messdiener, date, kirche, typ, wmf);
			this.titel = titel;
		}else{
			Utilities.logging(getClass(), getClass().getEnclosingConstructor(), "da ging was schief");
		}

	}

	public Messe(Date d, StandartMesse sm, WMainFrame wmf) {
		boolean isdrin = false;
		for (StandartMesse stm : wmf.getSMoheSonstiges()) {
			if (sm.toString().equals(stm.toString())) {
				isdrin = true;
				break;
			}
		}
		if (isdrin) {
			bearbeiten(false, sm.getAnz_messdiener(), d, sm.getOrt(), sm.getTyp(), wmf);
		}else{
			Utilities.logging(getClass(), getClass().getEnclosingConstructor(), "da ging was schief");
		}
	}

	/**
	 *
	 * @param anfang_stunde
	 * @param anfang_minute
	 * @param hochamt
	 * @param anz_messdiener
	 * @param anz_leiter
	 *            unbeachteet lassen, wenn -1
	 * @param date
	 * @param kirche
	 * @param type
	 */
	public Messe(boolean hochamt, int anz_medis, Date datummituhrzeit, String ort, String typ, WMainFrame wmf) {
		if (wmf.getPfarrei().getOrte().contains(ort) && wmf.getPfarrei().getTypen().contains(typ)) {
			bearbeiten(hochamt, anz_messdiener, date, kirche, typ, wmf);
		}else{
			Utilities.logging(getClass(), getClass().getEnclosingConstructor(), "da ging was schief");
		}

	}

	public String ausgeben() {
		String rtn = "";
		SimpleDateFormat df = new SimpleDateFormat("EE dd.MM. kk:mm", Locale.GERMAN);
		rtn = df.format(getDate()) + " Uhr";
		if (!titel.equals("")) {
			rtn += " " + titel;
		} else {
			rtn += " " + typ;
		}
		rtn += ", " + kirche;
		// ------------------------------------
		rtn += "\n";
		for (int i = 0; i < medis.size(); i++) {
			Messdiener m = medis.get(i);
			if (i == 0) {
				rtn += m.getVorname() + " " + m.getNachnname();
			} else {
				rtn += ", " + m.getVorname() + " " + m.getNachnname();
			}
		}
		if (leiter.size() > 0) {
			for (int i = 0; i < leiter.size(); i++) {
				Messdiener m = leiter.get(i);
				if (i == 0) {
					rtn += m.getVorname() + " " + m.getNachnname();
				} else {
					rtn += ", " + m.getVorname() + " " + m.getNachnname();
				}
			}
		}
		rtn += "\n";
		return rtn;
	}

	public ArrayList<String> ausgebenAlsArray() {
		ArrayList<String> Lrtn = new ArrayList<String>();
		SimpleDateFormat df = new SimpleDateFormat("EE dd.MM kk:mm", Locale.GERMAN);
		String rtn = df.format(getDate()) + " Uhr";
		if (!titel.equals("")) {
			rtn += " " + titel;
		} else {
			rtn += " " + typ;
		}
		rtn += ", " + kirche;
		// ------------------------------------
		Lrtn.add(rtn);
		rtn = "";
		for (int i = 0; i < medis.size(); i++) {
			Messdiener m = medis.get(i);
			if (i == 0) {
				rtn += m.getVorname() + " " + m.getNachnname();
			} else {
				rtn += ", " + m.getVorname() + " " + m.getNachnname();
			}
		}
		Lrtn.add(rtn);
		rtn = "";
		if (leiter.size() > 0) {
			for (int i = 0; i < leiter.size(); i++) {
				Messdiener m = leiter.get(i);
				if (i == 0) {
					rtn += m.getVorname() + " " + m.getNachnname();
				} else {
					rtn += ", " + m.getVorname() + " " + m.getNachnname();
				}
			}
		}
		Lrtn.add(rtn);
		return Lrtn;
	}

	/**
	 * Hiermit kann man die Messe bearbeiten
	 *
	 * @param anfang_stunde
	 * @param anfang_minute
	 * @param hochamt
	 * @param anz_messdiener
	 * @param date
	 * @param kirche
	 * @param type
	 */
	public void bearbeiten(boolean hochamt, int anz_messdiener, Date date, String kirche, String type,
			WMainFrame wmf) {
		setWochentag(df.format(date));
		setHochamt(hochamt);
		setKirche(kirche);
		setMesseTyp(type);
		setDate(date);
		setAnz_messdiener(anz_messdiener, wmf);
		/*
		 * for (int i = 0; i < anz_messdiener; i++) { Messdiener me = new
		 * Messdiener(); me.setLeer(this); medis.add(me);
		 * medis.sort(Messdiener.compForMedis);}
		 */
	}

	public void einteilen(Messdiener medi) {
		if (medi.getMessdatenDaten().kann(getDate())) {
			medi.getMessdatenDaten().einteilen(getDate(), isHochamt());
			medis.add(medi);
			medis.sort(Messdiener.compForMedis);
		}
	}

	public void einteilenZwang(Messdiener medi) {
		medi.getMessdatenDaten().einteilenZwang(getDate(), isHochamt());
		medis.add(medi);
		medis.sort(Messdiener.compForMedis);
	}

	public int getAnz_messdiener() {
		return anz_messdiener;
	}

	public Date getDate() {
		return date;
	}

	public ArrayList<Messdiener> getEingeteilte() {
		ArrayList<Messdiener> rtn = new ArrayList<>();
		rtn.addAll(medis);
		rtn.addAll(leiter);
		return rtn;
	}

	public StandartMesse getStandardMesse() {
		try {
			return em;
		} catch (NullPointerException e) {
			return new Sonstiges();
		}
	}

	public String getID() {
		String rtn = "";
		if (titel == null) {
			if (!getWochentag().equals("")) {
				rtn = getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + titel + " "
						+ kirche;
			} else {
				if (!getWochentag().equals("")) {
					rtn = getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + titel + " "
							+ kirche;
				}
			}
		} else {
			if (!getWochentag().equals("")) {
				rtn = getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr "
						+ typ + " " + kirche;
			} else {
				if (!getWochentag().equals("")) {
					rtn = getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr "
							+ typ + " " + kirche;
				}
			}
		}
		return rtn;
	}

	public String getIDHTML() {
		String rtn = "";
		if (!getWochentag().equals("")) {
			rtn = "<html><body>" + getWochentag() + " " + dfeee.format(date) + "&emsp;" + dftime.format(date) + " Uhr "
					+ typ + " " + kirche + "</body></html>";
		}
		return rtn;
	}

	/*
	 * public String getDeutschenWochentag() { switch (Wochentag) { case "Mon":
	 * return "Mo"; case "Tue": return "Di"; case "Wed": return "Mi"; case
	 * "Thu": return "Do"; case "Fri": return "Fr"; case "Sat": return "Sa";
	 * case "Son": return Wochentag;
	 *
	 * default: new Erroropener("Wochentag"); return ""; } }
	 */

	public String getKirche() {
		return kirche;
	}

	public String getMesseTyp() {
		return typ;
	}

	public String getWochentag() {
		return Wochentag;
	}

	public String htmlAusgeben() {
		String rtn = getIDHTML();
		rtn.substring(0, 14);

		for (int i = 0; i < medis.size(); i++) {
			try {
				rtn += medis.get(i).getVorname() + " " + medis.get(i).getNachnname() + ", ";
			} catch (NullPointerException e) {
				rtn += ",";
			}

		}
		if (rtn.endsWith(", ")) {
			rtn = rtn.substring(0, rtn.length() - 2);
		}
		rtn += "\n";
		for (int i = 0; i < leiter.size(); i++) {
			try {
				rtn += medis.get(i).makeId() + ",";
			} catch (NullPointerException e) {
				rtn += ",";
			}
		}
		if (rtn.endsWith(";")) {
			rtn = rtn.substring(0, rtn.length() - 1);
		}

		rtn += "</body></html>";
		return rtn;
	}

	public boolean isHochamt() {
		return hochamt;
	}

	public boolean istFertig() {
		int anz_leiter = leiter.size();
		int anz_medis = medis.size();
		for (int i = 0; i < leiter.size(); i++) {
			String id = leiter.get(i).makeId();
			if (id.equals("") || id.equals("LEER, LEER") || id.equals("Nachname, Vorname")) {
				medis.remove(i);
			}
		}
		for (int i = 0; i < medis.size(); i++) {
			String id = medis.get(i).makeId();
			if (id.equals("") || id.equals("LEER, LEER") || id.equals("Nachname, Vorname")) {
				medis.remove(i);
			}
		}
		if ((anz_medis + anz_leiter) >= anz_messdiener) {
			return true;
		} else {
			return false;
		}
	}

	public void LeiterEinteilen(Messdiener leiter) {
		if (leiter.isIstLeiter()) {
			if (leiter.getMessdatenDaten().kann(getDate())) {
				this.leiter.add(leiter);
			}
		}
	}

	private void setAnz_messdiener(int anz_messdiener, WMainFrame wmf) {
		if (anz_messdiener <= wmf.getEDVVerwalter().getMaxAnzMedis(wmf.getEDVVerwalter().getSavepath(), wmf)) {
			this.anz_messdiener = anz_messdiener;
		} else {
			new Erroropener("zu grosse Anzahl an Messdiener!");
			this.anz_messdiener = 0;
		}
	}

	private void setDate(Date date) {
		setWochentag(df.format(date));
		if (df.format(date).equals(getWochentag())) {
			this.date = date;
		}
	}

	private void setHochamt(boolean istHochamt) {
		hochamt = istHochamt;
	}

	private void setKirche(String kirche) {
		this.kirche = kirche;
	}

	private void setMesseTyp(String etyp) {
		this.typ = etyp;
	}

	/**
	 * Wird in date mit Uebergeben
	 *
	 * @return
	 */
	private void setWochentag(String wochentag) {
		if ((wochentag.startsWith("Mo")) || (wochentag.startsWith("Di")) || (wochentag.startsWith("Mi"))
				|| (wochentag.startsWith("Do")) || (wochentag.startsWith("Fr")) || (wochentag.startsWith("Sa"))
				|| (wochentag.startsWith("So"))) {
			Wochentag = wochentag;
		} else {
			new Erroropener("Wochentag" + wochentag);
		}
	}

	public void vorzeitigEiteilen(Messdiener medi, Date d) {
		medi.getMessdatenDaten().einteilen(d, isHochamt());
		medis.add(medi);
		medis.sort(Messdiener.compForMedis);
	}

	public int getnochbenoetigte() {
		int haben = leiter.size() + medis.size();
		int soll = anz_messdiener;
		return soll - haben;
	}

	public int getHatSchon() {
		return leiter.size() + medis.size();
	}

	public void setTitle(String titel) {
		this.titel = titel;

	}
}