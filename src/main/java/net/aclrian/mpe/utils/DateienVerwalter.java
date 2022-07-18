package net.aclrian.mpe.utils;

import javafx.stage.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.pfarrei.*;
import org.apache.commons.io.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public class DateienVerwalter implements IDateienVerwalter {
    public static final String PFARREI_DATEIENDUNG = ".xml.pfarrei";
    public static final String MESSDIENER_DATEIENDUNG = ".xml";
    private static IDateienVerwalter instance;
    private final File dir;
    private Pfarrei pf;
    private List<Messdiener> medis;
    private FileOutputStream pfarreiFos;
    private FileLock lock;

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
                Dialogs.getDialogs().warn("");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static IDateienVerwalter getInstance() {
        return instance;
    }

    public static void setInstance(IDateienVerwalter instance) {
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
            reloadMessdiener();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public File getSavePath() {
        return dir;
    }

    @Override
    public void reloadMessdiener() {
        medis = null;
    }

    @Override
    public List<Messdiener> getMessdiener() {
        if (medis == null) {
            ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{MESSDIENER_DATEIENDUNG.substring(1)}, true));
            medis = new ArrayList<>();
            files.forEach(file -> {
                ReadFile rf = new ReadFile();
                medis.add(rf.getMessdiener(file));
            });
            medis.forEach(Messdiener::setnewMessdatenDaten);
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

    @Override
    public FileLock getLock() {
        return lock;
    }

    @Override
    public FileOutputStream getPfarreiFileOutputStream() {
        return pfarreiFos;
    }

    @Override
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

    @Override
    public Pfarrei getPfarrei() {
        return pf;
    }
}
