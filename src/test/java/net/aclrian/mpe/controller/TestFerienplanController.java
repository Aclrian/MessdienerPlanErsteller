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
import net.aclrian.mpe.utils.Log;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestFerienplanController extends ApplicationTest {

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

    public static Date getToday() {
        return Calendar.getInstance().getTime();
    }

    public static Date getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static Date getYesterday() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 100, 100);
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
    public void testWithoutMessen() {
        Date d = getToday();
        d.setTime(d.getTime() + 1);
        List<Messe> messen = Arrays.asList(
                new Messe(false, 1, getToday(), "ort1", "typ1"),
                new Messe(false, 1, d, "ort1", "typ1"),
                new Messe(false, 2, getTomorrow(), "ort1", "typ1"),
                new Messe(false, 2, getYesterday(), "ort1", "typ1"));
        Mockito.when(mc.getMessen()).thenReturn(messen);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(medi1, medi2, medi3));
        Messdaten md1 = Mockito.mock(Messdaten.class);
        Mockito.when(medi1.getMessdatenDaten()).thenReturn(md1);
        Messdaten md2 = Mockito.mock(Messdaten.class);
        Mockito.when(medi2.getMessdatenDaten()).thenReturn(md2);
        Messdaten md3 = Mockito.mock(Messdaten.class);
        Mockito.when(medi3.getMessdatenDaten()).thenReturn(md3);
        DateienVerwalter.setInstance(dv);
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.FERIEN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterstartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        SimpleDateFormat df = new SimpleDateFormat(FerienplanController.PATTERN);

        Assertions.assertThat(instance.getDates().size()).isEqualTo(3);
        Assertions.assertThat(instance.getDates().get(0)).isEqualTo(df.format(getYesterday()));
        Assertions.assertThat(instance.getDates().get(1)).isEqualTo(df.format(getToday()));
        Assertions.assertThat(instance.getDates().get(2)).isEqualTo(df.format(getTomorrow()));
        try {
            GridPane gridPane = (GridPane) pane.getChildrenUnmodifiable().get(0);
            TableView<?> table = (TableView<?>) gridPane.getChildrenUnmodifiable().stream().filter(node -> node instanceof TableView).findFirst().orElse(null);

            assert table != null;
            Assertions.assertThat(table.getColumns().size()).isEqualTo(4);
            Assertions.assertThat(table.getItems().size()).isEqualTo(3);
            for (Object o : table.getItems()) {
                FerienplanController.Datentraeger o1 = (FerienplanController.Datentraeger) o;
                Assertions.assertThat(o1.get(df.format(getYesterday())).getProperty().get()).isEqualTo(false);
                Assertions.assertThat(o1.get(df.format(getToday())).getProperty().get()).isEqualTo(false);
                Assertions.assertThat(o1.get(df.format(getTomorrow())).getProperty().get()).isEqualTo(false);
            }
        } catch (IndexOutOfBoundsException | ClassCastException | AssertionError e) {
            Log.getLogger().error(e.getMessage(), e);
            Assertions.fail("Couldn't find table");
        }
    }

    @Test
    public void withData() {
        SimpleDateFormat df = new SimpleDateFormat(FerienplanController.PATTERN);

        List<Messe> messen = Arrays.asList(
                new Messe(false, 1, getToday(), "ort1", "typ1"),
                new Messe(false, 2, getTomorrow(), "ort1", "typ1"),
                new Messe(false, 2, getYesterday(), "ort1", "typ1"));
        Mockito.when(medi1.makeId()).thenReturn("a");
        Mockito.when(medi2.makeId()).thenReturn("b");
        Mockito.when(medi3.makeId()).thenReturn("c");

        Mockito.when(mc.getMessen()).thenReturn(messen);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(medi1, medi2, medi3));
        Messdaten md1 = Mockito.mock(Messdaten.class);
        Mockito.when(md1.ausgeteilt(df.format(getToday()))).thenReturn(true);
        Mockito.when(md1.ausgeteilt(df.format(getTomorrow()))).thenReturn(true);
        Mockito.when(medi1.getMessdatenDaten()).thenReturn(md1);
        Messdaten md2 = Mockito.mock(Messdaten.class);
        Mockito.when(medi2.getMessdatenDaten()).thenReturn(md2);
        Messdaten md3 = Mockito.mock(Messdaten.class);
        Mockito.when(medi3.getMessdatenDaten()).thenReturn(md3);
        Mockito.when(md3.ausgeteilt(df.format(getYesterday()))).thenReturn(true);
        Mockito.when(md3.ausgeteilt(df.format(getToday()))).thenReturn(true);
        Mockito.when(md3.ausgeteilt(df.format(getTomorrow()))).thenReturn(true);
        DateienVerwalter.setInstance(dv);
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.FERIEN.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterstartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        Assertions.assertThat(instance.getDates().size()).isEqualTo(3);
        Assertions.assertThat(instance.getDates().get(0)).isEqualTo(df.format(getYesterday()));
        Assertions.assertThat(instance.getDates().get(1)).isEqualTo(df.format(getToday()));
        Assertions.assertThat(instance.getDates().get(2)).isEqualTo(df.format(getTomorrow()));
        try {
            GridPane gridPane = (GridPane) pane.getChildrenUnmodifiable().get(0);
            @SuppressWarnings("unchecked")
            TableView<FerienplanController.Datentraeger> table = (TableView<FerienplanController.Datentraeger>) gridPane.getChildrenUnmodifiable().stream().filter(node -> node instanceof TableView).findFirst().orElse(null);
            assert table != null;
            Assertions.assertThat(table.getColumns().size()).isEqualTo(4);

            for (Object dt : table.getItems()) {
                if (dt instanceof FerienplanController.Datentraeger) {
                    if (((FerienplanController.Datentraeger) dt).getMessdiener().equalsIgnoreCase("a")) {
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getYesterday())).getProperty().get()).isEqualTo(false);
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getToday())).getProperty().get()).isEqualTo(true);
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getTomorrow())).getProperty().get()).isEqualTo(true);
                    } else if (((FerienplanController.Datentraeger) dt).getMessdiener().equalsIgnoreCase("b")) {
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getYesterday())).getProperty().get()).isEqualTo(false);
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getToday())).getProperty().get()).isEqualTo(false);
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getTomorrow())).getProperty().get()).isEqualTo(false);
                    } else if (((FerienplanController.Datentraeger) dt).getMessdiener().equalsIgnoreCase("c")) {
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getYesterday())).getProperty().get()).isEqualTo(true);
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getToday())).getProperty().get()).isEqualTo(true);
                        Assertions.assertThat(((FerienplanController.Datentraeger) dt).get(df.format(getTomorrow())).getProperty().get()).isEqualTo(true);
                    } else {
                        Assertions.fail("False Item in TableView-List");
                    }
                } else {
                    Assertions.fail(dt + "is not a " + FerienplanController.Datentraeger.class.getName());
                }
            }
            //some change action in TableView
            //make empty
            ((Button) pane.lookup("#leeren")).fire();
            Assertions.assertThat(table.getItems().size()).isEqualTo(3);
            FerienplanController.Datentraeger changedCellData = null;
            for (Object o : table.getItems()) {
                FerienplanController.Datentraeger o1 = (FerienplanController.Datentraeger) o;
                if (o1.getMessdiener().equalsIgnoreCase("b")) {
                    changedCellData = o1;
                }
                Assertions.assertThat(o1.get(df.format(getYesterday())).getProperty().get()).isEqualTo(false);
                Assertions.assertThat(o1.get(df.format(getToday())).getProperty().get()).isEqualTo(false);
                Assertions.assertThat(o1.get(df.format(getTomorrow())).getProperty().get()).isEqualTo(false);
            }
            table.getFocusModel().focus(1, table.getColumns().get(2));
            table.fireEvent(new KeyEvent(table, table, KeyEvent.KEY_PRESSED, " ", " ", KeyCode.SPACE, false, false, false, false));
            WaitForAsyncUtils.waitForFxEvents();

            assert changedCellData != null;
            Assertions.assertThat(changedCellData.get(df.format(getToday())).kann()).isEqualTo(true);
        } catch (IndexOutOfBoundsException | ClassCastException | AssertionError e) {
            Log.getLogger().error(e.getMessage(), e);
            Assertions.fail("Couldn't find table");
        }
    }
}