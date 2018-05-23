package net.aclrian.messdiener.deafault;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.References;

public class StandartMesse {
	private int beginn_stunde;
	private String beginn_minute;
	private String ort;
	private String Wochentag;
	private int anz_messdiener;
	private String typ;
	// private SimpleDateFormat dfeee = new SimpleDateFormat("dd.MM.");
	public StandartMesse(String wochentag, int beginn_h, String beginn_min, String ort, int anz_messdiener,
			String typ) {
		this.ort = ort;
		this.anz_messdiener = anz_messdiener;
		this.typ = typ;
		Wochentag = wochentag;
		beginn_stunde = beginn_h;
		beginn_minute = beginn_min;
	}

	public int getBeginn_stunde() {
		return beginn_stunde;
	}

	public Date getDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d = null;
		try {
			d = df.parse(String.valueOf(beginn_stunde) + ":" + beginn_minute);
		} catch (ParseException e) {
			new Erroropener("Es konnte kein Datum erstellt werden. Bitte die Eingaben überpr"+References.ue+"fen.");
			e.printStackTrace();
		}
		SimpleDateFormat ds = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dall = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
		String datum = ds.format(date);
		String uhrzeit = df.format(d);
		Date rtn = null;
		try {
			rtn = dall.parse(datum + ":" + uhrzeit);
		} catch (ParseException e) {
			new Erroropener("Es konnte kein Datum erstellt werden. Bitte die Eingaben überpr"+References.ue+"fen.");
			e.printStackTrace();
		}
		return rtn;

	}

	public String getBeginn_minute() {
		return beginn_minute;
	}

	public String getOrt() {
		return ort;
	}

	public String getWochentag() {
		return Wochentag;
	}

	public int getAnz_messdiener() {
		return anz_messdiener;
	}

	public String getTyp() {
		return typ;
	}

	public String[] getStrings() {
		String[] rtn = { Wochentag, ort, String.valueOf(beginn_stunde), beginn_minute, typ,
				String.valueOf(anz_messdiener) };
		return rtn;
	}

	@Override
	public String toString() {
		return Wochentag + "_" + String.valueOf(beginn_stunde) + ":" + beginn_minute + "-" + ort+ " " + typ +
				" " + String.valueOf(anz_messdiener);
	}
	
	public String toReduziertenString(){
		String rtn = Wochentag + "-" + String.valueOf(beginn_stunde) + "-" + beginn_minute + "" + //ort+ "-" + typ +
				"-" + String.valueOf(anz_messdiener);
		rtn.replace(':', '-');
		rtn.replace(' ', '-');
		rtn.replaceAll(":", "a");
		rtn.replaceAll(" ", "a");
		if (rtn.length() > 16) {
			rtn.substring(0, 15);
		}
		return rtn;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Messverhalten) {
			return this.toString().equals(obj.toString());
		}
			return false;
	}
	
	public static Comparator<StandartMesse> comfuerSMs = new Comparator<StandartMesse>() {
		
		@Override
		public int compare(StandartMesse o1, StandartMesse o2) {
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	};
	public String getBeginn_stundealsString() {
		String rtn = String.valueOf(this.beginn_stunde);
		if (rtn.length() != 2) {
			if (rtn.length() == 1) {
				rtn = "";
				rtn = "0"+String.valueOf(this.beginn_stunde);
			}
			else{
				System.out.println("da ist was schief gegeangen!!");
			}
		}
		return rtn;
	}
	
	public boolean Equals(StandartMesse smold){
		boolean anz = anz_messdiener == smold.getAnz_messdiener();
		boolean min = beginn_minute.equals(smold.getBeginn_minute());
		boolean std = beginn_stunde == smold.getBeginn_stunde();
		boolean ort = this.ort.equals(smold.getOrt());
		boolean typ = this.typ.equals(smold.getTyp());
		boolean tag = Wochentag.equals(smold.getWochentag());
		if (tag == typ == ort == std == min == anz) {
			return anz;
		}else{
			return false;
		}
	}
	
}
