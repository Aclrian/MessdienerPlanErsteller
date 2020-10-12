package net.aclrian.mpe.pfarrei;

import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Setting.Attribut;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class WriteFilePfarrei {

    private WriteFilePfarrei() {
    }

    public static void writeFile(Pfarrei pf, String savepath) {
        try {
            List<StandartMesse> wsm = pf.getStandardMessen();

            WriteFile.Container c = WriteFile.writeXMLFile();
            Document doc = c.getDocument();
            Element xml = c.getElement();

            Element body = doc.createElement("Body");
            xml.appendChild(body);

            Element standartmessen = doc.createElement("Standartmessem");
            body.appendChild(standartmessen);
            for (int i = 0; i < wsm.size(); i++) {
                StandartMesse m = wsm.get(i);
                if (m instanceof Sonstiges) {
                    continue;
                }

                Element stdm = doc.createElement("std_messe");
                stdm.setAttribute("id", String.valueOf(i));

                Element tag = doc.createElement("tag");
                tag.appendChild(doc.createTextNode(m.getWochentag()));
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

                standartmessen.appendChild(stdm);
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
            for (int i = 0; i < Einstellungen.LENGHT; i++) {
                Setting s = pf.getSettings().getDaten(i);
                Attribut a = s.getAttribut();
                int anz = s.getAnzDienen();
                String val = String.valueOf(anz);
                int id = s.getId();
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
            savepath = savepath == null ? DateienVerwalter.getInstance().getSavepath().getAbsolutePath() : savepath;
            Log.getLogger()
                    .info("Pfarrei wird gespeichert in:" + savepath + File.separator + datei + DateienVerwalter.PFARREDATEIENDUNG);
            File f = new File(savepath, datei + DateienVerwalter.PFARREDATEIENDUNG);
            StreamResult streamResult = new StreamResult(f);
            c.getTransformer().transform(domSource, streamResult);
            DateienVerwalter.getInstance().removeoldPfarrei(f);
        } catch (ParserConfigurationException | TransformerException exception) {
            Dialogs.getDialogs().error(exception, "Fehler bei Speichern der Pfarrei:");
        }
    }

    public static void writeFile(Pfarrei pf) {
        writeFile(pf, DateienVerwalter.getInstance().getSavepath().getAbsolutePath());
    }
}