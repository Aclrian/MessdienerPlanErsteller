package net.aclrian.mpe.controller;


import javafx.fxml.FXML;
import javafx.stage.Window;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import net.aclrian.fx.ATilePane;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StandardmesseController implements Controller {

    private final StandardMesse sm;
    private boolean locked = true;
    @FXML
    private ATilePane pane;
    @FXML
    private Button abbrechen;
    @FXML
    private Button fertig;
    @FXML
    private Text smesse;

    public StandardmesseController(StandardMesse sm) {
        this.sm = sm;
    }

    @Override
    public void initialize() {
        smesse.setText(sm.toKurzerBenutzerfreundlichenString());
        ArrayList<Messdiener> selected = new ArrayList<>();
        for (Messdiener m : DateienVerwalter.getInstance().getMessdiener()) {
                if (m.getDienverhalten().getBestimmtes(sm)) {
                    selected.add(m);
                }
        }
        pane.setSelected(selected);
    }

    @Override
    public void afterStartup(Window window, MainController mc) {
        abbrechen.setOnAction(event -> {
            locked = false;
            mc.changePane(MainController.EnumPane.START);
        });
        fertig.setOnAction(event -> {
            List<Messdiener> medis = DateienVerwalter.getInstance().getMessdiener();
            for (Messdiener m : medis) {
                try {
                    m.getDienverhalten().editiereBestimmteMesse(sm, pane.getSelected().contains(m));
                    WriteFile wf = new WriteFile(m);
                    wf.toXML();
                } catch (IOException e) {
                    Dialogs.getDialogs().error(e, "Konnte den Messdiener " + medis + " nicht speichern.");
                }
            }
            locked = false;
            mc.changePane(MainController.EnumPane.START);
        });
    }

    @Override
    public boolean isLocked() {
        return locked;
    }
}
