package net.aclrian.messdiener.utils;

import java.awt.Toolkit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;


import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.window.WMainFrame;
import net.aclrian.messdiener.window.planerstellen.ProgressBarDemo;

public class CreatePlanTask extends SwingWorker<Void, Void> {

	private Messdiener[] me;
	private ArrayList<Messe> m;
	public ProgressBarDemo pbd;
	private WMainFrame wmf;

	public CreatePlanTask(Messdiener[] medis, ArrayList<Messe> messen, ProgressBarDemo pbd, WMainFrame wmf) {
		this.wmf = wmf;
		this.pbd = pbd;
		this.setMessdiener(medis);
		this.setMessen(messen);
	}

	public Messdiener[] getMesdiener() {
		return me;
	}

	private void setMessdiener(Messdiener[] me) {
		this.me = me;
	}

	public ArrayList<Messe> getMessen() {
		return m;
	}

	private void setMessen(ArrayList<Messe> m) {
		this.m = m;
	}

	@Override
	public Void doInBackground() {
		float progress = 0;
		setProgress(1);
		System.out.println(m.size() + "§§§");
		float einzelnes = (m.size() + 1);
		einzelnes = 100 / einzelnes;
		SimpleDateFormat dstart = new SimpleDateFormat("MM*dd-yyyy");
		String anhang = "-23:59";
		Date start = m.get(0).getDate();
		System.out.println("ffff");
		String starts = dstart.format(start) + anhang;
		SimpleDateFormat dzurueck = new SimpleDateFormat("MM*dd-yyyy-hh:mm");
		String[] sm = starts.split("*");
		try{
		for (String string : sm) {
			System.out.println(string);
		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(sm[0] +"%%%");
		Integer ms = Integer.parseInt(sm[0]);
		ms=ms+6;
		starts = String.valueOf(ms)+"*"+sm[1];
		System.out.println(starts + "!!!");
		try {
			start = dzurueck.parse(starts);
		} catch (ParseException e1) {
			System.err.println("DU;M");
		}
		
		for (int i = 0; i < m.size(); i++) {
			try {
				if (start.after(m.get(i).getDate())) {
					for (Messdiener medi : me) {
						medi.getMessdatenDaten().naechsterMonat();
					}
					start = m.get(i).getDate();
					starts = dstart.format(start) + anhang;
					start = dzurueck.parse(starts);
				}
			//	System.out.println("DRAN: " + m.get(i).getID());
		//		System.out.println(m.get(i).getEnumdfMesse());
				einteilen(m.get(i) ,0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			progress = progress + einzelnes;
			setProgress((int) progress);
		}
		setProgress(100);
		for (Messe messe : m) {
	//		System.out.println(messe.ausgeben());
		}
		System.out.println("dfggffff");
		return null;
	}

	public void einteilen(Messe m, int runs) {
		if (m.istFertig()) {
			///System.err.println(runs);
			//System.out.println(m.ausgeben());
	//		System.out.println("FERTIG: "+ m.getID());
			return;
		}
		Random ran = new Random();
		int z = ran.nextInt(me.length);
		Messdiener medi = me[z];
	//	System.out.println(medi.makeId());
		if (medi.getMessdatenDaten().kannnoch()) {
			if (m.getEnumdfMesse() != EnumdeafaultMesse.Sonstiges) {
				if (medi.getDienverhalten().getBestimmtes(m.getEnumdfMesse())) {
					m.einteilen(medi);
					int i = ran.nextInt(10);
					if (i <= 3) {
						einteilen(m, medi.getMessdatenDaten().getPrioAnvertraute(), 2);
					}

				}
			} else {
				m.einteilen(medi);
			}
			/*int verhaeltins = this.m.size() / 100 
			if (i) {
				
			}*/
		} else {
			// !einteilen(m);
		}
		einteilen(m, runs+1);
	}/*
		 * @SuppressWarnings("null") private void einteilen(Messe m, Messdiener medi) {
		 * if (m.istFertig(false)) { if (!m.istFertig(true)) { einteilenFuerLeiter(m,
		 * (Boolean)null); } return; } m.einteilen(medi);
		 * 
		 * }
		 */

	private void einteilen(Messe m, ArrayList<Messdiener> anvertraute, int secondndchance) {
		if (m.istFertig()) {
			System.out.println(m.ausgeben() + "\nwar ein Erfolg!");
			return;
		}
		Random ran = new Random();
		if (anvertraute.size() >= 2) {
			for (int i = 0; i < anvertraute.size(); i++) {
				int r = ran.nextInt(anvertraute.size());
				Messdiener medi = anvertraute.get(r);
				einteilen(m, medi);
			}
		}

	}

	private void einteilen(Messe m2, Messdiener medi) {
		// TODO Auto-generated method stub

	}

	public void einteilenFuerLeiter(Messe m, boolean nullabel) {
		// TODO machen
		return;
	}

	@Override
	protected void done() {
		Toolkit.getDefaultToolkit().beep();
		pbd.getStartButton().setEnabled(true);
		pbd.setCursor(null); // turn off the wait cursor
		pbd.getTaskOutput().append("Done!\n");
		for (Messe messe : m) {
			pbd.getTaskOutput().append(messe.ausgeben());
		}
		pbd.setVisible(true);
		super.done();
	}

}
