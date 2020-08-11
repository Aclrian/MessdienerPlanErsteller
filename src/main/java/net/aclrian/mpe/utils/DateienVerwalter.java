package net.aclrian.mpe.utils;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.ReadFile;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.ReadFilePfarrei;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Sonstige Klasse, die viel mit Ordnerverwaltung und Sortieren zu tun hat.
 *
 * @author Aclrian
 */
public class DateienVerwalter {
    public static final String PFARREDATEIENDUNG = ".xml.pfarrei";
    public static final String TEXTDATEI = File.separator + ".messdienerOrdnerPfad.txt";
    private static DateienVerwalter dateienVerwalter;
    private final Window window;
    /**
     * Hier wird der Pfad gespeichert, indem die Messdiener gespeichert werden
     * sollen / sind <br>
     * andere Klassen erzeugen hiermit neue Messdiener an dem selben Ort</br>
     */
    private String savepath;
    private Pfarrei pf;
    private ArrayList<Messdiener> medis;

    private DateienVerwalter(Window window) throws NoSuchPfarrei {
        this.window = window;
        this.getSpeicherort();
        File f = getPfarreFile();
        if (f == null) {
            throw new NoSuchPfarrei(savepath);
        }
        pf = ReadFilePfarrei.getPfarrei(f.getAbsolutePath());
    }

    public static DateienVerwalter getDateienVerwalter() {
        return dateienVerwalter;
    }

    public static void reStart(Window window) throws NoSuchPfarrei {
        dateienVerwalter = new DateienVerwalter(window);
    }

    public Pfarrei getPfarrei() {
        if (pf == null) {
            reloadPfarrei();
        }
        return pf;
    }

    private void reloadPfarrei() {
        File f = getPfarreFile();
        if (f == null) {
            Dialogs.fatal("Es konnte keine Pfarrei gefunden werden.");
            return;
        }
        Log.getLogger().info("Pfarrei gefunden in: " + f);
        pf = ReadFilePfarrei.getPfarrei(f.getAbsolutePath());
    }

    private ArrayList<File> getPfarreiFiles() {
        ArrayList<File> files = new ArrayList<>();
        File f = new File(savepath);
        for (File file : Objects.requireNonNull(f.listFiles())) {
            String s = file.toString();
            if (s.endsWith(PFARREDATEIENDUNG)) {
                files.add(file);
            }

        }
        return files;
    }

    private File getPfarreFile() {
        ArrayList<File> files = getPfarreiFiles();
        if (files.size() != 1) {
            if (files.size() > 1) {
                Dialogs.warn("Es darf nur eine Datei mit der Endung: '" + PFARREDATEIENDUNG + "' in dem Ordner: " + savepath + " vorhanden sein.");
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
        for (File f : files) {
            if (!f.getAbsolutePath().contentEquals(neuePfarrei.getAbsolutePath())) {
                todel.add(f);
            } else candel = true;
        }
        if (candel) todel.forEach(file -> {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @return Ausgewaehlten Ordnerpfad
     */
    private String waehleOrdner() {
        DirectoryChooser f = new DirectoryChooser();
        String s = "Ordner wählen, in dem alles gespeichert werden soll:";
        f.setTitle(s);
        File file = f.showDialog(window);
        return file == null ? null : file.getPath();
    }

    private List<File> getPaths(File file, List<File> list) {
        if (file == null || list == null || !file.isDirectory())
            return Collections.emptyList();
        File[] fileArr = file.listFiles();
        assert fileArr != null;
        for (File f : fileArr) {
            if (f.isDirectory()) {
                getPaths(f, list);
            }
            list.add(f);
        }
        return list;
    }

    private List<File> getAlleMessdienerFiles(String path) {// 2
        Log.getLogger().info("verzName: " + path);
        return getPaths(new File(path), new ArrayList<>());
    }

    /**
     * @return Messdiener als List
     */
    public List<Messdiener> getAlleMedisVomOrdnerAlsList() {
        if (pf == null) {
            reloadPfarrei();
        }
        if (medis == null) {
            List<File> files = getAlleMessdienerFiles(savepath);
            medis = new ArrayList<>();
            for (File file : files) {
                ReadFile rf = new ReadFile();
                Messdiener m = rf.getMessdiener(file.getAbsolutePath());
                if (m != null) {
                    medis.add(m);
                }
            }
            for (Messdiener medi : medis) {
                medi.setnewMessdatenDaten();
            }
        }
        return medis;
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
        String homedir = System.getProperty("user.home");
        homedir = homedir + TEXTDATEI;
        File f = new File(homedir);
        try {
            Files.delete(f.toPath());
            savepath = "";
            getSpeicherort();
        } catch (IOException e) {
            Dialogs.warn("Konnte die Datei " + homedir + " nicht ändern.");
            e.printStackTrace();
        }
    }

    private void getSpeicherort() {
        String homedir = System.getProperty("user.home");
        homedir = homedir + TEXTDATEI;
        Log.getLogger().info("Das Home-Verzeichniss wurde gefunden: " + homedir);
        File f = new File(homedir);
        if (!f.exists()) {
            createSaveFile(homedir);
        } else {
            if (readSaveFile(homedir, f)) return;
        }
        Log.getLogger().info("Der Speicherort liegt in: " + savepath);
    }

    private boolean readSaveFile(String homedir, File f) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(homedir), StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                savepathNotFound(f, null);
                return true;
            }
            File saveFile = new File(line);
            if (saveFile.exists()) {
                setSavepath(line);
            } else {
                savepathNotFound(f, line);
            }
            if (this.savepath == null || this.savepath.equals("")) {
                Files.delete(f.toPath());
                getSavepath();
            }
        } catch (IOException e) {
            Dialogs.error(e, "Die Datei '" + homedir + "' konnte nicht gelesen werden.");
        }
        if (savepath == null) {
            savepath = waehleOrdner();
            try (
                    FileWriter fileWriter = new FileWriter(homedir);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                if (savepath != null) {
                    bufferedWriter.write(savepath);
                } else {
                    Dialogs.warn("Es wird ein Speicherort benötigt, um dort Messdiener zu speichern.\nBitte einen Speicherort eingeben!");
                    getSpeicherort();
                }
            } catch (IOException e) {
                Log.getLogger().info("Auf den Speicherort '" + f + "' kann nicht zugegriffen werden!");
                getSpeicherort();
            }
        }
        return false;
    }

    private void createSaveFile(String homedir) {
        String s;
        if (savepath == null || savepath.equals("")) {
            s = this.waehleOrdner();
        } else {
            s = savepath;
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(homedir))) {
            if (s != null) {
                Log.getLogger().info(s);
                bufferedWriter.write(s);
                setSavepath(s);
            } else {
                savepath = "";
                Dialogs.warn("Es wird ein Speicherort benötigt, um dort Messdiener zu speichern.\nBitte einen Speicherort eingeben!");
                getSpeicherort();
            }
        } catch (IOException e) {
            Dialogs.error(e, "Der Speicherort konnte nicht gespeichert werden.");
        }
    }

    private void savepathNotFound(File f, String line) {
        Log.getLogger().info("Der Speicherort aus '" + f + "' ('" + line + "') existiert nicht!");
        try {
            Files.delete(f.toPath());
            getSpeicherort();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void reloadMessdiener() {
        medis = null;
    }

    public static class NoSuchPfarrei extends Exception {
        private final String savepath;

        public NoSuchPfarrei(String savepath) {
            this.savepath = savepath;
        }

        public String getSavepath() {
            return savepath;
        }
    }
}
