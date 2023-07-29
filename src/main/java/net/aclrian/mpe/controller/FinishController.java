package net.aclrian.mpe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Window;
import javafx.util.Pair;
import net.aclrian.mpe.einteilung.Einteilung;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.*;
import net.aclrian.mpe.utils.export.PDFExport;
import net.aclrian.mpe.utils.export.WORDExport;
import org.springframework.web.util.UriUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class FinishController implements Controller {

    public static final String NICHT_EINGETEILTE_MESSDIENER = "Nicht eingeteilte Messdiener";

    private final String fertig;
    private final MainController.EnumPane pane;
    private final List<Messe> messen;
    private final List<Messdiener> nichtEingeteile;
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
        List<Messdiener> messdiener = DateienVerwalter.getInstance().getMessdiener();
        this.messen = messen;
        Einteilung einteilung = new Einteilung(messdiener, messen);
        einteilung.einteilen();
        StringBuilder s = new StringBuilder("<html>");
        for (int i = 0; i < messen.size(); i++) {
            Messe messe = messen.get(i);
            String m1 = messe.htmlAusgeben();
            m1 = m1.substring(6, m1.length() - 7);
            if (i == 0) {
                LocalDateTime start = messe.getDate();
                LocalDateTime ende = messen.get(messen.size() - 1).getDate();

                titel = "Messdienerplan vom " + DateUtil.DATE_SHORT.format(start) + " bis " + DateUtil.DATE_SHORT.format(ende);
                s.append("<h1>").append(titel).append("</h1>");
            }
            s.append("<p>").append(m1).append("</p>");
        }
        s.append("</html>");
        fertig = s.toString();
        nichtEingeteile = new ArrayList<>();
        for (Messdiener medi : messdiener) {
            if (medi.getMessdaten().getInsgesamtEingeteilt() == 0) {
                nichtEingeteile.add(medi);
            }
        }
        nichtEingeteile.sort(Messdiener.PERSON_COMPARATOR);

        final String officeHome = System.getenv("OFFICE_HOME");
        if (officeHome != null && !officeHome.isEmpty()) {
            MPELog.getLogger().info("alternatives OfficeHome gefunden bei: {}", officeHome);
            System.setProperty("office.home", officeHome);
        }
    }

    @Override
    public void initialize() {
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void afterStartup(Window window, MainController mc) {
        nichteingeteilte.setOnAction(event -> openNichtEingeteilteMessdiener());
        editor.setHtmlText(fertig);
        zurueck.setOnAction(p -> zurueck(mc));
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    private void zurueck(MainController mc) {
        Dialogs.YesNoCancelEnum delete = Dialogs.getDialogs().yesNoCancel("Hier bleiben",
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

    private void openNichtEingeteilteMessdiener() {
        Dialogs.getDialogs().show(nichtEingeteile, NICHT_EINGETEILTE_MESSDIENER);
    }

    public List<Messdiener> getNichtEingeteilteMessdiener() {
        return nichtEingeteile;
    }

    @FXML
    public void sendToEmail() {
        Pair<List<Messdiener>, StringBuilder> pair = getResourcesForEmail();
        try {
            URI uri = new URI(pair.getValue().toString());
            Desktop.getDesktop().mail(uri);
            MPELog.getLogger().info(pair.getValue());
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
            if (medi.getEmail().toString().equals("")) {
                noemail.add(medi);
            } else {
                sb.append("bcc=").append(medi.getEmail().toString()).append("&");
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
    public void sendToPDF(ActionEvent actionEvent) {
        try {
            pdfgen = new PDFExport(editor.getHtmlText(), titel).generateFile();
            if (actionEvent != null) {
                Desktop.getDesktop().open(pdfgen);
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Messdienerplan nicht zu PDF konvertieren.");
        }
    }

    @FXML
    public void sendToWORD(ActionEvent actionEvent) {
        try {
            wordgen = new WORDExport(editor.getHtmlText(), titel).generateFile();
            if (actionEvent != null) {
                Desktop.getDesktop().open(pdfgen);
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Messdienerplan nicht zu DOCX konvertieren.");
        }
    }

    public String getTitle() {
        return titel;
    }
}
