package net.aclrian.mpe.controller;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import net.aclrian.mpe.*;
import net.aclrian.mpe.utils.*;

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
    public void afterStartup(Window window, MainController mc) {
        //no operation
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}
