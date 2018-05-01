package net.aclrian.messdiener.window;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.differenzierung.WriteFile_Pfarrei;
import net.aclrian.messdiener.utils.DateienVerwalter;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
//import net.aclrian.messdiener.window.console.Console;
import net.aclrian.messdiener.window.medierstellen.WMediBearbeitenFrame;
import net.aclrian.messdiener.window.medierstellen.WWAnvertrauteFrame;
import net.aclrian.messdiener.window.planerstellen.WMessenHinzufuegen;
import net.aclrian.messdiener.window.planerstellen.WWMessenFrame;
import net.aclrian.update.VersionIDHandler;

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
	public static final String textdatei = "//.messdienerOrdnerPfad.txt";
	public static final String VersionID = "b591";
	// public static final int Max_einteilen = 3;
	public static String pfarredateiendung = ".xml.pfarrei";
	private Sonstiges sonstiges = new Sonstiges();
	private JPanel cp;
	/**
	 * Hier werden alle Messdiener aus {@link WMainFrame#savepath} gespeichert!
	 */
	private ArrayList<Messdiener> mediarray;
	private ArrayList<Messe> mesenarray;
	private DateienVerwalter util;
	private String savepath = "";
	private WWMessenFrame mf;
	private WMediBearbeitenFrame wf;
	private ArrayList<Messdiener> mediarraymitMessdaten;
	private Date[] d = new Date[2];
	private Pfarrei pf;
	private ArrayList<StandartMesse> standardmessen = new ArrayList<StandartMesse>();
	//private Console c;
