package net.aclrian.messdiener.window;

import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.CellRendererPane;
//import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

import com.toedter.calendar.JTextFieldDateEditor;

import net.aclrian.messdiener.deafault.ATextField;
import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.panels.AScrollBar;
import net.aclrian.messdiener.panels.MesseAnzeigen;
import net.aclrian.messdiener.panels.Start;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.start.AData;
import net.aclrian.messdiener.start.AProgress;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.auswaehlen.ACheckBox;
import net.aclrian.messdiener.window.auswaehlen.ATable;

public class WAlleMessen extends JFrame {

	private static final long serialVersionUID = -3010141011528401531L;

	private JPanel contentPane = new JPanel();
	private APanel panel;
	private JSpinner jdcanfang = new JSpinner();
	private JSpinner jdende = new JSpinner();
	private JLabel lblAlleMessdiener = new JLabel("Alle Messdiener");
	private JLabel lblAlleMesen = new JLabel("Alle Messen");
	private JButton minusmesse = new JButton("-");
	private JButton augemesse = new JButton("");
	private JButton plusmesse = new JButton("+");
	private JPanel messepanel = new JPanel();
	private JButton btnEinstellungen = new JButton("");
	private JLabel lblBis = new JLabel("bis:");
	private JButton btnPlanErstellen = new JButton("");
	private JLabel lblVon = new JLabel("von:");
	private JButton messestandarterzeugen = new JButton(References.pfeilrunter);
	private DefaultListModel<String> dfmesse = new DefaultListModel<String>();
	private JList<String> listmesse = new JList<String>();
	private DefaultListModel<String> dfmedi = new DefaultListModel<String>();
	private JList<String> listmedi = new JList<String>();
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

