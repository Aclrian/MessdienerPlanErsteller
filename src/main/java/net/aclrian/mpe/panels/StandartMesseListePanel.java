package net.aclrian.mpe.panels;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.aclrian.mpe.components.AList;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.start.References;
@Deprecated
public class StandartMesseListePanel extends APanel {

	private static final long serialVersionUID = -4151999826909159522L;
	@Deprecated
	public static StandartMesse getStandartmesse(AProgress ap) {
		/*
		 * ArrayList<String> slist = new ArrayList<>(); ArrayList<StandartMesse> smm =
		 * new ArrayList<>();
		 * ap.getAda().getPfarrei().getStandardMessen().sort(StandartMesse.comfuerSMs);
		 * for (StandartMesse sms : ap.getAda().getPfarrei().getStandardMessen()) { if
		 * (!(sms instanceof Sonstiges)) {
		 * slist.add(sms.toBenutzerfreundlichenString()); smm.add(sms); } } String[] s =
		 * new String[smm.size()]; s = slist.toArray(s); StandartMesse[] sm = new
		 * StandartMesse[smm.size()]; sm = smm.toArray(sm); /* JOptionPane op = new
		 * JOptionPane("Welche Standartmesse soll angezeigt werden?",
		 * JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, s); /
		 * JOptionPane jOptionPane = new
		 * JOptionPane("Welche Standartmesse soll angezeigt werden?",
		 * JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, s);
		 * WEinFrame.farbe(jOptionPane); JDialog inputDialog =
		 * jOptionPane.createDialog(ap.getWAlleMessen(),
		 * "Welche Standartmesse soll angezeigt werden?"); inputDialog.setVisible(true);
		 * if (jOptionPane.getValue() != null) { for (StandartMesse standartMesse : sm)
		 * { if
		 * (standartMesse.toBenutzerfreundlichenString().equals(jOptionPane.getValue()))
		 * { return standartMesse; } } }
		 */
		return null;
	}

	private JScrollPane pane = new JScrollPane();
	private JLabel label = new JLabel("Ausw" + References.ae + "hlen");

	private AList<Messdiener> alist;

	public StandartMesseListePanel(int dfbtnwidth, int dfbtnheight, ArrayList<Messdiener> hauptarray,
			Comparator<Messdiener> comp, AProgress ap, StandartMesse dfmesse) {
		super(dfbtnwidth, dfbtnheight, true, dfmesse.toBenutzerfreundlichenString(), ap);// "Standartmesse für alle
																							// Messdiener anzeigen:",
																							// ap);
		/*
		 * alist = new AList<>(hauptarray, comp); for (Messdiener messdiener :
		 * hauptarray) { if (messdiener.getDienverhalten().getBestimmtes(dfmesse,
		 * ap.getAda())) { alist.setSelected(messdiener); } }
		 * 
		 * add(pane); pane.setViewportView(alist); pane.setColumnHeaderView(label);
		 * label.setText(label.getText() + " f" + References.ue + "r " +
		 * dfmesse.toBenutzerfreundlichenString());
		 * getBtnSpeichern().addActionListener(e -> { ArrayList<Messdiener> list =
		 * alist.getSelected(); for (Messdiener messdiener : alist.getAlle()) { if
		 * (list.contains(messdiener)) {
		 * messdiener.getDienverhalten().editiereBestimmteMesse(ap.getAda(), dfmesse,
		 * true); } else {
		 * messdiener.getDienverhalten().editiereBestimmteMesse(ap.getAda(), dfmesse,
		 * false); } WriteFile wf = new WriteFile(messdiener,
		 * ap.getAda().getUtil().getSavepath()); try { wf.toXML(ap); } catch (Exception
		 * e1) { new Erroropener(e1); e1.printStackTrace(); } }
		 * ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true); });
		 * getBtnAbbrechen().addActionListener(e ->
		 * ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true)); graphics();
		 */
	}

	@Override
	public void graphics() {
		int width = this.getBounds().width;
		int heigth = this.getBounds().height;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		// int eingenschaften = width / 5;
		int stdhoehe = heigth / 20;
		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - getDfbtnheight(), getDfbtnwidth(),
				getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - getDfbtnwidth(), heigth - abstandhoch - getDfbtnheight(),
				getDfbtnwidth(), getDfbtnheight());
		pane.setBounds(abstandweit + getBtnAbbrechen().getBounds().x + getBtnAbbrechen().getBounds().width,
				abstandhoch + stdhoehe * 2, width - 2 * abstandweit - 2 * getDfbtnwidth(), 15 * stdhoehe);
	}

	public void setTitle(String title) {
		label.setText(title);
		pane.setColumnHeaderView(label);
	}
}
