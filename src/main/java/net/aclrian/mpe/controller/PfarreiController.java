package net.aclrian.mpe.controller;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.Main;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.Setting;
import net.aclrian.mpe.pfarrei.WriteFilePfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import static net.aclrian.mpe.utils.Log.getLogger;

public class PfarreiController {

    public static final String STANDARDMESSE_FXML = "/view/pfarrei/standardmesse.fxml";
    public static final String NICHTS_AUSGEWAEHLT = "Bitte eine Standardmesse auswählen:";
    public static final String MEHR_INFORMATIONEN = "Mehr Informationen";
    public static final String VERSTANDEN = "Verstanden";
    private static final String CENTERED = "-fx-alignment: CENTER;";
    private final Stage stage;
    private final Main main;
    // ----------------------------------------------------------------------
    @FXML
    private TableView<Setting> settingTableView = new TableView<>();
    @FXML
    private /*NOT final!*/ TableView<StandardMesse> table = new TableView<>();
    private String nameS = null;
    private int mediI;
    private int leiterI;
    private boolean hochamtB;
    private String savepath;
    private ObservableList<StandardMesse> ol = FXCollections.emptyObservableList();
    private boolean weiter = false;
    private Stage old;
    // ---------------------------------------
    private ObservableList<Setting> settings;
    @FXML
    private TableColumn<StandardMesse, String> time;
    @FXML
    private TableColumn<StandardMesse, String> ort;
    @FXML
    private TableColumn<StandardMesse, String> typ;
    @FXML
    private TableColumn<StandardMesse, String> anz;
    @FXML
    private TableColumn<Setting, Integer> eintritt;
    @FXML
    private TableColumn<Setting, Integer> anzahl;
    @FXML
    private TextField name;
    @FXML
    private CheckBox hochamt;
    @FXML
    private Slider leiter;
    @FXML
    private Slider medi;
    @FXML
    private Button neu;
    @FXML
    private Button loeschen;
    @FXML
    private Button weiterbtn;
    @FXML
    private Button zurueckbtn;

    public PfarreiController(Stage stage, Main m, Stage old) {
        this.stage = stage;
        this.main = m;
        this.old = old;
        Pfarrei pf = DateienVerwalter.getInstance().getPfarrei();
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/title_32.png"))));
        ol = FXCollections.observableArrayList(pf.getStandardMessen());
        ol.removeIf(Sonstiges.class::isInstance);
        ol.sort(StandardMesse.STANDARD_MESSE_COMPARATOR);
        settings = FXCollections.observableArrayList(pf.getSettings().getSettings());
        nameS = pf.getName();
        mediI = pf.getSettings().getDaten(0).anzahlDienen();
        leiterI = pf.getSettings().getDaten(1).anzahlDienen();
        hochamtB = pf.zaehlenHochaemterMit();
    }

    public PfarreiController(Stage stage, String savepath, Main main) {
        this.savepath = savepath;
        this.stage = stage;
        this.main = main;
    }

