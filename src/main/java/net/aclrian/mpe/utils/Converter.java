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

import com.lowagie.text.FontFactory;

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
		FontFactory.registerDirectories();
		html = finish.getText();
		FileWriter fw = new FileWriter(System.getProperty("user.home") + File.separator + "tmp.html");
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
				"<head><style>* {\n" + 
				" font-size: 100%;\n" + 
				" font-family: sans-serif;\n" + 
				"}h1{font-size: 18px; font-weight: bold;}h2{font-weight: bold;font-size: 14px}p{font-size: 12x}</style></head>");
		
		fw.write(html);
		fw.flush();
		fw.close();
		setHtmlFile(new File(System.getProperty("user.home") + File.separator + "tmp.html"));
		try {
			docx = toWord();
		} catch (Exception e) {
			new Erroropener(e);
			e.printStackTrace();
		}
	}

	private File toWord() throws IOException, JAXBException, Docx4JException {
		File docx = new java.io.File(System.getProperty("user.home") + File.separator +"tmp.docx");
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
				"Dokument wurde erfolgreich gespeichert in: " + System.getProperty("user.home") + File.separator + "tmp.docx");
		return this.docx = docx;
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
		File file = new File(System.getProperty("user.home") + File.separator + "tmp.pdf");
		OutputStream out = new FileOutputStream(file);
		PdfConverter.getInstance().convert(document, out, options);
		Utilities.logging(getClass(), "toPDF", "PDF wurde erstellt.");
		return file;
	}

	public File getDocx() {
		if(docx == null) {
			return new File(System.getProperty("user.home") + File.separator + "tmp.docx");
		}
		return docx;
	}

	public File getPfd() throws Docx4JException, IOException, JAXBException {
		if (pfd == null) {
			pfd = toPDF();
		}
		return pfd;
	}
}
