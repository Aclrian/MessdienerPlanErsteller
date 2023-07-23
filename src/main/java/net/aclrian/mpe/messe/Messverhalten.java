package net.aclrian.mpe.messe;

import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.MPELog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Speicher das Messverhalten eines Messdieners
 *
 * @author Aclrian
 */
public class Messverhalten {
    private final ArrayList<KannWelcheMesse> messen = new ArrayList<>();

    public Messverhalten() {
        for (StandardMesse standardMesse : DateienVerwalter.getInstance().getPfarrei().getStandardMessen()) {
            if (!(standardMesse instanceof Sonstiges)) {
                this.messen.add(new KannWelcheMesse(standardMesse, false));
            }
        }
    }

    private Messverhalten(Collection<KannWelcheMesse> kwm) {
        messen = new ArrayList<>(kwm);
    }

    public static Messverhalten convert(Collection<KannWelcheMesse> col) {
        return new Messverhalten(col);
    }

    public List<KannWelcheMesse> getKannWelcheMessen() {
        return messen;
    }

    public void editiereBestimmteMesse(StandardMesse messe, boolean kann) {
        if (messe instanceof Sonstiges || getBestimmtes(messe) == kann) {
            return;
        }
        int gefundenBei;
        for (int i = 0; i < messen.size(); i++) {
            StandardMesse sm = messen.get(i).getMesse();
            if (messe.toString().equals(sm.toString())) {
                gefundenBei = i;
                messen.remove(gefundenBei);
                messen.add(new KannWelcheMesse(messe, kann));
                return;
            }
        }
        MPELog.getLogger().warn("Standardmesse nicht gefunden: {}", messe);
    }

    /**
     * @param messe EnumdeafualtMesse
     * @return ob der Messdiener zu der Standard Messe kann
     */
    public boolean getBestimmtes(StandardMesse messe) {
        if (messe instanceof Sonstiges) {
            return true;
        }
        for (KannWelcheMesse kwm : messen) {
            StandardMesse sm = kwm.getMesse();
            if (sm.toString().equals(messe.toString())) {
                return kwm.kannDann();
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return messen.toString();
    }

    public Messverhalten copy() {
        Messverhalten mv = new Messverhalten();
        for (KannWelcheMesse kwm : messen) {
            if (this.getBestimmtes(kwm.getMesse())) {
                kwm.setKannDann(true);
            }
        }
        return mv;
    }

    /**
     * Klasse für das {@link Messverhalten}
     */
    public static class KannWelcheMesse { //NOPMD - suppressed DataClass
        public static final Comparator<KannWelcheMesse> SORT = (o1, o2) -> {
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
}