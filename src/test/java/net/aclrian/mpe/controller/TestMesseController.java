package net.aclrian.mpe.controller;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.aclrian.fx.*;
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
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

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
        scene = new Scene(pane, 100, 100);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        openMocks = MockitoAnnotations.openMocks(this);
    }

    @After
    public void close() throws Exception {
        openMocks.close();
    }

    @Test
    public void test() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Messdiener messdiener = Mockito.mock(Messdiener.class);
        Mockito.when(messdiener.getVorname()).thenReturn("v");
        Mockito.when(messdiener.getNachnname()).thenReturn("n");
        Mockito.when(messdiener.getEintritt()).thenReturn(Messdaten.getMaxYear());
        Mockito.when(messdiener.istLeiter()).thenReturn(false);
        Messverhalten mv = new Messverhalten();
        Mockito.when(messdiener.getDienverhalten()).thenReturn(mv);
        Messdaten md = Mockito.mock(Messdaten.class);
        Mockito.when(messdiener.getMessdaten()).thenReturn(md);
        Mockito.when(md.einteilenVorzeitig(Mockito.any(), Mockito.anyBoolean())).thenReturn(true);
        Mockito.when(dv.getMessdiener()).thenReturn(Collections.singletonList(messdiener));
        StandartMesse standartMesse = new StandartMesse(DayOfWeek.MONDAY, 8, "00", "o", 2, "t");
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standartMesse));
        Mockito.when(mc.getMessen()).thenReturn(messen);
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.MESSE.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterstartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        ((TextField) scene.lookup("#titel")).setText("title");
        ((TextField) scene.lookup("#ort")).setText("ort");
        final LocalDate now = LocalDate.now();
        String dateS = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        ((DatePicker) scene.lookup("#datum")).setValue(now);
        LocalTime time = LocalTime.now();
        String clock = time.format(DateTimeFormatter.ofPattern("HH:mm"));
        ((TimeSpinner) scene.lookup("#uhr")).getValueFactory().setValue(time);
        ((CheckBox) scene.lookup("#hochamt")).setSelected(true);
        Assertions.assertThat(scene.lookup("#smbearbeiten")).isInstanceOf(Button.class);
        Mockito.when(dialog.singleSelect(Mockito.anyList(), Mockito.eq(MesseController.STANDARTMESSE_AUSWAEHLEN))).thenReturn(standartMesse);
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
            Date date = Date.from(now.atTime(time).atZone(ZoneId.systemDefault()).toInstant());
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Assertions.assertThat(dateS + " " + clock).isEqualTo(df.format(date));
            Messe m = new Messe(true, 1, now.atTime(time), "ort", "title", standartMesse);
            return argument.equals(m);
        }));
        Assertions.assertThat(((ATilePane) scene.lookup("#list")).getChildren()).anyMatch(node -> {
            if(node instanceof ATilePane.ACheckBox && ((ATilePane.ACheckBox) node).getMessdiener().equals(messdiener)){
                ((ATilePane.ACheckBox) node).setSelected(true);
                return true;
            }
            return false;
        });
        ((SplitMenuButton) scene.lookup("#button")).getItems().stream().filter(menuItem -> menuItem.getId().equals("saveNew")).findFirst().orElseThrow().fire();
        Mockito.verify(messen, Mockito.times(1)).add(Mockito.argThat(argument -> {
            Date date = Date.from(now.atTime(time).atZone(ZoneId.systemDefault()).toInstant());
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Assertions.assertThat(dateS + " " + clock).isEqualTo(df.format(date));
            Messe m = new Messe(true, 1, now.atTime(time), "ort", "title", standartMesse);
            m.vorzeitigEiteilen(messdiener);
            return argument.equals(m);
        }));
    }
}