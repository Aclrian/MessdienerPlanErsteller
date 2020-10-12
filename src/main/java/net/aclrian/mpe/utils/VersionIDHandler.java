package net.aclrian.mpe.utils;

import net.aclrian.mpe.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static net.aclrian.mpe.utils.Log.getLogger;

public class VersionIDHandler {
    private static final String TAG = "tag_name";
    private static final URI urlToLatestReleaseJsonFile = URI.create("https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest");
    private static final URI alternativedownloadurl = URI.create("https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest");
    private static final String URL_WITH_TAG = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/";
    private static String internettid;

    private static String searchVersionID() throws IOException, ParseException {
        JSONObject jsonobj = parseJSONFile();
        return (String) jsonobj.get(TAG);
    }

    private static EnumHandling rankingVersionID() {
        try {
            internettid = searchVersionID();
            String programmsvid = Main.VERSION_ID;
            Log.getLogger().info("Running with: " + Main.VERSION_ID + " found: " + internettid);
            if (!internettid.contains(".")) return EnumHandling.IS_TOO_NEW;
            if (internettid.equals(programmsvid)) return EnumHandling.IS_THE_LATEST;
            return oldNewOrLatest(programmsvid);
        } catch (Exception e) {
            Log.getLogger().error(e);
            return EnumHandling.ERROR;
        }
    }

    private static EnumHandling oldNewOrLatest(String programmsvid) {
        String[] inumbers = internettid.split(Pattern.quote("."));
        String[] lnumbers = programmsvid.split(Pattern.quote("."));
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
        return internettid;
    }

    private static JSONObject parseJSONFile()
            throws IOException, ParseException {
        URL json = urlToLatestReleaseJsonFile.toURL();
        JSONParser parse = new JSONParser();
        InputStream is = json.openStream();
        Object obj = parse.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
        return (JSONObject) obj;
    }

    public static void versioncheck(boolean showall) {
        EnumHandling eh = rankingVersionID();
        switch (eh) {
            case IS_OLD:
                try {
                    Dialogs.getDialogs().open(new URI(VersionIDHandler.URL_WITH_TAG + getInternetId()),
                            eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
                } catch (IOException | URISyntaxException e) {
                    try {
                        Dialogs.getDialogs().open(VersionIDHandler.alternativedownloadurl,
                                eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
                    } catch (IOException e1) {
                        getLogger().warn("Die Download-Url konnte nicht aufgelöst werden.");
                    }
                }
                break;
            case IS_THE_LATEST:
            case IS_TOO_NEW:
                if (showall) Dialogs.getDialogs().info("Versionsüberprüfung", eh.getMessage());
                break;
            case ERROR:
                if (showall) Dialogs.getDialogs().error(eh.getMessage());
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
}