package com.voltdb.demographicanalytics.vo.logentry;

public enum Education {
    SOME_HIGH_SCHOOL (0, "Some High School"),
    HIGH_SCHOOL (1, "High School"),
    SOME_COLLEGE (2, "Some College"),
    COLLEGE_GRADUATE (3, "College Graduate"),
    POSTGRADUATE (4, "Postgraduate");
    
    int status = 0;
    String name;
    Education(int value, String name) {
        this.status = value;
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
}