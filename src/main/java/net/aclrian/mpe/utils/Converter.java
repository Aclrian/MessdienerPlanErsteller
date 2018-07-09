package net.aclrian.mpe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;

import net.aclrian.mpe.panels.Finish;
import net.aclrian.mpe.start.AProgress;

public class Converter {

	private File htmlFile;
	private String html = "";
	public final String url = "https://github.com/Aclrian/MessdienerPlanErsteller";
	private File docx;
	private File pfd;
	static boolean saveFO;

	public Converter(Finish finish) throws IOException {
		html = finish.getText();
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

		html = html.substring(0, html.length() - 14);
		html = html.replaceAll("</b>", "</h2>");
		html = html.replaceAll("<b>", "<h2>");
		html += "<br></br><p>Der Messdienerplan wurde erstellt mit: <a href='" + url + "'>MessdienerplanErsteller "
				+ AProgress.VersionID + "</a></p>" + "</body></html>";
		html = html.replaceAll("<head></head>",
				"<head><style>h1{font-size: 18px; font-weight: bold;}h2{font-weight: bold;font-size: 14px}p{font-size: 12x}</style></head>");

		fw.write(html);
		fw.flush();
		fw.close();
		setHtmlFile(new File(System.getProperty("user.home") + '\\' + "tmp.html"));
		try {
			docx = toWord();
		} catch (JAXBException | Docx4JException e) {
			new Erroropener(e.getMessage());
			e.printStackTrace();
		}
	}

	public File toWord() throws IOException, JAXBException, Docx4JException {
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

		Utilities.logging(getClass(), "toWord",
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

	public File toPDF() throws IOException, XWPFConverterException {
		InputStream doc = new FileInputStream(docx);
		XWPFDocument document = new XWPFDocument(doc);
		PdfOptions options = PdfOptions.create();
		File file = new File(System.getProperty("user.home") + '\\' + "tmp.pdf");
		OutputStream out = new FileOutputStream(file);
		PdfConverter.getInstance().convert(document, out, options);
		Utilities.logging(getClass(), "toPDF", "PDF wurde erstellt.");
return file;
//	WordprocessingMLPackage wordMLPackage;
//
//	Utilities.logging(getClass(), "toPDF", "Lade word-Document: " + docx.getAbsolutePath());
//	String inputfilepath = docx.getAbsolutePath();
//	wordMLPackage = WordprocessingMLPackage.load(docx);
//	FieldUpdater updater = new FieldUpdater(wordMLPackage);
//	updater.update(true);
//	File rtn = new File(System.getProperty("user.home") + '\\' + "tmp.pdf");
//	OutputStream os = new java.io.FileOutputStream(rtn);
//	if (!Docx4J.pdfViaFO()) {
//	    Utilities.logging(getClass(), "toPDF", "Using Plutext's PDF Converter; add docx4j-export-fo if you don't want that");
//	    try {
//		Docx4J.toPDF(wordMLPackage, os);
//	    } catch (Docx4JException e) {
//		new Erroropener(e.getMessage());
//		e.printStackTrace();
//		IOUtils.closeQuietly(os);
//		if (e.getCause() != null && e.getCause() instanceof ConversionException) {
//
//		    ConversionException ce = (ConversionException) e.getCause();
//		    ce.printStackTrace();
//		}
//	    }
//	    Utilities.logging(getClass(), "toPDF", "Datei: " + rtn + " wurde gespeichert.");
//	    return rtn;
//	}
//	Utilities.logging(getClass(), "toPDF", "Methode 2");
//	FOSettings foSettings = Docx4J.createFOSettings();
//	if (saveFO) {
//	    foSettings.setFoDumpFile(new java.io.File(inputfilepath + ".fo"));
//	}
//	foSettings.setWmlPackage(wordMLPackage);
//	Utilities.logging(getClass(), "toPDF", "Datei: " + rtn + " wurde gespeichert.");
//	if (wordMLPackage.getMainDocumentPart().getFontTablePart() != null) {
//	    wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
//	}
//	IOUtils.closeQuietly(os);
//	return rtn;
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
