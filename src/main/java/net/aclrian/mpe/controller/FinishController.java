package net.aclrian.mpe.controller;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;

import javafx.scene.web.HTMLEditor;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.Log;
import net.aclrian.mpe.utils.RemoveDoppelte;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class FinishController implements Controller {

	private final String fertig;

	private enum EnumAction {
		EinfachEinteilen, TypeBeachten
	}
	private boolean locked = true;
	private MainController.EnumPane pane;
	private ArrayList<Messe> messen;
	private String titel;
	private ArrayList<Messdiener> hauptarray, nichteingeteileList;
	@FXML
	private HTMLEditor editor;
	@FXML
	private Button mail,nichteingeteilte,zurück;

	public FinishController(MainController.EnumPane old, ArrayList<Messe> messen) {
		this.pane = old;
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
				titel = "Messdienerplan vom " + df.format(start) + " bis " + df.format(ende);
				s.append("<h1>").append(titel).append("</h1>");
			}
			s.append("<p>").append(m1).append("</p>");
		}
		s.append("</html>");
		fertig = s.toString();
		nichteingeteileList = new ArrayList<>();
		for (Messdiener medi : hauptarray) {
			if (medi.getMessdatenDaten().getInsgesamtEingeteilt() == 0) {
				nichteingeteileList.add(medi);
			}
		}
		nichteingeteileList.sort(Messdiener.compForMedis);
	}

	@Override
	public void initialize() {
		Toolkit.getDefaultToolkit().beep();
	}

	@Override
	public void afterstartup(Window window, MainController mc) {
		nichteingeteilte.setOnAction(event -> nichteingeteilte());
		editor.setHtmlText(fertig);
		zurück.setOnAction(p->this.zurueck(mc));
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public void zurueck(MainController mc) {
		Boolean löschen = Dialogs.yesNoCancel("Ja", "Nein", "Hier bleiben", "Soll die aktuelle Einteilung gelöscht werden, um einen neuen Plan mit ggf. geänderten Messen generieren zu können?");
		if (löschen == null) return;
		if (löschen) {
			DateienVerwalter.dv.reloadMessdiener();
			for (Messe m : messen) {
				m.nullen();
			}
		}
		locked = false;
		mc.setMesse(this, pane);
	}

	public ArrayList<Messe> getMessen(){
		return messen;
	}

	public void nichteingeteilte(){
		Dialogs.show(nichteingeteileList, "Nicht eingeteilte Messdiener");
	}

	@FXML
	public void toPDF(ActionEvent actionEvent) {
		ConverterProperties converterProperties = new ConverterProperties();
		converterProperties.setCharset("UTF-8");
		try {
			File out = new File(Log.getWorkingDir().getAbsolutePath() + File.separator + titel + ".pdf");
			if(out.exists())out.delete();
			HtmlConverter.convertToPdf(new ByteArrayInputStream(editor.getHtmlText().replaceAll("<p></p>","<br>").getBytes(StandardCharsets.UTF_8)),
					new FileOutputStream(out), converterProperties);
			Desktop.getDesktop().open(out);
		} catch (IOException e) {
			Dialogs.error(e, "Konnte den Messdienerplan nicht zu PDF konvertieren.");
		}
	}

	@FXML
	public void toWORD(ActionEvent actionEvent) {
		try{
			String input = editor.getHtmlText();
			input = input.replaceAll("</br>","");
			input = input.replaceAll("<br>","<br></br>");
			System.out.println(input);
			RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
			rfonts.setAscii("Century Gothic");
			XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
			NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
			wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
			ndp.unmarshalDefaultNumbering();
			XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
			XHTMLImporter.setHyperlinkStyle("Hyperlink");
			wordMLPackage.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(input, Log.getWorkingDir().getAbsolutePath()));

			File out = new File(Log.getWorkingDir()+File.separator +titel+".docx");
			if(out.exists())out.delete();
			wordMLPackage.save(out);
			Desktop.getDesktop().open(out);
		} catch(Exception e){
			Dialogs.error(e,"Word-Dokument konnte nicht erstellt werden.");
		}
	}

		public void neuerAlgorythmus() {
		hauptarray.sort(Messdiener.einteilen);
		// neuer Monat:
		Calendar start = Calendar.getInstance();
		start.setTime(messen.get(0).getDate());
		start.add(Calendar.MONTH, 1);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Log.getLogger().info("nächster Monat bei: " + df.format(start.getTime()));
		// EIGENTLICHER ALGORYTHMUS
			for (Messe me : messen) {
				if (me.getDate().after(start.getTime())) {
					start.add(Calendar.MONTH, 1);
					Log.getLogger().info("nächster Monat: Es ist " + df.format(me.getDate()));
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
			medis = get(new Sonstiges(), m, false, false);
			Log.getLogger().info(medis.size() + " für " + m.getNochBenoetigte());
			for (Messdiener medi : medis) {
				einteilen(m, medi, false, false);
			}
			if(!m.istFertig())zwang(m);
			break;
		case TypeBeachten:
			ArrayList<Messdiener> medis2;
			boolean zwang2 = false;
			medis2 = get(m.getStandardMesse(), m, false, false);
			Log.getLogger().info(medis2.size() + " für " + m.getNochBenoetigte());
			for (Messdiener messdiener : medis2) {
				einteilen(m, messdiener, false, false);
			}
			if(!m.istFertig())zwang(m);
			break;
		}
	}

	private void zwang(Messe m) {
		if(!(m.getStandardMesse() instanceof Sonstiges)) {
			if (Dialogs.frage("Die Messe " + m.getID().replaceAll("\t", "   ") + " hat zu wenige Messdiener.\nNoch " + m.getNochBenoetigte() + " werden benötigt.\nSollen Messdiener eingeteilt werden, die standartmäßig die Messe \n'" + m.getStandardMesse().tokurzerBenutzerfreundlichenString() + "' dienen können, aber deren Anzahl schon zu hoch ist?")) {
				ArrayList<Messdiener> medis = get(m.getStandardMesse(), m, false, true);
				Log.getLogger().warn(m+" einteilen ohne Anzahl beachten");
				for (Messdiener medi : medis) {
					einteilen(m, medi, false,true);
				}
			}
			if (m.istFertig()) return;
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy");
			if (Dialogs.frage("Die Messe " + m.getID().replaceAll("\t", "   ") + " hat zu wenige Messdiener.\nNoch " + m.getNochBenoetigte() + " werden benötigt.\nSollen Messdiener eingeteilt werden, die an dem Tag \n'" + df.format(m.getDate()) + "' dienen können?")) {
				ArrayList<Messdiener> medis = get(new Sonstiges(), m, false, false);
				Log.getLogger().warn(m+" einteilen ohne Standardmesse beachten");
				for (Messdiener medi : medis) {
					einteilen(m, medi, false,false);
				}
			}
			if (m.istFertig()) return;
		}
		if(Dialogs.frage("Die Messe " + m.getID().replaceAll("\t","   ") + " hat zu wenige Messdiener.\nNoch " + m.getNochBenoetigte() + " werden benötigt.\nSollen Messdiener zwangsweise eingeteilt werden?")){
			Log.getLogger().warn(m+" einteilen ohne Standardmesse beachten");
			if(m.istFertig()) return;
			ArrayList<Messdiener> medis = get(new Sonstiges(), m, true, true);
			for (Messdiener medi : medis) {
				einteilen(m, medi, true, true);
			}
		}
		zuwenige(m);
	}

	private void zuwenige(Messe m){
		if(!m.istFertig())Dialogs.error("Die Messe" + m.getID().replaceAll("\t","   ")+" wird zu wenige Messdiener haben.");
	}

		public void einteilen(Messe m, Messdiener medi, boolean zwangdate, boolean zwanganz) {
		boolean d;
		if (m.istFertig()) {
			return;
		} else{
			d = m.einteilen(medi, zwangdate, zwanganz);
		}
		if (!m.istFertig() && d) {
			ArrayList<Messdiener> anv = medi.getMessdatenDaten().getAnvertraute(DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList());

			RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();
			rd.removeDuplicatedEntries(anv);
			if (anv.size() >= 1) {
				anv.sort(Messdiener.einteilen);
				for (Messdiener messdiener : anv) {
					boolean b = messdiener.getDienverhalten().getBestimmtes(m.getStandardMesse());
					if (messdiener.getMessdatenDaten().kann(m.getDate(), zwangdate, zwanganz) && b) {
						Log.getLogger().info(messdiener.makeId() + " dient mit " + medi.makeId()+"?");
						einteilen(m, messdiener, zwangdate, zwanganz);
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

		public ArrayList<Messdiener> get(StandartMesse sm, Messe m, boolean zwangdate, boolean zwanganz) {
		ArrayList<Messdiener> al = new ArrayList<>();
		for (Messdiener medi : hauptarray) {
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = DateienVerwalter.dv.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm) && ii != 0
					&& medi.getMessdatenDaten().kann(m.getDate(), zwangdate, zwanganz)) {
				al.add(medi);
			}
		}
		Collections.shuffle(al);
		al.sort(Messdiener.einteilen);
		return al;
		}
@Deprecated
		public ArrayList<Messdiener> get(StandartMesse sm, Date d, boolean zwangdate, boolean zwanganz) {
		ArrayList<Messdiener> al = new ArrayList<>();
		ArrayList<Messdiener> al2 = new ArrayList<>();
		for (Messdiener medi : hauptarray) {
			int id = 0;
			if (medi.isIstLeiter()) {
				id++;
			}
			int ii = DateienVerwalter.dv.getPfarrei().getSettings().getDaten(id).getAnz_dienen();
			if (medi.getDienverhalten().getBestimmtes(sm) && ii != 0) {
				if (medi.getMessdatenDaten().kann(d, zwangdate, zwanganz)) {
					al.add(medi);
				} else if (medi.getMessdatenDaten().kann(d, true,true)) {
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
