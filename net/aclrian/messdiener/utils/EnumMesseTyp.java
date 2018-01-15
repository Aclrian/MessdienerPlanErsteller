package net.aclrian.messdiener.utils;

public enum EnumMesseTyp {
Taufe("Taufe"),
Messe("Hl. Messe"),
Hochzeit("Hochzeit");
	private String type;
	private EnumMesseTyp(String typ) {
		this.set(typ);
	}
	public String getType() {
		return type;
	}
	private void set(String type) {
		this.type = type;
	}
}
