package net.aclrian.mpe.controller;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.*;
import net.aclrian.fx.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.utils.*;

import java.io.*;
import java.util.*;

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
