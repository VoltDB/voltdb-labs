package client;

import java.util.Random;
import java.math.BigDecimal;
import java.math.MathContext;
import org.voltdb.types.TimestampType;

public class AdTrackingBenchmark extends BaseBenchmark {

    private Random rand = new Random();
    private MathContext mc = new MathContext(2);
    private BigDecimal bd0 = new BigDecimal(0);
    private long startTime = new TimestampType(System.currentTimeMillis()*1000).getTime();

    // inventory pre-sets
    private int sites = 1000;
    private int pagesPerSite = 20;

    // creatives pre-sets
    private int advertisers = 1000;
    private int campaignsPerAdvertiser = 10;
    private int creativesPerCampaign = 10;
    private int modulus = 100;

    // counters
    private int inventoryMaxID = 0;
    private int creativeMaxID = 0;
    private long iteration = 0L;

    // constructor
    public AdTrackingBenchmark(BenchmarkConfig config) {
        super(config);
        
        // set any instance attributes here
        sites = config.sites;
        pagesPerSite = config.pagespersite;
        advertisers = config.advertisers;
        campaignsPerAdvertiser = config.campaignsperadvertiser;
        creativesPerCampaign = config.creativespercampaign;
        modulus = creativesPerCampaign*3;
    }

    public void initialize() throws Exception {

        // generate inventory
        System.out.println("Loading Inventory table based on " + sites + 
                           " sites and " + pagesPerSite + " pages per site...");
        for (int i=1; i<=sites; i++) {
            for (int j=1; j<=pagesPerSite; j++) {
                inventoryMaxID++;
                client.callProcedure(new BenchmarkCallback("INVENTORY.insert"),
                                     "INVENTORY.insert",
                                     inventoryMaxID,
                                     i,
                                     j);
                // show progress
                if (inventoryMaxID % 5000 == 0) System.out.println("  " + inventoryMaxID);
            }
        }

        // generate creatives
        System.out.println("Loading Creatives table based on " + advertisers + 
                           " advertisers, each with " + campaignsPerAdvertiser + 
                           " campaigns, each with " + creativesPerCampaign + " creatives...");
        for (int advertiser=1; advertiser<=advertisers; advertiser++) {
            for (int campaign=1; campaign<=campaignsPerAdvertiser; campaign++) {
                for (int i=1; i<=creativesPerCampaign; i++) {
                    creativeMaxID++;
                    client.callProcedure(new BenchmarkCallback("CREATIVES.insert"),
                                         "CREATIVES.insert",
                                         creativeMaxID,
                                         campaign,
                                         advertiser);
                    // show progress
                    if (creativeMaxID % 5000 == 0) System.out.println("  " + creativeMaxID);
                }
            }
        }
    }

    public void iterate() throws Exception {

        // generate an impression
        
        // each iteration is 1 millisecond later
        // the faster the throughput rate, the faster time flies!
        // this is to get more interesting hourly or minutely results
        iteration++;
        TimestampType ts = new TimestampType(startTime+(iteration*1000)); 

        // random IP address
        int ipAddress = 
            rand.nextInt(256)*256*256*256 +
            rand.nextInt(256)*256*256 +
            rand.nextInt(256)*256 +
            rand.nextInt(256);

        long cookieUID = (long)rand.nextInt(1000000000);
        int creative = rand.nextInt(creativeMaxID)+1;
        int inventory = rand.nextInt(inventoryMaxID)+1;
        BigDecimal cost = new BigDecimal(rand.nextDouble()/5,mc);

        client.callProcedure(new BenchmarkCallback("TrackEvent"),
                             "TrackEvent",
                             ts,
                             ipAddress,
                             cookieUID,
                             creative,
                             inventory,
                             0,
                             cost);

        int i = rand.nextInt(100);
        int r = creative % modulus;
        // sometimes generate a click-through
        if ( (r==0 && i<10) || i == 0) { // 1% of the time at least, for 1/3 of campaigns 10% of the time
            client.callProcedure(new BenchmarkCallback("TrackEvent"),
                                 "TrackEvent",
                                 ts,
                                 ipAddress,
                                 cookieUID,
                                 creative,
                                 inventory,
                                 1,
                                 bd0);

            // 33% conversion rate
            if ( rand.nextInt(2) == 0 ) {
                client.callProcedure(new BenchmarkCallback("TrackEvent"),
                                     "TrackEvent",
                                     ts,
                                     ipAddress,
                                     cookieUID,
                                     creative,
                                     inventory,
                                     2,
                                     bd0);
            }
        }
    }

    public void printResults() throws Exception {
        
        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Transaction Results");
        System.out.println(HORIZONTAL_RULE);
        BenchmarkCallback.printProcedureResults("INVENTORY.insert");
        BenchmarkCallback.printProcedureResults("CREATIVES.insert");
        BenchmarkCallback.printProcedureResults("TrackEvent");

        super.printResults();
    }
    
    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = BenchmarkConfig.getConfig("AdTrackingBenchmark",args);
        
        BaseBenchmark benchmark = new AdTrackingBenchmark(config);
        benchmark.runBenchmark();

    }
}
