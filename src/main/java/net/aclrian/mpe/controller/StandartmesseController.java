package net.aclrian.mpe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Window;
import net.aclrian.fx.ATilePane;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;

import java.io.IOException;
import java.util.ArrayList;

public class StandartmesseController implements Controller {

    private StandartMesse sm;
    private boolean locked = true;
    @FXML
    private ATilePane pane;
    @FXML
    private Button abbrechen,fertig;
    @FXML
    private Text smesse;

    public StandartmesseController(StandartMesse sm){
        this.sm = sm;
    }

    @Override
    public void initialize() {
        smesse.setText(sm.tokurzerBenutzerfreundlichenString());
        ArrayList<Messdiener> selected = new ArrayList<>();
        for (Messdiener m : DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList()) {
                if (m.getDienverhalten().getBestimmtes(sm)) {
                    selected.add(m);
                }
        }
        pane.setSelected(selected);
    }

    @Override
    public void afterstartup(Window window, MainController mc) {
        abbrechen.setOnAction(event -> {
            locked = false;
            mc.changePane(MainController.EnumPane.start);
        });
        fertig.setOnAction(event -> {
            ArrayList<Messdiener> medis = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
            for (Messdiener m : medis) {
                try {
                    m.getDienverhalten().editiereBestimmteMesse(sm, pane.getSelected().contains(m));
                    WriteFile wf = new WriteFile(m);
                    wf.toXML();
                } catch (IOException e) {
                    Dialogs.error(e, "Konnte den Messdiener " + medis.toString() + " nicht speichern.");
                }
            }
            locked = false;
            mc.changePane(MainController.EnumPane.start);
        });
    }

    @Override
    public boolean isLocked() {
        return locked;
    }
}
