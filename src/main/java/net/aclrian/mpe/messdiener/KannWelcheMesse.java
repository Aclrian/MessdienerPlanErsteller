package net.aclrian.mpe.messdiener;

import net.aclrian.mpe.messe.*;

import java.util.*;

/**
 * Klasse fuer das {@link Messverhalten}
 *
 * @author Aclrian
 */
public class KannWelcheMesse {
    public static final Comparator<KannWelcheMesse> sort = (o1, o2) -> {
        StandardMesse sm1 = o1.messe;
        StandardMesse sm2 = o2.messe;
        return sm1.toString().compareToIgnoreCase(sm2.toString());
    };
    private StandardMesse messe;
    private boolean kannDann;

    public KannWelcheMesse(StandardMesse messe, boolean kann) {
        setMesse(messe);
        setKannDann(kann);
    }

    public StandardMesse getMesse() {
        return this.messe;
    }

    public void setMesse(StandardMesse messe) {
        this.messe = messe;
    }

    public boolean kannDann() {
        return kannDann;
    }

    public void setKannDann(boolean kannDann) {
        this.kannDann = kannDann;
    }

    @Override
    public String toString() {
        return messe.toString() + ":" + kannDann;
    }

    public String getString() {
        return messe.toKurzerBenutzerfreundlichenString();
    }
}