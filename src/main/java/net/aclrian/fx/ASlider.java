package net.aclrian.fx;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class ASlider {
	public static Label makeASlider(String value, Slider s) {
		s.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent e) {
				double delta = s.getOrientation().equals(Orientation.HORIZONTAL) ? e.getDeltaY() : e.getDeltaX();
				if (delta < 0) {
					double i = s.getValue() - 1;
					if (i > s.getMax())
						i = s.getMax();
					if (i < s.getMin())
						i = s.getMin();
					s.setValue(i);
				}
				if (delta > 0) {
					double i = s.getValue() + 1;
					if (i > s.getMax())
						i = s.getMax();
					if (i < s.getMin())
						i = s.getMin();
					s.setValue(i);
				}
			}
		});
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