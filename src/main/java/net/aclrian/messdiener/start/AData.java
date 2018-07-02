package net.aclrian.messdiener.start;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.aclrian.messdiener.messdiener.Messdiener;
import net.aclrian.messdiener.messe.Messe;
import net.aclrian.messdiener.messe.Sonstiges;
import net.aclrian.messdiener.messe.StandartMesse;
import net.aclrian.messdiener.pfarrei.Manuell;
import net.aclrian.messdiener.pfarrei.Pfarrei;
import net.aclrian.messdiener.pfarrei.WriteFile_Pfarrei;
import net.aclrian.messdiener.pfarrei.Manuell.EnumWorking;
import net.aclrian.messdiener.pfarrei.Manuell.Handling;
import net.aclrian.messdiener.utils.DateienVerwalter;
import net.aclrian.messdiener.utils.Utilities;

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
	} catch (Exception e) {
	    e.printStackTrace();
	    // new Erroropener("!jtfvorname.getText().equals("") no error");
	    WriteFile_Pfarrei wfp = new WriteFile_Pfarrei(ap);// TODO hier muss was gemacht werden
	    return;
	}
	// pf.getStandardMessen().clear();
	pf.getStandardMessen().add(sonstiges);
	// boolean hatsonstiges = false;
	/*
	 * for (StandartMesse sm : pf.getStandardMessen()) {
	 * if(sonstiges.isSonstiges(sm)) {hatsonstiges = true;} else{
	 * System.out.println(sm); pf.getStandardMessen().add(sm); } }
	 */

	mediarray = dv.getAlleMedisVomOrdnerAlsList(savepath, this);
	mediarray.sort(Messdiener.compForMedis);

	Utilities.logging(this.getClass(), "init", "Es wurden " + mediarray.size() + " gefunden!");
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

    public void erneuern(AProgress ap) {
	dv = new DateienVerwalter();
	savepath = dv.getSavepath();
	try {
	    pf = dv.getPfarrei();
	} catch (Exception e) {
	    // new Erroropener("no error");
	    WriteFile_Pfarrei wfp = new WriteFile_Pfarrei(ap);
	    // TODO
	    return;
	}
	pf.getStandardMessen().clear();
	pf.getStandardMessen().add(sonstiges);
	boolean hatsonstiges = false;
	/*
	 * for (StandartMesse sm : pf.getStandardMessen()) { //
	 * if(sonstiges.isSonstiges(sm)) {hatsonstiges = true;} // else{
	 * System.out.println(sm); pf.getStandardMessen().add(sm); // } }
	 */
	mediarray = dv.getAlleMedisVomOrdnerAlsList(savepath, this);

	if (!hatsonstiges) {
	    pf.getStandardMessen().add(sonstiges);
	}
	Utilities.logging(this.getClass(), new Object() {
	}.getClass().getEnclosingMethod().getName(), "Es wurden " + mediarray.size() + " gefunden!");
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

    public void fertig(WriteFile_Pfarrei wfp, Pfarrei pf, String savepath, AProgress ap) {
	this.pf = dv.getPfarrei();
	if (!this.pf.getStandardMessen().toString().equals(pf.getStandardMessen().toString())) {
	    ArrayList<StandartMesse> beibehalten = new ArrayList<StandartMesse>();
	    ArrayList<StandartMesse> verbleibende = new ArrayList<StandartMesse>();
	    for (StandartMesse sm : pf.getStandardMessen()) {
		boolean b = true;
		for (StandartMesse smold : this.pf.getStandardMessen()) {
		    if (sm.toString().equals(smold.toString())) {
			beibehalten.add(smold);
			b = false;
			break;
		    }
		}
		if (b) {
		    verbleibende.add(sm);
		}

	    }
	    ArrayList<StandartMesse> alte = new ArrayList<StandartMesse>();
	    ArrayList<String> alteS = new ArrayList<String>();
	    for (StandartMesse alt : this.pf.getStandardMessen()) {
		boolean b = false;
		for (StandartMesse sm : beibehalten) {

		    if (sm.toString().equals(alt.toString())) {
			b = true;
			break;
		    }
		}
		if (!b) {
		    alte.add(alt);
		    alteS.add("Überarbeitete " + alt.toString());
		}

	    }
	    ArrayList<Handling> hand = new ArrayList<Handling>();
	    alteS.add("Neu");
	    for (StandartMesse sm : verbleibende) {
		boolean fertig = false;
		do {

		    Object[] possibilities = new Object[0];
		    possibilities = alteS.toArray(possibilities);
		    String s = (String) JOptionPane.showInputDialog(null,
			    sm.toString() + " ist neu.\nIst sie wirkilch neu oder wurde sie nur überarbeitet?",
			    "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, possibilities, "Neu");

		    // If a string was returned, say so.
		    if ((s != null) && (s.length() > 0)) {
			fertig = true;
			EnumWorking ew;
			StandartMesse alt = null;
			if (s.equals("Neu")) {
			    alt = null;
			    ew = EnumWorking.neu;
			} else {
			    for (StandartMesse sm1 : alte) {
				if (s.endsWith(sm1.toString())) {
				    remove(alte, sm1);
				    ArrayList<String> aS = new ArrayList<String>();
				    for (String string : alteS) {
					if (!string.equals(s)) {
					    aS.add(string);
					}
				    }
				    possibilities = aS.toArray(possibilities);

				    alt = sm1;
				    break;
				}
			    }
			    if (alt == null) {
				fertig = false;
			    }
			    ew = EnumWorking.ueberarbeitet;
			}
			Handling h = new Handling(sm, ew, alt);
			hand.add(h);
		    }
		} while (fertig != true);
	    }
	    ArrayList<StandartMesse> bleibende = new ArrayList<StandartMesse>();
	    bleibende.addAll(beibehalten);
	    Manuell m = new Manuell(hand, ap, pf);
	    m.act();
	    WriteFile_Pfarrei.writeFile(pf, savepath);
	    JOptionPane.showInputDialog("Das Programm wird nun beendet und mit den neuen Daten gestartet!");
	    System.exit(0);
	} else {
	    String name = pf.getName();
	    name = name.replaceAll(" ", "_");
	    File f = new File(savepath + "\\" + name + AData.pfarredateiendung);
	    f.delete();
	    this.pf = pf;
	    WriteFile_Pfarrei.writeFile(pf, savepath);
	}
	dv = new DateienVerwalter(savepath);
	erneuern(ap);
    }
    
    private void remove(ArrayList<StandartMesse> smarray, StandartMesse sm) {
	for (int i = 0; i<smarray.size(); i++) {
		StandartMesse messe = smarray.get(i);
		if (messe.toString().equals(sm.toString())) {
			smarray.remove(i);
		}
	}
}

}
