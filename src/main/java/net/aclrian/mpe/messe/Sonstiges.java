package net.aclrian.mpe.messe;

public class Sonstiges extends StandartMesse {

	public static final String SONSTIGES_STRING = "Sonstiges";

	public Sonstiges() {
		super("", -1, "", "", -1, "");
	}

	@Override
	public String toString() {
		return SONSTIGES_STRING;
	}

	@Override
	public String tolangerBenutzerfreundlichenString() {
		return SONSTIGES_STRING;
	}

	@Override
	public String tokurzerBenutzerfreundlichenString() {
		return SONSTIGES_STRING;
	}
}
