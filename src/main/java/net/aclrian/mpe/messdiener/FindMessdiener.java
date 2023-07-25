package net.aclrian.mpe.messdiener;

import net.aclrian.mpe.controller.MediController;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import net.aclrian.mpe.utils.RemoveDoppelte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FindMessdiener {
    private final RemoveDoppelte<Messdiener> rd = new RemoveDoppelte<>();

    public FindMessdiener(Messdaten messdaten) { }

    public List<Messdiener> updateFreunde(Messdiener m) throws CouldFindMessdiener {
        return update(true, m.getFreunde(), m);
    }

    public List<Messdiener> updateGeschwister(Messdiener m) throws CouldFindMessdiener {
        return update(false, m.getGeschwister(), m);
    }

    private List<Messdiener> update(boolean isFreund, String[] strings, Messdiener m) throws CouldFindMessdiener {
        ArrayList<Messdiener> updatedList = new ArrayList<>();
        for (String value : strings) {
            Messdiener medi = null;
            if (!value.equals("") && !value.equals("LEER") && !value.equals("Vorname, Nachname")) {
                try {
                    medi = sucheMessdiener(value, m);
                } catch (CouldNotFindMessdiener e) {
                    messdienerNotFound(isFreund, strings, m, value, e);
                }
                if (medi != null) {
                    updatedList.add(medi);
                }
            }
        }
        return rd.removeDuplicatedEntries(updatedList);
    }

    public void messdienerNotFound(boolean isFreund, String[] s, Messdiener m, String value, CouldNotFindMessdiener e) {
        boolean beheben = Dialogs.getDialogs().frage(e.getMessage(),
                "ignorieren", "beheben");
        if (beheben) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
            list.remove(value);
            String[] gew = MediController.getArrayString(list, isFreund ? Messdiener.LENGHT_FREUNDE : Messdiener.LENGHT_GESCHWISTER);
            if (isFreund) {
                m.setFreunde(gew);
            } else {
                m.setGeschwister(gew);
            }
            WriteFile wf = new WriteFile(m);
            try {
                wf.saveToXML();
            } catch (IOException ex) {
                Dialogs.getDialogs().error(ex, "Konnte es nicht beheben.");
            }
            DateienVerwalter.getInstance().reloadMessdiener();
        }
    }

    public Messdiener sucheMessdiener(String geschwi, Messdiener akt) throws CouldFindMessdiener, CouldNotFindMessdiener {
        for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
            if (messdiener.toString().equals(geschwi)) {
                return messdiener;
            }
        }
        // additional Search for Vorname and Nachname
        String replaceSeparators = geschwi.replace(", ", " ")
                .replace("-", " ").replace("; ", " ").toLowerCase(Locale.getDefault());
        String[] parts = replaceSeparators.split(" ");
        Arrays.sort(parts);
        for (Messdiener messdiener : DateienVerwalter.getInstance().getMessdiener()) {
            String[] parts2 = messdiener.toString().replace(", ", " ")
                    .replace("-", " ").replace("; ", " ").toLowerCase(Locale.getDefault()).split(" ");
            Arrays.sort(parts2);
            if (Arrays.equals(parts, parts2)) {
                String message = "Konnte für " + akt.toString() + " : " + geschwi + " finden";
                MPELog.getLogger().info(message);
                throw new CouldFindMessdiener(messdiener.toString(), geschwi, message);
            }
        }

        throw new CouldNotFindMessdiener("Konnte für " + akt.toString() + " : " + geschwi + " nicht finden");
    }

    public static class CouldNotFindMessdiener extends Exception {
        public CouldNotFindMessdiener(String message) {
            super(message);
        }
    }

    public static class CouldFindMessdiener extends Exception {
        private final String messdienerID;
        private final String string;

        public CouldFindMessdiener(String foundID, String string, String message) {
            super(message);
            this.messdienerID = foundID;
            this.string = string;
        }

        public String getFoundMessdienerID() {
            return messdienerID;
        }

        public String getString() {
            return string;
        }
    }
}
