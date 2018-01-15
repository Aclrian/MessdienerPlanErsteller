package net.aclrian.messdiener.utils;
/**
 * Klasse für das {@link Messverhalten}
 * @author Aclrian
 *
 */
public class KannWelcheMesse {
	private EnumdeafaultMesse messe;
	private boolean kanndann;

	public EnumdeafaultMesse getMesse() {
		return this.messe;
	}

	public void setMesse(EnumdeafaultMesse messe) {
		this.messe = messe;
	}

	public boolean isKanndann() {
		return this.kanndann;
	}

	public void setKanndann(boolean kanndann) {
		this.kanndann = kanndann;
	}

	public KannWelcheMesse(EnumdeafaultMesse messe, boolean kann) {
		setMesse(messe);
		setKanndann(kann);
	}
}
