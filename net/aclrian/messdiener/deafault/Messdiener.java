package net.aclrian.messdiener.deafault;

import java.io.IOException;
import java.util.Comparator;

import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Utilities;
import net.aclrian.messdiener.utils.WriteFile;
import net.aclrian.messdiener.window.WMainFrame;

/**
 * Klasse, mit der Messdiener digital gespeichert werden können
 * 
 * @author Aclrian
 *
 */
public class Messdiener {
	private String Vorname = "";
	private String Nachname = "";
	private String[] Freunde = new String[5];
	private String[] Geschwister = new String[3];
	private Messverhalten dienverhalten;
	private int Eintritt = 2000;
	private boolean istLeiter = false;
	private Messdaten daten;
	public final static Comparator<Messdiener> compForMedis = new Comparator<Messdiener>() {

		@Override
		public int compare(Messdiener o1, Messdiener o2) {
			return o1.makeId().compareToIgnoreCase(o2.makeId());
		}
	};
	public static final Comparator<? super Messdiener> einteilen = new Comparator<Messdiener>() {
		@Override
		public int compare(Messdiener o1, Messdiener o2) {
			double d1 = o1.getMessdatenDaten().getSortierenDouble();// anz/max 0 kommt zuerst dann 0,5 --> 1
			double d2 = o2.getMessdatenDaten().getSortierenDouble();
			if (d1 == d2) {
				if (d1 == 0) {
					int i1 = o1.getMessdatenDaten().getMax_messenInt();
					int i2 = o2.getMessdatenDaten().getMax_messenInt();
					if (i1 == i2) {
						return 0;
					}
					if (i1 < i2) {
						return 1;
					}
					if (i1 > i2) {
						return -1;
					}
				}
			}
			if (d1 < d2) {
				// d1 vor d2
				return -1;
			} else if (d1 > d2) {
				// d2 vor d1
				return 1;

			} else {
		/*		if (o1.getMessdatenDaten().getMax_messen() < o2.getMessdatenDaten().getMax_messen()) {
					// d1 vor d2
					return -1;

				} else if (o1.getMessdatenDaten().getMax_messen() > o2.getMessdatenDaten().getMax_messen()) {
					// d2 vor d1
				}
*///TODO help what to do?
				return 0;
				// return o1.makeId().compareToIgnoreCase(o2.makeId());
			}
		}
	};

	@Override
	public String toString() {
		return makeId();
	}

	/**
	 * Konstruktor
	 */
	public Messdiener() {
		setDeafault();
	}

	/**
	 * 
	 * @param vname
	 *            Vorname
	 * @param nname
	 *            Nachname
	 * @param Eintritt
	 *            Jahr des Einfuehrung Ist davon abhängig, wie oft der Messdiener
	 *            eingeteilt werden soll
	 * @param istLeiter
	 *            Leiter, Ist davon abhängig, wie oft der Messdiener eingeteilt
	 *            werden soll
	 * @param dienverhalten
	 *            Wann kann er zu welchen Standart Messen (bspw. Sontag Morgen oder
	 *            Dienstag Abend) dienen
	 */
	public void setzeAllesNeu(String vname, String nname, int Eintritt, boolean istLeiter,
			Messverhalten dienverhalten) {
		setVorname(vname);
		setNachnname(nname);
		setEintritt(Eintritt);
		setIstLeiter(istLeiter);
		setDienverhalten(dienverhalten);
	}

	/**
	 * setzt Standart Werte
	 */
	private void setDeafault() {
		setzeAllesNeu("Vorname", "Nachname", 2000, this.istLeiter, new Messverhalten(null));
		for (int i = 0; i < Freunde.length; i++) {
			Freunde[i] = "";
		}
		for (int i = 0; i < Geschwister.length; i++) {
			Geschwister[i] = "";
		}
	}

	public void setVorname(String vorname) {
		this.Vorname = vorname;
	}

	public void setNachnname(String nachnname) {
		this.Nachname = nachnname;
	}

	/**
	 * Hier wird ein Array an IDs (Strings) der Freunde des Messdieners gespeichert.
	 * Dies erreicht man mit der Methode .makeID() Mit diesen Freunden wird dem
	 * Messdiener eingeteilt. <br>
	 * Man kann nur 5 Freunde haben.</br>
	 * 
	 * @param freunde
	 * 
	 */
	public void setFreunde(String[] freunde) {
		this.Freunde = freunde;
	}

	/**
	 * Hier wird ein Array an IDs (Strings) der Geschwister des Messdieners
	 * gespeichert. Dies erreicht man mit der Methode .makeID()<br>
	 * Mit diesen Geschwistern wird der Messdiener oft eingeteilt.</br>
	 * <br>
	 * Man kann nur drei Geschwister haben.</br>
	 * 
	 * @param geschwister
	 * 
	 */
	public void setGeschwister(String[] geschwister) {
		this.Geschwister = geschwister;
	}

	public void setEintritt(int eintritt) {
		if ((eintritt < 2200) && (eintritt > 1900)) {
			this.Eintritt = eintritt;
		} else {
			new Erroropener("Hast du dich beim Eintrittsjahr vertippt?");
		}
	}

	public void setIstLeiter(boolean istLeiter) {
		this.istLeiter = istLeiter;
	}

	/**
	 * Mit dieser Methode wird der Messdiener als .xml Datei lokal gespeichert
	 * 
	 * @param pfad
	 */
	public void makeXML(String pfad, WMainFrame wmf) {
		WriteFile wf = new WriteFile(this, pfad);
		try {
			wf.toXML(wmf);
		} catch (IOException e) {
			new Erroropener(e.getMessage());
			Utilities.logging(this.getClass(), this.getClass().getEnclosingMethod(), e.getMessage());
			e.printStackTrace();
		}
	}

	public String makeId() {
		return Nachname + ", " + Vorname;
	}

	public boolean isIstLeiter() {
		return this.istLeiter;
	}

	public String getVorname() {
		return this.Vorname;
	}

	public int getEintritt() {
		return this.Eintritt;
	}

	public String[] getGeschwister() {
		return this.Geschwister;
	}

	public String[] getFreunde() {
		return this.Freunde;
	}

	public String getNachnname() {
		return this.Nachname;
	}

	public Messverhalten getDienverhalten() {
		return this.dienverhalten;
	}

	public void setDienverhalten(Messverhalten dienverhalten) {
		this.dienverhalten = dienverhalten;
	}

	public Messdaten getMessdatenDaten() {
		return daten;
	}

	public void setMessdatenDaten(Messdaten daten) {
		this.daten = daten;
	}

	public void setnewMessdatenDaten(String savepath, int aktdatum, WMainFrame wmf) {
		this.daten = new Messdaten(this, wmf, aktdatum);
	}

	/*
	 * public void setLeer(Messe messe) { this.Vorname = "LEERV"; this.Nachname =
	 * "NLEER"; this.Eintritt= -1; setIstLeiter(istLeiter); this.dienverhalten = new
	 * Messverhalten();
	 * 
	 * }
	 */
}