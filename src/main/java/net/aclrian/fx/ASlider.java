package net.aclrian.fx;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

public class ASlider {

    public static final String THUMB = ".thumb";
    public static final String SLIDER_VALUE = "sliderValue";

    static void setEventHandler(Slider s) {
        s.setOnScroll(e -> {
            double delta = s.getOrientation().equals(Orientation.HORIZONTAL) ? e.getDeltaY() : e.getDeltaX();
            if (delta <= 0) {
                double i1 = s.getValue() - 1;
                if (i1 >= s.getMax()) {
                    i1 = s.getMax();
                } else {
                    i1 = s.getMin();
                }
                s.setValue(i1);
            } else {
                double i1 = s.getValue() + 1;
                if (i1 >= s.getMax()) {
                    i1 = s.getMax();
                } else {
                    i1 = s.getMin();
                }
                s.setValue(i1);
            }
        });
    }

    public static void makeASlider(String value, Slider s, Tooltip tooltip) {
        makeASlider(d -> {
            if (value.isEmpty()) {
                return String.valueOf((int) d);
            }
            return (value + ": " + (int) d);
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
        if (tooltip != null) l.setTooltip(tooltip);
    }

    public interface IFormatter {
        String getString(double d);
    }
}