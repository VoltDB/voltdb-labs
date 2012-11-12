package client;

import org.voltdb.client.ClientStats;
import java.util.Date;
import java.util.Random;

public class CardBenchmark extends GenericBenchmark {

    final int cardCount = 500000;
    final int transferPct = 2;
    Random rand = new Random();

    

    // constructor
    public CardBenchmark(BenchmarkConfig config) {
        super(config);
        
        // set any instance attributes here
        //market = new MarketSimulator();
    }

    public void initialize() throws Exception {

        System.out.println("Generating " + cardCount + " cards...");

        // loop to create 5M cards
        for (int i=0; i<cardCount; i++) {

            // generate a card
            String pan = Integer.toString(i); // TODO - pad with zeros for 16-digits
            Date now = new Date();

            // insert the card
            client.callProcedure(new GenericCallback("CARD_ACCOUNT.insert"),
                                 "CARD_ACCOUNT.insert",
                                 pan,
                                 1, // ACTIVE
                                 "ACTIVATED",
                                 500,
                                 500,
                                 "USD",
                                 now
                                 );
            if (i % 50000 == 0)
                System.out.println("  " + i);
            
        }
        System.out.println("  " + cardCount);


    }

    public void iterate() throws Exception {
        
        int id = rand.nextInt(cardCount-1);
        String pan = Integer.toString(id);

        client.callProcedure(new GenericCallback("Authorize"),
                             "Authorize",
                             pan,
                             25,
                             "USD"
                             );

        client.callProcedure(new GenericCallback("Redeem"),
                             "Redeem",
                             pan,
                             25,
                             "USD",
                             1
                             );

        if (rand.nextInt(100) < transferPct) {
            int id1 = rand.nextInt(cardCount-1);
            int id2 = rand.nextInt(cardCount-1);

            String pan1 = Integer.toString(id1);
            String pan2 = Integer.toString(id2);

            client.callProcedure(new GenericCallback("Transfer",10000),
                                 "Transfer",
                                 pan1,
                                 pan2,
                                 5,
                                 "USD"
                                 );
        }
        
    }

    public void printResults() throws Exception {
        
        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Transaction Results");
        System.out.println(HORIZONTAL_RULE);
        GenericCallbackCounter.printProcedureResults("CARD_ACCOUNT.insert");
        GenericCallbackCounter.printProcedureResults("Authorize");
        GenericCallbackCounter.printProcedureResults("Redeem");
        GenericCallbackCounter.printProcedureResults("Transfer");

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
        
        CardBenchmark benchmark = new CardBenchmark(config);
        benchmark.runBenchmark();

    }
}
