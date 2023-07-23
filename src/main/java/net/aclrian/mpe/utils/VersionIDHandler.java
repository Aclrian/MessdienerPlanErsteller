package net.aclrian.mpe.utils;


import com.google.gson.Gson;
import net.aclrian.mpe.MainApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static net.aclrian.mpe.utils.MPELog.getLogger;

public class VersionIDHandler {
    private static final URI URL_TO_LATEST_RELEASE_JSON_FILE = URI.create("https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest");
    private static final URI ALTERNATIVE_DOWNLOAD_URL = URI.create("https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest");
    private static final String URL_WITH_TAG = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/";
    private static String internetId;

    private static EnumHandling rankingVersionID() {
        try (InputStream is = URL_TO_LATEST_RELEASE_JSON_FILE.toURL().openStream()) {
            Gson gson = new Gson();
            internetId = gson.fromJson(new String(is.readAllBytes(), StandardCharsets.UTF_8), Version.class).getVersion();
            getLogger().info("Running with: {} found: {}", MainApplication.VERSION_ID, internetId);
            if (!internetId.contains(".")) return EnumHandling.IS_TOO_NEW;
            if (internetId.equals(MainApplication.VERSION_ID)) return EnumHandling.IS_THE_LATEST;
            return oldNewOrLatest();
        } catch (Exception e) {
            getLogger().error(e);
            return EnumHandling.ERROR;
        }
    }

    private static EnumHandling oldNewOrLatest() {
        String[] inumbers = internetId.split(Pattern.quote("."));
        String[] lnumbers = MainApplication.VERSION_ID.split(Pattern.quote("."));
        int i = 0;
        while (i < inumbers.length && i < lnumbers.length) {
            int inet = Integer.parseInt(inumbers[i]);
            int loc = Integer.parseInt(lnumbers[i]);
            if (inet > loc) {
                return EnumHandling.IS_OLD;
            } else if (inet < loc) {
                return EnumHandling.IS_TOO_NEW;
            }
            i++;
        }
        if (inumbers.length != lnumbers.length) {
            if (inumbers.length > lnumbers.length) {
                return EnumHandling.IS_OLD;
            } else return EnumHandling.IS_TOO_NEW;
        }
        return EnumHandling.IS_THE_LATEST;
    }

    private static String getInternetId() {
        return internetId;
    }

    public static void versionCheck(boolean showAll) {
        EnumHandling eh = rankingVersionID();
        switch (eh) {
            case IS_OLD:
                try {
                    Dialogs.getDialogs().open(new URI(VersionIDHandler.URL_WITH_TAG + getInternetId()),
                            eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
                } catch (IOException | URISyntaxException e) {
                    try {
                        Dialogs.getDialogs().open(VersionIDHandler.ALTERNATIVE_DOWNLOAD_URL,
                                eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
                    } catch (IOException e1) {
                        getLogger().warn("Die Download-Url konnte nicht aufgelöst werden.");
                    }
                }
                break;
            case ERROR:
                if (showAll) Dialogs.getDialogs().error(eh.getMessage());
                break;
            default:
                if (showAll) Dialogs.getDialogs().info("Versionsüberprüfung", eh.getMessage());
                break;
        }
        getLogger().info(eh.getMessage());
    }

    private enum EnumHandling {
        IS_THE_LATEST("Es wurde keine neuere Version gefunden"), IS_OLD("Es wurde eine neuere Version gefunden"),
        IS_TOO_NEW("Neuere Version"), ERROR("Bei der Versionsüberprüfung kam es zu einem Fehler");
        private final String message;

        EnumHandling(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class Version {
        private String tagName;

        public String getVersion() {
            return tagName;
        }
    }
}