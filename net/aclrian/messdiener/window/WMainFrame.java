package net.aclrian.messdiener.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.deafault.Sonstiges;
import net.aclrian.messdiener.deafault.StandartMesse;
import net.aclrian.messdiener.differenzierung.Manuell.EnumWorking;
import net.aclrian.messdiener.differenzierung.Manuell.Handling;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.differenzierung.Manuell;
import net.aclrian.messdiener.differenzierung.Pfarrei;
import net.aclrian.messdiener.differenzierung.WriteFile_Pfarrei;
import net.aclrian.messdiener.newy.progress.AProgress;
import net.aclrian.messdiener.utils.DateienVerwalter;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
//import net.aclrian.messdiener.window.console.Console;
import net.aclrian.messdiener.window.medierstellen.WMediBearbeitenFrame;
import net.aclrian.messdiener.window.medierstellen.WWAnvertrauteFrame;
import net.aclrian.messdiener.window.planerstellen.WMessenHinzufuegen;
import net.aclrian.messdiener.window.planerstellen.WMessenFrame;
import net.aclrian.update.VersionIDHandler;

/**
 * Hiermit startet alles: Es wird eine Benutzeroberflaeche zum Erstellen eines
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
	public static final String VersionID = "b598";
	// public static final int Max_einteilen = 3;
	public static String pfarredateiendung = ".xml.pfarrei";
	private Sonstiges sonstiges = new Sonstiges();
	private JPanel cp;
	
	private WMessenFrame mf;
	private WMediBearbeitenFrame wf;
	/**
	 * Hier werden alle Messdiener aus {@link WMainFrame#savepath} gespeichert!
	 */
	private ArrayList<Messdiener> mediarray;
	private ArrayList<Messe> mesenarray;
	private DateienVerwalter util;
	private String savepath = "";
	private ArrayList<Messdiener> mediarraymitMessdaten;
	private Date[] d = new Date[2];
	private Pfarrei pf;
	private ArrayList<StandartMesse> standardmessen = new ArrayList<StandartMesse>();
	private AProgress ap = new AProgress();
	// private Console c;
	// private JCheckBox chckbxZeigeKonsole;
	// private boolean cc = false;
	/**
	 * Hier mit startet alles!
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Runnable r2 = new Runnable() {

			@Override
			public void run() {
				VersionIDHandler vh = new VersionIDHandler();
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
					/*
					 * // boolean da= false; for (String string : args) { if
					 * (string.equals("net.aclrian.debug:true")) { da=true;
					 * //frame.c.changeVisibility();
					 * //frame.chckbxZeigeKonsole.setSelected(true); } }
					 */
					// System.out.println(da);
					WMainFrame frame = new WMainFrame();// da);
					t.interrupt();
					/*
					 * for (String string : args) { if
					 * (string.equals("net.aclrian.debug:true")) {
					 * //frame.c.changeVisibility(); //
					 * frame.chckbxZeigeKonsole.setSelected(true); } }
					 */
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
					frame.setAlwaysOnTop(false);
					/*
					 * WMainFrame frame = new WMainFrame();
					 * frame.setVisible(true);
					 */
				} catch (Exception e) {
					new Erroropener(e.getMessage());
					e.printStackTrace();
				}
			}
		};
		r.run();
	}

	/*
	 ** Hiermit sollen vor langer Zeit mal Konsolenausgaben gemacht werden
	 * (Alternative zu System.out.println();) / public static final Logger log =
	 * Logger.getLogger("net.aclrian.messdiener.window" +
	 * WMainFrame.class.getName());
	 */
	/**
	 * Konstruktor
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @wbp.parser.constructor
	 */
	public WMainFrame() {
		Utilities.logging(this.getClass(), this.getClass().getEnclosingConstructor(), "MpE startet...");
		start();
		util = new DateienVerwalter();
		erneuern();
		// if (!b) {
		// c = new Console();
		// }
		// c = new Console();
	}

	public void start() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		double weite = 625;
		double hoehe = 240;
		setBounds(Utilities.setFrameMittig(weite, hoehe));
		setTitle("Messdiener Kaarst");
		this.setIconImage(References.getIcon());
		cp = new JPanel();
		cp.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(cp);
		
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

		JLabel lblWarnung = new JLabel("<html>Bevor man einen Messdiener l" + References.oe
				+ "scht, indem man seine Datei l"+References.oe+"scht,<br />sollte man dafuer sorgen, dass seine Freunde und Geschwister nicht mehr seine Freunde und Geschwister sind!</html>\n ");
		lblWarnung.setBounds(18, 153, 574, 45);
		cp.setLayout(null);
		cp.add(btnMessdienrer);
		cp.add(btnNeuerPlan);
		cp.add(lblWarnung);

		JButton btnSpeicherortndern = new JButton("<html>Speicherort<br />" + References.ae + "ndern</html>");
		btnSpeicherortndern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				speicherOrt();
			}
		});
		btnSpeicherortndern.setBounds(229, 17, 139, 56);
		cp.add(btnSpeicherortndern);

		JButton btnpfarreindern = new JButton("<html>Pfarrei<br/>\u00E4ndern</html>");
		btnpfarreindern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				pfarreibearbeiten();
			}
		});
		btnpfarreindern.setBounds(229, 84, 139, 56);
		cp.add(btnpfarreindern);

		// chckbxZeigeKonsole = new JCheckBox("Zeige Konsole");
		/*
		 * chckbxZeigeKonsole.addItemListener(new ItemListener() {
		 * 
		 * @Override public void itemStateChanged(ItemEvent e) { if (b) {
		 * return; } if (cc) { //c = new Console(this); return; } //
		 * c.changeVisibility(); Utilities.logging(this.getClass(), "init",
		 * "Konsolen-Sichtbarkeit wurde geï¿½ndert."); } }); /*
		 */
		// chckbxZeigeKonsole.setBounds(234, 118, 134, 23);
		// cp.add(chckbxZeigeKonsole);
	}

	public void pfarreibearbeiten() {
		WriteFile_Pfarrei wf_pf = new WriteFile_Pfarrei(pf, this);
		this.setVisible(false);
		wf_pf.setVisible(true);
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
			// new Erroropener("no error");
			util = new DateienVerwalter();
		}
		savepath = util.getSavepath();
		// mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, this);
		wf = new WMediBearbeitenFrame(ap);
		wf.setVisible(true);
	}

	public void berechen() {
		d = mf.berechnen();
		if (d[0] != null && d[1] != null) {
			WMessenHinzufuegen wwmh = new WMessenHinzufuegen(d[0], d[1], ap);
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
		// mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, this);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(util.getSavepath(), year, ap.getAda());
		}
		mediarraymitMessdaten = mediarray;
		WMessenHinzufuegen wwmh = new WMessenHinzufuegen(anfang, ende, ap);
		wwmh.setVisible(true);
	}

	public void erneuern() {
		savepath = util.getSavepath();
		try {
			pf = util.getPfarrei();
			setTitle(pf.getName());
		} catch (Exception e) {
			// new Erroropener("no error");
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
			// if(sonstiges.isSonstiges(sm)) {hatsonstiges = true;}
			// else{
			System.out.println(sm);
			standardmessen.add(sm);
			// }
		}
		mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, ap.getAda());

		if (!hatsonstiges) {
			pf.getStandardMessen().add(sonstiges);
		}
		Utilities.logging(this.getClass(), new Object() {
		}.getClass().getEnclosingMethod().getName(), "Es wurden " + mediarray.size() + " gefunden!");
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
		if (wf.getGeschwister()) {
			WWAnvertrauteFrame af = new WWAnvertrauteFrame(ap.getAda(), wf.getMoben(), wf);
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
			// new Erroropener("no error");
			util = new DateienVerwalter();
		}
		savepath = util.getSavepath();
		mediarray = util.getAlleMedisVomOrdnerAlsList(savepath, ap.getAda());
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (Messdiener messdiener : mediarray) {
			messdiener.setnewMessdatenDaten(util.getSavepath(), year, ap.getAda());
		}
		mediarraymitMessdaten = mediarray;
		mf = new WMessenFrame(ap);
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
		util.erneuereSavepath();
		util.getSpeicherort();
		savepath = util.getSavepath();
		erneuern();
	}

	public Pfarrei getPfarrei() {
		if (pf == null) {
			pf = util.getPfarrei();
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
		this.pf = util.getPfarrei();
		if (!this.pf.getStandardMessen().toString().equals(pf.getStandardMessen().toString())) {
			ArrayList<StandartMesse> beibehalten = new ArrayList<StandartMesse>();
			ArrayList<StandartMesse> verbleibende = new ArrayList<StandartMesse>();
			for (StandartMesse sm : pf.getStandardMessen()) {
				boolean b = true;
				for (StandartMesse smold : this.pf.getStandardMessen()) {
					if (sm.toString().equals(smold.toString())) {
						beibehalten.add(smold);
						b = false;
						break;
					}
				}
				if (b) {
					verbleibende.add(sm);
				}

			}
			ArrayList<StandartMesse> alte = new ArrayList<StandartMesse>();
			ArrayList<String> alteS = new ArrayList<String>();
			for (StandartMesse alt : this.pf.getStandardMessen()) {
				boolean b = false;
				for (StandartMesse sm : beibehalten) {
					
					if (sm.toString().equals(alt.toString())) {
						b = true;
						break;
					}
				}
				if (!b) {
					alte.add(alt);
					alteS.add("Überarbeitete " + alt.toString());
				}

			}
			ArrayList<Handling> hand = new ArrayList<Handling>();
			alteS.add("Neu");
			for (StandartMesse sm : verbleibende) {
				boolean fertig = false;
				do {
					
					Object[] possibilities = new Object[0];
					possibilities = alteS.toArray(possibilities);
					String s = (String) JOptionPane.showInputDialog(null,
							sm.toString() + " ist neu.\nIst sie wirkilch neu oder wurde sie nur überarbeitet?",
							"Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, possibilities, "Neu");

					// If a string was returned, say so.
					if ((s != null) && (s.length() > 0)) {
						fertig = true;
						EnumWorking ew;
						StandartMesse alt = null;
						if (s.equals("Neu")) {
							alt = null;
							ew = EnumWorking.neu;
						} else {
							for (StandartMesse sm1 : alte) {
								if (s.endsWith(sm1.toString())) {
									 remove(alte,sm1);
									ArrayList<String> aS = new ArrayList<String>();
									for (String string : alteS) {
										if (!string.equals(s)) {
											aS.add(string);
										}
									}
									possibilities = aS.toArray(possibilities);
									
									alt = sm1;
									break;
								}
							}
							if (alt == null) {
								fertig = false;
							}
							ew = EnumWorking.ueberarbeitet;
						}
						Handling h = new Handling(sm, ew, alt);
						hand.add(h);
					}
				} while (fertig != true);
			}
			ArrayList<StandartMesse> bleibende = new ArrayList<StandartMesse>();
			bleibende.addAll(beibehalten);
			standardmessen = pf.getStandardMessen();
			Manuell m = new Manuell(hand, ap.getAda(), pf);
			m.act();
			WriteFile_Pfarrei.writeFile(pf, savepath);
			JOptionPane.showInputDialog("Das Programm wird nun beendet und mit den neuen Daten gestartet!");
			System.exit(0);
		} else {
			String name = pf.getName();
			name = name.replaceAll(" ", "_");
			File f = new File(savepath + "\\" + name + WMainFrame.pfarredateiendung);
			f.delete();
			this.pf = pf;
			WriteFile_Pfarrei.writeFile(pf, savepath);
		}
		util = new DateienVerwalter(savepath);
		erneuern();
	}

	private void remove(ArrayList<StandartMesse> smarray, StandartMesse sm) {
		for (int i = 0; i<smarray.size(); i++) {
			StandartMesse messe = smarray.get(i);
			if (messe.toString().equals(sm.toString())) {
				smarray.remove(i);
			}
		}
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

	/*
	 * public void consoleClose() { this.cc=true; //
	 * this.chckbxZeigeKonsole.setSelected(false); this.cc =false; }
	 */

	public boolean isEqual(ArrayList<StandartMesse> sm1, ArrayList<StandartMesse> sm2) {
		Predicate<StandartMesse> p = new Predicate<StandartMesse>() {
			@Override
			public boolean test(StandartMesse t) {
				return new Sonstiges().isSonstiges(t);
			}
		};
		sm1.removeIf(p);
		sm2.removeIf(p);
		if (sm1.size() == sm2.size()) {
			for (StandartMesse standartMesse : sm1) {
				boolean b = false;
				for (StandartMesse standartMesse2 : sm2) {
					if (standartMesse.toString().equals(standartMesse2.toString())) {
						b = true;
						break;
					}
				}
				if (!b) {
					return false;
				}
			}
			for (StandartMesse standartMesse : sm2) {
				boolean b = false;
				for (StandartMesse standartMesse2 : sm1) {
					if (standartMesse.toString().equals(standartMesse2.toString())) {
						b = true;
						break;
					}
				}
				if (!b) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
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