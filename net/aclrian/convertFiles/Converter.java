package net.aclrian.convertFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.org.apache.poi.util.IOUtils;
import org.docx4j.services.client.ConversionException;

import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.window.WMainFrame;
import net.aclrian.messdiener.window.planerstellen.WMessenErstellen;

public class Converter {

	private File htmlFile;
	private String html = "";
	public final String url = "https://github.com/Aclrian/MessdienerPlanErsteller";
	private File docx;
	private File pfd;

	public Converter(WMessenErstellen wme) throws IOException {
		html = wme.getText();
		FileWriter fw = new FileWriter(System.getProperty("user.home") + '\\' + "tmp.html");
		html = html.replaceAll("\n", "");
		html = html.replaceAll("<br>", "<br></br>");
		
		html = html.replaceAll("      ", " ");
		html = html.replaceAll("     ", " ");
		html = html.replaceAll("    ", " ");
		html = html.replaceAll("   ", " "); 
		html = html.replaceAll("  ", " ");
		html = html.replaceAll("> ", ">");
		html = html.replaceAll(" <", "<");
		
		
		
		html = html.substring(0, html.length() -14);
		html = html.replaceAll("</b>", "</h2>");
		html = html.replaceAll("<b>", "<h2>");
		html += "<p>Der Messdienerplan wurde erstellt mit: <a href='"+url+"'>MessdienerplanErsteller "+WMainFrame.VersionID+"</a></p>"+"</body></html>";
		html = html.replaceAll("<head></head>", "<head><style>h1{font-size: 18px; font-weight: bold;}h2{font-weight: bold;font-size: 14px}p{font-size: 12x}</style></head>");

		fw.write(html);
		fw.flush();
		fw.close();
		setHtmlFile(new File(System.getProperty("user.home") + '\\' + "tmp.html"));
		try {
			docx = toWord();
		//	pfd = toPDF();
		} catch (JAXBException | Docx4JException e) {
			new Erroropener(e.getMessage());
			e.printStackTrace();
		}
		
	}

	public Converter() throws IOException {
		try {
			JAXBContext.newInstance(this.getClass());
			// FooObject fooObj = (FooObject)u.unmarshal( new File( "foo.xml" ) );
		} catch (JAXBException e) {
			new Erroropener(e.getMessage());
			e.printStackTrace();
		}

		File f = new File(System.getProperty("user.home") + '\\' + "tmp.html");
		if (f.exists()) {
			setHtmlFile(f);
			BufferedReader in = new BufferedReader(new FileReader(f));
			String str = "";
			while ((str = in.readLine()) != null) {
				if (str != null || !str.equals("")) {
					html = html + str;
				}
			}
			in.close();
			//
			FileWriter fw = new FileWriter(System.getProperty("user.home") + '\\' + "tmp.html");
			// html = html.replaceAll("\n", "");
			// html = html.replaceAll("<br>", "<br></br>");
			// html = html.replaceAll(" ", "");// fünf Leeeeerzeichen zu einem
			fw.write(html);
			fw.flush();
			fw.close();
		} else {
			System.out.println("error");
		}

	}

	public File toWord() throws IOException, JAXBException, Docx4JException {
		/*
		 * Document doc = new Document(); DocumentBuilder builder = new
		 * DocumentBuilder(doc); //html = html.replaceAll("|", ""); //html.replace('|',
		 * '.'); builder.insertHtml(html);
		 * 
		 * Section currentSection = builder.getCurrentSection(); PageSetup pageSetup =
		 * currentSection.getPageSetup();
		 * 
		 * // Specify if we want headers/footers of the first page to be different from
		 * // other pages. // You can also use PageSetup.OddAndEvenPagesHeaderFooter
		 * property to specify // different headers/footers for odd and even pages.
		 * pageSetup.setDifferentFirstPageHeaderFooter(true);
		 * 
		 * // --- Create header for the first page. --- pageSetup.setHeaderDistance(20);
		 * builder.moveToHeaderFooter(HeaderFooterType.FOOTER_FIRST);
		 * builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER); //
		 * Specify header title for the first page.
		 * builder.write("Hergestellt von Aclrian durch "); String url =
		 * VersionIDHandler.alternativedownloadurl; url = url.substring(0, 8);
		 * builder.insertHyperlink("MessdienerplanErsteller " + WMainFrame.VersionID,
		 * url, false); doc.save(System.getProperty("user.home") + '\\' + "tmp.doc");
		 */ // docx:
		File docx = new java.io.File(System.getProperty("user.home") + "\\tmp.docx");
		String stringFromFile = FileUtils.readFileToString(htmlFile, "UTF-8");

		WordprocessingMLPackage docxOut = WordprocessingMLPackage.createPackage();
		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
		docxOut.getMainDocumentPart().addTargetPart(ndp);
		ndp.unmarshalDefaultNumbering();

		XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(docxOut);
		XHTMLImporter.setHyperlinkStyle("Hyperlink");

		docxOut.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(stringFromFile, null));
		docxOut.save(docx);

