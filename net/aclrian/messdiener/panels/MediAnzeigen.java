package net.aclrian.messdiener.panels;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import net.aclrian.messdiener.deafault.ATextField;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.window.APanel;
import net.aclrian.messdiener.window.auswaehlen.ATable;

public class MediAnzeigen extends APanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 9198327596275692676L;
	private JTextField textField = new ATextField("Vorname");
	private JTextField textField_1 = new ATextField("Nachname");
	private JSpinner spinner = new JSpinner();
	private JCheckBox chckbxLeiter = new JCheckBox("Leiter");
	private JScrollPane scrollPane = new JScrollPane();
	private JLabel lblGeschwister = new JLabel("Geschwister");
	private JButton button = new JButton(References.pfeillinks);
	private JButton button_1 = new JButton(References.pfeilrechts);
	private JScrollPane scrollPane_1 = new JScrollPane();
	private JLabel lblFreunde = new JLabel("Freunde");
	private JButton btnSpeichern = new JButton("Speichern");
	private JButton btnAbbrechen = new JButton("Abbrechen");
	private ATable atable = new ATable();

	/**
	 * Create the panel.
	 **/
	public MediAnzeigen(int dfbtnwidth, int dfbtnheight) {// int withe, int heigh) {// , WMainFrame wmf) {
		super(dfbtnwidth, dfbtnheight);
		setLayout(null);
		add(textField);
		add(textField_1);
		add(spinner);
		add(chckbxLeiter);
		add(scrollPane);
		scrollPane.setColumnHeaderView(lblGeschwister);
		add(button);
		add(button_1);
		add(scrollPane_1);
		scrollPane_1.setColumnHeaderView(lblFreunde);
		add(btnSpeichern);
		add(btnAbbrechen);
		add(atable);
		setVisible(true);
	}

	protected void graphics(int width, int heigth) {
		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		textField.setBounds(abstandweit, abstandhoch + stdhoehe, drei - abstandweit, stdhoehe);
		textField_1.setBounds(drei+abstandweit, abstandhoch + stdhoehe, drei - 2*abstandweit, stdhoehe);
		chckbxLeiter.setBounds(2*drei+abstandweit, abstandhoch+stdhoehe, drei-2*abstandweit, stdhoehe);
		btnAbbrechen.setBounds(abstandweit, heigth-abstandhoch-this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		btnSpeichern.setBounds(width-abstandweit-this.getDfbtnwidth(), heigth-abstandhoch-this.getDfbtnheight(), this.getDfbtnwidth(), this.getDfbtnheight());
		atable.setBounds(abstandweit, (int) (abstandhoch+drei*0.5), drei+drei, stdhoehe+stdhoehe);
		scrollPane_1.setBounds(269, 427, 257, 140);
		button.setBounds(52, 324, 117, 25);
		scrollPane.setBounds(269, 275, 257, 140);
		
		spinner.setBounds(22, 43, 28, 20);
		button_1.setBounds(52, 510, 117, 25);
	}

}