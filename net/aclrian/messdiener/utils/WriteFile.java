package net.aclrian.messdiener.utils;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messverhalten;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.start.AData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * Mit dieser Klasse werden Mesdiener in xml-Dateien gespeichert
 * @author Aclrain
 *
 */
public class WriteFile {
	private Messdiener me;
	private String path;

	/**
	 * 
	 * @param ps der Messdiener, der gespeichert werden soll
	 * @param path der Pfad, bei dem der Messdienergespeichter werden soll
	 */
	public WriteFile(Messdiener ps, String path) {
		this.me = ps;
		this.path = path;
	}
/**
 * speichert den Messdiener
 * @throws IOException wirft IOException bei bspw. zu wenig Rechten
 */
	public void toXML(AData ada)throws IOException {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty("indent", "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document document = documentBuilder.newDocument();

			Element xml = document.createElement("XML");
			document.appendChild(xml);

			Element root = document.createElement("Head");
			xml.appendChild(root);

			Element security = document.createElement("Programm-Author");
			security.setAttribute("alle_Rechte", "bei_Author");
			security.appendChild(document.createTextNode("Aclrian"));
			root.appendChild(security);

			Element employee = document.createElement("Body");
			xml.appendChild(employee);

			Element vName = document.createElement("Vorname");
			vName.appendChild(document.createTextNode(this.me.getVorname()));
			employee.appendChild(vName);

			Element nname = document.createElement("Nachname");
			nname.appendChild(document.createTextNode(this.me.getNachnname()));
			employee.appendChild(nname);

			Element mv = document.createElement("Messverhalten");
			Messverhalten dv = this.me.getDienverhalten();
			
			for (int i = 0; i < ada.getPfarrei().getStandardMessen().size(); i++) {
				StandartMesse messe = ada.getPfarrei().getStandardMessen().get(i);
				System.out.println(messe);
				if (AData.sonstiges.isSonstiges(messe)) {
					continue;
				}
				boolean kwm = dv.getBestimmtes(messe, ada);
				String s = messe.toReduziertenString();
				Element kwmesse = document.createElement(s);
				kwmesse.appendChild(document.createTextNode(String.valueOf(kwm)));
				mv.appendChild(kwmesse);
			}
			employee.appendChild(mv);
			
			
			Element leiter = document.createElement("Leiter");
			leiter.appendChild(document.createTextNode(String.valueOf(this.me.isIstLeiter())));
			employee.appendChild(leiter);

			Element eintritt = document.createElement("Eintritt");
			eintritt.appendChild(document.createTextNode(String.valueOf(this.me.getEintritt())));
			employee.appendChild(eintritt);
			
			//Freunde und Geschwister
			String[] f = me.getFreunde();
			String[] g = me.getGeschwister();
			Element anvertraute = document.createElement("Anvertraute");
			Element f1 = document.createElement("F1");
			try {
				if (f[0].equals("") || f[0] == null) {
					f1.appendChild(document.createTextNode("LEER"));
				} else {
					f1.appendChild(document.createTextNode(f[0]));
				}
			} catch (NullPointerException e) {
				me.getFreunde()[0] = "";
				f1.appendChild(document.createTextNode("LEER"));
			}

			anvertraute.appendChild(f1);

			Element f2 = document.createElement("F2");
			try {
				if (f[1].equals("") || f[1] == null) {
					f2.appendChild(document.createTextNode("LEER"));
				} else {
					f2.appendChild(document.createTextNode(f[1]));
				}
			} catch (NullPointerException e) {
				me.getFreunde()[1] = "";
				f2.appendChild(document.createTextNode("LEER"));
			}
			anvertraute.appendChild(f2);
			Element f3 = document.createElement("F3");
			try {
				if (f[2].equals("") || f[2] == null) {
					f3.appendChild(document.createTextNode("LEER"));
				} else {
					f3.appendChild(document.createTextNode(f[2]));
				}
			} catch (NullPointerException e) {
				me.getFreunde()[2] = "";
				f3.appendChild(document.createTextNode("LEER"));
			}
			anvertraute.appendChild(f3);

			Element f4 = document.createElement("F4");
			try {
				if (f[3].equals("") || f[3] == null) {
					f4.appendChild(document.createTextNode("LEER"));
				} else {
					f4.appendChild(document.createTextNode(f[3]));
				}
			} catch (NullPointerException e) {
				me.getFreunde()[3] = "";
				f4.appendChild(document.createTextNode("LEER"));
			}
			anvertraute.appendChild(f4);
			Element f5 = document.createElement("F5");
			try {
				if (f[4].equals("") || f[4] == null) {
					f5.appendChild(document.createTextNode("LEER"));
				} else {
					f5.appendChild(document.createTextNode(f[4]));
				}
			} catch (NullPointerException e) {
				me.getFreunde()[4] = "";
				f5.appendChild(document.createTextNode("LEER"));
			}
			anvertraute.appendChild(f5);

			// Geschwister
			Element g1 = document.createElement("g1");
			try {
				if (g[0].equals("") || g[0] == null) {
					g1.appendChild(document.createTextNode("LEER"));
				} else {
					System.out.println(":" + g[0]);
					g1.appendChild(document.createTextNode(g[0]));
				}
			} catch (NullPointerException e) {
				me.getGeschwister()[0] = "";
				g1.appendChild(document.createTextNode("LEER"));
			}
			anvertraute.appendChild(g1);
			Element g2 = document.createElement("g2");
			try {
				if (g[1].equals("") || g[1] == null) {
					g2.appendChild(document.createTextNode("LEER"));
				} else {
					g2.appendChild(document.createTextNode(g[1]));
				}
			} catch (NullPointerException e) {
				me.getGeschwister()[1] = "";
				g2.appendChild(document.createTextNode("LEER"));
			}
			anvertraute.appendChild(g2);
			Element g3 = document.createElement("g3");
			try {
				if (g[2].equals("") || g[2] == null) {
					g3.appendChild(document.createTextNode("LEER"));
				} else {
					g3.appendChild(document.createTextNode(g[2]));
				}
			} catch (NullPointerException e) {
				me.getGeschwister()[2] = "";
				g3.appendChild(document.createTextNode("LEER"));
			}
			anvertraute.appendChild(g3);

			employee.appendChild(anvertraute);

			DOMSource domSource = new DOMSource(document);
			if (this.path.contains("//")) {
				path.replaceAll("//", "/");
				path.replaceAll("/", "//");
			}
			String datei = this.me.getNachnname() + "_" + this.me.getVorname();

			System.out.println("Datei wird gespeichert in :" + path + "//" + datei + ".xml");
			StreamResult streamResult = new StreamResult(new File(path + "//" + datei + ".xml"));
			transformer.transform(domSource, streamResult);
			System.out.println("Done creating XML File for: " + this.me.getVorname() + " " + this.me.getNachnname());
		} catch (

		ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}