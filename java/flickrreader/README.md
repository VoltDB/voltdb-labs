Flickr Feed Reader Example
==========================

Reads a real Flickr JSON feed and stores the results in Volt. Volt extracts all the tags associated with the image and creates a leaderboard. The application queries Flickr about once per second to avoid feed limits and displays the results of the volt query every two seconds.

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

* Open a second terminal window to run the data generator application.
* Run the client

    mvn exec:java

Note: The client is set to connect to the localhost. It can be configured to run against another server.
