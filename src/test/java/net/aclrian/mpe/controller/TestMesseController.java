package net.aclrian.mpe.controller; //NOPMD - suppressed ExcessiveImports - for test purpose

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.fx.ATilePane;
import net.aclrian.fx.TimeSpinner;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messdiener.Messverhalten;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateUtil;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMesseController extends ApplicationTest {

    @Mock
    private DateienVerwalter dv;
    @Mock
    private MainController mc;
    @Mock
    private Dialogs dialog;
    @Mock
    private ArrayList<Messe> messen;
    private Pane pane;
    private MesseController instance;
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

    @Test
    public void test() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Messdiener messdiener = Mockito.mock(Messdiener.class);
        Mockito.when(messdiener.getVorname()).thenReturn("v");
        Mockito.when(messdiener.getNachname()).thenReturn("n");
        Mockito.when(messdiener.getEintritt()).thenReturn(DateUtil.getCurrentYear());
        Mockito.when(messdiener.istLeiter()).thenReturn(false);
        Messverhalten mv = new Messverhalten();
        Mockito.when(messdiener.getDienverhalten()).thenReturn(mv);
        Messdaten md = Mockito.mock(Messdaten.class);
        Mockito.when(messdiener.getMessdaten()).thenReturn(md);
        Mockito.when(md.vorzeitigEinteilen(Mockito.any(), Mockito.anyBoolean())).thenReturn(true);
        Mockito.when(dv.getMessdiener()).thenReturn(Collections.singletonList(messdiener));
        StandardMesse standardMesse = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "o", 2, "t");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standardMesse));
        Mockito.when(mc.getMessen()).thenReturn(messen);
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.MESSE.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        ((TextField) scene.lookup("#titel")).setText("title");
        ((TextField) scene.lookup("#ort")).setText("ort");
        final LocalDate now = LocalDate.now();
        String dateS = now.format(DateUtil.DATE);
        ((DatePicker) scene.lookup("#datum")).setValue(now);
        LocalTime time = LocalTime.now();
        String clock = time.format(DateUtil.TIME);
        ((TimeSpinner) scene.lookup("#uhr")).getValueFactory().setValue(time);
        ((CheckBox) scene.lookup("#hochamt")).setSelected(true);
        assertThat(scene.lookup("#smbearbeiten")).isInstanceOf(Button.class);
        Mockito.when(dialog.singleSelect(Mockito.anyList(), Mockito.eq(MesseController.STANDARDMESSE_AUSWAEHLEN))).thenReturn(standardMesse);
        Platform.runLater(() -> {

            ((TextField) scene.lookup("#titel")).setText("");
            ((SplitMenuButton) scene.lookup("#button")).fire();
            Mockito.verify(messen, Mockito.times(0)).add(Mockito.any());

            ((TextField) scene.lookup("#titel")).setText("title");

            ((TextField) scene.lookup("#ort")).setText("");
            ((SplitMenuButton) scene.lookup("#button")).fire();
            Mockito.verify(messen, Mockito.times(0)).add(Mockito.any());

            ((TextField) scene.lookup("#ort")).setText("ort");

            //uhr cannot set null

            ((TimeSpinner) scene.lookup("#uhr")).getValueFactory().setValue(time);

            ((DatePicker) scene.lookup("#datum")).setValue(null);
            ((SplitMenuButton) scene.lookup("#button")).fire();
            Mockito.verify(messen, Mockito.times(0)).add(Mockito.any());
            ((DatePicker) scene.lookup("#datum")).setValue(now);

            ((Button) scene.lookup("#smbearbeiten")).fire();
            ((Slider) scene.lookup("#slider")).setValue(1);
            ((SplitMenuButton) scene.lookup("#button")).fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(messen, Mockito.times(1)).add(Mockito.argThat(argument -> {
            LocalDateTime date = now.atTime(time);

            assertThat(dateS + " " + clock).isEqualTo(DateUtil.DATE_AND_TIME.format(date));
            Messe m = new Messe(true, 1, date, "ort", "title", standardMesse);
            return argument.equals(m);
        }));
        assertThat(((ATilePane) scene.lookup("#list")).getChildren()).anyMatch(node -> {
            if (node instanceof ATilePane.ACheckBox && ((ATilePane.ACheckBox) node).getMessdiener().equals(messdiener)) {
                ((ATilePane.ACheckBox) node).setSelected(true);
                return true;
            }
            return false;
        });
        ((SplitMenuButton) scene.lookup("#button")).getItems().stream().filter(menuItem -> menuItem.getId().equals("saveNew")).findFirst().orElseThrow().fire();
        Mockito.verify(messen, Mockito.times(1)).add(Mockito.argThat(argument -> {
            LocalDateTime date = now.atTime(time);
            assertThat(dateS + " " + clock).isEqualTo(DateUtil.DATE_AND_TIME.format(date));
            Messe m = new Messe(true, 1, now.atTime(time), "ort", "title", standardMesse);
            m.vorzeitigEinteilen(messdiener);
            return argument.equals(m);
        }));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialog, Mockito.times(0)).error(Mockito.any(), Mockito.anyString());
    }
}
