package net.aclrian.mpe.utils;


import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
        generateSpeicherort();
    }

    private void generateSpeicherort() {
        String homedir = System.getProperty("user.home");
        homedir = homedir + TEXTDATEI;
        MPELog.getLogger().info("Das Home-Verzeichnis wurde gefunden: {}", homedir);
        File f = new File(homedir);
        if (f.exists()) {
            readSaveFile(homedir, f);
        } else {
            createSaveFile(homedir);
        }
        MPELog.getLogger().info("Der Speicherort liegt in: {}", speicherortString);
    }

    private void readSaveFile(String homedir, File f) {
        String line = "";
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(homedir), StandardCharsets.UTF_8))) {
            line = bufferedReader.readLine();
            if (line == null) {
                line = "";
            }
        } catch (IOException ignored) { }
        File saveFile = new File(line);
        if (saveFile.exists()) {
            speicherortString = line;
        } else {
            MPELog.getLogger().info("Der Speicherort aus '{}' ('{}') existiert nicht!", f, line);
            try {
                Files.delete(f.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(-1);
            }
            do {
                speicherortString = waehleOrdner();
            } while (!new File(speicherortString).exists());
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
                MPELog.getLogger().info("Auf den Speicherort '{}' kann nicht zugegriffen werden!", f);
                getSpeicherortString();
            }

        }
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
                MPELog.getLogger().info(s);
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
     * @return Ausgewählten Ordnerpfad
     */
    private String waehleOrdner() {
        DirectoryChooser f = new DirectoryChooser();
        String s = "Ordner wählen, in dem alles gespeichert werden soll:";
        f.setTitle(s);
        File file = f.showDialog(window);
        return file == null ? "" : file.getPath();
    }

    public static File waehleDatei(Window window, FileChooser.ExtensionFilter filter, String title) {
        FileChooser f = new FileChooser();
        f.getExtensionFilters().add(filter);
        f.setTitle(title);
        return f.showOpenDialog(window);
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
