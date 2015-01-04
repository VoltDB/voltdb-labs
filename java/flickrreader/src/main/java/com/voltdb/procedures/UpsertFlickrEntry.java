package com.voltdb.procedures;

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

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

/**
 * Stored procedure that inserts or updates the flickr item in the DB. The
 * process checks if the record already exists and will update it. Flickr photo
 * data changes frequently so it is possible to have an image reappear in the
 * list but with updated data.
 * 
 * @author awilson
 * 
 */
public class UpsertFlickrEntry extends VoltProcedure {

    private final static SQLStmt SELECT_STREAM = new SQLStmt(
            "select * from stream where image_id like ?;");
    private final static SQLStmt UPDATE_STREAM = new SQLStmt(
            "update stream set json=? where image_id like ?;");
    private final static SQLStmt INSERT_STREAM = new SQLStmt(
            "insert into stream values(?,?,?);");

    private final static SQLStmt SELECT_TAG = new SQLStmt(
            "select * from tags where image_id like ? and tag like ?;");
    private final static SQLStmt INSERT_TAG = new SQLStmt(
            "insert into tags values(?,?);");

    public long run(String id, String title, String tags, String json) {

        String[] tagArray = tags.split("\\s");
        upsertStream(id, title, json);
        for (String tag : tagArray) {
            tag = tag.trim();
            if (tag.length() > 0) {
                upsertTags(id, tag);
            }
        }

        voltExecuteSQL(true);
        return 1;
    }

    private void upsertStream(String id, String title, String json) {
        voltQueueSQL(SELECT_STREAM, id);
        VoltTable[] selectResults = voltExecuteSQL();
        if (selectResults[0].getRowCount() > 0) {
            voltQueueSQL(UPDATE_STREAM, json, id);
        } else {
            voltQueueSQL(INSERT_STREAM, id, title, json);
        }
        voltExecuteSQL();
    }

    private void upsertTags(String id, String tag) {
        voltQueueSQL(SELECT_TAG, id, tag);
        VoltTable[] selectResults = voltExecuteSQL();
        if (selectResults[0].getRowCount() < 1) {
            voltQueueSQL(INSERT_TAG, id, tag);
        }
        voltExecuteSQL();
    }

}
