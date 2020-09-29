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
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TestFinishController extends ApplicationTest {

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
    public void cleanTest() {
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, m2, m3));
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(pf.getSettings()).thenReturn(einst);
        einst.editMaxDienen(false, 10);

        Mockito.when(m1.makeId()).thenReturn("m1");
        Mockito.when(m2.makeId()).thenReturn("m2");
        Mockito.when(m3.makeId()).thenReturn("m3");
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
        Mockito.when(m1.getMessdatenDaten()).thenReturn(md1);
        Messdaten md2 = new Messdaten(m2);
        Mockito.when(m2.getMessdatenDaten()).thenReturn(md2);
        Messdaten md3 = new Messdaten(m3);
        Mockito.when(m3.getMessdatenDaten()).thenReturn(md3);

        Mockito.doCallRealMethod().when(dialogs).show(Mockito.anyList(), Mockito.eq(FinishController.NICHT_EINGETEILTE_MESSDIENER));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.PLAN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                instance = new FinishController(null, Collections.emptyList());
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterstartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        Date date = TestFerienplanController.getToday();
        m1.getMessdatenDaten().einteilenVorzeitig(date, false);


        Platform.runLater(() -> Mockito.when(dialogs.yesNoCancel(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Dialogs.YesNoCancelEnum.CANCEL, Dialogs.YesNoCancelEnum.YES));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            if (pane.lookup("#zurueck") instanceof Button) {
                Button zurueck = (Button) pane.lookup("#zurueck");
                zurueck.fire();
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(m1.getMessdatenDaten().kanndann(date, false)).isFalse();
        Platform.runLater(() -> {
            if (pane.lookup("#zurueck") instanceof Button) {
                Button zurueck = (Button) pane.lookup("#zurueck");
                zurueck.fire();
            } else {
                Assertions.fail("Could not find zurueck");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        for (Messdiener m : dv.getMessdiener()) {
            if (m.getMessdatenDaten().getInsgesamtEingeteilt() != 0 || m.getMessdatenDaten().getAnzMessen() != 0) {
                Assertions.fail("Not all Messdaten are zero");
            }
        }
        Assertions.assertThat(m1.getMessdatenDaten().kanndann(date, false)).isTrue();
        Assertions.assertThat(instance.getNichtEingeteile()).contains(m1);
        Assertions.assertThat(instance.getNichtEingeteile()).contains(m2);
        Assertions.assertThat(instance.getNichtEingeteile()).contains(m3);

        Platform.runLater(() -> {
            if (pane.lookup("#nichteingeteilte") instanceof Button) {
                Button nichteingeteilte = (Button) pane.lookup("#nichteingeteilte");
                nichteingeteilte.fire();
            } else {
                Assertions.fail("Could not find nichteingeteilte");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();


        Platform.runLater(() -> {
            Assertions.assertThat(listWindows().size()).isEqualTo(2);
            Window w = listWindows().get(1);
            assert w != null;
            Node list = w.getScene().lookup("#list");
            if (list instanceof ListView) {
                Assertions.assertThat(((ListView<Messdiener>) list).getItems()).contains(m1, m2, m3);
            } else {
                Assertions.fail("Could not find ListView from Dialogs.show");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    //Ignore //turn it back
    @Test
    public void testEinteilung() {
        Messdiener m1Freund = Mockito.mock(Messdiener.class);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, m2, m3, m1Freund));
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        StandartMesse standartMesse = new StandartMesse("Mo", 8, "00", "o1", 2, "t1");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standartMesse));
        Mockito.when(pf.getSettings()).thenReturn(einst);
        einst.editMaxDienen(false, 10);

        Mockito.when(m1.makeId()).thenReturn("m1");
        Mockito.when(m2.makeId()).thenReturn("m2");
        Mockito.when(m3.makeId()).thenReturn("m3");
        final String m1FreundString = "dfc";
        Mockito.when(m1Freund.makeId()).thenReturn(m1FreundString);
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
        Mockito.when(m1.getMessdatenDaten()).thenReturn(md1);
        Messdaten md2 = new Messdaten(m2);
        Mockito.when(m2.getMessdatenDaten()).thenReturn(md2);
        Messdaten md3 = new Messdaten(m3);
        Mockito.when(m3.getMessdatenDaten()).thenReturn(md3);
        Messdaten md1F = new Messdaten(m1Freund);
        Mockito.when(m1Freund.getMessdatenDaten()).thenReturn(md1F);
        Messverhalten mv1 = new Messverhalten();
        Messverhalten mv2 = new Messverhalten();
        Messverhalten mv3 = new Messverhalten();
        Messverhalten mv1F = new Messverhalten();

        mv1.editiereBestimmteMesse(standartMesse, true);
        md1.austeilen(TestFerienplanController.getYesterday2());
        mv1F.editiereBestimmteMesse(standartMesse, true);
        md1F.austeilen(TestFerienplanController.getYesterday2());

        Mockito.when(m1.getDienverhalten()).thenReturn(mv1);
        Mockito.when(m2.getDienverhalten()).thenReturn(mv2);
        Mockito.when(m3.getDienverhalten()).thenReturn(mv3);
        Mockito.when(m1Freund.getDienverhalten()).thenReturn(mv1F);

        Messe me1 = new Messe(false, 1, TestFerienplanController.getYesterday2(), "o1", "t1");
        Messe me2 = new Messe(TestFerienplanController.getToday(), standartMesse);
        Messe me3 = new Messe(false, 1, TestFerienplanController.getTomorrow(), "o3", "t3");

        Mockito.doCallRealMethod().when(dialogs).show(Mockito.anyList(), Mockito.eq(FinishController.NICHT_EINGETEILTE_MESSDIENER));

        Assertions.assertThat(m1.getMessdatenDaten().kann(me1.getDate(), false, false)).isFalse();
        Assertions.assertThat(m1Freund.getMessdatenDaten().kann(me1.getDate(), false, false)).isFalse();
        Assertions.assertThat(m1.getMessdatenDaten().kann(me2.getDate(), false, false)).isTrue();
        Assertions.assertThat(m1Freund.getMessdatenDaten().kann(me2.getDate(), false, false)).isTrue();
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.PLAN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                List<Messe> messes = Arrays.asList(me1, me2, me3);
                instance = new FinishController(null, messes);
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterstartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(me1.istFertig() && me2.istFertig() && me3.istFertig()).isTrue();
        Assertions.assertThat(!me1.getEingeteilte().contains(m1) && !me1.getEingeteilte().contains(m1Freund)).isTrue();
        Assertions.assertThat(!me3.getEingeteilte().contains(m1) && !me3.getEingeteilte().contains(m1Freund)).isTrue();
        Assertions.assertThat(md1.kanndann(TestFerienplanController.getToday(), false)).isFalse();
        Assertions.assertThat(md1F.kanndann(TestFerienplanController.getToday(), false)).isFalse();
        Assertions.assertThat(me2.getEingeteilte().contains(m1) && me2.getEingeteilte().contains(m1)).isTrue();

        File out = new File(Log.getWorkingDir().getAbsolutePath() + File.separator + instance.getTitle() + ".pdf");
        try {
            if (out.exists()) {
                Files.delete(out.toPath());
            }
            instance.toPDF(null);
            Assertions.assertThat(out.exists()).isTrue();
            Files.delete(out.toPath());
        } catch (IOException e) {
            Log.getLogger().error(e.getMessage(), e);
            Assertions.fail(e.getMessage());
        }
        out = new File(Log.getWorkingDir() + File.separator + instance.getTitle() + ".docx");
        try {
            if (out.exists()) {
                Files.delete(out.toPath());
            }
            instance.toWORD(null);
            Assertions.assertThat(out.exists()).isTrue();
            Files.delete(out.toPath());
        } catch (IOException e) {
            Log.getLogger().error(e.getMessage(), e);
            Assertions.fail(e.getMessage());
        }

        Platform.runLater(() -> Mockito.when(dialogs.yesNoCancel(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Dialogs.YesNoCancelEnum.YES));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            if (pane.lookup("#zurueck") instanceof Button) {
                Button zurueck = (Button) pane.lookup("#zurueck");
                zurueck.fire();
            } else {
                Assertions.fail("Could not find zurueck");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        for (Messdiener m : dv.getMessdiener()) {
            if (m.getMessdatenDaten().getInsgesamtEingeteilt() != 0 || m.getMessdatenDaten().getAnzMessen() != 0) {
                Assertions.fail("Not all Messdaten are zero");
            }
        }
        Assertions.assertThat(me1.getEingeteilte().size()).isEqualTo(0);
        Assertions.assertThat(me2.getEingeteilte().size()).isEqualTo(0);
        Assertions.assertThat(me3.getEingeteilte().size()).isEqualTo(0);

        Pair<List<Messdiener>, StringBuilder> pair = instance.getResourcesForEmail();
        Assertions.assertThat(pair.getValue()).contains("mailto:");
        try {
            new URI(pair.getValue().toString());
        } catch (URISyntaxException e) {
            Assertions.fail(e.getMessage(), e);
        }

        Assertions.assertThat(pair.getKey()).hasSize(3).containsAll(Arrays.asList(m1, m2, m1Freund));

        Platform.runLater(() -> {
            if (pane.lookup("#mail") instanceof Button) {
                Button mail = (Button) pane.lookup("#mail");
                mail.fire();
            } else {
                Assertions.fail("Could not find mail");
            }
        });
    }
}
