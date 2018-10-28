package net.aclrian.mpe.pfarrei;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.aclrian.mpe.components.ATable;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.pfarrei.Setting.Attribut;
import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.AData;
import net.aclrian.mpe.start.AProgress;
import net.aclrian.mpe.start.WEinFrame;
import net.aclrian.mpe.utils.Erroropener;
import net.aclrian.mpe.utils.Utilities;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import net.aclrian.mpe.components.ARadioButton;

public class WriteFile_Pfarrei extends JFrame {

	private ArrayList<String> orte = new ArrayList<String>();
	private ArrayList<String> typen = new ArrayList<String>();
	private JScrollPane scrollPane;
	private JList<String> ortelist;
	private JScrollPane scrollPane_1;
	private JList<String> typenlist;
	private JButton orterm;
	private JButton typenrm;
	private JButton typenadd;
	private JButton orteadd;
	private ATable stdmessen_table;
	private JButton stdmessen_btnSpeichern;
	private JButton stdmessen_btnBearbeiten;
	private JSpinner stdmessen_comboBoxTyp;
	private SpinnerListModel modelTyp;
	private JSpinner stdmessen_comboBoxOrt;
	private SpinnerListModel modelOrt;
	private JLabel stdmessen_lblOrt;
	private JLabel stdmessen_lblTyp;
	private JSpinner stdmessen_spinnerstd;
	private JSpinner stdmessen_comboBoxTag;
	private SpinnerListModel modelTag;
	private JSpinner stdmessen_spinnermin;
	private JScrollPane stdmessen_scrollPane;
	private DefaultListModel<String> dlmtype = new DefaultListModel<String>();
	private DefaultListModel<String> dlmorte = new DefaultListModel<String>();
	private JPanel stdmessen_panel = new JPanel();
	private JPanel ortetypenpanel = new JPanel();

