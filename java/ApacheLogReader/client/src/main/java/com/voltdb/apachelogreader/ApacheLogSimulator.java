package com.voltdb.apachelogreader;

/* This file is part of VoltDB.
 * Copyright (C) 2008-2015 VoltDB Inc.
 *
 * This file contains original code and/or modifications of original code.
 * Any modifications made by VoltDB Inc. are licensed under the following
 * terms and conditions:
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.voltdb.VoltTable;
import org.voltdb.VoltType;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ClientStats;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.NullCallback;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

import com.voltdb.apachelogreader.configuration.SampleConfiguration;
import com.voltdb.apachelogreader.configuration.SampleConfigurationFactory;
import com.voltdb.apachelogreader.vo.logentry.CommonApacheLogEntry;
import com.voltdb.apachelogreader.vo.logentry.CommonApacheLogEntryFactory;

/**
 * Executes a process of adding new log entries to a log table and then
 * facilitating analytics against those entries.
 * 
 * @author awilson
 * 
 */
public class ApacheLogSimulator extends BaseVoltApp {

    /**
     * Starts the application. See SampleConfiguration for command line details
     * 
     * @param args
     * 
     */
    public static void main(String[] args) {
        SampleConfiguration config = SampleConfigurationFactory
                .getConfiguration(args);
        ApacheLogSimulator logStream = new ApacheLogSimulator(config);
        try {
            logStream.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApacheLogSimulator(SampleConfiguration config) {
        super(config);
    }

    /**
     * Starts a pair of threads to perform write and read operations to a piped
     * stream. The notion being that a piped stream reader simulates reading
     * data from a live file, much like you would if reading an apache log file.
     */
    @Override
    protected void execute() throws Exception {

        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter();
        // bind the reader to the writer or they will not do anything
        reader.connect(writer);

        // start the write and read threads
        ExecutorService executorWrite = Executors
                .newSingleThreadScheduledExecutor();

        ExecutorService executorRead = Executors
                .newSingleThreadScheduledExecutor();

        executorWrite.submit(new LogWriter(writer));
        // We track only the reader thread because we want to terminate the
        // application when either the write thread stops or the runtime
        // commandline configuration causes the app to terminate
        Future<Boolean> readerThread = executorRead
                .submit(new LogReader(reader));

        // The reader thread can pause if the write thread has been blocked for
        // some reason. We pause for a second and then try again.
        while (readerThread.isDone() == false) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        executorWrite.shutdown();
        executorRead.shutdown();

    }

    /*
     * Creates a read and a write thread to simulate an Apache server writing
     * live data to a file which is being read by a log reader
     */
    class LogWriter implements Callable<Boolean> {
        Writer out;

        public LogWriter(PipedWriter writer) {
            out = writer;
        }

        @Override
        public Boolean call() throws Exception {
            try {

                long runtime = config.runtime * 1000;
                long start = System.currentTimeMillis();

                // Write until we have exceeded the runtime configuration
                while (System.currentTimeMillis() - start < runtime) {
                    // Generate a log entry and write it to the output stream
                    String logEntry = CommonApacheLogEntryFactory.getLogEntry();
                    // Be sure to synchronize on out if you choose to make the
                    // writer multi-threaded.
                    out.write(logEntry);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return true;
        }

    }

    /*
     * The reader thread that reads from the piped reader and passes it to the
     * VoltDB stored proc
     */
    class LogReader implements Callable<Boolean> {
        PipedReader in;

        public LogReader(PipedReader reader) {
            in = reader;
        }

        @Override
        public Boolean call() {
            String remainderBuff = "";
            char buff[] = new char[1000];
            try {
                // Read in the buffer, blocking until we receive some data, EOF
                // or an error.
                int length = 0;
                while ((length = in.read(buff)) > -1) {
                    // We get back the complete buffer, even if the buffer only
                    // contains 100 characters. We have to truncate the rest of
                    // the buffer so that it can be parsed.
                    String tmp = remainderBuff
                            + new String(Arrays.copyOf(buff, length));
                    // Split the log entries into separate lines
                    String[] chunks = tmp.split("\n");

                    if (chunks.length > 1) {
                        remainderBuff = "";
                        for (int i = 0; i < chunks.length; i++) {
                            if (!processLine(chunks[i])) {
                                // This means that the log line failed to
                                // process, almost always an incomplete line
                                // that is split between two consecutive
                                // buffers.
                                remainderBuff += chunks[i];
                            }
                        }
                    } else {
                        // The log line was empty.
                        remainderBuff = tmp;
                    }
                    // reset the buffer.
                    buff = new char[1000];
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

                try {
                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return true;
        }

        /**
         * Parses and stores a log line
         * 
         * @param logLine
         * @return true if the log line could be parsed.
         * @throws Exception
         */
        private boolean processLine(String logLine) throws Exception {
            CommonApacheLogEntry entry = new CommonApacheLogEntry(logLine);
            boolean results = entry.parse();
            if (results) {
                // Call the VoltDB stored procedure. The NullCallback is a
                // convenient way to ignore the results of the procedure and run
                // asynchronously. The application should have some asynchronous
                // logic to track the success of an insert and present an
                // appropriate error if it fails.

                client.callProcedure(new NullCallback(), "InsertLogEntry",
                        entry.getInterval(), logLine, entry.getHost(),
                        entry.getExists(), entry.getUserId(),
                        entry.getTimestamp(), entry.getMethod(),
                        entry.getQuery(), entry.getClient(), entry.getStatus(),
                        entry.getSize(), entry.getExtra());

            }

            return results;
        }

    }

    @Override
    protected void printResults() {
        ClientStats stats = this.fullStatsContext.fetch().getStats();
        System.out.println(HORIZONTAL_RULE);
        System.out.printf("Throughput %d/s, ", stats.getTxnThroughput());
        System.out.printf("Aborts/Failures %d/%d%n",
                stats.getInvocationAborts(), stats.getInvocationErrors());
        System.out.println(HORIZONTAL_RULE);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.voltdb.apachelogreader.BaseVoltApp#printStatistics()
     */
    @Override
    public synchronized void printStatistics() {
        try {
            client.callProcedure(new UtilizationCallback(),
                    "GetUtilizationStats");
        } catch (Exception e) {
            System.out
                    .println("Error occurred trying to get utilization statistics");
            e.printStackTrace();
        }
        super.printStatistics();
    }

    class UtilizationCallback implements ProcedureCallback {

        @Override
        public void clientCallback(ClientResponse cr) throws Exception {
            if (cr.getStatus() == ClientResponse.SUCCESS) {
                VoltTable[] tables = cr.getResults();
                if (tables == null || tables.length == 0) {
                    System.out
                            .println("An error occurred in which the query executed but did not return any stats");
                } else {
                    System.out
                            .println("Interval       Files Downloaded               Total Bytes");
                    System.out
                            .println("---------------------------------------------------------");
                    while (tables[0].advanceRow()) {
                        Integer interval = (Integer) tables[0].get(0,
                                VoltType.INTEGER);
                        Integer downloads = (Integer) tables[0].get(1,
                                VoltType.INTEGER);
                        Integer bytes = (Integer) tables[0].get(2,
                                VoltType.INTEGER);

                        String intervalString = interval.toString();
                        String downloadsString = downloads.toString();
                        String bytesString = bytes.toString();
                        
                        String intervalPadding = getPadding(23 - downloadsString
                                        .length());
                        String downloadPadding = getPadding( 26 - bytesString.length());
                        

                        System.out.printf(" %s%s%s%s%s %n", intervalString,
                                intervalPadding, downloadsString,
                                downloadPadding, bytesString);
                    }

                }
            } else {
                System.out
                        .println("An error has occurred getting bandwith stats.");
            }

        }

        private String getPadding(int padLength) {
            char[] pad = new char[padLength];
            Arrays.fill(pad, ' ');
            return new String(pad);
        }
    }

}
