package net.aclrian.mpe.controller;


import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import javafx.scene.control.Label;
import net.aclrian.mpe.MainApplication;
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
        versionLabel.setText(MainApplication.VERSION_ID);
    }

    @Override
    public void afterStartup(Window window, MainController mc) {
        //no operation
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}
