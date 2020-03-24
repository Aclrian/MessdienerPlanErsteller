package net.aclrian.mpe.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.Messdiener.NotValidException;
import net.aclrian.mpe.messe.Messverhalten;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Log;

public class ConvertCSV {
	// public void setzeAllesNeu(String vname, String nname, int Eintritt, boolean
	// istLeiter,
	// Messverhalten dienverhalten String email) {
	// value: anweisung für medi, key ist index in welcher Zeile es steht
	/*
	 * 0:vorname| nur 1:nachname| nur 2:Eintritt 3:leiter|
	 * "":ja/!string.equals(""):nein 4:leiter| "":nein/!string.equals(""):ja
	 * 5:vorname nachname 6: nachname, vorname 7:email
	 * 
	 */

	public ConvertCSV(File f, ArrayList<Integer> sortierung, Window window) throws IOException {
		Log.getLogger().warn("Das Unter-Programm unterstützt die Vorlieben von Messdienern nicht!\n-Also wann sie dienen können");
		if (f.exists() && f.getName().endsWith(".csv")) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] elemente = line.split(";");
				String vorname = "Vorname", nachname = "Nachname", email = "";
				int eintritt = Calendar.getInstance().get(Calendar.YEAR);
				boolean leiter = false;
				for (int j = 0; j < elemente.length; j++) {
					if(elemente.length <2) {
						break;
					}
					switch (sortierung.get(j)) {
					case 0:// vorname
						vorname = elemente[j];
						break;
					case 1:
						nachname = elemente[j];
						break;
					case 2:
						eintritt = Integer.parseInt(elemente[j]);
						break;
					case 3:
						leiter = elemente[j].equals("");// ""==ja
						break;
					case 4:
						leiter = !elemente[j].equals("");// ""==nein
						break;
					case 5:
						String vollername = elemente[j];
						String[] array = vollername.split(" ");
						if (array.length != 2) {
							System.out.println(vollername + "hat nicht 2 Leerzeichen");
							if (array.length < 2) {
								vorname = array[0];
								nachname = array[0];
							} else {
								if (array[array.length - 2].toLowerCase(Locale.GERMAN)
										.equals(array[array.length - 2].toLowerCase(Locale.GERMAN))) {
									for (int i = 0; i < array.length - 2; vorname += "", i++) {
										vorname += array[i];
									}
									nachname = array[array.length - 2] + " " + array[array.length - 1];
								} else {
									for (int i = 0; i < array.length - 1; vorname += "", i++) {
										vorname += array[i];
									}
									nachname = array[array.length-1];
								}
							}
						} else {
							vorname = array[0];
							nachname = array[1];
						}
						break;
						
					case 6:
						String[] array2 = elemente[j].split(", ");
						if (array2.length!=2) {
							vorname=elemente[j];
							nachname = elemente[j];
						} else {
							nachname = array2[0];
							vorname = array2[1];
						}
						break;
					case 7:
						email = elemente[j];
						break;
					default:
						break;
					}
				}
				Messdiener m = new Messdiener(new File(DateienVerwalter.dv.getSavepath(window)+File.separator+nachname+", "+vorname+".xml"));
				m.setzeAllesNeuUndMailLeer(vorname, nachname, eintritt, leiter, new Messverhalten());
				try {
				m.setEmail(email);
				} catch (NotValidException e) {
					Log.getLogger().info(email + " von " + m.makeId() + " ist nicht gültig");
					m.setEmailEmpty();
				}
				m.makeXML(window);
			}
			br.close();
		}
	}

	
	/*public static void main(String[] args) {
		ArrayList<Integer> a = new ArrayList<>();
		a.add(0);
		a.add(1);
		a.add(3);
		a.add(2);
		try {
			new ConvertCSV(new File(""), a );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}*/
}
