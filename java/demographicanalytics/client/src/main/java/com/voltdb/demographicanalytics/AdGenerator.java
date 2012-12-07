package com.voltdb.demographicanalytics;

/* This file is part of VoltDB.
 * Copyright (C) 2008-2012 VoltDB Inc.
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
import java.util.List;

import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.NullCallback;

import com.voltdb.demographicanalytics.configuration.NetworkConfigJSON;
import com.voltdb.demographicanalytics.configuration.SampleConfiguration;
import com.voltdb.demographicanalytics.configuration.SampleConfigurationFactory;
import com.voltdb.demographicanalytics.vo.logentry.AdLogEntry;
import com.voltdb.demographicanalytics.vo.logentry.LogEntryFactory;
import com.voltdb.demographicanalytics.vo.logentry.NetworkProfile;

/**
 * Executes a process of adding new log entries to a log table and then 
 * facilitating analytics against those entries.
 * @author awilson
 * 
 */
public class AdGenerator extends BaseVoltApp {

    /**
     * @param args
     * 
     */
    public static void main(String[] args) {
        SampleConfiguration config = SampleConfigurationFactory
                .getConfiguration(args);
        AdGenerator logStream = new AdGenerator(config);
        try {
            logStream.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AdGenerator(SampleConfiguration config) {
        super(config);
    }

    @Override
    protected void execute() throws Exception {
        NetworkConfigJSON jsonReader = new NetworkConfigJSON(this.config);
        List<NetworkProfile> profiles = jsonReader.load();

        LogEntryFactory factory = new LogEntryFactory();
        for (NetworkProfile profile : profiles) {
            factory.addProfile(profile);
            System.out.println(profile);
        }

        createBatchs(factory);
    }

    protected void createBatchs(LogEntryFactory factory) {
        long start = System.currentTimeMillis();
        long end = start;
        int batch = 0;
        while (true) {
            while ((end - start) < 1000) {
                addBatch(factory, batch);
                end = System.currentTimeMillis();
            }

            System.out.println("Batch: " + batch + " time: " + (end - start));
            start = end;
            batch++;
        }
    }

    protected void addBatch(LogEntryFactory factory, int batch) {
        List<AdLogEntry> entries = factory.getNextBatch();
        for (AdLogEntry entry : entries) {
            try {
                
                this.client.callProcedure(new NullCallback(), "InsertLogEntry",
                        batch, entry.getNetwork(), entry.getCost(), entry
                                .getFirstName(), entry.getLastName(), entry
                                .getAgeActual(), entry.getIncomeActual(), entry
                                .getAge().toString(), entry.getIncome()
                                .toString(), entry.getSex().toString(), entry
                                .getMaritalStatus().toString(), entry
                                .getEducation().toString(), entry
                                .getOccupation().toString(), entry
                                .isConversion() == true ? 1 : 0);
            } catch (NoConnectionsException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void printResults() {
        // TODO Auto-generated method stub

    }
}
