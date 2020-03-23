package net.aclrian.mpe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import net.aclrian.mpe.Main;
import net.aclrian.mpe.utils.DateienVerwalter;

public class StartController implements Controller{
    @FXML
    private MenuItem mi;
    @FXML
    private Label version_label, name;

    public void initialize(){
        //mi.setText(Main.version);
    	name.setText(DateienVerwalter.dv.getPfarrei().getName());
        version_label.setText(Main.VersionID);
    }

	@Override
	public void afterstartup(Window window, MainController mc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLocked() {
		return false;
	}
}
