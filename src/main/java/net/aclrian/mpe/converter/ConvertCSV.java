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
            DateienVerwalter.getInstance().reloadMessdiener();
            // Round 1: replace ID
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

            // Round 2: add missing back references
            List<Messdiener> allMessdiener = DateienVerwalter.getInstance().getMessdiener();
            if (convertData.gegenseitgEintragen()) {
                DateienVerwalter.getInstance().reloadMessdiener();
                //TODO
            }
        }
    }

    //TODO make sure that dv has the right pfarrei selected so that Dienverhalten has the correct StandardMesse
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
                case VORNAME:
                    vorname = elemente[j];
                    break;
                case NACHNAME:
                    nachname = elemente[j];
                    break;
                case EINTRITT:
                    eintritt = Integer.parseInt(elemente[j]);
                    break;
                case LEITER:
                    leiter = !elemente[j].equals("");
                    break;
                case NICHT_LEITER:
                    leiter = elemente[j].equals("");
                    break;
                case NACHNAME_KOMMA_VORNAME:
                    String[] array2 = elemente[j].split(", ");
                    if (array2.length != 2) {
                        vorname = elemente[j];
                        nachname = elemente[j];
                    } else {
                        nachname = array2[0];
                        vorname = array2[1];
                    }
                    break;
                case EMAIL:
                    email = elemente[j];
                    break;
                case VORNAME_LEERZEICHEN_NACHNAME:
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
                    break;
                case NICHT_STANDARD_MESSE:
                    int i = (int) convertData.sortierung().subList(0, j).stream().filter(s -> s == Sortierung.NICHT_STANDARD_MESSE || s == Sortierung.STANDARD_MESSE).count();
                    dienverhalten.editiereBestimmteMesse(convertData.standardMesse().get(i), elemente[j].isEmpty());
                    break;
                case STANDARD_MESSE:
                    int k = (int) convertData.sortierung().subList(0, j).stream().filter(s -> s == Sortierung.NICHT_STANDARD_MESSE || s == Sortierung.STANDARD_MESSE).count();
                    dienverhalten.editiereBestimmteMesse(convertData.standardMesse().get(k), !elemente[j].isEmpty());
                    break;
                case FREUNDE:
                    String[] split = elemente[j].split(convertData.subdelimiter(), freunde.length);
                    if (split.length < freunde.length) {
                        freunde = new String[Messdiener.LENGHT_FREUNDE];
                        System.arraycopy(split, 0, freunde, 0, split.length);
                    } else {
                        freunde = split;
                    }
                    break;
                case GESCHWISTER:
                    String[] splitter = elemente[j].split(convertData.subdelimiter(), freunde.length);
                    if (splitter.length > freunde.length) {
                        geschwister = Arrays.copyOfRange(splitter, 0, freunde.length);
                    } else if (splitter.length < freunde.length) {
                        geschwister = Arrays.copyOfRange(splitter, 0, splitter.length);
                    } else {
                        geschwister = splitter;
                    }
                    break;
                case IGNORIEREN:
                default:
                    break;
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

    public enum Sortierung {
        VORNAME("Vorname"),
        NACHNAME("Nachname"),
        EINTRITT("Eintritt"),
        LEITER("Leiter"),
        NICHT_LEITER("Leiter (umgekehrt)"),
        NACHNAME_KOMMA_VORNAME("Name mit der Form  \"Nachname, Vorname\""),
        VORNAME_LEERZEICHEN_NACHNAME("Name mit der Form  \"Vorname Nachname\""),
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
                              String delimiter, String subdelimiter, Charset charset, boolean gegenseitgEintragen) {
    }
}