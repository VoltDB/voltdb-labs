# README for VoltDB Ad Tracking application #

Use Case
--------
This application simulates a high velocity stream of events (impressions, clickthroughs, conversions) that are enriched and ingested.  These events are randomly generated in the client, but represent a stream of events that would be received from the web.

The "TrackImpression" stored procedure processes these events.  It looks up the corresponding advertiser and campaign based on the creative ID which represents which ad was shown.  It also retrieves the corresponding web site and page based on the inventory ID from the event.  The timestamp and event type fields are converted to aid in aggregation, and all of this data is then inserted into the impression_data table.

Several views maintain real-time aggregations on this table to provide hourly and minutely summaries to provide instantaneous click-through rates (CTR) and conversion rates by campaign.

Code organization
-----------------
The code is divided into two projects:

- "db": the database project, which contains the schema, stored procedures and other configurations that are compiled into a catalog and run in a VoltDB database.  
- "client": a java client that loads a set of cards and then generates random card transactions a high velocity to simulate card activity.

See below for instructions on running these applications.  For any questions, 
please contact fieldengineering@voltdb.com.


Database application
--------------------

The database application must be started first.  It can be run from the command line
or the catalog file can be deployed using VoltDB Enterprise Manager which is
provided with VoltDB Enterprise Edition.


To build and run the database application:

    cd db
    ./run.sh

Optionally, you can just compile or build the catalog with the following commands

    ./run.sh srccompile
    ./run.sh catalog

You can remove the compiled code with:

    ./run.sh clean

To configure for more than 1 node, edit the deployment.xml file.  Basic instructions
are in Using VoltDB, Section 6.1, and all the options for the deploymen file are described
in Appendix D.


Client application
------------------
To build and run the database application:

    cd client
    ./run.sh

Optionally, you can just compile with the following commands

    ./run.sh srccompile

You can run the benchmark with:

    ./run.sh benchmark

You may want to edit the parameters for the benchmark.  To do this, edit the run.sh file, under
the section "function benchmark()".

You can remove the compiled code with:

    ./run.sh clean

