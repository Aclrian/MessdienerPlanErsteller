package net.aclrian.mpe.messe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import net.aclrian.mpe.utils.Dialogs;

public class StandartMesse {
	private int beginn_stunde;
	private String beginn_minute;
	private String ort;
	private String Wochentag;
	private int anz_messdiener;
	private String typ;

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

	public String getBeginn_minute() {
		return beginn_minute;
	}

	public int getBeginn_minuteInt(){return Integer.parseInt(beginn_minute);}

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

	public String getString() {
		return tokurzerBenutzerfreundlichenString();
	}

	@Override
	public String toString() {
		String s = beginn_stunde<10? "0"+beginn_stunde:beginn_stunde+"";
		return Wochentag + "_" + s + ":" + beginn_minute + "-" + ort + " " + typ + " "
				+ anz_messdiener;
	}

	public String tokurzerBenutzerfreundlichenString() {
		return Wochentag + ". " + beginn_stunde + ":" + beginn_minute + ": " + ort + " " + typ + " (" + anz_messdiener
				+ ")";
	}

	@SuppressWarnings("unused")
	public String toBenutzerfreundlichenString() {
		return typ + " jeden " + Wochentag + "., " + "um " + beginn_stunde + ":" + beginn_minute + " in " + ort;
	}

	public String toReduziertenString() {
		return Wochentag + "-" + beginn_stunde + "-" + beginn_minute  + // ort+ "-" + typ +
				"-" + anz_messdiener;
	}

	public String getWochentags() {
		if (Wochentag.equalsIgnoreCase("Mo")) {
			return "montags";
		}
		if (Wochentag.equalsIgnoreCase("Di")) {
			return "dienstags";
		}
		if (Wochentag.equalsIgnoreCase("Mi")) {
			return "mittwochs";
		}
		if (Wochentag.equalsIgnoreCase("Do")) {
			return "donnerstags";
		}
		if (Wochentag.equalsIgnoreCase("Fr")) {
			return "freitags";
		}
		if (Wochentag.equalsIgnoreCase("Sa")) {
			return "samstags";
		}
		if (Wochentag.equalsIgnoreCase("So")) {
			return "sonntags";
		}
		return Wochentag;
	}

	public String tolangerBenutzerfreundlichenString() {
		return getWochentags() + " um " + beginn_stunde + ":" + beginn_minute + " Uhr in " + ort + " als " + typ + " mit " + anz_messdiener
				+ " Messdienern";
	}
	public String getZeit() {
		return getWochentags() + " um " + beginn_stunde + ":" + beginn_minute + " Uhr";
	}
	public String getMessdienerAnzahl() {
		return anz_messdiener+ " Messdiener";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Messverhalten) {
			return this.toString().equals(obj.toString());
		}
		return false;
	}

	public static Comparator<StandartMesse> comfuerSMs = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());

	public String getBeginn_stundealsString() {
		String rtn = String.valueOf(this.beginn_stunde);
		if (rtn.length() != 2) {
			if (rtn.length() == 1) {
				rtn = "";
				rtn = "0" + this.beginn_stunde;
			}
		}
		return rtn;
	}
}
