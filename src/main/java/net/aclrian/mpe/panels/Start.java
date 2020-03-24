package net.aclrian.mpe.panels;

import java.net.URI;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;

import net.aclrian.mpe.start.AProgress;

@Deprecated
public class Start extends APanel {

	private static final long serialVersionUID = 7258230476105946056L;

	// private JLabel title = new
	// JLabel("<html><body><h1>MessdienerplanErsteller</h1></body></html>");
	private JLabel unterueberschrift = new JLabel();
	private JLabel pfarreilabel = new JLabel();
	private JButton speicherortaendern = new JButton(
			"<html><body>Speicherort ändern</body></html>");
	private JButton plangenerieren = new JButton("<html><body><h2>Plan generieren</h2></body></html>");
	private JButton pfaendern = new JButton("<html><body>Pfarrei ändern</body></html>");
	private JButton ferienplan = new JButton("Ferienplan erstellen");
	private JButton smanzeigen = new JButton(
			"<html><body>Standardmesse für <br>jeden Medi anzeigen</body></html>");
	private JEditorPane ep;
	private URI uri;
	@Deprecated
	public Start(int dfbtnwidht, int dfbtnheigth, AProgress ap) {
		super(dfbtnwidht, dfbtnheigth, false, "Willkommen beim MessdienerplanErsteller", ap);
		/*
		 * this.setLayout(null); //Font font = title.getFont(); /* StringBuffer style =
		 * new StringBuffer("font-family:" + font.getFamily() + ";");
		 * style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
		 * style.append("font-size:" + font.getSize() + "pt;");* / uri = null; try { uri
		 * = new URI("https://github.com/Aclrian/MessdienerPlanErsteller/"); } catch
		 * (URISyntaxException e2) { e2.printStackTrace(); } ep = new
		 * JEditorPane("text/html", "<html>" + "  <head></head><body>" +
		 * "    <a color=\"white\" href=\"https://github.com/Aclrian/MessdienerPlanErsteller/\">"
		 * + "<img src=\"" + References.class.getResource("GitHub_Logo_White.png") +
		 * "\" width=\"100\" height=\"41\" border=\"0\">" + "</a>  </body>" +
		 * "</html>"); ep.addHyperlinkListener(new HyperlinkListener() {
		 * 
		 * @Override public void hyperlinkUpdate(HyperlinkEvent e) { if
		 * (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) try {
		 * Desktop.getDesktop().browse(uri); } catch (Exception e1) { new
		 * Erroropener(e1); e1.printStackTrace(); } } }); ep.setEditable(false);
		 * add(ep); ep.setVisible(true);
		 * 
		 * unterueberschrift .setText("<html><body><h2>version: " + AProgress.VersionID
		 * + " von Aclrian</h2></body></html>");
		 * unterueberschrift.setHorizontalAlignment(JLabel.RIGHT);
		 * add(unterueberschrift); unterueberschrift.setVisible(true);
		 * 
		 * pfarreilabel.setText("<html><body><h2>f" + References.ue + "r <i>" +
		 * ap.getAda().getPfarrei().getName() + "</i></h2></body></html>");
		 * pfarreilabel.setHorizontalAlignment(JLabel.CENTER); add(pfarreilabel);
		 * pfarreilabel.setVisible(true);
		 * 
		 * // title.setHorizontalAlignment(JLabel.CENTER); // add(title); //
		 * title.setVisible(true);
		 * 
		 * add(smanzeigen); smanzeigen.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { StandartMesse sm =
		 * StandartMesseListePanel.getStandartmesse(ap); if (sm != null && !(sm
		 * instanceof Sonstiges)) { ap.getWAlleMessen().changeAP(sm); } else {
		 * Utilities.logging(this.getClass(), "init",
		 * "Die Eingabe wurde vom Benutzer abgebrochen."); } } });
		 * 
		 * add(ferienplan); ferienplan.addActionListener(e -> { if
		 * (ap.getAda().getMesenarray().size() > 0) {
		 * ap.getWAlleMessen().changeAP(EnumActivePanel.Abmelden, true); } else { new
		 * Erroropener(new Exception("Erst Messen erstellen")); } });
		 * ferienplan.setVisible(true);
		 * 
		 * add(speicherortaendern); speicherortaendern.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { String s =
		 * System.getProperty("user.home"); s += DateienVerwalter.textdatei; File f =
		 * new File(s); f.delete(); ap.getWAlleMessen().dispose(); ap.setNeu(true); }
		 * }); speicherortaendern.setVisible(true);
		 * 
		 * add(plangenerieren); plangenerieren.addActionListener(e -> { if
		 * (ap.getAda().getMesenarray().size() > 0) { herzstueck(ap); } else { new
		 * Erroropener(new Exception("Erst Messen erstellen")); } });
		 * plangenerieren.setVisible(true);
		 * 
		 * add(pfaendern); pfaendern.addActionListener(e ->
		 * ap.pfarreiaendern(ap.getAda().getPfarrei())); pfaendern.setVisible(true);
		 * graphics(); setVisible(true);
		 */
	}

	@Override
	public void graphics() {
		int width = this.getBounds().width;
		int heigth = this.getBounds().height;
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		// title.setBounds(abstandweit, abstandhoch, width - 2 * abstandweit, stdhoehe);
		// title.setVisible(true);
		pfarreilabel.setBounds(abstandweit, 2 * abstandhoch + stdhoehe, width - 2 * abstandweit, stdhoehe);
		pfarreilabel.setVisible(true);
		unterueberschrift.setBounds(abstandweit, heigth - abstandhoch - stdhoehe, width - 3 * abstandweit, stdhoehe);
		unterueberschrift.setVisible(true);
		speicherortaendern.setBounds(2 * abstandweit, 5 * stdhoehe, drei - abstandweit, 3 * stdhoehe);
		speicherortaendern.setVisible(true);
		plangenerieren.setBounds(drei, 10 * stdhoehe, drei - abstandweit, 3 * stdhoehe);
		plangenerieren.setVisible(true);
		pfaendern.setBounds(2 * drei - abstandweit, 5 * stdhoehe, drei - abstandweit, 3 * stdhoehe);
		pfaendern.setVisible(true);
		ep.setBounds(abstandweit * 1 + 2, heigth - 50, 100, 41);
		ep.setVisible(true);
		ferienplan.setBounds(2 * abstandweit, 15 * stdhoehe, drei - abstandweit, 3 * stdhoehe);
		smanzeigen.setBounds(2 * drei - abstandweit, 15 * stdhoehe, drei - abstandweit, 3 * stdhoehe);
	}

	private void herzstueck(AProgress ap) {
		// ap.getWAlleMessen().changeAP(EnumActivePanel.Finish, true);
	}

	@Override
	public String toString() {
		return this.getBounds() + super.toString();
	}
}
