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
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class TestStandardmesseController extends ApplicationTest {

    private final StandardMesse StandardMesse = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "o", 2, "t");
    @Mock
    private DateienVerwalter dv;
    @Mock
    private MainController mc;
    @Mock
    private Dialogs dialog;
    private Pane pane;
    private StandardmesseController instance;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        mc = Mockito.mock(MainController.class);
        dv = Mockito.mock(DateienVerwalter.class);
        dialog = Mockito.mock(Dialogs.class);
    }

    @Test
    public void test() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        File f = new File(System.getProperty("user.home"));
        Mockito.when(dv.getSavePath()).thenReturn(f);
        Messdiener m1 = Mockito.mock(Messdiener.class);
        Messdiener m2 = Mockito.mock(Messdiener.class);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(StandardMesse));
        final Messverhalten mv1 = new Messverhalten();
        Mockito.when(m1.getDienverhalten()).thenReturn(mv1);
        Mockito.when(m1.toString()).thenReturn("a");
        final Messverhalten mv2 = new Messverhalten();
        mv2.editiereBestimmteMesse(StandardMesse, true);
        Mockito.when(m2.getDienverhalten()).thenReturn(mv2);
        Mockito.when(m2.toString()).thenReturn("b");
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, m2));
        Mockito.when(m2.getFreunde()).thenReturn(new String[]{"", "", "", "", ""});
        Mockito.when(m2.getGeschwister()).thenReturn(new String[]{"", "", ""});
        Mockito.when(m1.getFreunde()).thenReturn(new String[]{"", "", "", "", ""});
        Mockito.when(m1.getGeschwister()).thenReturn(new String[]{"", "", "", "", ""});
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.STDMESSE.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                fl.setController(new StandardmesseController(StandardMesse));
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(scene.lookup("#pane")).isInstanceOf(ATilePane.class);
        int i = 0;
        for (Node n : ((ATilePane) scene.lookup("#pane")).getChildren()) {
            if (n instanceof ATilePane.ACheckBox) {
                if (((ATilePane.ACheckBox) n).getMessdiener().equals(m1)) {
                    Assertions.assertThat(((ATilePane.ACheckBox) n).isSelected()).isFalse();
                    i++;
                    ((ATilePane.ACheckBox) n).setSelected(true);
                } else if (((ATilePane.ACheckBox) n).getMessdiener().equals(m2)) {
                    Assertions.assertThat(((ATilePane.ACheckBox) n).isSelected()).isTrue();
                    i++;
                    ((ATilePane.ACheckBox) n).setSelected(false);
                }
            }
        }
        Assertions.assertThat(i).isEqualTo(2);
        Assertions.assertThat(scene.lookup("#fertig")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#fertig")).fire();
        final File aFile = new File(f.getAbsolutePath(),"a.xml");
        Assertions.assertThat(aFile).exists();
        final File bFile = new File(f.getAbsolutePath(), "b.xml");
        Assertions.assertThat(bFile).exists();
        try {
            String doW = DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
            Assertions.assertThat(Files.readString(aFile.toPath())).contains("<" + doW + "-8-00-2>true</" + doW + "-8-00-2>");
            Assertions.assertThat(Files.readString(bFile.toPath())).contains("<" + doW + "-8-00-2>false</" + doW + "-8-00-2>");
            Files.delete(aFile.toPath());
            Files.delete(bFile.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

}