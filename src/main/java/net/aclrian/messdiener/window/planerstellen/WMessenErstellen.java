package net.aclrian.messdiener.window.planerstellen;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import net.aclrian.convertFiles.Converter;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.differenzierung.Einstellungen;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AData;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.RemoveDoppelte;
import net.aclrian.messdiener.utils.Utilities;
import java.awt.Panel;

public class WMessenErstellen extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -7975062959588841974L;
	private JPanel contentPane;
	private Messdiener[] me;
	// private ArrayList<Messe> m;
	private final ArrayList<Messe> messen;
	private ArrayList<Messdiener> hauptarray = new ArrayList<Messdiener>();
	private JEditorPane editorPane = new JEditorPane("text/html", "");
	private DefaultListModel<Messdiener> dlm = new DefaultListModel<Messdiener>();
	private JList<Messdiener> list = new JList<>(dlm);
	private Converter con;
	
	private enum EnumAction {
		EinfachEinteilen(), TypeBeachten();
	}

	/**
	 * Create the frame.
	 */
	public WMessenErstellen(Messdiener[] me, ArrayList<Messe> m, AProgress ap) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Messen anzeigen");
		setIconImage(References.getIcon());
		setBounds(Utilities.setFrameMittig(987, 460));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.me = me;
		this.messen = m;
		neuerAlgorythmus(ap);
		StringBuffer s = new StringBuffer("<html>");
		for (int i = 0; i<m.size(); i++) {
			Messe messe = m.get(i);
			String m1 = messe.htmlAusgeben();
			m1 = m1.substring(6, m1.length()-7);
			if (i == 0) {
				Date start = messe.getDate();
				Date ende = m.get(m.size()-1).getDate();
				SimpleDateFormat df = new SimpleDateFormat("dd. MMMM", Locale.GERMAN);
				String text = "Messdienerplan vom " + df.format(start) + " bis " + df.format(ende);
				s.append("<h1>"+text+"</h1>");
			}
			s.append("<br>" + m1+ "</br>");
		}
		s.append("</html>");
		
		for (Messdiener medi : hauptarray) {
			if(medi.getMessdatenDaten().getInsgesamtEingeteilt() == 0) {
				dlm.addElement(medi);
			}
		}
		JScrollPane scp = new JScrollPane();
		scp.setBounds(747, 7, 220, 381);
		scp.setViewportView(list);
		scp.setColumnHeaderView(new JLabel("Nicht eingeteilte Messdiener:"));
		contentPane.add(scp);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 7, 679, 381);
		contentPane.add(scrollPane);
		HTMLEditorKit editorkit = new HTMLEditorKit();
		editorPane.setEditorKit(editorkit);
		scrollPane.setViewportView(editorPane);

		editorPane.setText(s.toString());

		JLabel lblFertig = new JLabel("Der fertig generierte Messdienerplan:");
		scrollPane.setColumnHeaderView(lblFertig);
		
		Panel panel = new Panel();
		panel.setBounds(10, 387, 679, 33);
		contentPane.add(panel);
				panel.setLayout(null);
		
				JButton btnZumWorddokument = new JButton("Als Worddokument "+ References.oe+"ffnen (Beta)");
				btnZumWorddokument.setBounds(19, 5, 254, 23);
				panel.add(btnZumWorddokument);
				
						JButton btnStatistik = new JButton("Statistik");
						btnStatistik.setBounds(292, 5, 92, 23);
						panel.add(btnStatistik);
						JButton btnZumPdfdokument = new JButton("Als PDF-Dokument "+ References.oe+"ffnen (Beta)");
						btnZumPdfdokument.setBounds(403, 5, 254, 23);
						panel.add(btnZumPdfdokument);
						btnZumPdfdokument.setVisible(true);
						btnZumPdfdokument.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								pdf();
							}
						});
						btnStatistik.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								statistic();
							}
						});
				btnZumWorddokument.setVisible(true);
				btnZumWorddokument.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							word();
						} catch (IOException e1) {
							new Erroropener(e1.getMessage() + ": Konnte die Word-Datei nicht speichern.");
							e1.printStackTrace();
						}

					}
				});

		try {
			con = new Converter(this);
		} catch (IOException e1) {
 			new Erroropener(e1.getMessage());
			e1.printStackTrace();
		}
		//---------------.-------------
		Toolkit.getDefaultToolkit().beep();
	}


	/*
	 * @SuppressWarnings("unused") private void einteilen(Messe m,
	 * ArrayList<Messdiener> anvertraute, int secondndchance) { if
	 * (m.istFertig()) { // System.out.println(m.ausgeben() +
	 * "\nwar ein Erfolg!"); return; } Random ran = new Random(); if
	 * (anvertraute.size() >= 1) { for (int i = 0; i < anvertraute.size(); i++)
	 * { int r = ran.nextInt(anvertraute.size()); Messdiener medi =
	 * anvertraute.get(r); m.einteilen(medi); } }
	 * 
	 * }
	 * 
	 * public void einteilen(Messe m, int runs) { if (m.istFertig()) { return; }
	 * Random ran = new Random(); int z = ran.nextInt(me.length); Messdiener
	 * medi = me[z]; // System.out.println(medi.makeId()); if
	 * (medi.getMessdatenDaten().kann(m.getDate())) { if (!medi.isIstLeiter()) {
	 * if (m.getEnumdfMesse() != EnumdeafaultMesse.Sonstiges) { if
	 * (medi.getDienverhalten().getBestimmtes(m.getEnumdfMesse())) {
	 * m.einteilen(medi); /* int i = ran.nextInt(10); if (i <= 3) { einteilen(m,
	 * medi.getMessdatenDaten().getPrioAnvertraute(), 2); } einteilen(m,
	 * medi.getMessdatenDaten().getPrioAnvertraute(), 0);
	 * 
	 * ArrayList<Messdiener> geschwis =
	 * medi.getMessdatenDaten().getGeschwister(); for (Messdiener messdiener :
	 * geschwis) { einteilen(m, messdiener, null); } } else if (runs >=
	 * me.length * 1.5) { // case: "Dienstagaben problem" int i =
	 * ran.nextInt(me.length); Messdiener med = me[i]; if
	 * (!med.getMessdatenDaten().getDateVonMessen().contains(m.getDate())) {
	 * 
	 * boolean keinleiter = false; while (!keinleiter) { if (!med.isIstLeiter())
	 * { keinleiter = true; } else { System.out.println("DAP: " + med +
	 * " hat Glück gehabt!"); i = ran.nextInt(me.length); med = me[i]; } }
	 * m.einteilenZwang(med); System.out.println("DAP: war nicht nett zu " +
	 * med.makeId()); } }
	 * 
	 * } else { m.einteilen(medi); } } } else { if (runs >= me.length * 1.5) {
	 * // case: "zu viele Messen, zu wenige Messdiener" int i =
	 * ran.nextInt(me.length); Messdiener med = me[i]; if
	 * (!med.getMessdatenDaten().getDateVonMessen().contains(m.getDate())) {
	 * boolean keinleiter = false; while (!keinleiter) { if (!med.isIstLeiter())
	 * { keinleiter = true; } else { System.out.println("ZVMZWM: " + med +
	 * " hat Glück gehabt!"); i = ran.nextInt(me.length); med = me[i]; }
	 * 
	 * } m.einteilenZwang(med); System.out.println("ZVMZWM: war nicht nett zu "
	 * + med.makeId() + " runs: " + runs); } } if (runs >= me.length * 2) { //
	 * stachoverflow return; } } einteilen(m, runs + 1);
	 * 
	 * }/*
	 * 
	 * @SuppressWarnings("null") private void einteilen(Messe m, Messdiener
	 * medi) { if (m.istFertig(false)) { if (!m.istFertig(true)) {
	 * einteilenFuerLeiter(m, (Boolean)null); } return; } m.einteilen(medi);
	 *
	 * }
	 * 
	 * 
	 * private void einteilen(Messe m, Messdiener anvertrauter, Object ohj) { if
	 * (m.istFertig()) { // System.out.println(m.ausgeben() +
	 * "\nwar ein Erfolg!"); return; } if
	 * (anvertrauter.getMessdatenDaten().kann(m.getDate())) {
	 * m.einteilen(anvertrauter); } /* Random ran = new Random(); if
	 * (anvertraute.size() >= 1) { for (int i = 0; i < anvertraute.size(); i++)
	 * { int r = ran.nextInt(anvertraute.size()); Messdiener medi =
	 * anvertraute.get(r); m.einteilen(medi); } }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */
	public ArrayList<Messe> getFertigeMessen() {
		return messen;
	}

	public ArrayList<Messe> getFertigeMessenZumBearbeiten() {
		ArrayList<Messe> rtn = messen;
		return rtn;
	}

	public void pdf() {
		Desktop d = Desktop.getDesktop();
		try {
			d.open(con.getPfd());
		} catch (IOException e) {
 			new Erroropener(e.getMessage());
			e.printStackTrace();
		} catch (Docx4JException e) {
 			new Erroropener(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
 			new Erroropener(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void statistic() {
		String messen = editorPane.getText();
		String statistic = "";
		for (Messdiener medi : me) {
			String s = medi.makeId() + ": " + medi.getMessdatenDaten().getInsgesamtEingeteilt() + "/"
					+ medi.getMessdatenDaten().getMax_messenInt() + "<br>";
			statistic += s;
		}
		statistic = statistic + messen;
		editorPane.setText(statistic);
	}

	public void word() throws IOException {
        Converter con = new Converter(this);
       try {
		Desktop d = Desktop.getDesktop();
		d.open(con.toWord());
	} catch (JAXBException e) {
		new Erroropener("Es gab einen internen Fehler. Bitte mit Copy Paste arbeiten.");
		e.printStackTrace();
	} catch (Docx4JException e) {
		System.err.println("Konnte nicht formatieren:");
		new Erroropener("Das Programm konnte den Plan nicht zu einer Word-Datei umwandeln.\nBitte mit Copy Paste arbeiten.");
		e.printStackTrace();
	}
        //f.deleteOnExit();
	}

	public void neuerAlgorythmus(AProgress ap) {
		for (Messdiener messdiener : me) {
			hauptarray.add(messdiener);
		}
		hauptarray.sort(Messdiener.einteilen);
		for (int i = 0; i < hauptarray.size(); i++) {
			System.out.println(hauptarray.get(i).getMessdatenDaten().getAnz_messen() + "/" + hauptarray.get(i).getMessdatenDaten().getMax_messen());
		}
		System.out.println("-------");
		// Dap und zvmzwm fruehzeitg erkennen und beheben
		// ArrayList<ArrayList<E>>
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
					int ii = ap.getAda().getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
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
				System.out.println();
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
						int medishinzufuegen = (int) Math.ceil((double) (fehlt / array.size()));//abrunden
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Es wurde abgerundet: "+ (double) (fehlt / array.size()) + "-->" + medishinzufuegen);
						for (Messdiener messdiener : array) {
							messdiener.getMessdatenDaten().addtomaxanz(medishinzufuegen, ap.getAda(), messdiener.isIstLeiter());
						}
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Zu den Messdienern, die am " + sm.getWochentag() + " um "
								+ sm.getBeginn_stunde() + " koennen, werden + " + medishinzufuegen
								+ " zu ihrem normalen Wert hinzugefuegt!");

					} catch (ArithmeticException e) {
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Kein Messdiener kann: " + sm.getWochentag() + sm.getBeginn_stunde() + ":"
								+ sm.getBeginn_minute());
					}
				}
			}
		}
		// DAV UND ZWMZVM ENDE
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), References.Ue+"berpruefung zu ende!");
		// neuer Monat:
		Calendar start = Calendar.getInstance();
		start.setTime(messen.get(0).getDate());
		start.add(Calendar.MONTH, 1);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "n"+References.ae+"chster Monat bei: " + df.format(start.getTime()));
		// EIGENTLICHER ALGORYTHMUS
		for (int i = 0; i<messen.size();i++) {
			Messe me = messen.get(i);
			// while(m.isfertig() || stackover){
			if (me.getDate().after(start.getTime())) {
				start.add(Calendar.MONTH, 1);
				Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "naechster Monat: Es ist " + df.format(me.getDate()));
				for (Messdiener messdiener : hauptarray) {
					messdiener.getMessdatenDaten().naechsterMonat();
				}
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"Messe dran: " + me.getID());
			if (me.getStandardMesse() instanceof Sonstiges) {
				this.einteilen(me, EnumAction.EinfachEinteilen, ap);
			} else {
				this.einteilen(me, EnumAction.TypeBeachten, ap);
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"Messe fertig: " + me.getID());
		}
	}

	private void einteilen(Messe m, EnumAction act, AProgress ap) {
		switch (act) {
		case EinfachEinteilen:
			ArrayList<Messdiener> medis;
			boolean zwang = false;
			try {
				medis = get(AData.sonstiges, m, ap.getAda());
			} catch (Exception e) {
				Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), e.getMessage());
				medis = beheben(m, ap.getAda());
				zwang = true;
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "\t" + medis.size() + " f"+References.ue+"r " + m.getnochbenoetigte());
			for (int j = 0; j < medis.size(); j++) {
				einteilen(m, medis.get(j), zwang, ap);
			}
			break;
		case TypeBeachten:
			ArrayList<Messdiener> medis2;
			boolean zwang2 = false;
			try {
				medis2 = get(m.getStandardMesse(), m, ap.getAda());
			} catch (Exception e) {
				Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), e.getMessage());
				medis2 = beheben(m, ap.getAda());
				zwang2 = true;
			}
			System.out.println(medis2.toString() + medis2.size());
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "\t" + medis2.size() + " f"+References.ue+"r " + m.getnochbenoetigte());
			for (int j = 0; j < medis2.size();) {
				System.out.println("drin");
				while (!m.istFertig()) {
					System.out.println("dr"+j+"n");
					einteilen(m, medis2.get(j), zwang2, ap);
					j++;
				}
				break;
			}
			System.out.println("fertig!");
			break;
		default:
			break;
		}
	}

	public void einteilen(Messe m, Messdiener medi, boolean zwang, AProgress ap) {
		boolean d = false;
		if (m.istFertig()) {
			System.out.println("fertig!!");
			return;
		} else if (zwang) {
			if (medi.getMessdatenDaten().kann(m.getDate(),zwang)) {
				m.einteilenZwang(medi);
				d = true;
			}
		} else {
			if (medi.getMessdatenDaten().kann(m.getDate(),zwang)) {
				m.einteilen(medi);
				d = true;
			}
		}
		if (!m.istFertig() && d == true) {
			ArrayList<Messdiener> anv = medi.getMessdatenDaten().getAnvertraute(ap.getMediarraymitMessdaten());
			RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
			rd.removeDuplicatedEntries(anv);
			if (anv.size() >= 1) {
				anv.sort(Messdiener.einteilen);
				for (Messdiener messdiener : anv) {
					boolean b = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse(), ap.getAda());
					if (m.getStandardMesse().toString().startsWith("D")) {
						System.out.println(b);
					}
					
					if (messdiener.getMessdatenDaten().kann(m.getDate(),zwang) && b) {
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "\t" + messdiener.makeId() + " dient mit " + medi.makeId());
						einteilen(m, messdiener, zwang, ap);
					}
				}
			}
		}

	}

	public void einteilen_alt(Messe m, ArrayList<Messdiener> medis, boolean zwang) {
		medis.sort(Messdiener.einteilen);
		for (int i = 0; i < medis.size(); i++) {
			if (m.istFertig()) {
				break;
			}
			if (zwang) {
				m.einteilenZwang(medis.get(i));
			} else {
				m.einteilen(medis.get(i));
			}
			if (m.istFertig()) {
				break;
			}
			ArrayList<Messdiener> anvertraaute = medis.get(i).getMessdatenDaten().getAnvertraute();
			if (anvertraaute.size() > 0) {
				ArrayList<Messdiener> kann = new ArrayList<Messdiener>();
				for (Messdiener messdiener : anvertraaute) {
					if (messdiener.getMessdatenDaten().kann(m.getDate(), zwang)) {
						kann.add(messdiener);
						medis.remove(messdiener);
					}
				}
				RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
				rd.removeDuplicatedEntries(kann);
				kann.sort(Messdiener.einteilen);
				kann.remove(medis.get(i));
				medis.addAll(i, kann);
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
				if (messdiener.getMessdatenDaten().kanndann(m.getDate(),false) && i < m.getnochbenoetigte()) {
					prov.add(messdiener);
					i++;
				}
			}
			for (Messdiener messdiener : prov) {
				new Erroropener("<html><body>Bei der Messe: " + m.getID()
						+ "<br></br>herrscht Messdiener-Knappheit</br><br>Daher wird wohl" + messdiener.makeId()
						+ "einspringen m"+References.ue+"ssen, weil er generell kann.</br></body></html>");
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
					int ii = e.getDaten()[id].getAnz_dienen();
					if (ii!=0 && i < m.getnochbenoetigte()) {
						new Erroropener("<html><body>Bei der Messe: " + m.getID()
								+ "<br></br>herrscht GRO"+References.GROssenSZ+"E Messdiener-Knappheit</br><br>Daher wird wohl"
								+ messdiener.makeId() + "einspringen m"+References.ue+"ssen.</br></body></html>");
						rtn.add(messdiener);
					}
				}
			} else {
				new Erroropener("<html><body>Die Messe:<br>" + m.getID()
						+ "</br><br>hat schon neue Messdiener bekommen, die schon zu oft eingeteilt sind, aber es herrscht Messdiener-Knappheit</br></body></html>");
			}
			// }
		}
		rtn.sort(Messdiener.einteilen);
		return rtn;
	}

	public ArrayList<Messdiener> get(StandartMesse sm, Messe m, AData ada) throws Exception {
		ArrayList<Messdiener> al = new ArrayList<Messdiener>();
		for (Messdiener medi : hauptarray) {
			// System.out.println(medi.makeId());
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = ada.getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm, ada) == true && ii!=0
					&& medi.getMessdatenDaten().kann(m.getDate(),false)) {
				al.add(medi);
			}
		}
		Collections.shuffle(al);
		al.sort(Messdiener.einteilen);
		if (al.size() >= m.getnochbenoetigte()) {
			return al;
		}
		throw new NotEnughtMedis(m);
	}

	public ArrayList<Messdiener> get(StandartMesse sm, Date d, AData ada) {
		ArrayList<Messdiener> al = new ArrayList<Messdiener>();
		ArrayList<Messdiener> al2 = new ArrayList<Messdiener>();
		for (Messdiener medi : hauptarray) {
			// System.out.println(medi.makeId());
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = ada.getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm, ada) == true && ii != 0) {
				if(medi.getMessdatenDaten().kann(d,false)) {
				al.add(medi);
				} else if(medi.getMessdatenDaten().kann(d,true)) {
					al2.add(medi);
				}
			}
		}
		Collections.shuffle(al);
		Collections.shuffle(al2);
		al.sort(Messdiener.einteilen);
		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i).getMessdatenDaten().getAnz_messen() + "/" + al.get(i).getMessdatenDaten().getMax_messen());
		}
		System.out.println("-------");
		
		al2.sort(Messdiener.einteilen);
		for (int i = 0; i < al2.size(); i++) {
			System.out.println(al2.get(i).getMessdatenDaten().getAnz_messen() + "/" + al2.get(i).getMessdatenDaten().getMax_messen());
		}
		System.out.println("-------");
		
		al.addAll(al2);
		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i).getMessdatenDaten().getAnz_messen() + "/" + al.get(i).getMessdatenDaten().getMax_messen());
		}
		System.out.println("-------");
		
		return al;
	}

	private class NotEnughtMedis extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1730056704969911415L;
		private Messe m;
		private Thread t = new Thread() {
			@Override
			public void run() {
				new Erroropener(getMessage());
			}
		};

		public NotEnughtMedis(Messe m) {
			this.m = m;
			t.run();
		}

		@Override
		public String getMessage() {
			String s = super.getMessage();
			s += "Zu wenige M"+References.oe+"gliche Messdiener bei: " + m.getID();

			return s;
		}
	}
	
	
	public String getText() {
		return editorPane.getText();
	}
}