	private static final long serialVersionUID = 1956424918258056103L;
	private JTextField ortefeld;
	private JTextField typenfeld;
	private JLabel stdmessen_spinnerstdlblAnz;
	private JSpinner stdmessen_spinneranz;
	private JButton btnZurck;
	private JButton btnWeiter;
	private JButton btnSpeichern;
	private JPanel panel;
	private JPanel panel_1;
	private ArrayList<StandartMesse> sm = new ArrayList<StandartMesse>();
	public static final String[] columnNames = { "Tag", "Ort", "Stunde", "Minute", "Typ", "Anzahl" };
	public static final String[] columnNames2 = { "Einf" + References.ue + "hrungsjahr des Messdieners", "Anzahl" };
	private DefaultTableModel dmmm = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	private DefaultTableModel dmmm2 = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			if (column != 0)
				return true;
			else
				return false;
		};
	};
	private JTable table;
	private JTextField textField;
	private JScrollPane scrollPane_2;
	private JSpinner spinner_max_leiter;
	private JSpinner spinner_max_andere;
	private Einstellungen seting;
	private JPanel einstellungenpanel;
	private ARadioButton rdbtnf3Ja;
	private ARadioButton rdbtnf3Nein;
	private ARadioButton rdbtnf2Nein;
	private ARadioButton rdbtnf2Ja;
	private ARadioButton rdbtnf1Ja;
	private ARadioButton rdbtnf1Nein;
	private JPanel fragen;
	private ButtonGroup bg1 = new ButtonGroup();
	private ButtonGroup bg12 = new ButtonGroup();
	private ButtonGroup bg2 = new ButtonGroup();
	private ButtonGroup bg3 = new ButtonGroup();
	private JLabel lblLeitern;
	private JPanel frage12;
	private JLabel lblf12;
	private ARadioButton rdbtnf12Ja;
	private ARadioButton rdbtnf12Nein;
	private JPanel frage3;
	private JLabel lblAnderen;
	private JLabel lblStandardAnzahlPro;
	private SpinnerModel model_andere = new SpinnerNumberModel(3, 1, 20, 1);
	boolean alterunabhaengig = false;
	private boolean leitergleichandere = false;
	private SpinnerModel model_leiter = new SpinnerNumberModel(0, 0, 20, 1);

	/**
	 * @wbp.parser.constructor
	 */
	public WriteFile_Pfarrei(AProgress ap) {
		start(ap);
	}

	public void start(AProgress ap) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Erste Schritte: Einstellungen erstellen");
		setIconImage(References.getIcon());
		setBounds(0, 0, 615, 365);
		setBounds(Utilities.setFrameMittig(623, 375));
		getContentPane().setLayout(null);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentMoved(ComponentEvent e) {
				update();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				update();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				update();
			}
		});
		seting = new Einstellungen();

		btnSpeichern = new JButton("Speichern");
		btnSpeichern.addActionListener(e -> pfarreispeichern(ap));
		btnSpeichern.setBounds(476, 302, 117, 25);
		getContentPane().add(btnSpeichern);

		btnZurck = new JButton("Zur" + References.ue + "ck");
		btnZurck.addActionListener(e -> zurueck());
		btnZurck.setBounds(217, 302, 117, 25);
		getContentPane().add(btnZurck);

		btnWeiter = new JButton("Weiter");
		btnWeiter.addActionListener(e -> weiter());
		btnWeiter.setBounds(347, 302, 117, 25);
		getContentPane().add(btnWeiter);

		stdmessen_panel.setVisible(false);
		stdmessen_panel.setBorder(
				new TitledBorder(WEinFrame.b, "Standartmessen", TitledBorder.LEADING, TitledBorder.TOP, null, WEinFrame.neuhell1));
		stdmessen_panel.setBounds(12, 12, 581, 278);
		getContentPane().add(stdmessen_panel);
		stdmessen_panel.setLayout(null);

		stdmessen_scrollPane = new JScrollPane();
		stdmessen_scrollPane.setBounds(12, 136, 557, 99);
		stdmessen_panel.add(stdmessen_scrollPane);

		stdmessen_table = new ATable(dmmm);
		stdmessen_scrollPane.setViewportView(stdmessen_table);
		stdmessen_table.setRequestFocusEnabled(false);
		stdmessen_table.setFocusable(false);

		stdmessen_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		stdmessen_table.setModel(dmmm);

		stdmessen_btnSpeichern = new JButton("Speichern");
		stdmessen_btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stdmessen_speichern();
			}
		});
		stdmessen_btnSpeichern.setBounds(448, 101, 121, 25);
		stdmessen_panel.add(stdmessen_btnSpeichern);

		panel_1 = new JPanel();
		panel_1.setBounds(12, 101, 418, 25);
		stdmessen_panel.add(panel_1);
		panel_1.setLayout(null);

		JLabel stdmessen_lblEintragNummer = new JLabel("Ausgew" + References.ae + "hlten Eintrag");
		stdmessen_lblEintragNummer.setBounds(12, 0, 160, 25);
		panel_1.add(stdmessen_lblEintragNummer);

		stdmessen_btnBearbeiten = new JButton("bearbeiten");
		stdmessen_btnBearbeiten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int i = (int) stdmessen_table.getSelectedRow();
					bearbeiten(i);
				} catch (Exception e2) {
					e2.printStackTrace();
					new Erroropener(new Exception("Treffe zun" + References.ae + "chst eine Auswahl"));
				}
			}
		});
		stdmessen_btnBearbeiten.setBounds(178, 0, 117, 25);
		panel_1.add(stdmessen_btnBearbeiten);

		JButton btnLschen = new JButton("l" + References.oe + "schen");
		btnLschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int i = stdmessen_table.getSelectedRow();
					loeschen(i);
				} catch (Exception e2) {
					new Erroropener(new Exception("Treffe zun" + References.ae + "chst eine Auswahl"));
				}
			}
		});
		btnLschen.setBounds(311, 0, 105, 25);
		panel_1.add(btnLschen);

		JLabel label_2 = new JLabel("/");
		label_2.setFont(new Font("Dialog", Font.BOLD, 14));
		label_2.setBounds(299, 0, 14, 25);
		panel_1.add(label_2);

		modelTyp = new SpinnerListModel();
		stdmessen_comboBoxTyp = new JSpinner(modelTyp);
		((DefaultEditor) stdmessen_comboBoxTyp.getEditor()).getTextField().setEditable(false);
		stdmessen_comboBoxTyp.setBounds(467, 17, 102, 25);
		stdmessen_panel.add(stdmessen_comboBoxTyp);

		modelOrt = new SpinnerListModel();
		stdmessen_comboBoxOrt = new JSpinner(modelOrt);
		((DefaultEditor) stdmessen_comboBoxOrt.getEditor()).getTextField().setEditable(false);
		stdmessen_comboBoxOrt.setBounds(292, 17, 114, 25);
		stdmessen_panel.add(stdmessen_comboBoxOrt);

		stdmessen_lblTyp = new JLabel("Typ:");
		stdmessen_lblTyp.setBounds(418, 17, 37, 25);
		stdmessen_panel.add(stdmessen_lblTyp);

		stdmessen_lblOrt = new JLabel("Ort:");
		stdmessen_lblOrt.setBounds(243, 17, 37, 25);
		stdmessen_panel.add(stdmessen_lblOrt);

		modelTag = new SpinnerListModel(new String[] { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" });
		stdmessen_comboBoxTag = new JSpinner(modelTag);
		((DefaultEditor) stdmessen_comboBoxTag.getEditor()).getTextField().setEditable(false);
		stdmessen_comboBoxTag.setBounds(117, 17, 114, 25);
		stdmessen_panel.add(stdmessen_comboBoxTag);
		//stdmessen_comboBoxTag.setModel(new DefaultComboBoxModel<String>());

		panel = new JPanel();
		panel.setBounds(100, 59, 114, 20);
		stdmessen_panel.add(panel);
		panel.setLayout(null);

		stdmessen_spinnermin = new JSpinner();
		stdmessen_spinnermin.setBounds(69, 0, 45, 20);
		panel.add(stdmessen_spinnermin);
		stdmessen_spinnermin.setModel(new SpinnerNumberModel(0, 0, 59, 1));

		JLabel label = new JLabel(":");
		label.setFont(new Font("Dialog", Font.BOLD, 14));
		label.setBounds(55, 0, 16, 20);
		panel.add(label);

		stdmessen_spinnerstd = new JSpinner();
		stdmessen_spinnerstd.setBounds(0, 0, 45, 20);
		panel.add(stdmessen_spinnerstd);
		stdmessen_spinnerstd.setModel(new SpinnerNumberModel(12, 0, 24, 1));

		JLabel stdmessen_lblBeginn = new JLabel("Beginn:");
		stdmessen_lblBeginn.setBounds(15, 59, 70, 20);
		stdmessen_panel.add(stdmessen_lblBeginn);

		JLabel stdmessen_lblWochentag = new JLabel("Wochentag:");
		stdmessen_lblWochentag.setBounds(12, 17, 93, 25);
		stdmessen_panel.add(stdmessen_lblWochentag);

		stdmessen_spinnerstdlblAnz = new JLabel("Anzahl der ben" + References.oe + "tigten Messdiener");
		stdmessen_spinnerstdlblAnz.setBounds(229, 59, 261, 20);
		stdmessen_panel.add(stdmessen_spinnerstdlblAnz);

		stdmessen_spinneranz = new JSpinner();
		stdmessen_spinneranz.setModel(new SpinnerNumberModel(6, 1, 20, 1));
		stdmessen_spinneranz.setBounds(505, 59, 45, 20);
		stdmessen_panel.add(stdmessen_spinneranz);

		JButton btnUpdate = new JButton("Tabelle aktualisieren (bei Fehlern)");
		btnUpdate.addActionListener(e -> update());
		btnUpdate.setBounds(12, 247, 219, 25);
		stdmessen_panel.add(btnUpdate);
		stdmessen_panel.setVisible(false);

		fragen = new JPanel();
		fragen.setBorder(new TitledBorder(WEinFrame.b, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		fragen.setBounds(12, 12, 581, 278);
		getContentPane().add(fragen);
		fragen.setLayout(null);
		fragen.setVisible(false);
		getContentPane().add(fragen);

		JPanel frageeins = new JPanel();
		frageeins.setBounds(10, 2, 561, 67);
		fragen.add(frageeins);
		frageeins.setLayout(null);

		JLabel lblf1 = new JLabel(
				"Soll die Anzahl, wie viele Messen ein Messdiener im Monat dient, abh\u00E4ngig vom Alter sein?");
		lblf1.setBounds(20, 11, 517, 24);
		frageeins.add(lblf1);

		rdbtnf1Ja = new ARadioButton("Ja");
		rdbtnf1Ja.setBounds(20, 42, 109, 23);
		frageeins.add(rdbtnf1Ja);

		rdbtnf1Nein = new ARadioButton("Nein");
		rdbtnf1Nein.setBounds(292, 42, 109, 23);
		frageeins.add(rdbtnf1Nein);

		JPanel fragezwei = new JPanel();
		fragezwei.setBounds(10, 140, 561, 67);
		fragen.add(fragezwei);
		fragezwei.setLayout(null);

		JLabel lblF2 = new JLabel("Sollen Leiter genauso oft eingeteilt werden wie normale Messdiener?");
		lblF2.setBounds(22, 11, 517, 24);
		fragezwei.add(lblF2);

		rdbtnf2Ja = new ARadioButton("Ja");
		rdbtnf2Ja.setBounds(22, 42, 109, 23);
		fragezwei.add(rdbtnf2Ja);

		rdbtnf2Nein = new ARadioButton("Nein");
		rdbtnf2Nein.setBounds(294, 42, 109, 23);
		rdbtnf2Nein.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				AbstractButton aButton = (AbstractButton) e.getSource();
				ButtonModel aModel = aButton.getModel();
				boolean selected = aModel.isSelected();
				frage3.setVisible(selected);
			}
		});
		fragezwei.add(rdbtnf2Nein);

		frage3 = new JPanel();
		frage3.setLayout(null);
		frage3.setBounds(10, 209, 561, 67);
		frage3.setVisible(false);
		fragen.add(frage3);

		JLabel lblf3 = new JLabel("Sollen Leiter " + References.ue + "berhaupt automatisch eingeteilt werden?");
		lblf3.setBounds(22, 11, 517, 24);
		frage3.add(lblf3);

		rdbtnf3Ja = new ARadioButton("Ja");
		rdbtnf3Ja.setBounds(22, 42, 109, 23);
		frage3.add(rdbtnf3Ja);

		rdbtnf3Nein = new ARadioButton("Nein");
		rdbtnf3Nein.setBounds(294, 42, 109, 23);
		frage3.add(rdbtnf3Nein);

		bg1.add(rdbtnf1Ja);
		bg1.add(rdbtnf1Nein);

		bg2.add(rdbtnf2Ja);
		bg2.add(rdbtnf2Nein);

		bg3.add(rdbtnf3Ja);
		bg3.add(rdbtnf3Nein);

		frage12 = new JPanel();
		frage12.setLayout(null);
		frage12.setBounds(10, 71, 561, 67);
		fragen.add(frage12);

		lblf12 = new JLabel("Sollen Hoch" + References.ae + "mter diese Anzahl erh" + References.oe + "hen?");
		lblf12.setBounds(22, 11, 517, 24);
		frage12.add(lblf12);

		rdbtnf12Ja = new ARadioButton("Ja");
		rdbtnf12Ja.setBounds(22, 42, 109, 23);
		frage12.add(rdbtnf12Ja);
		bg12.add(rdbtnf12Ja);

		rdbtnf12Nein = new ARadioButton("Nein");
		rdbtnf12Nein.setBounds(294, 42, 109, 23);
		frage12.add(rdbtnf12Nein);
		bg12.add(rdbtnf12Nein);

		einstellungenpanel = new JPanel();
		einstellungenpanel.setBorder(new TitledBorder(WEinFrame.b, "Einstellungen",
				TitledBorder.LEADING, TitledBorder.TOP, null, WEinFrame.neuhell1));
		einstellungenpanel.setBounds(12, 12, 581, 278);
		getContentPane().add(einstellungenpanel);
		einstellungenpanel.setLayout(null);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(304, 37, 265, 229);
		einstellungenpanel.add(scrollPane_2);

		table = new ATable(dmmm2);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.doLayout();
		scrollPane_2.setViewportView(table);
		table.setRequestFocusEnabled(false);
		table.setFocusable(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(286, 15, 8, 242);
		einstellungenpanel.add(separator);

		JLabel lblMaximaleAnzahlVon = new JLabel("<html><body>Maximale Anzahl<sup>1</sup> von:</body></html>");
		lblMaximaleAnzahlVon.setBounds(22, 120, 138, 20);
		einstellungenpanel.add(lblMaximaleAnzahlVon);

		lblLeitern = new JLabel("Leitern:");
		lblLeitern.setBounds(150, 98, 70, 15);
		einstellungenpanel.add(lblLeitern);

		lblAnderen = new JLabel("anderen:");
		lblAnderen.setBounds(150, 144, 70, 15);
		einstellungenpanel.add(lblAnderen);

		spinner_max_leiter = new JSpinner();
		spinner_max_leiter.setModel(model_leiter);
		spinner_max_leiter.setBounds(224, 96, 45, 20);
		einstellungenpanel.add(spinner_max_leiter);

		spinner_max_andere = new JSpinner();
		spinner_max_andere.setModel(model_andere);
		spinner_max_andere.setBounds(224, 142, 45, 20);
		einstellungenpanel.add(spinner_max_andere);

		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(22, 35, 236, 20);
		einstellungenpanel.add(textField);

		JLabel label_1 = new JLabel("Name der Messdienergemeinschaft:");
		label_1.setBounds(10, 15, 261, 20);
		einstellungenpanel.add(label_1);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(8, 77, 280, 7);
		einstellungenpanel.add(separator_1);

		lblStandardAnzahlPro = new JLabel("<html><body>Standard Anzahl<sup>1</sup> pro Jahrgang</body></html>");
		lblStandardAnzahlPro.setBounds(345, 12, 224, 20);
		einstellungenpanel.add(lblStandardAnzahlPro);

		JLabel lblmitanzahlIst = new JLabel(
				"<html><body><sup>1</sup>Mit <i>Anzahl</i> ist die Anzahl gemeint, <br>die ein Messdiener seines Jahrgangs bzw. als Leiter standartm\u00E4\u00DFig oder maximal in einem Monat eingeteilt wird.</body></html>");
		lblmitanzahlIst.setBounds(36, 192, 233, 74);
		einstellungenpanel.add(lblmitanzahlIst);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(8, 173, 280, 7);
		einstellungenpanel.add(separator_2);
		TableColumnModel columnModel = table.getColumnModel();
		einstellungenpanel.setVisible(false);

		ortetypenpanel.setBorder(new TitledBorder(WEinFrame.b,
				"Orte und Messetypen hizuf" + References.ue + "gen", TitledBorder.LEADING, TitledBorder.TOP,
				null, WEinFrame.neuhell1));
		ortetypenpanel.setBounds(12, 12, 581, 278);
		getContentPane().add(ortetypenpanel);
		ortetypenpanel.setLayout(null);

		ortefeld = new JTextField();
		ortefeld.setBounds(12, 52, 114, 25);
		ortetypenpanel.add(ortefeld);
		ortefeld.setColumns(10);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 89, 170, 131);
		ortetypenpanel.add(scrollPane);

		ortelist = new JList<String>(dlmorte);
		scrollPane.setViewportView(ortelist);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(379, 89, 170, 131);
		ortetypenpanel.add(scrollPane_1);

		typenlist = new JList<String>(dlmtype);
		scrollPane_1.setViewportView(typenlist);

		typenfeld = new JTextField();
		typenfeld.setColumns(10);
		typenfeld.setBounds(435, 52, 114, 25);
		ortetypenpanel.add(typenfeld);

		orterm = new JButton("L" + References.oe + "schen");
		orterm.addActionListener(e -> ortrm());
		orterm.setBounds(12, 232, 114, 25);
		ortetypenpanel.add(orterm);

		typenrm = new JButton("L" + References.oe + "schen");
		typenrm.addActionListener(e -> typrm());
		typenrm.setBounds(435, 232, 114, 25);
		ortetypenpanel.add(typenrm);

		orteadd = new JButton(" + ");
		orteadd.addActionListener(e -> ortadd());
		orteadd.setBounds(138, 52, 44, 25);
		ortetypenpanel.add(orteadd);

		typenadd = new JButton(" + ");
		typenadd.addActionListener(e -> typadd());
		typenadd.setBounds(379, 51, 44, 25);
		ortetypenpanel.add(typenadd);

		JLabel lblMesstypen = new JLabel("Messtypen");
		lblMesstypen.setBounds(379, 25, 98, 17);
		ortetypenpanel.add(lblMesstypen);

		JLabel lblOrte = new JLabel("Orte");
		lblOrte.setBounds(12, 26, 70, 15);
		ortetypenpanel.add(lblOrte);
		ortetypenpanel.setVisible(true);
		ortetypenpanel.setVisible(true);
		dmmm.setColumnIdentifiers(columnNames);

		btnZurck.setEnabled(false);
		btnSpeichern.setEnabled(false);

		dmmm2.setColumnIdentifiers(columnNames2);

		dmmm2.setDataVector(seting.getDatafuerTabelle(), columnNames2);
		columnModel.getColumn(0).setPreferredWidth(125);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3,
					int arg4, int arg5) {
				Component tableCellRendererComponent = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4,
						arg5);
				int align = DefaultTableCellRenderer.CENTER;
				((DefaultTableCellRenderer) tableCellRendererComponent).setHorizontalAlignment(align);
				return tableCellRendererComponent;
			}
		};
		for (int j = 0; j < columnModel.getColumnCount(); j++) {
			columnModel.getColumn(j).setCellRenderer(renderer);
		}
		for (int i = 0; i < stdmessen_table.getColumnModel().getColumnCount(); i++) {
			stdmessen_table.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
		WEinFrame.farbe(this);
	}

	public WriteFile_Pfarrei(Pfarrei pf, AProgress ap) {
		start(ap);
		btnZurck.setEnabled(true);
		btnZurck.addActionListener(e -> {
			if (ortetypenpanel.isVisible()) {
				ap.fertig(this, pf, ap.getAda().getSavepath(), ap);
				this.setVisible(false);
				this.dispose();
			}
		});
		panel_1.setVisible(true);
		setTitle("Einstellungen von " + pf.getName() + " " + References.ue + "berarbeiten");
		textField.setText(pf.getName());
		orte = pf.getOrte();
		for (String s : orte) {
			dlmorte.addElement(s);
		}
		typen = pf.getTypen();
		for (String s : typen) {
			dlmtype.addElement(s);
		}
		seting = pf.getSettings();
		dmmm2.setDataVector(seting.getDatafuerTabelle(), columnNames2);
		sm = pf.getStandardMessen();
		sm.removeIf(t -> new Sonstiges().isSonstiges(t));
		dmmm.setDataVector(getTableData(), columnNames);
		update();
		spinner_max_andere.setValue(pf.getSettings().getDaten()[0].getAnz_dienen());
		spinner_max_leiter.setValue(pf.getSettings().getDaten()[1].getAnz_dienen());
		if (pf.zaehlenHochaemterMit()) {
			rdbtnf12Ja.setSelected(true);
		} else {
			rdbtnf12Nein.setSelected(true);
		}
		boolean eins = false;
		for (int i = 2; i < 20; i++) {
			if (pf.getSettings().getDaten()[i].getAnz_dienen() != pf.getSettings().getDaten()[i + 1].getAnz_dienen()) {
				eins = true;
			}
		}
		rdbtnf1Ja.setSelected(eins);
		rdbtnf1Nein.setSelected(!eins);
		
		rdbtnf2Ja.setSelected(false);
		rdbtnf2Nein.setSelected(false);
		if (pf.getSettings().getDaten()[1].getAnz_dienen() == 0) {
			rdbtnf2Nein.setSelected(true);
			rdbtnf3Nein.setSelected(true);
		} else {
			rdbtnf3Nein.setSelected(false);
			rdbtnf3Ja.setSelected(false);
		}
	}

	public void bearbeiten(int i) {
		StandartMesse stdm = sm.get(i);
		stdmessen_comboBoxTag.getModel().setValue(stdm.getWochentag());
		stdmessen_comboBoxOrt.getModel().setValue(stdm.getOrt());
		stdmessen_comboBoxTyp.getModel().setValue(stdm.getTyp());
		stdmessen_spinnerstd.setValue(stdm.getBeginn_stunde());
		stdmessen_spinnermin.setValue(Integer.parseInt(stdm.getBeginn_minute()));
		stdmessen_spinneranz.setValue(stdm.getAnz_messdiener());
		sm.remove(i);
		update();
	}

	public void loeschen(int index) {
		sm.remove(index);
		update();
	}

	public void update() {
		dmmm.getDataVector().removeAllElements();
		revalidate();
		for (int i = 0; i < sm.size(); i++) {
			dmmm.addRow(getTableData()[i]);
		}
	}

	public void pfarreispeichern(AProgress ap) {
		String name = textField.getText();
		if (name == null || name.equals("")) {
			new Erroropener(new Exception("Bitte vorher etwas eingeben"));
			return;
		}
		boolean error = false;
		try {
			this.setEinstellungenfromGUI();
		} catch (Exception e) {
			new Erroropener(new Exception("Unzul" + References.ae + "ssige Eingabe in der Tabelle"));
			error = true;
		}
		if (error) {
			return;
		}
		String s = ap.getAda().getUtil().getSavepath();// waehleOrdner();
		boolean hochaemter = rdbtnf12Ja.isSelected();
		if (s != null) {
			Pfarrei pf = new Pfarrei(seting, sm, name, orte, typen, hochaemter);
			ap.fertig(this, pf, s, ap);
		}
	}

	public static void writeFile(Pfarrei pf, String pfad) {
		try {
			Sonstiges son = new Sonstiges();
			ArrayList<StandartMesse> Wsm = pf.getStandardMessen();

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty("indent", "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document doc = documentBuilder.newDocument();

			Element xml = doc.createElement("XML");
			doc.appendChild(xml);
			Element security = doc.createElement("Programm-Author");
			security.setAttribute("alle_Rechte", "bei_Author");
			security.appendChild(doc.createTextNode("Aclrian"));
			xml.appendChild(security);

			Element employee = doc.createElement("Body");
			xml.appendChild(employee);

			Element standartmessen = doc.createElement("Standartmessem");
			employee.appendChild(standartmessen);
			for (int i = 0; i < Wsm.size(); i++) {
				StandartMesse m = Wsm.get(i);
				if (son.isSonstiges(m)) {
					continue;
				}

				Element stdm = doc.createElement("std_messe");
				stdm.setAttribute("id", String.valueOf(i));

				Element tag = doc.createElement("tag");
				tag.appendChild(doc.createTextNode(m.getWochentag()));
				stdm.appendChild(tag);

				Element std = doc.createElement("std");
				std.appendChild(doc.createTextNode(String.valueOf(m.getBeginn_stunde())));
				stdm.appendChild(std);

				Element min = doc.createElement("min");
				min.appendChild(doc.createTextNode(m.getBeginn_minute()));
				stdm.appendChild(min);

				Element ort = doc.createElement("ort");
				ort.appendChild(doc.createTextNode(m.getOrt()));
				stdm.appendChild(ort);

				Element anz = doc.createElement("anz");
				anz.appendChild(doc.createTextNode(String.valueOf(m.getAnz_messdiener())));
				stdm.appendChild(anz);

				Element typ = doc.createElement("typ");
				typ.appendChild(doc.createTextNode(m.getTyp()));
				stdm.appendChild(typ);

				standartmessen.appendChild(stdm);
			}

			Element orte = doc.createElement("Orte");
			xml.appendChild(orte);
			for (int i = 0; i < pf.getOrte().size(); i++) {
				String ort = pf.getOrte().get(i);
				Element o = doc.createElement("ort");
				o.setAttribute("id", String.valueOf(i));
				o.appendChild(doc.createTextNode(ort));
				orte.appendChild(o);
			}

			Element type = doc.createElement("Type");
			xml.appendChild(type);
			for (int i = 0; i < pf.getTypen().size(); i++) {
				String typ = pf.getTypen().get(i);
				Element t = doc.createElement("typ");
				t.setAttribute("id", String.valueOf(i));
				t.appendChild(doc.createTextNode(typ));
				type.appendChild(t);
			}

			// Einstellungen
			Element einst = doc.createElement("Einstellungen");
			xml.appendChild(einst);

			Element hochamt = doc.createElement("hochaemter");
			int booleany = 0;
			if (pf.zaehlenHochaemterMit()) {
				booleany++;
			}
			hochamt.appendChild(doc.createTextNode(String.valueOf(booleany)));
			einst.appendChild(hochamt);

			// Settings
			Setting[] set = pf.getSettings().getDaten();
			for (int i = 0; i < Einstellungen.lenght; i++) {
				Attribut a = set[i].getA();
				int anz = set[i].getAnz_dienen();
				String val = String.valueOf(anz);
				int id = set[i].getId();
				String id_s = String.valueOf(id);
				switch (a) {
				case year:
					Element year = doc.createElement("setting");
					year.setAttribute("year", id_s);
					year.appendChild(doc.createTextNode(val));
					einst.appendChild(year);
					break;

				case max:
					Element ee = doc.createElement("setting");
					ee.setAttribute("Lleiter", id_s);
					ee.appendChild(doc.createTextNode(val));
					einst.appendChild(ee);
					break;
				}
			}

			DOMSource domSource = new DOMSource(doc);
			String datei = pf.getName();
			datei = datei.replaceAll(" ", "_");
			Utilities.logging(WriteFile_Pfarrei.class, "writeFile",
					"Pfarrei wird gespeichert in :" + pfad + "//" + datei + AData.pfarredateiendung);
			StreamResult streamResult = new StreamResult(new File(pfad + "//" + datei + AData.pfarredateiendung));
			transformer.transform(domSource, streamResult);
			Utilities.logging(WriteFile_Pfarrei.class, "writeFile",
					"Pfarrei: " + pf.getName() + "wurde erfolgreich gespeichert!");
		} catch (ParserConfigurationException pce) {
			new Erroropener(pce);
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			new Erroropener(tfe);
			tfe.printStackTrace();
		}

	}

	public void stdmessen_speichern() {
		String tag = (String) stdmessen_comboBoxTag.getModel().getValue();
		String ort = (String) stdmessen_comboBoxOrt.getModel().getValue();
		String typ = (String) stdmessen_comboBoxTyp.getModel().getValue();
		int beginn_h = (int) stdmessen_spinnerstd.getValue();
		String beginn_min = Integer.toString((int) stdmessen_spinnermin.getValue());
		if (beginn_min.length() == 1) {
			beginn_min = "0" + beginn_min;
		}
		int anz_messdiener = (int) stdmessen_spinneranz.getValue();
		StandartMesse stdm = new StandartMesse(tag, beginn_h, beginn_min, ort, anz_messdiener, typ);
		this.sm.add(stdm);
		Object[][] objs = getTableData();
		dmmm.addRow(objs[sm.size() - 1]);
		btnWeiter.setEnabled(true);
	}

	public void zurueck() {
		if (stdmessen_panel.isVisible()) {
			btnZurck.setEnabled(false);
			ortetypenpanel.setVisible(true);
			stdmessen_panel.setVisible(false);
			einstellungenpanel.setVisible(false);
			btnWeiter.setEnabled(true);
		} else if (fragen.isVisible()) {
			fragen.setVisible(false);
			stdmessen_panel.setVisible(true);
		} else {
			rdbtnf1Ja.setVisible(true);
			rdbtnf1Nein.setVisible(true);
			rdbtnf2Ja.setVisible(true);
			rdbtnf2Nein.setVisible(true);
			rdbtnf3Ja.setVisible(true);
			rdbtnf3Nein.setVisible(true);
			rdbtnf12Ja.setVisible(true);
			rdbtnf12Nein.setVisible(true);
			fragen.setVisible(true);
			stdmessen_panel.setVisible(false);
			ortetypenpanel.setVisible(false);
			einstellungenpanel.setVisible(false);
			btnSpeichern.setEnabled(false);
			btnWeiter.setEnabled(true);
		}
		WEinFrame.farbe(this);
	}

	public void weiter() {
		if (ortetypenpanel.isVisible()) {
			if (orte.size() > 0 && typen.size() > 0) {
				ortetypenpanel.setVisible(false);
				modelOrt.setList(orte);
				modelTyp.setList(typen);
				stdmessen_panel.setVisible(true);
				btnZurck.setEnabled(true);
				btnWeiter.setEnabled(false);
				if (sm.size() > 0) {
					btnWeiter.setEnabled(true);
				}
			} else {
				new Erroropener(new Exception("Bitte vorher etwas eingeben"));
			}
		} else if (fragen.isVisible()) {
			EnumDoing eh = EnumDoing.falscheEingabe;
			if (bg1.getSelection() != null && bg2.getSelection() != null && bg12.getSelection() != null) {
				if (getSelectedButtonText(bg2).equals(rdbtnf2Nein.getText())) {
					if (bg3.getSelection() != null) {
						eh = EnumDoing.richtigeEingabemit4Fragen;
					}
				} else {
					eh = EnumDoing.richtigeEingabemit3Fragen;
				}
			}
			if (eh == EnumDoing.falscheEingabe) {
				new Erroropener(new Exception("Bitte vorher alle Fragen beantworten"));
			} else {
				ChangeListener listener = new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent e) {
						if (alterunabhaengig) {
							dmmm2.setDataVector(Einstellungen.getEinheitsdata((int) model_andere.getValue()),
									columnNames2);

						}
						if (leitergleichandere) {
							spinner_max_leiter.setValue((int) model_andere.getValue());
						}

					}
				};
				Boolean enabeldJtable = getSelectedButtonText(bg1).equals(rdbtnf1Ja.getText());
				table.setEnabled(enabeldJtable);
				alterunabhaengig = rdbtnf1Nein.isSelected();
				if (rdbtnf2Ja.isSelected()) {
					// einwert fuer alle
					leitergleichandere = true;
					spinner_max_andere.removeChangeListener(listener);
					spinner_max_andere.addChangeListener(listener);
					spinner_max_leiter.setEnabled(false);
					lblLeitern.setEnabled(false);
				} else if (rdbtnf3Ja.isSelected()) {
					// 2 Werte
					leitergleichandere = false;
					spinner_max_andere.removeChangeListener(listener);
					spinner_max_leiter.setEnabled(true);
					lblLeitern.setEnabled(true);
				} else if (rdbtnf3Nein.isSelected()) {
					// Leiter = 0
					leitergleichandere = false;
					spinner_max_leiter.setEnabled(true);
					spinner_max_leiter.setValue(0);
					spinner_max_leiter.setEnabled(false);
					lblLeitern.setEnabled(false);
					spinner_max_andere.removeChangeListener(listener);
				}
				fragen.setVisible(false);
				einstellungenpanel.setVisible(true);
				rdbtnf1Ja.setVisible(false);
				rdbtnf1Nein.setVisible(false);
				rdbtnf2Ja.setVisible(false);
				rdbtnf2Nein.setVisible(false);
				rdbtnf3Ja.setVisible(false);
				rdbtnf3Nein.setVisible(false);
				rdbtnf12Ja.setVisible(false);
				rdbtnf12Nein.setVisible(false);
				// update for Listener
				if ((int) model_andere.getValue() < 20) {
					spinner_max_andere.setValue(model_andere.getNextValue());
					spinner_max_andere.setValue(model_andere.getPreviousValue());
				} else {
					spinner_max_andere.setValue(model_andere.getPreviousValue());
					spinner_max_andere.setValue(model_andere.getNextValue());
				}
				btnWeiter.setEnabled(false);
				btnSpeichern.setEnabled(true);
				WEinFrame.farbe(this.getContentPane());
			}

		} else {
			stdmessen_panel.setVisible(false);
			fragen.setVisible(true);
		}
		WEinFrame.farbe(this);
	}

	public void ortrm() {
		String s = ortelist.getSelectedValue();
		int i = ortelist.getSelectedIndex();
		if (i > -1) {
			dlmorte.remove(i);
			orte.remove(s);
		} else {
			new Erroropener(new Exception("Vorher etwas ausw" + References.ae + "hlen"));
		}
	}

	public void typrm() {
		String s = typenlist.getSelectedValue();
		int i = typenlist.getSelectedIndex();
		if (i > -1) {
			dlmtype.remove(i);
			typen.remove(s);
		} else {
			new Erroropener(new Exception("Vorher etwas ausw" + References.ae + "hlen"));
		}
	}

	public void typadd() {
		String s = typenfeld.getText();
		if (s.equals("")) {
			new Erroropener(new Exception("bitte etwas eingeben"));
		} else {
			typen.add(s);
			dlmtype.addElement(s);
		}
	}

	public void ortadd() {
		String s = ortefeld.getText();
		if (s.equals("")) {
			new Erroropener(new Exception("bitter vorher etwas eingeben"));
		} else {
			orte.add(s);
			dlmorte.addElement(s);
		}
	}

	public Object[][] getTableData() {
		Object[][] rtn = new String[sm.size()][6];
		for (int j = 0; j < sm.size(); j++) {
			rtn[j] = sm.get(j).getStrings();
		}
		return rtn;
	}

	public void setEinstellungenfromGUI() throws Exception {
		for (int j = 0; j < (Einstellungen.lenght - 2); j++) {
			String s = dmmm2.getValueAt(j, 1).toString();
			if (!s.matches("[0-9]+")) {
				throw new Exception();
			}
			int i = Integer.parseInt(s);
			if (i >= 0) {
				seting.editiereYear(j, i);
			} else {
				throw new Exception();
			}
		}
		seting.editMaxDienen(true, (int) spinner_max_leiter.getValue());
		seting.editMaxDienen(false, (int) spinner_max_andere.getValue());
	}

	public String getSelectedButtonText(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();

			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}

	public void setzeInTabelleDenGroesstenWert() {
		int andere = (int) spinner_max_andere.getValue();
		int leiter = (int) spinner_max_leiter.getValue();
		if (andere > leiter) {
			dmmm2.setDataVector(Einstellungen.getEinheitsdata(andere), columnNames2);
		} else {
			dmmm2.setDataVector(Einstellungen.getEinheitsdata(leiter), columnNames2);
		}
	}

	private enum EnumDoing {
		falscheEingabe, richtigeEingabemit3Fragen, richtigeEingabemit4Fragen;
	}
}