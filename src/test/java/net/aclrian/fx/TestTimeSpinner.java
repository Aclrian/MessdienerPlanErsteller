package net.aclrian.fx;

import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.aclrian.mpe.utils.*;
import org.junit.*;
import org.testfx.assertions.api.*;
import org.testfx.framework.junit.*;
import org.testfx.util.*;

import java.time.*;

public class TestTimeSpinner extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testConversion() {
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
        Assertions.assertThat(TimeSpinner.converter.fromString("").format(DateUtil.TIME)).isEqualTo("00:00");
        Assertions.assertThat(TimeSpinner.converter.fromString("13:50").format(DateUtil.TIME)).isEqualTo("13:50");
    }

    @Test
    public void testDeInkrement() {
        TimeSpinner spinner = new TimeSpinner();
        spinner.getValueFactory().setValue(LocalTime.of(12, 0));
        spinner.increment(2);
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(DateUtil.TIME.format(spinner.getValue())).isEqualTo("12:30");
        spinner.decrement();
        Assertions.assertThat(DateUtil.TIME.format(spinner.getValue())).isEqualTo("12:15");
    }


    @Test
    public void testConverter() {
        TimeSpinner spinner = new TimeSpinner();
        Assertions.assertThat(spinner.getValueFactory().getConverter()).isSameAs(TimeSpinner.converter);
    }

}