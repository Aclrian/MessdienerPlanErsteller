package net.aclrian.fx;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

public class TestASlider extends ApplicationTest {

    private final String stringValue = "object2342+3";
    private Slider slider;
    private Pane pane;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 100, 100);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testAslider() {
        slider = new Slider(1,10,3);
        Platform.runLater(() -> {
            pane.getChildren().add(slider);
            ASlider.makeASlider(d -> stringValue, slider, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(slider.lookup(ASlider.THUMB)).isNotNull();
        Platform.runLater(() -> slider.setValue(slider.getMax()));
        WaitForAsyncUtils.waitForFxEvents();
        Object o = slider.lookup(ASlider.THUMB).lookup("#"+ASlider.SLIDER_VALUE);
        Assertions.assertThat(o).isInstanceOf(Label.class);
        Assertions.assertThat(((Label) slider.lookup("#"+ASlider.SLIDER_VALUE)).getText()).contains(stringValue);
    }

    @Test
    public void testEventHandler(){
        slider = new Slider(1,10,3);
        Platform.runLater(() -> {
            pane.getChildren().add(slider);
            ASlider.setEventHandler(slider);
            slider.setValue(1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        double firstValue = slider.getValue();
        Assertions.assertThat(firstValue).isEqualTo(1);
        Platform.runLater(() -> slider.setValue(slider.getMax()));
        WaitForAsyncUtils.waitForFxEvents();
        double secondValue = slider.getValue();
        Assertions.assertThat(secondValue).isEqualTo(slider.getMax())
                .isNotEqualTo(firstValue);
    }

}
