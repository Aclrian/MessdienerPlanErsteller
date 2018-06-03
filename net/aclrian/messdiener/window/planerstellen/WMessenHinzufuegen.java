package net.aclrian.messdiener.window.planerstellen;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.newy.progress.AData;
import net.aclrian.messdiener.newy.progress.AProgress;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.auswaehlen.WMediAbwaehlen;
import net.aclrian.messdiener.window.auswaehlen.WMediAuswaehlen;

public class WMessenHinzufuegen extends JFrame {

	public enum EnumBoolean {
		True(), False(), Null();
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 7009804784408988513L;

	private JPanel contentPane;
	private JScrollPane scrollPane = new JScrollPane();
	private JList<String> list = new JList<String>();
	private JLabel lblAlleMessen = new JLabel("Alle Messen");
	private JButton btnMesseHinzufgen = new JButton("<html><body>Messe<br>hinzuf"+References.ue+"gen</body></html>");
	private JButton btnMessenEntfernen = new JButton("<html><body>Messe<br>entfernen</body></html>");
	private JPanel panel = new JPanel();
	private Checkbox chbxHochamt = new Checkbox("Hochamt");
	private JLabel lblUhrzeit = new JLabel("Uhrzeit");
	private JSpinner spinnerDatum = new JSpinner();
	// private JSpinner spinnerDatum = new JSpinner();
	private JLabel lblDatum = new JLabel("Datum");
	private JSpinner spinnerAnzahlMedis = new JSpinner();
	private JLabel lblAnzahlDerMessdiener = new JLabel("Anzahl der Medis");
	private JButton btnSpeichern = new JButton("Speichern");
	private JButton btnAbbrechen = new JButton("Abbrechen");
	private JButton btnPlanErstellen = new JButton("<html><body>Plan<br>Erstellen</body></html>");
	private ArrayList<Messdiener> hauptarray;
	private ArrayList<Messe> messen = new ArrayList<Messe>();
	private DefaultListModel<String> listmodel = new DefaultListModel<>();
	private JSpinner spinnerKirche = new JSpinner();
	private JSpinner spinnerTyp = new JSpinner();
	private JDateChooser dc = new JDateChooser();
	//private SpinnerModel spinnerDatumModel = new SpinnerDateModel();
	private AProgress ap;
	private JButton btnAnzeigen = new JButton("<html><body>Messe<br>anzeigen/<br>bearbeiten</body></html>");
	private JButton btnSetzeManuell = new JButton("manuell einteilen");
	private ArrayList<Messdiener> ausgewaehlte;
	private EnumBoolean nurleiter;
	private String titel;
	int intValue = Integer.parseInt("8a9db1", 16);
	private DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>();
	private JComboBox<String> comboBox = new JComboBox<String>(dcbm);
	private JCheckBox chckbxNeusetzen = new JCheckBox("neu setzen");
	private Color c = new Color(intValue);
	private Messe bearbeiten;
	private int ibearbeiten;

