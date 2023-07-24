package net.aclrian.mpe.utils.export;

import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.local.office.LocalOfficeUtils;

import java.io.File;

public class WORDExport implements IExporter {

    private final String html;
    private final String titel;

    public WORDExport(String html, String titel) {
        super();
        this.html = html;
        this.titel = titel;
    }

    public File generateFile() {
        if (LocalOfficeUtils.getDefaultOfficeHome() == null) {
            Dialogs.getDialogs().info("Für die Konvertierung wird LibreOffice (oder Openoffice) benötigt.",
                    "Wenn es trotz Installation nicht erkannt wird, kann dafür eine Systemvariable OFFICE_HOME angelegt werden,"
                    + "die den Installationspfad von der Officeanwendung enthält.");
            return null;
        }
        MPELog.getLogger().info("Converting PDF to WORD with JODConverter");
        return convert(DefaultDocumentFormatRegistry.DOCX, html, titel);
    }
}
