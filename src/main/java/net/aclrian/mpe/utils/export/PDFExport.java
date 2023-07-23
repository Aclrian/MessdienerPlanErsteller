package net.aclrian.mpe.utils.export;

import net.aclrian.mpe.utils.MPELog;

import java.io.File;
import java.io.IOException;

public class PDFExport implements IExporter {

    private final String html;
    private final String titel;

    public PDFExport(String html, String titel) {
        this.html = html;
        this.titel = titel;
    }

    @Override
    public File generateFile() throws IOException {
        File pdf = null;
        try{
            pdf = new JodPDFExport(html, titel).generateFile();
        } catch (Exception e){
            MPELog.getLogger().info("JODConverter failed attempting itext next:" + e.getMessage());
        }
        if (pdf == null){
            pdf = new TXTPDFExport(html, titel).generateFile();
        }
        return pdf;
    }
}
