package net.aclrian.messdiener.window.planerstellen;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.utils.EnumdeafaultMesse;
import net.aclrian.messdiener.utils.Util;
import net.aclrian.messdiener.window.WMainFrame;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;

public class WWMessenErstellen extends JFrame {

	private JPanel contentPane;
	private Messdiener[] me;
	private ArrayList<Messe> m;
	private JEditorPane editorPane = new JEditorPane();

	/**
	 * Create the frame.
	 */
	public WWMessenErstellen(Messdiener[] me, ArrayList<Messe> m, WMainFrame wmf) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 987, 436);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.m = m;
		this.me = me;
		Calendar start = Calendar.getInstance();
		start.setTime(m.get(0).getDate());
		start.add(Calendar.MONTH, 1);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		System.out.println(df.format(m.get(0).getDate()));
		System.out.println(df.format(start.getTime()));
		for (int i = 0; i < m.size(); i++) {
			try {
				if (m.get(i).getDate().after(start.getTime())) {
					System.err.println("neuer Monat:");
					for (Messdiener medi : me) {
						medi.getMessdatenDaten().naechsterMonat();
					}

					start.add(Calendar.MONTH, 1);
				}
				// System.err.println(start.before(m.get(i).getDate()));
				// System.out.println("DRAN: " + m.get(i).getID());
				// System.out.println(m.get(i).getEnumdfMesse());
				einteilen(m.get(i), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

		JButton btnZumWorddokument = new JButton("Zum Worddokument");
		btnZumWorddokument.setVisible(false);
		btnZumWorddokument.setBounds(747, 319, 167, 23);
		contentPane.add(btnZumWorddokument);
		btnZumWorddokument.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				word(wmf);

			}
		});
		JButton btnZumPdfdokument = new JButton("Zum PDF-Dokument");
		btnZumPdfdokument.setVisible(false);
		btnZumPdfdokument.setBounds(747, 365, 167, 23);
		btnZumPdfdokument.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pdf(wmf, m);
			}
		});
		contentPane.add(btnZumPdfdokument);

		JLabel lblFertig = new JLabel(
				"<html><body><br>Der Messdienerplan wurde fertig generier!<br>Der links angezeigteText kann nun in einen Textbearbeiter zu einem Dokument (bspw. .docx / .pdf) gespeichert werden.<br><br>Dazu eignen sich die Tastenkombinationen: <br>Alles ausw\u00E4hlen: Strg + A<br>Kopieren: Strg + C<br>Einf\u00FCgen: Strg + V</html></body>");
		lblFertig.setBounds(735, 7, 212, 301);
		contentPane.add(lblFertig);
		System.out.println("dfggffff");

	}

	public void pdf(WMainFrame wmf, ArrayList<Messe> messen) {
		Util util = wmf.getUtil();
		Date[] d = wmf.getDates();

		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		File file = new File(
				util.getPlanSavepath().getAbsolutePath() + df.format(d[0]) + "-" + df.format(d[1]) + ".pdf");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			document.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);

			contentStream.beginText();
			contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
			contentStream.setLeading(14.5f);
			// String[] data = editorPane.getText().split("\r\n");
			for (int i = 0; i < messen.size(); i++) {
				Messe mes = messen.get(i);
				ArrayList<String> list = mes.ausgebenAlsArray();
				for (int j = 0; j < list.size(); j++) {
					String text = list.get(j);
					// text = text.replace("\n", "").replace("\r", "");
					contentStream.showText(text);
					contentStream.newLine();
				}
			}
			// String text = "This is the sample document and we are adding
			// content to it.";

			contentStream.endText();
			contentStream.close();

			document.save(file);
			document.close();

			System.out.println("PDF created at: " + file.getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void word(WMainFrame wmf) {
		Util util = wmf.getUtil();
		Date[] d = wmf.getDates();

		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		File file = new File(

				// EMPTY_DOC_URL.openStream());
				util.getPlanSavepath().getAbsolutePath() + df.format(d[0]) + "-" + df.format(d[1]) + ".doc");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// ---------------------------------------
		WordprocessingMLPackage wordPackage = null;
		MainDocumentPart mainDocumentPart = null;
		boolean error = false;
		try {
			wordPackage = WordprocessingMLPackage.createPackage();
			mainDocumentPart = wordPackage.getMainDocumentPart();
			mainDocumentPart.addStyledParagraphOfText("Title", "Hello World!");
			mainDocumentPart.addParagraphOfText("Welcome To Baeldung");
			wordPackage.save(file);
			// template = WordprocessingMLPackage.load(new
			// FileInputStream(file));
		} catch (Docx4JException e1) {
			// TODO Auto-generated catch block
			error = true;
			e1.printStackTrace();
		}
		if (!error) {
			String[] data = editorPane.getText().split("\r\n");
			for (String string : data) {
				mainDocumentPart.addParagraphOfText(string);
			}
		}

		/*
		 * XWPFDocument doc = null; XWPFParagraph para = null; boolean error = false;
		 * try { doc = new XWPFDocument(new FileInputStream(file)); para =
		 * doc.createParagraph(); para.setAlignment(ParagraphAlignment.LEFT); } catch
		 * (EmptyFileException e) { System.err.println(file.exists());
		 * System.err.println(file.getAbsoluteFile()); } catch (IOException e) { // TODO
		 * Auto-generated catch block error = true; e.printStackTrace(); }
		 */
		// 7 if (!error) {
	}

	public void einteilen(Messe m, int runs) {
		// System.out.println(runs);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (m.istFertig()) {
			/// System.err.println(runs);
			// System.out.println(m.ausgeben());
			// System.out.println("FERTIG: " + m.getID());
			return;
		}
		Random ran = new Random();
		int z = ran.nextInt(me.length);
		Messdiener medi = me[z];
		// System.out.println(medi.makeId());
		if (medi.getMessdatenDaten().kannnoch()) {
			if (m.getEnumdfMesse() != EnumdeafaultMesse.Sonstiges) {
				if (medi.getDienverhalten().getBestimmtes(m.getEnumdfMesse())) {
					m.einteilen(medi);
					int i = ran.nextInt(10);
					if (i <= 3) {
						einteilen(m, medi.getMessdatenDaten().getPrioAnvertraute(), 2);
					}
					einteilen(m, medi.getMessdatenDaten().getPrioAnvertraute(), 0);

				} else if (runs >= me.length * 1.5) {
					// "case Dienstagaben problem"
					int i = ran.nextInt(me.length);
					Messdiener med = me[i];
					if (!med.getMessdatenDaten().getDateVonMessen().contains(m.getDate())) {
						m.einteilen(med);
						// System.out.println("DAP: war nicht nett zu " + med.makeId());
					}
				}

			} else {
				m.einteilen(medi);
			}
		} else {
			if (runs >= me.length * 1.5) {
				// case: zu viele Messen, zu wenige Messdiener"
				int i = ran.nextInt(me.length);
				Messdiener med = me[i];
				if (!med.getMessdatenDaten().getDateVonMessen().contains(m.getDate())) {
					m.einteilen(med);
					// System.out.println("ZVMZWM: war nicht nett zu " + med.makeId());
				}
			}
			einteilen(m, runs + 1);
		}
		einteilen(m, runs + 1);

	}/*
		 * @SuppressWarnings("null") private void einteilen(Messe m, Messdiener medi) {
		 * if (m.istFertig(false)) { if (!m.istFertig(true)) { einteilenFuerLeiter(m,
		 * (Boolean)null); } return; } m.einteilen(medi);
		 * 
		 * }
		 */

	private void einteilen(Messe m, ArrayList<Messdiener> anvertraute, int secondndchance) {
		if (m.istFertig()) {
			// System.out.println(m.ausgeben() + "\nwar ein Erfolg!");
			return;
		}
		Random ran = new Random();
		if (anvertraute.size() >= 2) {
			for (int i = 0; i < anvertraute.size(); i++) {
				int r = ran.nextInt(anvertraute.size());
				Messdiener medi = anvertraute.get(r);
				m.einteilen(medi);
			}
		}

	}

	public void done() {
		Toolkit.getDefaultToolkit().beep();

		for (Messe messe : m) {
			// System.out.println(messe.ausgeben());
		}
	}
}
