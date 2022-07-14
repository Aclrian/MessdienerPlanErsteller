package net.aclrian.mpe.controller;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.aclrian.mpe.Main;
import net.aclrian.mpe.controller.Select.Selecter;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.*;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static net.aclrian.mpe.utils.Log.getLogger;

public class MainController {
    public static final String NO_ACCESS = " konnte nicht zugegriffen werden!";
    public static final String GESPERRT = "Der Fensterbereich ist durch die Bearbeitung gesperrt!";
    public static final String STANDARTMESSE_AUSWAEHLEN = "Bitte Standartmesse auswählen:";
    private final Stage stage;
    private final Main m;
    private List<Messe> messen = new ArrayList<>();
    @FXML
    private GridPane grid;
    @FXML
    private AnchorPane apane;
    private Controller control;
    private EnumPane ep;

    public MainController(Main m, Stage s) {
        this.m = m;
        this.stage = s;
        s.setOnCloseRequest(e -> getLogger().info("Beenden"));
    }

    public void setMesse(List<Messe> changedMessen, EnumPane pane) {
        messen = changedMessen;
        changePane(pane);
    }

    public void changePaneMessdiener(Messdiener messdiener) {
        if (this.ep == EnumPane.MESSDIENER) {
            return;
        }
        if ((control == null || !control.isLocked())) {
            this.ep = EnumPane.MESSDIENER;
            apane.getChildren().removeIf(p -> true);
            URL u = getClass().getResource(ep.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            load(fl);
            if (control instanceof MediController mc) {
                mc.setMedi(messdiener);
            }
        } else {
            Dialogs.getDialogs().warn(GESPERRT);
        }
    }

    public void changePaneMesse(Messe messe) {
        if (this.ep == EnumPane.MESSE) {
            return;
        }
        if ((control == null || !control.isLocked())) {
            this.ep = EnumPane.MESSE;
            apane.getChildren().removeIf(p -> true);
            URL u = getClass().getResource(ep.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            load(fl);
            if (control instanceof MesseController mc) {
                mc.setMesse(messe);
                messen.remove(messe);
            }
        } else {
            Dialogs.getDialogs().warn(GESPERRT);
        }
    }

    private void changePane(StandartMesse sm) {
        if ((control == null || !control.isLocked()) && (ep != EnumPane.STDMESSE)) {
            this.ep = EnumPane.STDMESSE;
            apane.getChildren().removeIf(p -> true);

            URL u = getClass().getResource(ep.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            fl.setController(new StandartmesseController(sm));
            load(fl);
        } else {
            Dialogs.getDialogs().warn(GESPERRT);
        }
    }

    private void load(FXMLLoader fl) {
        Parent p;
        try {
            p = fl.load();
            AnchorPane.setBottomAnchor(p, 0d);
            AnchorPane.setRightAnchor(p, 0d);
            AnchorPane.setLeftAnchor(p, 0d);
            AnchorPane.setTopAnchor(p, 0d);
            apane.getChildren().add(p);
            control = fl.getController();
            control.afterstartup(p.getScene().getWindow(), this);
        } catch (IOException e) {
            Dialogs.getDialogs().fatal(e, "Auf " + ep.getLocation() + NO_ACCESS);
        }
    }

    public void changePane(EnumPane ep) {
        if (this.ep == ep) {
            return;
        }
        if ((control == null || !control.isLocked())) {
            EnumPane old = this.ep;
            this.ep = ep;
            apane.getChildren().removeIf(p -> true);
            URL u = getClass().getResource(ep.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            if (ep == EnumPane.SELECT_MEDI) {
                control = new Select(Selecter.MESSDIENER, this);
                fl.setController(control);
            } else if (ep == EnumPane.SELECT_MESSE) {
                control = new Select(Selecter.MESSE, this);
                fl.setController(control);
            } else if (ep == EnumPane.PLAN) {
                control = new FinishController(old, messen);
                fl.setController(control);
            }
            load(fl);
        } else {
            Dialogs.getDialogs().warn(GESPERRT);
        }
    }

    @FXML
    public void messe(ActionEvent actionEvent) {
        getLogger().info("zu Messen wechseln");
        changePane(EnumPane.SELECT_MESSE);
    }

    @FXML
    public void medi(ActionEvent actionEvent) {
        getLogger().info("zu Messdienern wechseln");
        changePane(EnumPane.SELECT_MEDI);
    }

    @FXML
    public void generieren(ActionEvent actionEvent) {
        if (!DateienVerwalter.getInstance().getMessdiener().isEmpty() && !messen.isEmpty()) {
            changePane(EnumPane.PLAN);
        } else {
            Dialogs.getDialogs().warn("Bitte erst Messen und Messdiener eingeben.");
        }
    }

    @FXML
    public void editPfarrei(ActionEvent actionEvent) {
        if (ep != EnumPane.START) {
            Dialogs.getDialogs().info("Bitte erst auf den Hauptbildschirm (F2) wechseln.");
            return;
        }
        messen = new ArrayList<>();
        PfarreiController.start(new Stage(), m, stage);
    }

    @FXML
    public void speicherort(ActionEvent actionEvent) {
        if (ep != EnumPane.START) {
            Dialogs.getDialogs().info("Bitte erst auf den Hauptbildschirm (F2) wechseln.");
            return;
        }
        Speicherort ort = new Speicherort(grid.getParent().getScene().getWindow());
        ort.changeDir();
        ((Stage) grid.getScene().getWindow()).close();
        Platform.runLater(()-> m.main(new Stage()));
    }

    @FXML
    public void ferienplan(ActionEvent actionEvent) {
        if (!DateienVerwalter.getInstance().getMessdiener().isEmpty() && !messen.isEmpty()) {
            changePane(EnumPane.FERIEN);
        } else {
            Dialogs.getDialogs().warn("Bitte erst Messen und Messdiener eingeben.");
        }
    }

    @FXML
    public void standardmesse(ActionEvent actionEvent) {
        smesse(actionEvent);
    }

    @FXML
    public void hauptbildschirm(ActionEvent actionEvent) {
        changePane(EnumPane.START);
    }

    @FXML
    public void smesse(ActionEvent actionEvent) {
        StandartMesse sm = (StandartMesse) Dialogs.getDialogs().singleSelect(DateienVerwalter.getInstance().getPfarrei().getStandardMessen(), STANDARTMESSE_AUSWAEHLEN);
        if (sm != null) {
            changePane(sm);
        }
    }

    @FXML
    public void info(ActionEvent actionEvent) {
        try {
            InfoController ic = new InfoController(stage);
            ic.start();
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Auf " + ep.getLocation() + NO_ACCESS);
        }
    }

    @FXML
    public void log(ActionEvent event) {
        try {
            Desktop.getDesktop().open(Log.getLogFile());
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte das Protokoll nicht öffnen:");
        }
    }

    @FXML
    public void savepath(ActionEvent event) {
        try {
            Desktop.getDesktop().open(DateienVerwalter.getInstance().getSavepath());
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Ordner nicht öffnen:");
        }
    }

    @FXML
    public void version(ActionEvent event) {
        VersionIDHandler.versioncheck(true);
    }

    @FXML
    public void workingdir(ActionEvent event) {
        try {
            Desktop.getDesktop().open(Log.getWorkingDir());
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Ordner nicht öffnen:");
        }
    }

    public List<Messe> getMessen() {
        return messen;
    }

    public EnumPane getEnumPane() {
        return ep;
    }

    public Controller getControl() {
        return control;
    }

    public enum EnumPane {
        MESSDIENER("/view/messdiener.fxml"), MESSE("/view/messe.fxml"), START("/view/mainmlg.fxml"), PLAN("/view/Aplan.fxml"),
        FERIEN("/view/fplan.fxml"), STDMESSE("/view/smesse.fxml"), SELECT_MEDI("/view/select.fxml"), SELECT_MESSE("/view/select.fxml");

        private final String location;

        EnumPane(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }
    }
}
