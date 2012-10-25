/* This file is part of VoltDB.
 * Copyright (C) 2008-2011 VoltDB Inc.
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

import java.util.HashMap;
import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;

@ProcInfo (
    singlePartition = false
)
public class GetHeatmap extends VoltProcedure
{
    public final SQLStmt resultStmt  = new SQLStmt( "SELECT lat, lon, SUM(record_count) FROM v_tweet GROUP BY lat, lon;");
    public final SQLStmt resultByTagStmt  = new SQLStmt( "SELECT lat, lon, SUM(record_count) FROM v_tag where tag=? group by lat, lon;");

    public VoltTable[] run(int precision, String tag)
    {
        if ( tag == null || tag.length() == 0) {
            voltQueueSQL(resultStmt);
        } else {
            voltQueueSQL(resultByTagStmt,tag);
        }
            
        VoltTable[] raw = voltExecuteSQL(true);

        if (precision == 1)
            return raw;

        final VoltTable result = new VoltTable( new VoltTable.ColumnInfo("lat", VoltType.SMALLINT)
                                           , new VoltTable.ColumnInfo("lon", VoltType.SMALLINT)
                                           , new VoltTable.ColumnInfo("record_count", VoltType.BIGINT)
                                           , new VoltTable.ColumnInfo("tag", VoltType.STRING )
                                           );

        final HashMap<String,Long> map = new HashMap<String,Long>();
        while(raw[0].advanceRow())
        {
            final String key = (raw[0].getLong(0)/(long)precision)*(long)precision + ":" + (raw[0].getLong(1)/(long)precision)*(long)precision;
            if (map.containsKey(key))
                map.put(key, (long)map.get(key) + raw[0].getLong(2));
            else
                map.put(key, raw[0].getLong(2));
        }
        for(String key : map.keySet())
        {
            String[] keyParts = key.split(":");
            result.addRow(Short.parseShort(keyParts[0]), Short.parseShort(keyParts[1]), (long)map.get(key), tag);
        }
        return new VoltTable[] {result};
    }
}
