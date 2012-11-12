package client;

import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NameGenerator {
    
    String[] firstnames;
    String[] lastnames;
    Random r = new Random();

    // constructors
    public NameGenerator() {
        this("data/firstnames.csv","data/lastnames.csv");
    }

    public NameGenerator(String firstnamesFile, String lastnamesFile) {
        
        firstnames = fileToStringArray(firstnamesFile);
        lastnames = fileToStringArray(lastnamesFile);
    }

    public String getFullName() {

        String firstName = firstnames[r.nextInt(firstnames.length - 1)];
        String lastName = lastnames[r.nextInt(lastnames.length - 1)];
        String fullName = firstName + " " + lastName;

        return fullName;
    }

    public static String[] fileToStringArray(String filename) {
        List<String> lines = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            System.out.println("loaded " + lines.size() + " lines from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[lines.size()]);
    }

}
