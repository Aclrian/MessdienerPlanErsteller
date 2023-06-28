package net.aclrian.mpe.messdiener;


import net.aclrian.mpe.controller.MediController;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.utils.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public Messdaten(Messdiener m) throws CouldFindMessdiener {
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

    public boolean kannVorzeitig(LocalDate date) {
        return !ausgeteilt.contains(date);
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

    public void update(Messdiener m) throws CouldFindMessdiener {
        update(false, m.getGeschwister(), m);
        update(true, m.getFreunde(), m);
    }

    private void update(boolean isFreund, String[] s, Messdiener m) throws CouldFindMessdiener {
        for (String value : s) {
            Messdiener medi = null;
            if (!value.equals("") && !value.equals("LEER") && !value.equals("Vorname, Nachname")) {
                try {
                    medi = sucheMessdiener(value, m);
                } catch (CouldNotFindMessdiener e) {
                    messdienerNotFound(isFreund, s, m, value, e);
                }
                if (medi != null) {
                    if (isFreund) {
                        this.freunde.add(medi);
                        freunde = rd.removeDuplicatedEntries(this.freunde);
                    } else {
                        this.geschwister.add(medi);
                        geschwister = rd.removeDuplicatedEntries(this.geschwister);
                    }
                }
            }
        }
    }

    private void messdienerNotFound(boolean isFreund, String[] s, Messdiener m, String value, CouldNotFindMessdiener e) {
        boolean beheben = Dialogs.getDialogs().frage(e.getMessage(),
                "ignorieren", "beheben");
        if (beheben) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
            list.remove(value);
            String[] gew = MediController.getArrayString(list, isFreund ? Messdiener.LENGHT_FREUNDE : Messdiener.LENGHT_GESCHWISTER);
            if (isFreund) m.setFreunde(gew);
            else m.setGeschwister(gew);
            WriteFile wf = new WriteFile(m);
            try {
                wf.toXML();
            } catch (IOException ex) {
                Dialogs.getDialogs().error(ex, "Konnte es nicht beheben.");
            }
            DateienVerwalter.getInstance().reloadMessdiener();
        }
    }

    public Messdiener sucheMessdiener(String geschwi, Messdiener akt) throws CouldNotFindMessdiener, CouldFindMessdiener {
        for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
            if (messdiener.toString().equals(geschwi)) {
                return messdiener;
            }
        }
        // additional Search for Vorname and Nachname
        String replaceSeparators = geschwi.replace(", ", " ")
                .replace("-", " ").replace("; ", " ").toLowerCase(Locale.getDefault());
        String[] parts = replaceSeparators.split(" ");
        Arrays.sort(parts);
        for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
            String[] parts2 = messdiener.toString().replace(", ", " ")
                    .replace("-", " ").replace("; ", " ").toLowerCase(Locale.getDefault()).split(" ");
            Arrays.sort(parts2);
            if (Arrays.equals(parts, parts2)) {
                String message = "Konnte für " + akt.toString() + " : " + geschwi + " finden";
                Log.getLogger().info(message);
                throw new CouldFindMessdiener(messdiener.toString(), geschwi, message);
            }
        }

        throw new CouldNotFindMessdiener("Konnte für " + akt.toString() + " : " + geschwi + " nicht finden");
    }

    public int getAnzMessen() {
        return anzMessen;
    }

    public static class CouldNotFindMessdiener extends Exception {
        public CouldNotFindMessdiener(String message) {
            super(message);
        }
    }

    public static class CouldFindMessdiener extends Exception {
        private final String messdienerID;
        private final String string;

        public CouldFindMessdiener(String foundID, String string, String message) {
            super(message);
            this.messdienerID = foundID;
            this.string = string;
        }

        public String getFoundMessdienerID() {
            return messdienerID;
        }

        public String getString() {
            return string;
        }
    }
}
