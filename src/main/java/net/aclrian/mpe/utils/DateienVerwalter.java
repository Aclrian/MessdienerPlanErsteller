package net.aclrian.mpe.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.ReadFile;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.ReadFile_Pfarrei;
import net.aclrian.mpe.resources.References;
import net.aclrian.mpe.start.AData;
import net.aclrian.mpe.start.AProgress;

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

	public DateienVerwalter() {
		this.getSpeicherort();
	}

	public DateienVerwalter(String savepath) {
		this.setSavepath(savepath);
	}

	public Pfarrei getPfarrei() {
		File f = getPfarreFile();
		Utilities.logging(getClass(), "getPfarrei", "Pfarrei gefunden in: " + f.toString());
		Pfarrei rtn = ReadFile_Pfarrei.getPfarrei(f.getAbsolutePath());
		if (rtn == null) {
			return null;
		} else {
			return rtn;
		}
	}

	private File getPfarreFile() {
		ArrayList<File> files = new ArrayList<File>();
		File f = new File(savepath);
		for (File file : f.listFiles()) {
			String s = file.toString();
			if (s.endsWith(AData.pfarredateiendung)) {
				files.add(file);
			}

		}
		if (files.size() != 1) {
			if (files.size() > 1) {
				new Erroropener(new Exception("Es darf nur eine Datei mit der Endung: '" + AData.pfarredateiendung
						+ "' in dem Ordner: " + savepath + " vorhanden sein."));
			} else {
				return null;
			}
		} else {
			return files.get(0);
		}
		return null;
	}

	/**
	 * 
	 * @return Ausgewaehlten Ordnerpfad
	 * @throws NullPointerException
	 */
	private String waehleOrdner() throws NullPointerException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (javax.swing.UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		JFileChooser f = new JFileChooser();
		String s = "Ordner w" + References.ae + "hlen, in dem alles gespeichert werden soll:";
		f.setDialogTitle(s);
		f.setApproveButtonText("Ausw" + References.ae + "hlen");
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int i = f.getBounds().width + 100;
		f.setBounds(f.getBounds().x, f.getBounds().y, i, f.getBounds().height);
		//WEinFrame.farbe(f);
		if (f.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			zurueckZumJavaLookandFeel();
			Utilities.logging(this.getClass(), "waehleOrdner", f.getSelectedFile().getPath());
			return f.getSelectedFile().getPath();
		} else {
			zurueckZumJavaLookandFeel();
			return null;
		}
	}

	private void zurueckZumJavaLookandFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
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
		Utilities.logging(this.getClass(), "getAlleMessdienerFiles2", "verzName: " + verzName);
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
	public ArrayList<Messdiener> getAlleMedisVomOrdnerAlsList(String pfad, AData ada) {
		ArrayList<File> files = getAlleMessdienerFiles(pfad);
		ArrayList<Messdiener> medis = new ArrayList<Messdiener>();
		for (File file : files) {
			ReadFile rf = new ReadFile();
			Utilities.logging(this.getClass(), "getAlleMedisVomOrdnerAlsList", file.getAbsolutePath());
			Messdiener m = rf.getMessdiener(file.getAbsolutePath(), ada);
			if (m != null) {
				medis.add(m);
			}
		}
		return medis;
	}

	public Messdiener sucheMessdiener(String geschwi, Messdiener akt, AProgress ap) throws Exception {
		for (Messdiener messdiener : ap.getAda().getMediarray()) {
			if (messdiener.makeId().equals(geschwi)) {
				return messdiener;
			}
		}
		throw new CouldnotFindMedi("Konnte f" + References.ue + "r " + akt.makeId() + " : " + geschwi + " nicht finden",
				akt, geschwi, ap);
	}

	public String getSavepath() {
		if (savepath == null || savepath.equals("")) {
			savepath = waehleOrdner();
		}
		return savepath;
	}

	private void setSavepath(String savepath) {
		this.savepath = savepath;
	}

	public void erneuereSavepath() {
		this.savepath = "";
		getSpeicherort();
	}

	private void getSpeicherort() {
		String homedir = System.getProperty("user.home");
		homedir = homedir + AData.textdatei;
		Utilities.logging(this.getClass(), "getSpeicherort", "Das Home-Verzeichniss wurde gefunden: " + homedir);
		File f = new File(homedir);
		if (!f.exists()) {
			String s;
			if (savepath == null || savepath.equals("")) {
				s = this.waehleOrdner();
			} else {
				s = savepath;
			}
			try {
				f.createNewFile();
				FileWriter fileWriter = new FileWriter(homedir);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				if (s != null) {
					bufferedWriter.write(s);
					setSavepath(s);
					bufferedWriter.close();
				} else {
					new Erroropener(new Exception("Bitte auswaehlen beim naechsten Mal!!!"));
					System.exit(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
				new Erroropener(e);
			}
		} else {
			try {
				String line = null;
				FileReader fileReader = new FileReader(homedir);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					File savepath = new File(line);
					if (savepath.exists()) {
						setSavepath(line);
					} else {
						Utilities.logging(this.getClass(), "getSpeicheort", "das sollte nicht passieren!!");
						savepath.mkdir();
						setSavepath(line);
					}
				}
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				new Erroropener(e);
			}
		}
	}

	public class CouldnotFindMedi extends Exception {
		private static final long serialVersionUID = 1L;
		private Messdiener me;

		private String falscherEintrag;
		private AProgress ap;

		public CouldnotFindMedi(String message, Messdiener me, String falscherEintrag, AProgress ap) {
			super(message);
			this.me = me;
			this.falscherEintrag = falscherEintrag;
			this.ap = ap;
		}

		public AProgress getAp() {
			return ap;
		}

		public Messdiener getMessdiener() {
			return me;
		}

		public String getFalscherEintrag() {
			return falscherEintrag;
		}
	}
}