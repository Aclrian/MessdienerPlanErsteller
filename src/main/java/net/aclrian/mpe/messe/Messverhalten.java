package net.aclrian.mpe.messe;

import java.util.ArrayList;
import java.util.Collection;

import net.aclrian.mpe.messdiener.KannWelcheMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Log;

/**
 * Speicher das Messverhalten eines Messdieners
 * 
 * @author Aclrian
 *
 */
public class Messverhalten {
	private ArrayList<KannWelcheMesse> messen = new ArrayList<>();

	public Messverhalten() {
			for (StandartMesse standartMesse : DateienVerwalter.dv.getPfarrei().getStandardMessen()) {
				if (!(standartMesse instanceof Sonstiges)) {
					this.messen.add(new KannWelcheMesse(standartMesse, false));
				}
			}
	}
	
	private Messverhalten(Collection<KannWelcheMesse> kwm) {
		messen = new ArrayList<>(kwm);
	}
	
	public ArrayList<KannWelcheMesse> getKannWelcheMessen() {
		return messen;
	}

	public void editiereBestimmteMesse(StandartMesse messe, boolean kann) {
		if (messe instanceof Sonstiges|| getBestimmtes(messe) == kann) {
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
		Log.getLogger().warn("Standartmesse nicht gefunden:"+messe);
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

	public void fuegeneueMesseHinzu(StandartMesse sm) {
		messen.add(new KannWelcheMesse(sm, false));
	}

	public static class NotFoundException extends Exception {

		private StandartMesse messe;

		public NotFoundException(StandartMesse messe) {
			this.messe = messe;
		}
		
		public StandartMesse getMesse() {
			return messe;
		}

	}

	public static Messverhalten convert(Collection<KannWelcheMesse> col) {
		return new Messverhalten(col);
	}
}