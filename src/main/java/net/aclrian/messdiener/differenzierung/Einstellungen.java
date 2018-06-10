package net.aclrian.messdiener.differenzierung;

import java.util.Calendar;

import net.aclrian.messdiener.differenzierung.Setting.Attribut;
import net.aclrian.messdiener.utils.Utilities;

public class Einstellungen {

	public static final int lenght = 21;
	private Setting[] settings = new Setting[lenght];

	public Einstellungen() {
		settings[0] = new Setting(Attribut.max, 0, 0);
		settings[1] = new Setting(Attribut.max, 1, 0);
		settings[2] = new Setting(Attribut.year, 0, 0);
		settings[3] = new Setting(Attribut.year, 1, 0);
		settings[4] = new Setting(Attribut.year, 2, 0);
		settings[5] = new Setting(Attribut.year, 3, 0);
		settings[6] = new Setting(Attribut.year, 4, 0);
		settings[7] = new Setting(Attribut.year, 5, 0);
		settings[8] = new Setting(Attribut.year, 6, 0);
		settings[9] = new Setting(Attribut.year, 7, 0);
		settings[10] = new Setting(Attribut.year, 8, 0);
		settings[11] = new Setting(Attribut.year, 9, 0);
		settings[12] = new Setting(Attribut.year, 10, 0);
		settings[13] = new Setting(Attribut.year, 11, 0);
		settings[14] = new Setting(Attribut.year, 12, 0);
		settings[15] = new Setting(Attribut.year, 13, 0);
		settings[16] = new Setting(Attribut.year, 14, 0);
		settings[17] = new Setting(Attribut.year, 15, 0);
		settings[18] = new Setting(Attribut.year, 16, 0);
		settings[19] = new Setting(Attribut.year, 17, 0);
		settings[20] = new Setting(Attribut.year, 18, 0);
	}

	public void editiereYear(int eintrittabstandzumaktjahr, int anz_max_dienen) {
		int i = eintrittabstandzumaktjahr + 2;
		if (i >= lenght) {
			return;
		}
		settings[i] = new Setting(Attribut.year, eintrittabstandzumaktjahr, anz_max_dienen);
		if (anz_max_dienen > settings[0].getAnz_dienen()) {
			Utilities.logging(this.getClass(),this.getClass().getEnclosingMethod().getName() ,"Die Anzahl (" + anz_max_dienen + ") von der ID: year=" + eintrittabstandzumaktjahr
					+ " ist groesser als der Maximale Standardwert von allen nicht Leitern. Dieser max. Wert wird somit neu gesetzt.");
			settings[0] = new Setting(Attribut.max, 0, anz_max_dienen);
		}
	}

	public void editMaxDienen(boolean istleiter, int anz_max_dienen) {
		System.out.println(istleiter + ": " + anz_max_dienen);
		int id = 0;
		if (istleiter) {
			id++;
		}
		settings[id] = new Setting(Attribut.max, id, anz_max_dienen);
	}

	public Integer[][] getDatafuerTabelle() {
		Integer[][] rtn = new Integer[lenght - 2][2];
		for (int i = 2; i < settings.length; i++) {
			Setting s = settings[i];
			int currentyear = Calendar.getInstance().get(Calendar.YEAR);
			rtn[i - 2][0] = currentyear-(i - 2);
			rtn[i - 2][1] = s.getAnz_dienen();
		}
		return rtn;
	}
	
	public static Integer[][] getEinheitsdata(int anz){
		Integer[][] rtn = new Integer[lenght - 2][2];
		for (int i = 0; i < (lenght-2); i++) {
			rtn[i][0] = i;
			rtn[i][1] = anz;
		}
		return rtn;
	}
	
	public Setting[] getDaten() {
		return settings;
	}
}
