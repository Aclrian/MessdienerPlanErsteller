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
    public enum Sortierung {
        VORNAME(0),
        NACHNAME(1),
        EINTRITT(2),
        LEITER(3),
        NICHT_LEITER(4),
        NACHNAME_KOMMA_VORNAME(5),
        VORNAME_LEERZEICHEN_NACHNAME(6),
        EMAIL(7),
        FREUNDE(8),
        GESCHWISTER(9),
        STANDARD_MESSE(10),
        NICHT_STANDARD_MESSE(11),
        IGNORIEREN(12);

        private final int number;

        Sortierung(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
    }

    public ConvertCSV(ConvertData convertData) throws IOException {
        Log.getLogger().warn("Das Unter-Programm unterstützt die Vorlieben von Messdienern nicht!\n-Also wann sie dienen können");
        if (convertData.file().exists() && convertData.file().getName().endsWith(".csv")) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(convertData.file()), convertData.charset()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    parseLineToMessdiener(line, convertData);
                }
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
                    leiter = elemente[j].equals("");
                    break;
                case NICHT_LEITER:
                    leiter = !elemente[j].equals("");
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
                    if(name.length == 2){
                        vorname = name[0];
                        nachname = name[1];
                    } else if( name.length > 2){
                        vorname = name[0];
                        nachname = elemente[j].substring(name[0].length()+1);
                    } else{
                        Log.getLogger().warn("Import: Line {}: Name konnte nicht geparst werden: {}", (j+1), Sortierung.VORNAME_LEERZEICHEN_NACHNAME);
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
                    if(split.length>freunde.length){
                        freunde = Arrays.copyOfRange(split,0, freunde.length);
                    } else if(split.length < freunde.length){
                        freunde = Arrays.copyOfRange(split, 0, split.length);
                    } else {
                        freunde = split;
                    }
                    break;
                case GESCHWISTER:
                    String[] splitter = elemente[j].split(convertData.subdelimiter(), freunde.length);
                    if(splitter.length>freunde.length){
                        geschwister = Arrays.copyOfRange(splitter,0, freunde.length);
                    } else if(splitter.length < freunde.length){
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
    }

    public record ConvertData(File file, List<Sortierung> sortierung, List<StandardMesse> standardMesse,
                              String delimiter, String subdelimiter, Charset charset) {
    }
}