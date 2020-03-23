package net.aclrian.mpe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;

import javafx.scene.web.HTMLEditor;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.start.References;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import net.aclrian.mpe.utils.RemoveDoppelte;

import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.*;

public class FinishController implements Controller {

	private final String fertig;

	private enum EnumAction {
		EinfachEinteilen, TypeBeachten
	}

	private ArrayList<Messe> messen;
	private ArrayList<Messdiener> hauptarray;
	@FXML
	private HTMLEditor editor;
	@FXML
	private Button pdf,mail,word;

	public FinishController(ArrayList<Messe> messen){
		this.hauptarray = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
		this.messen = messen;
		neuerAlgorythmus();
		StringBuilder s = new StringBuilder("<html>");
		for (int i = 0; i < messen.size(); i++) {
		Messe messe = messen.get(i);
		String m1 = messe.htmlAusgeben();
		m1 = m1.substring(6, m1.length() - 7);
		if (i == 0) {
		Date start = messe.getDate();
		Date ende = messen.get(messen.size() - 1).getDate();
		SimpleDateFormat df = new SimpleDateFormat("dd. MMMM", Locale.GERMAN);
		String text = "Messdienerplan vom " + df.format(start) + " bis " + df.format(ende);
		s.append("<h1>" + text + "</h1>");
		}
		s.append("<br>" + m1 + "</br>");
		}
		s.append("</html>");
		fertig = s.toString();
		ArrayList<Messdiener> nichteingeteile = new ArrayList<>();
		for (Messdiener medi : hauptarray) {
			if (medi.getMessdatenDaten().getInsgesamtEingeteilt() == 0) {
				nichteingeteile.add(medi);
			}
		}
		nichteingeteile.sort(Messdiener.compForMedis);
		Toolkit.getDefaultToolkit().beep();
	}

	@Override
	public void initialize() {}

	@Override
	public void afterstartup(Window window, MainController mc) {
		editor.setHtmlText(fertig);
	}

	@Override
	public boolean isLocked() {
		return true;
	}

	public void back(){
		/*if (ferienplan) {

			JOptionPane op = new JOptionPane(
					"Soll die aktuelle Einteilung gel"+References.oe+"scht werden, um einen neuen Plan mit ggf. ge"+References.ae+"nderten Messen/Abmeldungen generieren zu k\"+References.oe+\"nnen?",
					JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
			JFrame f = new JFrame();
			f.setAlwaysOnTop(true);
			JDialog dialog = op.createDialog(f, "Frage");
			WEinFrame.farbe(dialog);
			dialog.setVisible(true);
			try {
				if ((int) op.getValue() != 2) {
					zurueck((int) op.getValue() == 0, EnumActivePanel.Abmelden, ap);
				}
			} catch (Exception e2) {
				Utilities.logging(getClass(), "toback:ActionListener",
						"Die Eingabe wurde vom Benutzer abgebrochen.");
			}
		} else {
			JOptionPane op = new JOptionPane(
					"Soll die aktuelle Einteilung gel"+References.oe+"scht werden, um einen neuen Plan mit ggf. ge"+References.ae+"nderten Messen generieren zu k\"+References.oe+\"nnen?",
					JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
			JFrame f = new JFrame();
			f.setAlwaysOnTop(true);
			JDialog dialog = op.createDialog(f, "Frage");
			WEinFrame.farbe(dialog);
			dialog.setVisible(true);
			try {
				if ((int) op.getValue() != 2) {
					zurueck((int) op.getValue() == 0, EnumActivePanel.Start, ap);
				}
			} catch (Exception e2) {
				Utilities.logging(getClass(), "toback:ActionListener",
						"Die Eingabe wurde vom Benutzer abgebrochen.");
			}
		}*/
	}

	public void zurueck(boolean loeschen, MainController.EnumPane eap, MainController mc) {
		if (loeschen) {
			for (Messdiener medi : DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList()) {
				medi.getMessdatenDaten().nullen();
			}
			for (Messe m : mc.getMessen()) {
				m.nullen();
			}
		}
		mc.changePane(MainController.EnumPane.start);
	}

	private void open(boolean isword) {
		/*Converter con;
		try {
			con = new Converter(this);
			File f = (isword) ? con.getDocx() : con.toPDF();
			Desktop d = Desktop.getDesktop();
			d.open(f);
		} catch (IOException e) {
			new Erroropener(e);
			e.printStackTrace();
		}*/
		}

