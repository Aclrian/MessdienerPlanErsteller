package net.aclrian.mpe.messe;


import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.utils.DateUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class Messe implements Comparable<Messe> { //NOPMD - suppressed TooManyMethods - Model class cannot be refactored
    public static final Comparator<Messe> MESSE_COMPARATOR = Comparator.comparing(Messe::getDate);
    private final StandardMesse em;
    private boolean hochamt;
    private int anzMessdiener;


    private LocalDateTime date;
    private String kirche;
    private String typ;
    private ArrayList<Messdiener> medis = new ArrayList<>();
    private ArrayList<Messdiener> leiter = new ArrayList<>();

    public Messe(LocalDateTime d, StandardMesse sm) {
        this(false, sm.getAnzMessdiener(), d, sm.getOrt(), sm.getTyp(), sm);
    }

    public Messe(boolean hochamt, int anzMedis, LocalDateTime date, String ort, String typ) {
        this(hochamt, anzMedis, date, ort, typ, new Sonstiges());
    }

    public Messe(boolean hochamt, int anzMedis, LocalDateTime date, String ort, String typ, StandardMesse sm) {
        bearbeiten(hochamt, anzMedis, date, ort, typ);
        em = sm;
    }

    @Override
    public String toString() {
        return getID();
    }

    public void bearbeiten(boolean hochamt, int anzMessdiener, LocalDateTime date, String kirche, String type) {
        this.hochamt = hochamt;
        this.kirche = kirche;
        this.typ = type;
        this.date = date;
        this.anzMessdiener = anzMessdiener;
    }

    public boolean einteilen(Messdiener medi, boolean zwangdate, boolean zwanganz) {
        if (medi.getMessdaten().einteilen(date.toLocalDate(), hochamt, zwangdate, zwanganz)) {
            if (medi.istLeiter()) {
                leiter.add(medi);
                leiter.sort(Messdiener.MESSDIENER_COMPARATOR);
            } else {
                medis.add(medi);
                medis.sort(Messdiener.MESSDIENER_COMPARATOR);
            }
            return true;
        }
        return false;
    }

    public int getAnzMessdiener() {
        return anzMessdiener;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<Messdiener> getEingeteilte() {
        ArrayList<Messdiener> rtn = new ArrayList<>();
        rtn.addAll(medis);
        rtn.addAll(leiter);
        return rtn;
    }

    public StandardMesse getStandardMesse() {
        try {
            return em;
        } catch (NullPointerException e) {
            return new Sonstiges();
        }
    }

    public String getID() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee dd.MM.yyyy\tHH:mm");
        return formatter.format(date) + " Uhr " + typ + " " + kirche;
    }

    public String getIDHTML() {
        return "<html><font><p><b>" +
                date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " +
                DateUtil.DATE.format(date) + "&emsp;" + DateUtil.TIME.format(date) + " Uhr " +
                typ + " " + kirche + "</p></b></font></html>";
    }

    public String getKirche() {
        return kirche;
    }

    public String getMesseTyp() {
        return typ;
    }

    public String htmlAusgeben() {
        StringBuilder rtn = new StringBuilder(getIDHTML());
        rtn = new StringBuilder(rtn.substring(0, rtn.length() - 7));
        medis.sort(Messdiener.MESSDIENER_COMPARATOR);
        leiter.sort(Messdiener.MESSDIENER_COMPARATOR);
        ArrayList<Messdiener> out = medis;
        out.addAll(leiter);
        rtn.append("<p>");
        for (Messdiener messdiener : out) {
            rtn.append(messdiener.getVorname()).append(" ").append(messdiener.getNachnname()).append(", ");
        }
        if (rtn.toString().endsWith(", ")) {
            rtn = new StringBuilder(rtn.substring(0, rtn.length() - 2));
        }
        rtn.append("</p>");
        rtn.append("</html>");
        return rtn.toString();
    }

    public boolean isHochamt() {
        return hochamt;
    }

    public boolean istFertig() {
        int anzLeiter = leiter.size();
        int anzMedis = medis.size();
        return (anzMedis + anzLeiter) >= anzMessdiener;
    }

    public void nullen() {
        medis = new ArrayList<>();
        leiter = new ArrayList<>();
    }

    public boolean vorzeitigEinteilen(Messdiener medi) {
        if (medi.getMessdaten().einteilenVorzeitig(date.toLocalDate(), hochamt)) {
            if (medi.istLeiter()) {
                leiter.add(medi);
            } else {
                medis.add(medi);
            }
            medis.sort(Messdiener.MESSDIENER_COMPARATOR);
            return true;
        }
        return false;
    }

    public int getNochBenoetigte() {
        int haben = getHatSchon();
        int soll = anzMessdiener;
        return soll - haben;
    }

    private int getHatSchon() {
        return leiter.size() + medis.size();
    }

    @Override
    public int compareTo(Messe m) {
        return date.compareTo(m.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Messe messe = (Messe) o;

        if (hochamt != messe.hochamt || anzMessdiener != messe.anzMessdiener || !Objects.equals(date, messe.date) || !Objects.equals(kirche, messe.kirche)) {
            return false;
        }
        if (!Objects.equals(em, messe.em) || !Objects.equals(typ, messe.typ) || !Objects.equals(medis, messe.medis)) {
            return false;
        }
        return Objects.equals(leiter, messe.leiter);
    }

    @Override
    public int hashCode() {
        int result = hochamt ? 1 : 0;
        result = 31 * result + anzMessdiener;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (kirche != null ? kirche.hashCode() : 0);
        result = 31 * result + (em != null ? em.hashCode() : 0);
        result = 31 * result + (typ != null ? typ.hashCode() : 0);
        result = 31 * result + (medis != null ? medis.hashCode() : 0);
        result = 31 * result + (leiter != null ? leiter.hashCode() : 0);
        return result;
    }
}