package client;

import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcedureCallback;

public class GenericCallback implements ProcedureCallback {

    String procedureName;
    long maxErrors;

    // add CallAsyncImpl attribute  foo

    // constructors
    public GenericCallback(String procedure, long maxErrors) { 
        super();
        this.procedureName = procedure;
        this.maxErrors = maxErrors;
    }

    public GenericCallback(String procedure) {
        this(procedure, 5l);
    }

    @Override
    public void clientCallback(ClientResponse cr) {

        // increment call count
        GenericCallbackCounter.call(procedureName);

        if (cr.getStatus() == ClientResponse.SUCCESS) {
            
            // increment success count
            GenericCallbackCounter.success(procedureName);

        } else {
            // increment error count, and get total
            long totalErrors = GenericCallbackCounter.error(procedureName);

            // exit if max errors exceeded
            if (totalErrors > maxErrors) {
                System.err.println("exceeded " + maxErrors + " maximum database errors - exiting client");
                System.exit(-1);
            }

            // output error to console stderr
            System.err.println("DATABASE ERROR: " + cr.getStatusString());
        }
    }
}

