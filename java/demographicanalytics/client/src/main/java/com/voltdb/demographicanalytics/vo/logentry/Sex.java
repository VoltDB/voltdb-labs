package com.voltdb.demographicanalytics.vo.logentry;

public enum Sex {
    MALE (0, "Male"),
    FEMALE(1, "Female");
    
    int sex = 0;
    String name;
    Sex(int value, String name) {
        this.sex = value;
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
}