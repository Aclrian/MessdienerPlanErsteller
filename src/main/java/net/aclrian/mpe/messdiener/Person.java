package net.aclrian.mpe.messdiener;

import java.util.Comparator;

public class Person {
    private String vorname = "";
    private String nachname = "";
    private Email email = Email.EMPTY_EMAIL;
    public static final Comparator<Person> MESSDIENER_COMPARATOR = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());

    public Person(String vorname, String nachname, Email email) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String toString() {
        return nachname + ", " + vorname;
    }
}
