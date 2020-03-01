package net.aclrian.mpe;

import static net.aclrian.mpe.utils.Log.getLogger;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

public class PfarreiController {

	private ObservableList<StandartMesse> ol = FXCollections.emptyObservableList();
	private Stage stage;
	private boolean weiter = false;

	@FXML
	private TableView<StandartMesse> table = new TableView<StandartMesse>();
	@FXML
	private TableColumn<StandartMesse, String> time, ort, typ, anz;

	public PfarreiController(Stage stage) {
		this.stage = stage;
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void afterstartup(Window window) {
		if (weiter) {
		} else {
			time.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getZeit()));
			time.setStyle("-fx-alignment: CENTER;");
			ort.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrt()));
			ort.setStyle("-fx-alignment: CENTER;");
			typ.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTyp()));
			typ.setStyle("-fx-alignment: CENTER;");
			anz.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessdienerAnzahl()));
			anz.setStyle("-fx-alignment: CENTER;");

			table.setItems(ol);
		}
	}

	@FXML
	public void neu(ActionEvent e) {
		StandartMesse sm = Dialogs.standartmesse();
		Log.getLogger().info("");
		if (ol.isEmpty()) {
			ol = FXCollections.observableArrayList(sm);
			table.setItems(ol);
		} else {
			table.getItems().add(sm);
		}
		// table.autosize();
	}

	@FXML
	public void löschen(ActionEvent e) {
		StandartMesse a = table.getSelectionModel().getSelectedItem();
		ol.removeIf(sm -> {
			return sm.toString().equalsIgnoreCase(a.toString());
		});
	}

	@FXML
	public void weiter(ActionEvent e) {
		getLogger().info(((Node) e.getSource()).getParent().getScene() + "");
		weiter();
	}

	public void weiter() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/pfarrei/anzahl.fxml"));
		loader.setController(this);
		weiter = true;
		Parent root;
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("MessdienerplanErsteller");
			stage.setResizable(false);
			stage.show();
			afterstartup(stage);
			getLogger().info("neue Pfarrei erstellen...weiter");
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + loader.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}

	public void zurück() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/pfarrei/standardmesse.fxml"));
		loader.setController(this);
		weiter = false;
		Parent root;
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("MessdienerplanErsteller");
			stage.setResizable(false);
			stage.show();
			afterstartup(stage);
			getLogger().info("neue Pfarrei erstellen...zurück");
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + loader.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}

	public static void start(Stage stage) {
		FXMLLoader loader = new FXMLLoader();
		PfarreiController cont = new PfarreiController(stage);
		loader.setLocation(cont.getClass().getResource("/view/pfarrei/standardmesse.fxml"));
		loader.setController(cont);
		Parent root;
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("MessdienerplanErsteller");
			stage.setResizable(false);
			stage.show();
			cont.afterstartup(stage);
			Dialogs.info("Willkommen beim MessdienerplanErsteller!", "Als Erstes werden eine Daten benötigt:"
					+ System.lineSeparator()
					+ "Zunächst werden Standardmessen erstellt. Das sind die Messen, die sich wöchentlich wiederholen."
					+ System.lineSeparator()
					+ "Danach folgen Einstellungen, wie oft Messdiener eingeteilt werden sollen.");
			getLogger().info("neue Pfarrei erstellen...");
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + loader.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}

}
