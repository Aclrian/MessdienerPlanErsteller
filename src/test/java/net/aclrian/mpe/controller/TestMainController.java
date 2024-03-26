package net.aclrian.mpe.controller; //NOPMD - suppressed ExcessiveImports - for test purpose

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.aclrian.fx.TimeSpinner;
import net.aclrian.mpe.MainApplication;
import net.aclrian.mpe.messdiener.Email;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messdiener.Messverhalten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateUtil;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Speicherort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMainController extends ApplicationTest {

    @Mock
    private Dialogs dialogs;
    @Mock
    private DateienVerwalter dv;
    @Mock
    private Pfarrei pf;
    private Pane pane;
    private MainController instance;
    @Mock
    private MainApplication mainApplication;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.setResizable(true);
        this.stage = stage;
        stage.setWidth(10);
        stage.setHeight(10);

        mainApplication = Mockito.mock(MainApplication.class);
        dialogs = Mockito.mock(Dialogs.class);
        dv = Mockito.mock(DateienVerwalter.class);
        pf = Mockito.mock(Pfarrei.class);
    }

    //CHECKSTYLE:OFF: MethodLength
    public void testEachEnumPane() { //NOPMD - suppressed NPathComplexity - for test purpose
        Dialogs.setDialogs(dialogs);
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        String titel = "LOL ";
        Mockito.when(pf.getName()).thenReturn(titel);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(new StandardMesse(DayOfWeek.MONDAY, 9, "00", "o", 0, "t")));
        FXMLLoader loader = new FXMLLoader();

        loader.setController(new MainController(mainApplication, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/head.fxml"));
        try {
            pane = loader.load();
            Platform.runLater(() -> stage.show());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        WaitForAsyncUtils.waitForFxEvents();
        Scene scene = new Scene(pane);
        //Start
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setTitle("MessdienerplanErsteller");
            instance.changePane(MainController.EnumPane.START);
            Node name = scene.lookup("#name");
            if (name instanceof Label) {
                assertThat(((Label) name).getText()).isEqualToIgnoringCase(titel);
            } else {
                Assertions.fail("name not found");
            }
            instance.changePane(MainController.EnumPane.START);
            assertThat(name.isVisible()).isTrue();
        });
        WaitForAsyncUtils.waitForFxEvents();
        //Messdiener
        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.MESSDIENER);
            assertThat(stage.getScene().lookup("#name")).isNotNull();
        });
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.START);
            Mockito.verify(dialogs, Mockito.times(1)).warn(MainController.GESPERRT);
            Node button = scene.lookup("#button");
            if (button instanceof SplitMenuButton) {
                ObservableList<MenuItem> items = ((SplitMenuButton) button).getItems();
                items.stream().filter(menuItem -> menuItem.getId().equals("cancel")).findFirst().orElseThrow().fire();
                Messdiener messdiener = Mockito.mock(Messdiener.class);
                Mockito.when(messdiener.getVorname()).thenReturn("v");
                Mockito.when(messdiener.getNachname()).thenReturn("n");
                Mockito.when(messdiener.getGeschwister()).thenReturn(new String[0]);
                Mockito.when(messdiener.getFreunde()).thenReturn(new String[0]);
                Mockito.when(messdiener.getEintritt()).thenReturn(DateUtil.getCurrentYear());
                Mockito.when(messdiener.istLeiter()).thenReturn(false);
                try {
                    Mockito.when(messdiener.getEmail()).thenReturn(new Email("a@a.de"));
                } catch (Email.NotValidException e) {
                    Assertions.fail(e);
                }
                Messverhalten mv = new Messverhalten();
                Mockito.when(messdiener.getDienverhalten()).thenReturn(mv);
                instance.changePane(messdiener);

                Mockito.when(dv.getMessdiener()).thenReturn(Collections.singletonList(messdiener));
                final Messdaten md = Mockito.mock(Messdaten.class);
                Mockito.when(messdiener.getMessdaten()).thenReturn(md);

                assertThat(scene.lookup("#vorname")).isInstanceOf(TextField.class);
                assertThat(((TextField) scene.lookup("#vorname")).getText()).isEqualTo("v");
                assertThat(scene.lookup("#name")).isInstanceOf(TextField.class);
                assertThat(((TextField) scene.lookup("#name")).getText()).isEqualTo("n");
                assertThat(scene.lookup("#geschwisterListView")).isInstanceOf(ListView.class);
                assertThat(((ListView<?>) scene.lookup("#geschwisterListView")).getItems()).hasSize(1);
                assertThat(scene.lookup("#freundeListView")).isInstanceOf(ListView.class);
                assertThat(((ListView<?>) scene.lookup("#freundeListView")).getItems()).hasSize(1);
                assertThat(scene.lookup("#eintritt")).isInstanceOf(Slider.class);
                assertThat(((Slider) scene.lookup("#eintritt")).getValue()).isEqualTo(DateUtil.getCurrentYear());
                assertThat(scene.lookup("#leiter")).isInstanceOf(CheckBox.class);
                assertThat(((CheckBox) scene.lookup("#leiter")).isSelected()).isFalse();
                assertThat(scene.lookup("#email")).isInstanceOf(TextField.class);
                assertThat(((TextField) scene.lookup("#email")).getText()).isEqualTo("a@a.de");
                assertThat(scene.lookup("#table")).isInstanceOf(TableView.class);
                final TableView<?> table = (TableView<?>) scene.lookup("#table");
                assertThat(table.getItems()).hasSize(1);
                assertThat(((Messverhalten.KannWelcheMesse) table.getItems().get(0)).kannDann()).isFalse();

                assertThat(instance.getControl().isLocked()).isTrue();

            } else {
                Assertions.fail("could not find cancel");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        //Messe
        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.MESSE);
            Mockito.verify(dialogs, Mockito.times(2)).warn(MainController.GESPERRT);
            Node button = scene.lookup("#button");
            if (button instanceof SplitMenuButton) {
                ObservableList<MenuItem> items = ((SplitMenuButton) button).getItems();
                items.stream().filter(menuItem -> menuItem.getId().equals("cancel")).findFirst().orElseThrow().fire();

                Messe m = Mockito.mock(Messe.class);
                Mockito.when(m.getMesseTyp()).thenReturn("typ");
                Mockito.when(m.getKirche()).thenReturn("ort");
                Mockito.when(m.getID()).thenReturn("id");

                Mockito.when(m.getDate()).thenReturn(LocalDateTime.of(2020, 1, 1, 8, 0));
                Mockito.when(m.getAnzMessdiener()).thenReturn(1);
                Mockito.when(m.isHochamt()).thenReturn(true);
                Mockito.when(m.getStandardMesse()).thenReturn(new Sonstiges());
                Mockito.when(m.htmlAusgeben()).thenReturn("123456</html>");

                instance.changePane(m);
            } else {
                Assertions.fail("could not find cancel");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            assertThat(scene.lookup("#titel")).isInstanceOf(TextField.class);
            assertThat(((TextField) scene.lookup("#titel")).getText()).isEqualTo("typ");

            assertThat(scene.lookup("#ort")).isInstanceOf(TextField.class);
            assertThat(((TextField) scene.lookup("#ort")).getText()).isEqualTo("ort");

            assertThat(scene.lookup("#datum")).isInstanceOf(DatePicker.class);
            LocalDateTime date = LocalDate.of(2020, 1, 1).atTime(8, 0);
            assertThat(((DatePicker) scene.lookup("#datum")).getValue()).isEqualTo(date.toLocalDate());

            assertThat(scene.lookup("#uhr")).isInstanceOf(TimeSpinner.class);
            assertThat(((TimeSpinner) scene.lookup("#uhr")).getValue()).isEqualTo(date.toLocalTime());

            assertThat(scene.lookup("#slider")).isInstanceOf(Slider.class);
            assertThat(((Slider) scene.lookup("#slider")).getValue()).isEqualTo(1);

            assertThat(scene.lookup("#hochamt")).isInstanceOf(CheckBox.class);
            assertThat(((CheckBox) scene.lookup("#hochamt")).isSelected()).isTrue();

            assertThat(scene.lookup("#smesse")).isInstanceOf(Label.class);
            assertThat(((Label) scene.lookup("#smesse")).getText()).isEqualTo(Sonstiges.SONSTIGES_STRING);

            assertThat(instance.getControl().isLocked()).isTrue();

            instance.hauptbildschirm(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.MESSE);
        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.FERIEN);
            instance.changePane(MainController.EnumPane.MESSE);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.MESSE);

        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.FERIEN);
            Mockito.verify(dialogs, Mockito.times(5)).warn(MainController.GESPERRT);
            Node button = scene.lookup("#button");
            if (button instanceof SplitMenuButton) {
                ObservableList<MenuItem> items = ((SplitMenuButton) button).getItems();
                items.stream().filter(menuItem -> menuItem.getId().equals("cancel")).findFirst().orElseThrow().fire();
            } else {
                Assertions.fail("could not find cancel");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        //SelectMesse
        assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.SELECT_MESSE);

        Platform.runLater(() -> {
            assertThat(scene.lookup("#list")).isInstanceOf(ListView.class);
            assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(1);
            instance.messe(null);
            assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.SELECT_MESSE);
        });
        WaitForAsyncUtils.waitForFxEvents();
        //Ferien
        Platform.runLater(() -> {
            instance.ferienplan(null);
            assertThat(scene.lookup("#table")).isInstanceOf(TableView.class);
            assertThat(((TableView<?>) scene.lookup("#table")).getColumns()).hasSize(2);
            assertThat(((TableView<?>) scene.lookup("#table")).getItems()).hasSize(1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.FERIEN);
        //StdMessen
        Platform.runLater(() -> {
            Node button = scene.lookup("#zurueck");
            if (button instanceof Button) {
                ((Button) button).fire();
            } else {
                Assertions.fail("could not find zurueck");
            }
            Mockito.when(dialogs.singleSelect(Mockito.anyList(), Mockito.contains(MainController.STANDARDMESSE_AUSWAEHLEN))).thenReturn(new Sonstiges());
            instance.standardmesse(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(scene.lookup("#smesse")).isInstanceOf(Text.class);
        assertThat(((Text) scene.lookup("#smesse")).getText()).doesNotContain("#");
        //SelectMedi
        Platform.runLater(() -> {
            Node button = scene.lookup("#abbrechen");
            if (button instanceof Button) {
                ((Button) button).fire();
            } else {
                Assertions.fail("could not find abbrechen");
            }
            instance.medi(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.SELECT_MEDI);
        assertThat(scene.lookup("#list")).isInstanceOf(ListView.class);
        assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(1);

        //Plan
        Einstellungen einstellungen = new Einstellungen();
        Mockito.when(pf.getSettings()).thenReturn(einstellungen);
        // disabled on ci because of wired async errors with the HTML-Editor
        // and with @RetryingTest(5) at the method is only occur occasionally
        if (!System.getProperty("java.home").contains("hostedtoolcache")) {
            Platform.runLater(() -> instance.generieren(null));
        }
        WaitForAsyncUtils.waitForFxEvents();
        //Info
        Platform.runLater(() -> {
            instance.info(null);
            assertThat(this.listTargetWindows()).hasSizeGreaterThan(1);
            this.listTargetWindows().forEach(w -> {
                if (w instanceof Stage && w != stage) {
                    ((Stage) w).close();
                }
            });
            assertThat(this.listTargetWindows()).isNotEmpty();
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialogs, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }
    //CHECKSTYLE:ON: MethodLength

    @DisabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*hostedtoolcache.*", disabledReason = "should not run on ci")
    @Test
    public void testAwtDesktop() throws IOException {
        DateienVerwalter.setInstance(dv);
        final File file = new File(System.getProperty("user.dir"));
        Mockito.when(dv.getSavePath()).thenReturn(file);
        Mockito.when(pf.getName()).thenReturn("");
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(new MainController(mainApplication, stage));
        instance = loader.getController();

        instance.log(null);
        instance.workingdir(null);
        instance.savepath(null);
        loader.setLocation(getClass().getResource("/view/head.fxml"));
        loader.load();
        ((MainController) loader.getController()).changePane(MainController.EnumPane.START);
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.START);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialogs, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }

    @DisabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*hostedtoolcache.*", disabledReason = "Robotcontext should not run on ci")
    @Test
    public void testChangeSpeicherort() {
        Dialogs.setDialogs(dialogs);
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        String titel = "LOL ";
        Mockito.when(pf.getName()).thenReturn(titel);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(new StandardMesse(DayOfWeek.MONDAY, 9, "00", "o", 0, "t")));

        FXMLLoader loader = new FXMLLoader();
        loader.setController(new MainController(mainApplication, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/head.fxml"));
        try {
            pane = loader.load();
            Platform.runLater(() -> stage.show());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(System.getProperty("user.home")).isNotEmpty();
        File f = new File(System.getProperty("user.home"), Speicherort.TEXTDATEI);
        final Path copy = Path.of(System.getProperty("user.home"), Speicherort.TEXTDATEI + "c");
        try {
            Files.copy(f.toPath(), copy, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        assertThat(stage.focusedProperty().getValue()).isTrue();

        loader = new FXMLLoader();
        loader.setController(new MainController(mainApplication, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/head.fxml"));
        try {
            pane = loader.load();
            Platform.runLater(() -> stage.show());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        WaitForAsyncUtils.waitForFxEvents();
        Scene scene = new Scene(pane);
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setTitle("MessdienerplanErsteller");
            instance.changePane(MainController.EnumPane.SELECT_MEDI);
        });
        WaitForAsyncUtils.waitForFxEvents();
        long first = System.currentTimeMillis();
        instance.speicherort(null);
        WaitForAsyncUtils.waitForFxEvents();
        long sec = System.currentTimeMillis();
        assertThat(sec - first).isLessThan(1000L);
        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.START);
            instance.speicherort(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        try {
            Files.copy(copy, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.delete(copy);
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        assertThat(stage.focusedProperty().getValue()).isFalse();
        robotContext().getKeyboardRobot().press(KeyCode.ESCAPE);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialogs, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }

    @Test
    public void testPfarrei() {
        Dialogs.setDialogs(dialogs);
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Einstellungen einstellungen = new Einstellungen();
        Mockito.when(pf.getSettings()).thenReturn(einstellungen);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(new StandardMesse(DayOfWeek.MONDAY, 9, "00", "o", 0, "t")));
        FXMLLoader loader = new FXMLLoader();

        loader.setController(new MainController(mainApplication, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/head.fxml"));
        try {
            pane = loader.load();
            Platform.runLater(() -> stage.show());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        WaitForAsyncUtils.waitForFxEvents();
        Scene scene = new Scene(pane);
        assertThat(System.getProperty("user.home")).isNotEmpty();
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setTitle("MessdienerplanErsteller");
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(stage.focusedProperty().getValue()).isTrue();
        Platform.runLater(() -> instance.changePane(MainController.EnumPane.START));
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(stage.focusedProperty().getValue()).isTrue();
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> instance.editPfarrei(null));
        WaitForAsyncUtils.waitForFxEvents();
        final Node lookup = this.listWindows().get(1).getScene().lookup("#table");
        assertThat(lookup).isInstanceOf(TableView.class);
        assertThat(((TableView<?>) lookup).getItems().get(0)).hasToString(pf.getStandardMessen().get(0).toString());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialogs, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }
}
