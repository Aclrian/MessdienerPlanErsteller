package net.aclrian.messdiener.utils;

/**
 * Enum mit den verschiedenen Kirchen
 * 
 * @author Aclrian
 *
 */
public enum EnumOrt {
	St_Martinus("St. Martinus"), Alt_st_martin("Alt St. Martin"), Friedhof("Friedhof") , Sonstiges("");

	private String Ort;

	EnumOrt(String Ort) {
		setOrt(Ort);
	}

	public String getOrt() {
		return this.Ort;
	}

	private void setOrt(String ort) {
		this.Ort = ort;
	}
}
