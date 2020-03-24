package net.aclrian.mpe.pfarrei;

import java.io.IOException;
import java.util.ArrayList;

import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;

public class Manuell {
	public enum EnumWorking {
		ueberarbeitet, neu

	}

	public static class Handling {

		private StandartMesse sm;
		private EnumWorking ew;
		private StandartMesse alt;

		public Handling(StandartMesse sm, EnumWorking ew, StandartMesse alt) {
			this.sm = sm;
			this.ew = ew;
			if (ew == EnumWorking.ueberarbeitet) {
				this.alt = alt;
			}

		}

		public StandartMesse getAlt() {
			return alt;
		}

		public EnumWorking getEw() {
			return ew;
		}

		public StandartMesse getNeu() {
			return sm;
		}
	}

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
	private ArrayList<Handling> hand;
	private ArrayList<Messdiener> medis;

	private Pfarrei pf;

	public Manuell(ArrayList<Handling> hand, ArrayList<Messdiener> medis, Pfarrei pf) {
		// TODO need to be included

		this.hand = hand;
		this.medis = medis;
		this.pf = pf;
	}

	public void act(Window window) {
		for (Handling handling : hand) {
			if (handling.getEw() == EnumWorking.ueberarbeitet) {
				for (Messdiener m : medis) {
					m.getDienverhalten().ueberschreibeStandartMesse(handling.getAlt(), handling.getNeu());
				}
			}
			if (handling.getEw() == EnumWorking.neu) {
				Log.getLogger().info("Um in einer neuen Standartmesse Messdiener hinzu zu fügen, bitte den Messdiener auswählen.");
				for (Messdiener m : medis) {
					m.getDienverhalten().fuegeneueMesseHinzu(handling.getNeu());
				}
			}
		}
		WriteFile_Pfarrei.writeFile(pf, window);
		for (Messdiener m : medis) {
			WriteFile wf = new WriteFile(m);
			try {
				wf.toXML(window);
			} catch (IOException e) {
				Dialogs.error(e, "Es ist ein Fehler aufgetreten");
			}
		}
	}

}