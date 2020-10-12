package net.aclrian.mpe.utils;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Speicherort {

    public static final String TEXTDATEI = File.separator + ".messdienerOrdnerPfad.txt";
    private final Window window;
    /**
     * Hier wird der Pfad gespeichert, indem die Messdiener gespeichert werden
     * sollen / sind <br>
     * andere Klassen erzeugen hiermit neue Messdiener an dem selben Ort</br>
     */
    private String speicherortString;

    public Speicherort(Window window) {
        this.window = window;
        genaerateSpeicherort();
    }

    private void genaerateSpeicherort() {
        String homedir = System.getProperty("user.home");
        homedir = homedir + TEXTDATEI;
        Log.getLogger().info("Das Home-Verzeichniss wurde gefunden: " + homedir);
        File f = new File(homedir);
        if (!f.exists()) {
            createSaveFile(homedir);
        } else {
            if (readSaveFile(homedir, f))
                return;
        }
        Log.getLogger().info("Der Speicherort liegt in: " + speicherortString);
    }

    private boolean readSaveFile(String homedir, File f) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(homedir), StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                speicherortNotFound(f, null);
                return true;
            }
            File saveFile = new File(line);
            if (saveFile.exists()) {
                setSpeicherortString(line);
            } else {
                speicherortNotFound(f, line);
            }
            if (this.speicherortString == null || this.speicherortString.equals("")) {
                Files.delete(f.toPath());
                getSpeicherortString();
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Die Datei '" + homedir + "' konnte nicht gelesen werden.");
        }
        if (speicherortString == null) {
            speicherortString = waehleOrdner();
            try (FileWriter fileWriter = new FileWriter(homedir);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                if (speicherortString != null) {
                    bufferedWriter.write(speicherortString);
                } else {
                    Dialogs.getDialogs().warn(
                            "Es wird ein Speicherort benötigt, um dort Messdiener zu speichern.\nBitte einen Speicherort eingeben!");
                    getSpeicherortString();
                }
            } catch (IOException e) {
                Log.getLogger().info("Auf den Speicherort '" + f + "' kann nicht zugegriffen werden!");
                getSpeicherortString();
            }
        }
        return false;
    }

    private void createSaveFile(String homedir) {
        String s;
        if (speicherortString == null || speicherortString.equals("")) {
            s = waehleOrdner();
        } else {
            s = speicherortString;
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(homedir))) {
            if (s != null) {
                Log.getLogger().info(s);
                bufferedWriter.write(s);
                setSpeicherortString(s);
            } else {
                speicherortString = "";
                Dialogs.getDialogs().warn(
                        "Es wird ein Speicherort benötigt, um dort Messdiener zu speichern.\nBitte einen Speicherort eingeben!");
                getSpeicherortString();
            }
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Der Speicherort konnte nicht gespeichert werden.");
        }
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

    public String getSpeicherortString() {
        if (speicherortString == null || speicherortString.isEmpty()) {
            speicherortString = waehleOrdner();
        }
        return speicherortString;
    }

    private void setSpeicherortString(String speicherortString) {
        this.speicherortString = speicherortString;
    }

    private void speicherortNotFound(File f, String line) {
        Log.getLogger().info("Der Speicherort aus '" + f + "' ('" + line + "') existiert nicht!");
        try {
            Files.delete(f.toPath());
            getSpeicherortString();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void changeDir() {
        try {
            String homedir = System.getProperty("user.home");
            homedir = homedir + TEXTDATEI;
            Files.delete(new File(homedir).toPath());
            speicherortString = "";
            getSpeicherortString();
        } catch (IOException e) {
            Dialogs.getDialogs().error(e, "Konnte den Speicherort nicht wechseln: " + e.getLocalizedMessage());
        }
    }
}
