package net.aclrian.messdiener.utils;
/**
 * Speicher dat Messverhalten eines Messdieners
 * @author Aclrian
 *
 */
public class Messverhalten {
	private KannWelcheMesse[] messen = new KannWelcheMesse[7];

	public Messverhalten() {
	this.messen[0] = new KannWelcheMesse(EnumdeafaultMesse.Di_abend, false);
		this.messen[1] = new KannWelcheMesse(EnumdeafaultMesse.Sam_abend, false);
		this.messen[2] = new KannWelcheMesse(EnumdeafaultMesse.Sam_hochzeit, false);
		this.messen[3] = new KannWelcheMesse(EnumdeafaultMesse.Son_abend, false);
		this.messen[4] = new KannWelcheMesse(EnumdeafaultMesse.Son_morgen, false);
		this.messen[5] = new KannWelcheMesse(EnumdeafaultMesse.Son_taufe, false);
		}

	/**
	 * Bearbeitet das Messverhalten einer DeafaultMesse
	 * @param messe
	 * @param kann
	 */
	public void editiereBestimmteMesse(EnumdeafaultMesse messe, boolean kann) {
		switch (messe) {
		case Son_taufe:
			this.messen[0].setKanndann(kann);
			break;

		case Di_abend:
			this.messen[1].setKanndann(kann);
			break;

		case Sam_abend:
			this.messen[2].setKanndann(kann);
			break;

		case Son_abend:
			this.messen[3].setKanndann(kann);
			break;

		case Sam_hochzeit:
			this.messen[4].setKanndann(kann);
			break;
		case Son_morgen:
			this.messen[5].setKanndann(kann);
			break;
		case Sonstiges:
			break;
		default:
			break;
		}
	}
/**
 * 
 * @param messe EnumdeafualtMesse
 * @return ob der Messdiener zu der standart Messe kann
 */
	public boolean getBestimmtes(EnumdeafaultMesse messe) {
		boolean rt;

		switch (messe) {
		case Son_taufe:
		rt = this.messen[0].isKanndann();
		break;

		case Di_abend:
			rt = this.messen[1].isKanndann();
			break;

		case Sam_abend:
			rt = this.messen[2].isKanndann();
			break;

		case Son_abend:
			rt = this.messen[3].isKanndann();
			break;

		case Sam_hochzeit:
			rt = this.messen[4].isKanndann();
			break;

		case Son_morgen:
			rt = this.messen[5].isKanndann();
			break;
		default:
			rt = false;
		}

		return rt;
	}
}