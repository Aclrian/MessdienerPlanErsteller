package net.aclrian.mpe.messdiener;

import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import net.aclrian.mpe.utils.RemoveDoppelte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FindMessdiener {
    private static final RemoveDoppelte<Messdiener> REMOVE_DUPLICATES = new RemoveDoppelte<>();

    private FindMessdiener() { }

    public static List<Messdiener> updateFreunde(Messdiener m) {
        return update(m.getFreunde(), m, Messdiener.AnvertrauteHandler.FREUNDE);
    }

    public static List<Messdiener> updateGeschwister(Messdiener m) {
        return update(m.getGeschwister(), m, Messdiener.AnvertrauteHandler.GESCHWISTER);
    }

    private static List<Messdiener> update(String[] strings, Messdiener m, Messdiener.AnvertrauteHandler handler) {
        ArrayList<Messdiener> updatedList = new ArrayList<>();
        for (String value : strings) {
            Messdiener medi = null;
            if (!value.equals("") && !value.equals("LEER") && !value.equals("Vorname, Nachname")) {
                try {
                    medi = sucheMessdiener(value, m);
                } catch (CouldNotFindMessdiener e) {
                    messdienerNotFound(strings, m, value, e, handler);
                } catch (CouldFindMessdiener e) {
                    messdienerFound(m, e, handler);
                    return update(strings, m, handler);
                }
                if (medi != null) {
                    updatedList.add(medi);
                }
            }
        }
        return REMOVE_DUPLICATES.removeDuplicatedEntries(updatedList);
    }

    private static void messdienerFound(Messdiener m, CouldFindMessdiener e, Messdiener.AnvertrauteHandler handler) {
        String[] anvertrauteList = handler.get(m);
        for (int i = 0; i < anvertrauteList.length; i++) {
            if (anvertrauteList[i].equalsIgnoreCase(e.getString())) {
                anvertrauteList[i] = e.getFoundMessdienerID();
            }
        }
        handler.set(m, anvertrauteList);
        m.makeXML();

    }

    public static void messdienerNotFound(String[] s, Messdiener m, String value, CouldNotFindMessdiener e, Messdiener.AnvertrauteHandler handler) {
        boolean beheben = Dialogs.getDialogs().frage(e.getMessage(),
                "ignorieren", "beheben");
        if (beheben) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
            list.remove(value);
            handler.set(m, list);
            m.makeXML();
        }
    }

    public static Messdiener sucheMessdiener(String geschwi, Messdiener akt) throws CouldFindMessdiener, CouldNotFindMessdiener {
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
