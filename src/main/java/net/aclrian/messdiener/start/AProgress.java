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

	public void berechen(Date[] dates) {
		// alt
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

	public void pfareiNeu(WriteFile_Pfarrei wfp) {
		wam.setContentPane(wfp);
	}

	public void pfarreiGeaendert(WriteFile_Pfarrei wfp, Pfarrei pf) {
		// TODO
	}

	public WAlleMessen getWAlleMessen() {
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

}
