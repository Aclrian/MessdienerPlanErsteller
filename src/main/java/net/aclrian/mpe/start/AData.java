package net.aclrian.mpe.start;

import java.util.ArrayList;
import java.util.HashMap;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Utilities;

public class AData {
    public static final Sonstiges sonstiges = new Sonstiges();
    /**
     * Hier werden alle Messdiener aus {@link AData#savepath} gespeichert!
     */
    private ArrayList<Messdiener> mediarray;
    private ArrayList<Messe> messenarray = new ArrayList<Messe>();
    private DateienVerwalter dv;
    private String savepath = "";
    private Pfarrei pf;
    public static final String pfarredateiendung = ".xml.pfarrei";
    public static final String textdatei = "//.messdienerOrdnerPfad.txt";
    private HashMap<Messe, ArrayList<Messdiener>> voreingeteilte = new HashMap<>();

    public AData(AProgress ap) {
	dv = new DateienVerwalter();
	savepath = dv.getSavepath();
	try {
	    pf = dv.getPfarrei();
	} catch (NullPointerException e) {
	    ap.getPfarreiNeu();
	    return;
	} catch (Exception e) {
	    e.printStackTrace();
	    ap.pfarreiaendern(pf);
	    return;
	}
	pf.getStandardMessen().add(sonstiges);
	mediarray = dv.getAlleMedisVomOrdnerAlsList(savepath, this);
	mediarray.sort(Messdiener.compForMedis);
	Utilities.logging(this.getClass(), "init", "Es wurden " + mediarray.size() + " gefunden!");
	/*for (StandartMesse sm : pf.getStandardMessen()) {
	    System.out.println(sm + ":");
	    if (!(sm instanceof Sonstiges)) {
		for (Messdiener messdiener : mediarray) {
		    if (messdiener.getDienverhalten().getBestimmtes(sm, this)) {
			System.out.println(messdiener);
		    }
		}
		System.out.println("\n\n\n");
	    }

	}

	for (Messdiener messdiener : mediarray) {
	    if (!messdiener.isIstLeiter()) {
		System.out.println(messdiener);
	    }
	}*/
    }

    public ArrayList<Messdiener> getMediarray() {
	return mediarray;
    }

    public ArrayList<Messe> getMesenarray() {
	return messenarray;
    }

    public DateienVerwalter getUtil() {
	return dv;
    }

    public String getSavepath() {
	return savepath;
    }

    public Pfarrei getPfarrei() {
	return pf;
    }

    public ArrayList<StandartMesse> getSMoheSonstiges() {
	ArrayList<StandartMesse> sm = new ArrayList<StandartMesse>();
	sm.addAll(pf.getStandardMessen());
	for (int i = 0; i < sm.size(); i++) {
	    if (sonstiges.isSonstiges(sm.get(i))) {
		sm.remove(i);
	    }
	}
	sm.sort(StandartMesse.comfuerSMs);
	return sm;
    }

    public void erneuern(AProgress ap, String savepath) {
	dv = new DateienVerwalter();
	savepath = dv.getSavepath();
	try {
	    pf = dv.getPfarrei();
	    if (pf == null) {
		ap.getPfarreiNeu();
	    }
	} catch (Exception e) {
	    ap.pfarreiaendern(pf);
	    return;
	}
	pf.getStandardMessen().clear();
	pf.getStandardMessen().add(sonstiges);
	boolean hatsonstiges = false;
	mediarray = dv.getAlleMedisVomOrdnerAlsList(savepath, this);
	if (!hatsonstiges) {
	    pf.getStandardMessen().add(sonstiges);
	}
	Utilities.logging(this.getClass(), "erneuern", "Es wurden " + mediarray.size() + " gefunden!");
    }

    public void addMesse(Messe m) {
	messenarray.add(m);
	messenarray.sort(Messe.compForMessen);
    }

    public void addMedi(Messdiener medi) {
	mediarray.add(medi);
    }

    public HashMap<Messe, ArrayList<Messdiener>> getVoreingeteilte() {
	return voreingeteilte;
    }

    public void setPfarrei(Pfarrei pfarrei) {
	this.pf = pfarrei;
    }
}
