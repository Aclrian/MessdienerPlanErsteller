package net.aclrian.mpe.messdiener;

import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Mit dieser Klasse werden Mesdiener in xml-Dateien gespeichert
 *
 * @author Aclrain
 */
public class WriteFile {
    private final Messdiener me;

    /**
     * @param ps der Messdiener, der gespeichert werden soll
     */
    public WriteFile(Messdiener ps) {
        this.me = ps;
    }

    public static Container writeXMLFile() throws ParserConfigurationException, TransformerConfigurationException {
        Transformer transformer = TransformerFactory.newDefaultInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        Element xml = doc.createElement("XML");
        doc.appendChild(xml);
        Element security = doc.createElement("MpE-Creator");
        security.setAttribute("LICENSE", "MIT");
        security.appendChild(doc.createTextNode("Aclrian"));
        xml.appendChild(security);
        return new Container(transformer, doc, xml);
    }

    /**
     * speichert den Messdiener
     *
     * @throws IOException wirft IOException bei bspw. zu wenig Rechten
     */
    public void toXML() throws IOException {
        try {
            Container c = writeXMLFile();
            Document doc = c.doc;
            Element xml = c.e;
            Element body = doc.createElement("Body");
            xml.appendChild(body);

            Element vName = doc.createElement("Vorname");
            vName.appendChild(doc.createTextNode(me.getVorname()));
            body.appendChild(vName);

            Element nname = doc.createElement("Nachname");
            nname.appendChild(doc.createTextNode(me.getNachnname()));
            body.appendChild(nname);

            Element mail = doc.createElement("Email");
            mail.appendChild(doc.createTextNode(me.getEmail()));
            body.appendChild(mail);

            Element mv = doc.createElement("Messverhalten");
            Messverhalten dv = me.getDienverhalten();

            for (int i = 0; i < DateienVerwalter.getDateienVerwalter().getPfarrei().getStandardMessen().size(); i++) {
                StandartMesse messe = DateienVerwalter.getDateienVerwalter().getPfarrei().getStandardMessen().get(i);
                if (messe instanceof Sonstiges) {
                    continue;
                }
                boolean kwm = dv.getBestimmtes(messe);
                String s = messe.toReduziertenString();
                Element kwmesse = doc.createElement(s);
                kwmesse.appendChild(doc.createTextNode(String.valueOf(kwm)));
                mv.appendChild(kwmesse);
            }
            body.appendChild(mv);

            Element leiter = doc.createElement("Leiter");
            leiter.appendChild(doc.createTextNode(String.valueOf(this.me.isIstLeiter())));
            body.appendChild(leiter);

            Element eintritt = doc.createElement("Eintritt");
            eintritt.appendChild(doc.createTextNode(String.valueOf(this.me.getEintritt())));
            body.appendChild(eintritt);

            // Freunde und Geschwister
            String[] f = me.getFreunde();
            String[] g = me.getGeschwister();
            Element anvertraute = doc.createElement("Anvertraute");
            addFreunde(doc, f, anvertraute);

            // Geschwister
            addGeschwister(doc, g, anvertraute);

            body.appendChild(anvertraute);

            DOMSource domSource = new DOMSource(doc);
            String path = DateienVerwalter.getDateienVerwalter().getSavepath();
            String datei = this.me.getNachnname() + ", " + this.me.getVorname();

            File file = new File(path, datei + ".xml");
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            StreamResult result = new StreamResult(out);
            c.transformer.transform(domSource, result);
            Log.getLogger().info("Datei wird gespeichert in: " + path + "//" + datei + ".xml");
            me.setFile(file);
        } catch (ParserConfigurationException | TransformerException e) {
            Dialogs.error(e, "Konnte den Messdiener " + me.makeId() + " nicht lesen.");
        }
    }

    private void addGeschwister(Document doc, String[] g, Element anvertraute) {
        Element g1 = doc.createElement("g1");
        try {
            if (g[0].equals("")) {
                g1.appendChild(doc.createTextNode("LEER"));
            } else {
                g1.appendChild(doc.createTextNode(g[0]));
            }
        } catch (NullPointerException e) {
            me.getGeschwister()[0] = "";
            g1.appendChild(doc.createTextNode("LEER"));
        }
        anvertraute.appendChild(g1);
        Element g2 = doc.createElement("g2");
        try {
            if (g[1].equals("")) {
                g2.appendChild(doc.createTextNode("LEER"));
            } else {
                g2.appendChild(doc.createTextNode(g[1]));
            }
        } catch (NullPointerException e) {
            me.getGeschwister()[1] = "";
            g2.appendChild(doc.createTextNode("LEER"));
        }
        anvertraute.appendChild(g2);
        Element g3 = doc.createElement("g3");
        try {
            if (g[2].equals("")) {
                g3.appendChild(doc.createTextNode("LEER"));
            } else {
                g3.appendChild(doc.createTextNode(g[2]));
            }
        } catch (NullPointerException e) {
            me.getGeschwister()[2] = "";
            g3.appendChild(doc.createTextNode("LEER"));
        }
        anvertraute.appendChild(g3);
    }

    private void addFreunde(Document doc, String[] f, Element anvertraute) {
        Element f1 = doc.createElement("F1");
        try {
            if (f[0].equals("")) {
                f1.appendChild(doc.createTextNode("LEER"));
            } else {
                f1.appendChild(doc.createTextNode(f[0]));
            }
        } catch (NullPointerException e) {
            me.getFreunde()[0] = "";
            f1.appendChild(doc.createTextNode("LEER"));
        }

        anvertraute.appendChild(f1);

        Element f2 = doc.createElement("F2");
        try {
            if (f[1].equals("")) {
                f2.appendChild(doc.createTextNode("LEER"));
            } else {
                f2.appendChild(doc.createTextNode(f[1]));
            }
        } catch (NullPointerException e) {
            me.getFreunde()[1] = "";
            f2.appendChild(doc.createTextNode("LEER"));
        }
        anvertraute.appendChild(f2);
        Element f3 = doc.createElement("F3");
        try {
            if (f[2].equals("")) {
                f3.appendChild(doc.createTextNode("LEER"));
            } else {
                f3.appendChild(doc.createTextNode(f[2]));
            }
        } catch (NullPointerException e) {
            me.getFreunde()[2] = "";
            f3.appendChild(doc.createTextNode("LEER"));
        }
        anvertraute.appendChild(f3);

        Element f4 = doc.createElement("F4");
        try {
            if (f[3].equals("")) {
                f4.appendChild(doc.createTextNode("LEER"));
            } else {
                f4.appendChild(doc.createTextNode(f[3]));
            }
        } catch (NullPointerException e) {
            me.getFreunde()[3] = "";
            f4.appendChild(doc.createTextNode("LEER"));
        }
        anvertraute.appendChild(f4);
        Element f5 = doc.createElement("F5");
        try {
            if (f[4].equals("")) {
                f5.appendChild(doc.createTextNode("LEER"));
            } else {
                f5.appendChild(doc.createTextNode(f[4]));
            }
        } catch (NullPointerException e) {
            me.getFreunde()[4] = "";
            f5.appendChild(doc.createTextNode("LEER"));
        }
        anvertraute.appendChild(f5);
    }

    public static class Container {
        private final Document doc;
        private final Element e;
        private final Transformer transformer;

        public Container(Transformer transformer, Document doc, Element e) {
            this.transformer = transformer;
            this.doc = doc;
            this.e = e;
        }

        public Document getDocument() {
            return doc;
        }

        public Element getElement() {
            return e;
        }

        public Transformer getTransformer() {
            return transformer;
        }
    }
}