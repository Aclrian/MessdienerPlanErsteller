package net.aclrian.messdiener.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
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
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.util.Calendar;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JTextFieldDateEditor;

import net.aclrian.messdiener.panels.MediAnzeigen;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.utils.DateienVerwalter;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.window.medierstellen.WMediBearbeitenFrame;

public class WAlleMessen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3010141011528401531L;

	//
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					WAlleMessen frame = new WAlleMessen();
					frame.setVisible(true);
				} catch (Exception e) {
					new Erroropener(e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	private JPanel contentPane = new JPanel();
	private APanel panel;
	private JTextFieldDateEditor jdcanfang = new JTextFieldDateEditor();
	private JTextFieldDateEditor jdende = new JTextFieldDateEditor();
	private JScrollPane scrollPanemedi = new JScrollPane();
	private DefaultListModel<String> dfmedi = new DefaultListModel<String>();
	private JList<String> listmedi = new JList<String>();
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
	private JScrollPane messescrollPanel = new JScrollPane();
	private JButton augemedi = new JButton("");
	private JButton minusmedi = new JButton("-");
	private JButton plusmedi = new JButton("+");
	private JPanel medipanel = new JPanel();

	// private JButton btnStandart = new JButton("");
	//
	final static Color hell0 = new Color(20, 96, 88);
	final static Color hell1 = new Color(71, 142, 134);
	final static Color standart = new Color(39, 116, 108);
	final static Color dunkel1 = new Color(4, 72, 65);
	final static Color dunkel2 = new Color(0, 46, 41, 1);
	final static Color neuhell1 = new Color(42, 169, 156);
	final static Color neuhell2 = new Color(79, 189, 177);
	final static Border b = new BevelBorder(BevelBorder.LOWERED, hell0, hell0, hell0, hell0);
	final static Font f = new Font("SansSerif", 0, 45);
	// private Font f;
	private Calendar cal;

	public WAlleMessen() {
		this(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height,
				new DateienVerwalter());
	}

	/**
	 * Create the frame.
	 * 
	 */
	private WAlleMessen(int width, int heigth, DateienVerwalter dv) {
		// setBounds(new Rectangle(0, 0, 1600, 890));
		// setResizable(false);
		// int width = 1600;// Toolkit.getDefaultToolkit().getScreenSize().width;
		// int heigth = 890;// Toolkit.getDefaultToolkit().getScreenSize().height;
		// setBounds(0,0,533,534);
		this.setIconImage(WMainFrame.getIcon(new References()));
		setTitle("Messdienerplanersteller");
		setBounds(0, 0, width, heigth);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getScreenDevices()[0].getDefaultConfiguration();// getDefaultScreenDevice().getDefaultConfiguration();
		Rectangle bounds = gc.getBounds();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		Rectangle effectiveScreenArea = new Rectangle();

		effectiveScreenArea.x = bounds.x + screenInsets.left;
		effectiveScreenArea.y = bounds.y + screenInsets.top;
		effectiveScreenArea.height = bounds.height - screenInsets.top - screenInsets.bottom;
		effectiveScreenArea.width = gc.getBounds().width; // - screenInsets.left - screenInsets.right;
		this.setBounds(effectiveScreenArea);
		cal = Calendar.getInstance();
		jdcanfang.setDate(cal.getTime());
		cal.add(Calendar.MONTH, 1);
		jdende.setDate(cal.getTime());
		// new JDateChooser(cal.getTime());
		listmesse.setModel(dfmesse);
		listmedi.setModel(dfmedi);
		// setVisible(true);

		int wviertel = width / 4;
		int linieunten = heigth / 10;
		// int linienoben = linieunten * (1 + 1 / 3);// 1+1/3 Abstand wie bei linieunten
		int abstandlinie = heigth / 100;
		int abstandbreit = width / 100;
		// int abstandindreibuttons = (wviertel - abstandlinie) / 3;
		// int breite = wviertel / 3;
		@SuppressWarnings("unused")
		int panelWith = 2 * wviertel - 2 * abstandlinie;
		@SuppressWarnings("unused")
		int panelHight = heigth - abstandbreit - linieunten;
		int a = (width / 4) / 3;
		int i = heigth / 10 - 5 * (heigth/ 400);
		panel = new MediAnzeigen(a,i);// new APanel();
		graphics(width, heigth);
		contentPane.setBounds(new Rectangle(0, 0, 800, 600));
		contentPane.setBounds(0, 0, width, heigth);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 1));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(jdcanfang);
		contentPane.add(jdende);
		contentPane.add(scrollPanemedi);
		scrollPanemedi.setViewportView(listmedi);
		scrollPanemedi.setColumnHeaderView(lblAlleMessdiener);

		messescrollPanel.setColumnHeaderView(lblAlleMesen);
		contentPane.add(medipanel);
		medipanel.setLayout(null);
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, null, null, null));
		contentPane.add(panel);
		panel.setLayout(null);
		plusmedi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				WMediBearbeitenFrame wmbf = new WMediBearbeitenFrame(new WMainFrame());// new WMainFrame(false));
				wmbf.setVisible(true);
				// setzeinPanel(wmbf.getPanel());
			}
		});
		medipanel.add(plusmedi);
		medipanel.add(minusmedi);
		medipanel.add(augemedi);
		augemedi.setIcon(new ImageIcon(References.class.getResource("auge_new2.png")));
		augemedi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		// abstandbreit, abstandlinie, wviertel-2*abstandlinie,
		// heigth-abstandbreit-linieunten);
		// messestandarterzeugen.setFont(f);
		btnPlanErstellen.setIcon(new ImageIcon(WAlleMessen.class.getResource("/messen_erstellen.png")));
		contentPane.add(messescrollPanel);
		messescrollPanel.setViewportView(listmesse);
		contentPane.add(messestandarterzeugen);
		contentPane.add(lblVon);
		contentPane.add(lblBis);
		contentPane.add(btnPlanErstellen);
		contentPane.add(btnEinstellungen);
		messepanel.setLayout(null);
		contentPane.add(messepanel);
		messepanel.add(plusmesse);
		messepanel.add(minusmesse);
		augemesse.setIcon(new ImageIcon(References.class.getResource("auge_new2.png")));
		btnEinstellungen.setIcon(new ImageIcon(WAlleMessen.class.getResource("/settings.png")));
		messepanel.add(augemesse);
		// btnStandart.setIcon(new
		// ImageIcon(References.class.getResource("eigensacftenbearbeiten_new.png")));
		// contentPane.add(btnStandart);
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
		farbe(this.getContentPane());
		dfmedi.addElement("dsdfs");
		// pack();: --> dosn't work
	}

	/**
	 * @wbp.parser.constructor
	 */
	public WAlleMessen(boolean onlyWindowbilderUse) {
		this(1600, 900, new DateienVerwalter());
		setBounds(0, 0, 1600, 900);
	}

	private void farbe(Component c) {
		if (c instanceof JPanel) {
			for (Component c2 : ((JPanel) c).getComponents()) {
				farbe(c2);
			}
			c.setBackground(dunkel1);
		} else if (c instanceof JButton) {
			c.setForeground(standart);
			c.setBackground(dunkel1);
			((JButton) c).setBorder(b);
			if (((JButton) c).getText().length() == 1) {
				c.setFont(f);
			}
		} else if (c instanceof JScrollPane) {
			farbe(((JScrollPane) c).getViewport());
			farbe(((JScrollPane) c).getColumnHeader());
		} else if (c instanceof JLabel) {
			c.setForeground(standart);
			c.setBackground(dunkel1);
			if (((JLabel) c).getText().length() == 1) {
				c.setFont(f);
			}
			// ((JLabel) c).setBorder(b);
		} else if (c instanceof JList<?>) {
			c.setForeground(standart);
			Color col = new Color(0, 255, 255);
			((JList<?>) c).setSelectionBackground(col);
			//((JList<?>) c).setSelectionBackground(neuhell2);
			c.setBackground(neuhell1);
			/*if (((JList<?>) c).getModel() instanceof DefaultListModel<?>) {
				DefaultListModel<?> dlm = (DefaultListModel<?>) ((JList<?>) c).getModel();
				dlm.
			}*/
		} else if (c instanceof JViewport) {
			Font fs = new Font("SansSerif", 0, 26);
			c.setFont(fs);
			c.setBackground(standart);
			c.setForeground(dunkel1);
		} else if (c instanceof JTextFieldDateEditor) {
			((JTextFieldDateEditor) c).setBorder(b);
			c.setForeground(neuhell1);
			c.setBackground(dunkel1);
		}
		else if(c instanceof JCheckBox) {
			c.setForeground(standart);
			c.setBackground(dunkel1);
			ColorModel cm = c.getColorModel();
			/*if (cm instanceof DirectColorModel) {
				DirectColorModel dcm = (DirectColorModel) cm;
		//		dcm.
			}*/
		}
		else {
			System.out.println(c.getClass().getName());
		}
	}

	private void graphics(int width, int heigth) {

		width = this.getContentPane().getBounds().width;
		heigth = this.getContentPane().getBounds().height;
		int wviertel = width / 4;
		int linieunten = heigth / 10;
		int linienoben = linieunten * (1 + 1 / 3);// 1+1/3 Abstand wie bei linieunten
		int abstandlinie = heigth / 400;
		int abstandbreit = width / 100;
		int abstandindreibuttons = (wviertel - 2 * abstandbreit) / 3;
		int breite = wviertel / 3; 
		//breite, 
		
		btnEinstellungen.setBounds((int) (2.5 * wviertel) - wviertel - (breite / 2),
				heigth - linieunten - abstandlinie / 2, breite, linieunten - 5 * abstandlinie);
		scrollPanemedi.setBounds(abstandbreit, abstandlinie, wviertel - 2 * abstandbreit, // abstandlinie,
				heigth - abstandbreit - linieunten);// abstandbreit - linieunten);
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
		messescrollPanel.setBounds(3 * wviertel, abstandlinie + linienoben, wviertel - 2 * abstandbreit,
				heigth - abstandbreit - linienoben - linienoben);

		btnPlanErstellen.setBounds((int) (2.5 * wviertel) - (breite / 2), heigth - linieunten - abstandlinie / 2,
				breite, linieunten - 5 * abstandlinie);
		messepanel.setBounds(3 * wviertel, heigth - linieunten - abstandlinie - abstandlinie / 2,
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
		lblVon.setBounds((int) (wviertel * 3 + (0.5) * abstandbreit), abstandlinie, achtel - abstandbreit,
				linienoben / 2 - abstandlinie);
		lblBis.setBounds((int) (wviertel * 3 + (0.5) * abstandbreit), abstandlinie + linienoben / 2,
				achtel - abstandbreit, linienoben / 2 - abstandlinie);
		jdcanfang.setBounds((int) (wviertel * 3 + achtel * 0.7), abstandlinie, drittel, linienoben / 2 - abstandlinie);

		// Rectangle b = jdcanfang.getBounds();
		int lhalb = (linienoben / 2);
		int posende = abstandlinie + lhalb;
		jdende.setBounds((int) (wviertel * 3 + achtel * 0.7), posende, drittel, linienoben / 2 - abstandlinie);
		messestandarterzeugen.setBounds((int) (wviertel * 3 + achtel * 0.65 + drittel + abstandbreit), abstandlinie,
				rest / 2 - abstandbreit, linienoben - abstandlinie);
		// btnStandart.setBounds((int) (wviertel * 3 + achtel * 0.7 + drittel + (0.5) *
		// abstandbreit + rest / 2),
		// abstandlinie, (int) (rest / 2 - (0.5) * abstandbreit), linienoben -
		// abstandlinie);
		// f = new Font(lblBis.getFont().getName(), Font.PLAIN, width / 50);
		setTextEqualsToSize(getContentPane());
		Utilities.setFrameMittig(width, heigth);
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "-------------------------");
	}

	private void setTextEqualsToSize(Component c) {
		if (c instanceof JPanel) {
			for (Component c2 : ((JPanel) c).getComponents()) {
				setTextEqualsToSize(c2);
			}
		} else if (c instanceof JLabel) {
			int h = c.getHeight();
			int font = h / 2;
			System.out.println(font + " " + ((JLabel) c).getText());
			c.getFont().deriveFont(Font.PLAIN, font);

		} else if (c instanceof JButton) {
			int h = c.getHeight();
			int font = h / 2;
			System.out.println(font + " " + ((JButton) c).getText());
			c.getFont().deriveFont(Font.PLAIN, font);
		} else if (c instanceof JScrollPane) {
			setTextEqualsToSize(((JScrollPane) c).getColumnHeader());
		} else if (c instanceof JViewport) {
			int h = c.getHeight();
			int font = h / 2;
			System.out.println(font + " " + ((JViewport) c).getName());
			c.getFont().deriveFont(Font.PLAIN, font);
		}

	}

	public void setzeinPanel(JPanel pane) {

	}
}
