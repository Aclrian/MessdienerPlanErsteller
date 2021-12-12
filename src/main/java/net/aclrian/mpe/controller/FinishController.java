package net.aclrian.mpe.controller;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Window;
import javafx.util.Pair;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import net.aclrian.mpe.utils.RemoveDoppelte;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.jodconverter.local.office.LocalOfficeUtils;
import org.springframework.web.util.UriUtils;

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

    public static final String NICHT_EINGETEILTE_MESSDIENER = "Nicht eingeteilte Messdiener";

    private final String fertig;
    private final MainController.EnumPane pane;
    private final List<Messe> messen;
    private final List<Messdiener> hauptarray;
    private final ArrayList<Messdiener> nichtEingeteile;
    private boolean locked = true;
    private String titel;
    private File pdfgen;
    private File wordgen;
    @FXML
    private HTMLEditor editor;
    @FXML
    private Button mail;
    @FXML
    private Button word;
    @FXML
    private Button nichteingeteilte;
    @FXML
    private Button zurueck;

    public FinishController(MainController.EnumPane old, List<Messe> messen) {
        this.pane = old;
        this.hauptarray = DateienVerwalter.getInstance().getMessdiener();
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
        nichtEingeteile = new ArrayList<>();
        for (Messdiener medi : hauptarray) {
            if (medi.getMessdaten().getInsgesamtEingeteilt() == 0) {
                nichtEingeteile.add(medi);
            }
        }
        nichtEingeteile.sort(Messdiener.compForMedis);

        final String office_home = System.getenv("OFFICE_HOME");
        if (office_home != null && !office_home.isEmpty()) {
            Log.getLogger().info("alternatives OfficeHome gefunden bei: {}", office_home);
            System.setProperty("office.home", office_home);
        }
    }

    @Override
    public void initialize() {
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void afterstartup(Window window, MainController mc) {
        nichteingeteilte.setOnAction(event -> nichteingeteilte());
        editor.setHtmlText(fertig);
        zurueck.setOnAction(p -> zurueck(mc));
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    private void zurueck(MainController mc) {
        Dialogs.YesNoCancelEnum delete = Dialogs.getDialogs().yesNoCancel("Ja", "Nein", "Hier bleiben",
                "Soll die aktuelle Einteilung gelöscht werden, um einen neuen Plan mit ggf. geänderten Messen generieren zu können?");
        if (delete.equals(Dialogs.YesNoCancelEnum.CANCEL)) {
            return;
        }
        if (delete.equals(Dialogs.YesNoCancelEnum.YES)) {
            DateienVerwalter.getInstance().getMessdiener()
                    .forEach(m -> m.getMessdaten().nullen());
            for (Messe m : messen) {
                m.nullen();
            }
        }

        locked = false;
        mc.setMesse(messen, pane);
    }

    private void nichteingeteilte() {
        Dialogs.getDialogs().show(nichtEingeteile, NICHT_EINGETEILTE_MESSDIENER);
    }

    public List<Messdiener> getNichtEingeteile() {
        return nichtEingeteile;
    }

    @FXML
    public void toEMAIL() {
        Pair<List<Messdiener>, StringBuilder> pair = getResourcesForEmail();
        try {
            URI uri = new URI(pair.getValue().toString());
            Desktop.getDesktop().mail(uri);
            Log.getLogger().info(pair.getValue());
            if (!pair.getKey().isEmpty()) {
                Dialogs.getDialogs().show(pair.getKey(), "Messdiener ohne E-Mail-Addresse:");
            }
        } catch (IOException | URISyntaxException | IllegalArgumentException e) {
            Dialogs.getDialogs().error(e, "Konnte den mailto-Link (" + URLEncoder.encode(pair.getValue().toString(), StandardCharsets.UTF_8)
                    + ") nicht öffnen");
        }
    }

    public Pair<List<Messdiener>, StringBuilder> getResourcesForEmail() {
        List<Messdiener> medis = DateienVerwalter.getInstance().getMessdiener();
        StringBuilder sb = new StringBuilder("mailto:?");
        ArrayList<Messdiener> noemail = new ArrayList<>();
        for (Messdiener medi : medis) {
            if (medi.getEmail().equals("")) {
                noemail.add(medi);
            } else {
                sb.append("bcc=").append(medi.getEmail()).append("&");
            }
        }
        sb.append("subject=").append(UriUtils.encode(titel, StandardCharsets.UTF_8));
        StringBuilder sbb = new StringBuilder();
        StringBuilder ssb = new StringBuilder();
        if (pdfgen != null && pdfgen.exists()) {
            sbb.append("Als Anhang hinzufügen: ").append(pdfgen.getAbsolutePath()).append(" ?");
        }
        if (wordgen != null && wordgen.exists()) {
            ssb.append("Als Anhang hinzufügen: ").append(wordgen.getAbsolutePath()).append(" ?");
        }
        sb.append("&body=").append(UriUtils.encode(sbb.toString(), StandardCharsets.UTF_8))
                .append("%0D%0A")
                .append(UriUtils.encode(ssb.toString(), StandardCharsets.UTF_8));
        return new Pair<>(noemail, sb);
    }

    @FXML
    public void toPDF(ActionEvent actionEvent) {
        if (LocalOfficeUtils.getDefaultOfficeHome() != null) {
            Log.getLogger().info("Converting HTML to PDF with JODConverter");
            convert(actionEvent, false);
        }
        Log.getLogger().info("Converting HTML to PDF with iText");
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setCharset("UTF-8");
        try {
            File out = new File(Log.getWorkingDir().getAbsolutePath(), titel + ".pdf");
            HtmlConverter.convertToPdf(new ByteArrayInputStream(editor.getHtmlText().replace("<p></p>", "<br>").replace("\u2003", "    ").getBytes(StandardCharsets.UTF_8)),
                    new FileOutputStream(out), converterProperties);
            pdfgen = out;
            if (actionEvent != null) {
                Desktop.getDesktop().open(out);
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Messdienerplan nicht zu PDF konvertieren.");
        }
    }

    private void convert(ActionEvent actionEvent, boolean isToDocx) {
        String fileEnd = ".pdf";
        DocumentFormat format = DefaultDocumentFormatRegistry.HTML;
        if (isToDocx) {
            fileEnd = ".docx";
            format = DefaultDocumentFormatRegistry.DOCX;
        }
        final LocalOfficeManager officeManager = LocalOfficeManager.install();
        File out = new File(Log.getWorkingDir().getAbsolutePath(), titel + fileEnd);
        try {
            officeManager.start();
        } catch (OfficeException e) {
            Dialogs.getDialogs().error(e, "Fehler beim Konvertieren");
            return;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(editor.getHtmlText().replace("<p></p>", "")
                .replace("</br>", "")
                .replace("<br>", "<br></br>")
                .replace("</p><p><font></font></p><p><font><b>", "</p><br/><p><font></font></p><p><font><b>")
                .replace("\u2003", "    ").getBytes(StandardCharsets.UTF_8))){
            JodConverter.convert(bais).as(format).to(out).execute();
            if (isToDocx) {
                wordgen = out;
            } else {
                pdfgen = out;
            }
            if (actionEvent != null) {
                Desktop.getDesktop().open(out);
            }
        } catch (Exception e) {
            Dialogs.getDialogs().error(e, "Konnte den Messdienerplan nicht zu " + fileEnd + " konvertieren.");
        } finally {
            OfficeUtils.stopQuietly(officeManager);
        }
    }

    @FXML
    public void toWORD(ActionEvent actionEvent) {
        if (LocalOfficeUtils.getDefaultOfficeHome() == null) {
            Dialogs.getDialogs().info("Für die Konvertierung wird LibreOffice (oder Openoffice) benötigt.", "Wenn es trotz Installation nicht erkannt wird, kann dafür eine Systemvariable OFFICE_HOME angelegt werden, die den Installationspfad von der Officeanwendung enthält.");
            return;
        }
        Log.getLogger().info("Converting HTML to PDF with JODConverter");
        convert(actionEvent, true);
    }

    private void neuerAlgorythmus() {
        hauptarray.sort(Messdiener.einteilen);
        if (messen.isEmpty()) {
            return;
        }
        // neuer Monat:
        Calendar start = Calendar.getInstance();
        start.setTime(messen.get(0).getDate());
        start.add(Calendar.MONTH, 1);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        if (Log.getLogger().isDebugEnabled()) {
            Log.getLogger().info("nächster Monat bei: {}", df.format(start.getTime()));
        }
        // EIGENTLICHER ALGORYTHMUS
        for (Messe me : messen) {
            if (me.getDate().after(start.getTime())) {
                start.add(Calendar.MONTH, 1);
                if (Log.getLogger().isDebugEnabled()) {
                    Log.getLogger().info("nächster Monat: Es ist {}", df.format(me.getDate()));
                }
                for (Messdiener messdiener : hauptarray) {
                    messdiener.getMessdaten().naechsterMonat();
                }
            }
            Log.getLogger().info("Messe dran: {}", me.getID());
            if (me.getStandardMesse() instanceof Sonstiges) {
                this.einteilen(me, EnumAction.EINFACH_EINTEILEN);
            } else {
                this.einteilen(me, EnumAction.TYPE_BEACHTEN);
            }
            Log.getLogger().info("Messe fertig: {}", me.getID());
        }
    }

    private void einteilen(Messe m, EnumAction act) {
        List<Messdiener> medis;
        if (act == EnumAction.EINFACH_EINTEILEN) {
            medis = get(new Sonstiges(), m, false, false);
        } else {//EnumAction.TYPE_BEACHTEN
            medis = get(m.getStandardMesse(), m, false, false);
        }
        if (Log.getLogger().isDebugEnabled()) {
            Log.getLogger().info("{} für {}", medis.size(), m.getNochBenoetigte());
        }
        for (Messdiener medi : medis) {
            einteilen(m, medi, false, false);
        }
        if (!m.istFertig()) {
            zwang(m);
        }
    }

    public void zwang(Messe m, boolean zangDate, boolean zwangAnz, String loggerOutput) {
        List<Messdiener> medis = get(m.getStandardMesse(), m, zangDate, zwangAnz);
        if (Log.getLogger().isDebugEnabled()) {
            Log.getLogger().warn("{} {}", m, loggerOutput);
        }
        for (Messdiener medi : medis) {
            einteilen(m, medi, false, true);
        }
    }

    private void zwang(Messe m) {
        if (m.istFertig())
            return;
        final String start = "Die Messe ";
        String secondPart = " hat zu wenige Messdiener.\nNoch ";

        if (!(m.getStandardMesse() instanceof Sonstiges)) {
            if (Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                    + " werden benötigt.\nSollen Messdiener eingeteilt werden, die standartmäßig die Messe \n'"
                    + m.getStandardMesse().tokurzerBenutzerfreundlichenString()
                    + "' dienen können, aber deren Anzahl schon zu hoch ist?")) {
                zwang(m, false, true, " einteilen ohne Anzahl beachten");
            }
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy");
            if (Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                    + " werden benötigt.\nSollen Messdiener eingeteilt werden, die an dem Tag \n'"
                    + df.format(m.getDate()) + "' dienen können?")) {
                zwang(m, false, false, " einteilen ohne Standardmesse beachten");
            }
        }
        if (m.istFertig()) {
            return;
        }
        if (Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                + " werden benötigt.\nSollen Messdiener zwangsweise eingeteilt werden?")) {
            zwang(m, true, true, " einteilen ohne Standardmesse beachten");
        }
        zuwenige(m);
    }

    private void zuwenige(Messe m) {
        if (!m.istFertig())
            Dialogs.getDialogs().error("Die Messe" + m.getID().replace("\t", "   ") + " wird zu wenige Messdiener haben.");
    }

    private void einteilen(Messe m, Messdiener medi, boolean zwangdate, boolean zwanganz) {
        boolean d;
        if (m.istFertig()) {
            return;
        } else {
            d = m.einteilen(medi, zwangdate, zwanganz);
        }
        if (!m.istFertig() && d) {
            List<Messdiener> anv = medi.getMessdaten()
                    .getAnvertraute(DateienVerwalter.getInstance().getMessdiener());
            RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
            anv = rd.removeDuplicatedEntries(anv);
            if (!anv.isEmpty()) {
                anv.sort(Messdiener.einteilen);
                for (Messdiener messdiener : anv) {
                    boolean b = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse());
                    if (messdiener.getMessdaten().kann(m.getDate(), zwangdate, zwanganz) && b) {
                        Log.getLogger().info("{} dient mit {}?", messdiener, medi);
                        einteilen(m, messdiener, zwangdate, zwanganz);
                    }
                }
            }
        }
    }

    public List<Messdiener> get(StandartMesse sm, Messe m, boolean zwangdate, boolean zwanganz) {
        ArrayList<Messdiener> allForSMesse = new ArrayList<>();
        for (Messdiener medi : hauptarray) {
            int id = medi.istLeiter() ? 1 : 0;
            if (medi.getDienverhalten().getBestimmtes(sm)
                    && DateienVerwalter.getInstance().getPfarrei().getSettings().getDaten(id).getAnzDienen() != 0
                    && medi.getMessdaten().kann(m.getDate(), zwangdate, zwanganz)) {
                allForSMesse.add(medi);
            }
        }
        Collections.shuffle(allForSMesse);
        allForSMesse.sort(Messdiener.einteilen);
        return allForSMesse;
    }

    public String getTitle() {
        return titel;
    }

    private enum EnumAction {
        EINFACH_EINTEILEN, TYPE_BEACHTEN
    }
}
