package net.aclrian.messdiener.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.CellRendererPane;
//import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import net.aclrian.messdiener.deafault.ATextField;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.panels.APanel;
import net.aclrian.messdiener.panels.AbmeldenPane;
import net.aclrian.messdiener.panels.AbmeldenTable;
//import net.aclrian.messdiener.panels.AScrollBar;
import net.aclrian.messdiener.panels.Finish;
import net.aclrian.messdiener.panels.MediAnzeigen;
import net.aclrian.messdiener.panels.MesseAnzeigen;
import net.aclrian.messdiener.panels.Start;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AData;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.utils.WriteFile;
import net.aclrian.messdiener.window.auswaehlen.ACheckBox;
import net.aclrian.messdiener.window.auswaehlen.AList;
import net.aclrian.messdiener.window.auswaehlen.ATable;

public class WAlleMessen extends JFrame {

    private static final long serialVersionUID = -3010141011528401531L;

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
    private DefaultListModel<Messe> dfmesse = new DefaultListModel<Messe>();
    private JList<Messe> listmesse = new JList<Messe>(dfmesse);
    private DefaultListModel<Messdiener> dfmedi = new DefaultListModel<Messdiener>();
    private JList<Messdiener> listmedi = new JList<Messdiener>(dfmedi);
    private JScrollPane messescrollPanel = new JScrollPane();
    private JScrollPane mediscrollPanel = new JScrollPane();
    private JButton augemedi = new JButton("");
    private JButton minusmedi = new JButton("-");
    private JButton plusmedi = new JButton("+");
    private JPanel medipanel = new JPanel();

    // private JButton btnStandart = new JButton("");
    //
    public final static Color hell0 = new Color(20, 96, 88);
    public final static Color hell1 = new Color(71, 142, 134);
    // public static Color standart = new Color(39, 116, 108);
    public final static Color dunkel1 = new Color(4, 72, 65);
    public final static Color dunkel2 = new Color(0, 46, 41, 1);
    public final static Color neuhell1 = new Color(42, 169, 156);
    public final static Color neuhell2 = new Color(79, 189, 177);
    public final static Border b = new BevelBorder(BevelBorder.LOWERED, hell0, hell0, hell0, hell0);
    public final static Font f = new Font("SansSerif", 0, 45);
    // private Font f;
    private Calendar cal;
    private AProgress ap;

    private Container tmppane;

