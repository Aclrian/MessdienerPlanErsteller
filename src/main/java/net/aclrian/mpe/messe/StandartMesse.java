package net.aclrian.mpe.messe;

import java.util.Comparator;
import java.util.Objects;

public class StandartMesse {
    public static final Comparator<StandartMesse> STANDART_MESSE_COMPARATOR = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());
    private final int beginnStunde;
    private final String beginnMinute;
    private final String ort;
    private final String wochentag;
    private final int anzMessdiener;
    private final String typ;

    public StandartMesse(String wochentag, int beginnH, String beginnMin, String ort, int anzMessdiener,
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

    public String getWochentag() {
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
        return typ + " jeden " + wochentag + "., " + "um " + beginnStunde + ":" + beginnMinute + " in " + ort;
    }

    public String toReduziertenString() {
        return wochentag + "-" + beginnStunde + "-" + beginnMinute + // ort+ "-" + typ +
                "-" + anzMessdiener;
    }

    public String getWochentags() {
        if (wochentag.equalsIgnoreCase("Mo")) {
            return "montags";
        }
        if (wochentag.equalsIgnoreCase("Di")) {
            return "dienstags";
        }
        if (wochentag.equalsIgnoreCase("Mi")) {
            return "mittwochs";
        }
        if (wochentag.equalsIgnoreCase("Do")) {
            return "donnerstags";
        }
        if (wochentag.equalsIgnoreCase("Fr")) {
            return "freitags";
        }
        if (wochentag.equalsIgnoreCase("Sa")) {
            return "samstags";
        }
        if (wochentag.equalsIgnoreCase("So")) {
            return "sonntags";
        }
        return wochentag;
    }

    public String tolangerBenutzerfreundlichenString() {
        return getWochentags() + " um " + beginnStunde + ":" + beginnMinute + " Uhr in " + ort + " als " + typ + " mit " + anzMessdiener
                + " Messdienern";
    }

    public String getZeit() {
        return getWochentags() + " um " + beginnStunde + ":" + beginnMinute + " Uhr";
    }

    public String getMessdienerAnzahl() {
        return anzMessdiener + " Messdiener";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Messverhalten) {
            return this.toString().equals(obj.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginnStunde, beginnMinute, ort, wochentag, anzMessdiener, typ);
    }

    public String getBeginnStundealsString() {
        String rtn = String.valueOf(this.beginnStunde);
        if (rtn.length() == 1){
            rtn = "0" + this.beginnStunde;
        }
        return rtn;
    }
}
