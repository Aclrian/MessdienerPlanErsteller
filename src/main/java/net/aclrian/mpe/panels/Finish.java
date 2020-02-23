package net.aclrian.mpe.panels;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.start.References;
import net.aclrian.mpe.start.AData;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.start.WEinFrame;
import net.aclrian.mpe.start.WEinFrame.EnumActivePanel;
import net.aclrian.mpe.utils.Converter;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.RemoveDoppelte;
import net.aclrian.mpe.utils.Utilities;

enum EnumAction {
	EinfachEinteilen(), TypeBeachten();
}

public class Finish extends APanel {
	private static final long serialVersionUID = 1100706202326699632L;
	private JEditorPane editorPane = new JEditorPane("text/html", "");
	private DefaultListModel<Messdiener> dlm = new DefaultListModel<Messdiener>();
	private JList<Messdiener> list = new JList<Messdiener>(dlm);
	private JScrollPane sPMessen = new JScrollPane();
	private JScrollPane sPMedis = new JScrollPane();
	private JLabel labelmesse = new JLabel("Die generierten Messen:");
	private JLabel labelmedis = new JLabel("Nicht eingeteilte Messdiener:");
	private JButton topdf = new JButton("Zum Pdf-Dokument");
	private JButton todocx = new JButton("Zum Word-Dokument");
	private JButton toback = new JButton("Zur" + References.ue + "ck");
	private ArrayList<Messdiener> hauptarray;
	private ArrayList<Messe> messen;

