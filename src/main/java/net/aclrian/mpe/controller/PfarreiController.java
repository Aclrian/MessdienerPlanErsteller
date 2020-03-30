package net.aclrian.mpe.controller;

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
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.Main;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.Setting;
import net.aclrian.mpe.pfarrei.WriteFile_Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

public class PfarreiController {

	public static void start(Stage stage, String savepath, Main main) {
		FXMLLoader loader = new FXMLLoader();
		PfarreiController cont = new PfarreiController(stage, savepath, main);
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
			cont.afterstartup();
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

	public static void start(Stage stage, Main m, Stage old) {
		FXMLLoader loader = new FXMLLoader();
		PfarreiController cont = new PfarreiController(stage,m,old);
		loader.setLocation(cont.getClass().getResource("/view/pfarrei/standardmesse.fxml"));
		loader.setController(cont);
		Parent root;
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("MessdienerplanErsteller");
			stage.setResizable(false);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			cont.afterstartup();
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

	private String nameS = null;
	private int mediI, leiterI;
	private boolean hochamtB;
	private String savepath;
	private ObservableList<StandartMesse> ol = FXCollections.emptyObservableList();
	private Stage stage;
	private boolean weiter = false;
	private Main main;
	private Stage old;

	// ---------------------------------------
	private ObservableList<Setting> settings;
	@FXML
	private TableView<StandartMesse> table = new TableView<>();
	@FXML
	private TableColumn<StandartMesse, String> time, ort, typ, anz;
	// ----------------------------------------------------------------------
	@FXML
	private TableView<Setting> t2 = new TableView<>();
	@FXML
	private TableColumn<Setting, Integer> eintritt, anzahl;
	@FXML
	private JFXTextField name;
	@FXML
	private CheckBox hochamt;
	@FXML
	private Slider leiter, medi;

	public PfarreiController(Stage stage, Main m, Stage old) {
		this.stage = stage;
		this.main = m;
		this.old = old;
		Pfarrei pf = DateienVerwalter.dv.getPfarrei();
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/title_32.png")));
		ol = FXCollections.observableArrayList(pf.getStandardMessen());
		ol.sort(StandartMesse.comfuerSMs);
		settings = FXCollections.observableArrayList(pf.getSettings().getSettings());
		nameS = pf.getName();
		mediI = pf.getSettings().getDaten(0).getAnz_dienen();
		leiterI = pf.getSettings().getDaten(1).getAnz_dienen();
		hochamtB = pf.zaehlenHochaemterMit();
	}

	public PfarreiController(Stage stage, String savepath, Main main) {
		this.savepath = savepath;
		this.stage = stage;
		this.main = main;
	}

	public void afterstartup() {
		if (weiter) {
			ASlider.makeASlider("", leiter, null);
			leiter.setMax(30);
			leiter.setMin(0);
			leiter.setValue(3);
			ASlider.makeASlider("", medi, null);
			medi.setValue(3);
			medi.setMax(30);
			medi.setMin(0);
			if (settings == null) {
				Einstellungen e = new Einstellungen();
				settings = FXCollections.observableArrayList(e.getSettings());
			}
			eintritt.setCellValueFactory(new PropertyValueFactory<>("Jahr"));
			// cellData -> {new
			// SimpleIntegerProperty(Messdaten.getMaxYear()-cellData.getValue().getId());});
			eintritt.setStyle("-fx-alignment: CENTER;");
			anzahl.setCellValueFactory(new PropertyValueFactory<>("Anz_dienen"));
			anzahl.setStyle("-fx-alignment: CENTER;");
			t2.setItems(settings);
			t2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			t2.autosize();
			t2.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
					int i = t2.getSelectionModel().getSelectedIndex();
					Setting s = settings.get(i);
					settings.set(i, Dialogs.chance(s));
				}
			});
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
			table.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
					int i = table.getSelectionModel().getSelectedIndex();
					StandartMesse standartMesse = Dialogs.standartmesse(ol.get(i));
					if(standartMesse!=null){
						ol.remove(i);
						ol.add(standartMesse);
						ol.sort(StandartMesse.comfuerSMs);
					}
				}
			});
		}
	}

	@FXML
	public void loeschen(ActionEvent e) {
		StandartMesse a = table.getSelectionModel().getSelectedItem();
		ol.removeIf(sm -> sm.toString().equals(a.toString()));
	}

	@FXML
	public void neu(ActionEvent e) {
		StandartMesse sm = Dialogs.standartmesse(null);
		Log.getLogger().info("");
		if (ol.isEmpty()) {
			ol = FXCollections.observableArrayList(sm);
			table.setItems(ol);
		} else {
			table.getItems().add(sm);
		}
	}

	@FXML
	public void save(ActionEvent e) {
		if (name.getText().equals("")) {
			Dialogs.error("Bitte gebe einen Namen ein.");
			return;
		}
		Einstellungen einst = new Einstellungen();
		einst.editMaxDienen(false, (int) medi.getValue());
		einst.editMaxDienen(true, (int) leiter.getValue());
		for (int i = 0; i < Einstellungen.lenght - 2; i++) {
			einst.editiereYear(settings.get(i).getId(), settings.get(i).getAnz_dienen());
		}
		ArrayList<StandartMesse> sm = new ArrayList<>(ol);
		Pfarrei pf = new Pfarrei(einst, sm, name.getText(), hochamt.isSelected());
		Window s = ((Button) e.getSource()).getParent().getScene().getWindow();
		if(nameS!=null)WriteFile_Pfarrei.writeFile(pf);
		WriteFile_Pfarrei.writeFile(pf, savepath);
		if (old!=null){
			try {
				DateienVerwalter.re_start(old);
				((Stage) s).close();
				return;
			} catch (DateienVerwalter.NoSuchPfarrei e1) {
				Log.getLogger().error(e1.getMessage());
			}
		}
		try {
			((Stage) s).close();
			main.main(new Stage());
		} catch (Exception ex) {
			Dialogs.info("Das Programm muss nun neu gestartet werden.");
			System.exit(0);
		}
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
			afterstartup();
			getLogger().info("neue Pfarrei erstellen...weiter");
			if(nameS!=null) {
				name.setText(nameS);
				medi.setValue(mediI);
				leiter.setValue(leiterI);
				hochamt.setSelected(hochamtB);
			}
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + loader.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}

	@FXML
	public void weiter(ActionEvent e) {
		weiter();
		try {
			Dialogs.open(new URI(
					"https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Was-wird-unter-'Anzahl'-verstanden%3F"),
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
			Log.getLogger().warn(
					"NO PANIK: https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Was-wird-unter-'Anzahl'-verstanden%3F is dead! Long live GITHUB!");
		}
	}

	@FXML
	public void zurueck() {
		if(nameS!=null){
			nameS = name.getText();
			mediI = (int)medi.getValue();
			leiterI = (int)leiter.getValue();
			hochamtB = hochamt.isSelected();
		}
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
			afterstartup();
			getLogger().info("neue Pfarrei erstellen...zurück");
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + loader.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}
}
