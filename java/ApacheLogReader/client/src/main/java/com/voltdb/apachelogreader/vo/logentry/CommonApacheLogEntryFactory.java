package com.voltdb.apachelogreader.vo.logentry;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Generates a fake log entry that mimics what apache would normally generate.
 * @author awilson
 *
 */
public class CommonApacheLogEntryFactory {

    private final static Random random = new Random(7);
    
    private final static String HOSTS[] = { "foo.com", "bar.cc", "m24x9.org", "ghleipe.mn" };
    private final static String USERS[] = { "Mike", "Bob", "Grace", "Charlie", "-", "Pat", "John", "Alex", "7", "Chutuna" };
    private final static String ASSETS[] = { "/apache_pb.gif", "/my.js", "/foo.css", "/bar.png", "/bar_small.png", "/blog.css"};
    private final static int ASSET_SIZE[] = { 2345, 10002, 795, 102378, 2831, 31223};
    
    private final static Calendar calendar = new GregorianCalendar();
    private final static SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
    
    public final static String getLogEntry() {
        String host = getHost();
        String name = getUser();
        String timestamp = getTimestamp();
        String request = getRequestStatusSize();
        
        return String.format("%s - %s %s %s\n", host, name, timestamp, request);
    }

    private static String getRequestStatusSize() {
        Pair<String, Integer> asset = getAsset();
        
        return String.format("\"GET %s HTTP/1.0\" 200 %d", asset.asset, asset.size);
    }

    private static Pair<String, Integer> getAsset() {
        int index = random.nextInt(ASSETS.length);
        return new Pair<String, Integer>(ASSETS[index],ASSET_SIZE[index] );
        
    }

    private static String getTimestamp() {
        calendar.add(Calendar.SECOND, random.nextInt(3));
        String result =  simpleDate.format(calendar.getTime());
       
        return "[" + result + "]";
    }

    private static String getUser() {
        return getRandomArrayValue(USERS);
    }

    private static String getHost() {
        return random.nextBoolean() ? getIpAddress() : getHostName();
    }

    private static String getHostName() {
        return getRandomArrayValue(HOSTS);
    }

    private static String getIpAddress() {
        return String.format("%d.%d.%d.%d", random.nextInt(254),random.nextInt(254),random.nextInt(254),random.nextInt(254) );
    }
    
    private static String getRandomArrayValue(String[] array) {
        return array[random.nextInt(array.length)];
    }
    
}