    public static void start(Stage stage, String savepath, Main main) {
        FXMLLoader loader = new FXMLLoader();
        PfarreiController cont = new PfarreiController(stage, savepath, main);
        loader.setLocation(cont.getClass().getResource(STANDARDMESSE_FXML));
        loader.setController(cont);
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(Log.PROGRAMM_NAME);
            stage.setResizable(false);
            stage.show();
            cont.afterStartup();
            Dialogs.getDialogs().info("Willkommen beim MessdienerplanErsteller!", "Als Erstes werden eine Daten benötigt:"
                    + System.lineSeparator()
                    + "Zunächst werden Standardmessen erstellt. Das sind die Messen, die sich wöchentlich wiederholen."
                    + System.lineSeparator()
                    + "Danach folgen Einstellungen, wie oft Messdiener eingeteilt werden sollen.");
            getLogger().info("neue Pfarrei erstellen...");
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Auf " + loader.getLocation() + MainController.NO_ACCESS);
        }
    }

    public static void start(Stage stage, Main m, Stage old) {
        FXMLLoader loader = new FXMLLoader();
        PfarreiController cont = new PfarreiController(stage, m, old);
        loader.setLocation(cont.getClass().getResource(STANDARDMESSE_FXML));
        loader.setController(cont);
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(Log.PROGRAMM_NAME);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            cont.afterStartup();
            getLogger().info("neue Pfarrei erstellen...");
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Auf " + loader.getLocation() + MainController.NO_ACCESS);
        }
    }

    public void afterStartup() {
        if (weiter) {
            ASlider.makeASlider("", leiter, null);
            leiter.setMax(30);
            leiter.setMin(0);
            leiter.setValue(3);
            ASlider.makeASlider("", medi, null);
            medi.setValue(3);
            medi.setMax(30);
            medi.setMin(0);
            if (settings == null) {
                Einstellungen e = new Einstellungen();
                settings = FXCollections.observableArrayList(e.getSettings());
            }
            eintritt.setCellValueFactory(new PropertyValueFactory<>("Jahr"));
            eintritt.setStyle(CENTERED);
            anzahl.setCellValueFactory(new PropertyValueFactory<>(Setting.ANZAHL_DIENEN_NAME));
            anzahl.setStyle(CENTERED);
            settingTableView.setItems(settings);
            settingTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            settingTableView.autosize();
            settingTableView.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
                    int i = settingTableView.getSelectionModel().getSelectedIndex();
                    Setting s = settings.get(i);
                    settings.set(i, Dialogs.getDialogs().chance(s));
                }
            });
        } else {
            time.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getZeit()));
            time.setStyle(CENTERED);
            ort.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrt()));
            ort.setStyle(CENTERED);
            typ.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTyp()));
            typ.setStyle(CENTERED);
            anz.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessdienerAnzahl()));
            anz.setStyle(CENTERED);
            table.setItems(ol);
            table.setOnMouseClicked(getMouseEventEventHandlerForTable());
        }
    }

    private EventHandler<MouseEvent> getMouseEventEventHandlerForTable() {
        return e -> {
            if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
                int i = table.getSelectionModel().getSelectedIndex();
                StandardMesse standardMesse = Dialogs.getDialogs().standardmesse(ol.get(i));
                if (standardMesse != null) {
                    ol.remove(i);
                    ol.add(standardMesse);
                    ol.sort(StandardMesse.STANDARD_MESSE_COMPARATOR);
                }
            }
        };
    }

    @FXML
    public void loeschen(ActionEvent e) {
        StandardMesse a = table.getSelectionModel().getSelectedItem();
        if (a != null) {
            ol.removeIf(sm -> sm.toString().equals(a.toString()));
        } else {
            Dialogs.getDialogs().warn(NICHTS_AUSGEWAEHLT);
        }
    }

    @FXML
    public void neu(ActionEvent e) {
        StandardMesse sm = Dialogs.getDialogs().standardmesse(null);
        if (ol.isEmpty()) {
            ol = FXCollections.observableArrayList(sm);
            table.setItems(ol);
        } else {
            table.getItems().add(sm);
        }
    }

    @FXML
    public void save(ActionEvent e) {
        if (name.getText().isEmpty()) {
            Dialogs.getDialogs().warn("Bitte gebe einen Namen ein.");
            return;
        }
        Einstellungen einst = new Einstellungen();
        einst.editMaxDienen(false, (int) medi.getValue());
        einst.editMaxDienen(true, (int) leiter.getValue());
        for (int i = 0; i < Einstellungen.LENGTH - 2; i++) {
            einst.editiereYear(settings.get(i).id(), settings.get(i).anzahlDienen());
        }
        ArrayList<StandardMesse> sm = new ArrayList<>(ol);
        Pfarrei pf = new Pfarrei(einst, sm, name.getText(), hochamt.isSelected());
        Window s = ((Button) e.getSource()).getParent().getScene().getWindow();
        if (nameS != null)
            WriteFilePfarrei.writeFile(pf);
        WriteFilePfarrei.writeFile(pf, savepath);
        if (old != null) {
            try {
                DateienVerwalter.reStart(old);
                ((Stage) s).close();
                return;
            } catch (DateienVerwalter.NoSuchPfarrei e1) {
                getLogger().error(e1.getMessage());
            }
        }
        try {
            ((Stage) s).close();
            main.main(new Stage());
        } catch (Exception ex) {
            Dialogs.getDialogs().info("Das Programm muss nun neu gestartet werden.");
            System.exit(0);
        }
    }

    public void weiter() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/pfarrei/anzahl.fxml"));
        loader.setController(this);
        weiter = true;
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("MessdienerplanErsteller");
            stage.setResizable(false);
            stage.show();
            afterStartup();
            getLogger().info("neue Pfarrei erstellen...weiter");
            if (nameS != null) {
                name.setText(nameS);
                medi.setValue(mediI);
                leiter.setValue(leiterI);
                hochamt.setSelected(hochamtB);
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Auf " + loader.getLocation() + MainController.NO_ACCESS);
        }
    }

    @FXML
    public void weiter(ActionEvent e) {
        weiter();
        Dialogs.getDialogs().open(URI.create(
                        "https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Was-wird-unter-'Anzahl'-verstanden%3F"),
                "Nun geht es um die Anzahl, wie oft Messdiener eingeteilt werden sollen:"
                        + System.lineSeparator()
                        + "Hier gibt es die maximale Anzahl, bei der zwischen Leitern und normalen Messdienern unterschieden werden kann."
                        + System.lineSeparator()
                        + "Diese beschreibt die Anzahl an Messen, die Messdiener nicht mehr dienen kann, weil dieser schon an genug Messen gedient hat."
                        + System.lineSeparator()
                        + "Die 'Anzahl nach Eintrittsjahr' gibt, an wie häufig Messdiener des jeweiligen Jahrgangs generell eingeteilt werden."
                        + System.lineSeparator()
                        + "Dabei ist zu beachten, dass jedes Jahr ein Messdiener in die nächst-höhere Gruppe kommt.",
                MEHR_INFORMATIONEN, VERSTANDEN);
    }

    @FXML
    public void zurueck() {
        if (nameS != null) {
            nameS = name.getText();
            mediI = (int) medi.getValue();
            leiterI = (int) leiter.getValue();
            hochamtB = hochamt.isSelected();
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(STANDARDMESSE_FXML));
        loader.setController(this);
        weiter = false;
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("MessdienerplanErsteller");
            stage.setResizable(false);
            stage.show();
            afterStartup();
            getLogger().info("neue Pfarrei erstellen...zurück");
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Auf " + loader.getLocation() + MainController.NO_ACCESS);
        }
    }
}
