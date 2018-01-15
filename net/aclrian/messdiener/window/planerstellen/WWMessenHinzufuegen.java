package net.aclrian.messdiener.window.planerstellen;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.utils.EnumMesseTyp;
import net.aclrian.messdiener.utils.EnumOrt;
import net.aclrian.messdiener.utils.EnumdeafaultMesse;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Util;
import net.aclrian.messdiener.window.WMainFrame;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.activity.InvalidActivityException;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import java.awt.Checkbox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;
import javax.swing.JTextPane;
import java.awt.Font;

public class WWMessenHinzufuegen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7009804784408988513L;
	private JPanel contentPane;

	private JScrollPane scrollPane = new JScrollPane();
	private JList<String> list = new JList<String>();
	private JLabel lblAlleMessen = new JLabel("Alle Messen");
	private JButton btnMesseHinzufgen = new JButton("<html><body>Messen<br>hinzufügen</body></html>");
	private JButton btnMessenEntfernen = new JButton("<html><body>Messen<br>entfernen</body></html>");
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
	private JButton btnPlanErstellen = new JButton("Plan Erstellen");
	private ArrayList<Messdiener> hauptarray;
	private ArrayList<Messe> messen = new ArrayList<Messe>();
	private DefaultListModel<String> listmodel = new DefaultListModel<>();
	private JSpinner spinnerKirche = new JSpinner();
	private JSpinner spinnerTyp = new JSpinner();
	private JDateChooser dc = new JDateChooser();
	private SpinnerModel spinnerDatumModel = new SpinnerDateModel();
	private WMainFrame mainframe;
	private JButton btnAnzeigen = new JButton("Messe anzeigen");
	private JButton btnSetzeManuell = new JButton("Setze Medis manuell");
	private ArrayList<Messdiener> ausgewaehlte;
	private EnumBoolean nurleiter;
	private String titel;

	private enum EnumBoolean {
		True("true"), False("false"), Null("null");

		private String type;

		private EnumBoolean() {
		}

		private EnumBoolean(String type) {
			this.type = type;
		}
	}

	/**
	 * Create the frame.
	 */
	public WWMessenHinzufuegen(Date anfang, Date ende, WMainFrame wmf) {
		this.mainframe = wmf;
		this.setMediarray(wmf.getMediarraymitMessdaten());
		titel = "";
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 773, 595);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		// lblAlleMessen.set
		scrollPane.setBounds(5, 5, 441, 477);
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
			public void actionPerformed(ActionEvent arg0) {
				addMesse();
			}
		});

		btnMesseHinzufgen.setBounds(467, 0, 117, 55);
		contentPane.add(btnMesseHinzufgen);

		btnMessenEntfernen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeMesse();
			}
		});
		btnMessenEntfernen.setBounds(642, 0, 117, 55);
		contentPane.add(btnMessenEntfernen);

		panel.setBounds(458, 67, 292, 273);
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

		spinnerAnzahlMedis.setModel(new SpinnerNumberModel(new Integer(6), new Integer(1), null, new Integer(1)));
		spinnerAnzahlMedis.setBounds(148, 113, 45, 20);
		panel.add(spinnerAnzahlMedis);

		lblAnzahlDerMessdiener.setBounds(10, 116, 124, 15);
		panel.add(lblAnzahlDerMessdiener);
		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speichern();
			}
		});

		btnSpeichern.setBounds(10, 237, 117, 25);
		panel.add(btnSpeichern);
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				abbrechen();
			}
		});

		btnAbbrechen.setBounds(148, 237, 117, 25);
		panel.add(btnAbbrechen);

		spinnerKirche.setModel(new SpinnerListModel(EnumOrt.values()));
		spinnerKirche.setBounds(146, 175, 119, 20);
		panel.add(spinnerKirche);

		spinnerTyp.setModel(new SpinnerListModel(EnumMesseTyp.values()));
		spinnerTyp.setBounds(146, 206, 119, 20);
		panel.add(spinnerTyp);

		JLabel lblOrt = new JLabel("Ort");
		lblOrt.setBounds(10, 182, 70, 15);
		panel.add(lblOrt);

		JLabel lblTypDerMesse = new JLabel("Typ der Messe");
		lblTypDerMesse.setBounds(10, 208, 115, 15);
		panel.add(lblTypDerMesse);

		JCheckBox chckbxNurleiter = new JCheckBox("Nur Leiter");
		chckbxNurleiter.setBounds(168, 141, 97, 23);
		panel.add(chckbxNurleiter);
		btnSetzeManuell.setFont(new Font("Dialog", Font.BOLD, 10));

		btnSetzeManuell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				manuellEinteilen(chckbxNurleiter.isSelected());
			}
		});
		btnSetzeManuell.setBounds(10, 139, 147, 23);
		panel.add(btnSetzeManuell);

		JButton btnSetzeMesstitel = new JButton("Setze Messtitel");
		btnSetzeMesstitel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				messtitel();
			}
		});
		btnSetzeMesstitel.setToolTipText("bspw. Jahresabschlussmesse mit Sakramentalensegen");
		btnSetzeMesstitel.setBounds(131, 70, 124, 23);
		panel.add(btnSetzeMesstitel);

		btnPlanErstellen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				erstellen(wmf);
			}
		});

		btnPlanErstellen.setBounds(537, 351, 149, 96);
		contentPane.add(btnPlanErstellen);

		btnAnzeigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				messeanzeigen();
			}
		});
		btnAnzeigen.setBounds(140, 501, 196, 36);
		contentPane.add(btnAnzeigen);
		//
		panel.setVisible(false);
		contentPane.setVisible(false);

		JPanel vorher = new JPanel();
		vorher.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(vorher);
		JButton b = new JButton("Nun generienen.");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				b.setText("Bitte Warten...");
				generireDefaultMessen(anfang, ende);
				vorher.setVisible(false);
				setContentPane(contentPane);
				contentPane.setVisible(true);
			}
		});
		vorher.setLayout(null);
		b.setBounds(10, 11, 249, 124);
		vorher.add(b);

		JLabel lblJeNachdemWie = new JLabel(
				"<html>\r\n<body>\r\nJe nachdem, wie gro\u00DF der Absatand<br>\r\nzwischen den Daten ist, <br>\r\nk\u00F6nnte es eine Weile dauern!<br>\r\n</body>\r\n</html>");
		lblJeNachdemWie.setBounds(269, 11, 222, 124);
		vorher.add(lblJeNachdemWie);

		this.setVisible(true);
		// this.setEnabled(false);
		// generireDefaultMessen(anfang, ende);
		// this.setEnabled(true);
	}

	public void messtitel() {
		JFrame f = new JFrame();
		String s = JOptionPane.showInputDialog(f, "Bitte den Messtitel eingeben:", "Messe hinzufuegen",
				JOptionPane.QUESTION_MESSAGE);
		try {
			titel = s;
		} catch (NullPointerException e) {
			System.err.println("Vorgang wurde durch den Benutzer gestoppt.");
			titel = "";
		}
	}

	public void erstellen(WMainFrame wmf) {
		Messdiener[] mm = new Messdiener[hauptarray.size()];
		hauptarray.toArray(mm);
		WWMessenErstellen erst = new WWMessenErstellen(mm, messen, wmf);
		erst.setVisible(true);
	}

	public void manuellEinteilen(boolean selected) {
		WMediAuswaehlen wma = new WMediAuswaehlen(hauptarray, selected, this);
		btnAbbrechen.setEnabled(false);
		btnAnzeigen.setEnabled(false);
		btnMesseHinzufgen.setEnabled(false);
		btnMessenEntfernen.setEnabled(false);
		btnPlanErstellen.setEnabled(false);
		btnSetzeManuell.setEnabled(false);
		btnSpeichern.setEnabled(false);
		if (selected) {
			nurleiter = EnumBoolean.True;
		} else {
			nurleiter = EnumBoolean.False;
		}
		wma.setVisible(true);

	}

	public void getAusgewaehlte(ArrayList<Messdiener> list) {
		btnAbbrechen.setEnabled(true);
		btnAnzeigen.setEnabled(true);
		btnMesseHinzufgen.setEnabled(true);
		btnMessenEntfernen.setEnabled(true);
		btnPlanErstellen.setEnabled(true);
		btnSetzeManuell.setEnabled(true);
		btnSpeichern.setEnabled(true);
		ausgewaehlte = list;
	}

	public void erstellen(ActionEvent evt, WMainFrame wmf) {
		this.setVisible(true);
		if (messen.size() < 1) {
			new Erroropener("Es sollte wenigstens eine Messe geben, inder Messdiener eingeteilt werden sollen!", null);
		} else {
			int i = hauptarray.size();
			Messdiener[] mm = new Messdiener[i];
			hauptarray.toArray(mm);
			int year = Calendar.getInstance().get(Calendar.YEAR);
			/*
			 * try { for (Messdiener messdiener : mm) {
			 * messdiener.setnewMessdatenDaten(wmf.getUtil().getSavepath(), year, wmf); //
			 * System.out.println(messdiener.getMessdatenDaten().getAnz_messen() ); } }
			 * catch (Exception e) { e.printStackTrace(); }
			 */

			ProgressBarDemo.createAndShowGUI(mm, messen, wmf);
			// this.dispose();
		}
	}

	public void messeanzeigen() {
		if (list.getSelectedIndex() != -1) {
			setMesseinPanel(messen.get(list.getSelectedIndex()));
			panel.setVisible(true);
		} else {
			new Erroropener("Du musst eine Messe auswählen!", null);
		}
		/*
		 * Messe m = messen.get(list.getSelectedIndex()); WMessenAnzeigen wma = new
		 * WMessenAnzeigen(m); wma.setVisible(true);
		 */
	}

	private void setMesseinPanel(Messe messe) {
		spinnerDatum.setValue(messe.getDate());
		dc.setDate(messe.getDate());
		chbxHochamt.setState(messe.isHochamt());
		spinnerAnzahlMedis.setValue(messe.getAnz_messdiener());
		spinnerTyp.setValue(messe.getMesseTyp());
		spinnerKirche.setValue(messe.getKirche());
	}

	public void speichern() {
		boolean standart = false;
		try {
			ausgewaehlte.get(0).getEintritt();
		} catch (NullPointerException e) {
			standart = true;
		}

		SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat uhrzeeitdf = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dboth = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		Date datum = dc.getDate();
		String sdatum = ddf.format(datum);
		boolean cannotparse = false;
		Date uhrzeit = null;
		// try {

		spinnerDatumModel.getValue();
		if (spinnerDatum.getModel() instanceof SpinnerDateModel) {
			uhrzeit = (Date) spinnerDatum.getModel().getValue();
		}
		// uhrzeit = (Date) spinnerDatumModel.getValue();
		// uhrzeit = new SimpleDateFormat("EEE MMM dd HH:mm:ss zz
		// yyyy").parse(String.valueOf(spinnerDatum.getValue()));
		/*
		 * } catch (ParseException e1) { new Erroropener("FUNK!!!");
		 * e1.printStackTrace(); cannotparse = true; }
		 */

		Date datummituhrzeit = null;
		try {
			datummituhrzeit = dboth.parse(sdatum + "-" + uhrzeeitdf.format(uhrzeit));
		} catch (ParseException e) {
			cannotparse = true;
			new Erroropener("Das hat nicht gefunkt!");
			e.printStackTrace();
		}
		if (cannotparse == false) {
			boolean hochamt = Boolean.getBoolean(String.valueOf(chbxHochamt.getState()));
			int anz_medis = Integer.parseInt(String.valueOf(spinnerAnzahlMedis.getValue()));
			// int anz_leiter =
			// Integer.parseInt(String.valueOf(spinnerAnzahlLeiter.getValue()));
			EnumOrt ort = EnumOrt.valueOf(String.valueOf(spinnerKirche.getValue()));
			EnumMesseTyp typ = EnumMesseTyp.valueOf((String.valueOf(spinnerTyp.getValue())));
			Messe m;
			try {
				if (!titel.equals("")) {
					m = new Messe(hochamt, anz_medis, datummituhrzeit, ort, typ, titel, mainframe);
				} else {
					m = new Messe(hochamt, anz_medis, datummituhrzeit, ort, typ, mainframe);
				}
			} catch (NullPointerException e) {
				m = new Messe(hochamt, anz_medis, datummituhrzeit, ort, typ, mainframe);
			}

			if (!standart) {
				switch (nurleiter) {
				case False:
					for (Messdiener messdiener : ausgewaehlte) {
						m.vorzeitigEiteilen(messdiener, datummituhrzeit);
					}
					break;
				case True:
					for (Messdiener leiter : ausgewaehlte) {
						m.LeiterEinteilen(leiter);
					}
					break;
				default:
					break;
				}

				ausgewaehlte = null;
				nurleiter = EnumBoolean.Null;

			}
			messen.add(m);
			messen.sort(mainframe.getUtil().compForMessen);
			listmodel.removeAllElements();
			for (Messe messe : messen) {
				listmodel.addElement(messe.getIDHTML());
			}
			panel.setVisible(false);
		}

	}

	public void abbrechen() {
		ausgewaehlte = null;
		nurleiter = EnumBoolean.Null;
		panel.setVisible(false);
		titel = null;
	}

	public void removeMesse() {
		int i = list.getSelectedIndex();
		if (i != -1) {
			System.out.println(i);
			listmodel.removeElementAt(i);
			messen.remove(i);
		}

	}

	public void addMesse() {
		panel.setVisible(true);
	}

	public void generireDefaultMessen(Date anfang, Date ende) {
		Calendar start = Calendar.getInstance();
		start.setTime(anfang);
		SimpleDateFormat wochendagformat = new SimpleDateFormat("EEE");
		for (Date date = start.getTime(); date.before(ende); start.add(Calendar.DATE, 1), date = start.getTime()) {
			String tag = wochendagformat.format(date);
			for (EnumdeafaultMesse enummesse : EnumdeafaultMesse.values()) {
				if (enummesse != EnumdeafaultMesse.Sonstiges) {
					// DEBUG: System.out.println(enummesse.getWochentag() + ":"
					// + tag + "--");
					if (enummesse.getWochentag().equals(tag)) {

						Messe m = new Messe(enummesse, date, mainframe);
						listmodel.addElement(m.getIDHTML());
						messen.add(m);
						// System.out.println(m.getIDHTML());
					}
				}
			}
		}
	}

	public ArrayList<Messdiener> getMediarray() {
		return hauptarray;
	}

	private void setMediarray(ArrayList<Messdiener> hauptarray) {
		this.hauptarray = hauptarray;
	}

	public static void main(String[] args) {
		WMainFrame frame = new WMainFrame();
		frame.setVisible(false);
		frame.setUtil(new Util(// "E:\\.aaa_aproo\\zzzTESTgit\\_3GIT_\\XML"));
				"/mnt/DRIVE-N-GO/.aaa_aproo/zzzTESTgit/_3GIT_/XML"));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date anfang = null;
		Date ende = null;
		try {
			ende = df.parse("2017-12-31");
			anfang = df.parse("2017-12-31");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("DUM!!!");
		}
		frame.berechnen(anfang, ende);

	}
}
