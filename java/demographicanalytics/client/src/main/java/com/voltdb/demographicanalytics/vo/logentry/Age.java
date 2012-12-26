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
 * Converts a person's actual age into a demographic group.
 * @author awilson
 *
 */
public enum Age {
    RANGE_UNDER11 (0, "11 and under"),
    RANGE_12TO17 (1, "12 to 17"),
    RANGE_18TO34 (2, "18 to 34"),
    RANGE_35TO49 (3, "35 to 49"),
    RANGE_50TO64 (4 , "50 to 64"),
    RANGE_65TO74 (5, "65 to 74"),
    RANGE_74TO99 (6, "75 to 99"),
    RANGE_100 (7, "100 or older");

    int age = 0;
    String name;
    Age(int age, String name) {
        this.age = age;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final static Age getAge(int value) {
        Age result = null;
        if ( value < 12 ) {
            result = RANGE_UNDER11;
        } else if ( value < 18) {
            result = RANGE_12TO17;
        } else if ( value < 35) {
            result = RANGE_18TO34;
        } else if ( value < 50) {
            result = RANGE_35TO49;
        } else if ( value < 65) {
            result = RANGE_50TO64;
        } else if ( value < 75) {
            result = RANGE_65TO74;
        } else if ( value < 100) {
            result = RANGE_74TO99;
        } else {
            result = RANGE_100;
        }

        return result;
    }
}