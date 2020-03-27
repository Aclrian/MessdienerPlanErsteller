package net.aclrian.mpe.messdiener;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.validator.routines.EmailValidator;

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
	public final static Comparator<Messdiener> compForMedis = (o1, o2) -> o1.makeId().compareToIgnoreCase(o2.makeId());
	public static final Comparator<? super Messdiener> einteilen = (Comparator<Messdiener>) (o1, o2) -> {
		double d1 = o1.getMessdatenDaten().getSortierenDouble();// anz/max 0 kommt zuerst dann 0,5 --> 1
		double d2 = o2.getMessdatenDaten().getSortierenDouble();
		if (d1 == d2) {
			if (d1 == 0) {
				int i1 = o1.getMessdatenDaten().getMax_messenInt();
				int i2 = o2.getMessdatenDaten().getMax_messenInt();
				return Integer.compare(i2, i1);
			}
		}
		return Double.compare(d1, d2);
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
	 * @throws NotValidException Wenn email nicht valid ist
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
		for (int i = 0; i < freundelenght; i++) {
			Freunde[i] = "";
		}
		for (int i = 0; i < geschwilenght; i++) {
			Geschwister[i] = "";
		}
	}

	public void setVorname(String vorname) {
		this.Vorname = vorname;
	}

	public void setNachnname(String nachnname) {
		this.Nachname = nachnname;
	}

	public void setFreunde(String[] freunde) {
		this.Freunde = freunde;
	}

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
	 */
	public void makeXML() {
		WriteFile wf = new WriteFile(this);
		try {
			wf.toXML();
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

	public void setnewMessdatenDaten() {
		this.daten = new Messdaten(this);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Messdiener that = (Messdiener) o;
		return Objects.equals(file, that.file);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(Vorname, Nachname, Email, dienverhalten, Eintritt, istLeiter, daten, file);
		result = 31 * result + Arrays.hashCode(Freunde);
		result = 31 * result + Arrays.hashCode(Geschwister);
		return result;
	}

	public static class NotValidException extends Exception{
		public NotValidException() {
			super("Keine gültige E-Mail Addresse");
		}
	}
}