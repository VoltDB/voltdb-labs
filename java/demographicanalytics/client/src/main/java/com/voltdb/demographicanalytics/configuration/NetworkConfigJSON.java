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
