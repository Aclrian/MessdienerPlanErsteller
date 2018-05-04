package net.aclrian.convertFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.aspose.words.*;

import net.aclrian.messdiener.window.WMainFrame;
import net.aclrian.messdiener.window.planerstellen.WMessenErstellen;
import net.aclrian.update.VersionIDHandler;

public class Converter {

	private File htmlFile;
	private String html;

	public Converter(WMessenErstellen wme) throws IOException {
		html = wme.getText();
		FileWriter fw = new FileWriter(System.getProperty("user.home") + '\\' + "tmp.html");
		html = html.replaceAll("\n", "");
		html = html.replaceAll("<br>", "<br></br>");
		html = html.replaceAll("     ", "");// fünf Leeeeerzeichen zu einem
		fw.write(html);
		fw.flush();
		fw.close();
		setHtmlFile(new File(System.getProperty("user.home") + '\\' + "tmp.html"));
	}

	public Converter() throws IOException {
		File f = new File(System.getProperty("user.home") + '\\' + "tmp.html");
		if (f.exists()) {
			setHtmlFile(f);
			BufferedReader in = new BufferedReader(new FileReader(f));
			String str;
			while ((str = in.readLine()) != null) {
				html += str;
			}
			in.close();
		} else {
			System.out.println("error");
		}

	}

	public void toWord() throws Exception {
		Document doc = new Document();
		DocumentBuilder builder = new DocumentBuilder(doc);
		//html = html.replaceAll("|", "");
//html.replace('|', '.');
		builder.insertHtml(html);

		Section currentSection = builder.getCurrentSection();
		PageSetup pageSetup = currentSection.getPageSetup();

		// Specify if we want headers/footers of the first page to be different from
		// other pages.
		// You can also use PageSetup.OddAndEvenPagesHeaderFooter property to specify
		// different headers/footers for odd and even pages.
		pageSetup.setDifferentFirstPageHeaderFooter(true);

		// --- Create header for the first page. ---
		pageSetup.setHeaderDistance(20);
		builder.moveToHeaderFooter(HeaderFooterType.FOOTER_FIRST);
		builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);

		// Specify header title for the first page.
		builder.write("Hergestellt von Aclrian durch ");
		String url = VersionIDHandler.alternativedownloadurl;
		url = url.substring(0, 8);
		builder.insertHyperlink("MessdienerplanErsteller " + WMainFrame.VersionID, url, false);
		doc.save(System.getProperty("user.home") + '\\' + "tmp.doc");
		System.out.println("Dokument wurde erfolgreich gespeichert in: " + System.getProperty("user.home") + '\\' + "tmp.doc");
	
		
	
	}

	public File getHtmlFile() {
		return htmlFile;
	}

	private void setHtmlFile(File htmlFile) {
		this.htmlFile = htmlFile;
	}

	public static void main(String[] args) {
		Converter c;
		try {
			c = new Converter();
			c.toWord();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
