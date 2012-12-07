package com.voltdb.demographicanalytics.procedures;

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

import java.security.MessageDigest;
import java.util.TreeSet;

import javax.xml.bind.DatatypeConverter;
import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

/**
 * A complicated stored procedure that inserts a new log entry, adds or updates
 * it to the demographic rollup.
 * 
 * @author awilson
 * 
 */
@ProcInfo(partitionInfo = "log.network:1", singlePartition = true)
public class InsertLogEntry extends VoltProcedure {

    private final static int INVALID = -1;
    private final static String KEY_CHARACTER_ENCODING = "UTF-8";

    // Statements for updating the highwater table
    public final static SQLStmt GET_LOG_HIGHWATER = new SQLStmt(
            "SELECT interval_id FROM log_highwater where network=? order by interval_id desc;");

    public final static SQLStmt INSERT_LOG_HIGHWATER = new SQLStmt(
            "INSERT INTO log_highwater(interval_id,network) VALUES(?,?);");

    public final static SQLStmt UPDATE_LOG_HIGHWATER = new SQLStmt(
            "UPDATE log_highwater SET interval_id=? WHERE network=?");

    // Statements for updating the demographic_aggregation_table
    public final static SQLStmt SELECT_DEMOGRAPHIC_AGGREGATION = new SQLStmt(
            "SELECT impressions, conversions FROM demographic_aggregation WHERE profile_key=?;");

    public final static SQLStmt INSERT_DEMOGRAPHIC_AGGREGATION = new SQLStmt(
            "INSERT INTO demographic_aggregation (profile_key, network, sex, age, marital_status, income, education, occupation, cost, impressions, conversions)"
                    + " values(?,?,?,?,?,?,?,?,?,?,?);");

    public final static SQLStmt UPDATE_DEMOGRAPHIC_AGGREGATION = new SQLStmt(
            "UPDATE demographic_aggregation SET impressions=?, conversions=?"
                    + " where profile_key=?;");