	public Finish(int defaultButtonwidth, int defaultButtonheight, boolean ferienplan, AProgress ap) {
		super(defaultButtonwidth, defaultButtonheight, false, "Messdienerplan anzeigen", ap);
		HTMLEditorKit editorkit = new HTMLEditorKit();
		editorPane.setEditorKit(editorkit);
		sPMessen.setViewportView(editorPane);
		sPMessen.setColumnHeaderView(labelmesse);
		sPMedis.setViewportView(list);
		sPMedis.setColumnHeaderView(labelmedis);
		editorPane.setFont(new Font(Font.SANS_SERIF, editorPane.getFont().getSize(), editorPane.getFont().getSize()));
		editorPane.setText("<html><h1>Der Fertige Messdienerplan</h1></html>");
		add(sPMedis);
		add(sPMessen);

		add(todocx);
		todocx.addActionListener(e -> open(true));

		add(topdf);
		topdf.addActionListener(e -> open(false));
		add(toback);
		toback.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ferienplan) {
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
				}
			}
		});
		this.hauptarray = ap.getMediarraymitMessdaten();
		this.messen = ap.getAda().getMesenarray();
		// Generieren:
		// Vorzeitig
		for (Messe messe : messen) {
			ArrayList<Messdiener> vor = ap.getAda().getVoreingeteilte().get(messe);
			if (vor != null) {
				for (int i = 0; i < vor.size(); i++) {
					Messdiener messdiener = vor.get(i);
					for (Messdiener messdiener2 : ap.getMediarraymitMessdaten()) {
						if (messdiener.toString().equals(messdiener2.toString())) {
							messe.vorzeitigEiteilen(messdiener2, ap.getAda());
						}
					}
				}
			}
		}
		neuerAlgorythmus(ap);
		// to JEditPane
		StringBuffer s = new StringBuffer("<html>");
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
		editorPane.setText(s.toString());
		for (Messdiener medi : hauptarray) {
			if (medi.getMessdatenDaten().getInsgesamtEingeteilt() == 0) {
				dlm.addElement(medi);
			}
		}
		// Ende
		graphics();
		setVisible(true);
		Toolkit.getDefaultToolkit().beep();
	}

	public void zurueck(boolean loeschen, EnumActivePanel eap ,AProgress ap) {
		if (loeschen) {
			for (Messdiener medi : ap.getMediarraymitMessdaten()) {
				medi.getMessdatenDaten().nullen();
			}
			for (Messe m : ap.getAda().getMesenarray()) {
				m.nullen();
			}
		}
		setVisible(false);
		ap.getWAlleMessen().changeAP(eap, true);
	}

	private void open(boolean isword) {
		Converter con;
		try {
			con = new Converter(this);
			File f = (isword) ? con.getDocx() : con.toPDF();
			Desktop d = Desktop.getDesktop();
			d.open(f);
		} catch (IOException e) {
			new Erroropener(e);
			e.printStackTrace();
		}
	}

	public void neuerAlgorythmus(AProgress ap) {
		hauptarray.sort(Messdiener.einteilen);
		// Dap und zvmzwm fruehzeitg erkennen und beheben
		for (StandartMesse sm : ap.getAda().getPfarrei().getStandardMessen()) {
			int anz_real = 0;
			int anz_monat = 0;
			ArrayList<Messdiener> array = new ArrayList<Messdiener>();
			if (!(AData.sonstiges.isSonstiges(sm))) {
				for (Messdiener medi : hauptarray) {
					int id = 0;
					if (medi.isIstLeiter()) {
						id++;
					}
					int ii = ap.getAda().getPfarrei().getSettings().getDaten(id).getAnz_dienen();
					if (medi.getDienverhalten().getBestimmtes(sm, ap.getAda()) && ii != 0) {
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
					if (!AData.sonstiges.isSonstiges(me.getStandardMesse())) {
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
						int medishinzufuegen = (int) Math.ceil((double) (fehlt / array.size()));// abrunden
						Utilities.logging(this.getClass(), "neuerAlorythmus",
								"Es wurde abgerundet: " + (double) (fehlt / array.size()) + "-->" + medishinzufuegen);
						for (Messdiener messdiener : array) {
							messdiener.getMessdatenDaten().addtomaxanz(medishinzufuegen, ap.getAda(),
									messdiener.isIstLeiter());
						}
						String mitteilung = "Zu den Messdienern, die am " + sm.getWochentag() + " um " + sm.getBeginn_stunde()
								+ " koennen, werden + " + medishinzufuegen
								+ " zu ihrem normalen Wert hinzugef"+References.ue+"gt!";
						Utilities.logging(this.getClass(), "neuerAlorythmus",
								mitteilung);
						new Erroropener(new Exception(mitteilung));
					} catch (ArithmeticException e) {
						Utilities.logging(this.getClass(), "neuerAlorythmus", "Kein Messdiener kann: "
								+ sm.getWochentag() + sm.getBeginn_stunde() + ":" + sm.getBeginn_minute());
					}
				}
			}
		}
		// DAV UND ZWMZVM ENDE
		Utilities.logging(this.getClass(), "neuerAlorythmus", References.Ue + "berpruefung zu ende!");
		// neuer Monat:
		Calendar start = Calendar.getInstance();
		start.setTime(messen.get(0).getDate());
		start.add(Calendar.MONTH, 1);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Utilities.logging(this.getClass(), "neuerAlorythmus",
				"n" + References.ae + "chster Monat bei: " + df.format(start.getTime()));
		// EIGENTLICHER ALGORYTHMUS
		for (int i = 0; i < messen.size(); i++) {
			Messe me = messen.get(i);
			// while(m.isfertig() || stackover){
			if (me.getDate().after(start.getTime())) {
				start.add(Calendar.MONTH, 1);
				Utilities.logging(this.getClass(), "neuerAlorythmus",
						"naechster Monat: Es ist " + df.format(me.getDate()));
				for (Messdiener messdiener : hauptarray) {
					messdiener.getMessdatenDaten().naechsterMonat();
				}
			}
			Utilities.logging(this.getClass(), "neuerAlorythmus", "Messe dran: " + me.getID());
			if (me.getStandardMesse() instanceof Sonstiges) {
				this.einteilen(me, EnumAction.EinfachEinteilen, ap);
			} else {
				this.einteilen(me, EnumAction.TypeBeachten, ap);
			}
			Utilities.logging(this.getClass(), "neuerAlorythmus", "Messe fertig: " + me.getID());
		}
	}

	private void einteilen(Messe m, EnumAction act, AProgress ap) {
		switch (act) {
		case EinfachEinteilen:
			ArrayList<Messdiener> medis;
			boolean zwang = false;
			// try {
			medis = get(AData.sonstiges, m, ap.getAda());
			// } catch (Exception e) {
			// Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
			// e.getMessage());
			// medis = beheben(m, ap.getAda());
			// zwang = true;
			// }
			Utilities.logging(this.getClass(), "einteilen",
					"\t" + medis.size() + " f" + References.ue + "r " + m.getnochbenoetigte());
			if (m.getnochbenoetigte() > medis.size()) {
				Utilities.logging(this.getClass(), "einteilen", "Die Messe " + m.getID() + "hat zu wenige Messdiener");
				new Erroropener(new Exception("Die Messe " + m.getID() + "hat zu wenige Messdiener"));
			}
			for (int j = 0; j < medis.size(); j++) {
				einteilen(m, medis.get(j), zwang, ap);
			}
			break;
		case TypeBeachten:
			ArrayList<Messdiener> medis2;
			boolean zwang2 = false;
			// try {
			medis2 = get(m.getStandardMesse(), m, ap.getAda());
			// } catch (Exception e) {
			// Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
			// e.getMessage());
			// medis2 = beheben(m, ap.getAda());
			// zwang2 = true;
			// }
			Utilities.logging(this.getClass(), "einteilen",
					"\t" + medis2.size() + " f" + References.ue + "r " + m.getnochbenoetigte());
			if (m.getnochbenoetigte() > medis2.size()) {
				Utilities.logging(this.getClass(), "einteilen", "Die Messe " + m.getID() + "hat zu wenige Messdiener");
				new Erroropener(new Exception("Die Messe " + m.getID() + "hat zu wenige Messdiener"));
			}
			for (int j = 0; j < medis2.size(); j++) {
				einteilen(m, medis2.get(j), zwang2, ap);
			}
			break;
		default:
			break;
		}
	}

	public void einteilen(Messe m, Messdiener medi, boolean zwang, AProgress ap) {
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
		if (!m.istFertig() && d == true) {
			ArrayList<Messdiener> anv = medi.getMessdatenDaten().getAnvertraute(ap.getMediarraymitMessdaten());

			RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<Messdiener>();
			rd.removeDuplicatedEntries(anv);
			if (anv.size() >= 1) {
				anv.sort(Messdiener.einteilen);
				for (Messdiener messdiener : anv) {
					boolean b = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse(), ap.getAda());
					if (messdiener.getMessdatenDaten().kann(m.getDate(), zwang) && b) {
						Utilities.logging(this.getClass(), "einteilen2",
								"\t" + messdiener.makeId() + " dient mit " + medi.makeId());
						einteilen(m, messdiener, zwang, ap);
					}
				}
			}
		}
	}

	public ArrayList<Messdiener> beheben(Messe m, AData ada) {
		ArrayList<Messdiener> rtn = get(m.getStandardMesse(), m.getDate(), ada);
		if (rtn.size() < m.getnochbenoetigte()) {
			ArrayList<Messdiener> prov = new ArrayList<Messdiener>();
			hauptarray.sort(Messdiener.einteilen);
			int i = rtn.size();
			for (Messdiener messdiener : hauptarray) {
				if (messdiener.getMessdatenDaten().kanndann(m.getDate(), false) && i < m.getnochbenoetigte()) {
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
			if (rtn.size() < m.getnochbenoetigte()) {
				hauptarray.sort(Messdiener.einteilen);
				Einstellungen e = ada.getPfarrei().getSettings();
				for (Messdiener messdiener : hauptarray) {
					int id = 0;
					if (messdiener.isIstLeiter()) {
						id++;
					}
					int ii = e.getDaten(id).getAnz_dienen();
					if (ii != 0 && i < m.getnochbenoetigte()) {
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
	}

	public ArrayList<Messdiener> get(StandartMesse sm, Messe m, AData ada) {
		ArrayList<Messdiener> al = new ArrayList<Messdiener>();
		for (Messdiener medi : hauptarray) {
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = ada.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm, ada) == true && ii != 0
					&& medi.getMessdatenDaten().kann(m.getDate(), false)) {
				al.add(medi);
			}
		}
		Collections.shuffle(al);
		al.sort(Messdiener.einteilen);
		return al;
	}

	public ArrayList<Messdiener> get(StandartMesse sm, Date d, AData ada) {
		ArrayList<Messdiener> al = new ArrayList<Messdiener>();
		ArrayList<Messdiener> al2 = new ArrayList<Messdiener>();
		for (Messdiener medi : hauptarray) {
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = ada.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm, ada) == true && ii != 0) {
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
		 */
		/*
		 * al2.sort(Messdiener.einteilen); for (int i = 0; i < al2.size(); i++) {
		 * System.out.println(al2.get(i).getMessdatenDaten().getAnz_messen() + "/" +
		 * al2.get(i).getMessdatenDaten().getMax_messen()); }
		 */
		al.addAll(al2);
		/*
		 * for (int i = 0; i < al.size(); i++) {
		 * System.out.println(al.get(i).getMessdatenDaten().getAnz_messen() + "/" +
		 * al.get(i).getMessdatenDaten().getMax_messen()); }
		 */
		return al;
	}

	public String getText() {
		return editorPane.getText();
	}

	@Override
	public void graphics() {
		int width = this.getBounds().width;
		int heigth = this.getBounds().height;
		int drei = width / 3;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		int stdhoehe = heigth / 20;
		sPMedis.setBounds(abstandweit, abstandhoch + stdhoehe, drei - abstandweit,
				heigth - this.getDfbtnheight() - 4 * abstandhoch - stdhoehe);
		sPMessen.setBounds(drei + abstandweit, abstandhoch + stdhoehe, 2 * drei - 3 * abstandweit,
				heigth - this.getDfbtnheight() - 4 * abstandhoch - stdhoehe);

		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(),
				heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		toback.setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		Rectangle r = toback.getBounds();
		r.x = r.x + width;
		todocx.setBounds((int) (toback.getBounds().x + width * 0.5 - this.getDfbtnwidth() * 0.5), toback.getBounds().y,
				this.getDfbtnwidth(), this.getDfbtnheight());
		topdf.setBounds(width - abstandweit - this.getDfbtnwidth(), heigth - abstandhoch - this.getDfbtnheight(),
				this.getDfbtnwidth(), this.getDfbtnheight());
	}
}
