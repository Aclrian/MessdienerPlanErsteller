package net.aclrian.mpe.messe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import net.aclrian.mpe.messdiener.Messdiener;

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
	private ArrayList<Messdiener> medis = new ArrayList<>();
	private ArrayList<Messdiener> leiter = new ArrayList<>();

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

	public void bearbeiten(boolean hochamt, int anz_messdiener, Date date, String kirche, String type) {
		this.hochamt = hochamt;
		this.kirche = kirche;
		this.typ = type;
		this.date = date;
		this.anz_messdiener = anz_messdiener;
	}

	public boolean einteilen(Messdiener medi, boolean zwangdate, boolean zwanganz) {
		if(medi.getMessdatenDaten().einteilen(getDate(), isHochamt(), zwangdate, zwanganz)) {
			if (medi.isIstLeiter()) {
				leiter.add(medi);
				leiter.sort(Messdiener.compForMedis);
			} else {
				medis.add(medi);
				medis.sort(Messdiener.compForMedis);
			}
			return true;
		}
		return false;
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
		StringBuilder rtn = new StringBuilder("<html>");
		rtn.append("<font>");
		if (!getWochentag().equals("")) {
			rtn.append("<p><b>").append(df.format(getDate())).append(" ").append(dfeee.format(date)).append("&emsp;").append(dftime.format(date)).append(" Uhr ");
			rtn.append(typ).append(" ").append(kirche);
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
		StringBuilder rtn = new StringBuilder(getIDHTML());
		rtn = new StringBuilder(rtn.substring(0, rtn.length() - 7));
		medis.sort(Messdiener.compForMedis);
		leiter.sort(Messdiener.compForMedis);
		ArrayList<Messdiener> out = medis;
		out.addAll(leiter);
		rtn.append("<p>");
		for (Messdiener messdiener : out) {
			rtn.append(messdiener.getVorname()).append(" ").append(messdiener.getNachnname()).append(", ");
		}
		if (rtn.toString().endsWith(", ")) {
			rtn = new StringBuilder(rtn.substring(0, rtn.length() - 2));
		}
		rtn.append("</p>");
		rtn.append("</html>");
		return rtn.toString();
	}

	public boolean isHochamt() {
		return hochamt;
	}

	public boolean istFertig() {
		int anz_leiter = leiter.size();
		int anz_medis = medis.size();
		return (anz_medis + anz_leiter) >= anz_messdiener;
	}
	
	public void nullen() {
		medis = new ArrayList<>();
		leiter = new ArrayList<>();
	}

	public boolean vorzeitigEiteilen(Messdiener medi) {
		if(medi.getMessdatenDaten().einteilenVorzeitig(date,hochamt)){
			if (medi.isIstLeiter()) {
				leiter.add(medi);
			} else {
				medis.add(medi);
			}
			medis.sort(Messdiener.compForMedis);
			return true;
		}
		return false;
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