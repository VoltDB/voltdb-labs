package procedures;

import java.util.Calendar;
import java.util.Date;
import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltTableRow;
import org.voltdb.VoltType;
import org.voltdb.client.ClientResponse;
import org.voltdb.types.TimestampType;

public class TrackImpression extends VoltProcedure {

    public final SQLStmt selectCreative = new SQLStmt(
        "SELECT format_id, campaign_id, advertiser_id FROM creatives WHERE creative_id = ?;");

    public final SQLStmt selectInventory = new SQLStmt(
        "SELECT site_id, page_id FROM inventory WHERE inventory_id = ?;");

    public final SQLStmt insertImpression = new SQLStmt(
        "INSERT INTO impression_data VALUES (" +
        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
        ");");

    public long run( Date    utc_time,
                     int     ip_address,
                     long    creative_id,
                     long    inventory_id,
                     int     type_id
		     ) throws VoltAbortException {

        Calendar cal = Calendar.getInstance();
        cal.setTime(utc_time);

        // truncate to minute
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        Date utc_min = cal.getTime();
        
	// truncate further to hour
        cal.set(Calendar.MINUTE, 0);
        Date utc_hr = cal.getTime();

	// truncate further to day
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date utc_day = cal.getTime();

        // derive flags from type_id
        int is_impression = (type_id == 0) ? 1 : 0;
        int is_clickthrough = (type_id == 1) ? 1 : 0;
        int is_conversion = (type_id == 2) ? 1 : 0;

        // lookup creative_id and inventory_id
        voltQueueSQL(selectCreative, creative_id);
        voltQueueSQL(selectInventory, inventory_id);
        VoltTable lookups[] = voltExecuteSQL();

        VoltTableRow creative = lookups[0].fetchRow(0);
        int format_id = (int)creative.getLong(0);
        int campaign_id = (int)creative.getLong(1);
        int advertiser_id = (int)creative.getLong(2);

        VoltTableRow inventory = lookups[1].fetchRow(0);
        int site_id = (int)creative.getLong(0);
        int page_id = (int)creative.getLong(1);

	voltQueueSQL( insertImpression,
                      utc_time,
                      ip_address,
                      creative_id,
                      inventory_id,
                      type_id,
                      utc_day,
                      utc_hr,
                      utc_min,
                      format_id,
                      campaign_id,
                      advertiser_id,
                      site_id,
                      page_id,
                      is_impression,
                      is_clickthrough,
                      is_conversion
                      );

        voltExecuteSQL();

	return ClientResponse.SUCCESS;
	
    }
}
