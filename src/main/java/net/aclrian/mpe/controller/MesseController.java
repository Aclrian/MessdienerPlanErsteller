package net.aclrian.mpe.controller;

import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.stage.Window;
import net.aclrian.mpe.messe.Messe;

public class MesseController implements Controller {
	@FXML
	private JFXTextField titel, ort;

	@FXML
	private Slider slider;
	
	@FXML
	private CheckBox hochamt;
	
	@FXML
	private Button button;
	
	@FXML
	private MenuItem save_new, cancel;
	
	@FXML
	private ListView list;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterstartup(Window window, MainController mc) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setMesse(Messe messe) {
		// TODO Auto-generated method stub

	}

}
