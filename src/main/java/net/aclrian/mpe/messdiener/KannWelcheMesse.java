package net.aclrian.mpe.messdiener;

import java.util.ArrayList;
import java.util.Comparator;

import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.messe.StandartMesse;

/**
 * Klasse fuer das {@link Messverhalten}
 * 
 * @author Aclrian
 *
 */
public class KannWelcheMesse {
	public static final Comparator<KannWelcheMesse> sort = new Comparator<KannWelcheMesse>() {

		@Override
		public int compare(KannWelcheMesse o1, KannWelcheMesse o2) {
			StandartMesse sm1 = o1.messe;
			StandartMesse sm2 = o2.messe;
			return sm1.toString().compareToIgnoreCase(sm2.toString());
		}

	};
	private StandartMesse messe;
	private boolean kanndann;

	public StandartMesse getMesse() {
		return this.messe;
	}

	public void setMesse(StandartMesse messe) {
		this.messe = messe;
	}

	public boolean isKanndann() {
		return kanndann;
	}

	public void setKanndann(boolean kanndann) {
		this.kanndann = kanndann;
	}

	public KannWelcheMesse(StandartMesse messe, boolean kann) {
		setMesse(messe);
		setKanndann(kann);
	}

	@Override
	public String toString() {
		return messe.toString() + ":" + kanndann;
	}

	public String getString() {
		return messe.tokurzerBenutzerfreundlichenString();
	}
}