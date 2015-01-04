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
package tweets.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

@ProcInfo (
    singlePartition = false
)
public class GetTrending extends VoltProcedure
{
    public final SQLStmt tagStmt  = new SQLStmt( "SELECT tag, SUM(record_count) as tag_count FROM v_tag GROUP BY tag order by tag_count desc;");
    public final SQLStmt totalsStmt  = new SQLStmt( "insert into tweetStats( intervalID, tag, record_count) values( ?,?,?);");
    public final SQLStmt trendStmt  = new SQLStmt( "SELECT tag, record_count FROM tweetStats order by intervalID asc;");
    
    public VoltTable[] run()
    {
        voltQueueSQL(tagStmt);
        VoltTable[] tags = voltExecuteSQL();
        
        long transactionID = this.getTransactionId();
        while(tags[0].advanceRow())
        {
            String tag = tags[0].getString(0);
            long count = tags[0].getLong(1);
            this.voltQueueSQL(totalsStmt, transactionID, tag, count);
        }
        voltExecuteSQL();
        
        this.voltQueueSQL(trendStmt);
        VoltTable trends[] = voltExecuteSQL(true);
        
        VoltTable results[] = new VoltTable[] { tags[0], trends[0] };

        return results;
    }
}
