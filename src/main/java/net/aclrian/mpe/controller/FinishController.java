package net.aclrian.mpe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Window;
import javafx.util.Pair;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

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
        for (Messdiener medi : hauptarray) {
            if (medi.getMessdaten().getInsgesamtEingeteilt() == 0) {
                nichtEingeteile.add(medi);
            }
        }
        nichtEingeteile.sort(Messdiener.MESSDIENER_COMPARATOR);

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

    private void neuerAlgorythmus() {
        hauptarray.sort(Messdiener.MESSDIENER_EINTEILEN_COMPARATOR);
        if (messen.isEmpty()) {
            return;
        }
        // neuer Monat:
        LocalDate nextMonth = messen.get(0).getDate().toLocalDate();
        nextMonth = nextMonth.plusMonths(1);
        if (MPELog.getLogger().isDebugEnabled()) {
            MPELog.getLogger().info("nächster Monat bei: {}", DateUtil.DATE.format(nextMonth));
        }
        // EIGENTLICHER ALGORYTHMUS
        for (Messe me : messen) {
            if (me.getDate().toLocalDate().isAfter(nextMonth)) {
                nextMonth = nextMonth.plusMonths(1);
                if (MPELog.getLogger().isDebugEnabled()) {
                    MPELog.getLogger().info("nächster Monat: Es ist {}", DateUtil.DATE.format(me.getDate().toLocalDate()));
                }
                for (Messdiener messdiener : hauptarray) {
                    messdiener.getMessdaten().naechsterMonat();
                }
            }
            MPELog.getLogger().info("Messe dran: {}", me.getID());
            if (me.getStandardMesse() instanceof Sonstiges) {
                this.einteilen(me, EnumAction.EINFACH_EINTEILEN);
            } else {
                this.einteilen(me, EnumAction.TYPE_BEACHTEN);
            }
            MPELog.getLogger().info("Messe fertig: {}", me.getID());
        }
    }

    private void einteilen(Messe m, EnumAction act) {
        List<Messdiener> medis;
        if (act == EnumAction.EINFACH_EINTEILEN) {
            medis = get(new Sonstiges(), m, false, false);
        } else {//EnumAction.TYPE_BEACHTEN
            medis = get(m.getStandardMesse(), m, false, false);
        }
        if (MPELog.getLogger().isDebugEnabled()) {
            MPELog.getLogger().info("{} für {}", medis.size(), m.getNochBenoetigte());
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
        if (MPELog.getLogger().isDebugEnabled()) {
            MPELog.getLogger().warn("{} {}", m, loggerOutput);
        }
        for (Messdiener medi : medis) {
            einteilen(m, medi, false, true);
        }
    }

    private void zwang(Messe m) {
        if (m.istFertig()) {
            return;
        }
        final String start = "Die Messe ";
        String secondPart = " hat zu wenige Messdiener.\nNoch ";

        if (!(m.getStandardMesse() instanceof Sonstiges)) {
            if (Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                    + " werden benötigt.\nSollen Messdiener eingeteilt werden, die standardmäßig die Messe \n'"
                    + m.getStandardMesse().toKurzerBenutzerfreundlichenString()
                    + "' dienen können, aber deren Anzahl schon zu hoch ist?")) {
                zwang(m, false, true, " einteilen ohne Anzahl beachten");
            }

            if (Dialogs.getDialogs().frage(start + m.getID().replace("\t", "   ") + secondPart + m.getNochBenoetigte()
                    + " werden benötigt.\nSollen Messdiener eingeteilt werden, die an dem Tag \n'"
                    + DateUtil.DATE.format(m.getDate()) + "' dienen können?")) {
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
        zuWenige(m);
    }

    private void zuWenige(Messe m) {
        if (!m.istFertig()) {
            Dialogs.getDialogs().error("Die Messe" + m.getID().replace("\t", "   ") + " wird zu wenige Messdiener haben.");
        }
    }

    private void einteilen(Messe m, Messdiener medi, boolean zwangdate, boolean zwanganz) {
        boolean kannStandardMesse = false;
        if (!m.istFertig()) {
            kannStandardMesse = m.einteilen(medi, zwangdate, zwanganz);
        }
        if (!m.istFertig() && kannStandardMesse) {
            List<Messdiener> anv = medi.getMessdaten()
                    .getAnvertraute(DateienVerwalter.getInstance().getMessdiener());
            RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
            anv = rd.removeDuplicatedEntries(anv);
            anv.sort(Messdiener.MESSDIENER_EINTEILEN_COMPARATOR);
            for (Messdiener messdiener : anv) {
                if (m.istFertig()) {
                    break;
                }
                kannStandardMesse = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse());
                if (messdiener.getMessdaten().kann(m.getDate().toLocalDate(), zwangdate, zwanganz) && kannStandardMesse) {
                    MPELog.getLogger().info("{} dient mit {}?", messdiener, medi);
                    einteilen(m, messdiener, zwangdate, zwanganz);
                }
            }
        }
    }

    public List<Messdiener> get(StandardMesse sm, Messe m, boolean zwangdate, boolean zwanganz) {
        ArrayList<Messdiener> allForSMesse = new ArrayList<>();
        for (Messdiener medi : hauptarray) {
            int id = medi.istLeiter() ? 1 : 0;
            if (medi.getDienverhalten().getBestimmtes(sm)
                    && DateienVerwalter.getInstance().getPfarrei().getSettings().getDaten(id).anzahlDienen() != 0
                    && medi.getMessdaten().kann(m.getDate().toLocalDate(), zwangdate, zwanganz)) {
                allForSMesse.add(medi);
            }
        }
        Collections.shuffle(allForSMesse, new Random(System.nanoTime()));
        Arrays.sort(allForSMesse.toArray());
        allForSMesse.sort(Messdiener.MESSDIENER_EINTEILEN_COMPARATOR);
        return allForSMesse;
    }

    public String getTitle() {
        return titel;
    }

    private enum EnumAction {
        EINFACH_EINTEILEN, TYPE_BEACHTEN
    }
}
