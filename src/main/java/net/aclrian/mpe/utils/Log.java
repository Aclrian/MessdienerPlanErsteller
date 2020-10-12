package net.aclrian.mpe.utils;

import org.apache.log4j.*;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Log {
    private static final Logger LOGGER = Logger.getLogger(Log.class);
    public static final String PROGRAMM_NAME = "MessdienerplanErsteller";

    static {
        init();
    }

    private Log() {
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void init() {
        try {
            if (Files.notExists(getWorkingDir().toPath())) {
                Files.createFile(getWorkingDir().toPath());
            }
            Files.createDirectories(new File(FileSystemView.getFileSystemView().getDefaultDirectory(), PROGRAMM_NAME).toPath());
            Layout layout = new PatternLayout("[%d{yyyy-MM-dd HH:MM:ss}] [%-5p] [%l]: %m%n");
            FileAppender fileAppender = new FileAppender(layout, getLogFile().getAbsolutePath(), true);
            LOGGER.addAppender(fileAppender);
            LOGGER.setLevel(Level.ALL);
            LOGGER.info("-------------------------------");
        } catch (IOException e) {
            Log.getLogger().error(e.getMessage(), e);
        }
    }

    public static File getLogFile() {
        return new File(getWorkingDir().getAbsolutePath(), "MpE" + ".log");
    }

    public static File getWorkingDir() {
        return new File(FileSystemView.getFileSystemView().getDefaultDirectory(), PROGRAMM_NAME);
    }
}
