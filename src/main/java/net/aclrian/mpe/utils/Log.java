package net.aclrian.mpe.utils;

import org.apache.log4j.*;
import javax.swing.filechooser.FileSystemView;

import java.io.File;

public class Log {
	public static final Logger LOGGER = Logger.getLogger(Log.class);

	public static Logger getLogger() {
		return LOGGER;
	};

	static {
		init();
	}

	public static void init() {
		try {
			new File(FileSystemView.getFileSystemView().getDefaultDirectory() + File.separator
					+ "MessdienerplanErsteller").mkdir();
			Layout layout = new PatternLayout("[%d{yyyy-MM-dd HH:MM:ss}] [%-5p] [%l]: %m%n");
			FileAppender fileAppender = new FileAppender( layout, getLogFile().getAbsolutePath(), true );
			LOGGER.addAppender( fileAppender );
			LOGGER.setLevel( Level.ALL );
			LOGGER.info("-------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File getLogFile() {
		return new File(getWorkingDir().getAbsolutePath() + File.separator + "MpE" + ".log");
	}

	public static File getWorkingDir(){
		File f = new File(FileSystemView.getFileSystemView().getDefaultDirectory() + File.separator
				+ "MessdienerplanErsteller");
		if(!f.exists()) f.mkdir();
		return f;
	}
}
