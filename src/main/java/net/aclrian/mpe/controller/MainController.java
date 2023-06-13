package net.aclrian.mpe.controller;

import javafx.application.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.aclrian.mpe.*;
import net.aclrian.mpe.controller.Select.*;
import net.aclrian.mpe.converter.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.utils.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.*;

import static net.aclrian.mpe.utils.Log.*;

public class MainController {
    public static final String NO_ACCESS = " konnte nicht zugegriffen werden!";
    public static final String GESPERRT = "Der Fensterbereich ist durch die Bearbeitung gesperrt!";
    public static final String STANDARDMESSE_AUSWAEHLEN = "Bitte Standardmesse auswählen:";
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

    private void changePane(StandardMesse sm) {
        if ((control == null || !control.isLocked()) && (ep != EnumPane.STDMESSE)) {
            this.ep = EnumPane.STDMESSE;
            apane.getChildren().removeIf(p -> true);

            URL u = getClass().getResource(ep.getLocation());
            FXMLLoader fl = new FXMLLoader(u);
            fl.setController(new StandardmesseController(sm));
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
            control.afterStartup(p.getScene().getWindow(), this);
        } catch (IOException e) {
            Dialogs.getDialogs().fatal(e, "Auf " + ep.getLocation() + NO_ACCESS);
        } catch (Exception ex) {
            changePane(EnumPane.START);
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
                control = new Select(Selector.MESSDIENER, this);
                fl.setController(control);
            } else if (ep == EnumPane.SELECT_MESSE) {
                control = new Select(Selector.MESSE, this);
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
        Platform.runLater(() -> m.main(new Stage()));
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
        StandardMesse sm = (StandardMesse) Dialogs.getDialogs().singleSelect(DateienVerwalter.getInstance().getPfarrei().getStandardMessen(), STANDARDMESSE_AUSWAEHLEN);
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
            Desktop.getDesktop().open(DateienVerwalter.getInstance().getSavePath());
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Ordner nicht öffnen:");
        }
    }

    @FXML
    public void version(ActionEvent event) {
        VersionIDHandler.versionCheck(true);
    }

    @FXML
    public void workingdir(ActionEvent event) {
        try {
            Desktop.getDesktop().open(Log.getWorkingDir());
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Ordner nicht öffnen:");
        }
    }

    @FXML
    public void importCSV(ActionEvent event){
        ConvertCSV.ConvertData data;
        data = Dialogs.getDialogs().importDialog(stage.getScene().getWindow());
        if (data == null) return;
        try {
            ConvertCSV csv = new ConvertCSV(data);
            csv.start();
        } catch (Exception e) {
            Dialogs.getDialogs().error(e, e.getMessage());
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
