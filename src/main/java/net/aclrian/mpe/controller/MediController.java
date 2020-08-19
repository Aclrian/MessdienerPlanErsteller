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
import net.aclrian.mpe.controller.MainController.EnumPane;
import net.aclrian.mpe.messdiener.KannWelcheMesse;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.Messdiener.NotValidException;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.RemoveDoppelte;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.aclrian.mpe.utils.Log.getLogger;

public class MediController implements Controller {

    private static final String KONNTE = "Konnte den Messdiener '";
    private List<Messdiener> freund;
    private List<Messdiener> geschwi;
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
    private ListView<Label> geschwie;
    @FXML
    private ListView<Label> freunde;
    @FXML
    private SplitMenuButton button;
    @FXML
    private MenuItem cancel;
    @FXML
    private MenuItem saveNew;

    public static String[] getArrayString(List<?> freunde2, int begr) {
        String[] s = new String[begr];
        for (int i = 0; i < s.length; i++) {
            try {
                s[i] = freunde2.get(i).toString();
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                s[i] = "";
            }
        }
        return s;
    }

    private static void alteloeschen(Messdiener m, String[] array, ArrayList<Messdiener> ueberarbeitete) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(m.toString())) {
                array[i] = "";
                ueberarbeitete.add(m);
            }
        }
    }

    public static boolean remove(Messdiener m) {
        if (Dialogs.frage("Soll der Messdiener '" + m + "' wirklich gelöscht werden?", ButtonType.CANCEL.getText(),
                "Löschen")) {
            File file = m.getFile();
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                return false;
            }
            ArrayList<Messdiener> ueberarbeitete = new ArrayList<>();
            for (Messdiener messdiener : DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList()) {
                alteloeschen(m, messdiener.getFreunde(), ueberarbeitete);
                alteloeschen(m, messdiener.getGeschwister(), ueberarbeitete);
            }
            for (Messdiener medi : ueberarbeitete) {
                if (medi.toString().equals(m.toString())) {
                    continue;
                }
                WriteFile wf = new WriteFile(medi);
                try {
                    wf.toXML();
                } catch (IOException e) {
                    Dialogs.error(e, "Konnte bei dem Bekannten '" + medi
                            + "' von dem zulöschenden Messdiener diesen nicht löschen.");
                }
            }
            return true;
        }
        return false;
    }

    private static ArrayList<Messdiener> alteloeschen(Messdiener m, String[] array) {
        ArrayList<Messdiener> ueberarbeitete = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(m.toString())) {
                array[i] = "";
                ueberarbeitete.add(m);
            }
        }
        return ueberarbeitete;
    }

    public void initialize() {
        freund = new ArrayList<>();
        geschwi = new ArrayList<>();
        eintritt.setMax(Messdaten.getMaxYear());
        eintritt.setMin(Messdaten.getMinYear());
    }

    @Override
    public void afterstartup(Window window, MainController mc) {
        cancel.setOnAction(e -> {
            locked = false;
            mc.changePane(EnumPane.SELECT_MEDI);
        });
        saveNew.setOnAction(e -> {
            if (getMedi()) {
                locked = false;
                mc.changePaneMesse(null);
            }
        });
        button.setOnAction(e -> {
            if (getMedi()) {
                locked = false;
                mc.changePane(EnumPane.SELECT_MEDI);
            }
        });
        // Email valid
        email.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            boolean neu = newValue;
            if (neu) {
                email.setText(EmailValidator.getInstance().isValid(email.getText()) ? email.getText() : "");
            }
        });
        // Value in Silder
        ASlider.makeASlider("Eintritt", eintritt, null);
        eintritt.setValue(Messdaten.getMaxYear());
        bearbeitenFreunde = new Label("Bearbeiten");
        bearbeitenFreunde.setStyle("-fx-font-style: italic;");
        bearbeitenFreunde.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                List<Messdiener> selected = Dialogs.select(DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList(), moben,
                        freund, "Freunde auswählen:");
                if (selected.size() >= Messdiener.LENGHT_FREUNDE) {
                    Dialogs.error(
                            "Zu viele Freunde: Bitte nur " + (Messdiener.LENGHT_FREUNDE - 1) + " Messdiener angeben.");
                    handle(arg0);
                    return;
                }
                updateFreunde(selected);
            }
        });
        freunde.getItems().add(bearbeitenFreunde);

        bearbeitenGeschwister = new Label("Bearbeiten");
        bearbeitenGeschwister.setStyle("-fx-font-style: italic;");
        bearbeitenGeschwister.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                List<Messdiener> g = Dialogs.select(DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList(), moben, geschwi,
                        "Geschwister auswählen:");
                if (g.size() >= Messdiener.LENGHT_GESCHWISTER) {
                    Dialogs.error("Zu viele Geschwister: Bitte nur " + (Messdiener.LENGHT_GESCHWISTER - 1)
                            + " Messdiener angeben.");
                    handle(arg0);
                    return;
                }
                updateGeschwister(g);
            }
        });
        geschwie.getItems().add(bearbeitenGeschwister);

        stdm.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMesse().tokurzerBenutzerfreundlichenString()));
        kann.setCellFactory(CheckBoxTableCell.forTableColumn(kann));
        kann.setCellValueFactory(celldata -> {
            KannWelcheMesse cellValue = celldata.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(cellValue.isKanndann());
            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> cellValue.setKanndann(newValue));
            return property;
        });
        kann.setCellFactory(tc -> new CheckBoxTableCell<>());
        kann.setEditable(true);
        Messverhalten mv = new Messverhalten();
        ol = FXCollections.observableArrayList(mv.getKannWelcheMessen());
        ol.sort(KannWelcheMesse.sort);
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

    private void updateFreunde(List<Messdiener> freund) {
        this.freund = freund;
        freunde.getItems().removeIf(p -> true);
        for (Messdiener messdiener : this.freund) {
            if (moben != null && messdiener.makeId().equals(moben.makeId())) {
                continue;
            }
            freunde.getItems().add(new Label(messdiener.toString()));
        }
        freunde.getItems().add(bearbeitenFreunde);
    }

    private void updateGeschwister(List<Messdiener> geschwi2) {
        geschwi = geschwi2;
        geschwie.getItems().removeIf(p -> true);
        for (Messdiener messdiener : geschwi) {
            if (moben != null && messdiener.makeId().equals(moben.makeId())) {
                continue;
            }
            geschwie.getItems().add(new Label(messdiener.toString()));
        }
        geschwie.getItems().add(bearbeitenGeschwister);
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    private void setDienverhalten(List<KannWelcheMesse> o) {
        for (KannWelcheMesse kwm : ol) {
            for (KannWelcheMesse kwm2 : o) {
                if (kwm.getMesse().toString().equals(kwm2.getMesse().toString())
                        && (kwm.isKanndann() != kwm2.isKanndann())) {
                    kwm.setKanndann(!kwm.isKanndann());
                }
            }
        }
    }

    public boolean getMedi() {
        if (!name.getText().equals("") && !vorname.getText().equals("")) {
            try {
                // ME
                Messdiener m = new Messdiener(null);
                if (moben != null) {
                    m.setFile(moben.getFile());
                }
                ol = table.getItems();
                m.setzeAllesNeu(vorname.getText(), name.getText(), (int) eintritt.getValue(), leiter.isSelected(),
                        Messverhalten.convert(ol), email.getText());
                if (moben != null) {
                    checkForChangedName(m);
                }
                setAndUpdateAnvertraute(m);
                return true;
            } catch (NotValidException e) {
                Dialogs.warn("Bitte eine richtige E-Mail oder nichts angeben!");
            }
        } else {
            Dialogs.warn("Bitte einen Namen eintragen!");
        }
        return false;
    }

    public void setMedi(Messdiener messdiener) {
        if (messdiener == null) return;
        name.setText(messdiener.getNachnname());
        vorname.setText(messdiener.getVorname());
        email.setText(messdiener.getEmail());
        leiter.setSelected(messdiener.isIstLeiter());
        eintritt.setValue(messdiener.getEintritt());
        table.setItems(FXCollections.observableList(messdiener.getDienverhalten().getKannWelcheMessen()));
        updateFreunde(messdiener);
        updateGeschwister(messdiener);
        List<KannWelcheMesse> messen = messdiener.getDienverhalten().copy().getKannWelcheMessen();
        setDienverhalten(messen);
        moben = messdiener;
        getLogger().info("Messdiener wurde geladen");
    }

    private void checkForChangedName(Messdiener m) {
        boolean b = moben.getNachnname().equals(m.getNachnname());
        boolean bo = moben.getVorname().equals(m.getVorname());
        if (!b || !bo) {
            try {
                Files.delete(moben.getFile().toPath());
            } catch (IOException e) {
                Dialogs.error(e, "Konnte den alten-veränderten Messdiener nicht löschen.");
            }
        }
    }

    private void setAndUpdateAnvertraute(Messdiener m) {
        m.setFreunde(getArrayString(freund, Messdiener.LENGHT_FREUNDE));
        m.setGeschwister(getArrayString(geschwi, Messdiener.LENGHT_GESCHWISTER));
        List<Messdiener> bearbeitete = new ArrayList<>();
        for (Messdiener messdiener : DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList()) {
            bearbeitete.addAll(alteloeschen(m, messdiener.getFreunde()));
            bearbeitete.addAll(alteloeschen(m, messdiener.getGeschwister()));
            if (moben != null) {
                bearbeitete.addAll(alteloeschen(moben, messdiener.getFreunde()));
                bearbeitete.addAll(alteloeschen(moben, messdiener.getGeschwister()));
            }
        }
        for (Messdiener medi : geschwi) {
            addBekanntschaft(medi, m, true);
            bearbeitete.add(medi);
        }
        for (Messdiener medi : freund) {
            addBekanntschaft(medi, m, false);
        }
        bearbeitete.add(m);
        // speichern
        RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
        bearbeitete = rd.removeDuplicatedEntries(bearbeitete);
        try {
            for (Messdiener messdiener : bearbeitete) {
                WriteFile wf = new WriteFile(messdiener);
                wf.toXML();
            }
            getLogger().info("Messdiener " + m.makeId() + " wurde gespeichert!");
            DateienVerwalter.getDateienVerwalter().reloadMessdiener();
        } catch (IOException e) {
            Dialogs.error(e, KONNTE + m + "' nicht speichern");
        }
    }

    private void addBekanntschaft(Messdiener medi, Messdiener woben, boolean isGeschwister) {
        if (!isGeschwister) {
            ArrayList<String> freundeStrings = new ArrayList<>(Arrays.asList(medi.getFreunde()));
            medi.setFreunde(addToArray(freundeStrings, woben.toString(), Messdiener.LENGHT_FREUNDE));
        } else {
            ArrayList<String> geschwisterStrings = new ArrayList<>(Arrays.asList(medi.getGeschwister()));
            medi.setGeschwister(addToArray(geschwisterStrings, woben.toString(), Messdiener.LENGHT_GESCHWISTER));
        }
    }

    private String[] addToArray(ArrayList<String> strings, String toAdded, int limit) {
        strings.add(toAdded);
        strings.removeIf(t -> t == null || t.equals(""));
        String[] s = new String[limit];
        strings.toArray(s);
        return s;
    }

    private void updateFreunde(Messdiener medi) {
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(medi.getFreunde()));
        List<Messdiener> alle = DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList();
        ArrayList<Messdiener> al = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (searchForMessdiener(arrayList, alle, al, i)) continue;
            boolean beheben = Dialogs.frage(
                    KONNTE + arrayList.get(i) + "' als Freund von '" + medi + "' nicht finden!",
                    "Ignorieren", "Beheben");
            if (beheben) {
                arrayList.remove(i);
                String[] freundeArray = arrayList.toArray(new String[0]);
                medi.setFreunde(freundeArray);
                updateFreunde(medi);
                return;
            }
        }
        updateFreunde(al);
    }

    private boolean searchForMessdiener(List<String> arrayList, List<Messdiener> alle, List<Messdiener> al, int i) {
        if (arrayList.get(i).equals("")) {
            return true;
        }
        for (Messdiener messdiener : alle) {
            if (arrayList.get(i).equals(messdiener.toString())) {
                al.add(messdiener);
                break;
            }
        }
        return !al.isEmpty();
    }

    private void updateGeschwister(Messdiener medi) {
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(medi.getGeschwister()));
        List<Messdiener> alle = DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList();
        ArrayList<Messdiener> al = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (searchForMessdiener(arrayList, alle, al, i)) continue;
            boolean beheben = Dialogs.frage(
                    KONNTE + arrayList.get(i) + "' als Geschwister von '" + medi + "' nicht finden!",
                    "ignorieren", "beheben");
            if (beheben) {
                arrayList.remove(i);
                String[] gew = arrayList.toArray(new String[0]);
                medi.setGeschwister(gew);
                updateGeschwister(medi);
                return;
            }
        }
        updateGeschwister(al);
    }
}
