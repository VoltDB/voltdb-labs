# README for VoltDB Ad Tracking application #

Use Case
--------
This application simulates a high velocity stream of events (impressions, clickthroughs, conversions) that are enriched and ingested.  These events are randomly generated in the client, but represent a stream of events that would be received from web traffic.

The "TrackEvent" stored procedure processes these events.  It looks up the corresponding advertiser and campaign based on the creative ID which represents which ad was shown.  It also retrieves the corresponding web site and page based on the inventory ID from the event.  The timestamp and event type fields are converted to aid in aggregation, and all of this data is then inserted into the impression_data table.

Several views maintain real-time aggregations on this table to provide a minutely summary for each advertiser, plus drill-down reports grouped by campaign and creative to show detail-level metrics, costs and rates with real-time accuracy.

Code organization
-----------------
The code is divided into two projects:

- "db": the database project, which contains the schema, stored procedures and other configurations that are compiled into a catalog and run in a VoltDB database.  
- "client": a java client that loads a set of cards and then generates random card transactions a high velocity to simulate card activity.

See below for instructions on running these applications.  For any questions, 
please contact fieldengineering@voltdb.com.

Instructions
------------

1. Start the database in the background

     ./start_db.sh
     
2. Run the client application

    ./run_client.sh

3. Open a web browser to VoltDB Studio

    http://localhost:8080/studio
    
4. Run the following SQL commands for real-time reporting:

    exec advertiser_minutely 30;
    exec advertiser_summary 30;
    exec campaign_summary 30 1;

5. To stop the database and clean up temp files

    voltadmin shutdown
    ./clean.sh

