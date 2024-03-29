package net.aclrian.mpe.controller;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import net.aclrian.mpe.algorithms.Generation;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.Person;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Select implements Controller {
    private final Selector selector;
    private final MainController mc;
    public static final String GENERIEREN_ID = "generierenID";
    public static final String ZEITRAUM_WAEHLEN = "Für welchen Zeitraum sollen Messen generiert werden?";
    public static final String VON = "Von:";
    public static final String BIS = "Bis:";
    @FXML
    private GridPane gpane;
    @FXML
    private FlowPane pane;
    @FXML
    private Label text;
    @FXML
    private ListView<Label> list;
    @FXML
    private Button neu;
    @FXML
    private Button bearbeiten;
    @FXML
    private Button remove;

    public Select(Selector selector, MainController mc) {
        this.selector = selector;
        this.mc = mc;
    }

    @Override
    public void initialize() {
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    public void afterStartup(Window window, MainController mc) {
        neu.setOnAction(e -> neu());
        if (selector == Selector.MESSDIENER) {
            text.setText("Messdiener anzeigen & bearbeiten");
            list.getItems().removeIf(t -> true);
            messdiener(window, mc);
        } else if (selector == Selector.MESSE) {
            Button generieren = new Button();
            generieren.setId(GENERIEREN_ID);
            generieren.setText("Messen generieren");
            text.setText("Messen anzeigen & bearbeiten");
            messe(mc, generieren);
        }
    }

    private void messe(MainController mc, Button generieren) {
        List<Messe> messen = mc.getMessen();
        generieren.setOnAction(getEventHandlerGenerateForMesse(messen));
        pane.getChildren().add(generieren);

        updateMesse(mc.getMessen());
        list.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                int i = list.getSelectionModel().getSelectedIndex();
                if (i >= 0 && i < messen.size()) {
                    mc.changePane(messen.get(i));
                }
            }
        });
        bearbeiten.setOnAction(e -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && i < messen.size()) {
                mc.changePane(messen.get(i));
            }
        });
        remove.setOnAction(arg0 -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && list.getSelectionModel().getSelectedItem().getText().replace("\t\t", "\t").equals(messen.get(i).toString())) {
                messen.remove(i);
                updateMesse(mc.getMessen());
            }
        });
    }

    private EventHandler<ActionEvent> getEventHandlerGenerateForMesse(List<Messe> messen) {
        return arg0 -> {
            List<LocalDate> daten = Dialogs.getDialogs().getDates(ZEITRAUM_WAEHLEN,
                    VON, BIS);
            if (!daten.isEmpty()) {
                try {
                    messen.addAll(Generation.generiereDefaultMessen(daten.get(0), daten.get(1)));
                    messen.sort(Messe.MESSE_COMPARATOR);
                    ArrayList<Label> l = new ArrayList<>();
                    for (Messe m : messen) {
                        l.add(new Label(m.getID().replace("\t", "\t\t")));
                    }
                    list.getItems().removeIf(p -> true);
                    list.setItems(FXCollections.observableArrayList(l));
                } catch (Exception e) {
                    Dialogs.getDialogs().error(e, "Konnte die Messen nicht generieren.");
                }
            }
        };
    }

    private void messdiener(Window window, MainController mc) {
        List<Messdiener> data = DateienVerwalter.getInstance().getMessdiener();
        data.sort(Person.PERSON_COMPARATOR);
        for (Messdiener messdiener : data) {
            list.getItems().add(new Label(messdiener.toString()));
        }
        list.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                int i = list.getSelectionModel().getSelectedIndex();
                if (i >= 0 && i < data.size()) {
                    mc.changePane(data.get(i));
                }
            }
        });
        bearbeiten.setOnAction(e -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && i < data.size()) {
                mc.changePane(data.get(i));
            }
        });
        remove.setOnAction(arg0 -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && list.getSelectionModel().getSelectedItem().getText().equals(data.get(i).toString())
                    && Dialogs.remove(data.get(i))) {
                afterStartup(window, mc);
            }
        });
    }

    private void updateMesse(List<Messe> messen) {
        list.getItems().removeIf(t -> true);
        messen.sort(Messe.MESSE_COMPARATOR);
        for (Messe messe : messen) {
            list.getItems().add(new Label(messe.getID().replace("\t", "\t\t")));
        }
    }

    public void neu() {
        if (selector == Selector.MESSDIENER) {
            mc.changePane(MainController.EnumPane.MESSDIENER);
        } else if (selector == Selector.MESSE) {
            mc.changePane(MainController.EnumPane.MESSE);
        }
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    public enum Selector {
        MESSDIENER, MESSE
    }
}
