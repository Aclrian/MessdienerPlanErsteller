package net.aclrian.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class AListView extends GridPane {
	
	private ArrayList<ListView<Label>> listen;
	private List<ColumnConstraints> spalten;
	
	public AListView(List<Label> data, int anzahl) {
		listen = new ArrayList<ListView<Label>>();
		spalten = new ArrayList<ColumnConstraints>();
		for (int i = 0; i < anzahl; i++) {
			listen.add(new ListView<Label>());
			ColumnConstraints col = new ColumnConstraints();
			col.setPercentWidth(1f/anzahl);
			spalten.add(col);
		}
		for (int i = 0; i < data.size(); i++) {
			int rest = i%anzahl;
			listen.get(rest).getItems().add(data.get(i));
		}
	}

}
