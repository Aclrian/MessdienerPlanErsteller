package net.aclrian.messdiener.start;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.aclrian.messdiener.messdiener.Messdiener;
import net.aclrian.messdiener.messe.Messe;
import net.aclrian.messdiener.messe.StandartMesse;
import net.aclrian.messdiener.utils.Erroropener;

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

    /*
     * public void pfareiNeu(WriteFile_Pfarrei wfp) { wam.setContentPane(wfp); }
     */

    public Object[][] getAbmeldenTableVector(String[] s) {
	Object[][] rtn = new Object[ada.getMediarray().size()][s.length];
	/*
	 * for (Object[] objects : rtn) { for (Object objects2 : objects) { objects2 =
	 * new Object(); } }
	 */
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

    
}
