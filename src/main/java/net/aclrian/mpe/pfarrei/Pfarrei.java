package net.aclrian.mpe.pfarrei;

import java.util.ArrayList;

import net.aclrian.mpe.messe.StandartMesse;

public class Pfarrei {
	private String name;
	private ArrayList<StandartMesse> sm;
	private Einstellungen settings;
	private boolean hochaemter;

	public Pfarrei(Einstellungen settings, ArrayList<StandartMesse> sm, String name, boolean hochaemterzaelenmit) {
		this.settings = settings;
		this.sm = sm;
		this.name = name;
		this.hochaemter = hochaemterzaelenmit;
	}

	public ArrayList<StandartMesse> getStandardMessen() {
		return sm;
	}

	public String getName() {
		return name;
	}public Einstellungen getSettings() {
		return settings;
	}

	public boolean zaehlenHochaemterMit() {
		return hochaemter;
	}

}