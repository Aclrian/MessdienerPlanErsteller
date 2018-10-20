package net.aclrian.mpe.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import net.aclrian.mpe.components.AbmeldenTable;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.start.WEinFrame.EnumActivePanel;

public class AbmeldenPanel extends APanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5484960620506799921L;

	private AbmeldenTable abtable;
	private JScrollPane scrollpane = new JScrollPane();
	private JLabel label = new JLabel("Was soll der Hacken bedeuten:");
	private JRadioButton austeilen = new JRadioButton("kann nicht");
	private JRadioButton einteilen = new JRadioButton("kann dann");

	public AbmeldenPanel(int defaultButtonwidth, int defaultButtonheight, AProgress ap) {
		super(defaultButtonwidth, defaultButtonheight, true, "Ferienplan erstellen: Messdiener ab-/anmelden", ap);
		abtable = new AbmeldenTable(ap);
		scrollpane.setViewportView(abtable);
		austeilen.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(austeilen);
		bg.add(einteilen);
		add(label);
		add(austeilen);
		add(einteilen);
		add(scrollpane);
		getBtnSpeichern().setText("Plan erstellen");
		getBtnAbbrechen().addActionListener(e -> ap.getWAlleMessen().changeAP(EnumActivePanel.Start, true));
		getBtnSpeichern().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abtable.speichern(ap, einteilen.isSelected());
				ap.getWAlleMessen().changeAP(EnumActivePanel.Finish, true);
			}
		});
	}

	@Override
	public void graphics() {
		int width = this.getBounds().width;
		int heigth = this.getBounds().height;
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		scrollpane.setBounds(abstandweit, abstandhoch, width - 2 * abstandweit,
				heigth - 4 * abstandhoch - this.getDfbtnheight());
		getBtnAbbrechen().setBounds(abstandweit, heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(),
				this.getDfbtnheight());
		getBtnSpeichern().setBounds(width - abstandweit - this.getDfbtnwidth(),
				heigth - abstandhoch - this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		label.setBounds(2 * abstandweit + this.getDfbtnwidth(), heigth - 3 * abstandhoch - this.getDfbtnheight(), drei,
				stdhoehe);
		einteilen.setBounds(2 * abstandweit + this.getDfbtnwidth(),
				heigth - 3 * abstandhoch - this.getDfbtnheight() + stdhoehe, drei, stdhoehe);
		austeilen.setBounds(2 * abstandweit + this.getDfbtnwidth() + drei,
				heigth - 3 * abstandhoch - this.getDfbtnheight() + stdhoehe, drei, stdhoehe);
	}

}