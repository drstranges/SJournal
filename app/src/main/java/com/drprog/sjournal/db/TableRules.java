package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.entity.Rule;
import com.drprog.sjournal.db.utils.DataCheck;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 30.01.14.
 */
public class TableRules extends SQLBaseTable<Rule> {

    public static final String TABLE_NAME = "Rules";
    public static final String KEY_OPERATOR_ID = "Operator";
    public static final String KEY_ARGUMENT = "Argument";
    public static final String KEY_RESULT = "Result";

    protected static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            //KEY_SUMMARY_ENTRY_ID + " integer not null, " +
            KEY_OPERATOR_ID + " integer not null, " +
            KEY_ARGUMENT + " text, " +
            KEY_RESULT + " integer not null default 0" +
            ");";


    protected static void onCreateDb(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TABLE);

    }

    protected static void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Rule loadDbItem(Cursor cur) {
        Rule item = new Rule();
        item.setId(cur.getLong(cur.getColumnIndex(KEY_ID)));
        //item.setSummaryEntryId(cur.getInt(cur.getColumnIndex(KEY_SUMMARY_ENTRY_ID)));
        item.setOperatorId(cur.getInt(cur.getColumnIndex(KEY_OPERATOR_ID)));
        item.setArgument(cur.getString(cur.getColumnIndex(KEY_ARGUMENT)));
        item.setResult(cur.getInt(cur.getColumnIndex(KEY_RESULT)));
        return item;
    }

    @Override
    protected ContentValues convertToCV(Rule item) {
        ContentValues cv = new ContentValues();
        if (item.getId() != null) { cv.put(KEY_ID, item.getId()); }
        //cv.put(KEY_SUMMARY_ENTRY_ID, item.getSummaryEntryId());
        cv.put(KEY_OPERATOR_ID, item.getOperatorId());
        if (item.getArgument() != null) cv.put(KEY_ARGUMENT, item.getArgument());
        cv.put(KEY_RESULT, item.getResult());
        return cv;
    }


    public List<Rule> getRules(long entryId) {
        List<Rule> itemList = new ArrayList<Rule>();
        Cursor cur = TableRuleManagement.getRules(sqlDatabase, entryId);
        if (DataCheck.checkCursor(cur)) {
            do {
                Rule item = loadDbItem(cur);
                itemList.add(item);
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;
    }


    public long addRuleToEntry(long entryId, long ruleId) {
        return TableRuleManagement.insert(sqlDatabase, entryId, ruleId);
    }

    public long deleteRuleFromEntry(long entryId, long ruleId) {
        return TableRuleManagement.delete(sqlDatabase, entryId, ruleId);
    }

    public long deleteAllRulesFromEntry(long entryId) {
        return TableRuleManagement.deleteAllRules(sqlDatabase, entryId);
    }

    public Rule getRule(Rule rule) {
        return get(KEY_OPERATOR_ID + " = ? and " + KEY_ARGUMENT + " = ? and " + KEY_RESULT + " = ?",
                   new String[]{String.valueOf(rule.getOperatorId()),
                           rule.getArgument(),
                           String.valueOf(rule.getResult())}
        );
    }
}

