package net.aclrian.messdiener.differenzierung;

import java.util.ArrayList;
import java.util.HashSet;

import net.aclrian.messdiener.deafault.StandartMesse;

public class Pfarrei {
	private String name;
	private ArrayList<StandartMesse> sm;
	private ArrayList<String> orte;
	private ArrayList<String> typen;
	private Einstellungen settings;
	private boolean hochaemter;

	public Pfarrei(Einstellungen settings,ArrayList<StandartMesse> sm, String name,ArrayList<String> orte,ArrayList<String> typen, boolean hochaemterzaelenmit) {
		this.settings = settings;
		removeDuplicatedEntries(typen);
		removeDuplicatedEntries(orte);
		this.sm = sm;
		this.name = name;
		this.typen = typen;
		this.orte = orte;
		this.hochaemter = hochaemterzaelenmit;
	}
	 public void removeDuplicatedEntries(ArrayList<String> arrayList) {
		    HashSet<String> hashSet = new HashSet<String>(arrayList);
		    arrayList.clear();
		    arrayList.addAll(hashSet);
		  }
	
	public ArrayList<StandartMesse> getStandardMessen() {
		return sm;
	}

	public String getName() {
		return name;
	}
	public ArrayList<String> getOrte(){
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