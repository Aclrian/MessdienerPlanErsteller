package net.aclrian.mpe.utils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Setting;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.List;
import java.util.*;

public class Dialogs {

    public static final String ICON = "/images/title_32.png";
    private static Dialogs dialogs;

    private Dialogs() {
    }

    public static Dialogs getDialogs() {
        if (dialogs == null) {
            dialogs = new Dialogs();
        }
        return dialogs;
    }

    public static void setDialogs(Dialogs dialogs) {
        Dialogs.dialogs = dialogs;
    }

    private Alert alertbuilder(AlertType type, String headertext) {
        Alert a = new Alert(type);
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        a.setHeaderText(headertext);
        a.setTitle("MessdienerplanErsteller");

        return a;
    }

    private Alert alertbuilder(AlertType type, String headertext, Node n) {
        Alert a = alertbuilder(type, headertext);
        a.getDialogPane().setExpandableContent(n);
        a.getDialogPane().setExpanded(true);

        return a;
    }

    public void info(String string) {
        Log.getLogger().info(string);
        alertbuilder(AlertType.INFORMATION, string).showAndWait();
    }

    public void info(String header, String string) {
        Log.getLogger().info(string);
        Alert a = alertbuilder(AlertType.INFORMATION, header);
        a.setContentText(string);
        a.showAndWait();
    }

    public void warn(String string) {
        Log.getLogger().warn(string);
        alertbuilder(AlertType.WARNING, string).showAndWait();
    }

    public void error(String string) {
        Log.getLogger().error(string);
        if (Platform.isFxApplicationThread()) {
            alertbuilder(AlertType.ERROR, string).showAndWait();
        } else {
            Platform.runLater(() -> alertbuilder(AlertType.INFORMATION, string).showAndWait());
        }
    }

    public void error(Exception e, String string) {
        Log.getLogger().error(string);
        StringWriter stack = new StringWriter();
        e.printStackTrace(new PrintWriter(stack));
        Log.getLogger().error(stack.toString());
        String header;
        if (e.getLocalizedMessage() != null && !e.getLocalizedMessage().equals("")) {
            header = string + "\n" + e.getLocalizedMessage();
        } else if (e.getMessage() != null && !e.getMessage().equals("")) {
            header = string + "\n" + e.getMessage();
        } else {
            header = string;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        VBox vb = new VBox();
        Label l = new Label("Stacktrace:");
        TextArea ta = new TextArea(sw.toString());
        ta.setEditable(false);
        vb.getChildren().addAll(l, ta);

        if (Platform.isFxApplicationThread()) {
            alertbuilder(AlertType.ERROR, header, vb).showAndWait();

        } else {
            Platform.runLater(() -> alertbuilder(AlertType.INFORMATION, string).showAndWait());
        }
    }

    public void open(URI open, String string) throws IOException {
        Alert a = alertbuilder(AlertType.CONFIRMATION, string);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && (res.get() == ButtonType.OK)) {
            Desktop.getDesktop().browse(open);
        }
    }

    public void open(URI open, String string, String ok, String close) {
        ButtonType od = new ButtonType(ok, ButtonBar.ButtonData.OK_DONE);
        ButtonType cc = new ButtonType(close, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(AlertType.CONFIRMATION, "", od, cc);
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        a.setHeaderText(string);
        a.setTitle("MessdienerplanErsteller");
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == od) {
            try {
                Desktop.getDesktop().browse(open);
            } catch (IOException e) {
                dialogs.error("Konnte die URL: " + open + " nicht öffnen");
            }
        }
    }

    public boolean frage(String string) {
        Optional<ButtonType> res = alertbuilder(AlertType.CONFIRMATION, string).showAndWait();
        return res.isPresent() && (res.get() == ButtonType.OK);
    }

