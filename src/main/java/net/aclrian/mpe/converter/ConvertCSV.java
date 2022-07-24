package net.aclrian.mpe.converter;

import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messdiener.Messdiener.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.utils.*;

import java.io.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;

public class ConvertCSV {
    private final ConvertData convertData;
    private final ArrayList<Messdiener> importedMessdiener = new ArrayList<>();

    public ConvertCSV(ConvertData convertData) {
        this.convertData = convertData;
    }

    public void start() throws IOException {
        if (convertData.file().exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(convertData.file()), convertData.charset()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    parseLineToMessdiener(line, convertData);
                }
            }
            replaceLineNumberWithMessdiener();
            DateienVerwalter.getInstance().reloadMessdiener();
            replaceNameWithID();
            List<Messdiener> allMessdiener = DateienVerwalter.getInstance().getMessdiener();
            if (convertData.gegenseitigEintragen()) {
                createMissingBackReferences(allMessdiener);
            }
        }
    }

    private void parseLineToMessdiener(String line, ConvertData convertData) {
        String[] elemente = line.split(convertData.delimiter());
        String vorname = "Vorname";
        String nachname = "Nachname";
        String email = "";
        int eintritt = LocalDate.now().getYear();
        Messverhalten dienverhalten = new Messverhalten();
        boolean leiter = false;
        String[] freunde = new String[Messdiener.LENGHT_FREUNDE];
        String[] geschwister = new String[Messdiener.LENGHT_GESCHWISTER];
        Arrays.fill(freunde, "");
        Arrays.fill(geschwister, "");

        for (int j = 0; j < elemente.length; j++) {
            if (elemente.length < 2) {
                break;
            }
            switch (convertData.sortierung().get(j)) {
                case VORNAME -> vorname = elemente[j];
                case NACHNAME -> nachname = elemente[j];
                case EINTRITT -> eintritt = Integer.parseInt(elemente[j]);
                case LEITER -> leiter = !elemente[j].equals("");
                case NICHT_LEITER -> leiter = elemente[j].equals("");
                case NACHNAME_KOMMA_VORNAME -> {
                    String[] array2 = elemente[j].split(", ");
                    if (array2.length != 2) {
                        vorname = elemente[j];
                        nachname = elemente[j];
                    } else {
                        nachname = array2[0];
                        vorname = array2[1];
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
                        Log.getLogger().warn("Import: Line {}: Name konnte nicht geparst werden: {}", (j + 1), Sortierung.VORNAME_LEERZEICHEN_NACHNAME);
                    }
                }
                case NICHT_STANDARD_MESSE -> {
                    int i = (int) convertData.sortierung().subList(0, j).stream().filter(s -> s == Sortierung.NICHT_STANDARD_MESSE || s == Sortierung.STANDARD_MESSE).count();
                    dienverhalten.editiereBestimmteMesse(convertData.standardMesse().get(i), elemente[j].isEmpty());
                }
                case STANDARD_MESSE -> {
                    int k = (int) convertData.sortierung().subList(0, j).stream().filter(s -> s == Sortierung.NICHT_STANDARD_MESSE || s == Sortierung.STANDARD_MESSE).count();
                    dienverhalten.editiereBestimmteMesse(convertData.standardMesse().get(k), !elemente[j].isEmpty());
                }
                case FREUNDE -> {
                    String[] split = elemente[j].split(convertData.subdelimiter(), freunde.length);
                    if (split.length < freunde.length) {
                        freunde = new String[Messdiener.LENGHT_FREUNDE];
                        System.arraycopy(split, 0, freunde, 0, split.length);
                    } else {
                        freunde = split;
                    }
                }
                case GESCHWISTER -> {
                    String[] splitter = elemente[j].split(convertData.subdelimiter(), freunde.length);
                    if (splitter.length > freunde.length) {
                        geschwister = Arrays.copyOfRange(splitter, 0, freunde.length);
                    } else if (splitter.length < freunde.length) {
                        geschwister = Arrays.copyOfRange(splitter, 0, splitter.length);
                    } else {
                        geschwister = splitter;
                    }
                }
                default -> {
                    //Ignored
                }
            }
        }
        Messdiener m = new Messdiener(new File(DateienVerwalter.getInstance().getSavePath(), nachname + ", " + vorname + ".xml"));
        m.setzeAllesNeuUndMailLeer(vorname, nachname, eintritt, leiter, dienverhalten);
        m.setFreunde(freunde);
        m.setGeschwister(geschwister);
        try {
            m.setEmail(email);
        } catch (NotValidException e) {
            Log.getLogger().info("{} von {} ist nicht gültig", email, m);
            m.setEmailEmpty();
        }
        m.makeXML();
        importedMessdiener.add(m);
    }

    private void createMissingBackReferences(List<Messdiener> allMessdiener) {
        DateienVerwalter.getInstance().reloadMessdiener();
        for (Messdiener m : importedMessdiener) {
            List<Messdiener> anvertraute = m.getMessdaten().getAnvertraute(allMessdiener);
            for (Messdiener anvertraut : anvertraute) {
                if (!anvertraut.getMessdaten().getAnvertraute(allMessdiener).contains(m)) {
                    if (m.getMessdaten().getFreunde().contains(anvertraut)) {
                        for (int i = 0; i < Messdiener.LENGHT_GESCHWISTER; i++) {
                            if (m.getFreunde()[i].equals("") || m.getFreunde()[i].equals("LEER") || m.getFreunde()[i].equals("Vorname, Nachname")) {
                                m.getFreunde()[i] = anvertraut.toString();
                                DateienVerwalter.getInstance().reloadMessdiener();
                                m.makeXML();
                            }
                        }
                    } else {
                        for (int i = 0; i < Messdiener.LENGHT_GESCHWISTER; i++) {
                            if (m.getGeschwister()[i].equals("") || m.getGeschwister()[i].equals("LEER") || m.getGeschwister()[i].equals("Vorname, Nachname")) {
                                m.getGeschwister()[i] = anvertraut.toString();
                                DateienVerwalter.getInstance().reloadMessdiener();
                                m.makeXML();
                            }
                        }
                    }
                }
            }
        }
        DateienVerwalter.getInstance().getMessdiener();
    }

    private void replaceNameWithID() {
        for (Messdiener m : importedMessdiener) {
            try {
                m.setNewMessdatenDaten();
            } catch (Messdaten.CouldFindMedi e) {
                for (int i = 0; i < Messdiener.LENGHT_FREUNDE; i++) {
                    if (m.getFreunde()[i].equalsIgnoreCase(e.getString())) {
                        m.getFreunde()[i] = e.getMessdienerID();
                    }
                }
                for (int i = 0; i < Messdiener.LENGHT_GESCHWISTER; i++) {
                    if (m.getGeschwister()[i].equalsIgnoreCase(e.getString())) {
                        m.getGeschwister()[i] = e.getMessdienerID();
                    }
                }
            }
            m.makeXML();
        }
    }

    private void replaceLineNumberWithMessdiener() {
        final String pattern = " ?\\d{1," + (Math.log10(importedMessdiener.size()) + 1) + "} ?";
        for (Messdiener m : importedMessdiener) {
            for (int i = 0; i < m.getFreunde().length; i++) {
                if (m.getFreunde()[i].matches(pattern)) {
                    try {
                        m.getFreunde()[i] = importedMessdiener.get(Integer.parseInt(m.getFreunde()[i])).toString();
                        DateienVerwalter.getInstance().reloadMessdiener();
                    } catch (IndexOutOfBoundsException e) {
                        final String message = String.format("%s: %s in %s could not parsed as an valid Integer %s", e.getMessage(), m.getFreunde()[i], m, e.getCause());
                        Log.getLogger().warn(message, e);
                    }
                }
            }
            for (int i = 0; i < m.getGeschwister().length; i++) {
                if (m.getGeschwister()[i].matches(pattern)) {
                    try {
                        m.getGeschwister()[i] = importedMessdiener.get(Integer.parseInt(m.getGeschwister()[i])).toString();
                        DateienVerwalter.getInstance().reloadMessdiener();
                    } catch (IndexOutOfBoundsException e) {
                        final String message = String.format("%s: %s in %s could not parsed as an valid Integer %s", e.getMessage(), m.getFreunde()[i], m, e.getCause());
                        Log.getLogger().warn(message, e);
                    }
                }
            }
        }
        DateienVerwalter.getInstance().getMessdiener();
    }

    public enum Sortierung {
        VORNAME("Vorname"),
        VORNAME_LEERZEICHEN_NACHNAME("Name mit der Form  \"Vorname Nachname\""),
        NACHNAME_KOMMA_VORNAME("Name mit der Form  \"Nachname, Vorname\""),
        NACHNAME("Nachname"),
        EINTRITT("Eintritt"),
        LEITER("Leiter"),
        NICHT_LEITER("Leiter (umgekehrt)"),
        EMAIL("E-Mail"),
        FREUNDE("Liste der Freunde"),
        GESCHWISTER("Liste der Geschwister"),
        STANDARD_MESSE("Standardmesse"),
        NICHT_STANDARD_MESSE("Standardmesse (umgekehrt)"),
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
                              String delimiter, String subdelimiter, Charset charset, boolean gegenseitigEintragen) {
    }
}