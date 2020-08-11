package net.aclrian.mpe.pfarrei;

import net.aclrian.mpe.messdiener.Messdaten;

public class Setting {

	public enum Attribut{
		YEAR,
		MAX
	}
	private final Attribut attribut;
	private final int id;
	private final int anzDienen;
	public Setting(Attribut attribut, int id, int anz) {
		this.attribut = attribut;
		this.id = id;
		this.anzDienen = anz;
	}
	
	public Attribut getAttribut() {
		return attribut;
	}
	
	public int getId() {
		return id;
	}
	
	public Integer getJahr() {
		return Messdaten.getMaxYear()-id;
	}
	
	public int getAnzDienen() {
		return anzDienen;
	}

	@Override
	public String toString() {
		return attribut.name() + " " + id + " " + anzDienen;
	}

}