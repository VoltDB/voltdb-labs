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


//
// Accepts a vote, enforcing business logic: make sure the vote is for a valid
// contestant and that the voter (phone number of the caller) is not above the
// number of allowed votes.
//

package tweets.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

@ProcInfo (
    partitionInfo = "tweet.tag:2",
    singlePartition = true
)
public class Tweet extends VoltProcedure
{
    public final SQLStmt deleteStmt = new SQLStmt("DELETE FROM tweet WHERE expires < ? AND lat = ? and lon = ?;");
    public final SQLStmt insertStmt = new SQLStmt("INSERT INTO tweet (expires, lat, lon, tag) VALUES(?, ?, ?, ?);");
    public VoltTable[] run(double lat, double lon, String tag, int retainDuration)
    {
        long datetime = getTransactionTime().getTime()/1000;
        voltQueueSQL(
          deleteStmt
        , datetime
        , 180-(short)(Double.valueOf(lat).intValue()+90)
        , (short)(Double.valueOf(lon).intValue()+180)
        );
        voltQueueSQL(
          insertStmt
        , datetime+(long)retainDuration
        , 180-(short)(Double.valueOf(lat).intValue()+90)
        , (short)(Double.valueOf(lon).intValue()+180)
        , tag
        );
        voltExecuteSQL(true);
        return null;
    }
}
