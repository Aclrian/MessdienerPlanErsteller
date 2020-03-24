package net.aclrian.mpe.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.aclrian.mpe.Main;
import net.aclrian.mpe.utils.Log;

public class VersionIDHandler {
	public static final String tag = "tag_name";
	public static final String beta_start = "b";
	public static final String urlToLatestReleaseJsonFile = "https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest";
	public static final String downloadurltag = "browser_download_url";
	public static final String directdownload = "https://aclrian.github.io/MessdienerPlanErsteller/download.html";
	public static final String alternativedownloadurl = "https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest";
	public static final String urlwithtag ="https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/";
	private String internettid;
	
	public String searchVersionID() throws FileNotFoundException, URISyntaxException, IOException, ParseException {
		JSONObject jsonobj = doThigswithExceptions();
		String s = (String) jsonobj.get(tag);
		// ((JSONObject)((JSONArray)jsonobj.get("assets")).get(0)).get("browser_download_url"));
		return s;
	}

	public EnumHandling rankingVersionID() {
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
		} catch (URISyntaxException | IOException | ParseException e) {
			Log.getLogger().info("Running with: " + Main.VersionID);
			return EnumHandling.error;
		}
	}

	private String getBeta(String versionid, String latestID) {
		if (!versionid.startsWith(beta_start) && latestID.startsWith(beta_start)) {
			return latestID;
		} else if (versionid.startsWith(beta_start) && !latestID.startsWith(beta_start)) {
			return versionid;
		}
		return "";
	}

	public String getInternettid() {
		return internettid;
	}

	private JSONObject doThigswithExceptions()
			throws URISyntaxException, FileNotFoundException, IOException, ParseException, UnknownHostException {
		URL json = new URL(urlToLatestReleaseJsonFile);
		JSONParser parse = new JSONParser();
		InputStream is = json.openStream();
		Object obj = parse.parse(new InputStreamReader(is, Charset.forName("UTF-8")));
		JSONObject jsonobj = (JSONObject) obj;
		return jsonobj;
	}
	
	public static enum EnumHandling {
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
}