package net.aclrian.mpe;


import net.aclrian.mpe.utils.MPELog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class ApplikationStarter { //NOPMD - suppressed UseUtilityClass - contains only main method
    private static final URI WIKI = URI.create("https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Installationshinweise");

    public static void main(String[] args) {
        try {
            Class.forName("javafx.application.Application");
            MainApplication.main(args);
        } catch (ClassNotFoundException e) {
            MPELog.getLogger().error(e.getMessage(), e);
            int i = JOptionPane.showConfirmDialog(new JFrame(), "JAVAFX-Komponenten nicht gefunden!\nSoll die Hilfe-Website geöffnet werden?", "MessdienerplanErsteller", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            if (i == 0) {
                try {
                    Desktop.getDesktop().browse(WIKI);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(new JFrame(), ex.getLocalizedMessage(), "Hat leider nicht geklappt.", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
