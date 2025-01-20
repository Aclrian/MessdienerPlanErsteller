package net.aclrian.mpe.utils;


import com.google.gson.Gson;
import net.aclrian.mpe.MainApplication;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.regex.*;

import static net.aclrian.mpe.utils.MPELog.*;

/**
 * Prüft ob eine neuere Version der Software vorhanden ist.
 * Von dieser Klasse wird keine Instanz erstellt, sondern es sollte
 * die Methode versionCheck() aufgerufen werden.
 */
public class VersionIDHandler {
    private static final URI URL_TO_LATEST_RELEASE_JSON_FILE = URI.create("https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest");
    private static final URI ALTERNATIVE_DOWNLOAD_URL = URI.create("https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest");
    private static final String URL_WITH_TAG = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/";
    private static String internetId;

    private VersionIDHandler() {

    }

    /**
     * Holt die letzte Version aus dem Internet und vergleicht diese
     * mit der aktuellen Version
     * @return EnumVersionHandling was anzeigt, ob die Version aktuell,
     *  zu alt oder zu neu (Entwicklerversion) ist.
     */
    private static EnumVersionHandling rankingVersionID() {
        //Versuche Version der letzten Version zu ermitteln.
        try (InputStream is = URL_TO_LATEST_RELEASE_JSON_FILE.toURL().openStream()) {
            Gson gson = new Gson();
            //Internet version holen
            internetId = gson.fromJson(new String(is.readAllBytes(), StandardCharsets.UTF_8), Version.class).getVersion();
            getLogger().info("Running with: {} found: {}", MainApplication.VERSION_ID, internetId);
            if (!internetId.contains(".")) {
                return EnumVersionHandling.IS_TOO_NEW;
            }
            if (internetId.equals(MainApplication.VERSION_ID)) {
                return EnumVersionHandling.IS_THE_LATEST;
            }
            return compareVersions();
        } catch (Exception e) {
            getLogger().error(e);
            return EnumVersionHandling.ERROR;
        }
    }

    /**
     * Vergleich die Internet-Version und die aktuelle Version.
     * @return Ergebnis des Vergleichs als EnumVersionHandling
     */
    private static EnumVersionHandling compareVersions() {
        String[] inumbers = internetId.split(Pattern.quote("."));
        String[] lnumbers = MainApplication.VERSION_ID.split(Pattern.quote("."));
        int i = 0;
        //Die durch Punkt getrennten Zahlen von Links nach Rechts vergleichen.
        while (i < inumbers.length && i < lnumbers.length) {
            int inet = Integer.parseInt(inumbers[i]);
            int loc = Integer.parseInt(lnumbers[i]);
            if (inet > loc) {
                return EnumVersionHandling.IS_OLD;
            } else if (inet < loc) {
                return EnumVersionHandling.IS_TOO_NEW;
            }
            i++;
        }
        if (inumbers.length != lnumbers.length) {
            if (inumbers.length > lnumbers.length) {
                return EnumVersionHandling.IS_OLD;
            } else {
                return EnumVersionHandling.IS_TOO_NEW;
            }
        }
        //Wenn der Code hier ankommt, müssen die Versionen
        //gleich sein.
        return EnumVersionHandling.IS_THE_LATEST;
    }

    /**
     * Überprüft die aktuelle Version der Software gegen die Version im Internet.
     * @param showAll Wenn true, wird jede erkannte Version angezeigt.
     */
    public static void versionCheck(boolean showAll) {
        //Versionen vergleichen und Ergebnis in eh speichern.
        EnumVersionHandling eh = rankingVersionID();
        switch (eh) {
            case IS_OLD:
                try {
                    Dialogs.getDialogs().open(new URI(URL_WITH_TAG + internetId),
                            eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
                } catch (IOException | URISyntaxException e) {
                    try {
                        Dialogs.getDialogs().open(ALTERNATIVE_DOWNLOAD_URL,
                                eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
                    } catch (IOException e1) {
                        getLogger().warn("Die Download-Url konnte nicht aufgelöst werden.");
                    }
                }
                break;
            case ERROR:
                if (showAll) {
                    Dialogs.getDialogs().error(eh.getMessage());
                }
                break;
            default:
                if (showAll) {
                    Dialogs.getDialogs().info("Versionsüberprüfung", eh.getMessage());
                }
                break;
        }
        getLogger().info(eh.getMessage());
    }

    /**
     * EnumVersionHandling - Klasse, welche das Ergebnis eines Versionsvergleichs darstellt.
     */
    private enum EnumVersionHandling {
        IS_THE_LATEST("Es wurde keine neuere Version gefunden"), IS_OLD("Es wurde eine neuere Version gefunden"),
        IS_TOO_NEW("Neuere Version"), ERROR("Bei der Versionsüberprüfung kam es zu einem Fehler");
        private final String message;

        EnumVersionHandling(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class Version {
        //CHECKSTYLE:OFF: MemberName
        @SuppressWarnings("PMD")
        private String tag_name;

        public String getVersion() {
            return tag_name;
        }
        //CHECKSTYLE:ON: MemberName
    }
}
