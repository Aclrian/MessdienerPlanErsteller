package net.aclrian.messdiener.differenzierung;

import java.util.ArrayList;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.window.WMainFrame;

public class Manuell {
	public static String sm = "St. Martinus";
	public static String hlm = "Hl. Messe";
	public static String zh = "Hochzeit";
	public static String tf = "Taufe";
	public static StandartMesse Di_abend = new StandartMesse("Di", 19, "00", "Alt St. Martin", 2, hlm);
	public static StandartMesse Sam_hochzeit = new StandartMesse("Sa", 14, "00", sm, 2, zh);
	public static StandartMesse Sam_abend = new StandartMesse("Sa", 18, "30", sm, 6, hlm);
	public static StandartMesse Son_morgen = new StandartMesse("So", 10, "00", sm, 6, hlm);
	public static StandartMesse Son_taufe = new StandartMesse("So", 15, "00", sm, 2, tf);
	public static StandartMesse Son_abend = new StandartMesse("So", 18, "00", sm, 6, hlm);

	public static void main(String[] args) {

	}

	public static void updateXMLFiles() {
		System.out.println("s");
		System.out.println(Di_abend);
		System.out.println(Sam_hochzeit);
		System.out.println(Sam_abend);
		System.out.println(Son_morgen);
		System.out.println(Son_taufe);
		System.out.println(Son_abend);
		System.out.println("d");
		WMainFrame wmf = new WMainFrame();// false);
		System.out.println(wmf.getStandardmessen().size());
		for (StandartMesse sm : wmf.getStandardmessen()) {
			System.out.println(sm);
		}
		wmf.setVisible(false);
		ArrayList<Messdiener> me = new ArrayList<Messdiener>();
		for (Messdiener messdiener : wmf.getMediarray()) {
			System.out.println(messdiener.makeId());
			messdiener.setnewMessdatenDaten("F:\\Neuer Ordner (3)", 2018, wmf);
			me.add(messdiener);
			messdiener.getDienverhalten().update(wmf);
		}
		System.out.println("++++");
		for (Messdiener messdiener : me) {
			messdiener.makeXML("F:\\Neuer Ordner (3)", wmf);
		}
	}
}