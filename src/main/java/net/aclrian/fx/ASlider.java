package net.aclrian.fx;


import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;


public class ASlider {
	public static Label makeASlider(String value, Slider s) {
		s.applyCss();// Entfernt Error
		Pane p = (Pane) s.lookup(".thumb");
		Label l = new Label();
		s.valueProperty().addListener((observableValue, number, t1) -> {
			String as = value + (int) s.getValue();
			l.setText(as);
		});

		p.getChildren().add(l);
		return l;
	}
}