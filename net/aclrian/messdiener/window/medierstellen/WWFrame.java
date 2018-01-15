package net.aclrian.messdiener.window.medierstellen;

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.utils.EnumdeafaultMesse;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Messverhalten;
import net.aclrian.messdiener.utils.ReadFile;
import net.aclrian.messdiener.utils.Util;
import net.aclrian.messdiener.window.WMainFrame;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Font;

/**
 * Fügt neue Messdiener hinzu und bearbeitet Messdiener
 * 
 * @author Aclrian
 *
 */
public class WWFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * {@link WWFrame#savepath}
	 */
	private String savepath;
	private JPanel contentPane;
	private JTextField textFieldVorname;
	private JTextField textFieldNachname;
	private JButton btnNeuerMedi = new JButton("Neuer Medi");
	private JButton btnffneMedi = new JButton("\u00D6ffne Medi");
	private JLabel lblVorname = new JLabel("Vorname");
	private JCheckBox chckbxLeiter = new JCheckBox("Leiter");
	private JLabel lblNachname = new JLabel("Nachname");
	private JLabel lblEintrittsjahr = new JLabel("Eintrittsjahr");
	private JSpinner spinnerEJahr = new JSpinner();
	private JCheckBox chckbxDienstag = new JCheckBox("Di, Abend");
	private JCheckBox chckbxSaHochzeit = new JCheckBox("Sa, Hochzeit");
	private JCheckBox chckbxSaAbends = new JCheckBox("Sa, Abend");
	private JCheckBox chckbxSoMorgen = new JCheckBox("So, Morgen");
	private JCheckBox chckbxSoTaufe = new JCheckBox("So, Taufe");
	private JCheckBox chckbxSoAbend = new JCheckBox("So, Abend");
	private JButton btnAbbrechen = new JButton("Abbrechen");
	private JButton btnSpeichern = new JButton("Speichern");
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

