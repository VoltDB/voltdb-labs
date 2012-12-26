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
package com.voltdb.apachelogreader.vo.logentry;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for an apache common log line.
 * "47.140.159.130 - Bob [26/Dec/2012:13:00:01 -0500] "GET /bar.png
 * HTTP/1.0" 200 102378" is an example of a common log line. Extended log line
 * data is captured but not parsed.
 * 
 * @author awilson
 * 
 */
public class CommonApacheLogEntry extends ApacheLogEntry {

    // A mostly accurate regex for a common log line. Not entirely correct and
    // has not been tested on a variety of files and certainly does not handle
    // custom log lines.
    private final static String LOG_REGEX = "(.*)\\s(\\S+)\\s(\\S+)\\s\\[([\\w:/]+\\s[+\\-]\\d{4})\\]\\s\\\"(.*)\\s(.*)\\s(.*)\\\"\\s(\\d{3})\\s(\\d+)(\\s(.*))*";
    private final static Pattern LOG_PATTERN = Pattern.compile(LOG_REGEX,
            Pattern.DOTALL);

    private Matcher logMatcher;

    public CommonApacheLogEntry(String logStatement) {
        super(logStatement);
    }

    @Override
    public boolean parse() {
        this.logMatcher = CommonApacheLogEntry.LOG_PATTERN.matcher(this
                .getLogStatement());
        boolean results = this.logMatcher.matches();
        return results;
    }

    // A enum that simplifies parsing the various fields.
    private enum EntryField {
        HOST(1), 
        EXISTS(2), 
        USERID(3), 
        TIMESTAMP(4), 
        METHOD(5), 
        QUERY(6), 
        CLIENT(7), 
        STATUS(8), 
        SIZE(9), 
        EXTRA(10);

        private int group;

        private EntryField(int group) {
            this.group = group;
        }

        public String getGroupValue(Matcher matcher) {
            return matcher.group(this.group);
        }
    }

    public String getHost() {
        return EntryField.HOST.getGroupValue(this.logMatcher);
    }

    public String getClient() {
        return EntryField.CLIENT.getGroupValue(this.logMatcher);
    }

    public String getUserId() {
        return EntryField.USERID.getGroupValue(this.logMatcher);
    }

    public Timestamp getTimestamp() throws ParseException {
        String timeString = EntryField.TIMESTAMP.getGroupValue(this.logMatcher);
        // Converts the default log line time stamp into a usable date.
        SimpleDateFormat simpleDate = new SimpleDateFormat(
                "dd/MMM/yyyy:HH:mm:ss Z");
        Date date = simpleDate.parse(timeString);
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp;
    }

    public String getMethod() {
        return EntryField.METHOD.getGroupValue(this.logMatcher);
    }

    public String getQuery() {
        return EntryField.QUERY.getGroupValue(this.logMatcher);
    }

    public String getExists() {
        return EntryField.EXISTS.getGroupValue(this.logMatcher);
    }

    public String getStatus() {
        return EntryField.STATUS.getGroupValue(this.logMatcher);
    }

    public String getSize() {
        return EntryField.SIZE.getGroupValue(this.logMatcher);
    }

    public String getExtra() {
        return EntryField.EXTRA.getGroupValue(this.logMatcher);
    }

    /**
     * A method that translates the log's time stamp into a 15 minute interval.
     * Used for batching results.
     * 
     * @return The time interval
     * @throws ParseException
     */
    public int getInterval() throws ParseException {
        Timestamp timestamp = this.getTimestamp();
        long time = timestamp.getTime();

        int interval = (int) Math.floor(time / 900000);

        return interval;
    }
}
