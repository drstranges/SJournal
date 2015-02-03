package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.utils.DataCheck;

/**
 * Created by Romka on 09.07.2014.
 */
public class TableRuleManagement {
    //private static TableGroupManagement ourInstance = null;
    public static final String KEY_ID = "_id";
    public static final String TABLE_NAME = "RuleManagement";
    public static final String KEY_SUMMARY_ENTRY_ID = "SummaryEntryId";
    public static final String KEY_RULE_ID = "RuleId";

    private static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_SUMMARY_ENTRY_ID + " integer not null, " +
            KEY_RULE_ID + " integer not null, " +
            //"primary key (" + KEY_GROUP_ID + ", " + KEY_STUDENT_ID + "), " +
            "foreign key (" + KEY_SUMMARY_ENTRY_ID + ") references " +
            TableSummaryEntries.TABLE_NAME + "(" + TableSummaryEntries.KEY_ID +
            ") on delete cascade on update cascade, " +
            "foreign key (" + KEY_RULE_ID + ") references " + TableRules.TABLE_NAME +
            "(" + TableRules.KEY_ID + ") on delete cascade on update cascade);";


    protected static void onCreateDb(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TABLE);
    }

    protected static void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    public static Cursor getRules(SQLiteDatabase db, long entryId) {
        String sqlQ = "select table1.*"
                + " from " + TableRules.TABLE_NAME + " as table1 inner join " +
                TABLE_NAME + " as table2 on table1." + TableRules.KEY_ID + " = table2." +
                KEY_RULE_ID
                + " where table2." + KEY_SUMMARY_ENTRY_ID + " = ? order by table2."
                + KEY_ID;
        return db.rawQuery(sqlQ, new String[]{String.valueOf(entryId)});
    }

    public static int delete(SQLiteDatabase db, long entryId, long ruleId) {
        return db.delete(TABLE_NAME, KEY_SUMMARY_ENTRY_ID + " = ? and " + KEY_RULE_ID + " = ?",
                         new String[]{String.valueOf(entryId), String.valueOf(ruleId)});
    }

    public static int deleteAllRules(SQLiteDatabase db) {
        return db.delete(TABLE_NAME, null, null);
    }

    public static int deleteAllRules(SQLiteDatabase db, long entryId) {
        return db.delete(TABLE_NAME, KEY_SUMMARY_ENTRY_ID + " = ?",
                         new String[]{String.valueOf(entryId)});
    }

    public static long insert(SQLiteDatabase db, long entryId, long ruleId) {
        if (!exists(db, entryId, ruleId)) {
            ContentValues cv = new ContentValues();
            cv.put(KEY_SUMMARY_ENTRY_ID, entryId);
            cv.put(KEY_RULE_ID, ruleId);
            return db.insert(TABLE_NAME, null, cv);
        } else {
            return -1;
        }
    }

    public static boolean exists(SQLiteDatabase db, long entryId, long ruleId) {
        final String[] args = new String[]{String.valueOf(entryId), String.valueOf(ruleId)};
        int res = 0;
        String sqlQ = "select count(*) from " + TABLE_NAME
                + " where " + KEY_SUMMARY_ENTRY_ID + " = ? and " + KEY_RULE_ID + " = ?;";
        Cursor cur = db.rawQuery(sqlQ, args);
        if (DataCheck.checkCursor(cur)) {
            res = cur.getInt(0);
            cur.close();
        }
        return res > 0;
    }


}
