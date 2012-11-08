package com.voltdb.demographicanalytics.vo.logentry;

public enum Occupation {
    PROFESSIONAL (0, "Professional"),
    BLUE_COLLAR (1, "Blue Collar"),
    WHITE_COLLAR (2, "White Collar"),
    AGRICULTURAL (3, "Agriculture"),
    MILITARY (4, "Military");
    
    int status = 0;
    String name;
    Occupation(int value, String name) {
        this.status = value;
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
}