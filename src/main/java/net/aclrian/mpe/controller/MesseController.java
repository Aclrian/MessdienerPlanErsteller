package net.aclrian.mpe.controller;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import com.jfoenix.controls.JFXTimePicker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.fx.ATilePane;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.start.References;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class MesseController implements Controller {
	private boolean locked = true;
	private Messe moben = null;
	private StandartMesse s = null;

	@FXML
	private JFXTextField titel, ort;

	@FXML
	private JFXDatePicker datum;

	@FXML
	private JFXTimePicker uhr;

	@FXML
	private Slider slider;

	@FXML
	private CheckBox hochamt;

	@FXML
	private Label smesse;

	@FXML
	private SplitMenuButton button;

	@FXML
	private MenuItem save_new, cancel;

	@FXML
	private ATilePane list;

	@Override
	public void afterstartup(Window window, MainController mc) {
		ASlider.makeASlider("Messdiener: ", slider);
		slider.setValue(6d);
		slider.setMax(30);
		slider.setMin(1);
		cancel.setOnAction(e -> {
			locked = false;
			if(moben != null) mc.getMessen().add(moben);
			mc.changePane(MainController.EnumPane.selectMesse);
		});
		save_new.setOnAction(e -> {
			if (saveMesse(mc)) {
				locked = false;
				mc.changePaneMesse(null);
			} else{
				Dialogs.warn("Bitte einen Titel, Ort, Datum und Uhrzeit eigeben.");
			}
		});
		button.setOnAction(e -> {
			if (saveMesse(mc)) {
				locked = false;
				mc.changePane(MainController.EnumPane.selectMesse);
			}
		});

	}

	private boolean saveMesse(MainController mc) {
		if(datum.getValue() != null && uhr.getValue() != null && !ort.getText().equals("") && !titel.getText().equals("")){
			Messe m = new Messe(hochamt.isSelected(), (int)slider.getValue(), getDate(), ort.getText(), titel.getText(), s);
			for(Messdiener medi : list.getSelected()){
				m.vorzeitigEiteilen(medi);
			}
			mc.getMessen().add(m);
			mc.getMessen().sort(Messe.compForMessen);
			return true;
		}
		return false;
	}

	@Override
	public void initialize() {
		s = new Sonstiges();
		smesse.setText(s.tolangerBenutzerfreundlichenString());
		smesse.setDisable(true);
		uhr.set24HourView(true);
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public void setMesse(Messe messe) {
		if(messe == null)return;
		s=messe.getStandardMesse();
		smesse.setText(messe.getStandardMesse().tolangerBenutzerfreundlichenString());
		moben = messe;
		titel.setText(messe.getMesseTyp());
		ort.setText(messe.getKirche());
		slider.setValue(messe.getAnz_messdiener());
		hochamt.setSelected(messe.isHochamt());
		list.setSelected(messe.getEingeteilte());
		LocalDate ld = messe.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		datum.setValue(ld);
		uhr.setValue(LocalDateTime.ofInstant(messe.getDate().toInstant(), ZoneId.systemDefault()).toLocalTime());
		list.setSelected(messe.getEingeteilte());
	}

	private Date getDate(){
		return Date.from(datum.getValue().atTime(uhr.getValue()).atZone(ZoneId.systemDefault()).toInstant());
	}

	@FXML
	private void standardmesseBearbeiten(){
		StandartMesse s = Dialogs.singleSelect(DateienVerwalter.dv.getPfarrei().getStandardMessen(),"Standartmesse "+ References.ae +"ndern:");
		if (s!=null) {
			smesse.setText(s.tolangerBenutzerfreundlichenString());
			this.s = s;
		}
	}

}
