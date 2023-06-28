package net.aclrian.mpe.controller.converter;

import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;
import net.aclrian.mpe.controller.*;
import net.aclrian.mpe.converter.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.utils.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class ConvertController implements Controller {

    private final File file;
    @FXML
    private ListView<ConvertCSV.Sortierung> reihenfolge;
    @FXML
    private ListView<StandardMesse> smReihenfolge;
    @FXML
    private TextField delimiter;
    @FXML
    private TextField subdelimiter;
    @FXML
    private Button rfup;
    @FXML
    private Button rfdown;
    @FXML
    private Button rfrm;
    @FXML
    private Button rfreset;
    @FXML
    private Button rfadd;
    @FXML
    private Button rfadd1;
    @FXML
    private Button rfadd2;

    @FXML
    private Button smup;
    @FXML
    private Button smdown;
    @FXML
    private Button smrm;
    @FXML
    private Button smreset;
    @FXML
    private ChoiceBox<Charset> charset;
    @FXML
    private Accordion accordion;
    @FXML
    private CheckBox gegenseitgEintragen;
    @FXML
    private CheckBox chbox1statt0;

    public ConvertController(File file) {
        this.file = file;
    }

    @Override
    public void initialize() {
        smReihenfolge.setItems(FXCollections.observableList(DateienVerwalter.getInstance().getPfarrei().getStandardMessen().stream().filter(standardMesse -> !(standardMesse instanceof Sonstiges)).toList()));
        reihenfolge.setItems(FXCollections.observableList(Arrays.asList(ConvertCSV.Sortierung.values())));
        charset.setItems(FXCollections.observableArrayList(StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16LE, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE));
        charset.getSelectionModel().select(StandardCharsets.UTF_8);
        smReihenfolge.setCellFactory(sm -> {
            TextFieldListCell<StandardMesse> cell = new TextFieldListCell<>();
            cell.setConverter(new StandardMesseConverter());
            return cell;
        });
        reihenfolge.setCellFactory(sm -> {
            TextFieldListCell<ConvertCSV.Sortierung> cell = new TextFieldListCell<>();
            cell.setConverter(new SortierungConverter());
            return cell;
        });
        smReihenfolge.setEditable(false);
        reihenfolge.setEditable(false);
        accordion.setExpandedPane(accordion.getPanes().get(0));
    }

    public boolean isValid() {
        return !(delimiter.getText().isEmpty() || subdelimiter.getText().isEmpty() || reihenfolge.getItems().isEmpty() || smReihenfolge.getItems().isEmpty() ||
                charset.getValue() == null ||
                reihenfolge.getItems().stream().filter(sortierung -> sortierung == ConvertCSV.Sortierung.NICHT_STANDARD_MESSE ||
                sortierung == ConvertCSV.Sortierung.STANDARD_MESSE).count() > (DateienVerwalter.getInstance().getPfarrei().getStandardMessen().size() -1));
    }

    @Override
    public void afterStartup(Window window, MainController mc) {
        smreset.setOnAction(a -> smReihenfolge.setItems(FXCollections.observableList(DateienVerwalter.getInstance().getPfarrei().getStandardMessen())));
        rfreset.setOnAction(a -> reihenfolge.setItems(FXCollections.observableList(Arrays.asList(ConvertCSV.Sortierung.values()))));
        rfadd.setOnAction(a -> {
            final List<ConvertCSV.Sortierung> items = new ArrayList<>(reihenfolge.getItems().stream().toList());
            items.add(ConvertCSV.Sortierung.IGNORIEREN);
            reihenfolge.setItems(FXCollections.observableList(items));
        });
        rfadd1.setOnAction(a -> {
            final List<ConvertCSV.Sortierung> items = new ArrayList<>(reihenfolge.getItems().stream().toList());
            items.add(ConvertCSV.Sortierung.STANDARD_MESSE);
            reihenfolge.setItems(FXCollections.observableList(items));
        });
        rfadd2.setOnAction(a -> {
            final List<ConvertCSV.Sortierung> items = new ArrayList<>(reihenfolge.getItems().stream().toList());
            items.add(ConvertCSV.Sortierung.NICHT_STANDARD_MESSE);
            reihenfolge.setItems(FXCollections.observableList(items));
        });
        rfup.setOnAction(a -> {
            int i = reihenfolge.getSelectionModel().getSelectedIndex();
            if (i <= 0) return;
            Collections.swap(reihenfolge.getItems(), i - 1, i);
            reihenfolge.getSelectionModel().select(i - 1);
        });
        rfdown.setOnAction(a -> {
            int i = reihenfolge.getSelectionModel().getSelectedIndex();
            if (i < 0 || i >= reihenfolge.getItems().size()-1) return;
            Collections.swap(reihenfolge.getItems(), i, i + 1);
            reihenfolge.getSelectionModel().select(i + 1);
        });
        rfrm.setOnAction(a -> {
            int i = reihenfolge.getSelectionModel().getSelectedIndex();
            if (i < 0) return;
            final List<ConvertCSV.Sortierung> items = new ArrayList<>(reihenfolge.getItems());
            items.remove(i);
            reihenfolge.setItems(FXCollections.observableList(items));
        });
        smdown.setOnAction(a -> {
            int i = smReihenfolge.getSelectionModel().getSelectedIndex();
            if (i<0 || i >= smReihenfolge.getItems().size()-1) return;
            final List<StandardMesse> list = new ArrayList<>(smReihenfolge.getItems().stream().toList());
            Collections.swap(list, i, i + 1);
            smReihenfolge.setItems(FXCollections.observableList(list));
            smReihenfolge.getSelectionModel().select(i + 1);
        });
        smup.setOnAction(a -> {
            int i = smReihenfolge.getSelectionModel().getSelectedIndex();
            if (i <= 0) return;
            final List<StandardMesse> list = new ArrayList<>(smReihenfolge.getItems().stream().toList());
            Collections.swap(list, i - 1, i);
            smReihenfolge.setItems(FXCollections.observableList(list));
            smReihenfolge.getSelectionModel().select(i - 1);
        });
        smrm.setOnAction(a -> {
            int i = smReihenfolge.getSelectionModel().getSelectedIndex();
            if (i < 0) return;
            final List<StandardMesse> items = new ArrayList<>(smReihenfolge.getItems());
            items.remove(i);
            smReihenfolge.setItems(FXCollections.observableList(items));
        });
    }

    @FXML
    public void help(ActionEvent e) {
        Dialogs.getDialogs().info("Informationen zum Importieren", """
                Freunde und Geschwister: Bei diesem Eintrag wird eine Liste erwartet, die mit dem Trennzeichen für Listen die einzelnen Einträge aufspaltet.
                Ein jeder Eintrag steht für einen Messdiener.
                Dieser wird im System gesucht und mittels genauer Übereinstimmung nach dem Muster "Nachname, Vorname" gefunden.
                Falls nicht wird der Messdiener gesucht, der alle Einzelteile enthält.
                So wird "Heinz-Karl Rüdiger" mit dem Messdiener der Datei "Rüdiger, Karl-Heinz.xml" zugeordnet.
                Es ist auch möglich, die Listen mit den Zeilennummern der entsprechenden Messdienern zu füllen.
                Enthält eine Liste also "0;1;2" so wird die Liste die Messdiener enthalten, die in den ersten drei Zeilen stehen.
                Wenn die Zeilen mit 1 bei der Nummerierung starten kann das Häckchen bei der entsprechenden Option gesetzt werden, sodass die ersten drei Zeilen mit "1;2;3" aufgeführt werden können.
                                
                Standardmesse: Die Reihenfolge der Standardmesse Einträge im Reihenfolge-Abschnitt entsprechen der Reihenfolge der spezifischen Standardmesse im Standardmesse-Abschnitt
                und dient zur Identifizierung, welche Standartmesse zu welcher Spalte gehört.
                                
                Ein "(falls leer)" am Ende eines Eintrags bedeutet, dass der Messdiener ein Leiter ist oder zu dieser Standardmesse kann, wenn die entsprechende Zelle leer ist.
                Beim anderen Fall ist der entsprechende Eintrag wahr, wenn dort etwas steht, wie beispielsweise "x".
                """);
    }

    public ConvertCSV.ConvertData getData() {
        return new ConvertCSV.ConvertData(file, new ArrayList<>(reihenfolge.getItems()), new ArrayList<>(smReihenfolge.getItems()), delimiter.getText(), subdelimiter.getText(), charset.getValue(), gegenseitgEintragen.isSelected(), chbox1statt0.isSelected());
    }

    @Override
    public boolean isLocked() {
        return true;
    }

    private static class StandardMesseConverter extends StringConverter<StandardMesse> {

        @Override
        public String toString(StandardMesse standardMesse) {
            return standardMesse.toBenutzerfreundlichenString();
        }

        @Override
        public StandardMesse fromString(String s) {
            throw new UnsupportedOperationException("Cannot get Standardmesse from String");
        }
    }

    private static class SortierungConverter extends StringConverter<ConvertCSV.Sortierung> {

        @Override
        public String toString(ConvertCSV.Sortierung sortierung) {
            return sortierung.getName();
        }

        @Override
        public ConvertCSV.Sortierung fromString(String s) {
            throw new UnsupportedOperationException("Cannot get Sortierung from String");
        }
    }
}