		public void neuerAlgorythmus() {
		hauptarray.sort(Messdiener.einteilen);
		// TODO EFI-Brist-ZIENTer und notwendigkeit? Dap und zvmzwm fruehzeitg erkennen und beheben
		/*for (StandartMesse sm : DateienVerwalter.dv.getPfarrei().getStandardMessen()) {
			int anz_real = 0;
			int anz_monat = 0;

			ArrayList<Messdiener> array = new ArrayList<>();
			if (!(sm instanceof Sonstiges)) {
				for (Messdiener medi : hauptarray) {
					int id = 0;
					if (medi.isIstLeiter()) {
						id++;
					}
					int ii = DateienVerwalter.dv.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
					if (medi.getDienverhalten().getBestimmtes(sm) && ii != 0) {
						array.add(medi);
						anz_monat += medi.getMessdatenDaten().getkannnochAnz();
					}
				}
				anz_real = anz_monat;
				int benoetigte_anz = 0;
				ArrayList<Messe> dfmessen = new ArrayList<Messe>();
				Calendar start = Calendar.getInstance();
				start.setTime(messen.get(0).getDate());
				start.add(Calendar.MONTH, 1);
				for (Messe me : messen) {
					if (!(me.getStandardMesse() instanceof Sonstiges)) {
						if (me.getDate().after(start.getTime())) {
							anz_real = anz_real + anz_monat;
							start.setTime(me.getDate());
						}
						dfmessen.add(me);
						benoetigte_anz += sm.getAnz_messdiener();
					}
				}
				if (benoetigte_anz > anz_real) {
					int fehlt = benoetigte_anz - anz_real;
					try {
						int medishinzufuegen = (int) Math.ceil(fehlt / array.size());// abrunden
						Utilities.logging(this.getClass(), "neuerAlorythmus",
								"Es wurde abgerundet: " + (double) (fehlt / array.size()) + "-->" + medishinzufuegen);
						for (Messdiener messdiener : array) {
							messdiener.getMessdatenDaten().addtomaxanz(medishinzufuegen, messdiener.isIstLeiter());
						}
						String mitteilung = "Zu den Messdienern, die am " + sm.getWochentag() + " um " + sm.getBeginn_stunde()
								+ " koennen, werden + " + medishinzufuegen
								+ " zu ihrem normalen Wert hinzugef"+ References.ue +"gt!";
						Utilities.logging(this.getClass(), "neuerAlorythmus",
								mitteilung);
						new Erroropener(new Exception(mitteilung));
					} catch (ArithmeticException e) {
						Log.getLogger().info("Kein Messdiener kann: "
								+ sm.getWochentag() + sm.getBeginn_stunde() + ":" + sm.getBeginn_minute());
					}
				}
			}
		}*/
		// DAV UND ZWMZVM ENDE
		Log.getLogger().info(References.Ue + "berpruefung zu ende!");
		// neuer Monat:
		Calendar start = Calendar.getInstance();
		start.setTime(messen.get(0).getDate());
		start.add(Calendar.MONTH, 1);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Log.getLogger().info("n" + References.ae + "chster Monat bei: " + df.format(start.getTime()));
		// EIGENTLICHER ALGORYTHMUS
			for (Messe me : messen) {
				// while(m.isfertig() || stackover){
				if (me.getDate().after(start.getTime())) {
					start.add(Calendar.MONTH, 1);
					Log.getLogger().info("naechster Monat: Es ist " + df.format(me.getDate()));
					for (Messdiener messdiener : hauptarray) {
						messdiener.getMessdatenDaten().naechsterMonat();
					}
				}
				Log.getLogger().info("Messe dran: " + me.getID());
				if (me.getStandardMesse() instanceof Sonstiges) {
					this.einteilen(me, EnumAction.EinfachEinteilen);
				} else {
					this.einteilen(me, EnumAction.TypeBeachten);
				}
				Log.getLogger().info("Messe fertig: " + me.getID());
			}
		}

		private void einteilen(Messe m, EnumAction act) {
		switch (act) {
		case EinfachEinteilen:
			ArrayList<Messdiener> medis;
			boolean zwang = false;
			// try {
			medis = get(new Sonstiges(), m);
			// } catch (Exception e) {
			// Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
			// e.getMessage());
			// medis = beheben(m, ap.getAda());
			// zwang = true;
			// }
			Log.getLogger().info(medis.size() + " f" + References.ue + "r " + m.getNochBenoetigte());
			if (m.getNochBenoetigte() > medis.size()) {
				Dialogs.error("Die Messe " + m.getID().replaceAll("\t","   ") + "hat zu wenige Messdiener");
			}
			for (Messdiener medi : medis) {
				einteilen(m, medi, zwang);
			}
			break;
		case TypeBeachten:
			ArrayList<Messdiener> medis2;
			boolean zwang2 = false;
			// try {
			medis2 = get(m.getStandardMesse(), m);
			// } catch (Exception e) {
			// Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
			// e.getMessage());
			// medis2 = beheben(m, ap.getAda());
			// zwang2 = true;
			// }
			Log.getLogger().info(medis2.size() + " f" + References.ue + "r " + m.getNochBenoetigte());
			if (m.getNochBenoetigte() > medis2.size()) {
				Dialogs.error("Die Messe " + m.getID().replaceAll("\t","   ") + "hat zu wenige Messdiener");
			}
			for (Messdiener messdiener : medis2) {
				einteilen(m, messdiener, zwang2);
			}
			break;
		default:
			break;
		}
		}

