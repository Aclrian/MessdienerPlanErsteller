package net.aclrian.mpe.messe;

import net.aclrian.mpe.messdiener.KannWelcheMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Speicher das Messverhalten eines Messdieners
 *
 * @author Aclrian
 */
public class Messverhalten {
    private ArrayList<KannWelcheMesse> messen = new ArrayList<>();

    public Messverhalten() {
        for (StandartMesse standartMesse : DateienVerwalter.getDateienVerwalter().getPfarrei().getStandardMessen()) {
            if (!(standartMesse instanceof Sonstiges)) {
                this.messen.add(new KannWelcheMesse(standartMesse, false));
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

    public void editiereBestimmteMesse(StandartMesse messe, boolean kann) {
        if (messe instanceof Sonstiges || getBestimmtes(messe) == kann) {
            return;
        }
        int gefundenbei;
        for (int i = 0; i < messen.size(); i++) {
            StandartMesse sm = messen.get(i).getMesse();
            if (messe.toString().equals(sm.toString())) {
                gefundenbei = i;
                messen.remove(gefundenbei);
                messen.add(new KannWelcheMesse(messe, kann));
                return;
            }
        }
        Log.getLogger().warn("Standartmesse nicht gefunden:" + messe);
    }

    /**
     * @param messe EnumdeafualtMesse
     * @return ob der Messdiener zu der standart Messe kann
     */
    public boolean getBestimmtes(StandartMesse messe) {
        if (messe instanceof Sonstiges) {
            return true;
        }
        for (KannWelcheMesse kwm : messen) {
            StandartMesse sm = kwm.getMesse();
            if (sm.toString().equals(messe.toString())) {
                return kwm.isKanndann();
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
                kwm.setKanndann(true);
            }
        }
        return mv;
    }
}