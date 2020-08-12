package net.aclrian.mpe.messdiener;

import net.aclrian.mpe.messdiener.Messdiener.NotValidException;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * liesst aus einer xml-Datei den Messdiener und gibt diesen zurueck
 *
 * @author Aclrian
 */
public class ReadFile {
    /**
     * @param path die Datei
     * @return den ausgelesenen Messdiener
     */
    public Messdiener getMessdiener(String path) {
        Messdiener me = null;
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(new InputSource(String.valueOf(fXmlFile.toURI())));
            if (doc != null && fXmlFile.getAbsolutePath().endsWith(DateienVerwalter.MESSDIENERDATEIENDUNG)) {
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("XML");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == 1) {
                        Element eElement = (Element) nNode;
                        String mail;
                        String vname = eElement.getElementsByTagName("Vorname").item(0).getTextContent();
                        String nname = eElement.getElementsByTagName("Nachname").item(0).getTextContent();
                        mail = readMail(eElement);
                        int eintritt = readEintritt(eElement);
                        boolean istLeiter = Boolean.parseBoolean(eElement.getElementsByTagName("Leiter").item(0).getTextContent());
                        // Messverhalten
                        Messverhalten dienverhalten = readMessverhalten(eElement);
                        String[] freunde = new String[5];
                        String[] geschwister = new String[3];
                        readFreunde(fXmlFile, eElement, freunde);
                        readGeschwister(fXmlFile, eElement, geschwister);

                        me = toMessdiener(fXmlFile, mail, vname, nname, eintritt, istLeiter, dienverhalten);
                        me.setFreunde(freunde);
                        me.setGeschwister(geschwister);
                    }
                }
            }
        } catch (Exception e) {
            Dialogs.error(e, "Fehler beim Lesen der Datei: " + path);
            e.printStackTrace();
        }
        return me;
    }

    private Messdiener toMessdiener(File fXmlFile, String mail, String vname, String nname, int eintritt, boolean istLeiter, Messverhalten dienverhalten) {
        Messdiener me;
        me = new Messdiener(fXmlFile);
        try {
            me.setzeAllesNeu(vname, nname, eintritt, istLeiter, dienverhalten, mail);
        } catch (Exception e) {
            fixEmail(me, mail, vname, nname, eintritt, istLeiter, dienverhalten);
        }
        return me;
    }

    private String readMail(Element eElement) {
        String mail;
        try {
            mail = eElement.getElementsByTagName("Email").item(0).getTextContent();
        } catch (NullPointerException e) {
            mail = "";
        }
        return mail;
    }

    private int readEintritt(Element eElement) {
        int eintritt = Integer
                .parseInt(eElement.getElementsByTagName("Eintritt").item(0).getTextContent());
        if (eintritt < Messdaten.getMinYear()) {
            eintritt = Messdaten.getMinYear();
        }
        if (eintritt > Messdaten.getMaxYear()) {
            eintritt = Messdaten.getMaxYear();
        }
        return eintritt;
    }

    private Messverhalten readMessverhalten(Element eElement) {
        Messverhalten dienverhalten = new Messverhalten();
        for (StandartMesse sm : DateienVerwalter.getDateienVerwalter().getPfarrei().getStandardMessen()) {
            if (sm instanceof Sonstiges) {
                continue;
            }
            String sname = sm.toReduziertenString();
            boolean kann = false;
            try {
                String value = eElement.getElementsByTagName(sname).item(0).getTextContent();
                kann = value.equals("true");
            } catch (Exception e) {
                //Node not found
            }
            dienverhalten.editiereBestimmteMesse(sm, kann);
        }
        return dienverhalten;
    }

    private void readGeschwister(File fXmlFile, Element eElement, String[] geschwister) {
        String g1 = "LEER";
        String g2 = "LEER";
        String g3 = "LEER";
        try {
            g1 = eElement.getElementsByTagName("g1").item(0).getTextContent();
            g2 = eElement.getElementsByTagName("g2").item(0).getTextContent();
            g3 = eElement.getElementsByTagName("g3").item(0).getTextContent();
        } catch (NullPointerException e) {
            Log.getLogger().info("Es wurde eine Alte Version von Messdiener-Dateien gefunden!"
                    + fXmlFile.getName());
        }
        if (!g1.equals("LEER")) {
            geschwister[0] = g1;
        } else {
            geschwister[0] = "";
        }
        if (!g2.equals("LEER")) {
            geschwister[1] = g2;
        } else {
            geschwister[1] = "";
        }
        if (!g3.equals("LEER")) {
            geschwister[2] = g3;
        } else {
            geschwister[2] = "";
        }
    }

    private void readFreunde(File fXmlFile, Element eElement, String[] freunde) {
        String f1 = "LEER";
        String f2 = "LEER";
        String f3 = "LEER";
        String f4 = "LEER";
        String f5 = "LEER";
        try {
            f1 = eElement.getElementsByTagName("F1").item(0).getTextContent();
            f2 = eElement.getElementsByTagName("F2").item(0).getTextContent();
            f3 = eElement.getElementsByTagName("F3").item(0).getTextContent();
            f4 = eElement.getElementsByTagName("F4").item(0).getTextContent();
            f5 = eElement.getElementsByTagName("F5").item(0).getTextContent();
        } catch (NullPointerException e) {
            Log.getLogger().info("Es wurde eine Alte Version von Messdiener-Dateien gefunden!"
                    + fXmlFile.getName());
        }
        if (!f1.equals("LEER")) {
            freunde[0] = f1;
        } else {
            freunde[0] = "";
        }
        if (!f2.equals("LEER")) {
            freunde[1] = f2;
        } else {
            freunde[1] = "";
        }
        if (!f3.equals("LEER")) {
            freunde[2] = f3;
        } else {
            freunde[2] = "";
        }
        if (!f4.equals("LEER")) {
            freunde[3] = f4;
        } else {
            freunde[3] = "";
        }
        if (!f5.equals("LEER")) {
            freunde[4] = f5;
        } else {
            freunde[4] = "";
        }
    }

    private void fixEmail(Messdiener me, String mail, String vname, String nname, int eintritt, boolean istLeiter, Messverhalten dienverhalten) {
        if (Dialogs.frage("Die E-Mail-Addresse '" + mail + "' von " + me.makeId() + " ist ungültig und wird gelöscht.\nSoll eine neue eingegeben werden?")) {
            String s = Dialogs.text("Neue E-Mail-Adresse von " + me.makeId() + " eingeben:\nWenn keine vorhanden ist, soll das Feld leer bleiben.", "E-Mail:");
            if (!s.equals("")) me.setEmailEmpty();
            try {
                me.setEmail(s);
            } catch (NotValidException e) {
                fixEmail(me, s + "' bzw. '" + mail, vname, nname, eintritt, istLeiter, dienverhalten);
            }
        } else me.setzeAllesNeuUndMailLeer(vname, nname, eintritt, istLeiter, dienverhalten);
    }
}