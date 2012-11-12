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

exec Top10VolStocks

exec Top10VolAccts

exec Top10VolAcctsForStock <symbol>

exec SelectOrdersByAcctStock <acct_id> <symbol>



