package net.aclrian.mpe.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.controller.MainController.EnumPane;
import net.aclrian.mpe.messdiener.KannWelcheMesse;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.Messdiener.NotValidException;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.RemoveDoppelte;

import static net.aclrian.mpe.utils.Log.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;

public class MediController implements Controller {
	public static final String eintritt_str = "Eintritt: ";

	private ArrayList<Messdiener> freund, geschwi;
	private Messdiener moben;
	private boolean locked = true;

	private Label lf, lg;

	private ObservableList<KannWelcheMesse> ol;
	@FXML
	private TableView<KannWelcheMesse> table;
	@FXML
	private TableColumn<KannWelcheMesse, String> stdm;
	@FXML
	private TableColumn<KannWelcheMesse, Boolean> kann;
	@FXML
	private Slider eintritt;
	@FXML
	private JFXTextField name, vorname, email;
	@FXML
	private CheckBox leiter;
	@FXML
	private ListView<Label> geschwie, freunde;
	@FXML
	private SplitMenuButton button;
	@FXML
	private MenuItem cancel, save_new;

	public MediController() {
	}

	public void initialize() {
		freund = new ArrayList<Messdiener>();
		geschwi = new ArrayList<Messdiener>();
		eintritt.setMax(Messdaten.getMaxYear());
		eintritt.setMin(Messdaten.getMinYear());
	}

