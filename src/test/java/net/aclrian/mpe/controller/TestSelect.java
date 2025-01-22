package net.aclrian.mpe.controller; //NOPMD - suppressed ExcessiveImports - for test purpose

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
import net.aclrian.mpe.TestUtil;
import net.aclrian.mpe.messdiener.Email;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void close() {
        try {
            openMocks.close();
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @SuppressWarnings({"unchecked", "PMD.ExcessiveMethodLength"})
    @Test
    public void testMessdiener(@TempDir Path tempDir) {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(dv.getSavePath()).thenReturn(tempDir.toFile());
        Messdiener m1 = Mockito.mock(Messdiener.class);
        Messdiener m2 = Mockito.mock(Messdiener.class);
        File f1 = new File(tempDir.toFile(), "cdajklsdfkldjoa.xml");
        File f2 = new File(tempDir.toFile(), "sdjgdlöfkgjclvk.xml");
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
        Mockito.when(m1.getEmail()).thenReturn(Email.EMPTY_EMAIL);
        Mockito.when(m2.getEmail()).thenReturn(Email.EMPTY_EMAIL);
        Mockito.when(m1.getFreunde()).thenReturn(new String[]{"sdjgdlöfkgjclvk", "", "", "", "", ""});
        Mockito.when(m1.getGeschwister()).thenReturn(new String[]{"sdjgdlöfkgjclvk", "", ""});
        Mockito.when(m2.getFreunde()).thenReturn(new String[]{"cdajklsdfkldjoa", "", "", "", "", ""});
        Mockito.when(m2.getGeschwister()).thenReturn(new String[]{"cdajklsdfkldjoa", "", ""});

        TestUtil.mockSaveToXML(m1);
        TestUtil.mockSaveToXML(m2);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, m2), Arrays.asList(m1, m2), Collections.singletonList(m2));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.SELECT_MEDI.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                instance = new Select(Select.Selector.MESSDIENER, mc);
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        final Node list = scene.lookup("#list");
        assertThat(list).isInstanceOf(ListView.class);
        assertThat(((ListView<?>) list).getItems()).anyMatch(o -> o instanceof Label && ((Label) o).getText().equalsIgnoreCase(m1.toString()));
        assertThat(((ListView<?>) list).getItems()).anyMatch(o -> o instanceof Label && ((Label) o).getText().equalsIgnoreCase(m2.toString()));

        ((ListView<?>) list).getSelectionModel().select(0);
        list.fireEvent(new MouseEvent(
                MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                MouseButton.PRIMARY, 2, false, false, false, false, false,
                false, false, false, false, false, null));
        Mockito.verify(mc, Mockito.times(1)).changePane(m1);

        assertThat(scene.lookup("#bearbeiten")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#bearbeiten")).fire();
        Mockito.verify(mc, Mockito.times(2)).changePane(m1);

        assertThat(scene.lookup("#neu")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#neu")).fire();
        Mockito.verify(mc, Mockito.times(1)).changePane(MainController.EnumPane.MESSDIENER);

        assertThat(scene.lookup("#remove")).isInstanceOf(Button.class);
        Mockito.when(dialog.frage(Mockito.any(), Mockito.any(), Mockito.eq("Löschen"))).thenReturn(true);
        assertThat(((ListView<?>) list).getItems()).hasSize(2);
        Platform.runLater(() -> ((Button) scene.lookup("#remove")).fire());
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((ListView<?>) list).getItems()).hasSize(1);

        assertThat(f1).doesNotExist();
        assertThat(f2).exists();
        try {
            final String actual = Files.readString(f2.toPath());
            assertThat(actual).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.getProperty("line.separator")
                    + "<XML>" + System.getProperty("line.separator")
                    + "    <MpE-Creator LICENSE=\"MIT\">Aclrian</MpE-Creator>" + System.getProperty("line.separator")
                    + "    <Body>" + System.getProperty("line.separator")
                    + "        <Vorname/>" + System.getProperty("line.separator")
                    + "        <Nachname/>" + System.getProperty("line.separator")
                    + "        <Email/>" + System.getProperty("line.separator")
                    + "        <Messverhalten/>" + System.getProperty("line.separator")
                    + "        <Leiter>false</Leiter>" + System.getProperty("line.separator")
                    + "        <Eintritt>0</Eintritt>" + System.getProperty("line.separator")
                    + "        <Anvertraute>" + System.getProperty("line.separator")
                    + "            <F1>LEER</F1>" + System.getProperty("line.separator")
                    + "            <F2>LEER</F2>" + System.getProperty("line.separator")
                    + "            <F3>LEER</F3>" + System.getProperty("line.separator")
                    + "            <F4>LEER</F4>" + System.getProperty("line.separator")
                    + "            <F5>LEER</F5>" + System.getProperty("line.separator")
                    + "            <g1>LEER</g1>" + System.getProperty("line.separator")
                    + "            <g2>LEER</g2>" + System.getProperty("line.separator")
                    + "            <g3>LEER</g3>" + System.getProperty("line.separator")
                    + "        </Anvertraute>" + System.getProperty("line.separator")
                    + "    </Body>" + System.getProperty("line.separator")
                    + "</XML>" + System.getProperty("line.separator"));
            Files.delete(f2.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Mockito.verify(dv, Mockito.times(3)).getMessdiener();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialog, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMesse() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        final StandardMesse standardMesse = new StandardMesse(DayOfWeek.THURSDAY, 10, "00", "o1", 20, "t1");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standardMesse));
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
                instance = new Select(Select.Selector.MESSE, mc);
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(scene.lookup("#neu")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#neu")).fire();
        Mockito.verify(mc, Mockito.times(1)).changePane(MainController.EnumPane.MESSE);

        ((ListView<?>) scene.lookup("#list")).getSelectionModel().select(0);
        scene.lookup("#list").fireEvent(new MouseEvent(
                MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
                2, false, false, false, false, false, false, false, false, false, false, null));
        Mockito.verify(mc, Mockito.times(1)).changePane(m1);

        assertThat(scene.lookup("#bearbeiten")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#bearbeiten")).fire();
        Mockito.verify(mc, Mockito.times(2)).changePane(m1);

        assertThat(scene.lookup("#" + Select.GENERIEREN_ID)).isInstanceOf(Button.class);
        assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(2);
        Locale tmp = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);
        Platform.runLater(() -> ((Button) scene.lookup("#" + Select.GENERIEREN_ID)).fire());
        WaitForAsyncUtils.waitForFxEvents();
        Locale.setDefault(tmp);
        WaitForAsyncUtils.waitForFxEvents();
        System.out.println(((ListView<?>) scene.lookup("#list")).getItems());
        assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(3);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialog, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testeMesseRemove() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        final StandardMesse standardMesse = new StandardMesse(DayOfWeek.THURSDAY, 10, "00", "o1", 20, "t1");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standardMesse));
        LocalDate von = LocalDate.of(2020, 10, 1);
        LocalDate bis = LocalDate.of(2020, 10, 15);
        Mockito.when(dialog.getDates(Select.ZEITRAUM_WAEHLEN, Select.VON, Select.BIS)).thenReturn(Arrays.asList(von, bis));

        ArrayList<Messe> messen = new ArrayList<>();
        final Messe m1 = new Messe(false, 1, LocalDateTime.now(), "o", "t");
        messen.add(m1);
        messen.add(new Messe(true, 10, LocalDateTime.now(), "o2", "t2"));
        Mockito.when(mc.getMessen()).thenReturn(messen, messen, Collections.singletonList(m1));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.SELECT_MESSE.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                instance = new Select(Select.Selector.MESSE, mc);
                fl.setController(instance);
                pane.getChildren().add(fl.load());
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(scene.lookup("#remove")).isInstanceOf(Button.class);
        ((ListView<?>) scene.lookup("#list")).getSelectionModel().select(0);
        Mockito.when(dialog.frage(Mockito.any(), Mockito.any(), Mockito.eq("Löschen"))).thenReturn(true);
        assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(2);
        WaitForAsyncUtils.asyncFx(() -> ((Button) scene.lookup("#remove")).fire());
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(1);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialog, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }
}
