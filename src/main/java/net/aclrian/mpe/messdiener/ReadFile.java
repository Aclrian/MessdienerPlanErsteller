package net.aclrian.mpe.messdiener;


import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
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
 * liest aus einer xml-Datei den Messdiener und gibt diesen zurück
 *
 * @author Aclrian
 */
public class ReadFile {
    /**
     * @param xmlFile die Datei
     * @return den ausgelesenen Messdiener
     */
    public Messdiener getMessdiener(File xmlFile) {
        Messdiener me = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(new InputSource(String.valueOf(xmlFile.toURI())));
            if (doc != null && xmlFile.getAbsolutePath().endsWith(DateienVerwalter.MESSDIENER_DATEIENDUNG)) {
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
                        readFreunde(xmlFile, eElement, freunde);
                        readGeschwister(xmlFile, eElement, geschwister);

                        me = toMessdiener(xmlFile, mail, vname, nname, eintritt, istLeiter, dienverhalten);
                        me.setFreunde(freunde);
                        me.setGeschwister(geschwister);
                    }
                }
            }
        } catch (Exception e) {
            Dialogs.getDialogs().error(e, "Fehler beim Lesen der Datei: " + xmlFile);
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
        for (StandardMesse sm : DateienVerwalter.getInstance().getPfarrei().getStandardMessen()) {
            if (sm instanceof Sonstiges) {
                continue;
            }
            String sname = sm.toReduziertenString();
            boolean kann = false;
            try {
                String value = eElement.getElementsByTagName(sname).item(0).getTextContent();
                kann = value.equals("true");
            } catch (Exception e) {
                MPELog.getLogger().warn(e.getMessage(), e);
            }
            dienverhalten.editiereBestimmteMesse(sm, kann);
        }
        return dienverhalten;
    }

    private void readGeschwister(File fXmlFile, Element eElement, String[] geschwister) {
        String g1 = "";
        String g2 = "";
        String g3 = "";
        try {
            g1 = eElement.getElementsByTagName("g1").item(0).getTextContent();
            g2 = eElement.getElementsByTagName("g2").item(0).getTextContent();
            g3 = eElement.getElementsByTagName("g3").item(0).getTextContent();
        } catch (NullPointerException e) {
            MPELog.getLogger().info("Es wurde eine alte Version von Messdiener-Dateien gefunden! {}", fXmlFile.getName());
        }
        if (g1.equals("LEER")) {
            geschwister[0] = "";
        } else {
            geschwister[0] = g1;
        }
        if (g2.equals("LEER")) {
            geschwister[1] = "";
        } else {
            geschwister[1] = g2;
        }
        if (g3.equals("LEER")) {
            geschwister[2] = "";
        } else {
            geschwister[2] = g3;
        }
    }

    private void readFreunde(File fXmlFile, Element eElement, String[] freunde) {
        String f1 = "";
        String f2 = "";
        String f3 = "";
        String f4 = "";
        String f5 = "";
        try {
            f1 = eElement.getElementsByTagName("F1").item(0).getTextContent();
            f2 = eElement.getElementsByTagName("F2").item(0).getTextContent();
            f3 = eElement.getElementsByTagName("F3").item(0).getTextContent();
            f4 = eElement.getElementsByTagName("F4").item(0).getTextContent();
            f5 = eElement.getElementsByTagName("F5").item(0).getTextContent();
        } catch (NullPointerException e) {
            MPELog.getLogger().info("Es wurde eine alte Version von Messdiener-Dateien gefunden! {}", fXmlFile.getName());
        }
        if (f1.equals("LEER")) {
            freunde[0] = "";
        } else {
            freunde[0] = f1;
        }
        if (f2.equals("LEER")) {
            freunde[1] = "";
        } else {
            freunde[1] = f2;
        }
        if (f3.equals("LEER")) {
            freunde[2] = "";
        } else {
            freunde[2] = f3;
        }
        if (f4.equals("LEER")) {
            freunde[3] = "";
        } else {
            freunde[3] = f4;
        }
        if (f5.equals("LEER")) {
            freunde[4] = "";
        } else {
            freunde[4] = f5;
        }
    }

    private void fixEmail(Messdiener me, String mail, String vorname, String nachname, int eintritt, boolean leiter, Messverhalten dienverhalten) {
        if (Dialogs.getDialogs().frage("Die E-Mail-Addresse '" + mail + "' von " + me + " ist ungültig und wird gelöscht.\nSoll eine neue eingegeben werden?")) {
            String s = Dialogs.getDialogs().text("Neue E-Mail-Adresse von " + me + " eingeben:\nWenn keine vorhanden ist, soll das Feld leer bleiben.", "E-Mail:");
            if (!s.equals("")) {
                me.setEmailEmpty();
            }
            try {
                me.setEmail(s);
            } catch (Messdiener.NotValidException e) {
                fixEmail(me, s + "' bzw. '" + mail, vorname, nachname, eintritt, leiter, dienverhalten);
            }
        } else {
            me.setzeAllesNeuUndMailLeer(vorname, nachname, eintritt, leiter, dienverhalten);
        }
    }
}