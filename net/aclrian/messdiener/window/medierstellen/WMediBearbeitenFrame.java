package net.aclrian.messdiener.window.medierstellen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

//import org.eclipse.wb.swing.FocusTraversalOnArray;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messverhalten;
import net.aclrian.messdiener.utils.DateienVerwalter;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.ReadFile;
import net.aclrian.messdiener.utils.References;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.WMainFrame;
import net.aclrian.messdiener.window.auswaehlen.ATable;
import javax.swing.ScrollPaneConstants;

/**
 * Fügt neue Messdiener hinzu und bearbeitet Messdiener
 * 
 * @author Aclrian
 *
 */
public class WMediBearbeitenFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * {@link WMediBearbeitenFrame#savepath}
	 */
	private String savepath;
	private JPanel contentPane;
	private JTextField textFieldVorname;
	private JTextField textFieldNachname;
	private JButton btnNeuerMedi = new JButton("Neuer Medi");
	private JButton btnffneMedi = new JButton(References.Oe+"ffne Medi");
	private JLabel lblVorname = new JLabel("Vorname");
	private JCheckBox chckbxLeiter = new JCheckBox("Leiter");
	private JLabel lblNachname = new JLabel("Nachname");
	private JLabel lblEintrittsjahr = new JLabel("Eintritt");
	private JSpinner spinnerEJahr = new JSpinner();
	private JButton btnAbbrechen = new JButton("Abbrechen");
	private JButton btnSpeichern = new JButton("<html><body>Speichern & Neu</body></html>");
	private JButton btnAnvertraute = new JButton("<html><body><p>Geschwister</p><p>und Freunde</p></body></html>");
	private JPanel panel = new JPanel();
	private boolean mobenWurdeGeoeffntet = false;
	/**
	 * der Messdiener, der bearbeitet wird
	 */
	private Messdiener moben;
	/**
	 * das aktuelle Jahr
	 */
	private final int currentyear = Calendar.getInstance().get(Calendar.YEAR);
	private String mobensavepath;
	private final ATable table = new ATable();

