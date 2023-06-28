package net.aclrian.mpe.messdiener;


import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
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
 * Mit dieser Klasse werden Messdiener in xml-Dateien gespeichert
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
        return new Container(doc, xml, transformer);
    }

    public void toXML() throws IOException {
        try {
            Container c = writeXMLFile();
            Document doc = c.document();
            Element xml = c.element();
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

            for (int i = 0; i < DateienVerwalter.getInstance().getPfarrei().getStandardMessen().size(); i++) {
                StandardMesse messe = DateienVerwalter.getInstance().getPfarrei().getStandardMessen().get(i);
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
            leiter.appendChild(doc.createTextNode(String.valueOf(this.me.istLeiter())));
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
            File path = DateienVerwalter.getInstance().getSavePath();

            File file = new File(path, me + ".xml");
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            StreamResult result = new StreamResult(out);
            c.transformer().transform(domSource, result);
            out.close();
            Log.getLogger().info("Datei wird gespeichert in: {}", file);
            me.setFile(file);
        } catch (ParserConfigurationException | TransformerException e) {
            Dialogs.getDialogs().error(e, "Konnte den Messdiener " + me + " nicht lesen.");
        }
    }

    private void addGeschwister(Document doc, String[] g, Element anvertraute) {
        for (int i = 0; i < Messdiener.LENGHT_GESCHWISTER; i++) {
            Element ge = doc.createElement("g"+ (i+1));
            try {
                if (g[i].isEmpty()) {
                    ge.appendChild(doc.createTextNode("LEER"));
                } else {
                    ge.appendChild(doc.createTextNode(g[i]));
                }
            } catch (NullPointerException e) {
                me.getGeschwister()[i] = "";
                ge.appendChild(doc.createTextNode("LEER"));
            }
            anvertraute.appendChild(ge);
        }
    }

    private void addFreunde(Document doc, String[] f, Element anvertraute) {
        for (int i = 0; i < Messdiener.LENGHT_FREUNDE; i++) {
            Element fr = doc.createElement("F" + (i+1));
            try {
                if (f[i].isEmpty()) {
                    fr.appendChild(doc.createTextNode("LEER"));
                } else {
                    fr.appendChild(doc.createTextNode(f[i]));
                }
            } catch (NullPointerException e) {
                me.getFreunde()[i] = "";
                fr.appendChild(doc.createTextNode("LEER"));
            }
            anvertraute.appendChild(fr);
        }
    }

    public record Container(Document document, Element element, Transformer transformer) {
    }
}