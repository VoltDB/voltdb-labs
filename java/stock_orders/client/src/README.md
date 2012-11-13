# README for client Benchmark package #

This java client source is very similar in operation to the "voter" example, but has been refactored to separate business logic code from boilerplate benchmark code.

The **GenericBenchmark** class is the primary driving class, which implements most of the boilerplate benchmark code found in voter.

The **OrdersBenchmark** class extends GenericBenchmark, and overrides mainly three methods:

- **initialize** - checks that the symbols are loaded and gets the most recent Order ID
- **iterate** - each call to this method is one iteration of the main benchmark loop which is in the GenericBenchmark.runBenchmark method.  Here, we generate random orders.
- **printResults** - this is customized to print the number of calls, commits and rollbacks to each of the stored procedures used in the simulation, in addition to standard benchmark stats.

The **Loader** class is a generic CSV Loader superclass.
The **StocksLoader** class extends **Loader**, and does the specific parsing of each line of a file and calls the appropriate stored procedure to insert the record.

The remaining classes are "helpers":

- **MarketSimulator** - this class tracks the latest prices of all the symbols and helps in the generation of random orders.
- **GenericCallback** - a callback class that can be used to track the results (calls, commits, rollbacks) of stored procedure calls.  Rather than writing a new callback class for each stored procedure that gets called (which is still an option), this class can be used for any procedure call.
- **GenericCallbackCounter** - a class that maintains atomic counts of the results (calls, commits, rollbacks) as received by objects of the GenericCallback class.  Rather than putting atomic counters for each type of procedure call in the benchmark, and code to print the results for each counter, this class is reusable.


