package net.aclrian.mpe.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.aclrian.mpe.Main;

import static net.aclrian.mpe.utils.Log.getLogger;

public class VersionIDHandler {
	private static final String tag = "tag_name";
	//public static final String beta_start = "b";
	private static final URI urlToLatestReleaseJsonFile = URI.create("https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest");
	//public static final String directdownload = "https://aclrian.github.io/MessdienerPlanErsteller/download.html";
	private static final URI alternativedownloadurl = URI.create("https://github.com/Aclrian/MessdienerPlanErsteller/releases/latest");
	private static final String urlwithtag ="https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/";
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
			if (programmsvid.equals("")) {
				MavenXpp3Reader reader = new MavenXpp3Reader();
				Model model = reader.read(new FileReader("pom.xml"));
				programmsvid = model.getVersion();
			}
			if (!internettid.contains(".")) return EnumHandling.isTooNew;
			if (internettid.equals(programmsvid)) return EnumHandling.isTheLatest;
			else{
				String[] inumbers = internettid.split(Pattern.quote("."));
				String[] lnumbers = programmsvid.split(Pattern.quote("."));
				int i = 0;
				while (i<inumbers.length && i<lnumbers.length){
					int inet = Integer.parseInt(inumbers[i]);
					int loc = Integer.parseInt(lnumbers[i]);
					if(inet>loc){
						return EnumHandling.isOld;
					} else if(inet<loc){
						return EnumHandling.isTooNew;
					}
					i++;
				}
				if(inumbers.length != lnumbers.length){
					if(inumbers.length>lnumbers.length){
						return EnumHandling.isOld;
					} else return EnumHandling.isTooNew;
				}
				return EnumHandling.isTheLatest;
			}
		} catch(Exception e){
			Log.getLogger().info("Running with: " + Main.VersionID);
			return EnumHandling.error;
		}
	}

	private static String getInternettid() {
		return internettid;
	}

	private static JSONObject doThigswithExceptions()
			throws IOException, ParseException{
		URL json = urlToLatestReleaseJsonFile.toURL();
		JSONParser parse = new JSONParser();
		InputStream is = json.openStream();
		Object obj = parse.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
		return (JSONObject) obj;
	}
	
	private enum EnumHandling {
		isTheLatest("Es wurde keine neuere Version gefunden"), isOld("Es wurde eine neuere Version gefunden"),
		isTooNew("Neuereste Version"), error("Bei der Versionsüberprüfung kam es zu einem Fehler")
	//	betaRequest("Neue Beta-Version gefunden")
		;
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
		//	case betaRequest:
				try {
					Dialogs.open(new URI(VersionIDHandler.urlwithtag + getInternettid()),
							eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
				} catch (IOException | URISyntaxException e) {
					try {
						Dialogs.open(VersionIDHandler.alternativedownloadurl,
								eh.getMessage() + "!\nSoll die Download-Website geöffnet werden?");
					} catch (IOException e1) {
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