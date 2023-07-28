package net.aclrian.mpe.controller; //NOPMD - suppressed ExcessiveImports - for test purpose

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
import net.aclrian.mpe.MainApplication;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.ReadFilePfarrei;
import net.aclrian.mpe.pfarrei.Setting;
import net.aclrian.mpe.utils.DateUtil;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPfarreiController extends ApplicationTest {

    @Mock
    private DateienVerwalter dv;
    @Mock
    private MainApplication mainApplication;
    @Mock
    private Dialogs dialog;
    private PfarreiController instance;
    private Scene scene;
    private Stage stage;
    private AutoCloseable openMocks;

    @AfterAll
    public static void deleteFile() {
        File file = new File(System.getProperty("user.home"), "name" + DateienVerwalter.PFARREI_DATEIENDUNG);
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
        stage.setHeight(10);
        stage.setWidth(10);
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

    //CHECKSTYLE:OFF: MethodLength
    @SuppressWarnings("unchecked")
    @Test
    void test() { //NOPMD - suppressed NcssCount - for test purpose
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(dialog.chance(Mockito.any())).thenReturn(null);
        StandardMesse standardmesse = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "o", 9, "t");
        StandardMesse standardmesse1 = new StandardMesse(DayOfWeek.TUESDAY, 5, "09", "o1", 5, "t2");
        Mockito.when(dialog.standardmesse(Mockito.isNull())).thenReturn(standardmesse, standardmesse1);
        FXMLLoader loader = new FXMLLoader();
        PfarreiController cont = new PfarreiController(stage, System.getProperty("user.home"), mainApplication);
        loader.setLocation(cont.getClass().getResource(PfarreiController.STANDARDMESSE_FXML));
        loader.setController(cont);
        Parent root = null;
        try {
            root = loader.load();
            scene = new Scene(root, 10, 10);
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Platform.runLater(() -> {
            stage.setScene(scene);

            stage.setTitle(MPELog.PROGRAMM_NAME);
            stage.setResizable(false);
            stage.show();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(root).isNotNull();
        assertThat(scene).isNotNull();
        cont.afterStartup();
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(root.lookup("#table")).isInstanceOf(TableView.class);
        assertThat(((TableView<?>) root.lookup("#table")).getItems()).isEmpty();
        final Node neu = root.lookup("#neu");
        Platform.runLater(() -> {
            assertThat(neu).isInstanceOf(Button.class);
            ((Button) neu).fire();
            ((Button) neu).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((TableView<StandardMesse>) root.lookup("#table")).getItems()).contains(standardmesse, standardmesse1);
        assertThat(((TableView<StandardMesse>) root.lookup("#table")).getItems()).hasSize(2);
        final Node rm = root.lookup("#loeschen");
        Platform.runLater(() -> {
            assertThat(rm).isInstanceOf(Button.class);
            ((Button) rm).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((TableView<StandardMesse>) root.lookup("#table")).getItems()).hasSize(2);
        Mockito.verify(dialog, Mockito.times(1)).warn(PfarreiController.NICHTS_AUSGEWAEHLT);
        ((TableView<?>) root.lookup("#table")).getSelectionModel().select(1);

        Platform.runLater(() -> {
            assertThat(rm).isInstanceOf(Button.class);
            ((Button) rm).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((TableView<StandardMesse>) root.lookup("#table")).getItems()).hasSize(1);
        assertThat(((TableView<StandardMesse>) root.lookup("#table")).getItems()).contains(standardmesse);

        final Node weiter = root.lookup("#weiterbtn");
        Platform.runLater(() -> {
            assertThat(weiter).isInstanceOf(Button.class);
            ((Button) weiter).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        scene = stage.getScene();
        assertThat(scene.lookup("#table")).isNull();
        final Node zurueck = scene.lookup("#zurueckbtn");
        Platform.runLater(() -> {
            assertThat(zurueck).isInstanceOf(Button.class);
            ((Button) zurueck).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(scene.lookup("#table")).isNull();
        Platform.runLater(() -> {
            assertThat(weiter).isInstanceOf(Button.class);
            ((Button) weiter).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        scene = stage.getScene();
        assertThat(scene.lookup("#settingTableView")).isInstanceOf(TableView.class);
        assertThat(((TableView<Setting>) scene.lookup("#settingTableView")).getItems()).allMatch(setting ->
                setting.anzahlDienen() == 0 && setting.getJahr() <= DateUtil.getCurrentYear() && setting.getJahr() >= DateUtil.getYearCap());
        assertThat(((TableView<Setting>) scene.lookup("#settingTableView")).getItems()).hasSize(Einstellungen.LENGTH - 2);
        Mockito.verify(dialog, Mockito.times(2))
                .open(Mockito.any(), Mockito.anyString(), Mockito.eq(PfarreiController.MEHR_INFORMATIONEN), Mockito.eq(PfarreiController.VERSTANDEN));
        final Setting setting = ((TableView<Setting>) scene.lookup("#settingTableView")).getItems().get(0);
        final Setting newSetting = new Setting(setting.attribut(), setting.id(), 2);
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
        Platform.runLater(() -> scene.lookup("#settingTableView").fireEvent(
                new MouseEvent(
                    MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                    MouseButton.PRIMARY, 2, false, false, false, false, false,
                    false, false, false, false, false, null
                )
        ));
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((TableView<Setting>) scene.lookup("#settingTableView")).getItems().get(0)).isEqualTo(newSetting);
        Platform.runLater(() -> {
            assertThat(scene.lookup("#medi")).isInstanceOf(Slider.class);
            ((Slider) scene.lookup("#medi")).setValue(2);
            assertThat(scene.lookup("#leiter")).isInstanceOf(Slider.class);
            ((Slider) scene.lookup("#leiter")).setValue(4);
            assertThat(scene.lookup("#name")).isInstanceOf(TextField.class);
            TextField nameFiled = (TextField) scene.lookup("#name");
            nameFiled.setText("namesdfsdjklflös");
            assertThat(scene.lookup("#hochamt")).isInstanceOf(CheckBox.class);
            ((CheckBox) scene.lookup("#hochamt")).setSelected(true);
            assertThat(scene.lookup("#save")).isInstanceOf(Button.class);
            ((Button) scene.lookup("#save")).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        File file = new File(System.getProperty("user.home"), "namesdfsdjklflös" + DateienVerwalter.PFARREI_DATEIENDUNG);
        assertThat(file).exists();
        try {
            String doW = DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
            assertThat(Files.readString(file.toPath()).replace(System.getProperty("line.separator"), "\n")).isEqualTo("""
                    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                    <XML>
                        <MpE-Creator LICENSE="MIT">Aclrian</MpE-Creator>
                        <Body>
                            <Standardmessen>
                                <std_messe id="0">
                                    <tag>"""
                    + doW + """
                    </tag>
                                    <std>8</std>
                                    <min>00</min>
                                    <ort>o</ort>
                                    <anz>9</anz>
                                    <typ>t</typ>
                                </std_messe>
                            </Standardmessen>
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
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Pfarrei pfRead = null;
        try {
            pfRead = ReadFilePfarrei.getPfarrei(file.getAbsolutePath());
        } catch (Exception e) {
            Assertions.fail(e.getMessage(), e);
        }
        assertThat(pfRead).isNotNull();
        assertThat(pfRead.getStandardMessen()).containsExactlyInAnyOrder(standardmesse, new Sonstiges());
        assertThat(pfRead.zaehlenHochaemterMit()).isTrue();
        assertThat(pfRead.getName()).isEqualTo("namesdfsdjklflös");
        assertThat(pfRead.getSettings().getSettings()).hasSize(Einstellungen.LENGTH - 2);
        Einstellungen einst = new Einstellungen();
        einst.editMaxDienen(false, 2);
        einst.editMaxDienen(true, 4);
        einst.editiereYear(0, 2);
        assertThat(pfRead.getSettings().getDaten(0)).hasToString("MAX 0 2");
        assertThat(pfRead.getSettings().getDaten(1)).hasToString("MAX 1 4");
        assertThat(pfRead.getSettings().getSettings()).containsOnlyOnce(newSetting);
        assertThat(pfRead.getSettings().getSettings()).allMatch(s -> s.equals(newSetting) || s.anzahlDienen() == 0);
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }
    //CHECKSTYLE:ON: MethodLength
}
