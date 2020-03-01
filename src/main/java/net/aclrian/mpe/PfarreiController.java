package net.aclrian.mpe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Window;
import net.aclrian.mpe.controller.Controller;
import net.aclrian.mpe.controller.MainController;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.Dialogs;

public class PfarreiController implements Controller {

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

	
	@FXML
	public void neu(ActionEvent e) {
		StandartMesse sm = Dialogs.standartmesse();
		System.out.println(sm+"");
	}
	
	@FXML
	public void löschen(ActionEvent e) {
		
	}
	
	@FXML
	public void weiter(ActionEvent e) {
		
	}
	
}
