package com.voltdb.upsert.procs;

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

//
// Accepts a vote, enforcing business logic: make sure the vote is for a valid
// contestant and that the voter (phone number of the caller) is not above the
// number of allowed votes.
//

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

/**
 * Demonstrates a simple "upsert" stored procedure
 * 
 * @author awilson
 */
@ProcInfo(partitionInfo = "DEMOGRAPHIC_AGGREGATION.key:0", singlePartition = true)
public class UpsertDemographicStats extends VoltProcedure {

    public final SQLStmt select = new SQLStmt(
            "Select impressions, conversions from DEMOGRAPHIC_AGGREGATION "
                    + "where profile_id = ?;");

    public final SQLStmt update = new SQLStmt(
            "update DEMOGRAPHIC_AGGREGATION set impressions= ?, conversions=? "
                    + "where profile_id = ?;");

    public final SQLStmt insert = new SQLStmt(
            "INSERT INTO DEMOGRAPHIC_AGGREGATION (key, profile_id, network, sex, "
                    + "impressions, marital_status, income, education, "
                    + "occupation, conversions) VALUES (?,?,?,?,?,?,?,?,?,?);");

    /**
     * Runs the query. 1. Performs search for an existing demographic profile 2.
     * If a record is found then increment the number of impressions and
     * conversions 3. Otherwise insert the new profile.
     * 
     * TODO: Find a better way to share result codes without having to share a
     * compiled class file or it is possible that your client and procedure can
     * have differing result codes.
     * 
     * 
     * @param key
     * @param network
     * @param sex
     * @param impressions
     * @param maritalStatus
     * @param income
     * @param education
     * @param occupation
     * @param conversions
     * @return
     */
    public long run(String key, String network, String sex, int impressions,
            String maritalStatus, String income, String education,
            String occupation, int conversions) {
        long result = 0; // error
        String profileId = hash(":", key, network, sex, maritalStatus, income,
                education, occupation);

        voltQueueSQL(select, profileId);
        VoltTable[] selectResults = voltExecuteSQL();
        if (selectResults[0].advanceRow() == true) {
            impressions += (int) selectResults[0].getLong(0);
            conversions += (int) selectResults[0].getLong(1);
            voltQueueSQL(update, impressions, conversions, profileId);
            result = 2; // update
        } else {
            voltQueueSQL(insert, key, profileId, network, sex, impressions,
                    maritalStatus, income, education, occupation, conversions);
            result = 1;
        }
        voltExecuteSQL(true);

        return result;
    }

    /**
     * Creates a short, mostly unique hash key to act as a row id. This is not
     * intended for production use. It will create a key that can cause primary
     * key collisions with differing values though the likelihood is remote.
     * 
     * @param delim
     * @param parameters
     * @return
     */
    String hash(String delim, String... parameters) {
        StringBuilder sb = new StringBuilder();
        if (parameters != null) {
            for (String param : parameters) {
                sb.append(param.hashCode());
                sb.append(delim);
            }
        }
        return sb.substring(0, sb.length() - 1);
    }
}
