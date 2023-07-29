package net.aclrian.mpe.controller;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.messdiener.Email;
import net.aclrian.mpe.messdiener.FindMessdiener;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.Messverhalten;
import net.aclrian.mpe.utils.DateUtil;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.RemoveDoppelte;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static net.aclrian.mpe.utils.MPELog.getLogger;
import static net.aclrian.mpe.messdiener.Messverhalten.KannWelcheMesse;

public class MediController implements Controller {

    private static final String KONNTE = "Konnte den Messdiener '";
    public static final String FREUNDE_AUSWAEHLEN = "Freunde auswählen:";
    public static final String GESCHWISTER_AUSWAEHLEN = "Geschwister auswählen:";
    private List<Messdiener> freundeArray;
    private List<Messdiener> geschwisterArray;
    private Messdiener moben;
    private boolean locked = true;

    private Label bearbeitenFreunde;
    private Label bearbeitenGeschwister;

    private ObservableList<KannWelcheMesse> ol;
    @FXML
    private TableView<KannWelcheMesse> table;
    @FXML
    private TableColumn<KannWelcheMesse, String> stdm;
    @FXML
    private TableColumn<KannWelcheMesse, Boolean> kann;
    @FXML
    private Slider eintritt;
    @FXML
    private TextField name;
    @FXML
    private TextField vorname;
    @FXML
    private TextField email;
    @FXML
    private CheckBox leiter;
    @FXML
    private ListView<Label> geschwisterListView;
    @FXML
    private ListView<Label> freundeListView;
    @FXML
    private SplitMenuButton button;
    @FXML
    private MenuItem cancel;
    @FXML
    private MenuItem saveNew;
    public static final String FREUNDE_BEARBEITEN_ID = "freundeId";
    public static final String GESCHWISTER_BEARBEITEN_ID = "geschwisterId";

    public void initialize() {
        freundeArray = new ArrayList<>();
        geschwisterArray = new ArrayList<>();
        eintritt.setMax(DateUtil.getCurrentYear());
        eintritt.setMin(DateUtil.getYearCap());
    }

