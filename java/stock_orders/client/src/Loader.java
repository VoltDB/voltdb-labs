package client;

import au.com.bytecode.opencsv_voltpatches.CSVReader; // this is packaged in the voltdb-*.jar file

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.Properties;
//import java.util.concurrent.atomic.AtomicLong;

import org.voltdb.CLIConfig;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ProcedureCallback;

public class Loader {

    final LoaderConfig config;
    Client client;

    static class LoaderConfig extends CLIConfig {
        @Option(desc = "Comma separated list of the form server[:port] to connect to.")
        String servers = "localhost";

        @Option(desc = "[us|shanghai] specify which type of file to load.")
        String filename = "NYSE.csv";

        @Option(desc = "number of lines to skip in the file")
        int skiplines = 1;

    }

    private void connect(String hostnames[]) throws IOException {
	if (client == null) {
	    ClientConfig config = new ClientConfig();
	    client = ClientFactory.createClient(config);
	    if (hostnames != null && hostnames.length > 0) {
		for (String host : hostnames) {
                    System.out.println("connecting to " + host.trim());
		    client.createConnection(host.trim());
			
		}
	    } else {
		throw new IOException("No servers specified");
	    }
	}
    }

    /*
    private void loadProperties(String filename) throws Exception {
	try {
	    FileInputStream fis = new FileInputStream(filename);
	    props = new Properties();
	    props.load(fis);
	    fis.close();
	} catch (IOException ioe) {
	    System.err.println("IOException in loadProperties:");
	    System.err.println(ioe.getMessage());
	}
    }
    */

    protected void loadCSVFile(String filename) throws Exception {
	int counter = 0;
	String[] nextLine;
	
	try {
	    CSVReader reader = new CSVReader(new FileReader(filename));
            System.out.println("reading file " + filename);
	    if (config.skiplines > 0) {
                System.out.println("skipping " + config.skiplines + " header line(s)");
		for (int i=0; i<config.skiplines; i++) {
		    reader.readNext();
		}
	    }
	    
	    while ((nextLine = reader.readNext()) != null) {
		counter++;
		loadRecord(nextLine);
	    }
	    reader.close();
            System.out.println("loaded " + Integer.toString(counter) + " records from " + filename);

	} catch (Exception e) {
	    System.err.println("Exception in loadCSVFile on reading line " + counter + " of " + filename);
	    throw new Exception(e);
	}
    }

    protected void loadFlatFile(String filename) throws Exception {
	int counter = 0;
	String line = null;
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
            System.out.println("reading from file: " + filename);

	    while ((line = reader.readLine()) != null) {
		counter++;
		loadRecord(line);

		if (counter % 100000 == 0)
		    System.out.println("  read " + counter + " lines");
	    }
	    reader.close();
            System.out.println("sent " + Integer.toString(counter) + " records from " + filename);

	} catch (Exception e) {
	    System.err.println("Exception in loadFlatFile on reading line " + counter + " of " + filename);
	    throw new Exception(e);
	}
    }

    protected void loadRecord(String[] line) throws Exception {
	// takes a String[] - called from loadCSVFile
	/*
	ProcedureCallback cb = new LoaderCallback();
	Con.executeAsync(cb,
			 "UpsertMyRecord",
			 line[0],
			 line[1],
			 line[],
			 );
	*/
    }

    protected void loadRecord(String line) throws Exception {
	// takes a String - called from loadFlatFile
	/*
	ProcedureCallback cb = new LoaderCallback();
	Con.executeAsync(cb,
			 "UpsertMyRecord",
			 line[0],
			 line[1],
			 line[],
			 );
	*/
    }


    public void close() throws Exception {
	client.drain();
	client.close();
    }


    // constructor
    public Loader(LoaderConfig config) throws Exception {
        this.config = config;

	//loadProperties(properties);

	// get node host names from props and connect to them
	String[] hostnames = config.servers.split(",");
	connect(hostnames);
	
    } 

    public static void main(String[] args) throws Exception {

        LoaderConfig config = new LoaderConfig();
        config.parse(Loader.class.getName(), args);

        Loader loader = new Loader(config);

        // subclass should call something like this:
        loader.loadCSVFile(config.filename);

        loader.close();

    }
}
