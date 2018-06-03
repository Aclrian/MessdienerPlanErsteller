package net.aclrian.messdiener.newy.progress;

import java.util.ArrayList;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.differenzierung.WriteFile_Pfarrei;
import net.aclrian.messdiener.utils.DateienVerwalter;
import net.aclrian.messdiener.utils.Utilities;

public class AData {
	public static final Sonstiges sonstiges = new Sonstiges();
	/**
	 * Hier werden alle Messdiener aus {@link AData#savepath} gespeichert!
	 */
	private ArrayList<Messdiener> mediarray;
	private ArrayList<Messe> mesenarray;
	private DateienVerwalter dv;
	private String savepath = "";
	private Pfarrei pf;
	public static String pfarredateiendung = ".xml.pfarrei";
	
	public AData(AProgress ap) {
		dv = new DateienVerwalter();
		savepath = dv.getSavepath();
		try {
			pf = dv.getPfarrei();
		} catch (Exception e) {
			// new Erroropener("no error");
			WriteFile_Pfarrei wfp = new WriteFile_Pfarrei(ap);
			//TODO
			return;
		}
		pf.getStandardMessen().clear();
		pf.getStandardMessen().add(sonstiges);
		boolean hatsonstiges = false;
		for (StandartMesse sm : pf.getStandardMessen()) {
			// if(sonstiges.isSonstiges(sm)) {hatsonstiges = true;}
			// else{
			System.out.println(sm);
			pf.getStandardMessen().add(sm);
			// }
		}
		mediarray = dv.getAlleMedisVomOrdnerAlsList(savepath, this);

		if (!hatsonstiges) {
			pf.getStandardMessen().add(sonstiges);
		}
		Utilities.logging(this.getClass(), new Object() {
		}.getClass().getEnclosingMethod().getName(), "Es wurden " + mediarray.size() + " gefunden!");
	}
	
	public ArrayList<Messdiener> getMediarray() {
		return mediarray;
	}
	public ArrayList<Messe> getMesenarray() {
		return mesenarray;
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
			//TODO
			return;
		}
		pf.getStandardMessen().clear();
		pf.getStandardMessen().add(sonstiges);
		boolean hatsonstiges = false;
		for (StandartMesse sm : pf.getStandardMessen()) {
			// if(sonstiges.isSonstiges(sm)) {hatsonstiges = true;}
			// else{
			System.out.println(sm);
			pf.getStandardMessen().add(sm);
			// }
		}
		mediarray = dv.getAlleMedisVomOrdnerAlsList(savepath, this);

		if (!hatsonstiges) {
			pf.getStandardMessen().add(sonstiges);
		}
		Utilities.logging(this.getClass(), new Object() {
		}.getClass().getEnclosingMethod().getName(), "Es wurden " + mediarray.size() + " gefunden!");	
	}

}
