package net.aclrian.mpe.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.aclrian.mpe.Main;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class InfoController {
	private Stage s;

		@FXML
		private Text version;

		@FXML
		private Button folder;

		@FXML
		private Button log;

		@FXML
		private TableView<String[]> table;

		@FXML
		private TableColumn<String[], String> name, website, usage, lname, linhalt;

		@FXML
		private ListView<String> tools;

		@FXML
		private Hyperlink quellcode, MpEwebsite;

		@FXML
		private TitledPane pane;

		public InfoController(Stage parent) throws IOException {
			s = new Stage();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/AInfo.fxml"));
			loader.setController(this);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			s.setScene(scene);
			s.initModality(Modality.APPLICATION_MODAL);
			s.initOwner(parent);
			s.setOnShown(e->afterstartup());
			s.setTitle("MessdienerplanErsteller - Info");
			s.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/title_32.png")));
			s.setResizable(false);
		}

		public void start(){
			s.show();
		}

		public void afterstartup() {
			try {
				ArrayList<String> content = new ArrayList<>(Arrays.asList(IOUtils.toString(this.getClass().getResourceAsStream("/tools.txt"), StandardCharsets.UTF_8).split(System.lineSeparator())));
				tools.setItems(FXCollections.observableArrayList(content));
			} catch (IOException e) {
				e.printStackTrace();
			}
			pane.setExpanded(true);
			quellcode.setOnAction(e->{
				try {
					Desktop.getDesktop().browse(new URI(quellcode.getText()));
				} catch (IOException | URISyntaxException ex) {
					Dialogs.error(ex,"Konnte " +Log.getLogFile().getAbsolutePath() + " nicht öffnen.");
				}
			});
			MpEwebsite.setOnAction(e->{
				try {
					Desktop.getDesktop().browse(new URI(MpEwebsite.getText()));
				} catch (IOException | URISyntaxException ex) {
					Dialogs.error(ex,"Konnte " +Log.getLogFile().getAbsolutePath() + " nicht öffnen.");
				}
			});
			version.setText(Main.VersionID);
			folder.setOnAction(e->{
				try {
					Desktop.getDesktop().open(Log.getWorkingDir());
				} catch (IOException ex) {
					Dialogs.error(ex,"Konnte " +Log.getWorkingDir().getAbsolutePath() + " nicht öffnen.");
				}
			});
			log.setOnAction(e->{
				try {
					Desktop.getDesktop().open(Log.getLogFile());
				} catch (IOException ex) {
					Dialogs.error(ex,"Konnte " +Log.getLogFile().getAbsolutePath() + " nicht öffnen.");
				}
			});
			ArrayList<String[]> entries = new ArrayList<>();
			try {
				ArrayList<String> csv = new ArrayList<>(Arrays.asList(IOUtils.toString(this.getClass().getResourceAsStream("/abhängigkeiten.csv"), StandardCharsets.UTF_8).split(System.lineSeparator())));
				for (String entry:
					 csv) {
					entries.add(entry.split(Pattern.quote(",")));
				}
			} catch (IOException e) {
				Dialogs.error(e,"Konnte Abhängigkeiten nicht lesen.");
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
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
					ArrayList<String> sel = new ArrayList<>();
					sel.add(table.getSelectionModel().getSelectedItem()[2]);
					sel.add(table.getSelectionModel().getSelectedItem()[4]);
					Object o = Dialogs.singleSelect(sel, "Wähle eine Website zum Öffnen:");
					try {
						if (o!=null) Desktop.getDesktop().browse(new URI(o.toString()));
					} catch (IOException | URISyntaxException e) {
						Dialogs.error(e, "Konnte die Website nicht öffnen.");
					}

				}
			});
		}
}
