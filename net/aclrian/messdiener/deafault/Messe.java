package net.aclrian.messdiener.deafault;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import net.aclrian.messdiener.utils.EnumMesseTyp;
import net.aclrian.messdiener.utils.EnumOrt;
import net.aclrian.messdiener.utils.EnumdeafaultMesse;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.window.WMainFrame;

/**
 * Klasse von Messen
 * 
 * @author Aclrian
 *
 */
public class Messe {
	private String Wochentag;
	private boolean hochamt;
	private int anz_messdiener;
	private Date date;
	private EnumOrt kirche;
	private SimpleDateFormat df = new SimpleDateFormat("EEE", Locale.GERMAN);
	private SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.");
	private SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
	@SuppressWarnings("unused")
	private SimpleDateFormat dfall = new SimpleDateFormat("EEE dd.MM.yyyy:HH:ss", Locale.GERMAN);
	private EnumdeafaultMesse em = EnumdeafaultMesse.Sonstiges;
	private EnumMesseTyp etyp = EnumMesseTyp.Messe;
	private ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
	private ArrayList<Messdiener> leiter = new ArrayList<Messdiener>();
	private String titel = "";

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
	public Messe(boolean hochamt, int anz_messdiener, Date date, EnumOrt kirche, EnumMesseTyp type, WMainFrame wmf) {
		bearbeiten(hochamt, anz_messdiener, date, kirche, type, wmf);
	}

	/**
	 * 
	 * @param em
	 *            EnumOrt als StandartMesse
	 * @param date
	 *            wann sie sein soll
	 * @throws ParseException
	 */
	public Messe(EnumdeafaultMesse em, Date date, WMainFrame wmf) {
		if (em.getWochentag().equals(df.format(date))) {
			bearbeiten(false, em.getAnz_messdiener(), em.getDate(date), em.getOrt(), em.getTyp(), wmf);
			this.setEnumdfMesse(em);
		} else {
			new Erroropener("sad", null);
		}
	}

