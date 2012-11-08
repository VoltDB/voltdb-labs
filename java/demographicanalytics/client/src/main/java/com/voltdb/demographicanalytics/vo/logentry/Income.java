package com.voltdb.demographicanalytics.vo.logentry;

public enum Income {
    RANGE_UNDER25K (0,24999, "Under $24999"),
    RANGE_25TO35K (25000,34999, "$25000 to $34999"),
    RANGE_35TO50K (35000,49999, "$35000 to $49999"),
    RANGE_50TO75K (50000,74999, "$50000 to $74999"),
    RANGE_75KTO100K (75000,99999, "$75000 to $99999"),
    RANGE_100K (100000,999999, "Over $100000");
    
    int min = 0;
    int max = 0;
    String name;
    Income(int min, int max, String name) {
        this.min = min;
        this.max = max;
        this.name = name;
    }
    
    public int getMin() {
        return this.min;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public int getRange() {
        return this.getMax() - this.getMin();
    }
    
    public String toString() {
        return name;
    }
    
    public final static Income getIncome(int value) {
        Income result = null;
        if ( value < 25000 ) {
            result = RANGE_UNDER25K;
        } else if ( value < 35000) {
            result = RANGE_25TO35K;
        } else if ( value < 50000) {
            result = RANGE_35TO50K;
        } else if ( value < 75000) {
            result = RANGE_50TO75K;
        } else if ( value < 100000) {
            result = RANGE_75KTO100K;
        } else {
            result = RANGE_100K;
        }
        
        return result;
    }
    
    public final static Income getIncomeByIndex(int value) {
        return Income.values()[value];
    }
}