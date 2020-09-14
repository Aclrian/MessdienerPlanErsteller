package net.aclrian.mpe.messe;

import java.text.SimpleDateFormat;
import java.util.*;

import net.aclrian.mpe.messdiener.Messdiener;

/**
 * Klasse von Messen
 *
 * @author Aclrian
 *
 */
public class Messe implements Comparable<Messe>{
	@Override
	public String toString() {
		return getID();
	}

	public static final Comparator<Messe> compForMessen = Comparator.comparing(Messe::getDate);
	private boolean hochamt;
	private int anzMessdiener;
	private Date date;
	private String kirche;
	private final SimpleDateFormat dateFormatWeekday = new SimpleDateFormat("EEE", Locale.GERMAN);
	private final StandartMesse em;
	private String typ;
	private ArrayList<Messdiener> medis = new ArrayList<>();
	private ArrayList<Messdiener> leiter = new ArrayList<>();

	public Messe(Date d, StandartMesse sm) {
			this(false, sm.getAnzMessdiener(), d, sm.getOrt(), sm.getTyp(), sm);
	}

	public Messe(boolean hochamt, int anzMedis, Date datummituhrzeit, String ort, String typ) {
		this(hochamt, anzMedis, datummituhrzeit, ort, typ, new Sonstiges());
	}

	public Messe(boolean hochamt, int anzMedis, Date datummituhrzeit, String ort, String typ, StandartMesse sm) {
			bearbeiten(hochamt, anzMedis, datummituhrzeit, ort, typ);
			em = sm;
	}

	public void bearbeiten(boolean hochamt, int anzMessdiener, Date date, String kirche, String type) {
		this.hochamt = hochamt;
		this.kirche = kirche;
		this.typ = type;
		this.date = date;
		this.anzMessdiener = anzMessdiener;
	}

	public boolean einteilen(Messdiener medi, boolean zwangdate, boolean zwanganz) {
		if(medi.getMessdatenDaten().einteilen(getDate(), isHochamt(), zwangdate, zwanganz)) {
			if (medi.istLeiter()) {
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

	public int getAnzMessdiener() {
		return anzMessdiener;
	}

	public Date getDate() {
		return date;
	}

	public List<Messdiener> getEingeteilte() {
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
		SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
		return getWochentag() + " " + dfeee.format(date) + "\t" + dftime.format(date) + " Uhr " + typ + " " + kirche;
	}

	public String getIDHTML() {
		SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.");
		SimpleDateFormat dftime = new SimpleDateFormat("HH:mm");
		StringBuilder rtn = new StringBuilder("<html>");
		rtn.append("<font>");
		if (!getWochentag().equals("")) {
			rtn.append("<p><b>").append(dateFormatWeekday.format(getDate())).append(" ").append(dfeee.format(date)).append("&emsp;").append(dftime.format(date)).append(" Uhr ");
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
		return dateFormatWeekday.format(getDate());
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
		int anzLeiter = leiter.size();
		int anzMedis = medis.size();
		return (anzMedis + anzLeiter) >= anzMessdiener;
	}
	
	public void nullen() {
		medis = new ArrayList<>();
		leiter = new ArrayList<>();
	}

	public boolean vorzeitigEiteilen(Messdiener medi) {
		if(medi.getMessdatenDaten().einteilenVorzeitig(date,hochamt)){
			if (medi.istLeiter()) {
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
		int soll = anzMessdiener;
		return soll - haben;
	}

	private int getHatSchon() {
		return leiter.size() + medis.size();
	}

	@Override
	public int compareTo(Messe m) {
		return date.compareTo(m.getDate());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Messe messe = (Messe) o;

		if (hochamt != messe.hochamt) return false;
		if (anzMessdiener != messe.anzMessdiener) return false;
		if (!Objects.equals(date, messe.date)) return false;
		if (!Objects.equals(kirche, messe.kirche)) return false;
		if (!Objects.equals(em, messe.em)) return false;
		if (!Objects.equals(typ, messe.typ)) return false;
		if (!Objects.equals(medis, messe.medis)) return false;
		return Objects.equals(leiter, messe.leiter);
	}

	@Override
	public int hashCode() {
		int result = (hochamt ? 1 : 0);
		result = 31 * result + anzMessdiener;
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (kirche != null ? kirche.hashCode() : 0);
		result = 31 * result + dateFormatWeekday.hashCode();
		result = 31 * result + (em != null ? em.hashCode() : 0);
		result = 31 * result + (typ != null ? typ.hashCode() : 0);
		result = 31 * result + (medis != null ? medis.hashCode() : 0);
		result = 31 * result + (leiter != null ? leiter.hashCode() : 0);
		return result;
	}
}