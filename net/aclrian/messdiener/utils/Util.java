package net.aclrian.messdiener.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.aclrian.messdiener.deafault.Messdiener;
import net.aclrian.messdiener.deafault.Messe;
import net.aclrian.messdiener.window.medierstellen.WWAnvertrauteFrame;

/**
 * Sonstige Klasse, die viel mit Ordnerverwaltung und Sortieren zu tun hat.
 * 
 * @author Aclrian
 *
 */
public class Util {
	/**
	 * Hier wird der Pfad gespeichert, indem die Messdiener gespeichert werden
	 * sollen / sind <br>
	 * andere Klassen erzeugen hiermit neue Messdiener an dem selben Ort</br>
	 */
	private String savepath;

	public Util() {
		this.setSavepath(waehleOrdner());
	}

	public Util(String savepath) {
		this.setSavepath(savepath);
	}

	/**
	 * 
	 * @return Ausgewaehlten Ordnerpfad
	 * @throws NullPointerException
	 */
	public String waehleOrdner() throws NullPointerException {
		// throw new ArrayIndexOutOfBoundsException();
		JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(1);
		f.showSaveDialog(null);// OpenDialog(null);
		System.out.println("[Util:waehleOrdner()]: " + f.getSelectedFile().getPath());
		return f.getSelectedFile().getPath();
	}
	/*
	 * /** Launchs the application
	 * 
	 * @param args
	 *
	 * public static void main(String[] args) { // waehleOrdner();
	 * getMessdienerFile(
	 * "/media/aclrian/DRIVE-N-GO4/.aaa_aproo/zzzTESTgit/_3GIT_/XML"); }
	 */

	/**
	 * Öffnet den DateiAuswaehler an einem Ort mit dem angegebenen Pfad und gibt den
	 * Pfad der ausgewaehlten Datei an
	 * 
	 * @param path
	 *            Pfad
	 * @return Pfad der ausgewaehlten Datei
	 * @throws NullPointerException
	 */
	public String getMessdienerFile(String path) throws NullPointerException {
		String rtn = null;
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");

		chooser.setDialogTitle("Open schedule file");
		chooser.setFileFilter(xmlfilter);
		chooser.setCurrentDirectory(new File(path));
		chooser.showOpenDialog(null);
		try {
			rtn = chooser.getSelectedFile().getAbsolutePath();
		} catch (NullPointerException e) {
			System.err.println("getMessdienerFile(): Die Eingabe wurde vom Benutzer beendet!");
		}

		return rtn;
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

	private ArrayList<File> getAlleMessdienerFiles() {
		String verzName;
		try {
			verzName = waehleOrdner();
			System.out.println("[UTIL:getAlleMEssdienerFiles()]verzName: " + verzName);
		} catch (NullPointerException e) {
			return new ArrayList<File>();
		}

		ArrayList<File> files = getPaths(new File(verzName), new ArrayList<File>());
		if (files == null) {
			return new ArrayList<File>();
		}
		return files;
	}

	private ArrayList<File> getAlleMessdienerFiles(String path) {// 2
		String verzName = path;
		ArrayList<File> files = getPaths(new File(verzName), new ArrayList<File>());
		if (files == null) {
			return new ArrayList<File>();
		}
		return files;
	}

	public Messdiener[] getAlleMedisVomOrdner(String path) {// 1
		ArrayList<File> files = getAlleMessdienerFiles(path);
		Messdiener[] medis = new Messdiener[files.size()];
		int i = 0;
		for (File file : files) {
			ReadFile rf = new ReadFile();
			medis[i] = rf.getMessdiener(file.getAbsolutePath());
			i++;
		}
		return medis;
	}

	/**
	 * 
	 * @return Messdiener als Array
	 */
	public Messdiener[] getAlleMedisVomOrdner() {
		ArrayList<File> files = getAlleMessdienerFiles();
		Messdiener[] medis = new Messdiener[files.size()];
		int i = 0;
		for (File file : files) {
			ReadFile rf = new ReadFile();
			medis[i] = rf.getMessdiener(file.getAbsolutePath());
			i++;
		}
		return medis;
	}

	/**
	 * 
	 * @return Messdiener als List
	 */
	public ArrayList<Messdiener> getAlleMedisVomOrdnerAlsList() {
		ArrayList<File> files = getAlleMessdienerFiles();
		ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
		for (File file : files) {
			ReadFile rf = new ReadFile();
			medis.add(rf.getMessdiener(file.getAbsolutePath()));
		}
		return medis;
	}

	/**
	 * 
	 * @return Messdiener als List
	 */
	public ArrayList<Messdiener> getAlleMedisVomOrdnerAlsList(String pfad) {
		ArrayList<File> files = getAlleMessdienerFiles(pfad);
		ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
		for (File file : files) {
			ReadFile rf = new ReadFile();
			medis.add(rf.getMessdiener(file.getAbsolutePath()));
		}
		return medis;
	}

	public final Comparator<Messe> compForMessen = new Comparator<Messe>() {

		@Override
		public int compare(Messe o1, Messe o2) {
			Date do1 = o1.getDate();
			Date do2 = o2.getDate();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

			return df.format(do1).compareToIgnoreCase(df.format(do2));
		}
	};
	public final Comparator<Messdiener> compForMedis = new Comparator<Messdiener>() {

		@Override
		public int compare(Messdiener o1, Messdiener o2) {
			return o1.makeId().compareToIgnoreCase(o2.makeId());
		}
	};

	/**
	 * sucht einen Messdiener an Hand seiner ID und auf Grund des savepath
	 * 
	 * @param geschwi
	 * @param savepath
	 * @return
	 */
	public Messdiener sucheMessdiener(String geschwi, String savepath) {
		File file = new File(savepath + "//" + geschwi);
		if (file.exists()) {
			ReadFile rf = new ReadFile();
			return rf.getMessdiener(file.getAbsolutePath());
		} else {
			return null;
		}

	}

	public int getMaxAnzLeiter(String savepath) {
		Messdiener[] medis = getAlleMedisVomOrdner(savepath);
		int rtn = 0;
		for (Messdiener me : medis) {
			if (me.isIstLeiter()) {
				rtn++;
			}
		}
		return rtn;
	}

	public int getMaxAnzMedis(String savepath) {
		Messdiener[] medis = getAlleMedisVomOrdner(savepath);
		int rtn = medis.length;
		/*
		 * for (Messdiener me : medis) { if (me.isIstLeiter()) { rtn--; } }
		 */
		return rtn;

	}

	public Messdiener sucheMessdiener(String geschwi, ArrayList<Messdiener> alleMedis) throws Exception {
		if (geschwi.equals("")) {
			return null;
		} else {
			for (Messdiener messdiener : alleMedis) {
				if (messdiener.makeId().equals(geschwi)) {
					return messdiener;
				}
			}
			throw new Exception("Konnte nichts finden!");
		}
	}

	public String getSavepath() {
		return savepath;
	}

	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}

	public File getPlanSavepath() {
		File s = new File(savepath);
		File f = new File(s.getParent() + "\\" + "Plan");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	public static String getLoggerInfo(Class<?> loggingclass, String methodenName) {
		return "[" + loggingclass.getName() + ":"+ methodenName + "] ";
	}
	
	public static void logging(Class<?> loggedclass, String methodenname, String mitteilung) {
		System.out.println(getLoggerInfo(loggedclass, methodenname)+mitteilung);
	}
}