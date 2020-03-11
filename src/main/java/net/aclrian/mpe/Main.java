package net.aclrian.mpe;

import static net.aclrian.mpe.utils.Log.getLogger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.aclrian.mpe.controller.MainController;
import net.aclrian.mpe.controller.MainController.EnumPane;
import net.aclrian.mpe.start.VersionIDHandler;
import net.aclrian.mpe.start.VersionIDHandler.EnumHandling;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.DateienVerwalter.NoSuchPfarrei;

public class Main extends Application {
	/**
	 * Das ist die Versionsnummer. Das b zeigt eine Beta-Version an.
	 */
	public static final String VersionID = "b699";

	@Override
	public void start(Stage stage) throws Exception {
		//Object o = getParameters();
		main(stage);
	}

	public void main(Stage stage) {
		try {
			getLogger().info("MpE: Version: " + VersionID);
			getLogger().info("MpE-fx is starting");
			stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/title_32.png")));
			versioncheck();
			try {
			DateienVerwalter.re_start(stage);
			} catch (NoSuchPfarrei e) {
				PfarreiController.start(stage, e.getSavepath(), this);
				return;
			}
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/AAhaupt.fxml"));

			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);

			stage.setTitle("MessdienerplanErsteller");
			stage.setResizable(false);
			stage.show();
			((MainController)loader.getController()).changePane(EnumPane.start);
			getLogger().info("Startbildschirm geladen");
		} catch (Exception e) {
			Dialogs.error(e, "Es ist ein unerwarteter Fehler aufgetreten:");
		}
	}
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
		
		// TODO csv Converter
		/*} else if (args[0].startsWith("csv")) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Beschreibung";
				}

				@Override
				public boolean accept(File f) {
					return f.getName().endsWith(".csv");
				}
			});
			fc.setDialogTitle("W" + References.ae + "hle CSV-Datei");
			fc.setApproveButtonText("Ausw" + References.ae + "hlen");
		}*/
	
	}

	private void versioncheck() {
		VersionIDHandler vh = new VersionIDHandler();
		EnumHandling eh = vh.rankingVersionID();
		if (eh == EnumHandling.betaRequest | eh == EnumHandling.isOld) {
			try {
				Dialogs.open(new URI(VersionIDHandler.urlwithtag + vh.getInternettid()),
						eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
			} catch (IOException | URISyntaxException e) {
				try {
					Dialogs.open(new URI(VersionIDHandler.alternativedownloadurl),
							eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
				} catch (IOException | URISyntaxException e1) {
					getLogger().warning("Die Download-Url konnte nicht aufgelöst werden.");
				}
			}
		}
		getLogger().info(eh.getMessage());
	}
}