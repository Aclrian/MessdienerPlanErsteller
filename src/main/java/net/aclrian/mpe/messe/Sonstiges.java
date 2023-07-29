package net.aclrian.mpe.messe;


import java.time.DayOfWeek;

public class Sonstiges extends StandardMesse {

    public static final String SONSTIGES_STRING = "Sonstiges";

    public Sonstiges() {
        super(DayOfWeek.SUNDAY, -1, "", "", -1, "");
    }

    @Override
    public String toString() {
        return SONSTIGES_STRING;
    }

    @Override
    public String toLangerBenutzerfreundlichenString() {
        return SONSTIGES_STRING;
    }

    @Override
    public String toKurzerBenutzerfreundlichenString() {
        return SONSTIGES_STRING;
    }
}
