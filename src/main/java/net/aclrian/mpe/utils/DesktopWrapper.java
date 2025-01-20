
package net.aclrian.mpe.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Die Klasse kappselt die Aufrufe open, mail, browse von Desktop.getDesktop()
 */
public class DesktopWrapper {
    private enum CallAction {
        OPEN, MAIL, BROWSE
    }

    /**
     * Öffnet eine Datei oder einen Ordner mit dem Standardprogramm es Betriebssystems.
     * @param file Pfad zur Datei oder zum Ordner
     */
    public static void open(File file) {
        String errorMessage = "Die Destkopumgebung unterstützt leider das Öffen nicht.\n"
            + "Es sollte der Pfad:\n" + file + "\ngeöffnet werden.";
        callWithThread(CallAction.OPEN, file, null, errorMessage);
    }

    /**
     * Öffnet das Mailprogramm mit der übergebenen mailtoURI
     * @param mailtoURI
     */
    public static void mail(URI mailtoURI) {
        String errorMessage = "Die Destkopumgebung unterstützt leider das Öffen nicht.\n"
            + "Es sollte die URI:\n" + mailtoURI + "\ngeöffnet werden.";
        callWithThread(CallAction.MAIL, null, mailtoURI, errorMessage);
    }

    /**
     * Öffnet die übergebene uri im Browser
     * @param uri
     */
    public static void browse(URI uri) {
        String errorMessage = "Die Destkopumgebung unterstützt leider das Öffen nicht.\n"
            + "Es sollte dio URI:\n" + uri + "\ngeöffnet werden.";
        callWithThread(CallAction.BROWSE, null, uri, errorMessage);
    }

    /**
     * Führt die gewünschten Aufrufe innerhalb einen Threads auf.
     * @param cAction CallAction
     * @param file Datei oder Ordner beim Aufruf mit CallAction.OPEN
     * @param uri mailtoURI oder URI beim Aufruf mit CallAction.MAIL oder BROWSE
     * @param errorMessage Fehlermeldung
     */
    private static void callWithThread(CallAction cAction, File file, URI uri, String errorMessage) {
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    switch (cAction) {
                    case OPEN:
                        Desktop.getDesktop().open(file);
                        break;
                    case MAIL:
                        Desktop.getDesktop().mail(uri);
                        break;
                    case BROWSE:
                        Desktop.getDesktop().browse(uri);
                        break;
                    default:
                        Dialogs.getDialogs().error("Unbekannte cAction: '" + cAction + "' in callWithThread! Bitte Entwickler informieren.");
                    }

                } catch (IOException e) {
                        Dialogs.getDialogs().error(e, errorMessage);
                }
            }).start();
        } else {
            Dialogs.getDialogs().error(errorMessage);
        }
    }
}
