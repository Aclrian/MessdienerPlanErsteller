package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static net.aclrian.mpe.utils.DateUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class TestFerienplanController extends ApplicationTest {

    @Mock
    DateienVerwalter dv;
    @Mock
    MainController mc;
    private Pane pane;
    private FerienplanController instance;
    @Mock
    private Messdiener medi1;
    @Mock
    private Messdiener medi2;
    @Mock
    private Messdiener medi3;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        mc = Mockito.mock(MainController.class);
        dv = Mockito.mock(DateienVerwalter.class);
        medi1 = Mockito.mock(Messdiener.class);
        medi2 = Mockito.mock(Messdiener.class);
        medi3 = Mockito.mock(Messdiener.class);
    }

    @Test
    void testWithoutMessen() {
        final LocalDateTime tomorrow = getTomorrow().atTime(10, 42);
        final LocalDateTime yesterday = getYesterday().atTime(3, 14);
        final LocalDateTime today = getToday().atTime(0, 0);
        List<Messe> messen = Arrays.asList(
                new Messe(false, 1, today, "ort1", "typ1"),
                new Messe(false, 1, getToday().atTime(1, 0), "ort1", "typ1"),
                new Messe(false, 2, tomorrow, "ort1", "typ1"),
                new Messe(false, 2, yesterday, "ort1", "typ1"));
        Mockito.when(mc.getMessen()).thenReturn(messen);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(medi1, medi2, medi3));
        Messdaten md1 = Mockito.mock(Messdaten.class);
        Mockito.when(medi1.getMessdaten()).thenReturn(md1);
        Messdaten md2 = Mockito.mock(Messdaten.class);
        Mockito.when(medi2.getMessdaten()).thenReturn(md2);
        Messdaten md3 = Mockito.mock(Messdaten.class);
        Mockito.when(medi3.getMessdaten()).thenReturn(md3);
        DateienVerwalter.setInstance(dv);
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.FERIEN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(instance.getDates()).hasSize(3);
        assertThat(instance.getDates().get(0)).isEqualTo(DATE.format(yesterday));
        assertThat(instance.getDates().get(1)).isEqualTo(DATE.format(today));
        assertThat(instance.getDates().get(2)).isEqualTo(DATE.format(tomorrow));
        try {
            GridPane gridPane = (GridPane) pane.getChildrenUnmodifiable().get(0);
            TableView<?> table = (TableView<?>) gridPane.getChildrenUnmodifiable().stream().filter(node -> node instanceof TableView).findFirst().orElse(null);

            assert table != null;
            assertThat(table.getColumns()).hasSize(4);
            assertThat(table.getItems()).hasSize(3);
            for (Object o : table.getItems()) {
                FerienplanController.Datentraeger o1 = (FerienplanController.Datentraeger) o;
                assertThat(o1.get(DATE.format(yesterday)).property().get()).isFalse();
                assertThat(o1.get(DATE.format(today)).property().get()).isFalse();
                assertThat(o1.get(DATE.format(tomorrow)).property().get()).isFalse();
            }
        } catch (IndexOutOfBoundsException | ClassCastException | AssertionError e) {
            MPELog.getLogger().error(e.getMessage(), e);
            Assertions.fail("Couldn't find table", e);
        }
    }

    @Test
    void withData() {
        List<Messe> messen = Arrays.asList(
                new Messe(false, 1, getToday().atTime(0, 0), "ort1", "typ1"),
                new Messe(false, 2, getTomorrow().atTime(0, 0), "ort1", "typ1"),
                new Messe(false, 2, getYesterday().atTime(0, 0), "ort1", "typ1"));
        Mockito.when(medi1.toString()).thenReturn("a");
        Mockito.when(medi2.toString()).thenReturn("b");
        Mockito.when(medi3.toString()).thenReturn("c");

        Mockito.when(mc.getMessen()).thenReturn(messen);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(medi1, medi2, medi3));
        Messdaten md1 = Mockito.mock(Messdaten.class);
        Mockito.when(md1.ausgeteilt(getToday())).thenReturn(true);
        Mockito.when(md1.ausgeteilt(getTomorrow())).thenReturn(true);
        Mockito.when(medi1.getMessdaten()).thenReturn(md1);
        Messdaten md2 = Mockito.mock(Messdaten.class);
        Mockito.when(medi2.getMessdaten()).thenReturn(md2);
        Messdaten md3 = Mockito.mock(Messdaten.class);
        Mockito.when(medi3.getMessdaten()).thenReturn(md3);
        Mockito.when(md3.ausgeteilt(getYesterday())).thenReturn(true);
        Mockito.when(md3.ausgeteilt(getToday())).thenReturn(true);
        Mockito.when(md3.ausgeteilt(getTomorrow())).thenReturn(true);
        DateienVerwalter.setInstance(dv);
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.FERIEN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(instance.getDates()).hasSize(3);
        assertThat(instance.getDates().get(0)).isEqualTo(DATE.format(getYesterday()));
        assertThat(instance.getDates().get(1)).isEqualTo(DATE.format(getToday()));
        assertThat(instance.getDates().get(2)).isEqualTo(DATE.format(getTomorrow()));
        try {
            GridPane gridPane = (GridPane) pane.getChildrenUnmodifiable().get(0);
            @SuppressWarnings("unchecked")
            TableView<FerienplanController.Datentraeger> table = (TableView<FerienplanController.Datentraeger>) gridPane.getChildrenUnmodifiable().stream().filter(node -> node instanceof TableView).findFirst().orElse(null);
            assert table != null;
            assertThat(table.getColumns()).hasSize(4);

            for (FerienplanController.Datentraeger dt : table.getItems()) {
                if (dt.getMessdiener().equalsIgnoreCase("a")) {
                    assertThat(dt.get(DATE.format(getYesterday())).property().get()).isFalse();
                    assertThat(dt.get(DATE.format(getToday())).property().get()).isTrue();
                    assertThat(dt.get(DATE.format(getTomorrow())).property().get()).isTrue();
                } else if (dt.getMessdiener().equalsIgnoreCase("b")) {
                    assertThat(dt.get(DATE.format(getYesterday())).property().get()).isFalse();
                    assertThat(dt.get(DATE.format(getToday())).property().get()).isFalse();
                    assertThat(dt.get(DATE.format(getTomorrow())).property().get()).isFalse();
                } else if (dt.getMessdiener().equalsIgnoreCase("c")) {
                    assertThat(dt.get(DATE.format(getYesterday())).property().get()).isTrue();
                    assertThat(dt.get(DATE.format(getToday())).property().get()).isTrue();
                    assertThat(dt.get(DATE.format(getTomorrow())).property().get()).isTrue();
                } else {
                    Assertions.fail("Wrong Item in TableView-List");
                }
            }
            //some change action in TableView
            //make empty
            ((Button) pane.lookup("#leeren")).fire();
            assertThat(table.getItems()).hasSize(3);
            FerienplanController.Datentraeger changedCellData = null;
            for (FerienplanController.Datentraeger o : table.getItems()) {
                if (o.getMessdiener().equalsIgnoreCase("b")) {
                    changedCellData = o;
                }
                assertThat(o.get(DATE.format(getYesterday())).property().get()).isFalse();
                assertThat(o.get(DATE.format(getToday())).property().get()).isFalse();
                assertThat(o.get(DATE.format(getTomorrow())).property().get()).isFalse();
            }
            table.getFocusModel().focus(1, table.getColumns().get(2));
            table.fireEvent(new KeyEvent(table, table, KeyEvent.KEY_PRESSED, " ", " ", KeyCode.SPACE, false, false, false, false));
            WaitForAsyncUtils.waitForFxEvents();

            assert changedCellData != null;
            assertThat(changedCellData.get(DATE.format(getToday())).property().get()).isTrue();
        } catch (IndexOutOfBoundsException | ClassCastException | AssertionError e) {
            MPELog.getLogger().error(e.getMessage(), e);
            Assertions.fail("Couldn't find table");
        }
    }
}