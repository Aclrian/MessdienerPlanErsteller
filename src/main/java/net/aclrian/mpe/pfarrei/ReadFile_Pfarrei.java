package net.aclrian.mpe.pfarrei;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.AData;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.Utilities;

public class ReadFile_Pfarrei {

    public static Pfarrei getPfarrei(String pfadMitDateiundmitEndung) {
	Pfarrei pf = null;
	String name = "";
	boolean hochaemter = false;
	ArrayList<StandartMesse> sm = new ArrayList<StandartMesse>();
	ArrayList<String> orte = new ArrayList<String>();
	ArrayList<String> typen = new ArrayList<String>();
	Einstellungen einst = new Einstellungen();
	try {
	    File fXmlFile = new File(pfadMitDateiundmitEndung);
	    if (!fXmlFile.isDirectory()) {
		String s = fXmlFile.getAbsolutePath();
		if (s.endsWith(AData.pfarredateiendung)) {
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc;
		    try {
			doc = dBuilder.parse(fXmlFile);
		    } catch (org.xml.sax.SAXParseException e) {

			new Erroropener(e);
			doc = null;
		    }
		    if (doc != null) {

			doc.getDocumentElement().normalize();
			NodeList Lort = doc.getElementsByTagName("ort");
			for (int i = 0; i < Lort.getLength(); i++) {
			    Node nOrt = Lort.item(i);
			    if (nOrt.getNodeType() == 1) {
				Element eOrt = (Element) nOrt;
				orte.add(eOrt.getTextContent());
			    }

			}
			// Typ
			NodeList Ltyp = doc.getElementsByTagName("typ");
			for (int i = 0; i < Ltyp.getLength(); i++) {
			    Node nTyp = Ltyp.item(i);
			    if (nTyp.getNodeType() == 1) {
				Element eTyp = (Element) nTyp;
				typen.add(eTyp.getTextContent());
			    }
			}
			// Standartmessen
			NodeList nL = doc.getElementsByTagName("std_messe");
			for (int j = 0; j < nL.getLength(); j++) {
			    Node nsm = nL.item(j);
			    if (nsm.getNodeType() == 1) {
				Element eElement = (Element) nsm;
				String tag = eElement.getElementsByTagName("tag").item(0).getTextContent();
				int std = Integer
					.parseInt(eElement.getElementsByTagName("std").item(0).getTextContent());
				String min = eElement.getElementsByTagName("min").item(0).getTextContent();
				String ort = eElement.getElementsByTagName("ort").item(0).getTextContent();
				int anz = Integer
					.parseInt(eElement.getElementsByTagName("anz").item(0).getTextContent());
				String typ = eElement.getElementsByTagName("typ").item(0).getTextContent();
				StandartMesse stdm = new StandartMesse(tag, std, min, ort, anz, typ);
				sm.add(stdm);
			    }
			}
			// Einstellung
			NodeList nEii = doc.getElementsByTagName("Einstellungen");
			Node n = nEii.item(0);
			NodeList nEi = n.getChildNodes();
			for (int j = 0; j < nEi.getLength(); j++) {
			    Node ne = nEi.item(j);
			    if (ne.getNodeType() == 1) {
				Element eE = (Element) ne;
				if (eE.getTagName().equals("hochaemter")) {
				    String booleany = eE.getTextContent();
				    if (booleany.equals("1")) {
					hochaemter = true;
				    }
				    continue;
				}
				String val = eE.getTextContent();
				int anz = Integer.parseInt(val);
				if (eE.hasAttribute("year")) {
				    String id = eE.getAttribute("year");
				    int i = Integer.parseInt(id);
				    if (i < Einstellungen.lenght && i > -1) {
					einst.editiereYear(i, anz);
				    } else
					Utilities.logging(ReadFile_Pfarrei.class, "getPfarrei",
						"id ist zu gro" + References.ss + "!");
				} else if (eE.hasAttribute("Lleiter")) {
				    String id = eE.getAttribute("Lleiter");
				    int i = Integer.parseInt(id);
				    if (i == 0 || i == 1) {
					einst.editMaxDienen(id.equals("1"), anz);
				    } else {
					Utilities.logging(ReadFile_Pfarrei.class, "getPfarrei", "id Fehler!");
				    }

				} else {
				    Utilities.logging(ReadFile_Pfarrei.class, "getPfarrei",
					    "unbekannte Node:" + eE.getTagName());
				}
			    }
			}
		    }
		} else {
		    return pf;
		}
		String[] s2 = pfadMitDateiundmitEndung.split("\\" + File.separator);
		name = s2[s2.length - 1];
		name = name.substring(0, name.length() - AData.pfarredateiendung.length());
		name = name.replaceAll("_", " ");
		pf = new Pfarrei(einst, sm, name, orte, typen, hochaemter);
	    }
	} catch (Exception e) {
	    new Erroropener(e);
	    e.printStackTrace();
	}

	return pf;
    }
}
