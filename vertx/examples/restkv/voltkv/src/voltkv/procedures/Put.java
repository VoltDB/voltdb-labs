package voltkv.procedures;

import org.voltdb.*;

@ProcInfo(partitionInfo="store.key:0", singlePartition=true)
public class Put extends VoltProcedure {
    // Checks if key exists
    public final SQLStmt checkStmt = new SQLStmt("SELECT key FROM store WHERE key = ?;");
    // Updates a key/value pair
    public final SQLStmt updateStmt = new SQLStmt("UPDATE store SET value = ? WHERE key = ?;");
    // Inserts a key/value pair
    public final SQLStmt insertStmt = new SQLStmt("INSERT INTO store (key, value) VALUES (?, ?);");

    public VoltTable[] run(String key, byte[] value) {
        // Check whether the pair exists
        voltQueueSQL(checkStmt, key);
        // Insert new or update existing key depending on result
        if (voltExecuteSQL()[0].getRowCount() == 0)
            voltQueueSQL(insertStmt, key, value);
        else
            voltQueueSQL(updateStmt, value, key);
        return voltExecuteSQL(true);
    }
}

