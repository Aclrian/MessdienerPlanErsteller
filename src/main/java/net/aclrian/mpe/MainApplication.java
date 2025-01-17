package net.aclrian.mpe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.aclrian.mpe.controller.MainController;
import net.aclrian.mpe.controller.PfarreiController;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.VersionIDHandler;

import java.io.IOException;
import java.util.Objects;

import static net.aclrian.mpe.utils.MPELog.getLogger;

/**
 * MainApplication - Klasse
 * Hauptapplikationsklasse einer JavaFX - Anwendung.
 */
public class MainApplication extends Application {
    public static final String VERSION_ID = "1.0.7";

    /**
     * Funktion gibt die Aufrufparameter an launch() weiter.
     * @param args Dei Aufrufparameter
     */
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }

    /**
     * Eigentlich die start() - Methode von MainApplikation. Wurde direkt
     * von start() aufgerufen.
     * @param stage Stage die von start() an main() weitergeleitet wird.
     */
    public void main(Stage stage) {
        try {
            getLogger().info("MpE: Version: " + VERSION_ID);
            getLogger().info("MpE-fx is starting");
            stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/title_32.png"))));
            //Prüfe ob neue Version vorhanden ist und zeige nur Fenster,
            //wenn Version im Internet höher ist als die verwendete.
            VersionIDHandler.versionCheck(false);
            if (startPfarrei(stage)) {
                return;
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/head.fxml"));
            loader.setController(new MainController(this, stage));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle("MessdienerplanErsteller");
            stage.onCloseRequestProperty().addListener(observable -> {
                try {
                    if (DateienVerwalter.getInstance().getLock().isValid()) {
                        DateienVerwalter.getInstance().getLock().release();
                    }
                } catch (IOException e) {
                    Dialogs.getDialogs().error(e, e.getMessage());
                }
            });
            stage.show();
            ((MainController) loader.getController()).changePane(MainController.EnumPane.START);
            getLogger().info("Startbildschirm geladen");
        } catch (Exception e) {
            Dialogs.getDialogs().error(e, "Es ist ein unerwarteter Fehler aufgetreten:");
        }
    }

    private boolean startPfarrei(Stage stage) {
        try {
            DateienVerwalter.reStart(stage);
        } catch (DateienVerwalter.NoSuchPfarrei e) {
            PfarreiController.start(stage, e.getSavepath().getAbsolutePath(), this);
            return true;
        }
        return false;
    }

    /**
     * Leiten den Aufruf von start(Stage stage) nach main(Stage stage) um.
     */
    @Override
    public void start(Stage stage) {
        main(stage);
    }
}
