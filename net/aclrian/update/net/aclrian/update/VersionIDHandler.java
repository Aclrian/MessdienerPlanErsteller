package net.aclrian.update.net.aclrian.update;

import java.awt.Desktop;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.window.WMainFrame;

public class VersionIDHandler {
	public static final String tag = "tag_name";
	public static final String beta_start = "b";
	public static final String urlToLatestReleaseJsonFile = "https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest";
	public static final String downloadurltag = "browser_download_url";
	private URI downloadurl;
	public static final String alternativedownloadurl = "https://github.com/Aclrian/MessdienerPlanErsteller/releases";
	public String searchVersionID() throws FileNotFoundException, URISyntaxException, IOException, ParseException {
		JSONObject jsonobj = doThigswithExceptions();
		String s = (String) jsonobj.get(tag);
		/*String tmp = (String) jsonobj.get(downloadurltag);
		System.out.println(tmp);
		downloadurl = new URI(tmp);*/
		return s;
	}

	public EnumHandling rankingVersionID() {
		String internettid;
		try {
			internettid = searchVersionID();
			downloadurl= new URI("https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/"+internettid);
		} catch (URISyntaxException | IOException | ParseException e) {
			e.printStackTrace();
			return EnumHandling.error;
		}
		System.out.println("Running with: " + WMainFrame.VersionID + " found: " + internettid);
		if (WMainFrame.VersionID.equals(internettid)) {
			return EnumHandling.isTheLatest;
		}
		String programmsvid = WMainFrame.VersionID;
		int localversion;
		if (programmsvid.startsWith(beta_start)) {
			String tmp = programmsvid.substring(1);
			localversion = Integer.parseInt(tmp);
		} else {
			localversion = Integer.parseInt(WMainFrame.VersionID);
		}
		int internetversion;
		if (internettid.startsWith(beta_start)) {
			String s = internettid.substring(1);
			internetversion = Integer.parseInt(s);
		} else {
			internetversion = Integer.parseInt(internettid);
		}
		System.out.println("local: "+ WMainFrame.VersionID + " " + localversion);
		System.out.println("online: "+ internettid + " " + internetversion);
		if (localversion > internetversion) {
			// update or beta request
			if (WMainFrame.VersionID.equals(getBeta(WMainFrame.VersionID, internettid))) {
				return EnumHandling.isOld;
			}
			return EnumHandling.isTooNew;
		}
		if (internetversion > localversion) {
			if (internettid.equals(getBeta(WMainFrame.VersionID, internettid))) {
				return EnumHandling.betaRequest;
			}
			return EnumHandling.isOld;
		}
		return EnumHandling.isTheLatest;
		
	}

	private String getBeta(String versionid, String latestID) {
		if (!versionid.startsWith(beta_start) && latestID.startsWith(beta_start)) {
			return latestID;
		} else if (versionid.startsWith(beta_start) && !latestID.startsWith(beta_start)) {
			return versionid;
		}
		return "";
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

	public void act() {
		JFrame f = new JFrame();
		f.setAlwaysOnTop(true);
		EnumHandling eh = rankingVersionID();
		System.out.println(eh.name());
		if (eh != EnumHandling.error) {
			if (downloadurl == null) {
				try {
					downloadurl = new URI(alternativedownloadurl);
				} catch (URISyntaxException e1) {
					eh = EnumHandling.error;
				}
			}
		}
		switch (eh) {
		case betaRequest:
			JLabel label = new JLabel();
		    Font font = label.getFont();

		    // create some css from the label's font
		    StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
		    style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
		    style.append("font-size:" + font.getSize() + "pt;");

		    // html content
		    JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" //
		            + "Das Programm fand eine neue Beta-Version. <a href=\""+downloadurl+"\">Zum Update klicke hier</a>" //
		            + "</body></html>");

		    // handle link events
		    ep.addHyperlinkListener(new HyperlinkListener()
		    {
		        @Override
		        public void hyperlinkUpdate(HyperlinkEvent e)
		        {
		            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
						try {
							Desktop.getDesktop().browse(downloadurl);
						} catch (IOException e1) {
							e1.printStackTrace();
						} // roll your own link launcher or use Desktop if J6+
		        }
		    });
		    ep.setEditable(false);
		    ep.setBackground(label.getBackground());

		    JOptionPane.showMessageDialog(f,ep, "Neue Beta-Version", JOptionPane.INFORMATION_MESSAGE);
			break;			
		case isOld:
			JLabel label1 = new JLabel();
		    Font font1 = label1.getFont();

		    // create some css from the label's font
		    StringBuffer style1 = new StringBuffer("font-family:" + font1.getFamily() + ";");
		    style1.append("font-weight:" + (font1.isBold() ? "bold" : "normal") + ";");
		    style1.append("font-size:" + font1.getSize() + "pt;");

		    // html content
		    JEditorPane ep1 = new JEditorPane("text/html", "<html><body style=\"" + style1 + "\">" //
		            + "Das Programm fand eine neue Version. <a href=\""+downloadurl+"\">Zum Update klicke hier</a>" //
		            + "</body></html>");

		    // handle link events
		    ep1.addHyperlinkListener(new HyperlinkListener()
		    {
		        @Override
		        public void hyperlinkUpdate(HyperlinkEvent e)
		        {
		            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
						try {
							Desktop.getDesktop().browse(downloadurl);
						} catch (IOException e1) {
							e1.printStackTrace();
						} // roll your own link launcher or use Desktop if J6+
		        }
		    });
		    ep1.setEditable(false);
		    ep1.setBackground(label1.getBackground());
JOptionPane.showMessageDialog(f, ep1, "Neue Version", JOptionPane.INFORMATION_MESSAGE);
			break;
		case error:
			new Erroropener("<html>Fehler beim Suche nach Updates...<html>");
			break;
		default:
			//the latest or too new
			break;
		}
	}
	
	
	public static void main(String[] args) {
		VersionIDHandler vidh = new VersionIDHandler();
		vidh.act();
	}

	public enum EnumHandling {
		isTheLatest, isOld, isTooNew, error, betaRequest;
	}
}