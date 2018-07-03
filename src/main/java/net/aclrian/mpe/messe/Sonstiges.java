package net.aclrian.mpe.messe;

public class Sonstiges extends StandartMesse {

	public Sonstiges() {
		super("", -1, "", "", -1, "");
	}
	
	public boolean isSonstiges(StandartMesse sm) {
		if (sm instanceof Sonstiges) {
			return true;
		}
		return false;
	}
}
