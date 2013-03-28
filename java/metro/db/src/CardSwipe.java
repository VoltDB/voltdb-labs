package procedures;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.voltdb.*;
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

public class CardSwipe extends VoltProcedure {

    public final SQLStmt checkStmt = new SQLStmt("SELECT * FROM metrocards WHERE card_id = ?;");

    public final SQLStmt insertStmt = new SQLStmt("INSERT INTO card_swipes VALUES (?,?,?,?,?,?,?);");


    public long run( int           card_id,
		     TimestampType date_time,
		     int           location_id
		     ) throws VoltAbortException {

	long result = 0;

	voltQueueSQL(checkStmt, EXPECT_ZERO_OR_ONE_ROW, card_id);
	VoltTable check[] = voltExecuteSQL();
	

	if (check[0].getRowCount() == 0) {
	    // card was not found
	    return 0;
	}

	VoltTableRow card = check[0].fetchRow(0);
	String status = card.getString(1); // 1 is the index # of the card_status column

	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	int date_int = Integer.parseInt(df.format(date_time.asApproximateJavaDate()));

	df = new SimpleDateFormat("hh");
	int hour_int = Integer.parseInt(df.format(date_time.asApproximateJavaDate()));

	int is_green = 0;
	if (status.equals("green")) {
	    is_green = 1;
	    result = 1;
	}
	
	int is_red = 0;
	if (status.equals("red")) {
	    is_red = 1;
	    result = 2;
	}

	voltQueueSQL( insertStmt, 
		      card_id,
		      date_time,
		      date_int,
		      hour_int,
		      location_id,
		      is_green,
		      is_red);

	VoltTable[] retval = voltExecuteSQL(true);

	return result;

    }
}
