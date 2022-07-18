package net.aclrian.mpe.pfarrei;

import net.aclrian.mpe.messdiener.*;

public record Setting(Attribut attribut, int id, int anzahlDienen) {

    public Integer getJahr() {
        return Messdaten.getMaxYear() - id;
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