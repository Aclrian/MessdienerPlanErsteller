package net.aclrian.mpe.utils.export;

import net.aclrian.mpe.utils.MPELog;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.local.office.LocalOfficeUtils;

import java.io.File;

public class JodPDFExport implements IExporter {

    private final String html;
    private final String titel;

    public JodPDFExport(String html, String titel) {
        this.html = html;
        this.titel = titel;
    }

    public File generateFile() {
        if (LocalOfficeUtils.getDefaultOfficeHome() != null) {
            MPELog.getLogger().info("Converting HTML to PDF with JODConverter");
            return convert(DefaultDocumentFormatRegistry.PDF, html, titel);
        } else {
            return null;
        }
    }
}
