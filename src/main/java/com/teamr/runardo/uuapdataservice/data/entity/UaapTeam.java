package com.teamr.runardo.uuapdataservice.data.entity;

public enum UaapTeam {
    FEU (1, "Far Eastern University"),
    NU (2, "National University"),
    UP (3, "University of the Philippines"),
    UST (4, "University of Santo Tomas"),
    ADU (5, "Adamson University"),
    UE (6, "University of the East"),
    ADMU (7, "Ateneo de Manila University"),
    DLSU (8, "De La Salle University");

    private final int id;
    private final String name;

    UaapTeam(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static UaapTeam parse(String s) {
        return switch (s) {
            case "LASALLE", "DLSU", "DELASALLE", "DLS":
                yield UaapTeam.DLSU;
            case "ATENEO", "ADMU", "ADM":
                yield UaapTeam.ADMU;
            case "ADAMSON", "ADU":
                yield UaapTeam.ADU;
            case "UE", "UEA":
                yield UaapTeam.UE;
            case "USTE", "UST":
                yield UaapTeam.UST;
            case "FEU":
                yield UaapTeam.FEU;
            case "UP", "UPD":
                yield UaapTeam.UP;
            case "NU", "NATIONAL UNIVERSITY", "NUI":
                yield UaapTeam.NU;
            default:
                throw new IllegalStateException("Unexpected value: " + s);
        };
    }

    public static UaapTeam parse(int s) {
        return switch (s) {
            case 8:
                yield UaapTeam.DLSU;
            case 7:
                yield UaapTeam.ADMU;
            case 5:
                yield UaapTeam.ADU;
            case 6:
                yield UaapTeam.UE;
            case 4:
                yield UaapTeam.UST;
            case 1:
                yield UaapTeam.FEU;
            case 3:
                yield UaapTeam.UP;
            case 2:
                yield UaapTeam.NU;
            default:
                throw new IllegalStateException("Unexpected value: " + s);
        };
    }

    public String getName() {
        return name;
    }

    public static UaapUniv uaapUnivFactory(UaapTeam uaapTeam) {
        UaapUniv uaapUniv = new UaapUniv();
        uaapUniv.setId(uaapTeam.getId());
        uaapUniv.setUnivCode(uaapTeam.toString());
        uaapUniv.setUnivName(uaapTeam.getName());
        return uaapUniv;
    }
}