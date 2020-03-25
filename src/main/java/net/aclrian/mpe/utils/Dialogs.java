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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Setting;

public class Dialogs {

	public static class Icon {
	}

	private static Icon i = new Icon();

	public static void info(String string) {
		Log.getLogger().info(string);
		Alert a = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void info(String header, String string) {
		Log.getLogger().info(string);
		Alert a = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setTitle(header);
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void warn(String string) {
		Log.getLogger().warn(string);
		Alert a = new Alert(AlertType.WARNING);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void error(String string) {
		Log.getLogger().error(string);
		Alert a = new Alert(AlertType.ERROR);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void error(Exception e, String string) {
		Log.getLogger().error(string);
		Log.getLogger().error(e.getMessage());
		try {
			Log.getLogger().error(e.getCause().toString());
		} catch (NullPointerException e1) {}
		Alert a = new Alert(AlertType.ERROR);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		if(e.getLocalizedMessage()!= null && !e.getLocalizedMessage().equals("")) {
			a.setHeaderText(string+"\n"+e.getLocalizedMessage());
		} else if(e.getMessage()!= null && !e.getMessage().equals("")){
			a.setHeaderText(string+"\n"+e.getMessage());
		} else {
			a.setHeaderText(string);
		}
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

	/**
	 * 
	 * @param open
	 * @param string
	 * @throws IOException
	 */
	public static void open(URI open, String string) throws IOException {
		Alert a = new Alert(AlertType.CONFIRMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		Optional<ButtonType> res = a.showAndWait();
		if (res.get() == ButtonType.OK) {
			Desktop.getDesktop().browse(open);
		}
	}

	public static void open(URI open, String titel, String string, String ok, String close) throws IOException {
		ButtonType od = new ButtonType(ok, ButtonBar.ButtonData.OK_DONE);
		ButtonType cc = new ButtonType(close, ButtonBar.ButtonData.CANCEL_CLOSE);
		Alert a = new Alert(AlertType.CONFIRMATION, "", od, cc);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		a.setTitle(titel);
		Optional<ButtonType> res = a.showAndWait();
		if (res.get() == od) {
			Desktop.getDesktop().browse(open);
		}
	}

	public static boolean frage(String string) {
		Alert a = new Alert(AlertType.CONFIRMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		Optional<ButtonType> res = a.showAndWait();
		return res.get() == ButtonType.OK;
	}

	public static boolean frage(String string, String cancel, String ok) {
		Alert a = new Alert(AlertType.CONFIRMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		((Button) a.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(cancel);
		((Button) a.getDialogPane().lookupButton(ButtonType.OK)).setText(ok);
		Optional<ButtonType> res = a.showAndWait();
		return res.get() == ButtonType.OK;
	}

	public static Boolean yesNoCancel(String yes, String no, String cancel, String string){
		ButtonType od = new ButtonType(yes, ButtonBar.ButtonData.YES);
		ButtonType cc = new ButtonType(cancel, ButtonBar.ButtonData.CANCEL_CLOSE);
		ButtonType c = new ButtonType(no, ButtonBar.ButtonData.NO);
		Alert a = new Alert(AlertType.CONFIRMATION, "", od, cc, c);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		//((Button) a.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(cancel);
		//((Button) a.getDialogPane().lookupButton(ButtonType.OK)).setText(ok);
		Optional<ButtonType> res = a.showAndWait();
		if(res.get()==cc) return null;
		return res.get() == od;
	}

	public static void fatal(String string) {
		error(string);
		System.exit(-1);
	}

	public static <I> Object singleSelect(ArrayList<? extends I> list, String s) {
		Alert a = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(s);
		VBox v = new VBox();
		v.setSpacing(5d);
		ToggleGroup g = new ToggleGroup();
		for (Object value : list) {
			ARadioButton<?> b = new ARadioButton<>(value);
			b.setToggleGroup(g);
			v.getChildren().add(b);
		}
		a.getDialogPane().setExpandableContent(v);
		a.getDialogPane().setExpanded(true);
		a.showAndWait();
		if (g.getSelectedToggle() instanceof ARadioButton){
			Object o = ((ARadioButton<?>) g.getSelectedToggle()).getI();
			return o;
		}
		return null;
	}

	public static <I> ArrayList<I> select(ArrayList<I> dataAmBestenSortiert, ArrayList<I> selected, String string) {
		Alert a = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		ListView<CheckBox> lw = new ListView<CheckBox>();
		ArrayList<I> rtn = new ArrayList<I>();
		for (int i = 0; i < dataAmBestenSortiert.size(); i++) {
			boolean b = false;
			I e = dataAmBestenSortiert.get(i);
			for (int j = 0; j < selected.size(); j++) {
				if (selected.get(j).toString().equals(e.toString())) {
					rtn.add(e);
					b = true;
					break;
				}
			}
			String s = e instanceof StandartMesse ? ((StandartMesse) e).tolangerBenutzerfreundlichenString() :  e.toString();
			CheckBox ch = new CheckBox(s);
			ch.setSelected(b);
			ch.selectedProperty().addListener((arg0, old, neu) -> {
				if (neu && !old) {
					rtn.add(e);
				} else if (!neu && old) {
					rtn.remove(e);
				}
			});
			lw.getItems().add(ch);
		}
		a.getDialogPane().setExpandableContent(lw);
		a.getDialogPane().setExpanded(true);
		a.showAndWait();
		return rtn;
	}

	public static <I> ArrayList<I> selectS(ArrayList<I> dataAmBestenSortiert, ArrayList<String> selected,
			String string) {
		Alert a = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		ListView<CheckBox> lw = new ListView<CheckBox>();
		ArrayList<I> rtn = new ArrayList<I>();
		for (int i = 0; i < dataAmBestenSortiert.size(); i++) {
			boolean b = false;
			I e = dataAmBestenSortiert.get(i);
			for (int j = 0; j < selected.size(); j++) {
				if (selected.get(j).equals(e.toString())) {
					rtn.add(e);
					b = true;
					break;
				}
			}
			String s = e instanceof StandartMesse ? ((StandartMesse) e).tolangerBenutzerfreundlichenString() :  e.toString();
			CheckBox ch = new CheckBox(s);
			ch.setSelected(b);
			ch.selectedProperty().addListener((arg0, old, neu) -> {
				if (neu && !old) {
					rtn.add(e);
				} else if (!neu && old) {
					rtn.remove(e);
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
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
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
		HBox hb = new HBox(d1, d2);
		hb.setSpacing(20d);
		a.getDialogPane().setExpandableContent(hb);
		a.getDialogPane().setExpanded(true);
		Optional<ButtonType> o = a.showAndWait();
		try {
			if (o.get().equals(ButtonType.OK)) {
				ArrayList<Date> rtn = new ArrayList<Date>();
				rtn.add(0, Date.from(d1.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				rtn.add(1, Date.from(d2.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				return rtn;
			}
		}catch (NoSuchElementException e1){}
		return null;
	}

	public static StandartMesse standartmesse(StandartMesse sm) {
		Alert a = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
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
		ChangeListener<Object> e = (arg0, arg1, arg2) -> {
			try {
				if (!ort.getText().equals("") && !typ.getText().equals("")
						&& !wochentag.getValue().isBlank()) {
					a.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
				} else {
					a.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
				}
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
		a.getDialogPane().setExpanded(true);
		VBox v = new VBox(wochentag, ort, typ, stunde, minute, anz);
		v.setSpacing(20);
		a.getDialogPane().setExpandableContent(v);
		a.getDialogPane().setExpanded(true);
		a.setOnShown(arg0 -> {

			ASlider.makeASlider("Stunde: ", stunde);
			ASlider.makeASlider(minute, d -> {
				int i = (int) d;
				String as;
				if (i < 10) {
					as = "0" + i;
				} else
					as = String.valueOf(i);
				as = "Minute: " + as;
				return as;
			});
			ASlider.makeASlider("Anzahl: ", anz).setTooltip(new Tooltip("Anzahl der Messdiener"));
			if(sm!=null){
				ort.setText(sm.getOrt());
				typ.setText(sm.getTyp());
				wochentag.setValue(sm.getWochentag());
				stunde.setValue(sm.getBeginn_stunde());
				minute.setValue(1);
				minute.setValue(sm.getBeginn_minuteInt());
				anz.setValue(sm.getAnz_messdiener());
			} else {
				stunde.setValue(10);
				minute.setValue(1);
				minute.setValue(0);
				anz.setValue(5);
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
		} catch (NoSuchElementException e1) {
		}
		return null;
	}

	public static String text(String string, String kurz) {
		TextInputDialog dialog = new TextInputDialog();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		dialog.setTitle("Eingabe");
		dialog.setHeaderText(string);
		dialog.setContentText(kurz + ":");
		Optional<String> result = dialog.showAndWait();
		if (result.isEmpty()) {
			return "";
		}
		return result.get();
	}

	public static Setting chance(Setting s) {
		TextInputDialog dialog = new TextInputDialog();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
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
				Dialogs.warn("Bitte eine Zahl zwischen 0 und 31 eingeben.");
				return Dialogs.chance(s);
			}
			return new Setting(s.getA(), s.getId(), anz);
		} catch (Exception e) {
			Dialogs.warn(result.get()+" ist keine gültige Ganzzahl");
			return s;
		}
	}

	private static class ARadioButton<I> extends RadioButton {
		private I i;
		public ARadioButton(I i) {
			super(i instanceof StandartMesse ? ((StandartMesse) i).tolangerBenutzerfreundlichenString() : i.toString());
			this.i = i;
		}

		public I getI() {
			return i;
		}
	}

	public static void show(ArrayList<?> list, String string) {
		Alert a = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(i.getClass().getResourceAsStream("/images/title_32.png")));
		a.setHeaderText(string);
		ListView<?> lv = new ListView<>(FXCollections.observableArrayList(list));
		a.getDialogPane().setExpandableContent(lv);
		a.getDialogPane().setExpanded(true);
		a.showAndWait();
	}
}
