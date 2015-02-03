package com.drprog.sjournal.db.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 30.01.14.
 */
public abstract class SQLBaseTable<T> {

    public static final String KEY_ID = "_id";

    protected SQLiteDatabase sqlDatabase;

    public void onOpenDb(SQLiteDatabase db) {
        this.sqlDatabase = db;
    }

    protected abstract String getTableName();

    /**
     * This is a method to create new item and load data into its field from database.
     *
     * @param cur opened cursor.
     * @return item instance.
     */
    protected abstract T loadDbItem(Cursor cur);

    protected abstract ContentValues convertToCV(T item);

    protected T get(String selection, String[] args) {
        return get(selection, args, null);
    }

    protected T get(String selection, String[] args, String orderBy) {
        Cursor cur = sqlDatabase.query(
                getTableName(),                     // The table name
                null,                               // A list of which columns to return
                selection,                          // SQL WHERE clause
                args,                               // selectionArgs to SQL WHERE clause
                null,                               // GROUP BY clause
                null,                               // SQL HAVING clause
                orderBy);                              // SQL ORDER BY clause
        if (!DataCheck.checkCursor(cur)) return null;
        T item = loadDbItem(cur);
        cur.close();
        return item;
    }

    public T get(long id) {
        return get(KEY_ID + " = ?", new String[]{String.valueOf(id)}, null);
    }

    protected int getCount(String selection, String[] args) {
        int res = 0;
        String sqlQ = "select count(*) from " + getTableName()
                + " where " + selection + ";";
        Cursor cur = sqlDatabase.rawQuery(sqlQ, args);
        if (DataCheck.checkCursor(cur)) {
            res = cur.getInt(0);
            cur.close();
        }
        return res;
    }

    public List<T> getAll(String orderBy) {
        //List<T> itemList = new ArrayList<T>();
        Cursor cur = sqlDatabase.query(
                getTableName(),                    // The table name
                null,
                null,
                null,
                null,                               // GROUP BY clause
                null,
                orderBy);                          // SQL ORDER BY clause

//        if (DataCheck.checkCursor(cur)) {
//            do {
//                T item = loadDbItem(cur);
//                itemList.add(item);
//            }
//            while (cur.moveToNext());
//            cur.close();
//        }
//        return itemList;
        return getList(cur);
    }

    protected List<T> getList(String selection, String[] selectionArgs, String orderBy) {
        return getList(selection, selectionArgs, null, orderBy);
    }

    protected List<T> getList(String selection, String[] selectionArgs, String groupBy,
            String orderBy) {
        Cursor cur = sqlDatabase.query(
                getTableName(),                    // The table name
                null,
                selection,                         // SQL WHERE clause
                selectionArgs,                     // selectionArgs to SQL WHERE clause
                groupBy,                           // GROUP BY clause
                null,
                orderBy);                          // SQL ORDER BY clause
        return getList(cur);
    }

    protected List<T> getList(Cursor cur) {
        List<T> itemList = new ArrayList<T>();
        if (DataCheck.checkCursor(cur)) {
            do {
                T item = loadDbItem(cur);
                itemList.add(item);
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;
    }

    protected List<String> getStringList(String column, String selection, String[] selectionArgs) {
        return getStringList(column, selection, selectionArgs, null);
    }

    protected List<String> getStringList(String column, String selection, String[] selectionArgs,
            String groupBy) {
        List<String> itemList = new ArrayList<String>();
        assert (column != null);
        Cursor cur = sqlDatabase.query(
                getTableName(),                   // The table name
                new String[]{column},             // A list of which columns to return
                selection,
                selectionArgs,
                groupBy,                          // SQL GROUP BY clause
                null,
                column);                          // SQL ORDER BY clause
        if (DataCheck.checkCursor(cur)) {
            do {
                itemList.add(cur.getString(cur.getColumnIndex(column)));
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;
    }

    public List<String> getAllInColumn(String column) {
        return getStringList(column, null, null);
    }

    protected int delete(String selection, String[] selectionArgs) {
        return sqlDatabase.delete(getTableName(), selection, selectionArgs);
    }

    public int delete(long id) {
        return delete(KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteAll() {
        return sqlDatabase.delete(
                getTableName(),
                null,
                null);
    }

    public long insert(T item) {
        ContentValues cv = convertToCV(item);
        return sqlDatabase.insert(getTableName(), null, cv);
    }

    public int update(long oldId, T item) {
        ContentValues cv = convertToCV(item);
        try {
            return sqlDatabase.update(getTableName(), cv, KEY_ID + " = ?",
                                      new String[]{String.valueOf(oldId)});
        } catch (SQLException e) {
            e.printStackTrace();
//            Log.e(DebugUtils.TAG_DEBUG,
//                  "Error updating:" + " Table " + getTableName() + "; id = " + oldId, e);
            return -1;
        }
    }

    protected int update(String selection, String[] selectionArgs, T item) {
        ContentValues cv = convertToCV(item);
        try {
            return sqlDatabase.update(getTableName(), cv, selection, selectionArgs);
        } catch (SQLException e) {
            e.printStackTrace();
//            Log.e(DebugUtils.TAG_DEBUG, "Error updating:" + " Table " + getTableName()
//                    + "; class = " + item.getClass(), e);
            return -1;
        }
    }

    public boolean exists(Long id) {
        return getCount(KEY_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean exists(T item) {
        List<T> itemList = getAll(null);
        return itemList.contains(item);
    }
}
