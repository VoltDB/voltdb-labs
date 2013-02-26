package client;

import java.util.*;

public class CardBenchmark extends BaseBenchmark {

    private Random rand = new Random();
    private int cardCount = 500000;
    private int transferPct = 2;

    // constructor
    public CardBenchmark(BenchmarkConfig config) {
        super(config);
        
        // set any instance attributes here
        cardCount = config.cardcount;
        transferPct = config.transferpct;
    }

    public void initialize() throws Exception {

        System.out.println("Generating " + cardCount + " cards...");
        for (int i=0; i<cardCount; i++) {

            // generate a card
            String pan = Integer.toString(i); // TODO - pad with zeros for 16-digits
            Date now = new Date();

            // insert the card
            client.callProcedure(new BenchmarkCallback("CARD_ACCOUNT.insert"),
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

        client.callProcedure(new BenchmarkCallback("Authorize"),
                             "Authorize",
                             pan,
                             25,
                             "USD"
                             );

        client.callProcedure(new BenchmarkCallback("Redeem"),
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

            client.callProcedure(new BenchmarkCallback("Transfer",10000),
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
        BenchmarkCallback.printProcedureResults("CARD_ACCOUNT.insert");
        BenchmarkCallback.printProcedureResults("Authorize");
        BenchmarkCallback.printProcedureResults("Redeem");
        BenchmarkCallback.printProcedureResults("Transfer");

        super.printResults();
    }
    
    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = BenchmarkConfig.getConfig("CardBenchmark",args);
        
        BaseBenchmark benchmark = new CardBenchmark(config);
        benchmark.runBenchmark();

    }
}
