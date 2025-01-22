package net.aclrian.mpe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Window;
import javafx.util.Pair;
import net.aclrian.mpe.algorithms.Einteilung;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.*;
import net.aclrian.mpe.utils.export.WORDExport;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        StringBuilder s = new StringBuilder("<html lang=\"de\"><head>PLACEHOLDER")
                .append("<style>body{font-family: sans-serif;}</style>")
                .append("</head></body>");
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
        s.replace(22, 33, "<title>" + titel + "</title>");
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
            DesktopWrapper.mail(uri);
            MPELog.getLogger().info(pair.getValue());
            if (!pair.getKey().isEmpty()) {
                Dialogs.getDialogs().show(pair.getKey(), "Messdiener ohne E-Mail-Addresse:");
            }
        } catch (URISyntaxException | IllegalArgumentException e) {
            Dialogs.getDialogs().error(e, "Konnte den mailto-Link (" + URLEncoder.encode(pair.getValue().toString(), StandardCharsets.UTF_8)
                    + ") nicht öffnen");
        }
    }

    public Pair<List<Messdiener>, StringBuilder> getResourcesForEmail() {
        List<Messdiener> medis = DateienVerwalter.getInstance().getMessdiener();
        StringBuilder sb = new StringBuilder("mailto:?");
        ArrayList<Messdiener> noemail = new ArrayList<>();
        for (Messdiener medi : medis) {
            if (medi.getEmail().toString().isEmpty()) {
                noemail.add(medi);
            } else {
                sb.append("bcc=").append(medi.getEmail().toString()).append("&");
            }
        }
        sb.append("subject=").append(URLEncoder.encode(titel, StandardCharsets.UTF_8)
                .replace("+", "%20") // quickfix: I'll not fix this properly
        );
        sb.append("&body=%0D%0A");
        return new Pair<>(noemail, sb);
    }

    @FXML
    public void openHTML(ActionEvent actionEvent) {
        Path htmlFile = Path.of(System.getProperty("java.io.tmpdir"), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".html");
        try {
            Files.writeString(htmlFile, editor.getHtmlText());
            if (actionEvent != null) {
                DesktopWrapper.open(htmlFile.toFile());
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte die HTML-Datei nicht erstellen oder öffnen");
        }
    }

    @FXML
    public void sendToWORD(ActionEvent actionEvent) {
        WORDExport wordExporter = new WORDExport(editor.getHtmlText(), titel);
        wordExporter.generateFile();
        if (!wordExporter.openFile() && actionEvent != null) {
            Dialogs.getDialogs().error("Konnte den Messdienerplan nicht zu DOCX konvertieren.");
        }
    }

    public String getTitle() {
        return titel;
    }
}
