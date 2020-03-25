package net.aclrian.mpe.messe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.start.AData;

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

	public static final Comparator<Messe> compForMessen = Comparator.comparing(Messe::getDate);
	private boolean hochamt;
	private int anz_messdiener;
	private Date date;
	private String kirche;
	private SimpleDateFormat df = new SimpleDateFormat("EEE", Locale.GERMAN);
	private StandartMesse em;
	private String typ;
	private ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
	private ArrayList<Messdiener> leiter = new ArrayList<Messdiener>();

	public Messe(Date d, StandartMesse sm) {
			this(false, sm.getAnz_messdiener(), d, sm.getOrt(), sm.getTyp(), sm);
	}

	public Messe(boolean hochamt, int anz_medis, Date datummituhrzeit, String ort, String typ) {
		this(hochamt, anz_medis, datummituhrzeit, ort, typ, new Sonstiges());
	}

	public Messe(boolean hochamt, int anz_medis, Date datummituhrzeit, String ort, String typ, StandartMesse sm) {
			bearbeiten(hochamt, anz_medis, datummituhrzeit, ort, typ);
			em = sm;
	}

	public String ausgeben() {
		String rtn = "";
		SimpleDateFormat df = new SimpleDateFormat("EE dd.MM. kk:mm", Locale.GERMAN);
		rtn = df.format(getDate()) + " Uhr";
		rtn += " " + typ;
		rtn += ", " + kirche;
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
		rtn += " " + typ;
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

	public void bearbeiten(boolean hochamt, int anz_messdiener, Date date, String kirche, String type) {
		this.hochamt = hochamt;
		this.kirche = kirche;
		this.typ = type;
		this.date = date;
		this.anz_messdiener = anz_messdiener;
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
		SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.YYYY");
		SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
		return getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + typ + " " + kirche;
	}

	public String getIDHTML() {
		SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.");
		SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
		StringBuffer rtn = new StringBuffer("<html>");
		rtn.append("<font>");
		if (!getWochentag().equals("")) {
			rtn.append("<p><b>" + df.format(getDate()) + " " + dfeee.format(date) + "&emsp;" + dftime.format(date) + " Uhr ");
			rtn.append(typ + " " + kirche);
			rtn.append("</p></b></font></html>");
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
		return df.format(getDate());
	}

	public String htmlAusgeben() {
		String rtn = getIDHTML();
		rtn = rtn.substring(0, rtn.length() - 7);
		medis.sort(Messdiener.compForMedis);
		leiter.sort(Messdiener.compForMedis);
		ArrayList<Messdiener> out = medis;
		out.addAll(leiter);
		rtn += "<p>";
		for (int i = 0; i < out.size(); i++) {
			rtn += out.get(i).getVorname() + " " + out.get(i).getNachnname() + ", ";
		}
		if (rtn.endsWith(", ")) {
			rtn = rtn.substring(0, rtn.length() - 2);
		}
		rtn += "</p>";
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

	public void leiterEinteilen(Messdiener leiter) {
		if (leiter.isIstLeiter()) {
			if (leiter.getMessdatenDaten().kann(getDate(), false)) {
				this.leiter.add(leiter);
			}
		}
	}

	public void vorzeitigEiteilen(Messdiener medi) {
		if (medi.getMessdatenDaten().kannvorzeitg(date)) {
			medi.getMessdatenDaten().einteilenVorzeitig(date,hochamt);
			if (medi.isIstLeiter()) {
				leiter.add(medi);
			} else {
				medis.add(medi);
			}
			medis.sort(Messdiener.compForMedis);
		}
	}

	public int getNochBenoetigte() {
		int haben = getHatSchon();
		int soll = anz_messdiener;
		return soll - haben;
	}

	private int getHatSchon() {
		return leiter.size() + medis.size();
	}

}