package net.aclrian.mpe.utils;

import javafx.stage.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.pfarrei.*;
import org.apache.commons.io.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public class DateienVerwalter {
    public static final String PFARREI_DATEIENDUNG = ".xml.pfarrei";
    public static final String MESSDIENER_DATEIENDUNG = ".xml";
    private static DateienVerwalter instance;
    private final File dir;
    private Pfarrei pf;
    private List<Messdiener> medis;
    private FileOutputStream pfarreiFos;
    private FileLock lock;
    private boolean messdienerAlreadyNull = false;

    public DateienVerwalter(String path) throws NoSuchPfarrei {
        this.dir = new File(path);
        lookForPfarreiFile();
        Thread thread = new Thread(() -> {
            try (WatchService service = dir.toPath().getFileSystem().newWatchService()) {
                dir.toPath().register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                while (true) {
                    useKey(service);
                }
            } catch (IOException e) {
                Log.getLogger().error(e.getMessage(), e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static DateienVerwalter getInstance() {
        return instance;
    }

    public static void setInstance(DateienVerwalter instance) {
        DateienVerwalter.instance = instance;
    }

    public static void reStart(Stage stage) throws NoSuchPfarrei {
        Speicherort ort = new Speicherort(stage);
        setInstance(new DateienVerwalter(ort.getSpeicherortString()));
    }

    private void useKey(WatchService service) {
        WatchKey key;
        try {
            key = service.take();
            key.pollEvents();
            if (!messdienerAlreadyNull) reloadMessdiener();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public File getSavePath() {
        return dir;
    }

    public void reloadMessdiener() {
        messdienerAlreadyNull = true;
        medis = null;
    }

    public List<Messdiener> getMessdiener() {
        if (medis == null) {
            ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{MESSDIENER_DATEIENDUNG.substring(1)}, true));
            medis = new ArrayList<>();
            files.forEach(file -> {
                ReadFile rf = new ReadFile();
                medis.add(rf.getMessdiener(file));
            });
            for (Messdiener m : medis) {
                try {
                    m.setNewMessdatenDaten();
                } catch (Messdaten.CouldFindMessdiener e) {
                    for (int i = 0; i < Messdiener.LENGHT_FREUNDE; i++) {
                        if (m.getFreunde()[i].equalsIgnoreCase(e.getString())) {
                            m.getFreunde()[i] = e.getMessdienerID();
                        }
                    }
                    for (int i = 0; i < Messdiener.LENGHT_GESCHWISTER; i++) {
                        if (m.getGeschwister()[i].equalsIgnoreCase(e.getString())) {
                            m.getGeschwister()[i] = e.getMessdienerID();
                        }
                    }
                    m.makeXML();
                    return getMessdiener();
                }
            }
            messdienerAlreadyNull = false;
        }
        return medis;
    }

    //Pfarrei
    private void lookForPfarreiFile() throws NoSuchPfarrei {
        ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{PFARREI_DATEIENDUNG.substring(1)}, true));
        File pfarreiFile;
        if (files.size() != 1) {
            if (files.size() > 1) {
                Dialogs.getDialogs().warn("Es darf nur eine Datei mit der Endung: '" + PFARREI_DATEIENDUNG + "' in dem Ordner: "
                        + dir + " vorhanden sein.");
                pfarreiFile = files.get(0);
            } else {
                throw new NoSuchPfarrei(dir);
            }
        } else {
            pfarreiFile = files.get(0);
        }
        Log.getLogger().info("Pfarrei gefunden in: {}", pfarreiFile);
        try {
            pf = ReadFilePfarrei.getPfarrei(pfarreiFile.getAbsolutePath());
            pfarreiFos = new FileOutputStream(pfarreiFile, true);
            FileChannel channel = pfarreiFos.getChannel();
            lock = channel.lock();
        } catch (Exception e) {
            Dialogs.getDialogs().fatal(e, "Pfarrei-Datei konnte nicht vom Programm gehalten werden: " + e.getLocalizedMessage());
        }
    }

    public FileLock getLock() {
        return lock;
    }

    public FileOutputStream getPfarreiFileOutputStream() {
        return pfarreiFos;
    }

    public void removeOldPfarrei(File neuePfarrei) {
        ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{PFARREI_DATEIENDUNG.substring(1)}, true));
        ArrayList<File> toDel = new ArrayList<>();
        boolean candel = false;
        for (File f : files) {
            if (!f.getAbsolutePath().contentEquals(neuePfarrei.getAbsolutePath())) {
                toDel.add(f);
            } else {
                candel = true;
            }
        }
        if (candel)
            toDel.forEach(file -> {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    public Pfarrei getPfarrei() {
        return pf;
    }

    public static class NoSuchPfarrei extends Exception {
        private final File savepath;

        public NoSuchPfarrei(File savepath) {
            this.savepath = savepath;
        }

        public File getSavepath() {
            return savepath;
        }
    }
}
