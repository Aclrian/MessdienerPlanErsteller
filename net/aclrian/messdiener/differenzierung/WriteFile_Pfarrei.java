package net.aclrian.messdiener.differenzierung;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
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

import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.differenzierung.Setting.Attribut;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.WMainFrame;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

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
	private JTable stdmessen_table;
	private JButton stdmessen_btnSpeichern;
	private JButton stdmessen_btnBearbeiten;
	private JComboBox<String> stdmessen_comboBoxTyp;
	private JComboBox<String> stdmessen_comboBoxOrt;
	private JLabel stdmessen_lblOrt;
	private JLabel stdmessen_lblTyp;
	private JSpinner stdmessen_spinnerstd;
	private JComboBox<String> stdmessen_comboBoxTag;
	private JSpinner stdmessen_spinnermin;
	private JScrollPane stdmessen_scrollPane;
	private DefaultListModel<String> dlmtype = new DefaultListModel<String>();
	private DefaultListModel<String> dlmorte = new DefaultListModel<String>();
	private JPanel stdmessen_panel = new JPanel();
	private JPanel ortetypenpanel = new JPanel();
	/**
	 * 
	 */
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
	public static final String[] columnNames2 = { "Einf\u00FChrungsjahr", "Anzahl" };
	private DefaultTableModel dmmm = new DefaultTableModel() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	private DefaultTableModel dmmm2 = new DefaultTableModel() {
		/**
		 * 
		 */
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
	private JCheckBox chckbxErhhenHochmter;

	/**
	 * @wbp.parser.constructor
	 */
	public WriteFile_Pfarrei(WMainFrame wmf) {
		start(wmf);
	}
	
	public void start(WMainFrame wmf) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Getting start...");
		setBounds(0, 0, 615, 365);
		setBounds(Utilities.setFrameMittig(623, 375));
		getContentPane().setLayout(null);

		seting = new Einstellungen();

		btnSpeichern = new JButton("Speichern");
		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pfarreispeichern(wmf);
			}
		});
		btnSpeichern.setBounds(476, 302, 117, 25);
		getContentPane().add(btnSpeichern);

		btnZurck = new JButton("Zur\u00FCck");
		btnZurck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zurueck();
			}
		});
		btnZurck.setBounds(217, 302, 117, 25);
		getContentPane().add(btnZurck);

		btnWeiter = new JButton("Weiter");
		btnWeiter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				weiter();
			}
		});
		btnWeiter.setBounds(347, 302, 117, 25);
		getContentPane().add(btnWeiter);

		stdmessen_panel.setVisible(false);
		stdmessen_panel.setBorder(
				new TitledBorder(null, "Standartmessen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		stdmessen_panel.setBounds(12, 12, 581, 278);
		getContentPane().add(stdmessen_panel);
		stdmessen_panel.setLayout(null);

		stdmessen_scrollPane = new JScrollPane();
		stdmessen_scrollPane.setBounds(12, 136, 557, 99);
		stdmessen_panel.add(stdmessen_scrollPane);

		stdmessen_table = new JTable(dmmm);
		dmmm.setColumnIdentifiers(columnNames);
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

		JLabel stdmessen_lblEintragNummer = new JLabel("Ausgew\u00E4hlten Eintrag");
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
					new Erroropener("Treffe zun\u00E4chst eine Auswahl");
				}
			}
		});
		stdmessen_btnBearbeiten.setBounds(178, 0, 117, 25);
		panel_1.add(stdmessen_btnBearbeiten);
		
		JButton btnLschen = new JButton("l\u00F6schen");
		btnLschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int i = stdmessen_table.getSelectedRow();
					loeschen(i);
					} catch (Exception e2) {
						new Erroropener("Treffe zun\u00E4chst eine Auswahl");
					}
			} 
		});
		btnLschen.setBounds(311, 0, 105, 25);
		panel_1.add(btnLschen);
		
		JLabel label_2 = new JLabel("/");
		label_2.setFont(new Font("Dialog", Font.BOLD, 14));
		label_2.setBounds(299, 0, 14, 25);
		panel_1.add(label_2);

		stdmessen_comboBoxTyp = new JComboBox<String>();
		stdmessen_comboBoxTyp.setBounds(467, 17, 102, 25);
		stdmessen_panel.add(stdmessen_comboBoxTyp);

		stdmessen_comboBoxOrt = new JComboBox<String>();
		stdmessen_comboBoxOrt.setBounds(292, 17, 114, 25);
		stdmessen_panel.add(stdmessen_comboBoxOrt);

		stdmessen_lblTyp = new JLabel("Typ:");
		stdmessen_lblTyp.setBounds(418, 17, 37, 25);
		stdmessen_panel.add(stdmessen_lblTyp);

		stdmessen_lblOrt = new JLabel("Ort:");
		stdmessen_lblOrt.setBounds(243, 17, 37, 25);
		stdmessen_panel.add(stdmessen_lblOrt);

		stdmessen_comboBoxTag = new JComboBox<String>();
		stdmessen_comboBoxTag.setBounds(117, 17, 114, 25);
		stdmessen_panel.add(stdmessen_comboBoxTag);
		stdmessen_comboBoxTag
				.setModel(new DefaultComboBoxModel<String>(new String[] { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" }));

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

		stdmessen_spinnerstdlblAnz = new JLabel("Anzahl der ben\u00F6tigten Messdiener");
		stdmessen_spinnerstdlblAnz.setBounds(229, 59, 261, 20);
		stdmessen_panel.add(stdmessen_spinnerstdlblAnz);

		stdmessen_spinneranz = new JSpinner();
		stdmessen_spinneranz.setModel(new SpinnerNumberModel(6, 1, 20, 1));
		stdmessen_spinneranz.setBounds(505, 59, 45, 20);
		stdmessen_panel.add(stdmessen_spinneranz);
		
		JButton btnUpdate = new JButton("update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		btnUpdate.setBounds(12, 247, 85, 25);
		stdmessen_panel.add(btnUpdate);

		ortetypenpanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)),
				"Standard Orte und standard Messetypen hizuf\u00FCgen", TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(51, 51, 51)));
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

		orterm = new JButton("L\u00F6schen");
		orterm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ortrm();
			}
		});
		orterm.setBounds(12, 232, 114, 25);
		ortetypenpanel.add(orterm);

		typenrm = new JButton("L\u00F6schen");
		typenrm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typrm();
			}
		});
		typenrm.setBounds(435, 232, 114, 25);
		ortetypenpanel.add(typenrm);

		orteadd = new JButton("+");
		orteadd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ortadd();
			}
		});
		orteadd.setBounds(138, 52, 44, 25);
		ortetypenpanel.add(orteadd);

		typenadd = new JButton("+");
		typenadd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typadd();
			}
		});
		typenadd.setBounds(379, 51, 44, 25);
		ortetypenpanel.add(typenadd);

		JLabel lblMesstypen = new JLabel("Messtypen");
		lblMesstypen.setBounds(379, 25, 98, 17);
		ortetypenpanel.add(lblMesstypen);

		JLabel lblOrte = new JLabel("Orte");
		lblOrte.setBounds(12, 26, 70, 15);
		ortetypenpanel.add(lblOrte);
		ortetypenpanel.setVisible(true);
		stdmessen_panel.setVisible(false);

		btnZurck.setEnabled(false);
		btnSpeichern.setEnabled(false);

		einstellungenpanel = new JPanel();
		einstellungenpanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Einstellungen",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		einstellungenpanel.setBounds(12, 12, 581, 278);
		getContentPane().add(einstellungenpanel);
		einstellungenpanel.setLayout(null);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(345, 37, 224, 229);
		einstellungenpanel.add(scrollPane_2);

		table = new JTable(dmmm2);
		scrollPane_2.setViewportView(table);
		dmmm2.setColumnIdentifiers(columnNames2);
		table.setRequestFocusEnabled(false);
		table.setFocusable(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(325, 24, 8, 242);
		einstellungenpanel.add(separator);

		JLabel lblMaximaleAnzahlVon = new JLabel("<html><body>Maximale Anzahl<sup>1</sup> von:</body></html>");
		lblMaximaleAnzahlVon.setBounds(26, 92, 160, 20);
		einstellungenpanel.add(lblMaximaleAnzahlVon);

		JLabel lblLeitern = new JLabel("Leitern:");
		lblLeitern.setBounds(188, 70, 70, 15);
		einstellungenpanel.add(lblLeitern);

		JLabel lblAnderen = new JLabel("anderen:");
		lblAnderen.setBounds(188, 116, 70, 15);
		einstellungenpanel.add(lblAnderen);

		spinner_max_leiter = new JSpinner();
		spinner_max_leiter.setModel(new SpinnerNumberModel(0, 0, 20, 1));
		spinner_max_leiter.setBounds(262, 68, 45, 20);
		einstellungenpanel.add(spinner_max_leiter);

		spinner_max_andere = new JSpinner();
		spinner_max_andere.setModel(new SpinnerNumberModel(3, 1, 20, 1));
		spinner_max_andere.setBounds(262, 114, 45, 20);
		einstellungenpanel.add(spinner_max_andere);

		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(77, 36, 236, 20);
		einstellungenpanel.add(textField);

		JLabel label_1 = new JLabel("Name der Messdienergemeinschaft:");
		label_1.setBounds(8, 16, 261, 20);
		einstellungenpanel.add(label_1);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(8, 59, 319, 7);
		einstellungenpanel.add(separator_1);
		
		JLabel lblStandardAnzahlPro = new JLabel("<html><body>Standard Anzahl<sup>1</sup> pro Jahrgang</body></html>");
		lblStandardAnzahlPro.setBounds(345, 12, 224, 20);
		einstellungenpanel.add(lblStandardAnzahlPro);
		
		JLabel lblmitanzahlIst = new JLabel("<html><body><sup>1</sup>Mit <i>Anzahl</i> ist die Anzahl gemeint, <br>die ein Messdiener seines Jahrgangs bzw. als Leiter standartm\u00E4\u00DFig oder maximal in einem Monat eingeteilt wird.</body></html>");
		lblmitanzahlIst.setBounds(36, 192, 271, 74);
		einstellungenpanel.add(lblmitanzahlIst);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(8, 173, 319, 7);
		einstellungenpanel.add(separator_2);
		
		chckbxErhhenHochmter = new JCheckBox("Erh\u00F6hen Hoch\u00E4mter die Anzahl");
		chckbxErhhenHochmter.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxErhhenHochmter.setSelected(true);
		chckbxErhhenHochmter.setBounds(26, 142, 271, 23);
		einstellungenpanel.add(chckbxErhhenHochmter);
		chckbxErhhenHochmter.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		dmmm2.setDataVector(seting.getDatafuerTabelle(), columnNames2);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(125);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			/**
			 * 
			 */
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
		ortetypenpanel.setVisible(true);
		einstellungenpanel.setVisible(false);
	}
	

	public WriteFile_Pfarrei(Pfarrei pf, WMainFrame wmf) {
		start(wmf);
		setTitle("Einstellungen von " + pf.getName() + " ueberarbeiten");
		textField.setText(pf.getName());
		orte = pf.getOrte();
		for(String s : orte) {
			dlmorte.addElement(s);
		}
		typen = pf.getTypen();
		for(String s : typen) {
			dlmtype.addElement(s);
		}
		seting = pf.getSettings();
		dmmm2.setDataVector(seting.getDatafuerTabelle(), columnNames2);
		sm = pf.getStandardMessen();
		dmmm.setDataVector(getTableData(), columnNames);
		update();
		spinner_max_andere.setValue(pf.getSettings().getDaten()[0].getAnz_dienen());
		spinner_max_leiter.setValue(pf.getSettings().getDaten()[1].getAnz_dienen());
	}

	public void bearbeiten(int i) {
			StandartMesse stdm = sm.get(i);
			stdmessen_comboBoxTag.setSelectedItem(stdm.getWochentag());
			stdmessen_comboBoxOrt.setSelectedItem(stdm.getOrt());
			stdmessen_comboBoxTyp.setSelectedItem(stdm.getTyp());
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
		/*for (int i = 0; i < dmmm.getRowCount(); i++) {
			dmmm.removeRow(i);
		}*/
		for (int i = 0; i < sm.size(); i++) {
			dmmm.addRow(getTableData()[i]);
		}
		
	}

	public void pfarreispeichern(WMainFrame wmf) {
		String name = textField.getText();
		if (name == null || name.equals("")) {
			new Erroropener("Bitte vorher etwas eingeben");
			return;
		}
		boolean error = false;
		try {
			this.setEinstellungenfromGUI();
		} catch (Exception e) {
			new Erroropener("Unzulaessige Eingabe in der Tabelle");
			error = true;
		}
		if (error) {
			return;
		}
		String s= wmf.getEDVVerwalter().getSavepath();//waehleOrdner();
		boolean hochaemter = chckbxErhhenHochmter.isSelected();
		if (s != null) {
			Pfarrei pf = new Pfarrei(seting, sm, name, orte, typen, hochaemter);
			writeFile(pf, s);
			wmf.fertig(this, pf, s);
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
				if(son.isSonstiges(m)) {
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
			
			//Einstellungen
			Element einst = doc.createElement("Einstellungen");
			xml.appendChild(einst);
			
			Element hochamt = doc.createElement("hochaemter");
			int booleany = 0;
			if(pf.zaehlenHochaemterMit()) {
				booleany++;
			}
			hochamt.appendChild(doc.createTextNode(String.valueOf(booleany)));
			einst.appendChild(hochamt);
			
			//Settings
			Setting[] set = pf.getSettings().getDaten();
			for (int i = 0; i < Einstellungen.lenght; i++) {
				Attribut a = set[i].getA();
				int anz = set[i].getAnz_dienen();
				String val = String.valueOf(anz);
				int id= set[i].getId();
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

			Utilities.logging(WriteFile_Pfarrei.class, WriteFile_Pfarrei.class.getEnclosingMethod(),"Pfarrei wird gespeichert in :" + pfad + "//" + datei + WMainFrame.pfarredateiendung);
			StreamResult streamResult = new StreamResult(new File(pfad + "//" + datei + WMainFrame.pfarredateiendung));
			transformer.transform(domSource, streamResult);
			Utilities.logging(WriteFile_Pfarrei.class, WriteFile_Pfarrei.class.getEnclosingMethod(),"Pfarrei: " + pf.getName() + "wurde erfolgreich gespeichert!");
		} catch (

		ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	public void stdmessen_speichern() {
		String tag = (String) stdmessen_comboBoxTag.getSelectedItem();
		String ort = (String) stdmessen_comboBoxOrt.getSelectedItem();
		String typ = (String) stdmessen_comboBoxTyp.getSelectedItem();
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
		} else {
			stdmessen_panel.setVisible(true);
			ortetypenpanel.setVisible(false);
			einstellungenpanel.setVisible(false);
		}
	}

	public void weiter() {
		if (ortetypenpanel.isVisible()) {
			if (orte.size() > 0 && typen.size() > 0) {
				ortetypenpanel.setVisible(false);
				String[] ortearray = new String[orte.size()];
				stdmessen_comboBoxOrt.setModel(new DefaultComboBoxModel<String>(orte.toArray(ortearray)));
				String[] typenarray = new String[typen.size()];
				stdmessen_comboBoxTyp.setModel(new DefaultComboBoxModel<String>(typen.toArray(typenarray)));
				stdmessen_panel.setVisible(true);
				btnZurck.setEnabled(true);
				btnWeiter.setEnabled(false);
				if(sm.size() > 0) {
					btnWeiter.setEnabled(true);
				}
			} else {
				new Erroropener("Bitte vorher etwas eingeben");
			}
		} else {
			stdmessen_panel.setVisible(false);
			einstellungenpanel.setVisible(true);
			btnWeiter.setEnabled(false);
			btnSpeichern.setEnabled(true);

		}

	}

	public void ortrm() {
		String s = ortelist.getSelectedValue();
		int i = ortelist.getSelectedIndex();
		if (i > -1) {
			dlmorte.remove(i);
			orte.remove(s);
		} else {
			new Erroropener("Vorher etwas auswaehlen");
		}
	}

	public void typrm() {
		String s = typenlist.getSelectedValue();
		int i = typenlist.getSelectedIndex();
		if (i > -1) {
			dlmtype.remove(i);
			typen.remove(s);
		} else {
			new Erroropener("Vorher etwas auswaehlen");
		}
	}

	public void typadd() {
		String s = typenfeld.getText();
		if (s.equals("")) {
			new Erroropener("bitte etwas eingeben");
		} else {
			typen.add(s);
			dlmtype.addElement(s);
		}
	}

	public void ortadd() {
		String s = ortefeld.getText();
		if (s.equals("")) {
			new Erroropener("bitte etwas eingeben");
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

}
