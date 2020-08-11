package net.aclrian.mpe.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.RemoveDoppelte;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FerienplanController implements Controller {
    private final ArrayList<String> dates = new ArrayList<>();
    @FXML
    private TableColumn<Datentraeger, String> name;
    @FXML
    private TableView<Datentraeger> table;

    @FXML
    private Button zurueck;
    @FXML
    private Button leeren;
    @FXML
    private Button hilfe;
    @FXML
    private Button fertig;

    @Override
    public void initialize() {
        //no operation
    }

    @Override
    public void afterstartup(Window window, MainController mc) {
        List<Messdiener> medis = DateienVerwalter.getDateienVerwalter().getAlleMedisVomOrdnerAlsList();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<Datentraeger> daten = new ArrayList<>();
        for (Messe messe : mc.getMessen()) {
            dates.add(df.format(messe.getDate()));
        }
        RemoveDoppelte<String> rd = new RemoveDoppelte<>();
        rd.removeDuplicatedEntries(dates);
        dates.sort((o1, o2) -> {
            try {
                Date d1 = df.parse(o1);
                Date d2 = df.parse(o2);
                return d1.compareTo(d2);
            } catch (ParseException e) {
                e.printStackTrace();
                return o1.compareTo(o2);
            }
        });
        TableColumn<Datentraeger, String> column = new TableColumn<>();
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMessdiener()));
        column.setText("Messdiener");
        column.setPrefWidth(100);
        table.getColumns().add(column);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        for (String date : dates) {
            TableColumn<Datentraeger, Boolean> col = new TableColumn<>();
            col.setCellValueFactory(param -> param.getValue().get(date).getProperty());
            col.setCellFactory(param -> new CheckBoxTableCell<>());
            col.setEditable(true);
            col.setPrefWidth(100);
            col.setText(date);
            table.getColumns().add(col);
        }
        table.setEditable(true);
        column.setEditable(false);
        for (Messdiener m : medis) {
            daten.add(new Datentraeger(df, dates, m));
        }
        table.setItems(FXCollections.observableArrayList(daten));
        fertig.setOnAction(event -> mc.changePane(MainController.EnumPane.PLAN));
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                int i = table.getFocusModel().getFocusedCell().getColumn();
                if (i != 0) {
                    int row = table.getFocusModel().getFocusedCell().getRow();
                    String s = dates.get(i - 1);
                    table.getItems().get(row).get(s).setB(!table.getItems().get(row).get(s).isB());
                    table.refresh();
                    table.getFocusModel().focusLeftCell();
                    table.getFocusModel().focusRightCell();
                }
            }
        });
        zurueck.setOnAction(e -> mc.changePane(MainController.EnumPane.START));
        leeren.setOnAction(e -> {
            for (String d : dates) {
                table.getItems().forEach(dt -> dt.get(d).getProperty().set(false));
            }
        });
        hilfe.setOnAction(e -> Dialogs.info("Hier können Messdiener für bestimmte Tage abgemeldet werden:\n\nEin Hacken in einem Kasten heißt, dass der Messdiener an dem Tag nicht eingeteilt wird.\nMit den Pfeiltasten kann die Zelle gewechselt werden und mit dem Leerzeichen der Hacken gesetzt und entfernt werden.\n\nÄnderungen werden sofort umgesetzt und können nur durch Leeren komplett zurückgesetzt werden."));
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    private static class Datentraeger {
        private final HashMap<String, Datum> stringDatumHashMap = new HashMap<>();
        private final Messdiener m;

        public Datentraeger(SimpleDateFormat df, List<String> dates, Messdiener messdiener) {
            m = messdiener;
            for (String d : dates) {
                Datum datum = new Datum(messdiener.getMessdatenDaten().ausgeteilt(d));
                datum.getProperty().addListener((observable, oldValue, newValue) -> {
                    boolean old = oldValue;
                    boolean neu = newValue;
                    if (old != neu) {
                        try {
                            if (neu) {
                                messdiener.getMessdatenDaten().austeilen(df.parse(d));
                            } else {
                                messdiener.getMessdatenDaten().ausausteilen(df.parse(d));
                            }
                        } catch (ParseException e) {
                            Dialogs.error(e, "Konnte das Datum nicht bekommen.");
                        }
                    }
                });
                stringDatumHashMap.put(d, datum);
            }
        }

        public Datum get(String d) {
            return stringDatumHashMap.get(d);
        }

        public String getMessdiener() {
            return m.makeId();
        }

        private static class Datum {
            private final SimpleBooleanProperty b;

            public Datum(boolean b) {
                this.b = new SimpleBooleanProperty(b);
            }

            public boolean isB() {
                return b.get();
            }

            public void setB(boolean b) {
                this.b.setValue(b);
            }

            public SimpleBooleanProperty getProperty() {
                return b;
            }
        }
    }


}
