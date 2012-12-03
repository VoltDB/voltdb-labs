package client;

import org.voltdb.client.ClientStats;
import org.voltdb.VoltTable;

public class OrdersBenchmark extends GenericBenchmark {

    MarketSimulator market;
    
    // constructor
    public OrdersBenchmark(BenchmarkConfig config) {
        super(config);
        
        // set any instance attributes here
        market = new MarketSimulator();
    }

    public void initialize() throws Exception {
        market.getSymbols(client);
        market.getLastOrderID(client);
    }

    public void getSymbols() throws Exception {
        VoltTable tables[] = client.callProcedure("SYMBOLS_selectall").getResults();
        VoltTable table = tables[0];
        //gameCount = table.getRowCount();
    }


    public void iterate() throws Exception {
        market.benchmarkIteration(new GenericCallback("ORDERS.insert"), client);
    }

    public void printResults() throws Exception {
        
        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Transaction Results");
        System.out.println(HORIZONTAL_RULE);
        GenericCallbackCounter.printProcedureResults("ORDERS.insert");

        super.printResults();

    }
    
    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = new BenchmarkConfig();
        config.parse(GenericBenchmark.class.getName(), args);
        
        OrdersBenchmark benchmark = new OrdersBenchmark(config);
        benchmark.runBenchmark();

    }
}
