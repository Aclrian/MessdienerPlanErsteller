package net.aclrian.mpe;

import static net.aclrian.mpe.utils.Log.getLogger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.jfoenix.controls.JFXTextField;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.Setting;
import net.aclrian.mpe.pfarrei.WriteFile_Pfarrei;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

public class PfarreiController{
	private final String savepath;
	private ObservableList<StandartMesse> ol = FXCollections.emptyObservableList();
	private Stage stage;
	private boolean weiter = false;
	private Runnable main;
	// ---------------------------------------
	private ObservableList<Setting> settings;

	@FXML
	private TableView<StandartMesse> table = new TableView<StandartMesse>();
	@FXML
	private TableColumn<StandartMesse, String> time, ort, typ, anz;
	// ----------------------------------------------------------------------
	@FXML
	private TableView<Setting> t2 = new TableView<Setting>();
	@FXML
	private TableColumn<Setting, Integer> eintritt, anzahl;
	@FXML
	private JFXTextField name;
	@FXML
	private CheckBox hochamt;
	@FXML
	private Slider leiter, medi;

	public PfarreiController(Stage stage, String savepath, Runnable main) {
		this.savepath = savepath;
		this.stage = stage;
		this.main = main;
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void afterstartup(Window window) {
		if (weiter) {
			ASlider.makeASlider("", leiter);
			leiter.setMax(30);
			leiter.setMin(0);
			leiter.setValue(3);
			ASlider.makeASlider("", medi);
			medi.setValue(3);
			medi.setMax(30);
			medi.setMin(0);
			if (settings == null) {
				Einstellungen e = new Einstellungen();
				settings = FXCollections.observableArrayList(e.getSettings());
			}
			eintritt.setCellValueFactory(new PropertyValueFactory<Setting, Integer>("Jahr"));
			// cellData -> {new
			// SimpleIntegerProperty(Messdaten.getMaxYear()-cellData.getValue().getId());});
			eintritt.setStyle("-fx-alignment: CENTER;");
			anzahl.setCellValueFactory(new PropertyValueFactory<Setting, Integer>("Anz_dienen"));
			anzahl.setStyle("-fx-alignment: CENTER;");
			t2.setItems(settings);
			t2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			t2.autosize();
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
		weiter();
		try {
			Dialogs.open(new URI(
					"https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Was-wird-unter-'Anzahl'-verstanden%3F"),
					"Pfarrei erstellen",
					"Nun geht es um die Anzahl, wie oft Messdiener eingeteilt werden sollen:" + System.lineSeparator()
							+ "Hier gibt es die maximale Anzahl, bei der zwischen Leitern und normalen Messdienern unterschieden werden kann."
							+ System.lineSeparator()
							+ "Diese beschreibt die Anzahl an Messen, die Messdiener nicht mehr dienen kann, weil dieser schon an genug Messen gedient hat."
							+ System.lineSeparator()
							+ "Die 'Anzahl nach Eintrittsjahr' gibt, an wie häufig Messdiener des jeweiligen Jahrgangs generell eingeteilt werden."
							+ System.lineSeparator()
							+ "Dabei ist zu beachten, dass jedes Jahr ein Messdiener in die nächst-höhere Gruppe kommt.",
					"Mehr Informationen", "Verstanden");
		} catch (IOException | URISyntaxException e1) {
			Dialogs.info("Pfarrei erstellen", "Nun geht es um die Anzahl, wie oft Messdiener eingeteilt werden sollen."
					+ System.lineSeparator()
					+ "Hier gibt es die maximale Anzahl, bei der zwischen Leitern und normalen Messdienern unterschieden werden kann."
					+ System.lineSeparator()
					+ "Diese beschreibt die Anzahl an Messen, die Messdiener nicht mehr dienen kann, weil dieser schon an genug Messen gedient hat."
					+ System.lineSeparator()
					+ "Die 'Anzahl nach Eintrittsjahr' gibt, an wie häufig Messdiener des jeweiligen Jahrgangs generell eingeteilt werden."
					+ System.lineSeparator()
					+ "Dabei ist zu beachten, dass jedes Jahr ein Messdiener in die nächst-höhere Gruppe kommt.");
			Log.getLogger().warning(
					"NO PANIK: https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Was-wird-unter-'Anzahl'-verstanden%3F is dead! Long live GITHUB!");
		}
	}
	
	@FXML
	public void save(ActionEvent e) {
		if(name.getText().equalsIgnoreCase("")) {
			Dialogs.error("Bitte gebe einen Namen ein.");
			return;
		}
		Einstellungen einst = new Einstellungen();
		einst.editMaxDienen(false, (int)medi.getValue());
		einst.editMaxDienen(true, (int)leiter.getValue());
		for (int i = 0; i < Einstellungen.lenght-2; i++) {
			einst.editiereYear(settings.get(i).getId(), settings.get(i).getAnz_dienen());
		}
		ArrayList<StandartMesse> sm = new ArrayList<StandartMesse>(ol);
		Pfarrei pf = new Pfarrei(einst, sm, name.getText(),hochamt.isSelected());
		WriteFile_Pfarrei.writeFile(pf, ((Button)e.getSource()).getParent().getScene().getWindow(), savepath);
		Dialogs.info("Das Programm muss nun neu gestartet werden.");
		System.exit(0);
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

	public static void start(Stage stage, String savepath, Runnable r) {
		FXMLLoader loader = new FXMLLoader();
		PfarreiController cont = new PfarreiController(stage,savepath,r);
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
