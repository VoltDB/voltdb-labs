package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.Date;
import java.util.Random;

import org.voltdb.VoltTable;
import org.voltdb.VoltTableRow;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcedureCallback;
//import org.voltdb.client.exampleutils.ClientConnection;
import org.voltdb.types.VoltDecimalHelper;
import org.voltdb.types.TimestampType;

public class MarketSimulator {

    public class Order {
	// object attributes
	public long id;
	public int version;
	public int acct_id;
	public String acct_type;
	public String symbol;
	public String b_s;
	public int b_shares = 0;
	public int b_pend_shares = 0;
	public int b_canc_shares = 0;
	public int s_shares = 0;
	public int s_pend_shares = 0;
	public int s_canc_shares = 0;
	public double stop_price = 0;
	public double limit_price = 0;
	public TimestampType expires;
	public String time_in_force;
	public String handling_code;
    }

    public class Symbol {
	public String symbol;
	public double price;
    }
    
    private long last_order_id = 0;
    private final String[] b_s_vals = new String[] {"B","S"};
    private Random rand = new Random();
    private ArrayList<Symbol> symbols = new ArrayList<Symbol>();

    /*
      private int symbolIndex;
      private ArrayList<String> symbols = new ArrayList<String>();
      private ArrayList<Double> prices = new ArrayList<Double>();
    */

    // default constructor
    public MarketSimulator() {
	System.out.println();
	System.out.println("constructing new MarketSimulator");
    }

    public void getSymbols(Client client) throws Exception {
	
	// using synchronous call for convenience outside the benchmark run
	System.out.println("Initializing symbols for simulation");
	VoltTable results[] = client.callProcedure("SYMBOLS_selectall").getResults();
	VoltTable records = results[0];
	//System.out.println("  got results, rows = " + records.getRowCount());
        
        // if no symbols, exit with message
        if (records.getRowCount() == 0) {
            System.out.println("\nERROR - No symbols loaded!  Use one of the following commands:");
            System.out.println("   ./run.sh init-us");
            System.out.println("   ./run.sh init-shanghai");
            System.out.println("\nExiting");
            System.exit(1);
        }
	
	while (records.advanceRow()) {
	    Symbol s = new Symbol();
	    s.symbol = records.getString(0); //symbol
	    s.price = records.getDouble(2); //last_sale
	    symbols.add(s);
	    //System.out.println("  loading symbol " + records.getString(0) + " as " + s.symbol);
	}
    }

    public void getLastOrderID(Client client) throws Exception {
	
	// use synchronous call just this once ;)
	VoltTable results[] = client.callProcedure("select_max_order_id").getResults();
	VoltTable records = results[0];
	if (records.getRowCount() == 1) {
	    long last_id = records.fetchRow(0).getLong(0);
	    if ( last_id > 0 )
		last_order_id = last_id;
	}

	System.out.println("last order_id was " + last_order_id);
    }

    public Order newOrder() {
	
	Symbol s = symbols.get(rand.nextInt(symbols.size())); // retrieve a random symbol

	Order o = new Order();
	o.id = ++last_order_id; // increment last_order_id and use the new value
	o.version = 1;
	o.acct_id = rand.nextInt(1000)+1;
	o.acct_type = "";
	o.symbol = s.symbol;
	o.b_s = b_s_vals[rand.nextInt(2)];
	
	if (o.b_s == "B") {
	    // buy
	    o.b_shares = (rand.nextInt(100)+1)*10; //random 10-1000 on even tens
	    o.b_pend_shares = o.b_shares;
	} else {
	    o.s_shares = (rand.nextInt(100)+1)*10; //random 10-1000 on even tens
	    o.s_pend_shares = o.b_shares;
	}

	// price
	double new_price = s.price * (1+rand.nextGaussian()/100); // modify price randomly
	DecimalFormat df = new DecimalFormat("#.##");
 	o.limit_price = Double.valueOf(df.format(new_price)); // round to the nearest penny
        s.price = o.limit_price; // save price for next time

	o.expires = null;
	o.time_in_force = "";
	o.handling_code = "";

	return o;
    }

