package net.aclrian.fx;

import java.util.ArrayList;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.TilePane;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.utils.DateienVerwalter;

public class ATilePane extends TilePane {

	private class ACheckBox extends CheckBox {
		private Messdiener messdiener;

		public ACheckBox(Messdiener m) {
			super(m.toString());
			messdiener = m;
		}

		public Messdiener getMessdiener() {
			return messdiener;
		}
	}

	private ArrayList<Messdiener> medis;

	public ATilePane() {
		setAlignment(Pos.CENTER);
		setOrientation(Orientation.VERTICAL);
		setMaxHeight(Double.MAX_VALUE);
		setMaxWidth(Double.MAX_VALUE);
		setVgap(5d);
		try {
			medis = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
			medis.sort(Messdiener.compForMedis);
			for (Messdiener messdiener : medis) {
				ACheckBox cb = new ACheckBox(messdiener);
				cb.setMaxWidth(Double.MAX_VALUE);
				cb.setStyle("-fx-padding: 0 0 0 10");
				getChildren().add(cb);
			}
		} catch (Exception e) {
			// For SceneBuilder
		}

	}

	public ATilePane(ArrayList<Messdiener> selected) {
		setAlignment(Pos.CENTER);
		setOrientation(Orientation.VERTICAL);
		medis = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		medis.sort(Messdiener.compForMedis);
		for (Messdiener messdiener : medis) {
			ACheckBox cb = new ACheckBox(messdiener);
			getChildren().add(cb);
		}
		setSelected(selected);
	}

	public ArrayList<Messdiener> getSelected() {
		ArrayList<Messdiener> rtn = new ArrayList<>();
		for (Node n : getChildrenUnmodifiable()) {
			if (n instanceof ACheckBox && ((ACheckBox) n).isSelected())
				rtn.add(((ACheckBox) n).getMessdiener());
		}
		return rtn;
	}

	public void setSelected(ArrayList<Messdiener> eingeteilte) {
			for (Node n : getChildrenUnmodifiable()) {
				if (n instanceof ACheckBox) {
					for (Messdiener messdiener : eingeteilte) {
						if (messdiener.toString().equalsIgnoreCase(((ACheckBox) n).getMessdiener().toString())) {
							((CheckBox) n).setSelected(true);
						}
					}
				}

			}
	}
}
