package net.aclrian.mpe.pfarrei;

import java.io.IOException;
import java.util.ArrayList;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.utils.Erroropener;

public class Manuell {
    public enum EnumWorking {
	ueberarbeitet, neu

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
    private AProgress ap;
    private Pfarrei pf;

    public Manuell(ArrayList<Handling> hand, AProgress ap, Pfarrei pf) {
	this.hand = hand;
	this.ap = ap;
	this.pf = pf;
    }

    public void act() {
	for (Handling handling : hand) {
	    if (handling.getEw() == EnumWorking.ueberarbeitet) {
		for (Messdiener m : ap.getAda().getMediarray()) {
		    m.getDienverhalten().ueberschreibeStandartMesse(handling.getAlt(), handling.getNeu());
		}
	    }
	    if (handling.getEw() == EnumWorking.neu) {
		new Erroropener(new Exception("Um in einer neuen Standartmesse Messdiener hinzu zu f" + References.ue
			+ "gen, bitte den Messdiener ausw" + References.ae + "hlen."));
		for (Messdiener m : ap.getAda().getMediarray()) {
		    m.getDienverhalten().fuegeneueMesseHinzu(handling.getNeu());
		}
	    }
	}
	WriteFile_Pfarrei.writeFile(pf, ap.getAda().getUtil().getSavepath());
	for (Messdiener m : ap.getAda().getMediarray()) {
	    WriteFile wf = new WriteFile(m, ap.getAda().getUtil().getSavepath());
	    try {
		wf.toXML(ap);
	    } catch (IOException e) {
		new Erroropener(e);
		e.printStackTrace();
	    }
	}
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

	public StandartMesse getNeu() {
	    return sm;
	}

	public EnumWorking getEw() {
	    return ew;
	}

	public StandartMesse getAlt() {
	    return alt;
	}
    }

}