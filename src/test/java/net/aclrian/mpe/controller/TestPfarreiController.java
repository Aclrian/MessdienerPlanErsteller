package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import net.aclrian.mpe.Main;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.ReadFilePfarrei;
import net.aclrian.mpe.pfarrei.Setting;
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
import java.nio.file.Files;

public class TestPfarreiController extends ApplicationTest {
//PfarreiController.start(stage, e.getSavepath().getAbsolutePath(), this);

    @Mock
    private DateienVerwalter dv;
    @Mock
    private Main main;
    @Mock
    private Dialogs dialog;
    private Pane pane;
    private PfarreiController instance;
    private Scene scene;
    private Stage stage;

    @AfterClass
    public static void deleteFile() {
        File file = new File(System.getProperty("user.home"), "namesdfsdjklflös" + DateienVerwalter.PFARREDATEIENDUNG);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        scene = new Scene(pane, 1000, 1000);
        this.stage = stage;
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(dialog.chance(Mockito.any())).thenReturn(null);
        StandartMesse standartmesse = new StandartMesse("Mo", 8, "00", "o", 9, "t");
        StandartMesse standartmesse1 = new StandartMesse("Di", 5, "09", "o1", 5, "t2");
        Mockito.when(dialog.standartmesse(Mockito.isNull())).thenReturn(standartmesse, standartmesse1);
        FXMLLoader loader = new FXMLLoader();
        PfarreiController cont = new PfarreiController(stage, System.getProperty("user.home"), main);
        loader.setLocation(cont.getClass().getResource(PfarreiController.STANDARTMESSE_FXML));
        loader.setController(cont);
        Parent root;
        try {
            root = loader.load();
            scene = new Scene(root);
            Platform.runLater(() -> {
                stage.setScene(scene);

                stage.setTitle(Log.PROGRAMM_NAME);
                stage.setResizable(false);
                stage.show();
            });
            cont.afterstartup();
            WaitForAsyncUtils.waitForFxEvents();
            Assertions.assertThat(root.lookup("#table")).isInstanceOf(TableView.class);
            Assertions.assertThat(((TableView<?>) root.lookup("#table")).getItems()).hasSize(0);
            final Node neu = root.lookup("#neu");
            Platform.runLater(() -> {
                Assertions.assertThat(neu).isInstanceOf(Button.class);
                ((Button) neu).fire();
                ((Button) neu).fire();
            });
            WaitForAsyncUtils.waitForFxEvents();
            Assertions.assertThat(((TableView<StandartMesse>) root.lookup("#table")).getItems()).contains(standartmesse, standartmesse1);
            Assertions.assertThat(((TableView<StandartMesse>) root.lookup("#table")).getItems()).hasSize(2);
            final Node rm = root.lookup("#loeschen");
            Platform.runLater(() -> {
                Assertions.assertThat(rm).isInstanceOf(Button.class);
                ((Button) rm).fire();
            });
            WaitForAsyncUtils.waitForFxEvents();
            Assertions.assertThat(((TableView<StandartMesse>) root.lookup("#table")).getItems()).hasSize(2);
            Mockito.verify(dialog, Mockito.times(1)).warn(Mockito.eq(PfarreiController.NICHTS_AUSGEWAEHLT));
            ((TableView<?>) root.lookup("#table")).getSelectionModel().select(1);

            Platform.runLater(() -> {
                Assertions.assertThat(rm).isInstanceOf(Button.class);
                ((Button) rm).fire();
            });
            WaitForAsyncUtils.waitForFxEvents();
            Assertions.assertThat(((TableView<StandartMesse>) root.lookup("#table")).getItems()).hasSize(1);
            Assertions.assertThat(((TableView<StandartMesse>) root.lookup("#table")).getItems()).contains(standartmesse);

            final Node weiter = root.lookup("#weiterbtn");
            Platform.runLater(() -> {
                Assertions.assertThat(weiter).isInstanceOf(Button.class);
                ((Button) weiter).fire();
            });
            WaitForAsyncUtils.waitForFxEvents();

            scene = stage.getScene();
            Assertions.assertThat(scene.lookup("#table")).isNull();
            final Node zurueck = scene.lookup("#zurueckbtn");
            Platform.runLater(() -> {
                Assertions.assertThat(zurueck).isInstanceOf(Button.class);
                ((Button) zurueck).fire();
            });
            WaitForAsyncUtils.waitForFxEvents();
            Assertions.assertThat(scene.lookup("#table")).isNull();
            Platform.runLater(() -> {
                Assertions.assertThat(weiter).isInstanceOf(Button.class);
                ((Button) weiter).fire();
            });
            WaitForAsyncUtils.waitForFxEvents();
            scene = stage.getScene();
            Assertions.assertThat(scene.lookup("#settingTableView")).isInstanceOf(TableView.class);
            Assertions.assertThat(((TableView<Setting>) scene.lookup("#settingTableView")).getItems()).allMatch(setting ->
                    setting.getAnzDienen() == 0 && setting.getJahr() <= Messdaten.getMaxYear() && setting.getJahr() >= Messdaten.getMinYear());
            Assertions.assertThat(((TableView<Setting>) scene.lookup("#settingTableView")).getItems()).hasSize(Einstellungen.LENGHT - 2);
            Mockito.verify(dialog, Mockito.times(2)).open(Mockito.any(), Mockito.anyString(), Mockito.eq(PfarreiController.MEHR_INFORMATIONEN), Mockito.eq(PfarreiController.VERSTANDEN));
            final Setting setting = ((TableView<Setting>) scene.lookup("#settingTableView")).getItems().get(0);
            final Setting newSetting = new Setting(setting.getAttribut(), setting.getId(), 2);
            Mockito.when(dialog.chance(Mockito.eq(setting))).thenReturn(newSetting);
            Platform.runLater(() -> {
                scene.lookup("#settingTableView").requestFocus();
                scene.lookup("#settingTableView").requestFocus();
                ((TableView<Setting>) scene.lookup("#settingTableView")).getSelectionModel().select(0);
                ((TableView<Setting>) scene.lookup("#settingTableView")).getFocusModel().focus(0);
                Robot r = new Robot();
                r.keyPress(KeyCode.DOWN);
                r.keyPress(KeyCode.UP);
            });
            WaitForAsyncUtils.waitForFxEvents();
            Platform.runLater(() -> scene.lookup("#settingTableView").fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 2, false, false, false, false, false, false, false, false, false, false, null)));
            WaitForAsyncUtils.waitForFxEvents();
            Assertions.assertThat(((TableView<Setting>) scene.lookup("#settingTableView")).getItems().get(0)).isEqualTo(newSetting);
            Platform.runLater(() -> {
                Assertions.assertThat(scene.lookup("#medi")).isInstanceOf(Slider.class);
                ((Slider) scene.lookup("#medi")).setValue(2);
                Assertions.assertThat(scene.lookup("#leiter")).isInstanceOf(Slider.class);
                ((Slider) scene.lookup("#leiter")).setValue(4);
                Assertions.assertThat(scene.lookup("#name")).isInstanceOf(TextField.class);
                TextField nameFiled = (TextField) scene.lookup("#name");
                nameFiled.setText("namesdfsdjklflös");
                Assertions.assertThat(scene.lookup("#hochamt")).isInstanceOf(CheckBox.class);
                ((CheckBox) scene.lookup("#hochamt")).setSelected(true);
                Assertions.assertThat(scene.lookup("#save")).isInstanceOf(Button.class);
                ((Button) scene.lookup("#save")).fire();
            });
            WaitForAsyncUtils.waitForFxEvents();
            File file = new File(System.getProperty("user.home"), "namesdfsdjklflös" + DateienVerwalter.PFARREDATEIENDUNG);
            Assertions.assertThat(file).exists();
            Assertions.assertThat(Files.readString(file.toPath()).replace(System.getProperty("line.separator"), "\n")).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<XML>\n" +
                    "  <MpE-Creator LICENSE=\"MIT\">Aclrian</MpE-Creator>\n" +
                    "  <Body>\n" +
                    "    <Standartmessem>\n" +
                    "      <std_messe id=\"0\">\n" +
                    "        <tag>Mo</tag>\n" +
                    "        <std>8</std>\n" +
                    "        <min>00</min>\n" +
                    "        <ort>o</ort>\n" +
                    "        <anz>9</anz>\n" +
                    "        <typ>t</typ>\n" +
                    "      </std_messe>\n" +
                    "    </Standartmessem>\n" +
                    "  </Body>\n" +
                    "  <Einstellungen>\n" +
                    "    <hochaemter>1</hochaemter>\n" +
                    "    <setting Lleiter=\"0\">2</setting>\n" +
                    "    <setting Lleiter=\"1\">4</setting>\n" +
                    "    <setting year=\"0\">2</setting>\n" +
                    "    <setting year=\"1\">0</setting>\n" +
                    "    <setting year=\"2\">0</setting>\n" +
                    "    <setting year=\"3\">0</setting>\n" +
                    "    <setting year=\"4\">0</setting>\n" +
                    "    <setting year=\"5\">0</setting>\n" +
                    "    <setting year=\"6\">0</setting>\n" +
                    "    <setting year=\"7\">0</setting>\n" +
                    "    <setting year=\"8\">0</setting>\n" +
                    "    <setting year=\"9\">0</setting>\n" +
                    "    <setting year=\"10\">0</setting>\n" +
                    "    <setting year=\"11\">0</setting>\n" +
                    "    <setting year=\"12\">0</setting>\n" +
                    "    <setting year=\"13\">0</setting>\n" +
                    "    <setting year=\"14\">0</setting>\n" +
                    "    <setting year=\"15\">0</setting>\n" +
                    "    <setting year=\"16\">0</setting>\n" +
                    "    <setting year=\"17\">0</setting>\n" +
                    "    <setting year=\"18\">0</setting>\n" +
                    "  </Einstellungen>\n" +
                    "</XML>\n");
            Pfarrei pfRead = ReadFilePfarrei.getPfarrei(file.getAbsolutePath());
            Assertions.assertThat(pfRead.getStandardMessen()).containsExactlyInAnyOrder(standartmesse, new Sonstiges());
            Assertions.assertThat(pfRead.zaehlenHochaemterMit()).isTrue();
            Assertions.assertThat(pfRead.getName()).isEqualTo("namesdfsdjklflös");
            Assertions.assertThat(pfRead.getSettings().getSettings()).hasSize(Einstellungen.LENGHT - 2);
            Einstellungen einst = new Einstellungen();
            einst.editMaxDienen(false, 2);
            einst.editMaxDienen(true, 4);
            einst.editiereYear(0, 2);
            Assertions.assertThat(pfRead.getSettings().getDaten(0).toString()).isEqualTo("MAX 0 2");
            Assertions.assertThat(pfRead.getSettings().getDaten(1).toString()).isEqualTo("MAX 1 4");
            Assertions.assertThat(pfRead.getSettings().getSettings()).containsOnlyOnce(newSetting);
            Assertions.assertThat(pfRead.getSettings().getSettings()).allMatch(s -> s.equals(newSetting)|| s.getAnzDienen() == 0);
            Files.delete(file.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }
}