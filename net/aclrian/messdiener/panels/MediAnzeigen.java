package net.aclrian.messdiener.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.aclrian.messdiener.deafault.ATextField;

public class MediAnzeigen extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 9198327596275692676L;
	private JTextField textField = new ATextField("Vorname");
	private JTextField textField_1 = new ATextField("Nachname");
	private JSpinner spinner = new JSpinner();
	private JCheckBox chckbxLeiter = new JCheckBox("Leiter");
	private JCheckBox chckbxDiAbend = new JCheckBox("Di, Abend");
	private JCheckBox chckbxSaMorgen = new JCheckBox("Sa, Morgen");
	private JCheckBox chckbxSaTaufe = new JCheckBox("Sa, Taufe");
	private JCheckBox chckbxSaHochzeit = new JCheckBox("Sa, Hochzeit");
	private JCheckBox chckbxSaAbend = new JCheckBox("Sa, Abend");
	private JCheckBox chckbxSoMorgen = new JCheckBox("So, Morgen");
	private JCheckBox chckbxSoTaufe = new JCheckBox("So, Taufe");
	private JCheckBox chckbxSoAbend = new JCheckBox("So, Abend");
	private JScrollPane scrollPane = new JScrollPane();
	private JLabel lblGeschwister = new JLabel("Geschwister");
	private JButton button = new JButton("-->");
	private JButton button_1 = new JButton("-->");
	private JScrollPane scrollPane_1 = new JScrollPane();
	private JLabel lblFreunde = new JLabel("Freunde");
	private JButton btnSpeichern = new JButton("Speichern");
	private JButton btnAbbrechen = new JButton("Abbrechen");

	/**
	 * Create the panel.
	 **/
	public MediAnzeigen(String s) {// int withe, int heigh) {// , WMainFrame wmf) {
		setLayout(null);

		setBorder(new BevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY,
				Color.LIGHT_GRAY));

		// this.setBounds(0, 0, 567, 655);//setBounds(138, 11, withe, heigh);
		setLayout(null);
		if (s == null) {
			this.setBounds(0, 0, 567, 655);
		}
		add(textField);
		// textField.setColumns(10);
		add(textField_1);
		// textField_1.setColumns(10);
		add(spinner);
		add(chckbxLeiter);
		add(chckbxDiAbend);
		add(chckbxSaMorgen);
		add(chckbxSaTaufe);
		add(chckbxSaHochzeit);
		add(chckbxSaAbend);
		add(chckbxSoMorgen);
		add(chckbxSoTaufe);
		add(chckbxSoAbend);
		add(scrollPane);
		scrollPane.setColumnHeaderView(lblGeschwister);
		add(button);
		add(button_1);
		add(scrollPane_1);
		scrollPane_1.setColumnHeaderView(lblFreunde);
		add(btnSpeichern);
		add(btnAbbrechen);
		setVisible(true);
		graphics(getWidth(), getHeight());
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) { // if(!isworking) {
				Component c = (Component) e.getSource();
				int Lwidth = c.getBounds().width;
				int Lheigth = c.getBounds().height; // setVisible(false);
				// setBounds(c.getBounds()); System.out.println(c.getWidth()+"?" +
				// c.getHeight());
				graphics(Lwidth, Lheigth);
				setVisible(true);
			}
		});

	}

	protected void graphics(int width, int heigth) {
		this.setBounds(this.getBounds().x, this.getBounds().y, 488, 616);

		int drei = width / 3;
		int stdhoehe = heigth / 20;
		int abstandhoch = heigth / 100;
		int abstandweit = width / 100;
		textField.setBounds(abstandweit, abstandhoch + stdhoehe, drei - abstandweit, stdhoehe);
		textField_1.setBounds(drei, abstandhoch + stdhoehe, drei - abstandweit, stdhoehe);
		btnAbbrechen.setBounds(52, 607, 117, 25);
		btnSpeichern.setBounds(438, 618, 117, 25);
		scrollPane_1.setBounds(269, 427, 257, 140);
		button.setBounds(52, 324, 117, 25);
		scrollPane.setBounds(269, 275, 257, 140);
		chckbxSoAbend.setBounds(397, 233, 129, 23);
		chckbxSoTaufe.setBounds(397, 206, 129, 23);
		chckbxSoMorgen.setBounds(397, 175, 129, 23);
		chckbxSaAbend.setBounds(397, 140, 129, 23);
		chckbxSaHochzeit.setBounds(397, 108, 129, 23);
		chckbxSaTaufe.setBounds(397, 81, 129, 23);
		chckbxSaMorgen.setBounds(397, 54, 129, 23);
		chckbxDiAbend.setBounds(397, 25, 129, 23);
		chckbxLeiter.setBounds(165, 54, 129, 23);
		spinner.setBounds(22, 43, 28, 20);
		button_1.setBounds(52, 510, 117, 25);
	}

}