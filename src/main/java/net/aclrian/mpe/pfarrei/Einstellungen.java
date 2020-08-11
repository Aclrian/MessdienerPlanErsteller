package net.aclrian.mpe.pfarrei;

import java.util.ArrayList;
import java.util.List;

import net.aclrian.mpe.pfarrei.Setting.Attribut;
import net.aclrian.mpe.utils.Log;
public class Einstellungen {

	public static final int LENGHT = 21;
	private final Setting[] settings = new Setting[LENGHT];

	public Einstellungen() {
		settings[0] = new Setting(Attribut.MAX, 0, 0);
		settings[1] = new Setting(Attribut.MAX, 1, 0);
		for (int i = 2; i < settings.length; i++) {
			settings[i] = new Setting(Attribut.YEAR, i-2, 0);
		}
	}

	public void editiereYear(int eintrittabstandzumaktjahr, int anzMaxDienen) {
		int i = eintrittabstandzumaktjahr + 2;
		if (i >= LENGHT) {
			return;
		}
		settings[i] = new Setting(Attribut.YEAR, eintrittabstandzumaktjahr, anzMaxDienen);
		if (anzMaxDienen > settings[0].getAnzDienen()) {
			Log.getLogger().info("Die Anzahl (" + anzMaxDienen + ") von der ID: year=" + eintrittabstandzumaktjahr
					+ " ist groesser als der Maximale Standardwert von allen nicht Leitern. Dieser max. Wert wird somit neu gesetzt.");
			settings[0] = new Setting(Attribut.MAX, 0, anzMaxDienen);
		}
	}

	public void editMaxDienen(boolean istleiter, int anzMaxDienen) {
		int id = 0;
		if (istleiter) {
			id++;
		}
		settings[id] = new Setting(Attribut.MAX, id, anzMaxDienen);
	}
	
	public List<Setting> getSettings() {
		List<Setting> rtn = new ArrayList<>();
		for (Setting setting : settings) {
			if (setting.getAttribut() != Attribut.MAX) {
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
