package net.aclrian.mpe;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class ApplikationStarter {
    private static final URI wiki = URI.create("https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Installationshinweise");

    public static void main(String[] args) {
        try {
            Class.forName("javafx.application.Application");
            Main.main(args);
        } catch (ClassNotFoundException e) {
            int i = JOptionPane.showConfirmDialog(new JFrame(), "JAVAFX-Komponenten nicht gefunden!\nSoll die Hilfe-Website geöffnet werden?", "MessdienerplanErsteller", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            if (i == 0) {
                try {
                    Desktop.getDesktop().browse(wiki);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(new JFrame(), ex.getLocalizedMessage(), "Hat leider nicht geklappt.", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
