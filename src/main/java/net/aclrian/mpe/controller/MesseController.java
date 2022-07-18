package net.aclrian.mpe.controller;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import net.aclrian.fx.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messe.*;
import net.aclrian.mpe.utils.*;

import java.time.*;

import static net.aclrian.mpe.utils.Log.*;

public class MesseController implements Controller {
    public static final String STANDARDMESSE_AUSWAEHLEN = "Standardmesse ändern:";
    private boolean locked = true;
    private Messe moben = null;
    private StandardMesse standardMesse = null;

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
    public void afterStartup(Window window, MainController mc) {
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
                Dialogs.getDialogs().warn("Bitte einen Titel, Ort, Datum und Uhrzeit eingeben.");
            }
        });
        button.setOnAction(e -> {
            if (saveMesse(mc)) {
                locked = false;
                mc.changePane(MainController.EnumPane.SELECT_MESSE);
            } else {
                Dialogs.getDialogs().warn("Bitte einen Titel, Ort, Datum und Uhrzeit eingeben.");
            }
        });
        uhr.getValueFactory().setValue(null);
    }

    private boolean saveMesse(MainController mc) {
        if (datum.getValue() != null && uhr.getValue() != null && !ort.getText().equals("") && !titel.getText().equals("")) {
            Messe m = new Messe(hochamt.isSelected(), (int) slider.getValue(), getDate(), ort.getText(), titel.getText(), standardMesse);
            for (Messdiener medi : list.getSelected()) {
                if (!m.vorzeitigEinteilen(medi)) {
                    Dialogs.getDialogs().warn(medi + "{} konnte nicht vorzeitig eingeteilt werden.");
                }
            }
            mc.getMessen().add(m);
            mc.getMessen().sort(Messe.MESSE_COMPARATOR);
            return true;
        }
        return false;
    }

    @Override
    public void initialize() {
        standardMesse = new Sonstiges();
        smesse.setText(standardMesse.toLangerBenutzerfreundlichenString());
        smesse.setDisable(true);
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    public void setMesse(Messe messe) {
        if (messe == null) return;
        standardMesse = messe.getStandardMesse();
        smesse.setText(messe.getStandardMesse().toLangerBenutzerfreundlichenString());
        moben = messe;
        titel.setText(messe.getMesseTyp());
        ort.setText(messe.getKirche());
        slider.setValue(messe.getAnzMessdiener());
        hochamt.setSelected(messe.isHochamt());
        list.setSelected(messe.getEingeteilte());
        LocalDate ld = messe.getDate().toLocalDate();
        datum.setValue(ld);
        uhr.getValueFactory().setValue(messe.getDate().toLocalTime());
        list.setSelected(messe.getEingeteilte());
        getLogger().info("Messe wurde geladen");
    }

    private LocalDateTime getDate() {
        return LocalDateTime.of(datum.getValue(), uhr.getValue());
    }

    @FXML
    private void standardmesseBearbeiten() {
        StandardMesse s = (StandardMesse) Dialogs.getDialogs().singleSelect(DateienVerwalter.getInstance().getPfarrei().getStandardMessen(), STANDARDMESSE_AUSWAEHLEN);
        if (s != null) {
            smesse.setText(s.toLangerBenutzerfreundlichenString());
            this.standardMesse = s;
        }
    }
}
