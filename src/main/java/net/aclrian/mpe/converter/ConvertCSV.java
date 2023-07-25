package net.aclrian.mpe.converter;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.Messdiener.NotValidException;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ConvertCSV {
    private final ConvertData convertData;
    private final ArrayList<Messdiener> importedMessdiener = new ArrayList<>();

    public ConvertCSV(ConvertData convertData) {
        this.convertData = convertData;
    }

    public void start() throws IOException {
        if (convertData.file().exists()) {
            int success = 0;
            int error = 0;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(convertData.file()), convertData.charset()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Messdiener m = parseLineToMessdiener(line, convertData);
                    if (m != null) {
                        success++;
                        m.makeXML();
                        importedMessdiener.add(m);
                    } else {
                        error++;
                    }
                }
            }
            if (importedMessdiener.size() > 1) {
                replaceLineNumberWithMessdiener();
                DateienVerwalter.getInstance().reloadMessdiener();
                if (convertData.gegenseitigEintragen()) {
                    createMissingBackReferences();
                }
            }
            Dialogs.getDialogs().info("Es wurden " + success + " Messdiener erzeugt. " + error + " Zeilen wurden ignoriert.");
        }
    }

    public static Messdiener parseLineToMessdiener(String line, ConvertData convertData) { //NOPMD - suppressed CognitiveComplexity - need for switch in a loop
        String[] elemente = line.split(convertData.delimiter());
        if (elemente.length < 2) {
            return null;
        }
        if (convertData.sortierung.size() < elemente.length) {
            return null;
        }

        String vorname = "";
        String nachname = "";
        String email = "";
        int eintritt = LocalDate.now().getYear();
        Messverhalten dienverhalten = new Messverhalten();
        boolean leiter = false;
        String[] freunde = new String[Messdiener.LENGHT_FREUNDE];
        String[] geschwister = new String[Messdiener.LENGHT_GESCHWISTER];
        for (int j = 0; j < elemente.length; j++) {
            //CHECKSTYLE:OFF: InnerAssignment
            switch (convertData.sortierung().get(j)) {
                case VORNAME -> vorname = elemente[j];
                case NACHNAME -> nachname = elemente[j];
                case EINTRITT -> {
                    try {
                        eintritt = Integer.parseInt(elemente[j]);
                    } catch (NumberFormatException e) {
                        eintritt = LocalDate.now().getYear();
                    }
                }
                case LEITER -> leiter = !elemente[j].equals("");
                case NICHT_LEITER -> leiter = elemente[j].equals("");
                case NACHNAME_KOMMA_VORNAME -> {
                    String[] array2 = elemente[j].split(", ");
                    if (array2.length == 2) {
                        nachname = array2[0];
                        vorname = array2[1];
                    } else {
                        return null;
                    }
                }
                case EMAIL -> email = elemente[j];
                case VORNAME_LEERZEICHEN_NACHNAME -> {
                    String[] name = elemente[j].split(" ");
                    if (name.length == 2) {
                        vorname = name[0];
                        nachname = name[1];
                    } else if (name.length > 2) {
                        vorname = name[0];
                        nachname = elemente[j].substring(name[0].length() + 1);
                    } else {
                        return null;
                    }
                }
                case NICHT_STANDARD_MESSE -> {
                    int i = (int) convertData.sortierung()
                            .subList(0, j)
                            .stream()
                            .filter(s -> s == Sortierung.NICHT_STANDARD_MESSE || s == Sortierung.STANDARD_MESSE)
                            .count();
                    StandardMesse messe = convertData.standardMesse().get(i);
                    String s = elemente[j];
                    dienverhalten.editiereBestimmteMesse(messe, s.isEmpty());
                }
                case STANDARD_MESSE -> {
                    int k = (int) convertData.sortierung()
                            .subList(0, j)
                            .stream()
                            .filter(s -> s == Sortierung.NICHT_STANDARD_MESSE || s == Sortierung.STANDARD_MESSE)
                            .count();
                    StandardMesse messe = convertData.standardMesse().get(k);
                    String s = elemente[j];
                    dienverhalten.editiereBestimmteMesse(messe, !s.isEmpty());
                }
                case FREUNDE -> {
                    String[] split = elemente[j].split(convertData.subdelimiter());
                    freunde = new String[Messdiener.LENGHT_FREUNDE];
                    Arrays.fill(freunde, "");
                    System.arraycopy(split, 0, freunde, 0, split.length);
                    for (int i = Math.min(split.length, Messdiener.LENGHT_FREUNDE); i < Messdiener.LENGHT_FREUNDE; i++) {
                        freunde[i] = "";
                    }
                }
                case GESCHWISTER -> {
                    String[] split = elemente[j].split(convertData.subdelimiter());
                    geschwister = new String[Messdiener.LENGHT_GESCHWISTER];
                    Arrays.fill(geschwister, "");
                    System.arraycopy(split, 0, geschwister, 0, split.length);
                    for (int i = Math.min(split.length, Messdiener.LENGHT_GESCHWISTER); i < Messdiener.LENGHT_GESCHWISTER; i++) {
                        geschwister[i] = "";
                    }
                }
                case IGNORIEREN -> {
                }
                default -> {
                }
            }
            //CHECKSTYLE:ON: InnerAssignment
        }
        if (vorname.isEmpty() || nachname.isEmpty()) {
            return null;
        }
        Messdiener m = new Messdiener(new File(DateienVerwalter.getInstance().getSavePath(), nachname + ", " + vorname + ".xml"));
        m.setzeAllesNeuUndMailLeer(vorname, nachname, eintritt, leiter, dienverhalten);
        m.setFreunde(freunde);
        m.setGeschwister(geschwister);
        try {
            m.setEmail(email);
        } catch (NotValidException e) {
            MPELog.getLogger().info("{} von {} ist nicht gültig", email, m);
            m.setEmailEmpty();
        }
        return m;
    }

    private void createMissingBackReferences() {
        DateienVerwalter.getInstance().reloadMessdiener();
        for (Messdiener m : importedMessdiener) {
            addBackReferenz(m.getMessdaten().getFreunde(), m, Messdiener::getFreunde);
            addBackReferenz(m.getMessdaten().getGeschwister(), m, Messdiener::getGeschwister);
        }
    }

    private void addBackReferenz(List<Messdiener> anvertraute, Messdiener m, Function<Messdiener, String[]> getAnvertraute) {
        for (Messdiener anvertrauterMedi : anvertraute) {
            if (!anvertrauterMedi.getMessdaten().getFreunde().contains(m)) {
                String[] list = getAnvertraute.apply(anvertrauterMedi);
                for (int i = 0; i < list.length; i++) {
                    if (list[i].equals("")
                            || list[i].equals("LEER")
                            || list[i].equals("Vorname, Nachname")) {
                        list[i] = m.toString();
                        anvertrauterMedi.makeXML();
                        DateienVerwalter.getInstance().reloadMessdiener();
                        break;
                    }
                }
            }
        }
    }

    private void replaceLineNumberWithMessdiener() {
        for (Messdiener m : importedMessdiener) {
            m.setFreunde(replaceLineNumber(m, Messdiener::getFreunde));
            m.setGeschwister(replaceLineNumber(m, Messdiener::getGeschwister));
            m.makeXML();
        }

        importedMessdiener.forEach(Messdiener::setNewMessdatenDaten);
        DateienVerwalter.getInstance().getMessdiener();
    }

    private String[] replaceLineNumber(Messdiener m, Function<Messdiener, String[]> getAnvertraute) {
        int offset = convertData.lineStartetMit1 ? -1 : 0;
        final String pattern = " ?\\d{1," + (importedMessdiener.size() - 1) + "} ?";
        return Arrays.stream(getAnvertraute.apply(m))
                .map(freund -> {
                    if (freund.matches(pattern)) {
                        int index = Integer.parseInt(freund) + offset;
                        try {
                            return importedMessdiener.get(index).toString();
                        } catch (IndexOutOfBoundsException e) {
                            final String message = String.format(
                                    "%s: %s in %s could not parsed as an valid Integer %s",
                                    e.getMessage(), freund, m, e.getCause());
                            MPELog.getLogger().warn(message, e);
                        }
                    }
                    return freund;
                }).toArray(String[]::new);
    }

    public enum Sortierung {
        VORNAME("Vorname"),
        VORNAME_LEERZEICHEN_NACHNAME("Name mit der Form  \"Vorname Nachname\""),
        NACHNAME_KOMMA_VORNAME("Name mit der Form  \"Nachname, Vorname\""),
        NACHNAME("Nachname"),
        EINTRITT("Eintritt"),
        LEITER("Leiter"),
        NICHT_LEITER("Leiter (falls leer)"),
        EMAIL("E-Mail"),
        FREUNDE("Liste der Freunde"),
        GESCHWISTER("Liste der Geschwister"),
        STANDARD_MESSE("Standardmesse"),
        NICHT_STANDARD_MESSE("Standardmesse (falls leer)"),
        IGNORIEREN("Spalte ignorieren");

        private final String name;

        Sortierung(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public record ConvertData(File file, List<Sortierung> sortierung, List<StandardMesse> standardMesse,
                              String delimiter, String subdelimiter, Charset charset, boolean gegenseitigEintragen, boolean lineStartetMit1) {
    }
}
