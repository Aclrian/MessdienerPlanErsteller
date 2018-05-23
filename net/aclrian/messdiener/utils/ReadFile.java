package net.aclrian.messdiener.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messverhalten;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.window.WMainFrame;

/**
 * lieÃŸt aus einer xml-Datei den Messdiener und gibt diesen zuÃ¼rck
 * 
 * @author Aclrian
 *
 */
public class ReadFile {
	/**
	 * 
	 * @param pfadMitDateiundmitEndung
	 * @return den ausgelesenen Messdiener
	 */
	public Messdiener getMessdiener(String pfadMitDateiundmitEndung, WMainFrame wmf) {
		Messdiener me = new Messdiener();
		try {
			File fXmlFile = new File(pfadMitDateiundmitEndung);
			if (!fXmlFile.isDirectory()) {
				String s = fXmlFile.getAbsolutePath();
				if (s.endsWith(".xml")) {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc;
					try {
						doc = dBuilder.parse(fXmlFile);
					} catch (org.xml.sax.SAXParseException e) {
						new Erroropener(e.getMessage()+": Das Dokument konnte nicht gelesen werden.");
						doc = null;
					}
					if (doc != null) {

						doc.getDocumentElement().normalize();

						NodeList nList = doc.getElementsByTagName("XML");

						for (int temp = 0; temp < nList.getLength(); temp++) {

							Node nNode = nList.item(temp);

							if (nNode.getNodeType() == 1) {
								Element eElement = (Element) nNode;

								String vname = eElement.getElementsByTagName("Vorname").item(0).getTextContent();
								String nname = eElement.getElementsByTagName("Nachname").item(0).getTextContent();
								int Eintritt = Integer
										.parseInt(eElement.getElementsByTagName("Eintritt").item(0).getTextContent());
								boolean istLeiter = Boolean
										.valueOf(eElement.getElementsByTagName("Leiter").item(0).getTextContent());
								// Messverhalten
								Messverhalten dienverhalten = new Messverhalten(wmf);
								for (StandartMesse sm : wmf.getStandardmessen()) {
									if (wmf.getSonstiges().isSonstiges(sm)) {
										continue;
									}
									String sname = sm.toReduziertenString();
									//Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"Es wird f�r " + sname + " gesucht nach Eintr�gen");
									String value = eElement.getElementsByTagName(sname).item(0).getTextContent();
									boolean kann = value.equals("true");
									dienverhalten.editiereBestimmteMesse(wmf, sm, kann);
								}
/*
								dienverhalten.editiereBestimmteMesse(wmf, Holen.Di_abend, Boolean.valueOf(
										eElement.getElementsByTagName("Dienstag_Abend").item(0).getTextContent()));
								dienverhalten.editiereBestimmteMesse(wmf, Holen.Sam_hochzeit, Boolean.valueOf(
										eElement.getElementsByTagName("Samstag_14:00").item(0).getTextContent()));
								dienverhalten.editiereBestimmteMesse(wmf, Holen.Sam_abend, Boolean.valueOf(
										eElement.getElementsByTagName("Samstag_Abend").item(0).getTextContent()));
								dienverhalten.editiereBestimmteMesse(wmf, Holen.Son_morgen, Boolean.valueOf(
										eElement.getElementsByTagName("Sontag_Morgen").item(0).getTextContent()));
								dienverhalten.editiereBestimmteMesse(wmf, Holen.Son_taufe, Boolean.valueOf(
										eElement.getElementsByTagName("Sontag_Taufe").item(0).getTextContent()));
								System.out.println(vname+nname+Boolean.valueOf(
										eElement.getElementsByTagName("Sontag_Abend").item(0).getTextContent()));
								dienverhalten.editiereBestimmteMesse(wmf, Holen.Son_abend, Boolean.valueOf(
										eElement.getElementsByTagName("Sontag_Abend").item(0).getTextContent()));
*/
								
								String[] freunde = new String[5];
								String[] geschwister = new String[3];
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
									Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"Es wurde eine Alte Version von Messdiener-Dateien gefunden!"
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
								String g1 = "LEER";
								String g2 = "LEER";
								String g3 = "LEER";
								try {
									g1 = eElement.getElementsByTagName("g1").item(0).getTextContent();
									g2 = eElement.getElementsByTagName("g2").item(0).getTextContent();
									g3 = eElement.getElementsByTagName("g3").item(0).getTextContent();
								} catch (NullPointerException e) {
									Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"Es wurde eine Alte Version von Messdiener-Dateien gefunden!"
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
								if (!f1.equals("LEER")) {
									geschwister[2] = g3;
								} else {
									geschwister[2] = "";
								}

								me.setzeAllesNeu(vname, nname, Eintritt, istLeiter, dienverhalten);
								me.setFreunde(freunde);
								me.setGeschwister(geschwister);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			new Erroropener(e.getMessage());
			e.printStackTrace();
		}

		return me;
	}
}