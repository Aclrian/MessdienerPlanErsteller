package net.aclrian.mpe.controller; //NOPMD - suppressed ExcessiveImports - for test purpose

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.ReadFile;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateUtil;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.AfterAll;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMediController extends ApplicationTest {

    private final StandardMesse standardMesse = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "o", 2, "t");
    private final String medi = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.getProperty("line.separator")
            + "<XML>" + System.getProperty("line.separator")
            + "  <MpE-Creator LICENSE=\"MIT\">Aclrian</MpE-Creator>" + System.getProperty("line.separator")
            + "  <Body>" + System.getProperty("line.separator")
            + "    <Vorname>Lea</Vorname>" + System.getProperty("line.separator")
            + "    <Nachname>Tannenbusch</Nachname>" + System.getProperty("line.separator")
            + "    <Email/>" + System.getProperty("line.separator")
            + "    <Messverhalten>" + System.getProperty("line.separator")
            + "      <" + standardMesse.toReduziertenString() + ">true</" + standardMesse.toReduziertenString() + ">" + System.getProperty("line.separator")
            + "    </Messverhalten>" + System.getProperty("line.separator")
            + "    <Leiter>false</Leiter>" + System.getProperty("line.separator")
            + "    <Eintritt>2019</Eintritt>" + System.getProperty("line.separator")
            + "    <Anvertraute>" + System.getProperty("line.separator")
            + "      <F1>Müller, Lieschen</F1>" + System.getProperty("line.separator")
            + "      <F2>LEER</F2>" + System.getProperty("line.separator")
            + "      <F3>LEER</F3>" + System.getProperty("line.separator")
            + "      <F4>LEER</F4>" + System.getProperty("line.separator")
            + "      <F5>LEER</F5>" + System.getProperty("line.separator")
            + "      <g1>Tannenbusch, Nora</g1>" + System.getProperty("line.separator")
            + "      <g2>LEER</g2>" + System.getProperty("line.separator")
            + "      <g3>LEER</g3>" + System.getProperty("line.separator")
            + "    </Anvertraute>" + System.getProperty("line.separator")
            + "  </Body>" + System.getProperty("line.separator")
            + "</XML>\n";
    @Mock
    private DateienVerwalter dv;
    @Mock
    private MainController mc;
    @Mock
    private Dialogs dialog;
    private Pane pane;
    private MediController instance;
    private Scene scene;

    @AfterAll
    public static void removeFiles() {
        try {
            final File nora = new File(System.getProperty("user.home"), "Tannenbusch, Nora.xml");
            final File lea = new File(System.getProperty("user.home"), "Tannenbusch, Lea.xml");
            final File liesch = new File(System.getProperty("user.home"), "Müller, Lieschen.xml");
            Files.deleteIfExists(nora.toPath());
            Files.deleteIfExists(lea.toPath());
            Files.deleteIfExists(liesch.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    void testReadAndSave() { //NOPMD - suppressed NcssCount - for test purposes
        File f = new File(System.getProperty("user.home"), "Tannenbusch, Lea.xml");
        try {
            Files.writeString(f.toPath(), medi);
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Dialogs.setDialogs(dialog);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getSavePath()).thenReturn(f.getParentFile());
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standardMesse));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.MESSDIENER.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
                final Messdiener messdiener = new ReadFile().getMessdiener(f);
                instance.setMessdiener(messdiener);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(scene.lookup("#vorname")).isInstanceOf(TextField.class);
        assertThat(((TextField) scene.lookup("#vorname")).getText()).isEqualTo("Lea");
        ((TextField) scene.lookup("#vorname")).setText("Luisa");
        assertThat(scene.lookup("#name")).isInstanceOf(TextField.class);
        assertThat(((TextField) scene.lookup("#name")).getText()).isEqualTo("Tannenbusch");
        assertThat(scene.lookup("#geschwisterListView")).isInstanceOf(ListView.class);
        assertThat(((ListView<?>) scene.lookup("#geschwisterListView")).getItems()).hasSize(1);
        assertThat(scene.lookup("#freundeListView")).isInstanceOf(ListView.class);
        assertThat(((ListView<?>) scene.lookup("#freundeListView")).getItems()).hasSize(1);
        assertThat(scene.lookup("#eintritt")).isInstanceOf(Slider.class);
        assertThat(((Slider) scene.lookup("#eintritt")).getValue()).isEqualTo(2019);
        Platform.runLater(() -> ((Slider) scene.lookup("#eintritt")).setValue(DateUtil.getCurrentYear()));
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(scene.lookup("#leiter")).isInstanceOf(CheckBox.class);
        assertThat(((CheckBox) scene.lookup("#leiter")).isSelected()).isFalse();
        ((CheckBox) scene.lookup("#leiter")).setSelected(true);
        assertThat(scene.lookup("#email")).isInstanceOf(TextField.class);
        assertThat(((TextField) scene.lookup("#email")).getText()).isEmpty();
        ((TextField) scene.lookup("#email")).setText("lol");
        Platform.runLater(() -> scene.lookup("#email").requestFocus());
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> scene.lookup("#leiter").requestFocus());
        WaitForAsyncUtils.waitForFxEvents();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertThat(((TextField) scene.lookup("#email")).getText()).isEmpty();
        ((TextField) scene.lookup("#email")).setText("a@abc.de");
        Platform.runLater(() -> scene.lookup("#email").requestFocus());
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((TextField) scene.lookup("#email")).getText()).isEqualTo("a@abc.de");
        assertThat(scene.lookup("#table")).isInstanceOf(TableView.class);
        TableView<?> table = (TableView<?>) scene.lookup("#table");
        assertThat(table.getItems()).hasSize(1);
        assertThat(((Messverhalten.KannWelcheMesse) table.getItems().get(0)).kannDann()).isTrue();
        assertThat(((Messverhalten.KannWelcheMesse) table.getItems().get(0)).getMesse()).isEqualTo(standardMesse);
        ((Messverhalten.KannWelcheMesse) table.getItems().get(0)).setKannDann(false);
        Platform.runLater(() -> ((SplitMenuButton) scene.lookup("#button")).fire());
        WaitForAsyncUtils.waitForFxEvents();
        final File newFile = new File(System.getProperty("user.home"), "Tannenbusch, Luisa.xml");
        assertThat(newFile).exists();
        Platform.runLater(() -> {
            final Messdiener messdiener = new ReadFile().getMessdiener(newFile);
            instance.setMessdiener(messdiener);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(((TextField) scene.lookup("#vorname")).getText()).isEqualTo("Luisa");
        ((TextField) scene.lookup("#vorname")).setText("Luisa");
        assertThat(((TextField) scene.lookup("#name")).getText()).isEqualTo("Tannenbusch");
        assertThat(((ListView<?>) scene.lookup("#geschwisterListView")).getItems()).hasSize(1);
        assertThat(((ListView<?>) scene.lookup("#freundeListView")).getItems()).hasSize(1);
        assertThat(((Slider) scene.lookup("#eintritt")).getValue()).isEqualTo(DateUtil.getCurrentYear());
        assertThat(((CheckBox) scene.lookup("#leiter")).isSelected()).isTrue();
        assertThat(((TextField) scene.lookup("#email")).getText()).isEqualTo("a@abc.de");
        assertThat(table.getItems()).hasSize(1);
        assertThat(((Messverhalten.KannWelcheMesse) table.getItems().get(0)).kannDann()).isFalse();
        assertThat(((Messverhalten.KannWelcheMesse) table.getItems().get(0)).getMesse()).isEqualTo(standardMesse);
        assertThat(f).doesNotExist();
        try {
            Files.delete(newFile.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGeschwister() {
        File f = new File(System.getProperty("user.home"), "Tannenbusch, Lea.xml");
        try {
            Files.writeString(f.toPath(), medi);
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Dialogs.setDialogs(dialog);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(standardMesse));
        DateienVerwalter.setInstance(dv);
        Messdiener m1 = Mockito.mock(Messdiener.class);
        Mockito.when(m1.toString()).thenReturn("Tannenbusch, Nora");
        Mockito.when(m1.toString()).thenReturn("Tannenbusch, Nora");
        Mockito.when(m1.getGeschwister()).thenReturn(new String[]{"Tannenbusch, Lea", "", ""});
        final Messverhalten mv1 = new Messverhalten();
        Mockito.when(m1.getDienverhalten()).thenReturn(mv1);
        Mockito.when(m1.getFreunde()).thenReturn(new String[]{"", "", "", "", ""});
        Messdiener m2 = Mockito.mock(Messdiener.class);
        Mockito.when(m2.toString()).thenReturn("Müller, Lieschen");
        Mockito.when(m2.toString()).thenReturn("Müller, Lieschen");
        Mockito.when(m2.getGeschwister()).thenReturn(new String[]{"", "", ""});
        final Messverhalten mv2 = new Messverhalten();
        Mockito.when(m2.getDienverhalten()).thenReturn(mv2);
        Mockito.when(m2.getFreunde()).thenReturn(new String[]{"Tannenbusch, Lea", "", "", "", ""});
        Mockito.when(dv.getSavePath()).thenReturn(f.getParentFile());
        Mockito.when(dialog.select(Mockito.anyList(), Mockito.any(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Collections.singletonList(m2), Collections.singletonList(m1));

        final Messdiener messdiener = new ReadFile().getMessdiener(f);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, messdiener, m2));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.MESSDIENER.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
                instance.setMessdiener(messdiener);
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(scene.lookup("#geschwisterListView")).isInstanceOf(ListView.class);
        assertThat(((ListView<?>) scene.lookup("#geschwisterListView")).getItems()).hasSize(2);

        Platform.runLater(() -> {
            scene.lookup("#" + MediController.GESCHWISTER_BEARBEITEN_ID)
                    .fireEvent(new MouseEvent(
                            MouseEvent.MOUSE_CLICKED, 0d, 0d, 0d, 0d,
                            MouseButton.PRIMARY, 1, false, false, false, false, false, false,
                            false, false, false, false, null));
            scene.lookup("#" + MediController.FREUNDE_BEARBEITEN_ID)
                    .fireEvent(new MouseEvent(
                            MouseEvent.MOUSE_CLICKED, 0d, 0d, 0d, 0d,
                            MouseButton.PRIMARY, 1, false, false, false, false, false, false,
                            false, false, false, false, null));

            Node button = scene.lookup("#button");
            if (button instanceof SplitMenuButton) {
                ObservableList<MenuItem> items = ((SplitMenuButton) button).getItems();
                items.stream().filter(menuItem -> menuItem.getId().equals("saveNew")).findFirst().orElseThrow().fire();
            } else {
                Assertions.fail("couldn't find button");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(dialog, Mockito.times(1)).select(Mockito.anyList(), Mockito.any(), Mockito.anyList(), Mockito.eq(MediController.FREUNDE_AUSWAEHLEN));
        Mockito.verify(dialog, Mockito.times(1)).select(Mockito.anyList(), Mockito.any(), Mockito.anyList(), Mockito.eq(MediController.GESCHWISTER_AUSWAEHLEN));

        assertThat(((TextField) scene.lookup("#vorname")).getText()).isEmpty();
        Mockito.verify(m1).setFreunde(
                Mockito.argThat(
                        argument -> argument.length == 5
                                && argument[0].equalsIgnoreCase("Tannenbusch, Lea")
                                && argument[1] == null && argument[2] == null
                                && argument[3] == null && argument[4] == null));
        Mockito.verify(m2).setGeschwister(
                Mockito.argThat(
                        argument -> argument.length == 3
                                && argument[0].equalsIgnoreCase("Tannenbusch, Lea")
                                && argument[1] == null && argument[2] == null));

        Mockito.verify(m1, Mockito.times(24)).getFreunde();
        Mockito.verify(m1, Mockito.times(16)).getGeschwister();

        Mockito.verify(m2, Mockito.times(24)).getFreunde();
        Mockito.verify(m2, Mockito.times(16)).getGeschwister();

        final File nora = new File(System.getProperty("user.home"), "Tannenbusch, Nora.xml");
        final File lea = new File(System.getProperty("user.home"), "Tannenbusch, Lea.xml");
        final File liesch = new File(System.getProperty("user.home"), "Müller, Lieschen.xml");

        assertThat(nora).exists();
        assertThat(liesch).exists();
        assertThat(lea).exists();

        try {
            String doW = DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
            assertThat(Files.readString(lea.toPath())).isEqualTo(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.getProperty("line.separator")
                    + "<XML>" + System.getProperty("line.separator")
                    + "    <MpE-Creator LICENSE=\"MIT\">Aclrian</MpE-Creator>" + System.getProperty("line.separator")
                    + "    <Body>" + System.getProperty("line.separator")
                    + "        <Vorname>Lea</Vorname>" + System.getProperty("line.separator")
                    + "        <Nachname>Tannenbusch</Nachname>" + System.getProperty("line.separator")
                    + "        <Email/>" + System.getProperty("line.separator")
                    + "        <Messverhalten>" + System.getProperty("line.separator")
                    + "            <" + doW + "-8-00-2>true</" + doW + "-8-00-2>" + System.getProperty("line.separator")
                    + "        </Messverhalten>" + System.getProperty("line.separator")
                    + "        <Leiter>false</Leiter>" + System.getProperty("line.separator")
                    + "        <Eintritt>2019</Eintritt>" + System.getProperty("line.separator")
                    + "        <Anvertraute>" + System.getProperty("line.separator")
                    + "            <F1>Tannenbusch, Nora</F1>" + System.getProperty("line.separator")
                    + "            <F2>LEER</F2>" + System.getProperty("line.separator")
                    + "            <F3>LEER</F3>" + System.getProperty("line.separator")
                    + "            <F4>LEER</F4>" + System.getProperty("line.separator")
                    + "            <F5>LEER</F5>" + System.getProperty("line.separator")
                    + "            <g1>Müller, Lieschen</g1>" + System.getProperty("line.separator")
                    + "            <g2>LEER</g2>" + System.getProperty("line.separator")
                    + "            <g3>LEER</g3>" + System.getProperty("line.separator")
                    + "        </Anvertraute>" + System.getProperty("line.separator")
                    + "    </Body>" + System.getProperty("line.separator")
                    + "</XML>" + System.getProperty("line.separator")
            );

            Files.delete(lea.toPath());
            Files.delete(liesch.toPath());
            Files.delete(nora.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }
}
