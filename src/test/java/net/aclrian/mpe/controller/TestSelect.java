package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import org.junit.AfterClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class TestSelect extends ApplicationTest {

    @Mock
    private DateienVerwalter dv;
    @Mock
    private MainController mc;
    @Mock
    private Dialogs dialog;
    private Pane pane;
    private Select instance;
    private Scene scene;

    @AfterClass
    public static void removeFiles() {
        try {
            Files.deleteIfExists(new File(System.getProperty("user.home"), "cdajklsdfkldjoa.xml").toPath());
            Files.deleteIfExists(new File(System.getProperty("user.home"), "sdjgdlöfkgjclvk.xml").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        scene = new Scene(pane, 1000, 1000);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMedi() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(dv.getSavepath()).thenReturn(new File(System.getProperty("user.home")));
        Messdiener m1 = Mockito.mock(Messdiener.class);
        Messdiener m2 = Mockito.mock(Messdiener.class);
        File f1 = new File(System.getProperty("user.home"), "cdajklsdfkldjoa.xml");
        File f2 = new File(System.getProperty("user.home"), "sdjgdlöfkgjclvk.xml");
        try {
            Files.createFile(f1.toPath());
            Files.createFile(f2.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Mockito.when(m1.makeId()).thenReturn("cdajklsdfkldjoa");
        Mockito.when(m2.makeId()).thenReturn("sdjgdlöfkgjclvk");
        Mockito.when(m1.getFile()).thenReturn(f1);
        Mockito.when(m2.getFile()).thenReturn(f2);
        Mockito.when(m1.getFreunde()).thenReturn(new String[]{"sdjgdlöfkgjclvk", "", "", "", "", ""});
        Mockito.when(m1.getGeschwister()).thenReturn(new String[]{"sdjgdlöfkgjclvk", "", ""});
        Mockito.when(m2.getFreunde()).thenReturn(new String[]{"cdajklsdfkldjoa", "", "", "", "", ""});
        Mockito.when(m2.getGeschwister()).thenReturn(new String[]{"cdajklsdfkldjoa", "", ""});


        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, m2), Arrays.asList(m1, m2), Collections.singletonList(m2));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.SELECT_MEDI.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                instance = new Select(Select.Selecter.MESSDIENER, mc);
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterstartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        final Node list = scene.lookup("#list");
        Assertions.assertThat(list).isInstanceOf(ListView.class);
        Assertions.assertThat(((ListView<?>) list).getItems()).anyMatch(o -> o instanceof Label && ((Label) o).getText().equalsIgnoreCase(m1.toString()));
        Assertions.assertThat(((ListView<?>) list).getItems()).anyMatch(o -> o instanceof Label && ((Label) o).getText().equalsIgnoreCase(m2.toString()));

        ((ListView<?>) list).getSelectionModel().select(0);
        list.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 2, false, false, false, false, false, false, false, false, false, false, null));
        Mockito.verify(mc, Mockito.times(1)).changePaneMessdiener(Mockito.eq(m1));

        Assertions.assertThat(scene.lookup("#bearbeiten")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#bearbeiten")).fire();
        Mockito.verify(mc, Mockito.times(2)).changePaneMessdiener(Mockito.eq(m1));

        Assertions.assertThat(scene.lookup("#neu")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#neu")).fire();
        Mockito.verify(mc, Mockito.times(1)).changePaneMessdiener(Mockito.isNull());

        Assertions.assertThat(scene.lookup("#remove")).isInstanceOf(Button.class);
        Mockito.when(dialog.frage(Mockito.any(), Mockito.any(), Mockito.eq("Löschen"))).thenReturn(true);
        Assertions.assertThat(((ListView<?>) list).getItems()).hasSize(2);
        Platform.runLater(() -> ((Button) scene.lookup("#remove")).fire());
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(((ListView<?>) list).getItems()).hasSize(1);

        Assertions.assertThat(f1).doesNotExist();
        Assertions.assertThat(f2).exists();
        try {
            final String actual = Files.readString(f2.toPath());
            Assertions.assertThat(actual).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.getProperty("line.separator") +
                    "<XML>" + System.getProperty("line.separator") +
                    "  <MpE-Creator LICENSE=\"MIT\">Aclrian</MpE-Creator>" + System.getProperty("line.separator") +
                    "  <Body>" + System.getProperty("line.separator") +
                    "    <Vorname/>" + System.getProperty("line.separator") +
                    "    <Nachname/>" + System.getProperty("line.separator") +
                    "    <Email/>" + System.getProperty("line.separator") +
                    "    <Messverhalten/>" + System.getProperty("line.separator") +
                    "    <Leiter>false</Leiter>" + System.getProperty("line.separator") +
                    "    <Eintritt>0</Eintritt>" + System.getProperty("line.separator") +
                    "    <Anvertraute>" + System.getProperty("line.separator") +
                    "      <F1>LEER</F1>" + System.getProperty("line.separator") +
                    "      <F2>LEER</F2>" + System.getProperty("line.separator") +
                    "      <F3>LEER</F3>" + System.getProperty("line.separator") +
                    "      <F4>LEER</F4>" + System.getProperty("line.separator") +
                    "      <F5>LEER</F5>" + System.getProperty("line.separator") +
                    "      <g1>LEER</g1>" + System.getProperty("line.separator") +
                    "      <g2>LEER</g2>" + System.getProperty("line.separator") +
                    "      <g3>LEER</g3>" + System.getProperty("line.separator") +
                    "    </Anvertraute>" + System.getProperty("line.separator") +
                    "  </Body>" + System.getProperty("line.separator") +
                    "</XML>" + System.getProperty("line.separator"));
            Files.delete(f2.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Mockito.verify(dv, Mockito.times(3)).getMessdiener();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMesse() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        final StandartMesse standartMesse = new StandartMesse("Do", 10, "00", "o1", 20, "t1");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standartMesse));
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date von = df.parse("1.10.2020");
            Date bis = df.parse("15.10.2020");
            Mockito.when(dialog.getDates(Mockito.eq(Select.ZEITRAUM_WAEHLEN), Mockito.eq(Select.VON), Mockito.eq(Select.BIS))).thenReturn(Arrays.asList(von, bis));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<Messe> messen = new ArrayList<>();
        final Messe m1 = new Messe(false, 1, new Date(), "o", "t");
        messen.add(m1);
        messen.add(new Messe(true, 10, new Date(), "o2", "t2"));
        Mockito.when(mc.getMessen()).thenReturn(messen, messen, Collections.emptyList());
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.SELECT_MESSE.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                instance = new Select(Select.Selecter.MESSE, mc);
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterstartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        Assertions.assertThat(scene.lookup("#neu")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#neu")).fire();
        Mockito.verify(mc, Mockito.times(1)).changePaneMesse(Mockito.isNull());

        ((ListView<?>) scene.lookup("#list")).getSelectionModel().select(0);
        scene.lookup("#list").fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 2, false, false, false, false, false, false, false, false, false, false, null));
        Mockito.verify(mc, Mockito.times(1)).changePaneMesse(Mockito.eq(m1));

        Assertions.assertThat(scene.lookup("#bearbeiten")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#bearbeiten")).fire();
        Mockito.verify(mc, Mockito.times(2)).changePaneMesse(Mockito.eq(m1));

        Assertions.assertThat(scene.lookup("#" + Select.GENERIEREN_ID)).isInstanceOf(Button.class);
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(2);
        Platform.runLater(() -> ((Button) scene.lookup("#" + Select.GENERIEREN_ID)).fire());
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(4);

        Assertions.assertThat(scene.lookup("#remove")).isInstanceOf(Button.class);
        ((ListView<?>) scene.lookup("#list")).getSelectionModel().select(0);
        Mockito.when(dialog.frage(Mockito.any(), Mockito.any(), Mockito.eq("Löschen"))).thenReturn(true);
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(4);
        Platform.runLater(() -> ((Button) scene.lookup("#remove")).fire());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mc, Mockito.times(3)).getMessen();
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(0);
    }
}
