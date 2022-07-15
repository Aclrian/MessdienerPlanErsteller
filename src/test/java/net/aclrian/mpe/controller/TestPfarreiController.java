package net.aclrian.mpe.controller;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.robot.*;
import javafx.stage.*;
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
import java.time.*;

public class TestPfarreiController extends ApplicationTest {

    @Mock
    private DateienVerwalter dv;
    @Mock
    private Main main;
    @Mock
    private Dialogs dialog;
    private PfarreiController instance;
    private Scene scene;
    private Stage stage;
    private AutoCloseable openMocks;

    @AfterClass
    public static void deleteFile() {
        File file = new File(System.getProperty("user.home"), "name" + DateienVerwalter.PFARREDATEIENDUNG);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        scene = new Scene(pane, 10, 10);
        this.stage = stage;
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
    public void test() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(dialog.chance(Mockito.any())).thenReturn(null);
        StandartMesse standartmesse = new StandartMesse(DayOfWeek.MONDAY, 8, "00", "o", 9, "t");
        StandartMesse standartmesse1 = new StandartMesse(DayOfWeek.TUESDAY, 5, "09", "o1", 5, "t2");
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
            Mockito.verify(dialog, Mockito.times(1)).warn(PfarreiController.NICHTS_AUSGEWAEHLT);
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
            Mockito.when(dialog.chance(setting)).thenReturn(newSetting);
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
            Assertions.assertThat(Files.readString(file.toPath()).replace(System.getProperty("line.separator"), "\n")).isEqualTo("""
                    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                    <XML>
                      <MpE-Creator LICENSE="MIT">Aclrian</MpE-Creator>
                      <Body>
                        <Standartmessem>
                          <std_messe id="0">
                            <tag>Mo</tag>
                            <std>8</std>
                            <min>00</min>
                            <ort>o</ort>
                            <anz>9</anz>
                            <typ>t</typ>
                          </std_messe>
                        </Standartmessem>
                      </Body>
                      <Einstellungen>
                        <hochaemter>1</hochaemter>
                        <setting Lleiter="0">2</setting>
                        <setting Lleiter="1">4</setting>
                        <setting year="0">2</setting>
                        <setting year="1">0</setting>
                        <setting year="2">0</setting>
                        <setting year="3">0</setting>
                        <setting year="4">0</setting>
                        <setting year="5">0</setting>
                        <setting year="6">0</setting>
                        <setting year="7">0</setting>
                        <setting year="8">0</setting>
                        <setting year="9">0</setting>
                        <setting year="10">0</setting>
                        <setting year="11">0</setting>
                        <setting year="12">0</setting>
                        <setting year="13">0</setting>
                        <setting year="14">0</setting>
                        <setting year="15">0</setting>
                        <setting year="16">0</setting>
                        <setting year="17">0</setting>
                        <setting year="18">0</setting>
                      </Einstellungen>
                    </XML>
                    """);
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
            Assertions.assertThat(pfRead.getSettings().getSettings()).allMatch(s -> s.equals(newSetting) || s.getAnzDienen() == 0);
            Files.delete(file.toPath());
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
    }
}