		System.out.println("Finished editing the word document");
		// Fußzeile ende
		docxOut.save(docx);

		System.out.println(
				"Dokument wurde erfolgreich gespeichert in: " + System.getProperty("user.home") + '\\' + "tmp.docx");
		this.docx = docx;
		return docx;
	}
	
	public File getHtmlFile() {
		return htmlFile;
	}

	private void setHtmlFile(File htmlFile) {
		this.htmlFile = htmlFile;
	}
	

		
		
		// For demo/debugging purposes, save the intermediate XSL FO
		// Don't do this in production!
		static boolean saveFO;
		
	    public File toPDF() throws Docx4JException, IOException, JAXBException {
			
			// Font regex (optional)
			// Set regex if you want to restrict to some defined subset of fonts
			// Here we have to do this before calling createContent,
			// since that discovers fonts
		//	String regex = null;
			// Windows:
			// String
			// regex=".*(calibri|camb|cour|arial|symb|times|Times|zapf).*";
			//regex=".*(calibri|camb|cour|arial|times|comic|georgia|impact|LSANS|pala|tahoma|trebuc|verdana|symbol|webdings|wingding).*";
			// Mac
			// String
			// regex=".*(Courier New|Arial|Times New Roman|Comic Sans|Georgia|Impact|Lucida Console|Lucida Sans Unicode|Palatino Linotype|Tahoma|Trebuchet|Verdana|Symbol|Webdings|Wingdings|MS Sans Serif|MS Serif).*";
			//PhysicalFonts.setRegex(regex);

			// Document loading (required)
	 
	    	
			WordprocessingMLPackage wordMLPackage;
			
				// Load .docx or Flat OPC .xml
				System.out.println("Loading file from " + docx.getAbsolutePath());
				String inputfilepath = docx.getAbsolutePath();
				wordMLPackage = WordprocessingMLPackage.load(docx);
			
			// Refresh the values of DOCPROPERTY fields 
			FieldUpdater updater = new FieldUpdater(wordMLPackage);
			updater.update(true);

			File rtn = new File(System.getProperty("user.home") + '\\' + "tmp.pdf");
			
			// All methods write to an output stream
			OutputStream os = new java.io.FileOutputStream(rtn);
			
			
			if (!Docx4J.pdfViaFO()) {
				
				// Since 3.3.0, Plutext's PDF Converter is used by default

				System.out.println("Using Plutext's PDF Converter; add docx4j-export-fo if you don't want that");
				
				try {
					Docx4J.toPDF(wordMLPackage, os);
				} catch (Docx4JException e) {
					new Erroropener(e.getMessage());
					e.printStackTrace();
					// What did we write?
					IOUtils.closeQuietly(os);
					System.out.println(
							FileUtils.readFileToString(rtn));
					if (e.getCause()!=null
							&& e.getCause() instanceof ConversionException) {
						
						ConversionException ce = (ConversionException)e.getCause();
						ce.printStackTrace();
					}
				}
				System.out.println("Saved: " + rtn);

				return rtn;
			}

			System.out.println("Attempting to use XSL FO");
			
			/**
			 * Demo of PDF output.
			 * 
			 * PDF output is via XSL FO.
			 * First XSL FO is created, then FOP
			 * is used to convert that to PDF.
			 * 
			 * Don't worry if you get a class not
			 * found warning relating to batik. It
			 * doesn't matter.
			 * 
			 * If you don't have logging configured, 
			 * your PDF will say "TO HIDE THESE MESSAGES, 
			 * TURN OFF debug level logging for 
			 * org.docx4j.convert.out.pdf.viaXSLFO".  The thinking is
			 * that you need to be able to be warned if there
			 * are things in your docx which the PDF output
			 * doesn't support...
			 * 
			 * docx4j used to also support creating
			 * PDF via iText and via HTML. As of docx4j 2.5.0, 
			 * only viaXSLFO is supported.  The viaIText and 
			 * viaHTML source code can be found in src/docx4j-extras directory
			 *
			 */
			
			
			/*
			 * NOT WORKING?
			 * 
			 * If you are getting:
			 * 
			 *   "fo:layout-master-set" must be declared before "fo:page-sequence"
			 * 
			 * please check:
			 * 
			 * 1.  the jaxb-xslfo jar is on your classpath
			 * 
			 * 2.  that there is no stack trace earlier in the logs
			 * 
			 * 3.  your JVM has adequate memory, eg
			 * 
			 *           -Xmx1G -XX:MaxPermSize=128m
			 * 
			 */
			
			
			// Set up font mapper (optional)
			/*Mapper fontMapper = new IdentityPlusMapper();
			wordMLPackage.setFontMapper(fontMapper);
			*///TO DO weggelassen
			// .. example of mapping font Times New Roman which doesn't have certain Arabic glyphs
			// eg Glyph "ي" (0x64a, afii57450) not available in font "TimesNewRomanPS-ItalicMT".
			// eg Glyph "ج" (0x62c, afii57420) not available in font "TimesNewRomanPS-ItalicMT".
			// to a font which does
			//PhysicalFont font 
				//	= PhysicalFonts.get("Arial Unicode MS"); 
				// make sure this is in your regex (if any)!!!
//			if (font!=null) {
//				fontMapper.put("Times New Roman", font);
//				fontMapper.put("Arial", font);
//			}
//			fontMapper.put("Libian SC Regular", PhysicalFonts.get("SimSun"));

			// FO exporter setup (required)
			// .. the FOSettings object
	    	FOSettings foSettings = Docx4J.createFOSettings();
			if (saveFO) {
				foSettings.setFoDumpFile(new java.io.File(inputfilepath + ".fo"));
			}
			foSettings.setWmlPackage(wordMLPackage);
			
			// Document format: 
			// The default implementation of the FORenderer that uses Apache Fop will output
			// a PDF document if nothing is passed via 
			// foSettings.setApacheFopMime(apacheFopMime)
			// apacheFopMime can be any of the output formats defined in org.apache.fop.apps.MimeConstants eg org.apache.fop.apps.MimeConstants.MIME_FOP_IF or
			// FOSettings.INTERNAL_FO_MIME if you want the fo document as the result.
			//foSettings.setApacheFopMime(FOSettings.INTERNAL_FO_MIME);
			
			// Specify whether PDF export uses XSLT or not to create the FO
			// (XSLT takes longer, but is more complete).
			
			// Don't care what type of exporter you use
			Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
			
			// Prefer the exporter, that uses a xsl transformation
			// Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
			
			// Prefer the exporter, that doesn't use a xsl transformation (= uses a visitor)
			// .. faster, but not yet at feature parity
			// Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_NONXSL);
	    	
			System.out.println("Saved: " + rtn);

			// Clean up, so any ObfuscatedFontPart temp files can be deleted 
			if (wordMLPackage.getMainDocumentPart().getFontTablePart()!=null) {
				wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
			}		
			// This would also do it, via finalize() methods
			updater = null;
			foSettings = null;
			wordMLPackage = null;
			return rtn;
	}
	

	public File getDocx() {
			return docx;
		}

		public File getPfd() throws Docx4JException, IOException, JAXBException {
			if (pfd == null) {
				pfd = toPDF();
			}
			return pfd;
		}
}
