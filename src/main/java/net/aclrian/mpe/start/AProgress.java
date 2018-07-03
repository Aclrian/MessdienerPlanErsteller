package net.aclrian.mpe.start;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Manuell;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.WriteFile_Pfarrei;
import net.aclrian.mpe.pfarrei.Manuell.EnumWorking;
import net.aclrian.mpe.pfarrei.Manuell.Handling;
import net.aclrian.mpe.utils.Erroropener;

public class AProgress {

    private AData ada;
    public static final String VersionID = "b600";
    private ArrayList<Messdiener> memit;
    WEinFrame wam;

    public AProgress() {
	ada = new AData(this);
	wam = new WEinFrame(this);
    }

    public AData getAda() {
	return ada;
    }

    public ArrayList<Messdiener> getMediarraymitMessdaten() {
	if (memit != null) {
	    return memit;
	}
	ArrayList<Messdiener> mediarray = ada.getUtil().getAlleMedisVomOrdnerAlsList(ada.getSavepath(), ada);
	int year = Calendar.getInstance().get(Calendar.YEAR);
	for (Messdiener messdiener : mediarray) {
	    messdiener.setnewMessdatenDaten(ada.getUtil().getSavepath(), year, ada);
	}
	memit = mediarray;
	return mediarray;
    }

    public void start() {
	wam.setVisible(true);
    }

    public Object[][] getAbmeldenTableVector(String[] s) {
	Object[][] rtn = new Object[ada.getMediarray().size()][s.length];
	for (int i = 0; i < rtn.length; i++) {
	    Object[] o = rtn[i];
	    Messdiener m = ada.getMediarray().get(i);
	    o[0] = m.toString();
	    for (int j = 1; j < o.length; j++) {
		o[j] = false;
	    }
	}
	return rtn;
    }

    public WEinFrame getWAlleMessen() {
	return wam;
    }

    public ArrayList<Messe> optimieren(Calendar cal, StandartMesse sm, Date end, ArrayList<Messe> mes) {
	if (cal.getTime().before(end) && !AData.sonstiges.isSonstiges(sm)) {
	    SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
	    String tag = wochendagformat.format(cal.getTime());
	    System.out.println(cal.getTime());
	    System.out.println(sm.getWochentag());
	    if (tag.startsWith(sm.getWochentag())) {
		Date d = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat dfuhr = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
		try {
		    Date frting = dfuhr
			    .parse(df.format(d) + "-" + sm.getBeginn_stundealsString() + ":" + sm.getBeginn_minute());
		    Messe m = new Messe(frting, sm, ada);
		    mes.add(m);
		} catch (ParseException e) {
		    new Erroropener(
			    e.getMessage() + ": Konnte kein Datum erzeugen. Das sollte eigentlich nicht passieren.");
		    e.printStackTrace();
		}
		cal.add(Calendar.DATE, 7);
		mes = optimieren(cal, sm, end, mes);
	    } else {
		cal.add(Calendar.DATE, 1);
		mes = optimieren(cal, sm, end, mes);
	    }
	}
	return mes;
    }

    public ArrayList<Messe> generireDefaultMessen(Date anfang, Date ende) {
	ArrayList<Messe> rtn = new ArrayList<Messe>();
	Calendar start = Calendar.getInstance();
	for (StandartMesse sm : ada.getPfarrei().getStandardMessen()) {
	    if (!AData.sonstiges.isSonstiges(sm)) {
		start.setTime(anfang);
		ArrayList<Messe> m = optimieren(start, sm, ende, new ArrayList<Messe>());
		System.out.println(m + "fuer " + sm.toString());
		rtn.addAll(m);
	    }
	}
	rtn.sort(Messe.compForMessen);
	return rtn;

    }

    public ArrayList<Messdiener> getLeiter(ArrayList<Messdiener> param) {
	ArrayList<Messdiener> rtn = new ArrayList<Messdiener>();
	for (Messdiener messdiener : param) {
	    if (messdiener.isIstLeiter()) {
		rtn.add(messdiener);
	    }
	}
	return rtn;
    }

    public Messdiener getMessdienerFromString(String valueAt, ArrayList<Messdiener> arrayList) {
	for (Messdiener messdiener : arrayList) {
	    if (messdiener.toString().equals(valueAt)) {
		return messdiener;
	    }
	}
	return null;
    }

    public void pfarreigeaendert(Pfarrei pf) {
	WriteFile_Pfarrei wf = new WriteFile_Pfarrei(pf, this);
	wam.setContentPane(wf);
	WEinFrame.farbe(wam, false);
    }

    public void getPfarreiNeu() {
	WriteFile_Pfarrei wf = new WriteFile_Pfarrei(this);
	wam.setContentPane(wf);
    }

    public void fertig(WriteFile_Pfarrei wfp, Pfarrei pf, String savepath, AProgress ap) {
	ada.setPfarrei(ada.getUtil().getPfarrei());
	if (!ada.getPfarrei().getStandardMessen().toString().equals(pf.getStandardMessen().toString())) {
	    ArrayList<StandartMesse> beibehalten = new ArrayList<StandartMesse>();
	    ArrayList<StandartMesse> verbleibende = new ArrayList<StandartMesse>();
	    for (StandartMesse sm : pf.getStandardMessen()) {
		boolean b = true;
		for (StandartMesse smold : ada.getPfarrei().getStandardMessen()) {
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
	    for (StandartMesse alt : ada.getPfarrei().getStandardMessen()) {
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
	    ada.setPfarrei(pf);
	    WriteFile_Pfarrei.writeFile(pf, savepath);
	}
	
	ada.erneuern(ap,savepath);
    }

    private void remove(ArrayList<StandartMesse> smarray, StandartMesse sm) {
	for (int i = 0; i < smarray.size(); i++) {
	    StandartMesse messe = smarray.get(i);
	    if (messe.toString().equals(sm.toString())) {
		smarray.remove(i);
	    }
	}
    }

}