	/**
	 * Create the frame.
	 */
	public WWFrame(WMainFrame wmf) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 491, 249);
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
		this.savepath = wmf.getUtil().getSavepath();
		btnNeuerMedi.setBounds(10, 11, 118, 23);
		contentPane.add(btnNeuerMedi);
		btnffneMedi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oeffne(wmf.getUtil());
			}
		});

		btnffneMedi.setBounds(10, 45, 118, 23);
		contentPane.add(btnffneMedi);

		panel.setBounds(138, 11, 335, 193);
		contentPane.add(panel);
		panel.setLayout(null);
		panel.setVisible(false);

		textFieldVorname.setBounds(78, 11, 86, 20);
		panel.add(textFieldVorname);
		textFieldVorname.setColumns(10);

		lblVorname.setBounds(10, 14, 58, 14);
		panel.add(lblVorname);

		chckbxLeiter.setBounds(235, 34, 78, 23);
		panel.add(chckbxLeiter);

		lblNachname.setBounds(10, 38, 68, 14);
		panel.add(lblNachname);

		textFieldNachname.setBounds(78, 35, 102, 20);
		panel.add(textFieldNachname);
		textFieldNachname.setColumns(10);

		lblEintrittsjahr.setBounds(170, 14, 68, 14);
		panel.add(lblEintrittsjahr);

		spinnerEJahr.setModel(new SpinnerNumberModel(2017, 2000, currentyear, 1));
		spinnerEJahr.setBounds(245, 11, 68, 20);
		panel.add(spinnerEJahr);

		chckbxDienstag.setBounds(10, 59, 97, 23);
		panel.add(chckbxDienstag);

		chckbxSaHochzeit.setBounds(109, 59, 97, 23);
		panel.add(chckbxSaHochzeit);
		chckbxSaAbends.setFont(new Font("Tahoma", Font.PLAIN, 11));

		chckbxSaAbends.setBounds(109, 85, 93, 23);
		panel.add(chckbxSaAbends);

		chckbxSoMorgen.setBounds(10, 85, 97, 23);
		panel.add(chckbxSoMorgen);

		chckbxSoTaufe.setBounds(10, 111, 97, 23);
		panel.add(chckbxSoTaufe);

		chckbxSoAbend.setBounds(109, 109, 97, 23);
		panel.add(chckbxSoAbend);

		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abbrechen();
			}
		});
		btnAbbrechen.setBounds(31, 141, 113, 23);
		panel.add(btnAbbrechen);

		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speichern(wmf);
			}
		});
		btnSpeichern.setBounds(170, 141, 113, 23);
		panel.add(btnSpeichern);
		btnAnvertraute.setFont(new Font("Tahoma", Font.PLAIN, 11));

		btnAnvertraute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wmf.getGeschwister();
			}
		});
		btnAnvertraute.setBounds(212, 64, 101, 68);
		panel.add(btnAnvertraute);
		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[] { textFieldNachname, lblNachname, textFieldVorname, lblVorname, lblEintrittsjahr,
						spinnerEJahr, chckbxLeiter, chckbxDienstag, chckbxSaHochzeit, chckbxSoMorgen, chckbxSaAbends,
						chckbxSoTaufe, chckbxSoAbend, btnAnvertraute, btnAbbrechen, btnSpeichern }));
	}

	public void abbrechen() {
		moben = null;
		btnffneMedi.setEnabled(true);
		btnNeuerMedi.setEnabled(true);
		setzeleer();
		panel.setVisible(false);
		
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
			
		}
		else {
			new Erroropener("Du solltest den Messdiner erst einmal speichern, damit das Programm auch wirklich weiss, wie der Messdiener heisst!" , null);
			return false;
		}
		
	}

	/**
	 * oeffnet einen Messdiener
	 */
	public void oeffne(Util u) {
		String pfadmitDaoE = null;
		boolean abbruch = false;
		try {
			pfadmitDaoE = u.getMessdienerFile(savepath);
			File f = new File(pfadmitDaoE);
			savepath = f.getParent();
		} catch (NullPointerException e) {
			System.err.println("Vorgang wurde durch den Benutzer abgebrochen!");
			abbruch = true;
		}
		if (abbruch == false && pfadmitDaoE != null) {
			ReadFile rf = new ReadFile();
			Messdiener me = rf.getMessdiener(pfadmitDaoE);
			setzeMedi(me);
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
		Util u = wmf.getUtil();
		if (textFieldVorname.getText().equals("") || textFieldNachname.getText().equals("")) {
			new Erroropener("Unvollstaendige Eingabe!", null);
		} else {
			Messdiener me = getMeVonEingabe();
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

			me.makeXML(savepath);
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, "Erfolgreich gespeichert! Du kannst nun einen neuen Messdiener anlegen. Wenn du den alten bearbeiten willst musst du ihn erst  wieder oeffnen!", "Gespeichert!",
					JOptionPane.INFORMATION_MESSAGE);
			moben = me;
			textFieldVorname.setText("");
			spinnerEJahr.setValue(currentyear);
			chckbxLeiter.setSelected(false);
			//this.mobenWurdeGeoeffntet= true;
			//this.mobensavepath = savepath+ "//" + moben.makeId() + ".xml";
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
	public Messdiener getMeVonEingabe() {
		Messdiener me = new Messdiener();
		Messverhalten mv = new Messverhalten();
		mv.editiereBestimmteMesse(EnumdeafaultMesse.Di_abend, chckbxDienstag.isSelected());
		mv.editiereBestimmteMesse(EnumdeafaultMesse.Sam_abend, chckbxSaAbends.isSelected());
		mv.editiereBestimmteMesse(EnumdeafaultMesse.Sam_hochzeit, chckbxSaHochzeit.isSelected());
		mv.editiereBestimmteMesse(EnumdeafaultMesse.Son_abend, chckbxSoAbend.isSelected());
		mv.editiereBestimmteMesse(EnumdeafaultMesse.Son_morgen, chckbxSoMorgen.isSelected());
		mv.editiereBestimmteMesse(EnumdeafaultMesse.Son_taufe, chckbxSoTaufe.isSelected());
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
	 */
	private void setzeAlles(String vorname, String nachname, int Ejahr, boolean leiter, Messverhalten mv) {
		textFieldVorname.setText(vorname);
		textFieldNachname.setText(nachname);
		spinnerEJahr.setValue(Ejahr);
		chckbxLeiter.setSelected(leiter);
		chckbxDienstag.setSelected(mv.getBestimmtes(EnumdeafaultMesse.Di_abend));
		chckbxSaAbends.setSelected(mv.getBestimmtes(EnumdeafaultMesse.Sam_abend));
		chckbxSaHochzeit.setSelected(mv.getBestimmtes(EnumdeafaultMesse.Sam_hochzeit));
		chckbxSoAbend.setSelected(mv.getBestimmtes(EnumdeafaultMesse.Son_abend));
		chckbxSoMorgen.setSelected(mv.getBestimmtes(EnumdeafaultMesse.Son_morgen));
		chckbxSoTaufe.setSelected(mv.getBestimmtes(EnumdeafaultMesse.Son_taufe));
	}

	/**
	 * setzt die Benutzereingaben leer
	 */
	private void setzeleer() {
		setzeAlles("", "", 2017, false, new Messverhalten());

	}

	/**
	 * setzt die Eingaben fuer einen Messdiener ein
	 * 
	 * @param me
	 */
	public void setzeMedi(Messdiener me) {
		setzeAlles(me.getVorname(), me.getNachnname(), me.getEintritt(), me.isIstLeiter(), me.getDienverhalten());
		moben = me;
	}

	public Messdiener getMoben() {
		return moben;
	}

	public void setback(Messdiener me, WWAnvertrauteFrame wwAnvertrauteFrame) {
	Messdiener tmp = me;
	abbrechen();
	/*setzeMedi(me);
	moben = me;
	panel.setVisible(true);
	btnffneMedi.setEnabled(false);
	btnNeuerMedi.setEnabled(false);
	this.mobenWurdeGeoeffntet = true;*/
	}

}
