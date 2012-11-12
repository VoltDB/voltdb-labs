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

        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Client Workload Statistics");
        System.out.println(HORIZONTAL_RULE);
        ClientStats stats = fullStatsContext.fetch().getStats();
        System.out.printf("Average throughput:            %,9d txns/sec\n", stats.getTxnThroughput());
        System.out.printf("Average latency:               %,9d ms\n", stats.getAverageLatency());
        System.out.printf("95th percentile latency:       %,9d ms\n", stats.kPercentileLatency(.95));
        System.out.printf("99th percentile latency:       %,9d ms\n", stats.kPercentileLatency(.99));

        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" System Server Statistics");
        System.out.println(HORIZONTAL_RULE);
        if (config.autotune) {
            System.out.printf("Targeted Internal Avg Latency: %,9d ms\n", config.latencytarget);
        }
        System.out.printf("Reported Internal Avg Latency: %,9d ms\n", stats.getAverageInternalLatency());

        client.writeSummaryCSV(stats, config.statsfile);
    }
    
    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = new BenchmarkConfig();
        config.parse(GenericBenchmark.class.getName(), args);
        
        OrdersBenchmark benchmark = new OrdersBenchmark(config);
        benchmark.runBenchmark();

    }
}
