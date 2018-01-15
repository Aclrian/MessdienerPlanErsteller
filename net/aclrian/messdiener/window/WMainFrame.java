package net.aclrian.messdiener.window;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.utils.Util;
import net.aclrian.messdiener.window.medierstellen.WWAnvertrauteFrame;
import net.aclrian.messdiener.window.medierstellen.WWFrame;
import net.aclrian.messdiener.window.planerstellen.WWMessenFrame;
import net.aclrian.messdiener.window.planerstellen.WWMessenHinzufuegen;

/**
 * Hiermit startet alles: Es wird eine Benutzeroberfläche zum Erstellen eines
 * Messdiender und eiunes Messdienerplans erzeugt
 * 
 * @author Aclrian
 *
 */
public class WMainFrame extends JFrame {

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2278649234963113093L;
	private JPanel cp;
	/**
	 * Hier werden alle Messdiener aus {@link WMainFrame#savepath} gespeichert!
	 */
	private ArrayList<Messdiener> mediarray;
	private ArrayList<Messe> mesenarray;
	private Util util;

	public Util getUtil() {
		return util;
	}

	private String savepath = "";
	private WWMessenFrame mf;
	private WWFrame wf;
	private ArrayList<Messdiener> mediarraymitMessdaten;
	private Date[] d = new Date[2];

	/*
	 ** Hiermit sollen Konsolenausgaben gemacht werden (Alternative zu
	 * System.out.println();) / public static final Logger log =
	 * Logger.getLogger("net.aclrian.messdiener.window" +
	 * WMainFrame.class.getName());
	 */
	/**
	 * Konstruktor
	 */
	public WMainFrame() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 481);
		setTitle("Messdiener Kaarst");
		this.cp = new JPanel();
		this.cp.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.cp);

		JButton btnMessdienrer = new JButton("<html>Messdierner-<br />verwaltung</html>");
		btnMessdienrer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				beabeiten();
			}
		});

		JButton btnNeuerPlan = new JButton("neuer Plan");
		btnNeuerPlan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				neuerPlan();
			}

		});

		JLabel lblWarnung = new JLabel(
				"<html>Bevor man einen Messdiener loescht, indem man seine Datei loescht,<br />sollte man dafuer sorgen, dass seine Freunde und Geschwister nicht mehr seine Freunde und Geschwister sind!</html>\n ");
		GroupLayout gl_cp = new GroupLayout(cp);
		gl_cp.setHorizontalGroup(gl_cp.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_cp.createSequentialGroup().addContainerGap()
						.addGroup(gl_cp.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_cp.createSequentialGroup().addComponent(btnMessdienrer)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnNeuerPlan, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
								.addComponent(lblWarnung))
						.addContainerGap()));
		gl_cp.setVerticalGroup(gl_cp.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
				gl_cp.createSequentialGroup().addContainerGap()
						.addGroup(gl_cp.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnNeuerPlan, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnMessdienrer, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 211,
										Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblWarnung)
						.addContainerGap(191, Short.MAX_VALUE)));
		cp.setLayout(gl_cp);

		/*
		 * //Sachen für den Logger log.setLevel(Level.ALL); FileHandler txt = null; try
		 * { txt = new FileHandler("log_messdienerplanersteller.txt", true); } catch
		 * (SecurityException | IOException e) { e.printStackTrace(); }
		 * txt.setFormatter(new Formatter() {
		 * 
		 * @Override public String format(LogRecord record) { String rtn =
		 * this.formatMessage(record); SimpleDateFormat df = new
		 * SimpleDateFormat("YYYY:MM:DD HH:mm:ss"); Date d = new
		 * Date(record.getMillis()); String eins = df.format(d); String zwei =
		 * record.getLevel().getName(); return "[" +eins +zwei + "]" + rtn +"\n\r"; }
		 * }); log.addHandler(txt);
		 */
	}

	public void beabeiten() {
		try {
			if (util.getSavepath().equals("")) {
				util = new Util();
			}
		} catch (NullPointerException e) {
			util = new Util();
		}
		savepath = util.getSavepath();
		mediarray = util.getAlleMedisVomOrdnerAlsList(savepath);
		wf = new WWFrame(this);
		wf.setVisible(true);
	}

	public void neuerPlan() {
		try {
			if (util.getSavepath().equals("")) {
				util = new Util();
			}
		} catch (NullPointerException e) {
			util = new Util();
		}
		savepath = util.getSavepath();
		mediarray = util.getAlleMedisVomOrdnerAlsList(savepath);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(util.getSavepath(), year, this);
		}
		this.mediarraymitMessdaten = mediarray;
		mf = new WWMessenFrame(this);
		mf.setVisible(true);
	}

	/**
	 * Hiermit werden alle Medis aus {@link WMainFrame#mediarray} ausgegeben!
	 * 
	 * @return
	 */
	public ArrayList<Messdiener> getAlleMessdiener() {
		System.out.println(mediarray.size());
		return mediarray;
	}

	public void erneuern() {
		mediarray = util.getAlleMedisVomOrdnerAlsList(util.getSavepath());
	}
	
	/**
	 * Hier mit startet alles!
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// log.setLevel(Level.ALL);
					// log.warning("Alles startet!");
					WMainFrame frame = new WMainFrame();
					frame.setVisible(true);
					//frame.setUtil(new Util("N:\\.aaa_aproo\\zzzTESTgit\\_3GIT_\\XML"));
					// "/mnt/DRIVE-N-GO/.aaa_aproo/zzzTESTgit/_3GIT_/XML"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setUtil(Util util2) {
		this.util = util2;
	}

	public ArrayList<Messdiener> getMediarray() {
		return mediarray;
	}

	public void setMediarray(ArrayList<Messdiener> mediarray) {
		this.mediarray = mediarray;
	}

	public ArrayList<Messe> getMesenarray() {
		return mesenarray;
	}

	public void setMesenarray(ArrayList<Messe> mesenarray) {
		this.mesenarray = mesenarray;
	}

	public void berechen() {
		this.d = mf.berechnen(this);
		WWMessenHinzufuegen wwmh = new WWMessenHinzufuegen(d[0], d[1], this);
		wwmh.setVisible(true);
	}

	/**
	 * DEBUG
	 * 
	 * @param anfang
	 * @param ende
	 */
	public void berechnen(Date anfang, Date ende) {
		savepath = util.getSavepath();
		mediarray = util.getAlleMedisVomOrdnerAlsList(savepath);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(util.getSavepath(), year, this);
		}
		this.mediarraymitMessdaten = mediarray;
		WWMessenHinzufuegen wwmh = new WWMessenHinzufuegen(anfang, ende, this);
		wwmh.setVisible(true);
	}

	public void getGeschwister() {
		if (wf.getGeschwister(this)) {
			WWAnvertrauteFrame af = new WWAnvertrauteFrame(this, wf.getMoben(), wf);
			af.setVisible(true);
		}

	}

	public ArrayList<Messdiener> getMediarraymitMessdaten() {
		return mediarraymitMessdaten;
	}

	public Date[] getDates() {
		return d;
	}

}