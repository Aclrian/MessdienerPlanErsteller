package net.aclrian.update;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.aclrian.messdiener.window.WMainFrame;

class VersionIDHandler {
	public static final String tag = "tag_name";
	public static final String beta_start = "b";
	public static final String urlToLatestReleaseJsonFile = "https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest";

	public String searchVersionID() throws FileNotFoundException, URISyntaxException, IOException, ParseException {
		JSONObject jsonobj = doThigswithExceptions();
		String s = (String) jsonobj.get(tag);
		return s;
	}

	public EnumHandling rankingVersionID() {
		String latestID;
		try {
			latestID = searchVersionID();
		} catch (URISyntaxException | IOException | ParseException e) {
			e.printStackTrace();
			return EnumHandling.Error;
		}
		System.out.println("Running with: " + WMainFrame.VersionID + " found: " + latestID );
		if (WMainFrame.VersionID.equals(latestID)) {
			return EnumHandling.isTheLatest;
		}
		String programmsvid = WMainFrame.VersionID;
		int programmsversion;
		if (programmsvid.startsWith(beta_start)) {
			String tmp = programmsvid.substring(1);
			programmsversion = Integer.parseInt(tmp);
		} else {
			programmsversion = Integer.parseInt(WMainFrame.VersionID);
		}
		int version;
		if (latestID.startsWith(beta_start)) {
			String s = latestID.substring(1);
			version = Integer.parseInt(s);
		} else {
			version = Integer.parseInt(latestID);
		}
		if ((latestID.startsWith(beta_start) && WMainFrame.VersionID.startsWith(beta_start))|| (!latestID.startsWith(beta_start) && !WMainFrame.VersionID.startsWith(beta_start))) {
			if (programmsversion > version) {
				return EnumHandling.isTooNew;
			}
		}
		else {
			if (WMainFrame.VersionID.startsWith(beta_start)) {
				if (programmsversion/1000 >= version) {
					return EnumHandling.isTooNew;
				}
			}
			if (latestID.startsWith(beta_start)) {
				
			}
		}
		return EnumHandling.isOld;
	}

	private JSONObject doThigswithExceptions()
			throws URISyntaxException, FileNotFoundException, IOException, ParseException {
		URL json = new URL(urlToLatestReleaseJsonFile);
		JSONParser parse = new JSONParser();
		InputStream is = json.openStream();
		Object obj = parse.parse(new InputStreamReader(is, Charset.forName("UTF-8")));
		JSONObject jsonobj = (JSONObject) obj;
		return jsonobj;
	}

	public static void main(String[] args) {
		VersionIDHandler vidh = new VersionIDHandler();
		EnumHandling eh = vidh.rankingVersionID();
		System.out.println(eh.name());
	}


	public enum EnumHandling {
		isTheLatest(), isOld(), isTooNew(), Error();
	}
}