package net.aclrian.mpe.pfarrei;

import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.pfarrei.Setting.*;
import net.aclrian.mpe.utils.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.time.format.*;
import java.util.*;

public class WriteFilePfarrei {

    private WriteFilePfarrei() {
    }

    public static void writeFile(Pfarrei pf, String savepath) {
        try {
            List<StandardMesse> wsm = pf.getStandardMessen();

            WriteFile.Container c = WriteFile.writeXMLFile();
            Document doc = c.document();
            Element xml = c.element();

            Element body = doc.createElement("Body");
            xml.appendChild(body);

            Element standardmessen = doc.createElement("Standardmessen");
            body.appendChild(standardmessen);
            for (int i = 0; i < wsm.size(); i++) {
                StandardMesse m = wsm.get(i);
                if (m instanceof Sonstiges) {
                    continue;
                }

                Element stdm = doc.createElement("std_messe");
                stdm.setAttribute("id", String.valueOf(i));

                Element tag = doc.createElement("tag");
                tag.appendChild(doc.createTextNode(m.getWochentag().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())));
                stdm.appendChild(tag);

                Element std = doc.createElement("std");
                std.appendChild(doc.createTextNode(String.valueOf(m.getBeginnStunde())));
                stdm.appendChild(std);

                Element min = doc.createElement("min");
                min.appendChild(doc.createTextNode(m.getBeginnMinuteString()));
                stdm.appendChild(min);

                Element ort = doc.createElement("ort");
                ort.appendChild(doc.createTextNode(m.getOrt()));
                stdm.appendChild(ort);

                Element anz = doc.createElement("anz");
                anz.appendChild(doc.createTextNode(String.valueOf(m.getAnzMessdiener())));
                stdm.appendChild(anz);

                Element typ = doc.createElement("typ");
                typ.appendChild(doc.createTextNode(m.getTyp()));
                stdm.appendChild(typ);

                standardmessen.appendChild(stdm);
            }

            // Einstellungen
            Element einst = doc.createElement("Einstellungen");
            xml.appendChild(einst);

            Element hochamt = doc.createElement("hochaemter");
            int hochamtInt = 0;
            if (pf.zaehlenHochaemterMit()) {
                hochamtInt++;
            }
            hochamt.appendChild(doc.createTextNode(String.valueOf(hochamtInt)));
            einst.appendChild(hochamt);

            // Settings
            for (int i = 0; i < Einstellungen.LENGTH; i++) {
                Setting s = pf.getSettings().getDaten(i);
                Attribut a = s.attribut();
                int anz = s.anzahlDienen();
                String val = String.valueOf(anz);
                int id = s.id();
                String idS = String.valueOf(id);
                if (a == Attribut.YEAR) {
                    Element year = doc.createElement("setting");
                    year.setAttribute("year", idS);
                    year.appendChild(doc.createTextNode(val));
                    einst.appendChild(year);
                } else {
                    Element max = doc.createElement("setting");
                    max.setAttribute("Lleiter", idS);
                    max.appendChild(doc.createTextNode(val));
                    einst.appendChild(max);
                }
            }

            DOMSource domSource = new DOMSource(doc);
            String datei = pf.getName();
            datei = datei.replace(" ", "_");
            savepath = savepath == null ? DateienVerwalter.getInstance().getSavePath().getAbsolutePath() : savepath;
            File f = new File(savepath, datei + DateienVerwalter.PFARREI_DATEIENDUNG);
            Log.getLogger()
                    .info("Pfarrei wird gespeichert in: {}", f);
            StreamResult streamResult = new StreamResult(f);
            c.transformer().transform(domSource, streamResult);
            boolean notYetStarted = DateienVerwalter.getInstance() != null;
            if (notYetStarted) {
                DateienVerwalter.getInstance().removeOldPfarrei(f);
            }
        } catch (ParserConfigurationException | TransformerException exception) {
            Dialogs.getDialogs().error(exception, "Fehler bei Speichern der Pfarrei:");
        }
    }

    public static void writeFile(Pfarrei pf) {
        writeFile(pf, DateienVerwalter.getInstance().getSavePath().getAbsolutePath());
    }
}