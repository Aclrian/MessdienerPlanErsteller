package net.aclrian.mpe.pfarrei;

import java.util.ArrayList;
import java.util.List;

import net.aclrian.mpe.pfarrei.Setting.Attribut;
import net.aclrian.mpe.utils.Log;
public class Einstellungen {

	public static final int lenght = 21;
	private Setting[] settings = new Setting[lenght];

	public Einstellungen() {
		settings[0] = new Setting(Attribut.max, 0, 0);
		settings[1] = new Setting(Attribut.max, 1, 0);
		for (int i = 2; i < settings.length; i++) {
			settings[i] = new Setting(Attribut.year, i-2, 0);
		}
	}

	public void editiereYear(int eintrittabstandzumaktjahr, int anz_max_dienen) {
		int i = eintrittabstandzumaktjahr + 2;
		if (i >= lenght) {
			return;
		}
		settings[i] = new Setting(Attribut.year, eintrittabstandzumaktjahr, anz_max_dienen);
		if (anz_max_dienen > settings[0].getAnz_dienen()) {
			Log.getLogger().info("Die Anzahl (" + anz_max_dienen + ") von der ID: year=" + eintrittabstandzumaktjahr
					+ " ist groesser als der Maximale Standardwert von allen nicht Leitern. Dieser max. Wert wird somit neu gesetzt.");
			settings[0] = new Setting(Attribut.max, 0, anz_max_dienen);
		}
	}

	public void editMaxDienen(boolean istleiter, int anz_max_dienen) {
		int id = 0;
		if (istleiter) {
			id++;
		}
		settings[id] = new Setting(Attribut.max, id, anz_max_dienen);
	}
	
	public List<Setting> getSettings() {
		List<Setting> rtn = new ArrayList<>();
		for (Setting setting : settings) {
			if (setting.getA() != Attribut.max) {
				rtn.add(setting);
			}
		}
		return rtn;
	}

	public Setting getDaten(int index) {
		if (index>20) {
			return settings[20];
		}
		return settings[index];
	}
}
