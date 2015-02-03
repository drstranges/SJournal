package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.entity.StudyClassType;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.util.List;

/**
 * Created by Romka on 01.02.14.
 */
public class TableClassTypes extends SQLBaseTable<StudyClassType> {
    public static final String TABLE_NAME = "ClassTypes";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "Title";
    public static final String KEY_ABBR = "Abbr"; //Abbreviation
    public static final String KEY_ICON_INDEX = "IconIndex";

    protected static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_TITLE + " text not null unique, " +
            KEY_ABBR + " text not null unique, " +
            KEY_ICON_INDEX + " integer not null default 0);";

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
    protected StudyClassType loadDbItem(Cursor cur) {
        StudyClassType item = new StudyClassType();
        item.setId(cur.getLong(cur.getColumnIndex(KEY_ID)));
        item.setTitle(cur.getString(cur.getColumnIndex(KEY_TITLE)));
        item.setAbbr(cur.getString(cur.getColumnIndex(KEY_ABBR)));
        item.setIconIndex(cur.getInt(cur.getColumnIndex(KEY_ICON_INDEX)));
        return item;
    }

    @Override
    protected ContentValues convertToCV(StudyClassType item) {
        ContentValues cv = new ContentValues();
        if (item.getId() != null) { cv.put(KEY_ID, item.getId()); }
        cv.put(KEY_TITLE, item.getTitle());
        cv.put(KEY_ABBR, item.getAbbr());
        if (item.getIconIndex() != null) { cv.put(KEY_ICON_INDEX, item.getIconIndex()); }
        return cv;
    }

    public List<StudyClassType> getAllByFilter(Long subjectId, Long groupId) {
        String sqlQ;
        String[] selectionArgs;
        if (groupId == null && subjectId == null) {
            return getAll(KEY_ABBR);
        } else {
            sqlQ = "select distinct table1.*"
                    + " from " + TABLE_NAME + " as table1 inner join " + TableClasses.TABLE_NAME
                    + " as table2 on table1." + KEY_ID + " = table2." +
                    TableClasses.KEY_CLASS_TYPE_ID
                    + " where "
                    +
                    (groupId != null ? "table2." + TableClasses.KEY_GROUP_ID + " = ? " : "")
                    +
                    ((subjectId != null && groupId != null) ? "and " : "")
                    +
                    (subjectId != null ? "table2." + TableClasses.KEY_SUBJECT_ID + " = ? " : "")
                    + "order by table1." + KEY_ABBR;
            if (groupId != null && subjectId != null) {
                selectionArgs = new String[]{String.valueOf(groupId), String.valueOf(subjectId)};
            } else {
                selectionArgs = new String[]{
                        String.valueOf(groupId != null ? groupId : subjectId)
                };
            }
        }
        Cursor cur = sqlDatabase.rawQuery(sqlQ, selectionArgs);
        return super.getList(cur);
    }
}
