package net.aclrian.mpe.controller;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.pfarrei.*;
import net.aclrian.mpe.utils.*;
import org.junit.*;
import org.mockito.*;
import org.testfx.assertions.api.*;
import org.testfx.framework.junit.*;
import org.testfx.util.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

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
    private AutoCloseable openMocks;

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
        scene = new Scene(pane, 600, 400);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        openMocks = MockitoAnnotations.openMocks(this);
    }

    @After
    public void close() throws Exception {
        openMocks.close();
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
        Mockito.when(m1.toString()).thenReturn("cdajklsdfkldjoa");
        Mockito.when(m2.toString()).thenReturn("sdjgdlöfkgjclvk");
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
        Mockito.verify(mc, Mockito.times(1)).changePaneMessdiener(m1);

        Assertions.assertThat(scene.lookup("#bearbeiten")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#bearbeiten")).fire();
        Mockito.verify(mc, Mockito.times(2)).changePaneMessdiener(m1);

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
        final StandartMesse standartMesse = new StandartMesse(DayOfWeek.THURSDAY, 10, "00", "o1", 20, "t1");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standartMesse));
        LocalDate von = LocalDate.of(2020, 10, 1);
        LocalDate bis = LocalDate.of(2020, 10, 15);
        Mockito.when(dialog.getDates(Select.ZEITRAUM_WAEHLEN, Select.VON, Select.BIS)).thenReturn(Arrays.asList(von, bis));

        ArrayList<Messe> messen = new ArrayList<>();
        final Messe m1 = new Messe(false, 1, LocalDateTime.now(), "o", "t");
        messen.add(m1);
        messen.add(new Messe(true, 10, LocalDateTime.now(), "o2", "t2"));
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
        Mockito.verify(mc, Mockito.times(1)).changePaneMesse(m1);

        Assertions.assertThat(scene.lookup("#bearbeiten")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#bearbeiten")).fire();
        Mockito.verify(mc, Mockito.times(2)).changePaneMesse(m1);

        Assertions.assertThat(scene.lookup("#" + Select.GENERIEREN_ID)).isInstanceOf(Button.class);
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(2);
        var tmp = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);
        Platform.runLater(() -> ((Button) scene.lookup("#" + Select.GENERIEREN_ID)).fire());
        WaitForAsyncUtils.waitForFxEvents();
        Locale.setDefault(tmp);
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(4);
        Assertions.assertThat(scene.lookup("#remove")).isInstanceOf(Button.class);
        ((ListView<?>) scene.lookup("#list")).getSelectionModel().select(0);
        Mockito.when(dialog.frage(Mockito.any(), Mockito.any(), Mockito.eq("Löschen"))).thenReturn(true);
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(4);
        try {
            Thread.sleep(10000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> ((Button) scene.lookup("#remove")).fire());

        try {
            Thread.sleep(10000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        WaitForAsyncUtils.waitForFxEvents();
        List l = ((ListView<?>) scene.lookup("#list")).getItems();
        l.forEach(Log.getLogger()::info);
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems().size()).isEqualTo(3);
    }
}
