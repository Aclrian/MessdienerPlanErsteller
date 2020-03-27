package net.aclrian.mpe.controller;

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
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import net.aclrian.mpe.utils.VersionIDHandler;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static net.aclrian.mpe.utils.Log.getLogger;

public class MainController {
	private Stage stage;
	private ArrayList<Messe> messen = new ArrayList<>();
	private Main m;

	@FXML
	private GridPane grid;
	@FXML
	private AnchorPane apane;

	private Controller control;

	public MainController(Main m,Stage s) {
		this.m = m;
		this.stage = s;
		s.setOnCloseRequest(e-> getLogger().info("Beenden"));
	}

	private EnumPane ep;

	public void setMesse(FinishController finishController, EnumPane pane) {
		messen = finishController.getMessen();
		changePane(pane);
	}

	public enum EnumPane {
		messdiener("/view/messdiener.fxml"), messe("/view/messe.fxml"), start("/view/mainmlg.fxml"), plan("/view/Aplan.fxml"),
		ferien("/view/fplan.fxml"), stdmesse("/view/smesse.fxml"), selectMedi("/view/select.fxml"), selectMesse("/view/select.fxml");

		private String location;

		EnumPane(String location) {
			this.location = location;
		}

		public String getLocation() {
			return location;
		}
	}

	public void changePaneMessdiener(Messdiener messdiener) {
		this.ep = EnumPane.messdiener;
		apane.getChildren().removeIf(p -> true);
		URL u = getClass().getResource(ep.getLocation());
		FXMLLoader fl = new FXMLLoader(u);
		try {
			load(fl);
			if (control instanceof MediController) {
				((MediController) control).setMedi(messdiener);
			}
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + ep.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}

	public void changePaneMesse(Messe messe) {
		this.ep = EnumPane.messe;
		apane.getChildren().removeIf(p -> true);
		URL u = getClass().getResource(ep.getLocation());
		FXMLLoader fl = new FXMLLoader(u);
		try {
			load(fl);
			if (control instanceof MesseController) {
				((MesseController) control).setMesse(messe);
				messen.remove(messe);
			}
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + ep.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}

	private void changePane(StandartMesse sm) {
		 if ((control == null || !control.isLocked()) && !(ep == EnumPane.stdmesse)) {
			this.ep = EnumPane.stdmesse;
			apane.getChildren().removeIf(p -> true);

			URL u = getClass().getResource(ep.getLocation());
			FXMLLoader fl = new FXMLLoader(u);
			try {
				fl.setController(new StandartmesseController(sm));
				load(fl);
			} catch (IOException e) {
				Dialogs.error(e, "Auf " + ep.getLocation() + " konnte nicht zugegriffen werden!");
			}
		} else {
			Dialogs.warn("Der Fensterbereich ist durch die Bearbeitung gesperrt!");
		}
	}

	private void load(FXMLLoader fl) throws IOException {
		Parent p;
		p = fl.load();
		AnchorPane.setBottomAnchor(p, 0d);
		AnchorPane.setRightAnchor(p, 0d);
		AnchorPane.setLeftAnchor(p, 0d);
		AnchorPane.setTopAnchor(p, 0d);
		apane.getChildren().add(p);
		control = fl.getController();
		control.afterstartup(p.getScene().getWindow(), this);
	}

	public void changePane(EnumPane ep) {
		if ((control == null || !control.isLocked()) && !(this.ep == ep)) {
			EnumPane old = this.ep;
			this.ep = ep;
			apane.getChildren().removeIf(p -> true);
			URL u = getClass().getResource(ep.getLocation());
			FXMLLoader fl = new FXMLLoader(u);
			try {
				if (ep == EnumPane.selectMedi) {
					control = new Select(Selecter.Messdiener, this);
					fl.setController(control);
				}else if (ep == EnumPane.selectMesse) {
					control = new Select(Selecter.Messe, this);
					fl.setController(control);
				} else if(ep == EnumPane.plan){
					control = new FinishController(old,messen);
					fl.setController(control);
				}
				load(fl);
			} catch (IOException e) {
				Dialogs.error(e, "Auf " + ep.getLocation() + " konnte nicht zugegriffen werden!");
			}
		} else {
			Dialogs.warn("Der Fensterbereich ist durch die Bearbeitung gesperrt!");
		}
	}

	@FXML
	public void messe(ActionEvent actionEvent) {
		getLogger().info("zu Messen wechseln");
		changePane(EnumPane.selectMesse);
	}

	@FXML
	public void medi(ActionEvent actionEvent) {
		getLogger().info("zu Messdienern wechseln");
		changePane(EnumPane.selectMedi);
	}

	@FXML
	public void generieren(ActionEvent actionEvent) {
		if (DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList().size() > 0 && messen.size() > 0) {
			changePane(EnumPane.plan);
		} else{
			Dialogs.warn("Bitte erst Messen und Messdiener eingeben.");
		}
	}

	@FXML
	public void pfarrei_aendern(ActionEvent actionEvent) {
		if(ep!=EnumPane.start){
			Dialogs.info("Bitte erst auf den Hauptbildschirm (F2) wechseln.");
			return;
		}
		messen = new ArrayList<>();
		PfarreiController.start(new Stage(), m, stage);
	}

	@FXML
	public void speicherort(ActionEvent actionEvent) {
		if(ep!=EnumPane.start){
			Dialogs.info("Bitte erst auf den Hauptbildschirm (F2) wechseln.");
			return;
		}
			DateienVerwalter.dv.erneuereSavepath();
			((Stage)grid.getParent().getScene().getWindow()).close();
			m.main(new Stage());
	}

	@FXML
	public void ferienplan(ActionEvent actionEvent) {
		if (DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList().size() > 0 && messen.size() > 0) {
			changePane(EnumPane.ferien);
		} else {
			Dialogs.warn("Bitte erst Messen und Messdiener eingeben.");
		}
	}

	@FXML
	public void standardmesse(ActionEvent actionEvent) {
		smesse(actionEvent);
	}

	@FXML
	public void hauptbildschirm(ActionEvent actionEvent) {
		changePane(EnumPane.start);
	}

	@FXML
	public void smesse(ActionEvent actionEvent) {
		StandartMesse sm = (StandartMesse) Dialogs.singleSelect(DateienVerwalter.dv.getPfarrei().getStandardMessen(),"Bitte Standartmesse auswählen:");
		if (sm != null){
			changePane(sm);
		}
	}

	@FXML
	public void info(ActionEvent actionEvent) {
		try {
			InfoController ic = new InfoController(stage);
			ic.start();
		} catch (IOException e) {
			Dialogs.error(e, "Auf " + ep.getLocation() + " konnte nicht zugegriffen werden!");
		}
	}

	@FXML
	public void log(ActionEvent event) {
		try {
			Desktop.getDesktop().open(Log.getLogFile());
		} catch (IOException e) {
			Dialogs.error(e,"Konnte das Protokoll nicht öffnen:");
		}
	}

	@FXML
	public void savepath(ActionEvent event) {
		try {
			Desktop.getDesktop().open(new File(DateienVerwalter.dv.getSavepath()));
		} catch (IOException e) {
			Dialogs.error(e,"Konnte den Ordner nicht öffnen:");
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
			Dialogs.error(e,"Konnte den Ordner nicht öffnen:");
		}
	}

	public ArrayList<Messe> getMessen() {
		return messen;
	}
}
