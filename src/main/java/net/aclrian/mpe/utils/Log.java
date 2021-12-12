package net.aclrian.mpe.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Log {
    public static final String PROGRAMM_NAME = "MessdienerplanErsteller";
    private static final Logger LOGGER;

    static {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.reconfigure();
        LOGGER = LogManager.getLogger(Log.class);
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
                Files.createDirectories(getWorkingDir().toPath());
            }
            final Path dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), PROGRAMM_NAME).toPath();
            if (Files.notExists(dir)) {
                Files.createFile(dir);
            }
            LOGGER.info("-------------------------------");
        } catch (IOException e) {
            Log.getLogger().error(e.getMessage(), e);
        }
    }

    public static File getLogFile() {
        File f = new File("MpE.log");
        assert f.exists();
        return f;
    }

    public static File getWorkingDir() {
        return new File(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath(), PROGRAMM_NAME);
    }
}
