package net.aclrian.mpe.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Callback;
import net.aclrian.fx.CheckBoxCell;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import net.aclrian.mpe.utils.RemoveDoppelte;
import org.docx4j.org.apache.xpath.operations.Bool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FerienplanController implements Controller {
	private ArrayList<Messdiener> medis;
	private ArrayList<Datenträger> data;
	private ArrayList<String> dates = new ArrayList<>();
	private ArrayList<TableColumn<Datenträger, Boolean>> cols;
	@FXML
	private TableColumn<Datenträger, String> name;
	@FXML
	private TableView<Datenträger> table;

	@FXML
	private Button zurück, leeren, hilfe, fertig;

	@Override
	public void initialize() {

	}

	private class Datenträger{
		private class Datum{
			private String d;
			private SimpleBooleanProperty b;
			public Datum(String d, boolean b){
				this.b = new SimpleBooleanProperty(b);
				this.d = d;
			}

			public void setB(boolean b) {
				Log.getLogger().info("lol");
				this.b.setValue(b);
			}

			public boolean isB() {
				return b.get();
			}

			public SimpleBooleanProperty getProperty() {
				return b;
			}
		}
		private HashMap<String, Datum> hm = new HashMap<>();
		private Messdiener m;
		public Datenträger(SimpleDateFormat df, ArrayList<String> dates, Messdiener messdiener){
			m = messdiener;
			for (String d : dates) {
				Datum datum = new Datum(d,messdiener.getMessdatenDaten().ausgeteilt(d));
				datum.getProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						if(oldValue!=newValue){
							try {
								if (newValue) {
									messdiener.getMessdatenDaten().austeilen(df.parse(d));
								} else {
									messdiener.getMessdatenDaten().ausausteilen(df.parse(d));
								}
							} catch (ParseException e){
								Dialogs.error(e,"Konnte das Datum nicht bekommen.");
							}
						}
					}
				});
				hm.put(d,datum);
			}
		}

		public Datum get(String d) {
			return hm.get(d);
		}

		public String getMessdiener() {
			return m.makeId();
		}
	}
	@Override
	public void afterstartup(Window window, MainController mc) {
		medis = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		ArrayList<Datenträger> daten = new ArrayList<>();
		//messen = mc.getMessen();
		for (Messe messe : mc.getMessen()) {
			dates.add(df.format(messe.getDate()));
		}
		RemoveDoppelte<String> rd = new RemoveDoppelte<>();
		rd.removeDuplicatedEntries(dates);
		dates.sort((o1, o2) -> {
			try {
				Date d1 = df.parse(o1);
				Date d2 = df.parse(o2);
				return d1.compareTo(d2);
			} catch (ParseException e) {
				e.printStackTrace();
				return o1.compareTo(o2);
			}
		});
		TableColumn<Datenträger, String> column = new TableColumn<>();
		column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMessdiener()));
		column.setText("Messdiener");
		column.setPrefWidth(100);
		table.getColumns().add(column);
		/*TableColumn<Datenträger, Datenträger.Datum> co = new TableColumn<>();
		co.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().get(param.getTableColumn().getText())));
		co.setText("Daten");
		co.setPrefWidth(100);
		*///table.getColumns().add(column);
		table.getSelectionModel().setCellSelectionEnabled(true);
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		for (String date : dates){
			TableColumn<Datenträger, Boolean> col = new TableColumn<>();
			col.setCellValueFactory(param -> param.getValue().get(date).getProperty());
			col.setCellFactory(new Callback<TableColumn<Datenträger, Boolean>, TableCell<Datenträger, Boolean>>() {
				@Override
				public TableCell<Datenträger, Boolean> call(TableColumn<Datenträger, Boolean> param) {
					CheckBoxTableCell<Datenträger, Boolean> cc = new CheckBoxTableCell<>();////CheckBoxTableCell<>();
					return cc;
				}
			});
			col.setEditable(true);
			col.setPrefWidth(100);
			col.setText(date);
			table.getColumns().add(col);
		}
		table.setEditable(true);
		column.setEditable(false);
		for (Messdiener m: medis) {
			daten.add(new Datenträger(df,dates,m));
		}
		table.setItems(FXCollections.observableArrayList(daten));
		fertig.setOnAction(event -> {
			ObservableList<Datenträger> d = table.getItems();
			mc.changePane(MainController.EnumPane.plan);
		}
		);
		table.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE){
					int i = table.getFocusModel().getFocusedCell().getColumn();
					if(i!=0){
						int row = table.getFocusModel().getFocusedCell().getRow();
						String s = dates.get(i-1);
						table.getItems().get(row).get(s).setB(!table.getItems().get(row).get(s).isB());
						table.refresh();
						table.getFocusModel().focusLeftCell();
						table.getFocusModel().focusRightCell();
					}
				}
			}
		});
		zurück.setOnAction(e->mc.changePane(MainController.EnumPane.start));
		leeren.setOnAction(e->{
			for(String d : dates)
			table.getItems().forEach(dt-> dt.get(d).getProperty().set(false));
		});
		hilfe.setOnAction(e->Dialogs.info("Hier können Messdiener für bestimmte Tage abgemeldet werden:\n\nEin Hacken in einem Kasten heißt, dass der Messdiener an dem Tag nicht eingeteilt wird.\nMit den Pfeiltasten kann die Zelle gewechselt werden und mit dem Leerzeichen der Hacken gesetzt und entfernt werden.\n\nÄnderungen werden sofort umgesetzt und können nur durch Leeren komplett zurückgesetzt werden."));
	}

	@Override
	public boolean isLocked() {
		return false;
	}


}
