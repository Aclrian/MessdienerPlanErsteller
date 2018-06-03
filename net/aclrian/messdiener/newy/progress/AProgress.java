package net.aclrian.messdiener.newy.progress;

import java.util.ArrayList;
import java.util.Calendar;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.differenzierung.WriteFile_Pfarrei;

public class AProgress {

	private AData ada;

	public AProgress() {
		ada = new AData(this);
	}
	public AData getAda() {
		return ada;
	}
	
	public void getGeschwister() {
		// TODO Auto-generated method stub
		
	}
	public void berechen() {
		// TODO Auto-generated method stub
		
	}
	public ArrayList<Messdiener> getMediarraymitMessdaten() {
		ArrayList<Messdiener> mediarray = ada.getUtil().getAlleMedisVomOrdnerAlsList(ada.getSavepath(), ada);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(ada.getUtil().getSavepath(), year, ada);
		}
		return mediarray;
	}
	public void fertig(WriteFile_Pfarrei writeFile_Pfarrei, Pfarrei pf, String s) {
		// TODO Auto-generated method stub
		
	}

}