    public Order matchOrder(Order o) {

	o.version++;
	o.b_pend_shares = 0;
	o.s_pend_shares = 0;
	
	return o;
    }


    public Order cancelOrder(Order o) {

	// increment version
	o.version++;

	// cancel buy
	o.b_canc_shares = o.b_pend_shares;
	o.b_pend_shares = 0;

	// cancel sell
	o.s_canc_shares = o.s_pend_shares;
	o.s_pend_shares = 0;
	
	return o;
    }

    public void insertOrder(ProcedureCallback cb, Client client, Order o) throws Exception {

	// insert the order
	client.callProcedure(cb,
                             "ORDERS.insert",
                             o.id,
                             o.version,
                             o.acct_id,
                             o.acct_type,
                             o.symbol,
                             o.b_s,
                             o.b_shares,
                             o.b_pend_shares,
                             o.b_canc_shares,
                             o.s_shares,
                             o.s_pend_shares,
                             o.s_canc_shares,
                             o.stop_price,
                             o.limit_price,
                             o.expires,
                             o.time_in_force,
                             o.handling_code,
                             new TimestampType(new Date()),
                             new TimestampType(new Date())
                             );

    }
	
    public void benchmarkIteration(ProcedureCallback cb, Client client) throws Exception {

	// get a new random order and insert it
	Order o = newOrder(); 
	
	// match 1/3 of orders
	if (rand.nextInt(3) == 0) {
	    o = matchOrder(o);
	}

	// cancel 1/3 of orders
	if (rand.nextInt(3) == 0) {
	    o = cancelOrder(o);
	}

	insertOrder(cb,client,o);

    }


    public void modOrder() {
	// modify fields to make a new version of the last order
	//OrderID++;
	//Version++;
	//
	//CreationTime = new TimestampType(new Date());
	//TransactionTime = CreationTime;
	//HandlInst = rand.nextInt(3);
	//
	//CumQty += Qty;
	//LeavesQty = Qty/2;
	//Qty = Qty/2;
	//
	//UrgencyCode = rand.nextInt(4);
    }

    public void cancelOrder() {
	// modify fields to make a new version of the last order
	//OrderID++;
	//Version++;
	//
	//CreationTime = new TimestampType(new Date());
	//TransactionTime = CreationTime;
	//HandlInst = rand.nextInt(3);
	//
	//Qty = LeavesQty;
	//CancelledQty = LeavesQty;
	//LeavesQty = 0;
	//
	//UrgencyCode = rand.nextInt(4);
    }


    public void asyncUpsertOrder(ProcedureCallback cb, Client client) throws Exception {
	/*
          client.callProcedure(cb,
          "UpsertOrder",
          AlgoId,	   
          Integer.toString(OrderID),
          Integer.toString(OrigClOrderId),   
          Integer.toString(ClientOrderId),   
          Version,
          SymbolID,
          CreationTime,
          TransactionTime,
          HandlInst,
          Side,
          Qty,
          OrdType,
          Price,
          LeavesQty,
          CumQty,
          AvgPx,
          OrdStatus,	   
          Desk,		   
          Trader, 	   
          ClientId,	   
          OrderCapacity,   
          TimeInForce,	   
          Text, 	   
          UrgencyCode,	   
          OverfilledFlag,  
          AlertFlag,       
          CancelledQty,    
          Comments, 	   
          ServerInstance
          );
	*/
    }

    public void asyncQueryOrders(ProcedureCallback cb, Client client) throws Exception {

	/*
	client.callProcedure(cb,
			     "QueryOrder",
			     Integer.toString(rand.nextInt(50)), // random AlgoId
			     Integer.toString(rand.nextInt(10))   // random ServerInstance
			     );
	*/
	//client.callProcedure(cb,"query_client_desk_sum");
	//client.callProcedure(cb,"query_client_desk_tab");

	//client.callProcedure(cb,"server_algo_sum");
	//client.callProcedure(cb,"server_algo_tab");

    }


}
