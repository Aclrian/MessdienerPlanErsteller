package net.aclrian.fx;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.DateTimeException;
import java.time.LocalTime;

public class TestTimeSpinner extends ApplicationTest {

    private Pane pane;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testConvertion() {
        LocalTime time = LocalTime.of(12, 59);
        Assertions.assertThat(TimeSpinner.converter.toString(time)).isEqualTo("12:59");
        Assertions.assertThat(TimeSpinner.converter.toString(LocalTime.of(0, 0, 0))).isEqualTo("00:00");
    }

    @Test(expected = DateTimeException.class)
    public void testInitTime() {
        LocalTime time = LocalTime.of(25, 60);
        if (time != null) {
            Assertions.fail("25 o'clock exists?");
        }
    }

    @Test
    public void testFromString() {
        Assertions.assertThat(TimeSpinner.converter.fromString("").format(TimeSpinner.dtf)).isEqualTo("00:00");
        Assertions.assertThat(TimeSpinner.converter.fromString("13:50").format(TimeSpinner.dtf)).isEqualTo("13:50");
    }

    @Test
    public void testDeInkrement() {
        TimeSpinner spinner = new TimeSpinner();
        spinner.getValueFactory().setValue(LocalTime.of(12, 0));
        spinner.increment(2);
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(TimeSpinner.dtf.format(spinner.getValue())).isEqualTo("12:30");
        spinner.decrement();
        Assertions.assertThat(TimeSpinner.dtf.format(spinner.getValue())).isEqualTo("12:15");
    }


    @Test
    public void testConverter() {
        TimeSpinner spinner = new TimeSpinner();
        Assertions.assertThat(spinner.getValueFactory().getConverter()).isSameAs(TimeSpinner.converter);
    }

}