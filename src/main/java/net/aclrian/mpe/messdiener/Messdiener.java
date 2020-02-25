package net.aclrian.mpe.messdiener;

import java.io.File;
import java.util.Comparator;

import org.apache.commons.validator.routines.EmailValidator;

import javafx.stage.Window;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.utils.Dialogs;

/**
 * Klasse, mit der Messdiener digital gespeichert werden koennen
 * 
 * @author Aclrian
 *
 */
public class Messdiener {
	/**
	 * Dies ist die Anzahl, wie viele Freunde ein Messdiener haben kann.
	 */
	public static int freundelenght = 5;
	
	/**
	 * Dies ist die Anzahl, wie viele Freunde ein Messdiener haben kann.
	 */
	public static int geschwilenght = 3;
	private String Vorname = "";
	private String Nachname = "";
	private String Email = "";
	private String[] Freunde = new String[freundelenght];

	private String[] Geschwister = new String[geschwilenght];
	private Messverhalten dienverhalten;
	private int Eintritt = 2000;
	private boolean istLeiter = false;
	private Messdaten daten;
	private File file;
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
				return 0;
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
	public Messdiener(File f) {
		setDeafault();
		this.file = f;
	}

	/**
	 * 
	 * @param vname         Vorname
	 * @param nname         Nachname
	 * @param Eintritt      Jahr des Einfuehrung Ist davon abhaengig, wie oft der
	 *                      Messdiener eingeteilt werden soll
	 * @param istLeiter     Leiter, Ist davon abhaengig, wie oft der Messdiener
	 *                      eingeteilt werden soll
	 * @param dienverhalten Wann kann er zu welchen Standart Messen (bspw. Sontag
	 *                      Morgen oder Dienstag Abend) dienen
	 * @param email			eine gültige Email-Addresse
	 * @throws Exception 
	 */
	public void setzeAllesNeu(String vname, String nname, int Eintritt, boolean istLeiter,
			Messverhalten dienverhalten, String email) throws NotValidException {
		setVorname(vname);
		setNachnname(nname);
		setEintritt(Eintritt);
		setIstLeiter(istLeiter);
		setDienverhalten(dienverhalten);
		setEmail(email);
	}
	
	/**
	 * 
	 * @param vname         Vorname
	 * @param nname         Nachname
	 * @param Eintritt      Jahr des Einfuehrung Ist davon abhaengig, wie oft der
	 *                      Messdiener eingeteilt werden soll
	 * @param istLeiter     Leiter, Ist davon abhaengig, wie oft der Messdiener
	 *                      eingeteilt werden soll
	 * @param dienverhalten Wann kann er zu welchen Standart Messen (bspw. Sontag
	 *                      Morgen oder Dienstag Abend) dienen
	 * @throws Exception 
	 */
	public void setzeAllesNeuUndMailLeer(String vname, String nname, int Eintritt, boolean istLeiter,
			Messverhalten dienverhalten) {
		setVorname(vname);
		setNachnname(nname);
		setEintritt(Eintritt);
		setIstLeiter(istLeiter);
		setDienverhalten(dienverhalten);
		setEmailEmpty();
	}

	/**
	 * setzt Standart Werte
	 */
	private void setDeafault() {
		try {
			setzeAllesNeu("Vorname", "Nachname", 2000, this.istLeiter, new Messverhalten(), "");
		} catch (NotValidException e) {
			e.printStackTrace();
		}
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
		this.Eintritt = eintritt;
	}

	public void setIstLeiter(boolean istLeiter) {
		this.istLeiter = istLeiter;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public void setEmail(String email) throws NotValidException {
		if(email.contentEquals("") || EmailValidator.getInstance().isValid(email)) {
			Email = email;
			return;
		}
		throw new NotValidException();
	}
	public void setEmailEmpty(){
		Email = "";
	}

	/**
	 * Mit dieser Methode wird der Messdiener als .xml Datei lokal gespeichert
	 * 
	 * @param pfad
	 */
	public void makeXML(Window window) {
		WriteFile wf = new WriteFile(this);
		try {
			wf.toXML(window);
		} catch (Exception e) {
			Dialogs.error(e, "Der Messdiener '"+makeId()+"' konnte nicht gespeichert werden.");
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

	public void setnewMessdatenDaten(int aktdatum) {
		this.daten = new Messdaten(this);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;

	}
	
	public class NotValidException extends Exception{
		public NotValidException() {
			super("Keine gültige E-Mail Addresse");
		}
	}
}