    // Statements for inserting into the log
    public final static SQLStmt INSERT_LOG = new SQLStmt(
            "INSERT INTO log (interval_id, network, cost, first_name, last_name, age_actual, income_actual,"
                    + " age, income, sex, marital_status, education, occupation, conversion)"
                    + " VALUES(?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

    public final static SQLStmt GET_LOG_INTERVAL_STATISTICS = new SQLStmt(
            "SELECT network, sex, age, marital_status, income, education, occupation, cost, "
                    + " count(*) as impressions, SUM(conversion) as conversions FROM log WHERE interval_id=? and network=? "
                    + " GROUP BY network, sex, age, marital_status, income, education, occupation, cost"
                    + " ORDER BY network, sex, age, marital_status, income, education, occupation, cost;");

    public final static TreeSet<String> tree = new TreeSet<String>();

    /**
     * The root stored procedure method that inserts a new demographic object.
     * @param intervalId
     * @param network
     * @param cost
     * @param firstName
     * @param lastName
     * @param ageActual
     * @param incomeActual
     * @param age
     * @param income
     * @param sex
     * @param maritalStatus
     * @param education
     * @param occupation
     * @param conversion
     * @return
     */
    public long run(int intervalId, String network, int cost, String firstName,
            String lastName, int ageActual, int incomeActual, String age,
            String income, String sex, String maritalStatus, String education,
            String occupation, int conversion) {

        int highwater = getHighwater(network);
        if (highwater == INVALID) {
            insertHighwater(intervalId, network);
        } else if (highwater < intervalId) {
            // only update a demogrpahic aggregation once we've moved on to a 
            // new interval. Note, data coming in an old interval is not rolled
            // up again.
            updateDemographicAggregation(highwater, network);
            updateHighwater(intervalId, network);
        }

        voltQueueSQL(INSERT_LOG, intervalId, network, cost, firstName,
                lastName, ageActual, incomeActual, age, income, sex,
                maritalStatus, education, occupation, conversion);
        voltExecuteSQL(true);

        return 1;
    }
/**
 * Pulls all the rows for a given interval.
 * @param highwater
 * @param network
 */
    private void updateDemographicAggregation(int highwater, String network) {
        voltQueueSQL(GET_LOG_INTERVAL_STATISTICS, highwater, network);
        VoltTable[] logStats = voltExecuteSQL();
        while (logStats[0].advanceRow()) {
            String sex = logStats[0].getString("sex");
            String age = logStats[0].getString("age");
            String maritalStatus = logStats[0].getString("marital_status");
            String income = logStats[0].getString("income");
            String education = logStats[0].getString("education");
            String occupation = logStats[0].getString("occupation");
            long cost = logStats[0].getLong("cost");
            long impressions = logStats[0].getLong("impressions");
            long conversions = logStats[0].getLong("conversions");
            upsertDemographicAggregation(network, sex, age, maritalStatus,
                    income, education, occupation, cost, impressions,
                    conversions);

        }
    }
/**
 * Performs the actual aggregation of data.
 * @param network
 * @param sex
 * @param age
 * @param maritalStatus
 * @param income
 * @param education
 * @param occupation
 * @param cost
 * @param impressions
 * @param conversions
 */
    private void upsertDemographicAggregation(String network, String sex,
            String age, String maritalStatus, String income, String education,
            String occupation, long cost, long impressions, long conversions) {
// This is a useful optimization to find a demographic group more quickly.
        String profileKey = getProfileKey(network, sex, age, maritalStatus,
                income, education, occupation);

        voltQueueSQL(SELECT_DEMOGRAPHIC_AGGREGATION, profileKey);
        VoltTable[] aggregationResults = voltExecuteSQL();
        if (aggregationResults[0].advanceRow()) {
            long impressionResults = aggregationResults[0]
                    .getLong("impressions");
            long conversionResults = aggregationResults[0]
                    .getLong("conversions");
            voltQueueSQL(UPDATE_DEMOGRAPHIC_AGGREGATION, impressionResults
                    + impressions, conversionResults + conversions, profileKey);
        } else {

            voltQueueSQL(INSERT_DEMOGRAPHIC_AGGREGATION, profileKey, network,
                    sex, age, maritalStatus, income, education, occupation,
                    cost, impressions, conversions);
        }
        voltExecuteSQL();
    }

    /**
     * Converts a series of known demographic parameters into an SHA-1
     * key that is indexed and can be looked up quickly. This reduces future 
     * "WHERE" predicates to a single parameter.
     * @param network
     * @param sex
     * @param age
     * @param maritalStatus
     * @param income
     * @param education
     * @param occupation
     * @return
     */
    private String getProfileKey(String network, String sex, String age,
            String maritalStatus, String income, String education,
            String occupation) {
        String results = null;
        String rawKey = String.format("%s:%s:%s:%s:%s:%s:%s", network, sex,
                age, maritalStatus, income, education, occupation);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            byte[] byteKey = digest.digest(rawKey
                    .getBytes(KEY_CHARACTER_ENCODING));
            results = DatatypeConverter.printHexBinary(byteKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Sets the last interval that was rolled up.
     * @param intervalId
     * @param network
     */
    private void insertHighwater(int intervalId, String network) {
        voltQueueSQL(INSERT_LOG_HIGHWATER, intervalId, network);
        voltExecuteSQL();
    }

    private void updateHighwater(int intervalId, String network) {
        voltQueueSQL(UPDATE_LOG_HIGHWATER, intervalId, network);
        voltExecuteSQL();
    }

    protected int getHighwater(String network) {
        int results = INVALID;

        voltQueueSQL(GET_LOG_HIGHWATER, network);
        VoltTable[] highwaterResults = voltExecuteSQL();
        if (highwaterResults[0].advanceRow()) {
            results = (int) highwaterResults[0].getLong("interval_id");
        }

        return results;
    }
}
