package net.aclrian.messdiener.window.medierstellen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.pictures.References;
import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.utils.WriteFile;
import net.aclrian.messdiener.window.WMainFrame;

/**
 * Graphische Oberflaeche, mit man Messdiener Freunde und Geschwister zuweisen
 * kann
 * 
 * @author Aclrian
 *
 */
public class WWAnvertrauteFrame extends JFrame {
	/**
	 * Hier sind alle Komponenten aufgefuehrt, die es fuer das Graphische benoetigt
	 */
	JButton btnAbbrechen = new JButton("Abbrechen");
	JList<String> list = new JList<String>();
	JScrollPane scrollPane = new JScrollPane();
	DefaultListModel<String> listmodel = new DefaultListModel<String>();
	JScrollPane scrollPane_1 = new JScrollPane();
	JList<String> friedlist = new JList<String>();
	DefaultListModel<String> geschwiemodel = new DefaultListModel<String>();
	DefaultListModel<String> friendmodel = new DefaultListModel<String>();
	JScrollPane scrollPane_2 = new JScrollPane();
	JList<String> geschwielist = new JList<String>();
	JButton btnNachUnten = new JButton(References.pfeilrunter);
	JButton button = new JButton(References.pfeillinks);
	JButton button_1 = new JButton(References.pfeilrechts);
	JLabel lblGeschwister = new JLabel("Geschwister");
	JLabel lblFreunde = new JLabel("Freunde");
	JLabel lblAlleMessdiener = new JLabel("Alle Messdiener");
	JButton btnSave = new JButton("Speichern");
	/**
	 * Array an Messdienern von denen man auswaehlen kann
	 */
	ArrayList<Messdiener> hauptarray = new ArrayList<Messdiener>();
	/**
	 * Array an Messdienern die Geschwister sind / werden sollen
	 */
	ArrayList<Messdiener> geschwiarray = new ArrayList<Messdiener>();
	/**
	 * Array an Messdienern die Freunde sind / werden sollen
	 */
	ArrayList<Messdiener> friendarray = new ArrayList<Messdiener>();
	/**
	 * Deafault Seriennummer
	 */
	private static final long serialVersionUID = -2615835266426732955L;
	/**
	 * Array von allen Messdienern
	 */
	private ArrayList<Messdiener> alleMedis;
	/**
	 * Messdiener, von dem seine Geschwister und Freunde bestimmt werden sollen
	 */
	private Messdiener me;
	/**
	 * @see WMainFrame#savepath
	 */
	private String savepath;

	private final static Messdiener m = new Messdiener();

