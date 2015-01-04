/* This file is part of VoltDB.
 * Copyright (C) 2008-2015 VoltDB Inc.
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

package tweets;

import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import au.com.bytecode.opencsv_voltpatches.CSVReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Point;

public class TweetGenerator
{

    public final static String[] TAGS = new String[] { "bieber", "Lorem", "ipsum", "dolor", "sit",  "amet", "consectetur", "adipiscing", "elit", "Mauris hendrerit" };
    public static class TweetRequest
    {
        public final double lat;
        public final double lon;
        public final String tag;
        protected TweetRequest(double lat, double lon, String tag)
        {
            this.lat = lat;
            this.lon = lon;
            this.tag = tag;
        }
    }

    private final Random rand = new Random();
    private final Long[] index;
    private final long max;
    private final ArrayList<Point> data = new ArrayList<Point>();
    int counter = 0;
    
    public TweetGenerator() throws Exception
    {

        CSVReader reader = new CSVReader(new BufferedReader(new FileReader("generator_data.csv")), ',');
        String[] line;
        ArrayList<Long> rindex = new ArrayList<Long>();

        while ((line = reader.readNext()) != null)
        {
            // Skip empty lines (that will likely occur at the end)
            if (line.length == 1 && line[0].length() == 0)
                continue;

            rindex.add(Long.parseLong(line[0]));
            data.add(new Point(Integer.parseInt(line[2]), Integer.parseInt(line[1])));
        }
        index = rindex.toArray(new Long[] {});
        max = index[index.length-1];
    }
    /**
     * Receives/generates a simulated tweet request
     * @return Request details
     */
    public TweetRequest receive()
    {
        long r = (long)(rand.nextDouble()*max);
        int i = Arrays.binarySearch(index,r);
        if (i < 0){ 
            i = -(1+i);
        }
        // Favor tags toward the beginning of the array
        int nextRange = rand.nextInt(TweetGenerator.TAGS.length);
        nextRange = nextRange > 0 ? nextRange : 1;
        String tag = TweetGenerator.TAGS[ rand.nextInt(nextRange)];
        if ( counter++ > 60000) {
            shuffle(TweetGenerator.TAGS,1);
            counter = 0;
        }
        return new TweetRequest( data.get(i).getY()
                               , data.get(i).getX()
                               , tag
                               );
    }
    
    /*
     * Shuffle the array so that the data looks interesting instead of a bunch of 
     * evenly distributed tags.
     */
    private void shuffle(String[] tags, int swaps) {
        int tagsLength = tags.length -2;// cheat to skip the first element which will never move;
        for ( int index = 0; index < swaps; index++) {
            int source = rand.nextInt(tagsLength)+1;
            int destination = rand.nextInt(tagsLength)+1;
            
            String tmp = tags[destination];
            tags[destination] = tags[source];
            tags[source] = tmp;
        }
    }
}
