package net.aclrian.mpe.start;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.border.Border;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.StandartMesse;
import net.aclrian.mpe.panels.APanel;
import net.aclrian.mpe.panels.AbmeldenPanel;
import net.aclrian.mpe.panels.Finish;
import net.aclrian.mpe.panels.MediAnzeigen;
import net.aclrian.mpe.panels.MesseAnzeigen;
import net.aclrian.mpe.panels.Start;

public class WEinFrame extends JFrame {

	public enum EnumActivePanel {
		Start, Messe, Medi, Finish, Abmelden, Anzeigen;
	}

	private static final long serialVersionUID = -3010141011528401531L;
	public final static Color hell0 = new Color(20, 96, 88);
	public final static Color hell1 = new Color(71, 142, 134);
	public final static Color dunkel1 = new Color(4, 72, 65);
	public final static Color ndunkel1 = new Color(72, 4, 11);
	public final static Color dunkel2 = new Color(0, 46, 41);
	public final static Color neuhell1 = new Color(42, 169, 156);
	public final static Color nneuhell1 = new Color(169, 42, 55);
	public final static Color neuhell2 = new Color(79, 189, 177);
	public final static Border b = BorderFactory.createLineBorder(neuhell1, 2, true);// new
	// BevelBorder(BevelBorder.LOWERED,
	// neuhell1, neuhell1, neuhell1,
	// neuhell1);
	public final static Font f = new Font("SansSerif", 0, 45);

	private static JButton createZeroButton() {
		JButton jbutton = new JButton();
		jbutton.setPreferredSize(new Dimension(0, 0));
		jbutton.setMinimumSize(new Dimension(0, 0));
		jbutton.setMaximumSize(new Dimension(0, 0));
		return jbutton;
	}

	public static void farbe(Component c) {
		/*
		 * Border b3 = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, neuhell1,
		 * dunkel1);// Color.BLACK); if (c instanceof JButton) { ((JButton)
		 * c).setBorder(b); try { if (((JButton) c).getText().length() == 1) {
		 * c.setFont(f); } } catch (NullPointerException e) { // new
		 * Errorpoener("no error") } } if (c instanceof JList<?>) { ((JComponent)
		 * c).setOpaque(false); c.setBackground(neuhell1); ((JList<?>)
		 * c).setSelectionBackground(dunkel1); ((JList<?>)
		 * c).setSelectionForeground(neuhell1); } else if (c instanceof JEditorPane) {
		 * if (((JEditorPane) c).getText().toLowerCase().contains("img")) {
		 * c.setForeground(Color.WHITE); c.setBackground(dunkel1); } } else if (c
		 * instanceof CellRendererPane) { c.setForeground(dunkel2);
		 * c.setBackground(neuhell1); } else if (c instanceof ATable) {
		 * c.setForeground(neuhell1); c.setBackground(dunkel1); ((ATable)
		 * c).setBorder(null); ((ATable) c).setForegroundInHeader(dunkel1, neuhell1,
		 * dunkel2, neuhell1, dunkel1); ((ATable) c).setSelectionForeground(dunkel1);
		 * ((ATable) c).setSelectionBackground(neuhell1); } else if (c instanceof
		 * JComponent) { c.setBackground(dunkel1); c.setForeground(neuhell1); for
		 * (Component co : ((JComponent) c).getComponents()) { farbe(co); } if (c
		 * instanceof JScrollPane) { ((JComponent) c).setBorder(b); ((JScrollPane)
		 * c).getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
		 * 
		 * @Override protected JButton createDecreaseButton(int orientation) { return
		 * createZeroButton(); }
		 * 
		 * @Override protected JButton createIncreaseButton(int orientation) { return
		 * createZeroButton(); }
		 * 
		 * @Override protected void configureScrollBarColors() { this.thumbColor =
		 * neuhell1; }
		 * 
		 * }); ((JScrollPane) c).getVerticalScrollBar().setUI(new BasicScrollBarUI() {
		 * 
		 * @Override protected JButton createDecreaseButton(int orientation) { return
		 * createZeroButton(); }
		 * 
		 * @Override protected JButton createIncreaseButton(int orientation) { return
		 * createZeroButton(); }
		 * 
		 * @Override protected void configureScrollBarColors() { this.thumbColor =
		 * neuhell1; }
		 * 
		 * }); } } else if (c instanceof Window) { if (c instanceof JFrame) {
		 * farbe(((JFrame) c).getContentPane()); } else if (c instanceof JDialog) {
		 * farbe(((JDialog) c).getContentPane()); } } else { try {
		 * System.out.println(c); } catch (Exception e) { e.printStackTrace(); } } if (c
		 * instanceof JTextField) { ((JTextField) c).setBorder(b3); ((JTextField)
		 * c).setSelectionColor(neuhell1); ((JTextField)
		 * c).setSelectedTextColor(dunkel1); } if (c instanceof BasicArrowButton) {
		 * ((BasicArrowButton) c).setBorder(b3); if (!c.isEnabled()) {
		 * c.setBackground(ndunkel1); } } if (c instanceof JSpinner) { ((JSpinner)
		 * c).setBorder(null); if (!c.isEnabled()) { JTextField field =
		 * ((JSpinner.DefaultEditor) ((JSpinner) c).getEditor()).getTextField();
		 * field.setEditable(false); field.setBackground(ndunkel1); //
		 * field.setForeground(UIManager.getColor("FormattedTextField.foreground")); } }
		 * if (c instanceof ATable) { ((ATable) c).setBorder(null); } c.repaint();
		 */
	}

