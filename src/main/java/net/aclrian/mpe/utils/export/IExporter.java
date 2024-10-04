package net.aclrian.mpe.utils.export;

import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface IExporter {
    void generateFile() throws IOException;

    default File convert(DocumentFormat fileFormat, String html, String title) {

        final LocalOfficeManager officeManager = LocalOfficeManager.install();
        File out = new File(MPELog.getWorkingDir().getAbsolutePath(), title + "." + fileFormat.getExtension());
        try {
            officeManager.start();
        } catch (OfficeException e) {
            Dialogs.getDialogs().error(e, "Fehler beim Konvertieren");
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(html.replace("<p></p>", "")
                .replace("</br>", "")
                .replace("<br>", "<br></br>")
                .replace("</p><p><font></font></p><p><font><b>", "</p><br/><p><font></font></p><p><font><b>")
                .replace("\u2003", "    ").getBytes(StandardCharsets.UTF_8))) {
            JodConverter.convert(bais).as(fileFormat).to(out).execute();
        } catch (Exception e) {
            Dialogs.getDialogs().error(e, "Konnte den Messdienerplan nicht zu " + fileFormat.getExtension() + " konvertieren.");
        } finally {
            OfficeUtils.stopQuietly(officeManager);
        }
        return out;
    }

    boolean openFile();
}
