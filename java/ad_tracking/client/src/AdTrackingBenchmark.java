package client;

import java.util.*;

public class AdTrackingBenchmark extends BaseBenchmark {

    private Random rand = new Random();

    // inventory pre-sets
    private int sites = 1000;
    private int pagesPerSite = 20;

    // creatives pre-sets
    private int advertisers = 1000;
    private int campaignsPerAdvertiser = 10;
    private int creativesPerCampaign = 10;

    // counters
    private int inventoryMaxID = 0;
    private int creativeMaxID = 0;

    // constructor
    public AdTrackingBenchmark(BenchmarkConfig config) {
        super(config);
        
        // set any instance attributes here
        sites = config.sites;
        pagesPerSite = config.pagespersite;
        advertisers = config.advertisers;
        campaignsPerAdvertiser = config.campaignsperadvertiser;
        creativesPerCampaign = config.creativespercampaign;
    }

    public void initialize() throws Exception {

        // generate inventory
        System.out.println("Generating Inventory...");
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
        System.out.println("Generating Creatives...");
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
        int ipAddress = 
            rand.nextInt(256)*256*256*256 +
            rand.nextInt(256)*256*256 +
            rand.nextInt(256)*256 +
            rand.nextInt(256);

        long cookieUID = (long)rand.nextInt(1000000000);
        int creative = rand.nextInt(creativeMaxID)+1;
        int inventory = rand.nextInt(inventoryMaxID)+1;

        client.callProcedure(new BenchmarkCallback("TrackImpression"),
                             "TrackImpression",
                             System.currentTimeMillis(),
                             ipAddress,
                             cookieUID,
                             creative,
                             inventory,
                             0);

        // sometimes generate a click-through
        if ( (creative + inventory) % 20 == 0) {
            client.callProcedure(new BenchmarkCallback("TrackImpression"),
                                 "TrackImpression",
                                 System.currentTimeMillis(),
                                 ipAddress,
                                 cookieUID,
                                 creative,
                                 inventory,
                                 1);

            // sometimes generate a conversion
            if (rand.nextInt(10) == 0) {
                client.callProcedure(new BenchmarkCallback("TrackImpression"),
                                     "TrackImpression",
                                     System.currentTimeMillis(),
                                     ipAddress,
                                     cookieUID,
                                     creative,
                                     inventory,
                                     2);
            }
        }
    }

    public void printResults() throws Exception {
        
        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Transaction Results");
        System.out.println(HORIZONTAL_RULE);
        BenchmarkCallback.printProcedureResults("INVENTORY.insert");
        BenchmarkCallback.printProcedureResults("CREATIVES.insert");
        BenchmarkCallback.printProcedureResults("TrackImpression");

        super.printResults();
    }
    
    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = BenchmarkConfig.getConfig("AdTrackingBenchmark",args);
        
        BaseBenchmark benchmark = new AdTrackingBenchmark(config);
        benchmark.runBenchmark();

    }
}
