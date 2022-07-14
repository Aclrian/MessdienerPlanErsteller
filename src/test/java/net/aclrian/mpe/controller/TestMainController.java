package net.aclrian.mpe.controller;

import javafx.application.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import net.aclrian.fx.*;
import net.aclrian.mpe.*;
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
import java.nio.file.*;
import java.text.*;
import java.time.format.*;
import java.util.*;

public class TestMainController extends ApplicationTest {

    @Mock
    Dialogs dialogs;
    @Mock
    DateienVerwalter dv;
    @Mock
    Pfarrei pf;
    private Pane pane;
    private MainController instance;
    @Mock
    private Main main;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.setResizable(true);
        this.stage = stage;

        main = Mockito.mock(Main.class);
        dialogs = Mockito.mock(Dialogs.class);
        dv = Mockito.mock(DateienVerwalter.class);
        pf = Mockito.mock(Pfarrei.class);
    }

    @Test
    public void testEachEnumPane() {
        Dialogs.setDialogs(dialogs);
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        String titel = "LOL ";
        Mockito.when(pf.getName()).thenReturn(titel);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(new StandartMesse("Mo", 9, "00", "o", 0, "t")));
        FXMLLoader loader = new FXMLLoader();

        loader.setController(new MainController(main, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/AAhaupt.fxml"));
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
                Assertions.assertThat(((Label) name).getText().equalsIgnoreCase(titel)).isTrue();
            } else {
                Assertions.fail("name not found");
            }
            instance.changePane(MainController.EnumPane.START);
            Assertions.assertThat(name.isVisible()).isTrue();
        });
        WaitForAsyncUtils.waitForFxEvents();
        //Messdiener
        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.MESSDIENER);
            Assertions.assertThat(stage.getScene().lookup("#name")).isNotNull();
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
                Mockito.when(messdiener.getNachnname()).thenReturn("n");
                Mockito.when(messdiener.getGeschwister()).thenReturn(new String[0]);
                Mockito.when(messdiener.getFreunde()).thenReturn(new String[0]);
                Mockito.when(messdiener.getEintritt()).thenReturn(Messdaten.getMaxYear());
                Mockito.when(messdiener.istLeiter()).thenReturn(false);
                Mockito.when(messdiener.getEmail()).thenReturn("a@a.de");
                Messverhalten mv = new Messverhalten();
                Mockito.when(messdiener.getDienverhalten()).thenReturn(mv);
                instance.changePaneMessdiener(messdiener);

                Mockito.when(dv.getMessdiener()).thenReturn(Collections.singletonList(messdiener));
                final Messdaten md = Mockito.mock(Messdaten.class);//new Messdaten(messdiener);
                Mockito.when(messdiener.getMessdaten()).thenReturn(md);

                Assertions.assertThat(scene.lookup("#vorname")).isInstanceOf(TextField.class);
                Assertions.assertThat(((TextField) scene.lookup("#vorname")).getText()).isEqualTo("v");
                Assertions.assertThat(scene.lookup("#name")).isInstanceOf(TextField.class);
                Assertions.assertThat(((TextField) scene.lookup("#name")).getText()).isEqualTo("n");
                Assertions.assertThat(scene.lookup("#geschwie")).isInstanceOf(ListView.class);
                Assertions.assertThat(((ListView<?>) scene.lookup("#geschwie")).getItems().size()).isEqualTo(1);
                Assertions.assertThat(scene.lookup("#freunde")).isInstanceOf(ListView.class);
                Assertions.assertThat(((ListView<?>) scene.lookup("#freunde")).getItems()).hasSize(1);
                Assertions.assertThat(scene.lookup("#eintritt")).isInstanceOf(Slider.class);
                Assertions.assertThat(((Slider) scene.lookup("#eintritt")).getValue()).isEqualTo(Messdaten.getMaxYear());
                Assertions.assertThat(scene.lookup("#leiter")).isInstanceOf(CheckBox.class);
                Assertions.assertThat(((CheckBox) scene.lookup("#leiter")).isSelected()).isFalse();
                Assertions.assertThat(scene.lookup("#email")).isInstanceOf(TextField.class);
                Assertions.assertThat(((TextField) scene.lookup("#email")).getText()).isEqualTo("a@a.de");
                Assertions.assertThat(scene.lookup("#table")).isInstanceOf(TableView.class);
                final TableView<?> table = (TableView<?>) scene.lookup("#table");
                Assertions.assertThat(table.getItems()).hasSize(1);
                Assertions.assertThat(((KannWelcheMesse) table.getItems().get(0)).isKanndann()).isFalse();

                Assertions.assertThat(instance.getControl().isLocked()).isTrue();

            } else {
                Assertions.fail("could not find cancel");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        //Messe
        final String date = "01-01-2020 08:00";
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

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                try {
                    Mockito.when(m.getDate()).thenReturn(df.parse(date));
                } catch (ParseException e) {
                    Assertions.fail(e.getMessage(), e);
                }
                Mockito.when(m.getAnzMessdiener()).thenReturn(1);
                Mockito.when(m.isHochamt()).thenReturn(true);
                Mockito.when(m.getStandardMesse()).thenReturn(new Sonstiges());
                Mockito.when(m.htmlAusgeben()).thenReturn("123456</html>");

                instance.changePaneMesse(m);
            } else {
                Assertions.fail("could not find cancel");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            Assertions.assertThat(scene.lookup("#titel")).isInstanceOf(TextField.class);
            Assertions.assertThat(((TextField) scene.lookup("#titel")).getText()).isEqualTo("typ");

            Assertions.assertThat(scene.lookup("#ort")).isInstanceOf(TextField.class);
            Assertions.assertThat(((TextField) scene.lookup("#ort")).getText()).isEqualTo("ort");

            Assertions.assertThat(scene.lookup("#datum")).isInstanceOf(DatePicker.class);
            Assertions.assertThat(((DatePicker) scene.lookup("#datum")).getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).contains(date.substring(0, 6));

            Assertions.assertThat(scene.lookup("#uhr")).isInstanceOf(TimeSpinner.class);
            Assertions.assertThat(((TimeSpinner) scene.lookup("#uhr")).getValue().format(DateTimeFormatter.ofPattern("HH:mm"))).contains(date.substring(11));

            Assertions.assertThat(scene.lookup("#slider")).isInstanceOf(Slider.class);
            Assertions.assertThat(((Slider) scene.lookup("#slider")).getValue()).isEqualTo(1);

            Assertions.assertThat(scene.lookup("#hochamt")).isInstanceOf(CheckBox.class);
            Assertions.assertThat(((CheckBox) scene.lookup("#hochamt")).isSelected()).isTrue();

            Assertions.assertThat(scene.lookup("#smesse")).isInstanceOf(Label.class);
            Assertions.assertThat(((Label) scene.lookup("#smesse")).getText()).isEqualTo(Sonstiges.SONSTIGES_STRING);

            Assertions.assertThat(instance.getControl().isLocked()).isTrue();

            instance.hauptbildschirm(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.MESSE);
        Platform.runLater(() -> {
            instance.changePane(MainController.EnumPane.FERIEN);
            instance.changePane(MainController.EnumPane.MESSE);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.MESSE);

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
        Assertions.assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.SELECT_MESSE);

        Platform.runLater(() -> {
            Assertions.assertThat(scene.lookup("#list")).isInstanceOf(ListView.class);
            Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems().size()).isEqualTo(1);
            instance.messe(null);
            Assertions.assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.SELECT_MESSE);
        });

        //Ferien
        Platform.runLater(() -> {
            instance.ferienplan(null);
            Assertions.assertThat(scene.lookup("#table")).isInstanceOf(TableView.class);
            Assertions.assertThat(((TableView<?>) scene.lookup("#table")).getColumns().size()).isEqualTo(2);
            Assertions.assertThat(((TableView<?>) scene.lookup("#table")).getItems().size()).isEqualTo(1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.FERIEN);
        //StdMessen
        Platform.runLater(() -> {
            Node button = scene.lookup("#zurueck");
            if (button instanceof Button) {
                ((Button) button).fire();
            } else {
                Assertions.fail("could not find zurueck");
            }
            Mockito.when(dialogs.singleSelect(Mockito.anyList(), Mockito.contains(MainController.STANDARTMESSE_AUSWAEHLEN))).thenReturn(new Sonstiges());
            instance.standardmesse(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(scene.lookup("#smesse")).isInstanceOf(Text.class);
        Assertions.assertThat(((Text) scene.lookup("#smesse")).getText()).doesNotContain("#");
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
        Assertions.assertThat(instance.getEnumPane()).isEqualTo(MainController.EnumPane.SELECT_MEDI);
        Assertions.assertThat(scene.lookup("#list")).isInstanceOf(ListView.class);
        Assertions.assertThat(((ListView<?>) scene.lookup("#list")).getItems()).hasSize(1);

        //Plan
        Einstellungen einstellungen = new Einstellungen();
        Mockito.when(pf.getSettings()).thenReturn(einstellungen);

        Platform.runLater(() -> instance.generieren(null));

        //Info
        Platform.runLater(() -> {
            instance.info(null);
            Assertions.assertThat(this.listTargetWindows().size()).isGreaterThan(1);
            this.listTargetWindows().forEach(w -> {
                if (w instanceof Stage && w != stage) {
                    ((Stage) w).close();
                }
            });
            Assertions.assertThat(this.listTargetWindows().size()).isGreaterThan(0);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Ignore("should not run on ci")
    @Test
    public void testAwtDesktop() {
        DateienVerwalter.setInstance(dv);
        final File file = new File(System.getProperty("user.dir"));
        Mockito.when(dv.getSavepath()).thenReturn(file);

        FXMLLoader loader = new FXMLLoader();
        loader.setController(new MainController(main, stage));
        instance = loader.getController();

        instance.log(null);
        instance.workingdir(null);
        instance.savepath(null);
        Assertions.assertThat(true).isTrue();
    }
    
    @Ignore("Robotcontext should not run on ci")
    @Test
    public void testChangeSpeicherort() {
        Dialogs.setDialogs(dialogs);
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        String titel = "LOL ";
        Mockito.when(pf.getName()).thenReturn(titel);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(new StandartMesse("Mo", 9, "00", "o", 0, "t")));

        FXMLLoader loader = new FXMLLoader();
        loader.setController(new MainController(main, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/AAhaupt.fxml"));
        try {
            pane = loader.load();
            Platform.runLater(() -> stage.show());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(System.getProperty("user.home").isEmpty()).isFalse();
        File f = new File(System.getProperty("user.home"), Speicherort.TEXTDATEI);
        final Path copy = Path.of(System.getProperty("user.home"), Speicherort.TEXTDATEI + "c");
        try {
            Files.copy(f.toPath(), copy, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Assertions.assertThat(stage.focusedProperty().getValue()).isTrue();

        loader.setController(new MainController(main, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/AAhaupt.fxml"));
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
        long sec = System.currentTimeMillis();
        Assertions.assertThat(sec - first).isLessThan(1000L);
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
        Assertions.assertThat(stage.focusedProperty().getValue()).isFalse();
        robotContext().getKeyboardRobot().press(KeyCode.ESCAPE);
    }

    @Test
    public void testPfarrei() {
        Dialogs.setDialogs(dialogs);
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Einstellungen einstellungen = new Einstellungen();
        Mockito.when(pf.getSettings()).thenReturn(einstellungen);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(new StandartMesse("Mo", 9, "00", "o", 0, "t")));
        FXMLLoader loader = new FXMLLoader();

        loader.setController(new MainController(main, stage));
        instance = loader.getController();
        loader.setLocation(instance.getClass().getResource("/view/AAhaupt.fxml"));
        try {
            pane = loader.load();
            Platform.runLater(() -> stage.show());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        WaitForAsyncUtils.waitForFxEvents();
        Scene scene = new Scene(pane);
        Assertions.assertThat(System.getProperty("user.home").isEmpty()).isFalse();
        Platform.runLater(() -> {
            stage.setScene(scene);
            stage.setTitle("MessdienerplanErsteller");
        });
        Assertions.assertThat(stage.focusedProperty().getValue()).isTrue();
        Platform.runLater(() -> instance.changePane(MainController.EnumPane.START));
        Assertions.assertThat(stage.focusedProperty().getValue()).isTrue();
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> instance.editPfarrei(null));
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(stage.focusedProperty().getValue()).isFalse();
        final Node lookup = this.listWindows().get(1).getScene().lookup("#table");
        Assertions.assertThat(lookup).isInstanceOf(TableView.class);
        Assertions.assertThat(((TableView<?>) lookup).getItems().get(0).toString()).isEqualTo(pf.getStandardMessen().get(0).toString());
    }
}
