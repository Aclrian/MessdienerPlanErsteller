package net.aclrian.mpe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.aclrian.mpe.Main;

import static net.aclrian.mpe.utils.Log.getLogger;

public class VersionIDHandler {
	public static final String tag = "tag_name";
	public static final String beta_start = "b";
	public static final String urlToLatestReleaseJsonFile = "https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest";
	//public static final String directdownload = "https://aclrian.github.io/MessdienerPlanErsteller/download.html";
	public static final String alternativedownloadurl = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest";
	public static final String urlwithtag ="https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/";
	private static String internettid;
	
	private static String searchVersionID() throws IOException, ParseException {
		JSONObject jsonobj = doThigswithExceptions();
		return (String) jsonobj.get(tag);
	}

	private static EnumHandling rankingVersionID() {
		try {
			internettid = searchVersionID();
			Log.getLogger().info("Running with: " + Main.VersionID + " found: " + internettid);
			if (Main.VersionID.equals(internettid)) {
				return EnumHandling.isTheLatest;
			}
			String programmsvid = Main.VersionID;
			int localversion;
			if (programmsvid.startsWith(beta_start)) {
				String tmp = programmsvid.substring(1);
				localversion = Integer.parseInt(tmp);
			} else {
				localversion = Integer.parseInt(Main.VersionID);
			}
			int internetversion;
			if (internettid.startsWith(beta_start)) {
				String s = internettid.substring(1);
				internetversion = ((int) Double.parseDouble(s));
			} else {
				internetversion = Integer.parseInt(internettid);
			}
			if (localversion > internetversion) {
				// update or beta request
				if (Main.VersionID.equals(getBeta(Main.VersionID, internettid))) {
					return EnumHandling.isOld;
				}
				return EnumHandling.isTooNew;
			}
			if (internetversion > localversion) {
				if (internettid.equals(getBeta(Main.VersionID, internettid))) {
					return EnumHandling.betaRequest;
				}
				return EnumHandling.isOld;
			}
			return EnumHandling.isTheLatest;
		} catch (IOException | ParseException e) {
			Log.getLogger().info("Running with: " + Main.VersionID);
			return EnumHandling.error;
		}
	}

	private static String getBeta(String versionid, String latestID) {
		if (!versionid.startsWith(beta_start) && latestID.startsWith(beta_start)) {
			return latestID;
		} else if (versionid.startsWith(beta_start) && !latestID.startsWith(beta_start)) {
			return versionid;
		}
		return "";
	}

	public static String getInternettid() {
		return internettid;
	}

	private static JSONObject doThigswithExceptions()
			throws IOException, ParseException{
		URL json = new URL(urlToLatestReleaseJsonFile);
		JSONParser parse = new JSONParser();
		InputStream is = json.openStream();
		Object obj = parse.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
		return (JSONObject) obj;
	}
	
	private enum EnumHandling {
		isTheLatest("Es wurde keine neuere Version gefunden"), isOld("Es wurde eine neuere Version gefunden"),
		isTooNew("Neuereste Version"), error("Bei der Versionsüberprüfung kam es zu einem Fehler"),
		betaRequest("Neue Beta-Version gefunden");
		private String message;
		EnumHandling(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	public static void versioncheck(boolean showall) {
		EnumHandling eh = rankingVersionID();
		switch (eh){
			case isOld:
			case betaRequest:
				try {
					Dialogs.open(new URI(VersionIDHandler.urlwithtag + getInternettid()),
							eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
				} catch (IOException | URISyntaxException e) {
					try {
						Dialogs.open(new URI(VersionIDHandler.alternativedownloadurl),
								eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
					} catch (IOException | URISyntaxException e1) {
						getLogger().warn("Die Download-Url konnte nicht aufgelöst werden.");
					}
				}
				break;
			case isTheLatest:
			case isTooNew:
				if (showall) Dialogs.info("Versionsüberprüfung",eh.getMessage());
				break;
			case error:
				if (showall) Dialogs.error(eh.getMessage());
				break;
		}
		getLogger().info(eh.getMessage());
	}
}