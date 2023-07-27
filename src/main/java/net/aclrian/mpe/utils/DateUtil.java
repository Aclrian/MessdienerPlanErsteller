package net.aclrian.mpe.utils;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyy");
    /**
     * Format: "eee" or TextStyle.SHORT: Mo. in german Locale
     * "ccc" or SHORT_STANDALONE: Mo in german Locale
     */
    public static final DateTimeFormatter SHORT_STANDALONE = DateTimeFormatter.ofPattern("ccc", Locale.getDefault());
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_SHORT = DateTimeFormatter.ofPattern("dd. MMMM");
    public static final DateTimeFormatter DAY_OF_WEEK_LONG = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault());
    public static final DateTimeFormatter DATE_AND_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private DateUtil() {
    }

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static LocalDate getTomorrow() {
        return LocalDate.now().plusDays(1);
    }

    public static LocalDate getYesterday() {
        return LocalDate.now().plusDays(-1);
    }

    public static LocalDate getYesterdaysYesterday() {
        return LocalDate.now().plusDays(-2);
    }

    public static LocalDate getNextDay(LocalDate date) {
        return date.plusDays(1);
    }

    public static LocalDate getPreviousDay(LocalDate date) {
        return date.plusDays(-1);
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static int getYearCap() {
        return DateUtil.getCurrentYear() - 18;
    }
}
