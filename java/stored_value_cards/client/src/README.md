# README for client Benchmark package #

This java client source is very similar in operation to the "voter" example, but has been refactored to separate business logic code from boilerplate benchmark code.

The **GenericBenchmark** class is the primary driving class, which implements most of the boilerplate benchmark code found in voter.

The **CardBenchmark** class extends GenericBenchmark, and overrides mainly three methods:

- **initialize** - inserts the cards
- **iterate** - each call to this method is one iteration of the main benchmark loop which is in the GenericBenchmark.runBenchmark method.  Here, we simulate preauthorizations, purchases and transfers.
- **printResults** - this is customized to print the number of calls, commits and rollbacks to each of the stored procedures used in the simulation, in addition to standard benchmark stats.

The remaining classes are "helpers":

- **GenericCallback** - a callback class that can be used to track the results (calls, commits, rollbacks) of stored procedure calls.  Rather than writing a new callback class for each stored procedure that gets called (which is still an option), this class can be used for any procedure call.
- **GenericCallbackCounter** - a class that maintains atomic counts of the results (calls, commits, rollbacks) as received by objects of the GenericCallback class.  Rather than putting atomic counters for each type of procedure call in the benchmark, and code to print the results for each counter, this class is reusable.

If you were to use this package of classes as a starting point to build a new benchmark, the intention is that you would only need to modify CardBenchmark.
