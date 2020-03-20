package net.aclrian.mpe.controller;

import static net.aclrian.mpe.utils.Log.getLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

public class Select implements Controller {
	public enum Selecter {
		Messdiener, Messe;
	}
	@FXML
	private GridPane gpane;
	@FXML
	private FlowPane pane;
	@FXML
	private Label text;
	@FXML
	private ListView<Label> list;
	@FXML
	private Button neu, bearbeiten, remove;
	private Parent p;
	private Selecter sel;
	private MainController mc;

	public Select(Parent p, Selecter sel, MainController mc) {
		this.p = p;
		this.sel = sel;
		this.mc = mc;
	}

	@Override
	public void initialize() {
		// p.getChildrenUnmodifiable().add(list);
		list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	@Override
	public void afterstartup(Window window, MainController mc) {
		switch (sel) {
		case Messdiener:
			text.setText("Messdiener anzeigen & bearbeiten");
			list.getItems().removeIf(t -> true);
			ArrayList<Messdiener> data = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
			data.sort(Messdiener.compForMedis);
			for (int i = 0; i < data.size(); i++) {
				list.getItems().add(new Label(data.get(i).toString()));
			}
			list.setOnMouseClicked(mouseEvent -> {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						int i = list.getSelectionModel().getSelectedIndex();
						mc.changePaneMessdiener(data.get(i));
					}
				}
			});
				remove.setOnAction(arg0 -> {
				int i = list.getSelectionModel().getSelectedIndex();
				if ((list.getSelectionModel().getSelectedItem().getText().equalsIgnoreCase(data.get(i).toString()))
						&& MediController.remove(window, data.get(i))) {
					afterstartup(window,mc);
				}
			});
			neu.setOnAction(e -> neu());
			break;

		case Messe:
			ArrayList<Messe> datam = mc.getMessen();
			Button generieren = new Button();
			generieren.setText("Messen generieren");
			generieren.setOnAction(arg0 -> {
				ArrayList<Date> daten = Dialogs.getDates("Für welchen Zeitraum sollen Messen generiert werden?",
						"Von:", "Bis:");
				if (daten != null) {
					try {
						datam.addAll(Select.generireDefaultMessen(daten.get(0), daten.get(1)));
						datam.sort(Messe.compForMessen);
						ArrayList<Label> l = new ArrayList<>();
						for (Messe m : datam) {
							l.add(new Label(m.getID().replaceAll("\t", "\t\t")));
						}
						list.getItems().removeIf(p->{return true;});
						list.setItems(FXCollections.observableArrayList(l));
					} catch (Exception e) {
						Dialogs.error(e, "Konnte die Messen nicht generieren.");
					}
				}
			});
			pane.getChildren().add(generieren);
			
			text.setText("Messen anzeigen & bearbeiten");
			list.getItems().removeIf(t -> true);
			datam.sort(Messe.compForMessen);
			for (int i = 0; i < datam.size(); i++) {
				list.getItems().add(new Label(datam.get(i).getID().replaceAll("\t", "\t\t")));
			}
			list.setOnMouseClicked(mouseEvent -> {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						int i = list.getSelectionModel().getSelectedIndex();
						mc.changePaneMesse(datam.get(i));
					}
				}
			});
			remove.setOnAction(arg0 -> {
				if (mc.getMessen().removeIf(
						m -> list.getSelectionModel().getSelectedItem().getText().replaceAll("\t\t", "\t").equalsIgnoreCase(m.toString()))) {
					afterstartup(window, mc);
				}
			});
			break;
		}
	}

	public void neu() {
		if (sel == Selecter.Messdiener)
			mc.changePaneMessdiener(null);
		else if(sel == Selecter.Messe) mc.changePaneMesse(null);
	}

	public void remove() {
		if (sel == Selecter.Messdiener) {
			String s = list.getSelectionModel().getSelectedItem().getText();
			for (Messdiener medi : DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList()) {
				if (medi.toString().equalsIgnoreCase(s)) {
					MediController.remove(p.getScene().getWindow(), medi);
					break;
				}
			}
		} else if(sel == Selecter.Messe){

		}
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	public static ArrayList<Messe> generireDefaultMessen(Date anfang, Date ende) throws Exception {
		ArrayList<Messe> rtn = new ArrayList<Messe>();
		Calendar start = Calendar.getInstance();
		for (StandartMesse sm : DateienVerwalter.dv.getPfarrei().getStandardMessen()) {
			if (!(sm instanceof Sonstiges)) {
				start.setTime(anfang);
				ArrayList<Messe> m = optimieren(start, sm, ende, new ArrayList<Messe>());
				rtn.addAll(m);
			}
		}
		rtn.sort(Messe.compForMessen);
		Log.getLogger().info("DefaultMessen generiert");
		return rtn;
	}

	public static ArrayList<Messe> optimieren(Calendar cal, StandartMesse sm, Date end, ArrayList<Messe> mes)
			throws Exception {
		if (cal.getTime().before(end) && !(sm instanceof Sonstiges)) {
			SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
			String tag = wochendagformat.format(cal.getTime());
			if (tag.startsWith(sm.getWochentag())) {
				Date d = cal.getTime();
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				SimpleDateFormat dfuhr = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
				try {
					Date frting = dfuhr
							.parse(df.format(d) + "-" + sm.getBeginn_stundealsString() + ":" + sm.getBeginn_minute());
					Messe m = new Messe(frting, sm);
					mes.add(m);
				} catch (ParseException e) {
					getLogger().info("Parse Exception");
				}
				cal.add(Calendar.DATE, 7);
				mes = optimieren(cal, sm, end, mes);
			} else {
				cal.add(Calendar.DATE, 1);
				mes = optimieren(cal, sm, end, mes);
			}
		}
		return mes;
	}
}
