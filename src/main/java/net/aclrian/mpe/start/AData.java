package net.aclrian.mpe.start;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.Log;

public class AData {
	
	
	
	public static final Sonstiges sonstiges = new Sonstiges();
	/**
	 * Hier werden alle Messdiener aus {@link AData#savepath} gespeichert!
	 */
	private ArrayList<Messdiener> mediarray;
	private ArrayList<Messe> messenarray = new ArrayList<Messe>();
	private Pfarrei pf;
	private HashMap<Messe, ArrayList<Messdiener>> voreingeteilte = new HashMap<>();

	public AData() {
		try {
			pf = DateienVerwalter.dv.getPfarrei();
		} catch (NullPointerException e) {
			ap.getPfarreiNeu();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			ap.pfarreiaendern(pf);
			return;
		}
		pf.getStandardMessen().add(sonstiges);
		mediarray = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList(DateienVerwalter.dv.getSavepath(), pf.getStandardMessen());
		mediarray.sort(Messdiener.compForMedis);
		Log.getLogger().info("Es wurden " + mediarray.size() + " gefunden!");
		/*
		 * for (StandartMesse sm : pf.getStandardMessen()) { System.out.println(sm +
		 * ":"); if (!(sm instanceof Sonstiges)) { for (Messdiener messdiener :
		 * mediarray) { if (messdiener.getDienverhalten().getBestimmtes(sm, this)) {
		 * System.out.println(messdiener); } } System.out.println("\n\n\n"); }
		 * 
		 * }
		 * 
		 * for (Messdiener messdiener : mediarray) { if (!messdiener.isIstLeiter()) {
		 * System.out.println(messdiener); } }
		 */
	}
	public Object[][] getAbmeldenTableVector(String[] s, SimpleDateFormat df) {
		mediarray.sort(Messdiener.compForMedis);
		Object[][] rtn = new Object[mediarray.size()][s.length];
		for (int i = 0; i < mediarray.size(); i++) {
			Object[] o = rtn[i];
			Messdiener m = mediarray.get(i);
			o[0] = m.toString();
			for (int j = 1; j < s.length; j++) {
				o[j] = m.getMessdatenDaten().ausgeteilt(s[j]);
			}
		}
		return rtn;
	}
	
	/*private void update() {
		mediarray = dv.getAlleMedisVomOrdnerAlsList(savepath, this);
		for (Messdiener medi : mediarray) {
					Messdaten md = mmedi.getMessdatenDaten();
					md.update(medi, this);
				}
	}*/

	public void  generateMessdaten(Window window) {
		mediarray = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(DateienVerwalter.dv.getSavepath(), year, pf, mediarray);
		}
	}
	public ArrayList<Messe> optimieren(Calendar cal, StandartMesse sm, Date end, ArrayList<Messe> mes)
			throws Exception {
		if (cal.getTime().before(end) && !(sm instanceof Sonstiges)) {
			SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
			String tag = wochendagformat.format(cal.getTime());
			if (tag.startsWith(sm.getWochentag())) {
				Date d = cal.getTime();
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				SimpleDateFormat dfuhr = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
				try {
					Date frting = dfuhr
							.parse(df.format(d) + "-" + sm.getBeginn_stundealsString() + ":" + sm.getBeginn_minute());
					Messe m = new Messe(frting, sm, pf.getStandardMessen());
					mes.add(m);
				} catch (Exception e) {
					new Erroropener(e);
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

	public ArrayList<Messe> generireDefaultMessen(Date anfang, Date ende) throws Exception {
		ArrayList<Messe> rtn = new ArrayList<Messe>();
		Calendar start = Calendar.getInstance();
		for (StandartMesse sm : pf.getStandardMessen()) {
			if (!(sm instanceof Sonstiges)) {
				start.setTime(anfang);
				ArrayList<Messe> m = optimieren(start, sm, ende, new ArrayList<Messe>());
				rtn.addAll(m);
			}
		}
		rtn.sort(Messe.compForMessen);
		Log.getLogger().info("DefaultMessen generiert");
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

	public ArrayList<Messdiener> getMediarray() {
		return mediarray;
	}

	public ArrayList<Messe> getMesenarray() {
		return messenarray;
	}

	public Pfarrei getPfarrei() {
		return pf;
	}

	public ArrayList<StandartMesse> getSMoheSonstiges() {
		ArrayList<StandartMesse> sm = new ArrayList<StandartMesse>();
		sm.addAll(pf.getStandardMessen());
		for (int i = 0; i < sm.size(); i++) {
			if (sm.get(i) instanceof Sonstiges) {
				sm.remove(i);
			}
		}
		sm.sort(StandartMesse.comfuerSMs);
		return sm;
	}

	public void erneuern(Window window, String savepath) {
		savepath = DateienVerwalter.dv.getSavepath(window);
		DateienVerwalter.re_start(window);
		try {
			pf = DateienVerwalter.dv.getPfarrei();
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
		mediarray = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		if (!hatsonstiges) {
			pf.getStandardMessen().add(sonstiges);
		}
		System.out.println(pf.getStandardMessen());
		System.out.println(DateienVerwalter.dv.getPfarrei());
		Log.getLogger().info("Es wurden " + mediarray.size() + " gefunden!");
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
