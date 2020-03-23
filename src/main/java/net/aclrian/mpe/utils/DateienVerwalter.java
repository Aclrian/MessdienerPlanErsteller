package net.aclrian.mpe.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.ReadFile;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.ReadFile_Pfarrei;
import net.aclrian.mpe.start.References;

/**
 * Sonstige Klasse, die viel mit Ordnerverwaltung und Sortieren zu tun hat.
 * 
 * @author Aclrian
 *
 */
public class DateienVerwalter {
	/**
	 * Hier wird der Pfad gespeichert, indem die Messdiener gespeichert werden
	 * sollen / sind <br>
	 * andere Klassen erzeugen hiermit neue Messdiener an dem selben Ort</br>
	 */
	private String savepath;
	private Pfarrei pf;
	private static boolean ersterStart = true;

	public static final String pfarredateiendung = ".xml.pfarrei";
	public static final String textdatei = File.separator+".messdienerOrdnerPfad.txt";

	
	public static DateienVerwalter dv;
	
	public static void re_start(Window window) throws NoSuchPfarrei {
		if (ersterStart) {
			dv = new DateienVerwalter(window);
		} else {
			dv.getSavepath(window);
			dv.reloadPfarrei();
		}
		
	}
	
	private DateienVerwalter(Window window) throws NoSuchPfarrei {
		this.getSpeicherort(window);
		File f = getPfarreFile();
		if(f == null) {
			throw new NoSuchPfarrei(savepath);
		}
		pf = ReadFile_Pfarrei.getPfarrei(f.getAbsolutePath());
	}

	
	public Pfarrei getPfarrei() {
		if(pf==null) {
			reloadPfarrei();
		}
		return pf;
	}
	
	private void reloadPfarrei() {
		File f = getPfarreFile();
		if (f == null) {
			Dialogs.fatal("Es konnte keine ");
		}
		Log.getLogger().info("Pfarrei gefunden in: " + f.toString());
		pf = ReadFile_Pfarrei.getPfarrei(f.getAbsolutePath());
	}

	private ArrayList<File> getPfarreiFiles(){
		ArrayList<File> files = new ArrayList<File>();
		File f = new File(savepath);
		for (File file : f.listFiles()) {
			String s = file.toString();
			if (s.endsWith(pfarredateiendung)) {
				files.add(file);
			}

		}
		return files;
	}

	private File getPfarreFile() {
		ArrayList<File> files = getPfarreiFiles();
		if (files.size() != 1) {
			if (files.size() > 1) {
				Dialogs.warn("Es darf nur eine Datei mit der Endung: '" + pfarredateiendung+ "' in dem Ordner: " + savepath + " vorhanden sein.");
				return files.get(0);
			} else {
				return null;
			}
		} else {
			return files.get(0);
		}
	}


	public void removeoldPfarrei(File neuePfarrei) {
		ArrayList<File> files = getPfarreiFiles();
		ArrayList<File> todel = new ArrayList<>();
		boolean candel = false;
		for (File f: files) {
				if (!f.getAbsolutePath().contentEquals(neuePfarrei.getAbsolutePath())){
					todel.add(f);
				}else candel=true;
		}
		if (candel) todel.forEach(f->f.delete());
	}

	/**
	 * 
	 * @return Ausgewaehlten Ordnerpfad
	 * @throws NullPointerException
	 */
	private String waehleOrdner(Window window) throws NullPointerException {
		DirectoryChooser f = new DirectoryChooser();
		String s = "Ordner w" + References.ae + "hlen, in dem alles gespeichert werden soll:";
		f.setTitle(s);
		File file = f.showDialog(window);
		return file == null ? null : file.getPath();
	}

	private ArrayList<File> getPaths(File file, ArrayList<File> list) {
		if (file == null || list == null || !file.isDirectory())
			return null;
		File[] fileArr = file.listFiles();
		for (File f : fileArr) {
			if (f.isDirectory()) {
				getPaths(f, list);
			}
			list.add(f);
		}
		return list;
	}

	private ArrayList<File> getAlleMessdienerFiles(String path) {// 2
		String verzName = path;
		Log.getLogger().info("verzName: " + verzName);
		ArrayList<File> files = getPaths(new File(verzName), new ArrayList<File>());
		if (files == null) {
			return new ArrayList<File>();
		}
		return files;
	}