	public static void farbeFIRST() {
		/*
		 * UIManager.put("TextField.caretForeground", WEinFrame.neuhell1);
		 * UIManager.put("FormattedTextField.caretForeground", WEinFrame.neuhell1);
		 * UIManager.put("TextField.caretForeground", WEinFrame.neuhell1);
		 * UIManager.put("PasswordField.caretForeground", WEinFrame.neuhell1);
		 * UIManager.put("controlDkShadow", WEinFrame.neuhell1);
		 * UIManager.put("TextField.selectionForeground", nneuhell1);
		 * UIManager.put("TextField.selectionBackground", ndunkel1);
		 * UIManager.put("Button.disabledText", nneuhell1); Border b =
		 * BorderFactory.createLineBorder(neuhell2, 1, false); // BorderFactory.cra
		 * UIManager.put("Table.focusCellHighlightBorder", b);
		 * UIManager.put("List.focusCellHighlightBorder", b);
		 * UIManager.put("Button.select", WEinFrame.dunkel2);
		 * UIManager.put("CheckBox.border", b);
		 * UIManager.put("ComboBox.selectionBackground", neuhell1);
		 * UIManager.put("ComboBox.selectionForeground", dunkel1);
		 * UIManager.put("ComboBox.buttonShadow", dunkel1);
		 * UIManager.put("CheckBoxMenuItem.border", b);
		 * UIManager.put("Label.disabledForeground", nneuhell1);
		 * 
		 * //
		 * UIManager.put("Spinner:\"Spinner.nextButton\"[Disabled].foregroundPainter",
		 * // new SpinnerNextPainter);
		 */
	}