	/**
	 * Create the frame.
	 * 
	 */
	public WAlleMessen(AProgress ap) {
		// setBounds(new Rectangle(0, 0, 1600, 890));
		// setResizable(false);
		jdende.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DATE));
		JSpinner.DateEditor de_spinnerDatum = new JSpinner.DateEditor(jdende, "dd.MM.yyyy");
		jdende.setEditor(de_spinnerDatum);
		jdcanfang.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DATE));
		JSpinner.DateEditor pinnerDatum = new JSpinner.DateEditor(jdcanfang, "dd.MM.yyyy");
		jdcanfang.setEditor(pinnerDatum);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int heigth = Toolkit.getDefaultToolkit().getScreenSize().height;
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
		// new JDateChooser(cal.getTime());
		listmesse.setModel(dfmesse);
		listmedi.setModel(dfmedi);
		// setVisible(true);
		int a = (width / 4) / 3;
		int i = heigth / 10 - 5 * (heigth / 400);
		panel = new Start(a,i,ap.getAda().getPfarrei());// new MediAnzeigen(a, i, ap);// new
											// APanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 1));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(jdcanfang);
		contentPane.add(jdende);
		contentPane.add(mediscrollPanel);
		contentPane.add(messescrollPanel);
		messescrollPanel.setViewportView(listmesse);
		mediscrollPanel.setViewportView(listmedi);
		mediscrollPanel.setColumnHeaderView(lblAlleMessdiener);
		messescrollPanel.setColumnHeaderView(lblAlleMesen);

		contentPane.add(medipanel);
		medipanel.setLayout(null);
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, null, null, null));
		contentPane.add(panel);
		panel.setLayout(null);
		plusmedi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		medipanel.add(plusmedi);
		minusmedi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		medipanel.add(minusmedi);
		medipanel.add(augemedi);
		augemedi.setIcon(new ImageIcon(References.class.getResource("auge_new2.png")));
		augemedi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				update(ap.getAda());
			}
		});
		btnPlanErstellen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		// abstandbreit, abstandlinie, wviertel-2*abstandlinie,
		// heigth-abstandbreit-linieunten);
		// messestandarterzeugen.setFont(f);
		btnPlanErstellen.setIcon(new ImageIcon(WAlleMessen.class.getResource("/messen_erstellen.png")));
		
		messestandarterzeugen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date anfang = (Date) (jdcanfang.getModel().getValue());
				Date ende = (Date) (jdende.getModel().getValue());
				ArrayList<Messe> messsen = ap.generireDefaultMessen(anfang, ende);
				for (Messe messe : messsen) {
					addMesse(messe);
				}
			}
		});
		contentPane.add(messestandarterzeugen);
		contentPane.add(lblVon);
		contentPane.add(lblBis);
		contentPane.add(btnPlanErstellen);
		btnEinstellungen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		contentPane.add(btnEinstellungen);
		messepanel.setLayout(null);
		contentPane.add(messepanel);
		plusmesse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update(ap.getAda());
			}
		});
		messepanel.add(plusmesse);
		minusmesse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				}
		});
		messepanel.add(minusmesse);
		augemesse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		augemesse.setIcon(new ImageIcon(References.class.getResource("auge_new2.png")));
		btnEinstellungen.setIcon(new ImageIcon(WAlleMessen.class.getResource("/settings.png")));
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
		graphics();
		farbe(this.getContentPane(), false);
		//farbe(listmedi, false);
		//farbe(listmesse, false);

		this.setBounds(0, 0, width, heigth);// this.setBounds(effectiveScreenArea);
		// contentPane.setBounds(this.getBounds());

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		update(ap.getAda());
	}

	/**
	 * @wbp.parser.constructor
	 */
	public WAlleMessen(boolean onlyWindowbilderUse) {
		this(new AProgress());
		setBounds(0, 0, 1600, 900);
	}

	private void farbe(Component c, boolean d) {
		if (c instanceof JPanel) {
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
				//if (c != messescrollPanel && c != mediscrollPanel ) {
					farbe(com, false);
				//}
			}
			   
			((JScrollPane) c).setHorizontalScrollBar(new AScrollBar(dunkel2));
			//UIManager.put("ScrollBar._key_", new ColorUIResource(neuhell1));
			//UIManager.put("ScrollBar.thumb", new ColorUIResource(neuhell1));
			//((JScrollPane) c).getHorizontalScrollBar()
			// farbe(((JScrollPane) c).getViewport());
			// farbe(((JScrollPane) c).getColumnHeader());
		} else if (c instanceof JLabel) {
			if (((JLabel) c).getText().length() == 1) {
				c.setFont(f);
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
			for (Component com : ((JViewport) c).getComponents()) {
				// System.out.println(com.getClass());
				farbe(com, true);
			}

			c.setBackground(dunkel1);
			/* c.setForeground(standart); */
		} else if (c instanceof JTextFieldDateEditor) {
			((JTextFieldDateEditor) c).setBorder(b);
			c.setForeground(neuhell1);
			c.setBackground(dunkel1);
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
		} else if (c instanceof ATextField) {
			c.setBackground(dunkel1);
			c.setForeground(neuhell1);
			((ATextField) c).setBorder(b);
		} else if (c instanceof JSpinner) {
			for (Component ca : ((JSpinner) c).getComponents()) {
				farbe(ca, false);
			}
			((JSpinner) c).setBorder(null);
		} else if (c instanceof JFormattedTextField) {
			c.setBackground(dunkel1);
			c.setForeground(neuhell1);
			((JFormattedTextField) c).setBorder(b);
		} else if (c instanceof CellRendererPane) {
			c.setForeground(dunkel2);
			c.setBackground(neuhell1);

		} else if (c.getClass().toString().endsWith("JScrollPane$ScrollBar")) {
			System.out.println(c.getClass());
			c.setBackground(dunkel2);
			c.setForeground(neuhell1);
		} else if(c instanceof JEditorPane) {
			if (((JEditorPane) c).getText().toLowerCase().contains("Github".toLowerCase())) {
				
			}
			c.setForeground(Color.WHITE);
			c.setBackground(dunkel1);
			//c.setBackground(c);
		} else {
			try {
				System.out.println(c.getClass());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void graphics() {
		/*
		 * System.out.println(this.getBounds()); if (this.getBounds().width <
		 * 1010) { this.setVisible(false);
		 * this.setBounds(this.getBounds().x,this.getBounds().y,1010,this.
		 * getBounds().height); graphics(); //
		 * contentPane.setBounds(this.getBounds()); this.setVisible(true); }
		 * else if (this.getBounds().height < 568) { this.setVisible(false);
		 * this.setBounds(this.getBounds().x,this.getBounds().y,this.getBounds()
		 * .width,568); //contentPane.setBounds(this.getBounds()); graphics();
		 * this.setVisible(true); } else{
		 */
		int width = this.getContentPane().getBounds().width;
		int heigth = this.getContentPane().getBounds().height;
		int wviertel = width / 4;
		int linieunten = heigth / 10;
		int linienoben = linieunten * (1 + 1 / 3);// 1+1/3 Abstand wie bei
													// linieunten
		int abstandlinie = heigth / 400;
		int abstandbreit = width / 100;
		int abstandindreibuttons = (wviertel - 2 * abstandbreit) / 3;
		int breite = wviertel / 3;
		// breite,
		panel.setDfbtnBounds(breite, linieunten - abstandlinie);

		btnEinstellungen.setBounds((int) (2.5 * wviertel) - wviertel - (breite / 2),
				heigth - linieunten - abstandlinie / 2, breite, linieunten - 5 * abstandlinie);
		mediscrollPanel.setBounds(abstandbreit, abstandlinie, wviertel - 2 * abstandbreit, // abstandlinie,
				heigth - abstandbreit - linieunten);// abstandbreit -
													// linieunten);
		medipanel.setVisible(false);
		medipanel.setBounds(abstandbreit, heigth - linieunten - abstandlinie - abstandlinie / 2,
				wviertel - 2 * abstandbreit, linieunten);
		medipanel.setVisible(true);

		panel.setBounds((int) (wviertel + (abstandbreit * 0.5)), abstandlinie, 2 * wviertel - 2 * abstandbreit,
				heigth - abstandbreit - linieunten);
		plusmedi.setBounds(abstandbreit - abstandindreibuttons / 10, abstandlinie,
				abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
		minusmedi.setBounds(abstandbreit - abstandindreibuttons / 10 + abstandindreibuttons, abstandlinie,
				abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
		augemedi.setBounds(abstandbreit - abstandindreibuttons / 10 + (abstandindreibuttons * 2), abstandlinie,
				abstandindreibuttons - abstandindreibuttons / 10 - 1, linieunten - 5 * abstandlinie);
		Rectangle bounds = mediscrollPanel.getBounds();
		bounds.x = bounds.x+3*wviertel;
		bounds.y = bounds.y+linienoben+abstandlinie;
		bounds.height = bounds.height-linienoben-abstandlinie;
		messescrollPanel.setBounds(bounds);

		btnPlanErstellen.setBounds((int) (2.5 * wviertel) - (breite / 2), heigth - linieunten - abstandlinie / 2,
				breite, linieunten - 5 * abstandlinie);
		messepanel.setBounds(3 * wviertel+abstandbreit, heigth - linieunten - abstandlinie - abstandlinie / 2,
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
		lblVon.setBounds((int) (wviertel * 3 + (0.25) * abstandbreit), abstandlinie,
				(int) (achtel - abstandbreit + 0.45 * abstandbreit - 0.25 * abstandbreit),
				linienoben / 2 - abstandlinie);
		lblBis.setBounds((int) (wviertel * 3 + (0.5) * abstandbreit), abstandlinie + linienoben / 2,
				achtel - abstandbreit, linienoben / 2 - abstandlinie);
		jdcanfang.setBounds((int) (wviertel * 3 + achtel * 1), abstandlinie, drittel, linienoben / 2 - abstandlinie);

		// Rectangle b = jdcanfang.getBounds();
		int lhalb = (linienoben / 2);
		int posende = abstandlinie + lhalb;
		jdende.setBounds((int) (wviertel * 3 + achtel * 1), posende, drittel, linienoben / 2 - abstandlinie);
		messestandarterzeugen.setBounds((int) (wviertel * 3 + achtel * 1 + drittel + abstandbreit), abstandlinie,
				rest / 2 - abstandbreit, linienoben - abstandlinie);
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
			dfmesse.addElement(ap.getAda().getMesenarray().get(i).getIDHTML());
		}
	}

	public void update(AData ad) {
		ap.getAda().getMesenarray().sort(Messe.compForMessen);
		dfmedi.removeAllElements();
		for (int i = 0; i < ap.getAda().getMediarray().size(); i++) {
			dfmedi.addElement(ap.getAda().getMediarray().get(i).makeId());
		}
		ap.getAda().getMediarray().sort(Messdiener.compForMedis);
		dfmesse.removeAllElements();
		for (int i = 0; i < ap.getAda().getMesenarray().size(); i++) {
			dfmesse.addElement(ap.getAda().getMesenarray().get(i).getIDHTML());
		}
	}
	public void addMedi(Messdiener medi) {
		ap.getAda().addMedi(medi);
		dfmedi.removeAllElements();
		for (int i = 0; i < ap.getAda().getMediarray().size(); i++) {
			dfmedi.addElement(ap.getAda().getMediarray().get(i).makeId());
		}
	}

}
