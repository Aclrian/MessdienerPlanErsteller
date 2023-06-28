package net.aclrian.mpe.pfarrei;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.aclrian.mpe.messdiener.Messdaten;

public record Setting(Attribut attribut, int id, int anzahlDienen) {

    // If the Name of anzahlDienen is changed, change it here too!
    public static final String ANZAHL_DIENEN_NAME = "anzahlDienen";

    public Integer getJahr() {
        return Messdaten.getMaxYear() - id;
    }

    public IntegerProperty anzahlDienenProperty() {
        return new SimpleIntegerProperty(anzahlDienen);
    }

    @Override
    public String toString() {
        return attribut.name() + " " + id + " " + anzahlDienen;
    }

    public enum Attribut {
        YEAR,
        MAX
    }
}