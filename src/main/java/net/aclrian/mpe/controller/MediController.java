package net.aclrian.mpe.controller;

import java.util.Calendar;

import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import net.aclrian.fx.ASlider;
import net.aclrian.mpe.messdiener.Messdiener;

import static net.aclrian.mpe.utils.Log.getLogger;

public class MediController implements Controller {
	public static final String eintritt_str = "Eintritt: ";
	@FXML
	private Slider eintritt;
	@FXML
	private JFXTextField name, vorname, email;
	@FXML
	private CheckBox leiter;
	public MediController() {

	}

	public void initialize() {
		eintritt.setMax(getMaxYear());
		eintritt.setMin(getMinYear());
		
	}

	public static int getMinYear() {
		return getMaxYear() - 18;
	}

	public static int getMaxYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	@Override
	public void afterstartup() {
		// Value in Silder
		ASlider.makeASlider(eintritt_str, eintritt);
		eintritt.setValue(getMaxYear());
	}

	@Override
	public boolean isLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setMedi(Messdiener messdiener) {
		getLogger().info("Messdiener wurde geladen");
		name.setText(messdiener.getNachnname());
		vorname.setText(messdiener.getVorname());
		email.setText(messdiener.getEmail());
		leiter.setSelected(messdiener.isIstLeiter());
		eintritt.setValue(messdiener.getEintritt());
	}
}
