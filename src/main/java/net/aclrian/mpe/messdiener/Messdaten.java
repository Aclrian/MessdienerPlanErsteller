package net.aclrian.mpe.messdiener;


import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.utils.*;

import java.time.LocalDate;
import java.util.*;

public class Messdaten {
    public static final Comparator<? super Messdiener> MESSDIENER_EINTEILEN_COMPARATOR = (Comparator<Messdiener>) (o1, o2) -> {
        // Quotient aus aktuelle Anzahl und maximaler Anzahl, um sicherzustellen, dass die Messen fair unter den Messdiener aufgeteilt sind.
        double q1;
        Messdaten messdaten1 = o1.getMessdaten();
        if (messdaten1.maxMessen == 0) {
            q1 = Double.POSITIVE_INFINITY;
        } else {
            q1 = messdaten1.anzMessen / (double) messdaten1.maxMessen;
        }
        double q2;
        Messdaten messdaten = o2.getMessdaten();
        if (messdaten.maxMessen == 0) {
            q2 = Double.POSITIVE_INFINITY;
        } else {
            q2 = messdaten.anzMessen / (double) messdaten.maxMessen;
        }
        return Double.compare(q1, q2);
    };
    private final int maxMessen;
    private final List<Messdiener> geschwister;
    private final List<Messdiener> freunde;
    private int anzMessen;
    private int insgesamtEingeteilt;
    private ArrayList<LocalDate> eingeteilt = new ArrayList<>();
    private final ArrayList<LocalDate> ausgeteilt = new ArrayList<>();
    private ArrayList<LocalDate> pause = new ArrayList<>();

    public Messdaten(Messdiener m) {
        anzMessen = 0;
        insgesamtEingeteilt = 0;
        int eintritt = m.getEintritt();
        int aktdatum = DateUtil.getCurrentYear();
        boolean leiter = m.istLeiter();
        geschwister = FindMessdiener.updateGeschwister(m);
        freunde = FindMessdiener.updateFreunde(m);
        Einstellungen einstellungen = DateienVerwalter.getInstance().getPfarrei().getSettings();

        // ermittle maximale Anzahl an Messen pro Monat
        int id = leiter ? 1 : 0;
        int upperLimitForAnzahl = einstellungen.getDaten(id).anzahlDienen();
        int abstand = Integer.min(Integer.max(aktdatum - eintritt, 0), Einstellungen.LENGTH - 3);
        int maxMessen = einstellungen.getDaten(abstand + 2).anzahlDienen();
        this.maxMessen = Math.min(maxMessen, upperLimitForAnzahl);
    }

    public void austeilen(LocalDate d) {
        ausgeteilt.add(d);
    }

    public void ausAusteilenEntfernen(LocalDate d) {
        ausgeteilt.remove(d);
    }

    public boolean einteilen(LocalDate date, boolean hochamt, boolean datezwang, boolean anzzwang) {
        if (kann(date, datezwang, anzzwang)) {
            einteilen(date, hochamt);
            return true;
        }
        return false;
    }

    private void einteilen(LocalDate date, boolean hochamt) {
        eingeteilt.add(date);
        pause.add(DateUtil.getNextDay(date));
        pause.add(DateUtil.getPreviousDay(date));
        insgesamtEingeteilt++;
        if (!hochamt || DateienVerwalter.getInstance().getPfarrei().zaehlenHochaemterMit()) {
            anzMessen++;
        }
    }


    public boolean vorzeitigEinteilen(LocalDate date, boolean hochamt) {
        if (kannVorzeitig(date)) {
            einteilen(date, hochamt);
            return true;
        }
        return false;
    }

    public void nullen() {
        anzMessen = 0;
        insgesamtEingeteilt = 0;
        eingeteilt = new ArrayList<>();
        pause = new ArrayList<>();
    }

    public int getMaxMessen() {
        return maxMessen;
    }

    public boolean kannVorzeitig(LocalDate date) {
        return !ausgeteilt.contains(date);
    }

    public boolean kann(LocalDate date, boolean dateZwang, boolean zwang) {
        boolean kannNoch = maxMessen > anzMessen;
        boolean kannDate = !ausgeteilt.contains(date)
                && (dateZwang
                || !eingeteilt.contains(date) && !pause.contains(date));
        boolean nichtZuViele = anzMessen - maxMessen <= (int) (maxMessen * 0.2) + 1;
        return kannDate && (kannNoch || zwang && nichtZuViele);
    }

    public List<Messdiener> getFreunde() {
        return freunde;
    }

    public List<Messdiener> getGeschwister() {
        return geschwister;
    }

    public boolean ausgeteilt(LocalDate date) {
        return ausgeteilt.contains(date);
    }

    public void naechsterMonat() {
        anzMessen = 0;
    }

    public int getInsgesamtEingeteilt() {
        return insgesamtEingeteilt;
    }

    public int getAnzMessen() {
        return anzMessen;
    }
}
