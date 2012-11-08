package com.voltdb.demographicanalytics.vo.logentry;

public enum MaritalStatus {
    SINGLE (0, "Single"),
    MARRIED (1, "Married"),
    DIVORCED (2, "Divorced"),
    LIVING_TOGETHER (3, "Living together"),
    WIDOWED (4, "Widowed");
    
    int status = 0;
    String name;
    MaritalStatus(int value, String name) {
        this.status = value;
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
}