package net.aclrian.messdiener.deafault;

import java.io.IOException;
import java.util.ArrayList;

import net.aclrian.messdiener.utils.Erroropener;
import net.aclrian.messdiener.utils.Messverhalten;
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
		setzeAllesNeu("Vorname", "Nachname", 2000, this.istLeiter, new Messverhalten());
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
			new Erroropener("Hast du dich beim Eintrittsjahr vertippt?", null);
		}
	}

	public void setIstLeiter(boolean istLeiter) {
		this.istLeiter = istLeiter;
	}

	/**
	 * Mit dieser Methode wird der Messdiener als .xml Datei lokal gespeichert
	 * @param pfad
	 */
	public void makeXML(String pfad) {
		WriteFile wf = new WriteFile(this, pfad);
		try {
		wf.toXML();
		} catch (IOException e) {
			System.err.println(e.getMessage());
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

	/*public void setLeer(Messe messe) {
		this.Vorname = "LEERV";
		this.Nachname = "NLEER";
		this.Eintritt= -1;
		setIstLeiter(istLeiter);
		this.dienverhalten = new Messverhalten();
		
	}*/
}