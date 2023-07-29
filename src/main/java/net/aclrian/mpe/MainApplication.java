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

import java.util.Objects;

import static net.aclrian.mpe.utils.MPELog.getLogger;

public class MainApplication extends Application {
    public static final String VERSION_ID = "1.0.2";

    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }

    public void main(Stage stage) {
        try {
            getLogger().info("MpE: Version: " + VERSION_ID);
            getLogger().info("MpE-fx is starting");
            stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/title_32.png"))));
            VersionIDHandler.versionCheck(false);
            if (startPfarrei(stage)) {
                return;
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/AAhaupt.fxml"));
            loader.setController(new MainController(this, stage));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle("MessdienerplanErsteller");
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

    @Override
    public void start(Stage stage) {
        main(stage);
    }
}