	public Messe(boolean hochamt, int anz_medis, Date datummituhrzeit, EnumOrt ort, EnumMesseTyp typ, String titel,
			WMainFrame mainframe) {
		bearbeiten(hochamt, anz_messdiener, date, kirche, typ, mainframe);
		this.titel = titel;
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
	public void bearbeiten(boolean hochamt, int anz_messdiener, Date date, EnumOrt kirche, EnumMesseTyp type,
			WMainFrame wmf) {
		setWochentag(df.format(date));
		setHochamt(hochamt);
		setKirche(kirche);
		setMesseTyp(type);
		setDate(date);
		setAnz_messdiener(anz_messdiener, wmf);
		/*
		 * for (int i = 0; i < anz_messdiener; i++) { Messdiener me = new
		 * Messdiener(); me.setLeer(this); medis.add(me); }
		 */
	}

	/**
	 * Wird in date mit Uebergeben
	 * 
	 * @return
	 */
	private void setWochentag(String wochentag) {
		if ((wochentag.equals("Mo")) || (wochentag.equals("Di")) || (wochentag.equals("Mi")) || (wochentag.equals("Do"))
				|| (wochentag.equals("Fr")) || (wochentag.equals("Sa")) || (wochentag.equals("So"))) {
			this.Wochentag = wochentag;
		} else {
			new Erroropener("Wochentag" + wochentag, null);
		}
	}

	private void setHochamt(boolean istHochamt) {
		this.hochamt = istHochamt;
	}

	public String getWochentag() {
		return this.Wochentag;
	}

	public boolean isHochamt() {
		return this.hochamt;
	}

	public int getAnz_messdiener() {
		return anz_messdiener;
	}

	public Date getDate() {
		return date;
	}

	private void setDate(Date date) {
		setWochentag(df.format(date));
		if (df.format(date).equals(getWochentag())) {
			this.date = date;
		}
	}

	public String getKircheAlsString() {
		return kirche.getOrt();
	}

	public EnumOrt getKirche() {
		return kirche;
	}

	private void setKirche(EnumOrt kirche) {
		this.kirche = kirche;
	}

	/*
	 * public String getDeutschenWochentag() { switch (Wochentag) { case "Mon":
	 * return "Mo"; case "Tue": return "Di"; case "Wed": return "Mi"; case
	 * "Thu": return "Do"; case "Fri": return "Fr"; case "Sat": return "Sa";
	 * case "Son": return Wochentag;
	 * 
	 * default: new Erroropener("Wochentag"); return ""; } }
	 */

	public String getID() {
		String rtn = "";
		if (titel == null) {
			if (!getWochentag().equals("")) {
				rtn = getWochentag() + ". " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + titel + " "
						+ getKirche().getOrt();
			} else {
				if (!getWochentag().equals("")) {
					rtn = getWochentag() + ". " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr "
							+titel + " " + getKirche().getOrt();
				}
			}
		}
		else{
			if (!getWochentag().equals("")) {
				rtn = getWochentag() + ". " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + getMesseTyp().getType() + " "
						+ getKirche().getOrt();
			} else {
				if (!getWochentag().equals("")) {
					rtn = getWochentag() + ". " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr "
							+ getMesseTyp().getType() + " " + getKirche().getOrt();
				}
			}
		}
		return rtn;
	}

	public String getIDHTML() {
		String rtn = "";
		if (!getWochentag().equals("")) {
			rtn = "<html><body>" + getWochentag() + ". " + dfeee.format(date) + "&emsp;" + dftime.format(date) + " Uhr "
					+ getMesseTyp() + " " + getKirche() + "</body></html>";
		}
		return rtn;
	}

	public EnumdeafaultMesse getEnumdfMesse() {
		return em;
	}

	private void setEnumdfMesse(EnumdeafaultMesse em) {
		this.em = em;
	}

	public EnumMesseTyp getMesseTyp() {
		return etyp;
	}

	private void setMesseTyp(EnumMesseTyp etyp) {
		this.etyp = etyp;
	}

	@SuppressWarnings("null")
	private void setAnz_messdiener(int anz_messdiener, WMainFrame wmf) {
		if (anz_messdiener <= wmf.getUtil().getMaxAnzMedis(wmf.getUtil().getSavepath())) {
			this.anz_messdiener = anz_messdiener;
		} else {
			new Erroropener("zu grosse Anzahl an Messdiener!", null);
			this.anz_messdiener = (Integer) null;
		}
	}

	public boolean istFertig() {
		boolean leeer = false;
		try {
			for (int i = 0; i < anz_messdiener; i++) {

				Messdiener medi = medis.get(i);

				String id = medi.makeId();
				if (id.equals("") || id.equals("LEER, LEER") || id.equals("Nachname, Vorname")) {
					leeer = true;
				}
			}
		} catch (NullPointerException e) {
			leeer = true;
		} catch (IndexOutOfBoundsException e) {
			leeer = true;
		}

		return (!leeer);
	}

	public void einteilen(Messdiener medi) {
		if (medi.getMessdatenDaten().einteilen(getDate())) {
			medis.add(medi);
		}
	}

	public void vorzeitigEiteilen(Messdiener medi, Date d) {
		medi.getMessdatenDaten().einteilen(d);
		medis.add(medi);
	}

	public ArrayList<Messdiener> getEingeteilte() {
		ArrayList<Messdiener> rtn = new ArrayList<>();
		rtn.addAll(medis);
		rtn.addAll(leiter);
		return rtn;
	}

	public void LeiterEinteilen(Messdiener leiter) {
		if (leiter.isIstLeiter()) {
			if (leiter.getMessdatenDaten().einteilen(getDate())) {
				this.leiter.add(leiter);
			}
		}
	}

	public String ausgeben() {
		String rtn = "";
		SimpleDateFormat df = new SimpleDateFormat("EE dd.MM kk:mm", Locale.GERMAN);
		rtn = df.format(getDate()) + " Uhr";
		if(!titel.equals("")){
		rtn += " " + titel;
		}
		else{
			rtn += " " + etyp.getType();
		}
		rtn += ", " + getKirche().getOrt();
		//------------------------------------
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

	public ArrayList<String> ausgebenAlsArray() {
		ArrayList<String> Lrtn = new ArrayList<String>();
		SimpleDateFormat df = new SimpleDateFormat("EE dd.MM kk:mm", Locale.GERMAN);
		String rtn = df.format(getDate()) + " Uhr";
		if(!titel.equals("")){
		rtn += " " + titel;
		}
		else{
			rtn += " " + etyp.getType();
		}
		rtn += ", " + getKirche().getOrt();
		//------------------------------------
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
}