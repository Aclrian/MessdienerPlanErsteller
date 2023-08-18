package net.aclrian.mpe.algorithms;

import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.MPELog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class Generation {

    private Generation() { }

    public static List<Messe> generiereDefaultMessen(LocalDate anfang, LocalDate ende) {
        ArrayList<Messe> generatedMessen = new ArrayList<>();
        for (StandardMesse sm : DateienVerwalter.getInstance().getPfarrei().getStandardMessen()) {
            if (!(sm instanceof Sonstiges)) {
                generatedMessen.addAll(generiereDefaultMesseFuerStandardmesse(sm, anfang, ende));
            }
        }
        generatedMessen.sort(Messe.MESSE_COMPARATOR);
        MPELog.getLogger().info("DefaultMessen generiert");
        return generatedMessen;
    }

    public static List<Messe> generiereDefaultMesseFuerStandardmesse(StandardMesse sm, LocalDate start, LocalDate end) {
        List<Messe> generatedMessen = new ArrayList<>();
        if (sm instanceof Sonstiges) {
            return generatedMessen;
        }
        for (LocalDate date = start.with(TemporalAdjusters.next(sm.getWochentag())); date.isBefore(end); date = date.plusDays(7)) {
            LocalDateTime messeTime = date.atTime(sm.getBeginnStunde(), sm.getBeginnMinute());
            Messe m = new Messe(messeTime, sm);
            generatedMessen.add(m);
        }
        if (!sm.getNonDefaultWeeklyRepetition().isEmpty()) {
            // make sure that only the 1st, 2nd, 3rd, 4th or last Messe of a month will be returned
            List<Messe> selectMessen = new ArrayList<>();
            for (Messe messe : generatedMessen) {
                int generatedMesseOnDayOfWeek = messe.getDate().get(ChronoField.ALIGNED_WEEK_OF_MONTH);
                boolean atLastElement = generatedMessen.indexOf(messe) == generatedMessen.size() - 1;
                boolean noFiveElementsGenerated = generatedMessen.size() < 5;
                boolean pickLastElementInsteadOfNotExisting5thElement = sm.getNonDefaultWeeklyRepetition().contains(5)
                        && atLastElement
                        && noFiveElementsGenerated;
                if (sm.getNonDefaultWeeklyRepetition().contains(generatedMesseOnDayOfWeek)
                        || pickLastElementInsteadOfNotExisting5thElement) {
                    selectMessen.add(messe);
                }
            }
            generatedMessen.removeIf(messe -> true);
            generatedMessen.addAll(selectMessen);
        }
        return generatedMessen;
    }
}
