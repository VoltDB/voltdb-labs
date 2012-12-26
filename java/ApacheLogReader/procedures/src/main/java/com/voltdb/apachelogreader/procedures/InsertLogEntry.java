package com.voltdb.apachelogreader.procedures;

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

import java.sql.Timestamp;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;

/**
 * A simple stored procedure that stores the raw log line for later review and
 * stores a refined version of the log line for more detailed queries.
 * 
 * @author awilson
 * 
 */
@ProcInfo(partitionInfo = "log.interval_id:0", singlePartition = true)
public class InsertLogEntry extends VoltProcedure {

    public final static SQLStmt INSERT_LOG = new SQLStmt(
            "INSERT INTO log (interval_id, time_stamp, log_line) VALUES(?,?,?);");

    public final static SQLStmt INSERT_LOG_FIELDS = new SQLStmt(
            "INSERT INTO log_fields (interval_id, host, item_exists, user_id, time_stamp, method, url, client, status_code, size, extended) VALUES(?,?,?,?,?,?,?,?,?,?,?);");

    public long run(int intervalId, String logLine, String host, String exists,
            String userId, Timestamp timestamp, String method, String url,
            String client, int statusCode, int size, String extended) {

        // Store an unrefined version in case you need to process it again.
        voltQueueSQL(INSERT_LOG, intervalId, timestamp, logLine);

        // Store a refined version for executing queries.
        voltQueueSQL(INSERT_LOG_FIELDS, intervalId, host, exists, userId,
                timestamp, method, url, client, statusCode, size, extended);
        voltExecuteSQL(true);
        // Note that there is a materialized view in the DDL.sql file that
        // stores bandwidth stats on a per asset basis for each interval. This
        // is updated on each insert. The effect is that each transaction
        // performs three operations.

        return 1;
    }
}
