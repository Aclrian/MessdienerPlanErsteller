package net.aclrian.mpe.controller;

import java.util.ArrayList;

import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.TilePane;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.utils.DateienVerwalter;

public class MesseController implements Controller {
	@FXML
	private JFXTextField titel, ort;

	@FXML
	private Slider slider;
	
	@FXML
	private CheckBox hochamt;
	
	@FXML
	private SplitMenuButton button;
	
	@FXML
	private MenuItem save_new, cancel;
	
	@FXML
	private TilePane list;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterstartup(Window window, MainController mc) {
		ASlider.makeASlider("Messdiener: ", slider);
		slider.setValue(6d);
		slider.setMax(30);
		slider.setMin(1);
	}

	@Override
	public boolean isLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setMesse(Messe messe) {
		String stitel = messe.getTitle().equals("") ? messe.getMesseTyp():messe.getTitle();
		titel.setText(stitel);
		ort.setText(messe.getKirche());
		slider.setValue(messe.getAnz_messdiener());
		hochamt.setSelected(messe.isHochamt());
		ArrayList<Messdiener> medis = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		medis.sort(Messdiener.compForMedis);
		for (Messdiener medi : medis) {
			CheckBox b = new CheckBox(medi.toString());
			boolean sel = false;
			for(Messdiener m : messe.getEingeteilte()) {
				if (m.toString().equalsIgnoreCase(medi.toString())) {
					sel = true;
					break;
				}
			}
			b.setSelected(sel);
			b.setMaxWidth(Double.MAX_VALUE);
			list.getChildren().add(b);
		}
	}

}
