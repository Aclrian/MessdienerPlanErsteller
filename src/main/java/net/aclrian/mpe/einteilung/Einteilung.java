package net.aclrian.mpe.einteilung;

import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.utils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Einteilung {

    private final List<Messe> messen;
    private final List<Messdiener> messdiener;

    public Einteilung(List<Messdiener> messdiener, List<Messe> messen) {
        this.messdiener = messdiener;
        this.messen = messen;
    }

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
        if (!(sm instanceof Sonstiges)) {
            for (LocalDate date = start.with(sm.getWochentag()); date.isBefore(end); date = date.plusDays(7)) {
                LocalDateTime messeTime = start.atTime(sm.getBeginnStunde(), sm.getBeginnMinute());
                Messe m = new Messe(messeTime, sm);
                generatedMessen.add(m);
            }
        }
        return generatedMessen;
    }

    public void einteilen() {
        messdiener.sort(Messdaten.MESSDIENER_EINTEILEN_COMPARATOR);
        if (messen.isEmpty()) {
            return;
        }
        // neuer Monat:
        LocalDate nextMonth = messen.get(0).getDate().toLocalDate();
        nextMonth = nextMonth.plusMonths(1);
        if (MPELog.getLogger().isDebugEnabled()) {
            MPELog.getLogger().info("nächster Monat bei: {}", DateUtil.DATE.format(nextMonth));
        }
        // EIGENTLICHER ALGORYTHMUS
        for (Messe me : messen) {
            if (me.getDate().toLocalDate().isAfter(nextMonth)) {
                nextMonth = nextMonth.plusMonths(1);
                if (MPELog.getLogger().isDebugEnabled()) {
                    MPELog.getLogger().info("nächster Monat: Es ist {}", DateUtil.DATE.format(me.getDate().toLocalDate()));
                }
                for (Messdiener messdiener : messdiener) {
                    messdiener.getMessdaten().naechsterMonat();
                }
            }
            MPELog.getLogger().info("Messe dran: {}", me.getID());
            if (me.getStandardMesse() instanceof Sonstiges) {
                einteilen(me, EnumAction.EINFACH_EINTEILEN);
            } else {
                einteilen(me, EnumAction.TYPE_BEACHTEN);
            }
            MPELog.getLogger().info("Messe fertig: {}", me.getID());
        }
    }

    private void einteilen(Messe m, EnumAction act) {
        List<Messdiener> medis;
        if (act == EnumAction.EINFACH_EINTEILEN) {
            medis = get(new Sonstiges(), m, false, false);
        } else { //EnumAction.TYPE_BEACHTEN
            medis = get(m.getStandardMesse(), m, false, false);
        }
        if (MPELog.getLogger().isDebugEnabled()) {
            MPELog.getLogger().info("{} für {}", medis.size(), m.getNochBenoetigte());
        }
        for (Messdiener medi : medis) {
            einteilen(m, medi, false, false);
        }
        if (!m.istFertig()) {
            zwang(m);
        }
    }

    private void zwang(Messe m, boolean zangDate, boolean zwangAnz, String loggerOutput) {
        List<Messdiener> medis = get(m.getStandardMesse(), m, zangDate, zwangAnz);
        if (MPELog.getLogger().isDebugEnabled()) {
            MPELog.getLogger().warn("{} {}", m, loggerOutput);
        }
        for (Messdiener medi : medis) {
            einteilen(m, medi, false, true);
        }
    }

    private void zwang(Messe m) {
        if (m.istFertig()) {
            return;
        }
        final String start = "Die Messe ";
        String secondPart = " hat zu wenige Messdiener.\nNoch ";

        if (!(m.getStandardMesse() instanceof Sonstiges)) {
            if (Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                    + " werden benötigt.\nSollen Messdiener eingeteilt werden, die standardmäßig die Messe \n'"
                    + m.getStandardMesse().toKurzerBenutzerfreundlichenString()
                    + "' dienen können, aber deren Anzahl schon zu hoch ist?")) {
                zwang(m, false, true, " einteilen ohne Anzahl beachten");
            }

            if (Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                    + " werden benötigt.\nSollen Messdiener eingeteilt werden, die an dem Tag \n'"
                    + DateUtil.DATE.format(m.getDate()) + "' dienen können?")) {
                zwang(m, false, false, " einteilen ohne Standardmesse beachten");
            }
        }
        if (!m.istFertig() && Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                + " werden benötigt.\nSollen Messdiener zwangsweise eingeteilt werden?")) {
            zwang(m, true, true, " einteilen ohne Standardmesse beachten");
        }
        if (!m.istFertig()) {
            Dialogs.getDialogs().error("Die Messe" + m.getID().replace("\t", "   ") + " wird zu wenige Messdiener haben.");
        }
    }

    private void einteilen(Messe m, Messdiener medi, boolean zwangdate, boolean zwanganz) {
        boolean kannStandardMesse = false;
        if (!m.istFertig()) {
            kannStandardMesse = m.einteilen(medi, zwangdate, zwanganz);
        }
        if (!m.istFertig() && kannStandardMesse) {
            RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
            List<Messdiener> anvertraute = new ArrayList<>(medi.getMessdaten().getGeschwister());
            anvertraute.addAll(medi.getMessdaten().getFreunde());
            anvertraute = rd.removeDuplicatedEntries(anvertraute);
            anvertraute.sort(Messdaten.MESSDIENER_EINTEILEN_COMPARATOR);
            for (Messdiener messdiener : anvertraute) {
                if (m.istFertig()) {
                    break;
                }
                kannStandardMesse = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse());
                if (messdiener.getMessdaten().kann(m.getDate().toLocalDate(), zwangdate, zwanganz) && kannStandardMesse) {
                    MPELog.getLogger().info("{} dient mit {}?", messdiener, medi);
                    einteilen(m, messdiener, zwangdate, zwanganz);
                }
            }
        }
    }

    private List<Messdiener> get(StandardMesse sm, Messe m, boolean zwangdate, boolean zwanganz) {
        ArrayList<Messdiener> allForSMesse = new ArrayList<>();
        for (Messdiener medi : messdiener) {
            int id = medi.istLeiter() ? 1 : 0;
            if (medi.getDienverhalten().getBestimmtes(sm)
                    && DateienVerwalter.getInstance().getPfarrei().getSettings().getDaten(id).anzahlDienen() != 0
                    && medi.getMessdaten().kann(m.getDate().toLocalDate(), zwangdate, zwanganz)) {
                allForSMesse.add(medi);
            }
        }
        try {
            Collections.shuffle(allForSMesse, SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException e) {
            Dialogs.getDialogs().error(e, "Konnte keinen kryptografisch sicheren RNG finden.");
        }
        allForSMesse.sort(Messdaten.MESSDIENER_EINTEILEN_COMPARATOR);
        return allForSMesse;
    }

    private enum EnumAction {
        EINFACH_EINTEILEN, TYPE_BEACHTEN
    }
}
