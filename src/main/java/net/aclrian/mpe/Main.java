package net.aclrian.mpe;

import static net.aclrian.mpe.utils.Log.getLogger;

import java.io.FileReader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.aclrian.mpe.controller.MainController;
import net.aclrian.mpe.controller.MainController.EnumPane;
import net.aclrian.mpe.controller.PfarreiController;
import net.aclrian.mpe.utils.VersionIDHandler;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.DateienVerwalter.NoSuchPfarrei;
import net.aclrian.mpe.utils.Dialogs;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class Main extends Application {
	public static String VersionID;

	public static void main(String[] args) {
		Application.launch(Main.class, args);

		// TODO csv Converter
		/*
		 * } else if (args[0].startsWith("csv")) { JFileChooser fc = new JFileChooser();
		 * fc.setFileSelectionMode(JFileChooser.FILES_ONLY); fc.setFileFilter(new
		 * FileFilter() {
		 * 
		 * @Override public String getDescription() { return "Beschreibung"; }
		 * 
		 * @Override public boolean accept(File f) { return
		 * f.getName().endsWith(".csv"); } }); fc.setDialogTitle("W" + References.ae +
		 * "hle CSV-Datei"); fc.setApproveButtonText("Ausw" + References.ae + "hlen"); }
		 */

	}

	public void main(Stage stage) {
		try {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(new FileReader("pom.xml"));
			VersionID = model.getVersion();
			Object o = getParameters();
			getLogger().info("MpE: Version: " + VersionID);
			getLogger().info("MpE-fx is starting");
			stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/title_32.png")));
			VersionIDHandler.versioncheck(false);
			try {
				DateienVerwalter.re_start(stage);
			} catch (NoSuchPfarrei e) {
				PfarreiController.start(stage, e.getSavepath(), this);
				return;
			}
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/AAhaupt.fxml"));
			loader.setController(new MainController(this,stage));
			Parent root = loader.load();
			Scene scene = new Scene(root);
		/*	scene.getRoot().setStyle("-fx-base:black");
			scene.getStylesheets().add("/css/darkmode.css");
		*/	stage.setScene(scene);

			stage.setTitle("MessdienerplanErsteller");
			stage.setResizable(false);
			stage.show();
			((MainController) loader.getController()).changePane(EnumPane.start);
			getLogger().info("Startbildschirm geladen");
		} catch (Exception e) {
			Dialogs.error(e, "Es ist ein unerwarteter Fehler aufgetreten:");
		}
	}

	@Override
	public void start(Stage stage) {
		// Object o = getParameters();
		main(stage);
	}
}