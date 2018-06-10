package net.aclrian.messdiener.start;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.differenzierung.WriteFile_Pfarrei;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.WAlleMessen;
import net.aclrian.messdiener.window.planerstellen.WMessenHinzufuegen;

public class AProgress {

	private AData ada;
	public static final String VersionID = "b598";
	private ArrayList<Messdiener> memit;
	WAlleMessen wam;

	public AProgress() {
		ada = new AData(this);
		wam = new WAlleMessen(this);
	}
	
	public AData getAda() {
		return ada;
	}
	
	public void getGeschwister() {
		// TODO Auto-generated method stub
	}
	public void berechen(Date[] dates) {
		//alt
		WMessenHinzufuegen wmh = new WMessenHinzufuegen(dates[0], dates[1], this);
		wmh.setVisible(true);
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
	public void fertig(WriteFile_Pfarrei writeFile_Pfarrei, Pfarrei pf, String s) {
		// TODO Auto-generated method stub
		
	}
	
	public void start() {
		wam.setVisible(true);
	}

	public void pfareiNeu(WriteFile_Pfarrei wfp){
		wam.setContentPane(wfp);
	}
	public void pfarreiGeaendert(WriteFile_Pfarrei wfp, Pfarrei pf) {
		//TODO
	}

	public WAlleMessen getWAlleMessen() {
		return wam;
	}
	
	public ArrayList<Messe> generireDefaultMessen(Date anfang, Date ende) {
		ArrayList<Messe> rtn = new ArrayList<Messe>();
		Calendar start = Calendar.getInstance();
		start.setTime(anfang);
		SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
		for (Date date = start.getTime(); date.before(ende); start.add(Calendar.DATE, 1), date = start.getTime()) {
			String tag = wochendagformat.format(date);
			for (StandartMesse sm : ada.getPfarrei().getStandardMessen()) {
				if (!AData.sonstiges.isSonstiges(sm)) {
					// DEBUG: System.out.println(sm.getWochentag() + ":"
					// + tag + "--");
					// if (sm.getWochentag().equals(tag)) {
					Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), tag + ": " + sm.getWochentag() + tag.startsWith(sm.getWochentag()));
					if (tag.startsWith(sm.getWochentag())) {
						/*
						Calendar c = Calendar.getInstance();
						c.setTime(start.getTime());
						c.add(Calendar.HOUR_OF_DAY, sm.getBeginn_stunde());
						c.add(Calendar.MINUTE, Integer.parseInt(sm.getBeginn_minute()));*/
						Date d = start.getTime();
						SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
						SimpleDateFormat dfuhr = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
						try {
							Date frting = dfuhr.parse(df.format(d)+"-"+sm.getBeginn_stundealsString()+":"+sm.getBeginn_minute());
							Messe m = new Messe(frting, sm, ada);
							rtn.add(m);
						} catch (ParseException e) {
				 			new Erroropener(e.getMessage()+ ": Konnte kein Datum erzeugen. Das sollte eigentlich nicht passieren.");
							e.printStackTrace();
						}
						// System.out.println(m.getIDHTML());
					}
				}
			}
		}
		return rtn;
	}
	
}
