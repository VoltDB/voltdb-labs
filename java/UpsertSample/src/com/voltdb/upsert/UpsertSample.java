/* This file is part of VoltDB.
 * Copyright (C) 2008-2015 VoltDB Inc.
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

package com.voltdb.upsert;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.voltdb.client.ClientResponse;
import org.voltdb.client.ClientStats;
import org.voltdb.client.ProcedureCallback;

import com.voltdb.upsert.configuration.UpsertConfiguration;
import com.voltdb.upsert.configuration.UpsertConfigurationFactory;

/**
 * A short client application demonstrating how to write an upsert stored
 * procedure.
 * 
 * @author awilson
 * 
 */
public class UpsertSample extends BaseVoltApp {

    public static void main(String[] args) {
        UpsertConfiguration config = UpsertConfigurationFactory
                .getConfiguration(args);
        UpsertSample upsertSample = new UpsertSample(config);
        try {
            upsertSample.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tracks the number of actual inserts, updates and failures in the
    // asynchronous callback handler.
    AtomicLong inserts = new AtomicLong(0);
    AtomicLong updates = new AtomicLong(0);
    AtomicLong failed = new AtomicLong(0);

    // Sample demographic data
    String[] keys = new String[] { "a", "b", "c", "ab", "ac", "bc" };
    String[] networks = new String[] { "Ad source 1", "Ad source 2",
            "Ad source 3", "Ad source 4", "Ad source 5", "Ad source 6" };
    String[] sexes = new String[] { "male", "female" };
    String[] maritalStatuses = new String[] { "single", "married" };
    String[] incomes = new String[] { "49999 or less", "50000 or more" };
    String[] educations = new String[] { "Some High School", "High School",
            "Some College", "College Graduate", "Postgraduate" };
    String[] occupations = new String[] { "Professional", "Blue Collar",
            "White Collar", "Agriculture", "Military" };

    String[][] attributes = new String[][] { keys, networks, sexes,
            maritalStatuses, incomes, educations, occupations };

    // The Random is used for generating the demographic data. We use a seed
    // value to make the results repeatable across executions
    Random randomIndexSelector = new Random(7);

    public UpsertSample(UpsertConfiguration config) {
        super(config);
    }

    public void execute() throws Exception {
        // Run until the command line duration is met.
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (this.config.duration * 1000);

        while (System.currentTimeMillis() < endTime) {

            // Generate random demographic information and send it to the
            // cluster.
            String key = getRandomAttribute(0);
            String network = getRandomAttribute(1);
            String sex = getRandomAttribute(2);
            String maritalStatus = getRandomAttribute(3);
            String income = getRandomAttribute(4);
            String education = getRandomAttribute(5);
            String occupation = getRandomAttribute(6);

            // Impressions could be moved to the stored procedure unless you
            // want to count multiple impressions for the same profile
            int impressions = 1;
            // Sets whether the user "converted". This could be a number > 1 if
            // impressions needed to support multiple impressions for a single
            // upsert call.
            int conversions = this.randomIndexSelector.nextInt(2);

            // Use the asynchronous callprocedure method to achieve highest
            // throughput.
            this.client.callProcedure(new UpsertCallback(),
                    "UpsertDemographicStats", key, network, sex, impressions,
                    maritalStatus, income, education, occupation, conversions);
        }
    }

    protected String getRandomAttribute(int attributeIndex) {
        String[] attribute = this.attributes[attributeIndex];
        int length = attribute.length;

        return attribute[this.randomIndexSelector.nextInt(length)];
    }

    /**
     * Receives the result of a given query. Tracks the number of inserts,
     * updates and failures if there are any.
     * 
     * @author awilson
     * 
     */
    class UpsertCallback implements ProcedureCallback {
        @Override
        public void clientCallback(ClientResponse response) throws Exception {
            if (response.getStatus() == ClientResponse.SUCCESS) {
                long resultCode = response.getResults()[0].asScalarLong();
                if (resultCode == 1) { // insert
                    inserts.incrementAndGet();
                } else { // update
                    updates.incrementAndGet();
                }
            } else {
                failed.incrementAndGet();
            }
        }
    }

    /**
     * Writes the results of the example application. Adding the insert, update
     * and failure values should equal the results of
     * stats.getInvocationsCompleted.
     * 
     * Note: The performance stats are going to be slightly worse than they
     * really are because we are not allowing the cluster to warm up. The
     * cluster needs to perform some initialization operations that don't occur
     * until it receives queries. Consequently, the stats are a little off of
     * what they would be in a long running cluster.
     */
    @Override
    protected void printResults() {
        ClientStats stats = this.fullStatsContext.fetch().getStats();
        System.out.printf("%s%n" + "Inserts: %,d%n" + "Updates: %,d%n"
                + "Failures: %,d%n" + "Total Transactions: %,d%n"
                + "Average throughput: %,d txns/sec%n"
                + "Average latency: %,dms%n" + "%s%n", HORIZONTAL_RULE,
                this.inserts.get(), this.updates.get(), this.failed.get(),
                stats.getInvocationsCompleted(), stats.getTxnThroughput(),
                stats.getAverageLatency(), HORIZONTAL_RULE);

    }

}
