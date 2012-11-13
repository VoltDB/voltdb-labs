# README for VoltDB stock orders application#

This application performs a high velocity ingestion of equities order data and supports realtime analytic summaries of this order activity.

The code is divided into two projects.  Under the "db" directory is the database project, which contains the schema, stored procedures and other configurations that are compiled into a catalog and run in a VoltDB database.  Under the "client" directory is a java client that loads the stock symbols and then generates random orders at a high velocity to simulate market activity.

See below for instructions on running these applications.  For any questions, 
please contact fieldengineering@voltdb.com.


Database application: /db
-------------------------
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


Client application: /client
---------------------------
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

You can remove the compiled code with:
./run.sh clean


Demonstration Notes
-------------------
The following stored procedures show the results of realtime-calculated aggregations.  They can be run from SQLCMD or Studio while the client is running, or afterwards:

exec Top10VolStocks

exec Top10VolAccts

exec Top10VolAcctsForStock <symbol>

exec SelectOrdersByAcctStock <acct_id> <symbol>



