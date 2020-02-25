package net.aclrian.mpe.messe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.start.AData;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

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
			return o1.getDate().compareTo(o2.getDate());
		}
	};
	private String Wochentag;
	private boolean hochamt;
	private int anz_messdiener;
	private Date date;
	private String kirche;
	private SimpleDateFormat df = new SimpleDateFormat("EEE", Locale.GERMAN);
	private StandartMesse em = new Sonstiges();
	private String typ;
	private ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
	private ArrayList<Messdiener> leiter = new ArrayList<Messdiener>();
	private String titel = "";

	public Messe(boolean hochamt, int anz_medis, Date datummituhrzeit, String ort, String typ, String titel) {
			bearbeiten(hochamt, anz_medis, datummituhrzeit, ort, typ);
			this.titel = titel;
	}

	/*
	 * 
	 */
	public Messe(Date d, StandartMesse sm, ArrayList<StandartMesse> stme) {
		boolean isdrin = false;
		for (StandartMesse stm : stme) {
			if (sm instanceof Sonstiges) {
				continue;
			}
			if (sm.toString().equals(stm.toString())) {
				isdrin = true;
				break;
			}
		}
		if (isdrin) {
			this.em = sm;
			bearbeiten(false, sm.getAnz_messdiener(), d, sm.getOrt(), sm.getTyp());
		} else {
			Log.getLogger().info("da ging was schief");
		}
	}

	/**
	 *
	 * @param anfang_stunde
	 * @param anfang_minute
	 * @param hochamt
	 * @param anz_messdiener
	 * @param anz_leiter     unbeachteet lassen, wenn -1
	 * @param date
	 * @param kirche
	 * @param type
	 */
	public Messe(boolean hochamt, int anz_medis, Date datummituhrzeit, String ort, String typ) {
			bearbeiten(hochamt, anz_medis, datummituhrzeit, ort, typ);
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
	public void bearbeiten(boolean hochamt, int anz_messdiener, Date date, String kirche, String type) {
		this.Wochentag = df.format(date);
		this.hochamt = hochamt;
		this.kirche = kirche;
		this.typ = type;
		this.date = date;
		setAnz_messdiener(anz_messdiener);
	}

	public void einteilen(Messdiener medi) {
		if (medi.getMessdatenDaten().kann(getDate(), false)) {
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
		SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.");
		SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
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
				rtn = getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + typ + " "
						+ kirche;
			} else {
				if (!getWochentag().equals("")) {
					rtn = getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + typ + " "
							+ kirche;
				}
			}
		}
		return rtn;
	}

	public String getIDHTML() {
		SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.");
		SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
		StringBuffer rtn = new StringBuffer("<html>");
		rtn.append("<font>");
		if (!getWochentag().equals("")) {
			rtn.append("<b>" + getWochentag() + " " + dfeee.format(date) + "&emsp;" + dftime.format(date) + " Uhr ");
			if (titel.equals("")) {
				rtn.append(typ + " " + kirche);
			} else {
				rtn.append(getTitle() + " " + kirche);
			}
			rtn.append("</b></font></html>");
		}
		return rtn.toString();
	}

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
		rtn = rtn.substring(0, rtn.length() - 7);
		rtn += "<br></br>";
		medis.sort(Messdiener.compForMedis);
		leiter.sort(Messdiener.compForMedis);
		ArrayList<Messdiener> out = medis;
		out.addAll(leiter);
		for (int i = 0; i < out.size(); i++) {
			rtn += out.get(i).getVorname() + " " + out.get(i).getNachnname() + ", ";
		}
		if (rtn.endsWith(", ")) {
			rtn = rtn.substring(0, rtn.length() - 2);
		}
		rtn += "<br></br>";
		rtn += "</html>";
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
	
	public void nullen() {
		medis = new ArrayList<>();
		leiter = new ArrayList<>();
	}

	public void LeiterEinteilen(Messdiener leiter) {
		if (leiter.isIstLeiter()) {
			if (leiter.getMessdatenDaten().kann(getDate(), false)) {
				this.leiter.add(leiter);
			}
		}
	}

	private void setAnz_messdiener(int anz_messdiener) {
			this.anz_messdiener = anz_messdiener;
	}

	public void setDate(Date date) {
		setWochentag(df.format(date));
		if (df.format(date).equals(getWochentag())) {
			this.date = date;
		}
	}

	public void setHochamt(boolean istHochamt) {
		hochamt = istHochamt;
	}

	public void setKirche(String kirche) {
		this.kirche = kirche;
	}

	public void setMesseTyp(String etyp) {
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
			Dialogs.warn("Wochentag: " + wochentag + " existiert nicht!");
		}
	}

	public void vorzeitigEiteilen(Messdiener medi, AData ada) {
		if (medi.getMessdatenDaten().kannvorzeitg(date, medi.isIstLeiter(), ada)) {
			medi.getMessdatenDaten().einteilenVorzeitig(getDate(), isHochamt(), medi, ada);
			if (medi.isIstLeiter()) {
				leiter.add(medi);
			} else {
				medis.add(medi);
			}
			medis.sort(Messdiener.compForMedis);
		}
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

	public String getTitle() {
		return titel;
	}

	public void setStandartMesse(StandartMesse sm, AData ada) {
		boolean isdrin = false;
		for (StandartMesse stm : ada.getSMoheSonstiges()) {
			if (sm.toString().equals(stm.toString())) {
				isdrin = true;
				break;
			}
		}
		if (isdrin) {
			em = sm;
		}
	}
}