DEV NOTES
---------

Initialize:
 Insert games
 Insert players (from random name generator)

Iterate:
 random player starts game session, or get active player
 increase score
 sometimes (1/4)
    end session

Close:
 loop through all active sessions
 increase score
 end sessions
 

Game Play
 1. player logs in
 2. get status (current level, score, etc)
 3. play (one or more iterations)
    a. log some activity
    b. log score for activity
    c. (optional) achieve an award
    d. show updated score/rank
 4. player logs off or session expires


Schema:
  Games
  Users
  GamePlayers
  Scores
  DailyScores

Games:
FALKEN'S MAZE
BLACK JACK
GIN RUMMY
HEARTS
BRIDGE
CHECKERS
CHESS
POKER
FIGHTER COMBAT
GUERRILLA ENGAGEMENT
DESERT WARFARE
AIR-TO-GROUND ACTIONS
THEATERWIDE TACTICAL WARFARE
THEATERWIDE BIOTOXIC AND CHEMICAL WARFARE
GLOBAL THERMONUCLEAR WAR


Real-time analytics:
  Leaderboard
  Active players for each game

** Games Demo
*** Simulation
 - players buy credits, 
 - players earn credits by playing the game
 - players use credits to buy game stuff
 - offer free credits in certain situations
 - suggest games they would like
*** leaderboards
 - top 100 players for each game
 - players currently active
 - unique players daily
 - unique players last 7 days
 - unique players last 30 days
 - game launches hourly
 - game launches daily
 - game daily session length (min, max, avg, STDDEV)
 - custom events hourly, i.e. "Level_7_achieved"
 - custom events daily










This example includes a VoltDB database application, and a client application.

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
  cd db
  ./run.sh

Optionally, you can just compile with the following commands
  ./run.sh srccompile

Optionally, you can load stocks for us or shanghai with one of the following commands:
  ./run.sh init-us                        
  ./run.sh init-shanghai

You can run the benchmark with:
  ./run.sh benchmark

You may want to edit the parameters for the benchmark.  To do this, edit the run.sh file, under
the section "function benchmark()".

You can run the ad-hoc query benchmark with:
  ./run.sh adhoc-benchmark

You can remove the compiled code with:
./run.sh clean


Developer Notes
---------------
To modify this example application for another file and table, you could make the following changes:

DB app:
db/ddl.sql - replace table definition, add additional DDL if needed
db/src/*.java - create stored procedures 
db/project.xml - modify for your table name, column name, procedure names
	       - add single-SQL-statement stored procedures

CLIENT app:
client/run.sh - edit
extend client/src/Loader.java, following the example of StocksLoader.java for a custom file loader
extend client/src/GenericBenchmark.java, following the example of OrdersBenchmark for a custom benchmark


Demo Notes
----------
The following stored procedures can be run from SQLCMD or Studio, during or after the benchmark:



