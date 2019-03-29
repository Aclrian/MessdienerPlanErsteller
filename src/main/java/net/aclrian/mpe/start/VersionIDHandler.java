package net.aclrian.mpe.start;

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.BasicConfigurator;
//import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.Utilities;

public class VersionIDHandler {
	public static final String tag = "tag_name";
	public static final String beta_start = "b";
	public static final String urlToLatestReleaseJsonFile = "https://api.github.com/repos/Aclrian/MessdienerPlanErsteller/releases/latest";
	public static final String downloadurltag = "browser_download_url";
	private URI downloadurl;
	private URI directdownload;
	public static final String alternativedownloadurl = "https://github.com/Aclrian/MessdienerPlanErsteller/releases";

	public String searchVersionID() throws FileNotFoundException, URISyntaxException, IOException, ParseException {
		JSONObject jsonobj = doThigswithExceptions();
		String s = (String) jsonobj.get(tag);
		directdownload = new URI((String) "https://aclrian.github.io/MessdienerPlanErsteller/download.html");//((JSONObject)((JSONArray)jsonobj.get("assets")).get(0)).get("browser_download_url"));
		return s;
	}

	public EnumHandling rankingVersionID() {
		String internettid;
		try {
			internettid = searchVersionID();
			downloadurl = new URI("https://github.com/Aclrian/MessdienerPlanErsteller/releases/tag/" + internettid);
		} catch (URISyntaxException | IOException | ParseException e) {
			// new Erroropener(e.getMessage());
			return EnumHandling.error;
		}
		Utilities.logging(this.getClass(), "rankingVersionID",
				"Running with: " + AProgress.VersionID + " found: " + internettid);
		if (AProgress.VersionID.equals(internettid)) {
			return EnumHandling.isTheLatest;
		}
		String programmsvid = AProgress.VersionID;
		int localversion;
		if (programmsvid.startsWith(beta_start)) {
			String tmp = programmsvid.substring(1);
			localversion = Integer.parseInt(tmp);
		} else {
			localversion = Integer.parseInt(AProgress.VersionID);
		}
		int internetversion;
		if (internettid.startsWith(beta_start)) {
			String s = internettid.substring(1);
			internetversion = Integer.parseInt(s);
		} else {
			internetversion = Integer.parseInt(internettid);
		}
		if (localversion > internetversion) {
			// update or beta request
			if (AProgress.VersionID.equals(getBeta(AProgress.VersionID, internettid))) {
				return EnumHandling.isOld;
			}
			return EnumHandling.isTooNew;
		}
		if (internetversion > localversion) {
			if (internettid.equals(getBeta(AProgress.VersionID, internettid))) {
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
			throws URISyntaxException, FileNotFoundException, IOException, ParseException, UnknownHostException {
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
		Utilities.logging(this.getClass(), "act", eh.name());
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

			StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
			style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
			style.append("font-size:" + font.getSize() + "pt;");
			style.append("color:" + WEinFrame.neuhell1 + ";");
			style.append("background:" + WEinFrame.neuhell2);

			JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" //
					+ "Das Programm fand eine neue Beta-Version. <a href=\"" + downloadurl
					+ "\">Zum Update klicke hier</a>" //
					+ "</body></html>");

			ep.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
						try {
							Desktop.getDesktop().browse(downloadurl);
						} catch (Exception e1) {
							new Erroropener(e1);
							e1.printStackTrace();
						}
				}
			});
			ep.setEditable(false);
			ep.setBackground(label.getBackground());
			JOptionPane.showMessageDialog(f, ep, "Neue Beta-Version", JOptionPane.INFORMATION_MESSAGE);
			break;
		case isOld:
			try {
				Desktop.getDesktop().browse(directdownload);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			JLabel label1 = new JLabel();
			label1.setForeground(WEinFrame.neuhell1);
			Font font1 = label1.getFont();
			StringBuffer style1 = new StringBuffer("body{");
			style1.append("font-weight:" + (font1.isBold() ? "bold" : "normal") + ";");
			style1.append("font-size:" + font1.getSize() + "pt;");
			style1.append("color:" + "rgb(" + WEinFrame.dunkel1.getRed() + "," + WEinFrame.dunkel1.getGreen() + ","
					+ WEinFrame.dunkel1.getBlue() + ");");
			style1.append("background:" + "rgb(" + WEinFrame.neuhell1.getRed() + "," + WEinFrame.neuhell1.getGreen()
					+ "," + WEinFrame.neuhell1.getBlue() + ");}");
			//System.out.println(style1);
			JEditorPane ep1 = new JEditorPane("text/html", "<html>"
					+ "<head><style>body{background:rgb(42,169,156);}a{font-weight:bold;font-size:12pt;color:rgb(4,72,65);background:rgb(42,169,156);}</style></head><body>"
					+ //
					"<a href=\"" + downloadurl + "\">Das Programm fand eine neue Version. Zum Update klicke hier</a>"
					+ "</body></html>");
			ep1.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
						try {
							Desktop.getDesktop().browse(downloadurl);
						} catch (Exception e1) {
							new Erroropener(e1);
							e1.printStackTrace();
						}
				}
			});
			ep1.setEditable(false);
			ep1.setBackground(label1.getBackground());
			JOptionPane op = new JOptionPane(ep1, JOptionPane.INFORMATION_MESSAGE);
			JFrame f1 = new JFrame();
			WEinFrame.farbe(op);
			JDialog dialog = op.createDialog(f1, "Neue Version!");
			dialog.setVisible(true);
			break;
		case error:
			new Erroropener(new Exception("Fehler beim Suche nach Updates..."));
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		if(args.length < 1) {
		WEinFrame.farbeFIRST();
		BasicConfigurator.configure();
		VersionIDHandler vidh = new VersionIDHandler();
		vidh.act();
		AProgress ap = new AProgress(true);
		ap.start();
		} else if(args[0].startsWith("csv")) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "Beschreibung";
				}
				
				@Override
				public boolean accept(File f) {
					return f.getName().endsWith(".csv");
				}
			});
			fc.setDialogTitle("W"+References.ae+"hle CSV-Datei");
			fc.setApproveButtonText("Ausw" + References.ae + "hlen");
		}
	}

	public enum EnumHandling {
		isTheLatest("Es wurde keine neuere Version gefunden"), isOld("Eine neue Version wurde gefunden"),
		isTooNew("Neuereste Version"), error("Beim Versions-Request kam es zu einem Fehler"),
		betaRequest("Neue beta Version gefunden");
		private String message;

		EnumHandling(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}