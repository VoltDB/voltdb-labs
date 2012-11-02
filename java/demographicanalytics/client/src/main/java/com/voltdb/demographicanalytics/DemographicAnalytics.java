/* This file is part of VoltDB.
 * Copyright (C) 2008-2012 VoltDB Inc.
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

package com.voltdb.demographicanalytics;

import org.voltdb.client.ClientResponse;

import com.voltdb.demographicanalytics.configuration.SampleConfiguration;
import com.voltdb.demographicanalytics.configuration.SampleConfigurationFactory;


/**
 * A short client application demonstrating how to use stored procedure
 * expectations.
 * 
 * @author awilson
 * 
 */
public class DemographicAnalytics extends BaseVoltApp {

    // A couple of messages stating whether the login worked.
    private static final String AUTHENTICATION_ERROR = "%s authentication with %s/%s failed. Expected: %s%n";
    private static final String AUTHENTICATION_SUCCESS = "%s authentication with %s/%s successful. Expected: %s%n";

    public static void main(String[] args) {
        SampleConfiguration config = SampleConfigurationFactory
                .getConfiguration(args);
        DemographicAnalytics expectationSample = new DemographicAnalytics(config);
        try {
            expectationSample.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DemographicAnalytics(SampleConfiguration config) {
        super(config);
    }

    public void execute() throws Exception {
        // Add a new user with the default insert proc
        addUser("bob", "pw");

        // Should succeed
        testAuthenticationMethods("bob", "pw", false);

        // Should fail
        testAuthenticationMethods("bob", "bad password", true);

        // Should fail
        testAuthenticationMethods("mike", "invalid user", true);
    }

    /**
     * Executes both authentication methods to demonstrate that both
     * authentication methods work.
     * 
     * @param name
     * @param password
     * @param expectError
     */
    public void testAuthenticationMethods(String name, String password,
            boolean expectError) {
        testAutheticationSimple(name, password, expectError);
        testAutheticationExpectation(name, password, expectError);
        System.out.println("-------------------------------------------------");
    }

    /**
     * Simple authentication proc call
     * 
     * @param name
     * @param password
     * @param expectError
     */
    protected void testAutheticationSimple(String name, String password,
            boolean expectError) {
        testAuthentication(name, password, expectError, true);
    }

    /**
     * Expectation authentication proc call
     * 
     * @param name
     * @param password
     * @param expectError
     */
    protected void testAutheticationExpectation(String name, String password,
            boolean expectError) {
        testAuthentication(name, password, expectError, false);
    }

    /**
     * Executes the appropriate login method.
     * 
     * @param name
     * @param password
     * @param expectError
     * @param simple
     */
    protected void testAuthentication(String name, String password,
            boolean expectError, boolean simple) {
        String authenticationType = "";
        String procCall = "";

        try {
            // Choose the authentication method
            if (simple) {
                authenticationType = "Simple";
                procCall = "LoginProc1";
            } else {
                authenticationType = "Expectation";
                procCall = "LoginProc2";
            }

            // Execute it
            if (userProcCall(procCall, name, password)) {
                // This can be either LoginProc1 or LoginProc2
                System.out.printf(AUTHENTICATION_SUCCESS, authenticationType,
                        name, password, !expectError);
            } else {
                // This is only LoginProc1
                System.out.printf(AUTHENTICATION_ERROR, authenticationType,
                        name, password, expectError);
            }
        } catch (Exception e) {
            // This should only be LoginProc2 unless a system error occurs.
            System.out.printf(AUTHENTICATION_ERROR, authenticationType, name,
                    password, expectError);
        }
    }

    /**
     * Adds a user to the database
     * 
     * @param name
     * @param password
     * @return
     * @throws Exception
     */
    private boolean addUser(String name, String password) throws Exception {
        return userProcCall("USER_TABLE.insert", name, password);
    }

    /**
     * Generic stored procedure caller. All the stored procedures take only two
     * parameters: the user name and the password. The stored procedures also
     * only return either a zero for error or a non-zero for success.
     * 
     * @param proc
     * @param name
     * @param password
     * @return
     * @throws Exception
     */
    private boolean userProcCall(String proc, String name, String password)
            throws Exception {

        ClientResponse response = this.client.callProcedure(proc, name,
                password);
        return response.getResults()[0].asScalarLong() > 0;
    }

    @Override
    protected void printResults() {
    }

    @Override
    public void schedulePeriodicStats() {
    }

    @Override
    public void stopPeriodicStats() {
    }

}
