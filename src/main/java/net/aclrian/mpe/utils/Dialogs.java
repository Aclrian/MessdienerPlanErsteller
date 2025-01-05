package net.aclrian.mpe.utils; //NOPMD - suppressed ExcessiveImports - creates various dialogs - all in one class

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.controller.converter.ConvertController;
import net.aclrian.mpe.converter.ConvertCSV;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Setting;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.*;

public class Dialogs { //NOPMD - suppressed TooManyMethods - Utility class cannot be refactored

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

    private Alert alertBuilder(Alert.AlertType type, String headertext) {
        Alert a = new Alert(type);
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON))));
        a.setHeaderText(headertext);
        a.setTitle("MessdienerplanErsteller");

        return a;
    }

    private Alert alertBuilder(Alert.AlertType type, String header, Node n) {
        Alert a = alertBuilder(type, header);
        a.getDialogPane().setExpandableContent(n);
        a.getDialogPane().setExpanded(true);

        return a;
    }

    public void info(String string) {
        MPELog.getLogger().info(string);
        alertBuilder(Alert.AlertType.INFORMATION, string).showAndWait();
    }

    public void info(String header, String string) {
        MPELog.getLogger().info(string);
        Alert a = alertBuilder(Alert.AlertType.INFORMATION, header);
        a.setContentText(string);
        a.showAndWait();
    }

    public void warn(String string) {
        MPELog.getLogger().warn(string);
        alertBuilder(Alert.AlertType.WARNING, string).showAndWait();
    }

    public void error(String string) {
        MPELog.getLogger().error(string);
        if (Platform.isFxApplicationThread()) {
            alertBuilder(Alert.AlertType.ERROR, string).showAndWait();
        } else {
            Platform.runLater(() -> alertBuilder(Alert.AlertType.ERROR, string).showAndWait());
        }
    }

    public void error(Exception e, String string) {
        MPELog.getLogger().error(string);
        StringWriter stack = new StringWriter();
        e.printStackTrace(new PrintWriter(stack));
        MPELog.getLogger().error(stack);
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
            alertBuilder(Alert.AlertType.ERROR, header, vb).showAndWait();

        } else {
            Platform.runLater(() -> alertBuilder(Alert.AlertType.INFORMATION, string).showAndWait());
        }
    }

    public void open(URI open, String string) throws IOException {
        Alert a = alertBuilder(Alert.AlertType.CONFIRMATION, string);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            Desktop.getDesktop().browse(open);
        }
    }

    public void open(URI open, String string, String ok, String close) {
        ButtonType od = new ButtonType(ok, ButtonBar.ButtonData.OK_DONE);
        ButtonType cc = new ButtonType(close, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "", od, cc);
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON))));
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
        Optional<ButtonType> res = alertBuilder(Alert.AlertType.CONFIRMATION, string).showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }

    public boolean frage(String string, String cancel, String ok) {
        Alert a = alertBuilder(Alert.AlertType.CONFIRMATION, string);
        ((Button) a.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(cancel);
        ((Button) a.getDialogPane().lookupButton(ButtonType.OK)).setText(ok);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }

    public YesNoCancelEnum yesNoCancel(String cancel, String headerText) {
        ButtonType yesBtn = new ButtonType("Ja", ButtonBar.ButtonData.YES);
        ButtonType cancelBtn = new ButtonType(cancel, ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType noBtn = new ButtonType("Nein", ButtonBar.ButtonData.NO);
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "", yesBtn, cancelBtn, noBtn);
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON))));
        a.setHeaderText(headerText);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isEmpty() || res.get() == cancelBtn) {
            return YesNoCancelEnum.CANCEL;
        }
        return res.get() == yesBtn ? YesNoCancelEnum.YES : YesNoCancelEnum.NO;
    }

    public void fatal(Exception e, String string) {
        error(e, string);
        System.exit(-1);
    }

    public <I> Object singleSelect(List<? extends I> list, String s) {
        VBox v = new VBox();
        v.setSpacing(5d);
        ToggleGroup g = new ToggleGroup();
        for (Object value : list) {
            ARadioButton<?> b = new ARadioButton<>(value);
            b.setToggleGroup(g);
            v.getChildren().add(b);
        }
        alertBuilder(Alert.AlertType.INFORMATION, s, v).showAndWait();
        if (g.getSelectedToggle() instanceof ARadioButton) {
            return ((ARadioButton<?>) g.getSelectedToggle()).getI();
        }
        return null;
    }

    public <I> List<I> select(List<I> dataAmBestenSortiert, List<I> selected, String string) {
        Alert a = alertBuilder(Alert.AlertType.INFORMATION, string);
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
            String s = item instanceof StandardMesse sm ? sm.toLangerBenutzerfreundlichenString() : item.toString();
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


    public List<LocalDate> getDates(String string, String von, String bis) {
        DatePicker d1 = new DatePicker();
        d1.setPromptText(von);
        DatePicker d2 = new DatePicker();
        d2.setPromptText(bis);
        HBox hb = new HBox(d1, d2);
        hb.setSpacing(20d);
        Alert a = alertBuilder(Alert.AlertType.INFORMATION, string, hb);
        ChangeListener<Object> e = (arg0, arg1, arg2) -> {
            if (d1.getValue() != null && d2.getValue() != null && !d1.getValue().isAfter(d2.getValue())) {
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
            ArrayList<LocalDate> rtn = new ArrayList<>();
            rtn.add(0, d1.getValue());
            rtn.add(1, d2.getValue().plusDays(1));
            return rtn;
        }
        return Collections.emptyList();
    }

    public StandardMesse standardmesse(StandardMesse sm) {
        TextField ort = new TextField();
        ort.setPromptText("Ort:");
        TextField typ = new TextField();
        typ.setPromptText("Typ:");
        List<String> dayOfWeeks = Arrays.stream(DayOfWeek.values())
                .map(dow -> dow.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                .map(dow -> Objects.requireNonNullElse(dow, "") // fix ci error
                ).toList();
        ComboBox<String> wochentag = new ComboBox<>(
                FXCollections.observableArrayList(dayOfWeeks));
        wochentag.setPromptText("Wochentag:");
        Slider stunde = new Slider();
        stunde.setMajorTickUnit(1);
        stunde.setMinorTickCount(0);
        stunde.setSnapToTicks(true);
        stunde.setMin(0);
        stunde.setMax(24);
        stunde.setBlockIncrement(1);

        Slider minute = new Slider();
        minute.setMajorTickUnit(1);
        minute.setMinorTickCount(0);
        minute.setSnapToTicks(true);
        minute.setMin(0);
        minute.setMax(59);
        minute.setBlockIncrement(1);

        Slider anz = new Slider();
        minute.setMajorTickUnit(1);
        minute.setMinorTickCount(0);
        minute.setSnapToTicks(true);
        anz.setMin(0);
        anz.setMax(40);
        anz.setBlockIncrement(1);

        String defaultValueRepetition = "angewählten Wochentag";
        CheckBox monthlyWeek1 = new CheckBox("monatlich zum 1. " + defaultValueRepetition);
        monthlyWeek1.setPadding(new Insets(0, 0, 0, 30));
        CheckBox monthlyWeek2 = new CheckBox("monatlich zum 2. " + defaultValueRepetition);
        monthlyWeek2.setPadding(new Insets(0, 0, 0, 30));
        CheckBox monthlyWeek3 = new CheckBox("monatlich zum 3. " + defaultValueRepetition);
        monthlyWeek3.setPadding(new Insets(0, 0, 0, 30));
        CheckBox monthlyWeek4 = new CheckBox("monatlich zum 4. " + defaultValueRepetition);
        monthlyWeek4.setPadding(new Insets(0, 0, 0, 30));
        CheckBox monthlyWeek5 = new CheckBox("monatlich zum letzten " + defaultValueRepetition);
        monthlyWeek5.setPadding(new Insets(0, 0, 0, 30));
        List<CheckBox> monthlyCheckboxes = List.of(monthlyWeek1, monthlyWeek2, monthlyWeek3, monthlyWeek4, monthlyWeek5);
        VBox v = new VBox(wochentag, ort, typ, stunde, minute, anz, monthlyWeek1, monthlyWeek2, monthlyWeek3, monthlyWeek4, monthlyWeek5);
        v.setSpacing(20);
        Alert alert = alertBuilder(Alert.AlertType.INFORMATION, "Neue Standardmesse erstellen:");

        addListenersForStandardMesseDialog(ort, typ, wochentag, defaultValueRepetition, monthlyCheckboxes, alert);
        alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        alert.getDialogPane().setExpandableContent(v);
        alert.getDialogPane().setExpanded(true);
        alert.setOnShown(arg0 -> {
            ASlider.makeASlider("Stunde", stunde, null);
            ASlider.makeASlider(d -> {
                String as;
                if (d < 10) {
                    as = "0" + new DecimalFormat("#").format(d);
                } else {
                    as = new DecimalFormat("#").format(d);
                }
                as = "Minute: " + as;
                return as;
            }, minute, null);
            ASlider.makeASlider("Anzahl", anz, new Tooltip("Anzahl der Messdiener"));
            if (sm != null) {
                ort.setText(sm.getOrt());
                typ.setText(sm.getTyp());
                wochentag.setValue(sm.getWochentag().getDisplayName(TextStyle.FULL, Locale.getDefault()));
                stunde.setValue(sm.getBeginnStunde());
                minute.setValue(1);
                minute.setValue(sm.getBeginnMinute());
                anz.setValue(sm.getAnzMessdiener());
                int i = 1;
                for (CheckBox monthlyCheckbox : monthlyCheckboxes) {
                    monthlyCheckbox.setSelected(sm.getNonDefaultWeeklyRepetition().contains(i));
                    i++;
                }
            } else {
                stunde.setValue(10);
                minute.setValue(1);
                minute.setValue(0);
                anz.setValue(5);
            }
        });
        Optional<ButtonType> o = alert.showAndWait();
        if (o.isPresent() && o.get().equals(ButtonType.OK)) {
            return getStandardMesseFromDialog(ort, typ, wochentag, stunde, minute, anz, monthlyCheckboxes);
        }
        return null;
    }

    private static StandardMesse getStandardMesseFromDialog(TextField ort, TextField typ, ComboBox<String> wochentag,
                                                            Slider stunde, Slider minute, Slider anz, List<CheckBox> monthlyCheckboxes) {
        String min = String.valueOf((int) minute.getValue());
        if (((int) minute.getValue()) < 10) {
            min = "0" + min;
        }
        TemporalAccessor accessor = DateUtil.DAY_OF_WEEK_LONG.parse(wochentag.getValue());
        List<Integer> nonDefaultWeeklyRepetition = new ArrayList<>();
        int i = 1;
        for (CheckBox monthlyCheckbox : monthlyCheckboxes) {
            if (monthlyCheckbox.isSelected()) {
                nonDefaultWeeklyRepetition.add(i);
            }
            i++;
        }
        if (nonDefaultWeeklyRepetition.size() == StandardMesse.ALLOWED_REPETITION_NUMBERS.size()) {
            nonDefaultWeeklyRepetition = new ArrayList<>();
        }
        return new StandardMesse(DayOfWeek.from(accessor), (int) stunde.getValue(), min, ort.getText(),
                (int) anz.getValue(), typ.getText(), nonDefaultWeeklyRepetition);
    }

    private static void addListenersForStandardMesseDialog(TextField ort, TextField typ, ComboBox<String> wochentag,
                                                           String defaultValueRepetition, List<CheckBox> monthlyCheckboxes, Alert a) {
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
        ChangeListener<String> dayOfWeekChange = (observableValue, oldValue, newValue) -> {
            for (CheckBox monthlyCheckbox : monthlyCheckboxes) {
                if (newValue != null && !newValue.isEmpty()) {
                    monthlyCheckbox.setText(monthlyCheckbox.getText()
                            .replace(Objects.requireNonNullElse(oldValue, defaultValueRepetition), newValue));
                }
            }
        };
        wochentag.valueProperty().addListener(dayOfWeekChange);
    }

    public String text(String string, String kurz) {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON))));
        dialog.setTitle("Eingabe");
        dialog.setHeaderText(string);
        dialog.setContentText(kurz + ":");
        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }

    public static boolean remove(Messdiener m) {
        if (getDialogs().frage("Soll der Messdiener '" + m + "' wirklich gelöscht werden?", ButtonType.CANCEL.getText(),
                "Löschen")) {
            File file = m.getFile();
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                return false;
            }
            DateienVerwalter.getInstance().reloadMessdiener();
            ArrayList<Messdiener> ueberarbeitete = new ArrayList<>();
            for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
                ueberarbeitete.add(Messdiener.alteLoeschen(m, messdiener));
            }
            ueberarbeitete.removeIf(Objects::isNull);
            for (Messdiener medi : ueberarbeitete) {
                if (medi.toString().equals(m.toString())) {
                    continue;
                }
                medi.makeXML();
            }
            return true;
        }
        return false;
    }

    public void show(List<?> list, String string) {
        ListView<?> lv = new ListView<>(FXCollections.observableArrayList(list));
        lv.setId("list");
        alertBuilder(Alert.AlertType.INFORMATION, string, lv).showAndWait();
    }

    public List<Messdiener> select(List<Messdiener> data, Messdiener without, List<Messdiener> freund, String s) {
        data.remove(without);
        return select(data, freund, s);
    }

    public Setting chance(Setting s) {
        TextInputDialog dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON))));
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
                getDialogs().warn("Bitte eine Zahl zwischen 0 und 31 eingeben.");
                return getDialogs().chance(s);
            }
            return new Setting(s.attribut(), s.id(), anz);
        } catch (Exception e) {
            getDialogs().warn(result.get() + " ist keine gültige Ganzzahl");
            return s;
        }
    }

    public ConvertCSV.ConvertData importDialog(Window window) {
        File file = Speicherort.waehleDatei(window, new FileChooser.ExtensionFilter("CSV-Datei", "*.csv"), "Datei zum Importieren auswählen");
        if (file == null || !file.exists()) {
            return null;
        }
        URL u = getClass().getResource("/view/converter.fxml");
        FXMLLoader fl = new FXMLLoader(u);
        Parent p;
        ConvertController controller = new ConvertController(file);
        try {
            fl.setController(controller);
            p = fl.load();
        } catch (IOException e) {
            MPELog.getLogger().error(e.getMessage(), e);
            return null;
        }
        controller.afterStartup(null, null);

        Alert a = alertBuilder(Alert.AlertType.INFORMATION, "Spalten zuordnen", p);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) {
            return null;
        }
        if (!controller.isValid()) {
            getDialogs().error("Ein Eingabefeld ist leer oder es sind mehr Standartmessen angegeben als im System sind");
            return importDialog(window);
        }
        return controller.getData();
    }

    public enum YesNoCancelEnum {
        YES, NO, CANCEL
    }

    private static class ARadioButton<I> extends RadioButton {
        private final I i;

        private ARadioButton(I i) {
            super(i instanceof StandardMesse sm ? sm.toLangerBenutzerfreundlichenString() : i.toString());
            this.i = i;
        }

        public I getI() {
            return i;
        }
    }
}
