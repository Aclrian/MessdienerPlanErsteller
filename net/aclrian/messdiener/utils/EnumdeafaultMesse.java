package net.aclrian.messdiener.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enum mit Standartmessen
 * <br><b>BEACHTE:</b> Wochentag (also letzter String) in <b>Deutsch</b> schreiben und nur die ersten <b>drei Buchstaben</b>!
 * @author Aclrian
 *
 */
public enum EnumdeafaultMesse {
	//nach Wochentag und Uhrzeit sortieren
	Sam_hochzeit(14, "00", EnumOrt.St_Martinus, "Sa",2, EnumMesseTyp.Hochzeit), 
	Sam_abend(18, "30", EnumOrt.St_Martinus, "Sa", 6, EnumMesseTyp.Messe),
	Son_morgen(10, "00", EnumOrt.St_Martinus, "So", 6, EnumMesseTyp.Messe),
	Son_taufe(15, "00", EnumOrt.St_Martinus, "So", 2, EnumMesseTyp.Taufe), 
	Son_abend(18, "00", EnumOrt.St_Martinus, "So", 6, EnumMesseTyp.Messe), 
	Di_abend(19, "00", EnumOrt.Alt_st_martin, "Di", 2, EnumMesseTyp.Messe), 
	Sonstiges(0, "00", EnumOrt.Sonstiges, "", 0, EnumMesseTyp.Messe);
	
	
	private EnumdeafaultMesse(int beginn_stunde, String beginn_minute, EnumOrt ort, String Wochentag, int anz_messdiener, EnumMesseTyp typ) {
		setBeginn_minute(beginn_minute);
		setBeginn_stunde(beginn_stunde);
		setOrt(ort);
		setWochentag(Wochentag);
		setTyp(typ);
		setAnz_messdiener(anz_messdiener);
	}
	private int beginn_stunde;
	private String beginn_minute;
	private EnumOrt ort;
	private int anz_messdiener;
	private EnumMesseTyp typ;
	/**
	 * Wochentag (also letzter Stirng) in Englisch schreiben und nur die ersten drei Buchstaben
	 */
	private String Wochentag;

	public int getBeginn_stunde() {
		return this.beginn_stunde;
	}

	public void setBeginn_stunde(int beginn_stunde) {
		this.beginn_stunde = beginn_stunde;
	}

	public String getBeginn_minute() {
		return this.beginn_minute;
	}

	public void setBeginn_minute(String beginn_minute) {
		this.beginn_minute = beginn_minute;
	}

	public EnumOrt getOrt() {
		return this.ort;
	}
	public String getOrtalsString() {
		return getOrt().getOrt();
	}

	public void setOrt(EnumOrt ort) {
		this.ort = ort;
	}

	public String getWochentag() {
		return Wochentag;
	}

	public void setWochentag(String wochentag) {
		Wochentag = wochentag;
	}

	public Date getDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;
		try {
			d = df.parse(String.valueOf(beginn_stunde)+ ":" + beginn_minute);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat ds = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dall = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
		String datum = ds.format(date);
		String uhrzeit = df.format(d);
	Date rtn = null;
	try {
		rtn = dall.parse(datum+":"+uhrzeit);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(dall.format(rtn));
	return rtn;
	
	}
	
	

	public int getAnz_messdiener() {
		return anz_messdiener;
	}

	public void setAnz_messdiener(int anz_messdiener) {
		this.anz_messdiener = anz_messdiener;
	}

	public EnumMesseTyp getTyp() {
		return typ;
	}
	public String getTypeAlsString() {
		return typ.getType();
	}

	public void setTyp(EnumMesseTyp typ) {
		this.typ = typ;
	}

}