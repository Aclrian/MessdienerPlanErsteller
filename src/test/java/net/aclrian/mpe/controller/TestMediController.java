package net.aclrian.mpe.controller;

import javafx.application.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
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
import java.util.concurrent.*;

public class TestMediController extends ApplicationTest {

    private final StandardMesse StandardMesse = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "o", 2, "t");
    private final String medi = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.getProperty("line.separator") +
            "<XML>" + System.getProperty("line.separator") +
            "  <MpE-Creator LICENSE=\"MIT\">Aclrian</MpE-Creator>" + System.getProperty("line.separator") +
            "  <Body>" + System.getProperty("line.separator") +
            "    <Vorname>Lea</Vorname>" + System.getProperty("line.separator") +
            "    <Nachname>Tannenbusch</Nachname>" + System.getProperty("line.separator") +
            "    <Email/>" + System.getProperty("line.separator") +
            "    <Messverhalten>" + System.getProperty("line.separator") +
            "      <" + StandardMesse.toReduziertenString() + ">true</" + StandardMesse.toReduziertenString() + ">" + System.getProperty("line.separator") +
            "    </Messverhalten>" + System.getProperty("line.separator") +
            "    <Leiter>false</Leiter>" + System.getProperty("line.separator") +
            "    <Eintritt>2019</Eintritt>" + System.getProperty("line.separator") +
            "    <Anvertraute>" + System.getProperty("line.separator") +
            "      <F1>Müller, Lieschen</F1>" + System.getProperty("line.separator") +
            "      <F2>LEER</F2>" + System.getProperty("line.separator") +
            "      <F3>LEER</F3>" + System.getProperty("line.separator") +
            "      <F4>LEER</F4>" + System.getProperty("line.separator") +
            "      <F5>LEER</F5>" + System.getProperty("line.separator") +
            "      <g1>Tannenbusch, Nora</g1>" + System.getProperty("line.separator") +
            "      <g2>LEER</g2>" + System.getProperty("line.separator") +
            "      <g3>LEER</g3>" + System.getProperty("line.separator") +
            "    </Anvertraute>" + System.getProperty("line.separator") +
            "  </Body>" + System.getProperty("line.separator") +
            "</XML>\n";
    @Mock
    private DateienVerwalter dv;
    @Mock
    private MainController mc;
    @Mock
    private Dialogs dialog;
    private Pane pane;
    private MediController instance;
    private Scene scene;

    @AfterClass
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
    public void testReadAndSave() {
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
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(StandardMesse));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.MESSDIENER.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
                final Messdiener messdiener = new ReadFile().getMessdiener(f);
                instance.setMedi(messdiener);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(scene.lookup("#vorname")).isInstanceOf(TextField.class);
        Assertions.assertThat(((TextField) scene.lookup("#vorname")).getText()).isEqualTo("Lea");
        ((TextField) scene.lookup("#vorname")).setText("Luisa");
        Assertions.assertThat(scene.lookup("#name")).isInstanceOf(TextField.class);
        Assertions.assertThat(((TextField) scene.lookup("#name")).getText()).isEqualTo("Tannenbusch");
        Assertions.assertThat(scene.lookup("#geschwie")).isInstanceOf(ListView.class);
        Assertions.assertThat(((ListView<?>) scene.lookup("#geschwie")).getItems().size()).isEqualTo(1);
        Assertions.assertThat(scene.lookup("#freunde")).isInstanceOf(ListView.class);
        Assertions.assertThat(((ListView<?>) scene.lookup("#freunde")).getItems()).hasSize(1);
        Assertions.assertThat(scene.lookup("#eintritt")).isInstanceOf(Slider.class);
        Assertions.assertThat(((Slider) scene.lookup("#eintritt")).getValue()).isEqualTo(2019);
        Platform.runLater(() -> ((Slider) scene.lookup("#eintritt")).setValue(Messdaten.getMaxYear()));
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(scene.lookup("#leiter")).isInstanceOf(CheckBox.class);
        Assertions.assertThat(((CheckBox) scene.lookup("#leiter")).isSelected()).isFalse();
        ((CheckBox) scene.lookup("#leiter")).setSelected(true);
        Assertions.assertThat(scene.lookup("#email")).isInstanceOf(TextField.class);
        Assertions.assertThat(((TextField) scene.lookup("#email")).getText().isEmpty()).isTrue();
        ((TextField) scene.lookup("#email")).setText("lol");
        Platform.runLater(() -> scene.lookup("#email").requestFocus());
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> scene.lookup("#leiter").requestFocus());
        WaitForAsyncUtils.waitForFxEvents();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertThat(((TextField) scene.lookup("#email")).getText().isEmpty()).isTrue();
        ((TextField) scene.lookup("#email")).setText("a@abc.de");
        Platform.runLater(() -> scene.lookup("#email").requestFocus());
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(((TextField) scene.lookup("#email")).getText()).isEqualTo("a@abc.de");
        Assertions.assertThat(scene.lookup("#table")).isInstanceOf(TableView.class);
        TableView<?> table = (TableView<?>) scene.lookup("#table");
        Assertions.assertThat(table.getItems()).hasSize(1);
        Assertions.assertThat(((KannWelcheMesse) table.getItems().get(0)).kannDann()).isTrue();
        Assertions.assertThat(((KannWelcheMesse) table.getItems().get(0)).getMesse()).isEqualTo(StandardMesse);
        ((KannWelcheMesse) table.getItems().get(0)).setKannDann(false);
        Platform.runLater(() -> ((SplitMenuButton) scene.lookup("#button")).fire());
        WaitForAsyncUtils.waitForFxEvents();
        final File newFile = new File(System.getProperty("user.home"), "Tannenbusch, Luisa.xml");
        Assertions.assertThat(newFile).exists();
        Platform.runLater(() -> {
            final Messdiener messdiener = new ReadFile().getMessdiener(newFile);
            instance.setMedi(messdiener);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(((TextField) scene.lookup("#vorname")).getText()).isEqualTo("Luisa");
        ((TextField) scene.lookup("#vorname")).setText("Luisa");
        Assertions.assertThat(((TextField) scene.lookup("#name")).getText()).isEqualTo("Tannenbusch");
        Assertions.assertThat(((ListView<?>) scene.lookup("#geschwie")).getItems().size()).isEqualTo(1);
        Assertions.assertThat(((ListView<?>) scene.lookup("#freunde")).getItems()).hasSize(1);
        Assertions.assertThat(((Slider) scene.lookup("#eintritt")).getValue()).isEqualTo(Messdaten.getMaxYear());
        Assertions.assertThat(((CheckBox) scene.lookup("#leiter")).isSelected()).isTrue();
        Assertions.assertThat(((TextField) scene.lookup("#email")).getText()).isEqualTo("a@abc.de");
        Assertions.assertThat(table.getItems()).hasSize(1);
        Assertions.assertThat(((KannWelcheMesse) table.getItems().get(0)).kannDann()).isFalse();
        Assertions.assertThat(((KannWelcheMesse) table.getItems().get(0)).getMesse()).isEqualTo(StandardMesse);
        Assertions.assertThat(f).doesNotExist();
        try {
            Files.delete(newFile.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGeschwister() {
        File f = new File(System.getProperty("user.home"), "Tannenbusch, Lea.xml");
        try {
            Files.writeString(f.toPath(), medi);
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
        Dialogs.setDialogs(dialog);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Mockito.when(pf.getStandardMessen()).thenReturn(Collections.singletonList(StandardMesse));
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
        Mockito.when(dialog.select(Mockito.anyList(), Mockito.any(), Mockito.anyList(), Mockito.anyString())).thenReturn(Collections.singletonList(m2), Collections.singletonList(m1));

        final Messdiener messdiener = new ReadFile().getMessdiener(f);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(m1, messdiener, m2));
        Platform.runLater(() -> {
            URL u = getClass().getResource(MainController.EnumPane.MESSDIENER.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            try {
                pane.getChildren().add(fl.load());
                instance = fl.getController();
                instance.afterStartup(pane.getScene().getWindow(), mc);
                instance.setMedi(messdiener);
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
                Assertions.fail("Could not open " + MainController.EnumPane.FERIEN.getLocation() + e.getLocalizedMessage(), e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(scene.lookup("#geschwie")).isInstanceOf(ListView.class);
        Assertions.assertThat(((ListView<?>) scene.lookup("#geschwie")).getItems().size()).isEqualTo(2);

        Platform.runLater(() -> {
            scene.lookup("#" + MediController.GESCHWISTER_BEARBEITEN_ID).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0d, 0d, 0d, 0d, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
            scene.lookup("#" + MediController.FREUNDE_BEARBEITEN_ID).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0d, 0d, 0d, 0d, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));

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

        Assertions.assertThat(((TextField) scene.lookup("#vorname")).getText()).isEmpty();
        Mockito.verify(m1).setFreunde(Mockito.argThat(argument -> argument.length == 5 && argument[0].equalsIgnoreCase("Tannenbusch, Lea") && argument[1] == null && argument[2] == null && argument[3] == null && argument[4] == null));
        Mockito.verify(m2).setGeschwister(Mockito.argThat(argument -> argument.length == 3 && argument[0].equalsIgnoreCase("Tannenbusch, Lea") && argument[1] == null && argument[2] == null));

        Mockito.verify(m1, Mockito.times(24)).getFreunde();
        Mockito.verify(m1, Mockito.times(16)).getGeschwister();

        Mockito.verify(m2, Mockito.times(24)).getFreunde();
        Mockito.verify(m2, Mockito.times(16)).getGeschwister();

        final File nora = new File(System.getProperty("user.home"), "Tannenbusch, Nora.xml");
        final File lea = new File(System.getProperty("user.home"), "Tannenbusch, Lea.xml");
        final File liesch = new File(System.getProperty("user.home"), "Müller, Lieschen.xml");

        Assertions.assertThat(nora.exists()).isTrue();
        Assertions.assertThat(liesch.exists()).isTrue();
        Assertions.assertThat(lea.exists()).isTrue();

        try {
            String doW = DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
            Assertions.assertThat(Files.readString(lea.toPath())).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.getProperty("line.separator") +
                    "<XML>" + System.getProperty("line.separator") +
                    "  <MpE-Creator LICENSE=\"MIT\">Aclrian</MpE-Creator>" + System.getProperty("line.separator") +
                    "  <Body>" + System.getProperty("line.separator") +
                    "    <Vorname>Lea</Vorname>" + System.getProperty("line.separator") +
                    "    <Nachname>Tannenbusch</Nachname>" + System.getProperty("line.separator") +
                    "    <Email/>" + System.getProperty("line.separator") +
                    "    <Messverhalten>" + System.getProperty("line.separator") +
                    "      <" + doW + "-8-00-2>true</" + doW + "-8-00-2>" + System.getProperty("line.separator") +
                    "    </Messverhalten>" + System.getProperty("line.separator") +
                    "    <Leiter>false</Leiter>" + System.getProperty("line.separator") +
                    "    <Eintritt>2019</Eintritt>" + System.getProperty("line.separator") +
                    "    <Anvertraute>" + System.getProperty("line.separator") +
                    "      <F1>Tannenbusch, Nora</F1>" + System.getProperty("line.separator") +
                    "      <F2>LEER</F2>" + System.getProperty("line.separator") +
                    "      <F3>LEER</F3>" + System.getProperty("line.separator") +
                    "      <F4>LEER</F4>" + System.getProperty("line.separator") +
                    "      <F5>LEER</F5>" + System.getProperty("line.separator") +
                    "      <g1>Müller, Lieschen</g1>" + System.getProperty("line.separator") +
                    "      <g2>LEER</g2>" + System.getProperty("line.separator") +
                    "      <g3>LEER</g3>" + System.getProperty("line.separator") +
                    "    </Anvertraute>" + System.getProperty("line.separator") +
                    "  </Body>" + System.getProperty("line.separator") +
                    "</XML>" + System.getProperty("line.separator"));

            Files.delete(lea.toPath());
            Files.delete(liesch.toPath());
            Files.delete(nora.toPath());
        } catch (IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }
}
