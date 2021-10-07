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

public class DateienVerwalter implements IDateienVerwalter {
    public static final String PFARREDATEIENDUNG = ".xml.pfarrei";
    public static final String MESSDIENERDATEIENDUNG = ".xml";
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
    public File getSavepath() {
        return dir;
    }

    @Override
    public void reloadMessdiener() {
        medis = null;
    }

    @Override
    public List<Messdiener> getMessdiener() {
        if (medis == null) {
            ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{MESSDIENERDATEIENDUNG.substring(1)}, true));
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
        ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{PFARREDATEIENDUNG.substring(1)}, true));
        File pfarreiFile;
        if (files.size() != 1) {
            if (files.size() > 1) {
                Dialogs.getDialogs().warn("Es darf nur eine Datei mit der Endung: '" + PFARREDATEIENDUNG + "' in dem Ordner: "
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
    public void removeoldPfarrei(File neuePfarrei) {
        ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(dir, new String[]{PFARREDATEIENDUNG.substring(1)}, true));
        ArrayList<File> todel = new ArrayList<>();
        boolean candel = false;
        for (File f : files) {
            if (!f.getAbsolutePath().contentEquals(neuePfarrei.getAbsolutePath())) {
                todel.add(f);
            } else {
                candel = true;
            }
        }
        if (candel)
            todel.forEach(file -> {
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
