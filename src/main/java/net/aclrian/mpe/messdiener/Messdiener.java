package net.aclrian.mpe.messdiener;

import javafx.application.Platform;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Klasse, mit der Messdiener digital gespeichert werden können
 *
 * @author Aclrian
 */
public class Messdiener extends Person {
    /**
     * Dies ist die Anzahl, wie viele Freunde ein Messdiener haben kann.
     */
    public static final int LENGTH_GESCHWISTER = 3;
    /**
     * Dies ist die Anzahl, wie viele Freunde ein Messdiener haben kann.
     */
    public static final int LENGTH_FREUNDE = 5;
    private String[] freunde;
    private String[] geschwister;
    private final Messverhalten dienverhalten;
    private final int eintritt;
    private final boolean leiter;
    private Messdaten daten;
    private File file;

    public Messdiener(File f, String vorname, String nachname, Email email, int eintritt, boolean istLeiter,
                      Messverhalten dienverhalten) {
        super(vorname, nachname, email);
        this.file = f;
        this.eintritt = eintritt;
        this.leiter = istLeiter;
        this.dienverhalten = dienverhalten;
        freunde = new String[LENGTH_FREUNDE];
        Arrays.fill(freunde, "");
        geschwister = new String[LENGTH_GESCHWISTER];
        Arrays.fill(geschwister, "");
    }

    /**
     * Mit dieser Methode wird der Messdiener als .xml Datei lokal gespeichert
     */
    public void makeXML() {
        WriteFile wf = new WriteFile(this);
        try {
            file = wf.saveToXML();
            DateienVerwalter.getInstance().reloadMessdiener();
        } catch (Exception e) {
            Platform.runLater(() -> Dialogs.getDialogs().error(e, "Der Messdiener '" + this + "' konnte nicht gespeichert werden."));
        }
    }

    public boolean istLeiter() {
        return this.leiter;
    }

    public int getEintritt() {
        return this.eintritt;
    }

    public String[] getGeschwister() {
        return this.geschwister;
    }

    public void setGeschwister(String[] geschwister) {
        this.geschwister = Arrays.stream(geschwister).limit(LENGTH_GESCHWISTER).toArray(String[]::new);
    }

    public String[] getFreunde() {
        return this.freunde;
    }

    public void setFreunde(String[] freunde) {
        this.freunde = Arrays.stream(freunde).limit(LENGTH_FREUNDE).toArray(String[]::new);
    }

    public Messverhalten getDienverhalten() {
        return this.dienverhalten;
    }

    public Messdaten getMessdaten() {
        return daten;
    }

    public void setNewMessdatenDaten() {
        this.daten = new Messdaten(this);
    }

    public void addFreund(Messdiener freund) {
        for (int i = 0; i < freunde.length; i++) {
            if (freunde[i].isEmpty() || freunde[i].equals("LEER") || freunde[i].equals("Vorname, Nachname")) {
                freunde[i] = freund.toString();
                break;
            }
        }
    }

    public void addGeschwister(Messdiener geschwister) {
        for (int i = 0; i < this.geschwister.length; i++) {
            if (this.geschwister[i].isEmpty() || this.geschwister[i].equals("LEER") || this.geschwister[i].equals("Vorname, Nachname")) {
                this.geschwister[i] = geschwister.toString();
                break;
            }
        }
    }

    public static Messdiener alteLoeschen(Messdiener toDel, Messdiener toSearchIn) {
        if (toDel.equals(toSearchIn)) {
            return toDel;
        }
        Messdiener medi = null;
        for (int i = 0; i < toSearchIn.getGeschwister().length; i++) {
            if (toSearchIn.getGeschwister()[i].compareTo(toDel.toString()) == 0) {
                toSearchIn.getGeschwister()[i] = "";
                medi = toSearchIn;
            }
        }
        for (int i = 0; i < toSearchIn.getFreunde().length; i++) {
            if (toSearchIn.getFreunde()[i].compareTo(toDel.toString()) == 0) {
                toSearchIn.getFreunde()[i] = "";
                medi = toSearchIn;
            }
        }
        return medi;
    }
    public void setFreundeFromList(List<Messdiener> freunde) {
        this.freunde = getArrayString(freunde, LENGTH_FREUNDE);
    }

    public void setGeschwisterFromList(List<Messdiener> geschwister) {
        this.geschwister = getArrayString(geschwister, LENGTH_GESCHWISTER);
    }

    private static String[] getArrayString(List<?> list, int lenght) {
        String[] s = new String[lenght];
        for (int i = 0; i < s.length; i++) {
            try {
                s[i] = list.get(i).toString();
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                s[i] = "";
            }
        }
        return s;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Messdiener that = (Messdiener) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getVorname(), getNachname(), getEmail(), dienverhalten, eintritt, leiter, daten, file);
        result = 31 * result + Arrays.hashCode(freunde);
        result = 31 * result + Arrays.hashCode(geschwister);
        return result;
    }

    public static class AnvertrauteHandler {
        public static final AnvertrauteHandler FREUNDE = new AnvertrauteHandler("Freund", Messdiener::getFreunde, Messdiener::setFreunde);
        public static final AnvertrauteHandler GESCHWISTER = new AnvertrauteHandler("Geschwister", Messdiener::getGeschwister, Messdiener::setGeschwister);

        private final String name;
        private final Function<Messdiener, String[]> get;
        private final BiConsumer<Messdiener, String[]> set;

        private AnvertrauteHandler(String name, Function<Messdiener, String[]> get, BiConsumer<Messdiener, String[]> set) {
            this.name = name;
            this.get = get;
            this.set = set;
        }

        public String[] get(Messdiener messdiener) {
            return get.apply(messdiener);
        }

        public void set(Messdiener messdiener, String[] array) {
            set.accept(messdiener, array);
        }

        public void set(Messdiener messdiener, List<?> arrayList) {
            set.accept(messdiener, getArrayString(arrayList, get.apply(messdiener).length));
        }

        public String getName() {
            return name;
        }
    }
}
