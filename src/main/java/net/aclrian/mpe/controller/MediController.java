package net.aclrian.mpe.controller;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.*;
import net.aclrian.fx.*;
import net.aclrian.mpe.controller.MainController.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messdiener.Messdiener.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.utils.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static net.aclrian.mpe.utils.Log.*;

public class MediController implements Controller {

    private static final String KONNTE = "Konnte den Messdiener '";
    public static final String FREUNDE_AUSWAEHLEN = "Freunde auswählen:";
    public static final String GESCHWISTER_AUSWAEHLEN = "Geschwister auswählen:";
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
    public static final String FREUNDE_BEARBEITEN_ID = "freundeId";
    public static final String GESCHWISTER_BEARBEITEN_ID = "geschwieId";

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

    private static Messdiener alteloeschen(Messdiener todel, Messdiener tosearchin) {
        if(todel.equals(tosearchin)){
            return todel;
        }
        Messdiener medi = null;
        for (int i = 0; i < tosearchin.getGeschwister().length; i++) {
            if (tosearchin.getGeschwister()[i].compareTo(todel.toString()) == 0) {
                tosearchin.getGeschwister()[i] = "";
                medi = tosearchin;
            }
        }
        for (int i = 0; i < tosearchin.getFreunde().length; i++) {
            if (tosearchin.getFreunde()[i].compareTo(todel.toString()) == 0) {
                tosearchin.getFreunde()[i] = "";
                medi = tosearchin;
            }
        }
        return medi;
    }

    public static boolean remove(Messdiener m) {
        if (Dialogs.getDialogs().frage("Soll der Messdiener '" + m + "' wirklich gelöscht werden?", ButtonType.CANCEL.getText(),
                "Löschen")) {
            File file = m.getFile();
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                return false;
            }
            ArrayList<Messdiener> ueberarbeitete = new ArrayList<>();
            for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
                ueberarbeitete.add(alteloeschen(m, messdiener));
            }
            ueberarbeitete.removeIf(Objects::isNull);
            for (Messdiener medi : ueberarbeitete) {
                if (medi.toString().equals(m.toString())) {
                    continue;
                }
                WriteFile wf = new WriteFile(medi);
                try {
                    wf.toXML();
                } catch (IOException e) {
                    Dialogs.getDialogs().error(e, "Konnte bei dem Bekannten '" + medi
                            + "' von dem zulöschenden Messdiener diesen nicht löschen.");
                }
            }
            return true;
        }
        return false;
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
                vorname.setText("");
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
            if (Boolean.FALSE.equals(newValue)) {
                email.setText(Messdiener.EMAIL_PATTERN.matcher(email.getText()).matches() ? email.getText() : "");
            }
        });
        // Value in Silder
        ASlider.makeASlider("Eintritt", eintritt, null);
        eintritt.setValue(Messdaten.getMaxYear());
        bearbeitenFreunde = new Label("Bearbeiten");
        bearbeitenFreunde.setStyle("-fx-font-style: italic;");
        bearbeitenFreunde.setId(FREUNDE_BEARBEITEN_ID);
        bearbeitenFreunde.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                List<Messdiener> selected = Dialogs.getDialogs().select(DateienVerwalter.getInstance().getMessdiener(), moben,
                        freund, FREUNDE_AUSWAEHLEN);
                if (selected.size() >= Messdiener.LENGHT_FREUNDE) {
                    Dialogs.getDialogs().error(
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
        bearbeitenGeschwister.setId(GESCHWISTER_BEARBEITEN_ID);
        bearbeitenGeschwister.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                List<Messdiener> g = Dialogs.getDialogs().select(DateienVerwalter.getInstance().getMessdiener(), moben, geschwi,
                        GESCHWISTER_AUSWAEHLEN);
                if (g.size() >= Messdiener.LENGHT_GESCHWISTER) {
                    Dialogs.getDialogs().error("Zu viele Geschwister: Bitte nur " + (Messdiener.LENGHT_GESCHWISTER - 1)
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
            if (moben != null && messdiener.toString().equals(moben.toString())) {
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
            if (moben != null && messdiener.toString().equals(moben.toString())) {
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
                Dialogs.getDialogs().warn("Bitte eine richtige E-Mail oder nichts angeben!");
            }
        } else {
            Dialogs.getDialogs().warn("Bitte einen Namen eintragen!");
        }
        return false;
    }

    public void setMedi(Messdiener messdiener) {
        if (messdiener == null) {
            name.setText("");
        } else {
            name.setText(messdiener.getNachnname());
            vorname.setText(messdiener.getVorname());
            email.setText(messdiener.getEmail());
            leiter.setSelected(messdiener.istLeiter());
            eintritt.setValue(messdiener.getEintritt());
            table.setItems(FXCollections.observableList(messdiener.getDienverhalten().getKannWelcheMessen()));
            updateFreunde(messdiener);
            updateGeschwister(messdiener);
            List<KannWelcheMesse> messen = messdiener.getDienverhalten().copy().getKannWelcheMessen();
            setDienverhalten(messen);
            moben = messdiener;
            getLogger().info("Messdiener wurde geladen");
        }
    }

    private void checkForChangedName(Messdiener m) {
        boolean b = moben.getNachnname().equals(m.getNachnname());
        boolean bo = moben.getVorname().equals(m.getVorname());
        if (!b || !bo) {
            try {
                Files.delete(moben.getFile().toPath());
            } catch (IOException e) {
                Dialogs.getDialogs().error(e, "Konnte den alten-veränderten Messdiener nicht löschen.");
            }
        }
    }

    private void setAndUpdateAnvertraute(Messdiener m) {
        m.setFreunde(getArrayString(freund, Messdiener.LENGHT_FREUNDE));
        m.setGeschwister(getArrayString(geschwi, Messdiener.LENGHT_GESCHWISTER));
        List<Messdiener> bearbeitete = new ArrayList<>();
        for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
            bearbeitete.add(alteloeschen(m, messdiener));
            if (moben != null) {
                bearbeitete.add(alteloeschen(moben, messdiener));
            }
        }
        for (Messdiener medi : geschwi) {
            addBekanntschaft(medi, m, true);
            bearbeitete.add(medi);
        }
        for (Messdiener medi : freund) {
            addBekanntschaft(medi, m, false);
            bearbeitete.add(medi);
        }
        bearbeitete.removeIf(Objects::isNull);
        // speichern
        bearbeitete.add(m);
        RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
        bearbeitete = rd.removeDuplicatedEntries(bearbeitete);
        bearbeitete.removeIf(messdiener -> moben != null && messdiener.hashCode() == moben.hashCode());
        try {
            for (Messdiener messdiener : bearbeitete) {
                WriteFile wf = new WriteFile(messdiener);
                wf.toXML();
            }
            if (Log.getLogger().isDebugEnabled()) {
                getLogger().info("Messdiener {} wurde gespeichert!", m);
            }
            DateienVerwalter.getInstance().reloadMessdiener();
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, KONNTE + m + "' nicht speichern");
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
        List<Messdiener> alle = DateienVerwalter.getInstance().getMessdiener();
        ArrayList<Messdiener> al = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (searchForMessdiener(arrayList, alle, al, i)) continue;
            boolean beheben = Dialogs.getDialogs().frage(
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
        List<Messdiener> alle = DateienVerwalter.getInstance().getMessdiener();
        ArrayList<Messdiener> al = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (searchForMessdiener(arrayList, alle, al, i)) continue;
            boolean beheben = Dialogs.getDialogs().frage(
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
