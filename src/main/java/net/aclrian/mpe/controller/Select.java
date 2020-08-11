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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static net.aclrian.mpe.utils.Log.getLogger;

public class Select implements Controller {
    private final Selecter selecter;
    private final MainController mc;
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

    public static List<Messe> generireDefaultMessen(Date anfang, Date ende) {
        ArrayList<Messe> rtn = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        for (StandartMesse sm : DateienVerwalter.getDateienVerwalter().getPfarrei().getStandardMessen()) {
            if (!(sm instanceof Sonstiges)) {
                start.setTime(anfang);
                List<Messe> m = optimieren(start, sm, ende, new ArrayList<>());
                rtn.addAll(m);
            }
        }
        rtn.sort(Messe.compForMessen);
        Log.getLogger().info("DefaultMessen generiert");
        return rtn;
    }

    public static List<Messe> optimieren(Calendar cal, StandartMesse sm, Date end, List<Messe> mes) {
        if (cal.getTime().before(end) && !(sm instanceof Sonstiges)) {
            SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
            String tag = wochendagformat.format(cal.getTime());
            if (tag.startsWith(sm.getWochentag())) {
                Date d = cal.getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat dfuhr = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
                try {
                    Date frting = dfuhr
                            .parse(df.format(d) + "-" + sm.getBeginnStundealsString() + ":" + sm.getBeginnMinuteString());
                    Messe m = new Messe(frting, sm);
                    mes.add(m);
                } catch (ParseException e) {
                    getLogger().info("Parse Exception");
                }
                cal.add(Calendar.DATE, 7);
            } else {
                cal.add(Calendar.DATE, 1);
            }
            mes = optimieren(cal, sm, end, mes);
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
            if (i >= 0 && (list.getSelectionModel().getSelectedItem().getText().replaceAll("\t\t", "\t").equals(datam.get(i).toString()))) {
                datam.remove(i);
                updateMesse(mc.getMessen());
            }
        });
    }

    private EventHandler<ActionEvent> getEventHandlerGenerateForMesse(List<Messe> datam) {
        return arg0 -> {
            List<Date> daten = Dialogs.getDates("Für welchen Zeitraum sollen Messen generiert werden?",
                    "Von:", "Bis:");
            if (!daten.isEmpty()) {
                try {
                    datam.addAll(Select.generireDefaultMessen(daten.get(0), daten.get(1)));
                    datam.sort(Messe.compForMessen);
                    ArrayList<Label> l = new ArrayList<>();
                    for (Messe m : datam) {
                        l.add(new Label(m.getID().replaceAll("\t", "\t\t")));
                    }
                    list.getItems().removeIf(p -> true);
                    list.setItems(FXCollections.observableArrayList(l));
                } catch (Exception e) {
                    Dialogs.error(e, "Konnte die Messen nicht generieren.");
                }
            }
        };
    }

    private void messdiener(Window window, MainController mc) {
        List<Messdiener> data = DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList();
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
                DateienVerwalter.getDateienVerwalter().reloadMessdiener();
                afterstartup(window, mc);
            }
        });
    }

    private void updateMesse(List<Messe> datam) {
        list.getItems().removeIf(t -> true);
        datam.sort(Messe.compForMessen);
        for (Messe messe : datam) {
            list.getItems().add(new Label(messe.getID().replaceAll("\t", "\t\t")));
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