	/**
	 * Create the frame.
	 */
	public WMessenHinzufuegen(Date anfang, Date ende, AProgress ap) {
		this.ap = ap;
		setMediarray(ap.getMediarraymitMessdaten());
		setIconImage(References.getIcon());
		setTitle("Hier koennen noch Messen hinzugef"+References.ue+"gt werden:");
		titel = "";
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(Utilities.setFrameMittig(773, 595));
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		comboBox.setBounds(202, 494, 244, 24);
		getContentPane().add(comboBox);
		// lblAlleMessen.set
		scrollPane.setBounds(12, 5, 434, 477);
		contentPane.add(scrollPane);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// JScrollPane scrollPane = new JScrollPane();
		// JList list = new JList();
		list.setModel(listmodel);
		scrollPane.setViewportView(list);
		lblAlleMessen.setHorizontalAlignment(SwingConstants.CENTER);

		scrollPane.setColumnHeaderView(lblAlleMessen);
		JScrollPane scrollPane = new JScrollPane();
		// JList list = new JList();
		JLabel lblAlleMessen = new JLabel("Alle Messen");
		lblAlleMessen.setHorizontalAlignment(SwingConstants.CENTER);
		lblAlleMessen.setFont(lblAlleMessen.getFont().deriveFont(12f));
		scrollPane.setColumnHeaderView(lblAlleMessen);
		btnMesseHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addMesse();
			}
		});

		btnMesseHinzufgen.setBounds(479, 328, 110, 55);
		contentPane.add(btnMesseHinzufgen);

		btnMessenEntfernen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeMesse();
			}
		});
		btnMessenEntfernen.setBounds(479, 395, 110, 55);
		contentPane.add(btnMessenEntfernen);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(192, 192, 192), new Color(192, 192, 192),
				new Color(192, 192, 192), new Color(192, 192, 192)));

		panel.setBounds(458, 5, 284, 272);
		contentPane.add(panel);
		panel.setLayout(null);

		chbxHochamt.setBounds(10, 70, 115, 20);
		panel.add(chbxHochamt);

		lblUhrzeit.setBounds(12, 41, 62, 23);
		panel.add(lblUhrzeit);

		spinnerDatum.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
		JSpinner.DateEditor de_spinnerDatum = new JSpinner.DateEditor(spinnerDatum, "HH:mm");
		spinnerDatum.setEditor(de_spinnerDatum);
		spinnerDatum.setValue(new Date());
		spinnerDatum.setBounds(80, 43, 77, 20);
		panel.add(spinnerDatum);

		// spinnerDatum.setModel(
		// new SpinnerDateModel(new Date(1512428400000L), new
		// Date(1512428400000L),
		// null, Calendar.DAY_OF_YEAR));
		dc.setBounds(80, 12, 111, 20);
		dc.setDate(anfang);
		panel.add(dc);

		lblDatum.setBounds(10, 14, 70, 15);
		panel.add(lblDatum);

		spinnerAnzahlMedis.setModel(new SpinnerNumberModel(6, (1), null, (1)));
		spinnerAnzahlMedis.setBounds(148, 113, 45, 20);
		panel.add(spinnerAnzahlMedis);

		lblAnzahlDerMessdiener.setBounds(10, 116, 124, 15);
		panel.add(lblAnzahlDerMessdiener);
		btnSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				speichern();
			}
		});

		btnSpeichern.setBounds(10, 234, 117, 25);
		panel.add(btnSpeichern);
		btnAbbrechen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				abbrechen();
			}
		});

		btnAbbrechen.setBounds(148, 234, 117, 25);
		panel.add(btnAbbrechen);

		spinnerKirche.setModel(new SpinnerListModel(ap.getAda().getPfarrei().getOrte()));
		spinnerKirche.setBounds(146, 172, 119, 20);
		panel.add(spinnerKirche);

		spinnerTyp.setModel(new SpinnerListModel(ap.getAda().getPfarrei().getTypen()));
		spinnerTyp.setBounds(146, 203, 119, 20);
		panel.add(spinnerTyp);

		JLabel lblOrt = new JLabel("Ort");
		lblOrt.setBounds(10, 179, 70, 15);
		panel.add(lblOrt);

		JLabel lblTypDerMesse = new JLabel("Typ der Messe");
		lblTypDerMesse.setBounds(10, 205, 115, 15);
		panel.add(lblTypDerMesse);

		JCheckBox chckbxNurleiter = new JCheckBox("Nur Leiter");
		chckbxNurleiter.setBounds(158, 141, 97, 23);
		panel.add(chckbxNurleiter);
		btnSetzeManuell.setFont(new Font("Dialog", Font.PLAIN, 11));

		btnSetzeManuell.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				manuellEinteilen(chckbxNurleiter.isSelected());
			}
		});
		btnSetzeManuell.setBounds(10, 139, 134, 25);
		panel.add(btnSetzeManuell);

		JButton btnSetzeMesstitel = new JButton("Messtitel");
		btnSetzeMesstitel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				messtitel();
			}
		});
		btnSetzeMesstitel.setToolTipText("bspw. Jahresabschlussmesse mit Sakramentalensegen");
		btnSetzeMesstitel.setBounds(131, 70, 134, 23);
		panel.add(btnSetzeMesstitel);
		btnPlanErstellen.setBackground(c);

		btnPlanErstellen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				erstellen(ap.getAda());
			}
		});

		btnPlanErstellen.setBounds(601, 395, 110, 55);
		contentPane.add(btnPlanErstellen);

		btnAnzeigen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				messeanzeigen();
			}
		});
		btnAnzeigen.setBounds(601, 328, 110, 55);
		contentPane.add(btnAnzeigen);

		JButton btnManuellAusteilen = new JButton("austeilen");
		btnManuellAusteilen.setBounds(336, 530, 110, 25);
		contentPane.add(btnManuellAusteilen);

		chckbxNeusetzen.setBounds(202, 531, 126, 23);
		contentPane.add(chckbxNeusetzen);
		btnManuellAusteilen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				augewaehlenfuerausteilen();
			}
		});
		dcbm.addElement("");
		hauptarray.sort(Messdiener.compForMedis);
		for (Messdiener me : hauptarray) {
			dcbm.addElement(me.makeId());
		}
		//
		panel.setVisible(false);
		chbxHochamt.setVisible(false);
		contentPane.setVisible(false);

		JPanel vorher = new JPanel();
		vorher.setBorder(new EmptyBorder(5, 5, 5, 5));
		if (ap != null) {
			setContentPane(vorher);
		}
		// setContentPane(vorher);
		JButton b = new JButton("Nun generienen.");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				b.setText("Bitte Warten...");
				generireDefaultMessen(anfang, ende, ap.getAda());
				vorher.setVisible(false);
				setContentPane(contentPane);
				contentPane.setVisible(true);
			}
		});
		vorher.setLayout(null);
		b.setBounds(10, 11, 249, 124);
		vorher.add(b);

		JLabel lblJeNachdemWie = new JLabel(
				"<html>\r\n<body>\r\nJe nachdem, wie gro"+References.ss+" der Absatand<br>\r\nzwischen den Daten ist, <br>\r\nk\u00F6nnte es eine Weile dauern!<br>\r\n</body>\r\n</html>");
		lblJeNachdemWie.setBounds(269, 11, 222, 124);
		vorher.add(lblJeNachdemWie);

		setVisible(true);
		// this.setEnabled(false);
		// generireDefaultMessen(anfang, ende);
		// this.setEnabled(true);*/
	}

	public void abbrechen() {
		ausgewaehlte = new ArrayList<Messdiener>();
		nurleiter = EnumBoolean.Null;
		nurleiter.toString();
		panel.setVisible(false);
		chbxHochamt.setVisible(false);
		titel = null;
		btnMesseHinzufgen.setEnabled(true);
		btnMessenEntfernen.setEnabled(true);
		btnAnzeigen.setEnabled(true);
	}

	public void addMesse() {
		btnMesseHinzufgen.setEnabled(false);
		btnMessenEntfernen.setEnabled(false);
		btnAnzeigen.setEnabled(false);
		panel.setVisible(true);
		chbxHochamt.setVisible(true);
	}

	public void augewaehlenfuerausteilen() {
		int i = comboBox.getSelectedIndex();
		boolean catchy = false;
		WMediAbwaehlen wmab = null;
		boolean neusetzen = chckbxNeusetzen.isSelected();
		try {
			wmab = new WMediAbwaehlen(hauptarray.get(i - 1), neusetzen, this);
		} catch (IndexOutOfBoundsException e) {
			catchy = true;
			new Erroropener("Bitte Messdiener ausw"+References.ae+"hlen!");
		}
		if (!catchy) {
			wmab.setVisible(true);
		}
	}

	/*private void erstellen(ActionEvent evt, AData ada) {
		setVisible(true);
		if (messen.size() < 1) {
			new Erroropener("Es sollte wenigstens eine Messe geben, inder Messdiener eingeteilt werden sollen!");
		} else {
			int i = hauptarray.size();
			Messdiener[] mm = new Messdiener[i];
			hauptarray.toArray(mm);
			ProgressBarDemo.createAndShowGUI(mm, messen, ada);
			// this.dispose();
		}
	}*/

	public void erstellen(AData ada) {
		if (!messen.isEmpty()) {
			Messdiener[] mm = new Messdiener[hauptarray.size()];
			hauptarray.toArray(mm);
			WMessenErstellen erst = new WMessenErstellen(mm, messen, ada);
			erst.setVisible(true);
		} else {
			new Erroropener("Es sollte wenigstens eine Messe existieren!");
		}
	}

	public void generireDefaultMessen(Date anfang, Date ende, AData ada) {
		Calendar start = Calendar.getInstance();
		start.setTime(anfang);
		SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
		for (Date date = start.getTime(); date.before(ende); start.add(Calendar.DATE, 1), date = start.getTime()) {
			String tag = wochendagformat.format(date);
			for (StandartMesse sm : ada.getPfarrei().getStandardMessen()) {
				if (!AData.sonstiges.isSonstiges(sm)) {
					// DEBUG: System.out.println(sm.getWochentag() + ":"
					// + tag + "--");
					// if (sm.getWochentag().equals(tag)) {
					Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), tag + ": " + sm.getWochentag() + tag.startsWith(sm.getWochentag()));
					if (tag.startsWith(sm.getWochentag())) {
						/*
						Calendar c = Calendar.getInstance();
						c.setTime(start.getTime());
						c.add(Calendar.HOUR_OF_DAY, sm.getBeginn_stunde());
						c.add(Calendar.MINUTE, Integer.parseInt(sm.getBeginn_minute()));*/
						Date d = start.getTime();
						SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
						SimpleDateFormat dfuhr = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
						try {
							Date frting = dfuhr.parse(df.format(d)+"-"+sm.getBeginn_stundealsString()+":"+sm.getBeginn_minute());
							Messe m = new Messe(frting, sm, ap.getAda());
							listmodel.addElement(m.getIDHTML());
							messen.add(m);
						} catch (ParseException e) {
				 			new Erroropener(e.getMessage()+ ": Konnte kein Datum erzeugen. Das sollte eigentlich nicht passieren.");
							e.printStackTrace();
						}
						// System.out.println(m.getIDHTML());
					}
				}
			}
		}
	}

	public void getAusgewaehlte(ArrayList<Messdiener> list) {
		try {
		for (Messdiener messdiener : list) {
			ausgewaehlte.add(messdiener);
		}
		} catch (NullPointerException e) {
 			new Erroropener(e.getMessage() + ": Liste: " + list.toString() + "ausgewaehlte: " + ausgewaehlte.toString());
		}
		this.setEnabled(true);
	}

	public ArrayList<Messdiener> getMediarray() {
		return hauptarray;
	}

	public void manuellEinteilen(boolean selected) {
		WMediAuswaehlen wma = new WMediAuswaehlen(hauptarray, selected, this);
		this.setEnabled(false);
		if (selected) {
			nurleiter = EnumBoolean.True;
		} else {
			nurleiter = EnumBoolean.False;
		}
		wma.setVisible(true);
	}

	public void messeanzeigen() {
		if (list.getSelectedIndex() != -1) {
			ibearbeiten = list.getSelectedIndex();
			bearbeiten = messen.get(ibearbeiten);
			setMesseinPanel(messen.get(list.getSelectedIndex()));
			panel.setVisible(true);
			chbxHochamt.setVisible(true);
			btnMesseHinzufgen.setEnabled(false);
			btnMessenEntfernen.setEnabled(false);
			btnAnzeigen.setEnabled(false);

		} else {
			new Erroropener("Du musst eine Messe ausw"+References.ae+"hlen!");
		}
		/*
		 * Messe m = messen.get(list.getSelectedIndex()); WMessenAnzeigen wma = new
		 * WMessenAnzeigen(m); wma.setVisible(true);
		 */
	}

	public void messtitel() {
		JFrame f = new JFrame();
		String s = JOptionPane.showInputDialog(f, "Bitte den Messtitel eingeben:", "Messe hinzuf"+References.ue+"gen",
				JOptionPane.QUESTION_MESSAGE);
		try {
			titel = s;
		} catch (NullPointerException e) {
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Vorgang wurde durch den Benutzer gestoppt.");
			titel = "";
		}
	}

	public void removeMesse() {
		int i = list.getSelectedIndex();
		if (i != -1) {
			listmodel.removeElementAt(i);
			messen.remove(i);
		}

	}

	public void rueckgeben(Messdiener m) {
		hauptarray.set(comboBox.getSelectedIndex() - 1, m);
	}

	private void setMediarray(ArrayList<Messdiener> hauptarray) {
		this.hauptarray = hauptarray;
	}

	private void setMesseinPanel(Messe messe) {
		ausgewaehlte = messe.getEingeteilte();
		spinnerDatum.setValue(messe.getDate());
		dc.setDate(messe.getDate());
		chbxHochamt.setState(messe.isHochamt());
		spinnerAnzahlMedis.setValue(messe.getAnz_messdiener());
		spinnerTyp.setValue(messe.getMesseTyp());
		spinnerKirche.setValue(messe.getKirche());
	}

	public void speichern() {
		Messe m = null;
		boolean hochamt = Boolean.getBoolean(String.valueOf(chbxHochamt.getState()));
		int anz_medis = Integer.parseInt(String.valueOf(spinnerAnzahlMedis.getValue()));
		String ort = String.valueOf(spinnerKirche.getValue());
		String typ = String.valueOf(spinnerTyp.getValue());

		SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat uhrzeeitdf = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dboth = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		Date datum = dc.getDate();
		String sdatum = ddf.format(datum);
		boolean cannotparse = false;
		Date uhrzeit = null;
		if (spinnerDatum.getModel() instanceof SpinnerDateModel) {
			uhrzeit = (Date) spinnerDatum.getModel().getValue();
		}
		Date datummituhrzeit = null;
		try {
			datummituhrzeit = dboth.parse(sdatum + "-" + uhrzeeitdf.format(uhrzeit));
		} catch (ParseException e) {
			cannotparse = true;
			new Erroropener("Das hat nicht gefunkt!");
			e.printStackTrace();
		}
		if (ibearbeiten > -1 && bearbeiten != null && cannotparse == false) {
			if (!titel.equals("")) {
				bearbeiten.setTitle(titel);
			}
			bearbeiten.bearbeiten(hochamt, anz_medis, datummituhrzeit, ort, typ, ap.getAda());
			m = bearbeiten;
		} else {
			if (cannotparse == false) {
				try {
					if (titel != null || !titel.equals("")) {
						m = new Messe(hochamt, anz_medis, datummituhrzeit, ort, typ, titel, ap.getAda());
					} else {
						m = new Messe(hochamt, anz_medis, datummituhrzeit, ort, typ, ap.getAda());
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
					
					m = new Messe(hochamt, anz_medis, datummituhrzeit, ort, typ, ap.getAda());
				}
			}
		}
		for (Messdiener messdiener : ausgewaehlte) {
			if (messdiener.isIstLeiter()) {
				m.LeiterEinteilen(messdiener);
			}
			m.vorzeitigEiteilen(messdiener, ap.getAda());
		}

		ausgewaehlte = new ArrayList<Messdiener>();
		nurleiter = EnumBoolean.Null;

		if (ibearbeiten > -1 && bearbeiten != null) {
			messen.remove(ibearbeiten);
			messen.add(ibearbeiten, m);
			ibearbeiten = -1;
			bearbeiten = null;
		} else {
			messen.add(m);
		}
		messen.sort(Messe.compForMessen);
		listmodel.removeAllElements();
		for (Messe messe : messen) {
			listmodel.addElement(messe.getIDHTML());
		}
		panel.setVisible(false);
		chbxHochamt.setVisible(false);
		btnMesseHinzufgen.setEnabled(true);
		btnMessenEntfernen.setEnabled(true);
		btnAnzeigen.setEnabled(true);

	}

	public void update(WMediAbwaehlen wMediAbwaehlen, ArrayList<Messdiener> rtn) {
		if (wMediAbwaehlen != null) {
			hauptarray = rtn;
		}
	}
}
