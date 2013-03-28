Demographics Example
====================

This demonstrates how to implement a "closed-loop" real-time analytics application written in VoltDB. By "closed-loop" we mean that the data stays entirely within VoltDB ratehr than being exported to a traditional analytics database such as Netezza. The application simulates an ad network data feed, displaying the number of impressions and conversions across a series of ad networks. 

The sample application does the following:
* Generates user data and inserts it into a raw log table.
* The log table goes through an aggregation process that creates demographic profiles from the user data.

The sample also has a web UI:
* Gets the raw data feed to show how many times users view and convert on a per ad stream basis.
* Gets deeper analytics showing the top demographic groups per data source.
* All web queries are made through the VoltDB JSON port.
* All UI's are updated at small, regular intervals.

## Requirements
* VoltDB 2.8, Community or Enterprise Edition
* The sample is geared toward running on a single machine, such as a macbook. It can easily be configured to run on a server.
* Maven 2 or greater

## Building
* Execute 

    mvn clean install

## Running
* Open one terminalwindow to run VoltDB. 
* Go into the ./procs directory.
* Export your VOLT_HOME directory.
  
    export VOLT_HOME=/users/me/voltdb-<version>

* Start the server

    ./run.sh 

Note: You may need to clean the stored procedure catalog. If so, then run:

    ./run.sh clean

* Open a second terminal window to run the data generator application.
* Go into the ./client directory.
* Run the client
  
    ./run.sh

Note: The client is set to connect to the localhost. It can be configured to run against another server.

* Open the ./web/index.htm file in a browser.
Note: This will connect to VoltDB running on the localhost. If you move voltdb to a server, then create an ssh tunnel that will redirect your traffic by opening a new terminal and running:

    ssh -f user@personal-server.com -L 8080:personal-server.com:8080 -N

This will redirect all queries form your localhost to the correct the server.