    public boolean frage(String string, String cancel, String ok) {
        Alert a = alertbuilder(AlertType.CONFIRMATION, string);
        ((Button) a.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(cancel);
        ((Button) a.getDialogPane().lookupButton(ButtonType.OK)).setText(ok);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && (res.get() == ButtonType.OK);
    }

    public YesNoCancelEnum yesNoCancel(String yes, String no, String cancel, String string) {
        ButtonType yesbtn = new ButtonType(yes, ButtonBar.ButtonData.YES);
        ButtonType cc = new ButtonType(cancel, ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType nobtn = new ButtonType(no, ButtonBar.ButtonData.NO);
        Alert a = new Alert(AlertType.CONFIRMATION, "", yesbtn, cc, nobtn);
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        a.setHeaderText(string);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == cc) return YesNoCancelEnum.CANCEL;
        return res.isPresent() && (res.get() == yesbtn) ? YesNoCancelEnum.YES : YesNoCancelEnum.NO;
    }

    public void fatal(String string) {
        error(string);
        System.exit(-1);
    }

    public <I> Object singleSelect(List<? extends I> list, String s) {
        Alert a = new Alert(AlertType.INFORMATION);
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        a.setHeaderText(s);
        VBox v = new VBox();
        v.setSpacing(5d);
        ToggleGroup g = new ToggleGroup();
        for (Object value : list) {
            ARadioButton<?> b = new ARadioButton<>(value);
            b.setToggleGroup(g);
            v.getChildren().add(b);
        }
        alertbuilder(AlertType.INFORMATION, s, v).showAndWait();
        if (g.getSelectedToggle() instanceof ARadioButton) {
            return ((ARadioButton<?>) g.getSelectedToggle()).getI();
        }
        return null;
    }

    public <I> List<I> select(List<I> dataAmBestenSortiert, List<I> selected, String string) {
        Alert a = alertbuilder(AlertType.INFORMATION, string);
        ListView<CheckBox> lw = new ListView<>();
        ArrayList<I> rtn = new ArrayList<>();
        for (I item : dataAmBestenSortiert) {
            boolean b = false;
            for (I value : selected) {
                if (value.toString().equals(item.toString())) {
                    rtn.add(item);
                    b = true;
                    break;
                }
            }
            String s = item instanceof StandartMesse ? ((StandartMesse) item).tolangerBenutzerfreundlichenString() : item.toString();
            CheckBox ch = new CheckBox(s);
            ch.setSelected(b);
            ch.selectedProperty().addListener((arg0, oldB, neuB) -> {
                boolean neu = neuB;
                boolean old = oldB;
                if (neu && !old) {
                    rtn.add(item);
                } else if (!neu && old) {
                    rtn.remove(item);
                }
            });
            lw.getItems().add(ch);
        }
        a.getDialogPane().setExpandableContent(lw);
        a.getDialogPane().setExpanded(true);
        a.showAndWait();
        return rtn;
    }


    public List<Date> getDates(String string, String von, String bis) {
        DatePicker d1 = new DatePicker();
        d1.setPromptText(von);
        DatePicker d2 = new DatePicker();
        d2.setPromptText(bis);
        HBox hb = new HBox(d1, d2);
        hb.setSpacing(20d);
        Alert a = alertbuilder(AlertType.INFORMATION, string, hb);
        ChangeListener<Object> e = (arg0, arg1, arg2) -> {
            if (d1.getValue() != null && d2.getValue() != null &&
                    (!Date.from(d1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                            .after(Date.from(d2.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())))) {
                a.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                return;
            }
            a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        };
        d1.valueProperty().addListener(e);
        d1.focusedProperty().addListener(e);
        d2.valueProperty().addListener(e);
        d2.focusedProperty().addListener(e);
        Optional<ButtonType> o = a.showAndWait();
        if (o.isPresent() && o.get().equals(ButtonType.OK)) {
            ArrayList<Date> rtn = new ArrayList<>();
            rtn.add(0, Date.from(d1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            rtn.add(1, Date.from(d2.getValue().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()));
            return rtn;
        }
        return Collections.emptyList();
    }

    public StandartMesse standartmesse(StandartMesse sm) {
        TextField ort = new TextField();
        ort.setPromptText("Ort:");
        TextField typ = new TextField();
        typ.setPromptText("Typ:");
        ComboBox<String> wochentag = new ComboBox<>(
                FXCollections.observableArrayList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"));
        wochentag.setPromptText("Wochentag:");
        Slider stunde = new Slider();
        stunde.setMin(0);
        stunde.setMax(24);
        stunde.setBlockIncrement(1);

        Slider minute = new Slider();
        minute.setMin(0);
        minute.setMax(59);
        minute.setBlockIncrement(1);

        Slider anz = new Slider();
        anz.setMin(0);
        anz.setMax(40);
        anz.setBlockIncrement(1);

        VBox v = new VBox(wochentag, ort, typ, stunde, minute, anz);
        v.setSpacing(20);
        Alert a = alertbuilder(AlertType.INFORMATION, "Neue Standartmesse erstellen:");

        ChangeListener<Object> e = (arg0, arg1, arg2) -> {
            try {
                boolean okDisabled = ort.getText().equals("") || typ.getText().equals("") || wochentag.getValue().isBlank();
                a.getDialogPane().lookupButton(ButtonType.OK).setDisable(okDisabled);
            } catch (NullPointerException e2) {
                a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            }
        };
        typ.textProperty().addListener(e);
        typ.focusedProperty().addListener(e);
        ort.textProperty().addListener(e);
        ort.focusedProperty().addListener(e);
        wochentag.valueProperty().addListener(e);
        wochentag.focusedProperty().addListener(e);
        a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        a.getDialogPane().setExpandableContent(v);
        a.getDialogPane().setExpanded(true);
        a.setOnShown(arg0 -> {
            ASlider.makeASlider("Stunde", stunde, null);
            ASlider.makeASlider(d -> {
                String as;
                if (d < 10) {
                    as = "0" + new DecimalFormat("#").format(d);
                } else
                    as = new DecimalFormat("#").format(d);
                as = "Minute: " + as;
                return as;
            }, minute, null);
            ASlider.makeASlider("Anzahl", anz, new Tooltip("Anzahl der Messdiener"));
            if (sm != null) {
                ort.setText(sm.getOrt());
                typ.setText(sm.getTyp());
                wochentag.setValue(sm.getWochentag());
                stunde.setValue(sm.getBeginnStunde());
                minute.setValue(1);
                minute.setValue(sm.getBeginnMinute());
                anz.setValue(sm.getAnzMessdiener());
            } else {
                stunde.setValue(10);
                minute.setValue(1);
                minute.setValue(0);
                anz.setValue(5);
            }
        });
        Optional<ButtonType> o = a.showAndWait();
        if (o.isPresent() && o.get().equals(ButtonType.OK)) {
            String min = String.valueOf((int) minute.getValue());
            if (((int) minute.getValue()) < 10)
                min = "0" + min;
            return new StandartMesse(wochentag.getValue(), (int) stunde.getValue(), min, ort.getText(),
                    (int) anz.getValue(), typ.getText());
        }
        return null;
    }

    public String text(String string, String kurz) {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        dialog.setTitle("Eingabe");
        dialog.setHeaderText(string);
        dialog.setContentText(kurz + ":");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return "";
        }
        return result.get();
    }

    public Setting chance(Setting s) {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        dialog.setTitle("Eingabe");
        dialog.setHeaderText("Anzahl für den " + s.getJahr() + "er Jahrgang ändern");
        dialog.setContentText("neue Anzahl: ");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return s;
        }
        try {
            int anz = Integer.parseInt(result.get());
            if (anz < 0 || anz > 32) {
                Dialogs.getDialogs().warn("Bitte eine Zahl zwischen 0 und 31 eingeben.");
                return Dialogs.getDialogs().chance(s);
            }
            return new Setting(s.getAttribut(), s.getId(), anz);
        } catch (Exception e) {
            Dialogs.getDialogs().warn(result.get() + " ist keine gültige Ganzzahl");
            return s;
        }
    }

    public void show(List<?> list, String string) {
        ListView<?> lv = new ListView<>(FXCollections.observableArrayList(list));
        lv.setId("list");
        alertbuilder(AlertType.INFORMATION, string, lv).showAndWait();
    }

    public List<Messdiener> select(List<Messdiener> data, Messdiener without, List<Messdiener> freund, String s) {
        data.remove(without);
        return select(data, freund, s);
    }

    public enum YesNoCancelEnum {
        YES, NO, CANCEL
    }

    private static class ARadioButton<I> extends RadioButton {
        private final I i;

        private ARadioButton(I i) {
            super(i instanceof StandartMesse ? ((StandartMesse) i).tolangerBenutzerfreundlichenString() : i.toString());
            this.i = i;
        }

        public I getI() {
            return i;
        }
    }
}
