package net.aclrian.mpe.messdiener;

import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.utils.Dialogs;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * Klasse, mit der Messdiener digital gespeichert werden koennen
 *
 * @author Aclrian
 */
public class Messdiener {
    /**
     * Dies ist die Anzahl, wie viele Freunde ein Messdiener haben kann.
     */
    public static final int LENGHT_GESCHWISTER = 3;
    /**
     * Dies ist die Anzahl, wie viele Freunde ein Messdiener haben kann.
     */
    public static final int LENGHT_FREUNDE = 5;
    public static final Comparator<Messdiener> compForMedis = (o1, o2) -> o1.makeId().compareToIgnoreCase(o2.makeId());
    public static final Comparator<? super Messdiener> einteilen = (Comparator<Messdiener>) (o1, o2) -> {
        double d1 = o1.getMessdatenDaten().getSortierenDouble();// anz/max 0 kommt zuerst dann 0,5 --> 1
        double d2 = o2.getMessdatenDaten().getSortierenDouble();
        if ((d1 == d2) && (d1 == 0)) {
            int i1 = o1.getMessdatenDaten().getMaxMessen();
            int i2 = o2.getMessdatenDaten().getMaxMessen();
            return Integer.compare(i2, i1);
        }
        return Double.compare(d1, d2);
    };
    private String vorname = "";
    private String nachname = "";
    private String email = "";
    private String[] freunde = new String[LENGHT_FREUNDE];
    private String[] geschwister = new String[LENGHT_GESCHWISTER];
    private Messverhalten dienverhalten;
    private int eintritt = 2000;
    private boolean istLeiter = false;
    private Messdaten daten;
    private File file;

    /**
     * Konstruktor
     */
    public Messdiener(File f) {
        setDeafault();
        this.file = f;
    }

    @Override
    public String toString() {
        return makeId();
    }

    /**
     * @param vname         Vorname
     * @param nname         Nachname
     * @param eintritt      Jahr des Einfuehrung Ist davon abhaengig, wie oft der
     *                      Messdiener eingeteilt werden soll
     * @param istLeiter     Leiter, Ist davon abhaengig, wie oft der Messdiener
     *                      eingeteilt werden soll
     * @param dienverhalten Wann kann er zu welchen Standart Messen (bspw. Sontag
     *                      Morgen oder Dienstag Abend) dienen
     * @param email         eine gültige Email-Addresse
     * @throws NotValidException Wenn email nicht valid ist
     */
    public void setzeAllesNeu(String vname, String nname, int eintritt, boolean istLeiter,
                              Messverhalten dienverhalten, String email) throws NotValidException {
        setVorname(vname);
        setNachnname(nname);
        setEintritt(eintritt);
        setIstLeiter(istLeiter);
        setDienverhalten(dienverhalten);
        setEmail(email);
    }

    /**
     * @param vname         Vorname
     * @param nname         Nachname
     * @param eintritt      Jahr des Einfuehrung Ist davon abhaengig, wie oft der
     *                      Messdiener eingeteilt werden soll
     * @param istLeiter     Leiter, Ist davon abhaengig, wie oft der Messdiener
     *                      eingeteilt werden soll
     * @param dienverhalten Wann kann er zu welchen Standart Messen (bspw. Sontag
     *                      Morgen oder Dienstag Abend) dienen
     */
    public void setzeAllesNeuUndMailLeer(String vname, String nname, int eintritt, boolean istLeiter,
                                         Messverhalten dienverhalten) {
        setVorname(vname);
        setNachnname(nname);
        setEintritt(eintritt);
        setIstLeiter(istLeiter);
        setDienverhalten(dienverhalten);
        setEmailEmpty();
    }

    /**
     * setzt Standart Werte
     */
    private void setDeafault() {
        try {
            setzeAllesNeu("Vorname", "Nachname", 2000, this.istLeiter, new Messverhalten(), "");
        } catch (NotValidException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < LENGHT_FREUNDE; i++) {
            freunde[i] = "";
        }
        for (int i = 0; i < LENGHT_GESCHWISTER; i++) {
            geschwister[i] = "";
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws NotValidException {
        if (email.contentEquals("") || EmailValidator.getInstance().isValid(email)) {
            this.email = email;
            return;
        }
        throw new NotValidException();
    }

    public void setEmailEmpty() {
        email = "";
    }

    /**
     * Mit dieser Methode wird der Messdiener als .xml Datei lokal gespeichert
     */
    public void makeXML() {
        WriteFile wf = new WriteFile(this);
        try {
            wf.toXML();
        } catch (Exception e) {
            Dialogs.getDialogs().error(e, "Der Messdiener '" + makeId() + "' konnte nicht gespeichert werden.");
        }
    }

    public String makeId() {
        return nachname + ", " + vorname;
    }

    public boolean istLeiter() {
        return this.istLeiter;
    }

    public void setIstLeiter(boolean istLeiter) {
        this.istLeiter = istLeiter;
    }

    public String getVorname() {
        return this.vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public int getEintritt() {
        return this.eintritt;
    }

    public void setEintritt(int eintritt) {
        this.eintritt = eintritt;
    }

    public String[] getGeschwister() {
        return this.geschwister;
    }

    public void setGeschwister(String[] geschwister) {
        this.geschwister = geschwister;
    }

    public String[] getFreunde() {
        return this.freunde;
    }

    public void setFreunde(String[] freunde) {
        this.freunde = freunde;
    }

    public String getNachnname() {
        return this.nachname;
    }

    public void setNachnname(String nachnname) {
        this.nachname = nachnname;
    }

    public Messverhalten getDienverhalten() {
        return this.dienverhalten;
    }

    public void setDienverhalten(Messverhalten dienverhalten) {
        this.dienverhalten = dienverhalten;
    }

    public Messdaten getMessdatenDaten() {
        return daten;
    }

    public void setnewMessdatenDaten() {
        this.daten = new Messdaten(this);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Messdiener that = (Messdiener) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vorname, nachname, email, dienverhalten, eintritt, istLeiter, daten, file);
        result = 31 * result + Arrays.hashCode(freunde);
        result = 31 * result + Arrays.hashCode(geschwister);
        return result;
    }

    public static class NotValidException extends Exception {
        public NotValidException() {
            super("Keine gültige E-Mail Addresse");
        }
    }
}