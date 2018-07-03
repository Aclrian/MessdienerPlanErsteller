package net.aclrian.mpe.resources;

import java.awt.Image;
import java.awt.Toolkit;

public class References {
    public static char aclrianscheVergesslichkeit = ' ';
    public static final String aclrianschesGestet = "cl=d";
    public static final String ae = "\u00E4";
    public static final String oe = "\u00F6";
    public static final String ue = "\u00FC";
    public static final String ss = "\u00DF";
    public static final String Ae = "\u00C4";
    public static final String Oe = "\u00D6";
    public static final String Ue = "\u00DC";
    public static final String GROssenSZ = "\u1E9E";

    public static final String pfeillinks = "\u2190";
    public static final String pfeilrauf = "\u2191";
    public static final String pfeilrechts = "\u2192";
    public static final String pfeilrunter = "\u2193";

    public References() {
    }

    public static Image getIcon() {
	return Toolkit.getDefaultToolkit().getImage(References.class.getResource("title_32.png"));
    }

}