    @Override
    public void afterStartup(Window window, MainController mc) { //NOPMD - suppressed CognitiveComplexity - initialization that cannot be done with fxml
        cancel.setOnAction(e -> {
            locked = false;
            mc.changePane(MainController.EnumPane.SELECT_MEDI);
        });
        saveNew.setOnAction(e -> {
            if (saveMessdiener()) {
                locked = false;
                vorname.setText("");
            }
        });
        button.setOnAction(e -> {
            if (saveMessdiener()) {
                locked = false;
                mc.changePane(MainController.EnumPane.SELECT_MEDI);
            }
        });
        // Email valid
        email.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (Boolean.FALSE.equals(newValue)) {
                email.setText(Email.EMAIL_PATTERN.matcher(email.getText()).matches() ? email.getText() : "");
            }
        });
        // Value in Silder
        ASlider.makeASlider("Eintritt", eintritt, null);
        eintritt.setValue(DateUtil.getCurrentYear());
        bearbeitenFreunde = new Label("Bearbeiten");
        bearbeitenFreunde.setStyle("-fx-font-style: italic;");
        bearbeitenFreunde.setId(FREUNDE_BEARBEITEN_ID);
        bearbeitenFreunde.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                List<Messdiener> selected = Dialogs.getDialogs().select(DateienVerwalter.getInstance().getMessdiener(), moben,
                        freundeArray, FREUNDE_AUSWAEHLEN);
                if (selected.size() >= Messdiener.LENGTH_FREUNDE) {
                    Dialogs.getDialogs().error(
                            "Zu viele Freunde: Bitte nur " + (Messdiener.LENGTH_FREUNDE - 1) + " Messdiener angeben.");
                    handle(arg0);
                    return;
                }
                freundeArray = selected;
                updateAnvertrauteInView(freundeArray, freundeListView);
            }
        });
        freundeListView.getItems().add(bearbeitenFreunde);

        bearbeitenGeschwister = new Label("Bearbeiten");
        bearbeitenGeschwister.setStyle("-fx-font-style: italic;");
        bearbeitenGeschwister.setId(GESCHWISTER_BEARBEITEN_ID);
        bearbeitenGeschwister.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                List<Messdiener> g = Dialogs.getDialogs().select(DateienVerwalter.getInstance().getMessdiener(), moben, geschwisterArray,
                        GESCHWISTER_AUSWAEHLEN);
                if (g.size() >= Messdiener.LENGTH_GESCHWISTER) {
                    Dialogs.getDialogs().error("Zu viele Geschwister: Bitte nur " + (Messdiener.LENGTH_GESCHWISTER - 1)
                            + " Messdiener angeben.");
                    handle(arg0);
                    return;
                }
                geschwisterArray = g;
                updateAnvertrauteInView(geschwisterArray, geschwisterListView);
            }
        });
        geschwisterListView.getItems().add(bearbeitenGeschwister);

        stdm.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMesse().toKurzerBenutzerfreundlichenString()));
        kann.setCellFactory(CheckBoxTableCell.forTableColumn(kann));
        kann.setCellValueFactory(celldata -> {
            KannWelcheMesse cellValue = celldata.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(cellValue.kannDann());
            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setKannDann(newValue));
            return property;
        });
        kann.setCellFactory(tc -> new CheckBoxTableCell<>());
        kann.setEditable(true);

        Messverhalten mv = new Messverhalten();
        ol = FXCollections.observableArrayList(mv.getKannWelcheMessen());
        ol.sort(KannWelcheMesse.SORT);
        stdm.setReorderable(false);
        kann.setReorderable(false);
        stdm.setSortable(false);
        kann.setSortable(false);
        table.setItems(ol);
        table.setEditable(true);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stdm.setMaxWidth(1f * Integer.MAX_VALUE * 85);
        kann.setMaxWidth(1f * Integer.MAX_VALUE * 15);
    }

    private void updateAnvertrauteInView(
            List<Messdiener> updatedList,
            ListView<Label> listViewToBeUpdated
    ) {
        Label bearbeitenLabel = null;
        if (listViewToBeUpdated.getItems().contains(bearbeitenFreunde)) {
            bearbeitenLabel = bearbeitenFreunde;
        }
        if (listViewToBeUpdated.getItems().contains(bearbeitenGeschwister)) {
            bearbeitenLabel = bearbeitenGeschwister;
        }
        listViewToBeUpdated.getItems().removeIf(p -> true);
        for (Messdiener messdiener : updatedList) {
            if (moben != null && messdiener.toString().equals(moben.toString())) {
                continue;
            }
            listViewToBeUpdated.getItems().add(new Label(messdiener.toString()));
        }
        if (bearbeitenLabel != null) {
            listViewToBeUpdated.getItems().add(bearbeitenLabel);
        }
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    public boolean saveMessdiener() {
        if (name.getText().equals("") || vorname.getText().equals("")) {
            Dialogs.getDialogs().warn("Bitte einen Namen eintragen!");
        } else {
            try {
                ol = table.getItems();
                Messdiener m = new Messdiener(
                        null, vorname.getText(), name.getText(), new Email(email.getText()),
                        (int) eintritt.getValue(), leiter.isSelected(), Messverhalten.convert(ol)
                );
                m.setFreundeFromList(freundeArray);
                m.setGeschwisterFromList(geschwisterArray);
                if (moben != null) {
                    m.setFile(moben.getFile());
                }
                if (moben != null) {
                    handleChangedName(m);
                }
                anvertrauteEintragen(m);
                return true;
            } catch (Email.NotValidException e) {
                Dialogs.getDialogs().warn("Bitte eine richtige E-Mail oder nichts angeben!");
            }
        }
        return false;
    }

    private void handleChangedName(Messdiener m) {
        boolean nameChanged = moben.getNachname().equals(m.getNachname()) && moben.getVorname().equals(m.getVorname());
        if (!nameChanged) {
            try {
                Files.delete(moben.getFile().toPath());
            } catch (IOException e) {
                Dialogs.getDialogs().error(e, "Konnte den alten-veränderten Messdiener nicht löschen.");
            }
        }
    }

    public void setMessdiener(Messdiener messdiener) {
        if (messdiener == null) {
            name.setText("");
        } else {
            name.setText(messdiener.getNachname());
            vorname.setText(messdiener.getVorname());
            email.setText(messdiener.getEmail().toString());
            leiter.setSelected(messdiener.istLeiter());
            eintritt.setValue(messdiener.getEintritt());
            table.setItems(FXCollections.observableList(messdiener.getDienverhalten().getKannWelcheMessen()));

            freundeArray = FindMessdiener.updateFreunde(messdiener);
            updateAnvertrauteInView(freundeArray, freundeListView);
            geschwisterArray = FindMessdiener.updateGeschwister(messdiener);
            updateAnvertrauteInView(geschwisterArray, geschwisterListView);

            List<KannWelcheMesse> messen = messdiener.getDienverhalten().copy().getKannWelcheMessen();
            for (KannWelcheMesse kwm : ol) {
                for (KannWelcheMesse kwm2 : messen) {
                    if (kwm.getMesse().toString().equals(kwm2.getMesse().toString())
                            && kwm.kannDann() != kwm2.kannDann()) {
                        kwm.setKannDann(!kwm.kannDann());
                        break;
                    }
                }
            }
            moben = messdiener;
            getLogger().info("Messdiener wurde geladen");
        }
    }

    private void anvertrauteEintragen(Messdiener m) {
        List<Messdiener> bearbeitete = new ArrayList<>();
        for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
            bearbeitete.add(Messdiener.alteLoeschen(m, messdiener));
            if (moben != null) {
                bearbeitete.add(Messdiener.alteLoeschen(moben, messdiener));
            }
        }
        for (Messdiener medi : geschwisterArray) {
            medi.addGeschwister(m);
            bearbeitete.add(medi);
        }
        for (Messdiener medi : freundeArray) {
            medi.addFreund(m);
            bearbeitete.add(medi);
        }
        // speichern
        bearbeitete.add(m);
        RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
        bearbeitete = rd.removeDuplicatedEntries(bearbeitete);
        bearbeitete.removeIf(messdiener -> messdiener == null
                || moben != null && messdiener.hashCode() == moben.hashCode());
        for (Messdiener messdiener : bearbeitete) {
            messdiener.makeXML();
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().info("Messdiener {} wurde gespeichert!", m);
        }
    }
}
