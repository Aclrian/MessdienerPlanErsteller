package net.aclrian.mpe.messe;


import org.apache.logging.log4j.util.Strings;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

public class StandardMesse {
    public static final Comparator<StandardMesse> STANDARD_MESSE_COMPARATOR = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());
    /**
     * The elements that are allowed in nonDefaultWeeklyRepetition.
     * If the list ist not empty and a number n in that list included,
     * only the Messe of nth week of a month will be generated.
     * If there is no 5th week of one day of week, the 4th will be picked instead.
     */
    public static final List<Integer> ALLOWED_REPETITION_NUMBERS = List.of(1, 2, 3, 4, 5);
    private final int beginnStunde;
    private final String beginnMinute;
    private final String ort;
    private final DayOfWeek wochentag;
    private final int anzMessdiener;
    private final String typ;

    /**
     * Indicates on which Week of a month a Messe will be generated.
     * All possible Messen will be generated, if the list is empty.
     */
    private final List<Integer> nonDefaultWeeklyRepetition;

    public StandardMesse(DayOfWeek wochentag, int beginnStunde, String beginnMinute,
                         String ort, int anzMessdiener, String typ) {
        this(wochentag, beginnStunde, beginnMinute, ort, anzMessdiener, typ, new ArrayList<>());
    }

    public StandardMesse(DayOfWeek wochentag, int beginnStunde, String beginnMinute,
                         String ort, int anzMessdiener, String typ, List<Integer> nonDefaultWeeklyRepetition) {
        this.ort = ort;
        this.anzMessdiener = anzMessdiener;
        this.typ = typ;
        this.wochentag = wochentag;
        this.beginnStunde = beginnStunde;
        this.beginnMinute = beginnMinute;
        this.nonDefaultWeeklyRepetition = nonDefaultWeeklyRepetition;
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

    @Override
    public String toString() {
        String s = beginnStunde < 10 ? "0" + beginnStunde : beginnStunde + "";
        String repetition = "_" + Strings.join(nonDefaultWeeklyRepetition, ',');
        if (repetition.length() == 1) {
            repetition = "";
        }
        return wochentag.getValue() + "_" + s + ":" + beginnMinute + "-" + ort + " " + typ + " "
                + anzMessdiener + repetition;
    }

    public String toKurzerBenutzerfreundlichenString() {
        String repetition = Strings.join(nonDefaultWeeklyRepetition, ';').replace(";", "., ");
        return repetition + ". " + wochentag.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                + beginnStunde + ":" + beginnMinute + ": "
                + ort + " " + typ + " (" + anzMessdiener + ")";
    }

    public String toBenutzerfreundlichenString() {
        String repetition = Strings.join(nonDefaultWeeklyRepetition, ';').replace(";", ", ");
        if (!repetition.isEmpty()) {
            repetition += ". ";
        }
        return typ + " jeden " + repetition + wochentag.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + ", "
                + "um " + beginnStunde + ":" + beginnMinute + " in " + ort + " (" + anzMessdiener + ")";
    }

    public String toReduziertenString() {
        return wochentag.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()) + "-" + beginnStunde + "-" + beginnMinute // ort+ "-" + typ +
                + "-" + anzMessdiener;
    }

    public String toLangerBenutzerfreundlichenString() {
        String repetition = Strings.join(nonDefaultWeeklyRepetition, ';').replace(";", "., ");
        if (!repetition.isEmpty()) {
            repetition += ".";
        }
        return repetition + " " + wochentag.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " um " + beginnStunde + ":" + beginnMinute + " Uhr"
                + " in " + ort + " als " + typ + " mit " + anzMessdiener + " Messdienern";
    }

    public String getZeit() {
        return wochentag.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " um " + beginnStunde + ":" + beginnMinute + " Uhr";
    }

    public String getMessdienerAnzahl() {
        return anzMessdiener + " Messdiener";
    }

    public List<Integer> getNonDefaultWeeklyRepetition() {
        return nonDefaultWeeklyRepetition;
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

    public String getRepetitionString() {
        if (nonDefaultWeeklyRepetition.isEmpty()) {
            return "wöchentlich";
        }
        return "jeden " + Strings.join(nonDefaultWeeklyRepetition, ';').replace(";", "., ") + ".";
    }
}
