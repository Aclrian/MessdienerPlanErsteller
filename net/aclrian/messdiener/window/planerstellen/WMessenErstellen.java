package net.aclrian.messdiener.window.planerstellen;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.aclrian.messdiener.differenzierung.Einstellungen;

import net.aclrian.messdiener.deafault.Messdaten;
/*
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;*/
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.WMainFrame;

public class WMessenErstellen extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -7975062959588841974L;
	private JPanel contentPane;
	private Messdiener[] me;
	// private ArrayList<Messe> m;
	private JEditorPane editorPane = new JEditorPane();
	private final ArrayList<Messe> messen;
	private ArrayList<Messdiener> hauptarray = new ArrayList<Messdiener>();

	private enum EnumAction {
		EinfachEinteilen(), TypeBeachten();
	}

	/**
	 * Create the frame.
	 */
	public WMessenErstellen(Messdiener[] me, ArrayList<Messe> m, WMainFrame wmf) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Messen anzeigen");
		setBounds(Utilities.setFrameMittig(987, 450));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.me = me;
		this.messen = m;
		neuerAlgorythmus(wmf);
		String s = "";
		for (Messe messe : m) {
			s += "\n" + messe.ausgeben();
		}
		s.substring(2);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 7, 679, 381);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(editorPane);

		editorPane.setText("<html><body></body></html>");
		editorPane.setText(s);

		JLabel lblFertig = new JLabel("Der fertig generierte Messdienerplan:");
		scrollPane.setColumnHeaderView(lblFertig);

		JButton btnZumWorddokument = new JButton("Zum Worddokument //TODO");
		btnZumWorddokument.setVisible(true);
		btnZumWorddokument.setBounds(10, 394, 257, 23);
		contentPane.add(btnZumWorddokument);
		btnZumWorddokument.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// word(wmf);

			}
		});
		JButton btnZumPdfdokument = new JButton("Zum PDF-Dokument //TODO");
		btnZumPdfdokument.setVisible(true);
		btnZumPdfdokument.setBounds(443, 394, 257, 23);
		btnZumPdfdokument.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// pdf(wmf, m);
			}
		});
		contentPane.add(btnZumPdfdokument);

		JButton btnStatistik = new JButton("Statistik");
		btnStatistik.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				statistic();
			}
		});
		btnStatistik.setBounds(281, 392, 117, 25);
		contentPane.add(btnStatistik);

		done();

	}

	public void done() {
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
	 * }
	 */
	public ArrayList<Messe> getFertigeMessen() {
		return messen;
	}

	public ArrayList<Messe> getFertigeMessenZumBearbeiten() {
		ArrayList<Messe> rtn = messen;
		return rtn;
	}

	public void pdf(WMainFrame wmf, ArrayList<Messe> messen) {
		/*
		 * DateienVerwalter util = wmf.getEDVVerwalter(); Date[] d =
		 * wmf.getDates();
		 *
		 * SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy"); File file =
		 * new File( util.getPlanSavepath().getAbsolutePath() + df.format(d[0])
		 * + "-" + df.format(d[1]) + ".pdf"); if (!file.exists()) { try {
		 * file.createNewFile(); } catch (IOException e) { e.printStackTrace();
		 * } } try { /*PDDocument document = new PDDocument(); PDPage page = new
		 * PDPage(); document.addPage(page); PDPageContentStream contentStream =
		 * new PDPageContentStream(document, page);
		 *
		 * contentStream.beginText();
		 * contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
		 * contentStream.setLeading(14.5f); // String[] data =
		 * editorPane.getText().split("\r\n"); for (int i = 0; i <
		 * messen.size(); i++) { Messe mes = messen.get(i); ArrayList<String>
		 * list = mes.ausgebenAlsArray(); for (int j = 0; j < list.size(); j++)
		 * { String text = list.get(j); // text = text.replace("\n",
		 * "").replace("\r", ""); contentStream.showText(text);
		 * contentStream.newLine(); } } // String text = "This is the sample
		 * document and we are adding // content to it.";
		 *
		 * contentStream.endText(); contentStream.close();
		 *
		 * document.save(file); document.close();
		 *
		 * System.out.println("PDF created at: " + file.getCanonicalPath()); }
		 * catch (IOException e1) { // Auto-generated catch block
		 * e1.printStackTrace(); }
		 */
	}

	public void statistic() {
		String messen = editorPane.getText();
		String statistic = "";
		for (Messdiener medi : me) {
			String s = medi.makeId() + ": " + medi.getMessdatenDaten().getInsgesamtEingeteilt() + "/"
					+ medi.getMessdatenDaten().getMax_messenInt() + "\n";
			statistic += s;
		}
		statistic = statistic + messen;
		editorPane.setText(statistic);
	}

	public void word(WMainFrame wmf) {
		// DateienVerwalter util = wmf.getEDVVerwalter();
		// Date[] d = wmf.getDates();

		// SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		/*
		 * File file = new File(
		 * 
		 * // EMPTY_DOC_URL.openStream());
		 * util.getPlanSavepath().getAbsolutePath() + df.format(d[0]) + "-" +
		 * df.format(d[1]) + ".doc"); if (!file.exists()) { try {
		 * file.createNewFile(); } catch (IOException e) { e.printStackTrace();
		 * } }
		 * 
		 * // --------------------------------------- /* WordprocessingMLPackage
		 * wordPackage = null; MainDocumentPart mainDocumentPart = null; boolean
		 * error = false; try { wordPackage =
		 * WordprocessingMLPackage.createPackage(); mainDocumentPart =
		 * wordPackage.getMainDocumentPart();
		 * mainDocumentPart.addStyledParagraphOfText("Title", "Hello World!");
		 * mainDocumentPart.addParagraphOfText("Welcome To Baeldung");
		 * wordPackage.save(file); // template =
		 * WordprocessingMLPackage.load(new // FileInputStream(file)); } catch
		 * (Docx4JException e1) { // Auto-generated catch block error = true;
		 * e1.printStackTrace(); } if (!error) { String[] data =
		 * editorPane.getText().split("\r\n"); for (String string : data) {
		 * mainDocumentPart.addParagraphOfText(string); } }
		 *
		 * /* XWPFDocument doc = null; XWPFParagraph para = null; boolean error
		 * = false; try { doc = new XWPFDocument(new FileInputStream(file));
		 * para = doc.createParagraph();
		 * para.setAlignment(ParagraphAlignment.LEFT); } catch
		 * (EmptyFileException e) { System.err.println(file.exists());
		 * System.err.println(file.getAbsoluteFile()); } catch (IOException e) {
		 * // Auto-generated catch block error = true; e.printStackTrace(); }
		 */
		// 7 if (!error) {
	}

	public void neuerAlgorythmus(WMainFrame wmf) {
		for (Messdiener messdiener : me) {
			hauptarray.add(messdiener);
		}
		hauptarray.sort(Messdiener.einteilen);
		// Dap und zvmzwm fruehzeitg erkennen und beheben
		// ArrayList<ArrayList<E>>
		for (StandartMesse sm : wmf.getStandardmessen()) {
			int anz_real = 0;
			int anz_monat = 0;
			ArrayList<Messdiener> array = new ArrayList<Messdiener>();
			if (!(new Sonstiges().isSonstiges(sm))) {
				for (Messdiener medi : hauptarray) {
					int id = 0;
					if (medi.isIstLeiter()) {
						id++;
					}
					int ii = wmf.getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
					if (medi.getDienverhalten().getBestimmtes(sm, wmf) && ii != 0) {
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
					if (wmf.getSonstiges().isSonstiges(me.getStandardMesse())) {
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
							messdiener.getMessdatenDaten().addtomaxanz(medishinzufuegen, wmf, messdiener.isIstLeiter());
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
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Ueberpruefung zu ende!");
		// neuer Monat:
		Calendar start = Calendar.getInstance();
		start.setTime(messen.get(0).getDate());
		start.add(Calendar.MONTH, 1);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "naechster Monat bei: " + df.format(start.getTime()));
		// EIGENTLICHER ALGORYTHMUS
		for (Messe me : messen) {

			// while(m.isfertig() || stackover){
			if (me.getDate().after(start.getTime())) {
				start.add(Calendar.MONTH, 1);
				Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "naechster Monat: Es ist " + df.format(me.getDate()));
				for (Messdiener messdiener : hauptarray) {

					messdiener.getMessdatenDaten().naechsterMonat();
				}
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"Messe ferting: " + me.getID());
			if (wmf.getSonstiges().isSonstiges(me.getStandardMesse())) {
				this.einteilen(me, EnumAction.EinfachEinteilen, wmf);
			} else {
				this.einteilen(me, EnumAction.TypeBeachten, wmf);
			}
		}
	}

	private void einteilen(Messe m, EnumAction act, WMainFrame wmf) {
		switch (act) {
		case EinfachEinteilen:
			ArrayList<Messdiener> medis;
			boolean zwang = false;
			try {
				medis = get(wmf.getSonstiges(), m, wmf);
			} catch (Exception e) {
				Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), e.getMessage());
				medis = beheben(m, wmf);
				zwang = true;
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "\t" + medis.size() + " fuer " + m.getnochbenoetigte());
			for (int j = 0; j < medis.size(); j++) {
				einteilen(m, medis.get(j), zwang);
			}
			break;
		case TypeBeachten:
			ArrayList<Messdiener> medis2;
			boolean zwang2 = false;
			try {
				medis2 = get(m.getStandardMesse(), m, wmf);
			} catch (Exception e) {
				Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), e.getMessage());
				medis2 = beheben(m, wmf);
				zwang2 = true;
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "\t" + medis2.size() + " fuer " + m.getnochbenoetigte());
			for (int j = 0; j < medis2.size(); j++) {
				einteilen(m, medis2.get(j), zwang2);
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
			if (medi.getMessdatenDaten().kann(m.getDate())) {
				m.einteilenZwang(medi);
				d = true;
			}
		} else {
			if (medi.getMessdatenDaten().kann(m.getDate())) {
				m.einteilen(medi);
				d = true;
			}
		}
		if (!m.istFertig() && d == true) {
			ArrayList<Messdiener> anv = medi.getMessdatenDaten().getAnvertraute();
			if (anv.size() >= 1) {
				Messdaten.removeDuplicatedEntries(anv);
				anv.sort(Messdiener.einteilen);
				for (Messdiener messdiener : anv) {
					if (messdiener.getMessdatenDaten().kann(m.getDate())) {
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "\t" + messdiener.makeId() + " dient mit " + medi.makeId());
						einteilen(m, messdiener, zwang);
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
					if (messdiener.getMessdatenDaten().kann(m.getDate())) {
						kann.add(messdiener);
						medis.remove(messdiener);
					}
				}
				Messdaten.removeDuplicatedEntries(kann);
				kann.sort(Messdiener.einteilen);
				kann.remove(medis.get(i));
				medis.addAll(i, kann);
			}
		}
	}

	public ArrayList<Messdiener> beheben(Messe m, WMainFrame wmf) {
		ArrayList<Messdiener> rtn = get(m.getStandardMesse(), m.getDate(), wmf);
		if (rtn.size() < m.getnochbenoetigte()) {
			ArrayList<Messdiener> prov = new ArrayList<Messdiener>();
			hauptarray.sort(Messdiener.einteilen);
			int i = rtn.size();
			for (Messdiener messdiener : hauptarray) {
				if (messdiener.getMessdatenDaten().kanndann(m.getDate()) && i < m.getnochbenoetigte()) {
					prov.add(messdiener);
					i++;
				}
			}
			for (Messdiener messdiener : prov) {
				new Erroropener("<html><body>Bei der Messe: " + m.getID()
						+ "<br></br>herrscht Messdiener-Knappheit</br><br>Daher wird wohl" + messdiener.makeId()
						+ "einspringen muessen, weil er generell kann.</br></body></html>");
			}
			rtn.addAll(prov);
			// Wenn wirklich keiner mehr kann
			if (rtn.size() < m.getnochbenoetigte()) {
				hauptarray.sort(Messdiener.einteilen);
				Einstellungen e = wmf.getPfarrei().getSettings();
				for (Messdiener messdiener : hauptarray) {
					int id = 0;
					if (messdiener.isIstLeiter()) {
						id++;
					}
					int ii = e.getDaten()[id].getAnz_dienen();
					if (ii!=0 && i < m.getnochbenoetigte()) {
						new Erroropener("<html><body>Bei der Messe: " + m.getID()
								+ "<br></br>herrscht GROssE Messdiener-Knappheit</br><br>Daher wird wohl"
								+ messdiener.makeId() + "einspringen muessen.</br></body></html>");
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

	public ArrayList<Messdiener> get(StandartMesse sm, Messe m, WMainFrame wmf) throws Exception {
		ArrayList<Messdiener> al = new ArrayList<Messdiener>();
		for (Messdiener medi : hauptarray) {
			// System.out.println(medi.makeId());
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = wmf.getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm, wmf) == true && ii!=0
					&& medi.getMessdatenDaten().kann(m.getDate())) {
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

	public ArrayList<Messdiener> get(StandartMesse sm, Date d, WMainFrame wmf) {
		ArrayList<Messdiener> al = new ArrayList<Messdiener>();
		for (Messdiener medi : hauptarray) {
			// System.out.println(medi.makeId());
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = wmf.getPfarrei().getSettings().getDaten()[id].getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm, wmf) == true && ii != 0
					&& medi.getMessdatenDaten().kann(d)) {
				al.add(medi);
			}
		}
		Collections.shuffle(al);
		al.sort(Messdiener.einteilen);
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
			s += "Zu wenige Moegliche Messdiener bei: " + m.getID();

			return s;
		}
	}
	/*
	 * private void alterAlgorthythmus(ArrayList<Messe> m) { Calendar start =
	 * Calendar.getInstance(); start.setTime(m.get(0).getDate());
	 * start.add(Calendar.MONTH, 1); SimpleDateFormat df = new
	 * SimpleDateFormat("dd-MM-yyyy"); for (int i = 0; i < m.size(); i++) { try
	 * { if (m.get(i).getDate().after(start.getTime())) {
	 * System.err.println("neuer Monat:"); for (Messdiener medi : me) {
	 * medi.getMessdatenDaten().naechsterMonat(); } start.add(Calendar.MONTH,
	 * 1); } // System.err.println(start.before(m.get(i).getDate())); //
	 * System.out.println("DRAN: " + m.get(i).getID()); //
	 * System.out.println(m.get(i).getEnumdeafaultMesse()); einteilen(m.get(i),
	 * 0); } catch (Exception e) { e.printStackTrace(); } } } }
	 */
}