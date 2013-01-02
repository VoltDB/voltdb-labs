# README for VoltDB Games example application #

This application is meant to approximate the workload of a platform that supports multiple online games.  It manages sessions and keeps scores for thousands or even millions of players, while supporting realtime analytics.

The code is divided into two projects.  Under the "db" directory is the database project, which contains the schema, stored procedures and other configurations that are compiled into a catalog and run in a VoltDB database.  Under the "client" directory is a java client that loads some preliminary data and then generates random events at a high velocity to simulate player activity.

## Synopsis of the simulation ##

On Initialization:
 Games are inserted
 Random players are generated and inserted.

The benchmark has a brief warm-up period and then runs for a specified duration.  Each iteration of the main loop does the following:

- af random player starts a game session, or a current session is selected
- the player's score and ranking is retrieved
- the player's score increases, perhaps several times
- the player level's up, perhaps more than once.
- sometimes the player is set aside as an "ongoing session that will continue playing in another iteration, and sometimes the session ends.

See below for instructions on running the database and client applications.  For any questions, 
please contact fieldengineering@voltdb.com.

Database application: /db
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
are in Using VoltDB, Section 6.1, and all the options for the deployment file are described
in Appendix D.

Client application: /client
------------------
To build and run the client:
  cd db
  ./run.sh

Optionally, you can just compile with the following commands
  ./run.sh srccompile

You may want to edit the parameters for the benchmark.  To do this, edit the run.sh file, under
the section "function benchmark()".

You can remove the compiled code with:
./run.sh clean

