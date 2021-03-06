package net.aclrian.fx;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//exists because Spinner(@NamedArg("min") LocalTime min, @NamedArg("max") LocalTime max, @NamedArg("initialValue") LocalTime initialValue) is package private
public class TimeSpinner extends Spinner<LocalTime> {

    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    public static final StringConverter<LocalTime> converter = new StringConverter<>() {

        @Override
        public String toString(LocalTime time) {
            return dtf.format(time);
        }

        @Override
        public LocalTime fromString(String string) {
            String[] split = string.split(":");
            Integer[] time = new Integer[2];
            for (int i = 0; i < time.length; i++) {
                if (split.length > i && !split[i].isEmpty()) {
                    time[i] = Integer.parseInt(split[i]);
                } else {
                    time[i] = 0;
                }
            }
            return LocalTime.of(time[0], time[1]);
        }
    };

    public TimeSpinner() {
        super();
        setEditable(true);
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<>() {

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 15L));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 15L));
            }
        };

        valueFactory.setConverter(converter);
        this.setValueFactory(valueFactory);
    }
}
