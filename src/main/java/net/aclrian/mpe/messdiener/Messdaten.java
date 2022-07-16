package net.aclrian.mpe.messdiener;

import net.aclrian.mpe.controller.MediController;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.RemoveDoppelte;

import java.io.IOException;
import java.time.*;
import java.util.*;

public class Messdaten {

    private final int maxMessen;
    RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
    private List<Messdiener> geschwister;
    private List<Messdiener> freunde;
    private int anzMessen;
    private int insgesamtEingeteilt;
    private ArrayList<LocalDate> eingeteilt = new ArrayList<>();
    private ArrayList<LocalDate> ausgeteilt = new ArrayList<>();
    private ArrayList<LocalDate> pause = new ArrayList<>();

    public Messdaten(Messdiener m) {
        geschwister = new ArrayList<>();
        freunde = new ArrayList<>();
        update(m);
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

    public void ausausteilen(LocalDate d) {
        ausgeteilt.remove(d);
    }

    public int berecheMax(int eintritt, int aktdatum, boolean leiter, Einstellungen einstellungen) {
        int id = leiter ? 1 : 0;

        int zwei = einstellungen.getDaten(id).getAnzDienen();
        int abstand = Integer.min(Integer.max(aktdatum - eintritt, 0), Einstellungen.LENGHT - 3);
        int eins = einstellungen.getDaten(abstand + 2).getAnzDienen();
        if (zwei < eins) {
            eins = zwei;
        }
        return eins;
    }

    public boolean einteilen(LocalDate date, boolean hochamt, boolean datezwang, boolean anzzwang) {
        if (kann(date, datezwang, anzzwang)) {
            eingeteilt.add(date);
            pause.add(gettheNextDay(date));
            pause.add(getthepreviuosDay(date));
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
        if (kannvorzeitg(date)) {
            eingeteilt.add(date);
            insgesamtEingeteilt++;
            if (!hochamt) {
                pause.add(gettheNextDay(date));
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
        geschwister = rd.removeDuplicatedEntries(geschwister);
        freunde = rd.removeDuplicatedEntries(freunde);

        rtn.addAll(setTeilAnvertraute(medis, geschwister));
        rtn.addAll(setTeilAnvertraute(medis, freunde));

        return rd.removeDuplicatedEntries(rtn);
    }

    private ArrayList<Messdiener> setTeilAnvertraute(List<Messdiener> medis, List<Messdiener> freunde) {
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

    public boolean kannvorzeitg(LocalDate date) {
        return !contains(date, ausgeteilt);
    }

    public boolean kann(LocalDateTime date, boolean dateZwang, boolean zwang) {
        return kann(date.toLocalDate(), dateZwang, zwang);
    }
    public boolean kann(LocalDate date, boolean dateZwang, boolean zwang) {
        return kanndann(date, dateZwang) && (kannnoch() || (zwang && ((anzMessen - maxMessen) <= (int) (maxMessen * 0.2) + 1)));
    }

    public boolean kanndann(LocalDate date, boolean zwang) {
        if (eingeteilt.isEmpty() && ausgeteilt.isEmpty() && (zwang || pause.isEmpty())) {
            return true;
        }
        if (contains(date, ausgeteilt) || contains(gettheNextDay(date), ausgeteilt)
                || contains(getthepreviuosDay(date), ausgeteilt)) {
            return false;
        }
        if (!zwang) {
            if (contains(date, eingeteilt) || contains(gettheNextDay(date), eingeteilt)
                    || contains(getthepreviuosDay(date), eingeteilt)) {
                return false;
            }
            return !contains(date, pause);
        }
        return true;
    }

    private boolean contains(LocalDate date, ArrayList<LocalDate> array) {
        return array.contains(date);
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

    private LocalDate gettheNextDay(LocalDate date) {
        return date.plusDays(1);
    }

    private LocalDate getthepreviuosDay(LocalDate date) {
        return date.plusDays(-1);
    }

    @SuppressWarnings("unused")
    public void loescheAbwesendeDaten() {
        ausgeteilt = new ArrayList<>();
    }

    public double getSortierenDouble() {
        return anzMessen / getMaxMessenDouble();
    }

    public int getInsgesamtEingeteilt() {
        return insgesamtEingeteilt;
    }

    public void update(Messdiener m) {
        update(false, m.getGeschwister(), m);
        update(true, m.getFreunde(), m);
    }

    private void update(boolean isfreund, String[] s, Messdiener m) {
        for (String value : s) {
            Messdiener medi;
            if (!value.equals("") && !value.equals("LEER") && !value.equals("Vorname, Nachname")) {
                try {
                    medi = sucheMessdiener(value, m, DateienVerwalter.getInstance().getMessdiener());
                    if (medi != null) {
                        this.geschwister.add(medi);
                        geschwister = rd.removeDuplicatedEntries(this.geschwister);
                    }
                } catch (CouldnotFindMedi e) {
                    if (messdienerrNotFound(isfreund, s, m, value, e)) return;
                }
            }
        }
    }

    private boolean messdienerrNotFound(boolean isfreund, String[] s, Messdiener m, String value, CouldnotFindMedi e) {
        boolean beheben = Dialogs.getDialogs().frage(e.getMessage(),
                "ignorieren", "beheben");
        if (beheben) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
            list.remove(value);
            String[] gew = MediController.getArrayString(list, isfreund ? Messdiener.LENGHT_FREUNDE : Messdiener.LENGHT_GESCHWISTER);
            if (isfreund) m.setFreunde(gew);
            else m.setGeschwister(gew);
            WriteFile wf = new WriteFile(m);
            try {
                wf.toXML();
            } catch (IOException ex) {
                Dialogs.getDialogs().error(ex, "Konnte es nicht beheben.");
            }
            update(isfreund, gew, m);
            return true;
        }
        return false;
    }

    public Messdiener sucheMessdiener(String geschwi, Messdiener akt, List<Messdiener> medis) throws CouldnotFindMedi {
        for (Messdiener messdiener : medis) {
            if (messdiener.toString().equals(geschwi)) {
                return messdiener;
            }
        }
        throw new CouldnotFindMedi("Konnte für " + akt.toString() + " : " + geschwi + " nicht finden");
    }

    public int getAnzMessen() {
        return anzMessen;
    }

    public static class CouldnotFindMedi extends Exception {
        public CouldnotFindMedi(String message) {
            super(message);
        }
    }
}
