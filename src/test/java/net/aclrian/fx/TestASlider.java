package net.aclrian.fx;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TestASlider extends ApplicationTest {

    private final String stringValue = "object2342+3";
    private Slider instance;
    private Pane pane;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testAslider() {
        instance = new Slider(1, 10, 3);
        Platform.runLater(() -> {
            pane.getChildren().add(instance);
            ASlider.makeASlider(stringValue, instance, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.lookup(ASlider.THUMB)).isNotNull();
        Platform.runLater(() -> instance.setValue(instance.getMax()));
        WaitForAsyncUtils.waitForFxEvents();
        Object o = instance.lookup(ASlider.THUMB).lookup("#" + ASlider.SLIDER_VALUE);
        assertThat(o).isInstanceOf(Label.class);
        assertThat(((Label) instance.lookup("#" + ASlider.SLIDER_VALUE)).getText()).contains(stringValue + ": ");
    }

    @Test
    void testASlider2() {
        instance = new Slider(1, 10, 3);
        Platform.runLater(() -> {
            pane.getChildren().add(instance);
            ASlider.makeASlider("", instance, null);
            instance.setValue(1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.lookup(ASlider.THUMB)).isNotNull();
        assertThat(((Label) instance.lookup("#" + ASlider.SLIDER_VALUE)).getText()).isEqualTo("1");
    }

    @Test
    void testEventHandler() {
        instance = new Slider(1, 10, 3);
        Platform.runLater(() -> {
            pane.getChildren().add(instance);
            ASlider.setEventHandler(instance);
            instance.setValue(1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        double firstValue = instance.getValue();
        assertThat(firstValue).isEqualTo(1);
        Platform.runLater(() -> instance.setValue(instance.getMax()));
        WaitForAsyncUtils.waitForFxEvents();
        double secondValue = instance.getValue();

        assertThat(secondValue).isEqualTo(instance.getMax())
                .isNotEqualTo(firstValue);
        Platform.runLater(() -> instance.setValue(1));
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(instance.getValue()).isEqualTo(1);

        ScrollEvent event = new ScrollEvent(ScrollEvent.SCROLL,
                0d, 0d, 0d, 0d,
                false, false, false, false, false, false,
                1d, 1d, 1d, 1d,
                ScrollEvent.HorizontalTextScrollUnits.NONE, 1d,
                ScrollEvent.VerticalTextScrollUnits.NONE, 1d,
                0, null);
        ASlider.scrollValue(instance, event);

        assertThat(instance.getValue()).isEqualTo(2);

        event = new ScrollEvent(ScrollEvent.SCROLL,
                0d, 0d, 0d, 0d,
                false, false, false, false, false, false,
                -1d, -1d, -1d, -1d,
                ScrollEvent.HorizontalTextScrollUnits.NONE, 0d,
                ScrollEvent.VerticalTextScrollUnits.NONE, 0d,
                0, null);
        ASlider.scrollValue(instance, event);
        assertThat(instance.getValue()).isEqualTo(1);
        event = new ScrollEvent(ScrollEvent.SCROLL,
                0d, 0d, 0d, 0d,
                false, false, false, false, false, false,
                -1d, -1d, -1d, -1d,
                ScrollEvent.HorizontalTextScrollUnits.NONE, 0d,
                ScrollEvent.VerticalTextScrollUnits.NONE, 0d,
                0, null);
        ASlider.scrollValue(instance, event);
        assertThat(instance.getValue()).isEqualTo(1);
    }
}