		public void einteilen(Messe m, Messdiener medi, boolean zwang) {
		boolean d = false;
		if (m.istFertig()) {
			return;
		} else if (zwang) {
			if (medi.getMessdatenDaten().kann(m.getDate(), zwang)) {
				m.einteilenZwang(medi);
				d = true;
			}
		} else {
			if (medi.getMessdatenDaten().kann(m.getDate(), zwang)) {
				m.einteilen(medi);
				d = true;
			}
		}
		if (!m.istFertig() && d) {
			ArrayList<Messdiener> anv = medi.getMessdatenDaten().getAnvertraute(DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList());

			RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
			rd.removeDuplicatedEntries(anv);
			if (anv.size() >= 1) {
				anv.sort(Messdiener.einteilen);
				for (Messdiener messdiener : anv) {
					boolean b = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse());
					if (messdiener.getMessdatenDaten().kann(m.getDate(), zwang) && b) {
						Log.getLogger().info(messdiener.makeId() + " dient mit " + medi.makeId());
						einteilen(m, messdiener, zwang);
					}
				}
			}
		}
		}

		/*public ArrayList<Messdiener> beheben(Messe m) {
		ArrayList<Messdiener> rtn = get(m.getStandardMesse(), m.getDate());
		if (rtn.size() < m.getNochBenoetigte()) {
		ArrayList<Messdiener> prov = new ArrayList<>();
		hauptarray.sort(Messdiener.einteilen);
		int i = rtn.size();
		for (Messdiener messdiener : hauptarray) {
		if (messdiener.getMessdatenDaten().kanndann(m.getDate(), false) && i < m.getNochBenoetigte()) {
		prov.add(messdiener);
		i++;
		}
		}
		for (Messdiener messdiener : prov) {
		new Erroropener(new Exception("<html><body>Bei der Messe: " + m.getID()
		+ "<br></br>herrscht Messdiener-Knappheit</br><br>Daher wird wohl" + messdiener.makeId()
		+ "einspringen m" + References.ue + "ssen, weil er generell kann.</br></body></html>"));
		}
		rtn.addAll(prov);
		// Wenn wirklich keiner mehr kann
		if (rtn.size() < m.getNochBenoetigte()) {
		hauptarray.sort(Messdiener.einteilen);
		Einstellungen e = DateienVerwalter.dv.getPfarrei().getSettings();
		for (Messdiener messdiener : hauptarray) {
		int id = 0;
		if (messdiener.isIstLeiter()) {
		id++;
		}
		int ii = e.getDaten(id).getAnz_dienen();
		if (ii != 0 && i < m.getNochBenoetigte()) {
		new Erroropener(new Exception("<html><body>Bei der Messe: " + m.getID()
		+ "<br></br>herrscht GRO" + References.GROssenSZ
		+ "E Messdiener-Knappheit</br><br>Daher wird wohl" + messdiener.makeId()
		+ "einspringen m" + References.ue + "ssen.</br></body></html>"));
		rtn.add(messdiener);
		}
		}
		} else {
		new Erroropener(new Exception("<html><body>Die Messe:<br>" + m.getID()
		+ "</br><br>hat schon neue Messdiener bekommen, die schon zu oft eingeteilt sind, aber es herrscht Messdiener-Knappheit</br></body></html>"));
		}
		}
		rtn.sort(Messdiener.einteilen);
		return rtn;
		}*/

		public ArrayList<Messdiener> get(StandartMesse sm, Messe m) {
		ArrayList<Messdiener> al = new ArrayList<>();
		for (Messdiener medi : hauptarray) {
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = DateienVerwalter.dv.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm) && ii != 0
					&& medi.getMessdatenDaten().kann(m.getDate(), false)) {
				al.add(medi);
			}
		}
		Collections.shuffle(al);
		al.sort(Messdiener.einteilen);
		return al;
		}

		public ArrayList<Messdiener> get(StandartMesse sm, Date d) {
		ArrayList<Messdiener> al = new ArrayList<>();
		ArrayList<Messdiener> al2 = new ArrayList<>();
		for (Messdiener medi : hauptarray) {
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = DateienVerwalter.dv.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm) && ii != 0) {
				if (medi.getMessdatenDaten().kann(d, false)) {
					al.add(medi);
				} else if (medi.getMessdatenDaten().kann(d, true)) {
					al2.add(medi);
				}
			}
		}
		Collections.shuffle(al);
		Collections.shuffle(al2);
		al.sort(Messdiener.einteilen);
		/*
		 * for (int i = 0; i < al.size(); i++) {
		 * System.out.println(al.get(i).getMessdatenDaten().getAnz_messen() + "/" +
		 * al.get(i).getMessdatenDaten().getMax_messen()); }
		 * /
		/*
		 * al2.sort(Messdiener.einteilen); for (int i = 0; i < al2.size(); i++) {
		 * System.out.println(al2.get(i).getMessdatenDaten().getAnz_messen() + "/" +
		 * al2.get(i).getMessdatenDaten().getMax_messen()); }
		 * /
		 */
		al.addAll(al2);
		/*
		 * for (int i = 0; i < al.size(); i++) {
		 * System.out.println(al.get(i).getMessdatenDaten().getAnz_messen() + "/" +
		 * al.get(i).getMessdatenDaten().getMax_messen()); }
		 * /
		return al;*/
		return new ArrayList<>();
		}
}