	/**
	 * Create the frame.
	 */
	public WMediBearbeitenFrame(WMainFrame wmf) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Messdienerverwaltung");
		int weite = 595;
		int hoehe = 320;
		setIconImage(WMainFrame.getIcon(new References()));
		setBounds(Utilities.setFrameMittig(weite, hoehe));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		textFieldVorname = new JTextField();
		textFieldNachname = new JTextField();
		btnNeuerMedi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				neuenMediAnlegen();
			}
		});
		this.savepath = wmf.getEDVVerwalter().getSavepath();
		btnNeuerMedi.setBounds(10, 11, 118, 23);
		contentPane.add(btnNeuerMedi);
		btnffneMedi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oeffne(wmf.getEDVVerwalter(), wmf);
			}
		});

		btnffneMedi.setBounds(10, 45, 118, 23);
		contentPane.add(btnffneMedi);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY,
				Color.LIGHT_GRAY));

		panel.setBounds(134, 11, 434, 261);
		contentPane.add(panel);
		panel.setLayout(null);
		panel.setVisible(false);
		btnAnvertraute.setHorizontalAlignment(SwingConstants.LEFT);
		btnAnvertraute.setFont(new Font("Dialog", Font.BOLD, 12));

		btnAnvertraute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wmf.getGeschwister();
			}
		});

		spinnerEJahr.setModel(new SpinnerNumberModel(2017, 2000, currentyear, 1));
		spinnerEJahr.setBounds(353, 8, 68, 20);
		panel.add(spinnerEJahr);
		/*
		 * panel.setFocusTraversalPolicy(new FocusTraversalOnArray( new
		 * Component[] { textFieldNachname, lblNachname, textFieldVorname,
		 * lblVorname, lblEintrittsjahr, spinnerEJahr, chckbxLeiter,
		 * chckbxDienstag, chckbxSaHochzeit, chckbxSoMorgen, chckbxSaAbends,
		 * chckbxSoTaufe, chckbxSoAbend, btnAnvertraute, btnAbbrechen,
		 * btnSpeichern }));
		 */btnAnvertraute.setBounds(289, 68, 132, 46);
		panel.add(btnAnvertraute);
		textFieldVorname.setBounds(104, 8, 140, 20);
		panel.add(textFieldVorname);
		textFieldVorname.setColumns(10);

		lblVorname.setBounds(10, 8, 78, 20);
		panel.add(lblVorname);

		chckbxLeiter.setBounds(299, 35, 78, 21);
		panel.add(chckbxLeiter);

		lblNachname.setBounds(10, 36, 99, 20);
		panel.add(lblNachname);

		textFieldNachname.setBounds(104, 36, 140, 20);
		panel.add(textFieldNachname);
		textFieldNachname.setColumns(10);

		lblEintrittsjahr.setBounds(299, 8, 68, 20);
		panel.add(lblEintrittsjahr);

		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abbrechen(wmf);
			}
		});
		btnAbbrechen.setBounds(289, 211, 132, 35);
		panel.add(btnAbbrechen);

		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speichern(wmf);
			}
		});
		btnSpeichern.setBounds(289, 123, 132, 35);
		panel.add(btnSpeichern);

		JButton btnspeichernundSchlieen = new JButton("<html><body>Speichern </br>& Schli"+References.ss+"en</body></html>");
		btnspeichernundSchlieen.setHorizontalTextPosition(SwingConstants.CENTER);
		btnspeichernundSchlieen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				speichernundschliessen(wmf);
			}
		});
		btnspeichernundSchlieen.setBounds(289, 167, 132, 35);
		panel.add(btnspeichernundSchlieen);

		JScrollPane smaPane = new JScrollPane();
		smaPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		smaPane.setBounds(10, 64, 269, 189);
		panel.add(smaPane);

		JLabel lblMessdaten = new JLabel("Messdaten");
		lblMessdaten.setHorizontalTextPosition(SwingConstants.CENTER);
		lblMessdaten.setHorizontalAlignment(SwingConstants.CENTER);
		smaPane.setColumnHeaderView(lblMessdaten);

		smaPane.setViewportView(table);
		// contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new
		// Component[]{btnNeuerMedi, btnffneMedi, panel, btnAnvertraute,
		// lblVorname,
		// textFieldVorname, textFieldNachname, spinnerEJahr, chckbxLeiter,
		// lblNachname,
		// lblEintrittsjahr, chckbxDienstag, chckbxSaHochzeit, chckbxSoMorgen,
		// chckbxSoTaufe, chckbxSoAbend, btnSpeichern, btnAbbrechen,
		// btnspeichernundSchlieen, chckbxSaAbends}));
	}

	public void speichernundschliessen(WMainFrame wmf) {
		DateienVerwalter u = wmf.getEDVVerwalter();
		if (textFieldVorname.getText().equals("") || textFieldNachname.getText().equals("")) {
			new Erroropener("Unvollst"+References.ae+"ndige Eingabe!");
		} else {
			Messdiener me = getMeVonEingabe(wmf);
			if (mobenWurdeGeoeffntet) {
				if (!moben.getNachnname().equals(me.getNachnname()) && !moben.getVorname().equals(me.getVorname())) {
					File f = new File(mobensavepath);
					if (f.exists()) {
						f.delete();
					}
				}
				if (!moben.getNachnname().equals(me.getNachnname())) {
					File f = new File(mobensavepath);
					if (f.exists()) {
						f.delete();
					}
				}
				if (!moben.getVorname().equals(me.getVorname())) {
					File f = new File(mobensavepath);
					if (f.exists()) {
						f.delete();
					}
				}
			}
			if (this.savepath.equals("")) {
				this.savepath = u.waehleOrdner();
			}

			me.makeXML(savepath, wmf);
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, "Erfolgreich gespeichert!", "Gespeichert!",
					JOptionPane.INFORMATION_MESSAGE);
			moben = null;
			setzeleer(wmf);
			chckbxLeiter.setSelected(false);
			// this.mobenWurdeGeoeffntet= true;
			// this.mobensavepath = savepath+ "//" + moben.makeId() + ".xml";
			wmf.erneuern();
			abbrechen(wmf);
		}

	}

	public void abbrechen(WMainFrame wmf) {
		panel.setVisible(false);
		moben = null;
		btnffneMedi.setEnabled(true);
		btnNeuerMedi.setEnabled(true);
		setzeleer(wmf);
	}

	/**
	 * legt einen neuen Messdiener an
	 */
	public void neuenMediAnlegen() {
		this.mobenWurdeGeoeffntet = false;
		btnffneMedi.setEnabled(false);
		btnNeuerMedi.setEnabled(false);
		panel.setVisible(true);
	}

	/**
	 * startet {@link WWAnvertraueFrame} fuer den aktuellen Messdiener nicht nur
	 * fuer Geschwister
	 */
	public boolean getGeschwister(WMainFrame wmf) {
		boolean nullpointer = false;
		try {
			moben.makeId();
		} catch (Exception e) {
			nullpointer = true;
		}
		if (nullpointer == false) {
			return true;
		} else {
			new Erroropener(
					"Du solltest den Messdiner erst einmal speichern, damit das Programm auch wirklich wei"+References.GROssenSZ+", wie der Messdiener hei"+References.ss+"t!");
			return false;
		}

	}

	/**
	 * oeffnet einen Messdiener
	 */
	public void oeffne(DateienVerwalter u, WMainFrame wmf) {
		String pfadmitDaoE = null;
		boolean abbruch = false;
		try {
			pfadmitDaoE = u.getMessdienerFile(savepath);
			File f = new File(pfadmitDaoE);
			savepath = f.getParent();
		} catch (NullPointerException e) {
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),
					"Vorgang wurde durch den Benutzer abgebrochen!");
			abbruch = true;
		}
		if (abbruch == false && pfadmitDaoE != null) {
			ReadFile rf = new ReadFile();
			Messdiener me = rf.getMessdiener(pfadmitDaoE, wmf);
			setzeMedi(me, wmf);
			moben = me;
			panel.setVisible(true);
			btnffneMedi.setEnabled(false);
			btnNeuerMedi.setEnabled(false);
			this.mobenWurdeGeoeffntet = true;
			this.mobensavepath = pfadmitDaoE;
		}
	}

	/**
	 * speichert einen neuen Messdiener
	 */
	public void speichern(WMainFrame wmf) {
		DateienVerwalter u = wmf.getEDVVerwalter();
		if (textFieldVorname.getText().equals("") || textFieldNachname.getText().equals("")) {
			new Erroropener("Unvollst"+References.ae+"ndige Eingabe!");
		} else {
			Messdiener me = getMeVonEingabe(wmf);
			if (mobenWurdeGeoeffntet) {
				if (!moben.getNachnname().equals(me.getNachnname()) && !moben.getVorname().equals(me.getVorname())) {
					File f = new File(mobensavepath);
					if (f.exists()) {
						f.delete();
					}
				}
				if (!moben.getNachnname().equals(me.getNachnname())) {
					File f = new File(mobensavepath);
					if (f.exists()) {
						f.delete();
					}
				}
				if (!moben.getVorname().equals(me.getVorname())) {
					File f = new File(mobensavepath);
					if (f.exists()) {
						f.delete();
					}
				}
			}

			if (this.savepath.equals("")) {
				this.savepath = u.waehleOrdner();
			}

			me.makeXML(savepath, wmf);
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f,
					"Erfolgreich gespeichert! Du kannst nun einen neuen Messdiener anlegen. Wenn du den alten bearbeiten willst musst du ihn erst  wieder "+References.oe+"ffnen!",
					"Gespeichert!", JOptionPane.INFORMATION_MESSAGE);
			moben = me;
			textFieldVorname.setText("");
			spinnerEJahr.setValue(currentyear);
			chckbxLeiter.setSelected(false);
			// this.mobenWurdeGeoeffntet= true;
			// this.mobensavepath = savepath+ "//" + moben.makeId() + ".xml";
		}
		textFieldNachname.requestFocus();
		wmf.erneuern();
	}

	/**
	 * erstellt aus den Angaben aus den TextFelder / sonstigen
	 * Benutzeroberflaechen-Komponenten einen neuen Messdiener
	 * 
	 * @return neuer Messdiener
	 */
	public Messdiener getMeVonEingabe(WMainFrame wmf) {
		Messdiener me = new Messdiener();
		if (mobenWurdeGeoeffntet) {
			me = moben;
		}
		Messverhalten mv = table.getMessdaten(wmf);
		Object jahr = spinnerEJahr.getModel().getValue();
		int ja = Integer.parseInt(String.valueOf(jahr));
		me.setzeAllesNeu(textFieldVorname.getText(), textFieldNachname.getText(), ja, chckbxLeiter.isSelected(), mv);
		return me;
	}

	/**
	 * 
	 * @param vorname
	 *            Vorname
	 * @param nachname
	 *            Nachname
	 * @param Ejahr
	 *            Eintrittsjahr
	 * @param leiter
	 *            ist er/sie Leiter
	 * @param mv
	 *            / sein Messverhalten
	 * @param wmf
	 */
	private void setzeAlles(String vorname, String nachname, int Ejahr, boolean leiter, Messverhalten mv,
			WMainFrame wmf) {
		textFieldVorname.setText(vorname);
		textFieldNachname.setText(nachname);
		spinnerEJahr.setValue(Ejahr);
		chckbxLeiter.setSelected(leiter);
		table.setMessverhalten(mv, wmf);
	}

	/**
	 * setzt die Benutzereingaben leer
	 */
	private void setzeleer(WMainFrame wmf) {
		setzeAlles("", "", currentyear, false, new Messverhalten(wmf), wmf);

	}

	/**
	 * setzt die Eingaben fuer einen Messdiener ein
	 * 
	 * @param me
	 * @param wmf
	 */
	public void setzeMedi(Messdiener me, WMainFrame wmf) {
		setzeAlles(me.getVorname(), me.getNachnname(), me.getEintritt(), me.isIstLeiter(), me.getDienverhalten(), wmf);
		moben = me;
	}

	public Messdiener getMoben() {
		return moben;
	}

	public void setback(Messdiener me, WWAnvertrauteFrame wwAnvertrauteFrame, WMainFrame wmf) {
		// Messdiener tmp = me;
		abbrechen(wmf);
		/*
		 * setzeMedi(me); moben = me; panel.setVisible(true);
		 * btnffneMedi.setEnabled(false); btnNeuerMedi.setEnabled(false);
		 * this.mobenWurdeGeoeffntet = true;
		 */
	}
}
