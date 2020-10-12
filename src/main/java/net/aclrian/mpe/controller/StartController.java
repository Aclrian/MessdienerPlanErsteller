package net.aclrian.mpe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import net.aclrian.mpe.Main;
import net.aclrian.mpe.utils.DateienVerwalter;

public class StartController implements Controller {
    @FXML
    private MenuItem mi;
    @FXML
    private Label versionLabel;
    @FXML
    private Label name;

    public void initialize() {
        name.setText(DateienVerwalter.getInstance().getPfarrei().getName());
        versionLabel.setText(Main.VERSION_ID);
    }

    @Override
    public void afterstartup(Window window, MainController mc) {
        //no operation
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}
