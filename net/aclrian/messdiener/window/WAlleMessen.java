package net.aclrian.messdiener.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.util.Calendar;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import net.aclrian.messdiener.panels.MediAnzeigen;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.References;
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
	private MediAnzeigen panel;
	private JDateChooser jdcanfang = new JDateChooser(Calendar.getInstance().getTime());
	private JDateChooser jdende;
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

	private JButton btnStandart = new JButton("");
	//
	final Color hell0 = new Color(20, 96, 88);
	final Color hell1 = new Color(71, 142, 134);
	final Color standart = new Color(39, 116, 108);
	final Color dunkel1 = new Color(4, 72, 65);
	final Color dunkel2 = new Color(0, 46, 41, 1);
	//private Font f;
	private Calendar cal;

	/**
	 * Create the frame.
	 */
	public WAlleMessen() {
		// setBounds(new Rectangle(0, 0, 1600, 890));
		// setResizable(false);
		int width = 1600;// Toolkit.getDefaultToolkit().getScreenSize().width;
		int heigth = 890;// Toolkit.getDefaultToolkit().getScreenSize().height;
		// setBounds(0,0,533,534);
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
		effectiveScreenArea.width = bounds.width - screenInsets.left - screenInsets.right;
		this.setBounds(effectiveScreenArea);
		// setBounds(0, 0, 591, 570);
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		jdende = new JDateChooser(cal.getTime());
		listmesse.setModel(dfmesse);
		listmedi.setModel(dfmedi);
		// setVisible(true);
		setBounds(0, 0, width, heigth);
		int wviertel = width / 4;
		int linieunten = heigth / 10;
		//int linienoben = linieunten * (1 + 1 / 3);// 1+1/3 Abstand wie bei linieunten
		int abstandlinie = heigth / 100;
		int abstandbreit = width / 100;
		//int abstandindreibuttons = (wviertel - abstandlinie) / 3;
		//int breite = wviertel / 3;
		@SuppressWarnings("unused")
		int panelWith = 2 * wviertel - 2 * abstandlinie;
		@SuppressWarnings("unused")
		int panelHight = heigth - abstandbreit - linieunten;
		panel = new MediAnzeigen("leer");
		graphics(width, heigth);
		// pack();
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
				WMediBearbeitenFrame wmbf = new WMediBearbeitenFrame(new WMainFrame());//new WMainFrame(false));
				wmbf.setVisible(true);
				// setzeinPanel(wmbf.getPanel());
			}
		});
		medipanel.add(plusmedi);
		medipanel.add(minusmedi);
		medipanel.add(augemedi);
		augemedi.setIcon(new ImageIcon(WAlleMessen.class.getResource("/net/aclrian/messdiener/window/auge.png")));
		augemedi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		// abstandbreit, abstandlinie, wviertel-2*abstandlinie,
		// heigth-abstandbreit-linieunten);
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
		augemesse.setIcon(new ImageIcon(WAlleMessen.class.getResource("/auge.png")));
		btnEinstellungen.setIcon(new ImageIcon(WAlleMessen.class.getResource("/settings.png")));
		messepanel.add(augemesse);
		btnStandart.setIcon(new ImageIcon(WAlleMessen.class.getResource("/eigensacftenbearbeiten.png")));
		contentPane.add(btnStandart);
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
		farbe(getContentPane());
		// pack();: --> dosn't work
	}

	private void farbe(Component c) {
		/*
		 * if (c instanceof JPanel) { c.setBackground(standart); for (Component c2 :
		 * ((JPanel) c).getComponents()) { farbe(c2); } } else if (c instanceof JLabel)
		 * { c.setBackground(hell0); // c.setForeground(Color.WHITE); } else if (c
		 * instanceof JButton) { // c.setForeground(Color.WHITE);
		 * c.setBackground(hell0); // c.setForeground(Color.WHITE);
		 *
		 * } else if (c instanceof JScrollPane) { // lblAlleMesen.setForeground(hell0);
		 * lblAlleMesen.setBackground(dunkel2); //
		 * lblAlleMessdiener.setForeground(hell0);
		 * lblAlleMessdiener.setBackground(dunkel2); farbe(((JScrollPane)
		 * c).getColumnHeader()); } else if (c instanceof JViewport) {
		 * c.setBackground(hell0); // c.setBackground(dunkel2); //}
		 */
	}

	public int[] getFullscreen() {
		int[] rtn = new int[2];
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		rtn[0] = (int) width;
		rtn[1] = (int) height;
		return rtn;
	}

	private void graphics(int width, int heigth) {

		int wviertel = width / 4;
		int linieunten = heigth / 10;
		int linienoben = linieunten * (1 + 1 / 3);// 1+1/3 Abstand wie bei linieunten
		int abstandlinie = heigth / 100;
		int abstandbreit = width / 100;
		int abstandindreibuttons = (wviertel - abstandlinie) / 3;
		int breite = wviertel / 3;
		btnEinstellungen.setBounds((int) (2.5 * wviertel) - wviertel - (breite / 2), heigth - linieunten, breite,
				linieunten - 5 * abstandlinie);
		scrollPanemedi.setBounds(abstandbreit, abstandlinie, wviertel - 2 * abstandlinie,
				heigth - abstandbreit - linieunten);
		medipanel.setVisible(false);
		medipanel.setBounds(abstandbreit, heigth - linieunten - abstandlinie, wviertel - abstandlinie,
				linieunten - 3 * abstandlinie);
		medipanel.setVisible(true);

		panel.setBounds(wviertel + (abstandbreit / 2), abstandlinie, 2 * wviertel - 2 * abstandlinie,
				heigth - abstandbreit - linieunten);
		panel.setVisible(true);
		plusmedi.setBounds(abstandbreit - abstandindreibuttons / 10, abstandlinie,
				abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
		minusmedi.setBounds(abstandbreit - abstandindreibuttons / 10 + abstandindreibuttons, abstandlinie,
				abstandindreibuttons - abstandindreibuttons / 10, linieunten - 5 * abstandlinie);
		augemedi.setBounds(abstandbreit - abstandindreibuttons / 10 + (abstandindreibuttons * 2), abstandlinie,
				abstandindreibuttons - abstandindreibuttons / 10 - 1, linieunten - 5 * abstandlinie);
		messescrollPanel.setBounds(3 * wviertel, abstandlinie + linienoben, wviertel - 2 * abstandlinie,
				heigth - abstandbreit - linienoben - linienoben);
		btnPlanErstellen.setIcon(new ImageIcon(WAlleMessen.class.getResource("/messen_erstellen.png")));

		btnPlanErstellen.setBounds((int) (2.5 * wviertel) - (breite / 2), heigth - linieunten, breite,
				linieunten - 5 * abstandlinie);
		messepanel.setBounds(3 * wviertel, heigth - linieunten - abstandlinie, wviertel - 2 * abstandlinie,
				linieunten - 3 * abstandlinie);
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
		lblVon.setBounds(wviertel * 3, abstandlinie, achtel - abstandbreit, linienoben / 2 - abstandlinie);
		lblBis.setBounds(wviertel * 3, abstandlinie + linienoben / 2, achtel - abstandbreit,
				linienoben / 2 - abstandlinie);
		jdcanfang.setBounds(wviertel * 3 + achtel, abstandlinie, drittel, linienoben / 2 - abstandlinie);

		// Rectangle b = jdcanfang.getBounds();
		int lhalb = (linienoben / 2);
		int posende = abstandlinie + lhalb;
		jdende.setBounds(wviertel * 3 + achtel, posende, drittel, linienoben / 2 - abstandlinie);
		messestandarterzeugen.setBounds(wviertel * 3 + achtel + drittel + abstandbreit, abstandlinie,
				rest / 2 - abstandbreit, linienoben - abstandlinie);
		btnStandart.setBounds(wviertel * 3 + achtel + drittel + abstandbreit + rest / 2, abstandlinie,
				rest / 2 - abstandbreit, linienoben - abstandlinie);
		//f = new Font(lblBis.getFont().getName(), Font.PLAIN, width / 50);
		setTextEqualsToSize(getContentPane());
		Utilities.setFrameMittig(width, heigth);
		Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"-------------------------");
	}

	private void setTextEqualsToSize(Component c) {
		if (c instanceof JPanel) {
			for (Component c2 : ((JPanel) c).getComponents()) {
				setTextEqualsToSize(c2);
			}
		} else if (c instanceof JLabel) {
			int h = c.getHeight();
			int font = h / 2;
			System.out.println(font + " " + c.getName());
			c.getFont().deriveFont(Font.PLAIN, font);

		} else if (c instanceof JButton) {
			int h = c.getHeight();
			int font = h / 2;
			System.out.println(font + " " + c.getName());
			c.getFont().deriveFont(Font.PLAIN, font);
		} else if (c instanceof JScrollPane) {
			setTextEqualsToSize(((JScrollPane) c).getColumnHeader());
		} else if (c instanceof JViewport) {
			int h = c.getHeight();
			int font = h / 2;
			System.out.println(font + " " + c.getName());
			c.getFont().deriveFont(Font.PLAIN, font);
		}

	}

	public void setzeinPanel(JPanel pane) {

	}
}
