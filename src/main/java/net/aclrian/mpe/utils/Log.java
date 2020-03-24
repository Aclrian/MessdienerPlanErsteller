package net.aclrian.mpe.utils;

import org.apache.log4j.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class Log {
	public static final Logger LOGGER = Logger.getLogger(Log.class);//Thread.currentThread().getStackTrace()[0].getClassName());

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
		/*	FileHandler fh = new FileHandler(getWorkingDir().getAbsolutePath() + File.separator + "MpE" + ".log", true);
			fh.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					SimpleDateFormat logTime = new SimpleDateFormat("HH:mm:ss");
					Calendar cal = new GregorianCalendar();
					cal.setTimeInMillis(record.getMillis());
					return "[" + logTime.format(cal.getTime()) + "] [" + record.getLevel() + "] ["
							+ record.getSourceClassName() + "." + record.getSourceMethodName() + "]: "
							+ record.getMessage() + "\n";
				}
			});
		//	LOGGER.addHandler(fh);
*/			//SimpleLayout
// %d{yyyy-MM-dd}-%t-%x-%-5p-%-10c:%m%n
			Layout layout = new PatternLayout("[%d{yyyy-MM-dd HH:MM:ss}] [%-5p] [%l]: %m%n");
			/*ConsoleAppender consoleAppender = new ConsoleAppender( layout );
			LOGGER.addAppender( consoleAppender );*/
			FileAppender fileAppender = new FileAppender( layout, getWorkingDir().getAbsolutePath() + File.separator + "MpE" + ".log", true );
			LOGGER.addAppender( fileAppender );
			LOGGER.setLevel( Level.ALL );
			LOGGER.info("-------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File getWorkingDir(){
		File f = new File(FileSystemView.getFileSystemView().getDefaultDirectory() + File.separator
				+ "MessdienerplanErsteller");
		if(!f.exists()) f.mkdir();
		return f;
	}
}
