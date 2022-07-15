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
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static net.aclrian.mpe.utils.Log.getLogger;

public class Select implements Controller {
    private final Selecter selecter;
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

    public Select(Selecter selecter, MainController mc) {
        this.selecter = selecter;
        this.mc = mc;
    }

    public static List<Messe> generiereDefaultMessen(Date anfang, Date ende) {
        ArrayList<Messe> rtn = new ArrayList<>();
        for (StandartMesse sm : DateienVerwalter.getInstance().getPfarrei().getStandardMessen()) {
            if (!(sm instanceof Sonstiges)) {
                LocalDate start = Instant.ofEpochMilli(anfang.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate end = Instant.ofEpochMilli(ende.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                List<Messe> m = optimieren(start, sm, end, new ArrayList<>());
                rtn.addAll(m);
            }
        }
        rtn.sort(Messe.compForMessen);
        Log.getLogger().info("DefaultMessen generiert");
        return rtn;
    }

    public static List<Messe> optimieren(LocalDate start, StandartMesse sm, LocalDate end, List<Messe> mes) {
        if (start.isBefore(end) && !(sm instanceof Sonstiges)) {
            if (start.getDayOfWeek().compareTo(sm.getWochentag()) == 0) {
                LocalDateTime messeTime = start.atTime(sm.getBeginnStunde(), sm.getBeginnMinute());
                Messe m = new Messe(messeTime, sm);
                mes.add(m);
                start = start.plusDays(7);
            } else {
                start = start.plusDays(1);
            }
            mes = optimieren(start, sm, end, mes);
        }
        return mes;
    }

    @Override
    public void initialize() {
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    public void afterstartup(Window window, MainController mc) {
        neu.setOnAction(e -> neu());
        if (selecter == Selecter.MESSDIENER) {
            text.setText("Messdiener anzeigen & bearbeiten");
            list.getItems().removeIf(t -> true);
            messdiener(window, mc);
        } else if (selecter == Selecter.MESSE) {
            Button generieren = new Button();
            generieren.setId(GENERIEREN_ID);
            generieren.setText("Messen generieren");
            text.setText("Messen anzeigen & bearbeiten");
            messe(mc, generieren);
        }
    }

    private void messe(MainController mc, Button generieren) {
        List<Messe> datam = mc.getMessen();
        generieren.setOnAction(getEventHandlerGenerateForMesse(datam));
        pane.getChildren().add(generieren);

        updateMesse(mc.getMessen());
        list.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && (mouseEvent.getClickCount() == 2)) {
                int i = list.getSelectionModel().getSelectedIndex();
                if (i >= 0 && i < datam.size()) mc.changePaneMesse(datam.get(i));
            }
        });
        bearbeiten.setOnAction(e -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && i < datam.size()) mc.changePaneMesse(datam.get(i));
        });
        remove.setOnAction(arg0 -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && (list.getSelectionModel().getSelectedItem().getText().replace("\t\t", "\t").equals(datam.get(i).toString()))) {
                datam.remove(i);
                updateMesse(mc.getMessen());
            }
        });
    }

    private EventHandler<ActionEvent> getEventHandlerGenerateForMesse(List<Messe> datam) {
        return arg0 -> {
            List<Date> daten = Dialogs.getDialogs().getDates(ZEITRAUM_WAEHLEN,
                    VON, BIS);
            if (!daten.isEmpty()) {
                try {
                    datam.addAll(Select.generiereDefaultMessen(daten.get(0), daten.get(1)));
                    datam.sort(Messe.compForMessen);
                    ArrayList<Label> l = new ArrayList<>();
                    for (Messe m : datam) {
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
        data.sort(Messdiener.compForMedis);
        for (Messdiener datum : data) {
            list.getItems().add(new Label(datum.toString()));
        }
        list.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && (mouseEvent.getClickCount() == 2)) {
                int i = list.getSelectionModel().getSelectedIndex();
                if (i >= 0 && i < data.size())
                    mc.changePaneMessdiener(data.get(i));
            }
        });
        bearbeiten.setOnAction(e -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && i < data.size()) mc.changePaneMessdiener(data.get(i));
        });
        remove.setOnAction(arg0 -> {
            int i = list.getSelectionModel().getSelectedIndex();
            if (i >= 0 && (list.getSelectionModel().getSelectedItem().getText().equals(data.get(i).toString()))
                    && MediController.remove(data.get(i))) {
                DateienVerwalter.getInstance().reloadMessdiener();
                afterstartup(window, mc);
            }
        });
    }

    private void updateMesse(List<Messe> datam) {
        list.getItems().removeIf(t -> true);
        datam.sort(Messe.compForMessen);
        for (Messe messe : datam) {
            list.getItems().add(new Label(messe.getID().replace("\t", "\t\t")));
        }
    }

    public void neu() {
        if (selecter == Selecter.MESSDIENER)
            mc.changePaneMessdiener(null);
        else if (selecter == Selecter.MESSE) mc.changePaneMesse(null);
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    public enum Selecter {
        MESSDIENER, MESSE
    }
}
