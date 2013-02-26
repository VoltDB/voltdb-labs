package procedures;

import java.math.BigDecimal;
import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltTableRow;
import org.voltdb.VoltType;
import org.voltdb.client.ClientResponse;
import org.voltdb.types.TimestampType;

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

public class InsertTrade extends VoltProcedure {

    public final SQLStmt lastTick = new SQLStmt("SELECT price FROM trades WHERE" +
						" symbol = ?" +
						" ORDER BY seq_no DESC" +
						" LIMIT 1"
						);

    public final SQLStmt insertStmt = new SQLStmt("INSERT INTO trades VALUES (" +
						  "?,?,?,?,?,?,?,?,?,?,?,?" +
						  ");");

    public long run( String         symbol,
		     TimestampType  datetime,
		     double         price,
		     int            volume,
		     String         exchange,
		     String         sales_cond,
		     String         correction,
		     long           seq_no,
		     String         trade_stop
		     ) throws VoltAbortException {

	// check if symbol exists
	voltQueueSQL(lastTick, EXPECT_ZERO_OR_ONE_ROW, symbol);
	VoltTable last[] = voltExecuteSQL();

	int uptick = 0;
	int downtick = 0;
	int sametick = 0;

	if (last[0].getRowCount() == 1) {
	    VoltTableRow lastTickRow = last[0].fetchRow(0);
	    
	    double lastPrice = (double)lastTickRow.getDouble(2);
	    
	    if (lastPrice < price)
		uptick = 1;
	    if (lastPrice < price)
		downtick = 1;
	    if (lastPrice == price)
		sametick = 1;

	}
	
	// insert
	voltQueueSQL( insertStmt,
		      symbol,
		      datetime,
		      price,
		      volume,
		      exchange,
		      sales_cond,
		      correction,
		      seq_no,
		      trade_stop,
		      uptick,
		      downtick,
		      sametick
			  );

        // Passing true parameter since this is the last voltExecuteSQL for this procedure.
        VoltTable[] retval = voltExecuteSQL(true);

	return ClientResponse.SUCCESS;
	
    }
}