	private JPanel contentPane = new JPanel();
	private APanel activepanel;
	private JSpinner jdcanfang = new JSpinner();
	private JSpinner jdende = new JSpinner();
	private JLabel lblAlleMessdiener = new JLabel("Alle Messdiener");
	private JLabel lblAlleMesen = new JLabel("Alle Messen");
	private JButton minusmesse = new JButton("-");
	private JButton augemesse = new JButton("");
	private JButton plusmesse = new JButton("+");
	private JPanel messepanel = new JPanel();
	private JLabel lblBis = new JLabel("bis:");
	private JLabel lblVon = new JLabel("von:");
	private JButton messestandarterzeugen = new JButton(References.pfeilrunter);
	private DefaultListModel<Messe> dfmesse = new DefaultListModel<>();
	private JList<Messe> listmesse = new JList<>(dfmesse);
	private DefaultListModel<Messdiener> dfmedi = new DefaultListModel<>();
	private JList<Messdiener> listmedi = new JList<>(dfmedi);
	private JScrollPane messescrollPanel = new JScrollPane();
	private JScrollPane mediscrollPanel = new JScrollPane();
	private JButton augemedi = new JButton("");
	private JButton minusmedi = new JButton("-");

	private JButton plusmedi = new JButton("+");

	private JPanel medipanel = new JPanel();

	private Calendar cal;

	private AProgress ap;

