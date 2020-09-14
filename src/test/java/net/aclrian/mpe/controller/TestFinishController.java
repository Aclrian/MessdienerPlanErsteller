package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.NodeQuery;
import org.testfx.util.WaitForAsyncUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

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
        Scene scene = new Scene(pane, 100, 100);
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
            if(list instanceof ListView){
               Assertions.assertThat(((ListView<Messdiener>) list).getItems()).contains(m1, m2, m3);
            } else {
                Assertions.fail("Could not find ListView from Dialogs.show");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        dialogs = Mockito.mock(Dialogs.class);
    }

}