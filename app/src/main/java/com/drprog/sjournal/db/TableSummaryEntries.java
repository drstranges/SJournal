package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.entity.SummaryEntry;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.util.List;

/**
 * Created by Romka on 01.02.14.
 */
public class TableSummaryEntries extends SQLBaseTable<SummaryEntry> {
    public static final String TABLE_NAME = "SummaryEntries";

    public static final String KEY_SEMESTER = "Semester";
    public static final String KEY_GROUP_ID = "GroupId";
    public static final String KEY_SUBJECT_ID = "SubjectId";
    //public static final String KEY_TITLE = "Title";
    //public static final String KEY_ABBR = "Abbr"; //Abbreviation
    public static final String KEY_ROW_COLOR = "RowColor";
    public static final String KEY_ROW_TEXT_COLOR = "RowTextColor";
    //public static final String KEY_NOTE = "Note";
    public static final String KEY_CLASS_TYPE_ID = "ClassTypeId";
    public static final String KEY_CLASS_ID = "ClassId";
    public static final String KEY_IS_COUNTED = "IsCounted";


    protected static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_SEMESTER + " integer not null default 1, " +
            KEY_GROUP_ID + " integer not null, " +
            KEY_SUBJECT_ID + " integer not null, " +
            KEY_CLASS_TYPE_ID + " integer, " +
            KEY_CLASS_ID + " integer, " +
            KEY_IS_COUNTED + " integer not null default 1, " +
            KEY_ROW_COLOR + " integer, " +
            KEY_ROW_TEXT_COLOR + " integer, " +
//            "foreign key (" + KEY_CLASS_TYPE_ID + ") references " + TableClassTypes.TABLE_NAME +
//            "(" + TableClassTypes.KEY_ID + ") on delete cascade on update cascade, " +
            "foreign key (" + KEY_GROUP_ID + ") references " + TableGroups.TABLE_NAME +
            "(" + TableGroups.KEY_ID + ") on delete cascade on update cascade, " +
            "foreign key (" + KEY_SUBJECT_ID + ") references " + TableSubjects.TABLE_NAME +
            "(" + TableSubjects.KEY_ID + ") on delete cascade on update cascade);";


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
    protected SummaryEntry loadDbItem(Cursor cur) {
        SummaryEntry item = new SummaryEntry();
        item.setId(cur.getLong(cur.getColumnIndex(KEY_ID)));
        item.setSemester(cur.getInt(cur.getColumnIndex(KEY_SEMESTER)));
        item.setGroupId(cur.getLong(cur.getColumnIndex(KEY_GROUP_ID)));
        item.setSubjectId(cur.getLong(cur.getColumnIndex(KEY_SUBJECT_ID)));
        item.setCounted(cur.getInt(cur.getColumnIndex(KEY_IS_COUNTED))==1);
        if (!cur.isNull(cur.getColumnIndex(KEY_CLASS_TYPE_ID))) {
            item.setClassTypeId(cur.getLong(cur.getColumnIndex(KEY_CLASS_TYPE_ID)));
        }
        if (!cur.isNull(cur.getColumnIndex(KEY_CLASS_ID))) {
            item.setClassId(cur.getLong(cur.getColumnIndex(KEY_CLASS_ID)));
        }
        if (!cur.isNull(cur.getColumnIndex(KEY_ROW_COLOR))) {
            item.setRowColor(cur.getInt(cur.getColumnIndex(KEY_ROW_COLOR)));
        }
        if (!cur.isNull(cur.getColumnIndex(KEY_ROW_TEXT_COLOR))) {
            item.setTextColor(cur.getInt(cur.getColumnIndex(KEY_ROW_TEXT_COLOR)));
        }

        return item;
    }

    @Override
    protected ContentValues convertToCV(SummaryEntry item) {
        ContentValues cv = new ContentValues();
        if (item.getId() != null) { cv.put(KEY_ID, item.getId()); }
        cv.put(KEY_SEMESTER, item.getSemester());
        cv.put(KEY_GROUP_ID, item.getGroupId());
        cv.put(KEY_SUBJECT_ID, item.getSubjectId());
        cv.put(KEY_IS_COUNTED, item.isCounted() ? 1 : 0);
        //if (item.getClassTypeId() != null) {
            cv.put(KEY_CLASS_TYPE_ID, item.getClassTypeId());
//        }else{
//            cv.put(KEY_CLASS_TYPE_ID, "NULL");
//        }
        //if (item.getClassId() != null) {
            cv.put(KEY_CLASS_ID, item.getClassId());
        //}
        //if (item.getRowColor() != null) {
        cv.put(KEY_ROW_COLOR, item.getRowColor());
        // }
        //if (item.getTextColor() != null) {
        cv.put(KEY_ROW_TEXT_COLOR, item.getTextColor());
    //}
        return cv;
    }

    public List<SummaryEntry> getEntries(long groupId, long subjectId, int semester) {
        return super.getList(KEY_GROUP_ID + " = ? and "
                                     + KEY_SUBJECT_ID + " = ? and "
                                     + KEY_SEMESTER + " = ?",
                             new String[]{String.valueOf(groupId),
                                     String.valueOf(subjectId),
                                     String.valueOf(semester)},
                             KEY_ID
        ); //OrderBy
    }


}
