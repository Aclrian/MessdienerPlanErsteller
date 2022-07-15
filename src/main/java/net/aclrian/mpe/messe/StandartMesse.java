package net.aclrian.mpe.messe;

import java.time.*;
import java.time.format.*;
import java.util.*;

public class StandartMesse {
    public static final Comparator<StandartMesse> STANDART_MESSE_COMPARATOR = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());
    private final int beginnStunde;
    private final String beginnMinute;
    private final String ort;
    private final DayOfWeek wochentag;
    private final int anzMessdiener;
    private final String typ;

    public StandartMesse(DayOfWeek wochentag, int beginnH, String beginnMin, String ort, int anzMessdiener,
                         String typ) {
        this.ort = ort;
        this.anzMessdiener = anzMessdiener;
        this.typ = typ;
        this.wochentag = wochentag;
        beginnStunde = beginnH;
        beginnMinute = beginnMin;
    }

    public int getBeginnStunde() {
        return beginnStunde;
    }

    public String getBeginnMinuteString() {
        return beginnMinute;
    }

    public int getBeginnMinute() {
        return Integer.parseInt(beginnMinute);
    }

    public String getOrt() {
        return ort;
    }

    public DayOfWeek getWochentag() {
        return wochentag;
    }

    public int getAnzMessdiener() {
        return anzMessdiener;
    }

    public String getTyp() {
        return typ;
    }

    public String getString() {
        return tokurzerBenutzerfreundlichenString();
    }

    @Override
    public String toString() {
        String s = beginnStunde < 10 ? "0" + beginnStunde : beginnStunde + "";
        return wochentag + "_" + s + ":" + beginnMinute + "-" + ort + " " + typ + " "
                + anzMessdiener;
    }

    public String tokurzerBenutzerfreundlichenString() {
        return wochentag + ". " + beginnStunde + ":" + beginnMinute + ": " + ort + " " + typ + " (" + anzMessdiener
                + ")";
    }

    @SuppressWarnings("unused")
    public String toBenutzerfreundlichenString() {
        return typ + " jeden " + wochentag + "., " + "um " + beginnStunde + ":" + beginnMinute + " in " + ort + " ("+ anzMessdiener+")";
    }

    public String toReduziertenString() {
        return wochentag + "-" + beginnStunde + "-" + beginnMinute + // ort+ "-" + typ +
                "-" + anzMessdiener;
    }

    public String tolangerBenutzerfreundlichenString() {
        return wochentag.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " um " + beginnStunde + ":" + beginnMinute + " Uhr in " + ort + " als " + typ + " mit " + anzMessdiener
                + " Messdienern";
    }

    public String getZeit() {
        return wochentag.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " um " + beginnStunde + ":" + beginnMinute + " Uhr";
    }

    public String getMessdienerAnzahl() {
        return anzMessdiener + " Messdiener";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StandartMesse sm) {
            return this.tolangerBenutzerfreundlichenString().equals(sm.tolangerBenutzerfreundlichenString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginnStunde, beginnMinute, ort, wochentag, anzMessdiener, typ);
    }

    public String getBeginnStundealsString() {
        String rtn = String.valueOf(this.beginnStunde);
        if (rtn.length() == 1) {
            rtn = "0" + this.beginnStunde;
        }
        return rtn;
    }
}
