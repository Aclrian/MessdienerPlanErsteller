package net.aclrian.mpe.utils.export;

import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.local.office.LocalOfficeUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WORDExport implements IExporter {

    private final String html;
    private final String titel;
    private File file;

    public WORDExport(String html, String titel) {
        super();
        this.html = html;
        this.titel = titel;
    }

    public void generateFile() {
        if (LocalOfficeUtils.getDefaultOfficeHome() == null) {
            Dialogs.getDialogs().info("Für die Konvertierung wird LibreOffice benötigt.",
                    "Wenn es trotz Installation nicht erkannt wird, kann dafür eine Systemvariable OFFICE_HOME angelegt werden,"
                    + "die den Installationspfad von der Officeanwendung enthält.");
        }
        MPELog.getLogger().info("Converting PDF to WORD with JODConverter");
        file = convert(DefaultDocumentFormatRegistry.ODT, html, titel);
    }

    @Override
    public boolean openFile() {
        boolean success = true;
        if (file != null) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                success = false;
            }
        }
        return success;
    }
}
