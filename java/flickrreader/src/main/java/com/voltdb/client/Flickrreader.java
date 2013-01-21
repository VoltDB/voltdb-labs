package com.voltdb.client;

/* This file is part of VoltDB.
 * Copyright (C) 2008-2013 VoltDB Inc.
 *
 * This file contains original code and/or modifications of original code.
 * Any modifications made by VoltDB Inc. are licensed under the following
 * terms and conditions:
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.voltdb.VoltTable;
import org.voltdb.VoltType;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ClientStats;
import org.voltdb.client.NullCallback;
import org.voltdb.client.ProcedureCallback;

import com.google.gson.Gson;
import com.voltdb.client.configuration.SampleConfiguration;
import com.voltdb.client.configuration.SampleConfigurationFactory;
import com.voltdb.client.vo.JsonFlickrFeed;
import com.voltdb.client.vo.JsonFlickrItem;

/**
 * Reads a Flickr JSON feed of all newly added photographs and monitors keyword
 * usage. Each query is done once per second to prevent flickr rate limiting
 * errors. The feed also returns only twenty photo entries per query. The
 * application performs duplicate image and tag checking so that a given image
 * that is updated will only propagate the changes to the db while attributing
 * the keywords appropriately.
 * 
 * @author awilson
 * 
 */
public class Flickrreader extends BaseVoltApp {

    /**
     * Starts the application. See SampleConfiguration for command line details
     * 
     * @param args
     * 
     */
    public static void main(String[] args) {
        SampleConfiguration config = SampleConfigurationFactory
                .getConfiguration(args);
        Flickrreader logStream = new Flickrreader(config);
        try {
            logStream.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Flickrreader(SampleConfiguration config) {
        super(config);
    }

    /**
     * Sets up process for connecting to flickr, reading the feed and writing it
     * to volt. A separate thread will report the results.
     */
    @Override
    protected void execute() throws Exception {

        ScheduledExecutorService executorFeed = Executors
                .newScheduledThreadPool(1);

        ScheduledFuture<?> feedFuture = executorFeed.scheduleAtFixedRate(
                new FlickrReader(), 1, 1, TimeUnit.SECONDS);
        while (!feedFuture.isDone() && !feedFuture.isCancelled()) {
            Thread.sleep(1000);
        }
        executorFeed.shutdown();

    }

    /**
     * Thread that reads the feed and stores it.
     * 
     * @author awilson
     * 
     */
    class FlickrReader implements Runnable {

        public FlickrReader() {
        }

        public void run() {
            try {
                Gson gson = new Gson();
                JsonFlickrFeed feed = getJsonFeed(gson);
                for (JsonFlickrItem item : feed.getItems()) {
                    String tags = item.getTags();
                    // Strings from gson are usually not null, just being overly
                    // defensive.

                    tags = tags != null ? tags.trim() : null;
                    if (tags == null || tags.length() < 1) {
                        tags = "None";
                    }

                    // Use a hashkey that uniquely describes the image. The
                    // link,date taken and authorid are either entirely unique
                    // or highly unlikely to have a collision

                    String hashKey = getHashKey(item.getLink(),
                            item.getDateTaken(), item.getAuthorId());

                    // Store the data to the server. Note the NullCallback
                    // handler is something fine for demo purposes, but in a
                    // real application you should write your own callback
                    // handler in case an error occurs. The most likely error in
                    // this app is that the data being written is larger than
                    // the corresponding field size.
                    client.callProcedure(new NullCallback(),
                            "UpsertFlickrEntry", hashKey, item.getTitle(),
                            item.getTags(), gson.toJson(item));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getHashKey(String link, String dateTaken, String authorId) {
            String results = null;
            String rawKey = String
                    .format("%s:%s:%s", link, dateTaken, authorId);

            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.reset();
                byte[] byteKey = digest.digest(rawKey.getBytes("UTF-8"));
                results = DatatypeConverter.printHexBinary(byteKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }

        /**
         * Reads the raw feed and converts it into a series of Java value
         * objects.
         * 
         * @param gson
         * @return
         * @throws MalformedURLException
         * @throws IOException
         */
        protected JsonFlickrFeed getJsonFeed(Gson gson)
                throws MalformedURLException, IOException {
            String rawFeed = getRawFeed();
            JsonFlickrFeed feed = gson.fromJson(rawFeed.toString(),
                    JsonFlickrFeed.class);

            return feed;
        }

        /**
         * Gets the raw feed in json format
         * 
         * @return
         * @throws MalformedURLException
         * @throws IOException
         */
        protected String getRawFeed() throws MalformedURLException, IOException {
            URL url = new URL(
                    "http://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1");

            URLConnection connection = url.openConnection();

            InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream());
            int tmp;
            StringBuilder sb = new StringBuilder();
            while ((tmp = reader.read()) > -1) {
                sb.append((char) tmp);
            }
            
            return sb.toString();
        }

    }

    /**
     * Not called. This code is present in case you wanted to get statistics.
     * This application calls flickr so infrequently that the throughput stats
     * are very low because there are so few transactions in a given period.
     */
    @Override
    protected void printResults() {
        ClientStats stats = this.fullStatsContext.fetch().getStats();
        System.out.println(HORIZONTAL_RULE);
        System.out.printf("Throughput %d/s, ", stats.getTxnThroughput());
        System.out.printf("Aborts/Failures %d/%d%n",
                stats.getInvocationAborts(), stats.getInvocationErrors());
        System.out.println(HORIZONTAL_RULE);

    }

    /**
     * Invokes the procedure to get the top tag values.
     */
    @Override
    public synchronized void printStatistics() {
        try {
            // The rendering is done by the callback. This method gets called
            // once every 5 seconds because GetTopTags is a multi-partition
            // query and we want to keep the system free for ingestion.
            client.callProcedure(new TopTagsCallback(), "GetTopTags");
        } catch (Exception e) {
            System.out.println("Error occurred trying to get tag statistics");
            e.printStackTrace();
        }
        // super.printStatistics();
    }

    /**
     * Formats and displays the tag count query results.
     * 
     * @author awilson
     * 
     */
    class TopTagsCallback implements ProcedureCallback {

        @Override
        public void clientCallback(ClientResponse cr) throws Exception {
            if (cr.getStatus() == ClientResponse.SUCCESS) {
                VoltTable[] tables = cr.getResults();
                if (tables == null || tables.length == 0) {
                    System.out
                            .println("An error occurred in which the query executed but did not return any stats");
                } else {
                    System.out
                            .println("Tag                                      Count");
                    System.out
                            .println("----------------------------------------------");
                    while (tables[0].advanceRow()) {
                        String tag = (String) tables[0].get(0, VoltType.STRING);
                        Integer count = (Integer) tables[0].get(1,
                                VoltType.INTEGER);

                        String countString = count.toString();

                        String tagPadding = getPadding(41 - tag.length());

                        System.out.printf(" %s%s%s%n", tag, tagPadding,
                                countString);
                    }

                }
            } else {
                System.out.println("An error has occurred getting tag stats.");
            }

        }

        private String getPadding(int padLength) {
            char[] pad = new char[padLength];
            Arrays.fill(pad, ' ');
            return new String(pad);
        }
    }

}
