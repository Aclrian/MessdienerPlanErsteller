package net.aclrian.mpe.messdiener;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.aclrian.mpe.messdiener.Messdiener.NotValidException;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

/**
 * liesst aus einer xml-Datei den Messdiener und gibt diesen zurueck
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
	public Messdiener getMessdiener(String pfadMitDateiundmitEndung) {
		Messdiener me = null;
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
						Dialogs.error(e, "Die Datei " + fXmlFile + " konnte nicht gelesen werden.");
						doc = null;
					}
					if (doc != null) {
						doc.getDocumentElement().normalize();
						NodeList nList = doc.getElementsByTagName("XML");
						for (int temp = 0; temp < nList.getLength(); temp++) {
							Node nNode = nList.item(temp);
							if (nNode.getNodeType() == 1) {
								Element eElement = (Element) nNode;
								String mail;
								String vname = eElement.getElementsByTagName("Vorname").item(0).getTextContent();
								String nname = eElement.getElementsByTagName("Nachname").item(0).getTextContent();
								try {
									mail = eElement.getElementsByTagName("Email").item(0).getTextContent();
								} catch (NullPointerException e) {
									mail = "";
								}
								int Eintritt = Integer
										.parseInt(eElement.getElementsByTagName("Eintritt").item(0).getTextContent());
								if(Eintritt < Messdaten.getMinYear()) {
									Eintritt =Messdaten.getMinYear();
								}
								if(Eintritt > Messdaten.getMaxYear()) {
									Eintritt = Messdaten.getMaxYear();
								}
								boolean istLeiter = Boolean
										.valueOf(eElement.getElementsByTagName("Leiter").item(0).getTextContent());
								// Messverhalten
								Messverhalten dienverhalten = new Messverhalten();
								for (StandartMesse sm : DateienVerwalter.dv.getPfarrei().getStandardMessen()) {
									if (sm instanceof Sonstiges) {
										continue;
									}
									String sname = sm.toReduziertenString();
									String value = eElement.getElementsByTagName(sname).item(0).getTextContent();
									boolean kann = value.equals("true");
									dienverhalten.editiereBestimmteMesse(sm, kann);
								}
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
								me = new Messdiener(fXmlFile);
								try {
								me.setzeAllesNeu(vname, nname, Eintritt, istLeiter, dienverhalten, mail);
								} catch (Exception e) {
									fixEmail(me, mail, vname, nname, Eintritt, istLeiter, dienverhalten);
								}
								me.setFreunde(freunde);
								me.setGeschwister(geschwister);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Dialogs.error(e, "Fehler beim Lesen der Datei: " + pfadMitDateiundmitEndung);
			e.printStackTrace();
		}
		return me;
	}
	
	private void fixEmail(Messdiener me, String mail, String vname, String nname, int Eintritt, boolean istLeiter, Messverhalten dienverhalten) {
		if(Dialogs.frage("Die E-Mail-Addresse '" + mail +"' von "+ me.makeId() + " ist ungültig und wird gelöscht.\nSoll eine neue eingegeben werden?")) {
			String s = Dialogs.text("Neue E-Mail-Adresse von " + me.makeId() + " eingeben:\nWenn keine vorhanden ist, soll das Feld leer bleiben.", "E-Mail:");
			if(s == null)me.setEmailEmpty();
			try {
				me.setEmail(s);
			} catch (NotValidException e) {
				fixEmail(me, s+"' bzw. '" + mail, vname, nname, Eintritt, istLeiter, dienverhalten);
			}
		}else me.setzeAllesNeuUndMailLeer(vname, nname, Eintritt, istLeiter, dienverhalten);
	}
}