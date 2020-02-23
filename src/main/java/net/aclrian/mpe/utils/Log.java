package net.aclrian.mpe.utils;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {
	public static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

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
			FileHandler fh = new FileHandler(new JFileChooser().getFileSystemView().getDefaultDirectory()
					+ File.separator + "MessdienerplanErsteller" + File.separator + "MpE" + ".log", true);
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
			LOGGER.addHandler(fh);
			LOGGER.info("-------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
