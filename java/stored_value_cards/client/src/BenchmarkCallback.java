package client;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcedureCallback;

public class BenchmarkCallback implements ProcedureCallback {

    private static Multiset<String> stats = ConcurrentHashMultiset.create();
    String procedureName;
    long maxErrors;

    public static int count( String procedureName, String event ){
        return stats.add(procedureName + event, 1);
    }

    public static int getCount( String procedureName, String event ){
        return stats.count(procedureName + event);
    }

    public static void printProcedureResults(String procedureName) {
        System.out.println("  " + procedureName);
        System.out.println("        calls: " + getCount(procedureName,"call"));
        System.out.println("      commits: " + getCount(procedureName,"commit"));
        System.out.println("    rollbacks: " + getCount(procedureName,"rollback"));
    }

    public BenchmarkCallback(String procedure, long maxErrors) { 
        super();
        this.procedureName = procedure;
        this.maxErrors = maxErrors;
    }

    public BenchmarkCallback(String procedure) {
        this(procedure, 5l);
    }

    @Override
    public void clientCallback(ClientResponse cr) {

        count(procedureName,"call");

        if (cr.getStatus() == ClientResponse.SUCCESS) {
            count(procedureName,"commit");
        } else {
            long totalErrors = count(procedureName,"rollback");

            if (totalErrors > maxErrors) {
                System.err.println("exceeded " + maxErrors + " maximum database errors - exiting client");
                System.exit(-1);
            }

            System.err.println("DATABASE ERROR: " + cr.getStatusString());
        }
    }
}

