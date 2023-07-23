package net.aclrian.mpe.pfarrei;


import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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

            appendSettingsNodes(pf, doc, xml);

            DOMSource domSource = new DOMSource(doc);
            String datei = pf.getName();
            datei = datei.replace(" ", "_");
            savepath = savepath == null ? DateienVerwalter.getInstance().getSavePath().getAbsolutePath() : savepath;
            File f = new File(savepath, datei + DateienVerwalter.PFARREI_DATEIENDUNG);
            MPELog.getLogger()
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

    private static void appendSettingsNodes(Pfarrei pf, Document doc, Element xml) {
        Element einst = doc.createElement("Einstellungen");
        xml.appendChild(einst);

        Element hochamt = doc.createElement("hochaemter");
        int hochamtInt = 0;
        if (pf.zaehlenHochaemterMit()) {
            hochamtInt++;
        }
        hochamt.appendChild(doc.createTextNode(String.valueOf(hochamtInt)));
        einst.appendChild(hochamt);

        for (int i = 0; i < Einstellungen.LENGTH; i++) {
            Setting s = pf.getSettings().getDaten(i);
            Setting.Attribut a = s.attribut();
            int anz = s.anzahlDienen();
            String val = String.valueOf(anz);
            int id = s.id();
            String idS = String.valueOf(id);
            if (a == Setting.Attribut.YEAR) {
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
    }

    public static void writeFile(Pfarrei pf) {
        writeFile(pf, DateienVerwalter.getInstance().getSavePath().getAbsolutePath());
    }
}