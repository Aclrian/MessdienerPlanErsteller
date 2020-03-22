package net.aclrian.mpe.pfarrei;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javafx.stage.Window;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Setting.Attribut;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

public class WriteFile_Pfarrei {

	public static void writeFile(Pfarrei pf, Window window, String savepath) {
		try {
			ArrayList<StandartMesse> Wsm = pf.getStandardMessen();

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty("indent", "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document doc = documentBuilder.newDocument();

			Element xml = doc.createElement("XML");
			doc.appendChild(xml);
			Element security = doc.createElement("Programm-Author");
			security.setAttribute("alle_Rechte", "bei_Author");
			security.appendChild(doc.createTextNode("Aclrian"));
			xml.appendChild(security);

			Element employee = doc.createElement("Body");
			xml.appendChild(employee);

			Element standartmessen = doc.createElement("Standartmessem");
			employee.appendChild(standartmessen);
			for (int i = 0; i < Wsm.size(); i++) {
				StandartMesse m = Wsm.get(i);
				if (m instanceof Sonstiges) {
					continue;
				}

				Element stdm = doc.createElement("std_messe");
				stdm.setAttribute("id", String.valueOf(i));

				Element tag = doc.createElement("tag");
				tag.appendChild(doc.createTextNode(m.getWochentag()));
				stdm.appendChild(tag);

				Element std = doc.createElement("std");
				std.appendChild(doc.createTextNode(String.valueOf(m.getBeginn_stunde())));
				stdm.appendChild(std);

				Element min = doc.createElement("min");
				min.appendChild(doc.createTextNode(m.getBeginn_minute()));
				stdm.appendChild(min);

				Element ort = doc.createElement("ort");
				ort.appendChild(doc.createTextNode(m.getOrt()));
				stdm.appendChild(ort);

				Element anz = doc.createElement("anz");
				anz.appendChild(doc.createTextNode(String.valueOf(m.getAnz_messdiener())));
				stdm.appendChild(anz);

				Element typ = doc.createElement("typ");
				typ.appendChild(doc.createTextNode(m.getTyp()));
				stdm.appendChild(typ);

				standartmessen.appendChild(stdm);
			}

			/*
			 * Element orte = doc.createElement("Orte"); xml.appendChild(orte); for (int i =
			 * 0; i < pf.getOrte().size(); i++) { String ort = pf.getOrte().get(i); Element
			 * o = doc.createElement("ort"); o.setAttribute("id", String.valueOf(i));
			 * o.appendChild(doc.createTextNode(ort)); orte.appendChild(o); }
			 * 
			 * Element type = doc.createElement("Type"); xml.appendChild(type); for (int i =
			 * 0; i < pf.getTypen().size(); i++) { String typ = pf.getTypen().get(i);
			 * Element t = doc.createElement("typ"); t.setAttribute("id",
			 * String.valueOf(i)); t.appendChild(doc.createTextNode(typ));
			 * type.appendChild(t); }
			 */

			// Einstellungen
			Element einst = doc.createElement("Einstellungen");
			xml.appendChild(einst);

			Element hochamt = doc.createElement("hochaemter");
			int booleany = 0;
			if (pf.zaehlenHochaemterMit()) {
				booleany++;
			}
			hochamt.appendChild(doc.createTextNode(String.valueOf(booleany)));
			einst.appendChild(hochamt);

			// Settings
			for (int i = 0; i < Einstellungen.lenght; i++) {
				Setting s = pf.getSettings().getDaten(i);
				Attribut a = s.getA();
				int anz = s.getAnz_dienen();
				String val = String.valueOf(anz);
				int id = s.getId();
				String id_s = String.valueOf(id);
				switch (a) {
				case year:
					Element year = doc.createElement("setting");
					year.setAttribute("year", id_s);
					year.appendChild(doc.createTextNode(val));
					einst.appendChild(year);
					break;

				case max:
					Element ee = doc.createElement("setting");
					ee.setAttribute("Lleiter", id_s);
					ee.appendChild(doc.createTextNode(val));
					einst.appendChild(ee);
					break;
				}
			}

			DOMSource domSource = new DOMSource(doc);
			String datei = pf.getName();
			datei = datei.replaceAll(" ", "_");
			savepath = savepath==null ? DateienVerwalter.dv.getSavepath(window) : savepath;
			Log.getLogger()
					.info("Pfarrei wird gespeichert in :" + savepath + File.separator + datei + DateienVerwalter.pfarredateiendung);
			File f = new File(savepath + File.separator + datei + DateienVerwalter.pfarredateiendung);
			StreamResult streamResult = new StreamResult(f);
			transformer.transform(domSource, streamResult);
			DateienVerwalter.dv.removeoldPfarrei(f);
			Log.getLogger().info("Pfarrei: " + pf.getName() + "wurde erfolgreich gespeichert!");
		} catch (ParserConfigurationException pce) {
			Dialogs.error(pce, "Fehler bei Speichern der Pfarrei:");
		} catch (TransformerException tfe) {
			Dialogs.error(tfe, "Fehler bei Speichern der Pfarrei:");
		}

	}

	public static void writeFile(Pfarrei pf, Window window) {
		writeFile(pf, window, DateienVerwalter.dv.getSavepath(window));
	}
}