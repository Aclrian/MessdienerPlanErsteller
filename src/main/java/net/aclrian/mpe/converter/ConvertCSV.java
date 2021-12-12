package net.aclrian.mpe.converter;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.Messdiener.NotValidException;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;

public class ConvertCSV {

    /**
     * 0: Vorname 1: Nachname 2:Eintritt 3:ist Leiter?
     * (leer ist ja sonst nein)| 4: Leiter Gegenteil von 3
     * 6: Nachname, Vorname 7: E-Mail
     */
    public ConvertCSV(File f, List<Integer> sortierung) throws IOException {
        Log.getLogger().warn("Das Unter-Programm unterstützt die Vorlieben von Messdienern nicht!\n-Also wann sie dienen können");
        if (f.exists() && f.getName().endsWith(".csv")) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    pareLineToMessdiener(sortierung, line);
                }
            }
        }
    }

    private void pareLineToMessdiener(List<Integer> sortierung, String line) {
        String[] elemente = line.split(";");
        String vorname = "Vorname";
        String nachname = "Nachname";
        String email = "";
        int eintritt = Calendar.getInstance().get(Calendar.YEAR);
        boolean leiter = false;
        for (int j = 0; j < elemente.length; j++) {
            if (elemente.length < 2) {
                break;
            }
            switch (sortierung.get(j)) {
                case 0:
                    vorname = elemente[j];
                    break;
                case 1:
                    nachname = elemente[j];
                    break;
                case 2:
                    eintritt = Integer.parseInt(elemente[j]);
                    break;
                case 3:
                    leiter = elemente[j].equals("");
                    break;
                case 4:
                    leiter = !elemente[j].equals("");
                    break;
                case 6:
                    String[] array2 = elemente[j].split(", ");
                    if (array2.length != 2) {
                        vorname = elemente[j];
                        nachname = elemente[j];
                    } else {
                        nachname = array2[0];
                        vorname = array2[1];
                    }
                    break;
                case 7:
                    email = elemente[j];
                    break;
                default:
                    break;
            }
        }
        Messdiener m = new Messdiener(new File(DateienVerwalter.getInstance().getSavepath(),  nachname + ", " + vorname + ".xml"));
        m.setzeAllesNeuUndMailLeer(vorname, nachname, eintritt, leiter, new Messverhalten());
        try {
            m.setEmail(email);
        } catch (NotValidException e) {
            Log.getLogger().info("{} von {} ist nicht gültig", email, m);
            m.setEmailEmpty();
        }
        m.makeXML();
    }
}