    /**
     * Create the frame.
     * 
     */
    public WAlleMessen(AProgress ap) {
	listmedi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	listmesse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	jdende.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DATE));
	JSpinner.DateEditor de_spinnerDatum = new JSpinner.DateEditor(jdende, "dd.MM.yyyy");
	jdende.setEditor(de_spinnerDatum);
	jdcanfang.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DATE));
	JSpinner.DateEditor pinnerDatum = new JSpinner.DateEditor(jdcanfang, "dd.MM.yyyy");
	jdcanfang.setEditor(pinnerDatum);
	this.ap = ap;
	this.setIconImage(References.getIcon());
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
	// -
	// screenInsets.right;
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
	plusmedi.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		changeAP(EnumActivePanel.Medi, true);
	    }
	});
	medipanel.add(plusmedi);
	minusmedi.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		if (listmedi.getSelectedIndex() > -1) {
		    remove(getMedi(), ap.getAda().getMediarray());
		    for (Messdiener m : ap.getAda().getMediarray()) {
			System.out.println(m);
		    }
		    Messdiener m = dfmedi.remove(listmedi.getSelectedIndex());
		    String s = "Soll der Messdiener: '" + m.makeId() + "' auch als Datei gel" + References.oe
			    + "scht werden?\nDieser Vorgang kann nicht r" + References.ue + "ckg" + References.ae
			    + "ngig gemacht werden";
		    JOptionPane op = new JOptionPane(s, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);

		    JFrame f = new JFrame();
		    JDialog dialog = op.createDialog(f, "Auch als Datei l" + References.oe + "schen?");
		    // farbe(op, false);
		    farbe(dialog, false);
		    dialog.setVisible(true);
		    try {
			int selectedValue = (int) op.getValue();
			if (selectedValue == 0) {// abr: 2; ja: 0; nein: 1;
			    // exit: ;
			    File file = m.getFile();
			    file.delete();
			    ArrayList<Messdiener> ueberarbeitete = new ArrayList<Messdiener>();
			    for (Messdiener messdiener : ap.getAda().getMediarray()) {
				MediAnzeigen.alteloeschen(m, messdiener.getFreunde(), ueberarbeitete);
				MediAnzeigen.alteloeschen(m, messdiener.getGeschwister(), ueberarbeitete);
			    }
			    for (Messdiener medi : ueberarbeitete) {
				if (medi.toString().equals(m.toString())) {
				    continue;
				}
				WriteFile wf = new WriteFile(medi, ap.getAda().getUtil().getSavepath());
				try {
				    wf.toXML(ap);
				} catch (IOException e) {
				    new Erroropener("Konnte die Datei nicht speichern");
				    e.printStackTrace();
				}
			    }
			}
		    } catch (NullPointerException e) {
		    }
		}
	    }
	});
	medipanel.add(minusmedi);
	medipanel.add(augemedi);
	augemedi.setIcon(new ImageIcon(References.class.getResource("auge_new2.png")));
	augemedi.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		if (listmedi.getSelectedIndex() > -1) {
		    changeAP(EnumActivePanel.Medi, false);
		    if (activepanel instanceof MediAnzeigen) {
			Messdiener m = getMedi();
			if (m != null) {
			    ((MediAnzeigen) activepanel).setMedi(m, ap);
			    dfmedi.remove(listmedi.getSelectedIndex());
			    ap.getAda().getMediarray().remove(m);
			}
		    }
		}
	    }
	});
	messestandarterzeugen.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		listmesse.removeAll();
		Date anfang = (Date) (jdcanfang.getModel().getValue());
		Date ende = (Date) (jdende.getModel().getValue());
		ArrayList<Messe> messsen = ap.generireDefaultMessen(anfang, ende);
		System.out.println("h");
		System.out.println(messsen.size());
		for (int i = 0; i < messsen.size(); i++) {
		    addMesse(messsen.get(i));
		    System.out.println(i);
		}
		System.out.println("ende");
	    }
	});
	contentPane.add(messestandarterzeugen);
	contentPane.add(lblVon);
	contentPane.add(lblBis);
	messepanel.setLayout(null);
	contentPane.add(messepanel);
	plusmesse.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		changeAP(EnumActivePanel.Messe, true);
	    }
	});
	messepanel.add(plusmesse);
	minusmesse.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		remove(listmesse.getSelectedValue(), ap.getAda().getMesenarray());
		ap.getAda().getVoreingeteilte().remove(listmesse.getSelectedValue());
		update(ap.getAda());
	    }
	});
	messepanel.add(minusmesse);
	augemesse.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (listmesse.getSelectedIndex() > -1) {
		    changeAP(EnumActivePanel.Messe, false);
		    if (activepanel instanceof MesseAnzeigen) {
			Messe m = getMesse();
			if (m != null) {
			    ((MesseAnzeigen) activepanel).setMesse(m, ap);
			    dfmesse.removeElementAt(listmesse.getSelectedIndex());
			    ap.getAda().getMesenarray().remove(m);
			}
		    }
		    /*
		     * if (listmedi.getSelectedIndex() > -1) { changeAP(EnumActivePanel.Medi,
		     * false); if (activepanel instanceof MediAnzeigen) { Messdiener m = getMedi();
		     * System.out.println(m); ((MediAnzeigen) activepanel).setMedi(m, ap);
		     * dfmedi.remove(listmedi.getSelectedIndex());
		     * ap.getAda().getMediarray().remove(m); } } }
		     */
		}
	    }
	});
	augemesse.setIcon(new ImageIcon(References.class.getResource("auge_new2.png")));
	messepanel.add(augemesse);
	// btnStandart.setIcon(new
	// ImageIcon(References.class.getResource("eigensacftenbearbeiten_new.png")));
	// contentPane.add(btnStandart);
	addComponentListener(new ComponentAdapter() {

	    @Override
	    public void componentMoved(ComponentEvent e) {
		componentResized(e);
		super.componentMoved(e);
	    }

	    @Override
	    public void componentShown(ComponentEvent e) {
		componentResized(e);
		super.componentShown(e);
	    }

	    @Override
	    public void componentResized(ComponentEvent e) {
		graphics();
		setVisible(true);
	    }
	});

	// graphics();
	// farbe(this.getContentPane(), false);
	// farbe(listmedi, false);
	// farbe(listmesse, false);

	// this.setBackground(dunkel1);
	// this.setBounds(0, 0, width, heigth);// this.setBounds(effectiveScreenArea);
	// contentPane.setBounds(this.getBounds());

	// this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	update(ap.getAda());
	activepanel = new Start(86, 67, ap);
	this.add(activepanel);
	activepanel.setVisible(true);
	graphics();
	farbe(getContentPane(), false);

    }

    protected Messe getMesse() {
	for (Messe m : ap.getAda().getMesenarray()) {
	    if (m.toString().equals(listmesse.getSelectedValue().toString())) {
		return m;
	    }
	}
	return null;
    }

    public Messdiener getMedi() {
	for (Messdiener medi : ap.getAda().getMediarray()) {
	    if (medi.toString().equals(listmedi.getSelectedValue().toString())) {
		return medi;
	    }
	}
	return null;
    }

    /**
     * @wbp.parser.constructor
     */
    public WAlleMessen(boolean onlyWindowbilderUse) {
	this(new AProgress());
	setBounds(0, 0, 1600, 900);
    }

    public static void farbe(Component c, boolean d) {
	if (c instanceof JLayeredPane) {
	    for (Component c2 : ((JLayeredPane) c).getComponents()) {
		farbe(c2, false);
	    }
	    c.setBackground(dunkel1);
	} else if (c instanceof JPanel) {
	    for (Component c2 : ((JPanel) c).getComponents()) {
		farbe(c2, false);
	    }
	    c.setBackground(dunkel1);
	} else if (c instanceof JButton) {
	    c.setForeground(neuhell1);
	    c.setBackground(dunkel1);
	    ((JButton) c).setBorder(b);
	    if (((JButton) c).getText().length() == 1) {
		c.setFont(f);
	    }
	} else if (c instanceof JScrollPane) {
	    for (Component com : ((JScrollPane) c).getComponents()) {
		// System.out.println(com.getClass());
		// if (c != messescrollPanel && c != mediscrollPanel ) {
		farbe(com, false);
		// }
	    }

	    // ((JScrollPane) c).setHorizontalScrollBar(new
	    // AScrollBar(dunkel2));
	    // UIManager.put("ScrollBar._key_", new ColorUIResource(neuhell1));
	    // UIManager.put("ScrollBar.thumb", new ColorUIResource(neuhell1));
	    // ((JScrollPane) c).getHorizontalScrollBar()
	    // farbe(((JScrollPane) c).getViewport());
	    // farbe(((JScrollPane) c).getColumnHeader());
	} else if (c instanceof JLabel) {
	    try {
		if (((JLabel) c).getText().length() == 1) {
		    c.setFont(f);
		}
	    } catch (NullPointerException e) {
	    }
	    c.setForeground(neuhell1);
	    c.setBackground(dunkel1);
	    if (d) {
		/*
		 * c.setForeground(dunkel1); c.setBackground(neuhell1);
		 */
		((JLabel) c).setOpaque(true);
		((JLabel) c).setBorder(b);
	    }

	    // ((JLabel) c).setBorder(b);
	} else if (c instanceof AList<?>) {
	    /*
	     * for (Component comp : ((AList<?>) c).getComponents()) { farbe(comp, false); }
	     */
	    c.setForeground(neuhell1);
	    c.setBackground(dunkel1);
	} else if (c instanceof JList<?>) {
	    ((JComponent) c).setOpaque(false);
	    c.setBackground(neuhell2);
	    ((JList<?>) c).setSelectionBackground(dunkel1);
	    ((JList<?>) c).setSelectionForeground(neuhell2);
	} else if (c instanceof JViewport) {
	    // Font fs = new Font("SansSerif", 0, 26);
	    // c.setFont(fs);
	    // ViewportUI vui = new ViewportUI() {
	    // };
	    if (((JViewport) c).getView() != null) {
		farbe(((JViewport) c).getView(), true);
	    }
	    for (Component com : ((JViewport) c).getComponents()) {
		// System.out.println(com.getClass());
		farbe(com, true);
	    }

	    c.setBackground(dunkel1);
	    c.setForeground(neuhell1);
	} else if (c instanceof ACheckBox) {
	    c.setForeground(neuhell1);
	    c.setBackground(dunkel1);
	    // c.get
	    // ColorModel cm = c.getColorModel();
	    /*
	     * if (cm instanceof DirectColorModel) { DirectColorModel dcm =
	     * (DirectColorModel) cm; // dcm. }
	     */
	} else if (c instanceof ATable) {
	    c.setForeground(neuhell1);
	    c.setBackground(dunkel1);
	    ((ATable) c).setBorder(b);
	    // JTableHeader th = ((ATable) c).getTableHeader();
	    ((ATable) c).setForegroundInHeader(dunkel1, neuhell1);
	    ((ATable) c).setSelectionForeground(dunkel1);
	    ((ATable) c).setSelectionBackground(neuhell1);
	} else if (c instanceof AbmeldenTable) {
	    c.setForeground(neuhell1);
	    c.setBackground(dunkel1);
	    ((AbmeldenTable) c).setBorder(b);
	    // JTableHeader th = ((ATable) c).getTableHeader();
	    ((AbmeldenTable) c).setForegroundInHeader(dunkel1, neuhell1);
	    ((AbmeldenTable) c).setSelectionForeground(dunkel1);
	    ((AbmeldenTable) c).setSelectionBackground(neuhell1);
	    for (Component ca :  ((AbmeldenTable) c).getComponents()) {
		farbe(ca, false);
	    }
	} else if (c instanceof ATextField) {
	    c.setBackground(dunkel1);
	    c.setForeground(neuhell1);
	    ((ATextField) c).setBorder(b);
	} else if (c instanceof JSpinner) {
	    for (Component ca : ((JSpinner) c).getComponents()) {
		farbe(ca, false);
	    }
	    ((JSpinner) c).setBorder(null);
	    c.repaint();
	} else if (c instanceof JFormattedTextField) {
	    c.setBackground(dunkel1);
	    c.setForeground(neuhell1);
	    ((JFormattedTextField) c).setBorder(b);
	} else if (c instanceof CellRendererPane) {
	    c.setForeground(dunkel2);
	    c.setBackground(neuhell1);

	    /*
	     * } else if (c.getClass().toString().endsWith("JScrollPane$ScrollBar")) {
	     * System.out.println(c.getClass()); c.setBackground(dunkel2);
	     * c.setForeground(neuhell1);
	     */} else if (c instanceof JEditorPane) {
	    if (((JEditorPane) c).getText().toLowerCase().contains("img")) {
		c.setForeground(Color.WHITE);
		c.setBackground(dunkel1);
	    }
	} else if (c instanceof JOptionPane) {
	    for (Component c2 : ((JOptionPane) c).getComponents()) {
		farbe(c2, false);
	    }
	    c.setBackground(dunkel1);
	} else if (c instanceof JDialog) {
	    for (Component c2 : ((JDialog) c).getComponents()) {
		farbe(c2, false);
	    }
	    c.setBackground(dunkel1);
	} else if (c instanceof JRootPane) {
	    for (Component c2 : ((JRootPane) c).getComponents()) {
		farbe(c2, false);
	    }
	    c.setBackground(dunkel1);
	} else if (c instanceof JRadioButton) {
	    c.setForeground(neuhell1);
	    c.setBackground(dunkel1);
	} else {
	    try {
		System.out.println(c);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	c.repaint();
    }

    public void graphics() {
	update(ap.getAda());
	int width = this.getContentPane().getBounds().width;
	int heigth = this.getContentPane().getBounds().height;
	int wviertel = width / 4;
	int linieunten = heigth / 10;
	int linienoben = linieunten * (1 + 1 / 3);// 1+1/3 Abstand wie bei
	// linieunten
	int abstandlinie = heigth / 400;
	int abstandbreit = width / 100;
	int abstandindreibuttons = (wviertel - 2 * abstandbreit) / 3;
	// int breite = wviertel / 3;
	// breite,
	// activepanel.setDfbtnBounds(breite, linieunten - abstandlinie);

	mediscrollPanel.setBounds(abstandbreit, abstandlinie, wviertel - 2 * abstandbreit, // abstandlinie,
		heigth - abstandbreit - linieunten);// abstandbreit -
	// linieunten);
	medipanel.setVisible(false);
	medipanel.setBounds(abstandbreit, heigth - linieunten - abstandlinie - abstandlinie / 2,
		wviertel - 2 * abstandbreit, linieunten);
	medipanel.setVisible(true);
	activepanel.setBounds((int) (wviertel + (abstandbreit * 0.5)), 2 * abstandlinie,
		2 * wviertel - 2 * abstandbreit, heigth - abstandbreit - linieunten);

	if (activepanel instanceof Finish) {
	    activepanel.setBounds(0, 0, width, heigth);

	}
	activepanel.graphics();
	plusmedi.setBounds(abstandbreit - abstandindreibuttons / 10, abstandlinie,
		abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
	minusmedi.setBounds(abstandbreit - abstandindreibuttons / 10 + abstandindreibuttons, abstandlinie,
		abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
	augemedi.setBounds(abstandbreit - abstandindreibuttons / 10 + (abstandindreibuttons * 2), abstandlinie,
		abstandindreibuttons - abstandindreibuttons / 10 - 1, linieunten - 5 * abstandlinie);
	Rectangle bounds = mediscrollPanel.getBounds();
	bounds.x = bounds.x + 3 * wviertel;
	bounds.y = bounds.y + linienoben + abstandlinie;
	bounds.height = bounds.height - linienoben - abstandlinie;
	messescrollPanel.setBounds(bounds);

	messepanel.setBounds(3 * wviertel + abstandbreit, heigth - linieunten - abstandlinie - abstandlinie / 2,
		wviertel - 2 * abstandbreit, linieunten);
	plusmesse.setBounds(abstandbreit - abstandindreibuttons / 10, abstandlinie,
		abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
	minusmesse.setBounds(abstandbreit - abstandindreibuttons / 10 + abstandindreibuttons, abstandlinie,
		abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
	augemesse.setBounds(abstandbreit - abstandindreibuttons / 10 + (abstandindreibuttons * 2), abstandlinie,
		abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);

	int achtel = wviertel / 8;
	int drittel = wviertel / 3;
	int rest = wviertel - achtel - drittel - abstandbreit;
	linienoben = linienoben + 1 - 1;
	abstandlinie = abstandlinie + 1 - 1;
	abstandbreit = abstandbreit + 1 - 1;
	lblVon.setBounds((int) (wviertel * 3 + (0.25) * abstandbreit), abstandlinie + 2,
		(int) (achtel - abstandbreit + 0.45 * abstandbreit - 0.25 * abstandbreit),
		linienoben / 2 - abstandlinie - 2);
	lblBis.setBounds((int) (wviertel * 3 + (0.5) * abstandbreit), abstandlinie + linienoben / 2,
		achtel - abstandbreit, linienoben / 2 - abstandlinie);
	jdcanfang.setBounds((int) (wviertel * 3 + achtel * 1), abstandlinie, drittel, linienoben / 2 - abstandlinie);

	// Rectangle b = jdcanfang.getBounds();
	int lhalb = (linienoben / 2);
	int posende = abstandlinie + lhalb;
	jdende.setBounds((int) (wviertel * 3 + achtel * 1), posende, drittel, linienoben / 2 - abstandlinie);
	messestandarterzeugen.setBounds((int) (wviertel * 3 + achtel * 1 + drittel + abstandbreit),
		2 * abstandlinie + 1, rest / 2 - abstandbreit, linienoben - 2 * abstandlinie + 1);
	// btnStandart.setBounds((int) (wviertel * 3 + achtel * 0.7 + drittel +
	// (0.5) *
	// abstandbreit + rest / 2),
	// abstandlinie, (int) (rest / 2 - (0.5) * abstandbreit), linienoben -
	// abstandlinie);
	// f = new Font(lblBis.getFont().getName(), Font.PLAIN, width / 50);
	setTextEqualsToSize(getContentPane());
	Utilities.setFrameMittig(width, heigth);
    }// }

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

    public AProgress getAp() {
	return ap;
    }

    public void addMesse(Messe m) {
	ap.getAda().addMesse(m);
	dfmesse.removeAllElements();
	for (int i = 0; i < ap.getAda().getMesenarray().size(); i++) {
	    dfmesse.addElement(ap.getAda().getMesenarray().get(i));
	}
    }

    public void update(AData ad) {
	Messdiener medi = listmedi.getSelectedValue();
	Messe m = listmesse.getSelectedValue();
	ap.getAda().getMesenarray().sort(Messe.compForMessen);
	ap.getAda().getMediarray().sort(Messdiener.compForMedis);
	dfmedi.removeAllElements();
	for (int i = 0; i < ap.getAda().getMediarray().size(); i++) {
	    dfmedi.addElement(ap.getAda().getMediarray().get(i));
	    if (medi != null && medi.toString().equals(ap.getAda().getMediarray().get(i).toString())) {
		listmedi.setSelectedValue(medi, true);
	    }
	}
	dfmesse.removeAllElements();
	for (int i = 0; i < ap.getAda().getMesenarray().size(); i++) {
	    dfmesse.addElement(ap.getAda().getMesenarray().get(i));
	    if (m != null && m.toString().equals(ap.getAda().getMesenarray().get(i).toString())) {
		listmesse.setSelectedValue(m, true);
	    }
	}
    }

    public void addMedi(Messdiener medi) {
	ap.getAda().addMedi(medi);
	dfmedi.removeAllElements();
	for (int i = 0; i < ap.getAda().getMediarray().size(); i++) {
	    dfmedi.addElement(ap.getAda().getMediarray().get(i));
	}

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

    public JList<Messdiener> getListmedi() {
	return listmedi;
    }

    public void changeAP(EnumActivePanel eap, boolean withupdate) {
	try {
	    contentPane.remove(activepanel);
	} catch (NullPointerException e) {
	    e.printStackTrace();
	}
	int dfbtw = (this.getBounds().width / 4) / 3;
	int dfbth = this.getBounds().height / 10 - 5 * (this.getBounds().height / 400);
	switch (eap) {
	case Finish:
	    activepanel = new Finish(dfbtw, dfbth, ap);
	    break;
	case Messe:
	    activepanel = new MesseAnzeigen(dfbtw, dfbth, ap);
	    break;
	case Medi:
	    activepanel = new MediAnzeigen(dfbtw, dfbth, ap);
	    break;
	case Abmelden:
	    activepanel = new AbmeldenPane(dfbtw, dfbth, ap);
	    break;
	default:
	    System.out.println(dfbtw);
	    System.out.println(dfbth);
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
	    this.tmppane = this.getContentPane();
	    this.setContentPane(activepanel);
	}
	activepanel.setVisible(true);
	farbe(activepanel, false);
	graphics();
	if (withupdate) {
	    update(ap.getAda());
	}

    }

    public enum EnumActivePanel {
	Start, Messe, Medi, Finish, Abmelden;
    }
}
