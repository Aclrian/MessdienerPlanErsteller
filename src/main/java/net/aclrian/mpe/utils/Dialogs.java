package net.aclrian.mpe.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

public class Dialogs {

	public static void info(String string) {
		Log.getLogger().info(string);
		Alert a = new Alert(AlertType.INFORMATION);
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void warn(String string) {
		Log.getLogger().warning(string);
		Alert a = new Alert(AlertType.WARNING);
		a.setHeaderText(string);
		a.showAndWait();
	}

	public static void error(String string) {		
		Log.getLogger().severe(string);
		Alert a = new Alert(AlertType.ERROR);
		a.setHeaderText(string);
		a.showAndWait();
	}
	
	public static void error(Exception e, String string) {
		Log.getLogger().severe(string);
		Log.getLogger().severe(e.getMessage());
		try {
		Log.getLogger().severe(e.getCause().toString());	
		} catch(NullPointerException e1) {
			Log.getLogger().severe("no cause");
		}
		Alert a = new Alert(AlertType.ERROR);
		a.setHeaderText(string);
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		VBox vb = new VBox();
		Label l = new Label("Stacktrace:");
		TextArea ta = new TextArea(sw.toString());
		ta.setEditable(false);
		vb.getChildren().addAll(l,ta);
		a.getDialogPane().setExpandableContent(vb);
		a.showAndWait();
	}

	public static void open(URI open, String string) throws IOException {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setHeaderText(string);
		Optional<ButtonType> res = a.showAndWait();
		if (res.get() == ButtonType.OK) {
			Desktop.getDesktop().browse(open);
		}
	}
	
	public static boolean frage(String string) {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setHeaderText(string);
		Optional<ButtonType> res = a.showAndWait();
		return res.get() == ButtonType.OK;
	}

	public static void fatal(String string) {
		error(string);
		System.exit(-1);
	}

	public static String text(String string, String kurz) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Eingabe");
		dialog.setHeaderText(string);
		dialog.setContentText(kurz +":");
		Optional<String> result = dialog.showAndWait();
		if(result.isEmpty()) {
			return "";
		}
		return result.get();
	}
}
