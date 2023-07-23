package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.fx.ATilePane;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TestStandardmesseController extends ApplicationTest {

    private final StandardMesse standardMesse = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "o", 2, "t");
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
    void test() {
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        File f = new File(System.getProperty("user.home"));
        Mockito.when(dv.getSavePath()).thenReturn(f);
        Messdiener m1 = Mockito.mock(Messdiener.class);
        Messdiener m2 = Mockito.mock(Messdiener.class);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standardMesse));
        final Messverhalten mv1 = new Messverhalten();
        Mockito.when(m1.getDienverhalten()).thenReturn(mv1);
        Mockito.when(m1.toString()).thenReturn("a");
        final Messverhalten mv2 = new Messverhalten();
        mv2.editiereBestimmteMesse(standardMesse, true);
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
                fl.setController(new StandardmesseController(standardMesse));
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(scene.lookup("#pane")).isInstanceOf(ATilePane.class);
        int i = 0;
        for (Node n : ((ATilePane) scene.lookup("#pane")).getChildren()) {
            if (n instanceof ATilePane.ACheckBox) {
                if (((ATilePane.ACheckBox) n).getMessdiener().equals(m1)) {
                    assertThat(((ATilePane.ACheckBox) n).isSelected()).isFalse();
                    i++;
                    ((ATilePane.ACheckBox) n).setSelected(true);
                } else if (((ATilePane.ACheckBox) n).getMessdiener().equals(m2)) {
                    assertThat(((ATilePane.ACheckBox) n).isSelected()).isTrue();
                    i++;
                    ((ATilePane.ACheckBox) n).setSelected(false);
                }
            }
        }
        assertThat(i).isEqualTo(2);
        assertThat(scene.lookup("#fertig")).isInstanceOf(Button.class);
        ((Button) scene.lookup("#fertig")).fire();
        final File aFile = new File(f.getAbsolutePath(), "a.xml");
        assertThat(aFile).exists();
        final File bFile = new File(f.getAbsolutePath(), "b.xml");
        assertThat(bFile).exists();
        try {
            String doW = DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
            assertThat(Files.readString(aFile.toPath())).contains("<" + doW + "-8-00-2>true</" + doW + "-8-00-2>");
            assertThat(Files.readString(bFile.toPath())).contains("<" + doW + "-8-00-2>false</" + doW + "-8-00-2>");
            Files.delete(aFile.toPath());
            Files.delete(bFile.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

}