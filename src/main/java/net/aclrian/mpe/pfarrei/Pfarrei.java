package net.aclrian.mpe.pfarrei;

import java.util.ArrayList;

import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.RemoveDoppelte;

public class Pfarrei {
    private String name;
    private ArrayList<StandartMesse> sm;
    private ArrayList<String> orte;
    private ArrayList<String> typen;
    private Einstellungen settings;
    private boolean hochaemter;

    public Pfarrei(Einstellungen settings, ArrayList<StandartMesse> sm, String name, ArrayList<String> orte,
	    ArrayList<String> typen, boolean hochaemterzaelenmit) {
	this.settings = settings;
	RemoveDoppelte<String> rf = new RemoveDoppelte<>();
	rf.removeDuplicatedEntries(typen);
	rf.removeDuplicatedEntries(orte);
	this.sm = sm;
	this.name = name;
	this.typen = typen;
	this.orte = orte;
	this.hochaemter = hochaemterzaelenmit;
    }

    public ArrayList<StandartMesse> getStandardMessen() {
	return sm;
    }

    public String getName() {
	return name;
    }

    public ArrayList<String> getOrte() {
	return orte;
    }

    public ArrayList<String> getTypen() {
	return typen;
    }

    public Einstellungen getSettings() {
	return settings;
    }

    public boolean zaehlenHochaemterMit() {
	return hochaemter;
    }

}