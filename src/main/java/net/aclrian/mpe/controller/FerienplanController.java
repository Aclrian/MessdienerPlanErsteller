package net.aclrian.mpe.controller;

import com.lowagie.text.Table;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.RemoveDoppelte;

import javax.swing.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class FerienplanController implements Controller {
	private ArrayList<Messdiener> medis;
	private ArrayList<String> dates;
	private ArrayList<TableColumn<Messdiener, Boolean>> cols;
	@FXML
	private TableColumn<Messdiener, String> name;
	@FXML
	private TableView<Messdiener> table;

	@FXML
	private Button abbrechen;

	@FXML
	private Button fertig;

	public FerienplanController(MainController mc){
		medis = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		//messen = mc.getMessen();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
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
		dates.add(0, "Messdiener");
		for (String date : dates){
			var col = new TableColumn<>();
			col.setText(date);
		}
	}

	@Override
	public void initialize() {

	}

	@Override
	public void afterstartup(Window window, MainController mc) {
		table.setItems(FXCollections.observableArrayList(medis));
	}

	@Override
	public boolean isLocked() {
		return false;
	}


}