	@Override
	public void afterstartup(Window window, MainController mc) {
		cancel.setOnAction(e -> {
			locked = false;
			mc.changePane(EnumPane.selectMedi);
		});
		save_new.setOnAction(e -> {
			if (getMedi()) {
				locked = false;
				mc.changePaneMesse(null);
			}
		});
		button.setOnAction(e -> {
			if (getMedi()) {
				locked = false;
				mc.changePane(EnumPane.selectMedi);
			}
		});
		// Email valid
		email.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (!newValue) {
				email.setText(EmailValidator.getInstance().isValid(email.getText()) ? email.getText() : "");
			}
		});
		// Value in Silder
		ASlider.makeASlider(eintritt_str, eintritt);
		eintritt.setValue(Messdaten.getMaxYear());
		lf = new Label("Bearbeiten");
		lf.setStyle("-fx-font-style: italic;");
		lf.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				ArrayList<Messdiener> freunde = Dialogs.select(DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList(),
						freund, "Freunde auswählen:");
				if (freunde.size() >= Messdiener.freundelenght) {
					Dialogs.error(
							"Zu viele Freunde: Bitte nur " + (Messdiener.freundelenght - 1) + " Messdiener angeben.");
					handle(arg0);
					return;
				}
				updateFreunde(freunde);
			}
		});
		freunde.getItems().add(lf);

		lg = new Label("Bearbeiten");
		lg.setStyle("-fx-font-style: italic;");
		lg.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				Object o = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList().clone();
				ArrayList<Messdiener> g = Dialogs.select(DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList(), geschwi,
						"Geschwister auswählen:");
				if (g.size() >= Messdiener.geschwilenght) {
					Dialogs.error("Zu viele Geschwister: Bitte nur " + (Messdiener.geschwilenght - 1)
							+ " Messdiener angeben.");
					handle(arg0);
					return;
				}
				updateGeschwister(g);
			}
		});
		geschwie.getItems().add(lg);

		stdm.setCellValueFactory(new PropertyValueFactory<KannWelcheMesse, String>("String"));
		kann.setCellFactory(CheckBoxTableCell.forTableColumn(kann));
		kann.setCellValueFactory(new PropertyValueFactory<>("kanndann"));
		kann.setCellValueFactory(celldata -> {
			KannWelcheMesse cellValue = celldata.getValue();
			SimpleBooleanProperty property = new SimpleBooleanProperty(cellValue.isKanndann());
			// Add listener to handler change
			property.addListener((observable, oldValue, newValue) -> cellValue.setKanndann(newValue));
			return property;
		});
		kann.setCellFactory(tc -> new CheckBoxTableCell<>());
		kann.setEditable(true);
		Messverhalten mv = new Messverhalten();
		ol = FXCollections.observableArrayList(mv.getKannWelcheMessen());
		ol.sort(KannWelcheMesse.sort);
		stdm.setReorderable(false);
		kann.setReorderable(false);
		stdm.setSortable(false);
		kann.setSortable(false);
		table.setItems(ol);
		table.setEditable(true);

		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		stdm.setMaxWidth(1f * Integer.MAX_VALUE * 85);
		kann.setMaxWidth(1f * Integer.MAX_VALUE * 15);
	}

	private void updateFreunde(ArrayList<Messdiener> freunde2) {
		freund = freunde2;
		freunde.getItems().removeIf(p -> {
			return true;
		});
		for (Messdiener messdiener : freund) {
			freunde.getItems().add(new Label(messdiener.toString()));
		}
		freunde.getItems().add(lf);
	}

	private void updateGeschwister(ArrayList<Messdiener> geschwi2) {
		geschwi = geschwi2;
		geschwie.getItems().removeIf(p -> {
			return true;
		});
		for (Messdiener messdiener : geschwi) {
			geschwie.getItems().add(new Label(messdiener.toString()));
		}
		geschwie.getItems().add(lg);
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public void setMedi(Messdiener messdiener) {
		if(messdiener == null)return;
		name.setText(messdiener.getNachnname());
		vorname.setText(messdiener.getVorname());
		email.setText(messdiener.getEmail());
		leiter.setSelected(messdiener.isIstLeiter());
		eintritt.setValue(messdiener.getEintritt());
		updateFreunde(messdiener);
		updateGeschwister(messdiener);
		Object o = messdiener.getDienverhalten().getKannWelcheMessen().clone();
		if (o instanceof ArrayList<?>) {
			for (KannWelcheMesse kwm : ol) {
				for (Object k : ((ArrayList<?>) o)) {
					if (k instanceof KannWelcheMesse) {
						KannWelcheMesse kw = (KannWelcheMesse) k;
						if (kwm.getMesse().toString().equals(kw.getMesse().toString())
								&& (kwm.isKanndann() != kw.isKanndann())) {
							kwm.setKanndann(!kwm.isKanndann());
						}
					}
				}
			}
		}
		moben = messdiener;
		// ol.addAll(kwm);
		// ol.sort(KannWelcheMesse.sort);
		getLogger().info("Messdiener wurde geladen");
	}

	public boolean getMedi() {
		try {
			if (!name.getText().equals("") && !vorname.getText().equals("")) {
				// ME
				Messdiener m = new Messdiener(null);
				if (moben != null) {
					m.setFile(moben.getFile());
				}
				m.setzeAllesNeu(vorname.getText(), name.getText(), (int) eintritt.getValue(), leiter.isSelected(),
						Messverhalten.convert(ol), email.getText());
				if (moben != null) {
					boolean b = moben.getNachnname().equals(m.getNachnname());
					boolean bo = moben.getVorname().equals(m.getVorname());
					if (!b || !bo) {
						m.getFile().delete();
					}
				}
				m.setFreunde(getArrayString(freund, Messdiener.freundelenght));
				m.setGeschwister(getArrayString(geschwi, Messdiener.geschwilenght));
				ArrayList<Messdiener> bearbeitete = new ArrayList<Messdiener>();
				for (Messdiener messdiener : DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList()) {
					bearbeitete.addAll(alteloeschen(m, messdiener.getFreunde()));
					bearbeitete.addAll(alteloeschen(m, messdiener.getGeschwister()));
					if (moben != null) {
						bearbeitete.addAll(alteloeschen(moben, messdiener.getFreunde()));
						bearbeitete.addAll(alteloeschen(moben, messdiener.getGeschwister()));
					}
				}
				for (int i = 0; i < geschwi.size(); i++) {
					Messdiener medi = geschwi.get(i);
					addBekanntschaft(medi, m, true);
					bearbeitete.add(medi);
				}
				for (int i = 0; i < freund.size(); i++) {
					Messdiener medi = freund.get(i);
					addBekanntschaft(medi, m, false);
				}
				// speichern
				RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<Messdiener>();
				rd.removeDuplicatedEntries(bearbeitete);
				try {
					for (Messdiener messdiener : bearbeitete) {
						WriteFile wf = new WriteFile(messdiener);
						wf.toXML();
					}
					WriteFile wf = new WriteFile(m);
					wf.toXML();
					getLogger().info("Messdiener " + m.makeId() + " wurde gespeichert!");
				} catch (IOException e) {
					Dialogs.error(e, "Konnte den Messdiener '" + m + "' nicht speichern");
				}
				return true;
			} else {
				Dialogs.warn("Bitte einen Namen eintragen!");
			}
		} catch (NotValidException e) {
			Dialogs.warn("Bitte eine richtige E-Mail oder nichts angeben!");
		}
		return false;
	}

	private void addBekanntschaft(Messdiener medi, Messdiener woben, boolean isGeschwister) {
		if (!isGeschwister) {// Freunde
			// fuer medi
			ArrayList<String> leins = new ArrayList<>(Arrays.asList(medi.getFreunde()));
			leins.add(woben.toString());
			leins.removeIf(t -> t.equals(""));
			String[] s = new String[5];
			leins.toArray(s);
			for (int i = 0; i < s.length; i++) {
				if (s[i] == null) {
					s[i] = "";
				}

			}
			medi.setFreunde(s);
		} else {
			ArrayList<String> leins = new ArrayList<String>((List<String>) Arrays.asList(medi.getGeschwister()));
			leins.add(woben.toString());
			leins.removeIf(t -> t.equals(""));
			String[] s = new String[5];
			leins.toArray(s);
			for (int i = 0; i < s.length; i++) {
				if (s[i] == null) {
					s[i] = "";
				}

			}
			medi.setGeschwister(s);
		}
	}

	public static String[] getArrayString(ArrayList<?> freunde2, int begr) {
		String[] s = new String[begr];
		for (int i = 0; i < s.length; i++) {
			try {
				s[i] = freunde2.get(i).toString();
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				s[i] = "";
			}
		}
		return s;
	}

	private static void alteloeschen(Messdiener m, String[] array, ArrayList<Messdiener> ueberarbeitete) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(m.toString())) {
				array[i] = "";
				ueberarbeitete.add(m);
			}
		}
	}

	public static boolean remove(Window window, Messdiener m) {
		if (Dialogs.frage("Soll der Messdiener '" + m + "' wirklich gelöscht werden?", ButtonType.CANCEL.getText(),
				"Löschen")) {
			// moben.getFile().
			File file = m.getFile();
			file.delete();
			ArrayList<Messdiener> ueberarbeitete = new ArrayList<>();
			for (Messdiener messdiener : DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList()) {
				alteloeschen(m, messdiener.getFreunde(), ueberarbeitete);
				alteloeschen(m, messdiener.getGeschwister(), ueberarbeitete);
			}
			for (Messdiener medi : ueberarbeitete) {
				if (medi.toString().equals(m.toString())) {
					continue;
				}
				WriteFile wf = new WriteFile(medi);
				try {
					wf.toXML();
					return true;
				} catch (IOException e) {
					Dialogs.error(e, "Konnte bei dem Bekannten '" + medi
							+ "' von dem zulöschenden Messdiener diesen nicht löschen.");
					return false;
				}
			}
		}
		return false;
	}

	private static ArrayList<Messdiener> alteloeschen(Messdiener m, String[] array) {
		ArrayList<Messdiener> ueberarbeitete = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(m.toString())) {
				array[i] = "";
				ueberarbeitete.add(m);
			}
		}
		return ueberarbeitete;
	}

	private void updateFreunde(Messdiener medi) {
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(medi.getFreunde()));
		ArrayList<Messdiener> alle = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		ArrayList<Messdiener> al = new ArrayList<Messdiener>();
		loop: for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals("")) {
				continue;
			}
			for (int j = 0; j < alle.size(); j++) {
				if (arrayList.get(i).equals(alle.get(j).toString())) {
					al.add(alle.get(j));
					continue loop;
				}
			}
			boolean beheben = Dialogs.frage(
					"Konnte den Messdiener '" + arrayList.get(i) + "' als Freund von '" + medi + "' nicht finden!",
					"ignorieren", "beheben");
			if (beheben) {
				arrayList.remove(i);
				String[] freunde = arrayList.toArray(new String[arrayList.size()]);
				medi.setFreunde(freunde);
				updateFreunde(medi);
				return;
			}
		}
		updateFreunde(al);
	}

	private void updateGeschwister(Messdiener medi) {
		ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(medi.getGeschwister()));
		ArrayList<Messdiener> alle = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		ArrayList<Messdiener> al = new ArrayList<>();
		loop: for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals("")) {
				continue;
			}
			for (int j = 0; j < alle.size(); j++) {
				if (arrayList.get(i).equals(alle.get(j).toString())) {
					al.add(alle.get(j));
					continue loop;
				}
			}
			boolean beheben = Dialogs.frage(
					"Konnte den Messdiener '" + arrayList.get(i) + "' als Geschwister von '" + medi + "' nicht finden!",
					"ignorieren", "beheben");
			if (beheben) {
				arrayList.remove(i);
				String[] gew = arrayList.toArray(new String[arrayList.size()]);
				medi.setGeschwister(gew);
				updateGeschwister(medi);
				return;
			}
		}
		updateGeschwister(al);
	}
}