	/**
	 * Create the ONE frame.
	 * 
	 */
	public WEinFrame(AProgress ap) {
		// b = BorderFactory.createLineBorder(neuhell2, 1, true);
		listmedi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listmesse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jdende.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DATE));
		JSpinner.DateEditor de_spinnerDatum = new JSpinner.DateEditor(jdende, "dd.MM.yyyy");
		jdende.setEditor(de_spinnerDatum);
		jdcanfang.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DATE));
		JSpinner.DateEditor pinnerDatum = new JSpinner.DateEditor(jdcanfang, "dd.MM.yyyy");
		jdcanfang.setEditor(pinnerDatum);
		this.ap = ap;
		setIconImage(References.getIcon());
		setTitle("Messdienerplanersteller");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getScreenDevices()[0].getDefaultConfiguration();// getDefaultScreenDevice().getDefaultConfiguration();
		Rectangle bounds = gc.getBounds();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		Rectangle effectiveScreenArea = new Rectangle();

		effectiveScreenArea.x = bounds.x + screenInsets.left;
		effectiveScreenArea.y = bounds.y + screenInsets.top;
		effectiveScreenArea.height = bounds.height - screenInsets.top - screenInsets.bottom;
		effectiveScreenArea.width = gc.getBounds().width; // - screenInsets.left
		this.setBounds(effectiveScreenArea);
		cal = Calendar.getInstance();
		jdcanfang.setValue(cal.getTime());
		cal.add(Calendar.MONTH, 1);
		jdende.setValue(cal.getTime());
		listmesse.setModel(dfmesse);
		listmedi.setModel(dfmedi);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(jdcanfang);
		contentPane.add(jdende);
		contentPane.add(mediscrollPanel);
		contentPane.add(messescrollPanel);
		listmedi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listmesse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		messescrollPanel.setViewportView(listmesse);
		mediscrollPanel.setViewportView(listmedi);
		mediscrollPanel.setColumnHeaderView(lblAlleMessdiener);
		messescrollPanel.setColumnHeaderView(lblAlleMesen);

		contentPane.add(medipanel);
		medipanel.setLayout(null);
		plusmedi.addActionListener(e -> changeAP(EnumActivePanel.Medi, true));
		medipanel.add(plusmedi);
		minusmedi.addActionListener(arg0 -> {
//				if (listmedi.getSelectedIndex() > -1) {
//					remove(getMedi(), ap.getAda().getMediarray());
//					Messdiener m = dfmedi.remove(listmedi.getSelectedIndex());
//					String s = "Soll der Messdiener: '" + m.makeId() + "' auch als Datei gel" + References.oe
//							+ "scht werden?\nDieser Vorgang kann nicht r" + References.ue + "ckg" + References.ae
//							+ "ngig gemacht werden";
//					JOptionPane op = new JOptionPane(s, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
//
//					JFrame f = new JFrame();
//					farbe(op);
//					JDialog dialog = op.createDialog(f, "Auch als Datei l" + References.oe + "schen?");
//					farbe(dialog);
//					dialog.setVisible(true);
//					try {
//						int selectedValue = (int) op.getValue();
//						if (selectedValue == 0) {
//							System.out.println("ds");
//							File file = m.getFile();
//							file.delete();
//							ArrayList<Messdiener> ueberarbeitete = new ArrayList<Messdiener>();
//							for (Messdiener messdiener : ap.getAda().getMediarray()) {
//								MediAnzeigen.alteloeschen(m, messdiener.getFreunde(), ueberarbeitete);
//								MediAnzeigen.alteloeschen(m, messdiener.getGeschwister(), ueberarbeitete);
//							}
//							for (Messdiener medi : ueberarbeitete) {
//								if (medi.toString().equals(m.toString())) {
//									continue;
//								}
//								WriteFile wf = new WriteFile(medi, ap.getAda().getUtil().getSavepath());
//								try {
//									wf.toXML(ap);
//								} catch (IOException e) {
//									new Erroropener(new Exception("Konnte die Datei nicht speichern"));
//									e.printStackTrace();
//								}
//							}
//						}
//					} catch (NullPointerException e) {
//					}
//				}
		});
		medipanel.add(minusmedi);
		medipanel.add(augemedi);
		augemedi.setIcon(new ImageIcon(References.class.getResource("auge_new2.png")));
		augemedi.setDisabledIcon(new ImageIcon(References.class.getResource("auge_disabl.png")));
		augemedi.addActionListener(arg0 -> {
			if (listmedi.getSelectedIndex() > -1) {
				/*
				 * changeAP(EnumActivePanel.Medi, false); if (activepanel instanceof
				 * MediAnzeigen) { Messdiener m = getMedi(); if (m != null) { ((MediAnzeigen)
				 * activepanel).setMedi(m, ap); dfmedi.remove(listmedi.getSelectedIndex());
				 * ap.getAda().getMediarray().remove(m); } }
				 */
			}
		});
		/*
		 * messestandarterzeugen.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { listmesse.removeAll();
		 * Date anfang = (Date) (jdcanfang.getModel().getValue()); Date ende = (Date)
		 * (jdende.getModel().getValue()); ArrayList<Messe> messsen; try { messsen =
		 * ap.generireDefaultMessen(anfang, ende); } catch (Exception e1) { new
		 * Erroropener(e1); messsen = new ArrayList<>(); } for (int i = 0; i <
		 * messsen.size(); i++) { addMesse(messsen.get(i)); }
		 * ap.getAda().getMesenarray().sort(Messe.compForMessen); } });
		 * contentPane.add(messestandarterzeugen); contentPane.add(lblVon);
		 * contentPane.add(lblBis); messepanel.setLayout(null);
		 * contentPane.add(messepanel); plusmesse.addActionListener(new ActionListener()
		 * {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * changeAP(EnumActivePanel.Messe, true); } }); messepanel.add(plusmesse);
		 * minusmesse.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * remove(listmesse.getSelectedValue(), ap.getAda().getMesenarray());
		 * ap.getAda().getVoreingeteilte().remove(listmesse.getSelectedValue());
		 * update(ap.getAda()); } }); messepanel.add(minusmesse);
		 * augemesse.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { if
		 * (listmesse.getSelectedIndex() > -1) { changeAP(EnumActivePanel.Messe, false);
		 * if (activepanel instanceof MesseAnzeigen) { Messe m = getMesse(); if (m !=
		 * null) { ((MesseAnzeigen) activepanel).setMesse(m, ap);
		 * dfmesse.removeElementAt(listmesse.getSelectedIndex());
		 * ap.getAda().getMesenarray().remove(m); } } } } }); augemesse.setIcon(new
		 * ImageIcon(References.class.getResource("auge_new2.png")));
		 * augemesse.setDisabledIcon(new
		 * ImageIcon(References.class.getResource("auge_disabl.png")));
		 * messepanel.add(augemesse); addComponentListener(new ComponentAdapter() {
		 * 
		 * @Override public void componentMoved(ComponentEvent e) { componentResized(e);
		 * super.componentMoved(e); }
		 * 
		 * @Override public void componentShown(ComponentEvent e) { componentResized(e);
		 * super.componentShown(e); }
		 * 
		 * @Override public void componentResized(ComponentEvent e) { graphics();
		 * setVisible(true); } }); update(ap.getAda()); activepanel = new Start(86, 67,
		 * ap); this.add(activepanel); activepanel.setVisible(true); graphics();
		 * farbe(getContentPane());
		 */
	}

	/**
	 * @wbp.parser.constructor
	 */
	public WEinFrame(boolean onlyWindowbilderUse) {
		// this(new AProgress(true));
		setBounds(0, 0, 1600, 900);
	}

	public void addMedi(Messdiener medi) {
		/*
		 * ap.getAda().addMedi(medi); dfmedi.removeAllElements(); for (int i = 0; i <
		 * ap.getAda().getMediarray().size(); i++) {
		 * dfmedi.addElement(ap.getAda().getMediarray().get(i)); }
		 */
	}

	public void addMesse(Messe m) {
		/*
		 * ap.getAda().addMesse(m); dfmesse.removeAllElements(); for (int i = 0; i <
		 * ap.getAda().getMesenarray().size(); i++) {
		 * dfmesse.addElement(ap.getAda().getMesenarray().get(i)); }
		 */
	}

	public void changeAP(EnumActivePanel eap, boolean withupdate) {
		boolean ferienplan = false;
		try {
			if (activepanel instanceof AbmeldenPanel) {
				ferienplan = true;
			}
			if (activepanel instanceof Finish) {
				activepanel.setBorder(null);
				this.remove(activepanel);
				for (Component c : contentPane.getComponents()) {
					c.setVisible(true);
				}
			} // else {
			contentPane.remove(activepanel);
			// }
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		int dfbtw = (this.getBounds().width / 4) / 3;
		int dfbth = this.getBounds().height / 10 - 5 * (this.getBounds().height / 400);
		switch (eap) {
		case Finish:
			for (Component c : contentPane.getComponents()) {
				c.setVisible(false);
			}
			activepanel = new Finish(dfbtw, dfbth, ferienplan, ap);
			break;
		case Messe:
			activepanel = new MesseAnzeigen(dfbtw, dfbth, ap);
			break;
		case Medi:
			activepanel = new MediAnzeigen(dfbtw, dfbth, ap);
			break;
		case Abmelden:
			activepanel = new AbmeldenPanel(dfbtw, dfbth, ap);
			break;
		default:
			activepanel = new Start(dfbtw, dfbth, ap);
			break;
		}
		boolean bo = eap == EnumActivePanel.Start;
		minusmedi.setEnabled(bo);
		minusmesse.setEnabled(bo);
		plusmedi.setEnabled(bo);
		plusmesse.setEnabled(bo);
		augemedi.setEnabled(bo);
		augemesse.setEnabled(bo);
		if (!(activepanel instanceof Finish)) {
			this.add(activepanel, 1);
		} else {
			this.add(activepanel, 1);
			activepanel.setBounds(getBounds());
		}
		activepanel.setVisible(true);
		farbe(activepanel);
		graphics();
		/*
		 * if (withupdate) { update(ap.getAda()); }
		 */
	}

	public void changeAP(StandartMesse sm) {
		/*
		 * try { contentPane.remove(activepanel); } catch (NullPointerException e) {
		 * e.printStackTrace(); } int dfbtw = (this.getBounds().width / 4) / 3; int
		 * dfbth = this.getBounds().height / 10 - 5 * (this.getBounds().height / 400);
		 * activepanel = new StandartMesseListePanel(dfbtw, dfbth,
		 * ap.getAda().getMediarray(), Messdiener.compForMedis, ap, sm); boolean bo =
		 * false; minusmedi.setEnabled(bo); minusmesse.setEnabled(bo);
		 * plusmedi.setEnabled(bo); plusmesse.setEnabled(bo); augemedi.setEnabled(bo);
		 * augemesse.setEnabled(bo); activepanel.setVisible(true); this.add(activepanel,
		 * 1); farbe(activepanel); graphics(); update(ap.getAda());
		 */
	}

	public AProgress getAp() {
		return ap;
	}

	public JList<Messdiener> getListmedi() {
		return listmedi;
	}

	public Messdiener getMedi() {
		/*
		 * for (Messdiener medi : ap.getAda().getMediarray()) { if
		 * (medi.toString().equals(listmedi.getSelectedValue().toString())) { return
		 * medi; } }
		 */
		return null;
	}

	protected Messe getMesse() {
		/*
		 * for (Messe m : ap.getAda().getMesenarray()) { if
		 * (m.toString().equals(listmesse.getSelectedValue().toString())) { return m; }
		 * }
		 */
		return null;
	}

	public void graphics() {
		/*
		 * update(ap.getAda()); int width = this.getContentPane().getBounds().width; int
		 * heigth = this.getContentPane().getBounds().height; int wviertel = width / 4;
		 * int linieunten = heigth / 10; int linienoben = linieunten * (1 + 1 / 3); int
		 * abstandlinie = heigth / 400; int abstandbreit = width / 100; int
		 * abstandindreibuttons = (wviertel - 2 * abstandbreit) / 3;
		 * mediscrollPanel.setBounds(abstandbreit, abstandlinie, wviertel - 2 *
		 * abstandbreit, // abstandlinie, heigth - abstandbreit - linieunten);
		 * medipanel.setVisible(false); medipanel.setBounds(abstandbreit, heigth -
		 * linieunten - abstandlinie - abstandlinie / 2, wviertel - 2 * abstandbreit,
		 * linieunten); medipanel.setVisible(true); activepanel.setBounds((int)
		 * (wviertel + (abstandbreit * 0.5)), 2 * abstandlinie, 2 * wviertel - 2 *
		 * abstandbreit, heigth - abstandbreit - linieunten); if (activepanel instanceof
		 * Finish) { activepanel.setBounds(0, 0, width, heigth); }
		 * activepanel.graphics(); activepanel.setVisible(true);
		 * plusmedi.setBounds(abstandbreit - abstandindreibuttons / 10, abstandlinie,
		 * abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 *
		 * abstandlinie); minusmedi.setBounds(abstandbreit - abstandindreibuttons / 10 +
		 * abstandindreibuttons, abstandlinie, abstandindreibuttons -
		 * abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
		 * augemedi.setBounds(abstandbreit - abstandindreibuttons / 10 +
		 * (abstandindreibuttons * 2), abstandlinie, abstandindreibuttons -
		 * abstandindreibuttons / 10 - 1, linieunten - 5 * abstandlinie); Rectangle
		 * bounds = mediscrollPanel.getBounds(); bounds.x = bounds.x + 3 * wviertel;
		 * bounds.y = bounds.y + linienoben + abstandlinie; bounds.height =
		 * bounds.height - linienoben - abstandlinie;
		 * messescrollPanel.setBounds(bounds); messepanel.setBounds(3 * wviertel +
		 * abstandbreit, heigth - linieunten - abstandlinie - abstandlinie / 2, wviertel
		 * - 2 * abstandbreit, linieunten); plusmesse.setBounds(abstandbreit -
		 * abstandindreibuttons / 10, abstandlinie, abstandindreibuttons -
		 * abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
		 * minusmesse.setBounds(abstandbreit - abstandindreibuttons / 10 +
		 * abstandindreibuttons, abstandlinie, abstandindreibuttons -
		 * abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
		 * augemesse.setBounds(abstandbreit - abstandindreibuttons / 10 +
		 * (abstandindreibuttons * 2), abstandlinie, abstandindreibuttons -
		 * abstandindreibuttons / 10, linieunten - 5 * abstandlinie); int achtel =
		 * wviertel / 8; int drittel = wviertel / 3; int rest = wviertel - achtel -
		 * drittel - abstandbreit; linienoben = linienoben + 1 - 1; abstandlinie =
		 * abstandlinie + 1 - 1; abstandbreit = abstandbreit + 1 - 1;
		 * lblVon.setBounds((int) (wviertel * 3 + (0.25) * abstandbreit), abstandlinie +
		 * 2, (int) (achtel - abstandbreit + 0.45 * abstandbreit - 0.25 * abstandbreit),
		 * linienoben / 2 - abstandlinie - 2); lblBis.setBounds((int) (wviertel * 3 +
		 * (0.5) * abstandbreit), abstandlinie + linienoben / 2, achtel - abstandbreit,
		 * linienoben / 2 - abstandlinie); jdcanfang.setBounds((int) (wviertel * 3 +
		 * achtel * 1), abstandlinie, drittel, linienoben / 2 - abstandlinie); int lhalb
		 * = (linienoben / 2); int posende = abstandlinie + lhalb;
		 * jdende.setBounds((int) (wviertel * 3 + achtel * 1), posende, drittel,
		 * linienoben / 2 - abstandlinie); messestandarterzeugen.setBounds((int)
		 * (wviertel * 3 + achtel * 1 + drittel + abstandbreit), 2 * abstandlinie + 1,
		 * rest / 2 - abstandbreit, linienoben - 3 * abstandlinie + 1);
		 * setTextEqualsToSize(getContentPane()); Utilities.setFrameMittig(width,
		 * heigth);
		 */
	}

	private void remove(Messdiener medi, ArrayList<Messdiener> al) {
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).equals(medi)) {
				al.remove(i);
			}
		}
	}

	private void remove(Messe me, ArrayList<Messe> al) {
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).equals(me)) {
				al.remove(i);
			}
		}
	}

	private void setTextEqualsToSize(Component c) {
		if (c instanceof JPanel) {
			for (Component c2 : ((JPanel) c).getComponents()) {
				setTextEqualsToSize(c2);
			}
		} else if (c instanceof JLabel) {
			int h = c.getHeight();
			int font = h / 2;
			c.getFont().deriveFont(Font.PLAIN, font);

		} else if (c instanceof JButton) {
			int h = c.getHeight();
			int font = h / 2;
			c.getFont().deriveFont(Font.PLAIN, font);
		} else if (c instanceof JScrollPane) {
			setTextEqualsToSize(((JScrollPane) c).getColumnHeader());
		} else if (c instanceof JViewport) {
			int h = c.getHeight();
			int font = h / 2;
			c.getFont().deriveFont(Font.PLAIN, font);
		}
	}

	public void update(AData ad) {
		/*
		 * Messdiener medi = listmedi.getSelectedValue(); Messe m =
		 * listmesse.getSelectedValue();
		 * ap.getAda().getMesenarray().sort(Messe.compForMessen);
		 * ap.getAda().getMediarray().sort(Messdiener.compForMedis);
		 * dfmedi.removeAllElements(); for (int i = 0; i <
		 * ap.getAda().getMediarray().size(); i++) {
		 * dfmedi.addElement(ap.getAda().getMediarray().get(i)); if (medi != null &&
		 * medi.toString().equals(ap.getAda().getMediarray().get(i).toString())) {
		 * listmedi.setSelectedValue(medi, true); } } dfmesse.removeAllElements(); for
		 * (int i = 0; i < ap.getAda().getMesenarray().size(); i++) {
		 * dfmesse.addElement(ap.getAda().getMesenarray().get(i)); if (m != null &&
		 * m.toString().equals(ap.getAda().getMesenarray().get(i).toString())) {
		 * listmesse.setSelectedValue(m, true); } }
		 */
	}
}
