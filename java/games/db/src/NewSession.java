package procedures;

//import java.math.BigDecimal;
import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltTableRow;
//import org.voltdb.VoltType;
import org.voltdb.client.ClientResponse;

/** A VoltDB stored procedure is a Java class defining one or
 * more SQL statements and implementing a public
 * VoltTable[] run method. VoltDB requires a
 * ProcInfo annotation providing metadata for the
 * procedure.  The run method is
 * defined to accept one or more parameters. These parameters take the
 * values the client passes via the
 * Client.callProcedure invocation.
 * The VoltDB User Guide (https://community.voltdb.com/documentation) 
 * specifies valid stored procedure definitions,
 * including valid run method parameter types, required annotation
 * metadata, and correct use the Volt query interface.
 */

@ProcInfo(
    partitionInfo = "game_players.player_id: 1",
    singlePartition = true
)
public class NewSession extends VoltProcedure {

    public final SQLStmt checkStmt = new SQLStmt("SELECT * FROM game_players WHERE game_id = ? AND player_id = ?");
    public final SQLStmt insertStmt = new SQLStmt("INSERT INTO game_players VALUES (" +
						  "?,?,?,?,?,?" +
						  ");");

    public final SQLStmt updateStmt = new SQLStmt("UPDATE game_players SET " +
						  " active_session = 1" +
						  " WHERE game_id = ? AND player_id = ?;");
    
    public final SQLStmt rankStmt = new SQLStmt("SELECT COUNT(*) FROM game_players WHERE game_id = ? AND score > ? AND score < ?");

    public VoltTable[] run( int        game_id,
                          int        player_id
		     ) throws VoltAbortException {

	voltQueueSQL(checkStmt, game_id, player_id);
        VoltTable check[] = voltExecuteSQL();

        if (check[0].getRowCount() == 0) {
            // insert
	    voltQueueSQL(insertStmt,game_id,player_id,0,1,0,1); // default starting values
	} else {
            // update
	    voltQueueSQL(updateStmt,game_id,player_id);
            check[0].advanceRow();
            int score = (int)check[0].getLong(4);
	    voltQueueSQL(rankStmt,game_id,score,2147483647);
	}

        // Passing true parameter since this is the last voltExecuteSQL for this procedure.
        VoltTable[] retval = voltExecuteSQL(true);

	return check; // returning the existing session info
    }
}
