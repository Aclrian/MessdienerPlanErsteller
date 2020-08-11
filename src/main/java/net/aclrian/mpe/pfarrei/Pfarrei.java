package net.aclrian.mpe.pfarrei;

import net.aclrian.mpe.messe.StandartMesse;

import java.util.List;

public class Pfarrei {
    private final String name;
    private final List<StandartMesse> sm;
    private final Einstellungen settings;
    private final boolean hochaemter;

    public Pfarrei(Einstellungen settings, List<StandartMesse> sm, String name, boolean hochaemterzaelenmit) {
        this.settings = settings;
        this.sm = sm;
        this.name = name;
        this.hochaemter = hochaemterzaelenmit;
    }

    public List<StandartMesse> getStandardMessen() {
        return sm;
    }

    public String getName() {
        return name;
    }

    public Einstellungen getSettings() {
        return settings;
    }

    public boolean zaehlenHochaemterMit() {
        return hochaemter;
    }

}