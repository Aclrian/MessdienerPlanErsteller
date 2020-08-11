package net.aclrian.mpe.controller;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import net.aclrian.mpe.utils.RemoveDoppelte;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class FinishController implements Controller {

    private final String fertig;
    private final MainController.EnumPane pane;
    private final List<Messe> messen;
    private final List<Messdiener> hauptarray;
    private final ArrayList<Messdiener> nichteingeteileList;
    private boolean locked = true;
    private String titel;
    private File pdfgen;
    private File wordgen;
    @FXML
    private HTMLEditor editor;
    @FXML
    private Button mail;
    @FXML
    private Button nichteingeteilte;
    @FXML
    private Button zurueck;
    public FinishController(MainController.EnumPane old, List<Messe> messen) {
        this.pane = old;
        this.hauptarray = DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList();
        this.messen = messen;
        neuerAlgorythmus();
        StringBuilder s = new StringBuilder("<html>");
        for (int i = 0; i < messen.size(); i++) {
            Messe messe = messen.get(i);
            String m1 = messe.htmlAusgeben();
            m1 = m1.substring(6, m1.length() - 7);
            if (i == 0) {
                Date start = messe.getDate();
                Date ende = messen.get(messen.size() - 1).getDate();
                SimpleDateFormat df = new SimpleDateFormat("dd. MMMM", Locale.GERMAN);
                titel = "Messdienerplan vom " + df.format(start) + " bis " + df.format(ende);
                s.append("<h1>").append(titel).append("</h1>");
            }
            s.append("<p>").append(m1).append("</p>");
        }
        s.append("</html>");
        fertig = s.toString();
        nichteingeteileList = new ArrayList<>();
        for (Messdiener medi : hauptarray) {
            if (medi.getMessdatenDaten().getInsgesamtEingeteilt() == 0) {
                nichteingeteileList.add(medi);
            }
        }
        nichteingeteileList.sort(Messdiener.compForMedis);
    }

    @Override
    public void initialize() {
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void afterstartup(Window window, MainController mc) {
        nichteingeteilte.setOnAction(event -> nichteingeteilte());
        editor.setHtmlText(fertig);
        zurueck.setOnAction(p -> this.zurueck(mc));
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    private void zurueck(MainController mc) {
        try {
            boolean delete = Dialogs.yesNoCancel("Ja", "Nein", "Hier bleiben", "Soll die aktuelle Einteilung gelöscht werden, um einen neuen Plan mit ggf. geänderten Messen generieren zu können?");
            if (delete) {
                DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList().forEach(m -> m.getMessdatenDaten().nullen());
                for (Messe m : messen) {
                    m.nullen();
                }
            }
        } catch (NullPointerException e) {
            //do nothing
        }

        locked = false;
        mc.setMesse(this, pane);
    }

    public List<Messe> getMessen() {
        return messen;
    }

    private void nichteingeteilte() {
        Dialogs.show(nichteingeteileList, "Nicht eingeteilte Messdiener");
    }

    @FXML
    public void toEMAIL() {
        List<Messdiener> medis = DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList();
        StringBuilder sb = new StringBuilder("mailto:?");
        ArrayList<Messdiener> noemail = new ArrayList<>();
        for (Messdiener medi : medis) {
            if (medi.getEmail().equals("")) {
                noemail.add(medi);
            } else {
                sb.append("bcc=").append(medi.getEmail()).append("&");
            }
        }
        sb.append("subject=").append(org.springframework.web.util.UriUtils.encode(titel, StandardCharsets.UTF_8));
        StringBuilder sbb = new StringBuilder();
        StringBuilder ssb = new StringBuilder();
        if (pdfgen != null && pdfgen.exists()) {
            sbb.append("Als Anhang hinzufügen: ").append(pdfgen.getAbsolutePath()).append(" ?");
        }
        if (wordgen != null && wordgen.exists()) {
            ssb.append("Als Anhang hinzufügen: ").append(wordgen.getAbsolutePath()).append(" ?");
        }
        sb.append("&body=").append(org.springframework.web.util.UriUtils.encode(sbb.toString(), StandardCharsets.UTF_8)).append("%0D%0A").append(org.springframework.web.util.UriUtils.encode(ssb.toString(), StandardCharsets.UTF_8));
        try {
            URI uri = new URI(sb.toString());
            Desktop.getDesktop().mail(uri);
            Log.getLogger().info(sb.toString());
            if (noemail.isEmpty()) {
                Dialogs.show(noemail, "Messdiener ohne E-Mail-Addresse:");
            }
        } catch (IOException | URISyntaxException | IllegalArgumentException e) {
            Dialogs.error(e, "Konnte den mailto-Link (" + URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8) + ") nicht öffnen");
        }
    }

    @FXML
    public void toPDF(ActionEvent actionEvent) {
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setCharset("UTF-8");
        try {
            File out = new File(Log.getWorkingDir().getAbsolutePath() + File.separator + titel + ".pdf");
            //TODO should out deleted first?
            HtmlConverter.convertToPdf(new ByteArrayInputStream(editor.getHtmlText().replace("<p></p>", "<br>").getBytes(StandardCharsets.UTF_8)),
                    new FileOutputStream(out), converterProperties);
            pdfgen = out;
            Desktop.getDesktop().open(out);
        } catch (IOException e) {
            Dialogs.error(e, "Konnte den Messdienerplan nicht zu PDF konvertieren.");
        }
    }

    @FXML
    public void toWORD(ActionEvent actionEvent) {
        try {
            String input = editor.getHtmlText().replace("<p></p>", "");
            input = input.replace("</br>", "");
            input = input.replace("<br>", "<br></br>");
            Log.getLogger().debug(input);
            RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
            rfonts.setAscii("Century Gothic");
            XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
            wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
            ndp.unmarshalDefaultNumbering();
            XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);
            importer.setHyperlinkStyle("Hyperlink");
            wordMLPackage.getMainDocumentPart().getContent().addAll(importer.convert(input, Log.getWorkingDir().getAbsolutePath()));

            File out = new File(Log.getWorkingDir() + File.separator + titel + ".docx");
            //TODO should old deleted?
            wordMLPackage.save(out);
            wordgen = out;
            Desktop.getDesktop().open(out);
        } catch (Exception e) {
            Dialogs.error(e, "Word-Dokument konnte nicht erstellt werden.");
        }
    }

    private void neuerAlgorythmus() {
        hauptarray.sort(Messdiener.einteilen);
        // neuer Monat:
        Calendar start = Calendar.getInstance();
        start.setTime(messen.get(0).getDate());
        start.add(Calendar.MONTH, 1);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Log.getLogger().info("nächster Monat bei: " + df.format(start.getTime()));
        // EIGENTLICHER ALGORYTHMUS
        for (Messe me : messen) {
            if (me.getDate().after(start.getTime())) {
                start.add(Calendar.MONTH, 1);
                Log.getLogger().info("nächster Monat: Es ist " + df.format(me.getDate()));
                for (Messdiener messdiener : hauptarray) {
                    messdiener.getMessdatenDaten().naechsterMonat();
                }
            }
            Log.getLogger().info("Messe dran: " + me.getID());
            if (me.getStandardMesse() instanceof Sonstiges) {
                this.einteilen(me, EnumAction.EINFACH_EINTEILEN);
            } else {
                this.einteilen(me, EnumAction.TYPE_BEACHTEN);
            }
            Log.getLogger().info("Messe fertig: " + me.getID());
        }
    }

    private void einteilen(Messe m, EnumAction act) {
        if (act == EnumAction.EINFACH_EINTEILEN) {
            List<Messdiener> medis = get(new Sonstiges(), m, false, false);
            Log.getLogger().info(medis.size() + " für " + m.getNochBenoetigte());
            for (Messdiener medi : medis) {
                einteilen(m, medi, false, false);
            }
            if (!m.istFertig()) zwang(m);
        } else if (act == EnumAction.TYPE_BEACHTEN) {
            List<Messdiener> medis = get(m.getStandardMesse(), m, false, false);
            Log.getLogger().info(medis.size() + " für " + m.getNochBenoetigte());
            for (Messdiener messdiener : medis) {
                einteilen(m, messdiener, false, false);
            }
            if (!m.istFertig()) zwang(m);
        }
    }

    public void zwang(Messe m, boolean zangDate, boolean zwangAnz, String loggerOutput){
        List<Messdiener> medis = get(m.getStandardMesse(), m, zangDate, zwangAnz);
        Log.getLogger().warn(m + loggerOutput);
        for (Messdiener medi : medis) {
            einteilen(m, medi, false, true);
        }
    }

    private void zwang(Messe m) {
        if (m.istFertig()) return;
		final String start = "Die Messe ";
		String secondPart = " hat zu wenige Messdiener.\nNoch ";

		if (!(m.getStandardMesse() instanceof Sonstiges)) {
            if (Dialogs.frage(start + m.getID().replaceAll("\t", "   ") + secondPart + m.getNochBenoetigte() + " werden benötigt.\nSollen Messdiener eingeteilt werden, die standartmäßig die Messe \n'" + m.getStandardMesse().tokurzerBenutzerfreundlichenString() + "' dienen können, aber deren Anzahl schon zu hoch ist?")) {
                zwang(m, false, true, " einteilen ohne Anzahl beachten");
            }
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy");
            if (Dialogs.frage(start + m.getID().replaceAll("\t", "   ") + secondPart + m.getNochBenoetigte() + " werden benötigt.\nSollen Messdiener eingeteilt werden, die an dem Tag \n'" + df.format(m.getDate()) + "' dienen können?")) {
                zwang(m, false, false, " einteilen ohne Standardmesse beachten");
            }
        }
		if (m.istFertig()) {
		    return;
        }
        if (Dialogs.frage(start + m.getID().replaceAll("\t", "   ") + secondPart + m.getNochBenoetigte() + " werden benötigt.\nSollen Messdiener zwangsweise eingeteilt werden?")) {
            zwang(m, true, true , " einteilen ohne Standardmesse beachten");
        }
        zuwenige(m);
    }

    private void zuwenige(Messe m) {
        if (!m.istFertig())
            Dialogs.error("Die Messe" + m.getID().replaceAll("\t", "   ") + " wird zu wenige Messdiener haben.");
    }

    private void einteilen(Messe m, Messdiener medi, boolean zwangdate, boolean zwanganz) {
        boolean d;
        if (m.istFertig()) {
            return;
        } else {
            d = m.einteilen(medi, zwangdate, zwanganz);
        }
        if (!m.istFertig() && d) {
            List<Messdiener> anv = medi.getMessdatenDaten().getAnvertraute(DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList());
            RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
            rd.removeDuplicatedEntries(anv);
            if (!anv.isEmpty()) {
                anv.sort(Messdiener.einteilen);
                for (Messdiener messdiener : anv) {
                    boolean b = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse());
                    if (messdiener.getMessdatenDaten().kann(m.getDate(), zwangdate, zwanganz) && b) {
                        Log.getLogger().info(messdiener.makeId() + " dient mit " + medi.makeId() + "?");
                        einteilen(m, messdiener, zwangdate, zwanganz);
                    }
                }
            }
        }
    }

    public List<Messdiener> get(StandartMesse sm, Messe m, boolean zwangdate, boolean zwanganz) {
        ArrayList<Messdiener> al = new ArrayList<>();
        for (Messdiener medi : hauptarray) {
            int id = 0;
            if (medi.isIstLeiter()) {
                id++;
            }
            int ii = DateienVerwalter.getDateienVerwalter().getPfarrei().getSettings().getDaten(id).getAnzDienen();
            if (medi.getDienverhalten().getBestimmtes(sm) && ii != 0
                    && medi.getMessdatenDaten().kann(m.getDate(), zwangdate, zwanganz)) {
                al.add(medi);
            }
        }
        Collections.shuffle(al);
        al.sort(Messdiener.einteilen);
        return al;
    }

    private enum EnumAction {
        EINFACH_EINTEILEN, TYPE_BEACHTEN
    }
}
