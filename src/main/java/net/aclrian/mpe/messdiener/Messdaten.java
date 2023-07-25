package net.aclrian.mpe.messdiener;


import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.utils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Messdaten {

    private final int maxMessen;
    private List<Messdiener> geschwister;
    private List<Messdiener> freunde;
    private int anzMessen;
    private int insgesamtEingeteilt;
    private ArrayList<LocalDate> eingeteilt = new ArrayList<>();
    private final ArrayList<LocalDate> ausgeteilt = new ArrayList<>();
    private ArrayList<LocalDate> pause = new ArrayList<>();

    public Messdaten(Messdiener m) throws FindMessdiener.CouldFindMessdiener {
        FindMessdiener findMessdiener = new FindMessdiener(this);
        geschwister = findMessdiener.updateGeschwister(m);
        freunde = findMessdiener.updateFreunde(m);
        maxMessen = berecheMax(m.getEintritt(), getMaxYear(), m.istLeiter(),
                DateienVerwalter.getInstance().getPfarrei().getSettings());
        anzMessen = 0;
        insgesamtEingeteilt = 0;
    }

    public static int getMinYear() {
        return getMaxYear() - 18;
    }

    public static int getMaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public void austeilen(LocalDate d) {
        ausgeteilt.add(d);
    }

    public void ausAusteilenEntfernen(LocalDate d) {
        ausgeteilt.remove(d);
    }

    public int berecheMax(int eintritt, int aktdatum, boolean leiter, Einstellungen einstellungen) {
        int id = leiter ? 1 : 0;

        int zwei = einstellungen.getDaten(id).anzahlDienen();
        int abstand = Integer.min(Integer.max(aktdatum - eintritt, 0), Einstellungen.LENGTH - 3);
        int eins = einstellungen.getDaten(abstand + 2).anzahlDienen();
        if (zwei < eins) {
            eins = zwei;
        }
        return eins;
    }

    public boolean einteilen(LocalDate date, boolean hochamt, boolean datezwang, boolean anzzwang) {
        if (kann(date, datezwang, anzzwang)) {
            eingeteilt.add(date);
            pause.add(DateUtil.getNextDay(date));
            pause.add(DateUtil.getPreviousDay(date));
            anzMessen++;
            insgesamtEingeteilt++;
            if (hochamt) {
                anzMessen--;
            }
            return true;
        }
        return false;
    }


    public boolean einteilenVorzeitig(LocalDate date, boolean hochamt) {
        if (kannVorzeitig(date)) {
            eingeteilt.add(date);
            insgesamtEingeteilt++;
            if (!hochamt) {
                pause.add(DateUtil.getNextDay(date));
                anzMessen++;
            }
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

    public List<Messdiener> getAnvertraute(List<Messdiener> medis) {
        ArrayList<Messdiener> rtn = new ArrayList<>();
        RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
        geschwister = rd.removeDuplicatedEntries(geschwister);
        freunde = rd.removeDuplicatedEntries(freunde);

        rtn.addAll(vereinigeAnvertraute(medis, geschwister));
        rtn.addAll(vereinigeAnvertraute(medis, freunde));

        return rd.removeDuplicatedEntries(rtn);
    }

    private ArrayList<Messdiener> vereinigeAnvertraute(List<Messdiener> medis, List<Messdiener> freunde) {
        ArrayList<Messdiener> rtn = new ArrayList<>();
        for (Messdiener messdiener : freunde) {
            for (Messdiener medi : medis) {
                if (messdiener.toString().equals(medi.toString())) {
                    rtn.add(medi);
                }
            }
        }
        return rtn;
    }

    private double getMaxMessenDouble() {
        return (maxMessen == 0) ? 0.001 : maxMessen;
    }

    public int getMaxMessen() {
        return maxMessen;
    }

    public boolean kannVorzeitig(LocalDate date) {
        return !ausgeteilt.contains(date);
    }

    public boolean kann(LocalDateTime date, boolean dateZwang, boolean zwang) {
        return kann(date.toLocalDate(), dateZwang, zwang);
    }

    public boolean kann(LocalDate date, boolean dateZwang, boolean zwang) {
        return kannDann(date, dateZwang) && (kannnoch() || zwang && (anzMessen - maxMessen <= (int) (maxMessen * 0.2) + 1));
    }

    public boolean kannDann(LocalDate date, boolean zwang) {
        if (eingeteilt.isEmpty() && ausgeteilt.isEmpty() && (zwang || pause.isEmpty())) {
            return true;
        }
        if (ausgeteilt.contains(date) || ausgeteilt.contains(DateUtil.getNextDay(date))
                || ausgeteilt.contains(DateUtil.getPreviousDay(date))) {
            return false;
        }
        if (!zwang) {
            if (eingeteilt.contains(date) || eingeteilt.contains(DateUtil.getNextDay(date))
                    || eingeteilt.contains(DateUtil.getPreviousDay(date))) {
                return false;
            }
            return !pause.contains(date);
        }
        return true;
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

    private boolean kannnoch() {
        return maxMessen > anzMessen;
    }

    public void naechsterMonat() {
        anzMessen = 0;
    }

    public double getSortierenDouble() {
        return anzMessen / getMaxMessenDouble();
    }

    public int getInsgesamtEingeteilt() {
        return insgesamtEingeteilt;
    }

    public int getAnzMessen() {
        return anzMessen;
    }

    public static String[] mapFreunde(Messdiener messdiener) {
        return messdiener.getFreunde();
    }

    public static String[] mapGeschwister(Messdiener messdiener) {
        return messdiener.getFreunde();
    }
}
