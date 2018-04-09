package net.aclrian.messdiener.utils;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Utilities {


	public static String getLoggerInfo(Class<?> loggingclass, String methodenName) {
		return "[" + loggingclass.getName() + ":" + methodenName + "] ";
	}

	public static void logging(Class<?> loggedclass, String methodenname, String mitteilung) {
		//aos.
		if (methodenname == null) {
			methodenname = "init";
		}
		System.out.println(getLoggerInfo(loggedclass, methodenname) + mitteilung);
	}
	
	public static void logging(Class<?> loggedclass, Constructor<?> c, String mitteilung){
		Utilities.logging(loggedclass, "init", mitteilung);
	}
	public static Rectangle setFrameMittig(int weite, int hoehe) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point p = env.getCenterPoint();
		double Mx = p.getX();
		double My = p.getY();
		double x = Mx - (weite / 2);
		double y = My - (hoehe / 2);

		return new Rectangle((int) x, (int) y, weite, hoehe);
	}

	public static Rectangle setFrameMittig(double weite, double hoehe) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point p = env.getCenterPoint();
		double Mx = p.getX();
		double My = p.getY();
		double x = Mx - (weite / 2);
		double y = My - (hoehe / 2);
		return new Rectangle((int) x, (int) y, (int) weite, (int) hoehe);
	}

	public static void logging(Class<?> class1, Method enclosingMethod, String mitteilung) {
		String methname;
		try {
			methname = enclosingMethod.getName();
		} catch (Exception e) {
			methname = "unknown";
		}
		logging(class1, methname, mitteilung);
		
	}

}
