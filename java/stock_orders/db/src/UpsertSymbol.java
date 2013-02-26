package procedures;

import java.math.BigDecimal;
import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;
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

public class UpsertSymbol extends VoltProcedure {

    public final SQLStmt checkStmt = new SQLStmt("SELECT COUNT(*) FROM symbols WHERE symbol = ?");
    public final SQLStmt insertStmt = new SQLStmt("INSERT INTO symbols VALUES (" +
						  "?,?,?,?,?,?,?,?" +
						  ");");

    public final SQLStmt updateStmt = new SQLStmt("UPDATE symbols SET " +
						  " company_name = ?," +
						  " last_sale = ?," +
						  " market_cap = ?," +
						  " total_shares = ?," +
						  " ipo_year = ?," +
						  " sector = ?," +
						  " industry = ?" +
						  " WHERE symbol = ?;");
    
    public long run( String     symbol,
		     String     company,
		     double     last_sale,
		     double     market_cap,
		     long       total_shares,
		     int        ipo_year,
		     String     sector,
		     String     industry
		     ) throws VoltAbortException {


	// check if symbol exists
	voltQueueSQL(checkStmt, EXPECT_SCALAR_LONG, symbol);
	long rowsFound = voltExecuteSQL()[0].asScalarLong();

	// update or insert
	if (rowsFound == 1) {
	    voltQueueSQL( updateStmt,
			  company,
			  last_sale,
			  market_cap,
			  total_shares,
			  ipo_year,
			  sector,
			  industry,
			  symbol
			  );
	} else {
	    voltQueueSQL( insertStmt,
			  symbol,
			  company,
			  last_sale,
			  market_cap,
			  total_shares,
			  ipo_year,
			  sector,
			  industry
			  );
	}

        // Passing true parameter since this is the last voltExecuteSQL for this procedure.
        VoltTable[] retval = voltExecuteSQL(true);

	return ClientResponse.SUCCESS;
	
    }
}
