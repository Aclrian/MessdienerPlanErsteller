package net.aclrian.mpe.utils.export;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import net.aclrian.mpe.utils.MPELog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TXTPDFExport implements IExporter {

    private final String html;
    private final String titel;

    public TXTPDFExport(String html, String titel) {
        this.html = html;
        this.titel = titel;
    }

    public File generateFile() throws IOException {
            MPELog.getLogger().info("Converting HTML to PDF with iText");
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setCharset("UTF-8");
            File out = new File(MPELog.getWorkingDir().getAbsolutePath(), titel + ".pdf");
            HtmlConverter.convertToPdf(new ByteArrayInputStream(html.replace("<p></p>", "<br>").replace("\u2003", "    ").getBytes(StandardCharsets.UTF_8)),
                    new FileOutputStream(out), converterProperties);
            return out;
        }
}
