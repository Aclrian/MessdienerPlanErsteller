package net.aclrian.mpe.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.aclrian.mpe.components.AList;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.WriteFile;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.start.WEinFrame;
import net.aclrian.mpe.start.WEinFrame.EnumActivePanel;
import net.aclrian.mpe.utils.Erroropener;

public class StandartMesseListePanel extends APanel {

	private static final long serialVersionUID = -4151999826909159522L;

	private JScrollPane pane = new JScrollPane();
	private JLabel label = new JLabel("Ausw" + References.ae + "hlen");
	private AList<Messdiener> alist;

	public StandartMesseListePanel(int dfbtnwidth, int dfbtnheight, ArrayList<Messdiener> hauptarray,
			Comparator<Messdiener> comp, AProgress ap, StandartMesse dfmesse) {
		super(dfbtnwidth, dfbtnheight, true, dfmesse.toBenutzerfreundlichenString(),ap);//"Standartmesse für alle Messdiener anzeigen:", ap);
		alist = new AList<Messdiener>(hauptarray, comp);
		for (Messdiener messdiener : hauptarray) {
			if (messdiener.getDienverhalten().getBestimmtes(dfmesse, ap.getAda())) {
				alist.setSelected(messdiener);
			}
		}

		add(pane);
		pane.setViewportView(alist);
		pane.setColumnHeaderView(label);
		label.setText(label.getText() + " f" + References.ue + "r " + dfmesse.toBenutzerfreundlichenString());
		getBtnSpeichern().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Messdiener> list = alist.getSelected();
				for (Messdiener messdiener : alist.getAlle()) {
					if (list.contains(messdiener)) {
						messdiener.getDienverhalten().editiereBestimmteMesse(ap.getAda(), dfmesse, true);
					} else {
						messdiener.getDienverhalten().editiereBestimmteMesse(ap.getAda(), dfmesse, false);
					}
					WriteFile wf = new WriteFile(messdiener, ap.getAda().getUtil().getSavepath());
					try {
						wf.toXML(ap);
					} catch (Exception e1) {
						new Erroropener(e1);
						e1.printStackTrace();
					}
				}
				ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true);
			}

		});
		getBtnAbbrechen().addActionListener(e -> ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true));
		graphics();
	}

	public void setTitle(String title) {
		label.setText(title);
		pane.setColumnHeaderView(label);
	}

	public static StandartMesse getStandartmesse(AProgress ap) {
		ArrayList<String> slist = new ArrayList<>();
		ArrayList<StandartMesse> smm = new ArrayList<>();
		ap.getAda().getPfarrei().getStandardMessen().sort(StandartMesse.comfuerSMs);
		for (StandartMesse sms : ap.getAda().getPfarrei().getStandardMessen()) {
			if (!(sms instanceof Sonstiges)) {
				slist.add(sms.toBenutzerfreundlichenString());
				smm.add(sms);
			}
		}
		String[] s = new String[smm.size()];
		s = slist.toArray(s);
		StandartMesse[] sm = new StandartMesse[smm.size()];
		sm = smm.toArray(sm);
		/*
		 * JOptionPane op = new
		 * JOptionPane("Welche Standartmesse soll angezeigt werden?",
		 * JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, s);
		 */
		JOptionPane jOptionPane = new JOptionPane("Welche Standartmesse soll angezeigt werden?",
				JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, s);
		WEinFrame.farbe(jOptionPane);
		JDialog inputDialog = jOptionPane.createDialog(ap.getWAlleMessen(),
				"Welche Standartmesse soll angezeigt werden?");
		inputDialog.setVisible(true);
		if(jOptionPane.getValue() != null) {
		for (StandartMesse standartMesse : sm) {
			if(standartMesse.toBenutzerfreundlichenString().equals((String)jOptionPane.getValue())) {
				return standartMesse;
			}
		}}
		return null;
	}

	@Override
	public void graphics() {
		int width = this.getBounds().width;
		int heigth = this.getBounds().height;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		//int eingenschaften = width / 5;
		int stdhoehe = heigth / 20;
		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(),
				heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		pane.setBounds(abstandweit+getBtnAbbrechen().getBounds().x+getBtnAbbrechen().getBounds().width, abstandhoch+stdhoehe*2, width-2*abstandweit-2*getDfbtnwidth(), 15*stdhoehe);
	}
}
