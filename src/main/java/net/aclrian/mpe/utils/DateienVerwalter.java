package net.aclrian.mpe.utils;


import javafx.stage.Stage;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.ReadFile;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.pfarrei.ReadFilePfarrei;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

public class DateienVerwalter {
    public static final String PFARREI_DATEIENDUNG = ".xml.pfarrei";
    public static final String MESSDIENER_DATEIENDUNG = ".xml";
    private static DateienVerwalter instance;
    private final File dir;
    private Pfarrei pf;
    private List<Messdiener> medis;
    private FileLock lock;
    private boolean messdienerAlreadyNull = false;

    public DateienVerwalter(String path) throws NoSuchPfarrei {
        this.dir = new File(path);
        lookForPfarreiFile();
        Thread thread = new Thread(() -> {
            try (WatchService service = dir.toPath().getFileSystem().newWatchService()) {
                dir.toPath().register(
                        service,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.OVERFLOW
                );
                while (true) {
                    useKey(service);
                }
            } catch (IOException e) {
                MPELog.getLogger().error(e.getMessage(), e);
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
            if (!messdienerAlreadyNull) {
                reloadMessdiener();
            }
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
                    m.setNewMessdatenDaten();
            }
            messdienerAlreadyNull = false;
        }
        return medis;
    }

    //Pfarrei
    private void lookForPfarreiFile() throws NoSuchPfarrei {
        ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{PFARREI_DATEIENDUNG.substring(1)}, true));
        File pfarreiFile;
        if (files.size() == 1) {
            pfarreiFile = files.get(0);
        } else {
            if (files.size() > 1) {
                Dialogs.getDialogs().warn("Es darf nur eine Datei mit der Endung: '" + PFARREI_DATEIENDUNG + "' in dem Ordner: "
                        + dir + " vorhanden sein.");
                pfarreiFile = files.get(0);
            } else {
                throw new NoSuchPfarrei(dir);
            }
        }
        MPELog.getLogger().info("Pfarrei gefunden in: {}", pfarreiFile);
        try {
            pf = ReadFilePfarrei.getPfarrei(pfarreiFile.getAbsolutePath());
            FileOutputStream pfarreiFos = new FileOutputStream(pfarreiFile, true);
            FileChannel channel = pfarreiFos.getChannel();
            lock = channel.lock();
        } catch (Exception e) {
            Dialogs.getDialogs().fatal(e, "Pfarrei-Datei konnte nicht vom Programm gehalten werden: " + e.getLocalizedMessage());
        }
    }

    public FileLock getLock() {
        return lock;
    }

    public void removeOldPfarrei(File neuePfarrei) throws IOException {
        ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{PFARREI_DATEIENDUNG.substring(1)}, true));
        ArrayList<File> toDel = new ArrayList<>();
        boolean candel = false;
        for (File f : files) {
            if (f.getAbsolutePath().contentEquals(neuePfarrei.getAbsolutePath())) {
                candel = true;
            } else {
                toDel.add(f);
            }
        }
        if (candel) {
            getInstance().getLock().release();
            toDel.forEach(file -> {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    MPELog.getLogger().error(e);
                }
            });
        }
    }

    public Pfarrei getPfarrei() {
        return pf;
    }

    public static class NoSuchPfarrei extends Exception {
        private final File savepath;

        public NoSuchPfarrei(File savepath) {
            super();
            this.savepath = savepath;
        }

        public File getSavepath() {
            return savepath;
        }
    }
}
