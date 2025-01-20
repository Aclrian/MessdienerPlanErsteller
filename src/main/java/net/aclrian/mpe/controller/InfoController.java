package net.aclrian.mpe.controller; //NOPMD - suppressed ExcessiveImports - processes and adds data to the view


import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.aclrian.mpe.MainApplication;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.Objects;
import java.util.regex.*;

public class InfoController {
    private static final String KONNTE = "Konnte ";
    private static final String NICHT_OEFFNEN = " nicht öffnen.";

    private final Stage stage;

    @FXML
    private Text version;

    @FXML
    private Button folder;

    @FXML
    private Button log;

    @FXML
    private TableView<String[]> table;

    @FXML
    private TableColumn<String[], String> name;
    @FXML
    private TableColumn<String[], String> website;
    @FXML
    private TableColumn<String[], String> usage;
    @FXML
    private TableColumn<String[], String> lname;
    @FXML
    private TableColumn<String[], String> linhalt;

    @FXML
    private ListView<String> tools;

    @FXML
    private Hyperlink quellcode;
    @FXML
    private Hyperlink mpeWebsite;

    @FXML
    private TitledPane pane;

    public InfoController(Stage parent) throws IOException {
        stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/info.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        stage.setOnShown(e -> afterStartup());
        stage.setTitle("MessdienerplanErsteller - Info");
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/title_32.png"))));
        stage.setResizable(false);
    }

    public void start() {
        stage.show();
    }

    public void afterStartup() {
        try {
            ArrayList<String> content = new ArrayList<>(Arrays.asList(
                    IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/tools.txt")),
                            StandardCharsets.UTF_8).split(System.lineSeparator())));
            tools.setItems(FXCollections.observableArrayList(content));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pane.setExpanded(true);
        quellcode.setOnAction(browse(quellcode));
        mpeWebsite.setOnAction(browse(mpeWebsite));
        version.setText(MainApplication.VERSION_ID);
        folder.setOnAction(open(MPELog.getWorkingDir()));
        log.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(MPELog.getLogFile());
            } catch (IOException ex) {
                Dialogs.getDialogs().error(ex, KONNTE + MPELog.getLogFile().getAbsolutePath() + NICHT_OEFFNEN);
            }
        });
        ArrayList<String[]> entries = new ArrayList<>();
        try {
            ArrayList<String> csv = new ArrayList<>(
                    Arrays.asList(
                            IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/abhängigkeiten.csv")),
                    StandardCharsets.UTF_8)
                                    .split(System.lineSeparator())
                    )
            );
            for (String entry : csv) {
                entries.add(entry.split(Pattern.quote(",")));
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte Abhängigkeiten nicht lesen.");
        }
        name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        name.setPrefWidth(table.getWidth() / 5.0);
        website.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
        website.setPrefWidth(table.getWidth() / 2.5);
        usage.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        usage.setPrefWidth(table.getWidth() / 2.5);
        lname.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
        lname.setPrefWidth(table.getWidth() / 5.0);
        linhalt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4]));
        linhalt.setPrefWidth(table.getWidth() / 2.5);
        table.setItems(FXCollections.observableArrayList(entries));

        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                ArrayList<String> sel = new ArrayList<>();
                sel.add(table.getSelectionModel().getSelectedItem()[2]);
                sel.add(table.getSelectionModel().getSelectedItem()[4]);
                Object o = Dialogs.getDialogs().singleSelect(sel, "Wähle eine Website zum Öffnen:");
                try {
                    if (o != null) {
                        Desktop.getDesktop().browse(new URI(o.toString()));
                    }
                } catch (IOException | URISyntaxException e) {
                    Dialogs.getDialogs().error(e, "Konnte die Website nicht öffnen.");
                }

            }
        });
    }

    public EventHandler<ActionEvent> browse(Hyperlink link) {
        return event -> {
            try {
                Desktop.getDesktop().browse(new URI(link.getText()));
            } catch (IOException | URISyntaxException ex) {
                Dialogs.getDialogs().error(ex, KONNTE + MPELog.getLogFile().getAbsolutePath() + NICHT_OEFFNEN);
            }
        };
    }

    public EventHandler<ActionEvent> open(File file) {
        return event -> {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                Dialogs.getDialogs().error(ex, KONNTE + MPELog.getLogFile().getAbsolutePath() + NICHT_OEFFNEN);
            }
        };
    }
}
