package net.aclrian.frame.old;

import java.awt.Toolkit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.SwingWorker;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.Sonstiges;

public class CreatePlanTask extends SwingWorker<Void, Void> {

	private Messdiener[] me;
	private ArrayList<Messe> m;
	public ProgressBarDemo pbd;

	public CreatePlanTask(Messdiener[] medis, ArrayList<Messe> messen, ProgressBarDemo pbd) {
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
		float einzelnes = (m.size() + 1);
		einzelnes = 100 / einzelnes;
		SimpleDateFormat dstart = new SimpleDateFormat("MM*dd-yyyy");
		String anhang = "-23:59";
		Date start = m.get(0).getDate();
		String starts = dstart.format(start) + anhang;
		SimpleDateFormat dzurueck = new SimpleDateFormat("MM*dd-yyyy-hh:mm");
		String[] sm = starts.split("*");
		Integer ms = Integer.parseInt(sm[0]);
		ms=ms+6;
		starts = String.valueOf(ms)+"*"+sm[1];
		try {
			start = dzurueck.parse(starts);
		} catch (ParseException e1) {
			e1.printStackTrace();
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
		if (medi.getMessdatenDaten().kann(m.getDate(),false)) {//',false' added for compalitation
			if (new Sonstiges().equals(m.getStandardMesse())) {
				if (medi.getDienverhalten().getBestimmtes(m.getStandardMesse(), null)) {
					m.einteilen(medi);
					int i = ran.nextInt(10);
					if (i <= 3) {
						einteilen(m, medi.getMessdatenDaten().getGeschwister(), 2);
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