	/**
	 * Konstruktor erstellt von {@link WWAnvertrauteFrame}
	 * 
	 * @param
	 */
	/**
	 * 
	 * @param savepathofallmes
	 *            Von dort werden die Messdiener geholt und temporal fuer die
	 *            laufende Sitzung einer Instanz der Klasse gespeichert
	 * @param me
	 *            {@link WWAnvertrauteFrame#me}
	 * @param f
	 *            Uebergeordnete Instanz von {@link WWAnvertrauteFrame}
	 */
	public WWAnvertrauteFrame(WMainFrame wmf, Messdiener me, WMediBearbeitenFrame wwf) {
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("W"+References.ae+"hle Messdiener aus, die Geschwister oder Freunde werden sollen");
		setBounds(Utilities.setFrameMittig(583, 495));
		setIconImage(WMainFrame.getIcon(new References()));
		this.savepath = wmf.getEDVVerwalter().getSavepath();
		this.alleMedis = wmf.getAlleMessdiener();

		getContentPane().setLayout(null);

		scrollPane.setBounds(392, 12, 166, 425);
		getContentPane().add(scrollPane);

		scrollPane.setViewportView(list);

		list.setModel(listmodel);
		scrollPane.setColumnHeaderView(lblAlleMessdiener);

		scrollPane_1.setBounds(12, 12, 186, 158);
		getContentPane().add(scrollPane_1);

		scrollPane_1.setViewportView(friedlist);

		friedlist.setModel(friendmodel);
		scrollPane_1.setColumnHeaderView(lblFreunde);

		scrollPane_2.setBounds(12, 284, 186, 153);
		getContentPane().add(scrollPane_2);

		scrollPane_2.setViewportView(geschwielist);

		geschwielist.setModel(geschwiemodel);
		scrollPane_2.setColumnHeaderView(lblGeschwister);
		btnNachUnten.setFont(new Font("Tahoma", Font.PLAIN, 24));

		btnNachUnten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vomFreundZumGeschwisterMachen();
			}
		});
		btnNachUnten.setBounds(70, 196, 54, 61);
		getContentPane().add(btnNachUnten);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zumFreund();
			}
		});
		button.setBounds(232, 63, 117, 25);
		getContentPane().add(button);

		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wiederZurueck();
			}
		});
		button_1.setBounds(232, 291, 117, 25);
		getContentPane().add(button_1);

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speichern(wwf, wmf);
			}
		});
		btnSave.setBounds(232, 396, 117, 25);
		getContentPane().add(btnSave);
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				schliessen();
			}
		});

		btnAbbrechen.setBounds(232, 359, 117, 25);
		getContentPane().add(btnAbbrechen);

		this.me = me;
		// fürgt alle Messdiener (alleMedis) zum hauptarray hinzu es sei denn, der
		// Messdiener ist der aktuelle Mesdiener, der bearbeitet wird
		for (Messdiener messdiener : alleMedis) {
			if (!messdiener.makeId().equals(me.makeId())) {
				hauptarray.add(messdiener);

			}
		}
		// Der Hauptarray wird sortiert mit .makeID()
		hauptarray.sort(Messdiener.compForMedis);
		// alle Messdiener_IDs von Hauptarray werden zur graphischen Oberfläche
		// hinzugefügt
		for (Messdiener messdiener2 : hauptarray) {
			listmodel.addElement(messdiener2.makeId());
		}
		// Freundeueberpruefung: fuegt freunde zu friendarray und loescht die Messdiener
		// vom hauptarray

		if (me.getFreunde() != null) {
			for (String freundid : me.getFreunde()) {
				bearbeiteAnvertrauter(freundid, false, wmf);
			}
			// Geschwisterueberpruefung: fuegt geschwister zu geschwiearrayund loescht die
			// Messdiener vom hauptarray
			if (me.getGeschwister() != null) {
				for (String geschwiid : me.getGeschwister()) {
					bearbeiteAnvertrauter(geschwiid, true, wmf);
				}
			}
		}
	}

	public void bearbeiteAnvertrauter(String id, boolean geschwister, WMainFrame wmf) {
		Utilities.logging(this.getClass(), "bearbeiteAnvertraute", id + " ist dran.geschwister: " + geschwister);
		if (id != null && !id.equals("") && !id.equals("LEER")) {
			if (findeID(id, wmf) != new Messdiener()) {
				Messdiener geschwi = findeID(id, wmf);
				if (geschwi != m && geschwi != me) {
					if (geschwister) {
						geschwiarray.add(geschwi);
					} else {
						friendarray.add(geschwi);
					}
					int index = getIndex(hauptarray, geschwi);
					if (index > -1) {
						listmodel.remove(getIndex(hauptarray, geschwi));
						hauptarray.remove(geschwi);
						if (geschwister) {
							geschwiemodel.addElement(geschwi.makeId());
						} else {
							friendmodel.addElement(geschwi.makeId());
						}
					} else {
						Utilities.logging(this.getClass(), "bearbeiteAnvertraute",
								"Messdiener" + geschwi + "mit id " + id + " wurde nicht gefunden!");
					}

				}
			} else {
				Utilities.logging(this.getClass(), "bearbeiteAnvertraute", "Ung"+References.ue+"ltige Id: " + id);
			}
		}
	}

	/**
	 * Gibt aus der ID eines Mesdieners einen Messdiener, sofern dieser in dem
	 * Ordner von {@link WMainFrame#savepath}. Wenn nicht wird ein Messdiener mit
	 * Standartwerten {@link Messdiener#setDeafault()}zurueckgegeben
	 * 
	 * @param freundid
	 *            ID eines Messdieners
	 * @return ggf. Messdiener mit der id von <b>freundid</b>
	 */

	public Messdiener findeID(String freundid, WMainFrame wmf) {
		for (Messdiener messdiener : alleMedis) {
			if (messdiener.makeId().equals(freundid)) {
				return messdiener;
			}
		}
		return new Messdiener();
	}

	/**
	 * schlie�t das Fenster
	 */
	protected void schliessen() {
		this.dispose();

	}

	/**
	 * Sucht in allen Messdienern nach dem Messsdiener und gibt den Index des
	 * Messdieners aus
	 * 
	 * @param alleMedis2
	 *            alle Messdiener
	 * @param anvertrauter
	 *            der Messdiener, von dem der Index ausgegeben werden soll
	 * @return index des Messdieners in alleMedis2
	 */
	private int getIndex(ArrayList<Messdiener> alleMedis2, Messdiener anvertrauter) {
		int i = 0;
		int end = -1;

		for (Messdiener messdiener : alleMedis2) {
			if (messdiener.makeId().equals(anvertrauter.makeId())) {
				end = i;
			}
			i++;
		}
		return end;
	}

	/**
	 * verschiebt den aktuelle ausgewaehlten Messdiener vom hauptarray / list zu
	 * friendarray / friedlist
	 */
	public void zumFreund() {
		if (list.getSelectedIndex() > -1) {
			int i = list.getSelectedIndex();
			Messdiener me = hauptarray.get(i);
			friendarray.add(me);
			hauptarray.remove(me);
			friendmodel.addElement(me.makeId());
			listmodel.remove(i);
			/*
			 * Messdiener me = hauptarray.get(list.getSelectedIndex());
			 * System.out.println(me.makeId());
			 * 
			 */
			// System.out.println(me.makeId());
			// System.out.println(me.getEintritt());
		} else {
			new Erroropener("Du musst zuerst einen Messdiener ausw"+References.ae+"hlen!");
		}
	}

	/**
	 * verschiebt den aktuelle ausgewaehlten Messdiener vom friendarray / friedlist
	 * zum geschwiearray / geschwielist
	 */
	public void vomFreundZumGeschwisterMachen() {
		if (friedlist.getSelectedIndex() > -1) {
			// System.out.println(friedlist.getSelectedIndex());
			// System.out.println(friendarray.get(0));//friedlist.getSelectedIndex()));
			Messdiener me = friendarray.get(friedlist.getSelectedIndex());
			geschwiarray.add(me);
			friendarray.remove(friedlist.getSelectedIndex());
			geschwiemodel.addElement(me.makeId());
			friendmodel.remove(friedlist.getSelectedIndex());

		} else {
			new Erroropener("Du musst zuerst einen Messdiener ausw"+References.ae+"hlen!");
		}

	}

	/**
	 * verschiebt den aktuelle ausgewaehlten Messdiener vom geschwiearray /
	 * geschwielist zum hauptarray / list
	 */
	public void wiederZurueck() {
		if (geschwielist.getSelectedIndex() > -1) {
			Messdiener me = geschwiarray.get(geschwielist.getSelectedIndex());
			hauptarray.add(me);
			geschwiarray.remove(geschwielist.getSelectedIndex());
			listmodel.addElement(me.makeId());
			geschwiemodel.remove(geschwielist.getSelectedIndex());
		} else {
			new Erroropener("Du musst zuerst einen Messdiener ausw"+References.ae+"hlen!");
		}

	}

	/**
	 * speichert den Messdiener und alle seinen neuenGeschwister und Freunde
	 */
	public void speichern(WMediBearbeitenFrame wwf, WMainFrame wmf) {
		int gsize = geschwiarray.size();
		int fsize = friendarray.size();

		// ID

		if (geschwiarray.size() <= 3 && friendarray.size() <= 5) {
			// ME
			// alte löschen
			for (Messdiener messdiener : hauptarray) {
				String[] ids = messdiener.getFreunde();
				for (int i = 0; i < ids.length; i++) {
					if (ids[i].equals(me.makeId())) {
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),messdiener.makeId()
								+ ": hat einen Freund in dem aktuellen Messdiener, der keiner mehr sein m"+References.oe+"chte! bei: "
								+ i);
						// die id muss nicht die letzte sein, da beim ersten leeren String neue
						// Anvertraute reinkommen
						ids[i] = "";
						messdiener.setFreunde(ids);
						WriteFile wf = new WriteFile(messdiener, savepath);
						try {
							wf.toXML(wmf);
						} catch (IOException e) {
				 			new Erroropener(e.getMessage());
							e.printStackTrace();
						}
					}
				}
				String[] gids = messdiener.getGeschwister();
				// System.out.println(messdiener.makeId());
				for (int i = 0; i < gids.length; i++) {
					if (gids[i].equals(me.makeId())) {
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),messdiener.makeId()
								+ ": hat einen Geschwister in dem aktuellen Messdiener, der keiner mehr sein m"+References.oe+"chte! bei: "
								+ i);
						// die id muss nicht die letzte sein, da beim ersten leeren String neue
						// Anvertraute reinkommen
						gids[i] = "";
						messdiener.setGeschwister(gids);
						WriteFile wf = new WriteFile(messdiener, savepath);
						try {
							wf.toXML(wmf);
						} catch (IOException e) {
				 			new Erroropener(e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
			// neue
			// im me
			String[] a = new String[3];
			int ff = 0;
			for (Messdiener messdiener : geschwiarray) {
				a[ff] = messdiener.makeId();
				ff++;
			}
			me.setGeschwister(a);
			String[] b = new String[5];
			int f = 0;
			for (Messdiener messdiener : friendarray) {
				b[f] = messdiener.makeId();
				f++;
			}
			me.setFreunde(b);
			// me speichern
			WriteFile rf = new WriteFile(me, savepath);
			try {
				rf.toXML(wmf);
			} catch (IOException e) {
	 			new Erroropener(e.getMessage());
				e.printStackTrace();
			}
			// für mes Geschwister
			for (Messdiener geschwister : geschwiarray) {
				String[] ggs = geschwister.getGeschwister();
				boolean super1 = false;
				int j = -1;
				for (int i = 0; i < ggs.length; i++) {
					String id = ggs[i];
					if (id.equals(me.makeId())) {
						super1 = true;
					}
					if (id.equals("")) {
						j = i;
						break;
					}
				}
				if (super1 == false) {
					if (j == -1 || j > geschwister.getGeschwister().length) {
						Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(),"wahrscheinlich kein freier Platz mehr bei: " + geschwister.makeId());
					} else {
						ggs[j] = me.makeId();
						geschwister.setGeschwister(ggs);
						WriteFile wr = new WriteFile(geschwister, savepath);
						try {
							wr.toXML(wmf);
						} catch (IOException e) {
							new Erroropener(e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Geschwister wurden gespeichert!");
			// für mes Freunde
			for (Messdiener freund : friendarray) {
				System.out.println(freund.makeId() + "_FFFF ist nun draan!");
				String[] ggs = freund.getFreunde();
				boolean super2 = false;
				int j = -1;
				for (int i = 0; i < ggs.length; i++) {
					String id = ggs[i];
					if (id.equals(me.makeId())) {
						super2 = true;
						System.out.println("F:ALLES SUPER MIT: " + id);
					}
					if (id.equals("")) {
						System.out.println("F:leere id gefunden bei: " + i);
						j = i;
						break;
					}
				}
				if (super2 == false) {
					if (j == -1 || j > freund.getFreunde().length) {
						System.err.println("geht nicht freunnndi");
					} else {
						ggs[j] = me.makeId();
						freund.setFreunde(ggs);
						WriteFile wr = new WriteFile(freund, savepath);
						try {
							wr.toXML(wmf);
						} catch (IOException e) {
				 			new Erroropener(e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), "Freunde wurden gespeichert!");
			this.setEnabled(false);
			this.setVisible(false);
			wwf.setback(me, this, wmf);
			wmf.erneuern();
			this.dispose();
		} else if (gsize > 3) {
			new Erroropener("Man kann nur maximal 3 Geschwister haben");
		} else if (fsize > 5) {
			new Erroropener("Man kann nur maximal 5 Freunde haben");
		}
	}

	/**
	 * 
	 * @return friendarray als Messdiener[]
	 * @throws NullPointerException
	 */
	@Deprecated
	public Messdiener[] getFreunde() throws NullPointerException {
		return friendarray.toArray(new Messdiener[friendarray.size()]);

	}

	/**
	 * 
	 * @return geschwie als Messdiener[]
	 * @throws NullPointerException
	 */
	@Deprecated
	public Messdiener[] getGeschwister() throws NullPointerException {
		Messdiener[] tAme = null;
		return tAme;

	}
}
