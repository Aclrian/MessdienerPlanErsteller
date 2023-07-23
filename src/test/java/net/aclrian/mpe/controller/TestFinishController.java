package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateUtil;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.util.UriUtils;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TestFinishController extends ApplicationTest {

    @Mock
    MainController mc;
    @Mock
    DateienVerwalter dv;
    @Mock
    Messdiener m1;
    @Mock
    Messdiener m2;
    @Mock
    Messdiener m3;
    @Mock
    Einstellungen einst = new Einstellungen();
    @Mock
    Dialogs dialogs;
    @Mock
    Pfarrei pf;

    private Pane pane;
    private FinishController instance;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.show();
        dialogs = Mockito.mock(Dialogs.class);
        Dialogs.setDialogs(dialogs);
        pf = Mockito.mock(Pfarrei.class);
        mc = Mockito.mock(MainController.class);
        dv = Mockito.mock(DateienVerwalter.class);
        m1 = Mockito.mock(Messdiener.class);
        m2 = Mockito.mock(Messdiener.class);
        m3 = Mockito.mock(Messdiener.class);
        DateienVerwalter.setInstance(dv);
    }

    @SuppressWarnings("unchecked")
    @Test
    void cleanTest() throws Messdaten.CouldFindMessdiener {
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, m2, m3));
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(pf.getSettings()).thenReturn(einst);
        einst.editMaxDienen(false, 10);

        Mockito.when(m1.toString()).thenReturn("m1");
        Mockito.when(m2.toString()).thenReturn("m2");
        Mockito.when(m3.toString()).thenReturn("m3");
        Mockito.when(m1.istLeiter()).thenReturn(false);
        Mockito.when(m2.istLeiter()).thenReturn(false);
        Mockito.when(m3.istLeiter()).thenReturn(false);

        Mockito.when(m1.getGeschwister()).thenReturn(new String[0]);
        Mockito.when(m2.getGeschwister()).thenReturn(new String[0]);
        Mockito.when(m3.getGeschwister()).thenReturn(new String[0]);

        Mockito.when(m1.getFreunde()).thenReturn(new String[0]);
        Mockito.when(m2.getFreunde()).thenReturn(new String[0]);
        Mockito.when(m3.getFreunde()).thenReturn(new String[0]);

        Mockito.when(m1.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Mockito.when(m1.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Mockito.when(m1.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Messdaten md1 = new Messdaten(m1);
        Mockito.when(m1.getMessdaten()).thenReturn(md1);
        Messdaten md2 = new Messdaten(m2);
        Mockito.when(m2.getMessdaten()).thenReturn(md2);
        Messdaten md3 = new Messdaten(m3);
        Mockito.when(m3.getMessdaten()).thenReturn(md3);

        Mockito.doCallRealMethod().when(dialogs).show(Mockito.anyList(), Mockito.eq(FinishController.NICHT_EINGETEILTE_MESSDIENER));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.PLAN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                instance = new FinishController(null, Collections.emptyList());
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        LocalDate date = DateUtil.getToday();
        m1.getMessdaten().einteilenVorzeitig(date, false);


        Platform.runLater(() -> Mockito.when(dialogs.yesNoCancel(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Dialogs.YesNoCancelEnum.CANCEL, Dialogs.YesNoCancelEnum.YES));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            if (pane.lookup("#zurueck") instanceof Button zurueck) {
                zurueck.fire();
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(m1.getMessdaten().kanndann(date, false)).isFalse();
        Platform.runLater(() -> {
            if (pane.lookup("#zurueck") instanceof Button zurueck) {
                zurueck.fire();
            } else {
                Assertions.fail("Could not find zurueck");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        for (Messdiener m : dv.getMessdiener()) {
            if (m.getMessdaten().getInsgesamtEingeteilt() != 0 || m.getMessdaten().getAnzMessen() != 0) {
                Assertions.fail("Not all Messdaten are zero");
            }
        }
        assertThat(m1.getMessdaten().kanndann(date, false)).isTrue();
        assertThat(instance.getNichtEingeteilteMessdiener()).contains(m1);
        assertThat(instance.getNichtEingeteilteMessdiener()).contains(m2);
        assertThat(instance.getNichtEingeteilteMessdiener()).contains(m3);

        Platform.runLater(() -> {
            if (pane.lookup("#nichteingeteilte") instanceof Button nichteingeteilte) {
                nichteingeteilte.fire();
            } else {
                Assertions.fail("Could not find nichteingeteilte");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();


        Platform.runLater(() -> {
            assertThat(listWindows()).hasSize(2);
            Window w = listWindows().get(1);
            assert w != null;
            Node list = w.getScene().lookup("#list");
            if (list instanceof ListView) {
                assertThat(((ListView<Messdiener>) list).getItems()).contains(m1, m2, m3);
            } else {
                Assertions.fail("Could not find ListView from Dialogs.show");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testEinteilungWithoutDOCX() throws Messdaten.CouldFindMessdiener {
        testEinteilung(false);
    }

    @Disabled("Run Test with conversion to docx")
    @Test
    void testEinteilungWithDOCX() throws Messdaten.CouldFindMessdiener {
        testEinteilung(true);
    }

    private void testEinteilung(boolean skipDOCX) throws Messdaten.CouldFindMessdiener {
        Messdiener m1Freund = Mockito.mock(Messdiener.class);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, m2, m3, m1Freund));
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        StandardMesse standardMesse = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "o1", 2, "t1");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standardMesse));
        Mockito.when(pf.getSettings()).thenReturn(einst);
        einst.editMaxDienen(false, 10);
        einst.editMaxDienen(true, 10);
        einst.editiereYear(0, 9);

        Mockito.when(m1.toString()).thenReturn("m1");
        Mockito.when(m2.toString()).thenReturn("m2");
        Mockito.when(m3.toString()).thenReturn("m3");
        final String m1FreundString = "dfc";
        Mockito.when(m1Freund.toString()).thenReturn(m1FreundString);
        Mockito.when(m1Freund.istLeiter()).thenReturn(false);
        Mockito.when(m1.istLeiter()).thenReturn(false);
        Mockito.when(m2.istLeiter()).thenReturn(true);
        Mockito.when(m3.istLeiter()).thenReturn(false);

        Mockito.when(m1.getEmail()).thenReturn("");
        Mockito.when(m2.getEmail()).thenReturn("");
        Mockito.when(m3.getEmail()).thenReturn("a@w.de");
        Mockito.when(m1Freund.getEmail()).thenReturn("");

        Mockito.when(m1.getGeschwister()).thenReturn(new String[0]);
        Mockito.when(m2.getGeschwister()).thenReturn(new String[0]);
        Mockito.when(m3.getGeschwister()).thenReturn(new String[0]);
        Mockito.when(m1Freund.getGeschwister()).thenReturn(new String[0]);

        Mockito.when(m1.getFreunde()).thenReturn(new String[]{m1FreundString});
        Mockito.when(m2.getFreunde()).thenReturn(new String[0]);
        Mockito.when(m3.getFreunde()).thenReturn(new String[0]);
        Mockito.when(m1Freund.getFreunde()).thenReturn(new String[]{"m1"});

        Mockito.when(m1.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Mockito.when(m2.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Mockito.when(m3.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Mockito.when(m1Freund.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Messdaten md1 = new Messdaten(m1);
        Mockito.when(m1.getMessdaten()).thenReturn(md1);
        Messdaten md2 = new Messdaten(m2);
        Mockito.when(m2.getMessdaten()).thenReturn(md2);
        Messdaten md3 = new Messdaten(m3);
        Mockito.when(m3.getMessdaten()).thenReturn(md3);
        Messdaten md1F = new Messdaten(m1Freund);
        Mockito.when(m1Freund.getMessdaten()).thenReturn(md1F);
        Messverhalten mv1 = new Messverhalten();
        Messverhalten mv2 = new Messverhalten();
        Messverhalten mv3 = new Messverhalten();
        Messverhalten mv1F = new Messverhalten();

        mv1.editiereBestimmteMesse(standardMesse, true);
        md1.austeilen(DateUtil.getYesterdaysYesterday());
        mv1F.editiereBestimmteMesse(standardMesse, true);
        md1F.austeilen(DateUtil.getYesterdaysYesterday());

        Mockito.when(m1.getDienverhalten()).thenReturn(mv1);
        Mockito.when(m2.getDienverhalten()).thenReturn(mv2);
        Mockito.when(m3.getDienverhalten()).thenReturn(mv3);
        Mockito.when(m1Freund.getDienverhalten()).thenReturn(mv1F);

        Messe me1 = new Messe(false, 1, DateUtil.getYesterdaysYesterday().atTime(0, 0), "o1", "t1");
        Messe me2 = new Messe(DateUtil.getToday().atTime(0, 0), standardMesse);
        Messe me3 = new Messe(false, 1, DateUtil.getTomorrow().atTime(0, 0), "o3", "t3");

        Mockito.doCallRealMethod().when(dialogs).show(Mockito.anyList(), Mockito.eq(FinishController.NICHT_EINGETEILTE_MESSDIENER));

        assertThat(m1.getMessdaten().kann(me1.getDate(), false, false)).isFalse();
        assertThat(m1Freund.getMessdaten().kann(me1.getDate(), false, false)).isFalse();
        assertThat(m1.getMessdaten().kann(me2.getDate(), false, false)).isTrue();
        assertThat(m1Freund.getMessdaten().kann(me2.getDate(), false, false)).isTrue();
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.PLAN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                List<Messe> messes = Arrays.asList(me1, me2, me3);
                instance = new FinishController(null, messes);
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(me1.istFertig() && me2.istFertig() && me3.istFertig()).isTrue();
        assertThat(!me1.getEingeteilte().contains(m1) && !me1.getEingeteilte().contains(m1Freund)).isTrue();
        assertThat(!me3.getEingeteilte().contains(m1) && !me3.getEingeteilte().contains(m1Freund)).isTrue();
        assertThat(md1.kanndann(DateUtil.getToday(), false)).isFalse();
        assertThat(md1F.kanndann(DateUtil.getToday(), false)).isFalse();
        assertThat(me2.getEingeteilte().contains(m1) && me2.getEingeteilte().contains(m1)).isTrue();

        File out = new File(MPELog.getWorkingDir().getAbsolutePath(), instance.getTitle() + ".pdf");
        try {
            if (out.exists()) {
                Files.delete(out.toPath());
            }
            instance.toPDF(null);
            assertThat(out).exists();
            Files.delete(out.toPath());
        } catch (IOException e) {
            MPELog.getLogger().error(e.getMessage(), e);
            Assertions.fail(e.getMessage(), e);
        }
        if (skipDOCX) {
            out = new File(MPELog.getWorkingDir(), instance.getTitle() + ".docx");
            try {
                if (out.exists()) {
                    Files.delete(out.toPath());
                }
                instance.toWORD(null);
                assertThat(out).exists();
                Files.delete(out.toPath());
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
                Assertions.fail(e.getMessage(), e);
            }
        }

        Platform.runLater(() -> Mockito.when(dialogs.yesNoCancel(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Dialogs.YesNoCancelEnum.YES));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            if (pane.lookup("#zurueck") instanceof Button zurueck) {
                zurueck.fire();
            } else {
                Assertions.fail("Could not find zurueck");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        for (Messdiener m : dv.getMessdiener()) {
            if (m.getMessdaten().getInsgesamtEingeteilt() != 0 || m.getMessdaten().getAnzMessen() != 0) {
                Assertions.fail("Not all Messdaten are zero");
            }
        }
        assertThat(me1.getEingeteilte()).isEmpty();
        assertThat(me2.getEingeteilte()).isEmpty();
        assertThat(me3.getEingeteilte()).isEmpty();

        String from = String.format("%02d", DateUtil.getYesterdaysYesterday().getDayOfMonth());
        String to = String.format("%02d", DateUtil.getTomorrow().getDayOfMonth());
        String fromMonth = DateUtil.getYesterdaysYesterday().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String toMonth = DateUtil.getTomorrow().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        Pair<List<Messdiener>, StringBuilder> pair = instance.getResourcesForEmail();
        assertThat(pair.getValue()).hasToString("mailto:?bcc=a@w.de&subject=Messdienerplan%20vom%20" + from + ".%20" + UriUtils.encode(fromMonth, StandardCharsets.UTF_8) + "%20bis%20" + to + ".%20" + UriUtils.encode(toMonth, StandardCharsets.UTF_8) + "&body=%0D%0A");
        try {
            new URI(pair.getValue().toString());
        } catch (URISyntaxException e) {
            Assertions.fail(e.getMessage(), e);
        }

        assertThat(pair.getKey()).hasSize(3).containsAll(Arrays.asList(m1, m2, m1Freund));
    }
}
