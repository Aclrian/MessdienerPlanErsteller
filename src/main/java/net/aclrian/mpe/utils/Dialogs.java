package net.aclrian.mpe.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.messe.StandartMesse;

public class Dialogs {

	public static void info(String string) {
		Log.getLogger().info(string);
		Alert a = new Alert(AlertType.INFORMATION);
		a.setHeaderText(string);
		a.showAndWait();
	}
	
	public static void info(String header, String string) {
		Log.getLogger().info(string);
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle(header);
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void warn(String string) {
		Log.getLogger().warning(string);
		Alert a = new Alert(AlertType.WARNING);
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void error(String string) {
		Log.getLogger().severe(string);
		Alert a = new Alert(AlertType.ERROR);
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void error(Exception e, String string) {
		Log.getLogger().severe(string);
		Log.getLogger().severe(e.getMessage());
		try {
			Log.getLogger().severe(e.getCause().toString());
		} catch (NullPointerException e1) {
			Log.getLogger().severe("no cause");
		}
		Alert a = new Alert(AlertType.ERROR);
		a.setHeaderText(string);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		VBox vb = new VBox();
		Label l = new Label("Stacktrace:");
		TextArea ta = new TextArea(sw.toString());
		ta.setEditable(false);
		vb.getChildren().addAll(l, ta);
		a.getDialogPane().setExpandableContent(vb);
		a.showAndWait();
	}

	/**Use java.awt
	 * 
	 * @param open
	 * @param string
	 * @throws IOException
	 */
	@Deprecated
	public static void open(URI open, String string) throws IOException {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setHeaderText(string);
		Optional<ButtonType> res = a.showAndWait();
		if (res.get() == ButtonType.OK) {
			Desktop.getDesktop().browse(open);
		}
	}

	public static boolean frage(String string) {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setHeaderText(string);
		Optional<ButtonType> res = a.showAndWait();
		return res.get() == ButtonType.OK;
	}

	public static boolean frage(String string, String cancel, String ok) {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setHeaderText(string);
		((Button) a.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(cancel);
		((Button) a.getDialogPane().lookupButton(ButtonType.OK)).setText(ok);
		Optional<ButtonType> res = a.showAndWait();
		return res.get() == ButtonType.OK;
	}

	public static void fatal(String string) {
		error(string);
		System.exit(-1);
	}

	public static String text(String string, String kurz) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Eingabe");
		dialog.setHeaderText(string);
		dialog.setContentText(kurz + ":");
		Optional<String> result = dialog.showAndWait();
		if (result.isEmpty()) {
			return "";
		}
		return result.get();
	}

	public static <I> ArrayList<I> select(ArrayList<I> dataAmBestenSortiert, ArrayList<I> selected, String string) {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setHeaderText(string);
		ListView<CheckBox> lw = new ListView<CheckBox>();
		ArrayList<I> rtn = new ArrayList<I>();
		for (int i = 0; i < dataAmBestenSortiert.size(); i++) {
			boolean b = false;
			I e = dataAmBestenSortiert.get(i);
			for (int j = 0; j < selected.size(); j++) {
				if (selected.get(j).toString().equalsIgnoreCase(e.toString())) {
					rtn.add(e);
					b = true;
					break;
				}
			}

			CheckBox ch = new CheckBox(e.toString());
			ch.setSelected(b);
			ch.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean old, Boolean neu) {
					if (neu && !old) {
						rtn.add(e);
					} else if (!neu && old) {
						rtn.remove(e);
					}
				}
			});
			lw.getItems().add(ch);
		}
		a.getDialogPane().setExpandableContent(lw);
		a.showAndWait();
		return rtn;
	}

