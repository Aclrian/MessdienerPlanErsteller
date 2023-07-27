package net.aclrian.mpe.converter;

import net.aclrian.mpe.messdiener.Email;
import net.aclrian.mpe.messdiener.Messdiener;
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
        String emailString = "";
        int eintritt = LocalDate.now().getYear();
        Messverhalten dienverhalten = new Messverhalten();
        boolean leiter = false;
        String[] freunde = new String[Messdiener.LENGTH_FREUNDE];
        Arrays.fill(freunde, "");
        String[] geschwister = new String[Messdiener.LENGTH_GESCHWISTER];
        Arrays.fill(geschwister, "");
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
                case EMAIL -> emailString = elemente[j];
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
                    freunde = new String[Messdiener.LENGTH_FREUNDE];
                    Arrays.fill(freunde, "");
                    System.arraycopy(split, 0, freunde, 0, split.length);
                    for (int i = Math.min(split.length, Messdiener.LENGTH_FREUNDE); i < Messdiener.LENGTH_FREUNDE; i++) {
                        freunde[i] = "";
                    }
                }
                case GESCHWISTER -> {
                    String[] split = elemente[j].split(convertData.subdelimiter());
                    geschwister = new String[Messdiener.LENGTH_GESCHWISTER];
                    Arrays.fill(geschwister, "");
                    System.arraycopy(split, 0, geschwister, 0, split.length);
                    for (int i = Math.min(split.length, Messdiener.LENGTH_GESCHWISTER); i < Messdiener.LENGTH_GESCHWISTER; i++) {
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
        Email email = Email.EMPTY_EMAIL;
        File file = new File(DateienVerwalter.getInstance().getSavePath(), nachname + ", " + vorname + ".xml");
        Messdiener m = new Messdiener(file, vorname, nachname, email, eintritt, leiter, dienverhalten);
        m.setFreunde(freunde);
        m.setGeschwister(geschwister);
        try {
            m.setEmail(new Email(emailString));
        } catch (Email.NotValidException e) {
            MPELog.getLogger().info("{} von {} ist nicht gültig", emailString, m);
        }
        return m;
    }

    private void createMissingBackReferences() {
        for (Messdiener m : importedMessdiener) {
            addBackReferenz(m.getMessdaten().getFreunde(), m, Messdiener.AnvertrauteHandler.FREUNDE);
            addBackReferenz(m.getMessdaten().getGeschwister(), m, Messdiener.AnvertrauteHandler.GESCHWISTER);
        }
    }

    private void addBackReferenz(List<Messdiener> anvertraute, Messdiener m, Messdiener.AnvertrauteHandler handler) {
        for (Messdiener anvertrauterMedi : anvertraute) {
            if (Arrays.stream(handler.get(anvertrauterMedi)).noneMatch(id -> id.equals(m.toString()))) {
                String[] list = handler.get(anvertrauterMedi);
                for (int i = 0; i < list.length; i++) {
                    if (list[i].equals("")
                            || list[i].equals("LEER")
                            || list[i].equals("Vorname, Nachname")) {
                        list[i] = m.toString();
                        anvertrauterMedi.makeXML();
                        break;
                    }
                }
            }
        }
    }

    private void replaceLineNumberWithMessdiener() {
        for (Messdiener m : importedMessdiener) {
            m.setFreunde(replaceLineNumber(m, Messdiener.AnvertrauteHandler.FREUNDE));
            m.setGeschwister(replaceLineNumber(m, Messdiener.AnvertrauteHandler.GESCHWISTER));
            m.makeXML();
        }

        importedMessdiener.forEach(Messdiener::setNewMessdatenDaten);
        DateienVerwalter.getInstance().getMessdiener();
    }

    private String[] replaceLineNumber(Messdiener m, Messdiener.AnvertrauteHandler handler) {
        int offset = convertData.lineStartetMit1 ? -1 : 0;
        final String pattern = " ?\\d{1," + (importedMessdiener.size() - 1) + "} ?";
        return Arrays.stream(handler.get(m))
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
