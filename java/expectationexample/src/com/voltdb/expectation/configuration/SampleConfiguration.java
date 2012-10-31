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

package com.voltdb.expectation.configuration;

import org.voltdb.CLIConfig;
import org.voltdb.CLIConfig.Option;
/**
 * Command line parser
 * This sample only needs the server.
 * @author awilson
 *
 */
public class SampleConfiguration extends CLIConfig {

    @Option(desc = "Comma separated list volt servers to conenct to. server[:port]")
    public String servers = "localhost:21212";

    @Option(desc = "Volt user name")
    public String user = "";

    @Option(desc = "Volt password")
    public String password = "";

    @Option(desc = "How often to display transaction statistics.")
    public int displayinterval = 5;

    public SampleConfiguration() {
    }

    @Override
    public void validate() {
    }
}
