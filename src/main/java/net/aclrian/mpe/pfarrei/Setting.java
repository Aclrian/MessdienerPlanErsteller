package net.aclrian.mpe.pfarrei;

public class Setting {

	public enum Attribut{
		year,
		max;
	}
	private Attribut a;
	private int id;
	private int anz_dienen;
	public Setting(Attribut a, int id, int anz) {
		this.a = a;
		this.id = id;
		this.anz_dienen = anz;
	}
	
	public Attribut getA() {
		return a;
	}
	
	public int getId() {
		return id;
	}
	
	public int getAnz_dienen() {
		return anz_dienen;
	}
	@Override
	public String toString() {
		return a.name() + " " + id + " " + anz_dienen;
	}

}