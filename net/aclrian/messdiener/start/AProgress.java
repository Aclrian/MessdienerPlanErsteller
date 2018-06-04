package net.aclrian.messdiener.start;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.differenzierung.WriteFile_Pfarrei;
import net.aclrian.messdiener.window.WAlleMessen;
import net.aclrian.messdiener.window.planerstellen.WMessenHinzufuegen;

public class AProgress {

	private AData ada;
	public static final String VersionID = "b598";
	private ArrayList<Messdiener> memit;

	public AProgress() {
		ada = new AData(this);
	//	WAlleMessen wam = new WAlleMessen(this);
		//wam.setVisible(true);
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
		WAlleMessen wam = new WAlleMessen(this);
		wam.setVisible(true);
	}

}
