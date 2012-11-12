package client;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Random;

import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcedureCallback;


public class StocksLoader extends Loader {

    public static final String procedureName = "UpsertSymbol";

    private long requests = 0;
    private static AtomicLong successes = new AtomicLong();
    private static AtomicLong errors = new AtomicLong();
    private Random rand = new Random();

    static class StocksCallback implements ProcedureCallback {
	@Override
	public void clientCallback(ClientResponse cr) {
	    if (cr.getStatus() == ClientResponse.SUCCESS) {
		successes.incrementAndGet();
	    } else {
		long total_errors = errors.incrementAndGet();
		if (total_errors > 50) {
		    System.err.println("exceeded maximum database errors - exiting client");
		    System.exit(-1);
		}
		System.out.println("DATABASE ERROR: " + cr.getStatusString());
	    }
	}
    }

    protected void loadRecord(String line) throws Exception {
        // columns: 0 - Company, 1 - Symbol (first 6 chars)
        String[] cols = line.split("\t");
        String company = cols[0].trim();
        String symbol = cols[1].substring(0,6); // first 6 chars only

        // set a fake price, since prices are not provided
	Double last_sale = 1000l * (1+rand.nextGaussian());
	Double market_cap = null;
	Long total_shares = 10000000l;
	//BigDecimal total_shares = null;
	Integer ipo_year = null;
	String sector = null;
	String industry = null;

        ProcedureCallback cb = new StocksCallback();
        client.callProcedure(cb,
                             procedureName,
                             symbol,
                             company,
                             last_sale,
                             market_cap,
                             total_shares,
                             ipo_year,
                             sector,
                             industry
                             );
        requests++;
    }


    protected void loadRecord(String[] line) throws Exception {

	String symbol = line[0];
	String company = line[1];
	Double last_sale;
	Double market_cap;
	Long total_shares;
	//BigDecimal total_shares;
	Integer ipo_year;
	String sector = line[6];
	String industry = line[7];
	 
	// handle non-numeric values such as "n/a" as nulls
	try {
	    last_sale = new Double(line[2]);
	} catch (Exception e) {
	    last_sale = 50.00;
	}
	try {
	    market_cap = new Double(line[3]);
	} catch (Exception e) {
	    market_cap = null;
	}
	try {
	    total_shares = new Long(line[4]);
	    //total_shares = new BigDecimal(line[4]);
	} catch (Exception e) {
	    total_shares = null;
	}
	try {
	    ipo_year = new Integer(line[5]);
	} catch (Exception e) {
	    ipo_year = null;
	}

	try {
	    // call the stored procedure
	    ProcedureCallback cb = new StocksCallback();
	    client.callProcedure(cb,
				 procedureName,
				 symbol,
				 company,
				 last_sale,
				 market_cap,
				 total_shares,
				 ipo_year,
				 sector,
				 industry
				 );
	    requests++;

	} catch (Exception e) {
	    System.err.println("line: " + line);
	    throw new Exception(e);
	}
    }

    public void printResults() throws Exception {
	client.drain();

	System.out.println();
	System.out.println("Transaction Results");
	System.out.printf(
            " - %,9d Requests sent to VoltDB\n"
            + " - %,9d Committed\n"
            + " - %,9d Rolled Back due to failure\n"
            + "\n"
            , requests
            , successes.get()
            , errors.get()
			  );
    }

    // constructor
    public StocksLoader(LoaderConfig config) throws Exception {
	super(config);
    } 

    public static void main(String[] args) {
	try {
            LoaderConfig config = new LoaderConfig();
            config.parse(Loader.class.getName(), args);

	    StocksLoader loader = new StocksLoader(config);

            if (config.filename.equals("data/shanghai.txt")) {
                loader.loadFlatFile(config.filename);
            } else {
                loader.loadCSVFile(config.filename);
            }

	    loader.printResults();
	    loader.close();

	} catch (Exception e) {
	    System.err.println("Unexpected Exception:");
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	    System.exit(-1);
	}
    }
}