	/**
	 * 
	 * @return Messdiener als List
	 */
	public ArrayList<Messdiener> getAlleMedisVomOrdnerAlsList() {
		if (pf == null) {
			reloadPfarrei();
		}
		ArrayList<File> files = getAlleMessdienerFiles(savepath);
		ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
		for (File file : files) {
			ReadFile rf = new ReadFile();
			Messdiener m = rf.getMessdiener(file.getAbsolutePath());
			if (m != null) {
				medis.add(m);
			}
		}
		for (Messdiener medi :medis) {
			medi.setnewMessdatenDaten(medis);
		}
		return medis;
	}

	public String getSavepath(Window window) {
		if (savepath == null || savepath.equals("")) {
			savepath = waehleOrdner(window);
		}
		return savepath;
	}

	private void setSavepath(String savepath) {
		this.savepath = savepath;
	}

	public void erneuereSavepath(Window window) {
		String homedir = System.getProperty("user.home");
		homedir = homedir + textdatei;
		File f = new File(homedir);
		if(f.delete()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			savepath="";
			getSpeicherort(window);
		} else Dialogs.warn("Konnte die Datei " + homedir + " nicht "+References.ae+"ndern.");
	}

	private void getSpeicherort(Window window) {
		String homedir = System.getProperty("user.home");
		homedir = homedir + textdatei;
		Log.getLogger().info("Das Home-Verzeichniss wurde gefunden: " + homedir);
		File f = new File(homedir);
		if (!f.exists()) {
			String s;
			if (savepath == null || savepath.equals("")) {
				s = this.waehleOrdner(window);
			} else {
				s = savepath;
			}
			try {
				f.createNewFile();
				FileWriter fileWriter = new FileWriter(homedir);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				if (s != null) {
					Log.getLogger().info(s);
					bufferedWriter.write(s);
					setSavepath(s);
					bufferedWriter.close();
				} else {
					savepath="";
					Dialogs.warn("Es wird ein Speicherort benötigt, um dort Messdiener zu speichern.\nBitte einen Speicherort eingeben!");
					getSpeicherort(window);
				}
			} catch (IOException e) {
				Dialogs.error(e, "Der Speicherort konnte nicht gespeichert werden.");
			}
		} else {
			try {
				String line = null;
				InputStreamReader fileReader = new InputStreamReader(new FileInputStream(homedir),"UTF-8");
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					File savepath = new File(line);
					if (savepath.exists()) {
						setSavepath(line);
					} else {
						Log.getLogger().info("Der Speicherort '" + f + "' existiert nicht!");
						savepath.delete();
						getSpeicherort(window);
					}
					break;
				}
				if(savepath==null || savepath.equals("")){
					f.delete();
					getSavepath(window);
				}
				bufferedReader.close();
			} catch (IOException e) {
				Dialogs.error(e, "Die Datei '" + homedir+ "' konnte nicht gelesen werden.");
			}
			if(savepath == null) {
				savepath = waehleOrdner(window);
				try {
					f.createNewFile();
					FileWriter fileWriter = new FileWriter(homedir);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					if (savepath != null) {
						bufferedWriter.write(savepath);
						bufferedWriter.close();
					} else {
						Dialogs.warn("Es wird ein Speicherort benötigt, um dort Messdiener zu speichern.\nBitte einen Speicherort eingeben!");
						getSpeicherort(window);
					}
				} catch (IOException e) {
					Log.getLogger().info("Auf den Speicherort '" + f + "' kann nicht zugegriffen werden!");
					getSpeicherort(window);
				}
			}
		}
		Log.getLogger().info("Der Speicherort liegt in: " + savepath);
	}
	
	public void speicherortWechseln(Window window) {
		String homedir = System.getProperty("user.home");
		homedir = homedir + textdatei;
		File f = new File(homedir);
		f.delete();
		getSpeicherort(window);
	}

    public static class NoSuchPfarrei extends Exception{
		private final String savepath;
		public NoSuchPfarrei(String savepath) {
			this.savepath=savepath;
		}
		public String getSavepath() {
			return savepath;
		}
	}
}