	public static <I> ArrayList<I> selectS(ArrayList<I> dataAmBestenSortiert, ArrayList<String> selected,
			String string) {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setHeaderText(string);
		ListView<CheckBox> lw = new ListView<CheckBox>();
		ArrayList<I> rtn = new ArrayList<I>();
		for (int i = 0; i < dataAmBestenSortiert.size(); i++) {
			boolean b = false;
			I e = dataAmBestenSortiert.get(i);
			for (int j = 0; j < selected.size(); j++) {
				if (selected.get(j).equalsIgnoreCase(e.toString())) {
					rtn.add(e);
					b = true;
					break;
				}
			}

			CheckBox ch = new CheckBox(e.toString());
			ch.setSelected(b);
			ch.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean old, Boolean neu) {
					if (neu && !old) {
						rtn.add(e);
					} else if (!neu && old) {
						rtn.remove(e);
					}
				}
			});
			lw.getItems().add(ch);
		}
		a.getDialogPane().setExpanded(true);
		a.getDialogPane().setExpandableContent(lw);
		a.showAndWait();
		return rtn;
	}

	public static ArrayList<Date> getDates(String string, String dl, String dz) {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setHeaderText(string);
		JFXDatePicker d1 = new JFXDatePicker();
		d1.setPromptText(dl);
		JFXDatePicker d2 = new JFXDatePicker();
		d2.setPromptText(dz);
		ChangeListener<Object> e = new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> arg0, Object arg1, Object arg2) {
				if (d1.getValue() != null && d2.getValue() != null) {
					if (!Date.from(d1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
							.after(Date.from(d2.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
						a.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
						return;
					}
				}
				a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
			}
		};
		d1.valueProperty().addListener(e);
		d1.focusedProperty().addListener(e);
		d2.valueProperty().addListener(e);
		d2.focusedProperty().addListener(e);
		a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
		a.getDialogPane().setExpanded(true);
		a.getDialogPane().setExpandableContent(new HBox(d1, d2));
		Optional<ButtonType> o = a.showAndWait();
		if (o.get().equals(ButtonType.OK)) {
			ArrayList<Date> rtn = new ArrayList<Date>();
			rtn.add(0, Date.from(d1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			rtn.add(1, Date.from(d2.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			return rtn;
		}
		return null;
	}

//String wochentag, int beginn_h, String beginn_min, String ort, int anz_messdiener, String typ
	public static StandartMesse standartmesse() {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setHeaderText("Neue Standartmesse erstellen:");
		JFXTextField ort = new JFXTextField();
		ort.setPromptText("Ort:");
		JFXTextField typ = new JFXTextField();
		typ.setPromptText("Typ:");
		ComboBox<String> wochentag = new ComboBox<String>(
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
		ChangeListener<Object> e = new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> arg0, Object arg1, Object arg2) {
				try {
					if (!ort.getText().equalsIgnoreCase("") && !typ.getText().equalsIgnoreCase("")
							&& !wochentag.getValue().isBlank()) {
						a.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
						return;
					} else {
						a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
					}
				} catch (NullPointerException e2) {
					a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
				}
			}
		};
		typ.textProperty().addListener(e);
		typ.focusedProperty().addListener(e);
		ort.textProperty().addListener(e);
		ort.focusedProperty().addListener(e);
		wochentag.valueProperty().addListener(e);
		wochentag.focusedProperty().addListener(e);

		a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
		a.getDialogPane().setExpanded(true);
		VBox v = new VBox(wochentag, ort, typ, stunde, minute, anz);
		v.setSpacing(20);
		a.getDialogPane().setExpandableContent(v);
		a.setOnShown(new EventHandler<DialogEvent>() {

			@Override
			public void handle(DialogEvent arg0) {

				ASlider.makeASlider("Stunde: ", stunde);
				ASlider.makeASlider(minute, d -> {
					int i = (int) d;
					String as;
					if (i < 10) {
						as = "0" + String.valueOf(i);
					} else
						as = String.valueOf(i);
					as = "Minute: " + as;
					return as;
				});
				ASlider.makeASlider("Anzahl: ", anz).setTooltip(new Tooltip("Anzahl der Messdiener"));

				stunde.setValue(10);
				minute.setValue(1);
				minute.setValue(0);
				anz.setValue(6);
			}
		});
		Optional<ButtonType> o = a.showAndWait();
		try {
		if (o.get().equals(ButtonType.OK)) {
			String min = String.valueOf((int) minute.getValue());
			if (((int) minute.getValue()) < 10)
				min = "0" + min;
			return new StandartMesse(wochentag.getValue(), (int) stunde.getValue(), min, ort.getText(),
					(int) anz.getValue(), typ.getText());
		}
		}catch (NoSuchElementException e1) {}
		return null;
	}
}