//	private JCheckBox chckbxZeigeKonsole;
	//private boolean cc = false;
	/**
	 * Hier mit startet alles!
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Runnable r2 = new Runnable() {
			
			@Override
			public void run() {
				VersionIDHandler vh  = new VersionIDHandler();
				vh.act();
			}
		};
		Thread t = new Thread(r2);
		r2.run();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					// log.setLevel(Level.ALL);
					// log.warning("Alles startet!");
					t.start();
					System.out.println(".");
			/*	//	boolean da= false;
					for (String string : args) {
						if (string.equals("net.aclrian.debug:true")) {
							da=true;
							//frame.c.changeVisibility();
							//frame.chckbxZeigeKonsole.setSelected(true);
						}
					}*/
					//System.out.println(da);
					WMainFrame frame = new WMainFrame();//da);
					t.interrupt();
				/*	for (String string : args) {
						if (string.equals("net.aclrian.debug:true")) {
							//frame.c.changeVisibility();
						//	frame.chckbxZeigeKonsole.setSelected(true);
						}
					}*/
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
					frame.setAlwaysOnTop(false);
					/*WMainFrame frame = new WMainFrame();
					frame.setVisible(true);*/
					// frame.setUtil(new Util("N:\\.aaa_aproo\\zzzTESTgit\\_3GIT_\\XML"));
					// "/mnt/DRIVE-N-GO/.aaa_aproo/zzzTESTgit/_3GIT_/XML"));
				} catch (Exception e) {
					e.printStackTrace();
				}
		}};
		r.run();
	}

	/*
	 ** Hiermit sollen vor langer Zeit mal Konsolenausgaben gemacht werden (Alternative zu
	 * System.out.println();) / public static final Logger log =
	 * Logger.getLogger("net.aclrian.messdiener.window" +
	 * WMainFrame.class.getName());
	 */
	/**
	 * Konstruktor
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @wbp.parser.constructor
	 */
	public WMainFrame(){
		Utilities.logging(this.getClass(), this.getClass().getEnclosingConstructor(), "MpE startet...");
		start();
		util = new DateienVerwalter();
		erneuern();
		//if (!b) {
	//		c = new Console();
		//}
	//	c = new Console();
		}

	public void start() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		double weite = 625;
		double hoehe = 240;
		setBounds(Utilities.setFrameMittig(weite, hoehe));
		setTitle("Messdiener Kaarst");
		this.setIconImage(getIcon(this));
		cp = new JPanel();
		cp.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(cp);

		//
		
		// util.getSpeicherort();

		//
		JButton btnMessdienrer = new JButton("<html>Messdierner-<br />verwaltung</html>");
		btnMessdienrer.setBounds(15, 17, 204, 124);
		btnMessdienrer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				beabeiten();
			}
		});

		JButton btnNeuerPlan = new JButton("neuer Plan");
		btnNeuerPlan.setBounds(388, 17, 204, 124);
		btnNeuerPlan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				neuerPlan();
			}

		});

		JLabel lblWarnung = new JLabel(
				"<html>Bevor man einen Messdiener loescht, indem man seine Datei loescht,<br />sollte man dafuer sorgen, dass seine Freunde und Geschwister nicht mehr seine Freunde und Geschwister sind!</html>\n ");
		lblWarnung.setBounds(18, 153, 574, 45);
		cp.setLayout(null);
		cp.add(btnMessdienrer);
		cp.add(btnNeuerPlan);
		cp.add(lblWarnung);

		JButton btnSpeicherortndern = new JButton("<html>Speicherort<br />\u00E4ndern</html>");
		btnSpeicherortndern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				speicherOrt();
			}
		});
		btnSpeicherortndern.setBounds(229, 17, 139, 94);
		cp.add(btnSpeicherortndern);
		
		//chckbxZeigeKonsole = new JCheckBox("Zeige Konsole");
		/*chckbxZeigeKonsole.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (b) {
					return;
				}
				if (cc) {
					//c = new Console(this);
					return;
				}
		//		c.changeVisibility();
				Utilities.logging(this.getClass(), "init", "Konsolen-Sichtbarkeit wurde ge�ndert.");
			}
		});
		/*
		 * */
	//	chckbxZeigeKonsole.setBounds(234, 118, 134, 23);
	//	cp.add(chckbxZeigeKonsole);

		
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
	
	public WMainFrame(String s) {
		start();
		util = new DateienVerwalter(s);
		erneuern();
		Utilities.logging(this.getClass(), "init", "Es wurden " + mediarray.size() + " gefunden!");
		
	}

	public void beabeiten() {
		try {
			if (util.getSavepath().equals("")) {
				util = new DateienVerwalter();
			}
		} catch (NullPointerException e) {
			util = new DateienVerwalter();
		}
		savepath = util.getSavepath();
		//mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, this);
		wf = new WMediBearbeitenFrame(this);
		wf.setVisible(true);
	}

	public void berechen() {
		d = mf.berechnen(this);
		if (d[0] != null && d[1] != null) {
			WMessenHinzufuegen wwmh = new WMessenHinzufuegen(d[0], d[1], this);
			wwmh.setVisible(true);
		} else {
			new Erroropener("Bitte Daten eingeben!");
		}
	}

	/**
	 * DEBUG
	 *
	 * @param anfang
	 * @param ende
	 */
	public void berechnen(Date anfang, Date ende) {
		savepath = util.getSavepath();
		//mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, this);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(util.getSavepath(), year, this);
		}
		mediarraymitMessdaten = mediarray;
		WMessenHinzufuegen wwmh = new WMessenHinzufuegen(anfang, ende, this);
		wwmh.setVisible(true);
	}

	public void erneuern() {
		savepath = util.getSavepath();
		try {
			pf = util.getPfarrei(this);
			setTitle(pf.getName());
			} catch (Exception e) {
				WriteFile_Pfarrei wfp = new WriteFile_Pfarrei(this);
				setContentPane(wfp.getContentPane());
				setBounds(Utilities.setFrameMittig(wfp.getBounds().width, wfp.getBounds().height));
				return;
			}
		standardmessen.clear();
		standardmessen.add(sonstiges);
		boolean hatsonstiges = false;
		System.out.println("------------------------" + pf.getStandardMessen().size());
		for (StandartMesse sm : pf.getStandardMessen()) {
			//if(sonstiges.isSonstiges(sm)) {hatsonstiges = true;}
			//else{
			System.out.println(sm);
				standardmessen.add(sm);
				//}
		}
		mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, this);
		
		if (!hatsonstiges) {
			pf.getStandardMessen().add(sonstiges);
		}
		Utilities.logging(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName(), "Es wurden " + mediarray.size() + " gefunden!");
	}

	/**
	 * Hiermit werden alle Medis aus {@link WMainFrame#mediarray} ausgegeben!
	 *
	 * @return
	 */
	public ArrayList<Messdiener> getAlleMessdiener() {
		return mediarray;
	}

	public Date[] getDates() {
		return d;
	}

	public DateienVerwalter getEDVVerwalter() {
		return util;
	}

	public void getGeschwister() {
		if (wf.getGeschwister(this)) {
			WWAnvertrauteFrame af = new WWAnvertrauteFrame(this, wf.getMoben(), wf);
			af.setVisible(true);
		}

	}

	public ArrayList<Messdiener> getMediarray() {
		return mediarray;
	}

	public ArrayList<Messdiener> getMediarraymitMessdaten() {
		return mediarraymitMessdaten;
	}

	public ArrayList<Messe> getMesenarray() {
		return mesenarray;
	}

	public void neuerPlan() {
		try {
			if (util.getSavepath().equals("")) {
				util = new DateienVerwalter();
			}
		} catch (NullPointerException e) {
			util = new DateienVerwalter();
		}
		savepath = util.getSavepath();
		mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, this);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(util.getSavepath(), year, this);
		}
		mediarraymitMessdaten = mediarray;
		mf = new WWMessenFrame(this);
		mf.setVisible(true);
	}

	public void setMediarray(ArrayList<Messdiener> mediarray) {
		this.mediarray = mediarray;
	}

	public void setMesenarray(ArrayList<Messe> mesenarray) {
		this.mesenarray = mesenarray;
	}

	public void setUtil(DateienVerwalter util2) {
		util = util2;
	}

	public void speicherOrt() {
		String s = System.getProperty("user.home");
		s += textdatei;
		File f = new File(s);
		f.delete();
		util.getSpeicherort();
		savepath = util.getSavepath();
		erneuern();
	}

	public Pfarrei getPfarrei() {
		if (pf == null) {
			pf = util.getPfarrei(this);
		}
		return pf;
	}

	public Sonstiges getSonstiges() {
		return sonstiges;
	}

	public ArrayList<StandartMesse> getStandardmessen() {
		return standardmessen;
	}

	public void fertig(WriteFile_Pfarrei wfp, Pfarrei pf, String savepath) {
		this.pf = pf;
		util = new DateienVerwalter(savepath);
		erneuern();
		setContentPane(cp);
	}
	
	public ArrayList<StandartMesse> getSMoheSonstiges() {
		ArrayList<StandartMesse> sm = new ArrayList<StandartMesse>();
		sm.addAll(standardmessen);
		for (int i = 0; i < sm.size(); i++) {
			if (sonstiges.isSonstiges(sm.get(i))) {
				sm.remove(i);
			}
		}
		sm.sort(StandartMesse.comfuerSMs);
		return sm;
	}

	/*public void consoleClose() {
		this.cc=true;
	//	this.chckbxZeigeKonsole.setSelected(false);
		this.cc =false;
	}*/
	
	
	public static Image getIcon(WMainFrame wmf) {
		return Toolkit.getDefaultToolkit().getImage(wmf.getClass().getResource("title.png"));
	}
	
	@Override
	public void setVisible(boolean arg0) {
		
		super.setVisible(arg0);
		if (arg0) {
			setAlwaysOnTop(true);
			setAlwaysOnTop(false);
		}
		
	}

}