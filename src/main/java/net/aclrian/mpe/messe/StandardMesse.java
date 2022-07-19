package net.aclrian.mpe.messe;

import net.aclrian.mpe.utils.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

public class StandardMesse {
    public static final Comparator<StandardMesse> STANDARD_MESSE_COMPARATOR = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());
    private final int beginnStunde;
    private final String beginnMinute;
    private final String ort;
    private final DayOfWeek wochentag;
    private final int anzMessdiener;
    private final String typ;

    public StandardMesse(DayOfWeek wochentag, int beginnH, String beginnMin, String ort, int anzMessdiener,
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
        return toKurzerBenutzerfreundlichenString();
    }

    @Override
    public String toString() {
        String s = beginnStunde < 10 ? "0" + beginnStunde : beginnStunde + "";
        return wochentag.getValue() + "_" + s + ":" + beginnMinute + "-" + ort + " " + typ + " "
                + anzMessdiener;
    }

    public String toKurzerBenutzerfreundlichenString() {
        return wochentag.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + beginnStunde + ":" + beginnMinute + ": " + ort + " " + typ + " (" + anzMessdiener
                + ")";
    }

    public static StandardMesse fromBenutzerfreundlichenString(String s) {
        String[] split = s.split(" jeden ");
        if (split.length != 2) throw new IllegalArgumentException(s);
        String typ = split[0];
        split = split[1].split(", um");
        if (split.length != 2) throw new IllegalArgumentException(s);
        TemporalAccessor accessor = DateUtil.SHORT_STANDALONE.parse(split[0]);
        DayOfWeek dow = DayOfWeek.from(accessor);
        split = split[1].split(":");
        if (split.length != 2) throw new IllegalArgumentException(s);
        int stunde = Integer.parseInt(split[0]);
        split = split[1].split(" in ");
        if (split.length != 2) throw new IllegalArgumentException(s);
        String minute = split[0];
        split = split[1].split(" \\(");
        if (split.length != 2) throw new IllegalArgumentException(s);
        String ort = split[0];
        int anz = Integer.parseInt(split[1].substring(0, split[1].length() - 1));

        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return new StandardMesse(dow, stunde, minute, ort, anz, typ);
    }

    public String toBenutzerfreundlichenString() {
        return typ + " jeden " + wochentag.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + ", " + "um " + beginnStunde + ":" + beginnMinute + " in " + ort + " (" + anzMessdiener + ")";
    }

    public String toReduziertenString() {
        return wochentag.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()) + "-" + beginnStunde + "-" + beginnMinute + // ort+ "-" + typ +
                "-" + anzMessdiener;
    }

    public String toLangerBenutzerfreundlichenString() {
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
        if (obj instanceof StandardMesse sm) {
            return this.toLangerBenutzerfreundlichenString().equals(sm.toLangerBenutzerfreundlichenString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginnStunde, beginnMinute, ort, wochentag, anzMessdiener, typ);
    }
}
