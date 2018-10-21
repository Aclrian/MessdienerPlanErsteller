package net.aclrian.mpe.messe;

import java.util.ArrayList;

import net.aclrian.mpe.messdiener.KannWelcheMesse;
import net.aclrian.mpe.start.AData;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.Utilities;

/**
 * Speicher das Messverhalten eines Messdieners
 * 
 * @author Aclrian
 *
 */
public class Messverhalten {
    private ArrayList<KannWelcheMesse> messen = new ArrayList<KannWelcheMesse>();
    private int i = -1;

    public Messverhalten(AData ada) {
	try {
	    if (ada != null) {
		for (StandartMesse standartMesse : ada.getPfarrei().getStandardMessen()) {
		    if (!(standartMesse instanceof Sonstiges)) {
			this.messen.add(new KannWelcheMesse(standartMesse, false));
		    }
		}
		update(ada);
	    }
	} catch (NullPointerException e) {
	    new Erroropener(e);
	    e.printStackTrace();
	}
    }

    /**
     * Bearbeitet das Messverhalten einer DeafaultMesse
     * @param messe
     * @param kann
     */
    public void editiereBestimmteMesse(AData ada, StandartMesse messe, boolean kann) {
	if (AData.sonstiges.isSonstiges(messe) || getBestimmtes(messe, ada) == kann) {
	    return;
	}
	boolean b = false;
	int gefundenbei = -1;
	for (int i = 0; i < messen.size(); i++) {
	    StandartMesse sm = messen.get(i).getMesse();
	    if (messe.toString().equals(sm.toString())) {
		b = true;
		gefundenbei = i;
		break;
	    }
	}
	if (b) {
	    messen.remove(gefundenbei);
	    messen.add(new KannWelcheMesse(messe, kann));
	} else {
	    new Erroropener(new Exception("Standardmesse (" + messe.toBenutzerfreundlichenString() + ") existiert nicht!"));
	}
    }

    public void ueberschreibeStandartMesse(StandartMesse alt, StandartMesse neu) {
	for (KannWelcheMesse kannWelcheMesse : messen) {
	    if (kannWelcheMesse.getMesse().toString().equals(alt.toString())) {
		kannWelcheMesse.setMesse(neu);
	    }
	}
    }

    /**
     * 
     * @param messe
     *            EnumdeafualtMesse
     * @return ob der Messdiener zu der standart Messe kann
     */
    public boolean getBestimmtes(StandartMesse messe, AData ada) {
	if (AData.sonstiges.isSonstiges(messe)) {
	    return true;
	}
	boolean rt = false;
	for (KannWelcheMesse kwm : messen) {
	    StandartMesse sm = kwm.getMesse();
	    if (sm.toString().equals(messe.toString())) {
		return kwm.isKanndann();
	    }
	}
	new Erroropener(new Exception("Standardmesse (" + messe.toBenutzerfreundlichenString() + ") existiert nicht!"));
	update(ada);
	return rt;
    }

    public void update(AData ada) {
	for (int in = 0; in < messen.size(); in++) {
	    if (AData.sonstiges.isSonstiges(messen.get(in).getMesse())) {
		if (i == 0) {
		    i = in;
		} else {
		    messen.remove(in);
		}
	    }
	}
	if (istAnders(ada)) {
	    Utilities.logging(this.getClass(), "update", "Standartmessen habe sich geändert");
	    ArrayList<KannWelcheMesse> bleiben = new ArrayList<KannWelcheMesse>();
	    for (StandartMesse sm : ada.getPfarrei().getStandardMessen()) {
		boolean b = false;
		for (KannWelcheMesse kwm : messen) {
		    StandartMesse sm_old = kwm.getMesse();
		    if (sm.toString().equals(sm_old.toString())) {
			bleiben.add(kwm);
			b = true;
		    }
		}
		if (b == false) {
		    bleiben.add(new KannWelcheMesse(sm, false));
		}
	    }
	    messen = bleiben;
	}
    }

    private boolean istAnders(AData ada) {
	ArrayList<StandartMesse> smold = new ArrayList<StandartMesse>();
	for (KannWelcheMesse kwm : messen) {
	    if (!(kwm.getMesse() instanceof Sonstiges))
		smold.add(kwm.getMesse());
	}
	StandartMesse[] smold2 = new StandartMesse[smold.size()];
	smold.sort(StandartMesse.comfuerSMs);
	smold.toArray(smold2);
	ArrayList<StandartMesse> smwmf = ada.getSMoheSonstiges();
	StandartMesse[] smwmf2 = new StandartMesse[smwmf.size()];
	smwmf.toArray(smwmf2);
	if (smold2.toString().equals(smwmf2.toString())) {
	    return false;
	}
	if (smold.size() != smwmf.size()) {
	    System.out.println("sm ungleich");
	    return true;
	}
	for (StandartMesse sm : smold) {
	    boolean istenthalten = false;
	    for (StandartMesse standartMesse : smwmf) {
		if (sm.toString().equals(standartMesse.toString())) {
		    istenthalten = true;
		    break;
		}
	    }
	    if (!istenthalten) {
		return true;
	    }
	}
	for (StandartMesse standartMesse : smwmf) {
	    boolean istenthalten2 = false;
	    for (StandartMesse sm : smold) {
		if (sm.toString().equals(standartMesse.toString())) {
		    istenthalten2 = true;
		    break;
		}
	    }
	    if (!istenthalten2) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public String toString() {
	return messen.toString();
    }

    public Object[][] getData(AData ada) {
	update(ada);
	messen.sort(KannWelcheMesse.sort);
	Object[][] rtn = new Object[messen.size()][2];
	for (int i = 0; i < messen.size(); i++) {
	    KannWelcheMesse sm = messen.get(i);
	    rtn[i][0] = sm.getMesse().toBenutzerfreundlichenString();
	    rtn[i][1] = sm.isKanndann();
	}
	return rtn;
    }

    public void fuegeneueMesseHinzu(StandartMesse sm) {
	messen.add(new KannWelcheMesse(sm, false));
    }

}