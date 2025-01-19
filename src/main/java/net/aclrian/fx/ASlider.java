package net.aclrian.fx;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

/**
 * Die Klasse ist ein spezieller Schieberegler, der etwas größer ist.
 */
public class ASlider {
    public static final String THUMB = ".thumb";
    public static final String SLIDER_VALUE = "sliderValue";
    public static void setEventHandler(Slider s) {
        s.setOnScroll(e -> scrollValue(s, e));
    }

    /**
     * Privater Konstruktor, damit auf diesem Weg keine Instanz der Klasse erzeugt
     * werden kann und nur über die Funktion makeASlider()
     */
    private ASlider() {

    }

    /**
     * Berechnet den nächsten Wert beim Scrollen des Schiebereglers
     * @param s Der Scheiberegler der gescrollt wird
     * @param e Das ScrollEvent
     */
    public static void scrollValue(Slider s, javafx.scene.input.ScrollEvent e) {
        double delta = s.getOrientation().equals(Orientation.HORIZONTAL) ? e.getDeltaY() : e.getDeltaX();
        int amount = 0;
        if (delta < 0) {
            amount = -1;
        } else if (delta > 0) {
            amount = 1;
        }
        s.adjustValue(s.getValue() + amount);
    }

    /**
     * Erzeugt eine Instanz von ASlider
     * @param value => Name des Schiebereglers
     * @param s Slider der 
     * @param tooltip
     */
    public static void makeASlider(String value, Slider s, Tooltip tooltip) {
        makeASlider(d -> {
            if (value.isEmpty()) {
                return String.valueOf((int) d);
            }
            return value + ": " + (int) d;
        }, s, tooltip);
    }

    public static void makeASlider(IFormatter i, Slider s, Tooltip tooltip) {
        setEventHandler(s);
        s.applyCss();
        Pane p = (Pane) s.lookup(THUMB);
        Label l = new Label();
        l.setId(SLIDER_VALUE);
        s.valueProperty().addListener((observableValue, number, t1) -> {
            String as = i.getString(s.getValue());
            l.setText(as);
        });
        p.getChildren().add(l);
        if (tooltip != null) {
            l.setTooltip(tooltip);
        }
    }

    public interface IFormatter {
        String getString(double d);
    }
}
