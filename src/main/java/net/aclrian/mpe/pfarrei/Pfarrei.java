package net.aclrian.mpe.pfarrei;


import net.aclrian.mpe.messe.StandardMesse;

import java.util.List;

public class Pfarrei {
    private final String name;
    private final List<StandardMesse> sm;
    private final Einstellungen settings;
    private final boolean hochamt;

    public Pfarrei(Einstellungen settings, List<StandardMesse> sm, String name, boolean hochamtZaehltMit) {
        this.settings = settings;
        this.sm = sm;
        this.name = name;
        this.hochamt = hochamtZaehltMit;
    }

    public List<StandardMesse> getStandardMessen() {
        return sm;
    }

    public String getName() {
        return name;
    }

    public Einstellungen getSettings() {
        return settings;
    }

    public boolean zaehlenHochaemterMit() {
        return hochamt;
    }

}