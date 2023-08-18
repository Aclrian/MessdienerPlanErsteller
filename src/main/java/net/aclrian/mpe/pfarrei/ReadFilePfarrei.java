package net.aclrian.mpe.pfarrei;


import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.utils.DateUtil;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.MPELog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ReadFilePfarrei {

    private ReadFilePfarrei() {
    }

    public static Pfarrei getPfarrei(String pfadMitDateiundmitEndung) throws ParserConfigurationException, IOException, SAXException {
        Pfarrei pf = null;
        File fXmlFile = new File(pfadMitDateiundmitEndung);
        String s = fXmlFile.getAbsolutePath();
        if (s.endsWith(DateienVerwalter.PFARREI_DATEIENDUNG)) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newDefaultInstance();
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // DevSkim: reviewed DS137138 on 2023-07-28 by Aclrian
            dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false); // DevSkim: reviewed DS137138 by Aclrian
            dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false); // DevSkim: reviewed DS137138 by Aclrian
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            if (doc == null) {
                throw new IOException("Could not parse the file");
            }
            doc.getDocumentElement().normalize();

            String name;
            boolean hochaemter;
            ArrayList<StandardMesse> standardMessen = new ArrayList<>();
            Einstellungen einst = new Einstellungen();
            readStandardMessen(doc, standardMessen);
            hochaemter = readEinstellungen(einst, doc);

            String[] s2 = pfadMitDateiundmitEndung.split(Pattern.quote(File.separator));
            name = s2[s2.length - 1];
            name = name.substring(0, name.length() - DateienVerwalter.PFARREI_DATEIENDUNG.length());
            name = name.replace("_", " ");
            standardMessen.add(new Sonstiges());
            pf = new Pfarrei(einst, standardMessen, name, hochaemter);
        }
        return pf;
    }

    private static void readStandardMessen(Document doc, ArrayList<StandardMesse> standardMessen) {
        NodeList nL = doc.getElementsByTagName("std_messe");
        for (int j = 0; j < nL.getLength(); j++) {
            Node nsm = nL.item(j);
            if (nsm.getNodeType() != 1) {
                continue;
            }
            Element eElement = (Element) nsm;
            String tag = eElement.getElementsByTagName("tag").item(0).getTextContent();

            TemporalAccessor accessor = DateUtil.SHORT_STANDALONE.parse(tag);
            DayOfWeek dow = DayOfWeek.from(accessor);
            int std = Integer.parseInt(eElement.getElementsByTagName("std").item(0).getTextContent());
            String min = eElement.getElementsByTagName("min").item(0).getTextContent();
            String ort = eElement.getElementsByTagName("ort").item(0).getTextContent();
            int anz = Integer.parseInt(eElement.getElementsByTagName("anz").item(0).getTextContent());
            String typ = eElement.getElementsByTagName("typ").item(0).getTextContent();

            // non-weekly repetition
            List<Integer> nonDefaultWeeklyRepetition = new ArrayList<>();
            NodeList repetitionTag = eElement.getElementsByTagName("wdh");
            if (repetitionTag.getLength() == 1 && !repetitionTag.item(0).getTextContent().isEmpty()) {
                String[] listOfRepetition = repetitionTag.item(0).getTextContent().split(",");
                for (Integer i : StandardMesse.ALLOWED_REPETITION_NUMBERS) {
                    if (Arrays.stream(listOfRepetition).anyMatch(rep -> rep.equals(i.toString()))) {
                        nonDefaultWeeklyRepetition.add(i);
                    }
                }
            }

            StandardMesse sm = new StandardMesse(dow, std, min, ort, anz, typ, nonDefaultWeeklyRepetition);
            standardMessen.add(sm);
        }
    }

    private static boolean readEinstellungen(Einstellungen einst, Document doc) {
        NodeList nEii = doc.getElementsByTagName("Einstellungen");
        Node n = nEii.item(0);
        NodeList nEi = n.getChildNodes();
        boolean hochaemter = false;
        for (int j = 0; j < nEi.getLength(); j++) {
            Node ne = nEi.item(j);
            if (ne.getNodeType() == 1) {
                Element eE = (Element) ne;
                if (eE.getTagName().equals("hochaemter")) {
                    hochaemter = eE.getTextContent().equals("1");
                    continue;
                }
                readSetting(einst, eE);
            }
        }
        return hochaemter;
    }

    private static void readSetting(Einstellungen einst, Element eE) {
        String val = eE.getTextContent();
        int anz = Integer.parseInt(val);
        if (eE.hasAttribute("year")) {
            String id = eE.getAttribute("year");
            int i = Integer.parseInt(id);
            if (i < Einstellungen.LENGTH && i > -1) {
                einst.editiereYear(i, anz);
            } else {
                MPELog.getLogger().info("id ist zu groß!");
            }
        } else if (eE.hasAttribute("Lleiter")) {
            String id = eE.getAttribute("Lleiter");
            int i = Integer.parseInt(id);
            if (i == 0 || i == 1) {
                einst.editMaxDienen(id.equals("1"), anz);
            } else {
                MPELog.getLogger().info("id Fehler!");
            }

        } else {
            MPELog.getLogger().warn("unbekannte Node: {}", eE.getTagName());
        }
    }
}
