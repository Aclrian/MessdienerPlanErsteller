package net.aclrian.mpe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import net.aclrian.mpe.Main;

public class StartController implements Controller{
    @FXML
    private MenuItem mi;
    @FXML
    private Label version_label;

    public void setPfarrei(){

    }

    public void initialize(){
        //mi.setText(Main.version);
        version_label.setText(Main.VersionID);
    }

	@Override
	public void afterstartup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLocked() {
		return false;
	}
}
