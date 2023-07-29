package net.aclrian.fx;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.mpe.utils.DateUtil;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(TimeSpinner.CONVERTER.toString(time)).isEqualTo("12:59");
        assertThat(TimeSpinner.CONVERTER.toString(LocalTime.of(0, 0, 0))).isEqualTo("00:00");
    }

    @Test
    public void testInitTime() {
        assertThrows(DateTimeException.class, () -> LocalTime.of(25, 60));
    }

    @Test
    public void testFromString() {
        assertThat(TimeSpinner.CONVERTER.fromString("").format(DateUtil.TIME)).isEqualTo("00:00");
        assertThat(TimeSpinner.CONVERTER.fromString("13:50").format(DateUtil.TIME)).isEqualTo("13:50");
    }

    @Test
    public void testDeInkrement() {
        TimeSpinner spinner = new TimeSpinner();
        spinner.getValueFactory().setValue(LocalTime.of(12, 0));
        spinner.increment(2);
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(DateUtil.TIME.format(spinner.getValue())).isEqualTo("12:30");
        spinner.decrement();
        assertThat(DateUtil.TIME.format(spinner.getValue())).isEqualTo("12:15");
    }


    @Test
    public void testConverter() {
        TimeSpinner spinner = new TimeSpinner();
        assertThat(spinner.getValueFactory().getConverter()).isSameAs(TimeSpinner.CONVERTER);
    }

}
