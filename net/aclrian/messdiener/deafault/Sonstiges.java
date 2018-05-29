package net.aclrian.messdiener.deafault;

public class Sonstiges extends StandartMesse {

	public Sonstiges() {
		super("", -1, "", "", -1, "");
	}
	
	
	public boolean isSonstiges(StandartMesse sm) {
		return this.toString().equals(sm.toString());
	}
}
