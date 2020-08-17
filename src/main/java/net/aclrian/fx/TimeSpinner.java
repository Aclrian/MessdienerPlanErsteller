package net.aclrian.fx;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//exists because Spinner(@NamedArg("min") LocalTime min, @NamedArg("max") LocalTime max, @NamedArg("initialValue") LocalTime initialValue) is package private
//StringConverter content from: https://stackoverflow.com/a/32617768/8145512 from James_D
public class TimeSpinner extends Spinner<LocalTime> {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

    public TimeSpinner() {
        super();
        setEditable(true);

        StringConverter<LocalTime> converter = new StringConverter<>() {

            @Override
            public String toString(LocalTime time) {
                return dtf.format(time);
            }

            @Override
            public LocalTime fromString(String string) {
                String[] tokens = string.split(":");
                int hours = getIntField(tokens, 0);
                int minutes = getIntField(tokens, 1);
                int totalSeconds = (hours * 60 + minutes) * 60;
                return LocalTime.of((totalSeconds / 3600) % 24, (totalSeconds / 60) % 60);
            }

            private int getIntField(String[] tokens, int index) {
                if (tokens.length <= index || tokens[index].isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(tokens[index]);
            }
        };

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
