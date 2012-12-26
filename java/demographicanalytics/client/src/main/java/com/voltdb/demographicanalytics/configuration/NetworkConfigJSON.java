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
package com.voltdb.demographicanalytics.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voltdb.demographicanalytics.vo.logentry.NetworkProfile;

/**
 * Reads a json config file for setting up the data feed parameters.
 * @author awilson
 *
 */
public class NetworkConfigJSON {

    SampleConfiguration config;

    public NetworkConfigJSON(SampleConfiguration config) {
        this.config = config;
    }

    public List<NetworkProfile> load() throws IOException {
        File cfgFile = new File(config.configfile);
        if ( !cfgFile.exists() ) {
            throw new FileNotFoundException("File not found: " + config.configfile);
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<NetworkProfile>>() {}.getType();

        BufferedReader reader = new BufferedReader( new FileReader(cfgFile));
        List<NetworkProfile> profiles = gson.fromJson(reader, listType);
        reader.close();
        return profiles;
    }

}
