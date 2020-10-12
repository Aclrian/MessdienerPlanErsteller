package net.aclrian.mpe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import net.aclrian.fx.ASlider;
import net.aclrian.fx.ATilePane;
import net.aclrian.fx.TimeSpinner;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static net.aclrian.mpe.utils.Log.getLogger;

public class MesseController implements Controller {
    public static final String STANDARTMESSE_AUSWAEHLEN = "Standartmesse ändern:";
    private boolean locked = true;
    private Messe moben = null;
    private StandartMesse standartMesse = null;

    @FXML
    private TextField titel;

    @FXML
    private TextField ort;

    @FXML
    private DatePicker datum;

    @FXML
    private TimeSpinner uhr;

    @FXML
    private Slider slider;

    @FXML
    private CheckBox hochamt;

    @FXML
    private Label smesse;

    @FXML
    private SplitMenuButton button;

    @FXML
    private MenuItem saveNew;

    @FXML
    private MenuItem cancel;

    @FXML
    private ATilePane list;

    @FXML
    private Button smbearbeiten;

    @Override
    public void afterstartup(Window window, MainController mc) {
        ASlider.makeASlider("Messdiener", slider, null);
        slider.setValue(6d);
        slider.setMax(30);
        slider.setMin(1);
        cancel.setOnAction(e -> {
            locked = false;
            if (moben != null) mc.getMessen().add(moben);
            mc.changePane(MainController.EnumPane.SELECT_MESSE);
        });
        saveNew.setOnAction(e -> {
            if (saveMesse(mc)) {
                locked = false;
                mc.changePaneMesse(null);
            } else {
                Dialogs.getDialogs().warn("Bitte einen Titel, Ort, Datum und Uhrzeit eigeben.");
            }
        });
        button.setOnAction(e -> {
            if (saveMesse(mc)) {
                locked = false;
                mc.changePane(MainController.EnumPane.SELECT_MESSE);
            } else {
                Dialogs.getDialogs().warn("Bitte einen Titel, Ort, Datum und Uhrzeit eigeben.");
            }
        });
        uhr.getValueFactory().setValue(null);
    }

    private boolean saveMesse(MainController mc) {
        if (datum.getValue() != null && uhr.getValue() != null && !ort.getText().equals("") && !titel.getText().equals("")) {
            Messe m = new Messe(hochamt.isSelected(), (int) slider.getValue(), getDate(), ort.getText(), titel.getText(), standartMesse);
            for (Messdiener medi : list.getSelected()) {
                if (!m.vorzeitigEiteilen(medi)) {
                    Dialogs.getDialogs().warn(medi.makeId() + " konnte nicht vorzeitig eingeteilt werden.");
                }
            }
            mc.getMessen().add(m);
            mc.getMessen().sort(Messe.compForMessen);
            return true;
        }
        return false;
    }

    @Override
    public void initialize() {
        standartMesse = new Sonstiges();
        smesse.setText(standartMesse.tolangerBenutzerfreundlichenString());
        smesse.setDisable(true);
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    public void setMesse(Messe messe) {
        if (messe == null) return;
        standartMesse = messe.getStandardMesse();
        smesse.setText(messe.getStandardMesse().tolangerBenutzerfreundlichenString());
        moben = messe;
        titel.setText(messe.getMesseTyp());
        ort.setText(messe.getKirche());
        slider.setValue(messe.getAnzMessdiener());
        hochamt.setSelected(messe.isHochamt());
        list.setSelected(messe.getEingeteilte());
        LocalDate ld = messe.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datum.setValue(ld);
        uhr.getValueFactory().setValue(LocalTime.ofInstant(messe.getDate().toInstant(), ZoneId.systemDefault()));
        list.setSelected(messe.getEingeteilte());
        getLogger().info("Messe wurde geladen");
    }

    private Date getDate() {
        return Date.from(datum.getValue().atTime(uhr.getValue()).atZone(ZoneId.systemDefault()).toInstant());
    }

    @FXML
    private void standardmesseBearbeiten() {
        StandartMesse s = (StandartMesse) Dialogs.getDialogs().singleSelect(DateienVerwalter.getInstance().getPfarrei().getStandardMessen(), STANDARTMESSE_AUSWAEHLEN);
        if (s != null) {
            smesse.setText(s.tolangerBenutzerfreundlichenString());
            this.standartMesse = s;
        }
    }

}
