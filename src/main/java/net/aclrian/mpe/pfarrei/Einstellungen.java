package net.aclrian.mpe.pfarrei;


import net.aclrian.mpe.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class Einstellungen {

    public static final int LENGTH = 21;
    private final Setting[] settings = new Setting[LENGTH];

    public Einstellungen() {
        settings[0] = new Setting(Setting.Attribut.MAX, 0, 0);
        settings[1] = new Setting(Setting.Attribut.MAX, 1, 0);
        for (int i = 2; i < settings.length; i++) {
            settings[i] = new Setting(Setting.Attribut.YEAR, i - 2, 0);
        }
    }

    public void editiereYear(int eintrittAbstandZumAktuellemJahr, int anzMaxDienen) {
        int i = eintrittAbstandZumAktuellemJahr + 2;
        if (i >= LENGTH) {
            return;
        }
        settings[i] = new Setting(Setting.Attribut.YEAR, eintrittAbstandZumAktuellemJahr, anzMaxDienen);
        if (anzMaxDienen > settings[0].anzahlDienen()) {
            Log.getLogger().warn("Die Anzahl ({}) von der ID: {}"
                    + " ist größer als der maximale Standardwert von allen nicht Leitern. Dieser max. Wert wird somit neu gesetzt.", anzMaxDienen, eintrittAbstandZumAktuellemJahr);
            settings[0] = new Setting(Setting.Attribut.MAX, 0, anzMaxDienen);
        }
    }

    public void editMaxDienen(boolean leiter, int anzMaxDienen) {
        int id = 0;
        if (leiter) {
            id++;
        }
        settings[id] = new Setting(Setting.Attribut.MAX, id, anzMaxDienen);
    }

    public List<Setting> getSettings() {
        List<Setting> rtn = new ArrayList<>();
        for (Setting setting : settings) {
            if (setting.attribut() != Setting.Attribut.MAX) {
                rtn.add(setting);
            }
        }
        return rtn;
    }

    public Setting getDaten(int index) {
        if (index > LENGTH) {
            //if a medi has an older year of entry
            return settings[LENGTH];
        }
        return settings[index];
    }
}
