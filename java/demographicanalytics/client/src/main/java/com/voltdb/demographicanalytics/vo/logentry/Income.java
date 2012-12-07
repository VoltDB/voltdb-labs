/* This file is part of VoltDB.
 * Copyright (C) 2008-2012 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.voltdb.demographicanalytics.vo.logentry;

/**
 * Converts an actual income into a demographic group.
 * @author awilson
 *
 */
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